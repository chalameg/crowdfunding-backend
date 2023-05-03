package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.payment.chapa.ChapaRequestDTO;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrPaymentResponse;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrRequestDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentAddDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentUpdateDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    Payment addPayment(PaymentAddDTO paymentAddDTO);

    ResponseEntity updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO);

    List<Payment> getPaymentByCampaignId(Long campaignId);

    List<Payment> getPaymentByUserId(Long userId);

    ResponseEntity initializeChapaPayment(ChapaRequestDTO requestDTO);

    ResponseEntity verifyChapaPayment(String orderID);

    CompletableFuture<EbirrPaymentResponse> payWithEbirr(EbirrRequestDTO ebirrRequestDTO);

    void updatePaymentForEbirr(CompletableFuture<EbirrPaymentResponse> paymentFuture);
}
