package com.dxvalley.crowdfunding.paymentManager.ebirr;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestData {
    private String clientId;
    private String orderID;
    private String secrateKey;
    private String callBackUrl;
    private String apiKey;
    private String requestId;
    private String accountNo;
    private String amount;
    private String referenceId;
    private String invoiceId;
}

