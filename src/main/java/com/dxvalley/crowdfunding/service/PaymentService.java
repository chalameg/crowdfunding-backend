package com.dxvalley.crowdfunding.service;

import com.dxvalley.crowdfunding.dto.PaymentAddDTO;
import com.dxvalley.crowdfunding.dto.PaymentUpdateDTO;
import com.dxvalley.crowdfunding.model.Payment;

import java.util.List;

public interface PaymentService {
    Payment addPayment(PaymentAddDTO paymentAddDTO);

    void updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO);

    List<Payment> getPaymentByCampaignId(Long campaignId);

    List<Payment> getPaymentByUserId(Long userId);

    Double totalAmountCollectedOnPlatform();

    Double totalAmountCollectedForCampaign(Long campaignId);


}
