package com.dxvalley.crowdfunding.admin.paymentGateway;

import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentGateway;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.dto.PaymentGatewayReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/payment-gateways")
@RequiredArgsConstructor
public class AdminPaymentGatewayController {
    private final AdminPaymentGatewayService paymentGatewayService;

    @GetMapping
    public ResponseEntity<List<PaymentGateway>> getAllPaymentGateways() {
        return ResponseEntity.ok(paymentGatewayService.getAllPaymentGateways());
    }

    @PutMapping
    public ResponseEntity<PaymentGateway> setPaymentGatewayStatus(@RequestBody @Valid PaymentGatewayReq paymentGatewayReq) {
        return ResponseEntity.ok(paymentGatewayService.setPaymentGatewayStatus(paymentGatewayReq));
    }
}
