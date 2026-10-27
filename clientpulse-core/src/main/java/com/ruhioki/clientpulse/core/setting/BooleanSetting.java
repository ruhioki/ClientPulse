package com.ruhioki.clientpulse.core.setting;

public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String id, String name, String description, boolean defaultValue) {
        super(id, name, description, Boolean.valueOf(defaultValue));
    }

    @Override
    public String serialize() {
        return value().toString();
    }

    @Override
    protected Boolean parse(String serialized) {
        if ("true".equalsIgnoreCase(serialized)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(serialized)) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    protected Boolean validate(Boolean value) {
        return value;
    }
}
