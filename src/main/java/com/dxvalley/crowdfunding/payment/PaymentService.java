package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.payment.ebirr.EbirrPaymentResponse;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO1;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentUpdateDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    Payment addPayment(PaymentRequestDTO1 paymentAddDTO);

    ResponseEntity updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO);

    List<Payment> getPaymentByCampaignId(Long campaignId);

    List<Payment> getPaymentByUserId(Long userId);

    ResponseEntity initializeChapaPayment(PaymentRequestDTO requestDTO);

    ResponseEntity verifyChapaPayment(String orderID);
    ResponseEntity initializeCooPassPayment(PaymentRequestDTO requestDTO);
    ResponseEntity verifyCooPassPayment(String orderID);

    CompletableFuture<Void> chapaPaymentStatusChecker();

    CompletableFuture<EbirrPaymentResponse> payWithEbirr(PaymentRequestDTO paymentRequest);

    void updatePaymentForEbirr(CompletableFuture<EbirrPaymentResponse> paymentFuture);
}
