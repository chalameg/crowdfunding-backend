package com.dxvalley.crowdfunding.payment.chapa;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChapaService {

    private final Logger logger = LoggerFactory.getLogger(ChapaService.class);
    public ChapaInitializeResponse chapaInitialize(ChapaRequestDTO chapaRequest) {
        try {
            final String URI = "https://api.chapa.co/v1/transaction/initialize";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth("Bearer CHASECK_TEST-MozGIHYgzprWQQMVMbtg3zbr2XiyMwky");

            JSONObject requestBody = new JSONObject();
            requestBody.put("amount", chapaRequest.getAmount());
            requestBody.put("currency", "ETB");
            requestBody.put("email", chapaRequest.getEmail());
            requestBody.put("first_name", chapaRequest.getFirst_name());
            requestBody.put("last_name", chapaRequest.getLast_name());
            requestBody.put("phone_number", "0912345678");
            requestBody.put("tx_ref", chapaRequest.getOrderId());
//            requestBody.put("callback_url", "callback_url");
            requestBody.put("return_url", "http://localhost:3000/verifyChapa/" + chapaRequest.getOrderId() + "/" + chapaRequest.getCampaignId());
            requestBody.put("customization[title]", "Payment for my favourite merchant");
            requestBody.put("customization[description]", "I love online payments");

            HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
            ResponseEntity<ChapaInitializeResponse> response = restTemplate.postForEntity(URI, request, ChapaInitializeResponse.class);

            logger.info(String.valueOf(response.getBody()));
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ChapaVerifyResponse chapaVerify(String orderID) {
        try {
            final String URI = "https://api.chapa.co/v1/transaction/verify/" + orderID;

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setBearerAuth("Bearer CHASECK_TEST-MozGIHYgzprWQQMVMbtg3zbr2XiyMwky");

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<ChapaVerifyResponse> response = restTemplate.exchange(URI, HttpMethod.GET, request, ChapaVerifyResponse.class);

            logger.info(String.valueOf(response.getBody()));
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
