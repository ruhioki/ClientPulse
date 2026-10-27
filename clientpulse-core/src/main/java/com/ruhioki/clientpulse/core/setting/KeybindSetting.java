package com.ruhioki.clientpulse.core.setting;

public final class KeybindSetting extends Setting<Integer> {
    public static final int UNBOUND = 0;
    private static final int MAX_KEY_CODE = 255;

    public KeybindSetting(String id, String name, String description, int defaultValue) {
        super(id, name, description, Integer.valueOf(defaultValue));
    }

    public int keyCode() {
        return value().intValue();
    }

    @Override
    public String serialize() {
        return Integer.toString(keyCode());
    }

    @Override
    protected Integer parse(String serialized) {
        try {
            int parsed = Integer.parseInt(serialized);
            return parsed >= UNBOUND && parsed <= MAX_KEY_CODE ? Integer.valueOf(parsed) : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    protected Integer validate(Integer value) {
        int keyCode = value.intValue();
        if (keyCode < UNBOUND || keyCode > MAX_KEY_CODE) {
            throw new IllegalArgumentException("Key code must be between 0 and " + MAX_KEY_CODE + ".");
        }
        return value;
    }
}
