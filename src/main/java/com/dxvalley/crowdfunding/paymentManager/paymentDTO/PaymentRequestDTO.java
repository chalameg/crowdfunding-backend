package com.dxvalley.crowdfunding.payment.paymentDTO;

import com.dxvalley.crowdfunding.payment.PaymentProcessor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long userId;
    @NotNull(message = "Campaign ID cannot be null")
    private Long campaignId;
    @NotNull(message = "Amount cannot be null")
    private Double amount;
    @NotBlank(message = "paymentContactInfo cannot be blank")
    private String paymentContactInfo;
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotNull(message = "Anonymous payment status cannot be null")
    private Boolean isAnonymous;
    private String returnUrl;
    private String orderId;
    private PaymentProcessor paymentProcessor;
}




