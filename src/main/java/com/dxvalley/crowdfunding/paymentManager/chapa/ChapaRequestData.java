package com.dxvalley.crowdfunding.paymentManager.chapa;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChapaRequestData {
    public String clientId;
    public String secrateKey;
    public String callBackUrl;
    public String returnUrl;
    public String apiKey;
    public String email;
    public String first_name;
    public String last_name;
    public String tx_ref;
    public String title;
    public String description;
    public String currency;
    public String amount;
    public String authToken;

}
