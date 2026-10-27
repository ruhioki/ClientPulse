package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.module.ModuleRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ClientPulse189.MOD_ID, name = "ClientPulse", version = "0.1.0",
        acceptedMinecraftVersions = "[1.8.9]")
public final class ClientPulse189 {
    public static final String MOD_ID = "clientpulse";
    private static ModuleRegistry modules;

    @SidedProxy(clientSide = "com.ruhioki.clientpulse.v189.ClientProxy189",
            serverSide = "com.ruhioki.clientpulse.v189.CommonProxy189")
    public static CommonProxy189 proxy;

    @Mod.EventHandler
    public void initialize(FMLInitializationEvent event) {
        modules = ModuleRegistry.createDefault();
        proxy.initialize();
        System.out.println("[ClientPulse] Initialized for Minecraft 1.8.9.");
    }

    public static ModuleRegistry modules() {
        return modules;
    }
}
