package com.dxvalley.crowdfunding.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dxvalley.crowdfunding.models.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long>{
    Reward findRewardByRewardId(Long rewardId);
}
