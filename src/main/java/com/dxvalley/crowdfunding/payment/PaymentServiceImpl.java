package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.chapa.ChapaInitializeResponse;
import com.dxvalley.crowdfunding.payment.chapa.ChapaService;
import com.dxvalley.crowdfunding.payment.chapa.ChapaVerifyResponse;
import com.dxvalley.crowdfunding.payment.cooPass.CooPassInitResponse;
import com.dxvalley.crowdfunding.payment.cooPass.CooPassService;
import com.dxvalley.crowdfunding.payment.cooPass.CooPassVerifyResponse;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrPaymentRequest;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrPaymentResponse;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrService;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO1;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentUpdateDTO;
import com.dxvalley.crowdfunding.user.UserService;
import com.dxvalley.crowdfunding.user.Users;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final DateTimeFormatter dateTimeFormatter;
    private final ChapaService chapaService;
    private final EbirrService ebirrService;
    private final CooPassService cooPassService;
    private final PaymentUtils paymentUtils;

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

        String orderId = paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName());
        paymentRequest.setOrderId(orderId);
        paymentRequest.setPaymentProcessor(PaymentProcessor.CHAPA);

        Payment payment = paymentUtils.createPaymentFromRequestDTO(paymentRequest, campaign, user, orderId);

        try {
            payment.setPaymentStatus(PaymentStatus.PENDING.name());
            paymentRepository.save(payment);

            ChapaInitializeResponse response = chapaService.initializePayment(paymentRequest);
            return ApiResponse.success(response);
        } catch (Exception e) {
            paymentUtils.handleFailedPayment(payment, e);
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

        String orderId = paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName());
        paymentRequest.setOrderId(orderId);
        paymentRequest.setPaymentProcessor(PaymentProcessor.COOPASS);

        Payment payment = paymentUtils.createPaymentFromRequestDTO(paymentRequest, campaign, user, orderId);

        try {
            payment.setPaymentStatus(PaymentStatus.PENDING.name());
            paymentRepository.save(payment);

            CooPassInitResponse response = cooPassService.initializePayment(paymentRequest);
            return ApiResponse.success(response);
        } catch (Exception e) {
            paymentUtils.handleFailedPayment(payment, e);
            log.error(e.getMessage());
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Currently, we can't process payments with CooPass");
        }
    }

    /**
     * Verifies a CooPass payment with the given order ID.
     * @param orderId The order ID associated with the payment.
     * @return The API response.
     */
    @Override
    @Transactional
    public ResponseEntity verifyCooPassPayment(String orderId) {
        Payment payment = paymentUtils.getPaymentByOrderId(orderId);
        CooPassVerifyResponse verifyResponse = cooPassService.verifyPayment(orderId);

        paymentUtils.updatePaymentFromCooPassVerifyResponse(payment,verifyResponse);

        paymentUtils.updateCampaignFromPayment(payment);

        return ApiResponse.success("Transaction completed successfully");
    }


    /**
     * Processes a payment request with eBIRR payment gateway.
     * Creates a Payment object, saves it as pending, sends the payment request to eBIRR, and updates the payment status
     * based on the response from eBIRR. Also updates the associated Campaign object with the payment details.
     * @param paymentRequest The payment request DTO containing payment details.
     * @return A CompletableFuture object with the payment response from eBIRR.
     */
    @Override
    @Async
    @Transactional
    public CompletableFuture<EbirrPaymentResponse> payWithEbirr(PaymentRequestDTO paymentRequest) {
        Campaign campaign = paymentUtils.getCampaignById(paymentRequest.getCampaignId());
        Users user = paymentUtils.getUserById(paymentRequest.getUserId());

        String orderId = paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName());
        paymentRequest.setOrderId(orderId);
        paymentRequest.setPaymentProcessor(PaymentProcessor.EBIRR);

        Payment payment = paymentUtils.createPaymentFromRequestDTO(paymentRequest, campaign, user, orderId);

        try {
            payment.setPaymentStatus(PaymentStatus.PENDING.name());
            paymentRepository.save(payment);

            EbirrPaymentRequest ebirrPaymentRequest = ebirrService.requestToEbirrPaymentRequest(paymentRequest);
            EbirrPaymentResponse ebirrPaymentResponse = ebirrService.sendToEbirr(ebirrPaymentRequest);

            return CompletableFuture.completedFuture(ebirrPaymentResponse);
        } catch (Exception e) {
            paymentUtils.handleFailedPayment(payment, e);
            log.error(e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Updates a Payment object with the payment details returned from the eBIRR payment gateway,
     * and updates the associated Campaign object with the payment details.
     * @param paymentFuture A CompletableFuture object containing the payment response from eBIRR.
     */
    @Override
    @Transactional
    public void updatePaymentForEbirr(CompletableFuture<EbirrPaymentResponse> paymentFuture) {
        try {
            Payment payment = paymentUtils.getPaymentByOrderId(paymentFuture.get().getRequestId());

            payment.setPaymentStatus(PaymentStatus.SUCCESS.name());
            payment.setTransactionCompletedDate(paymentFuture.get().getTimestamp());
            payment.setTransactionId(paymentFuture.get().getParams().getTransactionId());
            paymentRepository.save(payment);

            paymentUtils.updateCampaignFromPayment(payment);
        } catch (Exception ex) {
            log.error("Error updatePaymentForEbirr ", ex.getMessage());
            throw new RuntimeException("Error updatePaymentForEbirr ", ex);
        }
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
                payment.setPaymentStatus(PaymentStatus.FAILED.name());
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


    @Override
    public Payment addPayment(PaymentRequestDTO1 paymentAddDTO) {
        try {
            Campaign campaign = paymentUtils.getCampaignById(paymentAddDTO.getCampaignId());
            Users user = paymentUtils.getUserById(paymentAddDTO.getUserId());

            Payment payment = new Payment();
            payment.setOrderId(paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName()));
            payment.setPayerFullName(user != null ? user.getFullName() : null);
            payment.setPaymentContactInfo(paymentAddDTO.getEmail());
            payment.setCampaign(campaign);
            payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
            payment.setPaymentStatus(PaymentStatus.PENDING.name());
            payment.setIsAnonymous(paymentAddDTO.getIsAnonymous());
            payment.setAmount(paymentAddDTO.getAmount());
            payment.setCurrency(paymentAddDTO.getCurrency());
            payment.setCampaign(campaign);
            payment.setUser(user);
            paymentRepository.save(payment);

            return new Payment(payment.getOrderId(), payment.getTransactionOrderedDate());
        } catch (DataAccessException ex) {
            log.error("Error Adding payment: {}", ex.getMessage());
            throw new RuntimeException("Error Adding payment ", ex);
        }
    }

    @Override
    public ResponseEntity updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO) {
        try {
            Payment payment = paymentUtils.getPaymentByOrderId(orderId);
            payment.setPayerFullName(paymentUpdateDTO.getPayerFullName());
            payment.setTransactionId(paymentUpdateDTO.getTransactionId());
            payment.setPaymentStatus(paymentUpdateDTO.getPaymentStatus());
            payment.setTransactionCompletedDate(LocalDateTime.now().format(dateTimeFormatter));
            paymentRepository.save(payment);

            paymentUtils.updateCampaignFromPayment(payment);
            return ApiResponse.success("Payment Updated Successfully");
        } catch (DataAccessException ex) {
            log.error("Error Updating payment: {}", ex.getMessage());
            throw new RuntimeException("Error Updating payment ", ex);
        }
    }

    @Override
    public List<Payment> getPaymentByCampaignId(Long campaignId) {
        try {
            List<Payment> payments = paymentRepository.findPaymentsByCampaignCampaignIdAndPaymentStatus(campaignId, "SUCCESS");
            payments.stream().filter(payment -> payment.getIsAnonymous()).forEach(payment -> payment.setPayerFullName("Anonymous"));

            log.info("Retrieved {} Payment by Campaign", payments.size());
            return payments;
        } catch (DataAccessException ex) {
            log.error("Error Getting Payment By Campaign: {}", ex.getMessage());
            throw new RuntimeException("Error Getting Payment By Campaign ", ex);
        }
    }

    @Override
    public List<Payment> getPaymentByUserId(Long userId) {
        try {
            userService.getUserById(userId); // to check existence of user
            List<Payment> payments = paymentRepository.findPaymentsByUserUserId(userId);
            if (payments.isEmpty()) {
                throw new ResourceNotFoundException("You have not contributed yet.");
            }
            log.info("Retrieved {} Payment by User", payments.size());
            return payments;
        } catch (DataAccessException ex) {
            log.error("Error Getting Payment By User: {}", ex.getMessage());
            throw new RuntimeException("Error Getting Payment By User ", ex);
        }
    }
}
