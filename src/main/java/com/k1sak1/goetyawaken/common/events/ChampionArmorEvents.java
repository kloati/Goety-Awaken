package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.items.armor.ChampionArmorItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChampionArmorEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void championArmorDamageReduce(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        float damageAmount = event.getAmount();
        float totalReduction = 0;

        for (EquipmentSlot slot : ChampionArmorItem.getArmorSlots()) {
            ItemStack stack = target.getItemBySlot(slot);
            if (stack.getItem() instanceof ChampionArmorItem armorItem) {

                float reduction = calculateReduction(armorItem, event.getSource());
                totalReduction += damageAmount * reduction;
            }
        }

        if (totalReduction > 0) {
            float newDamage = Math.max(0, damageAmount - totalReduction);
            event.setAmount(newDamage);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void championArmorFallImmunity(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.getSource().is(DamageTypeTags.IS_FALL)) {
            int setCount = getChampionArmorSetCount(entity);
            if (setCount >= 4) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void championArmorSetEffect(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide) {
            return;
        }

        int setCount = getChampionArmorSetCount(living);

        if (setCount >= 2) {
            if (living.tickCount % 20 == 0 && living.getHealth() > 0.0F) {
                int effectLevel = setCount >= 4 ? 2 : 1;
                living.addEffect(new MobEffectInstance(
                        GoetyEffects.SHIELDING.get(),
                        40,
                        effectLevel - 1,
                        false,
                        false));
            }
        }
    }

    private static float calculateReduction(ChampionArmorItem armorItem,
            net.minecraft.world.damagesource.DamageSource source) {
        if (isMagicDamage(source)) {
            return armorItem.getMagicResistance();
        } else if (isFireDamage(source)) {
            return armorItem.getFireResistance();
        } else if (isExplosionDamage(source)) {
            return armorItem.getExplosionResistance();
        } else if (isProjectileDamage(source)) {
            return armorItem.getProjectileResistance();
        }

        return 0.0F;
    }

    private static boolean isMagicDamage(net.minecraft.world.damagesource.DamageSource source) {
        return source.is(DamageTypeTags.WITCH_RESISTANT_TO) ||
                source.is(DamageTypes.MAGIC) ||
                source.is(DamageTypes.INDIRECT_MAGIC) ||
                source.is(DamageTypes.DRAGON_BREATH) ||
                source.is(DamageTypes.WITHER);
    }

    private static boolean isFireDamage(net.minecraft.world.damagesource.DamageSource source) {
        return source.is(DamageTypeTags.IS_FIRE) ||
                source.is(DamageTypes.IN_FIRE) ||
                source.is(DamageTypes.ON_FIRE) ||
                source.is(DamageTypes.LAVA) ||
                source.is(DamageTypes.HOT_FLOOR);
    }

    private static boolean isExplosionDamage(net.minecraft.world.damagesource.DamageSource source) {
        return source.is(DamageTypeTags.IS_EXPLOSION) ||
                source.is(DamageTypes.PLAYER_EXPLOSION) ||
                source.is(DamageTypes.EXPLOSION);
    }

    private static boolean isProjectileDamage(net.minecraft.world.damagesource.DamageSource source) {
        return source.is(DamageTypeTags.IS_PROJECTILE) ||
                source.is(DamageTypes.ARROW) ||
                source.is(DamageTypes.TRIDENT) ||
                source.is(DamageTypes.MOB_PROJECTILE) ||
                source.is(DamageTypes.FIREBALL) ||
                source.is(DamageTypes.WITHER_SKULL) ||
                source.is(DamageTypes.THROWN);
    }

    private static int getChampionArmorSetCount(LivingEntity living) {
        int count = 0;
        ItemStack helmet = living.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof ArmorItem helmetArmor &&
                helmetArmor.getMaterial() == com.k1sak1.goetyawaken.common.items.armor.ChampionArmorMaterial.CHAMPION) {
            count++;
        } else {
            return 0;
        }

        ItemStack chestplate = living.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.getItem() instanceof ArmorItem chestplateArmor &&
                chestplateArmor
                        .getMaterial() == com.k1sak1.goetyawaken.common.items.armor.ChampionArmorMaterial.CHAMPION) {
            count++;
        }

        ItemStack leggings = living.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.getItem() instanceof ArmorItem leggingsArmor &&
                leggingsArmor
                        .getMaterial() == com.k1sak1.goetyawaken.common.items.armor.ChampionArmorMaterial.CHAMPION) {
            count++;
        }

        ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof ArmorItem bootsArmor &&
                bootsArmor.getMaterial() == com.k1sak1.goetyawaken.common.items.armor.ChampionArmorMaterial.CHAMPION) {
            count++;
        }

        return count;
    }
}
