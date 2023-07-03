package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentMapper;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentResponse;
import com.dxvalley.crowdfunding.utils.CurrentLoggedInUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentRetrievalServiceImpl implements PaymentRetrievalService {
    private final PaymentRepository paymentRepository;
    private final CurrentLoggedInUser currentLoggedInUser;


    public List<PaymentResponse> getPaymentByCampaignId(Long campaignId) {
        List<Payment> payments = this.paymentRepository.findByCampaignIdAndPaymentStatus(campaignId, PaymentStatus.SUCCESS);
        return payments.stream().map(PaymentMapper::toPaymentResponse).toList();
    }

    public List<PaymentResponse> getPaymentByMe() {
        String username = currentLoggedInUser.getUserName();

        List<Payment> payments = paymentRepository.findByUserUsernameAndPaymentStatus(username, PaymentStatus.SUCCESS);
        if (payments.isEmpty())
            throw new ResourceNotFoundException("You have not made any contributions yet.");

        return payments.stream().map(PaymentMapper::toPaymentResponse).toList();
    }
}
