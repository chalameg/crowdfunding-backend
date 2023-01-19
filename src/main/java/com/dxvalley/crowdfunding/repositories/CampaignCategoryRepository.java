package com.dxvalley.crowdfunding.repositories;

import com.dxvalley.crowdfunding.models.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dxvalley.crowdfunding.models.CampaignCategory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



import com.dxvalley.crowdfunding.models.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.time.LocalDateTime;
import java.util.List;


@Repository
@Transactional(readOnly = true)
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {
    CampaignCategory findCampaignCategoryByCampaignCategoryId(Long campaignCategoryId);
    CampaignCategory findByName(String name);
}





