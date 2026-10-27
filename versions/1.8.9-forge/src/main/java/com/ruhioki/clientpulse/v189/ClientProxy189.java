package com.ruhioki.clientpulse.v189;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;

import com.ruhioki.clientpulse.core.hud.ClickRateTracker;
import com.ruhioki.clientpulse.core.hud.HudPosition;
import com.ruhioki.clientpulse.core.module.ClientModule;
import com.ruhioki.clientpulse.core.module.ModuleRegistry;
import com.ruhioki.clientpulse.core.setting.ColorSetting;
import com.ruhioki.clientpulse.core.setting.EnumSetting;
import com.ruhioki.clientpulse.core.setting.KeybindSetting;
import com.ruhioki.clientpulse.core.setting.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class ClientProxy189 extends CommonProxy189 {
    private static final KeyBinding OPEN_SETTINGS = new KeyBinding(
            "key.clientpulse.open_settings", Keyboard.KEY_RSHIFT, "key.categories.clientpulse");

    private File settingsFile;
    private long frameWindowStart;
    private int framesInWindow;
    private int measuredFramesPerSecond;
    private final ClickRateTracker leftClicks = new ClickRateTracker();
    private final ClickRateTracker rightClicks = new ClickRateTracker();

    @Override
    public void initialize() {
        settingsFile = new File(Minecraft.getMinecraft().mcDataDir, "config/clientpulse-1.8.9.properties");
        loadSettings();
        applyOpenGuiKeybind();
        ClientRegistry.registerKeyBinding(OPEN_SETTINGS);
        MinecraftForge.EVENT_BUS.register(this);
        Cosmetics189.initialize();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (OPEN_SETTINGS.isPressed() && minecraft.currentScreen == null) {
            minecraft.displayGuiScreen(new ClientPulseSettingsScreen189());
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        int keyCode = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState() && keyCode > 0
                && keyCode != OPEN_SETTINGS.getKeyCode()
                && Minecraft.getMinecraft().currentScreen == null
                && ClientPulse189.modules().toggleBoundModules(keyCode) > 0) {
            saveSettings();
        }
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        int button = Mouse.getEventButton();
        if (!Mouse.getEventButtonState() || Minecraft.getMinecraft().currentScreen != null
                || Minecraft.getMinecraft().thePlayer == null
                || !ClientPulse189.modules().get("cps-hud").enabled()) {
            return;
        }

        long now = System.nanoTime();
        if (button == 0) {
            leftClicks.recordClick(now);
        } else if (button == 1) {
            rightClicks.recordClick(now);
        }
    }

    @SubscribeEvent
    public void onHudRender(RenderGameOverlayEvent.Text event) {
        updateFrameRate();
        Minecraft minecraft = Minecraft.getMinecraft();
        ModuleRegistry modules = ClientPulse189.modules();
        ScaledResolution resolution = event.resolution;

        ClientModule fps = modules.get("fps-hud");
        if (fps.enabled()) {
            float scale = (float) ((NumberSetting) fps.setting("scale")).doubleValue();
            drawPositionedHudText(minecraft, resolution, fps,
                    "ClientPulse | FPS: " + measuredFramesPerSecond,
                    ((ColorSetting) fps.setting("color")).rgb(), scale);
        }

        ClientModule coordinates = modules.get("coordinates-hud");
        if (coordinates.enabled() && minecraft.thePlayer != null) {
            String precision = ((EnumSetting) coordinates.setting("precision")).value();
            int decimals = "Integer".equals(precision) ? 0 : "Two decimals".equals(precision) ? 2 : 1;
            String coordinateText = "XYZ: "
                    + formatCoordinate(minecraft.thePlayer.posX, decimals) + "  "
                    + formatCoordinate(minecraft.thePlayer.posY, decimals) + "  "
                    + formatCoordinate(minecraft.thePlayer.posZ, decimals);
            drawPositionedHudText(minecraft, resolution, coordinates, coordinateText,
                    ((ColorSetting) coordinates.setting("color")).rgb(), 1.0f);
        }

        ClientModule cps = modules.get("cps-hud");
        if (cps.enabled() && minecraft.thePlayer != null) {
            long now = System.nanoTime();
            String cpsText = "CPS: L " + leftClicks.clicksPerSecond(now)
                    + " | R " + rightClicks.clicksPerSecond(now);
            drawPositionedHudText(minecraft, resolution, cps, cpsText,
                    ((ColorSetting) cps.setting("color")).rgb(), 1.0f);
        }

        ClientModule keystrokes = modules.get("keystrokes-hud");
        if (keystrokes.enabled() && minecraft.thePlayer != null && minecraft.currentScreen == null) {
            KeystrokesHud189.render(minecraft, resolution, keystrokes);
        }

        ClientModule session = modules.get("session-hud");
        if (session.enabled() && minecraft.theWorld != null) {
            drawPositionedHudText(minecraft, resolution, session,
                    "Dimension: " + minecraft.theWorld.provider.getDimensionId(),
                    ((ColorSetting) session.setting("color")).rgb(), 1.0f);
        }

        ClientModule memory = modules.get("memory-hud");
        if (memory.enabled()) {
            Runtime runtime = Runtime.getRuntime();
            long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long max = runtime.maxMemory() / (1024 * 1024);
            drawPositionedHudText(minecraft, resolution, memory,
                    "Memory: " + used + " / " + max + " MB",
                    ((ColorSetting) memory.setting("color")).rgb(), 1.0f);
        }
    }

    @Override
    public void saveSettings() {
        applyOpenGuiKeybind();
        Properties properties = new Properties();
        ClientPulse189.modules().save(properties);

        Path settingsPath = settingsFile.toPath();
        Path temporaryPath = settingsPath.resolveSibling(settingsFile.getName() + ".tmp");
        try {
            Files.createDirectories(settingsPath.getParent());
            FileOutputStream output = new FileOutputStream(temporaryPath.toFile());
            try {
                properties.store(output, "ClientPulse 1.8.9 settings");
            } finally {
                output.close();
            }
            try {
                Files.move(temporaryPath, settingsPath,
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryPath, settingsPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            System.err.println("[ClientPulse] Could not save settings: " + exception.getMessage());
        }
    }

    @Override
    public boolean isOpenSettingsKey(int keyCode) {
        return OPEN_SETTINGS.getKeyCode() == keyCode;
    }

    public void applyOpenGuiKeybind() {
        ClientModule interfaceModule = ClientPulse189.modules().get("interface-theme");
        int keyCode = ((KeybindSetting) interfaceModule.setting("open-key")).keyCode();
        if (OPEN_SETTINGS.getKeyCode() != keyCode) {
            OPEN_SETTINGS.setKeyCode(keyCode);
            KeyBinding.resetKeyBindingArrayAndHash();
        }
    }

    private void loadSettings() {
        if (!settingsFile.isFile()) {
            return;
        }

        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(settingsFile);
            try {
                properties.load(input);
            } finally {
                input.close();
            }

            ModuleRegistry modules = ClientPulse189.modules();
            for (ClientModule module : modules.all()) {
                String legacyValue = properties.getProperty(module.id());
                String newKey = "module." + module.id() + ".enabled";
                if (legacyValue != null && properties.getProperty(newKey) == null) {
                    properties.setProperty(newKey, legacyValue);
                }
            }
            List<String> warnings = modules.restore(properties);
            for (String warning : warnings) {
                System.err.println("[ClientPulse] " + warning + " Using default value.");
            }
        } catch (IOException exception) {
            System.err.println("[ClientPulse] Could not load settings; using defaults: " + exception.getMessage());
        }
    }

    private void updateFrameRate() {
        long now = System.currentTimeMillis();
        if (frameWindowStart == 0) {
            frameWindowStart = now;
        }
        framesInWindow++;
        long elapsed = now - frameWindowStart;
        if (elapsed >= 1000) {
            measuredFramesPerSecond = (int) (framesInWindow * 1000L / elapsed);
            framesInWindow = 0;
            frameWindowStart = now;
        }
    }

    private static void drawPositionedHudText(Minecraft minecraft, ScaledResolution resolution,
            ClientModule module, String text, int rgb, float scale) {
        int textWidth = Math.round(minecraft.fontRendererObj.getStringWidth(text) * scale);
        int textHeight = Math.round(minecraft.fontRendererObj.FONT_HEIGHT * scale);
        int xPercent = (int) Math.round(((NumberSetting) module.setting("x-position")).doubleValue());
        int yPercent = (int) Math.round(((NumberSetting) module.setting("y-position")).doubleValue());
        int x = HudPosition.place(xPercent, resolution.getScaledWidth(), textWidth);
        int y = HudPosition.place(yPercent, resolution.getScaledHeight(), textHeight);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.scale(scale, scale, 1.0f);
            minecraft.fontRendererObj.drawString(text, Math.round(x / scale),
                    Math.round(y / scale), 0xFF000000 | rgb);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static String formatCoordinate(double value, int decimals) {
        if (decimals == 0) {
            return Long.toString(Math.round(value));
        }
        long factor = decimals == 1 ? 10L : 100L;
        long rounded = Math.round(value * factor);
        long whole = rounded / factor;
        long fraction = Math.abs(rounded % factor);
        return decimals == 1
                ? whole + "." + fraction
                : whole + "." + (fraction < 10L ? "0" : "") + fraction;
    }
}
