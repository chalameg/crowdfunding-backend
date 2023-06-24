package com.dxvalley.crowdfunding.paymentManager.ebirr;

import com.dxvalley.crowdfunding.exception.customException.PaymentCannotProcessedException;
import com.dxvalley.crowdfunding.paymentManager.payment.Payment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class EbirrService {
    private final RestTemplate restTemplate;
    private final EbirrProperties ebirrProperties;

    public RequestData createRequestData(Payment payment) {
        return RequestData.builder()
                .clientId(this.ebirrProperties.getClientId())
                .secrateKey(this.ebirrProperties.getSecrateKey())
                .apiKey(this.ebirrProperties.getApiKey())
                .orderID(payment.getOrderId())
                .requestId(payment.getOrderId())
                .referenceId(payment.getOrderId())
                .invoiceId(payment.getOrderId())
                .accountNo(payment.getPaymentContactInfo())
                .amount(String.valueOf(payment.getAmount()))
                .build();
    }

    public EbirrPaymentResponse sendPaymentRequest(Payment payment) {
        RequestData ebirrPaymentRequest = this.createRequestData(payment);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RequestData> request = new HttpEntity(ebirrPaymentRequest, headers);
            ResponseEntity<EbirrPaymentResponse> paymentResponse = this.restTemplate.postForEntity(this.ebirrProperties.getUrl(), request, EbirrPaymentResponse.class);
            if (paymentResponse.getStatusCode().is2xxSuccessful())
                return paymentResponse.getBody();
            else
                throw new PaymentCannotProcessedException("Error processing payment");

        } catch (Exception ex) {
            log.error("Error processing payment request for {}", ebirrPaymentRequest);
            log.error(ex.getMessage());
            throw new PaymentCannotProcessedException("Error processing payment: " + ex.getMessage());
        }
    }
}

@Component
@ConfigurationProperties(prefix = "ebirr")
@Getter
@Setter
class EbirrProperties {
    private String clientId;
    private String secrateKey;
    private String apiKey;
    private String url;
}

