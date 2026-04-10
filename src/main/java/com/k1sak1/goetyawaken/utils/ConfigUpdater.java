package com.k1sak1.goetyawaken.utils;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.k1sak1.goetyawaken.Config;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;

public class ConfigUpdater {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void updateGoetyApostleConfig() {
        if (!Config.callbackApostle) {
            LOGGER.info(
                    "Callback Apostle feature is disabled in GoetyAwaken config, skipping Goety Apostle config update");
            return;
        }
        LOGGER.info("Attempting to update Goety Apostle configuration");
        Path configDir = FMLPaths.CONFIGDIR.get();
        Path[] possiblePaths = {
                configDir.resolve("goety-mobs.toml"),
                configDir.resolve("goety" + File.separator + "goety-mobs.toml")
        };

        File goetyMobsConfigFile = null;
        Path goetyMobsConfigPath = null;

        for (Path path : possiblePaths) {
            if (path.toFile().exists()) {
                goetyMobsConfigFile = path.toFile();
                goetyMobsConfigPath = path;
                LOGGER.info("Found Goety config file at: {}", path.toString());
                break;
            }
        }

        if (goetyMobsConfigFile == null) {
            LOGGER.warn("No Goety config file found at any of the expected locations");
            LOGGER.warn("Expected paths:");
            for (Path path : possiblePaths) {
                LOGGER.warn("  - {}", path.toString());
            }
            return;
        }

        try {
            CommentedFileConfig config = CommentedFileConfig.builder(goetyMobsConfigFile)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();

            config.load();
            updateBooleanConfig(config, "apostleTornado", true);
            updateBooleanConfig(config, "apostleHardMagicResistance", true);
            updateBooleanConfig(config, "apostleQuickerRegen", true);
            updateBooleanConfig(config, "apostleResistance", true);

            config.save();
            config.close();

            LOGGER.info("Successfully updated Goety Apostle configuration");

        } catch (Exception e) {
            LOGGER.error("Failed to update Goety config file", e);
        }
    }

    private static void updateBooleanConfig(CommentedFileConfig config, String key, boolean newValue) {
        try {
            String fullKey = "Misc.Apostle." + key;
            Boolean currentValue = config.getOrElse(fullKey, false);
            if (currentValue != null && !currentValue) {
                config.set(fullKey, newValue);
                LOGGER.info("Updated config: {} changed from {} to {}", fullKey, false, newValue);
            } else if (currentValue != null) {
                LOGGER.info("Config {} is already {}, skipping update", fullKey, currentValue);
            } else {
                Boolean directValue = config.getOrElse(key, false);
                if (directValue != null && !directValue) {
                    config.set(key, newValue);
                } else {
                    config.set(fullKey, newValue);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to update config: {}", key, e);
        }
    }
}
