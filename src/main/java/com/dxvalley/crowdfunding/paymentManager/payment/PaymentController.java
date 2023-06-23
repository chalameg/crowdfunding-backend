package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.paymentManager.chapa.ChapaPaymentResponse;
import com.dxvalley.crowdfunding.paymentManager.chapa.VerifyResponse;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.*;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/payment"})
public class PaymentController {
    private final PaymentRetrievalService paymentRetrievalService;
    private final PaymentOperationService paymentOperationService;

    public PaymentController(final PaymentRetrievalService paymentRetrievalService, final PaymentOperationService paymentOperationService) {
        this.paymentRetrievalService = paymentRetrievalService;
        this.paymentOperationService = paymentOperationService;
    }

    @GetMapping({"/getPaymentByCampaign/{campaignId}"})
    public ResponseEntity<List<PaymentResponse>> getPaymentByCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(this.paymentRetrievalService.getPaymentByCampaignId(campaignId));
    }

    @GetMapping({"/getPaymentByUser/{userId}"})
    public ResponseEntity<List<PaymentResponse>> getPaymentByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(this.paymentRetrievalService.getPaymentByUserId(userId));
    }

    @PostMapping({"/payWithEbirr"})
    public ResponseEntity<ApiResponse> payWithEbirr(@RequestBody @Valid EbirrPaymentReqDTO ebirrPaymentReqDTO) {
        return this.paymentOperationService.processPaymentWithEbirr(ebirrPaymentReqDTO);
    }

    @PostMapping({"/add"})
    public ResponseEntity<?> addPayment(@RequestBody @Valid PaymentRequestDTO1 paymentAddDTO) {
        return this.paymentOperationService.addPayment(paymentAddDTO);
    }

    @PutMapping({"/update/{orderId}"})
    public ResponseEntity<?> updatePayment(@PathVariable String orderId, @RequestBody @Valid PaymentUpdateDTO paymentUpdateDTO) {
        return this.paymentOperationService.updatePayment(orderId, paymentUpdateDTO);
    }

    @PostMapping({"/chapaInitialize"})
    public ResponseEntity<ChapaPaymentResponse> initializeChapaPayment(@RequestBody @Valid ChapaDTO chapaRequest) {
        return this.paymentOperationService.initializeChapaPayment(chapaRequest);
    }

    @PutMapping({"/chapaVerify/{orderId}"})
    public ResponseEntity<?> verifyChapaPayment(@PathVariable String orderId, @RequestBody VerifyResponse verifyResponse) {
        return this.paymentOperationService.updatePaymentStatusForChapa(orderId, verifyResponse);
    }

    @PostMapping({"/cooPassInitialize"})
    public ResponseEntity<?> initializeCooPassPayment(@RequestBody @Valid PaymentRequestDTO paymentRequest) {
        return this.paymentOperationService.initializeCooPassPayment(paymentRequest);
    }

    @GetMapping({"/cooPassVerify/{orderId}"})
    public ResponseEntity<?> verifyCooPassPayment(@PathVariable String orderId) {
        return this.paymentOperationService.verifyCooPassPayment(orderId);
    }
}
