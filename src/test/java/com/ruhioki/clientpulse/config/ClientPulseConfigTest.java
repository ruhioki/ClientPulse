package com.ruhioki.clientpulse.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import com.ruhioki.clientpulse.core.feature.FeatureRegistry;

class ClientPulseConfigTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void savesAndLoadsFeatureStates() {
        Path configPath = temporaryDirectory.resolve("config").resolve("clientpulse.json");
        FeatureRegistry original = FeatureRegistry.createDefault();
        original.restoreEnabled(Set.of("fps-hud", "memory-hud"));
        new ClientPulseConfig(configPath, LoggerFactory.getLogger("config-test")).save(original);

        FeatureRegistry restored = FeatureRegistry.createDefault();
        new ClientPulseConfig(configPath, LoggerFactory.getLogger("config-test")).load(restored);

        assertTrue(restored.get("fps-hud").enabled());
        assertTrue(restored.get("memory-hud").enabled());
        assertFalse(restored.get("coordinates-hud").enabled());
    }
}
