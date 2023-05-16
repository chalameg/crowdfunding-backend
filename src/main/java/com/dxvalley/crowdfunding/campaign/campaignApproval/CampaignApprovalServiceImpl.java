package com.dxvalley.crowdfunding.campaign.campaignApproval;

import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalMapper;
import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignApprovalServiceImpl implements CampaignApprovalService {

    private final CampaignApprovalRepository campaignApprovalRepository;
    private final CampaignApprovalMapper campaignApprovalMapper;

    /**
     * Retrieves the campaign approval information based on the provided campaign ID.
     *
     * @param campaignId The ID of the campaign.
     * @return The campaign approval information.
     * @throws ResourceNotFoundException if there is no approval information available for the campaign.
     * @throws DatabaseAccessException   if an error occurs while accessing the database.
     */
    @Override
    public ApprovalResponse getCampaignApprovalByCampaignId(Long campaignId) {
        try {
            CampaignApproval campaignApproval = campaignApprovalRepository.findCampaignApprovalByCampaignCampaignId(campaignId)
                    .orElseThrow(() -> new ResourceNotFoundException("There is no approval information for this campaign."));

            return campaignApprovalMapper.toApprovalResponse(campaignApproval);

        } catch (DataAccessException ex) {
            log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                    getClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    ex.getMessage());

            throw new DatabaseAccessException("An error occurred while accessing the database");
        }
    }

}

