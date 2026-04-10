package com.k1sak1.goetyawaken.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttributeModifierManager {
    private static final UUID FRENZIED_ATTACK_SPEED_UUID = UUID.fromString("FA2389A2-9186-46AC-B896-C12AE9BD4B65");
    private static final UUID FRENZIED_MOVEMENT_SPEED_UUID = UUID.fromString("FB2389A2-1181-46AC-B896-C12AE9BD4B65");
    private static final UUID RAMPAGING_ATTACK_SPEED_UUID = UUID.fromString("FC2389A2-1116-46AC-B896-C12AE9BD4B65");
    private static final UUID BERSERK_ATTACK_SPEED_UUID = UUID.fromString("FD2389A2-1286-46AC-B116-C12AE9BD4A65");
    private static final UUID BERSERK_MOVEMENT_SPEED_UUID = UUID.fromString("FE2389A2-1486-46AC-B292-C12AE9AD4B65");
    private static final UUID MUCILAGE_ARMOR_UUID = UUID.fromString("FF2389A2-1586-46AC-B392-C12AE9BD4B65");
    private static final UUID MUCILAGE_ARMOR_TOUGHNESS_UUID = UUID.fromString("F02389A2-1686-46AC-B492-C12AE9BD4B65");
    private static final Map<UUID, AttributeModifier> activeModifiers = new HashMap<>();
    private static final Map<UUID, Long> rampagingModifierEndTime = new HashMap<>();

    /**
     * @param entity
     * @param amplifier
     */
    public static void applyFrenziedModifier(LivingEntity entity, int amplifier) {
        double speedBonus = (amplifier + 1) * 0.1;
        AttributeInstance attackSpeedAttribute = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            if (attackSpeedAttribute.getModifier(FRENZIED_ATTACK_SPEED_UUID) != null) {
                attackSpeedAttribute.removeModifier(FRENZIED_ATTACK_SPEED_UUID);
            }
            AttributeModifier attackSpeedModifier = new AttributeModifier(
                    FRENZIED_ATTACK_SPEED_UUID,
                    "Frenzied Attack Speed",
                    speedBonus,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
            attackSpeedAttribute.addTransientModifier(attackSpeedModifier);
            activeModifiers.put(FRENZIED_ATTACK_SPEED_UUID, attackSpeedModifier);
        }
        AttributeInstance movementSpeedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            if (movementSpeedAttribute.getModifier(FRENZIED_MOVEMENT_SPEED_UUID) != null) {
                movementSpeedAttribute.removeModifier(FRENZIED_MOVEMENT_SPEED_UUID);
            }
            AttributeModifier movementSpeedModifier = new AttributeModifier(
                    FRENZIED_MOVEMENT_SPEED_UUID,
                    "Frenzied Movement Speed",
                    speedBonus,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
            movementSpeedAttribute.addTransientModifier(movementSpeedModifier);
            activeModifiers.put(FRENZIED_MOVEMENT_SPEED_UUID, movementSpeedModifier);
        }
    }

    /**
     * @param entity
     */
    public static void removeFrenziedModifier(LivingEntity entity) {
        AttributeInstance attackSpeedAttribute = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null && attackSpeedAttribute.getModifier(FRENZIED_ATTACK_SPEED_UUID) != null) {
            attackSpeedAttribute.removeModifier(FRENZIED_ATTACK_SPEED_UUID);
        }

        AttributeInstance movementSpeedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null
                && movementSpeedAttribute.getModifier(FRENZIED_MOVEMENT_SPEED_UUID) != null) {
            movementSpeedAttribute.removeModifier(FRENZIED_MOVEMENT_SPEED_UUID);
        }

        activeModifiers.remove(FRENZIED_ATTACK_SPEED_UUID);
        activeModifiers.remove(FRENZIED_MOVEMENT_SPEED_UUID);
    }

    /**
     * @param entity
     * @param amplifier
     */
    public static void applyRampagingModifier(LivingEntity entity, int amplifier) {
        double speedBonus = 0.5;
        AttributeInstance attackSpeedAttribute = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            if (attackSpeedAttribute.getModifier(RAMPAGING_ATTACK_SPEED_UUID) != null) {
                attackSpeedAttribute.removeModifier(RAMPAGING_ATTACK_SPEED_UUID);
            }
            AttributeModifier attackSpeedModifier = new AttributeModifier(
                    RAMPAGING_ATTACK_SPEED_UUID,
                    "Rampaging Attack Speed",
                    speedBonus,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
            attackSpeedAttribute.addTransientModifier(attackSpeedModifier);
            activeModifiers.put(RAMPAGING_ATTACK_SPEED_UUID, attackSpeedModifier);
        }
    }

    /**
     * @param entity
     */
    public static void removeRampagingModifier(LivingEntity entity) {
        AttributeInstance attackSpeedAttribute = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null && attackSpeedAttribute.getModifier(RAMPAGING_ATTACK_SPEED_UUID) != null) {
            attackSpeedAttribute.removeModifier(RAMPAGING_ATTACK_SPEED_UUID);
        }

        activeModifiers.remove(RAMPAGING_ATTACK_SPEED_UUID);
        rampagingModifierEndTime.remove(entity.getUUID());
    }

    /**
     * @param entity
     */
    public static void checkAndRemoveExpiredRampagingModifiers(LivingEntity entity) {
        Long endTime = rampagingModifierEndTime.get(entity.getUUID());
        if (endTime != null && System.currentTimeMillis() > endTime) {
            removeRampagingModifier(entity);
        }
    }

    /**
     * @param entity
     * @param duration
     */
    public static void setRampagingModifierEndTime(LivingEntity entity, int duration) {
        long endTime = System.currentTimeMillis() + (duration * 50);
        rampagingModifierEndTime.put(entity.getUUID(), endTime);
    }

    /**
     * @param entity
     * @param amplifier
     */
    public static void applyBerserkModifier(LivingEntity entity, int amplifier) {
        double attackSpeedBonus = amplifier + 1;
        double movementSpeedBonus = (amplifier + 1) * 0.05;

        AttributeInstance attackSpeedAttribute = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            if (attackSpeedAttribute.getModifier(BERSERK_ATTACK_SPEED_UUID) != null) {
                attackSpeedAttribute.removeModifier(BERSERK_ATTACK_SPEED_UUID);
            }
            AttributeModifier attackSpeedModifier = new AttributeModifier(
                    BERSERK_ATTACK_SPEED_UUID,
                    "Berserk Attack Speed",
                    attackSpeedBonus,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
            attackSpeedAttribute.addTransientModifier(attackSpeedModifier);
            activeModifiers.put(BERSERK_ATTACK_SPEED_UUID, attackSpeedModifier);
        }

        AttributeInstance movementSpeedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            if (movementSpeedAttribute.getModifier(BERSERK_MOVEMENT_SPEED_UUID) != null) {
                movementSpeedAttribute.removeModifier(BERSERK_MOVEMENT_SPEED_UUID);
            }
            AttributeModifier movementSpeedModifier = new AttributeModifier(
                    BERSERK_MOVEMENT_SPEED_UUID,
                    "Berserk Movement Speed",
                    movementSpeedBonus,
                    AttributeModifier.Operation.ADDITION);
            movementSpeedAttribute.addTransientModifier(movementSpeedModifier);
            activeModifiers.put(BERSERK_MOVEMENT_SPEED_UUID, movementSpeedModifier);
        }
    }

    /**
     * @param entity
     */
    public static void removeBerserkModifier(LivingEntity entity) {
        AttributeInstance attackSpeedAttribute = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null && attackSpeedAttribute.getModifier(BERSERK_ATTACK_SPEED_UUID) != null) {
            attackSpeedAttribute.removeModifier(BERSERK_ATTACK_SPEED_UUID);
        }

        AttributeInstance movementSpeedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null
                && movementSpeedAttribute.getModifier(BERSERK_MOVEMENT_SPEED_UUID) != null) {
            movementSpeedAttribute.removeModifier(BERSERK_MOVEMENT_SPEED_UUID);
        }

        activeModifiers.remove(BERSERK_ATTACK_SPEED_UUID);
        activeModifiers.remove(BERSERK_MOVEMENT_SPEED_UUID);
    }

    public static void applyMucilagePossessionModifier(LivingEntity entity, int amplifier) {
        int armorReduction = (amplifier + 1) * 8;
        int toughnessReduction = (amplifier + 1) * 4;

        AttributeInstance armorAttribute = entity.getAttribute(Attributes.ARMOR);
        if (armorAttribute != null) {
            if (armorAttribute.getModifier(MUCILAGE_ARMOR_UUID) != null) {
                armorAttribute.removeModifier(MUCILAGE_ARMOR_UUID);
            }
            AttributeModifier armorModifier = new AttributeModifier(
                    MUCILAGE_ARMOR_UUID,
                    "Mucilage Possession Armor Reduction",
                    -armorReduction,
                    AttributeModifier.Operation.ADDITION);
            armorAttribute.addTransientModifier(armorModifier);
            activeModifiers.put(MUCILAGE_ARMOR_UUID, armorModifier);
        }

        AttributeInstance toughnessAttribute = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttribute != null) {
            if (toughnessAttribute.getModifier(MUCILAGE_ARMOR_TOUGHNESS_UUID) != null) {
                toughnessAttribute.removeModifier(MUCILAGE_ARMOR_TOUGHNESS_UUID);
            }
            AttributeModifier toughnessModifier = new AttributeModifier(
                    MUCILAGE_ARMOR_TOUGHNESS_UUID,
                    "Mucilage Possession Armor Toughness Reduction",
                    -toughnessReduction,
                    AttributeModifier.Operation.ADDITION);
            toughnessAttribute.addTransientModifier(toughnessModifier);
            activeModifiers.put(MUCILAGE_ARMOR_TOUGHNESS_UUID, toughnessModifier);
        }
    }

    public static void removeMucilagePossessionModifier(LivingEntity entity) {
        AttributeInstance armorAttribute = entity.getAttribute(Attributes.ARMOR);
        if (armorAttribute != null && armorAttribute.getModifier(MUCILAGE_ARMOR_UUID) != null) {
            armorAttribute.removeModifier(MUCILAGE_ARMOR_UUID);
        }

        AttributeInstance toughnessAttribute = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (toughnessAttribute != null && toughnessAttribute.getModifier(MUCILAGE_ARMOR_TOUGHNESS_UUID) != null) {
            toughnessAttribute.removeModifier(MUCILAGE_ARMOR_TOUGHNESS_UUID);
        }

        activeModifiers.remove(MUCILAGE_ARMOR_UUID);
        activeModifiers.remove(MUCILAGE_ARMOR_TOUGHNESS_UUID);
    }
}