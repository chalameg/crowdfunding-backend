package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.PaymentAddDTO;
import com.dxvalley.crowdfunding.dto.PaymentUpdateDTO;
import com.dxvalley.crowdfunding.models.Payment;
import com.dxvalley.crowdfunding.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @GetMapping("/getPaymentByCampaign/{campaignId}")
    public ResponseEntity<?> getPaymentByCampaign(@PathVariable Long campaignId) {
        List<Payment> payment = paymentService.getPaymentByCampaignId(campaignId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping("/getPaymentByUser/{userId}")
    public ResponseEntity<?> getPaymentByUser(@PathVariable Long userId) {
        List<Payment> payment = paymentService.getPaymentByUserId(userId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCampaign(@RequestBody @Valid PaymentAddDTO paymentAddDTO) {
        Payment payment = paymentService.addPayment(paymentAddDTO);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updateCampaign(@PathVariable String orderId, @RequestBody @Valid PaymentUpdateDTO paymentUpdateDTO) {
        paymentService.updatePayment(orderId, paymentUpdateDTO);
        ApiResponse response = new ApiResponse("success", "Payment Updated Successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
