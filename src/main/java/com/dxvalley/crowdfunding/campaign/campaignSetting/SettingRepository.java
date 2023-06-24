package com.dxvalley.crowdfunding.campaign.campaignSetting;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends CrudRepository<Setting, Short> {
}