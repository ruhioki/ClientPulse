package com.ruhioki.clientpulse.core.cosmetic;

public final class CosmeticDefinition {
    private final String id;
    private final String name;
    private final CosmeticCategory category;

    public CosmeticDefinition(String id, String name, CosmeticCategory category) {
        if (id == null || id.isEmpty() || name == null || name.isEmpty() || category == null) {
            throw new IllegalArgumentException("Cosmetic id, name and category are required");
        }
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public CosmeticCategory category() {
        return category;
    }
}
