package com.dxvalley.crowdfunding.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.dxvalley.crowdfunding.models.Campaign;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRepository extends JpaRepository<Campaign, Long>{
    Campaign findCampaignByCampaignId(Long campaignId);
    List<Campaign> findCampaignsByOwner(String owner);

    @Query("SELECT new Campaign(c.title, c.shortDescription, c.city,c.imageUrl,c.goalAmount," +
            "c.campaignDuration,c.projectType)" +
            " from Campaign as c where" +
            " c.campaignSubCategory.campaignCategory.campaignCategoryId = :categoryId")
    List<Campaign> findByCampaignByCategoryId(Long categoryId);

    @Query("SELECT new Campaign(c.title, c.shortDescription, c.city,c.imageUrl,c.goalAmount," +
            "c.campaignDuration,c.projectType)" +
            " from Campaign as c where" +
            " c.campaignSubCategory.campaignSubCategoryId = :subCategoryId")
    List<Campaign> findByCampaignBySubCategoryId(Long subCategoryId);

}
