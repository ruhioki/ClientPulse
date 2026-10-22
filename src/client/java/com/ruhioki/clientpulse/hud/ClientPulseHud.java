package com.ruhioki.clientpulse.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;

import com.ruhioki.clientpulse.core.feature.FeatureRegistry;

public final class ClientPulseHud {
    private static final int PANEL_COLOR = 0xB5101628;
    private static final int TEXT_COLOR = 0xFFE5EEFF;

    private final FeatureRegistry features;
    private int tickCounter;
    private List<String> lines = List.of();

    public ClientPulseHud(FeatureRegistry features) {
        this.features = features;
    }

    public void register() {
        net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.attachElementBefore(
                net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements.CHAT,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("clientpulse", "diagnostics"),
                this::render);
    }

    public void tick(Minecraft minecraft) {
        if (++tickCounter % 10 != 0) {
            return;
        }

        List<String> updatedLines = new ArrayList<>(4);
        if (features.get("fps-hud").enabled()) {
            updatedLines.add("FPS  " + minecraft.getFps());
        }
        if (features.get("coordinates-hud").enabled() && minecraft.player != null) {
            updatedLines.add(String.format(Locale.ROOT, "XYZ  %.1f  %.1f  %.1f",
                    minecraft.player.getX(), minecraft.player.getY(), minecraft.player.getZ()));
        }
        if (features.get("session-hud").enabled() && minecraft.level != null) {
            updatedLines.add("WORLD  " + minecraft.level.dimension().identifier().toString());
        }
        if (features.get("memory-hud").enabled()) {
            Runtime runtime = Runtime.getRuntime();
            long usedMegabytes = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMegabytes = runtime.maxMemory() / (1024 * 1024);
            updatedLines.add("MEMORY  " + usedMegabytes + " / " + maxMegabytes + " MB");
        }
        lines = List.copyOf(updatedLines);
    }

    private void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (lines.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int widestLine = 0;
        for (String line : lines) {
            widestLine = Math.max(widestLine, minecraft.font.width(line));
        }
        int lineHeight = minecraft.font.lineHeight + 3;
        graphics.fill(5, 5, 17 + widestLine, 13 + lines.size() * lineHeight, PANEL_COLOR);
        for (int index = 0; index < lines.size(); index++) {
            graphics.text(minecraft.font, Component.literal(lines.get(index)),
                    10, 9 + index * lineHeight, TEXT_COLOR, true);
        }
    }
}
