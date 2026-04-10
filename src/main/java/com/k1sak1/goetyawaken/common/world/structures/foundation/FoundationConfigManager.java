package com.k1sak1.goetyawaken.common.world.structures.foundation;

import com.k1sak1.goetyawaken.Config;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class FoundationConfigManager {

    private static final Set<String> FOUNDATION_WHITELIST = new HashSet<>();

    public static void init() {
        FOUNDATION_WHITELIST.clear();
        for (String entry : Config.foundationWhitelist) {
            FOUNDATION_WHITELIST.add(entry);
        }
    }

    public static int getScanAbove() {
        return Config.foundationScanAbove;
    }

    public static int getScanBelow() {
        return Config.foundationScanBelow;
    }

    public static boolean isStructureWhitelisted(ResourceLocation id) {
        String full = id.toString();
        return FOUNDATION_WHITELIST.contains(full);
    }
}
