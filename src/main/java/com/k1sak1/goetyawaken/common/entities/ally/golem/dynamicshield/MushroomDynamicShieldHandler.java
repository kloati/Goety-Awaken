package com.k1sak1.goetyawaken.common.entities.ally.golem.dynamicshield;

import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import net.minecraft.world.damagesource.DamageSource;
import java.lang.Math;

public class MushroomDynamicShieldHandler {
    public static int DEFAULT_LIMIT_TIME = com.k1sak1.goetyawaken.Config.MUSHROOM_DYNAMIC_SHIELD_DEFAULT_LIMIT_TIME
            .get();

    private final MushroomMonstrosity mushroomMonstrosity;

    public MushroomDynamicShieldHandler(MushroomMonstrosity mushroomMonstrosity) {
        this.mushroomMonstrosity = mushroomMonstrosity;
    }

    public float calculateDamageWithDynamicShield(DamageSource damageSource, float damageAmount) {
        if (shouldApplyDynamicShield(damageSource)) {
            int currentTime = mushroomMonstrosity.tickCount;
            int lastHurtTick = getLastHurtTick();
            int shieldDuration = getShieldDuration();

            int elapsedTicks = currentTime - lastHurtTick;
            if (elapsedTicks < shieldDuration) {
                float remainingRatio = (float) (shieldDuration - elapsedTicks) / shieldDuration;
                float damageReduction = remainingRatio;
                damageAmount *= (1.0f - damageReduction);
                damageAmount = Math.max(damageAmount, 0.0f);
            }
        }
        float maxHealth = mushroomMonstrosity.getMaxHealth();
        float threshold = maxHealth * 0.05f;
        if (damageAmount > threshold) {
            float excessDamage = damageAmount - threshold;
            float reducedExcessDamage = excessDamage * 0.05f;
            damageAmount = threshold + reducedExcessDamage;
        }

        setLastHurtTick(mushroomMonstrosity.tickCount);

        return damageAmount;
    }

    private boolean shouldApplyDynamicShield(DamageSource damageSource) {
        return true;
    }

    public int getShieldDuration() {
        return mushroomMonstrosity.getEntityData().get(MushroomMonstrosity.DYNAMIC_SHIELD_DURATION);
    }

    public void setShieldDuration(int duration) {
        mushroomMonstrosity.getEntityData().set(MushroomMonstrosity.DYNAMIC_SHIELD_DURATION, Math.max(duration, 0));
    }

    public int getLastHurtTick() {
        return mushroomMonstrosity.getEntityData().get(MushroomMonstrosity.LAST_HURT_TICK);
    }

    public void setLastHurtTick(int tick) {
        mushroomMonstrosity.getEntityData().set(MushroomMonstrosity.LAST_HURT_TICK, tick);
    }

    public void activateShield() {
        setLastHurtTick(mushroomMonstrosity.tickCount);
    }

    public int getRemainingShieldTime() {
        int elapsed = mushroomMonstrosity.tickCount - getLastHurtTick();
        int duration = getShieldDuration();
        return Math.max(0, duration - elapsed);
    }

    public boolean hasActiveShield() {
        return getRemainingShieldTime() > 0;
    }
}