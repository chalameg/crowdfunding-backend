package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.chapa.ChapaVerifyResponse;
import com.dxvalley.crowdfunding.payment.cooPass.CooPassVerifyResponse;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO;
import com.dxvalley.crowdfunding.user.UserService;
import com.dxvalley.crowdfunding.user.Users;
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
    private final UserService userService;
    private final DateTimeFormatter dateTimeFormatter;

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findPaymentByOrderId(orderId).
                orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    public Campaign getCampaignById(Long campaignId) {
        return campaignRepository.findCampaignByCampaignId(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
    }

    public Users getUserById(Long userId) {
        return userId != null ? userService.utilGetUserByUserId(userId)
                : null;
    }

    /**
     * Creates a Payment object from a PaymentRequestDTO request.
     *
     * @param paymentRequest The PaymentRequestDTO request.
     * @param campaign     The campaign associated with the payment.
     * @param user         The user associated with the payment.
     * @return The Payment object.
     */
    public Payment createPaymentFromRequestDTO(PaymentRequestDTO paymentRequest, Campaign campaign, Users user, String orderId) {
        Payment payment = new Payment();
        payment.setPayerFullName(paymentRequest.getFirstName() + " " + paymentRequest.getLastName());
        payment.setPaymentContactInfo(paymentRequest.getPaymentContactInfo());
        payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
        payment.setIsAnonymous(paymentRequest.getIsAnonymous());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency("ETB");
        payment.setPaymentProcessor(paymentRequest.getPaymentProcessor());
        payment.setOrderId(orderId);
        payment.setUser(user);
        payment.setCampaign(campaign);

        return payment;
    }

    /**
     * Handles a failed payment.
     *
     * @param payment The Payment object.
     * @param e       The exception that caused the payment to fail.
     * @return The API response.
     */
    public ResponseEntity handleFailedPayment(Payment payment, Exception e) {
        payment.setPaymentStatus(PaymentStatus.FAILED.name());
        paymentRepository.save(payment);

        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Currently, we can't process payments with Chapa");
    }

    /**
     * Updates a Payment object from a ChapaVerifyResponse object.
     * @param payment             The Payment object to be updated.
     * @param chapaVerifyResponse The ChapaVerifyResponse object containing the updated payment details.
     */
    @Transactional
    public void updatePaymentFromChapaVerifyResponse(Payment payment, ChapaVerifyResponse chapaVerifyResponse) {
        payment.setPaymentStatus(chapaVerifyResponse.getStatus().toUpperCase());
        payment.setTransactionCompletedDate(chapaVerifyResponse.getData().getUpdated_at());
        payment.setTransactionId(chapaVerifyResponse.getData().getReference());
        paymentRepository.save(payment);
    }

    /**
     * Updates a Payment object from a CooPassVerifyResponse object.
     * @param payment             The Payment object to be updated.
     * @param cooPassVerifyResponse The CooPassVerifyResponse object containing the updated payment details.
     */
    @Transactional
    public void updatePaymentFromCooPassVerifyResponse(Payment payment, CooPassVerifyResponse cooPassVerifyResponse) {
        payment.setPaymentStatus(cooPassVerifyResponse.getStatus().toUpperCase());
        payment.setTransactionCompletedDate(cooPassVerifyResponse.getData().getCompletedAt());
        payment.setTransactionId(cooPassVerifyResponse.getData().getTransactionId());
        paymentRepository.save(payment);
    }

    /**
     * Updates a Campaign object from a Payment object.
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
