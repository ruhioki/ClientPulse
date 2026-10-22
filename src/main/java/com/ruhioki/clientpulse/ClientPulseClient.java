package com.ruhioki.clientpulse;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for ClientPulse. Runs only on the Minecraft client.
 */
public class ClientPulseClient implements ClientModInitializer {
    public static final String MOD_ID = "clientpulse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("ClientPulse initialized. Learn. Build. Optimize.");
    }
}
