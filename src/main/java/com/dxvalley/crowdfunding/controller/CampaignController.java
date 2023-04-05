package com.dxvalley.crowdfunding.controller;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.CampaignAddRequestDto;
import com.dxvalley.crowdfunding.dto.CampaignDTO;
import com.dxvalley.crowdfunding.dto.CampaignLikeDTO;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
    @Autowired
    private CampaignService campaignService;

    @GetMapping("/getCampaigns")
    ResponseEntity<?> getCampaigns() {
        return new ResponseEntity<>(campaignService.getCampaigns(), HttpStatus.OK);
    }

    @GetMapping("/getEnabledCampaigns")
    ResponseEntity<?> getEnabledCampaigns() {
        return new ResponseEntity<>(campaignService.getEnabledCampaigns(), HttpStatus.OK);
    }

    @GetMapping("getCampaignById/{campaignId}")
    ResponseEntity<?> getCampaign(@PathVariable Long campaignId) {
        return new ResponseEntity<>(campaignService.getCampaignById(campaignId), HttpStatus.OK);
    }

    @GetMapping("/getCampaignByOwner/{owner}")
    ResponseEntity<?> getCampaignByOwner(@PathVariable String owner) {
        return new ResponseEntity<>(campaignService.getCampaignsByOwner(owner), HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByCategory/{categoryId}")
    ResponseEntity<?> getCampaignsByCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(campaignService.getCampaignByCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping("/getCampaignsBySubCategory/{subCategoryId}")
    ResponseEntity<?> getCampaignsBySubCategory(@PathVariable Long subCategoryId) {
        return new ResponseEntity<>(campaignService.getCampaignBySubCategory(subCategoryId), HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByStage/{campaignStage}")
    ResponseEntity<?> getCampaignsByStage(@PathVariable String campaignStage) {
        return new ResponseEntity<>(campaignService.getCampaignsByStage(campaignStage), HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByFundingType/{fundingTypeId}")
    ResponseEntity<?> getCampaignsByFundingType(@PathVariable Long fundingTypeId) {
        return new ResponseEntity<>(campaignService.getCampaignsByFundingType(fundingTypeId), HttpStatus.OK);
    }

    @GetMapping("/searchCampaigns")
    ResponseEntity<?> searchCampaigns(@RequestParam String searchParam) {
        return new ResponseEntity<>(campaignService.searchCampaigns(searchParam), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCampaign(@Valid @RequestBody CampaignAddRequestDto campaignAddRequestDto) {
        return new ResponseEntity<>(campaignService.addCampaign(campaignAddRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/like")
    public ResponseEntity<?> likeCampaign(@RequestBody @Valid CampaignLikeDTO campaignLikeDTO) {
        var result = campaignService.likeCampaign(campaignLikeDTO);
        ApiResponse response = new ApiResponse("success", result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("edit/{campaignId}")
    ResponseEntity<?> editCampaign(@PathVariable Long campaignId, @RequestBody CampaignDTO campaignDTO) {
        return new ResponseEntity<>(campaignService.editCampaign(campaignId, campaignDTO), HttpStatus.OK);
    }

    @PutMapping("uploadMedias/{campaignId}")
    ResponseEntity<?> uploadMedias(
            @PathVariable Long campaignId,
            @RequestParam(required = false) MultipartFile campaignImage,
            @RequestParam(required = false) String campaignVideo) {
        return new ResponseEntity<>(campaignService.uploadMedias(campaignId, campaignImage, campaignVideo), HttpStatus.OK);
    }

    @PutMapping("enableCampaign/{campaignId}")
    ResponseEntity<?> enableCampaign(@PathVariable Long campaignId) {
        return new ResponseEntity<>(campaignService.enableCampaign(campaignId), HttpStatus.OK);
    }

    @PutMapping("pause-resume/{campaignId}")
    ResponseEntity<?> pauseCampaign(@PathVariable Long campaignId) {
        return new ResponseEntity<>(campaignService.pauseOrResumeCampaign(campaignId), HttpStatus.OK);
    }

    @DeleteMapping("delete/{campaignId}")
    ResponseEntity<?> deleteCampaign(@PathVariable Long campaignId) throws ResourceNotFoundException {
        campaignService.deleteCampaign(campaignId);
        ApiResponse response = new ApiResponse("success", "Campaign successfully deleted!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
