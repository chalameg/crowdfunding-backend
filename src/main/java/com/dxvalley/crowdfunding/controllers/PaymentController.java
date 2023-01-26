package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.Payment;
import com.dxvalley.crowdfunding.repositories.CampaignRepository;
import com.dxvalley.crowdfunding.repositories.PaymentRepository;
import com.dxvalley.crowdfunding.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final CampaignRepository campaignRepository;
    @GetMapping("/getPaymentByCampaign/{campaignId}")
    Payment getPaymentByCampaign(@PathVariable Long campaignId) {
        return paymentRepository.findPaymentByCampaignId(campaignId);
    }
    @PostMapping("/add/{campaignId}")
    public ResponseEntity<?> addPayment(@PathVariable Long campaignId, @RequestParam() String bankAccount) {
        Payment payment = new Payment();

        var result = paymentRepository.findPaymentByBankAccount(bankAccount);
        if (result != null){
            return new  ResponseEntity<>( bankAccount + " already registered", HttpStatus.BAD_REQUEST);
        }
        var camp =  paymentRepository.findPaymentByCampaignId(campaignId);
        if(camp != null){
            return new  ResponseEntity<>( "This campaign already have bank account. try updating", HttpStatus.BAD_REQUEST);
        }
        var campaign = campaignRepository.findCampaignByCampaignId(campaignId);
        if (campaign == null){
            return new  ResponseEntity<>( "No campaign with this id.", HttpStatus.BAD_REQUEST);
        }
        payment.setBankAccount(bankAccount);
        payment.setCampaign(campaign);
        return new ResponseEntity<>(paymentRepository.save(payment),HttpStatus.OK);
    }
}
