package com.dxvalley.crowdfunding.payment.paymentGateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentGatewayDTO {
    @NotNull(message = "ID must not be null")
    private Long id;

    @NotNull(message = "Gateway name must not be null")
    private String gatewayName;

    @NotNull(message = "isActive must not be null")
    private Boolean isActive;

    @NotNull(message = "Updated by must not be null")
    private String updatedBy;
}