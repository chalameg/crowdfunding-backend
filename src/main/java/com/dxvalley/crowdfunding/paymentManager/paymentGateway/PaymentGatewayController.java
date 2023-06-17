package com.dxvalley.crowdfunding.paymentManager.paymentGateway;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment-gateways")
@RequiredArgsConstructor
public class PaymentGatewayController {
    private final PaymentGatewayService paymentGatewayService;

    @GetMapping
    public ResponseEntity<List<PaymentGateway>> getAllPaymentGateways() {
        return ResponseEntity.ok(paymentGatewayService.getAllPaymentGateways());
    }
}
