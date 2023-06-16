package com.dxvalley.crowdfunding.campaign.campaignCollaborator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    List<Collaborator> findCollaboratorsByCampaignIdAndAccepted(Long campaignId, boolean isAccepted);

    boolean existsByCampaignIdAndInviteeUsername(Long campaignId, String username);
}
