package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.hud.HudPosition;
import com.ruhioki.clientpulse.core.module.ClientModule;
import com.ruhioki.clientpulse.core.setting.ColorSetting;
import com.ruhioki.clientpulse.core.setting.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Lightweight, original keystrokes overlay for the Forge 1.8.9 adapter.
 * It only reads input state and draws simple rectangles/text; it does not
 * register gameplay key bindings or intercept player input.
 */
public final class KeystrokesHud189 {
    private static final int KEY_WIDTH = 24;
    private static final int KEY_HEIGHT = 20;
    private static final int GAP = 3;
    private static final int SPACE_HEIGHT = 16;
    private static final int MOUSE_HEIGHT = 16;
    private static final int PANEL_WIDTH = KEY_WIDTH * 3 + GAP * 2;
    private static final int PANEL_HEIGHT = KEY_HEIGHT * 2 + SPACE_HEIGHT + MOUSE_HEIGHT + GAP * 3;

    private KeystrokesHud189() {
    }

    public static void render(Minecraft minecraft, ScaledResolution resolution, ClientModule module) {
        int xPercent = (int) Math.round(((NumberSetting) module.setting("x-position")).doubleValue());
        int yPercent = (int) Math.round(((NumberSetting) module.setting("y-position")).doubleValue());
        int x = HudPosition.place(xPercent, resolution.getScaledWidth(), PANEL_WIDTH);
        int y = HudPosition.place(yPercent, resolution.getScaledHeight(), PANEL_HEIGHT);
        int accent = ((ColorSetting) module.setting("color")).rgb();

        Gui.drawRect(x - 3, y - 3, x + PANEL_WIDTH + 3, y + PANEL_HEIGHT + 3, 0x55000000);

        drawKey(minecraft, "W", x + KEY_WIDTH + GAP, y, KEY_WIDTH, KEY_HEIGHT,
                Keyboard.isKeyDown(Keyboard.KEY_W), accent);
        int movementY = y + KEY_HEIGHT + GAP;
        drawKey(minecraft, "A", x, movementY, KEY_WIDTH, KEY_HEIGHT,
                Keyboard.isKeyDown(Keyboard.KEY_A), accent);
        drawKey(minecraft, "S", x + KEY_WIDTH + GAP, movementY, KEY_WIDTH, KEY_HEIGHT,
                Keyboard.isKeyDown(Keyboard.KEY_S), accent);
        drawKey(minecraft, "D", x + (KEY_WIDTH + GAP) * 2, movementY, KEY_WIDTH, KEY_HEIGHT,
                Keyboard.isKeyDown(Keyboard.KEY_D), accent);

        int spaceY = movementY + KEY_HEIGHT + GAP;
        drawKey(minecraft, "SPACE", x, spaceY, PANEL_WIDTH, SPACE_HEIGHT,
                Keyboard.isKeyDown(Keyboard.KEY_SPACE), accent);

        int mouseY = spaceY + SPACE_HEIGHT + GAP;
        int mouseWidth = (PANEL_WIDTH - GAP) / 2;
        drawKey(minecraft, "LMB", x, mouseY, mouseWidth, MOUSE_HEIGHT,
                Mouse.isButtonDown(0), accent);
        drawKey(minecraft, "RMB", x + mouseWidth + GAP, mouseY,
                mouseWidth, MOUSE_HEIGHT, Mouse.isButtonDown(1), accent);
    }

    private static void drawKey(Minecraft minecraft, String label, int x, int y, int width, int height,
            boolean pressed, int accent) {
        int fill = pressed ? 0xD0000000 | accent : 0xAA171A24;
        int text = pressed ? 0xFFFFFFFF : 0xFFD7D9E2;
        Gui.drawRect(x, y, x + width, y + height, fill);
        if (pressed) {
            Gui.drawRect(x, y, x + width, y + 2, 0xFF000000 | accent);
        }
        int textX = x + (width - minecraft.fontRendererObj.getStringWidth(label)) / 2;
        int textY = y + (height - minecraft.fontRendererObj.FONT_HEIGHT) / 2;
        minecraft.fontRendererObj.drawString(label, textX, textY, text, true);
    }
}
