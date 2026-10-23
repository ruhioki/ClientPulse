package com.ruhioki.clientpulse.core.module;

public enum ModuleCategory {
    HUD("HUD"),
    VISUAL("Visual"),
    UTILITY("Utility"),
    PERFORMANCE("Performance");

    private final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
