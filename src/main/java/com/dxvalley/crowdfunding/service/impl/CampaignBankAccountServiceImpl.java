package com.dxvalley.crowdfunding.service.impl;

import com.dxvalley.crowdfunding.dto.BankAccountExistenceDTO;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.model.Campaign;
import com.dxvalley.crowdfunding.model.CampaignBankAccount;
import com.dxvalley.crowdfunding.repository.CampaignBankAccountRepository;
import com.dxvalley.crowdfunding.repository.CampaignRepository;
import com.dxvalley.crowdfunding.service.CampaignBankAccountService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CampaignBankAccountServiceImpl implements CampaignBankAccountService {
    @Autowired
    CampaignBankAccountRepository campaignBankAccountRepository;
    @Autowired
    CampaignRepository campaignRepository;
    private final Logger logger = LoggerFactory.getLogger(CampaignBankAccountServiceImpl.class);

    @Override
    public CampaignBankAccount getCampaignBankAccountByCampaignId(Long campaignId) {
        try {
            CampaignBankAccount campaignBankAccount = campaignBankAccountRepository.findCampaignBankAccountByCampaignId(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bank Account is not set for this campaign."));
            logger.info("Getting bank account for campaign ID: {}", campaignId);
            return campaignBankAccount;
        } catch (DataAccessException ex) {
            logger.error("Error while getting bank account: {}", ex.getMessage());
            throw new RuntimeException("Error while getting bank account", ex);
        }

    }

    @Override
    public BankAccountExistenceDTO checkBankAccountExistence(String bankAccount) throws Exception {
        ResponseEntity<?> response;
        try {
            String uri = "http://10.1.245.150:7080/v2/cbo/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestBody = "{\"criteriaValue\":" + bankAccount + "}";
            HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);
            response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);


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
            logger.error("Internal Server Error: {}", ex.getMessage());
            throw new Exception("Internal Server Error");
        }
    }

    @Override
    public CampaignBankAccount addBankAccount(Long campaignId, String bankAccount) {
        try {
            campaignBankAccountRepository.findCampaignBankAccountByCampaignId(campaignId)
                    .ifPresent(bankAccount1 -> campaignBankAccountRepository.delete(bankAccount1));

            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no campaign with this ID."));

            CampaignBankAccount campaignBankAccount = new CampaignBankAccount();
            campaignBankAccount.setBankAccount(bankAccount);
            campaignBankAccount.setCampaign(campaign);
            CampaignBankAccount savedBankAccount = campaignBankAccountRepository.save(campaignBankAccount);

            logger.info("New bank account has been added for campaign ID: {}", campaignId);
            return savedBankAccount;
        } catch (DataAccessException ex) {
            logger.error("Error while Adding bank account: {}", ex.getMessage());
            throw new RuntimeException("Error while Adding bank account", ex);
        }
    }

}