package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.BankAccountExistenceDTO;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.CampaignBankAccount;
import com.dxvalley.crowdfunding.repositories.CampaignBankAccountRepository;
import com.dxvalley.crowdfunding.repositories.CampaignRepository;
import com.dxvalley.crowdfunding.services.CampaignBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Service
public class CampaignBankAccountServiceImpl implements CampaignBankAccountService {
    @Autowired
    CampaignBankAccountRepository campaignBankAccountRepository;
    @Autowired
    CampaignRepository campaignRepository;

    @Override
    public CampaignBankAccount getCampaignBankAccountByCampaignId(Long campaignId) {
        return campaignBankAccountRepository.findCampaignBankAccountByCampaignId(campaignId);
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

        } catch (Exception e) {
            throw new Exception("Internal Server Error!");
        }

        String result = response.getBody().toString();
        System.out.println(result.length());
        if(result.length() > 2){
            JSONObject jsonObject = new JSONObject(result);
            var accountDetailsResponse =  jsonObject.getJSONObject("AccountDetailsResponse");
            var esbStatus =  accountDetailsResponse.getJSONObject("EsbStatus");
            if(esbStatus.getString("status").equals("Success")){
                return new BankAccountExistenceDTO(accountDetailsResponse.getString("name"));
            };
        }
        throw new ResourceNotFoundException("Account does not exist!");
    }

    @Override
    public CampaignBankAccount addBankAccount(Long campaignId, String bankAccount) {
        CampaignBankAccount campaignBankAccount = new CampaignBankAccount();
        campaignBankAccount.setBankAccount(bankAccount);
        campaignBankAccount.setCampaign(campaignRepository.findCampaignByCampaignId(campaignId).get());
        return campaignBankAccountRepository.save(campaignBankAccount);
    }

}
