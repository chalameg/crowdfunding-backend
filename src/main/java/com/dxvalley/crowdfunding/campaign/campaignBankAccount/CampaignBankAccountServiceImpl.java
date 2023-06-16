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

        String accountNumber = accountAddReq.getAccountNumber();
        String accountOwner = accountAddReq.getAccountOwner();

        validateBankAccount(accountNumber, campaign);
        CampaignBankAccount campaignBankAccount = getOrCreateCampaignBankAccount(accountNumber, accountOwner);

        campaign.setBankAccount(campaignBankAccount);
        campaignUtils.saveCampaign(campaign);

        return campaignBankAccount;
    }

    private void validateBankAccount(String accountNumber, Campaign campaign) {
        if (campaign.getBankAccount() != null && campaign.getBankAccount().getAccountNumber().equals(accountNumber)) {
            throw new ResourceAlreadyExistsException(accountNumber + " is already set up for this campaign.");
        }
    }

    private CampaignBankAccount getOrCreateCampaignBankAccount(String accountNumber, String accountOwner) {
        Optional<CampaignBankAccount> optionalCampaignBankAccount = campaignBankAccountRepository.findByAccountNumber(accountNumber);
        return optionalCampaignBankAccount.orElseGet(
                () -> createCampaignBankAccount(accountNumber, accountOwner));
    }

    private CampaignBankAccount createCampaignBankAccount(String accountNumber, String accountOwner) {
        CampaignBankAccount newBankAccount = new CampaignBankAccount();
        newBankAccount.setAccountNumber(accountNumber);
        newBankAccount.setAccountOwner(accountOwner);
        newBankAccount.setAddedAt(LocalDateTime.now().format(dateTimeFormatter));
        return campaignBankAccountRepository.save(newBankAccount);
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