package com.dxvalley.crowdfunding.campaign.campaignUpdate;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.userManager.user.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignProgressUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String createdAt;
    private String updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users author;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
}
