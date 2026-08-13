package com.k1sak1.goetyawaken.common.entities.ally;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class StatueCreeper extends AbstractCreeperServant {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(StatueCreeper.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(StatueCreeper.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(StatueCreeper.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TIER = SynchedEntityData.defineId(StatueCreeper.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(StatueCreeper.class,
            EntityDataSerializers.INT);

    public enum StatueState {
        STATUE,
        AWAKENING,
        ACTIVE,
        EXPLODING
    }

    public final AnimationState statueAnimationState = new AnimationState();
    public final AnimationState awakenAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState explodeAnimationState = new AnimationState();

    private int awakeningTick = 0;
    private static final int AWAKEN_DURATION = 30;

    public StatueCreeper(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.explosionRadius = 2;
        this.maxSwell = 45;
        this.setState(StatueState.STATUE);
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TIER, 1);
        this.entityData.define(DATA_STATE, StatueState.STATUE.ordinal());
    }

    public int getTier() {
        return this.entityData.get(DATA_TIER);
    }

    public void setTier(int tier) {
        this.entityData.set(DATA_TIER, Math.min(3, Math.max(1, tier)));
        this.updateAttributesForTier();
    }

    public StatueState getState() {
        return StatueState.values()[this.entityData.get(DATA_STATE)];
    }

    private void setState(StatueState state) {
        this.entityData.set(DATA_STATE, state.ordinal());
    }

    public boolean isActivated() {
        StatueState state = this.getState();
        return state == StatueState.ACTIVE || state == StatueState.EXPLODING;
    }

    public void activate(int tier) {
        if (this.getState() == StatueState.STATUE) {
            this.setTier(tier);
            this.setState(StatueState.AWAKENING);
            this.awakeningTick = 0;
            if (!this.level().isClientSide) {
                this.setHealth(this.getMaxHealth());
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.getState() == StatueState.STATUE) {
            this.activate(1);
        }
        return pSpawnData;
    }

    private void updateAttributesForTier() {
        int tier = this.getTier();
        double health = AttributesConfig.StatueCreeperHealth.get() + (tier - 1) * 6.0;
        double armor = AttributesConfig.StatueCreeperArmor.get() + (tier - 1) * 2.0;
        double armorToughness = AttributesConfig.StatueCreeperArmorToughness.get() + (tier - 1) * 2.0;
        this.explosionRadius = 2 + (tier - 1);

        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), health);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), armor);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), armorToughness);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.StatueCreeperHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ARMOR, AttributesConfig.StatueCreeperArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.StatueCreeperArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        int tier = this.getTier();
        double health = AttributesConfig.StatueCreeperHealth.get() + (tier - 1) * 6.0;
        double armor = AttributesConfig.StatueCreeperArmor.get() + (tier - 1) * 2.0;
        double armorToughness = AttributesConfig.StatueCreeperArmorToughness.get() + (tier - 1) * 2.0;
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), health);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.2D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), armor);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), armorToughness);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.updateAnimationStates();
        } else {
            this.updateServerLogic();
        }
    }

    private void updateServerLogic() {
        StatueState state = this.getState();

        if (state == StatueState.AWAKENING) {
            this.awakeningTick++;
            if (this.awakeningTick >= AWAKEN_DURATION) {
                this.setState(StatueState.ACTIVE);
                this.awakeningTick = 0;
            }
        }
    }

    private void updateAnimationStates() {
        StatueState state = this.getState();

        switch (state) {
            case STATUE:
                this.statueAnimationState.startIfStopped(this.tickCount);
                this.awakenAnimationState.stop();
                this.idleAnimationState.stop();
                this.walkAnimationState.stop();
                this.explodeAnimationState.stop();
                break;

            case AWAKENING:
                this.statueAnimationState.stop();
                this.awakenAnimationState.startIfStopped(this.tickCount);
                this.idleAnimationState.stop();
                this.walkAnimationState.stop();
                this.explodeAnimationState.stop();
                break;

            case ACTIVE:
                this.statueAnimationState.stop();
                this.awakenAnimationState.stop();
                if (this.swell > 0) {
                    this.explodeAnimationState.startIfStopped(this.tickCount);
                    this.idleAnimationState.stop();
                    this.walkAnimationState.stop();
                } else {
                    this.explodeAnimationState.stop();
                    float speed = (float) Math.sqrt(this.getDeltaMovement().horizontalDistanceSqr());
                    if (speed > 0.01F) {
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.idleAnimationState.stop();
                    } else {
                        this.walkAnimationState.stop();
                        this.idleAnimationState.startIfStopped(this.tickCount);
                    }
                }
                break;

            case EXPLODING:
                this.statueAnimationState.stop();
                this.awakenAnimationState.stop();
                this.idleAnimationState.stop();
                this.walkAnimationState.stop();
                this.explodeAnimationState.startIfStopped(this.tickCount);
                break;
        }
    }

    @Override
    protected void spawnSwellParticles() {
        if (this.swell > 0 && this.swell % 2 == 0 && this.level().isClientSide) {
            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK,
                    Blocks.STONE.defaultBlockState());
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(particle,
                        this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            float explosionRadius = (float) this.explosionRadius * f;
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                    explosionRadius, Level.ExplosionInteraction.NONE);

            if (this.level() instanceof ServerLevel serverLevel) {
                int r = 0x80;
                int g = 0x80;
                int b = 0x80;
                Vec3 vec3 = this.position();
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(r, g, b, (int) explosionRadius * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(r, g, b, (int) explosionRadius, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new VerticalCircleExplodeParticleOption(r, g, b, (int) explosionRadius * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(r, g, b, explosionRadius * 2.0F, 1),
                        vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);
            }

            applyExplosionEffects(explosionRadius);

            this.discard();
        }
    }

    @Override
    protected void applyEffectToEntity(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(GoetyEffects.STUNNED.get(), 30, 0, false, false, true));
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        StatueState state = this.getState();
        if (state == StatueState.STATUE || state == StatueState.AWAKENING) {
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void push(net.minecraft.world.entity.Entity pEntity) {
        if (this.getState() != StatueState.STATUE && this.getState() != StatueState.AWAKENING) {
            super.push(pEntity);
        }
    }

    @Override
    protected boolean isImmobile() {
        StatueState state = this.getState();
        return state == StatueState.STATUE || state == StatueState.AWAKENING
                || this.swell > 0 || super.isImmobile();
    }

    @Override
    protected void onSwellTargetApproach(LivingEntity target) {
        this.getNavigation().stop();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Tier", this.getTier());
        compound.putInt("StatueState", this.getState().ordinal());
        compound.putInt("AwakeningTick", this.awakeningTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Tier")) {
            this.setTier(compound.getInt("Tier"));
        }
        if (compound.contains("StatueState")) {
            this.setState(StatueState.values()[compound.getInt("StatueState")]);
        }
        if (compound.contains("AwakeningTick")) {
            this.awakeningTick = compound.getInt("AwakeningTick");
        }
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean removeWhenFarAway(double p_27519_) {
        return false;
    }

    public boolean isPushable() {
        return this.isAlive() && !this.isSpectator() && !this.onClimbable();
    }

    @Override
    protected void spawnExplosionParticles(ServerLevel serverLevel, float explosionRadius) {
        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK,
                Blocks.STONE.defaultBlockState());
        for (int i = 0; i < 50; i++) {
            double dx = (this.random.nextDouble() - 0.5D) * explosionRadius;
            double dy = this.random.nextDouble() * explosionRadius * 0.5D;
            double dz = (this.random.nextDouble() - 0.5D) * explosionRadius;
            serverLevel.sendParticles(particle,
                    this.getX() + dx, this.getY() + dy, this.getZ() + dz,
                    1, 0.0D, 0.0D, 0.0D, 0.1D);
        }
    }

    @Nullable
    public ResourceLocation getTextureByState() {
        if (!this.isActivated()) {
            return new ResourceLocation(GoetyAwaken.MODID, "textures/entity/statue_creeper_inactive.png");
        }
        int tier = this.getTier();
        return new ResourceLocation(GoetyAwaken.MODID, "textures/entity/statue_creeper_" + tier + ".png");
    }

    @Nullable
    public ResourceLocation getGlowTextureByState() {
        if (!this.isActivated()) {
            return null;
        }
        int tier = this.getTier();
        return new ResourceLocation(GoetyAwaken.MODID, "textures/entity/statue_creeper_" + tier + "_glow.png");
    }
}
