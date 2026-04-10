package com.k1sak1.goetyawaken.common.items.armor;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.function.Supplier;

public enum ChampionArmorMaterial implements ArmorMaterial {
    CHAMPION("champion", 36,
            Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
                p_266652_.put(ArmorItem.Type.BOOTS, 4);
                p_266652_.put(ArmorItem.Type.LEGGINGS, 7);
                p_266652_.put(ArmorItem.Type.CHESTPLATE, 9);
                p_266652_.put(ArmorItem.Type.HELMET, 4);
            }),
            30,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            2.0F,
            0.2F,
            () -> Ingredient.EMPTY);

    private static final int[] HEALTH_PER_SLOT = new int[] { 13, 15, 16, 11 };
    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util
            .make(new EnumMap<>(ArmorItem.Type.class), (p_266653_) -> {
                p_266653_.put(ArmorItem.Type.BOOTS, 48);
                p_266653_.put(ArmorItem.Type.LEGGINGS, 56);
                p_266653_.put(ArmorItem.Type.CHESTPLATE, 60);
                p_266653_.put(ArmorItem.Type.HELMET, 40);
            });
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final net.minecraft.sounds.SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    ChampionArmorMaterial(String name, int durabilityMultiplier,
            EnumMap<ArmorItem.Type, Integer> protectionFunctionForType, int enchantmentValue,
            net.minecraft.sounds.SoundEvent soundEvent, float toughness, float knockbackResistance,
            Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionFunctionForType = protectionFunctionForType;
        this.enchantmentValue = enchantmentValue;
        this.sound = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return HEALTH_FUNCTION_FOR_TYPE.get(type) * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.protectionFunctionForType.get(type);
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public net.minecraft.sounds.SoundEvent getEquipSound() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return GoetyAwaken.MODID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
