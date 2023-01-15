package com.dxvalley.crowdfunding.services;

import java.util.List;

import com.dxvalley.crowdfunding.models.FundingType;

public interface FundingTypeService {
    FundingType addFundingType (FundingType fundingType);
    FundingType editFundingType (FundingType fundingType);
    List<FundingType> getFundingTypes ();
    FundingType getFundingTypeById(Long fundingTypeId);
    void deleteFundingType( Long fundingTypeId);
}
