package com.dxvalley.crowdfunding.campaign.campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Optional<Campaign> findCampaignByIdAndCampaignStage(Long campaignId, CampaignStage campaignStage);

    List<Campaign> findCampaignsByUserUsername(String username);

    List<Campaign> findCampaignsByCampaignStage(CampaignStage campaignStage);

    List<Campaign> findCampaignsByCampaignStageIn(List<CampaignStage> campaignStages);

    List<Campaign> findCampaignsByFundingTypeIdAndCampaignStage(Long fundingTypeId, CampaignStage campaignStages);

    List<Campaign> findCampaignsByCampaignSubCategoryCampaignCategoryIdAndCampaignStageIn(Long categoryId, List<CampaignStage> campaignStages);

    List<Campaign> findCampaignsByCampaignSubCategoryIdAndCampaignStageIn(Long subCategoryId, List<CampaignStage> campaignStages);

    List<Campaign> findByBankAccountAccountNumber(String accountNumber);


    @Query(value = "SELECT * " +
            "FROM campaign WHERE document @@ to_tsquery(:searchValue) AND campaign_stage IN('FUNDING','COMPLETED')" +
            "ORDER BY ts_rank(document,plainto_tsquery(:searchValue)) DESC;", nativeQuery = true)
    List<Campaign> searchForCampaigns(String searchValue);

}
