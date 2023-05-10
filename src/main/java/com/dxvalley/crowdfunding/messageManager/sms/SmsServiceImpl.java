package com.dxvalley.crowdfunding.messageManager.sms;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;
@Service
public class SmsServiceImpl implements SmsService {
    private final String OPT_URI;
    private final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^(\\+251|0)[97]\\d{8}$");

    public SmsServiceImpl(@Value("${APP_CONNECT.OPT_URI}") String OPT_URI) {
        this.OPT_URI = OPT_URI;
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null)
            return false;
        phoneNumber = phoneNumber.trim();
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    @Override
    @Async
    public void sendOtp(String phoneNumber, String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject requestBody = new JSONObject();
            requestBody.put("mobile", phoneNumber);

            HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
            restTemplate.postForEntity(OPT_URI, request, String.class);

        } catch (Exception e) {
            throw new RuntimeException("Cannot sent sms due to Internal Server Error. try again later");
        }
    }
}

