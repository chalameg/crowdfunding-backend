package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.exception.customException.PaymentCannotProcessedException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.chapa.ChapaInitializeResponse;
import com.dxvalley.crowdfunding.payment.chapa.ChapaService;
import com.dxvalley.crowdfunding.payment.chapa.ChapaVerifyResponse;
import com.dxvalley.crowdfunding.payment.cooPass.CooPassInitResponse;
import com.dxvalley.crowdfunding.payment.cooPass.CooPassService;
import com.dxvalley.crowdfunding.payment.cooPass.CooPassVerifyResponse;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrPaymentResponse;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrService;
import com.dxvalley.crowdfunding.payment.paymentDTO.*;
import com.dxvalley.crowdfunding.payment.paymentGateway.PaymentGatewayService;
import com.dxvalley.crowdfunding.userManager.user.UserService;
import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.dxvalley.crowdfunding.utils.LoggingUtils.logError;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final DateTimeFormatter dateTimeFormatter;
    private final PaymentGatewayService paymentGatewayService;
    private final ChapaService chapaService;
    private final EbirrService ebirrService;
    private final CooPassService cooPassService;
    private final PaymentUtils paymentUtils;


    // Processes a payment request with the eBIRR payment gateway.
    @Override
    public ResponseEntity processPaymentWithEbirr(EbirrPaymentReqDTO ebirrPaymentReqDTO) {

        Payment payment = paymentUtils.createPaymentFromEbirrRequestDTO(ebirrPaymentReqDTO);
        try {
            paymentRepository.save(payment);
            log.info("Sending payment request to eBIRR: {}", payment);
            CompletableFuture<EbirrPaymentResponse> ebirrPaymentResponse = ebirrService.sendPaymentRequest(payment);
            ebirrPaymentResponse.thenAcceptAsync(paymentResponse -> {
                updatePaymentStatusForEbirr(ebirrPaymentResponse, payment.getOrderId());
            });
            log.info("Received success response from eBIRR: {}", ebirrPaymentResponse);
            return ApiResponse.success("Payment completed successfully");
        } catch (PaymentCannotProcessedException ex) {
            paymentUtils.handleFailedPayment(payment);
            logError(getClass(), "processPaymentWithEbirr", ex);
            throw new PaymentCannotProcessedException("Can't process payments with Ebirr");
        }
    }


    // Updates a Payment object with the payment details returned from the eBIRR payment gateway,
    // and updates the associated Campaign object with the payment details.
    @Transactional
    public void updatePaymentStatusForEbirr(CompletableFuture<EbirrPaymentResponse> paymentFuture, String orderId) {
        try {
            Payment payment = paymentUtils.getPaymentByOrderId(orderId);

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionCompletedDate(LocalDateTime.now().format(dateTimeFormatter));
            payment.setTransactionId(paymentFuture.get().getTransactionId());
            paymentRepository.save(payment);

            paymentUtils.updateCampaignFromPayment(payment);
        } catch (ExecutionException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Initializes a Chapa payment.
     *
     * @param paymentRequest The Chapa request DTO.
     * @return The API response with the Chapa initialize response.
     */
    @Override
    @Transactional
    public ResponseEntity initializeChapaPayment(PaymentRequestDTO paymentRequest) {
        Campaign campaign = paymentUtils.getCampaignById(paymentRequest.getCampaignId());
        Users user = paymentUtils.getUserById(paymentRequest.getUserId());

        boolean isActive = paymentGatewayService.isPaymentGatewayActive(PaymentProcessor.CHAPA.name());
        if (!isActive)
            return ApiResponse.error(HttpStatus.FORBIDDEN, "The payment gateway is currently deactivated");

        if (!(campaign.getCampaignStage() == CampaignStage.FUNDING))
            return ApiResponse.error(HttpStatus.FORBIDDEN, "This campaign is not in the funding stage and can't accept payment.");

        String orderId = paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName());
        paymentRequest.setOrderId(orderId);
        paymentRequest.setPaymentProcessor(PaymentProcessor.CHAPA);

        Payment payment = paymentUtils.createPaymentFromChapaPaymentReqDTO(paymentRequest);

        try {
            payment.setPaymentStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            ChapaInitializeResponse response = chapaService.initializePayment(paymentRequest);
            return ApiResponse.success(response);
        } catch (Exception e) {
            paymentUtils.handleFailedPayment(payment);
            log.error(e.getMessage());
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Currently, we can't process payments with Chapa");
        }
    }

    /**
     * Verifies a Chapa payment with the given order ID.
     *
     * @param orderId The order ID associated with the payment.
     * @return The API response.
     */
    @Override
    @Transactional
    public ResponseEntity verifyChapaPayment(String orderId) {
        Payment payment = paymentUtils.getPaymentByOrderId(orderId);
        ChapaVerifyResponse verifyResponse = chapaService.verifyPayment(orderId);

        paymentUtils.updatePaymentFromChapaVerifyResponse(payment, verifyResponse);

        paymentUtils.updateCampaignFromPayment(payment);

        return ApiResponse.success("Transaction completed successfully");
    }

    /**
     * Initializes a CooPass payment.
     *
     * @param paymentRequest The CooPass request DTO.
     * @return The API response with the Chapa initialize response.
     */
    @Override
    @Transactional
    public ResponseEntity initializeCooPassPayment(PaymentRequestDTO paymentRequest) {
        Campaign campaign = paymentUtils.getCampaignById(paymentRequest.getCampaignId());
        Users user = paymentUtils.getUserById(paymentRequest.getUserId());

        boolean isActive = paymentGatewayService.isPaymentGatewayActive(PaymentProcessor.COOPASS.name());
        if (!isActive)
            return ApiResponse.error(HttpStatus.FORBIDDEN, "The payment gateway is currently deactivated");

        if (!(campaign.getCampaignStage() == CampaignStage.FUNDING))
            return ApiResponse.error(HttpStatus.FORBIDDEN, "This campaign is not in the funding stage and can't accept payment.");

        String orderId = paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName());
        paymentRequest.setOrderId(orderId);
        paymentRequest.setPaymentProcessor(PaymentProcessor.COOPASS);

        Payment payment = paymentUtils.createPaymentFromRequestDTO(paymentRequest, campaign, user, orderId);

        try {
            payment.setPaymentStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            CooPassInitResponse response = cooPassService.initializePayment(paymentRequest);
            return ApiResponse.success(response);
        } catch (Exception e) {
            paymentUtils.handleFailedPayment(payment);
            log.error(e.getMessage());
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Currently, we can't process payments with CooPass");
        }
    }

    /**
     * Verifies a CooPass payment with the given order ID.
     *
     * @param orderId The order ID associated with the payment.
     * @return The API response.
     */
    @Override
    @Transactional
    public ResponseEntity verifyCooPassPayment(String orderId) {
        Payment payment = paymentUtils.getPaymentByOrderId(orderId);
        CooPassVerifyResponse verifyResponse = cooPassService.verifyPayment(orderId);

        paymentUtils.updatePaymentFromCooPassVerifyResponse(payment, verifyResponse);

        paymentUtils.updateCampaignFromPayment(payment);

        return ApiResponse.success("Transaction completed successfully");
    }


    /**
     * Checks the status of pending payments and updates them accordingly.
     * If a payment has been pending for more than 30 minutes, sets its status to FAILED.
     * Otherwise, attempts to verify the payment using the ChapaVerify API.
     */
    @Async
    @Override
    public CompletableFuture<Void> chapaPaymentStatusChecker() {
        List<Payment> pendingPayments = paymentRepository.findByPaymentStatus(PaymentStatus.PENDING.name());
        for (Payment payment : pendingPayments) {
            long paymentTime = Duration.between(LocalDateTime.parse(payment.getTransactionOrderedDate(), dateTimeFormatter), LocalDateTime.now()).toMinutes();
            if (paymentTime >= 30) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            } else {
                try {
                    verifyChapaPayment(payment.getOrderId());
                } catch (Exception e) {

                }
            }
        }
        return CompletableFuture.completedFuture(null);
    }


    /**
     * Adds a payment based on the provided PaymentRequestDTO1 object.
     *
     * @param paymentAddDTO the PaymentRequestDTO1 object containing payment details
     * @return a ResponseEntity representing the response for adding the payment
     * @throws RuntimeException if there is an error adding the payment
     */
    @Override
    public ResponseEntity<?> addPayment(PaymentRequestDTO1 paymentAddDTO) {
        Campaign campaign = paymentUtils.getCampaignById(paymentAddDTO.getCampaignId());
        Users user = paymentUtils.getUserById(paymentAddDTO.getUserId());

        boolean isActive = paymentGatewayService.isPaymentGatewayActive(PaymentProcessor.lookup(paymentAddDTO.getPaymentProcessor()).name());
        if (!isActive)
            return ApiResponse.error(HttpStatus.FORBIDDEN, "The payment gateway is currently deactivated");

        if (!(campaign.getCampaignStage() == CampaignStage.FUNDING))
            return ApiResponse.error(HttpStatus.FORBIDDEN, "This campaign is not in the funding stage and can't accept payment.");

        Payment payment = new Payment();
        payment.setOrderId(paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName()));
        payment.setPayerFullName(user != null ? user.getFullName() : null);
        payment.setPaymentContactInfo(paymentAddDTO.getEmail());
        payment.setCampaign(campaign);
        payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentProcessor(PaymentProcessor.lookup(paymentAddDTO.getPaymentProcessor()));
        payment.setIsAnonymous(paymentAddDTO.getIsAnonymous());
        payment.setAmount(paymentAddDTO.getAmount());
        payment.setCurrency(paymentAddDTO.getCurrency());
        payment.setCampaign(campaign);
        payment.setUser(user);
        paymentRepository.save(payment);

        return ApiResponse.created(new Payment(payment.getOrderId(), payment.getTransactionOrderedDate()));
    }

    @Override
    public ResponseEntity updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO) {
        Payment payment = paymentUtils.getPaymentByOrderId(orderId);
        payment.setPayerFullName(paymentUpdateDTO.getPayerFullName());
        payment.setTransactionId(paymentUpdateDTO.getTransactionId());
//            payment.setPaymentStatus(paymentUpdateDTO.getPaymentStatus());
        payment.setTransactionCompletedDate(LocalDateTime.now().format(dateTimeFormatter));
        paymentRepository.save(payment);

        paymentUtils.updateCampaignFromPayment(payment);
        return ApiResponse.success("Payment Updated Successfully");
    }

    @Override
    public List<PaymentResponse> getPaymentByCampaignId(Long campaignId) {
        List<Payment> payments = paymentRepository.findByCampaignIdAndPaymentStatus(campaignId, PaymentStatus.SUCCESS);

        return payments.stream().map(PaymentMapper::toPaymentResponse).toList();
    }

    @Override
    public List<Payment> getPaymentByUserId(Long userId) {
        userService.getUserById(userId); // to check existence of user
        List<Payment> payments = paymentRepository.findByUserUserId(userId);
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("You have not contributed yet.");
        }
        log.info("Retrieved {} Payment by User", payments.size());
        return payments;
    }
}
