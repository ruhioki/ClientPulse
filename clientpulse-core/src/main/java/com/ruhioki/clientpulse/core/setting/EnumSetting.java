package com.ruhioki.clientpulse.core.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class EnumSetting extends Setting<String> {
    private final List<String> options;

    public EnumSetting(String id, String name, String description, List<String> options, String defaultValue) {
        super(id, name, description, defaultValue);
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Enum setting requires at least one option.");
        }
        ArrayList<String> copy = new ArrayList<String>(options.size());
        for (String option : options) {
            copy.add(Objects.requireNonNull(option, "option"));
        }
        this.options = Collections.unmodifiableList(copy);
        setValue(defaultValue);
    }

    public List<String> options() {
        return options;
    }

    public String next() {
        int index = options.indexOf(value());
        return options.get((index + 1) % options.size());
    }

    @Override
    public String serialize() {
        return value();
    }

    @Override
    protected String parse(String serialized) {
        return options.contains(serialized) ? serialized : null;
    }

    @Override
    protected String validate(String value) {
        Objects.requireNonNull(value, "value");
        if (options == null) {
            return value;
        }
        if (!options.contains(value)) {
            throw new IllegalArgumentException("Unsupported option: " + value);
        }
        return value;
    }
}
