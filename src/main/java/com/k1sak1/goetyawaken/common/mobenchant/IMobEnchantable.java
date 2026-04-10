package com.k1sak1.goetyawaken.common.mobenchant;

import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public interface IMobEnchantable {

    int getMobEnchantLevel(MobEnchantType enchantType);

    void setMobEnchantLevel(MobEnchantType enchantType, int level);

    Map<MobEnchantType, Integer> getMobEnchants();

    void setMobEnchants(Map<MobEnchantType, Integer> enchants);

    default MobEnchantCapability getMobEnchantCapabilityInstance() {
        return null;
    }

    default boolean hasMobEnchantment() {
        return !getMobEnchants().isEmpty();
    }

    default void clearMobEnchantments() {
        for (MobEnchantType type : MobEnchantType.values()) {
            setMobEnchantLevel(type, 0);
        }
    }

    default boolean hasMobEnchantment(MobEnchantType enchantType) {
        return getMobEnchantLevel(enchantType) > 0;
    }

    default void saveMobEnchantData(CompoundTag compound) {
        CompoundTag enchantTag = new CompoundTag();
        for (MobEnchantType type : MobEnchantType.values()) {
            int level = getMobEnchantLevel(type);
            if (level > 0) {
                enchantTag.putInt(type.getName(), level);
            }
        }
        if (!enchantTag.isEmpty()) {
            compound.put("MobEnchantments", enchantTag);
        }
    }

    @SuppressWarnings("unchecked")
    default void loadMobEnchantData(CompoundTag compound) {
        clearMobEnchantments();
        if (compound.contains("MobEnchantments")) {
            CompoundTag enchantTag = compound.getCompound("MobEnchantments");
            for (MobEnchantType type : MobEnchantType.values()) {
                if (enchantTag.contains(type.getName())) {
                    int level = Math.min(enchantTag.getInt(type.getName()), type.getMaxLevel());
                    setMobEnchantLevel(type, level);
                }
            }
        }
    }
}
