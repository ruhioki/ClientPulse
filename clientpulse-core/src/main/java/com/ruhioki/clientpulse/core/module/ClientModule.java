package com.ruhioki.clientpulse.core.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ruhioki.clientpulse.core.setting.KeybindSetting;
import com.ruhioki.clientpulse.core.setting.Setting;

public class ClientModule {
    private final String id;
    private final String name;
    private final String description;
    private final ModuleCategory category;
    private final KeybindSetting keybind;
    private final List<Setting<?>> settings = new ArrayList<Setting<?>>();
    private boolean enabled;

    public ClientModule(String id, String name, String description, ModuleCategory category) {
        this.id = requireText(id, "id");
        this.name = requireText(name, "name");
        this.description = description == null ? "" : description;
        this.category = Objects.requireNonNull(category, "category");
        this.keybind = addSetting(new KeybindSetting(
                "keybind", "Keybind", "Press a key to toggle this module.", KeybindSetting.UNBOUND));
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

    public ModuleCategory category() {
        return category;
    }

    public KeybindSetting keybind() {
        return keybind;
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public <T extends Setting<?>> T addSetting(T setting) {
        Objects.requireNonNull(setting, "setting");
        for (Setting<?> current : settings) {
            if (current.id().equals(setting.id())) {
                throw new IllegalArgumentException("Duplicate setting id: " + setting.id());
            }
        }
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> settings() {
        return Collections.unmodifiableList(settings);
    }

    public Setting<?> setting(String id) {
        for (Setting<?> setting : settings) {
            if (setting.id().equals(id)) {
                return setting;
            }
        }
        throw new IllegalArgumentException("Unknown setting '" + id + "' for module '" + this.id + "'.");
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    private static String requireText(String value, String label) {
        Objects.requireNonNull(value, label);
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must not be empty.");
        }
        return value;
    }
}
