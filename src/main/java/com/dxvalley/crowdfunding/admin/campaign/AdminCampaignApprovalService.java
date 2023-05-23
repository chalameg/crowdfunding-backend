package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignApproval.ApprovalStatus;
import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApproval;
import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApprovalRepository;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalDTO;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalMapper;
import com.dxvalley.crowdfunding.exception.BadRequestException;
import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import com.dxvalley.crowdfunding.exception.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.fileUploadManager.FileUploadService;
import com.dxvalley.crowdfunding.user.UserService;
import com.dxvalley.crowdfunding.user.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCampaignApprovalService {
    private final CampaignApprovalRepository campaignApprovalRepository;
    private final CampaignUtils campaignUtils;
    private final UserService userService;
    private final DateTimeFormatter dateTimeFormatter;
    private final CampaignApprovalMapper campaignApprovalMapper;
    private final FileUploadService fileUploadService;


    /**
     * Retrieves the campaign approval information based on the provided campaign ID.
     *
     * @param campaignId The ID of the campaign.
     * @return The campaign approval information.
     * @throws ResourceNotFoundException if there is no approval information available for the campaign.
     * @throws DatabaseAccessException   if an error occurs while accessing the database.
     */
    public ApprovalResponse getCampaignApprovalByCampaignId(Long campaignId) {
        try {
            CampaignApproval campaignApproval = campaignApprovalRepository.findCampaignApprovalByCampaignCampaignId(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no approval information for this campaign."));

            return campaignApprovalMapper.toAdminApprovalResponse(campaignApproval);

        } catch (DataAccessException ex) {
            logError("getCampaignApprovalByCampaignId", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

    /**
     * Create a campaign approval.
     *
     * @param campaignApprovalDTO The DTO containing the campaign approval information.
     * @return The response entity with the created campaign approval.
     */

    @Transactional
    public ApprovalResponse approveCampaign(CampaignApprovalDTO campaignApprovalDTO) {
        try {
            Optional<CampaignApproval> existingApproval = campaignApprovalRepository
                    .findCampaignApprovalByCampaignCampaignId(campaignApprovalDTO.getCampaignId());
            if (existingApproval.isPresent()) {
                throw new ResourceAlreadyExistsException("This campaign has already been approved.");
            }

            Campaign campaign = campaignUtils.utilGetCampaignByIdAndStage(campaignApprovalDTO.getCampaignId(), CampaignStage.PENDING);
            if (!campaignUtils.isValidCampaign(campaign)) {
                throw new BadRequestException("Unable to approve the campaign with the provided data. Please provide all required information and try again.");
            }

            if (ApprovalStatus.ACCEPTED.name().equalsIgnoreCase(campaignApprovalDTO.getApprovalStatus())) {
                acceptCampaign(campaign);
            } else {
                rejectCampaign(campaign);
            }

            CampaignApproval campaignApproval = createCampaignApproval(campaign, campaignApprovalDTO);
            if (campaignApprovalDTO.getFiles() != null && !campaignApprovalDTO.getFiles().isEmpty()) {
                campaignApproval.setApprovalFiles(fileUploadService.uploadFiles(campaignApprovalDTO.getFiles()));
            }

            CampaignApproval savedCampaignApproval = campaignApprovalRepository.save(campaignApproval);
            return campaignApprovalMapper.toAdminApprovalResponse(savedCampaignApproval);
        } catch (DataAccessException ex) {
            logError("approveCampaign", ex);
            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }


    private CampaignApproval createCampaignApproval(Campaign campaign, CampaignApprovalDTO campaignApprovalDTO) {
        CampaignApproval campaignApproval = new CampaignApproval();

        campaignApproval.setCampaign(campaign);
        campaignApproval.setApprovedBy(campaignApprovalDTO.getApprovedBy());
        campaignApproval.setApprovalStatus(ApprovalStatus.lookup(campaignApprovalDTO.getApprovalStatus()));
        campaignApproval.setReason(campaignApprovalDTO.getReason());
        campaignApproval.setCommissionRate(campaignApprovalDTO.getCommissionRate());
        campaignApproval.setApprovalTime(LocalDateTime.now().format(dateTimeFormatter));

        return campaignApproval;
    }

    private void acceptCampaign(Campaign campaign) {
        configureCampaignForAcceptance(campaign);
        incrementUserTotalCampaigns(campaign.getOwner());
    }

    private void rejectCampaign(Campaign campaign) {
        campaign.setIsEnabled(false);
        campaign.setCampaignStage(CampaignStage.REJECTED);
        campaign.setApprovedAt(LocalDateTime.now().format(dateTimeFormatter));
        campaignUtils.utilUpdateCampaign(campaign);
    }

    private void configureCampaignForAcceptance(Campaign campaign) {
        campaign.setIsEnabled(true);
        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setApprovedAt(LocalDateTime.now().format(dateTimeFormatter));
        campaign.setExpiredAt(LocalDateTime.now().plusDays(campaign.getCampaignDuration()).format(dateTimeFormatter));
        campaignUtils.utilUpdateCampaign(campaign);
    }

    private void incrementUserTotalCampaigns(String username) {
        Users user = userService.utilGetUserByUsername(username);
        user.setTotalCampaigns((short) (user.getTotalCampaigns() != null ? user.getTotalCampaigns() + 1 : 1));
        userService.saveUser(user);
    }

    private void logError(String methodName, DataAccessException ex) {
        log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                getClass().getSimpleName(),
                methodName,
                ex.getMessage());
    }
}

