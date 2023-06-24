package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignApproval.ApprovalStatus;
import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApproval;
import com.dxvalley.crowdfunding.campaign.campaignApproval.CampaignApprovalRepository;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalMapper;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalReq;
import com.dxvalley.crowdfunding.campaign.campaignApproval.file.ApprovalFile;
import com.dxvalley.crowdfunding.campaign.campaignApproval.file.ApprovalFileService;
import com.dxvalley.crowdfunding.exception.customException.BadRequestException;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminCampaignApprovalService {
    private final CampaignApprovalRepository campaignApprovalRepository;
    private final CampaignUtils campaignUtils;
    private final UserUtils userUtils;
    private final DateTimeFormatter dateTimeFormatter;
    private final ApprovalFileService approvalFileService;

    public ApprovalResponse getCampaignApprovalByCampaignId(Long campaignId) {
        CampaignApproval campaignApproval = (CampaignApproval)this.campaignApprovalRepository.findByCampaignId(campaignId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no approval information for this campaign.");
        });
        return CampaignApprovalMapper.toAdminApprovalResponse(campaignApproval);
    }

    @Transactional
    public ResponseEntity<String> approveCampaign(CampaignApprovalReq campaignApprovalReq) {
        this.checkExistingApproval(campaignApprovalReq.getCampaignId());
        Campaign campaign = this.getCampaign(campaignApprovalReq.getCampaignId());
        CampaignApproval campaignApproval = this.createCampaignApproval(campaign, campaignApprovalReq);
        this.campaignApprovalRepository.save(campaignApproval);
        this.processCampaignApproval(campaign, campaignApprovalReq);
        return ResponseEntity.ok("Campaign " + campaignApprovalReq.getApprovalStatus().toLowerCase() + " Successfully");
    }

    private void checkExistingApproval(Long campaignId) {
        Optional<CampaignApproval> existingApproval = this.campaignApprovalRepository.findByCampaignId(campaignId);
        if (existingApproval.isPresent()) {
            throw new ResourceAlreadyExistsException("This campaign has already been approved.");
        }
    }

    private Campaign getCampaign(Long campaignId) {
        Campaign campaign = this.campaignUtils.utilGetCampaignByIdAndStage(campaignId, CampaignStage.PENDING);
        if (!this.campaignUtils.isValidCampaign(campaign)) {
            throw new BadRequestException("Unable to approve the campaign with the provided data. Please provide all required information and try again.");
        } else {
            return campaign;
        }
    }

    private CampaignApproval createCampaignApproval(Campaign campaign, CampaignApprovalReq campaignApprovalReq) {
        CampaignApproval campaignApproval = new CampaignApproval();
        campaignApproval.setCampaign(campaign);
        campaignApproval.setApprovedBy(campaignApprovalReq.getApprovedBy());
        campaignApproval.setApprovalStatus(ApprovalStatus.lookup(campaignApprovalReq.getApprovalStatus()));
        campaignApproval.setReason(campaignApprovalReq.getReason());
        campaignApproval.setCommissionRate(campaignApprovalReq.getCommissionRate());
        campaignApproval.setApprovedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        if (campaignApprovalReq.getFiles() != null && !campaignApprovalReq.getFiles().isEmpty()) {
            List<ApprovalFile> approvalFiles = this.approvalFileService.saveApprovalFile(campaignApprovalReq.getFiles());
            campaignApproval.setApprovalFiles(approvalFiles);
        }

        return campaignApproval;
    }

    private void processCampaignApproval(Campaign campaign, CampaignApprovalReq campaignApprovalReq) {
        if (ApprovalStatus.ACCEPTED.name().equalsIgnoreCase(campaignApprovalReq.getApprovalStatus())) {
            this.acceptCampaign(campaign);
        } else {
            this.rejectCampaign(campaign);
        }

    }

    private void acceptCampaign(Campaign campaign) {
        this.configureCampaignForAcceptance(campaign);
        this.incrementUserTotalCampaigns(campaign.getUser().getUsername());
    }

    private void rejectCampaign(Campaign campaign) {
        campaign.setEnabled(false);
        campaign.setCampaignStage(CampaignStage.REJECTED);
        campaign.setApprovedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        this.campaignUtils.saveCampaign(campaign);
    }

    private void configureCampaignForAcceptance(Campaign campaign) {
        campaign.setEnabled(true);
        campaign.setCampaignStage(CampaignStage.FUNDING);
        campaign.setApprovedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        campaign.setCompletedAt(LocalDateTime.now().plusDays((long)campaign.getCampaignDuration()).format(this.dateTimeFormatter));
        this.campaignUtils.saveCampaign(campaign);
    }

    private void incrementUserTotalCampaigns(String username) {
        Users user = this.userUtils.utilGetUserByUsername(username);
        user.setTotalCampaigns((short)(user.getTotalCampaigns() + 1));
        this.userUtils.saveUser(user);
    }

}

