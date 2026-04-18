package com.k1sak1.goetyawaken.common.entities.ally;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
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
import javax.annotation.Nullable;

import java.util.EnumSet;

public class CreeperServant extends Summoned implements ICustomAttributes {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(CreeperServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(CreeperServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(CreeperServant.class,
            EntityDataSerializers.BOOLEAN);
    private int oldSwell;
    private int swell;
    private int maxSwell = 30;
    private int explosionRadius = 3;

    public CreeperServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
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
        this.goalSelector.addGoal(1, new CreeperSwellGoal(this));
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
                .add(Attributes.MAX_HEALTH, AttributesConfig.CreeperServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, AttributesConfig.CreeperServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.CreeperServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.CreeperServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.CreeperServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.CreeperServantArmorToughness.get());
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

            if (this.swell > 0 && this.swell % 5 == 0 && this.level().isClientSide) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.ENTITY_EFFECT,
                        this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        (this.isPowered() ? 0.5D : 1.0D), 0.0D, 0.0D);
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
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f,
                    Level.ExplosionInteraction.NONE);
            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0x00FF00);
                float explosionSize = (float) this.explosionRadius * f;
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(colorUtil, explosionSize * 2.0F, 1),
                        this.getX(), BlockFinder.moveDownToGround(this) + 0.5F, this.getZ(),
                        1, 0, 0, 0, 0);
            }

            this.discard();
        }
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
    public MobType getMobType() {
        return com.Polarice3.Goety.init.ModMobType.NATURAL;
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

    static class CreeperSwellGoal extends Goal {
        private final CreeperServant creeper;

        public CreeperSwellGoal(CreeperServant creeper) {
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
                this.creeper.getNavigation().moveTo(target, 1.0D);
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