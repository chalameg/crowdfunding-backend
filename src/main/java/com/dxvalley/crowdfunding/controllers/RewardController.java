package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dxvalley.crowdfunding.services.RewardService;
import com.dxvalley.crowdfunding.models.Reward;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
    @Autowired
    private RewardService rewardService;

    @GetMapping("/getByCampaignId/{campaignId}")
    ResponseEntity<?> getRewards(@PathVariable Long campaignId) {
        var reward = rewardService.findRewardsByCampaignId(campaignId);
        return new ResponseEntity<>(reward, HttpStatus.OK);
    }

    @GetMapping("getRewardById/{rewardId}")
    ResponseEntity<?> getReward(@PathVariable Long rewardId) {
        Reward reward = rewardService.getRewardById(rewardId);
        return new ResponseEntity<>(reward, HttpStatus.OK);
    }

    @PostMapping("add/{campaignId}")
    public ResponseEntity<?> addReward(@RequestBody Reward reward, @PathVariable Long campaignId) {
        var reward1 = rewardService.addReward(campaignId, reward);
        return new ResponseEntity<>(reward1, HttpStatus.OK);
    }

    @PutMapping("edit/{rewardId}")
    ResponseEntity<?> editReward(@RequestBody Reward reward, @PathVariable Long rewardId) {
        var reward1 = rewardService.editReward(rewardId, reward);
        return new ResponseEntity<>(reward1, HttpStatus.OK);
    }

    @DeleteMapping("delete/{rewardId}")
    ResponseEntity<?> deleteReward(@PathVariable Long rewardId) {
        rewardService.deleteReward(rewardId);
        ApiResponse response = new ApiResponse("success", "Reward Deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}


