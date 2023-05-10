package com.dxvalley.crowdfunding.campaign.campaignBankAccount;

import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountDTO;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaignBankAccount")
public class CampaignBankAccountController {
    private final CampaignBankAccountService campaignBankAccountService;

    @GetMapping("/getCampaignBankAccountByCampaign/{campaignId}")
    ResponseEntity<?> getCampaignBankAccountByCampaign(@PathVariable Long campaignId) {
        BankAccountDTO campaignBankAccount = campaignBankAccountService.getCampaignBankAccountByCampaignId(campaignId);
        return ApiResponse.success(campaignBankAccount);
    }

    @PostMapping("/add/{campaignId}/{bankAccount}")
    public ResponseEntity<?> addCampaignBankAccount(@PathVariable Long campaignId, @PathVariable String bankAccount) {
        BankAccountDTO campaignBankAccount = campaignBankAccountService.addBankAccount(campaignId, bankAccount);
        return ApiResponse.success(campaignBankAccount);
    }

    @GetMapping("/checkBankAccountExistence/{bankAccount}")
    ResponseEntity<?> checkBankAccountExistence(@PathVariable String bankAccount){
        var response = campaignBankAccountService.checkBankAccountExistence(bankAccount);
        return ApiResponse.success(response);
    }
}
