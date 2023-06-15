package com.dxvalley.crowdfunding.userManager.user;

import java.util.Arrays;

public enum UserStatus {
    ACTIVE,
    BANNED;

    public static UserStatus lookup(String userStatus) {
        return Arrays.stream(UserStatus.values())
                .filter(e -> e.name().equalsIgnoreCase(userStatus)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid User Status."));
    }
}
