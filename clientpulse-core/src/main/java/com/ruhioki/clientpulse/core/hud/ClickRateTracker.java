package com.ruhioki.clientpulse.core.hud;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ClickRateTracker {
    // Keep this literal Java 6-compatible for the Minecraft 1.7.10 Forge target.
    private static final long WINDOW_NANOS = 1000000000L;

    private final Deque<Long> clickTimes = new ArrayDeque<Long>();

    /**
     * Records a click using a monotonic timestamp, such as System.nanoTime().
     * Expired entries are removed here as well as during reads so a quiet HUD
     * cannot leave an ever-growing queue behind.
     */
    public void recordClick(long timestampNanos) {
        pruneExpiredClicks(timestampNanos);
        clickTimes.addLast(Long.valueOf(timestampNanos));
    }

    public int clicksPerSecond(long nowNanos) {
        pruneExpiredClicks(nowNanos);
        return clickTimes.size();
    }

    private void pruneExpiredClicks(long nowNanos) {
        long cutoff = nowNanos - WINDOW_NANOS;
        while (!clickTimes.isEmpty() && clickTimes.peekFirst().longValue() <= cutoff) {
            clickTimes.removeFirst();
        }
    }
}
