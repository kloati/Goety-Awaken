package com.k1sak1.goetyawaken.common.entities.ally.golem;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import com.Polarice3.Goety.api.entities.IGolem;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.golem.RaiderGolemServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.common.entities.ally.golem.damagecap.MonstrosityDamageCapHandler;
import com.k1sak1.goetyawaken.common.entities.ally.golem.dynamicshield.MushroomDynamicShieldHandler;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModMagmaBomb;
import com.k1sak1.goetyawaken.common.items.MushroomMonstrosityHeadItem;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import com.Polarice3.Goety.common.entities.projectiles.VoidShockBomb;
import com.Polarice3.Goety.common.entities.projectiles.BlossomBall;
import com.Polarice3.Goety.common.entities.projectiles.BlastFungus;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public class MushroomMonstrosity extends RaiderGolemServant implements PlayerRideable, IAutoRideable, IGolem {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(MushroomMonstrosity.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(MushroomMonstrosity.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData.defineId(MushroomMonstrosity.class,
            EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> DYNAMIC_SHIELD_DURATION = SynchedEntityData.defineId(
            MushroomMonstrosity.class,
            EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LAST_HURT_TICK = SynchedEntityData.defineId(
            MushroomMonstrosity.class,
            EntityDataSerializers.INT);
    public static String ACTIVATE = "activate";
    public static String IDLE = "idle";
    public static String WALK = "walk";
    public static String TO_SIT = "to_sit";
    public static String TO_STAND = "to_stand";
    public static String SIT = "sit";
    public static String DEATH = "death";
    private int activateTick;
    public int isSittingDown;
    public int isStandingUp;
    public float deathRotation = 0.0F;
    public int deathTime = 0;
    public boolean clientStop;
    public AnimationState activateAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState toSitAnimationState = new AnimationState();
    public AnimationState toStandAnimationState = new AnimationState();
    public AnimationState sitAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();
    public AnimationState smashOldAnimationState = new AnimationState();
    public AnimationState smashAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState summon2AnimationState = new AnimationState();
    public AnimationState spitAnimationState = new AnimationState();
    public AnimationState strafeAnimationState = new AnimationState();
    private float glowAmount = 0.01F;
    private float bigGlow = 0.0F;
    private float minorGlow = 0.0F;
    public int smashOldTick;
    private int smashOldCool;
    public int smashTick;
    private int smashCool;
    public int summonTick;
    private int summonCool;
    private int summonCount;
    public int summon2Tick;
    private int summon2Cool;
    private int summon2Count;
    private int summon2TotalCount;
    public int spitTick;
    private int spitCool;
    private int dischargeCool;
    public int starfeTick;
    private int starfeCool;
    private int starfeFireCounter;
    private int starfeFireInterval = 1;
    private int starfeProjectileType;
    private MushroomDynamicShieldHandler dynamicShieldHandler;
    private MonstrosityDamageCapHandler damageCapHandler;
    private static final Set<ResourceLocation> MUSHROOM_BLOCKS = new HashSet<>();
    private static final int MUSHROOM_CHECK_RADIUS = 16;
    private static final double DAMAGE_REDUCTION_PER_BLOCK = 0.0005;
    private static final double MAX_DAMAGE_REDUCTION = 0.75;
    private int cachedMushroomCount = -1;
    private int lastMushroomCheckTick = -1;
    private int lastCheckedX = Integer.MIN_VALUE;
    private int lastCheckedY = Integer.MIN_VALUE;
    private int lastCheckedZ = Integer.MIN_VALUE;

    public MushroomMonstrosity(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        this.moveControl = new SlowRotMoveControl(this);
        this.dynamicShieldHandler = new MushroomDynamicShieldHandler(this);
        this.dynamicShieldHandler.setShieldDuration(MushroomDynamicShieldHandler.DEFAULT_LIMIT_TIME);
        this.damageCapHandler = new MonstrosityDamageCapHandler(this);
        initializeMushroomBlocks();
        this.setConfigurableAttributes();

    }

    private static void initializeMushroomBlocks() {
        MUSHROOM_BLOCKS.add(new ResourceLocation("minecraft", "mycelium"));
        MUSHROOM_BLOCKS.add(new ResourceLocation("minecraft", "mushroom_stem"));
        MUSHROOM_BLOCKS.add(new ResourceLocation("minecraft", "brown_mushroom_block"));
        MUSHROOM_BLOCKS.add(new ResourceLocation("minecraft", "red_mushroom_block"));
        MUSHROOM_BLOCKS.add(new ResourceLocation("minecraft", "brown_mushroom"));
        MUSHROOM_BLOCKS.add(new ResourceLocation("minecraft", "red_mushroom"));
        MUSHROOM_BLOCKS.add(new ResourceLocation("goetyawaken", "mushroom_coated_alloy_block"));
        MUSHROOM_BLOCKS.add(new ResourceLocation("goetyawaken", "poisonous_mushroom"));
    }

    public int getNearbyMushroomCount() {
        BlockPos currentPos = this.blockPosition();
        boolean shouldRecalculate = lastMushroomCheckTick != this.tickCount ||
                Math.abs(currentPos.getX() - lastCheckedX) > 2 ||
                Math.abs(currentPos.getY() - lastCheckedY) > 2 ||
                Math.abs(currentPos.getZ() - lastCheckedZ) > 2;

        if (!shouldRecalculate && cachedMushroomCount != -1) {
            return cachedMushroomCount;
        }

        int count = 0;
        if (this.level() != null) {
            BlockPos centerPos = currentPos;
            int radius = MUSHROOM_CHECK_RADIUS;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = centerPos.offset(x, y, z);
                        if (this.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= radius
                                * radius) {
                            BlockState blockState = this.level().getBlockState(pos);
                            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());

                            if (MUSHROOM_BLOCKS.contains(blockId)) {
                                count++;
                            }
                        }
                    }
                }
            }
        }

        cachedMushroomCount = count;
        lastMushroomCheckTick = this.tickCount;
        lastCheckedX = currentPos.getX();
        lastCheckedY = currentPos.getY();
        lastCheckedZ = currentPos.getZ();

        return count;
    }

    public double getMushroomDamageReduction() {
        int mushroomCount = getNearbyMushroomCount();
        double reduction = mushroomCount * DAMAGE_REDUCTION_PER_BLOCK;
        return Math.min(reduction, MAX_DAMAGE_REDUCTION);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.miscGoal();
        this.goalSelector.addGoal(2, new SummonGoal(this));
        this.goalSelector.addGoal(2, new SmashGoal(this));
        this.goalSelector.addGoal(2, new SmashOldGoal(this));
        this.goalSelector.addGoal(2, new Summon2Goal(this));
        this.goalSelector.addGoal(2, new SpitGoal(this));
        this.goalSelector.addGoal(2, new DischargeGoal(this));
        this.goalSelector.addGoal(1, new StarfeGoal(this));
        this.goalSelector.addGoal(6, new MushroomMonstrosity.AttackGoal(this, 1.2D));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(8, new RaiderWanderGoal<>(this, 1.0D, 10));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.MushroomMonstrosityHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MushroomMonstrosityArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.MushroomMonstrosityArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10D)
                .add(Attributes.ATTACK_KNOCKBACK, 6.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 2.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MushroomMonstrosityDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.MushroomMonstrosityFollowRange.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.MushroomMonstrosityHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.MushroomMonstrosityArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.MushroomMonstrosityArmorToughness.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.MushroomMonstrosityDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.MushroomMonstrosityFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(AUTO_MODE, false);
        this.entityData.define(DYNAMIC_SHIELD_DURATION, 0);
        this.entityData.define(LAST_HURT_TICK, 0);
    }

    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTO_MODE, autonomous);
        if (autonomous) {
            this.playSound(SoundEvents.ARROW_HIT_PLAYER);
            if (!this.isWandering()) {
                this.setWandering(true);
                this.setStaying(false);
            }
        }
    }

    public boolean isAutonomous() {
        return this.entityData.get(AUTO_MODE);
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        if (!this.isNoAi()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Mob mob) {
                if (MobsConfig.ServantRideAutonomous.get()) {
                    return null;
                }
                return mob;
            } else if (entity instanceof LivingEntity
                    && !this.isAutonomous()) {
                return (LivingEntity) entity;
            }
        }

        return null;
    }

    public boolean isControlledByLocalInstance() {
        return this.isEffectiveAi();
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight();
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    public void travel(@NotNull Vec3 pTravelVector) {
        if (this.isAlive()) {
            LivingEntity rider = this.getControllingPassenger();
            if (this.isVehicle()
                    && rider instanceof Player player
                    && !this.clientStopMoving()
                    && !this.isAutonomous()) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;
                float speed = this.getRiddenSpeed(player);
                float f = rider.xxa * speed;
                float f1 = rider.zza * speed;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;

                this.calculateEntityAnimation(false);
            } else {
                if (this.clientStopMoving()) {
                    pTravelVector = new Vec3(0, pTravelVector.y, 0);
                }
                super.travel(pTravelVector);
            }
        }
    }

    public boolean clientStopMoving() {
        if (this.level().isClientSide) {
            return this.clientStop;
        } else {
            return this.isActivating() || this.isSmashOldAttacking() || this.isSmashAttacking() || this.isSummoning()
                    || this.isSummoning2()
                    || this.isSpitting();
        }
    }

    public boolean isBelching() {
        return this.getFlag(2);
    }

    public boolean isSummoning() {
        return this.summonTick > 0;
    }

    public float getBigGlow() {
        return this.bigGlow;
    }

    public float getMinorGlow() {
        return this.minorGlow;
    }

    private void glow() {
        this.minorGlow = Mth.clamp(this.minorGlow + this.glowAmount, 0, 1);
        if (this.minorGlow == 0 || this.minorGlow == 1) {
            this.glowAmount *= -1;
        }
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double) (this.getBbWidth() * 6.0F + enemy.getBbWidth()) + 1.0D;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    protected float nextStep() {
        return this.moveDist + 1.5F;
    }

    @Override
    protected @NotNull BodyRotationControl createBodyControl() {
        return new RMBodyRotateControl(this);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.REDSTONE_MONSTROSITY_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.REDSTONE_MONSTROSITY_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.REDSTONE_MONSTROSITY_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        if (this.isAggressive()) {
            this.playSound(ModSounds.REDSTONE_MONSTROSITY_CHASE.get(), 2.0F, 1.0F);
        } else {
            this.playSound(ModSounds.REDSTONE_MONSTROSITY_STEP.get(), 2.0F, 1.0F);
        }
    }

    @Override
    protected float getSoundVolume() {
        return 3.0F;
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Pose.EMERGING) ? 1 : 0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_219420_) {
        super.recreateFromPacket(p_219420_);
        if (p_219420_.getData() == 1) {
            this.setPose(Pose.EMERGING);
        }

    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "activate")) {
            return 1;
        } else if (Objects.equals(animation, "idle")) {
            return 2;
        } else if (Objects.equals(animation, "walk")) {
            return 4;
        } else if (Objects.equals(animation, "to_sit")) {
            return 6;
        } else if (Objects.equals(animation, "to_stand")) {
            return 7;
        } else if (Objects.equals(animation, "sit")) {
            return 8;
        } else if (Objects.equals(animation, "death")) {
            return 10;
        } else if (Objects.equals(animation, "smash_old")) {
            return 11;
        } else if (Objects.equals(animation, "smash")) {
            return 12;
        } else if (Objects.equals(animation, "summon")) {
            return 13;
        } else if (Objects.equals(animation, "summon2")) {
            return 14;
        } else if (Objects.equals(animation, "spit")) {
            return 15;
        } else if (Objects.equals(animation, "strafe")) {
            return 16;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAllAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.activateAnimationState);
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.toSitAnimationState);
        animationStates.add(this.toStandAnimationState);
        animationStates.add(this.sitAnimationState);
        animationStates.add(this.deathAnimationState);
        animationStates.add(this.smashOldAnimationState);
        animationStates.add(this.smashAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.summon2AnimationState);
        animationStates.add(this.spitAnimationState);
        animationStates.add(this.strafeAnimationState);
        return animationStates;
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAllAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level().isClientSide()) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0:
                        break;
                    case 1:
                        this.activateAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.activateAnimationState);
                        break;
                    case 2:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 4:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        // this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case 6:
                        this.stopMostAnimation(this.toSitAnimationState);
                        this.toSitAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 7:
                        this.stopMostAnimation(this.toStandAnimationState);
                        this.toStandAnimationState.startIfStopped(this.tickCount);
                        break;
                    case 8:
                        this.sitAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.sitAnimationState);
                        break;
                    case 10:
                        this.deathAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.deathAnimationState);
                        break;
                    case 11:
                        this.smashOldAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.smashOldAnimationState);
                        break;
                    case 12:
                        this.smashAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.smashAnimationState);
                        break;
                    case 13:
                        this.summonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case 14:
                        this.summon2AnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summon2AnimationState);
                        break;
                    case 15:
                        this.spitAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.spitAnimationState);
                        break;
                    case 16:
                        this.strafeAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.strafeAnimationState);
                        break;
                }
            }
        }
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlag(int mask, boolean value) {
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
        this.setFlag(1, attacking);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ActivateTick", this.activateTick);
        pCompound.putInt("SmashOldTick", this.smashOldTick);
        pCompound.putInt("SmashOldCool", this.smashOldCool);
        pCompound.putInt("SmashTick", this.smashTick);
        pCompound.putInt("SmashCool", this.smashCool);
        pCompound.putInt("SummonTick", this.summonTick);
        pCompound.putInt("SummonCool", this.summonCool);
        pCompound.putInt("SummonCount", this.summonCount);
        pCompound.putInt("Summon2Tick", this.summon2Tick);
        pCompound.putInt("Summon2Cool", this.summon2Cool);
        pCompound.putInt("Summon2Count", this.summon2Count);
        pCompound.putInt("Summon2TotalCount", this.summon2TotalCount);
        pCompound.putInt("SpitTick", this.spitTick);
        pCompound.putInt("SpitCool", this.spitCool);
        pCompound.putInt("DischargeCool", this.dischargeCool);
        pCompound.putInt("StarfeTick", this.starfeTick);
        pCompound.putInt("StarfeCool", this.starfeCool);
        pCompound.putBoolean("AutoMode", this.isAutonomous());
        pCompound.putFloat("hpoint", this.getHealth());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ActivateTick")) {
            this.activateTick = pCompound.getInt("ActivateTick");
        }
        if (pCompound.contains("SmashOldTick")) {
            this.smashOldTick = pCompound.getInt("SmashOldTick");
        }
        if (pCompound.contains("SmashOldCool")) {
            this.smashOldCool = pCompound.getInt("SmashOldCool");
        }
        if (pCompound.contains("SmashTick")) {
            this.smashTick = pCompound.getInt("SmashTick");
        }
        if (pCompound.contains("SmashCool")) {
            this.smashCool = pCompound.getInt("SmashCool");
        }
        if (pCompound.contains("SummonTick")) {
            this.summonTick = pCompound.getInt("SummonTick");
        }
        if (pCompound.contains("SummonCool")) {
            this.summonCool = pCompound.getInt("SummonCool");
        }
        if (pCompound.contains("SummonCount")) {
            this.summonCount = pCompound.getInt("SummonCount");
        }
        if (pCompound.contains("Summon2Tick")) {
            this.summon2Tick = pCompound.getInt("Summon2Tick");
        }
        if (pCompound.contains("Summon2Cool")) {
            this.summon2Cool = pCompound.getInt("Summon2Cool");
        }
        if (pCompound.contains("Summon2Count")) {
            this.summon2Count = pCompound.getInt("Summon2Count");
        }
        if (pCompound.contains("Summon2TotalCount")) {
            this.summon2TotalCount = pCompound.getInt("Summon2TotalCount");
        }
        if (pCompound.contains("SpitTick")) {
            this.spitTick = pCompound.getInt("SpitTick");
        }
        if (pCompound.contains("SpitCool")) {
            this.spitCool = pCompound.getInt("SpitCool");
        }
        if (pCompound.contains("DischargeCool")) {
            this.dischargeCool = pCompound.getInt("DischargeCool");
        }
        if (pCompound.contains("StarfeTick")) {
            this.starfeTick = pCompound.getInt("StarfeTick");
        }
        if (pCompound.contains("StarfeCool")) {
            this.starfeCool = pCompound.getInt("StarfeCool");
        }
        if (pCompound.contains("AutoMode")) {
            this.setAutonomous(pCompound.getBoolean("AutoMode"));
        }
        if (pCompound.contains("hpoint")) {
            float savedHealth = pCompound.getFloat("hpoint");
            this.setHpoint(savedHealth);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, net.minecraft.world.DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED || pReason == MobSpawnType.COMMAND) {
            this.setPose(Pose.EMERGING);
        }
        this.isStandingUp = 0;
        this.setConfigurableAttributes();
        this.setHpoint(this.getMaxHealth());
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.REDSTONE_MONSTROSITY_AMBIENT.get();
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return !this.isSmashOldAttacking() && !this.isSmashAttacking()
                && !this.isSummoning() && !this.isSummoning2() && !this.isSpitting()
                && !this.isStarfeAttacking() && !this.isActivating() && super.hasLineOfSight(p_149755_);
    }

    public boolean canAnimateMove() {
        return super.canAnimateMove() && this.getCurrentAnimation() == this.getAnimationState(WALK);
    }

    private boolean isActuallyMoving() {
        double d0 = this.getX() - this.xo;
        double d1 = this.getZ() - this.zo;
        return d0 * d0 + d1 * d1 > (double) 2.5000003E-7F;
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F;
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isActivating() || this.isSmashOldAttacking()
                || this.isSmashAttacking()
                || this.isSummoning() || this.isSummoning2() || this.isSpitting()
                || this.isStarfeAttacking();
    }

    public boolean isActivating() {
        return this.hasPose(Pose.EMERGING);
    }

    public boolean isEasyMode() {
        return com.k1sak1.goetyawaken.Config.MUSHROOM_MONSTROSITY_EASY_MODE.get();
    }

    @Override
    public boolean isPushable() {
        return !this.isDeadOrDying();
    }

    public void tick() {
        super.tick();
        if (this.isDeadOrDying()) {
            this.setAnimationState(DEATH);
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }
        if (this.hasPose(Pose.EMERGING)) {
            ++this.activateTick;
            this.isStandingUp = 0;
            this.setAnimationState(ACTIVATE);
            if (this.activateTick == 1) {
                this.playSound(ModSounds.REDSTONE_MONSTROSITY_AWAKEN.get(), 10.0F, 1.0F);
            }
            if (this.activateTick == 40) {
                CameraShake.cameraShake(this.level(), this.position(), 40.0F, 0.5F, 0, 20);
            }
            if (this.activateTick > MathHelper.secondsToTicks(3.25F)) {
                this.setPose(Pose.STANDING);
            }
        }
        if (!this.level().isClientSide()) {
            if (this.isAlive() && !this.isActivating()) {
                if (!this.level().getBlockState(this.blockPosition().below()).isAir()) {
                    if (this.tickCount % 10 == 0) {
                        if (this.level() instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ModParticleTypes.BIG_FIRE_GROUND.get(), this.getX(), this.getY(),
                                    this.getZ(), 0, 0.0D, 0.0D, 0.0D, 0.5F);
                        }
                    }
                }

                if (this.isSmashOldAttacking()) {
                    this.makeBigGlow();
                    --this.smashOldTick;
                    if (this.smashOldTick == 37) {
                        this.performSmashOldAttack();
                    }

                    if (this.level() instanceof ServerLevel serverLevel && smashOldTick >= 37) {
                        Vec3 vec3 = this.getHorizontalLookAngle();
                        Vec3 attackPos = this.position().add(vec3.scale(5.0D));
                        com.Polarice3.Goety.utils.ColorUtil colorUtil = new com.Polarice3.Goety.utils.ColorUtil(
                                0xff8200);
                        serverLevel.sendParticles(
                                new com.Polarice3.Goety.client.particles.AoEParticleOption(9.0F, 1),
                                attackPos.x,
                                this.getY() + 0.25F,
                                attackPos.z,
                                0,
                                colorUtil.red(),
                                colorUtil.green(),
                                colorUtil.blue(),
                                1.0F);
                    }

                    if (this.smashOldTick <= 0) {
                        this.setSmashOldAttacking(false);
                        if (this.smashOldCool <= 0) {
                            this.smashOldCool = MathHelper.secondsToTicks(8);
                        }
                    }
                }

                if (this.isSmashAttacking()) {
                    this.makeBigGlow();
                    --this.smashTick;
                    if (this.smashTick == 31) {
                        this.performSmashAttack();
                    }
                    if (this.level() instanceof ServerLevel serverLevel && smashTick >= 31) {
                        Vec3 vec3 = this.getHorizontalLookAngle();
                        Vec3 attackPos = this.position().add(vec3.scale(5.0D));
                        com.Polarice3.Goety.utils.ColorUtil colorUtil = new com.Polarice3.Goety.utils.ColorUtil(
                                0xff8200);
                        serverLevel.sendParticles(
                                new com.Polarice3.Goety.client.particles.AoEParticleOption(16.0F, 1),
                                attackPos.x,
                                this.getY() + 0.25F,
                                attackPos.z,
                                0,
                                colorUtil.red(),
                                colorUtil.green(),
                                colorUtil.blue(),
                                1.0F);
                    }

                    if (this.smashTick <= 0) {
                        this.setSmashAttacking(false);
                        if (this.smashCool <= 0) {
                            this.smashCool = MathHelper.secondsToTicks(8);
                        }
                    }
                }

                if (this.isSummoning()) {
                    this.makeBigGlow();
                    --this.summonTick;

                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 5; ++i) {
                            double d0 = serverLevel.random.nextGaussian() * 0.02D;
                            double d1 = serverLevel.random.nextGaussian() * 0.02D;
                            double d2 = serverLevel.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(
                                    com.Polarice3.Goety.client.particles.ModParticleTypes.ELECTRIC.get(),
                                    this.getRandomX(0.5D),
                                    this.getEyeY() - serverLevel.random.nextInt(2), this.getRandomZ(0.5D), 0, d0, d1,
                                    d2, 0.5F);
                        }

                        if (this.summonTick < MathHelper.secondsToTicks(4.7F - 1)
                                && this.summonTick >= MathHelper.secondsToTicks(4.7F - 2)) {
                            if (!this.level().getBlockState(this.blockPosition().below()).isAir()) {
                                for (int j = 0; j < 4; ++j) {
                                    double d1 = this.getX()
                                            + (this.level().getRandom().nextDouble() - 0.5D)
                                                    * (double) this.getBbWidth() * 2.0D;
                                    double d2 = this.blockPosition().getY() + 0.5F;
                                    double d3 = this.getZ()
                                            + (this.level().getRandom().nextDouble() - 0.5D)
                                                    * (double) this.getBbWidth() * 2.0D;
                                    serverLevel.sendParticles(
                                            com.Polarice3.Goety.client.particles.ModParticleTypes.BIG_FIRE_GROUND.get(),
                                            d1, d2,
                                            d3, 0,
                                            0.0D, 0.0D, 0.0D, 0.5F);
                                }
                            }
                        }
                    }
                    if (this.summonTick == 48 && this.summonCount > 0) {
                        this.performSummon();
                        --this.summonCount;
                    }

                    if (this.summonTick <= 0) {
                        if (this.summonCool <= 0) {
                            this.summonCool = MathHelper.secondsToTicks(24);
                        }
                    }
                }

                if (this.isSummoning2()) {
                    LivingEntity target = this.getTarget();
                    if (target == null || !target.isAlive() || target.isRemoved()) {
                        this.summon2Tick = 0;
                        this.summon2Count = 0;
                    } else {
                        this.makeBigGlow();
                        --this.summon2Tick;
                        if (this.summon2Tick == 10) {
                            this.performSummon2();
                        }
                        if (this.summon2Tick <= 0) {
                            this.summon2Tick = 0;
                            --this.summon2Count;
                            if (this.summon2Count > 0) {
                                LivingEntity newTarget = this.getTarget();
                                if (newTarget == null || !newTarget.isAlive() || newTarget.isRemoved()) {
                                    this.summon2Count = 0;
                                    this.summon2Tick = 0;
                                    if (this.summon2Cool <= 0) {
                                        this.summon2Cool = MathHelper.secondsToTicks(16);
                                    }
                                } else {
                                    this.summon2Tick = 25;
                                    com.Polarice3.Goety.utils.MobUtil.instaLook(this, this.getTarget());
                                    this.playSound(
                                            com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_CHARGE.get());
                                }
                            } else {
                                if (this.summon2Cool <= 0) {
                                    this.summon2Cool = MathHelper.secondsToTicks(16);
                                }
                            }
                        }
                    }
                }

                if (this.isSpitting()) {
                    this.makeBigGlow();
                    --this.spitTick;
                    if (this.spitTick == 18) {
                        this.performSpit();
                    }

                    if (this.spitTick <= 0) {
                        this.setSpitting(false);
                        if (this.spitCool <= 0) {
                            this.spitCool = MathHelper.secondsToTicks(8);
                        }
                    }
                }

                if (this.starfeTick > 0) {
                    --this.starfeTick;
                    if (this.starfeTick <= 0) {
                        if (this.starfeCool <= 0) {
                            this.starfeCool = MathHelper.secondsToTicks(4);
                        }
                    } else {
                        if (this.starfeTick <= 21 && this.starfeTick >= 10) {
                            this.starfeFireCounter++;
                            if (this.starfeFireInterval > 0 && this.starfeFireCounter % this.starfeFireInterval == 0) {
                                this.spawnStarfeProjectile();
                            }
                        }
                    }
                }

                if (this.smashOldCool > 0) {
                    --this.smashOldCool;
                }

                if (this.smashCool > 0) {
                    --this.smashCool;
                }

                if (this.summonCool > 0) {
                    --this.summonCool;
                }

                if (this.summon2Cool > 0) {
                    --this.summon2Cool;
                }

                if (this.spitCool > 0) {
                    --this.spitCool;
                }

                if (!this.isSmashOldAttacking() && !this.isSmashAttacking() && !this.isSummoning()
                        && !this.isSummoning2() && !this.isSpitting() && !this.isStarfeAttacking()) {
                    this.level().broadcastEntityEvent(this, (byte) 7);
                    if (this.isStaying()) {
                        if (this.isSittingDown > 0) {
                            --this.isSittingDown;
                            this.setAnimationState(TO_SIT);
                        } else {
                            this.setAnimationState(SIT);
                        }
                    } else if (this.isActuallyMoving()) {
                        this.setAnimationState(WALK);
                    } else {
                        if (this.isStandingUp > 0) {
                            --this.isStandingUp;
                            this.setAnimationState(TO_STAND);
                        } else {
                            this.setAnimationState(IDLE);
                        }
                    }
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                    if (this.isStaying()) {
                        this.isSittingDown = MathHelper.secondsToTicks(1);
                    } else {
                        this.isSittingDown = 0;
                    }
                    this.isStandingUp = 0;
                }
            }
        } else {
            if (this.isAlive()) {
                if (this.bigGlow > 0.0F) {
                    this.bigGlow -= 0.1F;
                } else {
                    this.glow();
                }
            }
        }

        if (this.smashOldCool > 0) {
            --this.smashOldCool;
        }

        if (this.smashCool > 0) {
            --this.smashCool;
        }

        if (this.summonCool > 0) {
            --this.summonCool;
        }

        if (this.summon2Cool > 0) {
            --this.summon2Cool;
        }

        if (this.spitCool > 0) {
            --this.spitCool;
        }

        if (this.dischargeCool > 0) {
            --this.dischargeCool;
        }

        if (this.starfeCool > 0) {
            --this.starfeCool;
        }
    }

    public int getDeathTime() {
        return this.deathTime;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= MathHelper.secondsToTicks(5)) {
            this.spawnAnim();
            if (this.getTrueOwner() != null) {
                ItemStack itemStack = new ItemStack(
                        com.k1sak1.goetyawaken.common.blocks.ModBlocks.MOOSHROOM_MONSTROSITY_HEAD.get());
                MushroomMonstrosityHeadItem.setOwner(this.getTrueOwner(), itemStack);
                if (this.getCustomName() != null) {
                    MushroomMonstrosityHeadItem.setCustomName(this.getCustomName().getString(), itemStack);
                }
                com.Polarice3.Goety.common.entities.projectiles.FlyingItem flyingItem = new com.Polarice3.Goety.common.entities.projectiles.FlyingItem(
                        com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(), this.level(), this.getX(),
                        this.getY(), this.getZ());
                flyingItem.setOwner(this.getTrueOwner());
                flyingItem.setItem(itemStack);
                flyingItem.setParticle(net.minecraft.core.particles.ParticleTypes.CRIMSON_SPORE);
                flyingItem.setSecondsCool(60);
                this.level().addFreshEntity(flyingItem);
            }
            this.remove(RemovalReason.KILLED);
        }
        this.hurtTime = 1;
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    public void die(DamageSource p_21014_) {
        this.deathRotation = this.getYRot();
        super.die(p_21014_);
    }

    public void makeBigGlow() {
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        super.handleEntityEvent(p_21375_);
        if (p_21375_ == 5) {
            this.bigGlow = 1.0F;
        } else if (p_21375_ == 6) {
            this.clientStop = true;
        } else if (p_21375_ == 7) {
            this.clientStop = false;
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public void playSound(SoundEvent p_216991_) {
        if (!this.isSilent()) {
            this.playSound(p_216991_, this.getSoundVolume(), this.getVoicePitch());
        }
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
            if (this.getTrueOwner() != null && player.getUUID().equals(this.getTrueOwner().getUUID())
                    && this.getHealth() >= 1) {
                this.setHpoint(1.0F);
                super.tryKill(player);
            } else {
                super.tryKill(player);
            }
        }
    }

    public void setStaying(boolean staying) {
        super.setStaying(staying);
        if (staying) {
            this.isSittingDown = MathHelper.secondsToTicks(1);
        } else if (this.isGuardingArea()) {
            this.isStandingUp = MathHelper.secondsToTicks(1);
        }
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    public double canHurtRange(DamageSource pSource) {
        if (pSource.getEntity() != null) {
            return this.distanceTo(pSource.getEntity());
        } else if (pSource.getSourcePosition() != null) {
            return pSource.getSourcePosition().distanceTo(this.position());
        }

        return -1.0D;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(net.minecraft.world.damagesource.DamageTypes.GENERIC_KILL)
                && pAmount > this.getMaxHealth() * 5F
                && pSource.getEntity() == null) {
            this.remove(RemovalReason.KILLED);
            return false;
        }
        float adjustedAmount = pAmount;
        Entity directEntity = pSource.getDirectEntity();
        Entity sourceEntity = pSource.getEntity();
        if (isPotionDamageSource(pSource, directEntity, sourceEntity)) {
            adjustedAmount *= 0.15F;
        }

        float maxAllowedDamage = this.damageCapHandler.getMaxAllowedDamage();
        float firstCappedDamage = Math.min(adjustedAmount, maxAllowedDamage);
        if (this.canHurtRange(pSource) > 32.0D
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        // if (pSource.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)
        // && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
        // firstCappedDamage *= 0.15;
        // }
        if (pSource.is(net.minecraft.tags.DamageTypeTags.IS_FALL)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.world.damagesource.DamageTypes.THORNS)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if ((pSource.is(net.minecraft.world.damagesource.DamageTypes.CACTUS)
                || pSource.is(net.minecraft.world.damagesource.DamageTypes.SWEET_BERRY_BUSH))
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.world.damagesource.DamageTypes.FLY_INTO_WALL)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.world.damagesource.DamageTypes.CRAMMING)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.world.damagesource.DamageTypes.DROWN)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.world.damagesource.DamageTypes.DRY_OUT)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.tags.DamageTypeTags.DAMAGES_HELMET)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.tags.DamageTypeTags.IS_LIGHTNING)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.is(net.minecraft.world.damagesource.DamageTypes.STARVE)
                && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        if (pSource.getEntity() == null) {
            firstCappedDamage *= 0.1;
        }

        float finalCappedDamage = Math.min(firstCappedDamage, maxAllowedDamage);
        // if (this.getMaxHealth() > 0 && this.getHealth() / this.getMaxHealth() <=
        // 0.5F) {
        // float healthRatio = (float) (this.getHealth() / this.getMaxHealth());
        // finalCappedDamage *= Math.max(2.0F * healthRatio, 0.5F);
        // }
        return super.hurt(pSource, finalCappedDamage);
    }

    private boolean isPotionDamageSource(DamageSource pSource, Entity directEntity, Entity sourceEntity) {
        if (directEntity == null) {
            return false;
        }
        String className = directEntity.getClass().getName();
        if (directEntity instanceof net.minecraft.world.entity.projectile.ThrownPotion) {
            return true;
        }
        if (directEntity instanceof net.minecraft.world.entity.AreaEffectCloud) {
            return true;
        }
        if (className.toLowerCase().contains("brew") || className.toLowerCase().contains("potion")) {
            return true;
        }
        Class<?> clazz = directEntity.getClass();
        while (clazz != null) {
            String superClassName = clazz.getName();
            if (superClassName.equals("net.minecraft.world.entity.projectile.ThrownPotion") ||
                    superClassName.toLowerCase().contains("brew") ||
                    superClassName.toLowerCase().contains("potion")) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    @Override
    protected void actuallyHurt(DamageSource pDamageSource, float pDamage) {
        float maxAllowedDamage = this.damageCapHandler.getMaxAllowedDamage();
        float cappedDamage = Math.min(pDamage, maxAllowedDamage);
        double damageReduction = getMushroomDamageReduction();
        float reducedDamage = (float) (cappedDamage * (1.0 - damageReduction));
        float modifiedDamage = this.dynamicShieldHandler.calculateDamageWithDynamicShield(pDamageSource, reducedDamage);

        if (this.getMaxHealth() > 0 && this.getHealth() / this.getMaxHealth() <= 0.5F) {
            float healthRatio = (float) (this.getHealth() / this.getMaxHealth());
            modifiedDamage *= Math.max(2.0F * healthRatio, 0.5F);
        }

        super.actuallyHurt(pDamageSource, modifiedDamage);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if ((itemstack.is(Tags.Items.STORAGE_BLOCKS_REDSTONE)
                        || itemstack.is(ModBlocks.REINFORCED_REDSTONE_BLOCK.get().asItem()))
                        && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (itemstack.is(ModBlocks.REINFORCED_REDSTONE_BLOCK.get().asItem())) {
                        this.heal(this.getMaxHealth() / 4.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.25F);
                    } else {
                        this.heal((this.getMaxHealth() / 4.0F) / 8.0F);
                        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 0.25F, 0.75F);
                    }
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = serverLevel.random.nextGaussian() * 0.02D;
                            double d1 = serverLevel.random.nextGaussian() * 0.02D;
                            double d2 = serverLevel.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D),
                                    this.getRandomY(), this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    if (this.getFirstPassenger() != null && this.getFirstPassenger() != pPlayer) {
                        this.getFirstPassenger().stopRiding();
                        return InteractionResult.SUCCESS;
                    } else if (!(pPlayer.getItemInHand(pHand).getItem() instanceof IWand)) {
                        this.doPlayerRide(pPlayer);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void playHurtSound(DamageSource p_21357_) {
        super.playHurtSound(p_21357_);
    }

    public static class RMBodyRotateControl extends BodyRotationControl {
        private final Mob mob;
        private static final float MAX_ROTATE = 75;
        private int headStableTime;
        private float lastStableYHeadRot;

        public RMBodyRotateControl(Mob mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void clientTick() {
            if (this.isMoving()) {
                this.mob.yBodyRot = this.mob.getYRot();
                this.rotateHeadIfNecessary();
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.headStableTime = 0;
            } else {
                if (this.notCarryingMobPassengers()) {
                    if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
                        this.headStableTime = 0;
                        this.lastStableYHeadRot = this.mob.yHeadRot;
                        this.rotateBodyIfNecessary();
                    } else {
                        float limit = MAX_ROTATE;
                        ++this.headStableTime;
                        int speed = 10;
                        if (this.headStableTime > speed) {
                            limit = Math.max(1 - (this.headStableTime - speed) / speed, 0) * MAX_ROTATE;
                        }

                        this.mob.yBodyRot = approach(this.mob.yHeadRot, this.mob.yBodyRot, limit);
                    }
                }
            }
        }

        private void rotateBodyIfNecessary() {
            this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot,
                    (float) this.mob.getMaxHeadYRot());
        }

        private void rotateHeadIfNecessary() {
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot,
                    (float) this.mob.getMaxHeadYRot());
        }

        private boolean notCarryingMobPassengers() {
            return this.mob.getPassengers().isEmpty() || !(this.mob.getPassengers().get(0) instanceof Mob);
        }

        private boolean isMoving() {
            double d0 = this.mob.getX() - this.mob.xo;
            double d1 = this.mob.getZ() - this.mob.zo;
            return d0 * d0 + d1 * d1 > (double) 2.5000003E-7F;
        }

        public static float approach(float target, float current, float limit) {
            float delta = Mth.wrapDegrees(current - target);
            if (delta < -limit) {
                delta = -limit;
            } else if (delta >= limit) {
                delta = limit;
            }
            return target + delta * 0.55F;
        }
    }

    public static class SlowRotMoveControl extends MobUtil.noSpinControl {
        public SlowRotMoveControl(Mob mob) {
            super(mob);
        }

        private float trueSpeed;
        private float additionalRot;

        public void tick() {
            if (this.operation == MoveControl.Operation.STRAFE) {
                float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float f1 = (float) this.speedModifier * f;
                float f2 = this.strafeForwards;
                float f3 = this.strafeRight;
                float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
                this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, f1);
                if (f4 < 1.0F) {
                    f4 = 1.0F;
                }
                f4 = this.trueSpeed / f4;
                f2 *= f4;
                f3 *= f4;
                float f5 = Mth.sin(this.mob.getYRot() * ((float) Math.PI / 180F));
                float f6 = Mth.cos(this.mob.getYRot() * ((float) Math.PI / 180F));
                float f7 = f2 * f6 - f3 * f5;
                float f8 = f3 * f6 + f2 * f5;
                if (!this.isWalkable(f7, f8)) {
                    this.strafeForwards = 1.0F;
                    this.strafeRight = 0.0F;
                }
                this.mob.setSpeed(trueSpeed);
                this.mob.setZza(this.strafeForwards);
                this.mob.setXxa(this.strafeRight);
                this.operation = MoveControl.Operation.WAIT;
            } else if (this.operation == MoveControl.Operation.MOVE_TO) {
                this.operation = MoveControl.Operation.WAIT;
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                double d2 = this.wantedY - this.mob.getY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;
                if (d3 < (double) 2.5000003E-7F) {
                    this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, 0.0F);
                    this.mob.setZza(trueSpeed);
                    if (this.additionalRot > 0) {
                        this.additionalRot -= 0.1F;
                    }
                    return;
                } else {
                    this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed,
                            (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
                float f9 = (float) (Mth.atan2(d1, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                if (this.additionalRot < 1) {
                    this.additionalRot += 0.1F;
                }
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 5 + additionalRot * 5));
                this.mob.setSpeed(trueSpeed);
                this.mob.setZza(this.trueSpeed);
                BlockPos blockpos = this.mob.blockPosition();
                BlockState blockstate = this.mob.level().getBlockState(blockpos);
                VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos);
                if (d2 > (double) this.mob.getStepHeight()
                        && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.mob.getBbWidth())
                        || !voxelshape.isEmpty()
                                && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double) blockpos.getY()
                                && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
                    this.mob.getJumpControl().jump();
                    this.operation = MoveControl.Operation.JUMPING;
                }
            } else if (this.operation == MoveControl.Operation.JUMPING) {
                this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed,
                        (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                this.mob.setSpeed(this.trueSpeed);
                this.mob.setZza(trueSpeed);
                if (this.mob.onGround()) {
                    this.operation = MoveControl.Operation.WAIT;
                }
            } else {
                this.trueSpeed = Mth.lerp(0.1F, this.trueSpeed, 0.0F);
                this.mob.setZza(trueSpeed);
                if (this.additionalRot > 0) {
                    this.additionalRot -= 0.1F;
                }
            }
        }

        private boolean isWalkable(float p_24997_, float p_24998_) {
            PathNavigation pathnavigation = this.mob.getNavigation();
            if (pathnavigation != null) {
                NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
                return nodeevaluator == null || nodeevaluator.getBlockPathType(this.mob.level(),
                        Mth.floor(this.mob.getX() + (double) p_24997_),
                        this.mob.getBlockY(),
                        Mth.floor(this.mob.getZ() + (double) p_24998_)) == BlockPathTypes.WALKABLE;
            }
            return true;
        }
    }

    public boolean isSmashOldAttacking() {
        return this.smashOldTick > 0;
    }

    public boolean isSmashAttacking() {
        return this.smashTick > 0;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level().isClientSide && !this.isMeleeAttacking()) {
            this.setMeleeAttacking(true);
        }
        return true;
    }

    public void setSmashOldAttacking(boolean attacking) {
        this.smashOldTick = attacking ? (int) MathHelper.secondsToTicks(3.04F) : 0;
    }

    public void setSmashAttacking(boolean attacking) {
        this.smashTick = attacking ? (int) MathHelper.secondsToTicks(3.3F) : 0;
    }

    private void performSmashOldAttack() {
        this.makeBigGlow();
        this.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_SMASH.get(),
                this.getSoundVolume() * 2.0F, 0.2F);
        this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, this.getSoundVolume() / 2.0F, 0.9F);
        Vec3 vec3 = this.getHorizontalLookAngle();
        Vec3 attackPos = this.position().add(vec3.scale(5.0D));
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                new AABB(attackPos, attackPos).inflate(9.0D));
        for (LivingEntity target : targets) {
            if (this.distanceToSqr(target.position()) <= 9.0D * 9.0D &&
                    !com.Polarice3.Goety.utils.MobUtil.areAllies(this, target) && target != this) {
                if (!this.isEasyMode()) {
                    this.hurtTarget(target, 2.0F);
                    if (com.Polarice3.Goety.utils.MobUtil.healthIsHalved(this)) {
                        if (target instanceof LivingEntity) {
                            LivingEntity livingTarget = (LivingEntity) target;
                            com.k1sak1.goetyawaken.utils.MobEffectUtils.forceAdd(livingTarget, new MobEffectInstance(
                                    com.Polarice3.Goety.common.effects.GoetyEffects.STUNNED.get(), 60, 0), this);
                            com.k1sak1.goetyawaken.utils.MobEffectUtils.forceAdd(livingTarget, new MobEffectInstance(
                                    com.Polarice3.Goety.common.effects.GoetyEffects.CURSED.get(), 100, 0), this);
                        }
                    }
                } else {
                    this.hurtTarget(target, 1.0F);
                }

            }
        }
        com.Polarice3.Goety.common.entities.util.CameraShake.cameraShake(
                this.level(), this.position(), 25.0F, 0.3F, 0, 20);
        if (this.level() instanceof ServerLevel serverLevel) {
            com.Polarice3.Goety.utils.ColorUtil colorUtil = new com.Polarice3.Goety.utils.ColorUtil(0xff8200);
            Vec3 vec31 = this.position().add(vec3.scale(5.0D));
            com.Polarice3.Goety.utils.ServerParticleUtil.windShockwaveParticle(
                    serverLevel, colorUtil, 2, 0, 20, -1, vec31.add(0.0D, 1.0D, 0.0D));
            com.Polarice3.Goety.utils.ServerParticleUtil.windShockwaveParticle(
                    serverLevel, colorUtil, 4, 0, 20, -1, vec31.add(0.0D, 1.0D, 0.0D));

            // for (int i = 0; i <= 5; ++i) {
            // surroundTremor(this, i, 3, 0.0F, false, 0.1F, attackPos);
            // }

            serverLevel.sendParticles(
                    new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(
                            colorUtil.red(), colorUtil.green(), colorUtil.blue(), 9.0F, 1),
                    vec31.x, com.Polarice3.Goety.utils.BlockFinder.moveDownToGround(this), vec31.z,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void performSmashAttack() {
        this.makeBigGlow();
        this.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_SMASH.get(),
                this.getSoundVolume() * 2.0F, 0.2F);
        this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, this.getSoundVolume() / 2.0F, 0.9F);
        Vec3 vec3 = this.getHorizontalLookAngle();
        Vec3 attackPos = this.position().add(vec3.scale(5.0D));
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                new AABB(attackPos, attackPos).inflate(16.0D));
        for (LivingEntity target : targets) {
            if (this.distanceToSqr(target.position()) <= 16.0D * 16.0D &&
                    !com.Polarice3.Goety.utils.MobUtil.areAllies(this, target) && target != this) {
                if (!this.isEasyMode()) {
                    this.hurtTarget(target, 3.0F);
                    if (com.Polarice3.Goety.utils.MobUtil.healthIsHalved(this)) {
                        if (target instanceof LivingEntity) {
                            LivingEntity livingTarget = (LivingEntity) target;
                            com.k1sak1.goetyawaken.utils.MobEffectUtils.forceAdd(livingTarget, new MobEffectInstance(
                                    com.Polarice3.Goety.common.effects.GoetyEffects.STUNNED.get(), 60, 0), this);
                            com.k1sak1.goetyawaken.utils.MobEffectUtils.forceAdd(livingTarget, new MobEffectInstance(
                                    com.Polarice3.Goety.common.effects.GoetyEffects.CURSED.get(), 100, 0), this);
                        }
                    }
                } else {
                    this.hurtTarget(target, 2.0F);
                }
            }
        }
        com.Polarice3.Goety.common.entities.util.CameraShake.cameraShake(
                this.level(), this.position(), 25.0F, 0.3F, 0, 20);
        if (this.level() instanceof ServerLevel serverLevel) {
            com.Polarice3.Goety.utils.ColorUtil colorUtil = new com.Polarice3.Goety.utils.ColorUtil(0xff8200);
            Vec3 vec31 = this.position().add(vec3.scale(5.0D));
            com.Polarice3.Goety.utils.ServerParticleUtil.windShockwaveParticle(
                    serverLevel, colorUtil, 4, 0, 20, -1, vec31.add(0.0D, 1.0D, 0.0D));
            com.Polarice3.Goety.utils.ServerParticleUtil.windShockwaveParticle(
                    serverLevel, colorUtil, 8, 0, 20, -1, vec31.add(0.0D, 1.0D, 0.0D));
            // for (int i = 0; i <= 7; ++i) {
            // surroundTremor(this, i, 3, 0.0F, false, 0.1F, attackPos);
            // }

            serverLevel.sendParticles(
                    new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(
                            colorUtil.red(), colorUtil.green(), colorUtil.blue(), 16.0F, 1),
                    vec31.x, com.Polarice3.Goety.utils.BlockFinder.moveDownToGround(this), vec31.z,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public void hurtTarget(Entity target, float damageMultiplier) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier;
        float f1 = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity livingEntity) {
            f += (livingEntity.getMaxHealth() * 0.16F);
        }
        boolean flag = target.hurt(this.getServantAttack(), f);
        if (flag) {
            if (f1 > 0.0F && target instanceof LivingEntity livingEntity) {
                if (livingEntity.getBoundingBox().getSize() > this.getBoundingBox().getSize()) {
                    livingEntity.knockback((double) (f1 * 0.5F),
                            (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)),
                            (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
                } else {
                    com.Polarice3.Goety.utils.MobUtil.forcefulKnockBack(livingEntity,
                            (double) (f1 * 0.5F),
                            (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)),
                            (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))), 0.5D);
                }
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            this.doEnchantDamageEffects(this, target);
            this.setLastHurtMob(target);
        }

        if (target instanceof Player player && player.isBlocking()) {
            player.disableShield(true);
        } else {
            com.Polarice3.Goety.utils.MobUtil.disableShield(target);
        }
    }

    static class SmashOldGoal extends Goal {
        public MushroomMonstrosity mob;

        public SmashOldGoal(MushroomMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null
                    && this.mob.smashOldCool <= 0
                    && !this.mob.isSmashAttacking()
                    && !this.mob.isSummoning()
                    && !this.mob.isSummoning2()
                    && !this.mob.isSpitting()
                    && !this.mob.isStarfeAttacking()
                    && this.mob.getTarget().isAlive()
                    && this.mob.distanceTo(this.mob.getTarget()) <= 8.0F;
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.smashOldTick > 0;
        }

        @Override
        public void start() {
            this.mob.setMeleeAttacking(true);
            this.mob.setSmashOldAttacking(true);
            if (this.mob.getTarget() != null) {
                com.Polarice3.Goety.utils.MobUtil.instaLook(this.mob, this.mob.getTarget());
            }
            this.mob.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_GROWL.get());
            this.mob.setAnimationState("smash_old");
            this.mob.smashOldTick = 61;
        }

        @Override
        public void stop() {
            this.mob.setMeleeAttacking(false);
            this.mob.setAnimationState(MushroomMonstrosity.IDLE);
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 7);
            this.mob.setSmashOldAttacking(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class SmashGoal extends Goal {
        public MushroomMonstrosity mob;

        public SmashGoal(MushroomMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null
                    && this.mob.smashCool <= 0
                    && !this.mob.isSmashOldAttacking()
                    && !this.mob.isSummoning()
                    && !this.mob.isSummoning2()
                    && !this.mob.isSpitting()
                    && !this.mob.isStarfeAttacking()
                    && this.mob.getTarget().isAlive()
                    && this.mob.distanceTo(this.mob.getTarget()) <= 12.0F;
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.smashTick > 0;
        }

        @Override
        public void start() {
            this.mob.setMeleeAttacking(true);
            this.mob.setSmashAttacking(true);
            if (this.mob.getTarget() != null) {
                com.Polarice3.Goety.utils.MobUtil.instaLook(this.mob, this.mob.getTarget());
            }
            this.mob.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_GROWL.get());
            this.mob.setAnimationState("smash");
            this.mob.smashTick = 66;
        }

        @Override
        public void stop() {
            this.mob.setMeleeAttacking(false);
            this.mob.setAnimationState(MushroomMonstrosity.IDLE);
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 7);
            this.mob.setSmashAttacking(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class SummonGoal extends Goal {
        public MushroomMonstrosity mob;

        public SummonGoal(MushroomMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            int angryMooshroomCount = 0;
            for (com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom mooshroom : this.mob.level()
                    .getEntitiesOfClass(
                            com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom.class,
                            this.mob.getBoundingBox().inflate(16.0D))) {
                if (mooshroom.getOwnerUUID() != null && mooshroom.getOwnerUUID().equals(this.mob.getUUID())) {
                    angryMooshroomCount++;
                }
            }

            if (livingentity != null && livingentity.isAlive()) {
                return angryMooshroomCount < 3
                        && this.mob.summonCool <= 0
                        && !this.mob.isSmashOldAttacking()
                        && !this.mob.isSmashAttacking()
                        && !this.mob.isSummoning2()
                        && !this.mob.isSpitting()
                        && !this.mob.isStarfeAttacking()
                        && this.mob.onGround()
                        && this.mob.distanceTo(livingentity) < 16;
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.summonTick > 0;
        }

        @Override
        public void start() {
            super.start();
            if (this.mob.getTarget() != null) {
                com.Polarice3.Goety.utils.MobUtil.instaLook(this.mob, this.mob.getTarget());
            }
            this.mob.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_CHARGE.get());
            this.mob.setAnimationState("summon");
            this.mob.summonTick = 94;
            this.mob.summonCount = 1;
        }

        @Override
        public void stop() {
            this.mob.setAnimationState(MushroomMonstrosity.IDLE);
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 7);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public void performSummon() {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_CHARGE.get());
            int spawnedCount = 0;
            int attempts = 0;
            int totalsummoncount = 12;
            if (this.isEasyMode()) {
                totalsummoncount = 6;
            }
            while (spawnedCount < totalsummoncount && attempts < 30) {
                attempts++;
                double angle = this.level().getRandom().nextDouble() * Math.PI * 2;
                double distance = 12.0D + this.level().getRandom().nextDouble() * 12.0D;
                double x = this.getX() + Math.cos(angle) * distance;
                double z = this.getZ() + Math.sin(angle) * distance;
                double y = this.getY();
                com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom angryMooshroom = new com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom(
                        com.k1sak1.goetyawaken.common.entities.ModEntityType.ANGRY_MOOSHROOM.get(),
                        serverLevel);
                BlockPos pos = BlockPos.containing(x, y, z);
                BlockPos groundPos = pos;
                while (groundPos.getY() > serverLevel.getMinBuildHeight()
                        && serverLevel.getBlockState(groundPos).isAir()) {
                    groundPos = groundPos.below();
                }
                pos = groundPos.above();
                if (!serverLevel.noCollision(
                        angryMooshroom.getBoundingBox().move(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D))) {
                    continue;
                }
                angryMooshroom.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                angryMooshroom.setOwnerId(this.getUUID());
                angryMooshroom.setTrueOwner(this);
                boolean shouldUpgrade = this.getHealth() <= this.getMaxHealth() / 2.0F;

                angryMooshroom.setPersistenceRequired();
                int randomLifeTicks = 1200 + this.level().getRandom().nextInt(1201);
                angryMooshroom.setLimitedLife(randomLifeTicks);
                if (this.level().getRandom().nextBoolean()) {
                    angryMooshroom.setScreamingMooshroom(true);
                }
                serverLevel.addFreshEntity(angryMooshroom);
                angryMooshroom.finalizeSpawn(serverLevel,
                        serverLevel.getCurrentDifficultyAt(angryMooshroom.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                angryMooshroom.setUpgraded(shouldUpgrade);
                angryMooshroom.setHealth(angryMooshroom.getMaxHealth());
                spawnedCount++;
                for (int k = 0; k < 3; ++k) {
                    double d0 = serverLevel.random.nextGaussian() * 0.02D;
                    double d1 = serverLevel.random.nextGaussian() * 0.02D;
                    double d2 = serverLevel.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(com.Polarice3.Goety.client.particles.ModParticleTypes.ELECTRIC.get(),
                            x, y + 1.0D, z, 0, d0, d1, d2, 0.5F);
                }
                if (!serverLevel.getBlockState(BlockPos.containing(x, y, z).below()).isAir()) {
                    for (int j = 0; j < 4; ++j) {
                        double d1 = x + (this.level().getRandom().nextDouble() - 0.5D) * 2.0D;
                        double d2 = y + 0.5F;
                        double d3 = z + (this.level().getRandom().nextDouble() - 0.5D) * 2.0D;
                        serverLevel.sendParticles(
                                com.Polarice3.Goety.client.particles.ModParticleTypes.BIG_FIRE_GROUND.get(), d1, d2,
                                d3, 0,
                                0.0D, 0.0D, 0.0D, 0.5F);
                    }
                }
                if (spawnedCount == 6 || spawnedCount == 12) {
                    com.Polarice3.Goety.utils.ColorUtil colorUtil = new com.Polarice3.Goety.utils.ColorUtil(
                            0xff8200);
                    serverLevel.sendParticles(
                            new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(colorUtil.red(),
                                    colorUtil.green(),
                                    colorUtil.blue(), 3, 1),
                            x, com.Polarice3.Goety.utils.BlockFinder.moveDownToGround(this), z, 1, 0.0D, 0.0D,
                            0.0D, 0.0D);
                }
            }
        }
    }

    static class Summon2Goal extends Goal {
        public MushroomMonstrosity mob;

        public Summon2Goal(MushroomMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                double distance = this.mob.distanceTo(livingentity);
                return (this.mob.summon2Cool <= 0 || this.mob.summon2Count > 0)
                        && !this.mob.isSmashOldAttacking()
                        && !this.mob.isSmashAttacking()
                        && !this.mob.isSummoning()
                        && !this.mob.isSpitting()
                        && !this.mob.isStarfeAttacking()
                        && distance >= 16.0F;
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.mob.getTarget();
            return this.mob.summon2Tick > 0;
        }

        @Override
        public void start() {
            super.start();
            if (this.mob.getTarget() != null) {
                com.Polarice3.Goety.utils.MobUtil.instaLook(this.mob, this.mob.getTarget());
            }
            this.mob.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_CHARGE.get());
            this.mob.setAnimationState("summon2");
            if (this.mob.summon2Count <= 0) {
                this.mob.summon2TotalCount = 2 + this.mob.level().random.nextInt(5);
            }
            this.mob.summon2Count = this.mob.summon2TotalCount;
            this.mob.summon2Tick = 25;
        }

        @Override
        public void stop() {
            this.mob.setAnimationState(MushroomMonstrosity.IDLE);
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 7);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public void performSummon2() {
        LivingEntity target = this.getTarget();
        if (target != null && this.level() instanceof ServerLevel serverLevel) {
            this.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_CHARGE.get());
            for (int i = 0; i < 7; i++) {
                float angleOffset = (i - 3) * 10.0F;
                Vec3 startPos = new Vec3(this.getX(), this.getY(), this.getZ());
                Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0D,
                        target.getZ());
                Vec3 direction = targetPos.subtract(startPos).normalize();
                direction = direction.yRot((float) Math.toRadians(angleOffset));
                com.k1sak1.goetyawaken.common.entities.projectiles.MushroomMissile missile = new com.k1sak1.goetyawaken.common.entities.projectiles.MushroomMissile(
                        this, direction.x * 1.0D, direction.y * 1.0D, direction.z * 1.0D, serverLevel);
                missile.setOwner(this);
                missile.setPos(this.getX(), this.getY() + 0.5D, this.getZ());
                missile.setYRot((float) (Mth.atan2(direction.x, direction.z) * (double) (180F / (float) Math.PI)));
                missile.setXRot((float) (Mth.atan2(direction.y,
                        Mth.sqrt((float) (direction.x * direction.x + direction.z * direction.z)))
                        * (double) (180F / (float) Math.PI)));
                missile.yRotO = missile.getYRot();
                missile.xRotO = missile.getXRot();
                missile.setDeltaMovement(direction.scale(1.0D));
                if (!this.isEasyMode()) {
                    missile.setExtraDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F);
                }
                serverLevel.addFreshEntity(missile);
            }
            com.Polarice3.Goety.common.entities.util.CameraShake.cameraShake(this.level(), this.position(), 20.0F,
                    0.03F,
                    0, 20);
        }
    }

    public boolean isSummoning2() {
        return this.summon2Tick > 0;
    }

    public void setSpitting(boolean spitting) {
        this.spitTick = spitting ? (int) MathHelper.secondsToTicks(1.83F) : 0;
    }

    static class SpitGoal extends Goal {
        public MushroomMonstrosity mob;

        public SpitGoal(MushroomMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                double distance = this.mob.distanceTo(livingentity);
                boolean isWithinRange = distance <= 20.0F;
                return this.mob.spitCool <= 0
                        && isWithinRange
                        && !this.mob.isSmashOldAttacking()
                        && !this.mob.isSmashAttacking()
                        && !this.mob.isSummoning()
                        && !this.mob.isEasyMode()
                        && !this.mob.isStarfeAttacking()
                        && !this.mob.isSummoning2();
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.spitTick > 0;
        }

        @Override
        public void start() {
            super.start();
            if (this.mob.getTarget() != null) {
                com.Polarice3.Goety.utils.MobUtil.instaLook(this.mob, this.mob.getTarget());
            }
            this.mob.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_ARM.get(),
                    this.mob.getSoundVolume() * 0.7F, this.mob.getVoicePitch());
            this.mob.setAnimationState("spit");
            this.mob.spitTick = 37;
        }

        @Override
        public void stop() {
            this.mob.setAnimationState(MushroomMonstrosity.IDLE);
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 7);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public void performSpit() {
        LivingEntity target = this.getTarget();
        if (target != null && this.level() instanceof ServerLevel serverLevel) {
            this.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_BELCH.get(),
                    this.getSoundVolume(),
                    0.7F);
            this.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_GROWL.get(),
                    this.getSoundVolume() - 1.5F, 0.9F);
            int projectileType = this.level().getRandom().nextInt(5);
            switch (projectileType) {
                case 0:
                    for (int i = 0; i < 7; i++) {
                        VoidShockBomb bomb = new VoidShockBomb(this, serverLevel);
                        bomb.setPos(this.getX(), this.getY() + 4.5D, this.getZ());
                        bomb.setExtraDamage(17.0F);
                        Vec3 targetVec = target.position().subtract(this.position()).normalize();
                        double d1 = target.getX() - this.getX();
                        double d2 = target.getY() + target.getEyeHeight() - bomb.getY();
                        double d3 = target.getZ() - this.getZ();
                        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
                        float velocity = 1.0F;
                        if (target.distanceTo(this) <= 13.0F) {
                            velocity = Math.max(0.25F, target.distanceTo(this) / 13.0F);
                        }
                        this.shoot(bomb, d1, d2 + d4, d3, velocity, 30);
                        serverLevel.addFreshEntity(bomb);
                    }
                    break;

                case 1:
                    for (int i = 0; i < 12; i++) {
                        com.k1sak1.goetyawaken.common.entities.projectiles.MushroomScatterBomb scatterBomb = new com.k1sak1.goetyawaken.common.entities.projectiles.MushroomScatterBomb(
                                this, serverLevel);
                        scatterBomb.setPos(this.getX(), this.getY() + 4.5D, this.getZ());
                        Vec3 targetVec = target.position().subtract(this.position()).normalize();
                        double d1 = target.getX() - this.getX();
                        double d2 = target.getY() + target.getEyeHeight() - scatterBomb.getY();
                        double d3 = target.getZ() - this.getZ();
                        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
                        float velocity = 1.0F;
                        if (target.distanceTo(this) <= 13.0F) {
                            velocity = Math.max(0.25F, target.distanceTo(this) / 13.0F);
                        }
                        this.shoot(scatterBomb, d1, d2 + d4, d3, velocity, 30);
                        serverLevel.addFreshEntity(scatterBomb);
                    }
                    break;

                case 2:
                    for (int i = 0; i < 5; i++) {
                        BlossomBall ball = new BlossomBall(this, serverLevel);
                        ball.setPos(this.getX(), this.getY() + 4.5D, this.getZ());
                        ball.setRadius(5.0D);
                        ball.setDuration(10);
                        ball.setExtraDamage(18.0F);
                        Vec3 targetVec = target.position().subtract(this.position()).normalize();
                        double d1 = target.getX() - this.getX();
                        double d2 = target.getY() + target.getEyeHeight() - ball.getY();
                        double d3 = target.getZ() - this.getZ();
                        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
                        float velocity = 1.0F;
                        if (target.distanceTo(this) <= 13.0F) {
                            velocity = Math.max(0.25F, target.distanceTo(this) / 13.0F);
                        }
                        this.shoot(ball, d1, d2 + d4, d3, velocity, 30);
                        serverLevel.addFreshEntity(ball);
                    }
                    break;

                case 3:
                    for (int i = 0; i < 12; i++) {
                        BlastFungus fungus = new BlastFungus(this, serverLevel);
                        fungus.setPos(this.getX(), this.getY() + 4.5D, this.getZ());
                        Vec3 targetVec = target.position().subtract(this.position()).normalize();
                        double d1 = target.getX() - this.getX();
                        double d2 = target.getY() + target.getEyeHeight() - fungus.getY();
                        double d3 = target.getZ() - this.getZ();
                        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
                        float velocity = 1.0F;
                        if (target.distanceTo(this) <= 13.0F) {
                            velocity = Math.max(0.25F, target.distanceTo(this) / 13.0F);
                        }
                        this.shoot(fungus, d1, d2 + d4, d3, velocity, 30);
                        serverLevel.addFreshEntity(fungus);
                    }
                    break;

                case 4:
                    List<com.Polarice3.Goety.common.entities.ally.MagmaCubeServant> nearbyMagmaCubes = this.level()
                            .getEntitiesOfClass(com.Polarice3.Goety.common.entities.ally.MagmaCubeServant.class,
                                    this.getBoundingBox().inflate(16.0D));
                    for (com.Polarice3.Goety.common.entities.ally.MagmaCubeServant magmaCube : nearbyMagmaCubes) {
                        if (magmaCube.getOwner() != null && magmaCube.getOwner().equals(this.getOwner())) {
                            magmaCube.remove(Entity.RemovalReason.DISCARDED);
                        }
                    }

                    for (int i = 0; i < 7; i++) {
                        ModMagmaBomb bomb = new ModMagmaBomb(this, serverLevel);
                        bomb.setPos(this.getX(), this.getY() + 4.5D, this.getZ());
                        Vec3 targetVec = target.position().subtract(this.position()).normalize();
                        double d1 = target.getX() - this.getX();
                        double d2 = target.getY() + target.getEyeHeight() - bomb.getY();
                        double d3 = target.getZ() - this.getZ();
                        double d4 = Math.sqrt(d1 * d1 + d3 * d3);
                        float velocity = 1.0F;
                        if (target.distanceTo(this) <= 13.0F) {
                            velocity = Math.max(0.25F, target.distanceTo(this) / 13.0F);
                        }
                        this.shoot(bomb, d1, d2 + d4, d3, velocity, 30);
                        serverLevel.addFreshEntity(bomb);
                    }
                    break;
            }
        }
    }

    public void shoot(Projectile projectile, double xPower, double yPower, double zPower, float velocity,
            float inaccuracy) {
        Vec3 vec3 = (new Vec3(xPower, yPower, zPower)).normalize().add(
                this.level().getRandom().triangle(0.0D, 0.0172275D * (double) inaccuracy),
                this.level().getRandom().triangle(0.0D, 0.0172275D * 5),
                this.level().getRandom().triangle(0.0D, 0.0172275D * (double) inaccuracy))
                .scale((double) velocity);
        projectile.setDeltaMovement(vec3);
        double d0 = vec3.horizontalDistance();
        projectile.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
        projectile.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        projectile.yRotO = projectile.getYRot();
        projectile.xRotO = projectile.getXRot();
    }

    public boolean isSpitting() {
        return this.spitTick > 0;
    }

    public boolean isStarfeAttacking() {
        return this.starfeTick > 0;
    }

    public void setStarfeAttacking(boolean attacking) {
        this.starfeTick = attacking ? 30 : 0;
    }

    static class AttackGoal extends net.minecraft.world.entity.ai.goal.MeleeAttackGoal {
        public MushroomMonstrosity mob;
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(MushroomMonstrosity mob, double moveSpeed) {
            super(mob, moveSpeed, true);
            this.mob = mob;
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null
                    && !this.mob.isSmashOldAttacking()
                    && !this.mob.isSmashAttacking()
                    && !this.mob.isSummoning()
                    && !this.mob.isSummoning2()
                    && !this.mob.isSpitting()
                    && !this.mob.isStarfeAttacking()
                    && this.mob.getTarget().isAlive();
        }

        @Override
        public void start() {
            this.mob.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            this.mob.getNavigation().stop();
            if (this.mob.getTarget() == null) {
                this.mob.setAggressive(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity == null) {
                return;
            }

            this.mob.getLookControl().setLookAt(livingentity, this.mob.getMaxHeadYRot(), this.mob.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                this.mob.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity,
                    this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (this.mob.targetClose(enemy, distToEnemySqr)) {
                this.mob.doHurtTarget(enemy);
            }
        }
    }

    static class DischargeGoal extends Goal {
        public MushroomMonstrosity mob;

        public DischargeGoal(MushroomMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            if (target != null && target.isAlive()) {
                double distance = this.mob.distanceTo(target);
                boolean isOutOfRange = distance > 16.0F;
                boolean isNotOnCooldown = this.mob.dischargeCool <= 0;
                return isNotOnCooldown && isOutOfRange && this.mob.isEasyMode();
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            if (this.mob.getTarget() != null) {
                if (this.mob.level() instanceof ServerLevel serverLevel) {
                    com.Polarice3.Goety.common.magic.spells.wind.LaunchSpell dischargeSpell = new com.Polarice3.Goety.common.magic.spells.wind.LaunchSpell();
                    com.Polarice3.Goety.common.magic.SpellStat spellStat = dischargeSpell.defaultStats().setPotency(5);
                    dischargeSpell.SpellResult(serverLevel, this.mob, ItemStack.EMPTY, spellStat);
                    this.mob.dischargeCool = 640;
                }
            }
        }
    }

    static class StarfeGoal extends Goal {
        public MushroomMonstrosity mob;

        public StarfeGoal(MushroomMonstrosity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null
                    && this.mob.starfeCool <= 0
                    && !this.mob.isSmashOldAttacking()
                    && !this.mob.isSmashAttacking()
                    && !this.mob.isSummoning()
                    && !this.mob.isSummoning2()
                    && !this.mob.isSpitting()
                    && !this.mob.isEasyMode()
                    && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.starfeTick > 0;
        }

        @Override
        public void start() {
            super.start();
            if (this.mob.getTarget() != null) {
                com.Polarice3.Goety.utils.MobUtil.instaLook(this.mob, this.mob.getTarget());
            }
            this.mob.playSound(com.Polarice3.Goety.init.ModSounds.REDSTONE_MONSTROSITY_CHARGE.get());
            this.mob.setAnimationState("strafe");
            this.mob.setStarfeAttacking(true);
            this.mob.starfeProjectileType = this.mob.level().random.nextInt(13);
            this.mob.starfeFireCounter = 0;
            switch (this.mob.starfeProjectileType) {
                case 0:
                    this.mob.starfeFireInterval = 3;
                    break;
                case 7:
                    this.mob.starfeFireInterval = 1;
                    break;
                case 8:
                    this.mob.starfeFireInterval = 2;
                    break;
                case 10:
                    this.mob.starfeFireInterval = 2;
                    break;
                case 11:
                    this.mob.starfeFireInterval = 2;
                    break;
                case 12:
                    this.mob.starfeFireInterval = 10;
                    break;
                default:
                    this.mob.starfeFireInterval = 1;
                    break;
            }
        }

        @Override
        public void stop() {
            this.mob.setAnimationState(MushroomMonstrosity.IDLE);
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 7);
            this.mob.setStarfeAttacking(false);
        }
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr) {
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach
                || this.distanceTo(enemy) <= 8.0F
                || this.getBoundingBox().inflate(4.0D).intersects(enemy.getBoundingBox());
    }

    public boolean targetClose(LivingEntity entity) {
        return this.targetClose(entity, this.distanceToSqr(entity));
    }

    public double getXLeft() {
        return this.getX() + (this.getHorizontalLookAngle().x * 2) + (MobUtil.getHorizontalLeftLookAngle(this).x * 2);
    }

    public double getZLeft() {
        return this.getZ() + (this.getHorizontalLookAngle().z * 2) + (MobUtil.getHorizontalLeftLookAngle(this).z * 2);
    }

    public double getXRight() {
        return this.getX() + (this.getHorizontalLookAngle().x * 2) + (MobUtil.getHorizontalRightLookAngle(this).x * 2);
    }

    public double getZRight() {
        return this.getZ() + (this.getHorizontalLookAngle().z * 2) + (MobUtil.getHorizontalRightLookAngle(this).z * 2);
    }

    public static void surroundTremor(LivingEntity livingEntity, int distance, double topY, float side,
            boolean grab, float airborne) {
        surroundTremor(livingEntity, distance, topY, side, grab, airborne, livingEntity.position());
    }

    public static void surroundTremor(LivingEntity livingEntity, int distance, double topY, float side,
            boolean grab, float airborne, Vec3 centerPos) {
        int hitY = Mth.floor(livingEntity.getBoundingBox().minY - 0.5D);
        double spread = Math.PI * (double) 2.0F;
        int arcLen = Mth.ceil((double) distance * spread) + 1;
        double minY = centerPos.y - 1.0D;
        double maxY = centerPos.y + topY;

        for (int i = 0; i < arcLen; ++i) {
            double theta = ((double) i / ((double) arcLen - 1.0D) - 0.5D) * spread;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = centerPos.x + vx * (double) distance
                    + (double) side * Math.cos((double) (livingEntity.yBodyRot + 90.0F) * Math.PI / 180.0D);
            double pz = centerPos.z + vz * (double) distance
                    + (double) side * Math.sin((double) (livingEntity.yBodyRot + 90.0F) * Math.PI / 180.0D);
            float factor = 1.0F - (float) distance / 12.0F;
            int hitX = Mth.floor(px);
            int hitZ = Mth.floor(pz);
            BlockPos blockPos = new BlockPos(hitX, hitY, hitZ);

            BlockState blockState;
            int checkCount = 0;
            int maxCheckDepth = 20;
            for (blockState = livingEntity.level().getBlockState(
                    blockPos); blockState.getRenderShape() != net.minecraft.world.level.block.RenderShape.MODEL
                            && checkCount < maxCheckDepth; blockState = livingEntity.level().getBlockState(blockPos)) {
                blockPos = blockPos.below();
                checkCount++;
            }
            BlockState blockAbove = livingEntity.level().getBlockState(blockPos.above());

            if (blockState != net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
                    && !blockState.hasBlockEntity() && !blockAbove.blocksMotion()) {
                com.Polarice3.Goety.common.entities.util.ModFallingBlock fallingBlock = new com.Polarice3.Goety.common.entities.util.ModFallingBlock(
                        livingEntity.level(), Vec3.atCenterOf(blockPos.above()), blockState,
                        (float) (0.2D + livingEntity.level().random.nextGaussian() * 0.15D));
                livingEntity.level().addFreshEntity(fallingBlock);
            }

            net.minecraft.world.phys.AABB selection = new net.minecraft.world.phys.AABB(px - 0.5D, minY, pz - 0.5D,
                    px + 0.5D, maxY, pz + 0.5D);
            List<LivingEntity> entities = livingEntity.level().getEntitiesOfClass(LivingEntity.class, selection);
            for (LivingEntity target : entities) {
                if (!MobUtil.areAllies(target, livingEntity) && target != livingEntity) {
                    boolean flag = livingEntity.doHurtTarget(target);
                    if (flag) {
                        if (grab) {
                            double magnitude = -4.0D;
                            double x = vx * (double) (1.0F - factor) * magnitude;
                            double y = 0.0D;
                            if (target.onGround()) {
                                y += 0.15D;
                            }

                            double z = vz * (double) (1.0F - factor) * magnitude;
                            MobUtil.push(target, x, y, z);
                        } else {
                            MobUtil.push(target, 0.0D, (double) (airborne * (float) distance)
                                    + livingEntity.level().random.nextDouble() * 0.15D, 0.0D);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.getEffect() == GoetyEffects.DOOM.get() ||
                effectInstance.getEffect() == GoetyEffects.ACID_VENOM.get() ||
                effectInstance.getEffect() == GoetyEffects.VOID_TOUCHED.get() ||
                effectInstance.getEffect() == GoetyEffects.WOUNDED.get() ||
                effectInstance.getEffect() == GoetyEffects.STUNNED.get() ||
                effectInstance.getEffect() == GoetyEffects.TANGLED.get() ||
                effectInstance.getEffect() == GoetyEffects.WILD_RAGE.get() ||
                effectInstance.getEffect() == MobEffects.HARM) {
            return false;
        }

        if (effectInstance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            int newAmplifier = (int) Math.ceil((effectInstance.getAmplifier() / 2.0) - 1);
            if (newAmplifier < 0) {
                return false;
            } else {
                int newDuration = effectInstance.getDuration() / 4;
                MobEffectInstance modifiedEffect = new MobEffectInstance(
                        effectInstance.getEffect(),
                        newDuration,
                        newAmplifier,
                        effectInstance.isAmbient(),
                        effectInstance.isVisible(),
                        effectInstance.showIcon());
                return super.addEffect(modifiedEffect, entity);
            }
        }
        return super.addEffect(effectInstance, entity);
    }

    @Override
    public void setHealth(float health) {
        float currentHealth = this.getHealth();
        if (health < currentHealth) {
            float damage = currentHealth - health;
            float maxAllowedDamage = this.damageCapHandler.getMaxAllowedDamage();
            if (damage > maxAllowedDamage) {
                health = currentHealth - maxAllowedDamage;
            }
        }
        super.setHealth(health);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.MUSHROOM_MONSTROSITY_LIMIT.get();
    }

    protected void setHpoint(float health) {
        super.setHealth(health);
    }

    private void spawnStarfeProjectile() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        double spawnX = this.getXRight() + 2.0D;
        double spawnY = this.getY() + 2.0D;
        double spawnZ = this.getZRight() + 2.0D;
        Vec3 targetVec = target.position().subtract(spawnX, spawnY - 1, spawnZ).normalize();
        Vec3 velocity = targetVec.scale(2.0D);
        double spreadAngle = 0.2;
        double offsetX = (serverLevel.random.nextDouble() - 0.5) * spreadAngle;
        double offsetY = (serverLevel.random.nextDouble() - 0.5) * spreadAngle;
        double offsetZ = (serverLevel.random.nextDouble() - 0.5) * spreadAngle;

        Vec3 spreadVelocity = new Vec3(
                velocity.x + offsetX,
                velocity.y + offsetY,
                velocity.z + offsetZ).normalize().scale(3.0D);

        switch (this.starfeProjectileType) {
            case 0:
                com.Polarice3.Goety.common.entities.projectiles.IllBomb illBomb = new com.Polarice3.Goety.common.entities.projectiles.IllBomb(
                        spawnX, spawnY, spawnZ, serverLevel);
                illBomb.setOwner(this);
                illBomb.setDeltaMovement(velocity);
                serverLevel.addFreshEntity(illBomb);
                com.Polarice3.Goety.common.entities.projectiles.IllBomb illBombSpread = new com.Polarice3.Goety.common.entities.projectiles.IllBomb(
                        spawnX, spawnY, spawnZ, serverLevel);
                illBombSpread.setOwner(this);
                illBombSpread.setDeltaMovement(spreadVelocity);
                serverLevel.addFreshEntity(illBombSpread);
                break;
            case 1:
                com.Polarice3.Goety.common.entities.projectiles.ElectroOrb orb = new com.Polarice3.Goety.common.entities.projectiles.ElectroOrb(
                        serverLevel, this, target);
                orb.setPos(spawnX, spawnY, spawnZ);
                orb.setDeltaMovement(velocity);
                orb.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(orb);
                com.Polarice3.Goety.common.entities.projectiles.ElectroOrb orbSpread = new com.Polarice3.Goety.common.entities.projectiles.ElectroOrb(
                        serverLevel, this, target);
                orbSpread.setPos(spawnX, spawnY, spawnZ);
                orbSpread.setDeltaMovement(spreadVelocity);
                orbSpread.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(orbSpread);
                break;
            case 2:
                com.Polarice3.Goety.common.entities.projectiles.MagicBolt bolt = new com.Polarice3.Goety.common.entities.projectiles.MagicBolt(
                        serverLevel, this, targetVec.x * 1.0D, targetVec.y * 1.0D, targetVec.z * 1.0D);
                bolt.setPos(spawnX, spawnY, spawnZ);
                bolt.setDeltaMovement(velocity);
                bolt.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(bolt);
                Vec3 spreadTargetVec = spreadVelocity.normalize();
                com.Polarice3.Goety.common.entities.projectiles.MagicBolt boltSpread = new com.Polarice3.Goety.common.entities.projectiles.MagicBolt(
                        serverLevel, this, spreadTargetVec.x * 1.0D, spreadTargetVec.y * 1.0D,
                        spreadTargetVec.z * 1.0D);
                boltSpread.setPos(spawnX, spawnY, spawnZ);
                boltSpread.setDeltaMovement(spreadVelocity);
                boltSpread.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(boltSpread);
                break;
            case 3:
                com.Polarice3.Goety.common.entities.projectiles.PoisonBolt poisonBolt = new com.Polarice3.Goety.common.entities.projectiles.PoisonBolt(
                        spawnX, spawnY, spawnZ, velocity.x, velocity.y, velocity.z,
                        serverLevel);
                poisonBolt.setPos(spawnX, spawnY, spawnZ);
                poisonBolt.setOwner(this);
                poisonBolt.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(poisonBolt);
                com.Polarice3.Goety.common.entities.projectiles.PoisonBolt poisonBoltSpread = new com.Polarice3.Goety.common.entities.projectiles.PoisonBolt(
                        spawnX, spawnY, spawnZ, spreadVelocity.x, spreadVelocity.y, spreadVelocity.z,
                        serverLevel);
                poisonBoltSpread.setPos(spawnX, spawnY, spawnZ);
                poisonBoltSpread.setOwner(this);
                poisonBoltSpread.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(poisonBoltSpread);
                break;
            case 4:
                com.Polarice3.Goety.common.entities.projectiles.SnapFungus snapFungus = new com.Polarice3.Goety.common.entities.projectiles.SnapFungus(
                        spawnX, spawnY, spawnZ, serverLevel);
                snapFungus.setPos(spawnX, spawnY, spawnZ);
                snapFungus.setOwner(this);
                snapFungus.setDeltaMovement(velocity);
                serverLevel.addFreshEntity(snapFungus);
                com.Polarice3.Goety.common.entities.projectiles.SnapFungus snapFungusSpread = new com.Polarice3.Goety.common.entities.projectiles.SnapFungus(
                        spawnX, spawnY, spawnZ, serverLevel);
                snapFungusSpread.setPos(spawnX, spawnY, spawnZ);
                snapFungusSpread.setOwner(this);
                snapFungusSpread.setDeltaMovement(spreadVelocity);
                serverLevel.addFreshEntity(snapFungusSpread);
                break;
            case 5:
                com.Polarice3.Goety.common.entities.projectiles.SoulBolt soulBolt = new com.Polarice3.Goety.common.entities.projectiles.SoulBolt(
                        spawnX, spawnY, spawnZ, velocity.x, velocity.y, velocity.z,
                        serverLevel);
                soulBolt.setPos(spawnX, spawnY, spawnZ);
                soulBolt.setOwner(this);
                soulBolt.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(soulBolt);
                com.Polarice3.Goety.common.entities.projectiles.SoulBolt soulBoltSpread = new com.Polarice3.Goety.common.entities.projectiles.SoulBolt(
                        spawnX, spawnY, spawnZ, spreadVelocity.x, spreadVelocity.y, spreadVelocity.z,
                        serverLevel);
                soulBoltSpread.setPos(spawnX, spawnY, spawnZ);
                soulBoltSpread.setOwner(this);
                soulBoltSpread.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(soulBoltSpread);
                break;
            case 6:
                com.Polarice3.Goety.common.entities.projectiles.SteamMissile steamMissile = new com.Polarice3.Goety.common.entities.projectiles.SteamMissile(
                        spawnX, spawnY, spawnZ, velocity.x, velocity.y, velocity.z,
                        serverLevel);
                steamMissile.setPos(spawnX, spawnY, spawnZ);
                steamMissile.setOwner(this);
                steamMissile.setExtraDamage(5.0F);

                int missilesAdded = 0;
                for (int i = 0; i < 4; i++) {
                    double spreadX = velocity.x + (serverLevel.random.nextDouble() - 0.5);
                    double spreadY = velocity.y + (serverLevel.random.nextDouble() - 0.5);
                    double spreadZ = velocity.z + (serverLevel.random.nextDouble() - 0.5);
                    com.Polarice3.Goety.common.entities.projectiles.SteamMissile missile = new com.Polarice3.Goety.common.entities.projectiles.SteamMissile(
                            spawnX, spawnY, spawnZ, spreadX, spreadY, spreadZ, serverLevel);
                    missile.setPos(spawnX, spawnY, spawnZ);
                    missile.setOwner(this);
                    missile.setExtraDamage(5.0F);
                    boolean addedMissile = serverLevel.addFreshEntity(missile);
                    if (addedMissile)
                        missilesAdded++;
                }
                com.Polarice3.Goety.common.entities.projectiles.SteamMissile steamMissileSpread = new com.Polarice3.Goety.common.entities.projectiles.SteamMissile(
                        spawnX, spawnY, spawnZ, spreadVelocity.x, spreadVelocity.y, spreadVelocity.z,
                        serverLevel);
                steamMissileSpread.setPos(spawnX, spawnY, spawnZ);
                steamMissileSpread.setOwner(this);
                steamMissileSpread.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(steamMissileSpread);
                break;
            case 7:
                com.Polarice3.Goety.common.entities.projectiles.VoidShock voidShock = new com.Polarice3.Goety.common.entities.projectiles.VoidShock(
                        this, target, serverLevel);
                voidShock.setPos(spawnX, spawnY, spawnZ);
                voidShock.setDeltaMovement(velocity);
                voidShock.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(voidShock);
                com.Polarice3.Goety.common.entities.projectiles.VoidShock voidShockSpread = new com.Polarice3.Goety.common.entities.projectiles.VoidShock(
                        this, target, serverLevel);
                voidShockSpread.setPos(spawnX, spawnY, spawnZ);
                voidShockSpread.setDeltaMovement(spreadVelocity);
                voidShockSpread.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(voidShockSpread);
                break;
            case 8:
                com.Polarice3.Goety.common.entities.projectiles.WitherBolt witherBolt = new com.Polarice3.Goety.common.entities.projectiles.WitherBolt(
                        spawnX, spawnY, spawnZ, velocity.x, velocity.y, velocity.z,
                        serverLevel);
                witherBolt.setPos(spawnX, spawnY, spawnZ);
                witherBolt.setOwner(this);
                witherBolt.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(witherBolt);
                com.Polarice3.Goety.common.entities.projectiles.WitherBolt witherBoltSpread = new com.Polarice3.Goety.common.entities.projectiles.WitherBolt(
                        spawnX, spawnY, spawnZ, spreadVelocity.x, spreadVelocity.y, spreadVelocity.z,
                        serverLevel);
                witherBoltSpread.setPos(spawnX, spawnY, spawnZ);
                witherBoltSpread.setOwner(this);
                witherBoltSpread.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(witherBoltSpread);
                break;
            case 9:
                com.Polarice3.Goety.common.entities.projectiles.IceSpear iceSpear = new com.Polarice3.Goety.common.entities.projectiles.IceSpear(
                        spawnX, spawnY, spawnZ, serverLevel);
                iceSpear.setPos(spawnX, spawnY, spawnZ);
                iceSpear.setOwner(this);
                iceSpear.setDeltaMovement(velocity);
                iceSpear.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(iceSpear);
                com.Polarice3.Goety.common.entities.projectiles.IceSpear iceSpearSpread = new com.Polarice3.Goety.common.entities.projectiles.IceSpear(
                        spawnX, spawnY, spawnZ, serverLevel);
                iceSpearSpread.setPos(spawnX, spawnY, spawnZ);
                iceSpearSpread.setOwner(this);
                iceSpearSpread.setDeltaMovement(spreadVelocity);
                iceSpearSpread.setExtraDamage(5.0F);
                serverLevel.addFreshEntity(iceSpearSpread);
                break;
            case 10:
                com.k1sak1.goetyawaken.common.entities.projectiles.EndermiteEggEntity egg = new com.k1sak1.goetyawaken.common.entities.projectiles.EndermiteEggEntity(
                        com.k1sak1.goetyawaken.common.entities.ModEntityType.ENDERMITE_EGG.get(), serverLevel);
                egg.setPos(spawnX, spawnY, spawnZ);
                egg.setOwner(this);
                egg.setDeltaMovement(velocity);
                egg.setPowerLevel(3);
                serverLevel.addFreshEntity(egg);
                com.k1sak1.goetyawaken.common.entities.projectiles.EndermiteEggEntity eggSpread = new com.k1sak1.goetyawaken.common.entities.projectiles.EndermiteEggEntity(
                        com.k1sak1.goetyawaken.common.entities.ModEntityType.ENDERMITE_EGG.get(), serverLevel);
                eggSpread.setPos(spawnX, spawnY, spawnZ);
                eggSpread.setOwner(this);
                eggSpread.setDeltaMovement(spreadVelocity);
                eggSpread.setPowerLevel(3);
                serverLevel.addFreshEntity(eggSpread);
                break;
            case 11:
                com.k1sak1.goetyawaken.common.entities.projectiles.GhostFireBolt ghostFireBolt = new com.k1sak1.goetyawaken.common.entities.projectiles.GhostFireBolt(
                        com.k1sak1.goetyawaken.common.entities.ModEntityType.GHOST_FIRE_BOLT.get(), serverLevel);
                ghostFireBolt.setPos(spawnX, spawnY, spawnZ);
                ghostFireBolt.setOwner(this);
                ghostFireBolt.setDeltaMovement(velocity);
                serverLevel.addFreshEntity(ghostFireBolt);
                com.k1sak1.goetyawaken.common.entities.projectiles.GhostFireBolt ghostFireBoltSpread = new com.k1sak1.goetyawaken.common.entities.projectiles.GhostFireBolt(
                        com.k1sak1.goetyawaken.common.entities.ModEntityType.GHOST_FIRE_BOLT.get(), serverLevel);
                ghostFireBoltSpread.setPos(spawnX, spawnY, spawnZ);
                ghostFireBoltSpread.setOwner(this);
                ghostFireBoltSpread.setDeltaMovement(spreadVelocity);
                serverLevel.addFreshEntity(ghostFireBoltSpread);
                break;
            case 12:
                com.Polarice3.Goety.common.entities.projectiles.ModDragonFireball dragonFireball = new com.Polarice3.Goety.common.entities.projectiles.ModDragonFireball(
                        serverLevel, this, velocity.x, velocity.y, velocity.z);
                dragonFireball.setPos(spawnX, spawnY, spawnZ);
                serverLevel.addFreshEntity(dragonFireball);
                double leftAngle = Math.toRadians(30);
                Vec3 leftDirection = new Vec3(
                        velocity.x * Math.cos(leftAngle) - velocity.z * Math.sin(leftAngle),
                        velocity.y,
                        velocity.x * Math.sin(leftAngle) + velocity.z * Math.cos(leftAngle)).normalize().scale(2.0D);
                com.Polarice3.Goety.common.entities.projectiles.ModDragonFireball dragonFireballLeft = new com.Polarice3.Goety.common.entities.projectiles.ModDragonFireball(
                        serverLevel, this, leftDirection.x, leftDirection.y, leftDirection.z);
                dragonFireballLeft.setPos(spawnX, spawnY, spawnZ);
                serverLevel.addFreshEntity(dragonFireballLeft);
                double rightAngle = Math.toRadians(-30);
                Vec3 rightDirection = new Vec3(
                        velocity.x * Math.cos(rightAngle) - velocity.z * Math.sin(rightAngle),
                        velocity.y,
                        velocity.x * Math.sin(rightAngle) + velocity.z * Math.cos(rightAngle)).normalize().scale(2.0D);
                com.Polarice3.Goety.common.entities.projectiles.ModDragonFireball dragonFireballRight = new com.Polarice3.Goety.common.entities.projectiles.ModDragonFireball(
                        serverLevel, this, rightDirection.x, rightDirection.y, rightDirection.z);
                dragonFireballRight.setPos(spawnX, spawnY, spawnZ);
                serverLevel.addFreshEntity(dragonFireballRight);
                break;
        }
    }
}