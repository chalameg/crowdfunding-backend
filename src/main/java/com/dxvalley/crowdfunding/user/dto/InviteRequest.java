package com.dxvalley.crowdfunding.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class InviteRequest {
    @NotEmpty(message = "A username for the invitee must be provided.")
    private String username;
    @NotNull(message = "Campaign Id must be provided.")
    @Positive(message = "Campaign must be positive integer")
    private Long campaignId;
}
