package com.dxvalley.crowdfunding.campaign.campaignPromotion;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.campaignUtils.CampaignUtils;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionMapper;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionReq;
import com.dxvalley.crowdfunding.campaign.campaignPromotion.dto.PromotionResponse;
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
    public PromotionResponse addPromotion(PromotionReq promotionReq) {
        Promotion promotion = new Promotion();
        Campaign campaign = campaignUtils.utilGetCampaignById(promotionReq.getCampaignId());

        promotion.setPromotionLink(promotionReq.getPromotionLink());
        promotion.setDescription(promotionReq.getDescription());
        promotion.setCreatedAt(LocalDateTime.now().format(dateTimeFormatter));
        promotion.setCampaign(campaign);

        Promotion savedPromotion = promotionRepository.save(promotion);

        return PromotionMapper.toPromotionRes(savedPromotion);
    }

    @Override
    public PromotionResponse editPromotion(Long id, PromotionUpdateReq promotionUpdateReq) {
        Promotion promotion = utilGetPromotionById(id);

        promotion.setDescription(promotionUpdateReq.getDescription());
        promotion.setUpdatedAt(LocalDateTime.now().format(dateTimeFormatter));

        Promotion savedPromotion = promotionRepository.save(promotion);

        return PromotionMapper.toPromotionRes(savedPromotion);

    }

    @Override
    public PromotionResponse getPromotionById(Long promotionId) {
        return PromotionMapper.toPromotionRes(utilGetPromotionById(promotionId));
    }

    @Override
    public List<PromotionResponse> getPromotionByCampaign(Long campaignId) {
        List<Promotion> promotions = promotionRepository.findPromotionByCampaignId(campaignId);

        return promotions.stream().map(PromotionMapper::toPromotionRes).toList();
    }

    @Override
    public ResponseEntity<ApiResponse> deletePromotion(Long promotionId) {
        utilGetPromotionById(promotionId);
        promotionRepository.deleteById(promotionId);

        return ApiResponse.success("Deleted Successfully");
    }

    private Promotion utilGetPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion Not Found"));

    }

}
