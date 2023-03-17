package com.dxvalley.crowdfunding.service.impl;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.model.FundingType;
import com.dxvalley.crowdfunding.repository.FundingTypeRepository;
import com.dxvalley.crowdfunding.service.FundingTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

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
