package com.dxvalley.crowdfunding.campaign.campaignSetting;

import com.dxvalley.crowdfunding.campaign.campaignSetting.dto.SettingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/setting"})
public class SettingController {
    private final SettingService settingService;

    public SettingController(final SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public ResponseEntity<SettingResponse> getSetting() {
        return ResponseEntity.ok(this.settingService.getSetting());
    }
}
