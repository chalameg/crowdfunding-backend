package com.dxvalley.crowdfunding.admin.campaign;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTO;
import com.dxvalley.crowdfunding.campaign.campaign.dto.CampaignDTOMapper;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminCampaignService {
    private final CampaignRepository campaignRepository;

    private final UserService userService;

    private final CampaignDTOMapper campaignDTOMapper;

    private final DateTimeFormatter dateTimeFormatter;

    public List<Campaign> getCampaigns() {
        try {
            List<Campaign> campaigns = campaignRepository.findAll(Sort.by(Sort.Direction.ASC, "campaignId"));
            if (campaigns.isEmpty())
                throw new ResourceNotFoundException("Currently, There is no campaign.");

            return campaigns;
        } catch (DataAccessException ex) {
            log.error("Error retrieving campaigns: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving campaigns", ex);
        }
    }

    public CampaignDTO suspendResumeCampaign(Long campaignID) {
        Campaign campaign = utilGetCampaignById(campaignID);
        if (campaign.getCampaignStage().equals(CampaignStage.FUNDING))
            campaign.setCampaignStage(CampaignStage.SUSPENDED);
        else
            campaign.setCampaignStage(CampaignStage.FUNDING);
        var result = campaignRepository.save(campaign);
        return campaignDTOMapper.apply(result);
    }

    public CampaignDTO acceptRejectCampaign(Long campaignId, Boolean isAccepted) {
        try {
            Campaign campaign = utilGetCampaignById(campaignId);

            if(isAccepted){
                campaign.setIsEnabled(true);
                campaign.setCampaignStage(CampaignStage.FUNDING);
                campaign.setApprovedAt(LocalDateTime.now().format(dateTimeFormatter));
                campaign.setExpiredAt(LocalDateTime.now().plusDays(campaign.getCampaignDuration()).format(dateTimeFormatter));

                var user = userService.utilGetUserByUsername(campaign.getOwner());
                user.setTotalCampaigns((short) (user.getTotalCampaigns() + 1));
                userService.saveUser(user);

            } else
                campaign.setCampaignStage(CampaignStage.REJECTED);

            log.info("Campaign with id {} enabled", campaignId);
            return campaignDTOMapper.apply(campaignRepository.save(campaign));
        } catch (DataAccessException ex) {
            log.error("Error retrieving Enabling campaigns : {}", ex.getMessage());
            throw new RuntimeException("Error retrieving Enabling campaigns ", ex);
        }
    }



//    public void deleteCampaign(Long campaignId) {
//        try {
//            var campaign = getCampaignById(campaignId);
//            Optional<CampaignBankAccount> campaignBankAccount = Optional.ofNullable(campaign.getCampaignBankAccount());
//            Optional<List<Reward>> rewards = Optional.ofNullable(campaign.getRewards());
//            Optional<List<Collaborator>> collaborators = Optional.ofNullable(campaign.getCollaborators());
//            Optional<List<Promotion>> promotions = Optional.ofNullable(campaign.getPromotions());
//            Optional<List<Payment>> contributors = Optional.ofNullable(campaign.getContributors());
//
//            campaignBankAccount.ifPresent(campaignBankAccountRepository::delete);
//            rewards.ifPresent(rewardRepository::deleteAll);
//            collaborators.ifPresent(collaboratorRepository::deleteAll);
//            promotions.ifPresent(promotionRepository::deleteAll);
//            contributors.ifPresent(paymentRepository::deleteAll);
//
//            log.info("Campaign Deleted");
//            campaignRepository.deleteById(campaignId);
//        } catch (DataAccessException ex) {
//            log.error("Error Deleting campaigns: {}", ex.getMessage());
//            throw new RuntimeException("Error Deleting campaigns ", ex);
//        }
//    }

    //    utils methods for this class

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