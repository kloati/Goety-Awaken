package com.k1sak1.goetyawaken.common.entities.ally;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class JITBZombieServant extends ZombieServant {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(
            JITBZombieServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(
            JITBZombieServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(
            JITBZombieServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(JITBZombieServant.class,
            EntityDataSerializers.BYTE);

    private int oldSwell;
    private int swell;
    private int maxSwell = 20;
    private int explosionRadius = 3;
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState swellAnimationState = new AnimationState();
    public final AnimationState musicAnimationState = new AnimationState();

    @OnlyIn(Dist.CLIENT)
    private com.k1sak1.goetyawaken.client.audio.JITBZombieMusicLoop musicLoop;

    public JITBZombieServant(EntityType<? extends ZombieServant> type, Level worldIn) {
        super(type, worldIn);
    }

    protected LootingExplosion.BlockInteraction getBlockInteraction() {
        return LootingExplosion.BlockInteraction.KEEP;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SWELL_DIR, -1);
        this.entityData.define(DATA_IS_IGNITED, false);
        this.entityData.define(DATA_IS_POWERED, false);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new JITBZombieServantSwellGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        super.registerGoals();
    }

    @Override
    public void attackGoal() {
    }

    public boolean isBaby() {
        return false;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.JITBZombieServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, AttributesConfig.JITBZombieServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.JITBZombieServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.JITBZombieServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.JITBZombieServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.JITBZombieServantArmorToughness.get());
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            int i = this.getSwellDir();
            if (i > 0 && this.swell == 0) {
                this.playSound(ModSounds.JITBZOMBIE_BOING.get(), 1.0F, 1.0F);
            }

            this.swell += i;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeJITBZombieServant();
            }
            if (this.swell > 0) {
                this.swellAnimationState.startIfStopped(this.tickCount);
            } else {
                this.swellAnimationState.stop();
            }

            float speed = (float) Math.sqrt(this.getDeltaMovement().horizontalDistanceSqr());
            if (speed > 0.01F) {
                this.walkAnimationState.startIfStopped(this.tickCount);
            } else {
                this.walkAnimationState.stop();
            }

            if (this.isAggressive()) {
                this.musicAnimationState.startIfStopped(this.tickCount);
                if (this.level().isClientSide) {
                    manageMusicLoop();
                }
            } else {
                this.musicAnimationState.stop();
                if (this.level().isClientSide && this.musicLoop != null) {
                    this.musicLoop = null;
                }
            }
        }

        super.tick();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Fuse", 99)) {
            this.maxSwell = pCompound.getShort("Fuse");
        }

        if (pCompound.contains("ExplosionRadius", 99)) {
            this.explosionRadius = pCompound.getByte("ExplosionRadius");
        }

        if (pCompound.getBoolean("ignited")) {
            this.ignite();
        }

        if (pCompound.getBoolean("powered")) {
            this.setPowered(true);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putShort("Fuse", (short) this.maxSwell);
        pCompound.putByte("ExplosionRadius", (byte) this.explosionRadius);
        pCompound.putBoolean("ignited", this.isIgnited());
        pCompound.putBoolean("powered", this.isPowered());
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int pState) {
        this.entityData.set(DATA_SWELL_DIR, pState);
    }

    public boolean isIgnited() {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    public void ignite() {
        this.entityData.set(DATA_IS_IGNITED, true);
    }

    public boolean isPowered() {
        return this.entityData.get(DATA_IS_POWERED);
    }

    public void setPowered(boolean powered) {
        this.entityData.set(DATA_IS_POWERED, powered);
    }

    public int getSwell() {
        return this.swell;
    }

    public float getSwelling(float pPartialTicks) {
        return Mth.lerp(pPartialTicks, (float) this.oldSwell, (float) this.swell) / (float) (this.maxSwell - 2);
    }

    public void explodeJITBZombieServant() {
        if (!this.level().isClientSide) {
            int actualExplosionRadius = this.isPowered() ? this.explosionRadius * 2 : this.explosionRadius;
            LootingExplosion explosion = new LootingExplosion(this.level(), this, this.getX(), this.getY(),
                    this.getZ(), (float) actualExplosionRadius, false,
                    this.getBlockInteraction(), LootingExplosion.Mode.LOOT);
            explosion.explode();
            explosion.finalizeExplosion(true);
            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil whiteUtil = new ColorUtil(0xFFFFFF);
                ColorUtil purpleUtil = new ColorUtil(0x8000FF);
                Vec3 vec3 = this.position();
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(whiteUtil.red(), whiteUtil.green(), whiteUtil.blue(),
                                actualExplosionRadius * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(whiteUtil.red(), whiteUtil.green(), whiteUtil.blue(),
                                actualExplosionRadius, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new VerticalCircleExplodeParticleOption(whiteUtil.red(), whiteUtil.green(), whiteUtil.blue(),
                                actualExplosionRadius * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(purpleUtil.red(), purpleUtil.green(), purpleUtil.blue(),
                                actualExplosionRadius * 2.0F, 1),
                        vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);
                AABB explosionBox = this.getBoundingBox().inflate(actualExplosionRadius);
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, explosionBox)) {
                    if (entity != this && entity.distanceTo(this) <= actualExplosionRadius
                            && !MobUtil.areAllies(this, entity)) {
                        entity.addEffect(
                                new MobEffectInstance(GoetyEffects.SAPPED.get(), MathHelper.secondsToTicks(10), 0));
                    }
                }
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.JITBZOMBIE_EXPLOSION.get(), this.getSoundSource(), 1.0F, 1.0F);
            this.discard();
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

    static class JITBZombieServantSwellGoal extends Goal {
        private final JITBZombieServant jITBZombieServant;
        @Nullable
        private LivingEntity target;

        public JITBZombieServantSwellGoal(JITBZombieServant pJITBZombieServant) {
            this.jITBZombieServant = pJITBZombieServant;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            target = this.jITBZombieServant.getTarget();
            return this.jITBZombieServant.getSwellDir() > 0
                    || (target != null && this.jITBZombieServant.distanceToSqr(target) < 16.0D);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.jITBZombieServant.swell > 0) {
                return true;
            }
            if (this.target == null) {
                return false;
            } else if (!this.target.isAlive()) {
                return false;
            } else {
                return this.jITBZombieServant.getSwellDir() > 0;
            }
        }

        @Override
        public void start() {
            this.jITBZombieServant.getNavigation().stop();
            this.target = this.jITBZombieServant.getTarget();
        }

        @Override
        public void stop() {
            this.target = null;
            this.jITBZombieServant.setSwellDir(-1);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.jITBZombieServant.swell > 0) {
                this.jITBZombieServant.getNavigation().stop();
                this.jITBZombieServant.setSwellDir(1);
            } else if (this.target == null) {
                this.jITBZombieServant.setSwellDir(-1);
            } else if (this.jITBZombieServant.distanceToSqr(this.target) > 49.0D) {
                this.jITBZombieServant.setSwellDir(-1);
            } else if (!this.jITBZombieServant.getSensing().hasLineOfSight(this.target)) {
                this.jITBZombieServant.setSwellDir(-1);
            } else {
                this.jITBZombieServant.getNavigation().stop();
                this.jITBZombieServant.setSwellDir(1);
            }
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.CREEPER_SERVANT_LIMIT.get();
    }

    @OnlyIn(Dist.CLIENT)
    private void manageMusicLoop() {
        if (this.musicLoop != null && !this.musicLoop.isStopped()) {
            return;
        }

        this.musicLoop = new com.k1sak1.goetyawaken.client.audio.JITBZombieMusicLoop(
                ModSounds.JITBZOMBIE_MUSIC.get(), this);
        Minecraft.getInstance().getSoundManager().play(this.musicLoop);
    }
}
