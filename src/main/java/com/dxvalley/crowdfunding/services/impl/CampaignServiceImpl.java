package com.dxvalley.crowdfunding.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.repositories.CampaignRepository;
import com.dxvalley.crowdfunding.services.CampaignService;

@Service
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;

    public CampaignServiceImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }
    @Override
    public Campaign addCampaign(Campaign campaign) {
        return this.campaignRepository.save(campaign);
    }

    @Override
    public Campaign editCampaign(Campaign campaign) {
        return this.campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getCampaigns() {
        return this.campaignRepository.findAll();
    }

    @Override
    public Campaign getCampaignById(Long campaignId) {
        return this.campaignRepository.findCampaignByCampaignId(campaignId);
    }

    @Override
    public void deleteCampaign(Long campaignId) {
        campaignRepository.deleteById(campaignId);
    }
    @Override
    public List<Campaign> findCampaignsByOwner(String owner) {
        return campaignRepository.findCampaignsByOwner(owner);
    }
    
}
