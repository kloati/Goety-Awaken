package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.client.particle.RingParticle;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModelSnapshot;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class TowerGuardServant extends AbstractIllagerServant implements ICustomAttributes {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(TowerGuardServant.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(TowerGuardServant.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHIELD_HIDDEN = SynchedEntityData
            .defineId(TowerGuardServant.class, EntityDataSerializers.BOOLEAN);

    public float walkAnimSpeed = 1.0F;
    public int attackTick;
    public float shieldHealth = 10.0F;
    private int shieldInvulnTime = 0;
    public int chargeCooldown = 0;
    public int chargeTick = 0;
    public int shieldBreakTick = 0;
    public int shieldRegenTimer = 0;
    public int chargeStopTick = 0;
    public int reflectSoundTimer = 0;
    public Vec3 chargeDirection = Vec3.ZERO;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState shieldBreakAnimationState = new AnimationState();
    public AnimationState chargeAnimationState = new AnimationState();
    public AnimationState chargeCollidedStopAnimationState = new AnimationState();
    public AnimationState chargeNormalStopAnimationState = new AnimationState();
    public AnimationState standingByAnimationState = new AnimationState();
    public AnimationState saceAnimationState = new AnimationState();
    public final List<Pair<Vec3, ModelSnapshot>> trailSnapshots = new java.util.ArrayList<>(50);
    public float lastTrailTick = 0;
    public int baseAnimTransitionTick = 0;
    public static final int BASE_ANIM_TRANSITION_DURATION = 5;
    public String transitionFromKey = "";
    public String transitionToKey = "";
    private String currentAnimKey = "";

    public String getCurrentAnimKey() {
        return this.currentAnimKey;
    }

    public boolean shouldAddTrailSnapshot() {
        return this.isCharging();
    }

    public TowerGuardServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void setStaying(boolean staying) {
        boolean wasStaying = this.isStaying();
        super.setStaying(staying);
        if (!this.level().isClientSide && staying && !wasStaying) {
            this.getNavigation().stop();
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.miscGoal();
        this.attackGoal();
    }

    @Override
    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public void attackGoal() {
        this.goalSelector.addGoal(1, new ChargeStopGoal());
        this.goalSelector.addGoal(2, new ChargeGoal());
        this.goalSelector.addGoal(3, new ShieldBreakGoal());
        this.goalSelector.addGoal(4, new MeleeGoal());
        this.goalSelector.addGoal(5, new TowerGuardAttackGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TowerGuardHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TowerGuardMovementSpeed.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TowerGuardDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ARMOR, AttributesConfig.TowerGuardArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.TowerGuardArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.TowerGuardHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.TowerGuardMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.TowerGuardDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.TowerGuardArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.TowerGuardArmorToughness.get());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        this.setShield(true);
        this.setShieldHealth(AttributesConfig.TowerGuardShieldCapacity.get().floatValue());
        this.setShieldHidden(false);
        return pSpawnData;
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
        if (pCompound.contains("hasShield"))
            this.setShield(pCompound.getBoolean("hasShield"));
        if (pCompound.contains("ShieldHealth"))
            this.shieldHealth = pCompound.getFloat("ShieldHealth");
        if (pCompound.contains("ShieldHidden"))
            this.setShieldHidden(pCompound.getBoolean("ShieldHidden"));
        if (pCompound.contains("ShieldInvulnTime"))
            this.shieldInvulnTime = pCompound.getInt("ShieldInvulnTime");
        if (pCompound.contains("ChargeCooldown"))
            this.chargeCooldown = pCompound.getInt("ChargeCooldown");
        if (pCompound.contains("ShieldRegenTimer"))
            this.shieldRegenTimer = pCompound.getInt("ShieldRegenTimer");
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasShield", this.hasShield());
        pCompound.putFloat("ShieldHealth", this.shieldHealth);
        pCompound.putBoolean("ShieldHidden", this.isShieldHidden());
        pCompound.putInt("ShieldInvulnTime", this.shieldInvulnTime);
        pCompound.putInt("ChargeCooldown", this.chargeCooldown);
        pCompound.putInt("ShieldRegenTimer", this.shieldRegenTimer);
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof TowerGuardServant;
    }

    private boolean getFlag(int mask) {
        return (this.entityData.get(DATA_FLAGS_ID) & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value)
            i |= mask;
        else
            i &= ~mask;
        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean v) {
        this.setFlags(1, v);
        this.attackTick = 0;
    }

    public boolean isShieldBreaking() {
        return this.getFlag(2);
    }

    public void setShieldBreaking(boolean v) {
        this.setFlags(2, v);
        this.shieldBreakTick = 0;
    }

    public boolean isCharging() {
        return this.getFlag(4);
    }

    public void setCharging(boolean v) {
        this.setFlags(4, v);
        this.chargeTick = 0;
    }

    public boolean isChargeCollidedStopping() {
        return this.getFlag(8);
    }

    public void setChargeCollidedStopping(boolean v) {
        this.setFlags(8, v);
        this.chargeStopTick = 0;
    }

    public boolean isChargeNormalStopping() {
        return this.getFlag(16);
    }

    public void setChargeNormalStopping(boolean v) {
        this.setFlags(16, v);
        this.chargeStopTick = 0;
    }

    public boolean hasShield() {
        return this.entityData.get(HAS_SHIELD);
    }

    public void setShield(boolean shield) {
        this.entityData.set(HAS_SHIELD, shield);
    }

    public boolean isShieldHidden() {
        return this.entityData.get(SHIELD_HIDDEN);
    }

    public void setShieldHidden(boolean hidden) {
        this.entityData.set(SHIELD_HIDDEN, hidden);
    }

    public void setShieldHealth(float health) {
        this.shieldHealth = health;
    }

    public void destroyShield() {
        if (this.hasShield()) {
            this.shieldHealth = 0;
            this.setShield(false);
            this.setShieldHidden(true);
            this.shieldInvulnTime = 10;
            this.playSound(ModSounds.TOWER_GUARD_SHIELD_BREAK.get(), this.getSoundVolume(), this.getVoicePitch());
            this.playSound(ModSounds.TOWER_GUARD_SHIELD_BREAK_VOICE.get(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    public void regenerateShield() {
        if (!this.hasShield()) {
            this.shieldHealth = AttributesConfig.TowerGuardShieldCapacity.get().floatValue();
            this.setShield(true);
            this.setShieldHidden(false);
            this.shieldRegenTimer = 0;
            this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
        }
    }

    protected float getDamageAfterArmorAbsorb(DamageSource source, float amount) {
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            amount = net.minecraft.world.damagesource.CombatRules.getDamageAfterAbsorb(amount,
                    (float) this.getArmorValue(),
                    (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }
        return amount;
    }

    private boolean isDamageFromFront(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null)
            return false;
        Vec3 dirToAttacker = attacker.position().subtract(this.position()).normalize();
        Vec3 facing = this.getViewVector(1.0F);
        return dirToAttacker.dot(facing) > 0.3;
    }

    private void absorbDamageWithShield(float amount) {
        float newHealth = this.shieldHealth - amount;
        if (newHealth <= 0) {
            this.destroyShield();
        } else {
            this.shieldHealth = newHealth;
            this.playSound(ModSounds.TOWER_GUARD_DEFLECT.get(), 1.0F, 1.0F);
        }
    }

    public void updateKnockbackResistance() {
        Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE))
                .setBaseValue(this.hasShield() ? 1.0 : 0.8);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHIELD, true);
        this.entityData.define(SHIELD_HIDDEN, false);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (this.hasShield() && this.shieldInvulnTime > 0) {
                return false;
            }
            if (this.hasShield() && source.is(DamageTypeTags.IS_PROJECTILE)) {
                return false;
            }
            if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                    && this.isDamageFromFront(source)) {
                float finalDamage = this.getDamageAfterArmorAbsorb(source, amount);
                this.absorbDamageWithShield(finalDamage);
                this.shieldInvulnTime = 10;
                return false;
            }
            if (this.getTarget() != null && source.getEntity() instanceof LivingEntity le) {
                if (MobUtil.ownedCanAttack(this, le) && le != this.getTrueOwner()) {
                    double d0 = this.distanceTo(this.getTarget());
                    double d1 = this.distanceTo(le);
                    if (d1 < d0) {
                        this.setTarget(le);
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double pX, double pY, double pZ) {
        if (!this.hasShield()) {
            super.knockback(pX, pY, pZ);
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!this.level().isClientSide) {
                if (itemstack.getItem() == ModItems.PALE_STEEL_INGOT.get()
                        && this.getTarget() == null && this.hurtTime <= 0) {
                    if (!this.hasShield() || this.isShieldHidden()) {
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        this.setShield(true);
                        this.setShieldHealth(AttributesConfig.TowerGuardShieldCapacity.get().floatValue());
                        this.setShieldHidden(false);
                        this.shieldRegenTimer = 0;
                        this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public boolean isMoving() {
        return !(this.walkAnimation.speed() < 0.01F);
    }

    @Override
    public void tick() {
        super.tick();
        this.updateKnockbackResistance();
        if (!this.level().isClientSide) {
            if (this.shieldInvulnTime > 0)
                this.shieldInvulnTime--;
            if (this.chargeCooldown > 0)
                this.chargeCooldown--;
            if (this.reflectSoundTimer > 0)
                this.reflectSoundTimer--;
            if (this.isMeleeAttacking())
                this.attackTick++;
            if (this.isShieldBreaking())
                this.shieldBreakTick++;
            if (this.isCharging()) {
                this.chargeTick++;
            }
            if (this.isChargeCollidedStopping() || this.isChargeNormalStopping())
                this.chargeStopTick++;
            if (this.hasShield()) {
                this.reflectNearbyProjectiles();
            }
            if (!this.hasShield()) {
                this.shieldRegenTimer++;
                if (this.shieldRegenTimer >= 2400) {
                    this.regenerateShield();
                }
            } else {
                this.shieldRegenTimer = 0;
            }
        }

        if (this.level().isClientSide && this.isAlive()) {
            float ws = this.walkAnimation.speed();
            this.walkAnimSpeed = ws <= 0.5F ? 0.5F : 1.0F;

            this.saceAnimationState.startIfStopped(this.tickCount);

            if (this.baseAnimTransitionTick > 0) {
                this.baseAnimTransitionTick--;

                String midDesired = this.computeDesiredAnimKey();
                if (!midDesired.isEmpty() && !midDesired.equals(this.transitionToKey)) {
                    this.startAnimationForKey(midDesired);
                    this.transitionFromKey = this.transitionToKey;
                    this.transitionToKey = midDesired;
                    this.baseAnimTransitionTick = BASE_ANIM_TRANSITION_DURATION;
                    this.currentAnimKey = midDesired;
                } else if (this.baseAnimTransitionTick == 0) {
                    this.stopAnimationsNotForKey(this.transitionToKey);
                }
            } else {
                String desiredKey = this.computeDesiredAnimKey();

                if (!desiredKey.isEmpty() && !desiredKey.equals(this.currentAnimKey)) {
                    if (!this.currentAnimKey.isEmpty()) {
                        this.startAnimationForKey(desiredKey);
                        this.transitionFromKey = this.currentAnimKey;
                        this.transitionToKey = desiredKey;
                        this.baseAnimTransitionTick = BASE_ANIM_TRANSITION_DURATION;
                    } else {
                        this.startAnimationForKey(desiredKey);
                        this.stopAnimationsNotForKey(desiredKey);
                    }
                } else {
                    this.startAnimationForKey(desiredKey);
                    this.stopAnimationsNotForKey(desiredKey);
                }

                this.currentAnimKey = desiredKey;
            }
        }
    }

    private void reflectNearbyProjectiles() {
        Vec3 facing = this.getViewVector(1.0F);
        AABB frontBox = this.getBoundingBox().inflate(0.8).expandTowards(facing.scale(1.0));
        boolean reflected = false;
        for (Projectile proj : this.level().getEntitiesOfClass(Projectile.class, frontBox)) {
            if (proj.getOwner() == this)
                continue;
            Vec3 toProj = proj.position().subtract(this.position()).normalize();
            if (facing.dot(toProj) > 0.3) {
                proj.setDeltaMovement(proj.getDeltaMovement().scale(-0.6));
                proj.setOwner(this);
                reflected = true;
            }
        }
        if (reflected && this.reflectSoundTimer <= 0) {
            this.playSound(ModSounds.TOWER_GUARD_DEFLECT.get(), 1.0F, 1.0F);
            this.reflectSoundTimer = 10;
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4)
            this.setFlags(1, true);
        else if (id == 5)
            this.setFlags(2, true);
        else if (id == 6)
            this.setFlags(4, true);
        else if (id == 7)
            this.setFlags(8, true);
        else if (id == 8)
            this.setFlags(16, true);

        if (id == 4) {
            this.attackAnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
            this.attackTick = 0;
        } else if (id == 5) {
            this.shieldBreakAnimationState.stop();
            this.shieldBreakAnimationState.start(this.tickCount);
            this.shieldBreakTick = 0;
        } else if (id == 6) {
            this.chargeAnimationState.stop();
            this.chargeAnimationState.start(this.tickCount);
            this.chargeTick = 0;
        } else if (id == 7) {
            this.chargeCollidedStopAnimationState.stop();
            this.chargeCollidedStopAnimationState.start(this.tickCount);
            this.chargeStopTick = 0;
        } else if (id == 8) {
            this.chargeNormalStopAnimationState.stop();
            this.chargeNormalStopAnimationState.start(this.tickCount);
            this.chargeStopTick = 0;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public boolean isActionAnimating() {
        return this.isMeleeAttacking() || this.isShieldBreaking()
                || this.isCharging() || this.isChargeCollidedStopping() || this.isChargeNormalStopping();
    }

    private String computeDesiredAnimKey() {
        if (this.isMeleeAttacking())
            return "action_attack";
        if (this.isShieldBreaking())
            return "action_shield_break";
        if (this.isCharging())
            return "action_charge";
        if (this.isChargeCollidedStopping())
            return "action_charge_collided_stop";
        if (this.isChargeNormalStopping())
            return "action_charge_normal_stop";

        if (this.isStaying())
            return "base_standing_by";
        if (this.isMoving())
            return "base_walk";
        return "base_idle";
    }

    private void startAnimationForKey(String key) {
        switch (key) {
            case "base_idle":
                this.idleAnimationState.startIfStopped(this.tickCount);
                break;
            case "base_walk":
                this.walkAnimationState.startIfStopped(this.tickCount);
                break;
            case "base_standing_by":
                this.standingByAnimationState.startIfStopped(this.tickCount);
                break;
            case "action_attack":
                this.attackAnimationState.startIfStopped(this.tickCount);
                break;
            case "action_shield_break":
                this.shieldBreakAnimationState.startIfStopped(this.tickCount);
                break;
            case "action_charge":
                this.chargeAnimationState.startIfStopped(this.tickCount);
                break;
            case "action_charge_collided_stop":
                this.chargeCollidedStopAnimationState.startIfStopped(this.tickCount);
                break;
            case "action_charge_normal_stop":
                this.chargeNormalStopAnimationState.startIfStopped(this.tickCount);
                break;
        }
    }

    private void stopAnimationsNotForKey(String key) {
        if (!key.equals("base_idle"))
            this.idleAnimationState.stop();
        if (!key.equals("base_walk"))
            this.walkAnimationState.stop();
        if (!key.equals("base_standing_by"))
            this.standingByAnimationState.stop();
        if (!key.equals("action_attack"))
            this.attackAnimationState.stop();
        if (!key.equals("action_shield_break"))
            this.shieldBreakAnimationState.stop();
        if (!key.equals("action_charge"))
            this.chargeAnimationState.stop();
        if (!key.equals("action_charge_collided_stop"))
            this.chargeCollidedStopAnimationState.stop();
        if (!key.equals("action_charge_normal_stop"))
            this.chargeNormalStopAnimationState.stop();
    }

    private void stopOtherActionAnimations() {
        if (!this.isMeleeAttacking())
            this.attackAnimationState.stop();
        if (!this.isShieldBreaking())
            this.shieldBreakAnimationState.stop();
        if (!this.isCharging())
            this.chargeAnimationState.stop();
        if (!this.isChargeCollidedStopping())
            this.chargeCollidedStopAnimationState.stop();
        if (!this.isChargeNormalStopping())
            this.chargeNormalStopAnimationState.stop();
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.walkAnimationState);
        list.add(this.attackAnimationState);
        list.add(this.shieldBreakAnimationState);
        list.add(this.chargeAnimationState);
        list.add(this.chargeCollidedStopAnimationState);
        list.add(this.chargeNormalStopAnimationState);
        list.add(this.standingByAnimationState);
        return list;
    }

    public void stopAllAnimations() {
        for (AnimationState a : this.getAnimations())
            a.stop();
    }

    public static List<Entity> getTargets(Level level, LivingEntity pSource, double pRange) {
        List<Entity> list = new ArrayList<>();
        Vec3 lookVec = pSource.getViewVector(1.0F);
        double[] lookRange = new double[] { lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange };
        List<Entity> possibleList = level.getEntities(pSource,
                pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]));
        for (Entity hit : possibleList) {
            if (hit.isPickable() && hit != pSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR
                    .and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(hit)) {
                list.add(hit);
            }
        }
        return list;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        return (double) (this.getBbWidth() * 6.0F * this.getBbWidth() * 6.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr) {
        return (distToEnemySqr <= this.getAttackReachSqr(enemy)
                || this.getBoundingBox().intersects(enemy.getBoundingBox()))
                && this.hasLineOfSight(enemy);
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TOWER_GUARD_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.TOWER_GUARD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TOWER_GUARD_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.TOWER_GUARD_STEP.get();
    }

    @Override
    protected void playStepSound(BlockPos pPos, net.minecraft.world.level.block.state.BlockState pBlock) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @Override
    public void setBaby(boolean pChildZombie) {
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    class TowerGuardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.0F;

        public TowerGuardAttackGoal() {
            super(TowerGuardServant.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            if (TowerGuardServant.this.isStaying())
                return false;
            return TowerGuardServant.this.getTarget() != null && TowerGuardServant.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            TowerGuardServant.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            TowerGuardServant.this.getNavigation().stop();
            if (TowerGuardServant.this.getTarget() == null)
                TowerGuardServant.this.setAggressive(false);
        }

        @Override
        public void tick() {
            LivingEntity target = TowerGuardServant.this.getTarget();
            if (target == null)
                return;
            TowerGuardServant.this.lookControl.setLookAt(target, 30.0F, 30.0F);
            double dist = TowerGuardServant.this.distanceToSqr(target.getX(), target.getY(), target.getZ());

            if (--this.delayCounter <= 0 && !TowerGuardServant.this.targetClose(target, dist)) {
                this.delayCounter = 10;
                TowerGuardServant.this.getNavigation().moveTo(target, SPEED);
            }

            this.checkAndPerformAttack(target, TowerGuardServant.this.distanceToSqr(
                    target.getX(), target.getBoundingBox().minY, target.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (!TowerGuardServant.this.isMeleeAttacking() && !TowerGuardServant.this.isShieldBreaking()
                    && !TowerGuardServant.this.isCharging() && !TowerGuardServant.this.isChargeCollidedStopping()
                    && !TowerGuardServant.this.isChargeNormalStopping()) {
                double dist = TowerGuardServant.this.distanceTo(enemy);
                if (!TowerGuardServant.this.hasShield() && dist > 4.0
                        && TowerGuardServant.this.chargeCooldown <= 0) {
                    TowerGuardServant.this.setShieldBreaking(true);
                } else if (TowerGuardServant.this.targetClose(enemy, distToEnemySqr)) {
                    TowerGuardServant.this.setMeleeAttacking(true);
                }
            }
        }
    }

    class MeleeGoal extends Goal {
        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return TowerGuardServant.this.getTarget() != null && TowerGuardServant.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return TowerGuardServant.this.attackTick < 25;
        }

        @Override
        public void start() {
            TowerGuardServant.this.setMeleeAttacking(true);
            TowerGuardServant.this.level().broadcastEntityEvent(TowerGuardServant.this, (byte) 4);
            TowerGuardServant.this.attackTick = 0;
        }

        @Override
        public void stop() {
            TowerGuardServant.this.setMeleeAttacking(false);
            TowerGuardServant.this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = TowerGuardServant.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(TowerGuardServant.this, target);
                TowerGuardServant.this.setYBodyRot(TowerGuardServant.this.getYHeadRot());
                TowerGuardServant.this.setYRot(TowerGuardServant.this.getYHeadRot());
            }

            if (TowerGuardServant.this.attackTick == 1) {
                TowerGuardServant.this.playSound(ModSounds.TOWER_GUARD_ATTACK_PREPARE.get(),
                        TowerGuardServant.this.getSoundVolume(), TowerGuardServant.this.getVoicePitch());
            }
            if (TowerGuardServant.this.attackTick == 10) {
                TowerGuardServant.this.playSound(ModSounds.TOWER_GUARD_ATTACK.get(),
                        TowerGuardServant.this.getSoundVolume(), TowerGuardServant.this.getVoicePitch());
            }

            if (TowerGuardServant.this.attackTick == 13) {
                LivingEntity livingentity = TowerGuardServant.this.getTarget();
                if (livingentity != null && TowerGuardServant.this.targetClose(livingentity,
                        TowerGuardServant.this.distanceToSqr(livingentity.getX(),
                                livingentity.getY(), livingentity.getZ()))) {
                    if (TowerGuardServant.this.doHurtTarget(livingentity)) {
                        for (Entity entity : getTargets(TowerGuardServant.this.level(), TowerGuardServant.this, 3)) {
                            if (entity instanceof LivingEntity living
                                    && TowerGuardServant.this.hasLineOfSight(living)
                                    && !MobUtil.areAllies(living, TowerGuardServant.this)
                                    && living != livingentity
                                    && (!(livingentity instanceof ArmorStand)
                                            || !((ArmorStand) livingentity).isMarker())) {
                                TowerGuardServant.this.doHurtTarget(living);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ShieldBreakGoal extends Goal {
        public ShieldBreakGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return TowerGuardServant.this.getTarget() != null && TowerGuardServant.this.isShieldBreaking();
        }

        @Override
        public boolean canContinueToUse() {
            return TowerGuardServant.this.shieldBreakTick < 32;
        }

        @Override
        public void start() {
            TowerGuardServant.this.setShieldBreaking(true);
            TowerGuardServant.this.level().broadcastEntityEvent(TowerGuardServant.this, (byte) 5);
            TowerGuardServant.this.shieldBreakTick = 0;
            TowerGuardServant.this.playSound(ModSounds.TOWER_GUARD_CHARGE_START.get(),
                    TowerGuardServant.this.getSoundVolume(), TowerGuardServant.this.getVoicePitch());
        }

        @Override
        public void stop() {
            if (TowerGuardServant.this.shieldBreakTick >= 32) {
                LivingEntity target = TowerGuardServant.this.getTarget();
                if (target != null) {
                    Vec3 dir = target.position().subtract(TowerGuardServant.this.position());
                    TowerGuardServant.this.chargeDirection = new Vec3(dir.x, 0, dir.z).normalize();
                } else {
                    float yawRad = TowerGuardServant.this.getYRot() * Mth.DEG_TO_RAD;
                    TowerGuardServant.this.chargeDirection = new Vec3(-Mth.sin(yawRad), 0, Mth.cos(yawRad));
                }
                TowerGuardServant.this.setCharging(true);
                TowerGuardServant.this.setShieldBreaking(false);
            } else {
                TowerGuardServant.this.setShieldBreaking(false);
                TowerGuardServant.this.shieldBreakTick = 0;
            }
        }

        @Override
        public void tick() {
            LivingEntity target = TowerGuardServant.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(TowerGuardServant.this, target);
            }
            if (TowerGuardServant.this.level() instanceof ServerLevel sl) {
                int tick = TowerGuardServant.this.shieldBreakTick;
                if (tick == 8 || tick == 14 || tick == 20) {
                    sl.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                            TowerGuardServant.this.getX(), TowerGuardServant.this.getY() + 0.1,
                            TowerGuardServant.this.getZ(), 3, 0.3, 0.1, 0.3, 0.1);
                    sl.sendParticles(
                            new RingParticle.RingData(0, (float) Math.PI / 2, 20,
                                    0.8f, 0.6f, 0.4f, 1.0f, 10f, false,
                                    RingParticle.EnumRingBehavior.GROW),
                            TowerGuardServant.this.getX(), TowerGuardServant.this.getY() + 0.15,
                            TowerGuardServant.this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    TowerGuardServant.this.playSound(ModSounds.TOWER_GUARD_STEP.get(),
                            TowerGuardServant.this.getSoundVolume(), TowerGuardServant.this.getVoicePitch());
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ChargeGoal extends Goal {
        private boolean shouldStopEarly = false;

        public ChargeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return TowerGuardServant.this.isCharging() && TowerGuardServant.this.chargeDirection.lengthSqr() > 0.01;
        }

        @Override
        public boolean canContinueToUse() {
            if (TowerGuardServant.this.chargeTick >= 60)
                return false;
            if (TowerGuardServant.this.horizontalCollision) {
                shouldStopEarly = true;
                return false;
            }
            if (this.checkCliffAhead()) {
                shouldStopEarly = true;
                return false;
            }
            return true;
        }

        private boolean checkCliffAhead() {
            Vec3 pos = TowerGuardServant.this.position();
            Vec3 ahead = pos.add(TowerGuardServant.this.chargeDirection.scale(1.5));
            BlockPos groundPos = BlockPos.containing(ahead.x, pos.y - 1, ahead.z);
            BlockPos selfGround = BlockPos.containing(pos.x, pos.y - 1, pos.z);
            double groundY = TowerGuardServant.this.level().getBlockState(groundPos).isAir()
                    ? pos.y
                    : groundPos.getY();
            double selfY = TowerGuardServant.this.level().getBlockState(selfGround).isAir()
                    ? pos.y
                    : selfGround.getY();
            return (selfY - groundY) > 3;
        }

        @Override
        public void start() {
            TowerGuardServant.this.setCharging(true);
            TowerGuardServant.this.level().broadcastEntityEvent(TowerGuardServant.this, (byte) 6);
            TowerGuardServant.this.chargeTick = 0;
            this.shouldStopEarly = false;
        }

        @Override
        public void stop() {
            TowerGuardServant.this.setCharging(false);
            TowerGuardServant.this.chargeCooldown = 600;
            if (shouldStopEarly) {
                TowerGuardServant.this.setChargeCollidedStopping(true);
                TowerGuardServant.this.level().broadcastEntityEvent(TowerGuardServant.this, (byte) 7);
                TowerGuardServant.this.playSound(ModSounds.TOWER_GUARD_CHARGE_END_COLLIDE.get(),
                        TowerGuardServant.this.getSoundVolume(), TowerGuardServant.this.getVoicePitch());
                if (TowerGuardServant.this.level() instanceof ServerLevel sl) {
                    Vec3 pos = TowerGuardServant.this.position();
                    Vec3 dir = TowerGuardServant.this.chargeDirection;
                    Vec3 impact = pos.add(dir.scale(1.2));
                    float ringYaw = (float) Math.atan2(-dir.x, dir.z);
                    for (int i = 0; i < 15; i++) {
                        sl.sendParticles(ParticleTypes.CLOUD,
                                impact.x, impact.y + 1.0, impact.z,
                                1, 0.3, 0.3, 0.3, 0.05);
                    }
                    for (int i = 0; i < 8; i++) {
                        sl.sendParticles(ParticleTypes.POOF,
                                impact.x, impact.y + 1.2, impact.z,
                                1, 0.2, 0.1, 0.2, 0.02);
                    }
                    sl.sendParticles(
                            new RingParticle.RingData(ringYaw, 0.0F, 16, 1.0f, 1.0f, 1.0f, 0.8f, 50f,
                                    false, RingParticle.EnumRingBehavior.GROW),
                            impact.x, impact.y + 1.0, impact.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            } else {
                TowerGuardServant.this.setChargeNormalStopping(true);
                TowerGuardServant.this.level().broadcastEntityEvent(TowerGuardServant.this, (byte) 8);
                TowerGuardServant.this.playSound(ModSounds.TOWER_GUARD_CHARGE_END_NORMAL.get(),
                        TowerGuardServant.this.getSoundVolume(), TowerGuardServant.this.getVoicePitch());
            }
        }

        @Override
        public void tick() {
            double speed = TowerGuardServant.this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.5;
            Vec3 dir = TowerGuardServant.this.chargeDirection;
            TowerGuardServant.this.setDeltaMovement(dir.x * speed, TowerGuardServant.this.getDeltaMovement().y,
                    dir.z * speed);

            float targetYaw = (float) Mth.atan2(dir.z, dir.x) * Mth.RAD_TO_DEG - 90F;
            TowerGuardServant.this.setYRot(targetYaw);
            TowerGuardServant.this.yBodyRot = targetYaw;
            TowerGuardServant.this.yHeadRot = targetYaw;
            if (TowerGuardServant.this.chargeTick % 4 == 0
                    && TowerGuardServant.this.level() instanceof ServerLevel sl) {
                float yaw = (float) Math.toRadians(-TowerGuardServant.this.getYRot());
                sl.sendParticles(
                        new RingParticle.RingData(
                                yaw, 0.0F, 30, 1.0f, 1.0f, 1.0f,
                                1.0f, 40f, false,
                                RingParticle.EnumRingBehavior.GROW),
                        TowerGuardServant.this.getX(), TowerGuardServant.this.getY() + 0.5,
                        TowerGuardServant.this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            if (TowerGuardServant.this.chargeTick % 4 == 0) {
                TowerGuardServant.this.playSound(ModSounds.TOWER_GUARD_STEP.get(),
                        TowerGuardServant.this.getSoundVolume(), TowerGuardServant.this.getVoicePitch());
            }

            if (!TowerGuardServant.this.level().isClientSide) {
                AABB attackBox = TowerGuardServant.this.getBoundingBox().inflate(0.5).expandTowards(dir.scale(1.5));
                double chargeDmg = TowerGuardServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE)
                        + TowerGuardServant.this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 20;
                for (LivingEntity t : TowerGuardServant.this.level().getEntitiesOfClass(LivingEntity.class,
                        attackBox)) {
                    if (t != TowerGuardServant.this && !MobUtil.areAllies(t, TowerGuardServant.this)) {
                        boolean hurt = t.hurt(TowerGuardServant.this.damageSources().mobAttack(TowerGuardServant.this),
                                (float) chargeDmg);
                        if (hurt) {
                            Vec3 knockback = dir.scale(1.5);
                            t.push(knockback.x, 0.3, knockback.z);
                            TowerGuardServant.this.playSound(ModSounds.TOWER_GUARD_CHARGE_END_COLLIDE.get(),
                                    TowerGuardServant.this.getSoundVolume(), TowerGuardServant.this.getVoicePitch());
                        }
                    }
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ChargeStopGoal extends Goal {
        private int maxDuration;

        public ChargeStopGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return TowerGuardServant.this.isChargeCollidedStopping() || TowerGuardServant.this.isChargeNormalStopping();
        }

        @Override
        public boolean canContinueToUse() {
            return TowerGuardServant.this.chargeStopTick < this.maxDuration;
        }

        @Override
        public void start() {
            if (TowerGuardServant.this.isChargeCollidedStopping()) {
                this.maxDuration = 21;
            } else {
                this.maxDuration = 22;
            }
            TowerGuardServant.this.chargeStopTick = 0;
        }

        @Override
        public void stop() {
            TowerGuardServant.this.setChargeCollidedStopping(false);
            TowerGuardServant.this.setChargeNormalStopping(false);
            TowerGuardServant.this.chargeStopTick = 0;
            TowerGuardServant.this.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public void tick() {
            TowerGuardServant.this.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
