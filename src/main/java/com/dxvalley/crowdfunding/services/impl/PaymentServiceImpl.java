package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.dto.PaymentAddDTO;
import com.dxvalley.crowdfunding.dto.PaymentUpdateDTO;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.Payment;
import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.repositories.CampaignRepository;
import com.dxvalley.crowdfunding.repositories.PaymentRepository;
import com.dxvalley.crowdfunding.services.PaymentService;
import com.dxvalley.crowdfunding.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.security.SecureRandom;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CampaignRepository campaignRepository;
    @Autowired
    UserService userService;
    @Autowired
    private DateTimeFormatter dateTimeFormatter;
    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public Payment addPayment(PaymentAddDTO paymentAddDTO) {
        try {
            Campaign campaign = campaignRepository.findCampaignByCampaignId(paymentAddDTO.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
            Users user = paymentAddDTO.getUserId() != null ? userService.getUserById(paymentAddDTO.getUserId()) : null;

            Payment payment = new Payment();
            payment.setOrderId(generateUniqueOrderId(campaign.getFundingType().getName()));
            payment.setUser(user);
            payment.setCampaign(campaign);
            payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
            payment.setPaymentStatus("PENDING");
            payment.setIsAnonymous(paymentAddDTO.getIsAnonymous());
            payment.setAmount(paymentAddDTO.getAmount());
            payment.setCurrency(paymentAddDTO.getCurrency());
            payment.setCampaign(campaign);
            paymentRepository.save(payment);

            logger.info("Adding payment");
            return new Payment(payment.getOrderId(), payment.getTransactionOrderedDate());
        } catch (DataAccessException ex) {
            logger.error("Error Adding payment: {}", ex.getMessage());
            throw new RuntimeException("Error Adding payment ", ex);
        }
    }

    @Override
    public void updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO) {
        try {
            Payment payment = paymentRepository.findPaymentByOrderId(orderId).
                    orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

            payment.setPayerFullName(paymentUpdateDTO.getPayerFullName());
            payment.setTransactionId(paymentUpdateDTO.getTransactionId());
            payment.setPaymentStatus(paymentUpdateDTO.getPaymentStatus());
            payment.setTransactionCompletedDate(LocalDateTime.now().format(dateTimeFormatter));

            var campaign = payment.getCampaign();
            campaign.setNumberOfBackers(campaign.getNumberOfBackers() + 1);
            campaign.setTotalAmountCollected(campaign.getTotalAmountCollected() + payment.getAmount());

            logger.info("payment updated");
            campaignRepository.save(campaign);
        } catch (DataAccessException ex) {
            logger.error("Error Updating payment: {}", ex.getMessage());
            throw new RuntimeException("Error Updating payment ", ex);
        }
    }

    @Override
    public List<Payment> getPaymentByCampaignId(Long campaignId) {
        try {

            List<Payment> payments = paymentRepository.findPaymentsByCampaignCampaignIdAndPaymentStatus(campaignId, "COMPLETED");
            payments.stream().filter(payment -> payment.getIsAnonymous())
                    .forEach(payment -> payment.setPayerFullName("Anonymous"));

            logger.info("Retrieved {} Payment by Campaign", payments.size());
            return payments;
        } catch (DataAccessException ex) {
            logger.error("Error Getting Payment By Campaign: {}", ex.getMessage());
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
            logger.info("Retrieved {} Payment by User", payments.size());
            return payments;
        } catch (DataAccessException ex) {
            logger.error("Error Getting Payment By User: {}", ex.getMessage());
            throw new RuntimeException("Error Getting Payment By User ", ex);
        }
    }

    @Override
    public Double totalAmountCollectedOnPlatform() {
        return paymentRepository.findTotalAmountRaisedOnPlatform();
    }

    @Override
    public Double totalAmountCollectedForCampaign(Long campaignId) {
        return paymentRepository.findTotalAmountOfPaymentForCampaign(campaignId);
    }

    private String generateUniqueOrderId(String fundingType) {
        final String ALL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        final int LENGTH = 13;
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
