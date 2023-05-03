package com.dxvalley.crowdfunding.payment.chapa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapaRequestDTO {
    private Long userId;
    @NotNull(message = "Campaign ID cannot be null")
    private Long campaignId;

    @NotNull(message = "Amount cannot be null")
    private Double amount;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotNull(message = "Anonymous payment status cannot be null")
    private Boolean isAnonymous;

    @NotBlank(message = "Return URL cannot be blank")
    private String returnUrl;

    private String orderId;
}




