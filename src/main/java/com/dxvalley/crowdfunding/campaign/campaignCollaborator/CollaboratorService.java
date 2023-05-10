package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import com.dxvalley.crowdfunding.user.dto.InviteRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CollaboratorService {
    ResponseEntity sendInvitation(InviteRequest inviteRequest);

    List<Collaborator> getCollaborators();

    Collaborator getCollaboratorById(Long collaboratorId);

    List<Collaborator> getCollaboratorByCampaignId(Long campaignId);

    String deleteCollaborator(Long collaboratorId);

    ResponseEntity acceptOrRejectInvitation(Long collaboratorId, Boolean flag);
}
