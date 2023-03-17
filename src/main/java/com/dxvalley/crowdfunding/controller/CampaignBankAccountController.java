package com.dxvalley.crowdfunding.controller;

import com.dxvalley.crowdfunding.service.CampaignBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaignBankAccount")
public class CampaignBankAccountController {
    @Autowired
    CampaignBankAccountService campaignBankAccountService;

    @GetMapping("/getCampaignBankAccountByCampaign/{campaignId}")
    ResponseEntity<?> getCampaignBankAccountByCampaign(@PathVariable Long campaignId) {
        var campaignBankAccount = campaignBankAccountService.getCampaignBankAccountByCampaignId(campaignId);
        return new ResponseEntity<>(campaignBankAccount, HttpStatus.OK);
    }

    @PostMapping("/add/{campaignId}/{bankAccount}")
    public ResponseEntity<?> addCampaignBankAccount(
            @PathVariable Long campaignId,
            @PathVariable String bankAccount) {
        var campaignBankAccount = campaignBankAccountService.addBankAccount(campaignId, bankAccount);
        return new ResponseEntity<>(campaignBankAccount, HttpStatus.OK);
    }

    @GetMapping("/checkBankAccountExistence/{bankAccount}")
    ResponseEntity<?> checkBankAccountExistence(@PathVariable String bankAccount) throws Exception {
        var response = campaignBankAccountService.checkBankAccountExistence(bankAccount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
