package com.dxvalley.crowdfunding.campaign.campaignApproval;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaignApproval.file.ApprovalFile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;
    @Column(nullable = false)
    private String approvedBy;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;
    @OneToMany(fetch = FetchType.EAGER)
    private List<ApprovalFile> approvalFiles = new ArrayList<>();

    private Double commissionRate;
    @Column(nullable = false)
    private String approvedAt;
}

