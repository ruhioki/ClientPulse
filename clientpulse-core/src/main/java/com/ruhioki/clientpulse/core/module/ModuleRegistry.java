package com.ruhioki.clientpulse.core.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import com.ruhioki.clientpulse.core.setting.BooleanSetting;
import com.ruhioki.clientpulse.core.setting.ColorSetting;
import com.ruhioki.clientpulse.core.setting.EnumSetting;
import com.ruhioki.clientpulse.core.setting.KeybindSetting;
import com.ruhioki.clientpulse.core.setting.NumberSetting;
import com.ruhioki.clientpulse.core.setting.Setting;

public final class ModuleRegistry {
    private final Map<String, ClientModule> modules = new LinkedHashMap<String, ClientModule>();

    public static ModuleRegistry createDefault() {
        ModuleRegistry registry = new ModuleRegistry();

        ClientModule fps = registry.register(new ClientModule(
                "fps-hud", "FPS Counter", "Show the current frame rate.", ModuleCategory.HUD));
        fps.addSetting(new ColorSetting("color", "Text color", "RGB color in hexadecimal.", 0x74B7FF));
        fps.addSetting(new NumberSetting("scale", "Text scale", "Scale from 0.75x to 1.50x.",
                0.75, 1.50, 0.05, 1.0));
        addHudPosition(fps, 2, 2);

        ClientModule coordinates = registry.register(new ClientModule(
                "coordinates-hud", "Coordinates", "Show your current world position.", ModuleCategory.HUD));
        coordinates.addSetting(new ColorSetting("color", "Text color", "RGB color in hexadecimal.", 0xFFFFFF));
        coordinates.addSetting(new EnumSetting("precision", "Precision", "Coordinate decimal places.",
                options("Integer", "One decimal", "Two decimals"), "One decimal"));
        addHudPosition(coordinates, 2, 5);

        ClientModule session = registry.register(new ClientModule(
                "session-hud", "Session Info", "Show the current dimension.", ModuleCategory.HUD));
        session.addSetting(new ColorSetting("color", "Text color", "RGB color in hexadecimal.", 0xC2A7FF));
        addHudPosition(session, 2, 8);

        ClientModule memory = registry.register(new ClientModule(
                "memory-hud", "Memory Usage", "Show JVM memory usage in megabytes.", ModuleCategory.PERFORMANCE));
        memory.addSetting(new ColorSetting("color", "Text color", "RGB color in hexadecimal.", 0x64DDB2));
        addHudPosition(memory, 2, 11);

        ClientModule interfaceTheme = registry.register(new ClientModule(
                "interface-theme", "Interface Theme", "Customize the Click GUI appearance.", ModuleCategory.VISUAL));
        interfaceTheme.setEnabled(true);
        interfaceTheme.addSetting(new KeybindSetting(
                "open-key", "Open key", "Click to capture the Click GUI shortcut.", 54));
        interfaceTheme.addSetting(new EnumSetting("theme", "Theme", "Choose a panel palette.",
                options("Dark", "Midnight", "Blue", "Violet", "RGB"), "Dark"));
        interfaceTheme.addSetting(new ColorSetting("accent", "Custom accent", "RGB color in #RRGGBB format.",
                0x6CAEFF));
        interfaceTheme.addSetting(new BooleanSetting(
                "animations", "Animations", "Enable GUI motion and hover effects.", true));
        interfaceTheme.addSetting(new NumberSetting("animation-duration", "Animation duration",
                "Transition duration in milliseconds.", 80.0, 600.0, 20.0, 220.0));
        return registry;
    }

    public ClientModule register(ClientModule module) {
        Objects.requireNonNull(module, "module");
        if (modules.putIfAbsent(module.id(), module) != null) {
            throw new IllegalArgumentException("Duplicate module id: " + module.id());
        }
        return module;
    }

    public ClientModule get(String id) {
        ClientModule module = modules.get(id);
        if (module == null) {
            throw new IllegalArgumentException("Unknown module id: " + id);
        }
        return module;
    }

    public boolean contains(String id) {
        return modules.containsKey(id);
    }

    public List<ClientModule> all() {
        return Collections.unmodifiableList(new ArrayList<ClientModule>(modules.values()));
    }

    public List<ClientModule> filter(ModuleCategory category, String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<ClientModule> result = new ArrayList<ClientModule>();
        for (ClientModule module : modules.values()) {
            if (category != null && module.category() != category) {
                continue;
            }
            String searchable = (module.id() + " " + module.name() + " "
                    + module.description() + " " + module.category().displayName()).toLowerCase(Locale.ROOT);
            if (searchable.contains(normalized)) {
                result.add(module);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public int toggleBoundModules(int keyCode) {
        if (keyCode <= 0) {
            return 0;
        }
        int toggled = 0;
        for (ClientModule module : modules.values()) {
            if (module.keybind().keyCode() == keyCode) {
                module.toggle();
                toggled++;
            }
        }
        return toggled;
    }

    public void save(Properties properties) {
        for (ClientModule module : modules.values()) {
            String prefix = propertyPrefix(module);
            properties.setProperty(prefix + "enabled", Boolean.toString(module.enabled()));
            for (Setting<?> setting : module.settings()) {
                properties.setProperty(prefix + setting.id(), setting.serialize());
            }
        }
    }

    public List<String> restore(Properties properties) {
        List<String> warnings = new ArrayList<String>();
        for (ClientModule module : modules.values()) {
            String prefix = propertyPrefix(module);
            String enabledValue = properties.getProperty(prefix + "enabled");
            if (enabledValue != null) {
                if ("true".equalsIgnoreCase(enabledValue) || "false".equalsIgnoreCase(enabledValue)) {
                    module.setEnabled(Boolean.parseBoolean(enabledValue));
                } else {
                    warnings.add("Invalid enabled state for module " + module.id() + ".");
                }
            }
            for (Setting<?> setting : module.settings()) {
                String serialized = properties.getProperty(prefix + setting.id());
                if (serialized != null && !setting.restore(serialized)) {
                    warnings.add("Invalid value for setting " + module.id() + "." + setting.id() + ".");
                }
            }
        }
        return Collections.unmodifiableList(warnings);
    }

    private static String propertyPrefix(ClientModule module) {
        return "module." + module.id() + ".";
    }

    private static List<String> options(String... options) {
        List<String> result = new ArrayList<String>(options.length);
        Collections.addAll(result, options);
        return result;
    }

    private static void addHudPosition(ClientModule module, int x, int y) {
        module.addSetting(new NumberSetting("x-position", "Horizontal position",
                "Distance from the left edge in percent.", 0.0, 100.0, 1.0, x));
        module.addSetting(new NumberSetting("y-position", "Vertical position",
                "Distance from the top edge in percent.", 0.0, 100.0, 1.0, y));
    }
}
