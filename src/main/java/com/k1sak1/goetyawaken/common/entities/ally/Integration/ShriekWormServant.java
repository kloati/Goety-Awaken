package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import java.util.List;
import com.kyanite.deeperdarker.content.entities.DDMobType;
import com.kyanite.deeperdarker.content.DDSounds;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("deprecation, NullableProblems")
public class ShriekWormServant extends Summoned {
    public final AnimationState idleState = new AnimationState();
    public final AnimationState attackState = new AnimationState();
    public final AnimationState asleepState = new AnimationState();
    public final AnimationState emergeState = new AnimationState();
    public final AnimationState descendState = new AnimationState();
    private static final EntityDataAccessor<Boolean> DATA_ASLEEP = SynchedEntityData.defineId(ShriekWormServant.class,
            EntityDataSerializers.BOOLEAN);
    private int emergingTime;
    private int idleTime;

    public ShriekWormServant(EntityType<? extends ShriekWormServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ASLEEP, false);
    }

    public boolean isAsleep() {
        return this.entityData.get(DATA_ASLEEP);
    }

    public void setAsleep(boolean asleep) {
        this.entityData.set(DATA_ASLEEP, asleep);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Asleep", this.isAsleep());
        tag.putInt("IdleTime", this.idleTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Asleep")) {
            this.setAsleep(tag.getBoolean("Asleep"));
        }
        if (tag.contains("IdleTime")) {
            this.idleTime = tag.getInt("IdleTime");
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0, true) {
            @Override
            protected double getAttackReachSqr(LivingEntity pAttackTarget) {
                return 25.0 + pAttackTarget.getBbWidth();
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ShriekWormServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ShriekWormServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ShriekWormServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.ShriekWormServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ShriekWormServantArmorToughness.get())
                .add(Attributes.ATTACK_KNOCKBACK, 0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.ShriekWormServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.ShriekWormServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.ShriekWormServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.ShriekWormServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.ShriekWormServantArmorToughness.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.SHRIEK_WORM_SERVANT_LIMIT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return DDSounds.SHRIEK_WORM_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return DDSounds.SHRIEK_WORM_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return DDSounds.SHRIEK_WORM_HURT.get();
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
        super.tick();

        if (this.getPose() == Pose.EMERGING && ++emergingTime > 80)
            this.setPose(Pose.STANDING);

        if (this.getPose() == Pose.STANDING && !this.isAsleep()) {
            this.idleTime++;
            if (this.idleTime > 200) {
                this.idleTime = 0;
                this.setAsleep(true);
            }
        }

        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(5),
                entity -> entity != this && !MobUtil.areAllies(this, entity));
        if (!entities.isEmpty()) {
            this.setAsleep(false);
        } else {
            if (this.attackState.isStarted() && !this.idleState.isStarted()) {
                this.attackState.stop();
                this.idleState.start(this.tickCount);
            }
        }

        if (level().isClientSide()) {
            if (this.isAsleep() && !this.asleepState.isStarted()) {
                this.idleState.stop();
                this.attackState.stop();
                this.asleepState.start(this.tickCount);
            }
            if (!this.isAsleep() && !this.idleState.isStarted() && !this.emergeState.isStarted()
                    && !this.descendState.isStarted() && !this.attackState.isStarted()) {
                this.asleepState.stop();
                this.idleState.start(this.tickCount);
            }

            if (this.getPose() == Pose.EMERGING) {
                double sX = this.random.nextGaussian() * 0.02;
                double sY = this.random.nextGaussian() * 0.02;
                double sZ = this.random.nextGaussian() * 0.02;
                level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockStateOn()), getRandomX(1),
                        getY() + 1, getRandomZ(1), sX, sY, sZ);
            }
        }

        if (this.isAsleep())
            setBoundingBox(new AABB(this.position().x - 0.5, this.position().y, this.position().z - 0.5,
                    this.position().x + 0.5, this.position().y + 1.6, this.position().z + 0.5));
        else
            setBoundingBox(new AABB(this.position().x - 0.5, this.position().y, this.position().z - 0.5,
                    this.position().x + 0.5, this.position().y + 5.7, this.position().z + 0.5));

    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.idleState.stop();
            this.asleepState.stop();
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
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public void knockback(double pStrength, double pX, double pZ) {
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean canUpdateMove() {
        return false;
    }

    @Override
    public boolean isCommanded() {
        return false;
    }

    @Override
    public boolean canBeCommanded() {
        return false;
    }

    @Override
    public boolean canStay() {
        return true;
    }

    @Override
    public boolean canWander() {
        return false;
    }

    @Override
    public boolean canGuardArea() {
        return false;
    }

    @Override
    public boolean isFollowing() {
        return false;
    }

    @Override
    public boolean canFollow() {
        return false;
    }

}
