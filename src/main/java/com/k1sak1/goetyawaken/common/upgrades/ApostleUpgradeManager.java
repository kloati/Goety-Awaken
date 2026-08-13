package com.k1sak1.goetyawaken.common.upgrades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApostleUpgradeManager {
    private static final String APOSTLE_UPGRADE_DATA_KEY = "ApostleUpgradeData";
    private static final Map<UUID, ApostleUpgradeData> cache = new HashMap<>();

    public static ApostleUpgradeData getUpgradeData(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            ApostleUpgradeData cached = cache.get(entity.getUUID());
            if (cached != null) {
                return cached;
            }
        }
        CompoundTag entityTag = entity.getPersistentData();
        if (entityTag.contains(APOSTLE_UPGRADE_DATA_KEY)) {
            ApostleUpgradeData data = ApostleUpgradeData.loadNBT(entityTag.getCompound(APOSTLE_UPGRADE_DATA_KEY));
            if (!entity.level().isClientSide && data.isMarkedForUpgrade()) {
                cache.put(entity.getUUID(), data);
            }
            return data;
        }
        return new ApostleUpgradeData();
    }

    public static void saveUpgradeData(LivingEntity entity, ApostleUpgradeData data) {
        CompoundTag entityTag = entity.getPersistentData();
        entityTag.put(APOSTLE_UPGRADE_DATA_KEY, data.saveNBT());
        if (!entity.level().isClientSide) {
            if (data.isMarkedForUpgrade()) {
                cache.put(entity.getUUID(), data);
            } else {
                cache.remove(entity.getUUID());
            }
            if (entity instanceof Mob mob) {
                mob.setPersistenceRequired();
            }
            if (entity.level() instanceof ServerLevel serverLevel) {
                LevelChunk chunk = serverLevel.getChunkAt(entity.blockPosition());
                chunk.setUnsaved(true);
            }
        }
    }

    public static void saveUpgradeData(LivingEntity entity) {
        saveUpgradeData(entity, getUpgradeData(entity));
    }

    public static void markEntityForUpgrade(LivingEntity entity, Player player) {
        ApostleUpgradeData data = getUpgradeData(entity);
        data.setMarkedForUpgrade(true);
        data.setMarkedBy(player.getUUID());
        saveUpgradeData(entity, data);
    }

    public static boolean isMarkedForUpgrade(LivingEntity entity) {
        if (!entity.level().isClientSide && cache.containsKey(entity.getUUID())) {
            return true;
        }
        CompoundTag entityTag = entity.getPersistentData();
        if (!entityTag.contains(APOSTLE_UPGRADE_DATA_KEY)) {
            return false;
        }
        return getUpgradeData(entity).isMarkedForUpgrade();
    }

    public static boolean isUpgraded(LivingEntity entity) {
        return getUpgradeData(entity).isUpgraded();
    }

    public static void completeUpgrade(LivingEntity entity, int titleNumber) {
        ApostleUpgradeData data = getUpgradeData(entity);
        data.setUpgraded(true);
        data.setTitleNumber(titleNumber);
        saveUpgradeData(entity, data);
    }

    public static void clearUpgradeData(LivingEntity entity) {
        CompoundTag entityTag = entity.getPersistentData();
        if (entityTag.contains(APOSTLE_UPGRADE_DATA_KEY)) {
            entityTag.remove(APOSTLE_UPGRADE_DATA_KEY);
        }
        cache.remove(entity.getUUID());
    }

    public static void onEntityLeaveLevel(LivingEntity entity) {
        cache.remove(entity.getUUID());
    }

    public static void onServerStopping() {
        cache.clear();
    }

    public static int getCachedDataCount() {
        return cache.size();
    }
}
