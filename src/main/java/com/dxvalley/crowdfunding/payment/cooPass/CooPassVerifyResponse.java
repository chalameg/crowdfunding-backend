package com.dxvalley.crowdfunding.payment.cooPass;

import lombok.Data;

@Data
public class CooPassVerifyResponse {
    public String status;
    public PaymentData data;
}

