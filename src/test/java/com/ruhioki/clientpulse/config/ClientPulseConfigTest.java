package com.ruhioki.clientpulse.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import com.ruhioki.clientpulse.core.feature.ClientFeature;
import com.ruhioki.clientpulse.core.feature.FeatureRegistry;

class ClientPulseConfigTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void savesAndLoadsFeatureStates() {
        Path configPath = temporaryDirectory.resolve("config").resolve("clientpulse.json");
        FeatureRegistry original = FeatureRegistry.createDefault();
        original.restoreEnabled(Set.of("fps-hud", "memory-hud"));
        assertTrue(new ClientPulseConfig(configPath, LoggerFactory.getLogger("config-test")).save(original));

        FeatureRegistry restored = FeatureRegistry.createDefault();
        new ClientPulseConfig(configPath, LoggerFactory.getLogger("config-test")).load(restored);

        assertTrue(restored.get("fps-hud").enabled());
        assertTrue(restored.get("memory-hud").enabled());
        assertFalse(restored.get("coordinates-hud").enabled());
    }

    @Test
    void malformedConfigDoesNotPartiallyApplyFeatureStates() throws IOException {
        Path configPath = temporaryDirectory.resolve("clientpulse.json");
        Files.writeString(configPath,
                "{\"features\":{\"fps-hud\":true,\"memory-hud\":\"yes\"}}",
                StandardCharsets.UTF_8);

        FeatureRegistry registry = FeatureRegistry.createDefault();
        new ClientPulseConfig(configPath, LoggerFactory.getLogger("config-test")).load(registry);

        for (ClientFeature feature : registry.all()) {
            assertFalse(feature.enabled(), "Malformed config must leave defaults unchanged: " + feature.id());
        }
    }
}
