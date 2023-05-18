package com.dxvalley.crowdfunding.payment.paymentGateway.dto;

import com.dxvalley.crowdfunding.payment.paymentGateway.PaymentGateway;

public class PaymentGatewayMapper {
    public static PaymentGatewayDTO toGatewayDTO(PaymentGateway paymentGateway) {
        PaymentGatewayDTO paymentGatewayResponse = new PaymentGatewayDTO();
        paymentGatewayResponse.setGatewayName(paymentGateway.getGatewayName());
        paymentGatewayResponse.setIsActive(paymentGateway.getIsActive());

        return paymentGatewayResponse;
    }
}
