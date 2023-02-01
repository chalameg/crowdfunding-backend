package com.dxvalley.crowdfunding.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dxvalley.crowdfunding.models.CampaignSubCategory;
import com.dxvalley.crowdfunding.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dxvalley.crowdfunding.services.CampaignService;
import com.dxvalley.crowdfunding.services.FileUploadService;
import com.dxvalley.crowdfunding.services.FundingTypeService;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.FundingType;
import com.dxvalley.crowdfunding.models.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
public class CampaignController {
    private final CampaignService campaignService;
    private final FileUploadService fileUploadService;
    private final FundingTypeService fundingTypeService;
    private final PaymentRepository paymentRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final RewardRepository rewardRepository;
    private final CampaignSubCategoryRepository campaignSubCategoryRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;


    @GetMapping("/getCampaigns")
    List<Campaign> getCampaigns() {
        return this.campaignService.getCampaigns();
    }

    @GetMapping("getCampaignById/{campaignId}")
    Campaign getCampaign(@PathVariable Long campaignId) {
        var campaign =  campaignService.getCampaignById(campaignId);
        var payment =  paymentRepository.findPaymentByCampaignId(campaignId);
        var collaborators = collaboratorRepository.findAllCollaboratorByCampaignId(campaignId);
        var rewards = rewardRepository.findRewardsByCampaignId(campaignId);
        campaign.setPayment(payment);
        campaign.setCollaborators(collaborators);
        campaign.setRewards(rewards);

        System.out.println(campaign.getOwner());
        Users user = userRepository.findUserByUsername(campaign.getOwner());

        campaign.setOwnerName(user.getFullName());

        campaign.setNumberOfCampaigns(campaignRepository.findCampaignsByOwner(campaign.getOwner()).size());

        return campaign;
    }

    @GetMapping("/getCampaignByOwner/{owner}")
    List<Campaign> getCampaignByOwner(@PathVariable String owner) {
        return campaignService.getCampaignsByOwner(owner);
    }

    @GetMapping("/getCampaignsByCategory/{categoryId}")
    List<Campaign> getCampaignsByCategory(@PathVariable Long categoryId) {
        return campaignService.getCampaignByCategory(categoryId);
    }

    @GetMapping("/getCampaignsBySubCategory/{subCategoryId}")
    List<Campaign> getCampaignsBySubCategory(@PathVariable Long subCategoryId) {
        return campaignService.getCampaignBySubCategory(subCategoryId);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addCampaign(
            @RequestParam() String title,
            @RequestParam() String city,
            @RequestParam() String owner,
            @RequestParam() Long fundingTypeId,
            @RequestParam() Long campaignSubCategoryId) {

        Campaign campaign = new Campaign();

        FundingType fundingType = fundingTypeService.getFundingTypeById(fundingTypeId);
        CampaignSubCategory campaignSubCategory =  campaignSubCategoryRepository
                .findCampaignSubCategoryByCampaignSubCategoryId(campaignSubCategoryId);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        campaign.setTitle(title);
        campaign.setCity(city);
        campaign.setOwner(owner);
        campaign.setCampaignSubCategory(campaignSubCategory);
        campaign.setFundingType(fundingType);
        campaign.setIsEnabled(false);
        campaign.setDateCreated(dateFormat.format(currentDate));
        Campaign res = campaignService.addCampaign(campaign);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("edit/{campaignId}")
    ResponseEntity<?> editCampaign(
            @PathVariable Long campaignId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long fundingTypeId,
            @RequestParam(required = false) Long campaignSubCategoryId,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) String goalAmount,
            @RequestParam(required = false) String campaignDuration,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(required = false) String risks,
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String campaignStatus,

            @RequestParam(required = false) MultipartFile campaignImage,
            @RequestParam(required = false) MultipartFile campaignVideo
    ) {

        Campaign campaign = this.campaignService.getCampaignById(campaignId);
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

        FundingType fundingType = fundingTypeService.getFundingTypeById(fundingTypeId);
        CampaignSubCategory campaignSubCategory =  campaignSubCategoryRepository
                .findCampaignSubCategoryByCampaignSubCategoryId(campaignSubCategoryId);

        campaign.setFundingType(fundingType != null? fundingType : campaign.getFundingType());
        campaign.setCampaignSubCategory(campaignSubCategory != null? campaignSubCategory : campaign.getCampaignSubCategory());

        campaign.setTitle(title != null ? title : campaign.getTitle());
        campaign.setShortDescription(shortDescription != null ? shortDescription : campaign.getShortDescription());
        campaign.setCity(city != null ? city : campaign.getCity());
        campaign.setProjectType(projectType != null? projectType : campaign.getProjectType());
        campaign.setCampaignStatus(campaignStatus != null? campaignStatus : campaign.getCampaignStatus());
        campaign.setGoalAmount(goalAmount != null ? goalAmount : campaign.getGoalAmount());
        campaign.setCampaignDuration(campaignDuration != null ? campaignDuration : campaign.getCampaignDuration());
        campaign.setRisks(risks != null ? risks : campaign.getRisks());
        campaign.setDescription(description != null ? description : campaign.getDescription());
        campaign.setIsEnabled(isEnabled != null ? isEnabled : campaign.getIsEnabled());

        campaign.setImageUrl(imageUrl != null ? imageUrl : campaign.getImageUrl());
        campaign.setVideoLink(videoUrl != null ? videoUrl : campaign.getVideoLink());

        campaignService.editCampaign(campaign);
        return new ResponseEntity<>(campaign, HttpStatus.OK);
    }

    @DeleteMapping("/{campaignId}")
    ResponseEntity<?> deleteCampaign(@PathVariable Long campaignId) {
        Campaign campaign = this.campaignService.getCampaignById(campaignId);
        if(campaign == null) return new ResponseEntity<String>("Entry does not exist!", HttpStatus.BAD_REQUEST);
        campaignService.deleteCampaign(campaignId);
        ApiResponse response = new ApiResponse("success", "Campaign Deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}


@Getter
@Setter
@AllArgsConstructor
class CampaignResponse {
    Campaign Campaign;
    String status;
}
