package com.dxvalley.crowdfunding.campaign.campaignBankAccount;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignService;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountDTO;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountExistenceDTO;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountMapper;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CampaignBankAccountServiceImpl implements CampaignBankAccountService {
    private final CampaignBankAccountRepository campaignBankAccountRepository;
    private final CampaignService campaignService;
    private final String checkBankAccountURI;

    public CampaignBankAccountServiceImpl(
            CampaignBankAccountRepository campaignBankAccountRepository,
            CampaignService campaignService,
            @Value("${APP_CONNECT.CHECK_BANK_ACCOUNT_URI}") String checkBankAccountURI
            ) {
        this.campaignBankAccountRepository = campaignBankAccountRepository;
        this.campaignService = campaignService;
        this.checkBankAccountURI = checkBankAccountURI;
    }

    /**
     Description: Retrieves the bank account associated with a campaign using the campaign ID.
     @param campaignId The ID of the campaign for which the bank account is to be retrieved.
     @return BankAccountDTO object representing the campaign's bank account.
     @throws ResourceNotFoundException if the bank account is not set for the campaign.
     @throws RuntimeException if there is an error while retrieving the bank account.
     */
    @Override
    public BankAccountDTO getCampaignBankAccountByCampaignId(Long campaignId) {
        try {
            CampaignBankAccount campaignBankAccount = campaignBankAccountRepository.findCampaignBankAccountByCampaignCampaignId(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bank Account is not set for this campaign."));

            return BankAccountMapper.toBankAccountDTO(campaignBankAccount);
        } catch (DataAccessException ex) {
            log.error("Error while getting bank account: {}", ex.getMessage());
            throw new RuntimeException("Error while getting bank account", ex);
        }
    }

    /**
     Description: Checks the existence of a bank account using the given account number.
     @param bankAccount The bank account number to be checked.
     @return BankAccountExistenceDTO object containing the account name if the account exists.
     @throws ResourceNotFoundException if the account does not exist.
     @throws RuntimeException if there is an internal server error.
     */
    @Override
    public BankAccountExistenceDTO checkBankAccountExistence(String bankAccount){
            try {
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                JSONObject requestBody = new JSONObject();
                requestBody.put("criteriaValue", bankAccount);

                HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
                ResponseEntity<String> response = restTemplate.postForEntity(checkBankAccountURI, request, String.class);

                String result = response.getBody().toString();
                if (result.length() > 2) {
                    JSONObject jsonObject = new JSONObject(result);
                    var accountDetailsResponse = jsonObject.getJSONObject("AccountDetailsResponse");
                    var esbStatus = accountDetailsResponse.getJSONObject("EsbStatus");
                    if (esbStatus.getString("status").equals("Success")) {
                        return new BankAccountExistenceDTO(accountDetailsResponse.getString("name"));
                    }
                }
                throw new ResourceNotFoundException("Account does not exist!");
        } catch (Exception ex) {
            log.error("Internal Server Error: {}", ex.getMessage());
            throw new RuntimeException("Internal Server Error");
        }
    }

    /**
     Description: Adds a bank account to a campaign.
     @param campaignId The ID of the campaign to which the bank account will be added.
     @param bankAccount The bank account number to be added.
     @return BankAccountDTO object representing the added bank account.
     @throws RuntimeException if there is an error while adding the bank account.
     */
    @Override
    public BankAccountDTO addBankAccount(Long campaignId, String bankAccount) {
        try {
            campaignBankAccountRepository.findCampaignBankAccountByCampaignCampaignId(campaignId)
                    .ifPresent(bankAccount1 -> campaignBankAccountRepository.delete(bankAccount1));

            Campaign campaign = campaignService.utilGetCampaignById(campaignId);

            CampaignBankAccount campaignBankAccount = new CampaignBankAccount();
            campaignBankAccount.setBankAccount(bankAccount);
            campaignBankAccount.setCampaign(campaign);
            CampaignBankAccount savedBankAccount = campaignBankAccountRepository.save(campaignBankAccount);

            return BankAccountMapper.toBankAccountDTO(savedBankAccount);
        } catch (DataAccessException ex) {
            log.error("Error while Adding bank account: {}", ex.getMessage());
            throw new RuntimeException("Error while Adding bank account", ex);
        }
    }
}