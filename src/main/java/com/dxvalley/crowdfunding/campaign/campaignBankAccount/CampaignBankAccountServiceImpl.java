package com.dxvalley.crowdfunding.campaign.campaignBankAccount;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.AccountAddReq;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.AccountExistenceRes;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountMapper;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountRes;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignBankAccountServiceImpl implements CampaignBankAccountService {
    private final CampaignBankAccountRepository campaignBankAccountRepository;
    private final CampaignUtils campaignUtils;
    private final DateTimeFormatter dateTimeFormatter;
    private final RestTemplate restTemplate;
    @Value("${APP_CONNECT.CHECK_BANK_ACCOUNT_URI}")
    private String checkBankAccountURI;

    public BankAccountRes getByAccountNumber(String accountNumber) {
        CampaignBankAccount campaignBankAccount = (CampaignBankAccount)this.campaignBankAccountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> {
            return new ResourceNotFoundException("Bank Account is not Found.");
        });
        List<Campaign> campaigns = this.campaignUtils.getCampaignsByBankAccount(accountNumber);
        return BankAccountMapper.toBankAccountDTO(campaignBankAccount, campaigns);
    }

    public CampaignBankAccount addBankAccount(AccountAddReq accountAddReq) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(accountAddReq.getCampaignId());
        String accountNumber = accountAddReq.getAccountNumber();
        String accountOwner = accountAddReq.getAccountOwner();
        this.validateBankAccount(accountNumber, campaign);
        CampaignBankAccount campaignBankAccount = this.getOrCreateCampaignBankAccount(accountNumber, accountOwner);
        campaign.setBankAccount(campaignBankAccount);
        this.campaignUtils.saveCampaign(campaign);
        return campaignBankAccount;
    }

    private void validateBankAccount(String accountNumber, Campaign campaign) {
        if (campaign.getBankAccount() != null && campaign.getBankAccount().getAccountNumber().equals(accountNumber)) {
            throw new ResourceAlreadyExistsException(accountNumber + " is already set up for this campaign.");
        }
    }

    private CampaignBankAccount getOrCreateCampaignBankAccount(String accountNumber, String accountOwner) {
        Optional<CampaignBankAccount> optionalCampaignBankAccount = this.campaignBankAccountRepository.findByAccountNumber(accountNumber);
        return (CampaignBankAccount)optionalCampaignBankAccount.orElseGet(() -> {
            return this.createCampaignBankAccount(accountNumber, accountOwner);
        });
    }

    private CampaignBankAccount createCampaignBankAccount(String accountNumber, String accountOwner) {
        CampaignBankAccount newBankAccount = new CampaignBankAccount();
        newBankAccount.setAccountNumber(accountNumber);
        newBankAccount.setAccountOwner(accountOwner);
        newBankAccount.setAddedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        return (CampaignBankAccount)this.campaignBankAccountRepository.save(newBankAccount);
    }

    public AccountExistenceRes checkBankAccountExistence(String accountNumber) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestBody = "{\n    \"AccountDetailsRequest\": {\n        \"ESBHeader\": {\n            \"serviceCode\": \"740000\",\n            \"channel\": \"USSD\",\n            \"Service_name\": \"AccountDetail\",\n            \"Message_Id\": \"6255726662\"\n        },\n        \"ACCTCOMPANYVIEWType\": [\n            {\n                \"columnName\": \"ACCOUNT.NUMBER\",\n                \"criteriaValue\": \"" + accountNumber + "\",\n                \"operand\": \"EQ\"\n            }\n        ]\n    }\n}";
            HttpEntity<String> requestEntity = new HttpEntity(requestBody, headers);
            String responseBody = (String)this.restTemplate.postForObject(this.checkBankAccountURI, requestEntity, String.class, new Object[0]);
            return new AccountExistenceRes(this.getAccountTitle(responseBody));
        } catch (Exception var6) {
            throw new RuntimeException("Internal Server Error");
        }
    }

    private String getAccountTitle(String jsonString) {
        int useableBalIndex = jsonString.indexOf("\"ACCOUNTTITLE1\"");
        if (useableBalIndex != -1) {
            int valueStartIndex = jsonString.indexOf("\"", useableBalIndex + 15) + 1;
            int valueEndIndex = jsonString.indexOf("\"", valueStartIndex);
            return jsonString.substring(valueStartIndex, valueEndIndex);
        } else {
            return "No bank account was found with the provided account number.";
        }
    }
}