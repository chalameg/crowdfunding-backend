package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentMapper;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentResponse;
import com.dxvalley.crowdfunding.userManager.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentRetrievalServiceImpl implements PaymentRetrievalService {
    private final PaymentRepository paymentRepository;
    private final UserService userService;

    public PaymentRetrievalServiceImpl(final PaymentRepository paymentRepository, final UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    public List<PaymentResponse> getPaymentByCampaignId(Long campaignId) {
        List<Payment> payments = this.paymentRepository.findByCampaignIdAndPaymentStatus(campaignId, PaymentStatus.SUCCESS);
        return payments.stream().map(PaymentMapper::toPaymentResponse).toList();
    }

    public List<PaymentResponse> getPaymentByUserId(Long userId) {
        this.userService.getUserById(userId);
        List<Payment> payments = this.paymentRepository.findByUserUserId(userId);
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("You have not contributed yet.");
        } else {
            return payments.stream().map(PaymentMapper::toPaymentResponse).toList();
        }
    }
}