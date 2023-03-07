package com.dxvalley.crowdfunding.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentUpdateDTO {
    @NotBlank(message = "Payer full name cannot be blank")
    private String payerFullName;
    @NotBlank(message = "Transaction ID cannot be blank")
    private String transactionId;
    @NotBlank(message = "Payment status cannot be null")
    private String paymentStatus;

}
