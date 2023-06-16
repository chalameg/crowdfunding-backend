package com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CollaborationRequest {
    @NotEmpty(message = "A username for the inviter must be provided.")
    private String inviterUsername;

    @NotEmpty(message = "An email for the invitee must be provided.")
    private String inviteeEmail;

    @NotNull(message = "Campaign ID must be provided.")
    private Long campaignId;
}