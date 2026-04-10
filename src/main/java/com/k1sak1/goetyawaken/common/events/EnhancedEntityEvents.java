package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.common.entities.ally.Leapleaf;
import com.Polarice3.Goety.common.entities.ally.golem.SquallGolem;
import com.Polarice3.Goety.common.entities.ally.illager.PikerServant;
import com.Polarice3.Goety.common.entities.ally.spider.BroodMotherServant;
import com.Polarice3.Goety.common.entities.hostile.BroodMother;
import com.Polarice3.Goety.common.entities.hostile.illagers.Piker;
import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractBlastling;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractSnareling;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractWatchling;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnhancedEntityEvents {
    private static final Set<Integer> enhancedEntities = new HashSet<>();
    private static final UUID BLASTLING_DAMAGE_MODIFIER_UUID = UUID.fromString("cbbac6d5-7cc0-4b96-b7ba-35b02967f0bc");
    private static final UUID BLASTLING_HEALTH_MODIFIER_UUID = UUID.fromString("4289232c-04a8-49d5-bf1b-4e3a251e3b2a");
    private static final UUID SNARELING_DAMAGE_MODIFIER_UUID = UUID.fromString("8e7d1a3b-2f4c-4c9d-8e7d-1a3b2f4c9d8e");
    private static final UUID SNARELING_HEALTH_MODIFIER_UUID = UUID.fromString("9f8e2b4c-3a5d-5d0e-9f8e-2b4c3a5d0e9f");
    private static final UUID WATCHLING_DAMAGE_MODIFIER_UUID = UUID.fromString("a0f1d3e5-4a6b-6b1f-a0f1-d3e54a6b6b1f");
    private static final UUID WATCHLING_HEALTH_MODIFIER_UUID = UUID.fromString("b1c2e4f6-5c7d-7d2c-b1c2-e4f65c7d7d2c");
    private static final UUID SQUALLGOLEM_ARMOR_MODIFIER_UUID = UUID.fromString("c2d3f5a7-6e8f-8f3c-c2d3-f5a76e8f8f3c");
    private static final UUID SQUALLGOLEM_HEALTH_MODIFIER_UUID = UUID
            .fromString("d3e4a6b8-7a9b-9b4d-d3e4-a6b87a9b9b4d");
    private static final UUID LEAPLEAF_DAMAGE_MODIFIER_UUID = UUID.fromString("e4f5a7b9-8c0d-0d5e-e4f5-a7b98c0d0d5e");
    private static final UUID LEAPLEAF_HEALTH_MODIFIER_UUID = UUID.fromString("f5a6b8c0-9d1e-1e6f-f5a6-b8c09d1e1e6f");
    private static final UUID WILDFIRE_ARMOR_MODIFIER_UUID = UUID.fromString("a6b7c9d1-0e2f-2f7a-a6b7-c9d10e2f2f7a");
    private static final UUID WILDFIRE_HEALTH_MODIFIER_UUID = UUID.fromString("b7c8d0e2-1f3a-3a8b-b7c8-d0e21f3a3a8b");
    private static final UUID PIKER_ARMOR_MODIFIER_UUID = UUID.fromString("c8d9e1f3-2a4b-4b9c-c8d9-e1f32a4b4b9c");
    private static final UUID BROODMOTHER_BUFF_UUID = UUID.fromString("d9e0f2a4-3b5c-5c0d-d9e0-f2a43b5c5c0d");

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        if (enhancedEntities.contains(entity.getId())) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        if (Config.enableBlastlingEnhancement) {
            if (isBlastlingType(livingEntity)) {
                applyBlastlingEnhancement(livingEntity);
            }
        }
        if (Config.enableSnarelingEnhancement) {
            if (isSnarelingType(livingEntity)) {
                applySnarelingEnhancement(livingEntity);
            }
        }
        if (Config.enableWatchlingEnhancement) {
            if (isWatchlingType(livingEntity)) {
                applyWatchlingEnhancement(livingEntity);
            }
        }
        if (Config.enableSquallGolemEnhancement) {
            if (isSquallGolemType(livingEntity)) {
                applySquallGolemEnhancement(livingEntity);
            }
        }
        if (Config.enableLeapleafEnhancement) {
            if (isLeapleafType(livingEntity)) {
                applyLeapleafEnhancement(livingEntity);
            }
        }
        if (Config.enableWildfireEnhancement) {
            if (isWildfireType(livingEntity)) {
                applyWildfireEnhancement(livingEntity);
            }
        }
        if (Config.enablePikerEnhancement) {
            if (isPikerType(livingEntity)) {
                applyPikerEnhancement(livingEntity);
            }
        }
        if (Config.enableBroodMotherEnhancement) {
            if (isBroodMotherType(livingEntity)) {
                applyBroodMotherEnhancement(livingEntity);
            }
        }
    }

    private static boolean isBlastlingType(LivingEntity entity) {
        return entity instanceof AbstractBlastling;
    }

    private static boolean isSnarelingType(LivingEntity entity) {
        return entity instanceof AbstractSnareling;
    }

    private static boolean isWatchlingType(LivingEntity entity) {
        return entity instanceof AbstractWatchling;
    }

    private static boolean isSquallGolemType(LivingEntity entity) {
        return entity instanceof SquallGolem;
    }

    private static boolean isLeapleafType(LivingEntity entity) {
        return entity instanceof Leapleaf;
    }

    private static boolean isWildfireType(LivingEntity entity) {
        return entity instanceof Wildfire;
    }

    private static boolean isPikerType(LivingEntity entity) {
        return entity instanceof Piker || entity instanceof PikerServant;
    }

    private static boolean isBroodMotherType(LivingEntity entity) {
        return entity instanceof BroodMother || entity instanceof BroodMotherServant;
    }

    private static void applyBlastlingEnhancement(LivingEntity entity) {
        AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(BLASTLING_DAMAGE_MODIFIER_UUID) == null) {
            AttributeModifier damageModifier = new AttributeModifier(
                    BLASTLING_DAMAGE_MODIFIER_UUID,
                    "Blastling Enhancement Damage Bonus",
                    3.5D,
                    AttributeModifier.Operation.ADDITION);
            attackDamage.addPermanentModifier(damageModifier);
        }

        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(BLASTLING_HEALTH_MODIFIER_UUID) == null) {
            AttributeModifier healthModifier = new AttributeModifier(
                    BLASTLING_HEALTH_MODIFIER_UUID,
                    "Blastling Enhancement Health Bonus",
                    8.0D,
                    AttributeModifier.Operation.ADDITION);
            maxHealth.addPermanentModifier(healthModifier);
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + 8.0F));
        }

        enhancedEntities.add(entity.getId());
    }

    private static void applySnarelingEnhancement(LivingEntity entity) {
        AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(SNARELING_DAMAGE_MODIFIER_UUID) == null) {
            AttributeModifier damageModifier = new AttributeModifier(
                    SNARELING_DAMAGE_MODIFIER_UUID,
                    "Snareling Enhancement Damage Bonus",
                    6.0D,
                    AttributeModifier.Operation.ADDITION);
            attackDamage.addPermanentModifier(damageModifier);
        }

        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(SNARELING_HEALTH_MODIFIER_UUID) == null) {
            AttributeModifier healthModifier = new AttributeModifier(
                    SNARELING_HEALTH_MODIFIER_UUID,
                    "Snareling Enhancement Health Bonus",
                    14.0D,
                    AttributeModifier.Operation.ADDITION);
            maxHealth.addPermanentModifier(healthModifier);
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + 14.0F));
        }

        enhancedEntities.add(entity.getId());
    }

    private static void applyWatchlingEnhancement(LivingEntity entity) {
        AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(WATCHLING_DAMAGE_MODIFIER_UUID) == null) {
            AttributeModifier damageModifier = new AttributeModifier(
                    WATCHLING_DAMAGE_MODIFIER_UUID,
                    "Watchling Enhancement Damage Bonus",
                    2.0D,
                    AttributeModifier.Operation.ADDITION);
            attackDamage.addPermanentModifier(damageModifier);
        }
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(WATCHLING_HEALTH_MODIFIER_UUID) == null) {
            AttributeModifier healthModifier = new AttributeModifier(
                    WATCHLING_HEALTH_MODIFIER_UUID,
                    "Watchling Enhancement Health Bonus",
                    8.0D,
                    AttributeModifier.Operation.ADDITION);
            maxHealth.addPermanentModifier(healthModifier);
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + 8.0F));
        }

        enhancedEntities.add(entity.getId());
    }

    private static void applySquallGolemEnhancement(LivingEntity entity) {
        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(SQUALLGOLEM_ARMOR_MODIFIER_UUID) == null) {
            AttributeModifier armorModifier = new AttributeModifier(
                    SQUALLGOLEM_ARMOR_MODIFIER_UUID,
                    "Squall Golem Enhancement Armor Bonus",
                    6.0D,
                    AttributeModifier.Operation.ADDITION);
            armor.addPermanentModifier(armorModifier);
        }

        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(SQUALLGOLEM_HEALTH_MODIFIER_UUID) == null) {
            AttributeModifier healthModifier = new AttributeModifier(
                    SQUALLGOLEM_HEALTH_MODIFIER_UUID,
                    "Squall Golem Enhancement Health Bonus",
                    52.0D,
                    AttributeModifier.Operation.ADDITION);
            maxHealth.addPermanentModifier(healthModifier);
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + 52.0F));
        }

        enhancedEntities.add(entity.getId());
    }

    private static void applyLeapleafEnhancement(LivingEntity entity) {
        AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null && attackDamage.getModifier(LEAPLEAF_DAMAGE_MODIFIER_UUID) == null) {
            AttributeModifier damageModifier = new AttributeModifier(
                    LEAPLEAF_DAMAGE_MODIFIER_UUID,
                    "Leapleaf Enhancement Damage Bonus",
                    2.0D,
                    AttributeModifier.Operation.ADDITION);
            attackDamage.addPermanentModifier(damageModifier);
        }
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(LEAPLEAF_HEALTH_MODIFIER_UUID) == null) {
            AttributeModifier healthModifier = new AttributeModifier(
                    LEAPLEAF_HEALTH_MODIFIER_UUID,
                    "Leapleaf Enhancement Health Bonus",
                    30.0D,
                    AttributeModifier.Operation.ADDITION);
            maxHealth.addPermanentModifier(healthModifier);
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + 30.0F));
        }

        enhancedEntities.add(entity.getId());
    }

    private static void applyWildfireEnhancement(LivingEntity entity) {
        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(WILDFIRE_ARMOR_MODIFIER_UUID) == null) {
            AttributeModifier armorModifier = new AttributeModifier(
                    WILDFIRE_ARMOR_MODIFIER_UUID,
                    "Wildfire Enhancement Armor Bonus",
                    10.0D,
                    AttributeModifier.Operation.ADDITION);
            armor.addPermanentModifier(armorModifier);
        }
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(WILDFIRE_HEALTH_MODIFIER_UUID) == null) {
            AttributeModifier healthModifier = new AttributeModifier(
                    WILDFIRE_HEALTH_MODIFIER_UUID,
                    "Wildfire Enhancement Health Bonus",
                    25.0D,
                    AttributeModifier.Operation.ADDITION);
            maxHealth.addPermanentModifier(healthModifier);
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + 25.0F));
        }

        enhancedEntities.add(entity.getId());
    }

    private static void applyPikerEnhancement(LivingEntity entity) {
        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        if (armor != null && armor.getModifier(PIKER_ARMOR_MODIFIER_UUID) == null) {
            AttributeModifier armorModifier = new AttributeModifier(
                    PIKER_ARMOR_MODIFIER_UUID,
                    "Piker Enhancement Armor Bonus",
                    6.0D,
                    AttributeModifier.Operation.ADDITION);
            armor.addPermanentModifier(armorModifier);
        }

        enhancedEntities.add(entity.getId());
    }

    private static void applyBroodMotherEnhancement(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 0, false,
                false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0,
                false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, MobEffectInstance.INFINITE_DURATION, 0, false,
                false, false));

        enhancedEntities.add(entity.getId());
    }
}