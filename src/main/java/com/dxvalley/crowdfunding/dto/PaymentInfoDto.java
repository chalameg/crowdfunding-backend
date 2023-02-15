package com.dxvalley.crowdfunding.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentInfoDto {
    @NotNull
    private Long campaignId;
    @NotNull
    private Long userId;
    @NotNull
    private Double amount;

}
