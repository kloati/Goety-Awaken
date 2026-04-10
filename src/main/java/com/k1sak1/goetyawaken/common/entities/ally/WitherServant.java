package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModWitherSkullNoBlockBreak;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class WitherServant extends Summoned
        implements RangedAttackMob, PowerableMob, com.Polarice3.Goety.api.entities.IAutoRideable,
        com.Polarice3.Goety.api.entities.ICustomAttributes {
    private static final EntityDataAccessor<Integer> DATA_TARGET_A = SynchedEntityData.defineId(WitherServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_B = SynchedEntityData.defineId(WitherServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_C = SynchedEntityData.defineId(WitherServant.class,
            EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_ID_INV = SynchedEntityData
            .defineId(WitherServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_FIRST_SPAWN = SynchedEntityData
            .defineId(WitherServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(WitherServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_GOING_UP = SynchedEntityData
            .defineId(WitherServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_GOING_DOWN = SynchedEntityData
            .defineId(WitherServant.class, EntityDataSerializers.BOOLEAN);
    private static final int INVULNERABILITY_TIME = 220;
    private int invulnerableTicks = 0;
    private int shootCooldown = 0;
    private int teleportCooldown = 0;
    private int servantInvulnerableTicks = 0;
    private final float[] xRotHeads = new float[2];
    private final float[] yRotHeads = new float[2];
    private final float[] xRotOHeads = new float[2];
    private final float[] yRotOHeads = new float[2];
    private final int[] nextHeadUpdate = new int[2];
    private final int[] idleHeadUpdates = new int[2];
    private static final List<EntityDataAccessor<Integer>> DATA_TARGETS = ImmutableList.of(DATA_TARGET_A, DATA_TARGET_B,
            DATA_TARGET_C);
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (p_31504_) -> {
        return p_31504_.attackable();
    };
    private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(20.0D)
            .selector(LIVING_ENTITY_SELECTOR);

    public WitherServant(EntityType<? extends WitherServant> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setHealth(this.getMaxHealth() / 2.0F);
        this.xpReward = 50;

        if (this.isFirstSpawn()) {
            this.makeInvulnerable();
        }
        for (int i = 0; i < this.nextHeadUpdate.length; ++i) {
            this.nextHeadUpdate[i] = 40 + this.random.nextInt(40);
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double) this.getBbHeight() * 0.75D + 0.7D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TARGET_A, 0);
        this.entityData.define(DATA_TARGET_B, 0);
        this.entityData.define(DATA_TARGET_C, 0);
        this.entityData.define(DATA_ID_INV, 0);
        this.entityData.define(DATA_IS_FIRST_SPAWN, true);
        this.entityData.define(AUTO_MODE, false);
        this.entityData.define(DATA_IS_GOING_UP, false);
        this.entityData.define(DATA_IS_GOING_DOWN, false);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth() / 2);
        return pSpawnData;
    }

    public float getHeadYRot(int pHead) {
        if (pHead >= 0 && pHead < 3) {
            if (pHead == 0) {
                return this.yBodyRot;
            } else {
                return this.yRotHeads[pHead - 1];
            }
        }
        return 0.0F;
    }

    public float getHeadXRot(int pHead) {
        if (pHead >= 0 && pHead < 3) {
            if (pHead == 0) {
                return this.getXRot();
            } else {
                return this.xRotHeads[pHead - 1];
            }
        }
        return 0.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new WitherDoNothingGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        super.registerGoals();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WitherServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.6D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ARMOR, AttributesConfig.WitherServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.WitherServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.WitherServantHealth.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.6D);
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.FLYING_SPEED), 0.6D);
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), 40.0D);
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.WitherServantArmor.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.WitherServantArmorToughness.get());
    }

    public boolean isPowered() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    protected double getHeadX(int pHead) {
        if (pHead <= 0) {
            return this.getX();
        } else {
            float f = (this.yBodyRot + (float) (180 * (pHead - 1))) * ((float) Math.PI / 180F);
            float f1 = Mth.cos(f);
            return this.getX() + (double) f1 * 1.3D;
        }
    }

    protected double getHeadY(int pHead) {
        return pHead <= 0 ? this.getY() + 3.0D : this.getY() + 2.2D;
    }

    protected double getHeadZ(int pHead) {
        if (pHead <= 0) {
            return this.getZ();
        } else {
            float f = (this.yBodyRot + (float) (180 * (pHead - 1))) * ((float) Math.PI / 180F);
            float f1 = Mth.sin(f);
            return this.getZ() + (double) f1 * 1.3D;
        }
    }

    protected float rotlerp(float pAngle, float pTargetAngle, float pMaxIncrease) {
        float f = Mth.wrapDegrees(pTargetAngle - pAngle);
        if (f > pMaxIncrease) {
            f = pMaxIncrease;
        }

        if (f < -pMaxIncrease) {
            f = -pMaxIncrease;
        }

        return pAngle + f;
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        this.shootSkullAt(0, pTarget);
    }

    private void shootSkullAt(int pHead, LivingEntity pTarget) {
        this.shootSkullAt(pHead, pTarget.getX(), pTarget.getY() + (double) pTarget.getEyeHeight() * 0.5D,
                pTarget.getZ());
    }

    private void shootSkullAt(int pHead, double pX, double pY, double pZ) {
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1024, this.blockPosition(), 0);
        }

        double d0 = this.getHeadX(pHead);
        double d1 = this.getHeadY(pHead);
        double d2 = this.getHeadZ(pHead);
        double d3 = pX - d0;
        double d4 = pY - d1;
        double d5 = pZ - d2;
        ModWitherSkullNoBlockBreak witherskull = new ModWitherSkullNoBlockBreak(this.level(), this, d3, d4, d5);
        witherskull.setOwner(this);
        witherskull.setPos(d0, d1, d2);
        float baseExplosionPower = 2.0F;
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float explosionPower = baseExplosionPower + attackDamage * 0.2F;
        witherskull.setExplosionPower(Math.max(1.0F, explosionPower));
        this.level().addFreshEntity(witherskull);
    }

    private void shootSkullAt(int pHead, double pX, double pY, double pZ, boolean pIsBlueSkull) {
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1024, this.blockPosition(), 0);
        }

        double d0 = this.getHeadX(pHead);
        double d1 = this.getHeadY(pHead);
        double d2 = this.getHeadZ(pHead);
        double d3 = pX - d0;
        double d4 = pY - d1;
        double d5 = pZ - d2;
        ModWitherSkullNoBlockBreak witherskull = new ModWitherSkullNoBlockBreak(this.level(), this, d3, d4, d5);
        if (pIsBlueSkull) {
            witherskull.setDangerous(true);
        }
        witherskull.setOwner(this);
        witherskull.setPos(d0, d1, d2);
        float baseExplosionPower = 2.0F;
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float explosionPower = baseExplosionPower + attackDamage * 0.2F;
        witherskull.setExplosionPower(Math.max(1.0F, explosionPower));
        this.level().addFreshEntity(witherskull);
    }

    public void shootSkullAtDirection(double targetX, double targetY, double targetZ) {
        this.shootSkullAt(0, targetX, targetY, targetZ);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {

    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.isVehicle() && rider instanceof Player player && !this.isAutonomous()) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float baseSpeed = (float) this.getAttributeValue(Attributes.FLYING_SPEED);
                float speedFactor = 0.5F;
                float finalSpeed = baseSpeed * speedFactor;
                float strafe = rider.xxa * finalSpeed;
                float forward = rider.zza * finalSpeed;

                if (forward <= 0.0F) {
                    forward *= 0.25F;
                }
                Vec3 moveVector = new Vec3(strafe, 0.0D, forward);
                this.moveRelative(finalSpeed, moveVector);
                double verticalMotion = this.getDeltaMovement().y;
                boolean isFlyingUp = this.isGoingUp();
                boolean isFlyingDown = this.isGoingDown();
                if (isFlyingUp) {
                    verticalMotion += 0.15D;
                    if (this.onGround()) {
                        verticalMotion += 0.3D;
                        this.setOnGround(false);
                    }
                }
                if (isFlyingDown) {
                    verticalMotion -= 0.15D;
                }
                if (!this.onGround()) {
                    if (!isFlyingUp && !isFlyingDown) {
                        verticalMotion -= 0.00D;
                    } else {
                        verticalMotion -= 0.00D;
                    }
                }
                verticalMotion = Mth.clamp(verticalMotion, -0.5D, 0.5D);
                this.setDeltaMovement(this.getDeltaMovement().x, verticalMotion, this.getDeltaMovement().z);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
                this.lerpSteps = 0;
                this.calculateEntityAnimation(false);
                if (this.servantInvulnerableTicks > 0) {
                    this.servantInvulnerableTicks--;
                }
                return;
            }
        }
        if (this.servantInvulnerableTicks > 0) {
            super.travel(pTravelVector);
            return;
        }

        super.travel(pTravelVector);
    }

    public int getShootCooldown() {
        return this.shootCooldown;
    }

    public void setShootCooldown(int cooldown) {
        this.shootCooldown = cooldown;
    }

    @Override
    public void tick() {
        if (this.invulnerableTicks > 0) {
            this.invulnerableTicks--;
        }
        if (this.shootCooldown > 0) {
            this.shootCooldown--;
        }
        if (this.teleportCooldown > 0) {
            this.teleportCooldown--;
        }
        super.tick();
        if (!this.level().isClientSide() && this.isAlive()) {
            LivingEntity owner = this.getTrueOwner();
            if (owner != null && this.distanceToSqr(owner) > 1024.0D && this.getTarget() == null
                    && this.teleportCooldown <= 0 && this.isFollowing()) {
                this.teleportTowardsEntity(owner);
                this.teleportCooldown = 40;
            }
        }
        this.checkForArmorEffect();
    }

    @Override
    public void aiStep() {
        LivingEntity rider = this.getControllingPassenger();
        boolean isPlayerRiding = this.isVehicle() && rider instanceof Player && !this.isAutonomous();

        Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
        if (!this.level().isClientSide && this.getAlternativeTarget(0) > 0 && !isPlayerRiding) {
            Entity entity = this.level().getEntity(this.getAlternativeTarget(0));
            if (entity != null) {
                double d0 = vec3.y;
                if (this.getY() < entity.getY() || !this.isPowered() && this.getY() < entity.getY() + 5.0D) {
                    d0 = Math.max(0.0D, d0);
                    d0 += 0.3D - d0 * (double) 0.6F;
                }

                vec3 = new Vec3(vec3.x, d0, vec3.z);
                Vec3 vec31 = new Vec3(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());
                if (vec31.horizontalDistanceSqr() > 9.0D) {
                    Vec3 vec32 = vec31.normalize();
                    vec3 = vec3.add(vec32.x * 0.3D - vec3.x * 0.6D, 0.0D, vec32.z * 0.3D - vec3.z * 0.6D);
                }
            }
        }
        if (!isPlayerRiding) {
            this.setDeltaMovement(vec3);
        }

        if (vec3.horizontalDistanceSqr() > 0.05D) {
            this.setYRot((float) Mth.atan2(vec3.z, vec3.x) * (180F / (float) Math.PI) - 90.0F);
        }

        super.aiStep();

        for (int i = 0; i < 2; ++i) {
            this.yRotOHeads[i] = this.yRotHeads[i];
            this.xRotOHeads[i] = this.xRotHeads[i];
        }

        for (int j = 0; j < 2; ++j) {
            int k = this.getAlternativeTarget(j + 1);
            Entity entity1 = null;
            if (k > 0) {
                entity1 = this.level().getEntity(k);
            }

            if (entity1 != null) {
                double d9 = this.getHeadX(j + 1);
                double d1 = this.getHeadY(j + 1);
                double d3 = this.getHeadZ(j + 1);
                double d4 = entity1.getX() - d9;
                double d5 = entity1.getEyeY() - d1;
                double d6 = entity1.getZ() - d3;
                double d7 = Math.sqrt(d4 * d4 + d6 * d6);
                float f = (float) (Mth.atan2(d6, d4) * (double) (180F / (float) Math.PI)) - 90.0F;
                float f1 = (float) (-(Mth.atan2(d5, d7) * (double) (180F / (float) Math.PI)));
                this.xRotHeads[j] = this.rotlerp(this.xRotHeads[j], f1, 40.0F);
                this.yRotHeads[j] = this.rotlerp(this.yRotHeads[j], f, 10.0F);
            } else {
                this.yRotHeads[j] = this.rotlerp(this.yRotHeads[j], this.yBodyRot, 10.0F);
            }
        }

        boolean flag = this.isPowered();

        for (int l = 0; l < 3; ++l) {
            double d8 = this.getHeadX(l);
            double d10 = this.getHeadY(l);
            double d2 = this.getHeadZ(l);
            this.level().addParticle(ParticleTypes.SMOKE, d8 + this.random.nextGaussian() * (double) 0.3F,
                    d10 + this.random.nextGaussian() * (double) 0.3F, d2 + this.random.nextGaussian() * (double) 0.3F,
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void customServerAiStep() {
        if (this.servantInvulnerableTicks > 0) {
            int k1 = this.servantInvulnerableTicks - 1;

            if (k1 <= 0) {
                if (this.getHealth() >= this.getMaxHealth()) {
                    this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F,
                            Level.ExplosionInteraction.NONE);
                    if (!this.isSilent()) {
                        this.level().globalLevelEvent(1023, this.blockPosition(), 0);
                    }
                }
                this.setFirstSpawn(false);
                this.setInvulnerableTicks(0);
            }

            this.servantInvulnerableTicks = k1;
            this.setInvulnerableTicks(this.servantInvulnerableTicks);

            float healAmount = this.getMaxHealth() * 0.0025F;
            if (this.getHealth() < this.getMaxHealth()) {
                this.setHealth(Math.min(this.getHealth() + healAmount, this.getMaxHealth()));
            }
            return;
        } else {
            if (this.tickCount % 20 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(Math.min(this.getHealth() + 1.0F, this.getMaxHealth()));
            }

            super.customServerAiStep();

            for (int i = 1; i < 3; ++i) {
                if (this.tickCount >= this.nextHeadUpdate[i - 1]) {
                    this.nextHeadUpdate[i - 1] = this.tickCount + 10 + this.random.nextInt(10);
                    if (this.level().getDifficulty() == Difficulty.NORMAL
                            || this.level().getDifficulty() == Difficulty.HARD) {
                        int i3 = i - 1;
                        int j3 = this.idleHeadUpdates[i - 1];
                        this.idleHeadUpdates[i3] = this.idleHeadUpdates[i - 1] + 1;
                        if (j3 > 15) {
                        }
                    }

                    int l1 = this.getAlternativeTarget(i);
                    if (l1 > 0) {
                        LivingEntity livingentity = (LivingEntity) this.level().getEntity(l1);
                        if (livingentity != null && this.canAttack(livingentity)
                                && !(this.distanceToSqr(livingentity) > 900.0D) && this.hasLineOfSight(livingentity)) {
                            this.shootSkullAt(i + 1, livingentity);
                            this.nextHeadUpdate[i - 1] = this.tickCount + 40 + this.random.nextInt(20);
                            this.idleHeadUpdates[i - 1] = 0;
                        } else {
                            this.setAlternativeTarget(i, 0);
                        }
                    } else {
                        LivingEntity targetToAttack = null;
                        if (this.getTarget() != null && this.canAttack(this.getTarget())) {
                            targetToAttack = this.getTarget();
                        } else {
                            if (this.getTrueOwner() != null && this.getTrueOwner() instanceof Mob
                                    && ((Mob) this.getTrueOwner()).getTarget() != null
                                    && this.canAttack(((Mob) this.getTrueOwner()).getTarget())) {
                                targetToAttack = ((Mob) this.getTrueOwner()).getTarget();
                            }
                        }

                        if (targetToAttack != null) {
                            this.setAlternativeTarget(i, targetToAttack.getId());
                        }
                    }
                }
            }
        }

        if (this.getTarget() != null) {
            this.setAlternativeTarget(0, this.getTarget().getId());
        } else {
            if (this.tickCount >= this.nextHeadUpdate[0]) {
                this.nextHeadUpdate[0] = this.tickCount + 10 + this.random.nextInt(10);
                int l1 = this.getAlternativeTarget(0);
                if (l1 > 0) {
                    LivingEntity livingentity = (LivingEntity) this.level().getEntity(l1);
                    if (livingentity != null && this.canAttack(livingentity)
                            && !(this.distanceToSqr(livingentity) > 900.0D) && this.hasLineOfSight(livingentity)) {
                        this.shootSkullAt(0, livingentity);
                        this.nextHeadUpdate[0] = this.tickCount + 40 + this.random.nextInt(20);
                    } else {
                        this.setAlternativeTarget(0, 0);
                    }
                } else {
                    Predicate<LivingEntity> selector = (p_31504_) -> p_31504_.attackable()
                            && WitherServant.this.canAttack(p_31504_);
                    TargetingConditions targetingConditions = TargetingConditions.forCombat().range(20.0D)
                            .selector(selector);
                    List<LivingEntity> list = this.level().getNearbyEntities(LivingEntity.class,
                            targetingConditions, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));
                    if (!list.isEmpty() && this.canTarget()) {
                        LivingEntity livingentity1 = list.get(this.random.nextInt(list.size()));
                        this.setAlternativeTarget(0, livingentity1.getId());
                    }
                }
            }
        }
    }

    public int getAlternativeTarget(int pHead) {
        switch (pHead) {
            case 0:
                return this.entityData.get(DATA_TARGET_A);
            case 1:
                return this.entityData.get(DATA_TARGET_B);
            case 2:
                return this.entityData.get(DATA_TARGET_C);
            default:
                return 0;
        }
    }

    public void setAlternativeTarget(int pTargetOffset, int pNewId) {
        switch (pTargetOffset) {
            case 0:
                this.entityData.set(DATA_TARGET_A, pNewId);
                break;
            case 1:
                this.entityData.set(DATA_TARGET_B, pNewId);
                break;
            case 2:
                this.entityData.set(DATA_TARGET_C, pNewId);
                break;
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() != null && pSource.getEntity() instanceof LivingEntity
                && ((LivingEntity) pSource.getEntity()).getMobType() == MobType.UNDEAD) {
            pAmount *= 0.5F;
        }

        if (this.invulnerableTicks > 0) {
            return false;
        }

        if (this.isPowered() && pSource.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
            return false;
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return true;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target == this.getTrueOwner()) {
            return false;
        }

        if (MobUtil.areAllies(this, target)) {
            return false;
        }

        if (target instanceof TamableAnimal tamableAnimal && this.getTrueOwner() != null &&
                tamableAnimal.isTame() && tamableAnimal.getOwner() == this.getTrueOwner()) {
            return false;
        }

        return super.canAttack(target);
    }

    @Override
    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    public boolean canDestroy(BlockState pState) {
        return false;
    }

    public boolean canCollideWithBlock(BlockState pState) {
        return false;
    }

    private boolean teleportTowardsEntity(LivingEntity target) {
        if (!this.level().isClientSide() && this.isAlive() && target != null) {
            Vec3 vector3d = new Vec3(this.getX() - target.getX(), this.getY(0.5D) - target.getEyeY(),
                    this.getZ() - target.getZ());
            vector3d = vector3d.normalize();

            double distance = 4.0D;
            double d1 = target.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * distance;
            double d2 = target.getY() + (double) (this.random.nextInt(8) - 4) - vector3d.y * distance;
            double d3 = target.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * distance;

            return this.randomTeleport(d1, d2, d3, true);
        } else {
            return false;
        }
    }

    @Override
    public boolean canWander() {
        return true;
    }

    @Override
    public boolean canStay() {
        return true;
    }

    @Override
    public boolean canGuardArea() {
        return true;
    }

    @Override
    public boolean canFollow() {
        return true;
    }

    @Override
    public boolean canBeCommanded() {
        return true;
    }

    public void warnKill(Player player) {
        this.killChance = 60;
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("info.goety.servant.tryKill", this.getDisplayName()),
                true);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public void push(Entity p_21294_) {
        if (!this.level().isClientSide) {
            if (!this.isStaying()) {
                super.push(p_21294_);
            }
        }
    }

    protected void doPush(Entity p_20971_) {
        if (!this.level().isClientSide) {
            if (!this.isStaying()) {
                super.doPush(p_20971_);
            }
        }
    }

    public boolean canCollideWith(Entity p_20303_) {
        if (!this.isStaying()) {
            return super.canCollideWith(p_20303_);
        } else {
            return false;
        }
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(INVULNERABILITY_TIME);
        this.servantInvulnerableTicks = INVULNERABILITY_TIME;
        this.setFirstSpawn(true);
    }

    public void setInvulnerableTicks(int pInvulnerableTicks) {
        this.invulnerableTicks = pInvulnerableTicks;
    }

    public int getInvulnerableTicks() {
        return this.invulnerableTicks;
    }

    public int getServantInvulnerableTicks() {
        return this.servantInvulnerableTicks;
    }

    @Override
    public boolean canUpdateMove() {
        return this.servantInvulnerableTicks <= 0;
    }

    public boolean canTarget() {
        return this.servantInvulnerableTicks <= 0;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null) {
            if (target == this.getTrueOwner()) {
                return;
            }

            if (MobUtil.areAllies(this, target)) {
                return;
            }

            if (target instanceof TamableAnimal tamableAnimal && this.getTrueOwner() != null &&
                    tamableAnimal.isTame() && tamableAnimal.getOwner() == this.getTrueOwner()) {
                return;
            }
        }

        if (this.canTarget()) {
            super.setTarget(target);
        } else if (target != null) {
            return;
        } else {
            super.setTarget(null);
        }
    }

    private void checkForArmorEffect() {
        if (!this.level().isClientSide && this.isPowered()) {
            if (this.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                this.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            }
        }
    }

    public void onKillEntity(LivingEntity entity) {
        if (!this.level().isClientSide) {
            float healAmount = this.getMaxHealth() * 0.02F;
            this.heal(healAmount);

            MobUtil.createWitherRose(entity, this);
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        net.minecraft.world.entity.item.ItemEntity itementity = this
                .spawnAtLocation(net.minecraft.world.item.Items.NETHER_STAR);
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, Entity pEntity) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("AutoMode", this.isAutonomous());
        compound.putBoolean("HasSpawned", !this.isFirstSpawn());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setConfigurableAttributes();
        if (compound.contains("AutoMode")) {
            this.setAutonomous(compound.getBoolean("AutoMode"));
        }
        if (compound.contains("HasSpawned")) {
            this.setFirstSpawn(!compound.getBoolean("HasSpawned"));
        } else {
            this.setFirstSpawn(true);
        }
        this.servantInvulnerableTicks = 0;
        this.invulnerableTicks = 0;
        this.setInvulnerableTicks(0);
    }

    class WitherDoNothingGoal extends Goal {
        private final WitherServant wither;

        public WitherDoNothingGoal(WitherServant wither) {
            this.wither = wither;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return this.wither.getInvulnerableTicks() > 0;
        }
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getTrueOwner() == player) {
            if (player.isShiftKeyDown()) {
                return super.mobInteract(player, hand);
            } else {
                if (!this.level().isClientSide) {
                    if (this.servantInvulnerableTicks <= 0) {
                        this.doPlayerRide(player);
                        return InteractionResult.CONSUME;
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.witherServantLimit;
    }

    public void handleFlightControl(boolean flyUp, boolean flyDown) {
        this.entityData.set(DATA_IS_GOING_UP, flyUp);
        this.entityData.set(DATA_IS_GOING_DOWN, flyDown);
    }

    public boolean isGoingUp() {
        return this.entityData.get(DATA_IS_GOING_UP);
    }

    public boolean isGoingDown() {
        return this.entityData.get(DATA_IS_GOING_DOWN);
    }

    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTO_MODE, autonomous);
    }

    public boolean isAutonomous() {
        return this.entityData.get(AUTO_MODE);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof LivingEntity) {
                if (entity instanceof Mob) {
                    if (com.Polarice3.Goety.config.MobsConfig.ServantRideAutonomous.get()) {
                        return null;
                    }
                    return (LivingEntity) entity;
                } else {
                    if (!this.isAutonomous()) {
                        return (LivingEntity) entity;
                    }
                }
            }
        }
        return null;
    }

    public boolean isFirstSpawn() {
        return this.entityData.get(DATA_IS_FIRST_SPAWN);
    }

    public void setFirstSpawn(boolean firstSpawn) {
        this.entityData.set(DATA_IS_FIRST_SPAWN, firstSpawn);
    }
}