package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.payment.paymentDTO.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    ResponseEntity processPaymentWithEbirr(EbirrPaymentReqDTO ebirrPaymentReqDTO);

    ResponseEntity<?> addPayment(PaymentRequestDTO1 paymentAddDTO);

    ResponseEntity<?> updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO);

    List<PaymentResponse> getPaymentByCampaignId(Long campaignId);

    List<Payment> getPaymentByUserId(Long userId);

    ResponseEntity initializeChapaPayment(PaymentRequestDTO paymentRequest);

    ResponseEntity verifyChapaPayment(String orderID);

    ResponseEntity initializeCooPassPayment(PaymentRequestDTO requestDTO);

    ResponseEntity verifyCooPassPayment(String orderID);

    CompletableFuture<Void> chapaPaymentStatusChecker();

}
