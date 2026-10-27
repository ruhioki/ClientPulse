package com.ruhioki.clientpulse.core.setting;

import java.util.Objects;

public abstract class Setting<T> {
    private final String id;
    private final String name;
    private final String description;
    private T value;

    protected Setting(String id, String name, String description, T defaultValue) {
        this.id = requireText(id, "id");
        this.name = requireText(name, "name");
        this.description = description == null ? "" : description;
        this.value = validate(Objects.requireNonNull(defaultValue, "defaultValue"));
    }

    public final String id() {
        return id;
    }

    public final String name() {
        return name;
    }

    public final String description() {
        return description;
    }

    public final T value() {
        return value;
    }

    public final void setValue(T value) {
        this.value = validate(Objects.requireNonNull(value, "value"));
    }

    public final boolean restore(String serialized) {
        if (serialized == null) {
            return false;
        }
        T parsed = parse(serialized);
        if (parsed == null) {
            return false;
        }
        setValue(parsed);
        return true;
    }

    public abstract String serialize();

    protected abstract T parse(String serialized);

    protected abstract T validate(T value);

    private static String requireText(String value, String label) {
        Objects.requireNonNull(value, label);
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must not be empty.");
        }
        return value;
    }
}
