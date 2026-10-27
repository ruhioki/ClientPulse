package com.ruhioki.clientpulse.core.animation;

public final class TransitionAnimation {
    private final Animation progress = new Animation(1.0f);

    public void open(long durationMillis, boolean enabled) {
        progress.reset(enabled ? 0.0f : 1.0f);
        progress.animateTo(1.0f, enabled ? durationMillis : 0L, Easings.EASE_OUT_CUBIC);
    }

    public float progress() {
        return progress.value();
    }

    public float fade() {
        return progress();
    }

    public float scale(float startScale) {
        float clamped = clamp(startScale, 0.0f, 1.0f);
        return clamped + (1.0f - clamped) * progress();
    }

    public float slideX(float distance) {
        return distance * (1.0f - progress());
    }

    public float slideY(float distance) {
        return distance * (1.0f - progress());
    }

    public boolean isFinished() {
        return progress.isFinished();
    }

    public void reset() {
        progress.reset(1.0f);
    }

    private static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
