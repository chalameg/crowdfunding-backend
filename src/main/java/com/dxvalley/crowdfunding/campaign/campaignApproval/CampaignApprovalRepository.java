package com.dxvalley.crowdfunding.campaign.campaignApproval;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampaignApprovalRepository extends JpaRepository<CampaignApproval, Long> {
    Optional<CampaignApproval> findCampaignApprovalByCampaignCampaignId(Long campaignId);
}

