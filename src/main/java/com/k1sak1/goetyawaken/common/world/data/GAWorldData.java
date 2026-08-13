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
        if (world instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel targetLevel = serverLevel.getServer().getLevel(dim);
            if (targetLevel != null) {
                DimensionDataStorage storage = targetLevel.getDataStorage();
                return storage.computeIfAbsent(GAWorldData::load, GAWorldData::new, IDENTIFIER);
            }
        }
        return null;
    }

    public static GAWorldData getReadOnly(Level world, ResourceKey<Level> dim) {
        return get(world, dim);
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
        if (this.WitherNecromancerDefeatedOnce != defeatedOnce) {
            this.WitherNecromancerDefeatedOnce = defeatedOnce;
            this.setDirty();
        }
    }
}
