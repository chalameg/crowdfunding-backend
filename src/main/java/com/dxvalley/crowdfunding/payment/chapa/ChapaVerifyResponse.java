package com.dxvalley.crowdfunding.payment.chapa;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChapaVerifyResponse {
    private String message;
    private String status;
    private Data data;
}

@Setter
@Getter
class Customization {
    private String title;
    private String description;
    private Object logo;
}
