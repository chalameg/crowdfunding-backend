package com.dxvalley.crowdfunding.admin.paymentGateway;

import com.dxvalley.crowdfunding.payment.paymentGateway.dto.PaymentGatewayDTO;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/payment-gateways")
@RequiredArgsConstructor
public class AdminPaymentGatewayController {
    private final AdminPaymentGatewayService paymentGatewayService;

    @GetMapping
    public ResponseEntity<?> getAllPaymentGateways() {
        return ApiResponse.success(paymentGatewayService.getAllPaymentGateways());
    }
    @PutMapping
    public ResponseEntity<?> setPaymentGatewayStatus(@RequestBody @Valid PaymentGatewayDTO paymentGatewayDTO) {
        return ApiResponse.success(paymentGatewayService.setPaymentGatewayStatus(paymentGatewayDTO));
    }
}
