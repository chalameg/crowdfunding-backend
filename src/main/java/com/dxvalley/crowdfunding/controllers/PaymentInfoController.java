package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.dto.PaymentInfoDto;
import com.dxvalley.crowdfunding.models.PaymentInfo;
import com.dxvalley.crowdfunding.services.PaymentInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paymentInfo")
public class PaymentInfoController {
    @Autowired
    PaymentInfoService paymentInfoService;

    @GetMapping("/getPaymentInfoByCampaign/{campaignId}")
    public ResponseEntity<?> getPaymentInfoByCampaign(@PathVariable Long campaignId) {
        List<PaymentInfo> paymentInfo = paymentInfoService.getPaymentInfoByCampaignId(campaignId);
        return new ResponseEntity<>(paymentInfo, HttpStatus.OK);
    }

    @GetMapping("/getPaymentInfoByUser/{userId}")
    public ResponseEntity<?> getPaymentInfoByUser(@PathVariable Long userId) {
        List<PaymentInfo> paymentInfo = paymentInfoService.getPaymentInfoByUserId(userId);
        return new ResponseEntity<>(paymentInfo, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCampaign(@RequestBody @Valid PaymentInfoDto paymentInfoDto) {
        PaymentInfo paymentInfo = paymentInfoService.addPaymentInfo(paymentInfoDto);
        return new ResponseEntity<>(paymentInfo, HttpStatus.CREATED);
    }

}
