package com.ruhioki.clientpulse.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;

import com.ruhioki.clientpulse.core.feature.ClientFeature;
import com.ruhioki.clientpulse.core.feature.FeatureRegistry;

public final class ClientPulseConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path path;
    private final Logger logger;

    public ClientPulseConfig(Path path, Logger logger) {
        this.path = path;
        this.logger = logger;
    }

    public void load(FeatureRegistry registry) {
        if (!Files.exists(path)) {
            return;
        }

        try {
            JsonElement root = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), JsonElement.class);
            if (root == null || !root.isJsonObject()) {
                throw new JsonParseException("Configuration root must be a JSON object");
            }

            JsonElement featuresElement = root.getAsJsonObject().get("features");
            if (featuresElement == null || !featuresElement.isJsonObject()) {
                throw new JsonParseException("Configuration must contain a features object");
            }

            Set<String> enabledIds = new HashSet<>();
            for (Map.Entry<String, JsonElement> entry : featuresElement.getAsJsonObject().entrySet()) {
                JsonElement value = entry.getValue();
                if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
                    throw new JsonParseException("Feature state must be a boolean: " + entry.getKey());
                }
                if (value.getAsBoolean() && registry.contains(entry.getKey())) {
                    enabledIds.add(entry.getKey());
                }
            }
            registry.restoreEnabled(enabledIds);
        } catch (IOException | JsonParseException exception) {
            logger.warn("Could not load ClientPulse settings from {}. Default feature states will be used.",
                    path, exception);
        }
    }

    public boolean save(FeatureRegistry registry) {
        JsonObject root = new JsonObject();
        JsonObject featureStates = new JsonObject();
        for (ClientFeature feature : registry.all()) {
            featureStates.addProperty(feature.id(), feature.enabled());
        }
        root.add("features", featureStates);

        Path temporaryPath = path.resolveSibling(path.getFileName() + ".tmp");
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(temporaryPath, GSON.toJson(root), StandardCharsets.UTF_8);
            try {
                Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryPath, path, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException exception) {
            logger.error("Could not save ClientPulse settings to {}.", path, exception);
            try {
                Files.deleteIfExists(temporaryPath);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
                logger.warn("Could not remove temporary ClientPulse settings file {}.", temporaryPath, cleanupException);
            }
            return false;
        }
    }
}
