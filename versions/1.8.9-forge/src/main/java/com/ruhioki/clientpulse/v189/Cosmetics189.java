package com.ruhioki.clientpulse.v189;

import com.ruhioki.clientpulse.core.cosmetic.CosmeticProfile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Cosmetics189 {
    private static final Logger LOGGER = LogManager.getLogger("ClientPulse Cosmetics");
    private static final CosmeticProfile PROFILE = new CosmeticProfile();
    private static Path configuration;
    private static boolean initialized;

    private Cosmetics189() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        configuration = Paths.get(Minecraft.getMinecraft().mcDataDir.getAbsolutePath(),
                "config", "clientpulse-cosmetics.properties");
        load();
        MinecraftForge.EVENT_BUS.register(new CosmeticRenderer189());
    }

    public static CosmeticProfile profile() {
        initialize();
        return PROFILE;
    }

    public static void save() {
        initialize();
        Properties properties = new Properties();
        PROFILE.save(properties);
        Path temporary = configuration.resolveSibling(configuration.getFileName().toString() + ".tmp");
        try {
            Files.createDirectories(configuration.getParent());
            try (OutputStream output = Files.newOutputStream(temporary)) {
                properties.store(output, "ClientPulse local cosmetic settings");
            }
            try {
                Files.move(temporary, configuration, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, configuration, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            LOGGER.error("Unable to save cosmetic settings to " + configuration, exception);
        } finally {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException exception) {
                LOGGER.warn("Unable to remove temporary cosmetic settings file " + temporary, exception);
            }
        }
    }

    private static void load() {
        if (!Files.exists(configuration)) {
            return;
        }
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(configuration)) {
            properties.load(input);
        } catch (IOException exception) {
            LOGGER.error("Unable to load cosmetic settings from " + configuration, exception);
            return;
        }
        List<String> invalid = PROFILE.restore(properties);
        if (!invalid.isEmpty()) {
            LOGGER.warn("Ignored invalid cosmetic settings: " + invalid);
        }
    }
}
