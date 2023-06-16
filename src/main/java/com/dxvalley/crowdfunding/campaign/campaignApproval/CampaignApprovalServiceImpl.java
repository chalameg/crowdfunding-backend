package com.dxvalley.crowdfunding.campaign.campaignApproval;

import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalMapper;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignApprovalServiceImpl implements CampaignApprovalService {

    private final CampaignApprovalRepository campaignApprovalRepository;

    // Retrieves the campaign approval information by campaign ID.
    @Override
    public ApprovalResponse getCampaignApprovalByCampaignId(Long campaignId) {
        CampaignApproval campaignApproval = campaignApprovalRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no approval information for this campaign."));

        return CampaignApprovalMapper.toApprovalResponse(campaignApproval);
    }

}

