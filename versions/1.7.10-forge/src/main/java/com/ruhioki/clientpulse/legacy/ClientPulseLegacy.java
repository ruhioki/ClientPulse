package com.ruhioki.clientpulse.legacy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import com.ruhioki.clientpulse.core.feature.FeatureRegistry;

@Mod(modid = ClientPulseLegacy.MOD_ID, name = "ClientPulse", version = "0.1.0",
        acceptedMinecraftVersions = "[1.7.10]")
public final class ClientPulseLegacy {
    public static final String MOD_ID = "clientpulse";
    private static FeatureRegistry features;

    @SidedProxy(clientSide = "com.ruhioki.clientpulse.legacy.ClientProxy",
            serverSide = "com.ruhioki.clientpulse.legacy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void initialize(FMLInitializationEvent event) {
        features = FeatureRegistry.createDefault();
        proxy.initialize();
        System.out.println("[ClientPulse] Initialized for Minecraft 1.7.10.");
    }

    public static FeatureRegistry features() {
        return features;
    }

}
