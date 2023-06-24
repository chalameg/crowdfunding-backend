package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import java.util.List;

public interface FundingTypeService {
    List<FundingType> getFundingTypes();

    FundingType getFundingTypeById(Short fundingTypeId);
}
