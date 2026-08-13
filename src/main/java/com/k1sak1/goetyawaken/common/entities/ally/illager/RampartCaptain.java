package com.k1sak1.goetyawaken.common.entities.ally.illager;

import java.util.EnumSet;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ai.path.ModClimberNavigation;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.client.animation.AnimationDefinition;
import com.k1sak1.goetyawaken.client.animation.RampartCaptainAnimation;
import com.k1sak1.goetyawaken.common.items.ThreatBanner;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import java.util.function.Predicate;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.IceAxeItem;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class RampartCaptain extends AbstractIllagerServant {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(RampartCaptain.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DATA_IS_RUN = SynchedEntityData.defineId(RampartCaptain.class,
            EntityDataSerializers.BOOLEAN);
    private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (difficulty) -> difficulty == Difficulty.NORMAL
            || difficulty == Difficulty.HARD;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState alertAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();
    public final AnimationState attack1AnimationState = new AnimationState();
    public final AnimationState attack2AnimationState = new AnimationState();
    public final AnimationState throwAnimationState = new AnimationState();
    public final AnimationState summonAnimationState = new AnimationState();
    public final AnimationState windhornAnimationState = new AnimationState();
    public final AnimationState runAttackAnimationState = new AnimationState();
    public final AnimationState bannerAnimationState = new AnimationState();

    public int attackTick = 0;
    public int currentAttackVariant = 0;
    public boolean isIceAxeAttacking = false;
    public boolean shouldRenderMainHandItem = true;

    private int windHornCooldown = 0;
    private int windHornTick = 0;
    private boolean isBlowingHorn = false;

    private int throwCooldown = 0;
    private int throwTick = 0;
    private boolean isThrowing = false;
    private long lastDamageTime = 0;

    private int runAttackCooldown = 0;
    private int runAttackTick = 0;
    private boolean isRunAttacking = false;

    public static final String IDLE = "idle";
    public static final String ALERT = "alert";
    public static final String WALK = "walk";
    public static final String RUN = "run";
    public static final String ATTACK1 = "attack1";
    public static final String ATTACK2 = "attack2";
    public static final String THROW = "throw";
    public static final String SUMMON = "summon";
    public static final String WINDHORN = "windhorn";
    public static final String RUNATTACK = "runattack";
    public static final String BANNERSCALE = "bannerscale";

    private static final java.util.UUID TARGET_SPEED_BOOST_UUID = java.util.UUID
            .fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final AttributeModifier TARGET_SPEED_BOOST = new AttributeModifier(
            TARGET_SPEED_BOOST_UUID, "RampartCaptain target speed boost", 0.05D,
            AttributeModifier.Operation.ADDITION);

    public RampartCaptain(EntityType<? extends RampartCaptain> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(DATA_IS_RUN, false);
    }

    protected PathNavigation createNavigation(Level level) {
        if (MobsConfig.MountaineerClimb.get()) {
            return new ModClimberNavigation(this, level);
        }
        return super.createNavigation(level);
    }

    @Override
    public boolean canOpenDoors() {
        return true;
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
                amount *= 0.2F;
            }
        }
        return super.hurt(source, amount);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34088_, DifficultyInstance p_34089_,
            MobSpawnType p_34090_, @Nullable SpawnGroupData p_34091_, @Nullable CompoundTag p_34092_) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(p_34088_, p_34089_, p_34090_, p_34091_, p_34092_);
        RandomSource randomsource = p_34088_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_34089_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_34089_);
        return spawngroupdata;
    }

    protected SoundEvent getAmbientSound() {
        return com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_MUMBLE.get();
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource p_34103_) {
        return com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_DEATH.get();
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        if (this.isDeadOrDying() && !this.canRevive(pDamageSource) && !this.level().isClientSide) {
            if (this.getTrueOwner() != null
                    && this.getTrueOwner() instanceof net.minecraft.world.entity.player.Player player) {
                ItemStack threatBanner = new ItemStack(
                        com.k1sak1.goetyawaken.common.items.ModItems.THREAT_BANNER.get());
                ThreatBanner.setOwnerName(this.getTrueOwner(), threatBanner);
                ThreatBanner.setSummon(this, threatBanner);
                com.Polarice3.Goety.common.entities.projectiles.FlyingItem flyingItem = new com.Polarice3.Goety.common.entities.projectiles.FlyingItem(
                        com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                        this.level(),
                        this.getX(),
                        this.getY() + 1.0D,
                        this.getZ());
                flyingItem.setOwner(this.getTrueOwner());
                flyingItem.setItem(threatBanner);
                flyingItem.setSecondsCool(30);
                ReviveServantItem.setOwnerName(this.getTrueOwner(), threatBanner);
                this.level().addFreshEntity(flyingItem);
            }
        }
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_MUMBLE.get();
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
    public boolean canBeLeader() {
        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MountaineerBreakDoorGoal(this));
        this.goalSelector.addGoal(2, new WindHornGoal(this));
        this.goalSelector.addGoal(3, new ThrowGoal(this));
        this.goalSelector.addGoal(4, new RunAttackGoal(this));
        this.goalSelector.addGoal(5, new IceAxeAttackGoal(this));
        this.goalSelector.addGoal(6, new RampartCaptainAttackGoal(this, 1.0D, true));
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean p_33820_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_33820_) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    public boolean isRun() {
        return this.entityData.get(DATA_IS_RUN);
    }

    public void setIsRun(boolean run) {
        this.entityData.set(DATA_IS_RUN, run);
    }

    public int getWindHornCooldown() {
        return this.windHornCooldown;
    }

    public void setWindHornCooldown(int cooldown) {
        this.windHornCooldown = cooldown;
    }

    public int getWindHornTick() {
        return this.windHornTick;
    }

    public void setWindHornTick(int tick) {
        this.windHornTick = tick;
    }

    public boolean isBlowingHorn() {
        return this.isBlowingHorn;
    }

    public void setIsBlowingHorn(boolean blowing) {
        this.isBlowingHorn = blowing;
    }

    public int getThrowCooldown() {
        return this.throwCooldown;
    }

    public void setThrowCooldown(int cooldown) {
        this.throwCooldown = cooldown;
    }

    public int getThrowTick() {
        return this.throwTick;
    }

    public void setThrowTick(int tick) {
        this.throwTick = tick;
    }

    public boolean isThrowing() {
        return this.isThrowing;
    }

    public void setIsThrowing(boolean throwing) {
        this.isThrowing = throwing;
    }

    public long getLastDamageTime() {
        return this.lastDamageTime;
    }

    public void setLastDamageTime(long time) {
        this.lastDamageTime = time;
    }

    public int getRunAttackCooldown() {
        return this.runAttackCooldown;
    }

    public void setRunAttackCooldown(int cooldown) {
        this.runAttackCooldown = cooldown;
    }

    public int getRunAttackTick() {
        return this.runAttackTick;
    }

    public void setRunAttackTick(int tick) {
        this.runAttackTick = tick;
    }

    public boolean isRunAttacking() {
        return this.isRunAttacking;
    }

    public void setIsRunAttacking(boolean runAttacking) {
        this.isRunAttacking = runAttacking;
    }

    public boolean isClimbableBlock(BlockPos blockPos) {
        BlockState blockState = this.level().getBlockState(blockPos);
        return (blockState.is(BlockTags.ICE)
                || blockState.is(Tags.Blocks.STONE)
                || blockState.is(Tags.Blocks.COBBLESTONE)
                || blockState.is(BlockTags.DIRT)
                || blockState.is(BlockTags.SNOW))
                && blockState.isSolidRender(this.level(), blockPos);
    }

    public boolean onClimbable() {
        if (MobsConfig.MountaineerClimb.get()) {
            return this.isClimbing();
        }
        return super.onClimbable();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.bannerAnimationState.startIfStopped(this.tickCount);
            stopAnimationIfFinished(this.attack1AnimationState, RampartCaptainAnimation.ATTACK1);
            stopAnimationIfFinished(this.attack2AnimationState, RampartCaptainAnimation.ATTACK2);
            stopAnimationIfFinished(this.throwAnimationState, RampartCaptainAnimation.THROW);
            stopAnimationIfFinished(this.summonAnimationState, RampartCaptainAnimation.SUMMON);
            stopAnimationIfFinished(this.windhornAnimationState, RampartCaptainAnimation.WINDHORN);
            stopAnimationIfFinished(this.runAttackAnimationState, RampartCaptainAnimation.RUNATTACK);

            boolean performingAction = this.attack1AnimationState.isStarted()
                    || this.attack2AnimationState.isStarted()
                    || this.throwAnimationState.isStarted()
                    || this.summonAnimationState.isStarted()
                    || this.windhornAnimationState.isStarted()
                    || this.runAttackAnimationState.isStarted();
            boolean isRunning = this.isRun();
            boolean hasTarget = this.getTarget() != null;
            boolean isGuarding = this.isGuardingArea();

            if (performingAction) {
                this.idleAnimationState.stop();
                this.alertAnimationState.stop();
                this.walkAnimationState.stop();
                if (isRunning) {
                    this.runAnimationState.startIfStopped(this.tickCount);
                } else {
                    this.runAnimationState.stop();
                }
            } else if (isRunning) {
                this.runAnimationState.startIfStopped(this.tickCount);
                this.idleAnimationState.stop();
                this.alertAnimationState.stop();
                this.walkAnimationState.stop();
            } else if (isGuarding && !hasTarget) {
                this.alertAnimationState.startIfStopped(this.tickCount);
                this.idleAnimationState.stop();
                this.runAnimationState.stop();
                this.walkAnimationState.stop();
            } else {
                this.idleAnimationState.startIfStopped(this.tickCount);
                this.alertAnimationState.stop();
                this.runAnimationState.stop();
                this.walkAnimationState.stop();
            }
        }

        if (!this.level().isClientSide) {
            boolean shouldClimb = this.horizontalCollision
                    && !this.isStuckAtCeiling()
                    && MobsConfig.MountaineerClimb.get();
            this.setClimbing(shouldClimb);
            AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                if (this.getTarget() != null && this.getTarget().isAlive()) {
                    if (!speedAttr.hasModifier(TARGET_SPEED_BOOST)) {
                        speedAttr.addPermanentModifier(TARGET_SPEED_BOOST);
                    }
                } else {
                    if (speedAttr.hasModifier(TARGET_SPEED_BOOST)) {
                        speedAttr.removeModifier(TARGET_SPEED_BOOST);
                    }
                }
            }

            if (this.attackTick > 0) {
                this.attackTick--;
            }
            if (this.windHornCooldown > 0) {
                this.windHornCooldown--;
            }
            if (this.windHornTick > 0) {
                this.windHornTick--;
            }
            if (this.throwCooldown > 0) {
                this.throwCooldown--;
            }
            if (this.throwTick > 0) {
                this.throwTick--;
            }
            if (this.runAttackCooldown > 0) {
                this.runAttackCooldown--;
            }
            if (this.runAttackTick > 0) {
                this.runAttackTick--;
            }

            if (this.getTarget() == null || !this.getTarget().isAlive()) {
                if (this.isIceAxeAttacking) {
                    this.isIceAxeAttacking = false;
                    this.attackTick = 0;
                }
                if (this.isRunAttacking) {
                    this.isRunAttacking = false;
                    this.runAttackTick = 0;
                }
                if (this.isBlowingHorn) {
                    this.isBlowingHorn = false;
                    this.windHornTick = 0;
                }
                if (this.isThrowing) {
                    this.isThrowing = false;
                    this.throwTick = 0;
                }
            }
            if (this.isIceAxeAttacking && this.attackTick <= 0) {
                this.isIceAxeAttacking = false;
            }
            if (this.isRunAttacking && this.runAttackTick <= 0) {
                this.isRunAttacking = false;
                this.setIsRun(false);
            }
            if (this.isBlowingHorn && this.windHornTick <= 0) {
                this.isBlowingHorn = false;
            }
            if (this.isThrowing && this.throwTick <= 0) {
                this.isThrowing = false;
            }
        }
    }

    private static void stopAnimationIfFinished(AnimationState state, AnimationDefinition anim) {
        if (state.isStarted() && state.getAccumulatedTime() >= (long) (anim.lengthInSeconds() * 1000.0F)) {
            state.stop();
        }
    }

    private boolean isStuckAtCeiling() {
        BlockPos above = this.blockPosition().above(2);
        return this.level().getBlockState(above).isSolidRender(this.level(), above)
                && this.getDeltaMovement().y <= 0.01D;
    }

    public float getCurrentAttackDamage() {
        return (float) this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr) {
        return (distToEnemySqr <= this.getAttackReachSqr(enemy)
                || this.getBoundingBox().intersects(enemy.getBoundingBox())) && this.hasLineOfSight(enemy);
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return (double) (this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + attackTarget.getBbWidth());
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.stopAllAnimations();
            this.runAnimationState.start(this.tickCount);
        } else if (pId == 5) {
            this.stopAllAnimations();
            this.attack1AnimationState.start(this.tickCount);
        } else if (pId == 6) {
            this.stopAllAnimations();
            this.attack2AnimationState.start(this.tickCount);
        } else if (pId == 7) {
            this.stopAllAnimations();
            this.throwAnimationState.start(this.tickCount);
        } else if (pId == 8) {
            this.stopAllAnimations();
            this.summonAnimationState.start(this.tickCount);
        } else if (pId == 9) {
            this.stopAllAnimations();
            this.windhornAnimationState.start(this.tickCount);
        } else if (pId == 10) {
            this.stopAllAnimations();
            this.runAttackAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public void stopActionAnimations() {
        this.attack1AnimationState.stop();
        this.attack2AnimationState.stop();
        this.throwAnimationState.stop();
        this.summonAnimationState.stop();
        this.windhornAnimationState.stop();
        this.runAttackAnimationState.stop();
    }

    public void stopAllAnimations() {
        this.idleAnimationState.stop();
        this.alertAnimationState.stop();
        this.walkAnimationState.stop();
        this.runAnimationState.stop();
        this.stopActionAnimations();
    }

    public void triggerAnimation(String animation) {
        byte eventId = this.getEventId(animation);
        if (eventId > 0) {
            this.level().broadcastEntityEvent(this, eventId);
        }
    }

    public void triggerAnimation(byte eventId) {
        if (eventId > 0) {
            this.level().broadcastEntityEvent(this, eventId);
        }
    }

    public byte getEventId(String animation) {
        return switch (animation) {
            case IDLE -> 1;
            case ALERT -> 2;
            case WALK -> 3;
            case RUN -> 4;
            case ATTACK1 -> 5;
            case ATTACK2 -> 6;
            case THROW -> 7;
            case SUMMON -> 8;
            case WINDHORN -> 9;
            case RUNATTACK -> 10;
            default -> 0;
        };
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.RampartCaptainHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RampartCaptainDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.RampartCaptainArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.RampartCaptainArmorToughness.get());
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        EntityDimensions baseDimensions = super.getDimensions(pPose);
        return baseDimensions.scale(1.2F);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.RampartCaptainHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.RampartCaptainDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.RampartCaptainArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.RampartCaptainArmorToughness.get());
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219149_, DifficultyInstance p_219150_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.DIAMOND_ICE_AXE.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public boolean isMainWeapon(ItemStack itemStack) {
        return itemStack.getItem() instanceof IceAxeItem || itemStack.is(ModTags.Items.MOUNTAINEER_WEAPONS);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!(pPlayer.getOffhandItem().getItem() instanceof IWand)) {
                if (this.isMainWeapon(itemstack)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                                this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    static class MountaineerBreakDoorGoal extends BreakDoorGoal {
        private final RampartCaptain captain;

        public MountaineerBreakDoorGoal(RampartCaptain p_34112_) {
            super(p_34112_, 6, DOOR_BREAKING_PREDICATE);
            this.captain = p_34112_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canContinueToUse() {
            return captain.isRaiding() && super.canContinueToUse();
        }

        public boolean canUse() {
            return captain.isRaiding() && captain.random.nextInt(reducedTickDelay(10)) == 0 && super.canUse();
        }

        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }

    @Override
    public boolean canFreeze() {
        return false;
    }
}
