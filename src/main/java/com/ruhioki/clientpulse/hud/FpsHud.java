package com.ruhioki.clientpulse.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/** Renders a lightweight FPS counter on the client HUD. */
public final class FpsHud {
    private static final long SAMPLE_DURATION_NANOS = 1_000_000_000L;
    private static boolean enabled = true;
    private static boolean samplingStarted;
    private static long sampleStartNanos;
    private static int framesInSample;
    private static int framesPerSecond;

    private FpsHud() {
    }

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("clientpulse", "fps_hud"),
                FpsHud::render
        );
    }

    public static void toggleEnabled() {
        enabled = !enabled;
        samplingStarted = false;
        framesInSample = 0;
        framesPerSecond = 0;
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!enabled) {
            return;
        }

        long now = System.nanoTime();
        if (!samplingStarted) {
            sampleStartNanos = now;
            framesInSample = 0;
            samplingStarted = true;
        }
        framesInSample++;

        long elapsedNanos = now - sampleStartNanos;
        if (elapsedNanos >= SAMPLE_DURATION_NANOS) {
            framesPerSecond = (int) Math.round(framesInSample * (double) SAMPLE_DURATION_NANOS / elapsedNanos);
            framesInSample = 0;
            sampleStartNanos = now;
        }

        String text = "ClientPulse | " + framesPerSecond + " FPS";
        graphics.text(Minecraft.getInstance().font, text, 6, 6, 0xFFFFFFFF, true);
    }
}
