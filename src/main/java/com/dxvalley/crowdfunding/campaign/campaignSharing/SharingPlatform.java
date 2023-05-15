package com.dxvalley.crowdfunding.campaign.campaignSharing;

import java.util.Arrays;

public enum SharingPlatform {
    FACEBOOK,
    TIKTOK,
    TWITTER,
    TELEGRAM,
    WHATSAPP,
    LINKEDIN,
    INSTAGRAM,
    OTHER;

    public static String lookup(String sharingPlatform) {
        return Arrays.stream(SharingPlatform.values())
                .filter(e -> e.name().equalsIgnoreCase(sharingPlatform)).findAny().map(sharingPlatform1 -> sharingPlatform1.name())
                .orElse(SharingPlatform.OTHER.name());
    }
}
