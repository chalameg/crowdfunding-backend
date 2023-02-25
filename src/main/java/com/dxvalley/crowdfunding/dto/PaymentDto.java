package com.dxvalley.crowdfunding.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentDto {
    private Long userId;
    @NotNull
    private Long campaignId;
    @NotNull
    private Double amount;
    @NotNull
    private String payerFullName;
    @NotNull
    private String orderId;
    @NotNull
    private String paymentStatus;
    @NotNull
    private Boolean isAnonymous;
}
