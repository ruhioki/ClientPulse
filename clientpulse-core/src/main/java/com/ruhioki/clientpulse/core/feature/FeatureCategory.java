package com.ruhioki.clientpulse.core.feature;

public enum FeatureCategory {
    HUD("HUD"),
    QUALITY_OF_LIFE("Quality of Life"),
    DIAGNOSTICS("Diagnostics");

    private final String displayName;

    FeatureCategory(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
