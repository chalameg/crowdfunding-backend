package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.exception.customException.PaymentCannotProcessedException;
import com.dxvalley.crowdfunding.paymentManager.chapa.ChapaPaymentResponse;
import com.dxvalley.crowdfunding.paymentManager.chapa.ChapaService;
import com.dxvalley.crowdfunding.paymentManager.chapa.VerifyResponse;
import com.dxvalley.crowdfunding.paymentManager.cooPass.CooPassInitResponse;
import com.dxvalley.crowdfunding.paymentManager.cooPass.CooPassService;
import com.dxvalley.crowdfunding.paymentManager.cooPass.CooPassVerifyResponse;
import com.dxvalley.crowdfunding.paymentManager.ebirr.EbirrPaymentResponse;
import com.dxvalley.crowdfunding.paymentManager.ebirr.EbirrService;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.*;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentGatewayService;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentProcessor;
import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import com.dxvalley.crowdfunding.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PaymentOperationServiceImpl implements PaymentOperationService {
    private static final Logger log = LoggerFactory.getLogger(PaymentOperationServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final PaymentGatewayService paymentGatewayService;
    private final ChapaService chapaService;
    private final EbirrService ebirrService;
    private final CooPassService cooPassService;
    private final PaymentUtils paymentUtils;

    public ResponseEntity processPaymentWithEbirr(EbirrPaymentReqDTO ebirrPaymentReqDTO) {
        this.paymentUtils.validatePaymentPreconditions(ebirrPaymentReqDTO.getCampaignId(), PaymentProcessor.EBIRR);
        Payment payment = this.paymentUtils.createPaymentFromEbirrRequestDTO(ebirrPaymentReqDTO);

        try {
            this.paymentRepository.save(payment);
            log.info("Sending payment request to eBIRR: {}", payment);
            EbirrPaymentResponse ebirrPaymentResponse = this.ebirrService.sendPaymentRequest(payment);
            this.updatePaymentStatusForEbirr(ebirrPaymentResponse, payment.getOrderId());
            log.info("Received success response from eBIRR: {}", ebirrPaymentResponse);
            return ApiResponse.success("Payment completed successfully");
        } catch (PaymentCannotProcessedException var4) {
            this.paymentUtils.handleFailedPayment(payment);
            LoggingUtils.logError(this.getClass(), "processPaymentWithEbirr", var4);
            System.err.println(var4.getMessage());
            throw new PaymentCannotProcessedException("Can't process payments with Ebirr ");
        }
    }

    @Transactional
    public void updatePaymentStatusForEbirr(EbirrPaymentResponse paymentFuture, String orderId) {
        Payment payment = this.paymentUtils.getPaymentByOrderId(orderId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionCompletedDate(LocalDateTime.now().format(this.dateTimeFormatter));
        payment.setTransactionId(paymentFuture.getData().getTransactionId());
        this.paymentRepository.save(payment);
        double amountCollected = this.paymentUtils.updateCampaignFromPayment(payment);
        System.err.println(amountCollected);
    }

    @Transactional
    public ResponseEntity<?> initializeChapaPayment(ChapaDTO chapaDTOReq) {
        this.paymentUtils.validatePaymentPreconditions(chapaDTOReq.getCampaignId(), PaymentProcessor.EBIRR);
        Payment payment = this.paymentUtils.createPaymentFromChapaPaymentReqDTO(chapaDTOReq);
        ChapaDTO chapaDTO = this.completeChapaDTO(payment, chapaDTOReq);

        try {
            Payment savedPayment = (Payment) this.paymentRepository.save(payment);
            ChapaPaymentResponse response = this.chapaService.initializePayment(chapaDTO);
            return ApiResponse.success(response);
        } catch (Exception var6) {
            this.paymentUtils.handleFailedPayment(payment);
            log.error(var6.getMessage());
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Currently, we can't process payments with Chapa");
        }
    }

    private ChapaDTO completeChapaDTO(Payment payment, ChapaDTO chapaDTOReq) {
        chapaDTOReq.setOrderId(payment.getOrderId());
        return chapaDTOReq;
    }

    @Transactional
    public ResponseEntity updatePaymentStatusForChapa(String orderId, VerifyResponse verifyResponse) {
        return ApiResponse.success("Transaction completed successfully");
    }

    @Transactional
    public ResponseEntity initializeCooPassPayment(PaymentRequestDTO paymentRequest) {
        Campaign campaign = this.paymentUtils.getCampaignById(paymentRequest.getCampaignId());
        Users user = this.paymentUtils.getUserById(paymentRequest.getUserId());
        boolean isActive = this.paymentGatewayService.isPaymentGatewayActive(PaymentProcessor.COOPASS.name());
        if (!isActive) {
            return ApiResponse.error(HttpStatus.FORBIDDEN, "The payment gateway is currently deactivated");
        } else if (campaign.getCampaignStage() != CampaignStage.FUNDING) {
            return ApiResponse.error(HttpStatus.FORBIDDEN, "This campaign is not in the funding stage and can't accept payment.");
        } else {
            String orderId = this.paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName());
            paymentRequest.setOrderId(orderId);
            paymentRequest.setPaymentProcessor(PaymentProcessor.COOPASS);
            Payment payment = this.paymentUtils.createPaymentFromRequestDTO(paymentRequest, campaign, user, orderId);

            try {
                payment.setPaymentStatus(PaymentStatus.PENDING);
                this.paymentRepository.save(payment);
                CooPassInitResponse response = this.cooPassService.initializePayment(paymentRequest);
                return ApiResponse.success(response);
            } catch (Exception var8) {
                this.paymentUtils.handleFailedPayment(payment);
                log.error(var8.getMessage());
                return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Currently, we can't process payments with CooPass");
            }
        }
    }

    @Transactional
    public ResponseEntity verifyCooPassPayment(String orderId) {
        Payment payment = this.paymentUtils.getPaymentByOrderId(orderId);
        CooPassVerifyResponse verifyResponse = this.cooPassService.verifyPayment(orderId);
        this.paymentUtils.updatePaymentFromCooPassVerifyResponse(payment, verifyResponse);
        this.paymentUtils.updateCampaignFromPayment(payment);
        return ApiResponse.success("Transaction completed successfully");
    }

    public ResponseEntity<?> addPayment(PaymentRequestDTO1 paymentAddDTO) {
        Campaign campaign = this.paymentUtils.getCampaignById(paymentAddDTO.getCampaignId());
        Users user = this.paymentUtils.getUserById(paymentAddDTO.getUserId());
        boolean isActive = this.paymentGatewayService.isPaymentGatewayActive(PaymentProcessor.lookup(paymentAddDTO.getPaymentProcessor()).name());
        if (!isActive) {
            return ApiResponse.error(HttpStatus.FORBIDDEN, "The payment gateway is currently deactivated");
        } else if (campaign.getCampaignStage() != CampaignStage.FUNDING) {
            return ApiResponse.error(HttpStatus.FORBIDDEN, "This campaign is not in the funding stage and can't accept payment.");
        } else {
            Payment payment = new Payment();
            payment.setOrderId(this.paymentUtils.generateUniqueOrderId(campaign.getFundingType().getName()));
            payment.setPayerFullName(user != null ? user.getFullName() : null);
            payment.setPaymentContactInfo(paymentAddDTO.getEmail());
            payment.setCampaign(campaign);
            payment.setTransactionOrderedDate(LocalDateTime.now().format(this.dateTimeFormatter));
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setPaymentProcessor(PaymentProcessor.lookup(paymentAddDTO.getPaymentProcessor()));
            payment.setIsAnonymous(paymentAddDTO.getIsAnonymous());
            payment.setAmount(paymentAddDTO.getAmount());
            payment.setCurrency(paymentAddDTO.getCurrency());
            payment.setCampaign(campaign);
            payment.setUser(user);
            this.paymentRepository.save(payment);
            return ApiResponse.created(new Payment(payment.getOrderId(), payment.getTransactionOrderedDate()));
        }
    }

    public ResponseEntity updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO) {
        Payment payment = this.paymentUtils.getPaymentByOrderId(orderId);
        payment.setPayerFullName(paymentUpdateDTO.getPayerFullName());
        payment.setTransactionId(paymentUpdateDTO.getTransactionId());
        payment.setTransactionCompletedDate(LocalDateTime.now().format(this.dateTimeFormatter));
        this.paymentRepository.save(payment);
        this.paymentUtils.updateCampaignFromPayment(payment);
        return ApiResponse.success("Payment Updated Successfully");
    }
}
