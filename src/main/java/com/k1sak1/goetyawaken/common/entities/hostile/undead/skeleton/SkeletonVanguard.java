package com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import com.Polarice3.Goety.config.AttributesConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import net.minecraft.world.phys.Vec3;
import java.util.function.Predicate;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.util.Mth;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.MobSpawnType;

public class SkeletonVanguard extends Skeleton {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(SkeletonVanguard.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(SkeletonVanguard.class,
            EntityDataSerializers.BOOLEAN);
    public int attackTick;
    public int shieldHealth = 1;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState stayingAnimationState = new AnimationState();

    public SkeletonVanguard(EntityType<? extends Skeleton> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(4, new VanguardAttackGoal());
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        super.registerGoals();
    }

    class VanguardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.25F;

        public VanguardAttackGoal() {
            super(SkeletonVanguard.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            return SkeletonVanguard.this.getTarget() != null && SkeletonVanguard.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            SkeletonVanguard.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = SkeletonVanguard.this.getTarget();
            if (livingentity == null) {
                return;
            }

            SkeletonVanguard.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = SkeletonVanguard.this.distanceToSqr(livingentity.getX(), livingentity.getY(),
                    livingentity.getZ());

            if (--this.delayCounter <= 0 && !SkeletonVanguard.this.targetClose(livingentity, d0)) {
                this.delayCounter = 10;
                SkeletonVanguard.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, SkeletonVanguard.this.distanceToSqr(livingentity.getX(),
                    livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (SkeletonVanguard.this.targetClose(enemy, distToEnemySqr)) {
                if (!SkeletonVanguard.this.isMeleeAttacking()) {
                    SkeletonVanguard.this.setMeleeAttacking(true);
                }
            }
        }

        @Override
        protected void resetAttackCooldown() {
        }

        @Override
        public void stop() {
            SkeletonVanguard.this.getNavigation().stop();
            if (SkeletonVanguard.this.getTarget() == null) {
                SkeletonVanguard.this.setAggressive(false);
            }
        }
    }

    class MeleeGoal extends Goal {
        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return SkeletonVanguard.this.getTarget() != null && SkeletonVanguard.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return SkeletonVanguard.this.isMeleeAttacking() && SkeletonVanguard.this.attackTick < 20;
        }

        @Override
        public void start() {
            SkeletonVanguard.this.setMeleeAttacking(true);
            SkeletonVanguard.this.level().broadcastEntityEvent(SkeletonVanguard.this, (byte) 4);
        }

        @Override
        public void stop() {
            SkeletonVanguard.this.setMeleeAttacking(false);
        }

        @Override
        public void tick() {
            if (SkeletonVanguard.this.getTarget() != null && SkeletonVanguard.this.getTarget().isAlive()) {
                LivingEntity livingentity = SkeletonVanguard.this.getTarget();
                double d0 = SkeletonVanguard.this.distanceToSqr(livingentity.getX(), livingentity.getY(),
                        livingentity.getZ());
                SkeletonVanguard.this.getLookControl().setLookAt(livingentity, SkeletonVanguard.this.getMaxHeadYRot(),
                        SkeletonVanguard.this.getMaxHeadXRot());
                SkeletonVanguard.this.setYBodyRot(SkeletonVanguard.this.getYHeadRot());
                if (SkeletonVanguard.this.attackTick == 8) {
                    if (SkeletonVanguard.this.targetClose(livingentity, d0)) {
                        if (SkeletonVanguard.this.doHurtTarget(livingentity)) {
                            SkeletonVanguard.this.playSound(ModSounds.VANGUARD_SPEAR.get(), 1.0F, 1.0F);
                            for (Entity entity : getTargets(SkeletonVanguard.this.level(), SkeletonVanguard.this, 3)) {
                                if (entity instanceof LivingEntity living
                                        && SkeletonVanguard.this.hasLineOfSight(living)) {
                                    if (!living.isAlliedTo(SkeletonVanguard.this)
                                            && !SkeletonVanguard.this.isAlliedTo(living)
                                            && (living == livingentity
                                                    || living.getLastHurtByMob() == SkeletonVanguard.this)
                                            && (!(livingentity instanceof ArmorStand)
                                                    || !((ArmorStand) livingentity).isMarker())) {
                                        SkeletonVanguard.this.doHurtTarget(living);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        public static List<Entity> getTargets(Level level, LivingEntity pSource, double pRange) {
            List<Entity> list = new ArrayList<>();
            Vec3 lookVec = pSource.getViewVector(1.0F);
            double[] lookRange = new double[] { lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange };
            List<Entity> possibleList = level.getEntities(pSource,
                    pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]));

            Predicate<Entity> selector = entity -> entity.isPickable() && entity != pSource
                    && entity instanceof LivingEntity;
            for (Entity hit : possibleList) {
                if (selector.test(hit)) {
                    list.add(hit);
                }
            }
            return list;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Skeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.VanguardServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.VanguardServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.VanguardServantArmor.get());
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

    protected double getAttackReachSqr(LivingEntity enemy) {
        return this.getBbWidth() * 6.0F * this.getBbWidth() * 6.0F + enemy.getBbWidth();
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
                    serverLevel.sendParticles(
                            new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPRUCE_PLANKS)), this.getX(),
                            this.getY() + 1.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
        }
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.VANGUARD_STEP.get();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.VANGUARD_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.VANGUARD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.VANGUARD_DEATH.get();
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void reassessWeaponGoal() {
    }

    @Override
    public void setBaby(boolean pChildSkeleton) {
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
    }

    private boolean getVanguardFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setVanguardFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.getVanguardFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setVanguardFlags(1, attacking);
        this.attackTick = 0;
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    @Override
    public boolean doHurtTarget(Entity p_21372_) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (p_21372_ instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) p_21372_).getMobType());
            f1 += (float) EnchantmentHelper.getKnockbackBonus(this);
        }

        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            p_21372_.setSecondsOnFire(i * 4);
        }

        boolean flag = p_21372_.hurt(this.damageSources().mobAttack(this), f);
        if (flag) {
            if (f1 > 0.0F && p_21372_ instanceof LivingEntity living) {
                living.knockback((double) (f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)),
                        (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
            }

            this.doEnchantDamageEffects(this, p_21372_);
            this.setLastHurtMob(p_21372_);
        }

        return flag;
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.stayingAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        for (AnimationState animationState : this.getAnimations()) {
            animationState.stop();
        }
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
    public void die(DamageSource source) {
        if (!this.level().isClientSide && source.getDirectEntity() != null &&
                (source.getDirectEntity().getType().toString().contains("necro_bolt"))) {
            if (source.getEntity() instanceof Player player) {
                this.convertToServant(player);
            }
        }
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
                if (!this.isMeleeAttacking()) {
                    this.attackAnimationState.stop();
                    if (!this.isMoving()) {
                        this.walkAnimationState.stop();
                        if (this.isStaying() && !this.isPassenger()) {
                            this.idleAnimationState.stop();
                            this.stayingAnimationState.startIfStopped(this.tickCount);
                        } else {
                            this.idleAnimationState.startIfStopped(this.tickCount);
                            this.stayingAnimationState.stop();
                        }
                    } else {
                        this.idleAnimationState.stop();
                        this.stayingAnimationState.stop();
                        this.walkAnimationState.startIfStopped(this.tickCount);
                    }
                } else {
                    this.idleAnimationState.stop();
                    this.walkAnimationState.stop();
                    this.stayingAnimationState.stop();
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
            if (this.attackTick > 20) {
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
            com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant servant = (com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant) this
                    .convertTo(com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get(), true);
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
}