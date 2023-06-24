package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import com.dxvalley.crowdfunding.campaign.campaignFundingType.dto.FundingTypeReq;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundingTypeServiceImpl implements FundingTypeService {
    private final FundingTypeRepository fundingTypeRepository;

    public List<FundingType> getFundingTypes() {
        List<FundingType> fundingTypes = this.fundingTypeRepository.findAll();
        if (fundingTypes.isEmpty()) {
            throw new ResourceNotFoundException("There is no Funding Type");
        } else {
            return fundingTypes;
        }
    }

    public FundingType getFundingTypeById(Short fundingTypeId) {
        return (FundingType)this.fundingTypeRepository.findById(fundingTypeId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no Funding Type with this ID.");
        });
    }

    public FundingTypeServiceImpl(final FundingTypeRepository fundingTypeRepository) {
        this.fundingTypeRepository = fundingTypeRepository;
    }
}
