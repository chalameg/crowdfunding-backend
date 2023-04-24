package com.dxvalley.crowdfunding.payment.chapa;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapaRequestDTO {
    private Long userId;
    @NotNull(message = "campaignId cannot be null")
    private Long campaignId;
    @NotEmpty
    private Double amount;
    @NotEmpty
    private String email;
    @NotEmpty
    private String first_name;
    @NotEmpty
    private String last_name;
    @NotNull(message = "isAnonymous cannot be null")
    private Boolean isAnonymous;
    private String orderId;
}
