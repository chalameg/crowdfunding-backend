package com.dxvalley.crowdfunding.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
public class InviteRequest {
    @NotBlank(message = "A username for the invitee must be provided.")
    private String username;
   @Positive(message = "Campaign must be provided")
    private Long campaignId;
}
