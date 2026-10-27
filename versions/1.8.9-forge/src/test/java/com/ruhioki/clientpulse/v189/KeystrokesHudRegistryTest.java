package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.module.ClientModule;
import com.ruhioki.clientpulse.core.module.ModuleCategory;
import com.ruhioki.clientpulse.core.module.ModuleRegistry;
import com.ruhioki.clientpulse.core.setting.ColorSetting;
import com.ruhioki.clientpulse.core.setting.NumberSetting;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public final class KeystrokesHudRegistryTest {
    @Test
    public void keystrokesHudIsOptInAndHasPersistableDisplaySettings() {
        ModuleRegistry registry = ModuleRegistry.createDefault();
        ClientModule module = registry.get("keystrokes-hud");

        assertEquals(ModuleCategory.HUD, module.category());
        assertFalse(module.enabled());
        assertEquals(0xA78BFA, ((ColorSetting) module.setting("color")).rgb());
        assertEquals(2.0, ((NumberSetting) module.setting("x-position")).doubleValue(), 0.0001);
        assertEquals(30.0, ((NumberSetting) module.setting("y-position")).doubleValue(), 0.0001);
    }
}
