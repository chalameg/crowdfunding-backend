package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddDto;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface CampaignService {
    Campaign addCampaign(CampaignAddDto campaignAddRequestDto);

    String likeCampaign(CampaignLikeDTO campaignLikeDTO);

    CampaignDTO editCampaign(Long campaignId, CampaignDTO campaignDTO);

    List<CampaignDTO> getCampaigns();

    CampaignDTO getCampaignById(Long campaignId);

    List<CampaignDTO> getCampaignByCategory(Long categoryId);

    List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId);

    void deleteCampaign(Long campaignId);

    List<CampaignDTO> getCampaignsByOwner(String owner);

    Campaign enableCampaign(Long campaignId);

    List<CampaignDTO> searchCampaigns(String searchParam);

    List<CampaignDTO> getCampaignsByStage(String campaignStage);

    List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId);

    ResponseEntity pauseResumeCampaign(Long campaignID);

    Campaign utilGetCampaignById(Long campaignId);

    CampaignDTO uploadMedias(Long campaignId, MultipartFile campaignImage, String campaignVideo);

    CampaignDTO submitWithdrawCampaign(Long campaignId);

    CampaignDTO uploadFiles(Long campaignId, List<MultipartFile> files);

    CompletableFuture<ResponseEntity<?>> createCampaignWithMediaAsync(Long campaignId, List<MultipartFile> files);

    CompletableFuture<Void> updateCampaignStatus();
}
