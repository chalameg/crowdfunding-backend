package com.dxvalley.crowdfunding.payment.chapa;

import lombok.Data;

@Data
public class ChapaInitializeResponse {
    private String message;
    private String status;
    private CheckoutData data;
}

@Data
class CheckoutData {
    private String checkout_url;
}