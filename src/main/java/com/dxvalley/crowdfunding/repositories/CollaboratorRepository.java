package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.Collaborator;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    Collaborator findCollaboratorByCollaboratorId(Long CollaboratorId);
    @Query("SELECT new Collaborator(c.collaboratorId, c.users)" +
            " from Collaborator as c WHERE c.campaign.campaignId = :campaignId AND c.isEnabled = TRUE")
    List<Collaborator> findAllCollaboratorByCampaignId(Long campaignId);
}
