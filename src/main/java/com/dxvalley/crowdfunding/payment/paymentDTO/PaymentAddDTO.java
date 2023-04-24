package com.dxvalley.crowdfunding.payment.paymentDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public
class PaymentAddDTO {
    private Long userId;
    @NotNull(message = "campaignId cannot be null")
    private Long campaignId;
    @DecimalMin(value = "0.0", message = "amount must be greater than or equal to 0")
    private Double amount;
    @NotNull(message = "currency cannot be null")
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a three-letter code in uppercase")
    private String currency;
    private String email;
    private String first_name;
    private String last_name;
    @NotNull(message = "isAnonymous cannot be null")
    private Boolean isAnonymous;
    @NotEmpty
    private String paymentProcessor;
}
