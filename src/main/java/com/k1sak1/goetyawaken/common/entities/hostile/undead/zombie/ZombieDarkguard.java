package com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.config.AttributesConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

import java.util.EnumSet;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.BlockFinder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;

public class ZombieDarkguard extends Zombie {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(ZombieDarkguard.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(ZombieDarkguard.class,
            EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public int shieldHealth = 1;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState standAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();

    public ZombieDarkguard(EntityType<? extends Zombie> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.attackGoal();
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.addBehaviourGoals();

    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5,
                new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    protected void attackGoal() {
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(4, new BlackguardAttackGoal());
    }

    class BlackguardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.0F;

        public BlackguardAttackGoal() {
            super(ZombieDarkguard.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            return ZombieDarkguard.this.getTarget() != null
                    && ZombieDarkguard.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            ZombieDarkguard.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            ZombieDarkguard.this.getNavigation().stop();
            if (ZombieDarkguard.this.getTarget() == null) {
                ZombieDarkguard.this.setAggressive(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingentity = ZombieDarkguard.this.getTarget();
            if (livingentity == null) {
                return;
            }

            ZombieDarkguard.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = ZombieDarkguard.this.distanceToSqr(livingentity.getX(), livingentity.getY(),
                    livingentity.getZ());

            if (--this.delayCounter <= 0 && !ZombieDarkguard.this.targetClose(livingentity, d0)) {
                this.delayCounter = 10;
                ZombieDarkguard.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, ZombieDarkguard.this.distanceToSqr(livingentity.getX(),
                    livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (ZombieDarkguard.this.targetClose(enemy, distToEnemySqr)) {
                if (!ZombieDarkguard.this.isMeleeAttacking()) {
                    ZombieDarkguard.this.setMeleeAttacking(true);
                }
            }
        }
    }

    class MeleeGoal extends Goal {
        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return ZombieDarkguard.this.getTarget() != null
                    && ZombieDarkguard.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return ZombieDarkguard.this.isMeleeAttacking()
                    && ZombieDarkguard.this.attackTick < MathHelper.secondsToTicks(1.42F);
        }

        @Override
        public void start() {
            ZombieDarkguard.this.setMeleeAttacking(true);
            ZombieDarkguard.this.level().broadcastEntityEvent(ZombieDarkguard.this, (byte) 4);
        }

        @Override
        public void stop() {
            ZombieDarkguard.this.setMeleeAttacking(false);
        }

        @Override
        public void tick() {
            if (ZombieDarkguard.this.getTarget() != null) {
                LivingEntity livingentity = ZombieDarkguard.this.getTarget();
                MobUtil.instaLook(ZombieDarkguard.this, livingentity);
                ZombieDarkguard.this.setYBodyRot(ZombieDarkguard.this.getYHeadRot());
                ZombieDarkguard.this.setYRot(ZombieDarkguard.this.getYHeadRot());
            }

            if (ZombieDarkguard.this.attackTick == 1) {
                ZombieDarkguard.this.playSound(ModSounds.BLACKGUARD_PRE_ATTACK.get(),
                        ZombieDarkguard.this.getSoundVolume() + 1.0F, ZombieDarkguard.this.getVoicePitch());
            }
            if (ZombieDarkguard.this.attackTick == 9) {
                ZombieDarkguard.this.playSound(ModSounds.BLACKGUARD_SMASH.get(),
                        ZombieDarkguard.this.getSoundVolume() + 1.0F, ZombieDarkguard.this.getVoicePitch());
            }
            if (ZombieDarkguard.this.attackTick == 14) {
                double x = ZombieDarkguard.this.getX() + ZombieDarkguard.this.getHorizontalLookAngle().x * 2;
                double z = ZombieDarkguard.this.getZ() + ZombieDarkguard.this.getHorizontalLookAngle().z * 2;
                AABB aabb = MobUtil.makeAttackRange(x,
                        ZombieDarkguard.this.getY(),
                        z, 3, 3, 3);
                for (LivingEntity target : ZombieDarkguard.this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (target != ZombieDarkguard.this && !MobUtil.areAllies(target, ZombieDarkguard.this)
                            && (target == ZombieDarkguard.this.getTarget()
                                    || target.getLastHurtByMob() == ZombieDarkguard.this)) {
                        ZombieDarkguard.this.doHurtTarget(target);
                    }
                }
                if (ZombieDarkguard.this.level() instanceof ServerLevel serverLevel) {
                    BlockPos blockPos = BlockPos.containing(x, ZombieDarkguard.this.getY() - 1.0F, z);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK,
                            serverLevel.getBlockState(blockPos));
                    for (int i = 0; i < 2; ++i) {
                        serverLevel.sendParticles(option,
                                ZombieDarkguard.this.getX() + ZombieDarkguard.this.getHorizontalLookAngle().x * 2,
                                ZombieDarkguard.this.getY() + 0.25D,
                                ZombieDarkguard.this.getZ() + ZombieDarkguard.this.getHorizontalLookAngle().z * 2, 1,
                                0.0D, 0.0D, 0.0D, 0.0D);
                    }
                    double groundY = BlockFinder.moveDownToGround(ZombieDarkguard.this);
                    ColorUtil colorUtil = new ColorUtil(
                            serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                    serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(),
                            colorUtil.blue(), 1.5F, 1), x, groundY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BlackguardServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BlackguardServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.BlackguardServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.BlackguardServantToughness.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHIELD, true);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("hasShield")) {
            this.setShield(pCompound.getBoolean("hasShield"));
        }
        if (pCompound.contains("ShieldHeath")) {
            this.setShieldHealth(pCompound.getInt("ShieldHeath"));
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasShield", this.hasShield());
        pCompound.putInt("ShieldHeath", this.getShieldHealth());
    }

    public boolean hasShield() {
        return this.entityData.get(HAS_SHIELD);
    }

    public void setShield(boolean shield) {
        this.entityData.set(HAS_SHIELD, shield);
    }

    public int getShieldHealth() {
        return this.shieldHealth;
    }

    public void setShieldHealth(int shieldHealth) {
        this.shieldHealth = shieldHealth;
    }

    public boolean isStaying() {
        return false;
    }

    public boolean isMoving() {
        return this.getDeltaMovement().lengthSqr() > 0.01D;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        return (double) (this.getBbWidth() * 6.0F * this.getBbWidth() * 6.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr) {
        return (distToEnemySqr <= this.getAttackReachSqr(enemy)
                || this.getBoundingBox().intersects(enemy.getBoundingBox())) && this.hasLineOfSight(enemy);
    }

    public void destroyShield() {
        if (this.hasShield()) {
            if (this.getShieldHealth() > 1) {
                this.setShieldHealth(this.getShieldHealth() - 1);
                this.playSound(SoundEvents.SHIELD_BLOCK);
            } else {
                this.setShieldHealth(0);
                this.setShield(false);
                this.playSound(SoundEvents.SHIELD_BREAK);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ANVIL)),
                            this.getX(), this.getY() + 1.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
        }
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

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.standAnimationState);
        animationStates.add(this.attackAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        for (AnimationState animationState : this.getAnimations()) {
            animationState.stop();
        }
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.BLACKGUARD_STEP.get();
    }

    protected void playHurtSound(DamageSource p_21160_) {
        super.playHurtSound(p_21160_);
        this.playSound(ModSounds.PLATE.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public void setBaby(boolean pChildZombie) {
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    public void reassessWeaponGoal() {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.destroyShield();
                return false;
            } else {
                if (this.getTarget() != null) {
                    if (source.getEntity() instanceof LivingEntity livingEntity) {
                        double d0 = this.distanceTo(this.getTarget());
                        double d1 = this.distanceTo(livingEntity);
                        if (MobUtil.canAttack(this, livingEntity)) {
                            if (d0 > d1) {
                                this.setTarget(livingEntity);
                            }
                        }
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && source.getDirectEntity() != null &&
                (source.getDirectEntity().getType().toString().contains("necro_bolt"))) {
            if (source.getEntity() instanceof Player player) {
                this.convertToServant(player);
            }
        }
        this.playSound(ModSounds.PLATE_DROP.get(), this.getSoundVolume(), this.getVoicePitch());
        super.die(source);
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if (!this.hasShield()) {
            super.knockback(p_147241_, p_147242_, p_147243_);
        }
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.isAlive()) {
                this.idleAnimationState.animateWhen(!this.isMeleeAttacking() && !this.isStaying() && !this.isMoving(),
                        this.tickCount);
                this.standAnimationState.animateWhen(!this.isMeleeAttacking() && this.isStaying() && !this.isMoving(),
                        this.tickCount);
                if (!this.isMeleeAttacking()) {
                    this.attackAnimationState.stop();
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
            if (this.attackTick > MathHelper.secondsToTicks(1.42F)) {
                this.setMeleeAttacking(false);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.stopAllAnimations();
            this.attackAnimationState.start(this.tickCount);
        } else if (p_21375_ == 5) {
            this.attackTick = 0;
        } else if (p_21375_ == 6) {
            this.setShield(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    private boolean convertToServant(Player player) {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlackguardServant servant = (BlackguardServant) this
                    .convertTo(com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get(), true);
            if (servant != null) {
                servant.setTrueOwner(player);
                if (this.getTarget() != null) {
                    servant.setTarget(this.getTarget());
                }
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack stack = this.getItemBySlot(slot);
                    if (!stack.isEmpty()) {
                        servant.setItemSlot(slot, stack.copy());
                    }
                }
                servant.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(servant.blockPosition()),
                        MobSpawnType.CONVERSION, null, null);
                if (!servant.isSilent()) {
                    servant.level().levelEvent(null, 1026, servant.blockPosition(), 0);
                }
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, servant);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
    }
}