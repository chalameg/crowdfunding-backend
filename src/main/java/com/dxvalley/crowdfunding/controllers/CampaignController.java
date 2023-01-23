package com.dxvalley.crowdfunding.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dxvalley.crowdfunding.repositories.CampaignCategoryRepository;
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
import com.dxvalley.crowdfunding.models.CampaignCategory;
import com.dxvalley.crowdfunding.models.FundingType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
  private final CampaignService campaignService;
  private final FileUploadService fileUploadService;
  private final FundingTypeService fundingTypeService;

  private final CampaignCategoryRepository campaignCategoryRepository;
  

  public CampaignController(CampaignService campaignService, FileUploadService fileUploadService,
      FundingTypeService fundingTypeService, CampaignCategoryRepository campaignCategoryRepository) {
    this.campaignService = campaignService;
    this.fileUploadService = fileUploadService;
    this.fundingTypeService = fundingTypeService;
    this.campaignCategoryRepository = campaignCategoryRepository;
  }

  @GetMapping
  List<Campaign> getCampaigns() {
    return this.campaignService.getCampaigns();
  }

  @GetMapping("/{campaignId}")
  Campaign getCampaign(@PathVariable Long campaignId) {
        Campaign Campaign = campaignService.getCampaignById(campaignId);
       
        return Campaign ;
  }

  @GetMapping("/getCampaigns/{owner}")
  List<Campaign> getUserCampaigns(@PathVariable String owner) {
        List<Campaign> Campaign = campaignService.findCampaignsByOwner(owner);
       
        return Campaign ;
  }

  @PostMapping("/{fundingTypeId}/{campaignCategoryId}")
  public ResponseEntity<?> addCampaign(@RequestParam("title") String title,
    @RequestParam("shortDescription") String shortDescription,
    @RequestParam(required = false) String city,
    @RequestParam(required = false) String goalAmount,
    @RequestParam(required = false) String campaignDuration,
    @RequestParam(required = false) MultipartFile campaignImage,
    @RequestParam(required = false) String description,
    @RequestParam("owner") String owner,
    @RequestParam(required = false) String risks,
    @PathVariable Long fundingTypeId,
    @PathVariable Long campaignCategoryId
      // @RequestParam("campaignVideo") MultipartFile campaignVideo
    ) {
      
      String imageUrl;
      // String campaignVideoUrl;
      Campaign campaign = new Campaign();

      if(campaignImage != null){
        try {
          imageUrl = fileUploadService.uploadFile(campaignImage);
          // campaignVideoUrl = fileUploadService.uploadFileVideo(campaignVideo);
        } catch (Exception e) {
          ApiResponse response = new ApiResponse("error", "Bad file size or format!");
    
          return new ResponseEntity<>(response, HttpStatus.OK);
        }
      }else{
        imageUrl = null;
      }

      FundingType fundingType = fundingTypeService.getFundingTypeById(fundingTypeId);
      CampaignCategory campaignCategory =   campaignCategoryRepository.findCampaignCategoryByCampaignCategoryId(campaignCategoryId);
      
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date currentDate = new Date();
      campaign.setImageUrl(imageUrl);
      // campaign.setVideoUrl(campaignVideoUrl);
      campaign.setTitle(title);
      campaign.setShortDescription(shortDescription);
      campaign.setCity(city);
      campaign.setCampaignDuration(campaignDuration);
      campaign.setGoalAmount(goalAmount);
      campaign.setDecription(description);
      campaign.setRisks(risks);
      // campaign.setRewards(null);
      campaign.setOwner(owner);
      campaign.setIsEnabled(false);
      campaign.setDateCreated(dateFormat.format(currentDate));  
      campaign.setCampaignCategory(campaignCategory);
      campaign.setFundingType(fundingType);

      Campaign res = campaignService.addCampaign(campaign);

      return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @PutMapping("/{campaignId}")
  ResponseEntity<?> editCampaign(@RequestParam(required = false) String title,
    @RequestParam(required = false) String shortDescription,
    @RequestParam(required = false) String city,
    @RequestParam(required = false) String goalAmount,
    @RequestParam(required = false) String campaignDuration,
    @RequestParam(required = false) MultipartFile campaignImage,
    @RequestParam(required = false) String description,
    @RequestParam(required = false) Boolean isEnabled,
    @RequestParam(required = false) String risks, @PathVariable Long campaignId) {

      Campaign campaign = this.campaignService.getCampaignById(campaignId);
      String imageUrl;
      // String campaignVideoUrl;
      if(campaignImage != null){
        try {
          imageUrl = fileUploadService.uploadFile(campaignImage);
          // campaignVideoUrl = fileUploadService.uploadFileVideo(campaignVideo);
        } catch (Exception e) {
          ApiResponse response = new ApiResponse("error", "Bad file size or format!");
    
          return new ResponseEntity<>(response, HttpStatus.OK);
        }
      }else{
        imageUrl = null;
      }

    campaign.setTitle(title != null ? title : campaign.getTitle());
    campaign.setShortDescription(shortDescription != null ? shortDescription : campaign.getShortDescription());
    campaign.setCity(city != null ? city : campaign.getCity());
    campaign.setImageUrl(imageUrl != null ? imageUrl : campaign.getImageUrl());
    // campaign.setVideoLink(tempCampaign.getVideoLink() != null ? tempCampaign.getVideoLink() : campaign.getVideoLink());
    campaign.setGoalAmount(goalAmount != null ? goalAmount : campaign.getGoalAmount());
    campaign.setCampaignDuration(campaignDuration != null ? campaignDuration : campaign.getCampaignDuration());
    campaign.setRisks(risks != null ? risks : campaign.getRisks());
    campaign.setDecription(description != null ? description : campaign.getDecription());
    campaign.setIsEnabled(isEnabled != null ? isEnabled : campaign.getIsEnabled());

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
