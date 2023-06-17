package com.dxvalley.crowdfunding.paymentManager.ebirr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EbirrPaymentResponse {
    private String status;
    private PaymentData data;
    private int code;

    @Data
    public static class PaymentData {
        @JsonProperty("phoneNumber")
        private String phoneNumber;

        @JsonProperty("amount")
        private String amount;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("transactionId")
        private String transactionId;

        @JsonProperty("orderId")
        private String orderId;

        @JsonProperty("orderedAt")
        private String orderedAt;

        @JsonProperty("completedAt")
        private String completedAt;
    }
}
