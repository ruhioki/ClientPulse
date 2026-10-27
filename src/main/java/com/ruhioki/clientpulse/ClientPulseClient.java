package com.ruhioki.clientpulse;

import com.ruhioki.clientpulse.hud.FpsHud;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Client-side entry point for ClientPulse. */
public final class ClientPulseClient implements ClientModInitializer {
    public static final String MOD_ID = "clientpulse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("ClientPulse initialized. Learn. Build. Optimize.");
        FpsHud.register();
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main")
        );
        KeyMapping toggleFpsHud = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.clientpulse.toggle_fps_hud",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_F8,
                category
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleFpsHud.consumeClick()) {
                FpsHud.toggleEnabled();
            }
        });
    }
}
