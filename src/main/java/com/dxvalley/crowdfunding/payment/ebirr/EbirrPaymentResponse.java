package com.dxvalley.crowdfunding.payment.ebirr;

import lombok.Data;
@Data
public class EbirrPaymentResponse {
    private String schemaVersion;
    private String timestamp;
    private String requestId;
    private String sessionId;
    private String responseCode;
    private String errorCode;
    private String responseMsg;
    private EbirrResponseParams params;

}
