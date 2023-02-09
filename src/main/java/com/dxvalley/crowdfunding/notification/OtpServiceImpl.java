package com.dxvalley.crowdfunding.notification;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

@Service
public class OtpServiceImpl implements OtpService{
    @Override
    public ApiResponse sendOtp(String phoneNumber, String randomNumber) {

        ResponseEntity<String> res;
        try {
            String uri = "http://10.1.245.150:7080/v1/otp/";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String code = randomNumber;
            String requestBody = "{\"mobile\":" + "\"" + phoneNumber + "\"," + "\"text\":" + "\""
                    + code + "\"" + "}";

            HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);
            res = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            if (res.getStatusCode().toString().trim().equals("200 OK")) {
                return new ApiResponse(
                        "success",
                        "Message sent to your phone number.");

            } else {
                return new ApiResponse(
                        "bad request",
                        "Please inter valid Mobile number!");
            }
        } catch (Exception e) {
            return new ApiResponse(
                    "error",
                    "Sorry, SMS service is down for a moment. Please try again later.");

        }
    }


    }

