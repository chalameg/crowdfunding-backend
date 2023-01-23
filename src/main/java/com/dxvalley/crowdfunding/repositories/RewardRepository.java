package com.dxvalley.crowdfunding.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dxvalley.crowdfunding.models.Reward;
import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long>{
    Reward findRewardByRewardId(Long rewardId);
    List<Reward> findRewardsByCampaignCampaignId(Long campaignId);
}
