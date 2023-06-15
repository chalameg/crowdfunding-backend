package com.dxvalley.crowdfunding.campaign.campaignFundingType;

import com.dxvalley.crowdfunding.campaign.campaignFundingType.dto.FundingTypeReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fundingTypes")
public class FundingTypeController {
    private final FundingTypeService fundingTypeService;


    @GetMapping
    ResponseEntity<List<FundingType>> getFundingTypes() {
        return ResponseEntity.ok(fundingTypeService.getFundingTypes());
    }

    @GetMapping("/{fundingTypeId}")
    ResponseEntity<FundingType> getFundingType(@PathVariable Short fundingTypeId) {
        return ResponseEntity.ok(fundingTypeService.getFundingTypeById(fundingTypeId));
    }

    @PostMapping
    ResponseEntity<FundingType> addFundingType(@RequestBody @Valid FundingTypeReq fundingTypeReq) {
        return ResponseEntity.ok(fundingTypeService.addFundingType(fundingTypeReq));
    }

    @PutMapping("/{fundingTypeId}")
    ResponseEntity<FundingType> editFundingType(@PathVariable Short fundingTypeId, @RequestBody FundingTypeReq fundingTypeReq) {
        return ResponseEntity.ok(fundingTypeService.editFundingType(fundingTypeId, fundingTypeReq));
    }

    @DeleteMapping("/{fundingTypeId}")
    ResponseEntity<ApiResponse> deleteFundingType(@PathVariable Short fundingTypeId) {
        return fundingTypeService.deleteFundingType(fundingTypeId);
    }
}
