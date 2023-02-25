package com.dxvalley.crowdfunding.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.CampaignAddRequestDto;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.CampaignStage;
import com.dxvalley.crowdfunding.services.CampaignSubCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.dxvalley.crowdfunding.services.CampaignService;
import com.dxvalley.crowdfunding.services.FileUploadService;
import com.dxvalley.crowdfunding.services.FundingTypeService;
import com.dxvalley.crowdfunding.models.Campaign;


@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private FundingTypeService fundingTypeService;
    @Autowired
    private CampaignSubCategoryService campaignSubCategoryService;


    @GetMapping("/getCampaigns")
    ResponseEntity<?> getCampaigns() {
       return new ResponseEntity<>(
                campaignService.getCampaigns(),
                HttpStatus.OK);
    }
    @GetMapping("/getEnabledCampaigns")
    ResponseEntity<?> getEnabledCampaigns() {
        return new ResponseEntity<>(
                campaignService.getEnabledCampaigns(),
                HttpStatus.OK);
    }

    @GetMapping("getCampaignById/{campaignId}")
    ResponseEntity<?> getCampaign(@PathVariable Long campaignId)
            throws ResourceNotFoundException {
        return new ResponseEntity<>(
                campaignService.getCampaignById(campaignId),
                HttpStatus.OK);
    }

    @GetMapping("/getCampaignByOwner/{owner}")
    ResponseEntity<?> getCampaignByOwner(@PathVariable String owner) {
        return new ResponseEntity<>(
                campaignService.getCampaignsByOwner(owner),
                HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByCategory/{categoryId}")
   ResponseEntity<?> getCampaignsByCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(
                campaignService.getCampaignByCategory(categoryId),
                HttpStatus.OK);
    }

    @GetMapping("/getCampaignsBySubCategory/{subCategoryId}")
    ResponseEntity<?>  getCampaignsBySubCategory(@PathVariable Long subCategoryId) {
        return new ResponseEntity<>(
                campaignService.getCampaignBySubCategory(subCategoryId),
                HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByStage/{campaignStage}")
    ResponseEntity<?> getCampaignsByStage(@PathVariable String campaignStage) {
        return new ResponseEntity<>(
                campaignService.getCampaignsByStage(campaignStage),
                HttpStatus.OK);
    }

    @GetMapping("/getCampaignsByFundingType/{fundingTypeId}")
    ResponseEntity<?> getCampaignsByFundingType(@PathVariable Long fundingTypeId) {
        return new ResponseEntity<>(
                campaignService.getCampaignsByFundingType(fundingTypeId),
                HttpStatus.OK);
    }

    @GetMapping("/searchCampaigns")
    ResponseEntity<?> searchCampaigns(@RequestParam String searchParam) {
        return new ResponseEntity<>(
                campaignService.searchCampaigns(searchParam),
                HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCampaign(@Valid @RequestBody CampaignAddRequestDto campaignAddRequestDto) {
        Campaign res = campaignService.addCampaign(campaignAddRequestDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("edit/{campaignId}")
    ResponseEntity<?> editCampaign(
            @PathVariable Long campaignId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long fundingTypeId,
            @RequestParam(required = false) Long campaignSubCategoryId,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) Double goalAmount,
            @RequestParam(required = false) Short campaignDuration,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String risks,
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String campaignStage,

            @RequestParam(required = false) MultipartFile campaignImage,
            @RequestParam(required = false) MultipartFile campaignVideo
    ) throws ResourceNotFoundException {

        Campaign campaign = this.campaignService.getCampaignById(campaignId);
        if(campaign == null){
            throw new ResourceNotFoundException("There is no campaign with this ID.");
        }
        String imageUrl;
        String videoUrl;
        if(campaignImage != null){
            try {
                imageUrl = fileUploadService.uploadFile(campaignImage);
            } catch (Exception e) {
                ApiResponse response = new ApiResponse("error", "Bad file size or format!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }else{
            imageUrl = null;
        }

        if(campaignVideo != null){
            try {
                videoUrl = fileUploadService.uploadFileVideo(campaignVideo);
            } catch (Exception e) {
                ApiResponse response = new ApiResponse("error", "Bad file size or format!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }else{
            videoUrl = null;
        }

        LocalDateTime editedAt = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        campaign.setFundingType(fundingTypeId != null?
                fundingTypeService.getFundingTypeById(fundingTypeId) : campaign.getFundingType());

        campaign.setCampaignSubCategory(campaignSubCategoryId != null?
                campaignSubCategoryService.getCampaignSubCategoryById(campaignSubCategoryId) :
                campaign.getCampaignSubCategory());

        campaign.setEditedAt(editedAt.format(dateTimeFormatter));
        campaign.setTitle(title != null ? title : campaign.getTitle());
        campaign.setShortDescription(shortDescription != null ? shortDescription : campaign.getShortDescription());
        campaign.setCity(city != null ? city : campaign.getCity());
        campaign.setProjectType(projectType != null? projectType : campaign.getProjectType());
        campaign.setCampaignStage(campaignStage != null? CampaignStage.lookup(campaignStage): campaign.getCampaignStage());
        campaign.setGoalAmount(goalAmount != null ? goalAmount : campaign.getGoalAmount());
        campaign.setCampaignDuration(campaignDuration != null ? campaignDuration : campaign.getCampaignDuration());
        campaign.setRisks(risks != null ? risks : campaign.getRisks());
        campaign.setDescription(description != null ? description : campaign.getDescription());
        campaign.setImageUrl(imageUrl != null ? imageUrl : campaign.getImageUrl());
        campaign.setVideoLink(videoUrl != null ? videoUrl : campaign.getVideoLink());

        campaignService.editCampaign(campaign);
        return new ResponseEntity<>(campaign, HttpStatus.OK);
    }

    @PutMapping("enableCampaign/{campaignId}")
    ResponseEntity<?> enableCampaign(
            @PathVariable Long campaignId
    ) throws ResourceNotFoundException {
        var result =  campaignService.enableCampaign(campaignId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("delete/{campaignId}")
    ResponseEntity<?> deleteCampaign(@PathVariable Long campaignId) throws ResourceNotFoundException {
        campaignService.deleteCampaign(campaignId);
        ApiResponse response =  new ApiResponse("success","Campaign successfully deleted!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
