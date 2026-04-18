package com.k1sak1.goetyawaken.utils;

import net.minecraftforge.fml.ModList;

/**
 * Inspired by
 * <a href=
 * "https://github.com/Mega32K/ending_library/?tab=readme-ov-file">ending_library</a>
 * project.
 * Original author: MegaDarkness
 * </p>
 */
public class SafeClass {
    private static int modernuiLoaded = -1;
    private static int oculusLoaded = -1;

    public static boolean isModernUILoaded() {
        if (modernuiLoaded == -1) {
            modernuiLoaded = ModList.get().isLoaded("modernui") ? 1 : 2;
        }
        return modernuiLoaded == 1;
    }

    public static boolean isOculusLoaded() {
        if (oculusLoaded == -1) {
            oculusLoaded = ModList.get().isLoaded("oculus") ? 1 : 2;
        }
        return oculusLoaded == 1;
    }

    public static boolean usingShaderPack() {
        if (isOculusLoaded()) {
            return net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse();
        }
        return false;
    }
}
