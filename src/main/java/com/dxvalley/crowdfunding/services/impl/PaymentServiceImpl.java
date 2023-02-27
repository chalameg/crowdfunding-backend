package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.dto.PaymentDto;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.Payment;
import com.dxvalley.crowdfunding.repositories.CampaignRepository;
import com.dxvalley.crowdfunding.repositories.PaymentRepository;
import com.dxvalley.crowdfunding.services.PaymentService;
import com.dxvalley.crowdfunding.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CampaignRepository campaignRepository;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    UserService userService;

    @Override
    public Payment addPayment(PaymentDto paymentDto) {
        Payment payment = new Payment();
        Campaign campaign = campaignRepository.findCampaignByCampaignId(paymentDto.getCampaignId()).get();

        if (paymentDto.getUserId() != null)
            payment.setUser(userService.getUserById(paymentDto.getUserId()));
        payment.setCampaign(campaign);
        payment.setPayerFullName(paymentDto.getPayerFullName());
        payment.setIsAnonymous(paymentDto.getIsAnonymous());
        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentStatus(paymentDto.getPaymentStatus());
        payment.setOrderId(paymentDto.getOrderId());
        payment.setTransactionOrderDate(LocalDateTime.now().format(dateTimeFormatter));

        campaign.setNumberOfBackers(campaign.getNumberOfBackers() + 1);
        campaign.setTotalAmountCollected(campaign.getTotalAmountCollected() + paymentDto.getAmount());
        campaignRepository.save(campaign);

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentByCampaignId(Long campaignId) {
        var payments = paymentRepository.findPaymentByCampaignId(campaignId);
        if (payments.size() == 0) {
            throw new ResourceNotFoundException("Currently, there is no Payment for this Campaign.");
        }
        for (var payment : payments) {
            if (payment.getIsAnonymous() == true) payment.setPayerFullName("Anonymous");
        }
        return payments;
    }

    @Override
    public List<Payment> getPaymentByUserId(Long userId) {
        userService.getUserById(userId); // to check existence of user
        var payment = paymentRepository.findPaymentByUserId(userId);
        if (payment.size() == 0) {
            throw new ResourceNotFoundException("You have not contributed yet.");
        }
        return payment;
    }

    @Override
    public Double totalAmountCollectedOnPlatform() {
        return paymentRepository.findTotalAmountRaisedOnPlatform();
    }

    @Override
    public Double totalAmountCollectedForCampaign(Long campaignId) {
        return paymentRepository.findTotalAmountOfPaymentForCampaign(campaignId);
    }
}
