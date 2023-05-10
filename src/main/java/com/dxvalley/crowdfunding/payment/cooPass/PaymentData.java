package com.dxvalley.crowdfunding.payment.cooPass;

import lombok.Data;

@Data
public class PaymentData {
    public String phoneNumber;

    public String currency;

    public String amount;
    public String transactionId;

    public String orderId;
    public String orderedAt;
    public String completedAt;
}
