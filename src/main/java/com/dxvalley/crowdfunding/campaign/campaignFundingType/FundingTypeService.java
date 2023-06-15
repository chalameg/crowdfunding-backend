package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import com.dxvalley.crowdfunding.campaign.campaignFundingType.dto.FundingTypeReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FundingTypeService {
    FundingType addFundingType(FundingTypeReq fundingTypeReq);

    FundingType editFundingType(Short id, FundingTypeReq fundingTypeReq);

    List<FundingType> getFundingTypes();

    FundingType getFundingTypeById(Short fundingTypeId);

    ResponseEntity<ApiResponse> deleteFundingType(Short fundingTypeId);
}
