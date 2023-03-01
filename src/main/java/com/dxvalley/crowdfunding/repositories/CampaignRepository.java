package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.CampaignStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.dxvalley.crowdfunding.models.Campaign;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findCampaignByCampaignId(Long campaignId);

    List<Campaign> findCampaignsByOwner(String owner);

    List<Campaign> findCampaignsByCampaignStage(CampaignStage campaignStage);

    List<Campaign> findCampaignsByCampaignStageIn(List<CampaignStage> campaignStages);

    List<Campaign> findCampaignsByFundingTypeFundingTypeIdAndCampaignStageIn(Long fundingTypeId, List<CampaignStage> campaignStages);

    List<Campaign> findCampaignsByIsEnabled(Boolean isEnabled);

    List<Campaign> findCampaignsByCampaignSubCategoryCampaignCategoryCampaignCategoryIdAndCampaignStageIn(Long categoryId, List<CampaignStage> campaignStages);

    List<Campaign> findCampaignsByCampaignSubCategoryCampaignSubCategoryIdAndCampaignStageIn(Long subCategoryId, List<CampaignStage> campaignStages);

    @Query(value = "SELECT * " +
            "FROM campaign WHERE document @@ to_tsquery(:searchValue) AND campaign_stage IN('FUNDING','COMPLETED')" +
            "ORDER BY ts_rank(document,plainto_tsquery(:searchValue)) DESC;", nativeQuery = true)
    List<Campaign> searchForCampaigns(String searchValue);

}
