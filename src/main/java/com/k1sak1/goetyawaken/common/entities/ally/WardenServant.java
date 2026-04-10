package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.warden.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class WardenServant extends Summoned
        implements VibrationSystem, com.Polarice3.Goety.api.entities.IAutoRideable, ICustomAttributes {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<Integer> CLIENT_ANGER_LEVEL = SynchedEntityData
            .defineId(WardenServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData
            .defineId(WardenServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AUTO_MODE = SynchedEntityData
            .defineId(WardenServant.class, EntityDataSerializers.BOOLEAN);
    private static final int DARKNESS_RADIUS = 20;
    private static final int DARKNESS_DURATION = 260;
    private static final int DARKNESS_DISPLAY_LIMIT = 200;
    private static final int DARKNESS_INTERVAL = 120;
    private int tendrilAnimation;
    private int tendrilAnimationO;
    private int heartAnimation;
    private int heartAnimationO;
    private int darknessCooldown = 0;
    public AngerManagement angerManagement = new AngerManagement(this::canTargetEntity,
            java.util.Collections.emptyList());
    private static final long TARGET_TIMEOUT = 2400;
    private static final int DIG_COOLDOWN = 1200;
    private long lastTargetTime = 0;
    private long lastDigTime = 0;
    public final AnimationState roarAnimationState = new AnimationState();
    public final AnimationState sniffAnimationState = new AnimationState();
    public final AnimationState emergeAnimationState = new AnimationState();
    public final AnimationState diggingAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState sonicBoomAnimationState = new AnimationState();
    private boolean isRoaring = false;
    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener;
    private final VibrationSystem.User vibrationUser;
    private VibrationSystem.Data vibrationData;

    public VibrationSystem.Data getVibrationData() {
        return this.vibrationData;
    }

    public VibrationSystem.User getVibrationUser() {
        return this.vibrationUser;
    }

    class VibrationUser implements VibrationSystem.User {
        private static final int GAME_EVENT_LISTENER_RANGE = 16;
        private final PositionSource positionSource = new EntityPositionSource(WardenServant.this,
                WardenServant.this.getEyeHeight());

        public int getListenerRadius() {
            return 16;
        }

        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.WARDEN_CAN_LISTEN;
        }

        public boolean canTriggerAvoidVibration() {
            return true;
        }

        public boolean canReceiveVibration(ServerLevel p_282574_, BlockPos p_282323_, GameEvent p_283003_,
                GameEvent.Context p_282515_) {
            if (!WardenServant.this.isNoAi() && !WardenServant.this.isDeadOrDying()
                    && !WardenServant.this.getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN)
                    && !WardenServant.this.isDiggingOrEmerging()
                    && p_282574_.getWorldBorder().isWithinBounds(p_282323_)) {
                Entity entity = p_282515_.sourceEntity();
                if (entity instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity) entity;
                    if (!WardenServant.this.canTargetEntity(livingentity)) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        }

        public void onReceiveVibration(ServerLevel p_281325_, BlockPos p_282386_, GameEvent p_282261_,
                @Nullable Entity p_281438_, @Nullable Entity p_282582_, float p_283699_) {
            if (!WardenServant.this.isDeadOrDying()) {
                WardenServant.this.getBrain().setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE,
                        40L);
                p_281325_.broadcastEntityEvent(WardenServant.this, (byte) 61);
                WardenServant.this.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5.0F,
                        WardenServant.this.getVoicePitch());
                BlockPos blockpos = p_282386_;
                if (p_282582_ != null) {
                    if (WardenServant.this.closerThan(p_282582_, 30.0D)) {
                        if (WardenServant.this.getBrain().hasMemoryValue(MemoryModuleType.RECENT_PROJECTILE)) {
                            if (WardenServant.this.canTargetEntity(p_282582_)) {
                                blockpos = p_282582_.blockPosition();
                            }

                            WardenServant.this.increaseAngerAt(p_282582_);
                        } else {
                            WardenServant.this.increaseAngerAt(p_282582_, 10, true);
                        }
                    }

                    WardenServant.this.getBrain().setMemoryWithExpiry(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE,
                            100L);
                } else {
                    WardenServant.this.increaseAngerAt(p_281438_);
                }

                if (!WardenServant.this.getAngerLevel().isAngry()) {
                    Optional<LivingEntity> optional = WardenServant.this.angerManagement.getActiveEntity();
                    if (p_282582_ != null || optional.isEmpty() || optional.get() == p_281438_) {
                        WardenServantAi.setDisturbanceLocation(WardenServant.this, blockpos);
                    }
                }

            }
        }
    }

    public WardenServant(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
        this.vibrationUser = new WardenServant.VibrationUser();
        this.vibrationData = new VibrationSystem.Data();
        this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.lastTargetTime = this.level().getGameTime();
        this.lastDigTime = this.level().getGameTime();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        this.setPose(Pose.EMERGING);
        this.getBrain().setMemoryWithExpiry(MemoryModuleType.IS_EMERGING, Unit.INSTANCE,
                (long) WardenServantAi.EMERGE_DURATION);
        this.playSound(SoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.getPose() == Pose.EMERGING) {
            this.emergeAnimationState.start(this.tickCount);
        }

        if (this.getY() < this.level().getMinBuildHeight()) {
            BlockPos blockpos = this.blockPosition();
            BlockPos blockpos1 = blockpos.above(1);
            if (this.level() instanceof ServerLevel serverlevel && serverlevel.getBlockState(blockpos1).isAir()) {
                this.moveTo(blockpos1, this.getYRot(), this.getXRot());
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public void followGoal() {
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WardenServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WardenServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.WardenServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.WardenServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.WardenServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.3D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 1.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_KNOCKBACK), 1.5D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.WardenServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WardenServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.WardenServantArmorToughness.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLIENT_ANGER_LEVEL, 0);
        this.entityData.define(DATA_SADDLE_ID, false);
        this.entityData.define(AUTO_MODE, false);
    }

    private static final BehaviorControl<WardenServant> DIG_COOLDOWN_SETTER = BehaviorBuilder.create((p_258953_) -> {
        return p_258953_.group(p_258953_.registered(MemoryModuleType.DIG_COOLDOWN)).apply(p_258953_, (p_258960_) -> {
            return (p_258956_, p_258957_, p_258958_) -> {
                if (p_258953_.tryGet(p_258960_).isPresent()) {
                    p_258960_.setWithExpiry(Unit.INSTANCE, 1200L);
                }

                return true;
            };
        });
    });

    public static void onTargetInvalid(WardenServant p_219529_, LivingEntity p_219530_) {
        if (!p_219529_.canTargetEntity(p_219530_)) {
            p_219529_.clearAnger(p_219530_);
        }

        WardenServantAi.setDigCooldown(p_219529_);
    }

    public Optional<LivingEntity> getEntityAngryAt() {
        return this.getAngerLevel().isAngry() ? this.angerManagement.getActiveEntity() : Optional.empty();
    }

    public void clearAnger(Entity pEntity) {
        this.angerManagement.clearAnger(pEntity);
    }

    @Override
    public Brain<WardenServant> getBrain() {
        return (Brain<WardenServant>) super.getBrain();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return WardenServantAi.makeBrain(this, pDynamic);
    }

    private void logDebugInfo(String message) {
    }

    @Override
    protected void customServerAiStep() {

        if (this.level() instanceof ServerLevel serverlevel) {
            serverlevel.getProfiler().push("wardenBrain");
            this.getBrain().tick(serverlevel, this);
            serverlevel.getProfiler().pop();
        }
        super.customServerAiStep();

        if ((this.tickCount + this.getId()) % 120 == 0) {
            this.applyDarknessAround();
        }

        if (this.tickCount % 20 == 0) {
            if (this.level() instanceof ServerLevel serverlevel) {
                this.angerManagement.tick(serverlevel, this::canTargetEntity);
                this.syncClientAngerLevel();
            }
        }
        if (this.level() instanceof ServerLevel) {
            WardenServantAi.updateActivity(this);
        }
    }

    @Override
    public void tick() {
        Level level = this.level();
        if (level instanceof ServerLevel serverlevel) {
            VibrationSystem.Ticker.tick(serverlevel, this.vibrationData, this.vibrationUser);
            if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
                WardenServantAi.setDigCooldown(this);
            }
        }

        super.tick();
        if (this.hasPose(Pose.ROARING)) {
            this.setDeltaMovement(Vec3.ZERO);
        }

        if (this.level().isClientSide()) {
            if (this.tickCount % this.getHeartBeatDelay() == 0) {
                this.heartAnimation = 10;
                if (!this.isSilent()) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_HEARTBEAT,
                            this.getSoundSource(), 5.0F, this.getVoicePitch(), false);
                }
            }

            this.tendrilAnimationO = this.tendrilAnimation;
            if (this.tendrilAnimation > 0) {
                --this.tendrilAnimation;
            }

            this.heartAnimationO = this.heartAnimation;
            if (this.heartAnimation > 0) {
                --this.heartAnimation;
            }
            switch (this.getPose()) {
                case EMERGING:
                    this.clientDiggingParticles(this.emergeAnimationState);
                    break;
                case DIGGING:
                    this.clientDiggingParticles(this.diggingAnimationState);
                    break;
            }
        }
        if (!this.level().isClientSide) {
            this.checkDiggingCondition();
            WardenServantAi.tickSonicBoomEffect(this);
        }
    }

    public void clientDiggingParticles(AnimationState pAnimationState) {
        if ((float) pAnimationState.getAccumulatedTime() < 4500.0F) {
            RandomSource randomsource = this.getRandom();
            BlockState blockstate = this.getBlockStateOn();
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                for (int i = 0; i < 30; ++i) {
                    double d0 = this.getX() + (double) Mth.randomBetween(randomsource, -0.7F, 0.7F);
                    double d1 = this.getY();
                    double d2 = this.getZ() + (double) Mth.randomBetween(randomsource, -0.7F, 0.7F);
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), d0, d1, d2, 0.0D,
                            0.0D, 0.0D);
                }
            }
        }
    }

    private void checkDiggingCondition() {
        if (this.getTarget() != null) {
            this.lastTargetTime = this.level().getGameTime();
            return;
        }
        if (this.hasPose(Pose.ROARING) || this.hasPose(Pose.SNIFFING) ||
                this.hasPose(Pose.EMERGING) || this.hasPose(Pose.DIGGING)) {
            return;
        }
        long currentTime = this.level().getGameTime();
        if (currentTime - this.lastTargetTime > TARGET_TIMEOUT &&
                currentTime - this.lastDigTime > DIG_COOLDOWN) {
            if (this.getPose() == Pose.STANDING && !this.getBrain().hasMemoryValue(MemoryModuleType.DIG_COOLDOWN)) {
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, DIG_COOLDOWN);
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, null, 1);
                this.lastDigTime = currentTime;
            }
        }
    }

    private void updateAnger() {
        if (this.level() instanceof ServerLevel serverlevel) {
            this.angerManagement.tick(serverlevel, this::canTargetEntity);
            this.syncClientAngerLevel();
        }
    }

    @VisibleForTesting
    public void increaseAngerAt(@Nullable Entity pEntity, int pOffset, boolean pPlayListeningSound) {
        if (!this.isNoAi() && this.canTargetEntity(pEntity)) {
            WardenServantAi.setDigCooldown(this);
            boolean flag = !(this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                    .orElse((LivingEntity) null) instanceof Player);
            int i = this.angerManagement.increaseAnger(pEntity, pOffset);
            if (pEntity instanceof Player && flag && AngerLevel.byAnger(i).isAngry()) {
                this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }

            if (pPlayListeningSound) {
                this.playListeningSound();
            }
        }
    }

    public void increaseAngerAt(@Nullable Entity pEntity) {
        this.increaseAngerAt(pEntity, 35, true);
    }

    public void decreaseAnger(int amount) {
    }

    private void syncClientAngerLevel() {
        this.entityData.set(CLIENT_ANGER_LEVEL, this.getActiveAnger());
    }

    private int getActiveAnger() {
        return this.angerManagement.getActiveAnger(this.getTarget());
    }

    public AngerLevel getAngerLevel() {
        return AngerLevel.byAnger(this.getActiveAnger());
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse((LivingEntity) null);
    }

    private int getHeartBeatDelay() {
        float f = (float) this.getClientAngerLevel() / (float) AngerLevel.ANGRY.getMinimumAnger();
        return 40 - Mth.floor(Mth.clamp(f, 0.0F, 1.0F) * 30.0F);
    }

    public float getTendrilAnimation(float pPartialTick) {
        return Mth.lerp(pPartialTick, (float) this.tendrilAnimationO, (float) this.tendrilAnimation) / 10.0F;
    }

    public float getHeartAnimation(float pPartialTick) {
        return Mth.lerp(pPartialTick, (float) this.heartAnimationO, (float) this.heartAnimation) / 10.0F;
    }

    public int getClientAngerLevel() {
        return this.entityData.get(CLIENT_ANGER_LEVEL);
    }

    private void setClientAngerLevel(int angerLevel) {
        this.entityData.set(CLIENT_ANGER_LEVEL, angerLevel);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return livingEntity -> livingEntity instanceof WardenServant;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.wardenServantLimit;
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new GroundPathNavigation(this, pLevel) {
            protected boolean hasValidPathType(BlockPathTypes pPathType) {
                return pPathType != BlockPathTypes.DANGER_FIRE && pPathType != BlockPathTypes.DAMAGE_FIRE
                        && pPathType != BlockPathTypes.DAMAGE_OTHER && pPathType != BlockPathTypes.LAVA
                        && pPathType != BlockPathTypes.POWDER_SNOW && pPathType != BlockPathTypes.UNPASSABLE_RAIL
                        && super.hasValidPathType(pPathType);
            }
        };
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.hasPose(Pose.ROARING)) {
            return null;
        } else if (this.isDiggingOrEmerging()) {
            return null;
        } else {
            return this.getAngerLevel().getAmbientSound();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.WARDEN_HURT;
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
    protected SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(SoundEvents.WARDEN_STEP, 10.0F, 1.0F);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 10.0F, this.getVoicePitch());
        SonicBoom.setCooldown(this, 40);
        return super.doHurtTarget(pEntity);
    }

    @Override
    public boolean canDisableShield() {
        return true;
    }

    public void setAttackTarget(LivingEntity pAttackTarget) {
        this.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
        this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, pAttackTarget);
        this.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        SonicBoom.setCooldown(this, 200);
    }

    public LivingEntity getAttackTarget() {
        return this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    @VisibleForTesting
    public AngerManagement getAngerManagement() {
        return this.angerManagement;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean flag = super.hurt(pSource, pAmount);
        if (!this.level().isClientSide && !this.isNoAi() && !this.isDiggingOrEmerging()) {
            if (this.hasPose(Pose.ROARING)) {
                this.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
            }

            Entity entity = pSource.getEntity();
            this.increaseAngerAt(entity, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity;
                if (!pSource.isIndirect() || this.closerThan(livingentity, 5.0D)) {
                    this.setAttackTarget(livingentity);
                }
            }
        }

        return flag;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.roarAnimationState.stop();
            this.attackAnimationState.start(this.tickCount);
        } else if (pId == 61) {
            this.tendrilAnimation = 10;
        } else if (pId == 62) {
            this.sonicBoomAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public void die(DamageSource pCause) {
        long currentTime = this.level().getGameTime();
        if (currentTime - this.lastTargetTime > TARGET_TIMEOUT &&
                !this.hasPose(Pose.DIGGING) &&
                !this.hasPose(Pose.EMERGING)) {
            this.setPose(Pose.DIGGING);
            if (this.level() instanceof ServerLevel) {
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 100L);
            }
        }
        super.die(pCause);
    }

    protected void doPush(Entity pEntity) {
        if (!this.isNoAi() && !this.getBrain().hasMemoryValue(MemoryModuleType.TOUCH_COOLDOWN)) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
            this.increaseAngerAt(pEntity);
            WardenServantAi.setDisturbanceLocation(this, pEntity.blockPosition());
        }

        super.doPush(pEntity);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return this.isDiggingOrEmerging() && !pSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)
                ? true
                : super.isInvulnerableTo(pSource);
    }

    @Override
    public boolean isPushable() {
        return !this.isDiggingOrEmerging() && super.isPushable();
    }

    public boolean dampensVibrations() {
        return true;
    }

    private boolean isDiggingOrEmerging() {
        return this.hasPose(Pose.DIGGING) || this.hasPose(Pose.EMERGING);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        EntityDimensions entitydimensions = super.getDimensions(pPose);
        return this.isDiggingOrEmerging() ? EntityDimensions.fixed(entitydimensions.width, 1.0F) : entitydimensions;
    }

    private void applyDarknessAround() {
        if (this.level() instanceof ServerLevel serverLevel) {
            MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.DARKNESS, DARKNESS_DURATION, 0,
                    false, false);
            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(DARKNESS_RADIUS))) {
                if (!MobUtil.areAllies(this, livingEntity) && livingEntity != this.getTrueOwner()) {
                    livingEntity.addEffect(mobeffectinstance, this);
                }
            }
        }
    }

    public void notifyBySound(BlockPos pos, int angerIncrease) {
        if (this.level().isClientSide) {
            this.tendrilAnimation = 10;
        } else {
            this.level().broadcastEntityEvent(this, (byte) 61);
        }
        this.lastTargetTime = this.level().getGameTime();
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

        if (target.getType() == net.minecraft.world.entity.EntityType.ARMOR_STAND ||
                target.getType() == net.minecraft.world.entity.EntityType.ITEM_FRAME ||
                target.getType() == net.minecraft.world.entity.EntityType.GLOW_ITEM_FRAME ||
                target.getType() == net.minecraft.world.entity.EntityType.PAINTING) {
            return false;
        }

        return super.canAttack(target);
    }

    public boolean canAttackWithAnger(LivingEntity target) {
        if (!this.canAttack(target)) {
            return false;
        }
        AngerLevel angerLevel = this.getAngerLevel();
        return angerLevel.isAngry();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getTrueOwner() == player) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (!this.level().isClientSide && this.getHealth() < this.getMaxHealth()) {
                float healAmount = this.getHealAmount(itemstack);
                if (healAmount > 0) {
                    float oldHealth = this.getHealth();
                    this.heal(healAmount);
                    float newHealth = this.getHealth();
                    if (newHealth > oldHealth) {
                        if (!player.isCreative()) {
                            itemstack.shrink(1);
                        }
                        this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            if (player.isShiftKeyDown()) {
                return super.mobInteract(player, hand);
            } else {
                if (!this.level().isClientSide) {
                    if (!this.isDiggingOrEmerging()) {
                        player.startRiding(this);
                        return InteractionResult.CONSUME;
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    private float getHealAmount(ItemStack stack) {
        net.minecraft.world.item.Item item = stack.getItem();
        if (item == net.minecraft.world.item.Items.ECHO_SHARD) {
            return this.getMaxHealth() * 0.05F;
        } else if (item == net.minecraft.world.item.Items.SCULK) {
            return this.getMaxHealth() * 0.01F;
        } else if (item == net.minecraft.world.item.Items.SCULK_CATALYST) {
            return this.getMaxHealth() * 0.05F;
        } else if (item == net.minecraft.world.item.Items.SCULK_SHRIEKER) {
            return this.getMaxHealth() * 0.05F;
        }
        return 0.0F;
    }

    public void playListeningSound() {
        if (!this.hasPose(Pose.ROARING)) {
            this.playSound(this.getAngerLevel().getListeningSound(), 10.0F, this.getVoicePitch());
        }
    }

    @Contract("null->false")
    public boolean canTargetEntity(@Nullable Entity p_219386_) {
        if (p_219386_ instanceof LivingEntity livingentity) {
            if (this.level() == p_219386_.level() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(p_219386_)
                    && !this.isAlliedTo(p_219386_) && livingentity.getType() != EntityType.ARMOR_STAND
                    && !livingentity.isInvulnerable() && !livingentity.isDeadOrDying()
                    && this.level().getWorldBorder().isWithinBounds(livingentity.getBoundingBox())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("DarknessCooldown", this.darknessCooldown);
        pCompound.putBoolean("AutoMode", this.isAutonomous());
        AngerManagement.codec(this::canTargetEntity).encodeStart(NbtOps.INSTANCE, this.angerManagement)
                .resultOrPartial(LOGGER::error).ifPresent((p_219437_) -> {
                    pCompound.put("anger", p_219437_);
                });
        VibrationSystem.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationData).resultOrPartial(LOGGER::error)
                .ifPresent((p_219418_) -> {
                    pCompound.put("listener", p_219418_);
                });
        pCompound.putLong("LastTargetTime", this.lastTargetTime);
        pCompound.putLong("LastDigTime", this.lastDigTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
        if (pCompound.contains("DarknessCooldown")) {
            this.darknessCooldown = pCompound.getInt("DarknessCooldown");
        }
        if (pCompound.contains("AutoMode")) {
            this.setAutonomous(pCompound.getBoolean("AutoMode"));
        }
        if (pCompound.contains("anger")) {
            AngerManagement.codec(this::canTargetEntity).parse(new Dynamic<>(NbtOps.INSTANCE, pCompound.get("anger")))
                    .resultOrPartial(LOGGER::error).ifPresent((p_219394_) -> {
                        this.angerManagement = p_219394_;
                    });
            this.syncClientAngerLevel();
        }

        if (pCompound.contains("listener", 10)) {
            VibrationSystem.Data.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, pCompound.getCompound("listener")))
                    .resultOrPartial(LOGGER::error).ifPresent((p_281093_) -> {
                        this.vibrationData = p_281093_;
                    });
        }
        if (pCompound.contains("LastTargetTime")) {
            this.lastTargetTime = pCompound.getLong("LastTargetTime");
        }
        if (pCompound.contains("LastDigTime")) {
            this.lastDigTime = pCompound.getLong("LastDigTime");
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_POSE.equals(pKey)) {
            switch (this.getPose()) {
                case EMERGING:
                    this.emergeAnimationState.start(this.tickCount);
                    break;
                case DIGGING:
                    this.diggingAnimationState.start(this.tickCount);
                    break;
                case ROARING:
                    this.roarAnimationState.start(this.tickCount);
                    this.getNavigation().stop();
                    this.setDeltaMovement(Vec3.ZERO);
                    break;
                case SNIFFING:
                    this.sniffAnimationState.start(this.tickCount);
                    break;
            }
        }

        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Pose.EMERGING) ? 1 : 0);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        if (pPacket.getData() == 1) {
            this.setPose(Pose.EMERGING);
        }
    }

    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> pListenerConsumer) {
        Level level = this.level();
        if (level instanceof ServerLevel serverlevel) {
            pListenerConsumer.accept(this.dynamicGameEventListener, serverlevel);
        }
    }

    public void setAutonomous(boolean autonomous) {
        this.entityData.set(AUTO_MODE, autonomous);
        if (autonomous) {
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
            if (entity instanceof LivingEntity) {
                if (entity instanceof Mob) {
                    if (MobsConfig.ServantRideAutonomous.get()) {
                        return null;
                    }
                    return (LivingEntity) entity;
                } else if (!this.isAutonomous()) {
                    return (LivingEntity) entity;
                }
            }
        }
        return null;
    }

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
                float baseSpeed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float finalSpeed = baseSpeed * 1.2F;
                float f = rider.xxa * finalSpeed;
                float f1 = rider.zza * finalSpeed;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }
                if (this.isInWater()
                        && this.getFluidTypeHeight(ForgeMod.WATER_TYPE.get()) > this.getFluidJumpThreshold()
                        || this.isInLava()
                        || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType)
                                && height > this.getFluidJumpThreshold())) {
                    Vec3 vector3d = this.getDeltaMovement();
                    this.setDeltaMovement(vector3d.x, 0.04F, vector3d.z);
                    this.hasImpulse = true;
                    if (f1 > 0.0F) {
                        float f2 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
                        float f3 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
                        this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f2 * 0.04F), 0.0D,
                                (double) (0.4F * f3 * 0.04F)));
                    }
                }
                this.setSpeed(finalSpeed);
                super.travel(new Vec3(f, pTravelVector.y, f1));
                this.lerpSteps = 0;
            } else {
                super.travel(pTravelVector);
            }
        }
    }
}