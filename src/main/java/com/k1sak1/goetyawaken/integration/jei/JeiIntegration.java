package com.k1sak1.goetyawaken.integration.jei;

import net.minecraftforge.fml.ModList;

public final class JeiIntegration {
    private JeiIntegration() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded("jei");
    }

    public static void syncSearchText(String text) {
        if (isLoaded()) {
            try {
                Class<?> pluginClass = Class.forName("com.k1sak1.goetyawaken.integration.jei.GoetyAwakenJeiPlugin");
                java.lang.reflect.Method method = pluginClass.getMethod("syncSearchText", String.class);
                method.invoke(null, text);
            } catch (Exception e) {

            }
        }
    }
}
