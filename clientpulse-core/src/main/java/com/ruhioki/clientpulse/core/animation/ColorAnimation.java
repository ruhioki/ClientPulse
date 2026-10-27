package com.ruhioki.clientpulse.core.animation;

public final class ColorAnimation {
    private ColorAnimation() {
    }

    public static int interpolateRgb(int first, int second, float progress) {
        float amount = Math.max(0.0f, Math.min(1.0f, progress));
        int red = interpolate((first >> 16) & 0xFF, (second >> 16) & 0xFF, amount);
        int green = interpolate((first >> 8) & 0xFF, (second >> 8) & 0xFF, amount);
        int blue = interpolate(first & 0xFF, second & 0xFF, amount);
        return red << 16 | green << 8 | blue;
    }

    public static int rainbow(long timeMillis, float cyclesPerSecond, float saturation, float brightness) {
        float hue = (timeMillis / 1000.0f * cyclesPerSecond) % 1.0f;
        if (hue < 0.0f) {
            hue += 1.0f;
        }
        return java.awt.Color.HSBtoRGB(hue, clamp(saturation), clamp(brightness)) & 0xFFFFFF;
    }

    private static int interpolate(int first, int second, float amount) {
        return Math.max(0, Math.min(255, Math.round(first + (second - first) * amount)));
    }

    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}
