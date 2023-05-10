package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fundingTypes")
public class FundingTypeController {
  private final FundingTypeService fundingTypeService;

  public FundingTypeController(FundingTypeService fundingTypeService) {
    this.fundingTypeService = fundingTypeService;
  }

  @GetMapping
  List<FundingType> getFundingTypes() {
    return this.fundingTypeService.getFundingTypes();
  }

  @GetMapping("/{fundingTypeId}")
  FundingType getEqubCategory(@PathVariable Long fundingTypeId) {
    return fundingTypeService.getFundingTypeById(fundingTypeId);
  }

  @PostMapping
  FundingType addFundingType(@RequestBody FundingType fundingTypes) {
    return fundingTypeService.addFundingType(fundingTypes);
  }

  @PutMapping("/{fundingTypeId}")
  FundingType editFundingType(@RequestBody FundingType tempFundingType, @PathVariable Long fundingTypeId) {
    FundingType fundingType = this.fundingTypeService.getFundingTypeById(fundingTypeId);
    fundingType.setName(tempFundingType.getName());

    return fundingTypeService.editFundingType(fundingType);
  }

  @DeleteMapping("/{fundingTypeId}")
  void deleteFundingType(@PathVariable Long fundingTypeId) {
    fundingTypeService.deleteFundingType(fundingTypeId);
  }
}
