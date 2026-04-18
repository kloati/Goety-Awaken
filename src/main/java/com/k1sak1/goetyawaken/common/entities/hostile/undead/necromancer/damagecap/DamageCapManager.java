package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.damagecap;

import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Inspired by Youkai-Homecoming
 * 
 * @author lcy0x1 (Original Author)
 * @see <a href=
 *      "https://github.com/Minecraft-LightLand/Youkai-Homecoming">Youkai-Homecoming
 *      Repository</a>
 * @license LGPL-2.1
 */
public class DamageCapManager {

    private final AbstractNamelessOne controlledEntity;
    private int damageCooldownTicks = 0;
    private int lastDamageReceivedTick = 0;
    private int lastProcessedDamageTick = 0;
    private boolean damageCallInitiated = false;
    private boolean actuallyHurtInvoked = false;
    private boolean finalHurtProcessed = false;
    private float accumulatedIllegalDamage = 0;
    private float clientCurrentHealth = -1;
    private float clientPeakHealth = -1;

    private static final class DataAccessors {
        static final EntityDataAccessor<Float> CURRENT_COMBAT_HEALTH = SynchedEntityData
                .defineId(AbstractNamelessOne.class, EntityDataSerializers.FLOAT);
        static final EntityDataAccessor<Float> PEAK_COMBAT_HEALTH = SynchedEntityData
                .defineId(AbstractNamelessOne.class, EntityDataSerializers.FLOAT);
    }

    private static final float HEALTH_LOSS_THRESHOLD_RATIO = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneDamageCapPercent
            .get().floatValue();
    private static final int BASE_DAMAGE_COOLDOWN = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneHitCooldown
            .get();
    private static final int DYNAMIC_REDUCTION_WINDOW = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneDynamicReductionTime
            .get();

    public DamageCapManager(AbstractNamelessOne controlledEntity) {
        this.controlledEntity = controlledEntity;
    }

    public void initializeSyncedData() {
        float defaultMaxHealth = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneHealth.get().floatValue();
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

        validateHealthConsistency();
    }

    public boolean handleHurt(DamageSource source, float amount) {
        this.damageCallInitiated = true;

        if (damageCooldownTicks > 0) {
            this.damageCallInitiated = false;
            return false;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (source.is(DamageTypes.GENERIC_KILL)) {
                return true;
            }
            if (source.getEntity() instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    return true;
                }
            }
        }

