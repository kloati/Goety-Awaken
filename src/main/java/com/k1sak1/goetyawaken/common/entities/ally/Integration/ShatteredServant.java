package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.kyanite.deeperdarker.content.entities.DDMobType;
import com.kyanite.deeperdarker.content.DDSounds;
import com.kyanite.deeperdarker.content.entities.goals.DisturbanceGoal;
import com.kyanite.deeperdarker.content.entities.goals.DisturbanceListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import java.util.function.BiConsumer;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class ShatteredServant extends Summoned implements DisturbanceListener, VibrationSystem {
    public final AnimationState idleState = new AnimationState();
    public final AnimationState attackState = new AnimationState();
    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener;
    private final VibrationSystem.User vibrationUser;
    private final VibrationSystem.Data vibrationData;
    public BlockPos disturbanceLocation;

    public ShatteredServant(EntityType<? extends ShatteredServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
        this.vibrationUser = new ShatteredServant.VibrationUser();
        this.vibrationData = new VibrationSystem.Data();
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, true));
        this.goalSelector.addGoal(2, new DisturbanceGoal(this, 1.2));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.6));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ShatteredServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ShatteredServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ShatteredServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.ShatteredServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ShatteredServantFollowRange.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ShatteredServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.ShatteredServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.ShatteredServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.ShatteredServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.ShatteredServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.ShatteredServantFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.ShatteredServantArmorToughness.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.SHATTERED_SERVANT_LIMIT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return DDSounds.SHATTERED_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return DDSounds.SHATTERED_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return DDSounds.SHATTERED_HURT.get();
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
        if (this.level() instanceof ServerLevel level) {
            Ticker.tick(level, this.vibrationData, this.vibrationUser);
        }

        super.tick();

        if (level().isClientSide()) {
            if (!this.attackState.isStarted() && !this.idleState.isStarted()) {
                this.idleState.start(this.tickCount);
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
        private final PositionSource positionSource = new EntityPositionSource(ShatteredServant.this,
                ShatteredServant.this.getEyeHeight());

        @Override
        public int getListenerRadius() {
            return 16;
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
            playSound(DDSounds.SHATTERED_NOTICE.get(), 2, 1);
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
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (health != null && attack != null && speed != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.ShatteredServantHealth.get() * 1.5D);
                attack.setBaseValue(AttributesConfig.ShatteredServantDamage.get() + 4.0D);
                speed.setBaseValue(AttributesConfig.ShatteredServantMovementSpeed.get() + 0.05D);
            } else {
                health.setBaseValue(AttributesConfig.ShatteredServantHealth.get());
                attack.setBaseValue(AttributesConfig.ShatteredServantDamage.get());
                speed.setBaseValue(AttributesConfig.ShatteredServantMovementSpeed.get());
            }
        }
        this.refreshDimensions();
        this.setHealth(this.getMaxHealth());
    }
}
