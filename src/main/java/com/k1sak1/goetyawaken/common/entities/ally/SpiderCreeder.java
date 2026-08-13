package com.k1sak1.goetyawaken.common.entities.ally;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ai.path.ModClimberNavigation;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

import java.util.EnumSet;

public class SpiderCreeder extends AbstractCreeperServant {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(SpiderCreeder.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(SpiderCreeder.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(SpiderCreeder.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(SpiderCreeder.class,
            EntityDataSerializers.BYTE);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState swellAnimationState = new AnimationState();

    public SpiderCreeder(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.explosionRadius = 4;
        this.moveControl = new net.minecraft.world.entity.ai.control.MoveControl(this);
    }

    @Override
    protected EntityDataAccessor<Integer> getSwellDirAccessor() {
        return DATA_SWELL_DIR;
    }

    @Override
    protected EntityDataAccessor<Boolean> getPoweredAccessor() {
        return DATA_IS_POWERED;
    }

    @Override
    protected EntityDataAccessor<Boolean> getIgnitedAccessor() {
        return DATA_IS_IGNITED;
    }

    @Override
    protected void onSwellTargetApproach(LivingEntity target) {
        this.getNavigation().stop();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new ModClimberNavigation(this, level);
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean climbing) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }
        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SpiderCreederSwellGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        super.registerGoals();
    }

    public void ignite() {
        this.entityData.set(getIgnitedAccessor(), true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.getBoolean("ignited")) {
            this.ignite();
        }
        if (pCompound.getBoolean("powered")) {
            this.setPowered(true);
        }
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SpiderCreederHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, AttributesConfig.SpiderCreederArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.SpiderCreederArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.SpiderCreederHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SpiderCreederArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.SpiderCreederArmorToughness.get());
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CREEDER_IDLE.get();
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.SPIDER_STEP, 1.0F, 1.0F);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            this.climb();
        }
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            int i = this.getSwellDir();
            if (i > 0 && this.swell == 0) {
                this.playSound(ModSounds.CREEDER_HISS.get(), 1.0F, 0.5F);
            }

            this.swell += i;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
            this.idleAnimationState.startIfStopped(this.tickCount);
            if (this.swell > 0) {
                this.swellAnimationState.startIfStopped(this.tickCount);
            } else {
                this.swellAnimationState.stop();
            }

            float speed = (float) Math.sqrt(this.getDeltaMovement().horizontalDistanceSqr());
            if (speed > 0.01F) {
                this.walkAnimationState.startIfStopped(this.tickCount);
                this.idleAnimationState.stop();
            } else {
                this.walkAnimationState.stop();
            }
        }

        super.tick();
    }

    public void climb() {
        if (this.horizontalCollision && this.getTarget() != null) {
            double horizontalDistance = Math.sqrt(
                    Math.pow(this.getX() - this.getTarget().getX(), 2) +
                            Math.pow(this.getZ() - this.getTarget().getZ(), 2));

            if (horizontalDistance <= 5.0D && this.getTarget().getY() > this.getY()) {
                BlockPos climbPos = this.getClimbPos();
                if (climbPos != null) {
                    this.getMoveControl().setWantedPosition(
                            climbPos.getX() + 0.5D,
                            climbPos.getY(),
                            climbPos.getZ() + 0.5D,
                            1.0D);
                    this.setClimbing(true);
                }
            }
        } else {
            this.setClimbing(false);
        }
    }

    private int getClimbHeight() {
        int height = 0;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int y = 1; y <= 8; y++) {
            mutablePos.set(this.blockPosition().getX(), this.blockPosition().getY() + y, this.blockPosition().getZ());
            BlockState state = this.level().getBlockState(mutablePos);
            if (!state.blocksMotion() && !this.isStuckAtCeiling()) {
                height = y;
            } else {
                break;
            }
        }
        return height;
    }

    private boolean isClimbable(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        return state.blocksMotion() && !state.is(Blocks.AIR);
    }

    private BlockPos getClimbPos() {
        BlockPos targetPos = this.getClimbablePos();
        if (targetPos != null) {
            int climbHeight = this.getClimbHeight();
            if (climbHeight > 0) {
                return targetPos.above(climbHeight);
            }
        }
        return null;
    }

    private BlockPos getClimbablePos() {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        BlockPos entityPos = this.blockPosition();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    mutablePos.set(entityPos.getX() + x, entityPos.getY(), entityPos.getZ() + z);
                    if (this.isClimbable(mutablePos)) {
                        return mutablePos.immutable();
                    }
                }
            }
        }
        return null;
    }

    private boolean isStuckAtCeiling() {
        BlockPos above = this.blockPosition().above(2);
        return this.level().getBlockState(above).blocksMotion() && this.getDeltaMovement().y() <= 0.01D;
    }

    @Override
    public int getSwell() {
        return this.swell;
    }

    @Override
    protected void explodeCreeper() {
        if (!this.level().isClientSide) {
            int actualExplosionRadius = this.isPowered() ? this.explosionRadius * 2 : this.explosionRadius;
            LootingExplosion explosion = new LootingExplosion(this.level(), this, this.getX(), this.getY(),
                    this.getZ(), (float) actualExplosionRadius, false,
                    this.getBlockInteraction(), LootingExplosion.Mode.LOOT);
            explosion.explode();
            explosion.finalizeExplosion(true);
            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0xff0000);
                Vec3 vec3 = this.position();
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                actualExplosionRadius * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                actualExplosionRadius, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                actualExplosionRadius * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                actualExplosionRadius * 2.0F, 1),
                        vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);
                for (int i = 0; i < 100; i++) {
                    double offsetX = (this.random.nextDouble() - 0.5D) * actualExplosionRadius * 2;
                    double offsetY = (this.random.nextDouble() - 0.5D) * actualExplosionRadius * 2;
                    double offsetZ = (this.random.nextDouble() - 0.5D) * actualExplosionRadius * 2;
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LAVA,
                            this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                            1, 0, 0, 0, 0);
                }

                AABB explosionBox = this.getBoundingBox().inflate(actualExplosionRadius);
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, explosionBox)) {
                    if (entity != this && entity.distanceTo(this) <= actualExplosionRadius
                            && !MobUtil.areAllies(this, entity)) {
                        entity.addEffect(
                                new MobEffectInstance(GoetyEffects.FLAMMABLE.get(), MathHelper.secondsToTicks(30), 0));
                        entity.addEffect(
                                new MobEffectInstance(GoetyEffects.WANE.get(), MathHelper.secondsToTicks(5), 0));
                        entity.setSecondsOnFire(5);
                    }
                }
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 1.0F, 1.0F);
            this.discard();
        }
    }

    @Override
    protected void spawnExplosionParticles(ServerLevel serverLevel, float explosionRadius) {
    }

    @Override
    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
        if (!this.level().isClientSide) {
            this.setPowered(true);
        }
        if (this.level().isClientSide) {
            for (int i = 0; i < 10; ++i) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
                        this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        (this.random.nextDouble() - 0.5D) * 0.5D,
                        (this.random.nextDouble() - 0.5D) * 0.5D,
                        (this.random.nextDouble() - 0.5D) * 0.5D);
            }
        }
        super.thunderHit(p_19927_, p_19928_);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.CREEPER_SERVANT_LIMIT.get();
    }

    class SpiderCreederSwellGoal extends Goal {
        @Nullable
        private LivingEntity target;

        public SpiderCreederSwellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            target = SpiderCreeder.this.getTarget();
            return SpiderCreeder.this.getSwellDir() > 0
                    || (target != null && SpiderCreeder.this.distanceToSqr(target) < 16.0D);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.target == null) {
                return false;
            } else if (!this.target.isAlive()) {
                return false;
            } else if (SpiderCreeder.this.distanceToSqr(this.target) > 64.0D) {
                return false;
            } else {
                return SpiderCreeder.this.getSwellDir() > 0 || SpiderCreeder.this.swell > 0;
            }
        }

        @Override
        public void start() {
            SpiderCreeder.this.getNavigation().stop();
            this.target = SpiderCreeder.this.getTarget();
        }

        @Override
        public void stop() {
            this.target = null;
            SpiderCreeder.this.setSwellDir(-1);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.target == null) {
                SpiderCreeder.this.setSwellDir(-1);
            } else if (SpiderCreeder.this.distanceToSqr(this.target) > 49.0D) {
                SpiderCreeder.this.setSwellDir(-1);
            } else if (!SpiderCreeder.this.getSensing().hasLineOfSight(this.target)) {
                SpiderCreeder.this.setSwellDir(-1);
            } else {
                SpiderCreeder.this.getNavigation().stop();
                SpiderCreeder.this.setSwellDir(1);
            }
        }
    }
}
