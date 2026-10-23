package com.ruhioki.clientpulse.core.cosmetic;

public enum CosmeticCategory {
    CAPES("Capes"),
    WINGS("Wings"),
    HATS("Hats"),
    TRAILS("Trails"),
    BADGES("Badges");

    private final String displayName;

    CosmeticCategory(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
