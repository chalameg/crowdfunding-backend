package com.dxvalley.crowdfunding.campaign.campaignCollaborator.dto;

import com.dxvalley.crowdfunding.campaign.campaignCollaborator.Collaborator;

public class CollaboratorMapper {

    public static CollaboratorResponse toCollResponse(Collaborator collaborator) {
        CollaboratorResponse collaboratorResponse = CollaboratorResponse.builder()
                .id(collaborator.getId())
                .accepted(collaborator.isAccepted())
                .invitee(collaborator.getInvitee().getFullName())
                .inviteeUsername(collaborator.getInvitee().getUsername())
                .campaignId(collaborator.getCampaign().getId())
                .invitationSentAt(collaborator.getInvitationSentAt())
                .invitationExpiredAt(collaborator.getInvitationExpiredAt())
                .respondedAt(collaborator.getRespondedAt())
                .build();

        return collaboratorResponse;
    }
}
