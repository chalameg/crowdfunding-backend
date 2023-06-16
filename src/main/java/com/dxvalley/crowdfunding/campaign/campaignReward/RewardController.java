package com.dxvalley.crowdfunding.campaign.campaignReward;

import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardRequest;
import com.dxvalley.crowdfunding.campaign.campaignReward.dto.RewardResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rewards")
public class RewardController {
    private final RewardService rewardService;

    @GetMapping("/getByCampaignId/{campaignId}")
    ResponseEntity<List<RewardResponse>> getRewards(@PathVariable Long campaignId) {
        List<RewardResponse> rewards = rewardService.getByCampaign(campaignId);
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("getRewardById/{rewardId}")
    ResponseEntity<RewardResponse> getReward(@PathVariable Long rewardId) {
        RewardResponse reward = rewardService.getRewardById(rewardId);
        return ResponseEntity.ok(reward);
    }

    @PostMapping("/add")
    public ResponseEntity<RewardResponse> addReward(@RequestBody @Valid RewardRequest rewardRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rewardService.addReward(rewardRequest));
    }

    @PutMapping("/edit/{rewardId}")
    ResponseEntity<RewardResponse> editReward(@PathVariable Long rewardId, @RequestBody RewardRequest rewardRequest) {
        return ResponseEntity.ok(rewardService.editReward(rewardId, rewardRequest));
    }

    @DeleteMapping("delete/{rewardId}")
    ResponseEntity<ApiResponse> deleteReward(@PathVariable Long rewardId) {
        return rewardService.deleteReward(rewardId);
    }

}


