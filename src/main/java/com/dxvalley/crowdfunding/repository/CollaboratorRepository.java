package com.dxvalley.crowdfunding.repository;

import com.dxvalley.crowdfunding.model.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    Optional<Collaborator> findCollaboratorByCollaboratorId(Long CollaboratorId);
    @Query("SELECT new Collaborator(c.collaboratorId, c.users,c.invitationSentAt,c.respondedAt)" +
            " from Collaborator as c WHERE c.campaign.campaignId = :campaignId AND c.isAccepted = TRUE")
    List<Collaborator> findAllCollaboratorByCampaignId(Long campaignId);
}