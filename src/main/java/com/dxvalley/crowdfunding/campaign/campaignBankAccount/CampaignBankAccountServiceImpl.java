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
    @Value("${APP_CONNECT.CHECK_BANK_ACCOUNT_URI}")
    private String checkBankAccountURI;


    // Retrieves the bank account with associated campaign.
    @Override
    public BankAccountRes getByAccountNumber(String accountNumber) {

        CampaignBankAccount campaignBankAccount = campaignBankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bank Account is not Found."));
        List<Campaign> campaigns = campaignUtils.getCampaignsByBankAccount(accountNumber);

        return BankAccountMapper.toBankAccountDTO(campaignBankAccount, campaigns);
    }


    //Adds a bank account to a campaign.
    @Override
    public CampaignBankAccount addBankAccount(AccountAddReq accountAddReq) {
        Campaign campaign = campaignUtils.utilGetCampaignById(accountAddReq.getCampaignId());
        Optional<CampaignBankAccount> optionalCampaignBankAccount = campaignBankAccountRepository.findByAccountNumber(accountAddReq.getAccountNumber());

        if (campaign.getBankAccount().getAccountNumber().equals(accountAddReq.getAccountNumber()))
            throw new ResourceAlreadyExistsException(accountAddReq.getAccountNumber() + " is already set up for this campaign.");

        CampaignBankAccount campaignBankAccount = optionalCampaignBankAccount.orElseGet(() -> {
            CampaignBankAccount newBankAccount = new CampaignBankAccount();
            newBankAccount.setAccountNumber(accountAddReq.getAccountNumber());
            newBankAccount.setAccountOwner(accountAddReq.getAccountOwner());
            newBankAccount.setAddedAt(LocalDateTime.now().format(dateTimeFormatter));
            return campaignBankAccountRepository.save(newBankAccount);
        });

        campaign.setBankAccount(campaignBankAccount);
        campaignUtils.saveCampaign(campaign);

        return campaignBankAccount;
    }

    // Description: Checks the existence of a bank account using the given account number.
    @Override
    public AccountExistenceRes checkBankAccountExistence(String accountNumber) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject requestBody = new JSONObject();
            requestBody.put("criteriaValue", accountNumber);

            HttpEntity<JSONObject> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(checkBankAccountURI, request, String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());

            if (jsonResponse.has("AccountDetailsResponse")) {
                JSONObject accountDetailsResponse = jsonResponse.getJSONObject("AccountDetailsResponse");
                JSONObject esbStatus = accountDetailsResponse.getJSONObject("EsbStatus");
                if (esbStatus.getString("status").equals("Success")) {
                    String accountName = accountDetailsResponse.getString("name");
                    return new AccountExistenceRes(accountName);
                }
            }

            throw new ResourceNotFoundException("Account does not exist!");
        } catch (Exception ex) {
            throw new RuntimeException("Internal Server Error");
        }
    }

}