package com.dxvalley.crowdfunding.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dxvalley.crowdfunding.models.FundingType;
import com.dxvalley.crowdfunding.services.FundingTypeService;

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
