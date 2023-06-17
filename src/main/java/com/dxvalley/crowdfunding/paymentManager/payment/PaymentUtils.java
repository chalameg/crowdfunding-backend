package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.exception.customException.ForbiddenException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.chapa.ChapaVerifyResponse;
import com.dxvalley.crowdfunding.payment.cooPass.CooPassVerifyResponse;
import com.dxvalley.crowdfunding.payment.paymentDTO.ChapaPaymentReqDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.EbirrPaymentReqDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO;
import com.dxvalley.crowdfunding.payment.paymentGateway.PaymentGatewayService;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PaymentUtils {
    private final PaymentRepository paymentRepository;
    private final CampaignRepository campaignRepository;
    private final UserUtils userUtils;
    private final PaymentGatewayService paymentGatewayService;
    private final DateTimeFormatter dateTimeFormatter;

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findPaymentByOrderId(orderId).
                orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    public Campaign getCampaignById(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
    }

    public Users getUserById(Long userId) {
        return userId != null ? userUtils.utilGetUserByUserId(userId)
                : null;
    }


    /**
     * Validates the preconditions for payment processing.
     *
     * @param campaign         The campaign to validate.
     * @param paymentProcessor The payment processor being used.
     * @throws ForbiddenException If the payment gateway is inactive or the campaign is not in the funding stage.
     */
    public void validatePaymentPreconditions(Long campaignId, PaymentProcessor paymentProcessor) {
        Campaign campaign = getCampaignById(campaignId);
        boolean isActive = paymentGatewayService.isPaymentGatewayActive(paymentProcessor.name());
        if (!isActive)
            throw new ForbiddenException("The payment gateway is currently unavailable. Please try again later.");

        if (campaign.getCampaignStage() != CampaignStage.FUNDING)
            throw new ForbiddenException("This campaign is not accepting payments at the moment. Please check back later.");
    }


    /**
     * Creates a Payment object from a EbirrPaymentReqDTO request.
     *
     * @param ebirrPaymentReqDTO The EbirrPaymentReqDTO request.
     * @return The Payment object.
     */
    public Payment createPaymentFromEbirrRequestDTO(EbirrPaymentReqDTO ebirrPaymentReqDTO) {
        Users user = getUserById(ebirrPaymentReqDTO.getUserId());
        Campaign campaign = getCampaignById(ebirrPaymentReqDTO.getCampaignId());
        String orderId = generateUniqueOrderId(campaign.getFundingType().getName());

        Payment payment = new Payment();

        payment.setPayerFullName(ebirrPaymentReqDTO.getFirstName() + " " + ebirrPaymentReqDTO.getLastName());
        payment.setPaymentContactInfo(ebirrPaymentReqDTO.getPhoneNumber());
        payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
        payment.setIsAnonymous(ebirrPaymentReqDTO.isAnonymous());
        payment.setAmount(ebirrPaymentReqDTO.getAmount());
        payment.setCurrency("ETB");
        payment.setPaymentProcessor(PaymentProcessor.EBIRR);
        payment.setOrderId(orderId);
        payment.setUser(user);
        payment.setCampaign(campaign);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        return payment;
    }


    public Payment createPaymentFromChapaRequestDTO(ChapaPaymentReqDTO chapaPaymentReqDTO, Campaign campaign) {
        Users user = getUserById(chapaPaymentReqDTO.getUserId());
        String orderId = generateUniqueOrderId(campaign.getFundingType().getName());

        Payment payment = new Payment();

        payment.setPayerFullName(chapaPaymentReqDTO.getFirstName() + " " + chapaPaymentReqDTO.getLastName());
        payment.setPaymentContactInfo(chapaPaymentReqDTO.getEmail());
        payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
        payment.setIsAnonymous(chapaPaymentReqDTO.isAnonymous());
        payment.setAmount(chapaPaymentReqDTO.getAmount());
        payment.setCurrency("ETB");
        payment.setPaymentProcessor(PaymentProcessor.EBIRR);
        payment.setOrderId(orderId);
        payment.setUser(user);
        payment.setCampaign(campaign);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        return payment;
    }


    /**
     * Creates a Payment object from a ChapaPaymentReqDTO request.
     *
     * @param chapaPaymentReqDTO The ChapaPaymentReqDTO request.
     * @return The Payment object.
     */
    public Payment createPaymentFromChapaPaymentReqDTO(PaymentRequestDTO chapaPaymentReqDTO) {
        Campaign campaign = getCampaignById(chapaPaymentReqDTO.getCampaignId());
        Users user = getUserById(chapaPaymentReqDTO.getUserId());
        String orderId = generateUniqueOrderId(campaign.getFundingType().getName());

        Payment payment = new Payment();

        payment.setPayerFullName(chapaPaymentReqDTO.getFirstName() + " " + chapaPaymentReqDTO.getLastName());
        payment.setPaymentContactInfo(chapaPaymentReqDTO.getPaymentContactInfo());
        payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
        payment.setIsAnonymous(chapaPaymentReqDTO.getIsAnonymous());
        payment.setAmount(chapaPaymentReqDTO.getAmount());
        payment.setCurrency("ETB");
        payment.setPaymentProcessor(PaymentProcessor.CHAPA);
        payment.setOrderId(orderId);
        payment.setUser(user);
        payment.setCampaign(campaign);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        return payment;
    }

    public Payment createPaymentFromRequestDTO(PaymentRequestDTO paymentRequest, Campaign campaign, Users user, String orderId) {

        Payment payment = new Payment();

        payment.setPayerFullName(paymentRequest.getFirstName() + " " + paymentRequest.getLastName());
        payment.setPaymentContactInfo(paymentRequest.getPaymentContactInfo());
        payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
        payment.setIsAnonymous(paymentRequest.getIsAnonymous());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency("ETB");
        payment.setPaymentProcessor(PaymentProcessor.CHAPA);
        payment.setOrderId(orderId);
        payment.setUser(user);
        payment.setCampaign(campaign);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        return payment;
    }

    /**
     * Handles a failed payment.
     *
     * @param payment The Payment object.
     * @return The API response.
     */
    public ResponseEntity handleFailedPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Currently, we can't process payments with " + payment.getPaymentProcessor());
    }

    /**
     * Updates a Payment object from a ChapaVerifyResponse object.
     *
     * @param payment             The Payment object to be updated.
     * @param chapaVerifyResponse The ChapaVerifyResponse object containing the updated payment details.
     */
    @Transactional
    public void updatePaymentFromChapaVerifyResponse(Payment payment, ChapaVerifyResponse chapaVerifyResponse) {
//        payment.setPaymentStatus(chapaVerifyResponse.getStatus().toUpperCase());
        payment.setTransactionCompletedDate(chapaVerifyResponse.getData().getUpdated_at());
        payment.setTransactionId(chapaVerifyResponse.getData().getReference());
        paymentRepository.save(payment);
    }

    /**
     * Updates a Payment object from a CooPassVerifyResponse object.
     *
     * @param payment               The Payment object to be updated.
     * @param cooPassVerifyResponse The CooPassVerifyResponse object containing the updated payment details.
     */
    @Transactional
    public void updatePaymentFromCooPassVerifyResponse(Payment payment, CooPassVerifyResponse cooPassVerifyResponse) {
//        payment.setPaymentStatus(cooPassVerifyResponse.getStatus().toUpperCase());
        payment.setTransactionCompletedDate(cooPassVerifyResponse.getData().getCompletedAt());
        payment.setTransactionId(cooPassVerifyResponse.getData().getTransactionId());
        paymentRepository.save(payment);
    }

    /**
     * Updates a Campaign object from a Payment object.
     *
     * @param payment The Payment object to be used to update the Campaign object.
     */
    @Transactional
    public void updateCampaignFromPayment(Payment payment) {
        Campaign campaign = payment.getCampaign();
        campaign.setNumberOfBackers(campaign.getNumberOfBackers() + 1);
        double amountCollected = campaign.getTotalAmountCollected() + (payment.getCurrency().equals("USD")
                ? payment.getAmount() * 53.90 : payment.getAmount());
        campaign.setTotalAmountCollected(amountCollected);
        campaignRepository.save(campaign);
    }

    /**
     * Generates a unique order ID based on the funding type.
     *
     * @param fundingType The type of funding for the order.
     * @return The unique order ID.
     */
    public String generateUniqueOrderId(String fundingType) {
        final String ALL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        final int LENGTH = 17;
        SecureRandom random = new SecureRandom();

        String orderId;
        do {
            StringBuilder stringBuilder = new StringBuilder(LENGTH);
            for (int i = 0; i < LENGTH; i++) {
                int randomIndex = random.nextInt(ALL_CHARS.length());
                stringBuilder.append(ALL_CHARS.charAt(randomIndex));
            }
            String randomString = stringBuilder.toString();

            orderId = switch (fundingType.toUpperCase()) {
                case "DONATION" -> "DN_" + randomString;
                case "EQUITY" -> "EQ_" + randomString;
                case "REWARD" -> "RW_" + randomString;
                default -> randomString;
            };

        } while (paymentRepository.findPaymentByOrderId(orderId).isPresent());

        return orderId;
    }


}
