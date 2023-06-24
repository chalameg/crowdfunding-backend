package com.dxvalley.crowdfunding.paymentManager.chapa;

public class ChapaPaymentResponse {
    public Url data;
    public String message;
    public String status;

    public static class Url {
        public String checkout_url;

        public Url() {
        }
    }
}