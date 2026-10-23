package com.ruhioki.clientpulse.core.hud;

public final class HudPosition {
    private HudPosition() {
    }

    public static int place(int percentage, int viewportExtent, int elementExtent) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("HUD position percentage must be between 0 and 100.");
        }
        if (viewportExtent < 0 || elementExtent < 0) {
            throw new IllegalArgumentException("HUD viewport and element extents must not be negative.");
        }
        int available = Math.max(0, viewportExtent - elementExtent);
        return Math.round(available * (percentage / 100.0f));
    }
}
