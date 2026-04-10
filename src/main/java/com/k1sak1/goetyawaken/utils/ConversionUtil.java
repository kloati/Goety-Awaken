package com.k1sak1.goetyawaken.utils;

import com.Polarice3.Goety.api.entities.IOwned;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import java.util.Collection;

public class ConversionUtil {

    @Nullable
    public static <T extends LivingEntity> T convertToServant(LivingEntity originalEntity, @Nullable Entity killer) {
        if (originalEntity.level().isClientSide() || !(originalEntity.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        if (!EntityMappingUtil.canBeConverted(originalEntity.getType())) {
            return null;
        }

        EntityType<?> servantType = EntityMappingUtil.getServantType(originalEntity.getType());
        if (servantType == null) {
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            T servantEntity = (T) servantType.create(serverLevel);
            if (servantEntity == null) {
                return null;
            }

            servantEntity.moveTo(originalEntity.getX(), originalEntity.getY(), originalEntity.getZ(),
                    originalEntity.getYRot(), originalEntity.getXRot());

            inheritEquipment(originalEntity, servantEntity);
            inheritPotionEffects(originalEntity, servantEntity);
            inheritBasicAttributes(originalEntity, servantEntity);

            if (servantEntity instanceof IOwned ownedServant && killer instanceof LivingEntity livingKiller) {
                ownedServant.setOwnerId(livingKiller.getUUID());
            }

            if (servantEntity instanceof Mob mobServant) {
                mobServant.finalizeSpawn(
                        serverLevel,
                        serverLevel.getCurrentDifficultyAt(servantEntity.blockPosition()),
                        MobSpawnType.MOB_SUMMONED,
                        null,
                        null);
            }

            if (serverLevel.addFreshEntity(servantEntity)) {
                return servantEntity;
            }

        } catch (Exception e) {
            originalEntity.level().getServer().sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("转换失败: " + e.getMessage()));
        }

        return null;
    }

    private static void inheritEquipment(LivingEntity original, LivingEntity servant) {
        ItemStack mainHandItem = original.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!mainHandItem.isEmpty()) {
            servant.setItemSlot(EquipmentSlot.MAINHAND, mainHandItem.copy());
        }

        ItemStack offHandItem = original.getItemBySlot(EquipmentSlot.OFFHAND);
        if (!offHandItem.isEmpty()) {
            servant.setItemSlot(EquipmentSlot.OFFHAND, offHandItem.copy());
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack armorItem = original.getItemBySlot(slot);
                if (!armorItem.isEmpty()) {
                    servant.setItemSlot(slot, armorItem.copy());
                }
            }
        }

        if (servant instanceof Mob mobServant && original instanceof Mob originalMob) {
            mobServant.setDropChance(EquipmentSlot.MAINHAND,
                    com.Polarice3.Goety.utils.MobUtil.getEquipmentDropChance(originalMob, EquipmentSlot.MAINHAND));
            mobServant.setDropChance(EquipmentSlot.OFFHAND,
                    com.Polarice3.Goety.utils.MobUtil.getEquipmentDropChance(originalMob, EquipmentSlot.OFFHAND));
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    mobServant.setDropChance(slot,
                            com.Polarice3.Goety.utils.MobUtil.getEquipmentDropChance(originalMob, slot));
                }
            }
        }
    }

    private static void inheritPotionEffects(LivingEntity original, LivingEntity servant) {
        Collection<MobEffectInstance> effects = original.getActiveEffects();
        for (MobEffectInstance effect : effects) {
            MobEffectInstance newEffect = new MobEffectInstance(
                    effect.getEffect(),
                    effect.getDuration(),
                    effect.getAmplifier(),
                    effect.isAmbient(),
                    effect.isVisible(),
                    effect.showIcon());
            servant.addEffect(newEffect);
        }
    }

    private static void inheritBasicAttributes(LivingEntity original, LivingEntity servant) {
        if (original.hasCustomName()) {
            servant.setCustomName(original.getCustomName());
        }

        if (original.isOnFire()) {
            servant.setRemainingFireTicks(original.getRemainingFireTicks());
        }

        if (original.isInvisible()) {
            servant.setInvisible(true);
        }

        if (original.hasGlowingTag()) {
            servant.setGlowingTag(true);
        }
    }

    public static boolean canConvert(LivingEntity originalEntity, @Nullable Entity killer) {
        if (originalEntity.level().isClientSide()) {
            return false;
        }

        if (!(killer instanceof LivingEntity)) {
            return false;
        }

        if (originalEntity.isRemoved()) {
            return false;
        }

        return EntityMappingUtil.canBeConverted(originalEntity.getType());
    }
}