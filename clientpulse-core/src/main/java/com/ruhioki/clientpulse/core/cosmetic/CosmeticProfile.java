package com.ruhioki.clientpulse.core.cosmetic;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class CosmeticProfile {
    private static final int MIN_ANIMATION_DURATION = 200;
    private static final int MAX_ANIMATION_DURATION = 3000;
    private static final double MIN_SCALE = 0.5;
    private static final double MAX_SCALE = 1.5;

    private final Map<CosmeticCategory, String> selected =
            new EnumMap<CosmeticCategory, String>(CosmeticCategory.class);
    private final Set<String> favorites = new HashSet<String>();
    private boolean enabled;
    private boolean animationsEnabled = true;
    private int color = 0x65B8FF;
    private double scale = 1.0;
    private int animationDuration = 900;
    private double capeVerticalOffset;
    private double wingVerticalOffset;

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean animationsEnabled() {
        return animationsEnabled;
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        this.animationsEnabled = animationsEnabled;
    }

    public int color() {
        return color;
    }

    public void setColor(int color) {
        if (color < 0 || color > 0xFFFFFF) {
            throw new IllegalArgumentException("Cosmetic color must be RGB");
        }
        this.color = color;
    }

    public double scale() {
        return scale;
    }

    public void setScale(double scale) {
        if (Double.isNaN(scale) || Double.isInfinite(scale)
                || scale < MIN_SCALE || scale > MAX_SCALE) {
            throw new IllegalArgumentException("Cosmetic scale must be between 0.5 and 1.5");
        }
        this.scale = scale;
    }

    public int animationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        if (animationDuration < MIN_ANIMATION_DURATION || animationDuration > MAX_ANIMATION_DURATION) {
            throw new IllegalArgumentException("Animation duration must be between 200 and 3000 ms");
        }
        this.animationDuration = animationDuration;
    }

    public double capeVerticalOffset() {
        return capeVerticalOffset;
    }

    public void setCapeVerticalOffset(double offset) {
        capeVerticalOffset = validateOffset(offset);
    }

    public double wingVerticalOffset() {
        return wingVerticalOffset;
    }

    public void setWingVerticalOffset(double offset) {
        wingVerticalOffset = validateOffset(offset);
    }

    public void reset() {
        enabled = false;
        animationsEnabled = true;
        color = 0x65B8FF;
        scale = 1.0;
        animationDuration = 900;
        capeVerticalOffset = 0.0;
        wingVerticalOffset = 0.0;
        selected.clear();
        favorites.clear();
    }

    public String selected(CosmeticCategory category) {
        return selected.get(category);
    }

    public void select(CosmeticCategory category, String id) {
        if (category == null) {
            throw new IllegalArgumentException("Cosmetic category is required");
        }
        if (id == null || id.isEmpty()) {
            selected.remove(category);
            return;
        }
        CosmeticDefinition definition = CosmeticCatalog.find(id);
        if (definition == null || definition.category() != category) {
            throw new IllegalArgumentException("Cosmetic does not belong to the selected category");
        }
        selected.put(category, id);
    }

    public boolean isFavorite(String id) {
        return favorites.contains(id);
    }

    public void setFavorite(String id, boolean favorite) {
        if (CosmeticCatalog.find(id) == null) {
            throw new IllegalArgumentException("Unknown cosmetic: " + id);
        }
        if (favorite) {
            favorites.add(id);
        } else {
            favorites.remove(id);
        }
    }

    public List<String> restore(Properties properties) {
        CosmeticProfile restored = new CosmeticProfile();
        List<String> invalid = new ArrayList<String>();

        String value = properties.getProperty("cosmetics.enabled");
        if (value != null) {
            Boolean parsed = parseBoolean(value);
            if (parsed == null) {
                invalid.add("cosmetics.enabled");
            } else {
                restored.setEnabled(parsed.booleanValue());
            }
        }

        value = properties.getProperty("cosmetics.animations");
        if (value != null) {
            Boolean parsed = parseBoolean(value);
            if (parsed == null) {
                invalid.add("cosmetics.animations");
            } else {
                restored.setAnimationsEnabled(parsed.booleanValue());
            }
        }

        value = properties.getProperty("cosmetics.color");
        if (value != null) {
            try {
                restored.setColor(Integer.parseInt(value, 16));
            } catch (IllegalArgumentException exception) {
                invalid.add("cosmetics.color");
            }
        }

        value = properties.getProperty("cosmetics.scale");
        if (value != null) {
            try {
                restored.setScale(Double.parseDouble(value));
            } catch (IllegalArgumentException exception) {
                invalid.add("cosmetics.scale");
            }
        }

        value = properties.getProperty("cosmetics.animation-duration");
        if (value != null) {
            try {
                restored.setAnimationDuration(Integer.parseInt(value));
            } catch (IllegalArgumentException exception) {
                invalid.add("cosmetics.animation-duration");
            }
        }

        value = properties.getProperty("cosmetics.cape-y-offset");
        if (value != null) {
            try {
                restored.setCapeVerticalOffset(Double.parseDouble(value));
            } catch (IllegalArgumentException exception) {
                invalid.add("cosmetics.cape-y-offset");
            }
        }

        value = properties.getProperty("cosmetics.wings-y-offset");
        if (value != null) {
            try {
                restored.setWingVerticalOffset(Double.parseDouble(value));
            } catch (IllegalArgumentException exception) {
                invalid.add("cosmetics.wings-y-offset");
            }
        }

        for (CosmeticCategory category : CosmeticCategory.values()) {
            String id = properties.getProperty("cosmetics.selected." + category.name().toLowerCase());
            if (id != null) {
                try {
                    restored.select(category, id);
                } catch (IllegalArgumentException exception) {
                    invalid.add("cosmetics.selected." + category.name().toLowerCase());
                }
            }
        }

        value = properties.getProperty("cosmetics.favorites");
        if (value != null && !value.isEmpty()) {
            String[] ids = value.split(",", -1);
            for (String id : ids) {
                try {
                    restored.setFavorite(id, true);
                } catch (IllegalArgumentException exception) {
                    invalid.add("cosmetics.favorites");
                    break;
                }
            }
        }

        enabled = restored.enabled;
        animationsEnabled = restored.animationsEnabled;
        color = restored.color;
        scale = restored.scale;
        animationDuration = restored.animationDuration;
        capeVerticalOffset = restored.capeVerticalOffset;
        wingVerticalOffset = restored.wingVerticalOffset;
        selected.clear();
        selected.putAll(restored.selected);
        favorites.clear();
        favorites.addAll(restored.favorites);
        return invalid;
    }

    public void save(Properties properties) {
        properties.setProperty("cosmetics.schema", "1");
        properties.setProperty("cosmetics.enabled", Boolean.toString(enabled));
        properties.setProperty("cosmetics.animations", Boolean.toString(animationsEnabled));
        properties.setProperty("cosmetics.color", String.format("%06X", Integer.valueOf(color)));
        properties.setProperty("cosmetics.scale", Double.toString(scale));
        properties.setProperty("cosmetics.animation-duration", Integer.toString(animationDuration));
        properties.setProperty("cosmetics.cape-y-offset", Double.toString(capeVerticalOffset));
        properties.setProperty("cosmetics.wings-y-offset", Double.toString(wingVerticalOffset));
        for (CosmeticCategory category : CosmeticCategory.values()) {
            String id = selected.get(category);
            if (id != null) {
                properties.setProperty("cosmetics.selected." + category.name().toLowerCase(), id);
            }
        }
        List<String> sortedFavorites = new ArrayList<String>(favorites);
        java.util.Collections.sort(sortedFavorites);
        properties.setProperty("cosmetics.favorites", join(sortedFavorites));
    }

    private static Boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    private static double validateOffset(double offset) {
        if (Double.isNaN(offset) || Double.isInfinite(offset) || offset < -0.5 || offset > 0.5) {
            throw new IllegalArgumentException("Cosmetic vertical offset must be between -0.5 and 0.5");
        }
        return offset;
    }

    private static String join(List<String> values) {
        StringBuilder joined = new StringBuilder();
        for (String value : values) {
            if (joined.length() > 0) {
                joined.append(',');
            }
            joined.append(value);
        }
        return joined.toString();
    }
}
