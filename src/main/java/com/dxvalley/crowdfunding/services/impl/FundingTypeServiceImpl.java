package com.dxvalley.crowdfunding.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.FundingType;
import com.dxvalley.crowdfunding.repositories.FundingTypeRepository;

import com.dxvalley.crowdfunding.services.FundingTypeService;

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
        return this.fundingTypeRepository.findFundingTypeByFundingTypeId(fundingTypeId);
    }

    @Override
    public void deleteFundingType(Long fundingTypeId) {
        fundingTypeRepository.deleteById(fundingTypeId);
    }
    
}
