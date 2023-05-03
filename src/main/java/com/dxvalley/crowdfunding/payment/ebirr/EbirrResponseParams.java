package com.dxvalley.crowdfunding.payment.ebirr;

import lombok.Data;

@Data
public class EbirrResponseParams {

    private String issuerApprovalCode;
    private String accountNo;
    private String accountType;
    private String accountholder;
    private String state;
    private String merchantCharges;
    private String customerCharges;
    private String referenceId;
    private String transactionId;
    private String accountExpDate;
    private String issuerTransactionId;
    private String txAmount;
}
