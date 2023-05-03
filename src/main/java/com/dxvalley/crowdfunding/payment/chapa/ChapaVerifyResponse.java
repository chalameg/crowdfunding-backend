package com.dxvalley.crowdfunding.payment.chapa;


import lombok.Data;

@Data
public class ChapaVerifyResponse {
    private String message;
    private String status;
    private PaymentData data;
}

@Data
class Customization {
    private String title;
    private String description;
    private Object logo;
}
