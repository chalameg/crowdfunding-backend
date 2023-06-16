package com.dxvalley.crowdfunding.campaign.campaignApproval.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalFileRepository extends JpaRepository<ApprovalFile, Long> {
}
