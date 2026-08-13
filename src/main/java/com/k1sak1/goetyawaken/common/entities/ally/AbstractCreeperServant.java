package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

import java.util.EnumSet;

public abstract class AbstractCreeperServant extends Summoned implements ICustomAttributes {
    protected int oldSwell;
    protected int swell;
    protected int maxSwell = 30;
    protected int explosionRadius = 3;

    protected AbstractCreeperServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected abstract EntityDataAccessor<Integer> getSwellDirAccessor();

    protected abstract EntityDataAccessor<Boolean> getPoweredAccessor();

    protected abstract EntityDataAccessor<Boolean> getIgnitedAccessor();

    protected LootingExplosion.BlockInteraction getBlockInteraction() {
        return LootingExplosion.BlockInteraction.KEEP;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(getSwellDirAccessor(), -1);
        this.entityData.define(getPoweredAccessor(), false);
        this.entityData.define(getIgnitedAccessor(), false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CreeperSwellGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));

        super.registerGoals();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.entityData.get(getPoweredAccessor())) {
            compound.putBoolean("powered", true);
        }
        compound.putShort("Fuse", (short) this.maxSwell);
        compound.putByte("ExplosionRadius", (byte) this.explosionRadius);
        compound.putBoolean("ignited", this.entityData.get(getIgnitedAccessor()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setConfigurableAttributes();
        this.entityData.set(getPoweredAccessor(), compound.getBoolean("powered"));
        if (compound.contains("Fuse", 99)) {
            this.maxSwell = compound.getShort("Fuse");
        }
        if (compound.contains("ExplosionRadius", 99)) {
            this.explosionRadius = compound.getByte("ExplosionRadius");
        }
        if (compound.contains("ignited", 99)) {
            this.entityData.set(getIgnitedAccessor(), compound.getBoolean("ignited"));
        }
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

            this.spawnSwellParticles();

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

    protected void spawnSwellParticles() {
        if (this.swell > 0 && this.swell % 5 == 0 && this.level().isClientSide) {
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.ENTITY_EFFECT,
                    this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                    this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                    this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                    (this.isPowered() ? 0.5D : 1.0D), 0.0D, 0.0D);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    public int getSwell() {
        return this.swell;
    }

    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, (float) this.oldSwell, (float) this.swell) / (float) (this.maxSwell - 2);
    }

    public int getSwellDir() {
        return this.entityData.get(getSwellDirAccessor());
    }

    public void setSwellDir(int state) {
        this.entityData.set(getSwellDirAccessor(), state);
    }

    public boolean isPowered() {
        return this.entityData.get(getPoweredAccessor());
    }

    public void setPowered(boolean powered) {
        this.entityData.set(getPoweredAccessor(), powered);
    }

    public boolean isIgnited() {
        return this.entityData.get(getIgnitedAccessor());
    }

    public void setIgnited(boolean ignited) {
        this.entityData.set(getIgnitedAccessor(), ignited);
    }

    @Override
    public boolean isPushable() {
        return !this.isIgnited();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
        this.entityData.set(getPoweredAccessor(), true);
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

    protected void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            float explosionRadius = (this.explosionRadius - 1) * f;

            LootingExplosion explosion = new LootingExplosion(this.level(), this, this.getX(), this.getY(),
                    this.getZ(), explosionRadius, false,
                    this.getBlockInteraction(), LootingExplosion.Mode.LOOT);
            explosion.explode();
            explosion.finalizeExplosion(true);

            applyExplosionEffects(explosionRadius);

            if (this.level() instanceof ServerLevel serverLevel) {
                spawnExplosionParticles(serverLevel, explosionRadius);
            }

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 1.0F, 1.0F);

            this.discard();
        }
    }

    protected void applyExplosionEffects(float explosionRadius) {
        AABB explosionBox = this.getBoundingBox().inflate(explosionRadius);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, explosionBox)) {
            if (entity != this && entity.distanceTo(this) <= explosionRadius && !MobUtil.areAllies(this, entity)) {
                applyEffectToEntity(entity);
            }
        }
    }

    protected void applyEffectToEntity(LivingEntity entity) {
    }

    protected abstract void spawnExplosionParticles(ServerLevel serverLevel, float explosionRadius);

    @Override
    public MobType getMobType() {
        return com.Polarice3.Goety.init.ModMobType.NATURAL;
    }

    protected class CreeperSwellGoal extends Goal {
        public CreeperSwellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = AbstractCreeperServant.this.getTarget();
            return AbstractCreeperServant.this.getSwellDir() > 0
                    || (livingentity != null && AbstractCreeperServant.this.distanceToSqr(livingentity) < 9.0D);
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
            LivingEntity target = AbstractCreeperServant.this.getTarget();
            if (target == null) {
                AbstractCreeperServant.this.setSwellDir(-1);
            } else if (AbstractCreeperServant.this.distanceToSqr(target) > 49.0D) {
                AbstractCreeperServant.this.setSwellDir(-1);
            } else if (!AbstractCreeperServant.this.getSensing().hasLineOfSight(target)) {
                AbstractCreeperServant.this.setSwellDir(-1);
            } else {
                onSwellTargetApproach(target);
                AbstractCreeperServant.this.setSwellDir(1);
            }
        }
    }

    protected void onSwellTargetApproach(LivingEntity target) {
        this.getNavigation().moveTo(target, 1.0D);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.creeperServantLimit;
    }
}
