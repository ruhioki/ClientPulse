package com.ruhioki.clientpulse.core.feature;

import java.util.Objects;

public final class ClientFeature {
    private final String id;
    private final String name;
    private final String description;
    private final FeatureCategory category;
    private boolean enabled;

    public ClientFeature(String id, String name, String description, FeatureCategory category) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.category = Objects.requireNonNull(category, "category");
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public FeatureCategory category() {
        return category;
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
