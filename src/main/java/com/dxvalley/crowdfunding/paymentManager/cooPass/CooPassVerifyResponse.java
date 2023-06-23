package com.dxvalley.crowdfunding.paymentManager.cooPass;

import lombok.Data;

@Data
public class CooPassVerifyResponse {
    public String status;
    public PaymentData data;
}

