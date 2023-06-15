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
@RequiredArgsConstructor
public class FundingTypeServiceImpl implements FundingTypeService {

    private final FundingTypeRepository fundingTypeRepository;

    @Override
    public FundingType addFundingType(FundingTypeReq fundingTypeReq) {
        String name = fundingTypeReq.getName();
        validateFundingTypeNameNotExist(name);
        FundingType fundingType = createFundingType(name);

        return fundingTypeRepository.save(fundingType);
    }

    @Override
    public FundingType editFundingType(Short id, FundingTypeReq fundingTypeReq) {
        FundingType fundingType = getFundingTypeById(id);

        fundingType.setName(fundingTypeReq.getName());

        return fundingTypeRepository.save(fundingType);
    }

    @Override
    public List<FundingType> getFundingTypes() {

        List<FundingType> fundingTypes = fundingTypeRepository.findAll();

        if (fundingTypes.isEmpty())
            throw new ResourceNotFoundException("There is no Funding Type");

        return fundingTypes;
    }

    @Override
    public FundingType getFundingTypeById(Short fundingTypeId) {
        return fundingTypeRepository.findById(fundingTypeId).orElseThrow(
                () -> new ResourceNotFoundException("There is no Funding Type with this ID.")
        );
    }

    @Override
    public ResponseEntity<ApiResponse> deleteFundingType(Short fundingTypeId) {
        getFundingTypeById(fundingTypeId);
        fundingTypeRepository.deleteById(fundingTypeId);

        return ApiResponse.success("Deleted Successfully");
    }

    private void validateFundingTypeNameNotExist(String name) {
        FundingType fundingType = fundingTypeRepository.findByName(name);
        if (fundingType != null)
            throw new ResourceAlreadyExistsException("There is already a funding type with this name!");

    }

    private FundingType createFundingType(String name) {
        FundingType fundingType = new FundingType();
        fundingType.setName(name);

        return fundingType;
    }
}
