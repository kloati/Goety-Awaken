package com.k1sak1.goetyawaken.common.compat.touhoulittlemaid;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public enum TouhouLittleMaidLoaded {
    TOUHOULITTLEMAID("touhou_little_maid");

    private final boolean loaded;

    TouhouLittleMaidLoaded(String modid) {
        this.loaded = ModList.get() != null && ModList.get().getModContainerById(modid).isPresent();
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}