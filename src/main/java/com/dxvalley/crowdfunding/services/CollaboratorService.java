package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.models.Collaborator;


public interface CollaboratorService {
    Collaborator addCollaborator (Collaborator collaborator);
    Collaborator editCollaborator (Collaborator collaborator);
    List<Collaborator> getCollaborators ();
    Collaborator getCollaboratorById(Long collaboratorId);
    void deleteCollaborator( Long collaboratorId);
    List<Collaborator> findAllCollaboratorByCampaignCampaignId(Long campaignId);
}
