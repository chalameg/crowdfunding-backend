package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.payment.chapa.ChapaRequestDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentAddDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentUpdateDTO;

import java.util.List;

public interface PaymentService {
    Payment addPayment(PaymentAddDTO paymentAddDTO);

    void updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO);

    List<Payment> getPaymentByCampaignId(Long campaignId);

    List<Payment> getPaymentByUserId(Long userId);

    Double totalAmountCollectedOnPlatform();

    Double totalAmountCollectedForCampaign(Long campaignId);

    Object chapaInitialize(ChapaRequestDTO chapaRequest);

    Object chapaVerify(String orderID);
}