        return true;
    }

    public float clampDamage(DamageSource source, float amount) {
        if (!damageCallInitiated && !finalHurtProcessed) {
            recordIllegalDamage(amount, source.getEntity());
            return 0;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (source.getEntity() instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    this.damageCallInitiated = false;
                    return amount;
                }
            }
            if (source.is(DamageTypes.GENERIC_KILL)) {
                this.damageCallInitiated = false;
                return amount;
            }
            if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
                if (amount > calculateMaximumAllowedDamage()) {
                    recordIllegalDamage(amount - calculateMaximumAllowedDamage(), source.getEntity());
                }
                this.damageCallInitiated = false;
                return Math.min(calculateMaximumAllowedDamage(), amount);
            }
        }

        float maxDamage = calculateMaximumAllowedDamage();
        float cappedAmount = Math.min(amount, maxDamage);

        float finalAmount = applyTimeBasedReduction(cappedAmount);

        if (finalAmount > maxDamage) {
            finalAmount = maxDamage;
            recordIllegalDamage(finalAmount - maxDamage, source.getEntity());
        }

        damageCooldownTicks = BASE_DAMAGE_COOLDOWN;
        lastDamageReceivedTick = controlledEntity.tickCount;
        return finalAmount;
    }

    public boolean handleActuallyHurt(DamageSource source, float amount) {
        this.actuallyHurtInvoked = true;

        if (!damageCallInitiated && !finalHurtProcessed) {
            recordIllegalDamage(amount, source.getEntity());
            this.actuallyHurtInvoked = false;
            return false;
        }

        this.actuallyHurtInvoked = false;
        return true;
    }

    public void hurtFinal(DamageSource source, float amount) {
        if (!Float.isFinite(amount)) {
            return;
        }

        amount = clampDamage(source, amount);
        if (amount <= 0) {
            return;
        }

        this.finalHurtProcessed = true;

        float currentProgress = getCurrentCombatHealth();
        float newProgress = Math.max(0, currentProgress - amount);
        setCurrentCombatHealth(newProgress);
        synchronizeVanillaHealth(newProgress);
        this.lastProcessedDamageTick = controlledEntity.tickCount;
        this.finalHurtProcessed = false;
        this.damageCallInitiated = false;
        this.actuallyHurtInvoked = false;
    }

    public boolean handleSetHealth(float health) {
        if (!Float.isFinite(health)) {
            return false;
        }

        if (controlledEntity.level().isClientSide()) {
            setCurrentCombatHealth(health);
            return false;
        }

        float currentProgress = getCurrentCombatHealth();
        if (health <= currentProgress) {
            if (!damageCallInitiated && !actuallyHurtInvoked && !finalHurtProcessed) {
                float healthDrop = currentProgress - health;
                recordIllegalDamage(healthDrop, null);
                float maxAllowedDrop = calculateMaximumAllowedDamage();
                if (healthDrop > maxAllowedDrop) {
                    return false;
                }
            }
            float healthDrop = currentProgress - health;
            float maxAllowedDrop = calculateMaximumAllowedDamage();
            if (healthDrop > maxAllowedDrop) {
                health = currentProgress - maxAllowedDrop;
                recordIllegalDamage(healthDrop - maxAllowedDrop, null);
            }
        }

        setCurrentCombatHealth(health);
        synchronizeVanillaHealth(health);
        return true;
    }

    public float calculateMaximumAllowedDamage() {
        return controlledEntity.getMaxHealth() * HEALTH_LOSS_THRESHOLD_RATIO;
    }

    private float applyTimeBasedReduction(float amount) {
        int timeSinceLastHit = controlledEntity.tickCount - lastProcessedDamageTick;
        if (timeSinceLastHit < DYNAMIC_REDUCTION_WINDOW) {
            float reductionRatio = (float) timeSinceLastHit / DYNAMIC_REDUCTION_WINDOW;
            return amount * reductionRatio;
        }
        return amount;
    }

    public void recordIllegalDamage(float amount, @Nullable Entity causer) {
        this.accumulatedIllegalDamage += amount;
        if (this.controlledEntity.level().isClientSide()) {
            return;
        }
    }

    public void clearIllegalDamage() {
        this.accumulatedIllegalDamage = 0;
    }

    public float getAccumulatedIllegalDamage() {
        return this.accumulatedIllegalDamage;
    }

    public boolean isInCooldownState() {
        return damageCooldownTicks > 0;
    }

    public float getCurrentCombatHealth() {
        if (controlledEntity.level().isClientSide) {
            if (clientCurrentHealth < 0) {
                return controlledEntity.getVanillaHealth();
            }
            return clientCurrentHealth;
        }

        if (controlledEntity.getEntityDataAccessor() == null) {
            return controlledEntity.getMaxHealth();
        }
        return controlledEntity.getEntityDataAccessor().get(DataAccessors.CURRENT_COMBAT_HEALTH);
    }

    public void setCurrentCombatHealth(float amount) {
        if (controlledEntity.getEntityDataAccessor() == null || !Float.isFinite(amount)) {
            return;
        }
        float maxHealth = getPeakCombatHealth();
        float currentProgress = getCurrentCombatHealth();
        if (amount < currentProgress && !damageCallInitiated && !actuallyHurtInvoked && !finalHurtProcessed) {
            recordIllegalDamage(currentProgress - amount, null);
        }

        float clampedAmount = Math.max(0, Math.min(amount, maxHealth));
        controlledEntity.getEntityDataAccessor().set(DataAccessors.CURRENT_COMBAT_HEALTH, clampedAmount);
        if (!controlledEntity.level().isClientSide) {
            syncToClients();
        }
    }

    public float getPeakCombatHealth() {
        if (controlledEntity.level().isClientSide) {
            if (clientPeakHealth < 0) {
                return controlledEntity.getMaxHealth();
            }
            return clientPeakHealth;
        }
        if (controlledEntity.getEntityDataAccessor() == null) {
            return controlledEntity.getMaxHealth();
        }
        return controlledEntity.getEntityDataAccessor().get(DataAccessors.PEAK_COMBAT_HEALTH);
    }

    public void setClientCombatHealth(float currentHealth, float peakHealth) {
        this.clientCurrentHealth = currentHealth;
        this.clientPeakHealth = peakHealth;
    }

    private void syncToClients() {
        if (controlledEntity.level().isClientSide)
            return;

        float currentHealth = getCurrentCombatHealth();
        float peakHealth = getPeakCombatHealth();

        com.k1sak1.goetyawaken.common.network.ModNetwork.sentToTrackingEntityAndPlayer(
                controlledEntity,
                new com.k1sak1.goetyawaken.common.network.client.CCombatHealthSyncPacket(
                        controlledEntity.getId(),
                        currentHealth,
                        peakHealth));
    }

    public void setPeakCombatHealth(float max) {
        if (controlledEntity.getEntityDataAccessor() == null || !Float.isFinite(max)) {
            return;
        }
        controlledEntity.getEntityDataAccessor().set(DataAccessors.PEAK_COMBAT_HEALTH, max);
    }

    private void synchronizeVanillaHealth(float progress) {
        controlledEntity.setVanillaHealth(progress);
    }

    public boolean isDamageCallInitiated() {
        return this.damageCallInitiated;
    }

    public void setDamageCallInitiated(boolean damageCallInitiated) {
        this.damageCallInitiated = damageCallInitiated;
    }

    public boolean isActuallyHurtInvoked() {
        return this.actuallyHurtInvoked;
    }

    public boolean isFinalHurtProcessed() {
        return this.finalHurtProcessed;
    }

    public int getDamageCooldownTicks() {
        return damageCooldownTicks;
    }

    public void setDamageCooldownTicks(int cooldown) {
        this.damageCooldownTicks = cooldown;
    }

    public boolean isHealthChangeValid(float newHealth) {
        if (!Float.isFinite(newHealth)) {
            return false;
        }

        float currentHealth = getCurrentCombatHealth();
        if (newHealth > currentHealth) {
            return true;
        }

        float healthLoss = currentHealth - newHealth;
        float maxAllowedLoss = calculateMaximumAllowedDamage();

        return healthLoss <= maxAllowedLoss;
    }

    public void validateHealthConsistency() {
        float progress = getCurrentCombatHealth();
        float maxHealth = getPeakCombatHealth();

        if (progress > maxHealth || progress < 0) {
            setCurrentCombatHealth(maxHealth);
        }

        float vanillaHealth = controlledEntity.getHealth();
        if (vanillaHealth < progress && !controlledEntity.level().isClientSide()) {
            recordIllegalDamage(progress - vanillaHealth, null);
            synchronizeVanillaHealth(progress);
        }
    }

    public boolean isCurrentlyUnderAttack() {
        return this.damageCallInitiated || this.actuallyHurtInvoked || this.finalHurtProcessed
                || this.damageCooldownTicks > 0;
    }

    public float enforceHardDamageCap(DamageSource source, float rawDamage) {
        float maxAllowedDamage = calculateMaximumAllowedDamage();
        if (rawDamage > maxAllowedDamage) {
            recordIllegalDamage(rawDamage - maxAllowedDamage, source.getEntity());
            return maxAllowedDamage;
        }
        return rawDamage;
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