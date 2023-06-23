package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.exception.customException.ForbiddenException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.paymentManager.cooPass.CooPassVerifyResponse;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.ChapaDTO;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.EbirrPaymentReqDTO;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentRequestDTO;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentGatewayService;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentProcessor;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentUtils {
    private final PaymentRepository paymentRepository;
    private final CampaignRepository campaignRepository;
    private final UserUtils userUtils;
    private final PaymentGatewayService paymentGatewayService;
    private final DateTimeFormatter dateTimeFormatter;

    public Payment getPaymentByOrderId(String orderId) {
        return this.paymentRepository.findPaymentByOrderId(orderId).orElseThrow(() -> {
            return new ResourceNotFoundException("Payment not found");
        });
    }

    public Campaign getCampaignById(Long campaignId) {
        return this.campaignRepository.findById(campaignId).orElseThrow(() -> {
            return new ResourceNotFoundException("Campaign not found");
        });
    }

    public Users getUserById(Long userId) {
        return userId != null ? this.userUtils.utilGetUserByUserId(userId) : null;
    }

    //Validates the preconditions for payment processing.
    public void validatePaymentPreconditions(Long campaignId, PaymentProcessor paymentProcessor) {
        Campaign campaign = this.getCampaignById(campaignId);
        boolean isActive = this.paymentGatewayService.isPaymentGatewayActive(paymentProcessor.name());
        if (!isActive) {
            throw new ForbiddenException("The payment gateway is currently unavailable. Please try again later.");
        } else if (campaign.getCampaignStage() != CampaignStage.FUNDING) {
            throw new ForbiddenException("This campaign is not accepting payments at the moment. Please check back later.");
        }
    }


    // Creates a Payment object from a EbirrPaymentReqDTO request.
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


    public Payment createPaymentFromChapaPaymentReqDTO(ChapaDTO chapaDTO) {
        Campaign campaign = this.getCampaignById(chapaDTO.getCampaignId());
        Users user = this.getUserById(chapaDTO.getUserId());
        String orderId = this.generateUniqueOrderId(campaign.getFundingType().getName());

        Payment payment = new Payment();

        payment.setPayerFullName(chapaDTO.getFirstName() + " " + chapaDTO.getLastName());
        payment.setPaymentContactInfo(chapaDTO.getEmail());
        payment.setTransactionOrderedDate(LocalDateTime.now().format(this.dateTimeFormatter));
        payment.setIsAnonymous(chapaDTO.isAnonymous());
        payment.setAmount(chapaDTO.getAmount());
        payment.setCurrency("ETB");
        payment.setPaymentProcessor(PaymentProcessor.CHAPA);
        payment.setOrderId(orderId);
        payment.setUser(user);
        payment.setCampaign(campaign);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return payment;
    }


    // Creates a Payment object from a ChapaPaymentReqDTO request.
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
        payment.setTransactionOrderedDate(LocalDateTime.now().format(this.dateTimeFormatter));
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

    public void handleFailedPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        this.paymentRepository.save(payment);
    }

    @Transactional
    public void updatePaymentFromCooPassVerifyResponse(Payment payment, CooPassVerifyResponse cooPassVerifyResponse) {
        payment.setTransactionCompletedDate(cooPassVerifyResponse.getData().getCompletedAt());
        payment.setTransactionId(cooPassVerifyResponse.getData().getTransactionId());
        this.paymentRepository.save(payment);
    }

    @Transactional
    public double updateCampaignFromPayment(Payment payment) {
        Campaign campaign = payment.getCampaign();
        List<Payment> payments = this.paymentRepository.findByCampaignIdAndPaymentStatus(campaign.getId(), PaymentStatus.SUCCESS);
        if (payments.isEmpty()) {
            return 0.0;
        } else {
            double totalAmountCollected = payments.stream().mapToDouble((pay) -> {
                return pay.getCurrency().equals("USD") ? pay.getAmount() * 54.0 : pay.getAmount();
            }).sum();
            campaign.setNumberOfBackers(campaign.getNumberOfBackers() + 1);
            campaign.setTotalAmountCollected(totalAmountCollected);
            this.campaignRepository.save(campaign);
            return totalAmountCollected;
        }
    }


    // Generates a unique order ID based on the funding type.
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
