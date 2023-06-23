package com.dxvalley.crowdfunding.paymentManager.chapa;

import lombok.Data;

@Data
public class VerifyResponse {
    private String status;
    private String txn_id;
}
