package com.k1sak1.goetyawaken.integration.pinyin;

import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

public final class PinyinIntegration {
    private static final String JECH_MOD_ID = "jecharacters";
    private static final String MATCH_CLASS = "me.towdium.jecharacters.utils.Match";

    private static boolean initialized = false;
    private static boolean available = false;
    private static Method containsMethod;

    private PinyinIntegration() {
    }

    private static void ensureInit() {
        if (initialized) {
            return;
        }
        initialized = true;

        boolean modPresent;
        try {
            modPresent = ModList.get() != null && ModList.get().isLoaded(JECH_MOD_ID);
        } catch (Throwable ignored) {
            modPresent = false;
        }

        if (!modPresent) {
            return;
        }

        try {
            Class<?> matchClass = Class.forName(MATCH_CLASS);
            containsMethod = matchClass.getMethod("contains", String.class, CharSequence.class);
            available = true;
        } catch (Throwable ignored) {
            available = false;
            containsMethod = null;
        }
    }

    public static boolean isAvailable() {
        ensureInit();
        return available;
    }

    public static boolean contains(String haystack, String needle) {
        if (needle == null || needle.isEmpty()) {
            return true;
        }
        if (haystack == null) {
            return false;
        }

        ensureInit();
        if (available && containsMethod != null) {
            try {
                Object result = containsMethod.invoke(null, haystack, needle);
                if (result instanceof Boolean b) {
                    return b;
                }
            } catch (Throwable ignored) {

            }
        }

        return haystack.toLowerCase().contains(needle.toLowerCase());
    }
}
