package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
//TODO: update current impl
@Service
public class FundingTypeServiceImpl implements FundingTypeService{

    private final FundingTypeRepository fundingTypeRepository;

    public FundingTypeServiceImpl(FundingTypeRepository fundingTypeRepository) {
        this.fundingTypeRepository = fundingTypeRepository;
    }

    @Override
    public FundingType addFundingType(FundingType fundingType) {
        return this.fundingTypeRepository.save(fundingType);
    }

    @Override
    public FundingType editFundingType(FundingType fundingType) {
        return this.fundingTypeRepository.save(fundingType);
    }

    @Override
    public List<FundingType> getFundingTypes() {
        return this.fundingTypeRepository.findAll();
    }

    @Override
    public FundingType getFundingTypeById(Long fundingTypeId) {
        return this.fundingTypeRepository.findFundingTypeByFundingTypeId(fundingTypeId).orElseThrow(
                () ->  new ResourceNotFoundException("There is no Funding Type with this ID.")
        );
    }
    @Override
    public void deleteFundingType(Long fundingTypeId) {
        fundingTypeRepository.deleteById(fundingTypeId);
    }
}
