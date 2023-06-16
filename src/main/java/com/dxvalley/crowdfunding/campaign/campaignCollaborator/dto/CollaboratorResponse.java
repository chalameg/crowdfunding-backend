package com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollaboratorResponse {
    private Long id;
    private boolean accepted;
    private String invitee;
    private String inviteeUsername;
    private String inviter;
    private Long campaignId;
    private String invitationSentAt;
    private String invitationExpiredAt;
    private String respondedAt;
}
