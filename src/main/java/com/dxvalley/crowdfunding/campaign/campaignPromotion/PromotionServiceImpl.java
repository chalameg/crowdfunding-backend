package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionMapper;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionReq;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionRes;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionUpdateReq;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final CampaignUtils campaignUtils;
    private final DateTimeFormatter dateTimeFormatter;

    @Override
    public PromotionRes addPromotion(PromotionReq promotionReq) {
        Promotion promotion = new Promotion();
        Campaign campaign = campaignUtils.utilGetCampaignById(promotion.getId());

        promotion.setPromotionLink(promotionReq.getPromotionLink());
        promotion.setDescription(promotionReq.getDescription());
        promotion.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
        promotion.setCampaign(campaign);

        Promotion savedPromotion = promotionRepository.save(promotion);

        return PromotionMapper.toPromotionRes(savedPromotion);
    }

    @Override
    public PromotionRes editPromotion(Long id, PromotionUpdateReq promotionUpdateReq) {
        Promotion promotion = getPromotionById(id);

        promotion.setDescription(promotionUpdateReq.getDescription());
        promotion.setUpdatedAt(LocalDateTime.now().format(dateTimeFormatter));

        Promotion savedPromotion = promotionRepository.save(promotion);

        return PromotionMapper.toPromotionRes(savedPromotion);

    }

    @Override
    public Promotion getPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow();
    }

    @Override
    public List<PromotionRes> getPromotionByCampaign(Long campaignId) {
        List<Promotion> promotions = promotionRepository.findPromotionByCampaignId(campaignId);
        if (promotions.isEmpty())
            throw new ResourceNotFoundException("Promotion not found for this campaign");

        return promotions.stream().map(PromotionMapper::toPromotionRes).toList();
    }

    @Override
    public ResponseEntity<ApiResponse> deletePromotion(Long promotionId) {
        getPromotionById(promotionId);
        promotionRepository.deleteById(promotionId);

        return ApiResponse.success("Deleted Successfully");
    }
}
