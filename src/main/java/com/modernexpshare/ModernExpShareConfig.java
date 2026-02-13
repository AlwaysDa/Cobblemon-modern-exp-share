package com.modernexpshare;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModernExpShareConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "cobblemon_modern_exp_share.json";
    private static final String LEGACY_FILE_NAME = ModernExpShareMod.MOD_ID + ".json";

    private static volatile ModernExpShareConfig current = defaults();

    public double sharedExpMultiplier = 0.5;
    public double sharedEvMultiplier = 1.0;

    public static ModernExpShareConfig get() {
        return current;
    }

    public static void loadOrCreate(MinecraftServer server) {
        // Server-authoritative config (reads from config/ on dedicated or integrated server)
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDir.resolve(FILE_NAME);
        Path legacyConfigPath = configDir.resolve(LEGACY_FILE_NAME);

        ModernExpShareConfig loaded = null;
        Path loadedFromPath = null;
        Path writeBackToPath = configPath;

        if (Files.exists(configPath)) {
            loadedFromPath = configPath;
        } else if (Files.exists(legacyConfigPath)) {
            // Backward-compat: read legacy file if present
            loadedFromPath = legacyConfigPath;
        }

        if (loadedFromPath != null) {
            try (Reader reader = Files.newBufferedReader(loadedFromPath)) {
                loaded = GSON.fromJson(reader, ModernExpShareConfig.class);
            } catch (IOException | JsonSyntaxException e) {
                ModernExpShareMod.LOGGER.warn("Failed to read config {}, using defaults: {}", loadedFromPath, e.toString());
            }
        }

        if (loaded == null) {
            loaded = defaults();
        }

        // Always write the resolved config to the new path (and create file if missing).
        try {
            Files.createDirectories(writeBackToPath.getParent());
            try (Writer writer = Files.newBufferedWriter(writeBackToPath)) {
                GSON.toJson(loaded, writer);
            }
        } catch (IOException e) {
            ModernExpShareMod.LOGGER.warn("Failed to write config {}: {}", writeBackToPath, e.toString());
        }

        loaded.sharedExpMultiplier = clampNonNegative(loaded.sharedExpMultiplier, 0.5);
        loaded.sharedEvMultiplier = clampNonNegative(loaded.sharedEvMultiplier, 1.0);
        current = loaded;

        ModernExpShareMod.LOGGER.info("Config loaded: sharedExpMultiplier={}, sharedEvMultiplier={} ({})",
            current.sharedExpMultiplier, current.sharedEvMultiplier, writeBackToPath);
    }

    private static double clampNonNegative(double value, double fallback) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {
            return fallback;
        }
        return value;
    }

    private static ModernExpShareConfig defaults() {
        return new ModernExpShareConfig();
    }
}
