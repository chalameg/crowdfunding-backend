package com.dxvalley.crowdfunding.payment.ebirr;

import com.dxvalley.crowdfunding.exception.PaymentCannotProcessedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EbirrService {
    private final DateTimeFormatter dateTimeFormatter;
    private final String schemaVersion;
    private final String channelName;
    private final String serviceName;
    private final String merchantUid;
    private final String paymentMethod;
    private final String apiKey;
    private final String apiUserId;
    private final String ebirrURI;

    public EbirrService(
            DateTimeFormatter dateTimeFormatter,
            @Value("${EBIRR.SCHEMA_VERSION}") String schemaVersion,
            @Value("${EBIRR.CHANNEL_NAME}") String channelName,
            @Value("${EBIRR.SERVICE_NAME}") String serviceName,
            @Value("${EBIRR.MERCHANT_UID}") String merchantUid,
            @Value("${EBIRR.PAYMENT_METHOD}") String paymentMethod,
            @Value("${EBIRR.API_KEY}") String apiKey,
            @Value("${EBIRR.API_USER_ID}") String apiUserId,
            @Value("${EBIRR.EBIRR_URI}") String ebirrURI
            ) {
        this.schemaVersion = schemaVersion;
        this.channelName = channelName;
        this.serviceName = serviceName;
        this.merchantUid = merchantUid;
        this.paymentMethod = paymentMethod;
        this.apiKey = apiKey;
        this.apiUserId = apiUserId;
        this.ebirrURI = ebirrURI;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    /**
     * Converts an instance of EbirrRequestDTO to an instance of EbirrPaymentRequest.
     * @param ebirrRequestDTO An instance of EbirrRequestDTO.
     * @return An instance of EbirrPaymentRequest.
     */
    public EbirrPaymentRequest requestToEbirrPaymentRequest(EbirrRequestDTO ebirrRequestDTO) {

        EbirrPaymentRequest paymentRequest = new EbirrPaymentRequest();
        paymentRequest.setSchemaVersion(schemaVersion);
        paymentRequest.setRequestId(ebirrRequestDTO.getOrderId());
        paymentRequest.setTimestamp(LocalDateTime.now().format(dateTimeFormatter));
        paymentRequest.setChannelName(channelName);
        paymentRequest.setServiceName(serviceName);

        ServiceParams serviceParams = new ServiceParams();
        serviceParams.setMerchantUid(merchantUid);
        serviceParams.setPaymentMethod(paymentMethod);
        serviceParams.setApiKey(apiKey);
        serviceParams.setApiUserId(apiUserId);

        PayerInfo payerInfo = new PayerInfo();
        payerInfo.setAccountNo(ebirrRequestDTO.getPhoneNumber());
        serviceParams.setPayerInfo(payerInfo);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setAmount(ebirrRequestDTO.getAmount());
        transactionInfo.setCurrency("ETB");
        transactionInfo.setDescription("description");
        transactionInfo.setReferenceId(ebirrRequestDTO.getOrderId());
        transactionInfo.setInvoiceId(ebirrRequestDTO.getOrderId());
        serviceParams.setTransactionInfo(transactionInfo);
        paymentRequest.setServiceParams(serviceParams);

        return paymentRequest;
    }

    /**
     * Sends a payment request to the Ebirr payment gateway.
     * @param ebirrPaymentRequest The Ebirr payment request to be sent.
     * @return The payment response from Ebirr payment gateway.
     * @throws PaymentCannotProcessedException If there is an error processing the payment request.
     */
    public EbirrPaymentResponse sendToEbirr(EbirrPaymentRequest ebirrPaymentRequest) {
        try {
            log.info("Request to ebirr from jigii => {}", ebirrPaymentRequest);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EbirrPaymentRequest> request = new HttpEntity<>(ebirrPaymentRequest, headers);
            ResponseEntity<EbirrPaymentResponse> paymentResponse = restTemplate.postForEntity(ebirrURI, request, EbirrPaymentResponse.class);

            if (paymentResponse.getBody().getResponseMsg().equals("RCS_SUCCESS")) {
                log.info("Success Response from ebirr => {}", paymentResponse.getBody());
                return paymentResponse.getBody();
            }

            log.error("Error Response from ebirr => {}", paymentResponse.getBody());
            throw new PaymentCannotProcessedException("Error processing payment: " + paymentResponse.getBody().getResponseMsg());
        } catch (Exception e) {
            log.error("Error processing payment request for {}", ebirrPaymentRequest);
            log.error(e.getMessage());
            throw new PaymentCannotProcessedException("Error processing payment: " + e.getMessage());
        }
    }
}
