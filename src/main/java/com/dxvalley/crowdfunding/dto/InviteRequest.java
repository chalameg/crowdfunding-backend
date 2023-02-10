package com.dxvalley.crowdfunding.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
public class InviteRequest {
    @NotEmpty(message = "A username for the invitee must be provided.")
    private String username;
    @NotNull(message = "Campaign Id must be provided.")
    @Positive(message = "Campaign must be positive integer")
    private Long campaignId;
}
