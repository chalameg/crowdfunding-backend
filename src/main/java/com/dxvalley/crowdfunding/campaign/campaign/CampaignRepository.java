package com.dxvalley.crowdfunding.campaign.campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findCampaignByCampaignId(Long campaignId);

    List<Campaign> findCampaignsByOwner(String owner);

    List<Campaign> findCampaignsByCampaignStage(CampaignStage campaignStage);

    List<Campaign> findCampaignsByCampaignStageIn(List<CampaignStage> campaignStages);

    List<Campaign> findCampaignsByFundingTypeFundingTypeIdAndCampaignStageIn(Long fundingTypeId, List<CampaignStage> campaignStages);

    List<Campaign> findCampaignsByCampaignSubCategoryCampaignCategoryCampaignCategoryIdAndCampaignStageIn(Long categoryId, List<CampaignStage> campaignStages);

    List<Campaign> findCampaignsByCampaignSubCategoryCampaignSubCategoryIdAndCampaignStageIn(Long subCategoryId, List<CampaignStage> campaignStages);

    @Query(value = "SELECT * " +
            "FROM campaign WHERE document @@ to_tsquery(:searchValue) AND campaign_stage IN('FUNDING','COMPLETED')" +
            "ORDER BY ts_rank(document,plainto_tsquery(:searchValue)) DESC;", nativeQuery = true)
    List<Campaign> searchForCampaigns(String searchValue);

}
