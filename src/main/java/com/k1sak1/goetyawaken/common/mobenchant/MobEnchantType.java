package com.k1sak1.goetyawaken.common.mobenchant;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public enum MobEnchantType {
    PROTECTION("protection", 32767),
    TOUGH("tough", 32767, createToughModifiers()),
    HEALTH_BOOST("health_boost", 32767, createHealthBoostModifiers()),
    SPEEDY("speedy", 32767, createSpeedyModifiers()),
    STRONG("strong", 32767, createStrongModifiers()),
    THORN("thorn", 32767),
    RESURRECTION_AURA("resurrection_aura", 1),
    MULTISHOT("multishot", 255),
    HUGE("huge", 255, createHugeModifiers());

    private final String name;
    private final int maxLevel;
    private final AttributeModifierInfo[] attributeModifiers;

    MobEnchantType(String name, int maxLevel) {
        this(name, maxLevel, null);
    }

    MobEnchantType(String name, int maxLevel, AttributeModifierInfo[] modifiers) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.attributeModifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public AttributeModifierInfo[] getAttributeModifiers() {
        return attributeModifiers;
    }

    public boolean hasAttributeModifiers() {
        return attributeModifiers != null && attributeModifiers.length > 0;
    }

    private static AttributeModifierInfo[] createToughModifiers() {
        return new AttributeModifierInfo[] {
                new AttributeModifierInfo(
                        "mobenchant-tough-armor",
                        Attributes.ARMOR,
                        4.0,
                        AttributeModifier.Operation.ADDITION),
                new AttributeModifierInfo(
                        "mobenchant-tough-toughness",
                        Attributes.ARMOR_TOUGHNESS,
                        1.0,
                        AttributeModifier.Operation.ADDITION)
        };
    }

    private static AttributeModifierInfo[] createHealthBoostModifiers() {
        return new AttributeModifierInfo[] {
                new AttributeModifierInfo(
                        "mobenchant-health-boost",
                        Attributes.MAX_HEALTH,
                        2.0,
                        AttributeModifier.Operation.ADDITION,
                        true)
        };
    }

    private static AttributeModifierInfo[] createSpeedyModifiers() {
        return new AttributeModifierInfo[] {
                new AttributeModifierInfo(
                        "mobenchant-speedy",
                        Attributes.MOVEMENT_SPEED,
                        0.02,
                        AttributeModifier.Operation.ADDITION)
        };
    }

    private static AttributeModifierInfo[] createStrongModifiers() {
        return new AttributeModifierInfo[] {
                new AttributeModifierInfo(
                        "mobenchant-strong",
                        Attributes.ATTACK_DAMAGE,
                        1.0,
                        AttributeModifier.Operation.ADDITION)
        };
    }

    private static AttributeModifierInfo[] createHugeModifiers() {
        return new AttributeModifierInfo[] {
                new AttributeModifierInfo(
                        "mobenchant-huge-health",
                        Attributes.MAX_HEALTH,
                        0.1,
                        AttributeModifier.Operation.MULTIPLY_TOTAL,
                        true)
        };
    }

    public static MobEnchantType byName(String name) {
        for (MobEnchantType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static class AttributeModifierInfo {
        private final String baseUuid;
        private final Attribute attribute;
        private final double baseAmount;
        private final AttributeModifier.Operation operation;
        private final boolean healsEntity;

        public AttributeModifierInfo(String baseUuid, Attribute attribute, double baseAmount,
                AttributeModifier.Operation operation) {
            this(baseUuid, attribute, baseAmount, operation, false);
        }

        public AttributeModifierInfo(String baseUuid, Attribute attribute, double baseAmount,
                AttributeModifier.Operation operation, boolean healsEntity) {
            this.baseUuid = baseUuid;
            this.attribute = attribute;
            this.baseAmount = baseAmount;
            this.operation = operation;
            this.healsEntity = healsEntity;
        }

        public String getBaseUuid() {
            return baseUuid;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public double getBaseAmount() {
            return baseAmount;
        }

        public AttributeModifier.Operation getOperation() {
            return operation;
        }

        public boolean shouldHealEntity() {
            return healsEntity;
        }

        public double getAmountForLevel(int level) {
            return baseAmount * level;
        }

        public UUID getUuid() {
            return UUID.nameUUIDFromBytes(baseUuid.getBytes());
        }
    }
}
