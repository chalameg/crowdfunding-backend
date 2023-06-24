package com.dxvalley.crowdfunding.paymentManager.paymentDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EbirrPaymentReqDTO {
    private Long userId;
    private @NotNull(message = "Campaign ID cannot be null")
    Long campaignId;
    private @NotNull(message = "Amount cannot be null") Double amount;
    private @NotBlank(message = "phoneNumber cannot be blank"
    ) String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean isAnonymous;
    private String orderId;

}
