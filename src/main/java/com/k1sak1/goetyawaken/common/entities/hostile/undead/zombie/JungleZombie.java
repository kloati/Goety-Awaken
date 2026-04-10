package com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class JungleZombie extends Zombie implements Enemy {
    private static final EntityDataAccessor<Boolean> DATA_UPGRADED_ID = SynchedEntityData.defineId(JungleZombie.class,
            EntityDataSerializers.BOOLEAN);

    public JungleZombie(EntityType<? extends Zombie> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, (Double) AttributesConfig.JungleZombieServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, (double) 35.0F).add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, (Double) AttributesConfig.JungleZombieServantDamage.get())
                .add(Attributes.ARMOR, (Double) AttributesConfig.JungleZombieServantArmor.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_UPGRADED_ID, false);
    }

    public boolean isUpgraded() {
        return this.getEntityData().get(DATA_UPGRADED_ID);
    }

    public void setUpgraded(boolean upgraded) {
        this.getEntityData().set(DATA_UPGRADED_ID, upgraded);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                (Double) AttributesConfig.JungleZombieServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                (Double) AttributesConfig.JungleZombieServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                (Double) AttributesConfig.JungleZombieServantDamage.get());
    }

    protected SoundEvent getAmbientSound() {
        return (SoundEvent) ModSounds.JUNGLE_ZOMBIE_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return (SoundEvent) ModSounds.JUNGLE_ZOMBIE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return (SoundEvent) ModSounds.JUNGLE_ZOMBIE_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return (SoundEvent) ModSounds.JUNGLE_ZOMBIE_STEP.get();
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.tickCount % 5 == 0 && this.level().random.nextBoolean()) {
            double[] colors = MathHelper.rgbParticle(2735172);
            this.level().addParticle((ParticleOptions) ModParticleTypes.BIG_CULT_SPELL.get(), this.getX(),
                    this.getY() + (double) 1.0F, this.getZ(), colors[0], colors[1], colors[2]);
        }

    }

    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = super.doHurtTarget(pEntity);
        if (flag && pEntity instanceof LivingEntity livingEntity) {
            MobEffect mobEffect = MobEffects.POISON;
            if (this.isUpgraded()) {
                mobEffect = (MobEffect) GoetyEffects.ACID_VENOM.get();
            }

            livingEntity.addEffect(new MobEffectInstance(mobEffect, MathHelper.secondsToTicks(5)), this);
        }

        return flag;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsUpgraded", this.isUpgraded());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setUpgraded(pCompound.getBoolean("IsUpgraded"));
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.random.nextFloat() < 0.1F) {
            this.setUpgraded(true);
        }

        return spawnData;
    }

}