package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.FundingType;

public interface FundingTypeRepository extends JpaRepository<FundingType,Long> {
    FundingType findFundingTypeByFundingTypeId(Long fundingTypeId);
}
