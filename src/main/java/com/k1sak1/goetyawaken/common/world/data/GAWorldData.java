package com.k1sak1.goetyawaken.common.world.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class GAWorldData extends SavedData {

    private static final String IDENTIFIER = "goety_awaken_world_data";
    private boolean WitherNecromancerDefeatedOnce = false;

    private GAWorldData() {
        super();
    }

    public static GAWorldData get(Level world, ResourceKey<Level> dim) {
        if (world instanceof ServerLevel) {
            ServerLevel overworld = world.getServer().getLevel(dim);
            DimensionDataStorage storage = overworld.getDataStorage();
            GAWorldData data = storage.computeIfAbsent(GAWorldData::load, GAWorldData::new, IDENTIFIER);
            if (data != null) {
                data.setDirty();
            }
            return data;
        }
        return null;
    }

    public static GAWorldData load(CompoundTag nbt) {
        GAWorldData data = new GAWorldData();
        data.WitherNecromancerDefeatedOnce = nbt.getBoolean("WitherNecromancerDefeatedOnce");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putBoolean("WitherNecromancerDefeatedOnce", WitherNecromancerDefeatedOnce);
        return compound;
    }

    public boolean isWitherNecromancerDefeatedOnce() {
        return WitherNecromancerDefeatedOnce;
    }

    public void setWitherNecromancerDefeatedOnce(boolean defeatedOnce) {
        this.WitherNecromancerDefeatedOnce = defeatedOnce;
    }
}
