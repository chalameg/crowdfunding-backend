package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.FundingType;

import java.util.Optional;

public interface FundingTypeRepository extends JpaRepository<FundingType,Long> {
    Optional<FundingType> findFundingTypeByFundingTypeId(Long fundingTypeId);
}
