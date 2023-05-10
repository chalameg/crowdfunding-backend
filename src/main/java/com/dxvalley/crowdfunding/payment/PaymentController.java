package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.payment.ebirr.EbirrPaymentResponse;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO1;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentUpdateDTO;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/getPaymentByCampaign/{campaignId}")
    public ResponseEntity<?> getPaymentByCampaign(@PathVariable Long campaignId) {
        return ApiResponse.success(paymentService.getPaymentByCampaignId(campaignId));
    }

    @GetMapping("/getPaymentByUser/{userId}")
    public ResponseEntity<?> getPaymentByUser(@PathVariable Long userId) {
        return ApiResponse.success(paymentService.getPaymentByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPayment(@RequestBody @Valid PaymentRequestDTO1 paymentAddDTO) {
        return ApiResponse.success(paymentService.addPayment(paymentAddDTO));
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updatePayment(@PathVariable String orderId, @RequestBody @Valid PaymentUpdateDTO paymentUpdateDTO) {
        return paymentService.updatePayment(orderId, paymentUpdateDTO);
    }

    @PostMapping("/chapaInitialize")
    public ResponseEntity<?> initializeChapaPayment(@RequestBody @Valid PaymentRequestDTO chapaRequest) {
        return paymentService.initializeChapaPayment(chapaRequest);
    }
    @PostMapping("/cooPassInitialize")
    public ResponseEntity<?> initializeCooPassPayment(@RequestBody @Valid PaymentRequestDTO paymentRequest) {
        return paymentService.initializeCooPassPayment(paymentRequest);
    }

    @GetMapping("/chapaVerify/{orderId}")
    public ResponseEntity<?> verifyChapaPayment(@PathVariable String orderId) {
        return paymentService.verifyChapaPayment(orderId);
    }
    @GetMapping("/cooPassVerify/{orderId}")
    public ResponseEntity<?> verifyCooPassPayment(@PathVariable String orderId) {
        return paymentService.verifyCooPassPayment(orderId);
    }

    @PostMapping("/ebirrPayment")
    public ResponseEntity<?> payWithEbirr(@RequestBody @Valid PaymentRequestDTO paymentRequest) {
        CompletableFuture<EbirrPaymentResponse> paymentFuture = paymentService.payWithEbirr(paymentRequest);
        paymentFuture.thenAcceptAsync(paymentSuccessful -> {
            paymentService.updatePaymentForEbirr(paymentFuture);
        });
        return ApiResponse.success("You will receive a USSD notification on your phone. Check it out to complete payment.");
    }
}
