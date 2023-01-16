package com.dxvalley.crowdfunding.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dxvalley.crowdfunding.services.CampaignService;
import com.dxvalley.crowdfunding.services.FileUploadService;
import com.dxvalley.crowdfunding.models.Campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
  private final CampaignService campaignService;
  private final FileUploadService fileUploadService;
  
  
  public CampaignController(CampaignService campaignService, FileUploadService fileUploadService) {
    this.campaignService = campaignService;
    this.fileUploadService = fileUploadService;
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

  @GetMapping("/getCampaigns/{ownerId}")
  List<Campaign> getUserCampaigns(@PathVariable String owner) {
        List<Campaign> Campaign = campaignService.findCampaignsByOwner(owner);
       
        return Campaign ;
  }

  @PostMapping
  public ResponseEntity<?> addCampaign(@RequestParam("title") String title,
    @RequestParam("shortDescription") String shortDescription,
    @RequestParam("city") String city,
    @RequestParam("goalAmount") String goalAmount,
    @RequestParam("campaignDuration") String campaignDuration,
    @RequestParam("campaignImage") MultipartFile campaignImage,
    @RequestParam("description") String description,
    @RequestParam("owner") String owner,
    @RequestParam("risks") String risks
      // @RequestParam("campaignVideo") MultipartFile campaignVideo
    ) {
      
      String imageUrl;
      // String campaignVideoUrl;
      Campaign campaign = new Campaign();
      try {
        imageUrl = fileUploadService.uploadFile(campaignImage);
        // campaignVideoUrl = fileUploadService.uploadFileVideo(campaignVideo);
      } catch (Exception e) {
        createUserResponse response = new createUserResponse("error", "Bad file size or format!");
  
        return new ResponseEntity<>(response, HttpStatus.OK);
      }
      
      campaign.setImageUrl(imageUrl);
      // campaign.setVideoUrl(campaignVideoUrl);
      campaign.setTitle(title);
      campaign.setShortDescription(shortDescription);
      campaign.setCity(city);
      campaign.setCampaignDuration(campaignDuration);
      campaign.setGoalAmount(goalAmount);
      campaign.setDecription(description);
      campaign.setRisks(risks);
      campaign.setReward(null);
      campaign.setOwner(owner);
      campaign.setIsEnabled(false);
  
      Campaign res = campaignService.addCampaign(campaign);

    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @PutMapping("/{campaignId}")
  Campaign editCampaign(@RequestBody Campaign tempCampaign, @PathVariable Long campaignId) {
    Campaign campaign = this.campaignService.getCampaignById(campaignId);
    campaign.setTitle(tempCampaign.getTitle() != null ? tempCampaign.getTitle() : campaign.getTitle());
    campaign.setShortDescription(tempCampaign.getShortDescription() != null ? tempCampaign.getShortDescription() : campaign.getShortDescription());
    campaign.setCity(tempCampaign.getCity() != null ? tempCampaign.getCity() : campaign.getCity());
    campaign.setImageUrl(tempCampaign.getImageUrl() != null ? tempCampaign.getImageUrl() : campaign.getImageUrl());
    campaign.setVideoLink(tempCampaign.getVideoLink() != null ? tempCampaign.getVideoLink() : campaign.getVideoLink());
    campaign.setGoalAmount(tempCampaign.getGoalAmount() != null ? tempCampaign.getGoalAmount() : campaign.getGoalAmount());
    campaign.setCampaignDuration(tempCampaign.getCampaignDuration() != null ? tempCampaign.getCampaignDuration() : campaign.getCampaignDuration());
    campaign.setIsEnabled(tempCampaign.getIsEnabled() != null ? tempCampaign.getIsEnabled() : campaign.getIsEnabled());

    return campaignService.editCampaign(campaign);
  }

  @DeleteMapping("/{campaignId}")
  ResponseEntity<?> deleteCampaign(@PathVariable Long campaignId) {
    Campaign campaign = this.campaignService.getCampaignById(campaignId);

    if(campaign == null) return new ResponseEntity<String>("Entry does not exist!", HttpStatus.BAD_REQUEST);

    campaignService.deleteCampaign(campaignId);

    return new ResponseEntity<String>("Deleted", HttpStatus.OK);
  }

}


@Getter
@Setter
@AllArgsConstructor
class CampaignResponse {
  Campaign Campaign;
  String status;
}
