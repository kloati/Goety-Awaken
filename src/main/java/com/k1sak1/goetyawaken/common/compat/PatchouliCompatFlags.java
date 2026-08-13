package com.k1sak1.goetyawaken.common.compat;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import vazkii.patchouli.api.PatchouliAPI;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.MOD)
public class PatchouliCompatFlags {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        registerFlag(ModLoadedUtil.TOUHOU_LITTLE_MAID, "touhou_little_maid_loaded");
        registerFlag(ModLoadedUtil.MASQUERADER, "masquerader_mod_loaded");
        registerFlag(ModLoadedUtil.MEET_YOUR_FIGHT, "meetyourfight_loaded");
        registerFlag(ModLoadedUtil.TAKES_A_PILLAGE, "takesapillage_loaded");
        registerFlag(ModLoadedUtil.DEEPER_DARKER, "deeperdarker_loaded");
    }

    private static void registerFlag(String modId, String flagKey) {
        if (ModLoadedUtil.isModLoaded(modId)) {
            PatchouliAPI.get().setConfigFlag("goetyawaken:" + flagKey, true);
        }
    }
}
