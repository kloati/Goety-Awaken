package com.k1sak1.goetyawaken.common.entities.ally.golem.damagecap;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class MonstrosityDamageCapHandler {
    private final LivingEntity entity;
    private float lastDamageTime = 0.0F;
    private float accumulatedDamage = 0.0F;

    public MonstrosityDamageCapHandler(LivingEntity entity) {
        this.entity = entity;
    }

    public float calculateMaximumAllowedDamage() {
        return (float) entity.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH)
                * (float) (double) com.k1sak1.goetyawaken.Config.MUSHROOM_MONSTROSITY_DAMAGE_CAP.get();
    }

    public float applyDamageCap(DamageSource source, float damage) {
        float maxAllowedDamage = calculateMaximumAllowedDamage();
        if (damage > maxAllowedDamage) {
            return maxAllowedDamage;
        }

        return damage;
    }

    public float adjustHealth(float targetHealth) {
        float currentHealth = entity.getHealth();
        float maxAllowedDamage = calculateMaximumAllowedDamage();
        float healthDifference = currentHealth - targetHealth;
        if (healthDifference > maxAllowedDamage) {
            return currentHealth - maxAllowedDamage;
        }

        return targetHealth;
    }

    public boolean shouldProcessDamage(DamageSource source, float damage) {
        float maxAllowedDamage = calculateMaximumAllowedDamage();
        return damage <= maxAllowedDamage;
    }
}