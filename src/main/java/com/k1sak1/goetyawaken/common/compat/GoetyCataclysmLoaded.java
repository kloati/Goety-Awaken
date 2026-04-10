package com.k1sak1.goetyawaken.common.compat;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public enum GoetyCataclysmLoaded {
    INSTANCE("goety_cataclysm");

    private final boolean loaded;

    GoetyCataclysmLoaded(String modid) {
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}