package com.ruhioki.clientpulse.core.animation;

public final class Animation {
    private float from;
    private float to;
    private long startedAtNanos;
    private long durationNanos;
    private Easing easing = Easings.LINEAR;
    private boolean running;

    public Animation(float initialValue) {
        reset(initialValue);
    }

    public void animateTo(float target, long durationMillis, Easing easing) {
        animateTo(target, durationMillis, easing, System.nanoTime());
    }

    public void animateTo(float target, long durationMillis, Easing easing, long nowNanos) {
        if (Float.isNaN(target) || Float.isInfinite(target)) {
            throw new IllegalArgumentException("Animation target must be finite.");
        }
        float current = value(nowNanos);
        if (current == target) {
            reset(target);
            return;
        }
        this.from = current;
        this.to = target;
        this.startedAtNanos = nowNanos;
        this.durationNanos = Math.max(0L, durationMillis) * 1000000L;
        this.easing = easing == null ? Easings.LINEAR : easing;
        this.running = durationNanos > 0L;
        if (!running) {
            this.from = target;
        }
    }

    public float value() {
        return value(System.nanoTime());
    }

    public float value(long nowNanos) {
        if (!running) {
            return from;
        }
        long elapsed = Math.max(0L, nowNanos - startedAtNanos);
        if (elapsed >= durationNanos) {
            finish();
            return from;
        }
        float progress = (float) elapsed / (float) durationNanos;
        float eased = easing.apply(progress);
        return from + (to - from) * eased;
    }

    public boolean isFinished() {
        return !running;
    }

    public boolean isFinished(long nowNanos) {
        if (running && nowNanos - startedAtNanos >= durationNanos) {
            finish();
        }
        return !running;
    }

    public void reset(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            throw new IllegalArgumentException("Animation value must be finite.");
        }
        this.from = value;
        this.to = value;
        this.startedAtNanos = 0L;
        this.durationNanos = 0L;
        this.easing = Easings.LINEAR;
        this.running = false;
    }

    private void finish() {
        this.from = to;
        this.running = false;
    }
}
