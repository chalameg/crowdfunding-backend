package com.dxvalley.crowdfunding.campaign.campaignApproval;

import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.ApprovalResponse;
import com.dxvalley.crowdfunding.campaign.campaignApproval.dto.CampaignApprovalMapper;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CampaignApprovalServiceImpl implements CampaignApprovalService {
    private final CampaignApprovalRepository campaignApprovalRepository;

    public CampaignApprovalServiceImpl(final CampaignApprovalRepository campaignApprovalRepository) {
        this.campaignApprovalRepository = campaignApprovalRepository;
    }

    public ApprovalResponse getCampaignApprovalByCampaignId(Long campaignId) {
        CampaignApproval campaignApproval = this.campaignApprovalRepository.findByCampaignId(campaignId).orElseThrow(() -> {
            return new ResourceNotFoundException("There is no approval information for this campaign.");
        });
        return CampaignApprovalMapper.toApprovalResponse(campaignApproval);
    }
}

