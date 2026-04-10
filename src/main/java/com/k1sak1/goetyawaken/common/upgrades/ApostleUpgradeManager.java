package com.k1sak1.goetyawaken.common.upgrades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.ConcurrentHashMap;

public class ApostleUpgradeManager {
    private static final String APOSTLE_UPGRADE_DATA_KEY = "ApostleUpgradeData";
    private static final ConcurrentHashMap<LivingEntity, ApostleUpgradeData> entityUpgradeData = new ConcurrentHashMap<>();

    public static ApostleUpgradeData getUpgradeData(LivingEntity entity) {
        ApostleUpgradeData data = entityUpgradeData.get(entity);
        if (data == null) {
            CompoundTag entityTag = entity.getPersistentData();
            if (entityTag.contains(APOSTLE_UPGRADE_DATA_KEY)) {
                data = ApostleUpgradeData.loadNBT(entityTag.getCompound(APOSTLE_UPGRADE_DATA_KEY));
                entityUpgradeData.put(entity, data);
            } else {
                data = new ApostleUpgradeData();
                entityUpgradeData.put(entity, data);
            }
        }
        return data;
    }

    public static void saveUpgradeData(LivingEntity entity) {
        ApostleUpgradeData data = entityUpgradeData.get(entity);
        if (data != null) {
            CompoundTag entityTag = entity.getPersistentData();
            entityTag.put(APOSTLE_UPGRADE_DATA_KEY, data.saveNBT());
            if (!entity.level().isClientSide && entity instanceof net.minecraft.world.entity.Mob mob) {
                mob.setPersistenceRequired();
            }
        }
    }

    public static void markEntityForUpgrade(LivingEntity entity, Player player) {
        ApostleUpgradeData data = getUpgradeData(entity);
        data.setMarkedForUpgrade(true);
        data.setMarkedBy(player.getUUID());

        saveUpgradeData(entity);
        if (!entity.level().isClientSide && entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.setPersistenceRequired();
        }
    }

    public static boolean isMarkedForUpgrade(LivingEntity entity) {
        ApostleUpgradeData data = getUpgradeData(entity);
        return data.isMarkedForUpgrade();
    }

    public static boolean isUpgraded(LivingEntity entity) {
        ApostleUpgradeData data = getUpgradeData(entity);
        return data.isUpgraded();
    }

    public static void completeUpgrade(LivingEntity entity, int titleNumber) {
        ApostleUpgradeData data = getUpgradeData(entity);
        data.setUpgraded(true);
        data.setTitleNumber(titleNumber);
        saveUpgradeData(entity);
        entity.getPersistentData();
        if (!entity.level().isClientSide && entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.setPersistenceRequired();
        }
    }

    public static void clearUpgradeData(LivingEntity entity) {
        entityUpgradeData.remove(entity);
        CompoundTag entityTag = entity.getPersistentData();
        if (entityTag.contains(APOSTLE_UPGRADE_DATA_KEY)) {
            entityTag.remove(APOSTLE_UPGRADE_DATA_KEY);
        }
    }
}