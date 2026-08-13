package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.damagecap;

import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;

public class DamageCapManager {

    private final AbstractNamelessOne controlledEntity;
    private int damageCooldownTicks;
    private int lastDamageProcessedTick;

    private static final class DataAccessors {
        static final EntityDataAccessor<Float> CURRENT_COMBAT_HEALTH = SynchedEntityData
                .defineId(AbstractNamelessOne.class, EntityDataSerializers.FLOAT);
        static final EntityDataAccessor<Float> PEAK_COMBAT_HEALTH = SynchedEntityData
                .defineId(AbstractNamelessOne.class, EntityDataSerializers.FLOAT);
    }

    private static final float HEALTH_LOSS_THRESHOLD_RATIO = AttributesConfig.NamelessOneDamageCapPercent.get()
            .floatValue();
    private static final int BASE_DAMAGE_COOLDOWN = AttributesConfig.NamelessOneHitCooldown.get();
    private static final int DYNAMIC_REDUCTION_WINDOW = AttributesConfig.NamelessOneDynamicReductionTime.get();

    public DamageCapManager(AbstractNamelessOne controlledEntity) {
        this.controlledEntity = controlledEntity;
    }

    public void initializeSyncedData() {
        float defaultMaxHealth = AttributesConfig.NamelessOneHealth.get().floatValue();
        controlledEntity.getEntityDataAccessor().define(DataAccessors.CURRENT_COMBAT_HEALTH, defaultMaxHealth);
        controlledEntity.getEntityDataAccessor().define(DataAccessors.PEAK_COMBAT_HEALTH, defaultMaxHealth);
    }

    public void performTick() {
        if (damageCooldownTicks > 0) {
            damageCooldownTicks--;
        }
        if (controlledEntity.tickCount == 1) {
            float maximumHealth = controlledEntity.getMaxHealth();
            if (maximumHealth > 0 && Math.abs(getPeakCombatHealth() - maximumHealth) > 0.1F) {
                setPeakCombatHealth(maximumHealth);
                if (getCurrentCombatHealth() > maximumHealth) {
                    setCurrentCombatHealth(maximumHealth);
                }
            }
        }
    }

    public float applyDamageCap(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (source.is(DamageTypes.GENERIC_KILL)) {
                return amount;
            }
            if (source.getEntity() instanceof Player player
                    && player.getAbilities().instabuild) {
                return amount;
            }
        }
        if (damageCooldownTicks > 0) {
            return 0.0F;
        }
        float maxAllowed = calculateMaximumAllowedDamage();
        float capped = Math.min(amount, maxAllowed);
        capped = applyDynamicReduction(capped);
        damageCooldownTicks = BASE_DAMAGE_COOLDOWN;
        lastDamageProcessedTick = controlledEntity.tickCount;
        return capped;
    }

    private float applyDynamicReduction(float amount) {
        int elapsed = controlledEntity.tickCount - lastDamageProcessedTick;
        if (elapsed < DYNAMIC_REDUCTION_WINDOW) {
            return amount * ((float) elapsed / DYNAMIC_REDUCTION_WINDOW);
        }
        return amount;
    }

    public void applyCombatHealthReduction(float actualDamage) {
        float current = getCurrentCombatHealth();
        float next = Math.max(0.0F, current - actualDamage);
        setCurrentCombatHealth(next);
        controlledEntity.setVanillaHealth(next);
    }

    public void healCombatHealth(float healAmount) {
        float current = getCurrentCombatHealth();
        float peak = getPeakCombatHealth();
        float next = Math.min(peak, current + healAmount);
        if (next != current) {
            setCurrentCombatHealth(next);
            controlledEntity.setVanillaHealth(next);
        }
    }

    public float calculateMaximumAllowedDamage() {
        return controlledEntity.getMaxHealth() * HEALTH_LOSS_THRESHOLD_RATIO;
    }

    public boolean isInCooldownState() {
        return damageCooldownTicks > 0;
    }

    public boolean isCurrentlyUnderAttack() {
        return damageCooldownTicks > 0;
    }

    public float getCurrentCombatHealth() {
        SynchedEntityData data = controlledEntity.getEntityDataAccessor();
        if (data == null) {
            return controlledEntity.getMaxHealth();
        }
        return data.get(DataAccessors.CURRENT_COMBAT_HEALTH);
    }

    public void setCurrentCombatHealth(float amount) {
        SynchedEntityData data = controlledEntity.getEntityDataAccessor();
        if (data == null || !Float.isFinite(amount)) {
            return;
        }
        float current = getCurrentCombatHealth();
        if (amount > current) {
            data.set(DataAccessors.CURRENT_COMBAT_HEALTH, Math.max(0.0F, amount));
        } else {
            float peak = getPeakCombatHealth();
            data.set(DataAccessors.CURRENT_COMBAT_HEALTH, Math.max(0.0F, Math.min(amount, peak)));
        }
    }

    public float getPeakCombatHealth() {
        SynchedEntityData data = controlledEntity.getEntityDataAccessor();
        if (data == null) {
            return controlledEntity.getMaxHealth();
        }
        return data.get(DataAccessors.PEAK_COMBAT_HEALTH);
    }

    public void setPeakCombatHealth(float max) {
        SynchedEntityData data = controlledEntity.getEntityDataAccessor();
        if (data == null || !Float.isFinite(max)) {
            return;
        }
        data.set(DataAccessors.PEAK_COMBAT_HEALTH, Math.max(0.0F, max));
    }

    @Deprecated
    public void validateHealthConsistency() {
    }

    @Deprecated
    public boolean handleHurt(DamageSource source, float amount) {
        return true;
    }

    @Deprecated
    public boolean handleActuallyHurt(DamageSource source, float amount) {
        return true;
    }

    @Deprecated
    public void hurtFinal(DamageSource source, float amount) {
        applyCombatHealthReduction(amount);
    }

    @Deprecated
    public void setDamageCallInitiated(boolean value) {
    }

    @Deprecated
    public boolean isDamageCallInitiated() {
        return false;
    }

    @Deprecated
    public boolean isActuallyHurtInvoked() {
        return false;
    }

    @Deprecated
    public boolean isFinalHurtProcessed() {
        return false;
    }

    @Deprecated
    public int getDamageCooldownTicks() {
        return damageCooldownTicks;
    }

    @Deprecated
    public void setDamageCooldownTicks(int cooldown) {
        this.damageCooldownTicks = cooldown;
    }

    @Deprecated
    public void setClientCombatHealth(float currentHealth, float peakHealth) {
    }

    @Deprecated
    public float getAccumulatedIllegalDamage() {
        return 0.0F;
    }

    @Deprecated
    public void clearIllegalDamage() {
    }

    @Deprecated
    public boolean isHealthChangeValid(float newHealth) {
        return true;
    }

    public static float getHealthLossThresholdRatio() {
        return HEALTH_LOSS_THRESHOLD_RATIO;
    }

    public static int getBaseDamageCooldown() {
        return BASE_DAMAGE_COOLDOWN;
    }

    public static int getDynamicReductionWindow() {
        return DYNAMIC_REDUCTION_WINDOW;
    }
}
