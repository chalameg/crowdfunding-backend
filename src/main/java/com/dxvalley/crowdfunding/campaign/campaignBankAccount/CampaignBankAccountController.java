package com.dxvalley.crowdfunding.campaign.campaignBankAccount;

import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.AccountAddReq;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.AccountExistenceRes;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountRes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaignBankAccount")
public class CampaignBankAccountController {
    private final CampaignBankAccountService campaignBankAccountService;

    @GetMapping("/byAccountNumber/{accountNumber}")
    ResponseEntity<BankAccountRes> getCampaignBankAccountByCampaign(
            @PathVariable @Pattern(regexp = "^[0-9]{13}$",
                    message = "Account number must be 13 digits") String accountNumber) {
        BankAccountRes campaignBankAccount = campaignBankAccountService.getByAccountNumber(accountNumber);
        return ResponseEntity.ok(campaignBankAccount);
    }

    @PostMapping("/add")
    public ResponseEntity<CampaignBankAccount> addCampaignBankAccount(@RequestBody @Valid AccountAddReq accountAddReq) {
        CampaignBankAccount campaignBankAccount = campaignBankAccountService.addBankAccount(accountAddReq);
        return ResponseEntity.ok(campaignBankAccount);
    }

    @GetMapping("/checkBankAccountExistence/{bankAccount}")
    ResponseEntity<AccountExistenceRes> checkBankAccountExistence(@PathVariable String bankAccount) {
        AccountExistenceRes response = campaignBankAccountService.checkBankAccountExistence(bankAccount);
        return ResponseEntity.ok(response);
    }
}
