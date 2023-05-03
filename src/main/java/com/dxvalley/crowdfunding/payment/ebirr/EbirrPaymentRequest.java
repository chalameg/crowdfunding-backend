package com.dxvalley.crowdfunding.payment.ebirr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EbirrPaymentRequest {
    private String schemaVersion;
    private String requestId;
    private String timestamp;
    private String channelName;
    private String serviceName;
    private ServiceParams serviceParams;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ServiceParams {
    private String merchantUid;
    private String paymentMethod;
    private String apiKey;
    private String apiUserId;
    private PayerInfo payerInfo;
    private TransactionInfo transactionInfo;
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class PayerInfo {
    private String accountNo;
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class TransactionInfo {
    private String amount;
    private String currency;
    private String description;
    private String referenceId;
    private String invoiceId;
}
