package com.dxvalley.crowdfunding.service;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.InviteRequest;
import com.dxvalley.crowdfunding.model.Collaborator;

import java.util.List;

public interface CollaboratorService {
    ApiResponse sendInvitation(InviteRequest inviteRequest);
    List<Collaborator> getCollaborators ();
    Collaborator getCollaboratorById(Long collaboratorId);
    List<Collaborator> getCollaboratorByCampaignId(Long campaignId);
    String deleteCollaborator( Long collaboratorId);
    public ApiResponse acceptOrRejectInvitation(Long collaboratorId, Boolean flag);
}
