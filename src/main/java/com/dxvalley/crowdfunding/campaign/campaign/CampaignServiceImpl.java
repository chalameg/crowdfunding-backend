package com.dxvalley.crowdfunding.campaign.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignAddDto;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTOMapper;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccount;
import com.dxvalley.crowdfunding.campaign.campaignBankAccount.CampaignBankAccountRepository;
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
import com.dxvalley.crowdfunding.exception.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.exception.UserNotEnabledException;
import com.dxvalley.crowdfunding.fileUpload.FileUploadService;
import com.dxvalley.crowdfunding.payment.Payment;
import com.dxvalley.crowdfunding.payment.PaymentRepository;
import com.dxvalley.crowdfunding.payment.PaymentService;
import com.dxvalley.crowdfunding.user.UserService;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final CampaignBankAccountRepository campaignBankAccountRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final RewardRepository rewardRepository;
    private final PromotionRepository promotionRepository;
    private final FundingTypeService fundingTypeService;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final CampaignSubCategoryService campaignSubCategoryService;
    private final UserService userService;
    private final CampaignDTOMapper campaignDTOMapper;
    private final CampaignLikeRepository campaignLikeRepository;
    private final FileUploadService fileUploadService;
    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public Campaign addCampaign(CampaignAddDto campaignAddRequestDto) {
        try {
            FundingType fundingType = fundingTypeService.getFundingTypeById(campaignAddRequestDto.getFundingTypeId());
            CampaignSubCategory campaignSubCategory = campaignSubCategoryService.getCampaignSubCategoryById(campaignAddRequestDto.getCampaignSubCategoryId());
            var user = userService.utilGetUserByUsername(campaignAddRequestDto.getOwner());
            if (!user.getIsEnabled()) throw new UserNotEnabledException("User is not enabled to add campaigns");

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
            Campaign registeredCampaign = campaignRepository.save(campaign);

            return registeredCampaign;
        } catch (DataAccessException ex) {
            log.error("Error Adding campaigns: {}", ex.getMessage());
            throw new RuntimeException("Error Adding campaigns", ex);
        }
    }

    @Override
    public CampaignDTO editCampaign(Long campaignId, CampaignDTO campaignDTO) {
        try {
            Campaign campaign = utilGetCampaignById(campaignId);

            campaign.setTitle(Optional.ofNullable(campaignDTO.getTitle()).orElse(campaign.getTitle()));
            campaign.setShortDescription(Optional.ofNullable(campaignDTO.getShortDescription()).orElse(campaign.getShortDescription()));
            campaign.setCity(Optional.ofNullable(campaignDTO.getCity()).orElse(campaign.getCity()));
            campaign.setProjectType(Optional.ofNullable(campaignDTO.getProjectType()).orElse(campaign.getProjectType()));
            campaign.setGoalAmount(Optional.ofNullable(campaignDTO.getGoalAmount()).orElse(campaign.getGoalAmount()));
            campaign.setCampaignDuration(Optional.ofNullable(campaignDTO.getCampaignDuration()).orElse(campaign.getCampaignDuration()));
            campaign.setRisks(Optional.ofNullable(campaignDTO.getRisks()).orElse(campaign.getRisks()));
            campaign.setDescription(Optional.ofNullable(campaignDTO.getDescription()).orElse(campaign.getDescription()));
            campaign.setCommissionRate(Optional.ofNullable(campaignDTO.getCommissionRate()).orElse(campaign.getCommissionRate()));
            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            Campaign result = campaignRepository.save(campaign);

            return campaignDTOMapper.applyById(result);
        } catch (DataAccessException ex) {
            log.error("Error editing campaign with ID: {}", campaignId, ex);
            throw new RuntimeException("Error editing campaign with ID", ex);
        }
    }

    @Override
    public CampaignDTO uploadMedias(Long campaignId, MultipartFile campaignImage, String campaignVideo) {
        try {
            Campaign campaign = utilGetCampaignById(campaignId);
            String imageUrl = null;
            if (campaignImage != null)
                imageUrl = fileUploadService.uploadFile(campaignImage);

            campaign.setImageUrl(Optional.ofNullable(imageUrl).orElse(campaign.getImageUrl()));
            campaign.setVideoLink(Optional.ofNullable(campaignVideo).orElse(campaign.getVideoLink()));
            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            Campaign savedCampaign = campaignRepository.save(campaign);

            return campaignDTOMapper.apply(savedCampaign);
        } catch (DataAccessException ex) {
            log.error("Error uploading campaign media for campaign ID: {}", campaignId, ex);
            throw new RuntimeException("Error uploading campaign media", ex);
        }
    }

    @Override
    public CampaignDTO uploadFiles(Long campaignId, List<MultipartFile> files) {
        try {
            Campaign campaign = utilGetCampaignById(campaignId);

            List<String> campaignMediaList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file == null)
                    continue;
                // Save the file to Cloudinary
                String publicId = fileUploadService.uploadFile(file);
                campaignMediaList.add(publicId);
            }

            campaign.setFiles(campaignMediaList);
            campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
            Campaign savedCampaign = campaignRepository.save(campaign);

            return campaignDTOMapper.apply(savedCampaign);
        } catch (DataAccessException ex) {
            log.error("Error uploading campaign media for campaign ID: {}", campaignId, ex);
            throw new RuntimeException("Error uploading campaign media", ex);
        }
    }

    public CompletableFuture<ResponseEntity<?>> createCampaignWithMediaAsync(Long campaignId, List<MultipartFile> files) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Campaign campaign = utilGetCampaignById(campaignId);

                List<String> campaignMediaList = new ArrayList<>();
                for (MultipartFile file : files) {
                    // Save the file to Cloudinary
                    String publicId = fileUploadService.uploadFile(file);
                    campaignMediaList.add(publicId);
                }

                campaign.setFiles(campaignMediaList);
                campaign.setEditedAt(LocalDateTime.now().format(dateTimeFormatter));
                Campaign savedCampaign = campaignRepository.save(campaign);

                return ApiResponse.success(campaignDTOMapper.apply(savedCampaign));
            } catch (DataAccessException ex) {
                log.error("Error uploading campaign files for campaign ID: {}", campaignId, ex);
                throw new RuntimeException("Error uploading campaign files", ex);
            }
        });
    }


    /**
     * Asynchronously updates the campaign status for all campaigns whose expiration date has passed.
     *
     * @return a CompletableFuture representing the completion of the update operation
     */
    @Async
    @Override
    public CompletableFuture<Void> updateCampaignStatus() {
        List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignStage(CampaignStage.FUNDING);

        for (Campaign campaign : campaigns) {
            LocalDateTime expiredAt = LocalDateTime.parse(campaign.getExpiredAt(), dateTimeFormatter);
            if (expiredAt.isBefore(LocalDateTime.now())) {
                campaign.setCampaignStage(CampaignStage.COMPLETED);
            }
        }

        campaignRepository.saveAll(campaigns);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CampaignDTO submitWithdrawCampaign(Long campaignId) {
        try {
            Campaign campaign = utilGetCampaignById(campaignId);
            if (campaign.getCampaignStage().equals(CampaignStage.INITIAL))
                campaign.setCampaignStage(CampaignStage.PENDING);
            else
                campaign.setCampaignStage(CampaignStage.INITIAL);
            Campaign savedCampaign = campaignRepository.save(campaign);

            return campaignDTOMapper.apply(savedCampaign);
        } catch (DataAccessException ex) {
            log.error("Error Canceling campaigns: {}", ex.getMessage());
            throw new RuntimeException("Error Canceling campaigns", ex);
        }
    }

    @Override
    public String likeCampaign(CampaignLikeDTO campaignLikeDTO) {
        try {
            var campaignLike = new CampaignLike();
            var result = campaignLikeRepository.findByCampaignCampaignIdAndUserUserId(campaignLikeDTO.getCampaignId(), campaignLikeDTO.getUserId());
            var campaign = utilGetCampaignById(campaignLikeDTO.getCampaignId());
            if (result != null) {
                campaignLikeRepository.delete(result);
                campaign.setNumberOfLikes(campaign.getNumberOfLikes() - 1);
                campaignRepository.save(campaign);
                return "Disliked Successfully";
            }

            campaignLike.setUser(userService.utilGetUserByUserId(campaignLikeDTO.getUserId()));
            campaignLike.setCampaign(campaign);
            campaignLikeRepository.save(campaignLike);
            campaign.setNumberOfLikes(campaign.getNumberOfLikes() + 1);
            campaignRepository.save(campaign);

            return "Liked Successfully";
        } catch (DataAccessException ex) {
            log.error("Error Liking campaigns: {}", ex.getMessage());
            throw new RuntimeException("Error Liking campaigns", ex);
        }
    }

    /**
     * Pauses or resumes a campaign based on its current stage.
     *
     * @param campaignID The ID of the campaign to pause or resume.
     * @return ResponseEntity containing the updated CampaignDTO if successful, or an error response if unsuccessful.
     */
    @Override
    public ResponseEntity<?> pauseResumeCampaign(Long campaignID) {
        Campaign campaign = utilGetCampaignById(campaignID);

        if (campaign.getCampaignStage() == CampaignStage.PAUSED) {
            campaign.setCampaignStage(CampaignStage.FUNDING);
        } else if (campaign.getCampaignStage() == CampaignStage.FUNDING) {
            campaign.setCampaignStage(CampaignStage.PAUSED);
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Pausing and resuming a campaign is only allowed when the campaign stage is 'FUNDING' or 'PAUSED'.");
        }

        Campaign updatedCampaign = campaignRepository.save(campaign);
        return ApiResponse.success(campaignDTOMapper.apply(updatedCampaign));
    }


    @Override
    public List<CampaignDTO> getCampaigns() {
        try {

            List<Campaign> campaigns = campaignRepository.findCampaignsByCampaignStageIn(List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("Currently, There is no campaign.");

            return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns", ex);
        }
    }

    @Override
    public Campaign enableCampaign(Long campaignId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Campaign campaign = utilGetCampaignById(campaignId);
            if (campaign.getIsEnabled()) {
                throw new ResourceAlreadyExistsException("This campaign is already enabled.");
            }
            campaign.setIsEnabled(true);
            campaign.setCampaignStage(CampaignStage.FUNDING);
            campaign.setEnabledAt(now.format(dateTimeFormatter));
            campaign.setExpiredAt(now.plusDays(campaign.getCampaignDuration()).format(dateTimeFormatter));

            var user = userService.utilGetUserByUsername(campaign.getOwner());
            user.setTotalCampaigns((short) (user.getTotalCampaigns() + 1));
            userService.saveUser(user);

            return campaignRepository.save(campaign);
        } catch (DataAccessException ex) {
            log.error("Error retrieving Enabling campaigns : {}", ex.getMessage());
            throw new RuntimeException("Error retrieving Enabling campaigns ", ex);
        }
    }

    @Override
    public CampaignDTO getCampaignById(Long campaignId) {
        try {
            Campaign campaign = utilGetCampaignById(campaignId);

            CampaignDTO campaignDTO = campaignDTOMapper.applyById(campaign);
            var campaignBankAccount = campaignBankAccountRepository.findCampaignBankAccountByCampaignCampaignId(campaignId);
            var collaborators = collaboratorRepository.findAllCollaboratorByCampaignId(campaignId);
            var rewards = rewardRepository.findRewardsByCampaignId(campaignId);
            var promotions = promotionRepository.findPromotionByCampaignId(campaignId);
            var user = userService.getUserByUsername(campaign.getOwner());
            var contributors = paymentService.getPaymentByCampaignId(campaignId);

            if (campaignBankAccount.isPresent()) campaignDTO.setCampaignBankAccount(campaignBankAccount.get());
            campaignDTO.setCollaborators(collaborators);
            campaignDTO.setRewards(rewards);
            campaignDTO.setPromotions(promotions);
            campaignDTO.setContributors(contributors);
            campaignDTO.setOwnerFullName(user.getFullName());
            campaignDTO.setNumberOfBackers(contributors.size());

            return campaignDTO;
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns by Id: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns by Id", ex);
        }
    }

    @Override
    public List<CampaignDTO> getCampaignByCategory(Long categoryId) {
        try {
            var campaigns = campaignRepository.findCampaignsByCampaignSubCategoryCampaignCategoryCampaignCategoryIdAndCampaignStageIn(categoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
            if (campaigns.isEmpty()) {
                throw new ResourceNotFoundException("There is no campaign for this category.");
            }

            return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns by category: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns by category ", ex);
        }
    }

    @Override
    public List<CampaignDTO> getCampaignBySubCategory(Long subCategoryId) {
        try {
            var campaigns = campaignRepository.findCampaignsByCampaignSubCategoryCampaignSubCategoryIdAndCampaignStageIn(subCategoryId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
            if (campaigns.isEmpty()) {
                throw new ResourceNotFoundException("There is no campaign for this sub-category.");
            }

            return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns by sub-category: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns by sub-category ", ex);
        }
    }

    @Override
    public List<CampaignDTO> getCampaignsByOwner(String owner) {
        try {
            List<Campaign> campaigns = campaignRepository.findCampaignsByOwner(owner);
            if (campaigns.isEmpty()) throw new ResourceNotFoundException("There is no campaign for this User.");

            return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns by owner: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns by owner ", ex);
        }
    }

    @Override
    public List<CampaignDTO> searchCampaigns(String searchParam) {
        try {
            // Clean up the search parameter string by trimming whitespace and splitting by non-word characters
            String[] searchParamArray = searchParam.trim().split("\\W+");
            // Join the cleaned-up search parameters with "|" to create a regex search pattern
            String searchPattern = String.join("|", searchParamArray);
            var campaigns = campaignRepository.searchForCampaigns(searchPattern);

            if (campaigns.isEmpty()) {
                throw new ResourceNotFoundException("No campaigns found with this search parameter.");
            }

            return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns by searchParam: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns by searchParam ", ex);
        }
    }

    @Override
    public List<CampaignDTO> getCampaignsByFundingType(Long fundingTypeId) {
        try {
            var campaigns = campaignRepository.findCampaignsByFundingTypeFundingTypeIdAndCampaignStageIn(fundingTypeId, List.of(CampaignStage.FUNDING, CampaignStage.COMPLETED));
            if (campaigns.isEmpty()) throw new ResourceNotFoundException("There is no campaign for this Funding Type.");

            return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns by Funding Type: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns by Funding Type ", ex);
        }
    }

    @Override
    public List<CampaignDTO> getCampaignsByStage(String campaignStage) {
        try {
            CampaignStage result = CampaignStage.lookup(campaignStage);
            var campaigns = campaignRepository.findCampaignsByCampaignStage(result);
            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("There is no campaign at " + campaignStage + " stage.");

            return campaigns.stream().map(campaignDTOMapper).collect(Collectors.toList());
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns by stage: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns by stage ", ex);
        }
    }

    @Override
    public void deleteCampaign(Long campaignId) {
        try {
            CampaignDTO campaign = getCampaignById(campaignId);
            Optional<CampaignBankAccount> campaignBankAccount = Optional.ofNullable(campaign.getCampaignBankAccount());
            Optional<List<Reward>> rewards = Optional.ofNullable(campaign.getRewards());
            Optional<List<Collaborator>> collaborators = Optional.ofNullable(campaign.getCollaborators());
            Optional<List<Promotion>> promotions = Optional.ofNullable(campaign.getPromotions());
            Optional<List<Payment>> contributors = Optional.ofNullable(campaign.getContributors());
            Optional<List<CampaignLike>> likes = Optional.ofNullable(campaignLikeRepository.findByCampaignCampaignId(campaignId));

            campaignBankAccount.ifPresent(campaignBankAccountRepository::delete);
            rewards.ifPresent(rewardRepository::deleteAll);
            collaborators.ifPresent(collaboratorRepository::deleteAll);
            promotions.ifPresent(promotionRepository::deleteAll);
            contributors.ifPresent(paymentRepository::deleteAll);
            likes.ifPresent(campaignLikeRepository::deleteAll);

            campaignRepository.deleteById(campaignId);
        } catch (DataAccessException ex) {
            log.error("Error Deleting campaigns: {}", ex.getMessage());
            throw new RuntimeException("Error Deleting campaigns ", ex);
        }
    }

    //    utils methods for this class
    @Override
    public Campaign utilGetCampaignById(Long campaignId) {
        try {
            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no campaign with this ID."));
            return campaign;
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns by Id: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns by Id", ex);
        }

    }
}