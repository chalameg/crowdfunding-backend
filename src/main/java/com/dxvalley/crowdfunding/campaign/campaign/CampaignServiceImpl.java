package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddDto;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTOMapper;
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
import com.dxvalley.crowdfunding.exception.BadRequestException;
import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import com.dxvalley.crowdfunding.exception.ForbiddenException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.fileUpload.FileUploadService;
import com.dxvalley.crowdfunding.payment.PaymentService;
import com.dxvalley.crowdfunding.payment.paymentDTO.PaymentResponse;
import com.dxvalley.crowdfunding.user.UserService;
import com.dxvalley.crowdfunding.user.Users;
import com.dxvalley.crowdfunding.user.dto.UserDTO;
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
    private final CampaignDTOMapper campaignDTOMapper;
    private final CampaignLikeRepository campaignLikeRepository;
    private final FileUploadService fileUploadService;
    private final DateTimeFormatter dateTimeFormatter;
    private final CampaignUtils campaignUtils;


    /**
     * Adds a new campaign.
     *
     * @param campaignAddRequestDto The DTO containing campaign details.
     * @return The registered campaign.
     * @throws ForbiddenException      If the user is not enabled to add campaigns.
     * @throws DatabaseAccessException If an error occurs while accessing the database.
     */
    @Override
    public Campaign addCampaign(CampaignAddDto campaignAddRequestDto) {
        try {
            // Get the user by username
            Users user = userService.utilGetUserByUsername(campaignAddRequestDto.getOwner());

            if (!user.getIsEnabled()) {
                throw new ForbiddenException("User is not enabled to add campaigns");
            }

            FundingType fundingType = fundingTypeService.getFundingTypeById(campaignAddRequestDto.getFundingTypeId());
            CampaignSubCategory campaignSubCategory = campaignSubCategoryService.getCampaignSubCategoryById(campaignAddRequestDto.getCampaignSubCategoryId());

            // Create a new campaign
            Campaign campaign = new Campaign();
            campaign.setTitle(campaignAddRequestDto.getTitle());
            campaign.setCity(campaignAddRequestDto.getCity());
            campaign.setOwner(user.getUsername());
            campaign.setCampaignSubCategory(campaignSubCategory);
            campaign.setFundingType(fundingType);
            campaign.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            campaign.setIsEnabled(false);
            campaign.setCampaignStage(CampaignStage.INITIAL);
            campaign.setGoalAmount(0.0);
            campaign.setTotalAmountCollected(0.0);
            campaign.setNumberOfBackers(0);
            campaign.setNumberOfLikes(0);
            campaign.setCampaignDuration((short) 0);

            // Save the campaign to the database
            Campaign registeredCampaign = campaignRepository.save(campaign);

            return registeredCampaign;
        } catch (DataAccessException ex) {
            logError("addCampaign", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Edits a campaign with the specified campaign ID.
     *
     * @param campaignId  The ID of the campaign to be edited.
     * @param campaignDTO The DTO containing the updated campaign details.
     * @return The edited campaign DTO.
     * @throws RuntimeException         If an error occurs while editing the campaign.
     * @throws IllegalArgumentException If the campaign ID is invalid.
     */
    @Override
    public CampaignDTO editCampaign(Long campaignId, CampaignDTO campaignDTO) {
        try {
            // Retrieve the campaign by ID
            Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

            // If the campaign is not in the initial state, throw a ForbiddenException
            if (!campaign.getCampaignStage().equals(CampaignStage.INITIAL))
                throw new ForbiddenException("Campaign cannot be updated unless it is in the initial stage");

            // Update campaign properties if the new values are not null
            campaign.setTitle(Optional.ofNullable(campaignDTO.getTitle()).orElse(campaign.getTitle()));
            campaign.setShortDescription(Optional.ofNullable(campaignDTO.getShortDescription()).orElse(campaign.getShortDescription()));
            campaign.setCity(Optional.ofNullable(campaignDTO.getCity()).orElse(campaign.getCity()));
            campaign.setProjectType(Optional.ofNullable(campaignDTO.getProjectType()).orElse(campaign.getProjectType()));
            campaign.setGoalAmount(Optional.ofNullable(campaignDTO.getGoalAmount()).orElse(campaign.getGoalAmount()));
            campaign.setCampaignDuration(Optional.ofNullable(campaignDTO.getCampaignDuration()).orElse(campaign.getCampaignDuration()));
            campaign.setRisks(Optional.ofNullable(campaignDTO.getRisks()).orElse(campaign.getRisks()));
            campaign.setDescription(Optional.ofNullable(campaignDTO.getDescription()).orElse(campaign.getDescription()));
            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

            // Save the updated campaign to the database
            Campaign editedCampaign = campaignRepository.save(campaign);

            return campaignDTOMapper.applyById(editedCampaign);
        } catch (DataAccessException ex) {
            logError("editCampaign", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Uploads media files (campaign image and video) for a campaign with the specified campaign ID.
     *
     * @param campaignId    The ID of the campaign to upload media files for.
     * @param campaignImage The multipart file containing the campaign image.
     * @param campaignVideo The URL of the campaign video.
     * @return The campaign DTO with updated media information.
     * @throws DatabaseAccessException If an error occurs while accessing the database.
     */
    @Override
    public CampaignDTO uploadMedias(Long campaignId, MultipartFile campaignImage, String campaignVideo) {
        try {
            Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

            // Upload campaign image if provided
            if (campaignImage != null) {
                String imageUrl = fileUploadService.uploadFile(campaignImage);
                campaign.setImageUrl(Optional.ofNullable(imageUrl).orElse(campaign.getImageUrl()));
            }

            campaign.setVideoLink(Optional.ofNullable(campaignVideo).orElse(campaign.getVideoLink()));
            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

            Campaign savedCampaign = campaignRepository.save(campaign);

            return campaignDTOMapper.apply(savedCampaign);
        } catch (DataAccessException ex) {
            logError("uploadMedias", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Uploads multiple files for a campaign with the specified campaign ID.
     *
     * @param campaignId The ID of the campaign to upload files for.
     * @param files      The list of multipart files to be uploaded.
     * @return The campaign DTO with updated file information.
     * @throws DatabaseAccessException If an error occurs while accessing the database.
     */
    @Override
    public CampaignDTO uploadFiles(Long campaignId, List<MultipartFile> files) {
        try {
            Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

            // Upload files if provided
            if (files != null && !files.isEmpty()) {
                List<String> fileUrls = fileUploadService.uploadFiles(files);
                campaign.setFiles(fileUrls);
            }

            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

            Campaign savedCampaign = campaignRepository.save(campaign);

            return campaignDTOMapper.apply(savedCampaign);
        } catch (DataAccessException ex) {
            logError("uploadFiles", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Submits a campaign for approval with the specified campaign ID.
     *
     * @param campaignId The ID of the campaign to be submitted.
     * @return The submitted campaign DTO.
     * @throws ForbiddenException      If the campaign is not in the initial or pending stage and cannot be submitted.
     * @throws BadRequestException     If the campaign data is not valid and cannot be submitted.
     * @throws DatabaseAccessException If an error occurs while accessing the database.
     */
    @Override
    public CampaignDTO submitCampaign(Long campaignId) {
        try {
            // Retrieve the campaign by ID
            Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

            // Check if the campaign is in the initial or pending stage
            if (!campaign.getCampaignStage().equals(CampaignStage.INITIAL) && !campaign.getCampaignStage().equals(CampaignStage.PENDING))
                throw new ForbiddenException("Campaign cannot be submitted for approval unless it is in the initial or pending stage");

            // Check if the campaign data is valid
            if (!campaignUtils.isValidCampaign(campaign))
                throw new BadRequestException("Unable to submit the campaign for approval with the provided data. Please provide all required information and try again.");

            // Update campaign stage and edited timestamp
            campaign.setCampaignStage(CampaignStage.PENDING);
            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

            Campaign savedCampaign = campaignRepository.save(campaign);

            return campaignDTOMapper.apply(savedCampaign);
        } catch (DataAccessException ex) {
            logError("submitCampaign", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    /**
     * Withdraws a campaign with the specified campaign ID.
     *
     * @param campaignId The ID of the campaign to be withdrawn.
     * @return The withdrawn campaign DTO.
     * @throws ForbiddenException      If the campaign is not in the pending stage and cannot be withdrawn.
     * @throws DatabaseAccessException If an error occurs while accessing the database.
     */
    @Override
    public CampaignDTO withdrawCampaign(Long campaignId) {
        try {
            Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

            // Check if the campaign is in the pending stage
            if (!campaign.getCampaignStage().equals(CampaignStage.PENDING))
                throw new ForbiddenException("Campaign cannot be withdrawn unless it is in the pending stage");

            // Update campaign stage and edited timestamp
            campaign.setCampaignStage(CampaignStage.INITIAL);
            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));

            Campaign savedCampaign = campaignRepository.save(campaign);

            return campaignDTOMapper.apply(savedCampaign);
        } catch (DataAccessException ex) {
            logError("withdrawCampaign", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    @Override
    public CampaignDTO pauseCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        if (!campaign.getCampaignStage().equals(CampaignStage.FUNDING))
            throw new ForbiddenException("Campaign cannot be paused unless it is in the funding stage");

        return null;
    }

    @Override
    public CampaignDTO resumeCampaign(Long campaignId) {
        Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);

        if (!campaign.getCampaignStage().equals(CampaignStage.PAUSED))
            throw new ForbiddenException("Campaign cannot be resumed unless it is in the paused stage");
        return null;
    }


    /**
     * Likes or dislikes a campaign based on the provided user and campaign IDs.
     *
     * @param campaignLikeDTO The DTO containing the user and campaign IDs.
     * @return A message indicating the success of the operation.
     * @throws DatabaseAccessException If an error occurs while accessing the database.
     */
    @Override
    public String likeCampaign(CampaignLikeDTO campaignLikeDTO) throws DatabaseAccessException {
        try {
            Campaign campaign = campaignUtils.utilGetCampaignById(campaignLikeDTO.getCampaignId());
            var user = userService.utilGetUserByUserId(campaignLikeDTO.getUserId());
            var campaignLike = campaignLikeRepository.findByCampaignCampaignIdAndUserUserId(campaignLikeDTO.getCampaignId(), campaignLikeDTO.getUserId());

            if (campaignLike != null) {
                campaignLikeRepository.delete(campaignLike);
                campaign.setNumberOfLikes(campaign.getNumberOfLikes() - 1);
                campaignRepository.save(campaign);
                return "Disliked Successfully";
            }

            campaignLike = new CampaignLike();
            campaignLike.setUser(user);
            campaignLike.setCampaign(campaign);
            campaignLikeRepository.save(campaignLike);
            campaign.setNumberOfLikes(campaign.getNumberOfLikes() + 1);
            campaignRepository.save(campaign);

            return "Liked Successfully";
        } catch (DataAccessException ex) {
            logError("likeCampaign", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
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
            List<Collaborator> collaborators = collaboratorRepository.findAllCollaboratorByCampaignId(campaignId);
            List<Reward> rewards = rewardRepository.findRewardsByCampaignId(campaignId);
            List<Promotion> promotions = promotionRepository.findPromotionByCampaignId(campaignId);
            UserDTO user = userService.getUserByUsername(campaign.getOwner());
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
                    findCampaignsByCampaignSubCategoryCampaignCategoryCampaignCategoryIdAndCampaignStageIn(
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
            List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignSubCategoryCampaignSubCategoryIdAndCampaignStageIn(subCategoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));

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
     * @param owner The username of the owner.
     * @return The list of campaign DTOs for the specified owner.
     * @throws ResourceNotFoundException If there are no campaigns available for the specified owner.
     * @throws DatabaseAccessException   If an error occurs while accessing the database.
     */
    @Override
    public List<CampaignDTO> getCampaignsByOwner(String owner) {
        try {
            List<Campaign> campaigns = campaignRepository.findCampaignsByOwner(owner);

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
                    findCampaignsByFundingTypeFundingTypeIdAndCampaignStageIn(
                            fundingTypeId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));

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
            Campaign campaign = campaignUtils.utilGetCampaignById(campaignId);
//            Optional<CampaignBankAccount> campaignBankAccount = Optional.ofNullable(campaign.getCampaignBankAccount());
//            Optional<List<Reward>> rewards = Optional.ofNullable(campaign.getRewards());
//            Optional<List<Collaborator>> collaborators = Optional.ofNullable(campaign.getCollaborators());
//            Optional<List<Promotion>> promotions = Optional.ofNullable(campaign.getPromotions());
//            Optional<List<Payment>> contributors = Optional.ofNullable(campaign.getContributors());
//            Optional<List<CampaignLike>> likes = Optional.ofNullable(campaignLikeRepository.findByCampaignCampaignId(campaignId));
//
//            campaignBankAccount.ifPresent(campaignBankAccountRepository::delete);
//            rewards.ifPresent(rewardRepository::deleteAll);
//            collaborators.ifPresent(collaboratorRepository::deleteAll);
//            promotions.ifPresent(promotionRepository::deleteAll);
//            contributors.ifPresent(paymentRepository::deleteAll);
//            likes.ifPresent(campaignLikeRepository::deleteAll);

            campaign.setCampaignStage(CampaignStage.DELETED);

            campaignRepository.save(campaign);
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