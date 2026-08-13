package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MathHelper;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TrackingFireball extends SpellHurtingProjectile implements ItemSupplier {
    private int explosionPower = 3;
    private float extraDamage = 0.0F;
    private int trackingDelay = 10;
    private int age = 0;
    private static final int MAX_TRAILS = 15;
    private static final net.minecraft.network.syncher.EntityDataAccessor<Boolean> DATA_HAS_TRAIL = net.minecraft.network.syncher.SynchedEntityData
            .defineId(TrackingFireball.class,
                    net.minecraft.network.syncher.EntityDataSerializers.BOOLEAN);
    protected static final net.minecraft.network.syncher.EntityDataAccessor<java.util.Optional<java.util.UUID>> TARGET_UNIQUE_ID = net.minecraft.network.syncher.SynchedEntityData
            .defineId(TrackingFireball.class,
                    net.minecraft.network.syncher.EntityDataSerializers.OPTIONAL_UUID);
    protected static final net.minecraft.network.syncher.EntityDataAccessor<Integer> TARGET_CLIENT_ID = net.minecraft.network.syncher.SynchedEntityData
            .defineId(TrackingFireball.class,
                    net.minecraft.network.syncher.EntityDataSerializers.INT);

    @OnlyIn(Dist.CLIENT)
    private java.util.List<com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition> trailPositions;

    @OnlyIn(Dist.CLIENT)
    private java.util.List<com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition> getTrailPositions() {
        if (this.trailPositions == null) {
            this.trailPositions = new java.util.ArrayList<>();
        }
        return this.trailPositions;
    }

    @OnlyIn(Dist.CLIENT)
    public java.util.List<com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition> getPublicTrailPoints() {
        return getTrailPositions();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HAS_TRAIL, false);
        this.entityData.define(TARGET_UNIQUE_ID, java.util.Optional.empty());
        this.entityData.define(TARGET_CLIENT_ID, -1);
    }

    public boolean hasTrail() {
        return this.entityData.get(DATA_HAS_TRAIL);
    }

    public void setHasTrail(boolean hasTrail) {
        this.entityData.set(DATA_HAS_TRAIL, hasTrail);
    }

    @javax.annotation.Nullable
    public LivingEntity getTarget() {
        if (!this.level().isClientSide) {
            java.util.UUID uuid = this.getTargetId();
            if (uuid == null)
                return null;
            if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                return serverLevel.getEntity(uuid) instanceof LivingEntity living ? living : null;
            }
            return null;
        } else {
            int id = this.getTargetClientId();
            return id <= -1 ? null : this.level().getEntity(id) instanceof LivingEntity living ? living : null;
        }
    }

    public void setTarget(@javax.annotation.Nullable LivingEntity target) {
        if (target != null) {
            this.entityData.set(TARGET_UNIQUE_ID, java.util.Optional.of(target.getUUID()));
            this.entityData.set(TARGET_CLIENT_ID, target.getId());
        }
    }

    @javax.annotation.Nullable
    public java.util.UUID getTargetId() {
        return this.entityData.get(TARGET_UNIQUE_ID).orElse(null);
    }

    public void setTargetId(@javax.annotation.Nullable java.util.UUID uuid) {
        this.entityData.set(TARGET_UNIQUE_ID, java.util.Optional.ofNullable(uuid));
    }

    public int getTargetClientId() {
        return this.entityData.get(TARGET_CLIENT_ID);
    }

    public void setTargetClientId(int id) {
        this.entityData.set(TARGET_CLIENT_ID, id);
    }

    public TrackingFireball(EntityType<? extends TrackingFireball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public TrackingFireball(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ) {
        super(ModEntityType.TRACKING_FIREBALL.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
        this.explosionPower = 3;
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {
            LootingExplosion.BlockInteraction blockInteraction;
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner())) {
                Entity owner = this.getOwner();
                if (owner instanceof com.Polarice3.Goety.api.entities.IOwned owned && owned.isHostile()) {
                    blockInteraction = LootingExplosion.BlockInteraction.DESTROY_WITH_DECAY;
                } else {
                    blockInteraction = LootingExplosion.BlockInteraction.KEEP;
                }
            } else {
                blockInteraction = LootingExplosion.BlockInteraction.KEEP;
            }

            LootingExplosion explosion = new LootingExplosion(this.level(), this, this.getX(), this.getY(),
                    this.getZ(), (float) this.explosionPower, false,
                    blockInteraction, LootingExplosion.Mode.LOOT);
            explosion.explode();
            explosion.finalizeExplosion(true);
            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0xff4400);
                Vec3 vec3 = this.position();
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                this.explosionPower * 2.0F, 1),
                        vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);

                for (int i = 0; i < 50; i++) {
                    double offsetX = (this.random.nextDouble() - 0.5D) * this.explosionPower * 2;
                    double offsetY = (this.random.nextDouble() - 0.5D) * this.explosionPower * 2;
                    double offsetZ = (this.random.nextDouble() - 0.5D) * this.explosionPower * 2;
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LAVA,
                            this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                            1, 0, 0, 0, 0);
                }
            }
            this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, 4.0F, 1.0F);
            this.playSound(com.Polarice3.Goety.init.ModSounds.HELL_BLAST_IMPACT.get(), 4.0F, 1.0F);

            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float damage = 4.0F + this.extraDamage;
            if (entity1 instanceof LivingEntity livingOwner) {
                entity.hurt(this.damageSources().mobProjectile(this, livingOwner), damage);
                this.doEnchantDamageEffects(livingOwner, entity);
            } else {
                entity.hurt(this.damageSources().explosion(this, entity1), damage);
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null) {
            if (pEntity == this.getOwner() || com.Polarice3.Goety.utils.MobUtil.areAllies(this.getOwner(), pEntity)) {
                return false;
            }
        }
        return super.canHitEntity(pEntity);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("ExplosionPower", (byte) this.explosionPower);
        pCompound.putFloat("ExtraDamage", this.extraDamage);
        pCompound.putInt("TrackingDelay", this.trackingDelay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("ExplosionPower", 99)) {
            this.explosionPower = pCompound.getByte("ExplosionPower");
        }
        if (pCompound.contains("ExtraDamage", 99)) {
            this.extraDamage = pCompound.getFloat("ExtraDamage");
        }
        if (pCompound.contains("TrackingDelay", 99)) {
            this.trackingDelay = pCompound.getInt("TrackingDelay");
        }
    }

    public void setExtraDamage(float damage) {
        this.extraDamage = damage;
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= MathHelper.secondsToTicks(15)) {
            this.discard();
        }
        this.age++;
        if (this.age >= this.trackingDelay && !this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            Entity owner = this.getOwner();
            if (target == null || !target.isAlive() || (owner != null
                    && (target == owner || com.Polarice3.Goety.utils.MobUtil.areAllies(owner, target)))) {
                if (owner instanceof net.minecraft.world.entity.Mob mobOwner) {
                    target = mobOwner.getTarget();
                    if (target != null && target.isAlive()) {
                        this.setTarget(target);
                        this.setTargetId(target.getUUID());
                    }
                }
            }

            if (target != null && target.isAlive()) {
                if (owner != null) {
                    if (target == owner
                            || com.Polarice3.Goety.utils.MobUtil.areAllies(owner, target)) {
                        this.setTarget(null);
                        this.setTargetId(null);
                        return;
                    }
                }
                Vec3 toTarget = target.position().subtract(this.position()).normalize();
                Vec3 currentMovement = this.getDeltaMovement();
                double trackingStrength = 0.10;
                Vec3 newMovement = currentMovement.lerp(toTarget.scale(1.5), trackingStrength);
                this.setDeltaMovement(newMovement);
            } else {
                Vec3 currentMovement = this.getDeltaMovement();
                if (currentMovement.lengthSqr() > 0.001) {
                    this.setDeltaMovement(currentMovement);
                }
            }
        }

        if (this.level().isClientSide) {
            this.handleClientTick();
        } else {
            if (!this.hasTrail()) {
                this.setHasTrail(true);
            }
        }
    }

    private void handleClientTick() {
        if (this.hasTrail()) {
            this.initializeTrail();
            this.updateTrail();
        }
    }

    private void initializeTrail() {
        if (this.hasTrail() && this.getTrailPositions().isEmpty()) {
            Vec3 centerPos = this.getBoundingBox().getCenter();
            this.getTrailPositions().add(new com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition(centerPos, 0));
        }
    }

    private void updateTrail() {
        Vec3 centerPos = this.getBoundingBox().getCenter();
        this.getTrailPositions().add(0, new com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition(centerPos, 0));
        while (this.getTrailPositions().size() > MAX_TRAILS) {
            this.getTrailPositions().remove(this.getTrailPositions().size() - 1);
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.level().isClientSide && this.trailPositions != null) {
            this.trailPositions.clear();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(0.9375F, 0.9375F);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected float getInertia() {
        return 0.95F + this.boltSpeed;
    }

    @Override
    public ItemStack getItem() {
        return Items.FIRE_CHARGE.getDefaultInstance();
    }
}
