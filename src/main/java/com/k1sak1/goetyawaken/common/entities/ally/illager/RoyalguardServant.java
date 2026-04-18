package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.items.ModItems;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class RoyalguardServant extends AbstractIllagerServant implements ICustomAttributes {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(RoyalguardServant.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(RoyalguardServant.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHIELD_HIDDEN = SynchedEntityData.defineId(
            RoyalguardServant.class,
            EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public int shieldHealth = 0;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState standAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState patrolWalkAnimationState = new AnimationState();

    public RoyalguardServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.miscGoal();
        this.goalSelector.addGoal(1, new RaiderServant.ObtainLeaderBannerGoal<>(this));
        this.attackGoal();
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
        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            this.setItemSlot(equipmentslot, ItemStack.EMPTY);
        }
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

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof RoyalguardServant;
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
        return ModSounds.BLACKGUARD_STEP.get();
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
    public boolean hurt(DamageSource source, float amount) {

        if (!this.level().isClientSide) {
            if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.absorbDamageWithShield(amount);
                return false;
            }
            if (this.getTarget() != null) {
                if (source.getEntity() instanceof LivingEntity livingEntity) {
                    LivingEntity target = this.getTarget();
                    double d0 = target != null ? this.distanceTo(target) : 0.0D;
                    double d1 = this.distanceTo(livingEntity);
                    if (MobUtil.ownedCanAttack(this, livingEntity) && livingEntity != this.getTrueOwner()) {
                        if (d0 > d1) {
                            this.setTarget(livingEntity);
                        }
                    }
                }
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if (!this.hasShield()) {
            super.knockback(p_147241_, p_147242_, p_147243_);
        }
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

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!this.level().isClientSide) {
                if (this.validFood(itemstack)) {
                    if (this.canHaveMoreFood() && this.getInventory().canAddItem(itemstack)) {
                        this.getInventory().addItem(itemstack.copyWithCount(1));
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                        if (this.level() instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 7; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02D;
                                double d1 = this.random.nextGaussian() * 0.02D;
                                double d2 = this.random.nextGaussian() * 0.02D;
                                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                                        this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
                if (item == ModItems.PALE_STEEL_INGOT.get() && this.getTarget() == null
                        && this.hurtTime <= 0) {
                    if (!this.hasShield() || this.isShieldHidden()) {
                        if (!pPlayer.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        this.setShield(true);
                        this.setShieldHealth(0);
                        this.setShieldHidden(false);
                        this.level().broadcastEntityEvent(this, (byte) 6);
                        this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (this.canBeLeader()
                    && !this.getLeaderBannerInstance().isEmpty()
                    && com.Polarice3.Goety.utils.ItemHelper.sameBanner(itemstack, this.getBannerPatternInstance())
                    && !ItemStack.matches(this.getItemBySlot(EquipmentSlot.HEAD), this.getLeaderBannerInstance())) {
                ItemStack helmet = this.getItemBySlot(EquipmentSlot.HEAD);
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                this.playSound(this.getCelebrateSound(), 1.0F, this.getVoicePitch());
                this.dropEquipment(EquipmentSlot.HEAD, helmet);
                this.setItemSlot(EquipmentSlot.HEAD, this.getLeaderBannerInstance());
                this.setGuaranteedDrop(EquipmentSlot.HEAD);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                            this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHIELD, true);
        this.entityData.define(SHIELD_HIDDEN, false);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    class RoyalguardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.0F;

        public RoyalguardAttackGoal() {
            super(RoyalguardServant.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            boolean hasTarget = RoyalguardServant.this.getTarget() != null;
            boolean isTargetAlive = hasTarget && RoyalguardServant.this.getTarget().isAlive();
            boolean result = hasTarget && isTargetAlive;

            return result;
        }

        @Override
        public void start() {
            RoyalguardServant.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            RoyalguardServant.this.getNavigation().stop();
            if (RoyalguardServant.this.getTarget() == null) {
                RoyalguardServant.this.setAggressive(false);
            }
        }

        @Override
        public void tick() {

            LivingEntity livingentity = RoyalguardServant.this.getTarget();
            if (livingentity == null) {

                return;
            }

            RoyalguardServant.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = RoyalguardServant.this.distanceToSqr(livingentity.getX(), livingentity.getY(),
                    livingentity.getZ());

            if (--this.delayCounter <= 0 && !RoyalguardServant.this.targetClose(livingentity, d0)) {

                this.delayCounter = 10;
                RoyalguardServant.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, RoyalguardServant.this.distanceToSqr(livingentity.getX(),
                    livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (RoyalguardServant.this.targetClose(enemy, distToEnemySqr)) {
                if (!RoyalguardServant.this.isMeleeAttacking()) {
                    RoyalguardServant.this.setMeleeAttacking(true);
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
            boolean hasTarget = RoyalguardServant.this.getTarget() != null;
            boolean isAttacking = RoyalguardServant.this.isMeleeAttacking();
            boolean result = hasTarget && isAttacking;

            return result;
        }

        @Override
        public boolean canContinueToUse() {
            boolean result = RoyalguardServant.this.attackTick < 28;

            return result;
        }

        @Override
        public void start() {
            RoyalguardServant.this.setMeleeAttacking(true);
            RoyalguardServant.this.level().broadcastEntityEvent(RoyalguardServant.this, (byte) 4);
            RoyalguardServant.this.attackTick = 0;
        }

        @Override
        public void stop() {
            RoyalguardServant.this.setMeleeAttacking(false);
            RoyalguardServant.this.attackTick = 0;
        }

        @Override
        public void tick() {

            if (RoyalguardServant.this.getTarget() != null) {
                LivingEntity livingentity = RoyalguardServant.this.getTarget();
                MobUtil.instaLook(RoyalguardServant.this, livingentity);
                RoyalguardServant.this.setYBodyRot(RoyalguardServant.this.getYHeadRot());
                RoyalguardServant.this.setYRot(RoyalguardServant.this.getYHeadRot());
            }

            if (RoyalguardServant.this.attackTick == 1) {
                SoundEvent[] attackSounds = {
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_ATTACK_1.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_ATTACK_2.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_ATTACK_3.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_ATTACK_4.get()
                };
                RoyalguardServant.this.playSound(
                        attackSounds[RoyalguardServant.this.random.nextInt(attackSounds.length)],
                        RoyalguardServant.this.getSoundVolume() + 1.0F, RoyalguardServant.this.getVoicePitch());
            }
            if (RoyalguardServant.this.attackTick == 9) {
                SoundEvent[] smashSounds = {
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SMASH_1.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SMASH_2.get(),
                        com.k1sak1.goetyawaken.init.ModSounds.ROYAL_GUARD_SMASH_3.get()
                };
                RoyalguardServant.this.playSound(smashSounds[RoyalguardServant.this.random.nextInt(smashSounds.length)],
                        RoyalguardServant.this.getSoundVolume() + 1.0F, RoyalguardServant.this.getVoicePitch());
            }
            if (RoyalguardServant.this.attackTick == 14) {
                double x = RoyalguardServant.this.getX() + RoyalguardServant.this.getHorizontalLookAngle().x * 2;
                double z = RoyalguardServant.this.getZ() + RoyalguardServant.this.getHorizontalLookAngle().z * 2;
                AABB aabb = MobUtil.makeAttackRange(x,
                        RoyalguardServant.this.getY(),
                        z, 3, 3, 3);
                for (LivingEntity target : RoyalguardServant.this.level().getEntitiesOfClass(LivingEntity.class,
                        aabb)) {
                    if (target != RoyalguardServant.this && !MobUtil.areAllies(target, RoyalguardServant.this)) {
                        RoyalguardServant.this.doHurtTarget(target);
                    }
                }
                if (RoyalguardServant.this.level() instanceof ServerLevel serverLevel) {
                    BlockPos blockPos = BlockPos.containing(x, RoyalguardServant.this.getY() - 1.0F, z);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK,
                            serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 2; ++i) {
                        ServerParticleUtil.circularParticles(serverLevel, option,
                                RoyalguardServant.this.getX() + RoyalguardServant.this.getHorizontalLookAngle().x * 2,
                                RoyalguardServant.this.getY() + 0.25D,
                                RoyalguardServant.this.getZ() + RoyalguardServant.this.getHorizontalLookAngle().z * 2,
                                1.5F);
                    }
                    ColorUtil colorUtil = new ColorUtil(
                            serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                    serverLevel.sendParticles(
                            new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(colorUtil.red(),
                                    colorUtil.green(), colorUtil.blue(), 1.5F, 1),
                            x, BlockFinder.moveDownToGround(RoyalguardServant.this), z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}