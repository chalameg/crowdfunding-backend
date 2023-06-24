package com.dxvalley.crowdfunding.admin.setting;

import com.dxvalley.crowdfunding.campaign.campaignSetting.Setting;
import com.dxvalley.crowdfunding.campaign.campaignSetting.SettingService;
import com.dxvalley.crowdfunding.campaign.campaignSetting.dto.SettingUpdateReq;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/admin/setting"})
public class AdminSettingController {
    private final SettingService settingService;

    public AdminSettingController(final SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public ResponseEntity<Setting> getSetting() {
        return ResponseEntity.ok(this.settingService.getSettingForAdmin());
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<Setting> updateSetting(@PathVariable Short id, @RequestBody @Valid SettingUpdateReq newSetting) {
        return ResponseEntity.ok(this.settingService.updateSetting(id, newSetting));
    }
}