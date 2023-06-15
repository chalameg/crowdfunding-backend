package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.image.CampaignImageService;
import com.dxvalley.crowdfunding.campaign.campaign.campaignMedia.video.CampaignVideoService;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddReq;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTOMapper;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignUpdateReq;
import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApproval;
import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApprovalRepository;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccountRepository;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.dto.BankAccountDTO;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.Collaborator;
import com.dxvalley.crowdfunding.campaign.campaignCollaborator.CollaboratorRepository;
import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingType;
import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingTypeService;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLike;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeDTO;
import com.dxvalley.crowdfunding.campaign.campaignLike.CampaignLikeRepository;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.Promotion;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.PromotionRepository;
import com.dxvalley.crowdfunding.campaign.campaignReward.Reward;
import com.dxvalley.crowdfunding.campaign.campaignReward.RewardRepository;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategory;
import com.dxvalley.crowdfunding.campaign.campaignSubCategory.CampaignSubCategoryService;
import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.Payment;
import com.dxvalley.crowdfunding.payment.PaymentRepository;
import com.dxvalley.crowdfunding.payment.PaymentService;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentResponse;
import com.dxvalley.crowdfunding.userManager.user.UserService;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.userManager.userDTO.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final RewardRepository rewardRepository;
    private final PromotionRepository promotionRepository;
    private final FundingTypeService fundingTypeService;
    private final PaymentService paymentService;
    private final CampaignSubCategoryService campaignSubCategoryService;
    private final UserService userService;
    private final UserUtils userUtils;
    private final CampaignDTOMapper campaignDTOMapper;
    private final CampaignLikeRepository campaignLikeRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final CampaignBankAccountRepository campaignBankAccountRepository;
    private final PaymentRepository paymentRepository;
    private final CampaignUtils campaignUtils;
    private final CampaignVideoService campaignVideoService;
    private final CampaignImageService campaignImageService;
    private final CampaignApprovalRepository campaignApprovalRepository;


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

        return campaignDTOMapper.applyById(editedCampaign);
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

        return campaignDTOMapper.apply(savedCampaign);
    }

    //Submits a campaign for approval with the specified campaign ID.
    @Override
    public CampaignDTO submitCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignForSubmission(campaign);

        campaign.setCampaignStage(CampaignStage.PENDING);
        campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignDTOMapper.apply(savedCampaign);
    }


    //  Withdraws a campaign with the specified campaign ID.
    @Override
    public CampaignDTO withdrawCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignStage(campaign, CampaignStage.PENDING, "Campaign cannot be withdrawn unless it is in the pending stage");

        campaign.setCampaignStage(CampaignStage.INITIAL);
        campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignDTOMapper.apply(savedCampaign);
    }

    @Override
    public CampaignDTO pauseCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignStage(campaign, CampaignStage.FUNDING, "Campaign cannot be paused unless it is in the funding stage");

        campaign.setCampaignStage(CampaignStage.PAUSED);
        campaign.setPausedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignDTOMapper.apply(savedCampaign);
    }

    @Override
    public CampaignDTO resumeCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        campaignUtils.validateCampaignStage(campaign, CampaignStage.PAUSED, "Campaign cannot be resumed unless it is in the paused stage");

        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setResumedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignDTOMapper.apply(savedCampaign);
    }


    // Uploads multiple files for a campaign with the specified campaign ID.
    @Override
    public CampaignDTO uploadFiles(Long campaignId, List<MultipartFile> files) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        // Upload files if provided
