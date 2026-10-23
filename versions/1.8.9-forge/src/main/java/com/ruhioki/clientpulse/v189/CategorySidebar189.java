package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.module.ClientModule;
import com.ruhioki.clientpulse.core.module.ModuleCategory;
import com.ruhioki.clientpulse.core.module.ModuleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public final class CategorySidebar189 {
    private final int[] counts = new int[ModuleCategory.values().length];
    private final int totalCount;

    public CategorySidebar189(ModuleRegistry modules) {
        this.totalCount = modules.all().size();
        for (ClientModule module : modules.all()) {
            counts[module.category().ordinal()]++;
        }
    }

    public void draw(Minecraft minecraft, ClickGuiTheme189 theme, int x, int y,
            int width, int rowStride, ModuleCategory selected, int mouseX, int mouseY, float alpha) {
        int rowHeight = Math.max(14, rowStride - 4);
        drawRow(minecraft, theme, x, y, width, rowHeight, "All modules", totalCount,
                selected == null, mouseX, mouseY, alpha);
        int rowY = y + rowStride;
        for (ModuleCategory category : ModuleCategory.values()) {
            int count = counts[category.ordinal()];
            if (count == 0) {
                continue;
            }
            drawRow(minecraft, theme, x, rowY, width, rowHeight, category.displayName(), count,
                    category == selected, mouseX, mouseY, alpha);
            rowY += rowStride;
        }
    }

    public ModuleCategory categoryAt(int mouseX, int mouseY, int x, int y, int width, int rowStride) {
        int rowHeight = Math.max(14, rowStride - 4);
        if (inside(mouseX, mouseY, x, y, width, rowHeight)) {
            return null;
        }
        int rowY = y + rowStride;
        for (ModuleCategory category : ModuleCategory.values()) {
            if (counts[category.ordinal()] == 0) {
                continue;
            }
            if (inside(mouseX, mouseY, x, rowY, width, rowHeight)) {
                return category;
            }
            rowY += rowStride;
        }
        return null;
    }

    private void drawRow(Minecraft minecraft, ClickGuiTheme189 theme, int x, int y, int width,
            int height, String label, int count, boolean active, int mouseX, int mouseY, float alpha) {
        boolean hovered = inside(mouseX, mouseY, x, y, width, height);
        if (active || hovered) {
            Gui.drawRect(x, y, x + width, y + height,
                    withAlpha(active ? theme.accentArgb(0.22f) : 0xFF2E3644, alpha));
        }
        if (active) {
            Gui.drawRect(x, y, x + 3, y + height, theme.accentArgb(alpha));
        }
        minecraft.fontRendererObj.drawString(label, x + 12, y + (height - 8) / 2,
                withAlpha(active ? theme.text() : theme.muted(), alpha));
        minecraft.fontRendererObj.drawString(Integer.toString(count),
                x + width - 22, y + (height - 8) / 2, withAlpha(theme.muted(), alpha));
    }

    private static boolean inside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    static int withAlpha(int color, float alpha) {
        int originalAlpha = color >>> 24;
        int newAlpha = Math.max(0, Math.min(255, Math.round(originalAlpha * alpha)));
        return newAlpha << 24 | color & 0xFFFFFF;
    }
}
