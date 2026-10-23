package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.animation.Animation;
import com.ruhioki.clientpulse.core.animation.Easings;
import com.ruhioki.clientpulse.core.module.ClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public final class ModuleButton189 {
    private final ClientModule module;
    private final Animation hover = new Animation(0.0f);
    private final Animation enabled = new Animation(0.0f);
    private boolean wasHovered;
    private boolean wasEnabled;

    public ModuleButton189(ClientModule module) {
        this.module = module;
        this.wasEnabled = module.enabled();
        this.enabled.reset(wasEnabled ? 1.0f : 0.0f);
    }

    public ClientModule module() {
        return module;
    }

    public void draw(Minecraft minecraft, ClickGuiTheme189 theme, int x, int y, int width, int height,
            int mouseX, int mouseY, float alpha) {
        boolean isHovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        if (isHovered != wasHovered) {
            wasHovered = isHovered;
            hover.animateTo(isHovered ? 1.0f : 0.0f, theme.animationDuration(), Easings.EASE_OUT_CUBIC);
        }
        if (module.enabled() != wasEnabled) {
            wasEnabled = module.enabled();
            enabled.animateTo(wasEnabled ? 1.0f : 0.0f, theme.animationDuration(), Easings.EASE_OUT_CUBIC);
        }

        float hoverValue = theme.animationsEnabled() ? hover.value() : (isHovered ? 1.0f : 0.0f);
        float enabledValue = theme.animationsEnabled() ? enabled.value() : (module.enabled() ? 1.0f : 0.0f);
        int base = interpolate(theme.card(), theme.cardHover(), hoverValue);
        Gui.drawRect(x, y, x + width, y + height, CategorySidebar189.withAlpha(base, alpha));
        Gui.drawRect(x, y, x + 3, y + height,
                CategorySidebar189.withAlpha(theme.accentArgb(0.25f + enabledValue * 0.75f), alpha));

        int textColor = CategorySidebar189.withAlpha(module.enabled() ? theme.text() : theme.muted(), alpha);
        minecraft.fontRendererObj.drawString(module.name(), x + 11, y + 9, textColor);
        String details = module.category().displayName() + " - " + module.description();
        details = minecraft.fontRendererObj.trimStringToWidth(details, Math.max(0, width - 66));
        minecraft.fontRendererObj.drawString(details,
                x + 11, y + 22, CategorySidebar189.withAlpha(theme.muted(), alpha));

        int switchWidth = 30;
        int switchLeft = x + width - switchWidth - 10;
        int switchTop = y + (height - 12) / 2;
        Gui.drawRect(switchLeft, switchTop, switchLeft + switchWidth, switchTop + 12,
                CategorySidebar189.withAlpha(interpolate(0xFF414B5B, theme.accent(), enabledValue), alpha));
        int knobLeft = switchLeft + 2 + Math.round((switchWidth - 14) * enabledValue);
        Gui.drawRect(knobLeft, switchTop + 2, knobLeft + 8, switchTop + 10,
                CategorySidebar189.withAlpha(0xFFFFFFFF, alpha));
    }

    public boolean toggleAt(int mouseX, int mouseY, int x, int y, int width, int height) {
        int switchLeft = x + width - 40;
        if (mouseX >= switchLeft && mouseX < x + width && mouseY >= y && mouseY < y + height) {
            module.toggle();
            return true;
        }
        return false;
    }

    private static int interpolate(int first, int second, float amount) {
        int red = blend((first >> 16) & 0xFF, (second >> 16) & 0xFF, amount);
        int green = blend((first >> 8) & 0xFF, (second >> 8) & 0xFF, amount);
        int blue = blend(first & 0xFF, second & 0xFF, amount);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    private static int blend(int first, int second, float amount) {
        return Math.max(0, Math.min(255, Math.round(first + (second - first) * amount)));
    }
}
