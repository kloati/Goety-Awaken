package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.kyanite.deeperdarker.content.DDItems;
import com.kyanite.deeperdarker.content.entities.DDMobType;
import com.kyanite.deeperdarker.content.DDSounds;
import com.kyanite.deeperdarker.content.entities.goals.DisturbanceGoal;
import com.kyanite.deeperdarker.content.entities.goals.DisturbanceListener;
import com.kyanite.deeperdarker.util.DDDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("deprecation, NullableProblems")
public class StalkerServant extends Summoned implements DisturbanceListener, VibrationSystem {
    public final AnimationState idleState = new AnimationState();
    public final AnimationState attackState = new AnimationState();
    public final AnimationState ringAttackState = new AnimationState();
    public final AnimationState emergeState = new AnimationState();

    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener;
    private final VibrationSystem.User vibrationUser;
    private final VibrationSystem.Data vibrationData;
    public BlockPos disturbanceLocation;
    private int emergingTime;
    private int rangedCooldown = 440;

    public StalkerServant(EntityType<? extends StalkerServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
        this.vibrationUser = new StalkerServant.VibrationUser();
        this.vibrationData = new VibrationSystem.Data();
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity pAttackTarget) {
                return 8.0 + pAttackTarget.getBbWidth();
            }
        });
        this.goalSelector.addGoal(2, new DisturbanceGoal(this, 1.1));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.StalkerServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.StalkerServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.StalkerServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.StalkerServantArmor.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.StalkerServantKnockbackResistance.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.StalkerServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.StalkerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.StalkerServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.StalkerServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.StalkerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE),
                AttributesConfig.StalkerServantKnockbackResistance.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.StalkerServantArmorToughness.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.STALKER_SERVANT_LIMIT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return DDSounds.STALKER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return DDSounds.STALKER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return DDSounds.STALKER_HURT.get();
    }

    @Override
    public MobType getMobType() {
        return DDMobType.SCULK;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        return super.doHurtTarget(pEntity);
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel level) {
            Ticker.tick(level, this.vibrationData, this.vibrationUser);
        }

        super.tick();

        if (this.getPose() == Pose.EMERGING) {
            if (++emergingTime > 70)
                this.setPose(Pose.STANDING);
            this.setTarget(null);
        }

        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(10, 8, 10),
                entity -> entity != this && !MobUtil.areAllies(this, entity));
        if (!entities.isEmpty()) {
            this.rangedCooldown--;
            if (this.rangedCooldown < -200) {
                if (level().isClientSide())
                    this.ringAttackState.stop();
                this.rangedCooldown = 440;
            } else if (this.rangedCooldown < 0 && !level().isClientSide()) {
                for (LivingEntity entity : entities) {
                    entity.hurt(DDDamageTypes.source(this.level(), DDDamageTypes.RING, entity, this), 2);
                }
                if (this.rangedCooldown % 40 == 0 && level() instanceof ServerLevel serverLevel) {
                    int spawn = this.random.nextIntBetweenInclusive(1, 3);
                    for (int i = 0; i < spawn; i++) {
                        BlockPos spawnPos = new BlockPos((int) getRandomX(5), (int) getRandomY(), (int) getRandomZ(5));
                        SculkLeechServant leech = (SculkLeechServant) ModIntegrationRegistry.SCULK_LEECH_SERVANT.get()
                                .spawn(serverLevel, spawnPos, MobSpawnType.EVENT);
                        if (leech != null) {
                            leech.setTrueOwner(this);
                        }
                    }
                }
            }
        } else if (this.rangedCooldown < 0)
            this.rangedCooldown--;

        if (level().isClientSide()) {
            if (!this.idleState.isStarted() && !this.attackState.isStarted() && !this.ringAttackState.isStarted()) {
                this.idleState.start(this.tickCount);
            }

            if (this.rangedCooldown == 0) {
                this.idleState.stop();
                this.attackState.stop();
                this.ringAttackState.start(this.tickCount);
            }

            if (this.getPose() == Pose.EMERGING) {
                double sX = this.random.nextGaussian() * 0.02;
                double sY = this.random.nextGaussian() * 0.02;
                double sZ = this.random.nextGaussian() * 0.02;
                level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockStateOn()), getRandomX(1),
                        getY(), getRandomZ(1), sX, sY, sZ);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.idleState.stop();
            this.attackState.start(this.tickCount);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (pKey.equals(DATA_POSE)) {
            if (this.getPose() == Pose.EMERGING)
                this.emergeState.start(this.tickCount);
            if (this.getPose() == Pose.STANDING)
                this.emergeState.stop();
        }

        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.TRIGGERED)
            this.setPose(Pose.EMERGING);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> pListenerConsumer) {
        if (this.level() instanceof ServerLevel level) {
            pListenerConsumer.accept(this.dynamicGameEventListener, level);
        }
    }

    public boolean canTargetEntity(Entity target) {
        if (target instanceof LivingEntity entity) {
            return this.level() == target.level() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)
                    && !this.isAlliedTo(target) && entity.getType() != EntityType.ARMOR_STAND
                    && !entity.isInvulnerable()
                    && !entity.isDeadOrDying() && this.level().getWorldBorder().isWithinBounds(entity.getBoundingBox());
        }

        return false;
    }

    @Override
    public BlockPos getDisturbanceLocation() {
        return this.disturbanceLocation;
    }

    @Override
    public void setDisturbanceLocation(BlockPos disturbancePos) {
        this.disturbanceLocation = disturbancePos;
    }

    @Override
    public Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public User getVibrationUser() {
        return this.vibrationUser;
    }

    class VibrationUser implements VibrationSystem.User {
        private final PositionSource positionSource = new EntityPositionSource(StalkerServant.this,
                StalkerServant.this.getEyeHeight());

        @Override
        public int getListenerRadius() {
            return 20;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.WARDEN_CAN_LISTEN;
        }

        @Override
        public boolean canTriggerAvoidVibration() {
            return true;
        }

        @Override
        public boolean canReceiveVibration(ServerLevel pLevel, BlockPos pPos, GameEvent pGameEvent,
                GameEvent.Context pContext) {
            if (!isNoAi() && !isDeadOrDying() && !getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN)
                    && pLevel.getWorldBorder().isWithinBounds(pPos)) {
                if (pContext.sourceEntity() instanceof LivingEntity target)
                    return canTargetEntity(target);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onReceiveVibration(ServerLevel pLevel, BlockPos pPos, GameEvent pGameEvent, Entity pEntity,
                Entity pPlayerEntity, float pDistance) {
            if (isDeadOrDying())
                return;
            playSound(DDSounds.STALKER_NOTICE.get(), 2, 1);
            if (pEntity != null && canTargetEntity(pEntity)) {
                if (pEntity instanceof LivingEntity target && target.getMobType() != DDMobType.SCULK)
                    setTarget(target);
                return;
            }

            if (getTarget() != null)
                setTarget(null);
            disturbanceLocation = pPos;
        }
    }

    @Override
    public void die(DamageSource cause) {
        if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
            ItemStack crystal = new ItemStack(DDItems.SOUL_CRYSTAL.get());
            if (this.getTrueOwner() != null) {
                FlyingItem flyingItem = new FlyingItem(
                        com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                        this.level(),
                        this.getX(),
                        this.getY() + 1.0D,
                        this.getZ());
                flyingItem.setOwner(this.getTrueOwner());
                flyingItem.setItem(crystal);
                flyingItem.setParticle(ParticleTypes.ENCHANT);
                flyingItem.setSecondsCool(30);
                this.level().addFreshEntity(flyingItem);
            } else {
                ItemEntity itemEntity = this.spawnAtLocation(crystal);
                if (itemEntity != null) {
                    itemEntity.setExtendedLifetime();
                }
            }
        }
        super.die(cause);
    }

}
