package com.k1sak1.goetyawaken.common.mobenchant;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.network.server.SMobEnchantSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumMap;
import java.util.Map;

public class MobEnchantCapability implements IMobEnchantable {

    private final LivingEntity entity;
    private final Map<MobEnchantType, Integer> enchantments;

    public MobEnchantCapability(LivingEntity entity) {
        this.entity = entity;
        this.enchantments = new EnumMap<>(MobEnchantType.class);
        for (MobEnchantType type : MobEnchantType.values()) {
            enchantments.put(type, 0);
        }
    }

    @Override
    public int getMobEnchantLevel(MobEnchantType enchantType) {
        return enchantments.getOrDefault(enchantType, 0);
    }

    @Override
    public void setMobEnchantLevel(MobEnchantType enchantType, int level) {
        int clampedLevel = Math.max(0, Math.min(level, enchantType.getMaxLevel()));
        enchantments.put(enchantType, clampedLevel);

        if (clampedLevel > 0) {
            applyEnchantmentEffect(enchantType, clampedLevel);
        } else {
            removeEnchantmentEffect(enchantType);
        }

        updateGlint();

        if (enchantType == MobEnchantType.HUGE) {
            if (!entity.level().isClientSide()) {
                SMobEnchantSyncPacket packet = new SMobEnchantSyncPacket(entity.getId(), enchantType, clampedLevel);
                GoetyAwaken.network.channel.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
            }
            entity.refreshDimensions();
        }
    }

    @Override
    public Map<MobEnchantType, Integer> getMobEnchants() {
        return new EnumMap<>(enchantments);
    }

    @Override
    public void setMobEnchants(Map<MobEnchantType, Integer> enchants) {
        for (MobEnchantType type : MobEnchantType.values()) {
            setMobEnchantLevel(type, 0);
        }
        for (Map.Entry<MobEnchantType, Integer> entry : enchants.entrySet()) {
            setMobEnchantLevel(entry.getKey(), entry.getValue());
        }
    }

    private void applyEnchantmentEffect(MobEnchantType type, int level) {
        if (!type.hasAttributeModifiers()) {
            return;
        }

        MobEnchantType.AttributeModifierInfo[] modifiers = type.getAttributeModifiers();
        for (MobEnchantType.AttributeModifierInfo modifierInfo : modifiers) {
            var attribute = entity.getAttribute(modifierInfo.getAttribute());
            if (attribute != null) {
                attribute.removeModifier(modifierInfo.getUuid());
                attribute.addPermanentModifier(
                        new AttributeModifier(
                                modifierInfo.getUuid(),
                                type.getName(),
                                modifierInfo.getAmountForLevel(level),
                                modifierInfo.getOperation()));
                if (modifierInfo.shouldHealEntity() && modifierInfo
                        .getAttribute() == net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH) {
                    entity.heal((float) entity
                            .getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH));
                }
            }
        }
    }

    private void removeEnchantmentEffect(MobEnchantType type) {
        if (!type.hasAttributeModifiers()) {
            return;
        }

        MobEnchantType.AttributeModifierInfo[] modifiers = type.getAttributeModifiers();
        for (MobEnchantType.AttributeModifierInfo modifierInfo : modifiers) {
            var attribute = entity.getAttribute(modifierInfo.getAttribute());
            if (attribute != null) {
                attribute.removeModifier(modifierInfo.getUuid());
            }
        }
    }

    private void updateGlint() {
        if (entity instanceof IAncientGlint glint) {
            if (glint.hasAncientGlint()) {
                return;
            }
            if (hasMobEnchantment()) {
                glint.setAncientGlint(true);
                glint.setGlintTextureType("enchant");
            }
        }
    }

    public float getProtectionPercentage() {
        int level = getMobEnchantLevel(MobEnchantType.PROTECTION);
        return Math.min(level * 0.04f, 0.80f);
    }

    public float getThornPercentage() {
        int level = getMobEnchantLevel(MobEnchantType.THORN);
        return level * 0.10f;
    }

    public boolean hasMobEnchantment() {
        for (int level : enchantments.values()) {
            if (level > 0) {
                return true;
            }
        }
        return false;
    }

    public int getMobEnchantCount() {
        int count = 0;
        for (int level : enchantments.values()) {
            if (level > 0) {
                count++;
            }
        }
        return count;
    }

    public void setMobEnchantLevelClientOnly(MobEnchantType enchantType, int level) {
        int clampedLevel = Math.max(0, Math.min(level, enchantType.getMaxLevel()));
        enchantments.put(enchantType, clampedLevel);
    }

    public void saveToNBT(CompoundTag compound) {
        IMobEnchantable.super.saveMobEnchantData(compound);
    }

    public void loadFromNBT(CompoundTag compound) {
        IMobEnchantable.super.loadMobEnchantData(compound);
    }
}
