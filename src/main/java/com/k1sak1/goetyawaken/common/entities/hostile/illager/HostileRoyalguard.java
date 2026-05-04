package com.k1sak1.goetyawaken.common.entities.hostile.illager;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class HostileRoyalguard extends AbstractIllager implements ICustomAttributes {
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState standAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState patrolWalkAnimationState = new AnimationState();

    @Override
    public void applyRaidBuffs(int pWave, boolean p_213660_2_) {
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(HostileRoyalguard.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(HostileRoyalguard.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHIELD_HIDDEN = SynchedEntityData.defineId(
            HostileRoyalguard.class,
            EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public int shieldHealth = 0;

    public HostileRoyalguard(EntityType<? extends AbstractIllager> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.miscGoal();
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1,
                (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        this.attackGoal();
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new RoyalguardWanderGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    static class RoyalguardWanderGoal extends RandomStrollGoal {
        public RoyalguardWanderGoal(AbstractIllager p_25983_, double p_25984_) {
            super(p_25983_, p_25984_, 120, false);
        }
    }

    public void attackGoal() {
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(4, new RoyalguardAttackGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.RoyalguardServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RoyalguardServantDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.ARMOR, AttributesConfig.RoyalguardServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.RoyalguardServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.RoyalguardServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.30);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.RoyalguardServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.RoyalguardServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.RoyalguardServantArmorToughness.get());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        return pSpawnData;
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
        if (pCompound.contains("hasShield")) {
            this.setShield(pCompound.getBoolean("hasShield"));
        }
        if (pCompound.contains("ShieldHeath")) {
            this.setShieldHealth(pCompound.getInt("ShieldHeath"));
        }
        if (pCompound.contains("ShieldHidden")) {
            this.setShieldHidden(pCompound.getBoolean("ShieldHidden"));
        } else if (pCompound.contains("hasShield")) {
            this.setShieldHidden(!pCompound.getBoolean("hasShield"));
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasShield", this.hasShield());
        pCompound.putInt("ShieldHeath", this.getShieldHealth());
        pCompound.putBoolean("ShieldHidden", this.isShieldHidden());
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
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

    public int getShieldHealth() {
        return this.shieldHealth;
    }

    public void setShieldHealth(int shieldHealth) {
        this.shieldHealth = shieldHealth;
    }

    public void destroyShield() {
        if (this.hasShield()) {
            this.setShieldHealth(0);
            this.setShield(false);
            this.setShieldHidden(true);
            SoundEvent[] shieldBreakSounds = {
                    com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SHIELD_BREAK_1.get(),
                    com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SHIELD_BREAK_2.get(),
                    com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SHIELD_BREAK_3.get()
            };
            this.playSound(shieldBreakSounds[this.random.nextInt(shieldBreakSounds.length)], this.getSoundVolume(),
                    this.getVoicePitch());
            if (this.level() instanceof ServerLevel serverLevel) {
                ServerParticleUtil.addParticlesAroundSelf(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK,
                        net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState()), this);
            }
        }
    }

    public void absorbDamageWithShield(float amount) {
        int currentShieldHealth = this.getShieldHealth();
        int newShieldHealth = currentShieldHealth + (int) Math.ceil(amount);
        if (newShieldHealth >= 10) {
            this.destroyShield();
        } else {
            this.setShieldHealth(newShieldHealth);
            this.playSound(SoundEvents.SHIELD_BLOCK);
        }
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    protected boolean isSunSensitive() {
        return false;
    }

    protected boolean convertsInWater() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        SoundEvent[] deathSounds = {
                com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_DEATH_1.get(),
                com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_DEATH_2.get()
        };
        return deathSounds[this.random.nextInt(deathSounds.length)];
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        SoundEvent[] hurtSounds = {
                com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_HURT.get(),
                com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_HURT_3.get()
        };
        return hurtSounds[this.random.nextInt(hurtSounds.length)];
    }

    @Override
    protected void playHurtSound(DamageSource pSource) {
        super.playHurtSound(pSource);
        this.playSound(ModSounds.PLATE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public void die(DamageSource pDamageSource) {
        this.playSound(ModSounds.PLATE_DROP.get(), this.getSoundVolume(), this.getVoicePitch());
        super.die(pDamageSource);
    }

    protected SoundEvent getStepSound() {
        return com.Polarice3.Goety.init.ModSounds.BLACKGUARD_STEP.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    @Override
    public void setBaby(boolean pChildZombie) {
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.isAlive()) {
                this.idleAnimationState.animateWhen(!this.isMeleeAttacking() && !this.isStaying() && !this.isMoving(),
                        this.tickCount);
                this.standAnimationState.animateWhen(!this.isMeleeAttacking() && this.isStaying() && !this.isMoving(),
                        this.tickCount);

                boolean isMoving = this.isMoving();
                float currentSpeed = this.walkAnimation.speed();
                float patrolSpeedThreshold = 0.5f;

                if (!this.isMeleeAttacking() && isMoving) {
                    if (currentSpeed <= patrolSpeedThreshold) {
                        this.patrolWalkAnimationState.startIfStopped(this.tickCount);
                        this.walkAnimationState.stop();
                    } else {
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.patrolWalkAnimationState.stop();
                    }
                } else {
                    this.walkAnimationState.stop();
                    this.patrolWalkAnimationState.stop();
                }

                if (!this.isMeleeAttacking()) {
                    this.attackAnimationState.stop();
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
        }

        if (this.tickCount % 100 == 0 && this.random.nextInt(3) == 0 && !this.isMeleeAttacking() && this.isAlive() &&
                !this.isMoving() && this.getTarget() == null) {
            SoundEvent[] idleSounds = {
                    com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_IDLE_1.get(),
                    com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_IDLE_2.get(),
                    com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_IDLE_3.get(),
                    com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_IDLE_4.get()
            };
            this.playSound(idleSounds[this.random.nextInt(idleSounds.length)], this.getSoundVolume() * 0.8F,
                    this.getVoicePitch());
        }
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if (!this.hasShield()) {
            super.knockback(p_147241_, p_147242_, p_147243_);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if (!this.level().isClientSide) {
            if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.is(DamageTypeTags.BYPASSES_SHIELD) ) {
                this.absorbDamageWithShield(amount);
                return false;
            }
            if (this.getTarget() != null) {
                if (source.getEntity() instanceof LivingEntity livingEntity) {
                    LivingEntity target = this.getTarget();
                    double d0 = target != null ? this.distanceTo(target) : 0.0D;
                    double d1 = this.distanceTo(livingEntity);
                }
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.stopAllAnimations();
            this.attackAnimationState.start(this.tickCount);
            this.attackTick = 0;
        } else if (p_21375_ == 5) {
            this.attackTick = 0;
        } else if (p_21375_ == 6) {
            this.setShield(true);
            this.setShieldHealth(0);
            this.setShieldHidden(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        return (double) (this.getBbWidth() * 6.0F * this.getBbWidth() * 6.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr) {
        double attackReach = this.getAttackReachSqr(enemy);
        boolean inRange = (distToEnemySqr <= attackReach
                || this.getBoundingBox().intersects(enemy.getBoundingBox()));
        boolean hasLineOfSight = this.hasLineOfSight(enemy);

        return inRange && hasLineOfSight;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    public static List<Entity> getTargets(Level level, LivingEntity pSource, double pRange) {
        List<Entity> list = new ArrayList<>();
        List<Entity> possibleList = level.getEntities(pSource,
                pSource.getBoundingBox().inflate(pRange, pRange, pRange));

        for (Entity hit : possibleList) {
            if (hit.isPickable() && hit != pSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR
                    .and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(hit)) {
                double distance = pSource.distanceTo(hit);
                if (distance <= pRange) {
                    list.add(hit);
                }
            }
        }
        return list;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHIELD, true);
        this.entityData.define(SHIELD_HIDDEN, false);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    public boolean isMoving() {
        return !(this.walkAnimation.speed() < 0.01F);
    }

    public boolean isStaying() {
        return false;
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.standAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.patrolWalkAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        for (AnimationState animationState : this.getAnimations()) {
            animationState.stop();
        }
        this.patrolWalkAnimationState.stop();
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    class RoyalguardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.0F;

        public RoyalguardAttackGoal() {
            super(HostileRoyalguard.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            boolean hasTarget = HostileRoyalguard.this.getTarget() != null;
            boolean isTargetAlive = hasTarget && HostileRoyalguard.this.getTarget().isAlive();
            boolean result = hasTarget && isTargetAlive;

            return result;
        }

        @Override
        public void start() {
            HostileRoyalguard.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            HostileRoyalguard.this.getNavigation().stop();
            if (HostileRoyalguard.this.getTarget() == null) {
                HostileRoyalguard.this.setAggressive(false);
            }
        }

        @Override
        public void tick() {

            LivingEntity livingentity = HostileRoyalguard.this.getTarget();
            if (livingentity == null) {

                return;
            }

            HostileRoyalguard.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = HostileRoyalguard.this.distanceToSqr(livingentity.getX(), livingentity.getY(),
                    livingentity.getZ());

            if (--this.delayCounter <= 0 && !HostileRoyalguard.this.targetClose(livingentity, d0)) {

                this.delayCounter = 10;
                HostileRoyalguard.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, HostileRoyalguard.this.distanceToSqr(livingentity.getX(),
                    livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (HostileRoyalguard.this.targetClose(enemy, distToEnemySqr)) {
                if (!HostileRoyalguard.this.isMeleeAttacking()) {
                    HostileRoyalguard.this.setMeleeAttacking(true);
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
            boolean hasTarget = HostileRoyalguard.this.getTarget() != null;
            boolean isAttacking = HostileRoyalguard.this.isMeleeAttacking();
            boolean result = hasTarget && isAttacking;

            return result;
        }

        @Override
        public boolean canContinueToUse() {
            boolean result = HostileRoyalguard.this.attackTick < 28;

            return result;
        }

        @Override
        public void start() {
            HostileRoyalguard.this.setMeleeAttacking(true);
            HostileRoyalguard.this.level().broadcastEntityEvent(HostileRoyalguard.this, (byte) 4);
            HostileRoyalguard.this.attackTick = 0;
        }

        @Override
        public void stop() {
            HostileRoyalguard.this.setMeleeAttacking(false);
            HostileRoyalguard.this.attackTick = 0;
        }

        @Override
        public void tick() {

            if (HostileRoyalguard.this.getTarget() != null) {
                LivingEntity livingentity = HostileRoyalguard.this.getTarget();
                MobUtil.instaLook(HostileRoyalguard.this, livingentity);
                HostileRoyalguard.this.setYBodyRot(HostileRoyalguard.this.getYHeadRot());
                HostileRoyalguard.this.setYRot(HostileRoyalguard.this.getYHeadRot());
            }

            if (HostileRoyalguard.this.attackTick == 1) {
                SoundEvent[] attackSounds = {
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_ATTACK_1.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_ATTACK_2.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_ATTACK_3.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_ATTACK_4.get()
                };
                HostileRoyalguard.this.playSound(
                        attackSounds[HostileRoyalguard.this.random.nextInt(attackSounds.length)],
                        HostileRoyalguard.this.getSoundVolume() + 1.0F, HostileRoyalguard.this.getVoicePitch());
            }
            if (HostileRoyalguard.this.attackTick == 9) {
                SoundEvent[] smashSounds = {
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SMASH_1.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SMASH_2.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SMASH_3.get()
                };
                HostileRoyalguard.this.playSound(smashSounds[HostileRoyalguard.this.random.nextInt(smashSounds.length)],
                        HostileRoyalguard.this.getSoundVolume() + 1.0F, HostileRoyalguard.this.getVoicePitch());
            }
            if (HostileRoyalguard.this.attackTick == 14) {
                double x = HostileRoyalguard.this.getX() + HostileRoyalguard.this.getHorizontalLookAngle().x * 2;
                double z = HostileRoyalguard.this.getZ() + HostileRoyalguard.this.getHorizontalLookAngle().z * 2;
                AABB aabb = MobUtil.makeAttackRange(x,
                        HostileRoyalguard.this.getY(),
                        z, 3, 3, 3);
                for (LivingEntity target : HostileRoyalguard.this.level().getEntitiesOfClass(LivingEntity.class,
                        aabb)) {
                    if (target != HostileRoyalguard.this && !MobUtil.areAllies(target, HostileRoyalguard.this)
                            && target == HostileRoyalguard.this.getTarget()) {
                        HostileRoyalguard.this.doHurtTarget(target);
                    }
                }
                if (HostileRoyalguard.this.level() instanceof ServerLevel serverLevel) {
                    BlockPos blockPos = BlockPos.containing(x, HostileRoyalguard.this.getY() - 1.0F, z);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK,
                            serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 2; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option,
                                HostileRoyalguard.this.getX() + HostileRoyalguard.this.getHorizontalLookAngle().x * 2,
                                HostileRoyalguard.this.getY() + 0.25D,
                                HostileRoyalguard.this.getZ() + HostileRoyalguard.this.getHorizontalLookAngle().z * 2,
                                1.5F);
                    }
                    ColorUtil colorUtil = new ColorUtil(
                            serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                    serverLevel.sendParticles(
                            new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(colorUtil.red(),
                                    colorUtil.green(), colorUtil.blue(), 1.5F, 1),
                            x, BlockFinder.moveDownToGround(HostileRoyalguard.this), z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
