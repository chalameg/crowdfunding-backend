package com.dxvalley.crowdfunding.paymentManager.paymentGateway.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PaymentGatewayReq {
    @NotEmpty(message = "Gateway name must not be empty")
    private String gatewayName;

    @NotEmpty(message = "isActive must not be empty")
    private Boolean isActive;

    @NotEmpty(message = "Updated by must not be empty")
    private String updatedBy;
}