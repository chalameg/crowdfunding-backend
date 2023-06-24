package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image.CampaignImageService;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video.CampaignVideoService;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddReq;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignMapper;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignUpdateReq;
import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingType;
import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingTypeService;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLike;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeRepository;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeReq;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategory;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategoryService;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CampaignOperationServiceImpl implements CampaignOperationService {
    private final FundingTypeService fundingTypeService;
    private final CampaignSubCategoryService campaignSubCategoryService;
    private final UserUtils userUtils;
    private final CampaignMapper campaignMapper;
    private final CampaignLikeRepository campaignLikeRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final CampaignUtils campaignUtils;
    private final CampaignVideoService campaignVideoService;
    private final CampaignImageService campaignImageService;

    public Campaign addCampaign(CampaignAddReq campaignAddReq) {
        Users user = this.userUtils.utilGetUserByUsername(campaignAddReq.getOwner());
        this.userUtils.verifyUser(user);
        this.userUtils.verifyUserEmail(user);
        FundingType fundingType = this.fundingTypeService.getFundingTypeById(campaignAddReq.getFundingTypeId());
        CampaignSubCategory campaignSubCategory = this.campaignSubCategoryService.getCampaignSubCategoryById(campaignAddReq.getCampaignSubCategoryId());
        Campaign campaign = this.createCampaign(campaignAddReq, user, campaignSubCategory, fundingType);
        Campaign registeredCampaign = this.campaignUtils.saveCampaign(campaign);
        return registeredCampaign;
    }

    private Campaign createCampaign(CampaignAddReq campaignAddReq, Users user, CampaignSubCategory campaignSubCategory, FundingType fundingType) {
        return Campaign.builder().title(campaignAddReq.getTitle()).city(campaignAddReq.getCity()).user(user).campaignSubCategory(campaignSubCategory).fundingType(fundingType).createdAt(LocalDateTime.now().format(this.dateTimeFormatter)).campaignStage(CampaignStage.INITIAL).build();
    }

    public CampaignDTO editCampaign(Long campaignId, CampaignUpdateReq campaignUpdateReq) {
        try {
            Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
            this.campaignUtils.validateCampaignStage(campaign, CampaignStage.INITIAL, "Campaign cannot be updated unless it is in the initial stage");
            if (campaignUpdateReq.getTitle() != null) {
                campaign.setTitle(campaignUpdateReq.getTitle());
            }

            if (campaignUpdateReq.getShortDescription() != null) {
                campaign.setShortDescription(campaignUpdateReq.getShortDescription());
            }

            if (campaignUpdateReq.getCity() != null) {
                campaign.setCity(campaignUpdateReq.getCity());
            }

            if (campaignUpdateReq.getProjectType() != null) {
                campaign.setProjectType(campaignUpdateReq.getProjectType());
            }

            if (campaignUpdateReq.getGoalAmount() != null) {
                campaign.setGoalAmount(campaignUpdateReq.getGoalAmount());
            }

            if (campaignUpdateReq.getCampaignDuration() != null) {
                campaign.setCampaignDuration(campaignUpdateReq.getCampaignDuration());
            }

            if (campaignUpdateReq.getRisks() != null) {
                campaign.setRisks(campaignUpdateReq.getRisks());
            }

            if (campaignUpdateReq.getDescription() != null) {
                campaign.setDescription(campaignUpdateReq.getDescription());
            }

            campaign.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
            Campaign editedCampaign = this.campaignUtils.saveCampaign(campaign);
            if (campaignUpdateReq.getCampaignImage() != null) {
                this.campaignImageService.addImage(campaign, campaignUpdateReq.getCampaignImage());
            }

            if (campaignUpdateReq.getCampaignVideoUrl() != null && campaign.getVideo() == null) {
                this.campaignVideoService.addVideo(campaign, campaignUpdateReq.getCampaignVideoUrl());
            }

            return this.campaignMapper.toDTOById(editedCampaign);
        } catch (Exception var5) {
            System.err.println(var5);
            System.err.println(var5.getMessage());
            throw new RuntimeException();
        }
    }

    public CampaignDTO submitCampaign(Long campaignId) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
        this.campaignUtils.validateCampaignForSubmission(campaign);
        campaign.setCampaignStage(CampaignStage.PENDING);
        campaign.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Campaign savedCampaign = this.campaignUtils.saveCampaign(campaign);
        return this.campaignMapper.toDTO(savedCampaign);
    }

    public CampaignDTO withdrawCampaign(Long campaignId) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
        this.campaignUtils.validateCampaignStage(campaign, CampaignStage.PENDING, "Campaign cannot be withdrawn unless it is in the pending stage");
        campaign.setCampaignStage(CampaignStage.INITIAL);
        campaign.setEditedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Campaign savedCampaign = this.campaignUtils.saveCampaign(campaign);
        return this.campaignMapper.toDTO(savedCampaign);
    }

    public CampaignDTO pauseCampaign(Long campaignId) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
        this.campaignUtils.validateCampaignStage(campaign, CampaignStage.FUNDING, "Campaign cannot be paused unless it is in the funding stage");
        campaign.setCampaignStage(CampaignStage.PAUSED);
        campaign.setPausedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Campaign savedCampaign = this.campaignUtils.saveCampaign(campaign);
        return this.campaignMapper.toDTO(savedCampaign);
    }

    public CampaignDTO resumeCampaign(Long campaignId) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
        this.campaignUtils.validateCampaignStage(campaign, CampaignStage.PAUSED, "Campaign cannot be resumed unless it is in the paused stage");
        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setResumedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        Campaign savedCampaign = this.campaignUtils.saveCampaign(campaign);
        return this.campaignMapper.toDTO(savedCampaign);
    }

    public ResponseEntity<ApiResponse> likeCampaign(CampaignLikeReq campaignLikeReq) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignLikeReq.getCampaignId());
        Users user = this.userUtils.utilGetUserByUserId(campaignLikeReq.getUserId());
        CampaignLike campaignLike = this.campaignLikeRepository.findByCampaignIdAndUserUserId(campaignLikeReq.getCampaignId(), campaignLikeReq.getUserId());
        this.updateCampaignLikes(campaign, user, campaignLike);
        return campaignLike != null ? ApiResponse.success("Disliked Successfully") : ApiResponse.success("Liked Successfully");
    }

    private void updateCampaignLikes(Campaign campaign, Users user, CampaignLike campaignLike) {
        if (campaignLike != null) {
            this.campaignLikeRepository.delete(campaignLike);
            campaign.setNumberOfLikes(campaign.getNumberOfLikes() - 1);
        } else {
            campaignLike = new CampaignLike();
            campaignLike.setUser(user);
            campaignLike.setCampaign(campaign);
            this.campaignLikeRepository.save(campaignLike);
            campaign.setNumberOfLikes(campaign.getNumberOfLikes() + 1);
        }

        this.campaignUtils.saveCampaign(campaign);
    }

    public ResponseEntity<ApiResponse> deleteCampaign(Long campaignId) {
        Campaign campaign = this.campaignUtils.utilGetCampaignById(campaignId);
        campaign.setCampaignStage(CampaignStage.DELETED);
        this.campaignUtils.saveCampaign(campaign);
        return ApiResponse.success("Campaign successfully deleted!");
    }
}