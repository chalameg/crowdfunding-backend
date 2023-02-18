package com.dxvalley.crowdfunding.models;

import java.util.Arrays;

public enum CurrencyCode {
    ETB,
    EUR,
    GBP,
    USD,
    AED;

    public static CurrencyCode lookup(String currencyCode) {
        return Arrays.stream(CurrencyCode.values())
                .filter(e -> e.name().equalsIgnoreCase(currencyCode)).findAny()
                .orElseThrow(()-> new IllegalArgumentException("Invalid Currency Code."));
    }
}
