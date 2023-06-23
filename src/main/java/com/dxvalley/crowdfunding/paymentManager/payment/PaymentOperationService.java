package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.paymentManager.chapa.VerifyResponse;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.*;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentOperationService {
    ResponseEntity<ApiResponse> processPaymentWithEbirr(EbirrPaymentReqDTO ebirrPaymentReqDTO);

    ResponseEntity<?> addPayment(PaymentRequestDTO1 paymentAddDTO);

    ResponseEntity<?> updatePayment(String orderId, PaymentUpdateDTO paymentUpdateDTO);

    ResponseEntity initializeChapaPayment(ChapaDTO chapaDTO);

    ResponseEntity updatePaymentStatusForChapa(String orderID, VerifyResponse verifyResponse);

    ResponseEntity initializeCooPassPayment(PaymentRequestDTO requestDTO);

    ResponseEntity verifyCooPassPayment(String orderID);
}