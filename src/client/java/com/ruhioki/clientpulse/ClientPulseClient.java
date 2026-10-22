package com.ruhioki.clientpulse;

import java.nio.file.Path;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruhioki.clientpulse.config.ClientPulseConfig;
import com.ruhioki.clientpulse.core.feature.FeatureRegistry;
import com.ruhioki.clientpulse.hud.ClientPulseHud;
import com.ruhioki.clientpulse.screen.ClientPulseScreen;

public final class ClientPulseClient implements ClientModInitializer {
    public static final String MOD_ID = "clientpulse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static FeatureRegistry features;
    private static ClientPulseConfig config;
    private static ClientPulseHud hud;
    private static KeyMapping openSettingsKey;

    @Override
    public void onInitializeClient() {
        features = FeatureRegistry.createDefault();
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");
        config = new ClientPulseConfig(configPath, LOGGER);
        config.load(features);

        hud = new ClientPulseHud(features);
        hud.register();
        registerOpenSettingsKey();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            hud.tick(client);
            while (openSettingsKey.consumeClick()) {
                if (client.gui.screen() == null) {
                    client.gui.setScreen(new ClientPulseScreen(null, features));
                }
            }
        });

        LOGGER.info("ClientPulse initialized for Minecraft {}.", "26.3");
    }

    private static void registerOpenSettingsKey() {
        KeyMapping.Category category = KeyMapping.Category.register(id("clientpulse"));
        openSettingsKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.clientpulse.open_settings",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_RSHIFT,
                category));
    }

    public static boolean saveConfig() {
        return config.save(features);
    }

    public static boolean openSettingsKeyMatches(KeyEvent keyEvent) {
        return openSettingsKey.matches(keyEvent);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
