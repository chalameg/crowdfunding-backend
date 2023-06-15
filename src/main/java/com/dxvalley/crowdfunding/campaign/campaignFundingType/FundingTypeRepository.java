package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundingTypeRepository extends JpaRepository<FundingType, Short> {
    FundingType findByName(String name);
}
