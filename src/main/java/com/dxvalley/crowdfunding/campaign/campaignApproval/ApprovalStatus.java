package com.dxvalley.crowdfunding.campaign.campaignApproval;

import java.util.Arrays;

public enum ApprovalStatus {
    ACCEPTED,
    REJECTED;

    public static ApprovalStatus lookup(String approvalStatus) {
        return Arrays.stream(ApprovalStatus.values())
                .filter(e -> e.name().equalsIgnoreCase(approvalStatus)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid approvalStatus."));
    }
}

