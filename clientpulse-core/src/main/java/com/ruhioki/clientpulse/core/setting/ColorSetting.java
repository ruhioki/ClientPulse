package com.ruhioki.clientpulse.core.setting;

import java.util.Locale;

public final class ColorSetting extends Setting<Integer> {
    public ColorSetting(String id, String name, String description, int defaultRgb) {
        super(id, name, description, Integer.valueOf(defaultRgb & 0xFFFFFF));
    }

    public int rgb() {
        return value().intValue();
    }

    public String hexValue() {
        return String.format(Locale.ROOT, "#%06X", Integer.valueOf(rgb()));
    }

    @Override
    public String serialize() {
        return hexValue();
    }

    @Override
    protected Integer parse(String serialized) {
        String normalized = serialized.startsWith("#") ? serialized.substring(1) : serialized;
        if (normalized.length() != 6) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(normalized, 16));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    protected Integer validate(Integer value) {
        int rgb = value.intValue();
        if (rgb < 0 || rgb > 0xFFFFFF) {
            throw new IllegalArgumentException("RGB color must be between #000000 and #FFFFFF.");
        }
        return value;
    }
}
