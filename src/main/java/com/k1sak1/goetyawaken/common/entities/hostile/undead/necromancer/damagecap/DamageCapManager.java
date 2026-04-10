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

    private final AbstractNamelessOne entity;
    private int hitCooldown = 0;
    private int lastHurtTick = 0;
    private int lastProcessedHurtTick = 0;
    private boolean hurtCall = false;
    private boolean actuallyHurtCall = false;
    private boolean hurtFinalCall = false;
    private float illegalDamage = 0;
    private static final EntityDataAccessor<Float> COMBAT_PROGRESS = SynchedEntityData
            .defineId(AbstractNamelessOne.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_COMBAT_PROGRESS = SynchedEntityData
            .defineId(AbstractNamelessOne.class, EntityDataSerializers.FLOAT);

    private static final float DAMAGE_THRESHOLD_PERCENT = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneDamageCapPercent
            .get().floatValue();
    private static final int DEFAULT_HIT_COOLDOWN = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneHitCooldown
            .get();
    private static final int DEFAULT_DYNAMIC_REDUCTION_TIME = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneDynamicReductionTime
            .get();

    public DamageCapManager(AbstractNamelessOne entity) {
        this.entity = entity;
    }

    public void defineSynchedData() {
        float defaultHealth = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneHealth.get().floatValue();
        entity.getEntityDataAccessor().define(COMBAT_PROGRESS, defaultHealth);
        entity.getEntityDataAccessor().define(MAX_COMBAT_PROGRESS, defaultHealth);
    }

    public void tick() {
        if (hitCooldown > 0) {
            hitCooldown--;
        }

        if (entity.tickCount == 1) {
            float maxHealth = entity.getMaxHealth();
            if (maxHealth > 0 && Math.abs(getMaxCombatProgress() - maxHealth) > 0.1F) {
                setMaxCombatProgress(maxHealth);
                if (getCombatProgress() > maxHealth) {
                    setCombatProgress(maxHealth);
                }
            }
        }

        validateCombatProgress();

    }

    public boolean handleHurt(DamageSource source, float amount) {
        this.hurtCall = true;

        if (hitCooldown > 0) {
            this.hurtCall = false;
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
        if (!hurtCall && !hurtFinalCall) {
            notifyIllegalDamage(amount, source.getEntity());
            return 0;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (source.getEntity() instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    this.hurtCall = false;
                    return amount;
                }
            }
            if (source.is(DamageTypes.GENERIC_KILL)) {
                this.hurtCall = false;
                return amount;
            }
            if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
                if (amount > getMaxAllowedDamage()) {
                    notifyIllegalDamage(amount - getMaxAllowedDamage(), source.getEntity());
                }
                this.hurtCall = false;
                return Math.min(getMaxAllowedDamage(), amount);
            }
        }

        float maxDamage = getMaxAllowedDamage();
        float cappedAmount = Math.min(amount, maxDamage);

        float finalAmount = applyDynamicReduction(cappedAmount);

        if (finalAmount > maxDamage) {
            finalAmount = maxDamage;
            notifyIllegalDamage(finalAmount - maxDamage, source.getEntity());
        }

        hitCooldown = DEFAULT_HIT_COOLDOWN;
        lastHurtTick = entity.tickCount;
        return finalAmount;
    }

    public boolean handleActuallyHurt(DamageSource source, float amount) {
        this.actuallyHurtCall = true;

        if (!hurtCall && !hurtFinalCall) {
            notifyIllegalDamage(amount, source.getEntity());
            this.actuallyHurtCall = false;
            return false;
        }

        this.actuallyHurtCall = false;
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

        this.hurtFinalCall = true;

        float currentProgress = getCombatProgress();
        float newProgress = Math.max(0, currentProgress - amount);
        setCombatProgress(newProgress);
        syncToVanillaHealth(newProgress);
        this.lastProcessedHurtTick = entity.tickCount;
        this.hurtFinalCall = false;
        this.hurtCall = false;
        this.actuallyHurtCall = false;
    }

    public boolean handleSetHealth(float health) {
        if (!Float.isFinite(health)) {
            return false;
        }

        if (entity.level().isClientSide()) {
            setCombatProgress(health);
            return false;
        }

        float currentProgress = getCombatProgress();
        if (health <= currentProgress) {
            if (!hurtCall && !actuallyHurtCall && !hurtFinalCall) {
                float healthDrop = currentProgress - health;
                notifyIllegalDamage(healthDrop, null);
                float maxAllowedDrop = getMaxAllowedDamage();
                if (healthDrop > maxAllowedDrop) {
                    return false;
                }
            }
            float healthDrop = currentProgress - health;
            float maxAllowedDrop = getMaxAllowedDamage();
            if (healthDrop > maxAllowedDrop) {
                health = currentProgress - maxAllowedDrop;
                notifyIllegalDamage(healthDrop - maxAllowedDrop, null);
            }
        }

        setCombatProgress(health);
        syncToVanillaHealth(health);
        return true;
    }

    public float getMaxAllowedDamage() {
        return entity.getMaxHealth() * DAMAGE_THRESHOLD_PERCENT;
    }

    private float applyDynamicReduction(float amount) {
        int timeSinceLastHit = entity.tickCount - lastProcessedHurtTick;
        if (timeSinceLastHit < DEFAULT_DYNAMIC_REDUCTION_TIME) {
            float reductionRatio = (float) timeSinceLastHit / DEFAULT_DYNAMIC_REDUCTION_TIME;
            return amount * reductionRatio;
        }
        return amount;
    }

    public void notifyIllegalDamage(float amount, @Nullable Entity causer) {
        this.illegalDamage += amount;
        if (entity.level().isClientSide()) {
            return;
        }
    }

    public void resetIllegalDamage() {
        this.illegalDamage = 0;
    }

    public float getIllegalDamage() {
        return this.illegalDamage;
    }

    public boolean isInvulnerable() {
        return hitCooldown > 0;
    }

    public float getCombatProgress() {
        if (entity.getEntityDataAccessor() == null) {
            return entity.getMaxHealth();
        }
        return entity.getEntityDataAccessor().get(COMBAT_PROGRESS);
    }

    public void setCombatProgress(float amount) {
        if (entity.getEntityDataAccessor() == null || !Float.isFinite(amount)) {
            return;
        }
        float maxHealth = getMaxCombatProgress();
        float currentProgress = getCombatProgress();
        if (amount < currentProgress && !hurtCall && !actuallyHurtCall && !hurtFinalCall) {
            notifyIllegalDamage(currentProgress - amount, null);
        }

        float clampedAmount = Math.max(0, Math.min(amount, maxHealth));
        entity.getEntityDataAccessor().set(COMBAT_PROGRESS, clampedAmount);
    }

    public float getMaxCombatProgress() {
        if (entity.getEntityDataAccessor() == null) {
            return entity.getMaxHealth();
        }
        return entity.getEntityDataAccessor().get(MAX_COMBAT_PROGRESS);
    }

    public void setMaxCombatProgress(float max) {
        if (entity.getEntityDataAccessor() == null || !Float.isFinite(max)) {
            return;
        }
        entity.getEntityDataAccessor().set(MAX_COMBAT_PROGRESS, max);
    }

    private void syncToVanillaHealth(float progress) {
        entity.setVanillaHealth(progress);
    }

    public boolean isHurtCall() {
        return this.hurtCall;
    }

    public void setHurtCall(boolean hurtCall) {
        this.hurtCall = hurtCall;
    }

    public boolean isActuallyHurtCall() {
        return this.actuallyHurtCall;
    }

    public boolean isHurtFinalCall() {
        return this.hurtFinalCall;
    }

    public int getHitCooldown() {
        return hitCooldown;
    }

    public void setHitCooldown(int cooldown) {
        this.hitCooldown = cooldown;
    }

    public boolean isValidHealthChange(float newHealth) {
        if (!Float.isFinite(newHealth)) {
            return false;
        }

        float currentHealth = getCombatProgress();
        if (newHealth > currentHealth) {
            return true;
        }

        float healthLoss = currentHealth - newHealth;
        float maxAllowedLoss = getMaxAllowedDamage();

        return healthLoss <= maxAllowedLoss;
    }

    public void validateCombatProgress() {
        float progress = getCombatProgress();
        float maxHealth = getMaxCombatProgress();

        if (progress > maxHealth || progress < 0) {
            setCombatProgress(maxHealth);
        }

        float vanillaHealth = entity.getHealth();
        if (vanillaHealth < progress && !entity.level().isClientSide()) {
            notifyIllegalDamage(progress - vanillaHealth, null);
            syncToVanillaHealth(progress);
        }
    }

    public boolean isUnderAttack() {
        return this.hurtCall || this.actuallyHurtCall || this.hurtFinalCall || this.hitCooldown > 0;
    }

    public float applyHardDamageLimit(DamageSource source, float rawDamage) {
        float maxAllowedDamage = getMaxAllowedDamage();
        if (rawDamage > maxAllowedDamage) {
            notifyIllegalDamage(rawDamage - maxAllowedDamage, source.getEntity());
            return maxAllowedDamage;
        }
        return rawDamage;
    }

    public static float getDamageThresholdPercent() {
        return DAMAGE_THRESHOLD_PERCENT;
    }

    public static int getDefaultHitCooldown() {
        return DEFAULT_HIT_COOLDOWN;
    }

    public static int getDefaultDynamicReductionTime() {
        return DEFAULT_DYNAMIC_REDUCTION_TIME;
    }

}