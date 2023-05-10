package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import java.util.List;

public interface FundingTypeService {
    FundingType addFundingType(FundingType fundingType);

    FundingType editFundingType(FundingType fundingType);

    List<FundingType> getFundingTypes();

    FundingType getFundingTypeById(Long fundingTypeId);

    void deleteFundingType(Long fundingTypeId);
}
