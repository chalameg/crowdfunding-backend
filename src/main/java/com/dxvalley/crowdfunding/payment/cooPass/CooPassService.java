package com.dxvalley.crowdfunding.payment.cooPass;

import com.dxvalley.crowdfunding.exception.PaymentCannotProcessedException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CooPassService {
    private final String paymentInitiationURI;
    private final String paymentVerificationURI;
    private final String secretKey;
    private final String clientId;
    private final String apiKey;

    public CooPassService(
            @Value("${COOPASS.PAYMENT_INITIATION_URI}") String paymentInitiationURI,
            @Value("${COOPASS.PAYMENT_VERIFICATION_URI}") String paymentVerificationURI,
            @Value("${COOPASS.SECRET_KEY}") String secretKey,
            @Value("${COOPASS.CLIENT_ID}") String clientId,
            @Value("${COOPASS.API_KEY}") String apiKey) {
        this.paymentInitiationURI = paymentInitiationURI;
        this.secretKey = secretKey;
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.paymentVerificationURI = paymentVerificationURI;
    }

    /**
     * Initializes a payment with cooPass.
     *
     * @param paymentRequestDTO The cooPass request DTO.
     * @return The cooPass initialize response.
     * @throws RuntimeException if there is an error while initiating the payment request.
     */
    public CooPassInitResponse initializePayment(PaymentRequestDTO paymentRequestDTO) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject requestBody = new JSONObject();
            requestBody.put("clientId", clientId);
            requestBody.put("secretKey", secretKey);
            requestBody.put("callBackUrl", "http://192.168.137.76:8181/api/payment/cooPassVerify/" + paymentRequestDTO.getOrderId());
            requestBody.put("returnUrl", paymentRequestDTO.getReturnUrl());
            requestBody.put("apiKey", apiKey);
            requestBody.put("orderId", paymentRequestDTO.getOrderId());
            requestBody.put("currency", "ETB");
            requestBody.put("amount", paymentRequestDTO.getAmount());

            HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
            ResponseEntity<CooPassInitResponse> response = restTemplate.postForEntity(paymentInitiationURI, request, CooPassInitResponse.class);

            log.info("Request to cooPass from jigii => {}|| cooPassInitializeResponse => {}", paymentRequestDTO, response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Cannot initiate cooPass payment request for {} request", paymentRequestDTO);
            throw new PaymentCannotProcessedException(e.getMessage());
        }
    }

    /**
     * Verifies payment by sending a GET request to CooPass's API.
     *
     * @param orderID The unique identifier for the payment transaction.
     * @return A CooPassVerifyResponse object containing the payment verification details.
     * @throws ResourceNotFoundException If the payment cannot be verified.
     */
    public CooPassVerifyResponse verifyPayment(String orderID) {
        try {
            final String URI = paymentVerificationURI + orderID;
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<CooPassVerifyResponse> response = restTemplate.exchange(URI, HttpMethod.GET, request, CooPassVerifyResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("CooPassVerifyResponse for {} orderID => {}", orderID, response.getBody());
                return response.getBody();
            }

            log.error("Cannot Verify payment on CooPass for {} orderID", orderID);
            throw new PaymentCannotProcessedException("Cannot verify payment");
        } catch (Exception e) {
            log.error("Cannot Verify payment on CooPass for {} orderID", orderID);
            throw new PaymentCannotProcessedException("Cannot verify payment");
        }
    }
}
