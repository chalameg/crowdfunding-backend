package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.model.Campaign;
import com.dxvalley.crowdfunding.model.Users;
import com.dxvalley.crowdfunding.payment.chapa.ChapaInitializeResponse;
import com.dxvalley.crowdfunding.payment.chapa.ChapaRequestDTO;
import com.dxvalley.crowdfunding.payment.chapa.ChapaService;
import com.dxvalley.crowdfunding.payment.chapa.ChapaVerifyResponse;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentAddDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentUpdateDTO;
import com.dxvalley.crowdfunding.repository.CampaignRepository;
import com.dxvalley.crowdfunding.repository.UserRepository;
import com.dxvalley.crowdfunding.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CampaignRepository campaignRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private DateTimeFormatter dateTimeFormatter;
    @Autowired
    ChapaService chapaService;
    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public Payment addPayment(PaymentAddDTO paymentAddDTO) {
        try {
            Campaign campaign = campaignRepository.findCampaignByCampaignId(paymentAddDTO.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
            Users user = paymentAddDTO.getUserId() != null ?
                    userRepository.findById(paymentAddDTO.getUserId()).get() : null;

            Payment payment = new Payment();
            payment.setOrderId(generateUniqueOrderId(campaign.getFundingType().getName()));
            payment.setUser(user);
            payment.setPayerFullName(user != null ? user.getFullName() : null);
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
            var amount_collected = campaign.getTotalAmountCollected() + (payment.getCurrency().equals("USD")
                    ? payment.getAmount() * 53.90 : payment.getAmount());
            campaign.setTotalAmountCollected(amount_collected);
            paymentRepository.save(payment);
            campaignRepository.save(campaign);

            logger.info("payment updated");
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
        try {
            var amount = paymentRepository.findTotalAmountRaisedOnPlatform();
            logger.info("getting total Amount collected on platform = {} ", amount);
            return amount;
        } catch (DataAccessException ex) {
            logger.error("Error Getting totalAmountCollectedOnPlatform: {}", ex.getMessage());
            throw new RuntimeException("Error Getting totalAmountCollectedOnPlatform", ex);
        }
    }

    @Override
    public Double totalAmountCollectedForCampaign(Long campaignId) {
        try {
            var amount = paymentRepository.findTotalAmountOfPaymentForCampaign(campaignId);
            logger.info("getting total Amount collected for campaign = {} ", amount);
            return amount;
        } catch (DataAccessException ex) {
            logger.error("Error Getting total amount collected for campaign: {}", ex.getMessage());
            throw new RuntimeException("Error Getting total amount collected for campaign", ex);
        }
    }


    @Override
    public Object chapaInitialize(ChapaRequestDTO chapaRequest) {
        try {
            Campaign campaign = campaignRepository.findCampaignByCampaignId(chapaRequest.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
            Users user = chapaRequest.getUserId() != null ?
                    userRepository.findById(chapaRequest.getUserId()).get() : null;

            String orderId = generateUniqueOrderId(campaign.getFundingType().getName());

            chapaRequest.setOrderId(orderId);
            ChapaInitializeResponse chapaInitializeResponse = chapaService.chapaInitialize(chapaRequest);

            Payment payment = new Payment();
            payment.setPayerFullName(chapaRequest.getFirst_name() + " " + chapaRequest.getLast_name());
            payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
            payment.setPaymentStatus("PENDING");
            payment.setIsAnonymous(chapaRequest.getIsAnonymous());
            payment.setAmount(chapaRequest.getAmount());
            payment.setCurrency("ETB");
            payment.setPaymentProcessor(PaymentProcessor.CHAPA);
            payment.setOrderId(orderId);
            payment.setUser(user);
            payment.setCampaign(campaign);
            paymentRepository.save(payment);

            return chapaInitializeResponse;
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public Object chapaVerify(String orderID) {
        try {
            Payment payment = paymentRepository.findPaymentByOrderId(orderID).
                    orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

            ChapaVerifyResponse chapaVerifyResponse = chapaService.chapaVerify(orderID);

            payment.setPaymentStatus(chapaVerifyResponse.getStatus());
            payment.setTransactionCompletedDate(chapaVerifyResponse.getData().getUpdated_at());

            var campaign = payment.getCampaign();
            campaign.setNumberOfBackers(campaign.getNumberOfBackers() + 1);
            var amount_collected = campaign.getTotalAmountCollected() + (payment.getCurrency().equals("USD")
                    ? payment.getAmount() * 53.90 : payment.getAmount());
            campaign.setTotalAmountCollected(amount_collected);
            paymentRepository.save(payment);
            campaignRepository.save(campaign);

            return new ApiResponse("success", "Transaction completed successfully");
        } catch (Exception e) {
            return new ApiResponse("error", "Transaction is failed.");
        }
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

