package com.dxvalley.crowdfunding.payment.ebirr;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EbirrRequestDTO {
    private Long userId;
    @NotNull(message = "campaignId cannot be null")
    private Long campaignId;
    @NotBlank(message = "amount cannot be blank")
    private String amount;
    @NotBlank(message = "phoneNumber cannot be blank")
    private String phoneNumber;
    @NotBlank(message = "firstName cannot be blank")
    private String firstName;
    @NotBlank(message = "lastName cannot be blank")
    private String lastName;
    @NotNull(message = "isAnonymous cannot be null")
    private Boolean isAnonymous;
    private String orderId;
}