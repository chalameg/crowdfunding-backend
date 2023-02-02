package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.models.Promotion;
import com.dxvalley.crowdfunding.repositories.PromotionRepository;
import com.dxvalley.crowdfunding.services.PromotionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public Promotion addPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion editPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion getPromotionById(Long promotionId) {
        return promotionRepository.findPromotionByPromotionId(promotionId);
    }

    @Override
    public List<Promotion> getPromotionByCampaign(Long campaignId) {
        return promotionRepository.findPromotionByCampaignId(campaignId);
    }

    @Override
    public List<Promotion> getPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public void deletePromotion(Long promotionId) {
      promotionRepository.deleteById(promotionId);
    }
}
