package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.models.CampaignBankAccount;
import com.dxvalley.crowdfunding.repositories.CampaignBankAccountRepository;
import com.dxvalley.crowdfunding.services.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaignBankAccount")
public class CampaignBankAccountController {
    private final CampaignBankAccountRepository campaignBankAccountRepository;
    private final CampaignService campaignService;
    
    @GetMapping("/getCampaignBankAccountByCampaign/{campaignId}")
    CampaignBankAccount getCampaignBankAccountByCampaign(@PathVariable Long campaignId) {
        return campaignBankAccountRepository.findCampaignBankAccountByCampaignId(campaignId);
    }
    @PostMapping("/add/{campaignId}")
    public ResponseEntity<?> addCampaignBankAccount(@PathVariable Long campaignId, @RequestParam() String bankAccount) {
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
}
