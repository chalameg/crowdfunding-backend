package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddDto;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CampaignService {
    Campaign addCampaign(CampaignAddDto campaignAddRequestDto);

    CampaignDTO editCampaign(Long campaignId, CampaignDTO campaignDTO);

    CampaignDTO uploadMedias(Long campaignId, MultipartFile campaignImage, String campaignVideo);

    CampaignDTO uploadFiles(Long campaignId, List<MultipartFile> files);

    CampaignDTO submitCampaign(Long campaignId);

    CampaignDTO withdrawCampaign(Long campaignId);

    CampaignDTO pauseCampaign(Long campaignId);

    CampaignDTO resumeCampaign(Long campaignId);

    String likeCampaign(CampaignLikeDTO campaignLikeDTO);

    List<CampaignDTO> getCampaigns();

    CampaignDTO getCampaignById(Long campaignId);

    List<CampaignDTO> getCampaignByCategory(Long categoryId);

    List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId);

    List<CampaignDTO> getCampaignsByOwner(String owner);

    List<CampaignDTO> searchCampaigns(String searchParam);

    List<CampaignDTO> getCampaignsByStage(String campaignStage);

    List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId);

    void deleteCampaign(Long campaignId);
}
