package com.dxvalley.crowdfunding.payment.paymentGateway;

import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-gateways")
@RequiredArgsConstructor
public class PaymentGatewayController {
    private final PaymentGatewayService paymentGatewayService;

    @GetMapping
    public ResponseEntity<?> getAllPaymentGateways() {
        return ApiResponse.success(paymentGatewayService.getAllPaymentGateways());
    }
}
