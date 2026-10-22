package com.ruhioki.clientpulse.core.feature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

class FeatureRegistryTest {
    @Test
    void filtersByCategoryAndCaseInsensitiveSearch() {
        FeatureRegistry registry = FeatureRegistry.createDefault();

        assertEquals(3, registry.filter(FeatureCategory.HUD, "").size());
        assertEquals("coordinates-hud", registry.filter(FeatureCategory.HUD, "WORLD POSITION").get(0).id());
        assertEquals("memory-hud", registry.filter(FeatureCategory.DIAGNOSTICS, "memory").get(0).id());
        assertTrue(registry.filter(FeatureCategory.QUALITY_OF_LIFE, "").isEmpty());
    }

    @Test
    void restoresOnlyRegisteredEnabledFeatures() {
        FeatureRegistry registry = FeatureRegistry.createDefault();
        registry.restoreEnabled(Set.of("fps-hud", "removed-feature"));

        assertTrue(registry.get("fps-hud").enabled());
        assertFalse(registry.get("coordinates-hud").enabled());
        assertThrows(IllegalArgumentException.class, () -> registry.get("removed-feature"));
    }

    @Test
    void rejectsDuplicateFeatureIds() {
        FeatureRegistry registry = FeatureRegistry.createDefault();

        assertThrows(IllegalArgumentException.class, () -> registry.register(new ClientFeature(
                "fps-hud", "Duplicate", "Duplicate ID", FeatureCategory.HUD)));
    }
}
