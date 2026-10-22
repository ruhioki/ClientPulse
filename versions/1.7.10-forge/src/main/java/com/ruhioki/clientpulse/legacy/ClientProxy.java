package com.ruhioki.clientpulse.legacy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import com.ruhioki.clientpulse.core.feature.ClientFeature;
import com.ruhioki.clientpulse.core.feature.FeatureRegistry;

public final class ClientProxy extends CommonProxy {
    private static final KeyBinding OPEN_SETTINGS = new KeyBinding(
            "key.clientpulse.open_settings", Keyboard.KEY_RSHIFT, "key.categories.clientpulse");

    private File settingsFile;
    private long frameWindowStart;
    private int framesInWindow;
    private int measuredFramesPerSecond;

    @Override
    public void initialize() {
        settingsFile = new File(Minecraft.getMinecraft().mcDataDir, "config/clientpulse-1.7.10.properties");
        loadSettings();
        ClientRegistry.registerKeyBinding(OPEN_SETTINGS);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            boolean pressed = OPEN_SETTINGS.isPressed();
            Minecraft minecraft = Minecraft.getMinecraft();
            if (pressed && minecraft.currentScreen == null) {
                minecraft.displayGuiScreen(new ClientPulseSettingsScreen());
            }
        }
    }

    @SubscribeEvent
    public void onHudRender(RenderGameOverlayEvent.Text event) {
        updateFrameRate();
        Minecraft minecraft = Minecraft.getMinecraft();
        FeatureRegistry features = ClientPulseLegacy.features();

        if (features.get("fps-hud").enabled()) {
            event.left.add("ClientPulse | FPS: " + measuredFramesPerSecond);
        }
        if (features.get("coordinates-hud").enabled() && minecraft.thePlayer != null) {
            event.left.add(String.format(java.util.Locale.ROOT, "XYZ: %.1f  %.1f  %.1f",
                    minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ));
        }
        if (features.get("session-hud").enabled() && minecraft.theWorld != null) {
            event.left.add("Dimension: " + minecraft.theWorld.provider.dimensionId);
        }
        if (features.get("memory-hud").enabled()) {
            Runtime runtime = Runtime.getRuntime();
            long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long max = runtime.maxMemory() / (1024 * 1024);
            event.left.add("Memory: " + used + " / " + max + " MB");
        }
    }

    @Override
    public void saveSettings() {
        Properties properties = new Properties();
        for (ClientFeature feature : ClientPulseLegacy.features().all()) {
            properties.setProperty(feature.id(), Boolean.toString(feature.enabled()));
        }

        File parent = settingsFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            System.err.println("[ClientPulse] Could not create settings directory: " + parent);
            return;
        }

        try {
            FileOutputStream output = new FileOutputStream(settingsFile);
            try {
                properties.store(output, "ClientPulse 1.7.10 settings");
            } finally {
                output.close();
            }
        } catch (IOException exception) {
            System.err.println("[ClientPulse] Could not save settings: " + exception.getMessage());
        }
    }

    @Override
    public boolean isOpenSettingsKey(int keyCode) {
        return OPEN_SETTINGS.getKeyCode() == keyCode;
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
            Set<String> enabledIds = new HashSet<String>();
            for (ClientFeature feature : ClientPulseLegacy.features().all()) {
                if (Boolean.parseBoolean(properties.getProperty(feature.id(), "false"))) {
                    enabledIds.add(feature.id());
                }
            }
            ClientPulseLegacy.features().restoreEnabled(enabledIds);
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
}
