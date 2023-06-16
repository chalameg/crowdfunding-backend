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
import org.springframework.web.multipart.MultipartFile;

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


    // Adds a new campaign.
    @Override
    public Campaign addCampaign(CampaignAddReq campaignAddReq) {
        Users user = userUtils.utilGetUserByUsername(campaignAddReq.getOwner());
        userUtils.verifyUser(user);

        FundingType fundingType = fundingTypeService.getFundingTypeById(campaignAddReq.getFundingTypeId());
        CampaignSubCategory campaignSubCategory = campaignSubCategoryService.getCampaignSubCategoryById(campaignAddReq.getCampaignSubCategoryId());

        Campaign campaign = createCampaign(campaignAddReq, user, campaignSubCategory, fundingType);

        Campaign registeredCampaign = campaignUtils.saveCampaign(campaign);

        return registeredCampaign;
    }

    private Campaign createCampaign(CampaignAddReq campaignAddReq, Users user,
                                    CampaignSubCategory campaignSubCategory, FundingType fundingType) {

        return Campaign.builder()
                .title(campaignAddReq.getTitle())
                .city(campaignAddReq.getCity())
                .user(user)
                .campaignSubCategory(campaignSubCategory)
                .fundingType(fundingType)
                .createdAt(LocalDateTime.now().format(dateTimeFormatter))
                .campaignStage(CampaignStage.INITIAL)
                .build();
    }


    // Edits a campaign with the specified campaign ID.
    @Override
    public CampaignDTO editCampaign(Long campaignId, CampaignUpdateReq campaignUpdateReq) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignStage(campaign, CampaignStage.INITIAL, "Campaign cannot be updated unless it is in the initial stage");

        if (campaignUpdateReq.getTitle() != null) campaign.setTitle(campaignUpdateReq.getTitle());
        if (campaignUpdateReq.getShortDescription() != null)
            campaign.setShortDescription(campaignUpdateReq.getShortDescription());
        if (campaignUpdateReq.getCity() != null) campaign.setCity(campaignUpdateReq.getCity());
        if (campaignUpdateReq.getProjectType() != null) campaign.setProjectType(campaignUpdateReq.getProjectType());
        if (campaignUpdateReq.getGoalAmount() != null) campaign.setGoalAmount(campaignUpdateReq.getGoalAmount());
        if (campaignUpdateReq.getCampaignDuration() != null)
            campaign.setCampaignDuration(campaignUpdateReq.getCampaignDuration());
        if (campaignUpdateReq.getRisks() != null) campaign.setRisks(campaignUpdateReq.getRisks());
        if (campaignUpdateReq.getDescription() != null) campaign.setDescription(campaignUpdateReq.getDescription());
        campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign editedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignMapper.toDTOById(editedCampaign);
    }

    // Uploads media files (campaign image and video) for a campaign with the specified campaign ID.
    @Override
    public CampaignDTO uploadCampaignMedias(Long campaignId, MultipartFile campaignImage, String videoUrl) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        if (campaignImage != null)
            campaign.addImage(campaignImageService.saveCampaignImage(campaignImage));

        if (videoUrl != null)
            campaign.addVideo(campaignVideoService.saveCampaignVideo(videoUrl));

        campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignMapper.toDTO(savedCampaign);
    }

    //Submits a campaign for approval with the specified campaign ID.
    @Override
    public CampaignDTO submitCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignForSubmission(campaign);

        campaign.setCampaignStage(CampaignStage.PENDING);
        campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignMapper.toDTO(savedCampaign);
    }


    //  Withdraws a campaign with the specified campaign ID.
    @Override
    public CampaignDTO withdrawCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignStage(campaign, CampaignStage.PENDING, "Campaign cannot be withdrawn unless it is in the pending stage");

        campaign.setCampaignStage(CampaignStage.INITIAL);
        campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignMapper.toDTO(savedCampaign);
    }

    @Override
    public CampaignDTO pauseCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignStage(campaign, CampaignStage.FUNDING, "Campaign cannot be paused unless it is in the funding stage");

        campaign.setCampaignStage(CampaignStage.PAUSED);
        campaign.setPausedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignMapper.toDTO(savedCampaign);
    }

    @Override
    public CampaignDTO resumeCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignStage(campaign, CampaignStage.PAUSED, "Campaign cannot be resumed unless it is in the paused stage");

        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setResumedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignMapper.toDTO(savedCampaign);
    }

    // Likes or dislikes a campaign based on the provided user and campaign IDs.
    @Override
    public ResponseEntity<ApiResponse> likeCampaign(CampaignLikeReq campaignLikeReq) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignLikeReq.getCampaignId());
        Users user = userUtils.utilGetUserByUserId(campaignLikeReq.getUserId());
        CampaignLike campaignLike = campaignLikeRepository.findByCampaignIdAndUserUserId(campaignLikeReq.getCampaignId(), campaignLikeReq.getUserId());

        updateCampaignLikes(campaign, user, campaignLike);

        return campaignLike != null ? ApiResponse.success("Disliked Successfully") : ApiResponse.success("Liked Successfully");
    }


    private void updateCampaignLikes(Campaign campaign, Users user, CampaignLike campaignLike) {
        if (campaignLike != null) {
            campaignLikeRepository.delete(campaignLike);
            campaign.setNumberOfLikes(campaign.getNumberOfLikes() - 1);
        } else {
            campaignLike = new CampaignLike();
            campaignLike.setUser(user);
            campaignLike.setCampaign(campaign);
            campaignLikeRepository.save(campaignLike);
            campaign.setNumberOfLikes(campaign.getNumberOfLikes() + 1);
        }
        campaignUtils.saveCampaign(campaign);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

//        Optional<List<Reward>> rewards = Optional.ofNullable(campaign.getRewards());
//        Optional<List<Collaborator>> collaborators = Optional.ofNullable(campaign.getCollaborators());
//        Optional<List<Promotion>> promotions = Optional.ofNullable(campaign.getPromotions());
//        List<Payment> payments = paymentRepository.findByCampaignId(campaign.getCampaignId());
//
//        Optional<List<CampaignLike>> likes = Optional.ofNullable(campaignLikeRepository.findByCampaignId(campaignId));
//
//        rewards.ifPresent(rewardRepository::deleteAll);
//        collaborators.ifPresent(collaboratorRepository::deleteAll);
//        promotions.ifPresent(promotionRepository::deleteAll);
////            payments.ifPresent(paymentRepository.);
//
//        if (!payments.isEmpty()) {
//            for (var payment : payments) {
//                payment.setCampaign(null);
//            }
//        }
//        likes.ifPresent(campaignLikeRepository::deleteAll);
//
//        Optional<CampaignApproval> campaignApproval = campaignApprovalRepository.findCampaignApprovalByCampaignId(campaign.getCampaignId());
//        if (campaignApproval.isPresent())
//            campaignApprovalRepository.delete(campaignApproval.get());

        campaign.setCampaignStage(CampaignStage.DELETED);
        campaignUtils.saveCampaign(campaign);

        return ApiResponse.success("Campaign successfully deleted!");
    }

}