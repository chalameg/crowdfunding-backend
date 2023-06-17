package com.dxvalley.crowdfunding.payment.chapa;

import lombok.Data;

@Data
public class PaymentData {
    private String first_name;
    private String last_name;
    private String email;
    private String currency;
    private double amount;
    private double charge;
    private String mode;
    private String method;
    private String type;
    private String status;
    private String reference;
    private String tx_ref;
    private Customization customization;
    private Object meta;
    private String created_at;
    private String updated_at;
}
