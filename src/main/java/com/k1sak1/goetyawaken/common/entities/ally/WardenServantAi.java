package com.k1sak1.goetyawaken.common.entities.ally;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import com.k1sak1.goetyawaken.common.entities.ally.sensing.WardenServantEntitySensor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.k1sak1.goetyawaken.common.entities.ally.behavior.SetRoarTargetServant;
import com.k1sak1.goetyawaken.common.entities.ally.behavior.StopAttackingIfTargetInvalidServant;
import com.k1sak1.goetyawaken.common.entities.ally.behavior.TryToSniffServant;
import com.k1sak1.goetyawaken.common.entities.ally.behavior.SetWardenLookTargetServant;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ModMemoryModuleTypes {
    public static final MemoryModuleType<LivingEntity> PLAYER_TRIGGERED_SONIC_BOOM = new MemoryModuleType<>(
            Optional.empty());
}

public class WardenServantAi {
    private static final Logger LOGGER = LoggerFactory.getLogger(WardenServantAi.class);
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.5F;
    private static final float SPEED_MULTIPLIER_WHEN_INVESTIGATING = 0.7F;
    private static final float SPEED_MULTIPLIER_WHEN_FIGHTING = 1.2F;
    private static final int MELEE_ATTACK_COOLDOWN = 18;
    private static final int DIGGING_DURATION = Mth.ceil(100.0F);
    public static final int EMERGE_DURATION = Mth.ceil(133.59999F);
    public static final int ROAR_DURATION = Mth.ceil(84.0F);
    private static final int SNIFFING_DURATION = Mth.ceil(83.2F);
    public static final int DIGGING_COOLDOWN = 1200;
    private static final int DISTURBANCE_LOCATION_EXPIRY_TIME = 100;
    private static final List<SensorType<? extends net.minecraft.world.entity.ai.sensing.Sensor<? super WardenServant>>> SENSOR_TYPES = List
            .of(
                    SensorType.NEAREST_PLAYERS,
                    new SensorType<>(WardenServantEntitySensor::new));
    private static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.NEAREST_ATTACKABLE,
            MemoryModuleType.ROAR_TARGET,
            MemoryModuleType.DISTURBANCE_LOCATION,
            MemoryModuleType.RECENT_PROJECTILE,
            MemoryModuleType.IS_SNIFFING,
            MemoryModuleType.IS_EMERGING,
            MemoryModuleType.ROAR_SOUND_DELAY,
            MemoryModuleType.DIG_COOLDOWN,
            MemoryModuleType.ROAR_SOUND_COOLDOWN,
            MemoryModuleType.SNIFF_COOLDOWN,
            MemoryModuleType.TOUCH_COOLDOWN,
            MemoryModuleType.VIBRATION_COOLDOWN,
            MemoryModuleType.SONIC_BOOM_COOLDOWN,
            MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN,
            MemoryModuleType.SONIC_BOOM_SOUND_DELAY,
            ModMemoryModuleTypes.PLAYER_TRIGGERED_SONIC_BOOM);
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

    public static void updateActivity(WardenServant pWarden) {
        pWarden.getBrain().setActiveActivityToFirstValid(ImmutableList.of(
                Activity.EMERGE,
                Activity.DIG,
                Activity.ROAR,
                Activity.FIGHT,
                Activity.INVESTIGATE,
                Activity.SNIFF,
                Activity.IDLE));
    }

    public static Brain<WardenServant> makeBrain(WardenServant pWarden, com.mojang.serialization.Dynamic<?> pOps) {
        Brain.Provider<WardenServant> provider = Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
        Brain<WardenServant> brain = provider.makeBrain(pOps);
        initCoreActivity(brain);
        initEmergeActivity(brain);
        initDiggingActivity(brain);
        initIdleActivity(brain);
        initRoarActivity(brain);
        initFightActivity(pWarden, brain);
        initInvestigateActivity(brain);
        initSniffingActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<WardenServant> pBrain) {
        pBrain.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim(0.8F), SetWardenLookTargetServant.create(),
                new LookAtTargetSink(45, 90), new MoveToTargetSink()));
    }

    private static void initEmergeActivity(Brain<WardenServant> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.EMERGE, 5,
                ImmutableList.of(new WardenServantAi.EmergingServant(EMERGE_DURATION)), MemoryModuleType.IS_EMERGING);
    }

    private static void initDiggingActivity(Brain<WardenServant> pBrain) {
        pBrain.addActivityWithConditions(Activity.DIG,
                ImmutableList.of(Pair.of(0, new WardenServantAi.ForceUnmountServant()),
                        Pair.of(1, new WardenServantAi.DiggingServant(DIGGING_DURATION))),
                ImmutableSet.of(Pair.of(MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.DIG_COOLDOWN, MemoryStatus.VALUE_ABSENT)));
    }

    private static void initIdleActivity(Brain<WardenServant> pBrain) {
        pBrain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                SetRoarTargetServant.create((WardenServant warden) -> warden.getEntityAngryAt()),
                TryToSniffServant.create(),
                new RunOne<>(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(Pair.of(RandomStroll.stroll(0.5F), 2), Pair.of(new DoNothing(30, 60), 1)))));
    }

    private static void initInvestigateActivity(Brain<WardenServant> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.INVESTIGATE, 5,
                ImmutableList.of(
                        SetRoarTargetServant.create((WardenServant warden) -> warden.getEntityAngryAt()),
                        GoToTargetLocation.create(MemoryModuleType.DISTURBANCE_LOCATION, 2, 0.7F)),
                MemoryModuleType.DISTURBANCE_LOCATION);
    }

    private static void initRoarActivity(Brain<WardenServant> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.ROAR, 10,
                ImmutableList.of(new WardenServantAi.RoarServant()),
                MemoryModuleType.ROAR_TARGET);
    }

    private static void initSniffingActivity(Brain<WardenServant> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5,
                ImmutableList.of(
                        SetRoarTargetServant.create((WardenServant warden) -> warden.getEntityAngryAt()),
                        new WardenServantAi.SniffingServant(SNIFFING_DURATION)),
                MemoryModuleType.IS_SNIFFING);
    }

    private static void initFightActivity(WardenServant pWarden, Brain<WardenServant> pBrain) {
        pBrain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10,
                ImmutableList.of(DIG_COOLDOWN_SETTER,
                        StopAttackingIfTargetInvalidServant.create((LivingEntity p_219540_) -> {
                            return !pWarden.getAngerLevel().isAngry() || !pWarden.canTargetEntity(p_219540_);
                        }, WardenServant::onTargetInvalid, false),
                        SetEntityLookTarget.create((LivingEntity p_219535_) -> {
                            return isTarget(pWarden, p_219535_);
                        }, (float) pWarden.getAttributeValue(Attributes.FOLLOW_RANGE)),
                        SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F),
                        new SonicBoomServant(),
                        MeleeAttack.create(18)),
                MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isTarget(WardenServant pWarden, LivingEntity pEntity) {
        return pWarden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter((p_219509_) -> {
            return p_219509_ == pEntity;
        }).isPresent();
    }

    public static void setDigCooldown(LivingEntity pEntity) {
        pEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
    }

    public static void setSonicBoomCooldown(LivingEntity pEntity, int pCooldown) {
        pEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, (long) pCooldown);
    }

    public static void setDisturbanceLocation(WardenServant pWarden, net.minecraft.core.BlockPos pDisturbanceLocation) {
        if (pWarden.level().getWorldBorder().isWithinBounds(pDisturbanceLocation)
                && !pWarden.getEntityAngryAt().isPresent()
                && !pWarden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            setDigCooldown(pWarden);
            pWarden.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 100L);
            pWarden.getBrain().setMemoryWithExpiry(MemoryModuleType.LOOK_TARGET,
                    new net.minecraft.world.entity.ai.behavior.BlockPosTracker(pDisturbanceLocation), 100L);
            pWarden.getBrain().setMemoryWithExpiry(MemoryModuleType.DISTURBANCE_LOCATION, pDisturbanceLocation, 100L);
            pWarden.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
    }

    private static void push(Entity pEntity, double pX, double pY, double pZ) {
        pEntity.setDeltaMovement(pEntity.getDeltaMovement().add(pX, pY, pZ));
    }

    private static boolean areAllies(LivingEntity pEntity, Entity pOther) {
        if (pOther == pEntity) {
            return true;
        } else if (pOther.getTeam() != null && pEntity.isAlliedTo(pOther.getTeam())) {
            return true;
        } else if (pOther instanceof LivingEntity livingEntity) {
            if (pEntity instanceof WardenServant wardenServant) {
                if (wardenServant.getTrueOwner() != null &&
                        (pOther == wardenServant.getTrueOwner() || wardenServant.getTrueOwner().isAlliedTo(pOther))) {
                    return true;
                }
            }
            return pEntity.isAlliedTo(pOther);
        } else {
            return false;
        }
    }

    public static void increaseAngerAt(WardenServant pWarden, @Nullable Entity pEntity, int pOffset,
            boolean pPlayListeningSound) {
        if (!pWarden.isNoAi() && pWarden.canTargetEntity(pEntity)) {
            setDigCooldown(pWarden);
            boolean flag = !(pWarden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET)
                    .orElse((LivingEntity) null) instanceof Player);
            int i = pWarden.angerManagement.increaseAnger(pEntity, pOffset);
            if (pEntity instanceof Player && flag && AngerLevel.byAnger(i).isAngry()) {
                pWarden.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }

            if (pPlayListeningSound) {
                pWarden.playListeningSound();
            }
        }
    }

    public static void increaseAngerAt(WardenServant pWarden, @Nullable Entity pEntity) {
        increaseAngerAt(pWarden, pEntity, 35, true);
    }

    /**
     * @param wardenServant 监守者仆从
     * @param player        控制监守者的玩家
     */
    public static void executeSonicBoomCrystallizationEffect(WardenServant wardenServant, ServerPlayer player) {
        setSonicBoomCooldown(wardenServant, 40);

        wardenServant.level().broadcastEntityEvent(wardenServant, (byte) 62);

        wardenServant.playSound(net.minecraft.sounds.SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);

        wardenServant.getBrain().setMemoryWithExpiry(
                net.minecraft.world.entity.ai.memory.MemoryModuleType.SONIC_BOOM_SOUND_DELAY,
                net.minecraft.util.Unit.INSTANCE,
                34L);

        wardenServant.getBrain().setMemory(
                ModMemoryModuleTypes.PLAYER_TRIGGERED_SONIC_BOOM,
                player);
    }

    /**
     * @param wardenServant
     */
    public static void tickSonicBoomEffect(WardenServant wardenServant) {
        if (wardenServant.getBrain().hasMemoryValue(ModMemoryModuleTypes.PLAYER_TRIGGERED_SONIC_BOOM) &&
                !wardenServant.getBrain()
                        .hasMemoryValue(net.minecraft.world.entity.ai.memory.MemoryModuleType.SONIC_BOOM_SOUND_DELAY)) {
            LivingEntity player = wardenServant.getBrain().getMemory(ModMemoryModuleTypes.PLAYER_TRIGGERED_SONIC_BOOM)
                    .orElse(null);
            if (player != null && player.isPassengerOfSameVehicle(wardenServant)) {
                executeSonicBoomEffect(wardenServant);
            }
            wardenServant.getBrain().eraseMemory(ModMemoryModuleTypes.PLAYER_TRIGGERED_SONIC_BOOM);
        }
    }

    /**
     * @param wardenServant 监守者仆从
     */
    private static void executeSonicBoomEffect(WardenServant wardenServant) {
        if (!(wardenServant.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 direction = wardenServant.getViewVector(1.0F);
        if (wardenServant.getControllingPassenger() instanceof ServerPlayer player) {
            direction = player.getLookAngle();
        }

        Vec3 startPosition = wardenServant.position().add(0.0D, (double) 1.6F, 0.0D);

        Vec3 normalizedDirection = direction.normalize();

        wardenServant.playSound(net.minecraft.sounds.SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);

        for (int i = 1; i < Mth.floor(direction.length()) + 15; ++i) {
            Vec3 particlePos = startPosition.add(normalizedDirection.scale((double) i));
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                    particlePos.x, particlePos.y, particlePos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        AABB effectArea = wardenServant.getBoundingBox().inflate(15.0D, 20.0D, 15.0D);
        List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, effectArea);

        for (LivingEntity entity : entities) {
            if (entity == wardenServant || entity == wardenServant.getTrueOwner()) {
                continue;
            }

            Vec3 entityPos = entity.position();
            Vec3 toEntity = entityPos.subtract(startPosition);

            double distanceToLine = toEntity.cross(normalizedDirection).length();

            if (distanceToLine < 2.0D) {
                entity.hurt(serverLevel.damageSources().sonicBoom(wardenServant), 10.0F);

                double knockbackResistance = entity
                        .getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
                double verticalKnockback = 0.5D * (1.0D - knockbackResistance);
                double horizontalKnockback = 2.5D * (1.0D - knockbackResistance);

                entity.push(
                        normalizedDirection.x() * horizontalKnockback,
                        normalizedDirection.y() * verticalKnockback,
                        normalizedDirection.z() * horizontalKnockback);
            }
        }
    }

    public static class ForceUnmountServant extends Behavior<WardenServant> {
        public ForceUnmountServant() {
            super(ImmutableMap.of());
        }

        protected boolean checkExtraStartConditions(ServerLevel p_238424_, WardenServant p_238425_) {
            return p_238425_.isPassenger();
        }

        protected void start(ServerLevel p_238410_, WardenServant p_238411_, long p_238412_) {
            p_238411_.unRide();
        }
    }

    public static class EmergingServant extends Behavior<WardenServant> {
        public EmergingServant(int pDuration) {
            super(ImmutableMap.of(MemoryModuleType.IS_EMERGING, MemoryStatus.VALUE_PRESENT,
                    MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET,
                    MemoryStatus.REGISTERED), pDuration);
        }

        protected boolean canStillUse(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            return true;
        }

        protected void start(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            pEntity.setPose(Pose.EMERGING);
            pEntity.playSound(SoundEvents.WARDEN_EMERGE, 5.0F, 1.0F);
        }

        protected void stop(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            if (pEntity.hasPose(Pose.EMERGING)) {
                pEntity.setPose(Pose.STANDING);
            }
        }
    }

    public static class DiggingServant extends Behavior<WardenServant> {
        public DiggingServant(int pDuration) {
            super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT,
                    MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), pDuration);
        }

        protected boolean canStillUse(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            return pEntity.hasPose(Pose.DIGGING) && pEntity.getRemovalReason() == null;
        }

        protected boolean checkExtraStartConditions(ServerLevel pLevel, WardenServant pOwner) {
            return pOwner.onGround() || pOwner.isInWater() || pOwner.isInLava();
        }

        protected void start(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            if (pEntity.onGround()) {
                pEntity.setPose(Pose.DIGGING);
                pEntity.playSound(SoundEvents.WARDEN_DIG, 5.0F, 1.0F);
            } else {
                pEntity.playSound(SoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
                this.stop(pLevel, pEntity, pGameTime);
            }
        }

        protected void stop(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            if (pEntity.hasPose(Pose.DIGGING)) {
                if (pEntity.getRemovalReason() == null) {
                    pEntity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }

        protected void tick(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            if (pEntity.level().isClientSide && pEntity.tickCount % 3 == 0) {
                pEntity.clientDiggingParticles(pEntity.diggingAnimationState);
            }
            pEntity.setDeltaMovement(Vec3.ZERO);
        }
    }

    public static class RoarServant extends Behavior<WardenServant> {
        private static final int TICKS_BEFORE_PLAYING_ROAR_SOUND = 25;
        private static final int ROAR_ANGER_INCREASE = 20;

        public RoarServant() {
            super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_PRESENT,
                    MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.ROAR_SOUND_COOLDOWN,
                    MemoryStatus.REGISTERED, MemoryModuleType.ROAR_SOUND_DELAY, MemoryStatus.REGISTERED),
                    ROAR_DURATION);
        }

        protected void start(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            Brain<WardenServant> brain = pEntity.getBrain();
            brain.setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            LivingEntity livingentity = pEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).get();
            pEntity.lookAt(livingentity, 10.0F, 10.0F);
            pEntity.setPose(Pose.ROARING);
            pEntity.increaseAngerAt(livingentity, 20, false);
        }

        protected boolean canStillUse(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            return true;
        }

        protected void tick(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            if (!pEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_DELAY)
                    && !pEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
                pEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE,
                        (long) (ROAR_DURATION - 25));
                pEntity.playSound(SoundEvents.WARDEN_ROAR, 3.0F, 1.0F);
            }
        }

        protected void stop(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            if (pEntity.hasPose(Pose.ROARING)) {
                pEntity.setPose(Pose.STANDING);
            }

            pEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).ifPresent(pEntity::setAttackTarget);
            pEntity.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
        }
    }

    public static class SniffingServant extends Behavior<WardenServant> {
        private static final double ANGER_FROM_SNIFFING_MAX_DISTANCE_XZ = 6.0D;
        private static final double ANGER_FROM_SNIFFING_MAX_DISTANCE_Y = 20.0D;

        public SniffingServant(int pDuration) {
            super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT,
                    MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET,
                    MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                    MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.REGISTERED, MemoryModuleType.DISTURBANCE_LOCATION,
                    MemoryStatus.REGISTERED, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.REGISTERED), pDuration);
        }

        protected boolean canStillUse(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            return true;
        }

        protected void start(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            pEntity.playSound(SoundEvents.WARDEN_SNIFF, 5.0F, 1.0F);
        }

        protected void stop(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            if (pEntity.hasPose(Pose.SNIFFING)) {
                pEntity.setPose(Pose.STANDING);
            }

            pEntity.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
            pEntity.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE).filter(pEntity::canTargetEntity)
                    .ifPresent((p_289391_) -> {
                        if (pEntity.closerThan(p_289391_, 6.0D, 20.0D)) {
                            pEntity.increaseAngerAt(p_289391_);
                        }

                        if (!pEntity.getBrain().hasMemoryValue(MemoryModuleType.DISTURBANCE_LOCATION)) {
                            WardenServantAi.setDisturbanceLocation(pEntity, p_289391_.blockPosition());
                        }
                    });
        }
    }

    public static class SonicBoomServant extends Behavior<WardenServant> {
        private static final int DISTANCE_XZ = 15;
        private static final int DISTANCE_Y = 20;
        private static final double KNOCKBACK_VERTICAL = 0.5D;
        private static final double KNOCKBACK_HORIZONTAL = 2.5D;
        public static final int COOLDOWN = 40;
        private static final int TICKS_BEFORE_PLAYING_SOUND = Mth.ceil(34.0D);
        private static final int DURATION = Mth.ceil(60.0F);

        public SonicBoomServant() {
            super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                    MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryStatus.VALUE_ABSENT,
                    MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryStatus.REGISTERED,
                    MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryStatus.REGISTERED), DURATION);
        }

        protected boolean checkExtraStartConditions(ServerLevel pLevel, WardenServant pOwner) {
            return pOwner.closerThan(pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0D, 20.0D);
        }

        protected boolean canStillUse(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            return true;
        }

        protected void start(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            pEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long) DURATION);
            pEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE,
                    (long) TICKS_BEFORE_PLAYING_SOUND);
            pLevel.broadcastEntityEvent(pEntity, (byte) 62);
            pEntity.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
        }

        protected void tick(ServerLevel pLevel, WardenServant pOwner, long pGameTime) {
            pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((p_289393_) -> {
                pOwner.getLookControl().setLookAt(p_289393_.position());
            });
            if (!pOwner.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_DELAY)
                    && !pOwner.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {
                pOwner.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE,
                        (long) (DURATION - TICKS_BEFORE_PLAYING_SOUND));
                pOwner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(pOwner::canTargetEntity)
                        .filter((p_217707_) -> {
                            return pOwner.closerThan(p_217707_, 15.0D, 20.0D);
                        }).ifPresent((p_217704_) -> {
                            Vec3 vec3 = pOwner.position().add(0.0D, (double) 1.6F, 0.0D);
                            Vec3 vec31 = p_217704_.getEyePosition().subtract(vec3);
                            Vec3 vec32 = vec31.normalize();

                            for (int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
                                Vec3 vec33 = vec3.add(vec32.scale((double) i));
                                pLevel.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0D, 0.0D,
                                        0.0D, 0.0D);
                            }

                            pOwner.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                            p_217704_.hurt(pLevel.damageSources().sonicBoom(pOwner), 10.0F);
                            double d1 = 0.5D * (1.0D - p_217704_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                            double d0 = 2.5D * (1.0D - p_217704_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                            p_217704_.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
                        });
            }
        }

        protected void stop(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
            WardenServantAi.setSonicBoomCooldown(pEntity, 40);
        }
    }
}