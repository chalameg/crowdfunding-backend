package com.dxvalley.crowdfunding.paymentManager.chapa;

import com.dxvalley.crowdfunding.exception.customException.PaymentCannotProcessedException;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.ChapaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapaService {
    private final ChapaProperties chapaProperties;
    private final RestTemplate restTemplate;


    public ChapaRequestData createRequestData(ChapaDTO chapaDTO) {
        return ChapaRequestData.builder().clientId(this.chapaProperties.getClientId()).secrateKey(this.chapaProperties.getSecrateKey())
                .apiKey(this.chapaProperties.getApiKey())
                .callBackUrl(chapaProperties.getCallBackUrl() + chapaDTO.getOrderId()).returnUrl(chapaDTO.getReturnUrl()).email(chapaDTO.getEmail()).first_name(chapaDTO.getFirstName()).last_name(chapaDTO.getLastName()).tx_ref(chapaDTO.getOrderId()).title("Deboo App").description("Deboo App").currency("ETB").authToken(this.chapaProperties.getAuthToken()).amount(String.valueOf(chapaDTO.getAmount())).build();
    }


    public ChapaPaymentResponse initializePayment(ChapaDTO chapaDTO) {
        ChapaRequestData chapaRequestData = this.createRequestData(chapaDTO);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ChapaRequestData> request = new HttpEntity(chapaRequestData, headers);
            ResponseEntity<ChapaPaymentResponse> paymentResponse = this.restTemplate.postForEntity(this.chapaProperties.getUrl(), request, ChapaPaymentResponse.class);

            if (paymentResponse.getStatusCode().is2xxSuccessful()) {
                return paymentResponse.getBody();
            } else {
                throw new PaymentCannotProcessedException("Error processing payment");
            }
        } catch (Exception var6) {
            log.error("Cannot initiate payment request for {} request", chapaDTO);
            throw new PaymentCannotProcessedException(var6.getMessage());
        }
    }
}
