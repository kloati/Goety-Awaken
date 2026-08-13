package com.k1sak1.goetyawaken.common.compat;

import net.minecraftforge.fml.ModList;

public class ModLoadedUtil {
    public static final String MEET_YOUR_FIGHT = "meetyourfight";
    public static final String MASQUERADER = "masquerader_mod";
    public static final String TOUHOU_LITTLE_MAID = "touhou_little_maid";
    public static final String GOETY_CATACLYSM = "goety_cataclysm";
    public static final String TAKES_A_PILLAGE = "takesapillage";
    public static final String DEEPER_DARKER = "deeperdarker";

    public static boolean isModLoaded(String modid) {
        return ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }
}
