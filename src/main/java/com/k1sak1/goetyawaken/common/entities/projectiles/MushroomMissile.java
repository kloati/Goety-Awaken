package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class MushroomMissile extends SpellHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_HAS_TARGET = SynchedEntityData.defineId(MushroomMissile.class,
            EntityDataSerializers.BOOLEAN);
    private double xd;
    private double yd;
    private double zd;
    private final Vec3[] trailPositions = new Vec3[64];
    private int trailPointer = -1;

    public MushroomMissile(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public MushroomMissile(double pX, double pY, double pZ, double pXPower, double pYPower, double pZPower,
            Level pLevel) {
        super(ModEntityType.MUSHROOM_MISSILE.get(), pX, pY, pZ, pXPower, pYPower, pZPower, pLevel);
    }

    public MushroomMissile(LivingEntity pShooter, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(ModEntityType.MUSHROOM_MISSILE.get(), pShooter, pXPower, pYPower, pZPower, pLevel);
        Vec3 direction = new Vec3(pXPower, pYPower, pZPower).normalize();
        this.setYRot((float) (Mth.atan2(direction.x, direction.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(direction.y,
                Mth.sqrt((float) (direction.x * direction.x + direction.z * direction.z)))
                * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HAS_TARGET, false);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    protected static float lerpRotation(float pCurrentRotation, float pTargetRotation) {
        while (pTargetRotation - pCurrentRotation < -180.0F) {
            pCurrentRotation -= 360.0F;
        }

        while (pTargetRotation - pCurrentRotation >= 180.0F) {
            pCurrentRotation += 360.0F;
        }

        return Mth.lerp(0.5F, pCurrentRotation, pTargetRotation);
    }

    public void rotateToMatchMovement() {
        Vec3 movement = this.getDeltaMovement();
        if (movement.lengthSqr() > 0.001D) {
            double horizontalDistance = Mth.sqrt((float) (movement.x * movement.x + movement.z * movement.z));
            this.setXRot(
                    lerpRotation(this.xRotO, (float) (Mth.atan2(movement.y, horizontalDistance) * (180D / Math.PI))));
            this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(movement.x, movement.z) * (180D / Math.PI))));
        }
    }

    public boolean isOnFire() {
        return false;
    }

    protected void onHitEntity(EntityHitResult p_37626_) {
        super.onHitEntity(p_37626_);
        if (!this.level().isClientSide) {
            Entity entity = p_37626_.getEntity();
            Entity owner = this.getOwner();
            float explosionDamage = (float) com.k1sak1.goetyawaken.Config.mushroomMissileBaseDamage
                    + this.getExtraDamage();
            DamageSource damageSource = com.k1sak1.goetyawaken.utils.ModDamageSource.mushroomMissile(this, owner);

            if (entity instanceof LivingEntity livingEntity && owner != null
                    && !com.Polarice3.Goety.utils.MobUtil.areAllies(owner, livingEntity)) {
                boolean flag = livingEntity.hurt(damageSource, explosionDamage);
                if (flag) {
                    double dx = livingEntity.getX() - this.getX();
                    double dz = livingEntity.getZ() - this.getZ();
                    double magnitude = Math.sqrt(dx * dx + dz * dz);
                    if (magnitude > 0.0F) {
                        dx /= magnitude;
                        dz /= magnitude;
                        livingEntity.push(dx * 0.5D, 0.3D, dz * 0.5D);
                    }
                    removeRandomBeneficialEffect(livingEntity);
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 100, 1));
                }
            }

            this.explode();
        }
    }

    protected void onHit(HitResult p_37628_) {
        super.onHit(p_37628_);
        if (!this.level().isClientSide) {
            this.explode();
        }
    }

    private void explode() {
        if (!this.level().isClientSide) {
            Entity entity = this.getOwner();
            Vec3 vec3 = this.position();
            float explosionDamage = (float) com.k1sak1.goetyawaken.Config.mushroomMissileBaseDamage
                    + this.getExtraDamage();
            DamageSource damageSource = com.k1sak1.goetyawaken.utils.ModDamageSource.mushroomMissile(this, entity);

            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(3.0D));
            for (LivingEntity target : entities) {
                if (target != this.getOwner() && !MobUtil.areAllies(entity, target)) {
                    double distance = this.distanceTo(target);
                    if (distance <= 3.0F) {
                        target.hurt(damageSource, explosionDamage);
                        double dx = target.getX() - this.getX();
                        double dz = target.getZ() - this.getZ();
                        double magnitude = Math.sqrt(dx * dx + dz * dz);
                        if (magnitude > 0.0F) {
                            dx /= magnitude;
                            dz /= magnitude;
                            target.push(dx * 0.5D, 0.3D, dz * 0.5D);
                        }
                        target.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 100, 1));
                    }
                }
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(serverLevel,
                        ModParticleTypes.BIG_FIRE.get(), this);
                com.Polarice3.Goety.utils.ColorUtil colorUtil = new com.Polarice3.Goety.utils.ColorUtil(0xdd9c16);
                serverLevel.sendParticles(
                        new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(colorUtil.red,
                                colorUtil.green, colorUtil.blue, 4, 1),
                        vec3.x, com.Polarice3.Goety.utils.BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D,
                        0.0D, 0.0D);
                serverLevel.sendParticles(
                        new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(colorUtil.red,
                                colorUtil.green, colorUtil.blue, 5, 1),
                        vec3.x, com.Polarice3.Goety.utils.BlockFinder.moveDownToGround(this), vec3.z, 1, 0.0D, 0.0D,
                        0.0D, 0.0D);
                org.joml.Vector3f vector3f = new org.joml.Vector3f(
                        net.minecraft.world.phys.Vec3.fromRGB24(0x7a6664).toVector3f());
                org.joml.Vector3f vector3f2 = new org.joml.Vector3f(
                        net.minecraft.world.phys.Vec3.fromRGB24(0xeca294).toVector3f());
                com.Polarice3.Goety.client.particles.DustCloudParticleOption cloudParticleOptions = new com.Polarice3.Goety.client.particles.DustCloudParticleOption(
                        vector3f, 1.0F);
                com.Polarice3.Goety.client.particles.DustCloudParticleOption cloudParticleOptions2 = new com.Polarice3.Goety.client.particles.DustCloudParticleOption(
                        vector3f2, 1.0F);
                for (int i = 0; i < 2; ++i) {
                    com.Polarice3.Goety.utils.ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions,
                            vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 2.0F * 2);
                }
                com.Polarice3.Goety.utils.ServerParticleUtil.circularParticles(serverLevel, cloudParticleOptions2,
                        vec3.x, this.getY() + 0.25D, vec3.z, 0, 0.14D, 0, 2.0F * 2);
            }

            this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, 4.0F, 1.0F);
            this.playSound(ModSounds.REDSTONE_EXPLODE.get(), 4.0F, 1.0F);

            this.discard();
        }
    }

    private void removeRandomBeneficialEffect(LivingEntity target) {
        Collection<MobEffectInstance> beneficialEffects = new ArrayList<>();
        for (MobEffectInstance effectInstance : target.getActiveEffects()) {
            if (effectInstance.getEffect().getCategory() == MobEffectCategory.BENEFICIAL) {
                beneficialEffects.add(effectInstance);
            }
        }
        if (!beneficialEffects.isEmpty()) {
            MobEffectInstance[] effectsArray = beneficialEffects.toArray(new MobEffectInstance[0]);
            MobEffectInstance randomEffect = effectsArray[target.getRandom().nextInt(effectsArray.length)];

            int currentAmplifier = randomEffect.getAmplifier();
            int currentDuration = randomEffect.getDuration();

            if (currentAmplifier <= 0) {
                target.removeEffect(randomEffect.getEffect());
            } else {
                target.removeEffect(randomEffect.getEffect());
                int newAmplifier = currentAmplifier - 1;
                int newDuration = currentDuration / 2;
                target.addEffect(new MobEffectInstance(randomEffect.getEffect(), newDuration, newAmplifier));
            }
        }
    }

    public void tick() {
        super.tick();

        if (this.tickCount >= MathHelper.secondsToTicks(10)) {
            this.discard();
        }

        if (this.level().isClientSide) {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() - vec3.x;
            double d1 = this.getY() - vec3.y;
            double d2 = this.getZ() - vec3.z;
            this.level().addParticle(ModParticleTypes.BIG_FIRE.get(),
                    d0 + ((this.level().random.nextDouble() / 4)
                            * (this.level().random.nextIntBetweenInclusive(-1, 1))),
                    d1 + 0.15D,
                    d2 + ((this.level().random.nextDouble() / 4)
                            * (this.level().random.nextIntBetweenInclusive(-1, 1))),
                    0.0D, 0.0D, 0.0D);
        }

        Vec3 trailAt = this.position().add(0, this.getBbHeight() / 2F, 0);
        if (this.trailPointer == -1) {
            Arrays.fill(trailPositions, trailAt);
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer] = trailAt;
        if (!this.level().isClientSide && this.tickCount > 1) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }
    }

    public Vec3 getTrailPosition(int pointer, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    public boolean hasTrail() {
        return this.trailPointer != -1;
    }

    public float getGravity() {
        return 0.0F;
    }

    protected float getInertia() {
        return 0.82F;
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null) {
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity) {
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)) {
                    return false;
                }
                if (pEntity instanceof MushroomMissile) {
                    return false;
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    protected ParticleOptions getTrailParticle() {
        return ModParticleTypes.NONE.get();
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}