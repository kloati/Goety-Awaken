package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.k1sak1.goetyawaken.common.entities.ally.ShulkerServant;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.entities.ally.EndermanServant;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
import java.lang.reflect.Field;

public class ModShulkerBullet extends ShulkerBullet {
    private UUID ownerUUID;
    private float customDamage = 4.0F;
    private int effectDuration = 200;
    private int effectAmplifier = 0;
    private ResourceLocation effectType = null;

    public ModShulkerBullet(EntityType<? extends ShulkerBullet> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ModShulkerBullet(Level pLevel, ShulkerServant pShulker, Entity pTarget,
            net.minecraft.core.Direction.Axis pAxis) {
        super(pLevel, pShulker, pTarget, pAxis);
        this.ownerUUID = pShulker.getUUID();
    }

    public ModShulkerBullet(Level pLevel, LivingEntity pShooter, Entity pTarget,
            net.minecraft.core.Direction.Axis pAxis) {
        super(pLevel, pShooter, pTarget, pAxis);
        this.ownerUUID = pShooter.getUUID();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();

        if (isFriendly(entity)) {
            return;
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;

            livingEntity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()),
                    this.customDamage);
            if (this.effectType != null && !this.effectType.toString().isEmpty()) {
                net.minecraft.world.effect.MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(this.effectType);
                if (effect != null) {
                    livingEntity.addEffect(new MobEffectInstance(effect, this.effectDuration, this.effectAmplifier));
                } else {
                    livingEntity.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.LEVITATION,
                            this.effectDuration, this.effectAmplifier));
                }
            } else {
                livingEntity.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.LEVITATION, 200, 0));
            }
        }

        this.discard();
    }

    private boolean isFriendly(Entity entity) {
        if (this.ownerUUID == null) {
            return false;
        }

        Entity owner = this.getOwner();
        if (owner != null && MobUtil.areAllies(owner, entity)) {
            return true;
        }

        if (entity.getUUID().equals(this.ownerUUID)) {
            return true;
        }

        if (owner != null && entity == owner) {
            return true;
        }

        if (entity instanceof com.Polarice3.Goety.common.entities.ally.Summoned) {
            com.Polarice3.Goety.common.entities.ally.Summoned summoned = (com.Polarice3.Goety.common.entities.ally.Summoned) entity;
            return summoned.getTrueOwner() != null && summoned.getTrueOwner().getUUID().equals(this.ownerUUID);
        }

        if (entity instanceof ShulkerServant) {
            ShulkerServant servant = (ShulkerServant) entity;
            return servant.getTrueOwner() != null && servant.getTrueOwner().getUUID().equals(this.ownerUUID);
        }

        if (entity instanceof EndermanServant) {
            EndermanServant servant = (EndermanServant) entity;
            return servant.getTrueOwner() != null && servant.getTrueOwner().getUUID().equals(this.ownerUUID);
        }

        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.END_ROD,
                    this.getX() - this.getDeltaMovement().x * 0.1D,
                    this.getY() - this.getDeltaMovement().y * 0.1D,
                    this.getZ() - this.getDeltaMovement().z * 0.1D,
                    0.0D, 0.0D, 0.0D);
        }
    }

    public void setCustomDamage(float damage) {
        this.customDamage = damage;
    }

    public void setEffectType(ResourceLocation effectType) {
        this.effectType = effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = new ResourceLocation(effectType);
    }

    public void setEffectDuration(int duration) {
        this.effectDuration = duration;
    }

    public void setEffectAmplifier(int amplifier) {
        this.effectAmplifier = amplifier;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public void setFlightSpeed(double speed) {
        Vec3 motion = this.getDeltaMovement();
        double currentSpeed = motion.length();
        if (currentSpeed > 0) {
            Vec3 normalizedMotion = motion.normalize();
            this.setDeltaMovement(normalizedMotion.scale(speed));
        }
    }

    public void setTarget(Entity target) {
        try {
            Field finalTargetField = ShulkerBullet.class.getDeclaredField("finalTarget");
            finalTargetField.setAccessible(true);
            finalTargetField.set(this, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("CustomDamage", this.customDamage);
        if (this.effectType != null) {
            pCompound.putString("EffectType", this.effectType.toString());
        }
        pCompound.putInt("EffectDuration", this.effectDuration);
        pCompound.putInt("EffectAmplifier", this.effectAmplifier);
        if (this.ownerUUID != null) {
            pCompound.putUUID("OwnerUUID", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("CustomDamage")) {
            this.customDamage = pCompound.getFloat("CustomDamage");
        }
        if (pCompound.contains("EffectType")) {
            String effectTypeStr = pCompound.getString("EffectType");
            this.effectType = new ResourceLocation(effectTypeStr);
        }
        if (pCompound.contains("EffectDuration")) {
            this.effectDuration = pCompound.getInt("EffectDuration");
        }
        if (pCompound.contains("EffectAmplifier")) {
            this.effectAmplifier = pCompound.getInt("EffectAmplifier");
        }
        if (pCompound.hasUUID("OwnerUUID")) {
            this.ownerUUID = pCompound.getUUID("OwnerUUID");
        }
    }
}