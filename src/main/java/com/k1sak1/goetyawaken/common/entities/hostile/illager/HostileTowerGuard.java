package com.k1sak1.goetyawaken.common.entities.hostile.illager;

import com.k1sak1.goetyawaken.client.particle.RingParticle;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class HostileTowerGuard extends HuntingIllagerEntity implements ICustomAttributes {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(HostileTowerGuard.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(HostileTowerGuard.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHIELD_HIDDEN = SynchedEntityData
            .defineId(HostileTowerGuard.class, EntityDataSerializers.BOOLEAN);

    public int attackTick;
    public float walkAnimSpeed = 1.0F;
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

    public HostileTowerGuard(EntityType<? extends HuntingIllagerEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ChargeStopGoal());
        this.goalSelector.addGoal(2, new ChargeGoal());
        this.goalSelector.addGoal(3, new ShieldBreakGoal());
        this.goalSelector.addGoal(4, new MeleeGoal());
        this.goalSelector.addGoal(5, new TowerGuardAttackGoal());
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D, 120, false));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean p_213660_2_) {
    }

    @Override
    public boolean canJoinRaid() {
        return true;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EMPTY;
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

    private boolean getFlag(int mask) {
        return (this.entityData.get(DATA_FLAGS_ID) & mask) != 0;
    }

    private void setFlags(int mask, boolean v) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (v)
            i |= mask;
        else
            i &= ~mask;
        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    public boolean isMeleeAttacking() {
        return getFlag(1);
    }

    public void setMeleeAttacking(boolean v) {
        setFlags(1, v);
        this.attackTick = 0;
    }

    public boolean isShieldBreaking() {
        return getFlag(2);
    }

    public void setShieldBreaking(boolean v) {
        setFlags(2, v);
        this.shieldBreakTick = 0;
    }

    public boolean isCharging() {
        return getFlag(4);
    }

    public void setCharging(boolean v) {
        setFlags(4, v);
        this.chargeTick = 0;
    }

    public boolean isChargeCollidedStopping() {
        return getFlag(8);
    }

    public void setChargeCollidedStopping(boolean v) {
        setFlags(8, v);
        this.chargeStopTick = 0;
    }

    public boolean isChargeNormalStopping() {
        return getFlag(16);
    }

    public void setChargeNormalStopping(boolean v) {
        setFlags(16, v);
        this.chargeStopTick = 0;
    }

    public boolean hasShield() {
        return this.entityData.get(HAS_SHIELD);
    }

    public void setShield(boolean s) {
        this.entityData.set(HAS_SHIELD, s);
    }

    public boolean isShieldHidden() {
        return this.entityData.get(SHIELD_HIDDEN);
    }

    public void setShieldHidden(boolean h) {
        this.entityData.set(SHIELD_HIDDEN, h);
    }

    public void setShieldHealth(float h) {
        this.shieldHealth = h;
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
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR))
            amount = net.minecraft.world.damagesource.CombatRules.getDamageAfterAbsorb(amount,
                    (float) this.getArmorValue(), (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        return amount;
    }

    private boolean isDamageFromFront(DamageSource source) {
        Entity a = source.getEntity();
        if (a == null)
            return false;
        return a.position().subtract(this.position()).normalize().dot(this.getViewVector(1.0F)) > 0.3;
    }

    private void absorbDamageWithShield(float amount) {
        float n = this.shieldHealth - amount;
        if (n <= 0) {
            this.destroyShield();
        } else {
            this.shieldHealth = n;
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
            if (this.hasShield() && this.shieldInvulnTime > 0)
                return false;
            if (this.hasShield() && source.is(DamageTypeTags.IS_PROJECTILE)) {
                return false;
            }
            if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                    && this.isDamageFromFront(source)) {
                float fd = this.getDamageAfterArmorAbsorb(source, amount);
                this.absorbDamageWithShield(fd);
                this.shieldInvulnTime = 10;
                return false;
            }
            if (this.getTarget() != null && source.getEntity() instanceof LivingEntity le) {
                if (!MobUtil.areAllies(le, this) && le.distanceTo(this) < this.distanceTo(this.getTarget()))
                    this.setTarget(le);
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double x, double y, double z) {
        if (!this.hasShield())
            super.knockback(x, y, z);
    }

    public boolean isMoving() {
        return !(this.walkAnimation.speed() < 0.01F);
    }

    public boolean isStaying() {
        return false;
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
            this.attackAnimationState.start(tickCount);
            attackTick = 0;
        } else if (id == 5) {
            this.shieldBreakAnimationState.stop();
            this.shieldBreakAnimationState.start(tickCount);
            shieldBreakTick = 0;
        } else if (id == 6) {
            this.chargeAnimationState.stop();
            this.chargeAnimationState.start(tickCount);
            chargeTick = 0;
        } else if (id == 7) {
            this.chargeCollidedStopAnimationState.stop();
            this.chargeCollidedStopAnimationState.start(tickCount);
            chargeStopTick = 0;
        } else if (id == 8) {
            this.chargeNormalStopAnimationState.stop();
            this.chargeNormalStopAnimationState.start(tickCount);
            chargeStopTick = 0;
        } else
            super.handleEntityEvent(id);
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

        if (this.isStaying() && !this.isMoving())
            return "base_standing_by";
        if (!this.isStaying() && this.isMoving())
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
        return List.of(idleAnimationState, walkAnimationState, attackAnimationState, shieldBreakAnimationState,
                chargeAnimationState, chargeCollidedStopAnimationState, chargeNormalStopAnimationState,
                standingByAnimationState);
    }

    public void stopAllAnimations() {
        for (AnimationState a : getAnimations())
            a.stop();
    }

    public static List<Entity> getTargets(Level l, LivingEntity s, double r) {
        List<Entity> list = new ArrayList<>();
        Vec3 lv = s.getViewVector(1.0F);
        double[] lr = new double[] { lv.x() * r, lv.y() * r, lv.z() * r };
        for (Entity e : l.getEntities(s, s.getBoundingBox().expandTowards(lr[0], lr[1], lr[2]))) {
            if (e.isPickable() && e != s
                    && EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(e))
                list.add(e);
        }
        return list;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    protected double getAttackReachSqr(LivingEntity e) {
        return (double) (this.getBbWidth() * 6.0F * this.getBbWidth() * 6.0F + e.getBbWidth());
    }

    public boolean targetClose(LivingEntity e, double d) {
        return (d <= getAttackReachSqr(e) || this.getBoundingBox().intersects(e.getBoundingBox()))
                && this.hasLineOfSight(e);
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public void setBaby(boolean b) {
    }

    class TowerGuardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.0F;

        public TowerGuardAttackGoal() {
            super(HostileTowerGuard.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            return HostileTowerGuard.this.getTarget() != null && HostileTowerGuard.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            HostileTowerGuard.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            HostileTowerGuard.this.getNavigation().stop();
            if (HostileTowerGuard.this.getTarget() == null)
                HostileTowerGuard.this.setAggressive(false);
        }

        @Override
        public void tick() {
            LivingEntity t = HostileTowerGuard.this.getTarget();
            if (t == null)
                return;
            HostileTowerGuard.this.lookControl.setLookAt(t, 30.0F, 30.0F);
            double d = HostileTowerGuard.this.distanceToSqr(t.getX(), t.getY(), t.getZ());
            if (--this.delayCounter <= 0 && !HostileTowerGuard.this.targetClose(t, d)) {
                this.delayCounter = 10;
                HostileTowerGuard.this.getNavigation().moveTo(t, SPEED);
            }
            this.checkAndPerformAttack(t,
                    HostileTowerGuard.this.distanceToSqr(t.getX(), t.getBoundingBox().minY, t.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity e, double d) {
            if (!HostileTowerGuard.this.isMeleeAttacking() && !HostileTowerGuard.this.isShieldBreaking()
                    && !HostileTowerGuard.this.isCharging() && !HostileTowerGuard.this.isChargeCollidedStopping()
                    && !HostileTowerGuard.this.isChargeNormalStopping()) {
                double dist = HostileTowerGuard.this.distanceTo(e);
                if (!HostileTowerGuard.this.hasShield() && dist > 4.0
                        && HostileTowerGuard.this.chargeCooldown <= 0) {
                    HostileTowerGuard.this.setShieldBreaking(true);
                } else if (HostileTowerGuard.this.targetClose(e, d)) {
                    HostileTowerGuard.this.setMeleeAttacking(true);
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
            return HostileTowerGuard.this.getTarget() != null && HostileTowerGuard.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return HostileTowerGuard.this.attackTick < 25;
        }

        @Override
        public void start() {
            HostileTowerGuard.this.setMeleeAttacking(true);
            HostileTowerGuard.this.level().broadcastEntityEvent(HostileTowerGuard.this, (byte) 4);
            HostileTowerGuard.this.attackTick = 0;
        }

        @Override
        public void stop() {
            HostileTowerGuard.this.setMeleeAttacking(false);
            HostileTowerGuard.this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity t = HostileTowerGuard.this.getTarget();
            if (t != null) {
                MobUtil.instaLook(HostileTowerGuard.this, t);
                HostileTowerGuard.this.setYBodyRot(HostileTowerGuard.this.getYHeadRot());
                HostileTowerGuard.this.setYRot(HostileTowerGuard.this.getYHeadRot());
            }
            if (HostileTowerGuard.this.attackTick == 1) {
                HostileTowerGuard.this.playSound(ModSounds.TOWER_GUARD_ATTACK_PREPARE.get(),
                        HostileTowerGuard.this.getSoundVolume(), HostileTowerGuard.this.getVoicePitch());
            }
            if (HostileTowerGuard.this.attackTick == 10) {
                HostileTowerGuard.this.playSound(ModSounds.TOWER_GUARD_ATTACK.get(),
                        HostileTowerGuard.this.getSoundVolume(), HostileTowerGuard.this.getVoicePitch());
            }
            if (HostileTowerGuard.this.attackTick == 13) {
                LivingEntity le = HostileTowerGuard.this.getTarget();
                if (le != null && HostileTowerGuard.this.targetClose(le,
                        HostileTowerGuard.this.distanceToSqr(le.getX(), le.getY(), le.getZ()))) {
                    if (HostileTowerGuard.this.doHurtTarget(le)) {
                        for (Entity e : getTargets(HostileTowerGuard.this.level(), HostileTowerGuard.this, 3)) {
                            if (e instanceof LivingEntity l && HostileTowerGuard.this.hasLineOfSight(l)
                                    && !MobUtil.areAllies(l, HostileTowerGuard.this) && l != le
                                    && (!(le instanceof ArmorStand)
                                            || !((ArmorStand) le).isMarker()))
                                HostileTowerGuard.this.doHurtTarget(l);
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
            return HostileTowerGuard.this.getTarget() != null && HostileTowerGuard.this.isShieldBreaking();
        }

        @Override
        public boolean canContinueToUse() {
            return HostileTowerGuard.this.shieldBreakTick < 32;
        }

        @Override
        public void start() {
            HostileTowerGuard.this.setShieldBreaking(true);
            HostileTowerGuard.this.level().broadcastEntityEvent(HostileTowerGuard.this, (byte) 5);
            HostileTowerGuard.this.shieldBreakTick = 0;
            HostileTowerGuard.this.playSound(ModSounds.TOWER_GUARD_CHARGE_START.get(),
                    HostileTowerGuard.this.getSoundVolume(), HostileTowerGuard.this.getVoicePitch());
        }

        @Override
        public void stop() {
            if (HostileTowerGuard.this.shieldBreakTick >= 32) {
                LivingEntity t = HostileTowerGuard.this.getTarget();
                Vec3 d;
                if (t != null) {
                    d = t.position().subtract(HostileTowerGuard.this.position());
                } else {
                    float y = Mth.DEG_TO_RAD * HostileTowerGuard.this.getYRot();
                    d = new Vec3(-Mth.sin(y), 0, Mth.cos(y));
                }
                HostileTowerGuard.this.chargeDirection = new Vec3(d.x, 0, d.z).normalize();
                HostileTowerGuard.this.setCharging(true);
                HostileTowerGuard.this.setShieldBreaking(false);
            } else {
                HostileTowerGuard.this.setShieldBreaking(false);
                HostileTowerGuard.this.shieldBreakTick = 0;
            }
        }

        @Override
        public void tick() {
            LivingEntity t = HostileTowerGuard.this.getTarget();
            if (t != null)
                MobUtil.instaLook(HostileTowerGuard.this, t);
            if (HostileTowerGuard.this.level() instanceof ServerLevel sl) {
                int tk = HostileTowerGuard.this.shieldBreakTick;
                if (tk == 8 || tk == 14 || tk == 20) {
                    sl.sendParticles(ParticleTypes.ANGRY_VILLAGER, HostileTowerGuard.this.getX(),
                            HostileTowerGuard.this.getY() + 0.1, HostileTowerGuard.this.getZ(), 3, 0.3, 0.1, 0.3, 0.1);
                    sl.sendParticles(
                            new RingParticle.RingData(0, (float) Math.PI / 2, 20, 0.8f, 0.6f, 0.4f, 1.0f, 10f,
                                    false, RingParticle.EnumRingBehavior.GROW),
                            HostileTowerGuard.this.getX(), HostileTowerGuard.this.getY() + 0.15,
                            HostileTowerGuard.this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    HostileTowerGuard.this.playSound(ModSounds.TOWER_GUARD_STEP.get(),
                            HostileTowerGuard.this.getSoundVolume(), HostileTowerGuard.this.getVoicePitch());
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
            return HostileTowerGuard.this.isCharging() && HostileTowerGuard.this.chargeDirection.lengthSqr() > 0.01;
        }

        @Override
        public boolean canContinueToUse() {
            if (HostileTowerGuard.this.chargeTick >= 60)
                return false;
            if (HostileTowerGuard.this.horizontalCollision) {
                shouldStopEarly = true;
                return false;
            }
            if (checkCliff()) {
                shouldStopEarly = true;
                return false;
            }
            return true;
        }

        private boolean checkCliff() {
            Vec3 p = HostileTowerGuard.this.position();
            Vec3 a = p.add(HostileTowerGuard.this.chargeDirection.scale(1.5));
            double gy = HostileTowerGuard.this.level().getBlockState(BlockPos.containing(a.x, p.y - 1, a.z)).isAir()
                    ? p.y
                    : BlockPos.containing(a.x, p.y - 1, a.z).getY();
            double sy = HostileTowerGuard.this.level().getBlockState(BlockPos.containing(p.x, p.y - 1, p.z)).isAir()
                    ? p.y
                    : BlockPos.containing(p.x, p.y - 1, p.z).getY();
            return (sy - gy) > 3;
        }

        @Override
        public void start() {
            HostileTowerGuard.this.setCharging(true);
            HostileTowerGuard.this.level().broadcastEntityEvent(HostileTowerGuard.this, (byte) 6);
            HostileTowerGuard.this.chargeTick = 0;
            this.shouldStopEarly = false;
        }

        @Override
        public void stop() {
            HostileTowerGuard.this.setCharging(false);
            HostileTowerGuard.this.chargeCooldown = 600;
            if (shouldStopEarly) {
                HostileTowerGuard.this.setChargeCollidedStopping(true);
                HostileTowerGuard.this.level().broadcastEntityEvent(HostileTowerGuard.this, (byte) 7);
                HostileTowerGuard.this.playSound(ModSounds.TOWER_GUARD_CHARGE_END_COLLIDE.get(),
                        HostileTowerGuard.this.getSoundVolume(), HostileTowerGuard.this.getVoicePitch());
                if (HostileTowerGuard.this.level() instanceof ServerLevel sl) {
                    Vec3 pos = HostileTowerGuard.this.position();
                    Vec3 dir = HostileTowerGuard.this.chargeDirection;
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
                HostileTowerGuard.this.setChargeNormalStopping(true);
                HostileTowerGuard.this.level().broadcastEntityEvent(HostileTowerGuard.this, (byte) 8);
                HostileTowerGuard.this.playSound(ModSounds.TOWER_GUARD_CHARGE_END_NORMAL.get(),
                        HostileTowerGuard.this.getSoundVolume(), HostileTowerGuard.this.getVoicePitch());
            }
        }

        @Override
        public void tick() {
            Vec3 d = HostileTowerGuard.this.chargeDirection;
            double sp = HostileTowerGuard.this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.5;
            HostileTowerGuard.this.setDeltaMovement(d.x * sp, HostileTowerGuard.this.getDeltaMovement().y, d.z * sp);
            float ty = (float) Math.atan2(d.z, d.x) * Mth.RAD_TO_DEG - 90F;
            HostileTowerGuard.this.setYRot(ty);
            HostileTowerGuard.this.yBodyRot = ty;
            HostileTowerGuard.this.yHeadRot = ty;
            if (HostileTowerGuard.this.chargeTick % 4 == 0
                    && HostileTowerGuard.this.level() instanceof ServerLevel sl) {
                float yaw = (float) Math.toRadians(-HostileTowerGuard.this.getYRot());
                sl.sendParticles(
                        new RingParticle.RingData(yaw, 0.0F, 30, 1.0f, 1.0f, 1.0f, 1.0f, 40f,
                                false, RingParticle.EnumRingBehavior.GROW),
                        HostileTowerGuard.this.getX(), HostileTowerGuard.this.getY() + 0.5,
                        HostileTowerGuard.this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (HostileTowerGuard.this.chargeTick % 4 == 0) {
                HostileTowerGuard.this.playSound(ModSounds.TOWER_GUARD_STEP.get(),
                        HostileTowerGuard.this.getSoundVolume(), HostileTowerGuard.this.getVoicePitch());
            }
            if (!HostileTowerGuard.this.level().isClientSide) {
                AABB ab = HostileTowerGuard.this.getBoundingBox().inflate(0.5).expandTowards(d.scale(1.5));
                double cd = HostileTowerGuard.this.getAttributeValue(Attributes.ATTACK_DAMAGE)
                        + HostileTowerGuard.this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 20;
                for (LivingEntity tg : HostileTowerGuard.this.level().getEntitiesOfClass(LivingEntity.class, ab)) {
                    if (tg != HostileTowerGuard.this && !MobUtil.areAllies(tg, HostileTowerGuard.this)) {
                        boolean h = tg.hurt(HostileTowerGuard.this.damageSources().mobAttack(HostileTowerGuard.this),
                                (float) cd);
                        if (h) {
                            Vec3 kb = d.scale(1.5);
                            tg.push(kb.x, 0.3, kb.z);
                            HostileTowerGuard.this.playSound(ModSounds.TOWER_GUARD_CHARGE_END_COLLIDE.get(),
                                    HostileTowerGuard.this.getSoundVolume(), HostileTowerGuard.this.getVoicePitch());
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
        private int maxDur;

        public ChargeStopGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return HostileTowerGuard.this.isChargeCollidedStopping() || HostileTowerGuard.this.isChargeNormalStopping();
        }

        @Override
        public boolean canContinueToUse() {
            return HostileTowerGuard.this.chargeStopTick < this.maxDur;
        }

        @Override
        public void start() {
            if (HostileTowerGuard.this.isChargeCollidedStopping()) {
                this.maxDur = 21;
            } else {
                this.maxDur = 22;
            }
            HostileTowerGuard.this.chargeStopTick = 0;
        }

        @Override
        public void stop() {
            HostileTowerGuard.this.setChargeCollidedStopping(false);
            HostileTowerGuard.this.setChargeNormalStopping(false);
            HostileTowerGuard.this.chargeStopTick = 0;
            HostileTowerGuard.this.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public void tick() {
            HostileTowerGuard.this.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
