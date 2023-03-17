package com.dxvalley.crowdfunding.repository;

import com.dxvalley.crowdfunding.model.FundingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FundingTypeRepository extends JpaRepository<FundingType,Long> {
    Optional<FundingType> findFundingTypeByFundingTypeId(Long fundingTypeId);
}
