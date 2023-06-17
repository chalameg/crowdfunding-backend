package com.dxvalley.crowdfunding.payment.ebirr;

import lombok.Data;

@Data
public class EbirrPaymentResponse {
    private String Message;
    private String errorCode;
    private String state;
    private String transactionId;
    private String responseCode;
    private String responseMsg;
    private String issuerTransactionId;
}
