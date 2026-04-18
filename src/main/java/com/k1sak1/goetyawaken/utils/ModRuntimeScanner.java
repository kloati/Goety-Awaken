package com.k1sak1.goetyawaken.utils;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModRuntimeScanner {
    private static final Set<String> DETECTED_MODS = new HashSet<>();
    private static volatile boolean initialized = false;

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            try {
                ModList modList = ModList.get();
                if (modList != null) {
                    modList.getModContainerById("");
                    modList.getMods().forEach(mod -> {
                        DETECTED_MODS.add(mod.getModId());
                    });
                }
            } catch (NoClassDefFoundError e) {
                scanViaFMLLoader();
            }
            initialized = true;
        } catch (Exception e) {
            DETECTED_MODS.clear();
            initialized = true;
        }
    }

    private static void scanViaFMLLoader() {
        try {
            var loadingModList = FMLLoader.getLoadingModList();
            if (loadingModList != null) {
                loadingModList.getMods().forEach(modInfo -> {
                    DETECTED_MODS.add(modInfo.getModId());
                });
            }
        } catch (Exception e) {
        }
    }

    public static boolean isModLoaded(String modId) {
        if (!initialized) {
            initialize();
        }
        return DETECTED_MODS.contains(modId);
    }

    public static Set<String> getAllModIds() {
        if (!initialized) {
            initialize();
        }
        return Collections.unmodifiableSet(DETECTED_MODS);
    }

    public static void reset() {
        DETECTED_MODS.clear();
        initialized = false;
    }
}
