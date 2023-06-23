package com.dxvalley.crowdfunding.messageManager.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;
@Service
public class SmsServiceImpl implements SmsService {
    private final String OPT_URI;
    private final RestTemplate restTemplate;
    private final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^(\\+251|0)[97]\\d{8}$");

    public SmsServiceImpl(@Value("${APP_CONNECT.OPT_URI}") String OPT_URI, RestTemplate restTemplate) {
        this.OPT_URI = OPT_URI;
        this.restTemplate = restTemplate;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        } else {
            phoneNumber = phoneNumber.trim();
            return this.PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
        }
    }

    @Async
    public void sendOtp(String phoneNumber, String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = "{\"Mobile\": \"" + phoneNumber + "\", \"Text\": \"" + code + "\"}";
            HttpEntity<String> request = new HttpEntity(requestBody, headers);

            this.restTemplate.postForEntity(this.OPT_URI, request, String.class, new Object[0]);
        } catch (Exception var6) {
            throw new RuntimeException("Cannot sent sms due to Internal Server Error. try again later");
        }
    }
}

