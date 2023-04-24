package com.dxvalley.crowdfunding.payment.chapa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapaInitializeResponse {
    private String message;
    private String status;
    private CheckoutData data;
}

@Getter
@Setter
class CheckoutData {
    private String checkout_url;
}