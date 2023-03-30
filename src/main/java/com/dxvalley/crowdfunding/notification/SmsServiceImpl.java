package com.dxvalley.crowdfunding.notification;

import lombok.Getter;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;

@Service
public class SmsServiceImpl implements SmsService {

    private final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^(\\+251|0)[97]\\d{8}$");

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null)
            return false;
        phoneNumber = phoneNumber.trim();
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    @Override
    @Async
    public void sendOtp(String phoneNumber, String randomNumber) {

        try {
            ResponseEntity<String> res;
            String uri = "http://10.1.245.150:7080/v1/otp/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String code = randomNumber;
            String requestBody = "{\"mobile\":" + "\"" + phoneNumber + "\"," + "\"text\":" + "\""
                    + code + "\"" + "}";

            HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);
            res = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

//            if (!(res.getBody().getStatus().equals("Success"))) {
//                throw new RuntimeException("Cannot sent sms due to Internal Server Error. try again later");
//            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot sent sms due to Internal Server Error. try again later");
        }

    }

    @Getter
    private class OtpResponse {
        private String status;
    }

}

