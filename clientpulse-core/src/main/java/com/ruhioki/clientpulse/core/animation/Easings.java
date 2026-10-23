package com.ruhioki.clientpulse.core.animation;

public final class Easings {
    public static final Easing LINEAR = new Easing() {
        @Override
        public float apply(float progress) {
            return progress;
        }
    };

    public static final Easing EASE_OUT_CUBIC = new Easing() {
        @Override
        public float apply(float progress) {
            float inverse = 1.0f - progress;
            return 1.0f - inverse * inverse * inverse;
        }
    };

    public static final Easing EASE_IN_OUT = new Easing() {
        @Override
        public float apply(float progress) {
            return progress < 0.5f
                    ? 4.0f * progress * progress * progress
                    : 1.0f - (float) Math.pow(-2.0f * progress + 2.0f, 3.0) / 2.0f;
        }
    };

    private Easings() {
    }
}
