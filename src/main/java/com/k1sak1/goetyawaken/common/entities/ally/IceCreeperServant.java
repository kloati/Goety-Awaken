package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import java.util.EnumSet;

public class IceCreeperServant extends Summoned implements ICustomAttributes {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(
            IceCreeperServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(
            IceCreeperServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(
            IceCreeperServant.class,
            EntityDataSerializers.BOOLEAN);
    private int oldSwell;
    private int swell;
    private int maxSwell = 30;
    private int explosionRadius = 3;

    public IceCreeperServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected LootingExplosion.BlockInteraction getBlockInteraction() {
        return LootingExplosion.BlockInteraction.KEEP;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SWELL_DIR, -1);
        this.entityData.define(DATA_IS_POWERED, false);
        this.entityData.define(DATA_IS_IGNITED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new IceCreeperSwellGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));

        super.registerGoals();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.IceCreeperServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ARMOR, AttributesConfig.IceCreeperServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.IceCreeperServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.IceCreeperServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 0.5D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.IceCreeperServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.IceCreeperServantArmorToughness.get());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.entityData.get(DATA_IS_POWERED)) {
            compound.putBoolean("powered", true);
        }
        compound.putShort("Fuse", (short) this.maxSwell);
        compound.putByte("ExplosionRadius", (byte) this.explosionRadius);
        compound.putBoolean("ignited", this.entityData.get(DATA_IS_IGNITED));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        return pSpawnData;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setConfigurableAttributes();
        this.entityData.set(DATA_IS_POWERED, compound.getBoolean("powered"));
        if (compound.contains("Fuse", 99)) {
            this.maxSwell = compound.getShort("Fuse");
        }
        if (compound.contains("ExplosionRadius", 99)) {
            this.explosionRadius = compound.getByte("ExplosionRadius");
        }
        if (compound.contains("ignited", 99)) {
            this.entityData.set(DATA_IS_IGNITED, compound.getBoolean("ignited"));
        }
    }

    @Override
    public int getMaxFallDistance() {
        return this.getTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;

            if (this.isIgnited()) {
                this.setSwellDir(1);
            }

            int i = this.getSwellDir();
            this.swell += i;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.level().isClientSide) {
                if (this.tickCount % 5 == 0) {
                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                            this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                            this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                            this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                            (this.random.nextDouble() - 0.5D) * 0.1D,
                            (this.random.nextDouble() - 0.5D) * 0.1D,
                            (this.random.nextDouble() - 0.5D) * 0.1D);
                }

                if (this.swell > 0 && this.swell % 5 == 0) {
                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                            this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                            this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                            this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                            (this.isPowered() ? 0.5D : 1.0D), 0.0D, 0.0D);
                }
            }

            if (this.swell >= this.maxSwell - 10 && this.swell < this.maxSwell - 5) {
                if (!this.level().isClientSide) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.CREEPER_PRIMED, this.getSoundSource(), 1.0F, 1.0F);
                }
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
        }
        super.tick();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, (float) this.oldSwell, (float) this.swell) / (float) (this.maxSwell - 2);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int state) {
        this.entityData.set(DATA_SWELL_DIR, state);
    }

    public boolean isPowered() {
        return this.entityData.get(DATA_IS_POWERED);
    }

    public boolean isIgnited() {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    public void setIgnited(boolean ignited) {
        this.entityData.set(DATA_IS_IGNITED, ignited);
    }

    private void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            float explosionRadius = (this.explosionRadius - 1) * f;

            LootingExplosion explosion = new LootingExplosion(this.level(), this, this.getX(), this.getY(),
                    this.getZ(), explosionRadius, false,
                    this.getBlockInteraction(), LootingExplosion.Mode.LOOT);
            explosion.explode();
            explosion.finalizeExplosion(true);

            AABB explosionBox = this.getBoundingBox().inflate(explosionRadius);
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, explosionBox)) {
                if (entity != this && entity.distanceTo(this) <= explosionRadius && !MobUtil.areAllies(this, entity)) {
                    entity.addEffect(
                            new MobEffectInstance(GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks(60), 3));
                }
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0x00a8ff);
                Vec3 vec3 = this.position();
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                explosionRadius * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                explosionRadius, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                explosionRadius * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                explosionRadius * 2.0F, 1),
                        vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);

                for (int i = 0; i < 100; i++) {
                    double offsetX = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
                    double offsetY = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
                    double offsetZ = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                            this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                            1, 0, 0, 0, 0);
                }
            }

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 1.0F, 1.0F);

            this.discard();
        }
    }

    @Override
    public boolean isPushable() {
        return !this.isIgnited();
    }

    @Override
    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
        this.entityData.set(DATA_IS_POWERED, true);
        super.thunderHit(p_19927_, p_19928_);
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
    }

    public void setPowered(boolean powered) {
        this.entityData.set(DATA_IS_POWERED, powered);
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public int getTicksFrozen() {
        return 0;
    }

    @Override
    public MobType getMobType() {
        return com.Polarice3.Goety.init.ModMobType.NATURAL;
    }

    static class IceCreeperSwellGoal extends Goal {
        private final IceCreeperServant creeper;

        public IceCreeperSwellGoal(IceCreeperServant creeper) {
            this.creeper = creeper;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.creeper.getTarget();
            return this.creeper.getSwellDir() > 0
                    || (livingentity != null && this.creeper.distanceToSqr(livingentity) < 9.0D);
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.creeper.getTarget();
            if (target == null) {
                this.creeper.setSwellDir(-1);
            } else if (this.creeper.distanceToSqr(target) > 49.0D) {
                this.creeper.setSwellDir(-1);
            } else if (!this.creeper.getSensing().hasLineOfSight(target)) {
                this.creeper.setSwellDir(-1);
            } else {
                this.creeper.getNavigation().stop();
                this.creeper.setSwellDir(1);
            }
        }
    }

    @Override
    public boolean canUpdateMove() {
        return true;
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

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.creeperServantLimit;
    }
}