package com.dxvalley.crowdfunding.paymentManager.paymentDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapaDTO {
    private Long userId;
    private @NotNull(message = "Campaign ID cannot be null")
    Long campaignId;
    private @NotNull(message = "Amount cannot be null")
    Double amount;
    private @NotBlank(message = "email cannot be blank")
    String email;
    private @NotBlank(message = "First name cannot be blank")
    String firstName;
    private @NotBlank(message = "Last name cannot be blank")
    String lastName;
    private boolean isAnonymous;
    private @NotBlank(message = "returnUrl cannot be blank")
    String returnUrl;
    private String orderId;
}