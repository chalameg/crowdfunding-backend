package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.dto.PaymentDto;
import com.dxvalley.crowdfunding.models.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getPaymentByCampaignId(Long campaignId);
    Double totalAmountCollectedOnPlatform();
    Double totalAmountCollectedForCampaign(Long campaignId);
    public List<Payment> getPaymentByUserId(Long userId);
    Payment addPayment(PaymentDto paymentDto);
}
