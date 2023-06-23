package com.dxvalley.crowdfunding.paymentManager.chapa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chapa")
@Data
@NoArgsConstructor
@AllArgsConstructor
class ChapaProperties {
    private String clientId;
    private String secrateKey;
    private String apiKey;
    private String url;
    private String callBackUrl;
    private String authToken;

}
