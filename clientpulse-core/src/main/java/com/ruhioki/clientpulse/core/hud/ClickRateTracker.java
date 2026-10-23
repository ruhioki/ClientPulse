package com.ruhioki.clientpulse.core.hud;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ClickRateTracker {
    private static final long WINDOW_NANOS = 1_000_000_000L;

    private final Deque<Long> clickTimes = new ArrayDeque<Long>();

    public void recordClick(long timestampNanos) {
        clickTimes.addLast(Long.valueOf(timestampNanos));
    }

    public int clicksPerSecond(long nowNanos) {
        long cutoff = nowNanos - WINDOW_NANOS;
        while (!clickTimes.isEmpty() && clickTimes.peekFirst().longValue() <= cutoff) {
            clickTimes.removeFirst();
        }
        return clickTimes.size();
    }
}
