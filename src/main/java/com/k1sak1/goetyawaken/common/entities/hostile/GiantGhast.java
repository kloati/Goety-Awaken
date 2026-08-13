package com.k1sak1.goetyawaken.common.entities.hostile;

import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.SummonedFlying;
import com.Polarice3.Goety.common.entities.projectiles.HellBlast;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.k1sak1.goetyawaken.common.entities.projectiles.GiantGhastFireball;
import com.k1sak1.goetyawaken.common.entities.projectiles.TrackingFireball;
import com.k1sak1.goetyawaken.common.entities.projectiles.GiantHellBlast;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import com.Polarice3.Goety.api.entities.IAutoRideable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public class GiantGhast extends SummonedFlying implements IAutoRideable {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING = SynchedEntityData.defineId(GiantGhast.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(GiantGhast.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SIDE_ATTACK = SynchedEntityData.defineId(GiantGhast.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SIDE_ATTACK_TEXTURE = SynchedEntityData.defineId(
            GiantGhast.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_OUTPOURING = SynchedEntityData.defineId(GiantGhast.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_GHAST_AUTO_MODE = SynchedEntityData.defineId(GiantGhast.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_GHAST_IS_GOING_UP = SynchedEntityData.defineId(
            GiantGhast.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_GHAST_IS_GOING_DOWN = SynchedEntityData.defineId(
            GiantGhast.class,
            EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState shootAnimationState = new AnimationState();

    public static final String IDLE = "idle";
    public static final String FLY = "fly";
    public static final String SHOOT = "shoot";

    private int explosionPower = 1;

    public int shootTick = 0;
    public boolean isShooting = false;
    public int shootCooldown = 0;

    private int projectileType = -1;
    private int projectileCount = 0;
    private int projectilesSpawned = 0;
    private int stun;
    private BlockPos spawnPoint = null;

    private BlockPos targetPoint = null;

    private int sideAttackCooldown = 0;
    private int sideAttackDuration = 0;
    private boolean isSideAttacking = false;

    private float accumulatedDamage = 0.0F;

    private int heatHealTimer = 0;

    private int summonTimer = 0;

    public int outpouringTick = 0;
    public int outpouringCooldown = 0;

    private boolean useGiantHellBlast = false;

    public GiantGhast(EntityType<? extends SummonedFlying> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new GiantGhastMoveControl(this);
        this.setNoGravity(true);
        this.setPersistenceRequired();
    }

    public GiantGhast(Level worldIn) {
        this(ModEntityType.GIANT_GHAST.get(), worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(7, new LookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
        this.goalSelector.addGoal(5, new FlyingGoal(this));
        this.goalSelector.addGoal(3, new OutpouringGoal(this));
        this.goalSelector.addGoal(7, new GiantGhastShootFireballGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, pLevel);
        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(true);
        navigator.setCanPassDoors(true);
        return navigator;
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public boolean canCollideWith(Entity entity) {
        return true;
    }

    public boolean canBeCollidedWith() {
        return !this.isDeadOrDying();
    }

    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void followGoal() {
    }

    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean pCharging) {
        this.entityData.set(DATA_IS_CHARGING, pCharging);
    }

    public boolean isSideAttacking() {
        return this.entityData.get(DATA_SIDE_ATTACK);
    }

    public void setSideAttacking(boolean sideAttacking) {
        this.entityData.set(DATA_SIDE_ATTACK, sideAttacking);
    }

    public int getSideAttackTexture() {
        return this.entityData.get(DATA_SIDE_ATTACK_TEXTURE);
    }

    public void setSideAttackTexture(int texture) {
        this.entityData.set(DATA_SIDE_ATTACK_TEXTURE, texture);
    }

    public boolean isOutpouring() {
        return this.entityData.get(DATA_IS_OUTPOURING);
    }

    public void setOutpouring(boolean outpouring) {
        this.entityData.set(DATA_IS_OUTPOURING, outpouring);
    }

    public int getExplosionPower() {
        return this.explosionPower;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING, false);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(DATA_SIDE_ATTACK, false);
        this.entityData.define(DATA_SIDE_ATTACK_TEXTURE, 0);
        this.entityData.define(DATA_IS_OUTPOURING, false);
        this.entityData.define(DATA_GHAST_AUTO_MODE, false);
        this.entityData.define(DATA_GHAST_IS_GOING_UP, false);
        this.entityData.define(DATA_GHAST_IS_GOING_DOWN, false);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GiantGhastHealth.get())
                .add(Attributes.FOLLOW_RANGE, 128.0D)
                .add(Attributes.ARMOR, AttributesConfig.GiantGhastArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.GiantGhastArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GiantGhastDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.GiantGhastHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.GiantGhastDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.GiantGhastArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.GiantGhastArmorToughness.get());
    }

    @Override
    protected float getStandingEyeHeight(Pose p_32741_, EntityDimensions p_32742_) {
        return 5.2F;
    }

    @Override
    protected float getSoundVolume() {
        return 10.0F;
    }

    @Override
    public float getVoicePitch() {
        return 0.5F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return com.k1sak1.goetyawaken.init.ModSounds.GIANT_GHAST_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_32615_) {
        return com.k1sak1.goetyawaken.init.ModSounds.GIANT_GHAST_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return com.k1sak1.goetyawaken.init.ModSounds.GIANT_GHAST_DEATH.get();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    private static boolean isReflectedFireball(DamageSource p_238408_) {
        return (p_238408_.getDirectEntity() instanceof LargeFireball
                || p_238408_.getDirectEntity() instanceof TrackingFireball) && p_238408_.getEntity() instanceof Player;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.level().isClientSide) {
            float maxDamage = this.getMaxHealth();
            this.accumulatedDamage += Math.min(pAmount, maxDamage);
            float percentageDamage = this.getMaxHealth() * 0.15F;
            if (isReflectedFireball(pSource)) {
                super.hurt(pSource, percentageDamage);
                return true;
            }

        }
        if (pSource.is(ModDamageSource.DISMISSED)) {
            this.lifeSpanDamage();
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.isStaying()) {
            this.getMoveControl().strafe(0.0F, 0.0F);
        }
        if (this.isAlive()) {
            if (this.isCharging()) {
                this.stun = 20;
            } else {
                if (this.stun > 0) {
                    --this.stun;
                }
            }
        }
        if (!this.level().isClientSide) {
            if (!this.isDeadOrDying()) {
                this.checkHealthLossAndSummon();
                this.checkHeatHeal();
                this.checkTimedSummon();
            }
            if (this.outpouringCooldown > 0) {
                this.outpouringCooldown--;
            }
            if (this.isOutpouring() && this.outpouringTick > 0) {
                this.outpouringTick--;
            }
            if (this.sideAttackCooldown > 0) {
                this.sideAttackCooldown--;
            } else if (!this.isSideAttacking && this.getTarget() != null) {
                this.isSideAttacking = true;
                this.sideAttackDuration = 30;
                this.setSideAttackTexture(this.getRandom().nextInt(2));
                this.setSideAttacking(true);
            }

            if (this.isSideAttacking) {
                this.sideAttackDuration--;
                if (this.sideAttackDuration == 15) {
                    this.spawnSideAttackFireballs();
                }
                if (this.sideAttackDuration <= 0) {
                    this.isSideAttacking = false;
                    this.setSideAttacking(false);
                    this.sideAttackCooldown = 100;
                }
            }

            if (this.getTarget() != null) {
                BlockPos currentTargetPos = this.getTarget().blockPosition();
                if (this.targetPoint == null || this.targetPoint.distSqr(currentTargetPos) > Mth.square(24)) {
                    this.targetPoint = currentTargetPos;
                }
            }
            if (this.isShooting) {
                this.shootTick++;
                if (this.shootTick == 1) {
                    this.level().broadcastEntityEvent(this, (byte) 4);
                    prepareProjectiles();
                    this.playSound(com.k1sak1.goetyawaken.init.ModSounds.GIANT_GHAST_SIDE_ATTACK_CHARGE.get(), 10.0F,
                            0.8F);
                } else if (this.shootTick >= 18 && this.shootTick <= 30) {
                    if (this.projectilesSpawned < this.projectileCount) {
                        int totalTicks = 13;
                        int shouldSpawn = (int) Math
                                .ceil((double) (this.shootTick - 17) * this.projectileCount / totalTicks);

                        while (this.projectilesSpawned < shouldSpawn
                                && this.projectilesSpawned < this.projectileCount) {
                            spawnSingleProjectile();
                            this.projectilesSpawned++;
                        }
                    }
                    if (this.shootTick == 30) {
                        this.playFireballShootSound();
                    }
                } else if (this.shootTick >= 80) {
                    this.isShooting = false;
                    this.shootTick = 0;
                    this.shootCooldown = 60;
                    this.setAnimationState(IDLE);
                    this.projectileType = -1;
                    this.projectileCount = 0;
                    this.projectilesSpawned = 0;
                    this.useGiantHellBlast = false;
                }
            } else {
                Vec3 movement = this.getDeltaMovement();
                double horizontalSpeed = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
                double verticalSpeed = Math.abs(movement.y);
                double totalSpeed = Math.sqrt(horizontalSpeed * horizontalSpeed + verticalSpeed * verticalSpeed);
                if (totalSpeed > 0.5D) {
                    if (this.getCurrentAnimation() != this.getAnimationState(FLY)) {
                        this.setAnimationState(FLY);
                    }
                } else {
                    if (this.getCurrentAnimation() != this.getAnimationState(IDLE)) {
                        this.setAnimationState(IDLE);
                    }
                }
            }
            if (this.shootCooldown > 0) {
                this.shootCooldown--;
            }
        }
    }

    public void lifeSpanDamage() {
        this.dismiss();
    }

    public void tryKill(Player player) {
        this.lifeSpanDamage();
    }

    public void dismiss() {
        if (!this.level().isClientSide) {
            for (int i = 0; i < this.level().random.nextInt(35) + 10; ++i) {
                ServerParticleUtil.smokeParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(),
                        this.level());
            }
            if (!this.isHostile()) {
                if (this.getTrueOwner() != null
                        && this.getTrueOwner() instanceof net.minecraft.world.entity.player.Player player) {
                    ItemStack gloomytear = new ItemStack(
                            com.k1sak1.goetyawaken.common.items.ModItems.GLOOMY_TEARS.get());
                    com.Polarice3.Goety.common.entities.projectiles.FlyingItem flyingItem = new com.Polarice3.Goety.common.entities.projectiles.FlyingItem(
                            com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                            this.level(),
                            this.getX(),
                            this.getY() + 1.0D,
                            this.getZ());
                    flyingItem.setOwner(this.getTrueOwner());
                    flyingItem.setItem(gloomytear);
                    flyingItem.setSecondsCool(30);
                    this.level().addFreshEntity(flyingItem);
                }
            }
        }
        this.playSound((SoundEvent) ModSounds.GHAST_DISAPPEAR.get(), this.getSoundVolume(), this.getVoicePitch());
        this.discard();
    }

    @Override
    public void handleEntityEvent(byte pEventId) {
        if (pEventId == 4) {
            this.idleAnimationState.stop();
            this.flyAnimationState.stop();
            this.shootAnimationState.start(this.tickCount);
            this.setAnimationState(SHOOT);
        } else {
            super.handleEntityEvent(pEventId);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pAccessor) {
        if (ANIM_STATE.equals(pAccessor)) {
            if (this.level().isClientSide) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.flyAnimationState.stop();
                        this.shootAnimationState.stop();
                        break;
                    case 2:
                        this.flyAnimationState.startIfStopped(this.tickCount);
                        this.idleAnimationState.stop();
                        this.shootAnimationState.stop();
                        break;
                    case 3:
                        this.shootAnimationState.start(this.tickCount);
                        this.idleAnimationState.stop();
                        this.flyAnimationState.stop();
                        break;
                }
            }
        }
        super.onSyncedDataUpdated(pAccessor);
    }

    public void setAnimationState(String animation) {
        int id = this.getAnimationState(animation);
        this.entityData.set(ANIM_STATE, id);
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (IDLE.equals(animation)) {
            return 1;
        } else if (FLY.equals(animation)) {
            return 2;
        } else if (SHOOT.equals(animation)) {
            return 3;
        } else {
            return 0;
        }
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    private void prepareProjectiles() {
        int projectileChoice = this.getRandom().nextInt(3);
        this.useGiantHellBlast = false;

        switch (projectileChoice) {
            case 0:
                this.projectileType = 0;
                this.projectileCount = 3 + this.getRandom().nextInt(3);
                break;
            case 1:
                this.projectileType = 1;
                this.projectileCount = 13;
                break;
            case 2:
                this.projectileType = 2;
                this.projectileCount = 1;
                break;
        }

        if (AttributesConfig.GiantGhastAllowGiantHellBlast.get()
                && (this.projectileType == 0 || this.projectileType == 2)) {
            Difficulty difficulty = this.level().getDifficulty();
            double probability = 0.0;
            switch (difficulty) {
                case HARD:
                    probability = 0.5;
                    break;
                case NORMAL:
                    probability = 0.2;
                    break;
                case EASY:
                    probability = 0.1;
                    break;
                case PEACEFUL:
                default:
                    probability = 0.0;
                    break;
            }

            if (this.getRandom().nextDouble() < probability) {
                this.useGiantHellBlast = true;
                this.projectileCount = 1;
            }
        }

        this.projectilesSpawned = 0;
    }

    private void spawnSingleProjectile() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }

        Level world = this.level();
        Vec3 centerPos = new Vec3(this.getX(), this.getY(0.5D), this.getZ());
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (this.useGiantHellBlast) {
            spawnSingleGiantHellBlast(world, centerPos, target, attackDamage);
            return;
        }

        switch (this.projectileType) {
            case 0:
                spawnSingleHellBlast(world, centerPos, target, attackDamage);
                break;
            case 1:
                spawnSingleLavaball(world, centerPos, target, attackDamage);
                break;
            case 2:
                spawnSingleGiantGhastFireball(world, centerPos, target, attackDamage);
                break;
            default:
                spawnSingleGiantGhastFireball(world, centerPos, target, attackDamage);
                break;
        }
    }

    private void spawnSingleHellBlast(Level world, Vec3 centerPos, LivingEntity target, float attackDamage) {
        if (target == null) {
            return;
        }
        Vec3 toTarget = target.position().subtract(centerPos).normalize();
        double spreadAngle = (this.getRandom().nextFloat() - 0.5F) * 0.3F;
        Vec3 direction = rotateVector(toTarget, spreadAngle);

        HellBlast hellBlast = new HellBlast(this, direction.x, direction.y, direction.z, world);
        hellBlast.setPos(centerPos.x, centerPos.y, centerPos.z);
        world.addFreshEntity(hellBlast);
    }

    private void spawnSingleHellBolt(Level world, Vec3 centerPos, LivingEntity target, float attackDamage) {
        if (target == null) {
            return;
        }
        Vec3 toTarget = target.position().subtract(centerPos).normalize();
        int boltCount = 1 + this.getRandom().nextInt(5);
        for (int i = 0; i < boltCount; i++) {
            double spreadX = toTarget.x + (this.getRandom().nextDouble() - 0.5) * 0.1;
            double spreadY = toTarget.y + (this.getRandom().nextDouble() - 0.5) * 0.1;
            double spreadZ = toTarget.z + (this.getRandom().nextDouble() - 0.5) * 0.1;
            HellBolt hellBolt = new HellBolt(this, spreadX, spreadY, spreadZ, world);
            hellBolt.setPos(centerPos.x, centerPos.y, centerPos.z);
            world.addFreshEntity(hellBolt);
        }
    }

    private void spawnSingleLavaball(Level world, Vec3 centerPos, LivingEntity target, float attackDamage) {
        if (target == null) {
            return;
        }
        Vec3 toTarget = target.position().subtract(centerPos).normalize();
        double spreadAngle = (this.getRandom().nextFloat() - 0.5F) * 0.3F;
        Vec3 direction = rotateVector(toTarget, spreadAngle);

        com.Polarice3.Goety.common.entities.projectiles.Lavaball lavaball = new com.Polarice3.Goety.common.entities.projectiles.Lavaball(
                world, centerPos.x, centerPos.y, centerPos.z, direction.x, direction.y, direction.z);
        lavaball.setPos(centerPos.x, centerPos.y, centerPos.z);
        lavaball.setOwner(this);
        lavaball.setDamage(attackDamage * 0.6F);
        if (this.isHostile()) {
            lavaball.setDangerous(net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, this));
        } else {
            lavaball.setDangerous(false);
        }

        lavaball.setExplosionPower(2.0F);
        world.addFreshEntity(lavaball);
    }

    private void spawnSingleGiantGhastFireball(Level world, Vec3 centerPos, LivingEntity target, float attackDamage) {
        if (target == null) {
            return;
        }
        Vec3 toTarget = target.position().subtract(centerPos).normalize();
        double spreadAngle = (this.getRandom().nextFloat() - 0.5F) * 0.2F;
        Vec3 direction = rotateVector(toTarget, spreadAngle);
        GiantGhastFireball fireball = new GiantGhastFireball(
                this, direction.x, direction.y, direction.z, world);
        fireball.setPos(centerPos.x, centerPos.y, centerPos.z);
        fireball.setExtraDamage(attackDamage * 0.5F);
        world.addFreshEntity(fireball);
    }

    private void spawnSingleGiantHellBlast(Level world, Vec3 centerPos, LivingEntity target, float attackDamage) {
        if (target == null) {
            return;
        }
        Vec3 toTarget = target.position().subtract(centerPos).normalize();
        double spreadAngle = (this.getRandom().nextFloat() - 0.5F) * 0.2F;
        Vec3 direction = rotateVector(toTarget, spreadAngle);
        GiantHellBlast hellBlast = new GiantHellBlast(
                this, direction.x, direction.y, direction.z, world);
        hellBlast.setPos(centerPos.x, centerPos.y, centerPos.z);
        hellBlast.setExtraDamage(attackDamage * 0.5F);
        world.addFreshEntity(hellBlast);
    }

    private Vec3 rotateVector(Vec3 original, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double newX = original.x * cos - original.z * sin;
        double newZ = original.x * sin + original.z * cos;
        return new Vec3(newX, original.y, newZ).normalize();
    }

    private void spawnSideAttackFireballs() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }

        Level world = this.level();
        Vec3 centerPos = new Vec3(this.getX(), this.getY(0.5D), this.getZ());
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        Vec3 lookVector = this.getViewVector(1.0F);
        Vec3 normalLeft = new Vec3(-lookVector.z, 0, lookVector.x).normalize();
        Vec3 normalRight = new Vec3(lookVector.z, 0, -lookVector.x).normalize();
        spawnTrackingFireball(world, centerPos, normalLeft, target, attackDamage);
        spawnTrackingFireball(world, centerPos, normalRight, target, attackDamage);
    }

    private void spawnTrackingFireball(Level world, Vec3 centerPos, Vec3 direction, LivingEntity target,
            float attackDamage) {
        Vec3 spawnPos = centerPos.add(direction.scale(3.0));

        com.k1sak1.goetyawaken.common.entities.projectiles.TrackingFireball fireball = new com.k1sak1.goetyawaken.common.entities.projectiles.TrackingFireball(
                world, this, direction.x, direction.y, direction.z);
        fireball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yRot = (float) (Math.atan2(direction.x, direction.z) * (180.0D / Math.PI));
        float xRot = (float) (Math.atan2(direction.y, horizontalDistance) * (180.0D / Math.PI));
        fireball.setYRot(yRot);
        fireball.setXRot(xRot);
        fireball.setDeltaMovement(direction.scale(0.75D));
        fireball.setExtraDamage(attackDamage * 0.2F);
        fireball.setTarget(target);
        world.addFreshEntity(fireball);
    }

    private void playFireballShootSound() {
        this.playSound(com.k1sak1.goetyawaken.init.ModSounds.GIANT_GHAST_FIREBALL_SHOOT.get(), 5.0F,
                (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F);
    }

    public void playLastWordsSound() {
        if (!this.level().isClientSide) {
            this.playSound(com.k1sak1.goetyawaken.init.ModSounds.GIANT_GHAST_LAST_WORDS.get(), 10.0F, 0.6F);
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        if (pReason == RemovalReason.KILLED || pReason == RemovalReason.DISCARDED) {
            this.playLastWordsSound();
        }
        super.remove(pReason);
    }

    private void checkHealthLossAndSummon() {
        if (this.level().isClientSide) {
            return;
        }

        float maxHealth = this.getMaxHealth();
        float threshold = maxHealth * 0.25F;
        while (this.accumulatedDamage >= threshold) {
            this.accumulatedDamage -= threshold;
            this.performRandomSummon();
        }
    }

    private void checkHeatHeal() {
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount % 5 != 0) {
            return;
        }

        BlockPos entityPos = this.blockPosition();
        boolean isNearHeatSource = false;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = entityPos.offset(x, y, z);
                    BlockState blockState = this.level().getBlockState(checkPos);
                    if (blockState.is(Blocks.FIRE) ||
                            blockState.is(Blocks.SOUL_FIRE) ||
                            blockState.is(Blocks.LAVA) ||
                            blockState.is(Blocks.MAGMA_BLOCK) ||
                            (blockState.is(Blocks.CAMPFIRE) && blockState.getValue(CampfireBlock.LIT)) ||
                            (blockState.is(Blocks.SOUL_CAMPFIRE) && blockState.getValue(CampfireBlock.LIT))) {
                        isNearHeatSource = true;
                        break;
                    }
                }
                if (isNearHeatSource)
                    break;
            }
            if (isNearHeatSource)
                break;
        }

        if (isNearHeatSource) {
            this.heatHealTimer++;
            if (this.heatHealTimer >= 20) {
                this.heatHealTimer = 0;
                float currentHealth = this.getHealth();
                float maxHealth = this.getMaxHealth();
                if (currentHealth < maxHealth) {
                    this.setHealth(Math.min(currentHealth + 5.0F, maxHealth));
                }
            }
        } else {
            this.heatHealTimer = 0;
        }
    }

    private void checkTimedSummon() {
        if (this.level().isClientSide) {
            return;
        }
        if (this.getTarget() != null) {
            this.summonTimer++;
            if (this.summonTimer >= 500) {
                this.summonTimer = 0;
                this.performRandomSummon();
            }
        } else {
            this.summonTimer = 0;
        }
    }

    private void performRandomSummon() {
        if (this.level().isClientSide) {
            return;
        }

        net.minecraft.server.level.ServerLevel serverLevel = (net.minecraft.server.level.ServerLevel) this.level();
        RandomSource random = this.getRandom();

        if (random.nextBoolean()) {
            this.summonGhastServant(serverLevel);
        } else {
            int count = 2 + random.nextInt(2);
            for (int i = 0; i < count; i++) {
                this.summonMiniGhast(serverLevel);
            }
        }
        this.playSummonParticles(serverLevel);
    }

    private void summonGhastServant(net.minecraft.server.level.ServerLevel serverLevel) {
        com.Polarice3.Goety.common.entities.ally.GhastServant servant = new com.Polarice3.Goety.common.entities.ally.GhastServant(
                com.Polarice3.Goety.common.entities.ModEntityType.GHAST_SERVANT.get(), this.level());
        net.minecraft.core.BlockPos spawnPos = this.blockPosition().offset(
                this.getRandom().nextInt(17) - 8,
                this.getRandom().nextInt(5) - 2,
                this.getRandom().nextInt(17) - 8);
        servant.setTrueOwner(this);
        servant.moveTo(spawnPos, this.getYRot(), 0.0F);
        servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.MOB_SUMMONED, null, null);
        servant.setLimitedLife(com.Polarice3.Goety.utils.MobUtil.getSummonLifespan(this.level()));

        serverLevel.addFreshEntity(servant);
    }

    private void summonMiniGhast(net.minecraft.server.level.ServerLevel serverLevel) {
        com.Polarice3.Goety.common.entities.ally.MiniGhast miniGhast = new com.Polarice3.Goety.common.entities.ally.MiniGhast(
                com.Polarice3.Goety.common.entities.ModEntityType.MINI_GHAST.get(), this.level());
        net.minecraft.core.BlockPos spawnPos = this.blockPosition().offset(
                this.getRandom().nextInt(13) - 6,
                this.getRandom().nextInt(5) - 2,
                this.getRandom().nextInt(13) - 6);
        miniGhast.setTrueOwner(this);
        miniGhast.moveTo(spawnPos, this.getYRot(), 0.0F);
        miniGhast.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.MOB_SUMMONED, null, null);

        miniGhast.setLimitedLife(com.Polarice3.Goety.utils.MobUtil.getSummonLifespan(this.level()));

        serverLevel.addFreshEntity(miniGhast);
    }

    private void playSummonParticles(net.minecraft.server.level.ServerLevel serverLevel) {
        com.Polarice3.Goety.utils.ColorUtil redColor = new com.Polarice3.Goety.utils.ColorUtil(0xff3333);
        com.Polarice3.Goety.utils.ServerParticleUtil.summonUndeadParticles(
                serverLevel,
                this,
                redColor,
                0xff0000,
                0xff6666);
    }

    @Nullable
    private net.minecraft.core.BlockPos findSuitableSpawnPosition(int radius) {
        for (int i = 0; i < 10; i++) {
            int offsetX = this.getRandom().nextInt(radius * 2 + 1) - radius;
            int offsetY = this.getRandom().nextInt(4) - 1;
            int offsetZ = this.getRandom().nextInt(radius * 2 + 1) - radius;

            net.minecraft.core.BlockPos pos = this.blockPosition().offset(offsetX, offsetY, offsetZ);
            if (this.level().isEmptyBlock(pos) && this.level().isEmptyBlock(pos.above())) {
                return pos;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.spawnPoint = this.blockPosition();
        this.setBoundPos(this.spawnPoint);
        this.outpouringCooldown = 200;
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Stun", this.stun);
        if (this.spawnPoint != null) {
            compound.put("SpawnPoint", net.minecraft.nbt.NbtUtils.writeBlockPos(this.spawnPoint));
        }

        if (this.targetPoint != null) {
            compound.put("TargetPoint", net.minecraft.nbt.NbtUtils.writeBlockPos(this.targetPoint));
        }
        compound.putInt("OutpouringTick", this.outpouringTick);
        compound.putInt("OutpouringCooldown", this.outpouringCooldown);
        compound.putBoolean("IsOutpouring", this.isOutpouring());
        compound.putBoolean("GhastAutonomous", this.isAutonomous());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("SpawnPoint")) {
            this.spawnPoint = net.minecraft.nbt.NbtUtils.readBlockPos(compound.getCompound("SpawnPoint"));
        }

        if (compound.contains("TargetPoint")) {
            this.targetPoint = net.minecraft.nbt.NbtUtils.readBlockPos(compound.getCompound("TargetPoint"));
        }
        this.stun = compound.getInt("Stun");
        this.outpouringTick = compound.getInt("OutpouringTick");
        this.outpouringCooldown = compound.getInt("OutpouringCooldown");
        this.setOutpouring(compound.getBoolean("IsOutpouring"));
        if (compound.contains("GhastAutonomous")) {
            this.setAutonomous(compound.getBoolean("GhastAutonomous"));
        }
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            double yOffset = this.getPassengersRidingOffset()
                    + passenger.getMyRidingOffset()
                    + 1.5D;
            float yaw = this.getYRot() * Mth.DEG_TO_RAD;
            double forwardOffset = 3.5D;
            double xOff = -Mth.sin(yaw) * forwardOffset;
            double zOff = Mth.cos(yaw) * forwardOffset;
            moveFunc.accept(passenger,
                    this.getX() + xOff,
                    this.getY() + yOffset,
                    this.getZ() + zOff);
        }
    }

    @Override
    public boolean isPushable() {
        return !this.isDeadOrDying();
    }

    protected void doPush(Entity p_28839_) {
        if (p_28839_ instanceof LivingEntity livingEntity) {
            if (SummonTargetGoal.predicate(this).test(livingEntity) && this.getRandom().nextInt(20) == 0) {
                this.setTarget(livingEntity);
            }
        }

        super.doPush(p_28839_);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof LivingEntity) {
                if (!this.isAutonomous()) {
                    return (LivingEntity) entity;
                }
            }
        }
        return null;
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
                float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float speedFactor = 0.2F;
                float finalSpeed = speed * speedFactor;
                float strafe = rider.xxa * finalSpeed;
                float forward = rider.zza * finalSpeed;
                if (forward <= 0.0F) {
                    forward *= 0.25F;
                }
                Vec3 moveVector = new Vec3(strafe, 0.0D, forward);
                this.moveRelative(finalSpeed, moveVector);
                double verticalMotion = this.getDeltaMovement().y;
                if (this.isGoingUp()) {
                    verticalMotion += 0.10D;
                    if (this.onGround()) {
                        verticalMotion += 0.3D;
                        this.setOnGround(false);
                    }
                }
                if (this.isGoingDown()) {
                    verticalMotion -= 0.10D;
                }
                verticalMotion = Mth.clamp(verticalMotion, -0.5D, 0.5D);
                this.setDeltaMovement(this.getDeltaMovement().x, verticalMotion, this.getDeltaMovement().z);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
                this.lerpSteps = 0;
                this.calculateEntityAnimation(false);
                return;
            }
        }
        super.travel(pTravelVector);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getTrueOwner() == player) {
            if (player.isShiftKeyDown()) {
                return super.mobInteract(player, hand);
            } else {
                if (!this.level().isClientSide) {
                    player.setYRot(this.getYRot());
                    player.setXRot(this.getXRot());
                    player.startRiding(this);
                    return InteractionResult.CONSUME;
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    public void handleFlightControl(boolean flyUp, boolean flyDown) {
        this.entityData.set(DATA_GHAST_IS_GOING_UP, flyUp);
        this.entityData.set(DATA_GHAST_IS_GOING_DOWN, flyDown);
    }

    public boolean isGoingUp() {
        return this.entityData.get(DATA_GHAST_IS_GOING_UP);
    }

    public boolean isGoingDown() {
        return this.entityData.get(DATA_GHAST_IS_GOING_DOWN);
    }

    @Override
    public void setAutonomous(boolean autonomous) {
        this.entityData.set(DATA_GHAST_AUTO_MODE, autonomous);
    }

    @Override
    public boolean isAutonomous() {
        return this.entityData.get(DATA_GHAST_AUTO_MODE);
    }

    public boolean hasAllyOnTop() {
        AABB topArea = new AABB(
                this.getBoundingBox().minX - 0.5D, this.getBoundingBox().maxY - 0.25D,
                this.getBoundingBox().minZ - 0.5D, this.getBoundingBox().maxX + 0.5D,
                this.getBoundingBox().maxY + 2.0D, this.getBoundingBox().maxZ + 0.5D);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, topArea);
        for (Player p : players) {
            if (!this.getPassengers().contains(p) && MobUtil.areAllies(this, p)) {
                return true;
            }
        }
        return false;
    }

    static class GiantGhastMoveControl extends MoveControl {
        private final GiantGhast ghast;
        private int floatDuration;

        public GiantGhastMoveControl(GiantGhast p_i45838_1_) {
            super(p_i45838_1_);
            this.ghast = p_i45838_1_;
        }

        public void tick() {
            if (this.ghast.isVehicle() && !this.ghast.isAutonomous()) {
                return;
            }
            if (this.operation == Operation.MOVE_TO && this.floatDuration-- <= 0) {
                this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
                Vec3 vector3d = new Vec3(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(),
                        this.wantedZ - this.ghast.getZ());
                double d0 = vector3d.length();
                vector3d = vector3d.normalize();
                if (this.canReach(vector3d, Mth.ceil(d0))) {
                    this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(vector3d.scale(0.1)));
                } else {
                    this.operation = Operation.WAIT;
                }
            }

        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.ghast.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.ghast.level().noCollision(this.ghast, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class GiantGhastLookControl extends LookControl {
        public GiantGhastLookControl(Mob p_33235_) {
            super(p_33235_);
        }

        public void tick() {
            if (this.mob.getTarget() == null) {
                Vec3 vec3 = this.mob.getDeltaMovement();
                this.mob.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI));
                this.mob.yBodyRot = this.mob.getYRot();
            } else {
                LivingEntity livingentity = this.mob.getTarget();
                double d0 = 64.0D;
                if (livingentity.distanceToSqr(this.mob) < 4096.0D) {
                    double d1 = livingentity.getX() - this.mob.getX();
                    double d2 = livingentity.getZ() - this.mob.getZ();
                    this.mob.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    this.mob.yBodyRot = this.mob.getYRot();
                }
            }
        }
    }

    static class FlyingGoal extends Goal {
        private final GiantGhast ghast;

        public FlyingGoal(GiantGhast p_i45836_1_) {
            this.ghast = p_i45836_1_;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl moveControl = this.ghast.getMoveControl();
            if (!this.ghast.isCommanded() && !this.ghast.isStaying()
                    && !(this.ghast.isVehicle() && !this.ghast.isAutonomous())
                    && !this.ghast.hasAllyOnTop()) {
                if (!moveControl.hasWanted()) {
                    return true;
                } else {
                    double d0 = moveControl.getWantedX() - this.ghast.getX();
                    double d1 = moveControl.getWantedY() - this.ghast.getY();
                    double d2 = moveControl.getWantedZ() - this.ghast.getZ();
                    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                    return d3 < (double) 1.0F || d3 > (double) 3600.0F;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void start() {
            RandomSource random = this.ghast.getRandom();
            float distance = 16.0F;
            BlockPos blockPos = null;
            if (this.ghast.getBoundPos() != null) {
                blockPos = this.ghast.getBoundPos();
                if (this.ghast.getTarget() != null) {
                    BlockPos blockPos1 = this.ghast.getTarget().blockPosition().above(4);
                    if (this.ghast.isWithinGuard(blockPos1)) {
                        blockPos = blockPos1;
                    }
                }
            } else if (this.ghast.getTrueOwner() != null && this.ghast.isFollowing()) {
                blockPos = this.ghast.getTrueOwner().blockPosition().above(4);
            } else if (this.ghast.getTarget() != null) {
                LivingEntity target = this.ghast.getTarget();
                Vec3 targetLookVector = target.getViewVector(1.0F);
                Vec3 horizontalLookVector = new Vec3(targetLookVector.x, 0, targetLookVector.z).normalize();
                Vec3 behindPosition = this.ghast.position().subtract(horizontalLookVector.scale(8.0));
                blockPos = BlockPos.containing(behindPosition);
            }

            if (blockPos != null) {
                if (this.ghast.distanceToSqr(Vec3.atCenterOf(blockPos)) < (double) Mth.square(distance)) {
                    Vec3 vector3d = Vec3.atCenterOf(blockPos).subtract(this.ghast.position()).normalize();
                    double X = this.ghast.getX() + vector3d.x * (double) distance
                            + (double) ((random.nextFloat() * 2.0F - 1.0F) * distance);
                    double Y = this.ghast.getY() + vector3d.y * (double) distance
                            + (double) ((random.nextFloat() * 2.0F - 1.0F) * distance);
                    double Z = this.ghast.getZ() + vector3d.z * (double) distance
                            + (double) ((random.nextFloat() * 2.0F - 1.0F) * distance);
                    this.ghast.getMoveControl().setWantedPosition(X, Y, Z, (double) 0.25F);
                } else {
                    this.ghast.getMoveControl().setWantedPosition((double) blockPos.getX() + (double) 0.5F,
                            (double) blockPos.getY(), (double) blockPos.getZ() + (double) 0.5F, (double) 0.25F);
                }
            } else {
                double d0 = this.ghast.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * distance);
                double d1 = this.ghast.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * distance);
                double d2 = this.ghast.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * distance);
                this.ghast.getMoveControl().setWantedPosition(d0, d1, d2, (double) 0.25F);
            }

        }
    }

    static class GiantGhastLookGoal extends Goal {
        private final GiantGhast ghast;

        public GiantGhastLookGoal(GiantGhast pGhast) {
            this.ghast = pGhast;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.ghast.getTarget() == null) {
                Vec3 vec3 = this.ghast.getDeltaMovement();
                this.ghast.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI));
                this.ghast.yBodyRot = this.ghast.getYRot();
            } else {
                LivingEntity livingentity = this.ghast.getTarget();
                double d0 = 64.0D;
                if (livingentity.distanceToSqr(this.ghast) < 4096.0D) {
                    double d1 = livingentity.getX() - this.ghast.getX();
                    double d2 = livingentity.getZ() - this.ghast.getZ();
                    this.ghast.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    this.ghast.yBodyRot = this.ghast.getYRot();
                }
            }
        }
    }

    static class LookAroundGoal extends Goal {
        private final GiantGhast ghast;

        public LookAroundGoal(GiantGhast p_i45839_1_) {
            this.ghast = p_i45839_1_;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.ghast.getTarget() == null) {
                Vec3 vector3d = this.ghast.getDeltaMovement();
                this.ghast.setYRot(-((float) Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float) Math.PI));
            } else {
                LivingEntity livingentity = this.ghast.getTarget();
                double d1 = livingentity.getX() - this.ghast.getX();
                double d2 = livingentity.getZ() - this.ghast.getZ();
                this.ghast.getLookControl().setLookAt(livingentity, 10.0F, (float) this.ghast.getMaxHeadXRot());
                this.ghast.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
            }

            this.ghast.yBodyRot = this.ghast.getYRot();
        }
    }

    static class GiantGhastShootFireballGoal extends Goal {
        private final GiantGhast ghast;

        public GiantGhastShootFireballGoal(GiantGhast pGhast) {
            this.ghast = pGhast;
        }

        public boolean canUse() {
            LivingEntity target = this.ghast.getTarget();
            if (target == null || !target.isAlive() || this.ghast.isShooting ||
                    this.ghast.shootCooldown > 0 || this.ghast.isOutpouring()) {
                return false;
            }
            return this.ghast.hasLineOfSight(target);
        }

        public boolean canContinueToUse() {
            return this.ghast.isShooting;
        }

        public void start() {
            this.ghast.isShooting = true;
            this.ghast.shootTick = 0;
        }

        public void stop() {
            this.ghast.setCharging(false);
            if (this.ghast.shootTick < 80) {
                this.ghast.isShooting = false;
                this.ghast.shootTick = 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class OutpouringGoal extends Goal {
        private final GiantGhast ghast;

        public OutpouringGoal(GiantGhast pGhast) {
            this.ghast = pGhast;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.ghast.getTarget();
            if (target == null || !target.isAlive() || this.ghast.isOutpouring() ||
                    this.ghast.getHealth() >= this.ghast.getMaxHealth() / 2 ||
                    this.ghast.outpouringCooldown > 0 || this.ghast.isShooting) {
                return false;
            }
            return this.ghast.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            if (!this.ghast.isOutpouring()) {
                return false;
            }
            if (this.ghast.outpouringTick <= 0) {
                return false;
            }
            LivingEntity target = this.ghast.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.ghast.setOutpouring(true);
            this.ghast.outpouringTick = 240;
            this.ghast.playLastWordsSound();
        }

        @Override
        public void tick() {
            LivingEntity target = this.ghast.getTarget();
            if (target == null) {
                return;
            }

            this.ghast.getLookControl().setLookAt(target, 30.0F, (float) this.ghast.getMaxHeadXRot());

            int currentTick = this.ghast.outpouringTick;
            if (currentTick < 220 && currentTick > 0) {
                Level world = this.ghast.level();
                Vec3 centerPos = new Vec3(this.ghast.getX(), this.ghast.getY(0.5D), this.ghast.getZ());
                float attackDamage = (float) this.ghast.getAttributeValue(Attributes.ATTACK_DAMAGE);

                if (currentTick % 4 == 0) {
                    this.ghast.spawnSingleHellBolt(world, centerPos, target, attackDamage);
                }
                if (currentTick % 30 == 0) {
                    this.ghast.spawnSingleHellBlast(world, centerPos, target, attackDamage);
                    this.ghast.playFireballShootSound();
                }
                if (currentTick % 60 == 5) {
                    this.ghast.spawnSideAttackFireballs();
                    this.ghast.playFireballShootSound();
                }
                if (currentTick % 15 == 0) {
                    this.ghast.spawnSingleLavaball(world, centerPos, target, attackDamage);
                    this.ghast.playFireballShootSound();
                }
            }
        }

        @Override
        public void stop() {
            int endingTick = Math.max(0, this.ghast.outpouringTick);
            int cooldown = 1600 * (240 - endingTick) / 240;
            this.ghast.outpouringCooldown = cooldown;
            this.ghast.outpouringTick = 0;
            this.ghast.setOutpouring(false);
        }
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (!this.level().isClientSide) {
            if (!this.isHostile()) {
                if (this.getTrueOwner() != null
                        && this.getTrueOwner() instanceof net.minecraft.world.entity.player.Player player) {
                    ItemStack gloomytear = new ItemStack(
                            com.k1sak1.goetyawaken.common.items.ModItems.GLOOMY_TEARS.get());
                    com.Polarice3.Goety.common.entities.projectiles.FlyingItem flyingItem = new com.Polarice3.Goety.common.entities.projectiles.FlyingItem(
                            com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                            this.level(),
                            this.getX(),
                            this.getY() + 1.0D,
                            this.getZ());
                    flyingItem.setOwner(this.getTrueOwner());
                    flyingItem.setItem(gloomytear);
                    flyingItem.setSecondsCool(30);
                    this.level().addFreshEntity(flyingItem);
                }
            }
        }
        super.die(pDamageSource);
    }
}
