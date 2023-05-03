package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.payment.chapa.ChapaRequestDTO;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrPaymentResponse;
import com.dxvalley.crowdfunding.payment.ebirr.EbirrRequestDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentAddDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentUpdateDTO;
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
    public ResponseEntity<?> addPayment(@RequestBody @Valid PaymentAddDTO paymentAddDTO) {
        return ApiResponse.success(paymentService.addPayment(paymentAddDTO));
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updatePayment(@PathVariable String orderId, @RequestBody @Valid PaymentUpdateDTO paymentUpdateDTO) {
        return paymentService.updatePayment(orderId, paymentUpdateDTO);
    }

    @PostMapping("/chapaInitialize")
    public ResponseEntity<?> initializeChapaPayment(@RequestBody @Valid ChapaRequestDTO chapaRequest) {
        return paymentService.initializeChapaPayment(chapaRequest);
    }

    @GetMapping("/chapaVerify/{orderId}")
    public ResponseEntity<?> verifyChapaPayment(@PathVariable String orderId) {
        return paymentService.verifyChapaPayment(orderId);
    }

    @PostMapping("/ebirrPayment")
    public ResponseEntity<?> payWithEbirr(@RequestBody @Valid EbirrRequestDTO ebirrRequestDTO) {
        CompletableFuture<EbirrPaymentResponse> paymentFuture = paymentService.payWithEbirr(ebirrRequestDTO);
        paymentFuture.thenAcceptAsync(paymentSuccessful -> {
            paymentService.updatePaymentForEbirr(paymentFuture);
        });
        return ApiResponse.success("You will receive a USSD notification on your phone. Check it out to complete payment.");
    }
}
