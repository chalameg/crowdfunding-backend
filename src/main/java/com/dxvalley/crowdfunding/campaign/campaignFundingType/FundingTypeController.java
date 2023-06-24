package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/fundingTypes"})
public class FundingTypeController {
    private final FundingTypeService fundingTypeService;

    @GetMapping
    ResponseEntity<List<FundingType>> getFundingTypes() {
        return ResponseEntity.ok(this.fundingTypeService.getFundingTypes());
    }

    @GetMapping({"/{fundingTypeId}"})
    ResponseEntity<FundingType> getFundingType(@PathVariable Short fundingTypeId) {
        return ResponseEntity.ok(this.fundingTypeService.getFundingTypeById(fundingTypeId));
    }

    public FundingTypeController(final FundingTypeService fundingTypeService) {
        this.fundingTypeService = fundingTypeService;
    }
}