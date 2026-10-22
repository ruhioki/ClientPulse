package com.ruhioki.clientpulse.core.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class FeatureRegistry {
    private final Map<String, ClientFeature> features = new LinkedHashMap<String, ClientFeature>();

    public static FeatureRegistry createDefault() {
        FeatureRegistry registry = new FeatureRegistry();
        registry.register(new ClientFeature(
                "fps-hud", "FPS Counter", "Show the current frame rate.", FeatureCategory.HUD));
        registry.register(new ClientFeature(
                "coordinates-hud", "Coordinates", "Show your current world position.", FeatureCategory.HUD));
        registry.register(new ClientFeature(
                "session-hud", "Session Info", "Show the current dimension.", FeatureCategory.HUD));
        registry.register(new ClientFeature(
                "memory-hud", "Memory Usage", "Show JVM memory usage in megabytes.", FeatureCategory.DIAGNOSTICS));
        return registry;
    }

    public void register(ClientFeature feature) {
        Objects.requireNonNull(feature, "feature");
        if (features.putIfAbsent(feature.id(), feature) != null) {
            throw new IllegalArgumentException("Duplicate feature id: " + feature.id());
        }
    }

    public ClientFeature get(String id) {
        ClientFeature feature = features.get(id);
        if (feature == null) {
            throw new IllegalArgumentException("Unknown feature id: " + id);
        }
        return feature;
    }

    public boolean contains(String id) {
        return features.containsKey(id);
    }

    public List<ClientFeature> all() {
        return Collections.unmodifiableList(new ArrayList<ClientFeature>(features.values()));
    }

    public List<ClientFeature> filter(FeatureCategory category, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<ClientFeature> result = new ArrayList<ClientFeature>();
        for (ClientFeature feature : features.values()) {
            if (category != null && feature.category() != category) {
                continue;
            }

            String searchableText = (feature.name() + " " + feature.description() + " "
                    + feature.id() + " " + feature.category().displayName()).toLowerCase(Locale.ROOT);
            if (searchableText.contains(normalizedQuery)) {
                result.add(feature);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public void restoreEnabled(Set<String> enabledIds) {
        for (ClientFeature feature : features.values()) {
            feature.setEnabled(enabledIds.contains(feature.id()));
        }
    }
}
