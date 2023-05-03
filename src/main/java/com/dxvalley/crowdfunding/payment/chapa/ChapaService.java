package com.dxvalley.crowdfunding.payment.chapa;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ChapaService {
    private final String bearerToken;
    private final String paymentInitiationURI;
    private final String paymentVerificationURI;

    public ChapaService(@Value("${CHAPA.BEARER_TOKEN}") String bearerToken,
                        @Value("${CHAPA.PAYMENT_INITIATION_URI}") String paymentInitiationURI,
                        @Value("${CHAPA.PAYMENT_VERIFICATION_URI}") String paymentVerificationURI) {
        this.bearerToken = bearerToken;
        this.paymentInitiationURI = paymentInitiationURI;
        this.paymentVerificationURI = paymentVerificationURI;
    }

    /**
     * Initializes a payment with Chapa.
     *
     * @param chapaRequest The Chapa request DTO.
     * @return The Chapa initialize response.
     * @throws RuntimeException if there is an error while initiating the payment request.
     */
    public ChapaInitializeResponse initializePayment(ChapaRequestDTO chapaRequest) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(bearerToken);

            JSONObject requestBody = new JSONObject();
            requestBody.put("amount", chapaRequest.getAmount());
            requestBody.put("currency", "ETB");
            requestBody.put("email", chapaRequest.getEmail());
            requestBody.put("first_name", chapaRequest.getFirstName());
            requestBody.put("last_name", chapaRequest.getLastName());
            requestBody.put("tx_ref", chapaRequest.getOrderId());
            requestBody.put("callback_url", "http://localhost:8181/api/payment/chapaVerify/" + chapaRequest.getOrderId());
            requestBody.put("return_url", chapaRequest.getReturnUrl());
            requestBody.put("customization[title]", "Payment for my favourite merchant");
            requestBody.put("customization[description]", "I love online payments");

            HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
            ResponseEntity<ChapaInitializeResponse> response = restTemplate.postForEntity(paymentInitiationURI, request, ChapaInitializeResponse.class);

            log.info("Request to chapa from jigii => {}|| ChapaInitializeResponse => {}", chapaRequest, response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Cannot initiate payment request for {} request", chapaRequest);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Verifies payment by sending a GET request to Chapa's API.
     *
     * @param orderID The unique identifier for the payment transaction.
     * @return A ChapaVerifyResponse object containing the payment verification details.
     * @throws ResourceNotFoundException If the payment cannot be verified.
     */
    public ChapaVerifyResponse verifyPayment(String orderID) {
        try {
            final String URI = paymentVerificationURI + orderID;
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setBearerAuth(bearerToken);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<ChapaVerifyResponse> response = restTemplate.exchange(URI, HttpMethod.GET, request, ChapaVerifyResponse.class);

            log.info("ChapaVerifyResponse for {} orderID => {}", orderID, response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Cannot Verify payment for {} orderID", orderID);
            throw new ResourceNotFoundException("Payment not paid yet");
        }
    }
}
