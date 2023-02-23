package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.models.CampaignBankAccount;
import com.dxvalley.crowdfunding.repositories.CampaignBankAccountRepository;
import com.dxvalley.crowdfunding.services.CampaignBankAccountService;
import com.dxvalley.crowdfunding.services.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaignBankAccount")
public class CampaignBankAccountController {
    @Autowired
    private CampaignBankAccountRepository campaignBankAccountRepository;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    CampaignBankAccountService campaignBankAccountService;
    
    @GetMapping("/getCampaignBankAccountByCampaign/{campaignId}")
    CampaignBankAccount getCampaignBankAccountByCampaign(@PathVariable Long campaignId) {
        campaignBankAccountService.getCampaignBankAccountByCampaignId(campaignId);

        return campaignBankAccountRepository.findCampaignBankAccountByCampaignId(campaignId);
    }

    @PostMapping("/add/{campaignId}")
    public ResponseEntity<?> addCampaignBankAccount(
            @PathVariable Long campaignId,
            @RequestParam String bankAccount) {

        campaignBankAccountService.addBankAccount(campaignId, bankAccount);

        CampaignBankAccount campaignBankAccount = new CampaignBankAccount();

        // var result = campaignBankAccountRepository.findCampaignBankAccountByBankAccount(bankAccount);
        // if (result != null){
        //     return new  ResponseEntity<>( bankAccount + " already registered", HttpStatus.BAD_REQUEST);
        // }
        var camp =  campaignBankAccountRepository.findCampaignBankAccountByCampaignId(campaignId);
        if(camp != null){
            return new  ResponseEntity<>( "This campaign already have bank account. try updating", HttpStatus.BAD_REQUEST);
        }
        var campaign = campaignService.getCampaignById(campaignId);

        campaignBankAccount.setBankAccount(bankAccount);
        campaignBankAccount.setCampaign(campaign);
        return new ResponseEntity<>(campaignBankAccountRepository.save(campaignBankAccount),HttpStatus.OK);
    }
    @GetMapping("/checkBankAccountExistence/{bankAccount}")
    ResponseEntity<?> checkBankAccountExistence(@PathVariable String bankAccount) throws Exception {
       var response =  campaignBankAccountService.checkBankAccountExistence(bankAccount);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
