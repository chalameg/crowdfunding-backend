package com.dxvalley.crowdfunding.payment;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.payment.chapa.ChapaRequestDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentAddDTO;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentUpdateDTO;
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

    @PostMapping("/chapaInitialize")
    public ResponseEntity<?> chapaInitialize(@RequestBody ChapaRequestDTO chapaRequest) {
        return new ResponseEntity<>(paymentService.chapaInitialize(chapaRequest), HttpStatus.OK);
    }

    @GetMapping("/chapaVerify/{orderId}")
    public ResponseEntity<?> chapaVerify(@PathVariable String orderId) {
        return new ResponseEntity<>(paymentService.chapaVerify(orderId), HttpStatus.OK);
    }
}
