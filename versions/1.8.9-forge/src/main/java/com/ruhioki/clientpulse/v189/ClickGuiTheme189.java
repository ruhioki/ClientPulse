package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.animation.ColorAnimation;
import com.ruhioki.clientpulse.core.module.ClientModule;
import com.ruhioki.clientpulse.core.module.ModuleRegistry;
import com.ruhioki.clientpulse.core.setting.BooleanSetting;
import com.ruhioki.clientpulse.core.setting.ColorSetting;
import com.ruhioki.clientpulse.core.setting.EnumSetting;
import com.ruhioki.clientpulse.core.setting.NumberSetting;

public final class ClickGuiTheme189 {
    private final ModuleRegistry modules;

    public ClickGuiTheme189(ModuleRegistry modules) {
        this.modules = modules;
    }

    public int panel() {
        return "Midnight".equals(theme()) ? 0xF10C101B : 0xF0121724;
    }

    public int sidebar() {
        return "Midnight".equals(theme()) ? 0xF2090C14 : 0xF51A2232;
    }

    public int card() {
        return "Blue".equals(theme()) ? 0xF0182434 : 0xF0202735;
    }

    public int cardHover() {
        return "Blue".equals(theme()) ? 0xF0243853 : 0xF02D3545;
    }

    public int muted() {
        return 0xFF9BA9BD;
    }

    public int text() {
        return 0xFFF0F4FA;
    }

    public int accent() {
        String theme = theme();
        if ("RGB".equals(theme)) {
            return ColorAnimation.rainbow(System.currentTimeMillis(), 0.08f, 0.82f, 1.0f);
        }
        if (!interfaceModule().enabled()) {
            return 0xFF6CAEFF;
        }
        if ("Midnight".equals(theme)) {
            return 0xFF9B8CFF;
        }
        if ("Blue".equals(theme)) {
            return 0xFF51B9FF;
        }
        if ("Violet".equals(theme)) {
            return 0xFFB083FF;
        }
        return color("accent").rgb();
    }

    public int accentArgb(float alpha) {
        int alphaByte = Math.max(0, Math.min(255, Math.round(alpha * 255.0f)));
        return alphaByte << 24 | accent() & 0xFFFFFF;
    }

    public int animationDuration() {
        return (int) Math.round(number("animation-duration").doubleValue());
    }

    public boolean animationsEnabled() {
        return bool("animations").value().booleanValue();
    }

    private String theme() {
        return ((EnumSetting) interfaceModule().setting("theme")).value();
    }

    private ClientModule interfaceModule() {
        return modules.get("interface-theme");
    }

    private ColorSetting color(String id) {
        return (ColorSetting) interfaceModule().setting(id);
    }

    private NumberSetting number(String id) {
        return (NumberSetting) interfaceModule().setting(id);
    }

    private BooleanSetting bool(String id) {
        return (BooleanSetting) interfaceModule().setting(id);
    }
}
