package com.ruhioki.clientpulse.v189;

import java.util.Properties;

import com.ruhioki.clientpulse.core.animation.Animation;
import com.ruhioki.clientpulse.core.animation.ColorAnimation;
import com.ruhioki.clientpulse.core.animation.Easings;
import com.ruhioki.clientpulse.core.hud.HudPosition;
import com.ruhioki.clientpulse.core.module.ClientModule;
import com.ruhioki.clientpulse.core.module.ModuleCategory;
import com.ruhioki.clientpulse.core.module.ModuleRegistry;
import com.ruhioki.clientpulse.core.setting.ColorSetting;
import com.ruhioki.clientpulse.core.setting.NumberSetting;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class FoundationTest {
    @Test
    public void animationRetargetsFromCurrentValueAndFinishesAtTarget() {
        Animation animation = new Animation(0.0f);
        animation.animateTo(1.0f, 100L, Easings.LINEAR, 0L);
        assertEquals(0.5f, animation.value(50000000L), 0.0001f);

        animation.animateTo(0.0f, 100L, Easings.EASE_OUT_CUBIC, 50000000L);
        assertEquals(0.5f, animation.value(50000000L), 0.0001f);
        assertEquals(0.0f, animation.value(150000000L), 0.0001f);
        assertTrue(animation.isFinished());
    }

    @Test
    public void animationCanBeDisabledAndReset() {
        Animation animation = new Animation(0.2f);
        animation.animateTo(1.0f, 0L, Easings.EASE_IN_OUT);
        assertEquals(1.0f, animation.value(), 0.0001f);
        assertTrue(animation.isFinished());
        animation.reset(0.4f);
        assertEquals(0.4f, animation.value(), 0.0001f);
        assertTrue(animation.isFinished());
    }

    @Test
    public void rgbInterpolationAndRainbowStayWithinRgbRange() {
        assertEquals(0x808000, ColorAnimation.interpolateRgb(0xFF0000, 0x00FF00, 0.5f));
        int color = ColorAnimation.rainbow(500L, 0.2f, 1.0f, 1.0f);
        assertTrue(color >= 0 && color <= 0xFFFFFF);
    }

    @Test
    public void settingsClampAndRejectInvalidSerializedValues() {
        NumberSetting number = new NumberSetting("scale", "Scale", "", 0.5, 2.0, 0.25, 1.0);
        number.setValue(Double.valueOf(1.91));
        assertEquals(2.0, number.doubleValue(), 0.0001);
        assertFalse(number.restore("NaN"));

        ColorSetting color = new ColorSetting("accent", "Accent", "", 0x123456);
        assertTrue(color.restore("#ABCDEF"));
        assertEquals(0xABCDEF, color.rgb());
        assertFalse(color.restore("not-a-color"));
    }

    @Test
    public void hudPositionStaysInsideViewportAtEachAnchor() {
        assertEquals(0, HudPosition.place(0, 854, 100));
        assertEquals(377, HudPosition.place(50, 854, 100));
        assertEquals(754, HudPosition.place(100, 854, 100));
        assertEquals(0, HudPosition.place(50, 80, 100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hudPositionRejectsInvalidPercentage() {
        HudPosition.place(101, 854, 100);
    }

    @Test
    public void registryPersistsModuleAndSettingStateAndValidatesRestoration() {
        ModuleRegistry registry = ModuleRegistry.createDefault();
        ClientModule fps = registry.get("fps-hud");
        fps.setEnabled(true);
        ((ColorSetting) fps.setting("color")).setValue(Integer.valueOf(0x12ABEF));
        ((NumberSetting) fps.setting("x-position")).setValue(Double.valueOf(37.0));

        Properties saved = new Properties();
        registry.save(saved);
        ModuleRegistry restored = ModuleRegistry.createDefault();
        assertTrue(restored.restore(saved).isEmpty());
        assertTrue(restored.get("fps-hud").enabled());
        assertEquals(0x12ABEF, ((ColorSetting) restored.get("fps-hud").setting("color")).rgb());
        assertEquals(37.0, ((NumberSetting) restored.get("fps-hud").setting("x-position")).doubleValue(),
                0.0001);

        saved.setProperty("module.fps-hud.scale", "infinity");
        assertFalse(restored.restore(saved).isEmpty());
        assertEquals(ModuleCategory.HUD, restored.get("fps-hud").category());
    }
}