//            if (files != null && !files.isEmpty()) {
//                List<String> fileUrls = fileUploadService.uploadFiles(files);
//                campaign.setFiles(fileUrls);
//            }

        campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

        Campaign savedCampaign = campaignUtils.saveCampaign(campaign);

        return campaignDTOMapper.apply(savedCampaign);

    }


    // Likes or dislikes a campaign based on the provided user and campaign IDs.
    @Override
    public String likeCampaign(CampaignLikeDTO campaignLikeDTO) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignLikeDTO.getCampaignId());
        Users user = userUtils.utilGetUserByUserId(campaignLikeDTO.getUserId());
        CampaignLike campaignLike = campaignLikeRepository.findByCampaignIdAndUserUserId(campaignLikeDTO.getCampaignId(), campaignLikeDTO.getUserId());

        updateCampaignLikes(campaign, user, campaignLike);

        return campaignLike != null ? "Disliked Successfully" : "Liked Successfully";
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


    /**
     * Retrieves the list of campaigns in the funding or completed stage.
     *
     * @return The list of campaign DTOs.
     * @throws ResourceNotFoundException If there are no campaigns available.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public List<CampaignDTO> getCampaigns() {
        try {
            List<Campaign> campaigns = campaignRepository.
                    findCampaignsByCampaignStageIn(List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));

            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("Currently, there are no campaigns available.");

            return campaigns.stream()
                    .map(campaignDTOMapper)
                    .toList();
        } catch (DataAccessException ex) {
            logError("getCampaigns", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Retrieves a campaign by its ID along with additional details such as bank account, collaborators, rewards, promotions, contributors, and owner information.
     *
     * @param campaignId The ID of the campaign to retrieve.
     * @return The campaign DTO with additional details.
     * @throws ResourceNotFoundException If the campaign is not found.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public CampaignDTO getCampaignById(Long campaignId) {
        try {
            Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

            // Create the campaign DTO
            CampaignDTO campaignDTO = campaignDTOMapper.applyById(campaign);

            // Retrieve campaign bank account
            BankAccountDTO campaignBankAccount = campaignUtils.getCampaignBankAccountByCampaignId(campaignId);
            campaignDTO.setCampaignBankAccount(campaignBankAccount);

            // Retrieve collaborators, rewards, promotions, contributors, and owner information
            List<Collaborator> collaborators = collaboratorRepository.findCollaboratorsByCampaignIdAndAccepted(campaignId, true);
            List<Reward> rewards = rewardRepository.findByCampaignId(campaignId);
            List<Promotion> promotions = promotionRepository.findPromotionByCampaignId(campaignId);
            UserResponse user = userService.getUserByUsername(campaign.getUser().getUsername());
            List<PaymentResponse> contributors = paymentService.getPaymentByCampaignId(campaignId);

            // Set additional details in the campaign DTO
            campaignDTO.setCollaborators(collaborators);
            campaignDTO.setRewards(rewards);
            campaignDTO.setPromotions(promotions);
            campaignDTO.setContributors(contributors);
            campaignDTO.setOwnerFullName(user.getFullName());
            campaignDTO.setNumberOfBackers(contributors.size());

            return campaignDTO;
        } catch (DataAccessException ex) {
            logError("getCampaignById", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Retrieves campaigns by category ID in the funding or completed stage.
     *
     * @param categoryId The ID of the category.
     * @return The list of campaign DTOs for the specified category.
     * @throws ResourceNotFoundException If there are no campaigns available for the specified category.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public List<CampaignDTO> getCampaignByCategory(Long categoryId) {
        try {
            List<Campaign> campaigns = campaignRepository.
                    findCampaignsByCampaignSubCategoryCampaignCategoryIdAndCampaignStageIn(
                            categoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));

            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("There are no campaigns available for this category.");

            return campaigns.stream()
                    .map(campaignDTOMapper)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logError("getCampaignByCategory", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Retrieves campaigns by sub-category ID in the funding or completed stage.
     *
     * @param subCategoryId The ID of the sub-category.
     * @return The list of campaign DTOs for the specified sub-category.
     * @throws ResourceNotFoundException If there are no campaigns available for the specified sub-category.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId) {
        try {
            List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignSubCategoryIdAndCampaignStageIn(subCategoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));

            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("There are no campaigns available for this sub-category.");

            return campaigns.stream()
                    .map(campaignDTOMapper)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logError("getCampaignBySubCategory", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Retrieves campaigns by owner username.
     *
     * @param username The username of the owner.
     * @return The list of campaign DTOs for the specified owner.
     * @throws ResourceNotFoundException If there are no campaigns available for the specified owner.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public List<CampaignDTO> getCampaignsByOwner(String username) {
        try {
            List<Campaign> campaigns = campaignRepository.findCampaignsByUserUsername(username);

            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("There are no campaigns available for this user.");

            return campaigns.stream()
                    .map(campaignDTOMapper)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logError("getCampaignsByOwner", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Searches campaigns based on the provided search parameter.
     *
     * @param searchParam The search parameter.
     * @return The list of campaign DTOs matching the search parameter.
     * @throws ResourceNotFoundException If no campaigns are found with the specified search parameter.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public List<CampaignDTO> searchCampaigns(String searchParam) {
        try {
            // Clean up the search parameter string by trimming whitespace and splitting by non-word characters
            String[] searchParamArray = searchParam.trim().split("\\W+");
            // Join the cleaned-up search parameters with "|" to create a regex search pattern
            String searchPattern = String.join("|", searchParamArray);

            List<Campaign> campaigns = campaignRepository.searchForCampaigns(searchPattern);

            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("No campaigns found with this search parameter.");

            return campaigns.stream()
                    .map(campaignDTOMapper)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logError("searchCampaigns", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Retrieves campaigns by funding type ID.
     *
     * @param fundingTypeId The ID of the funding type.
     * @return The list of campaign DTOs for the specified funding type.
     * @throws ResourceNotFoundException If there are no campaigns available for the specified funding type.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId) {
        try {
            List<Campaign> campaigns = campaignRepository.
                    findCampaignsByFundingTypeIdAndCampaignStage(fundingTypeId, CampaignStage.FUNDING);

            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("There are no campaigns available for this funding type.");

            return campaigns.stream()
                    .map(campaignDTOMapper)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logError("getCampaignsByFundingType", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    /**
     * Retrieves campaigns by campaign stage.
     *
     * @param campaignStage The campaign stage.
     * @return The list of campaign DTOs for the specified campaign stage.
     * @throws ResourceNotFoundException If there are no campaigns available at the specified stage.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public List<CampaignDTO> getCampaignsByStage(String campaignStage) {
        try {
            CampaignStage result = CampaignStage.lookup(campaignStage);
            List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignStage(result);

            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("There are no campaigns available at the " + campaignStage + " stage.");

            return campaigns.stream()
                    .map(campaignDTOMapper)
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logError("getCampaignsByStage", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    @Override
    public void deleteCampaign(Long campaignId) {
        try {
            CampaignDTO campaign = getCampaignById(campaignId);
            Optional<BankAccountDTO> campaignBankAccount = Optional.ofNullable(campaign.getCampaignBankAccount());
            Optional<List<Reward>> rewards = Optional.ofNullable(campaign.getRewards());
            Optional<List<Collaborator>> collaborators = Optional.ofNullable(campaign.getCollaborators());
            Optional<List<Promotion>> promotions = Optional.ofNullable(campaign.getPromotions());
            List<Payment> payments = paymentRepository.findByCampaignId(campaign.getCampaignId());

            Optional<List<CampaignLike>> likes = Optional.ofNullable(campaignLikeRepository.findByCampaignId(campaignId));

            if (campaignBankAccount.isPresent())
                campaignBankAccountRepository.deleteById(campaignBankAccount.get().getCampaignBankAccountId());
            rewards.ifPresent(rewardRepository::deleteAll);
            collaborators.ifPresent(collaboratorRepository::deleteAll);
            promotions.ifPresent(promotionRepository::deleteAll);
//            payments.ifPresent(paymentRepository.);

            if (!payments.isEmpty()) {
                for (var payment : payments) {
                    payment.setCampaign(null);
                }
            }
            likes.ifPresent(campaignLikeRepository::deleteAll);

            Optional<CampaignApproval> campaignApproval = campaignApprovalRepository.findCampaignApprovalByCampaignId(campaign.getCampaignId());
            if (campaignApproval.isPresent())
                campaignApprovalRepository.delete(campaignApproval.get());

            campaign.setCampaignStage(CampaignStage.DELETED);

            campaignRepository.deleteById(campaign.getCampaignId());
        } catch (DataAccessException ex) {
            logError("deleteCampaign", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    private void logError(String methodName, DataAccessException ex) {
        log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                getClass().getSimpleName(),
                methodName,
                ex.getMessage());
    }
}