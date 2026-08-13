package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.client.renderer.GiantGhastFireballTextures;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class GiantGhastFireball extends com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile {
    private static final EntityDataAccessor<Integer> DATA_ANIMATION = SynchedEntityData
            .defineId(GiantGhastFireball.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_TRAIL = SynchedEntityData
            .defineId(GiantGhastFireball.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(GiantGhastFireball.class,
            EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData
            .defineId(GiantGhastFireball.class, EntityDataSerializers.FLOAT);
    private int explosionPower = 6;
    private static final int MAX_TRAILS = 20;
    private boolean hasExploded = false;

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

    public GiantGhastFireball(EntityType<? extends GiantGhastFireball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GiantGhastFireball(LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, Level pLevel) {
        super(ModEntityType.GIANT_GHAST_FIREBALL.get(), pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
        this.rotateToMatchMovement();
    }

    public GiantGhastFireball(double pX, double pY, double pZ, double pAccelX, double pAccelY, double pAccelZ,
            Level pWorld) {
        super(ModEntityType.GIANT_GHAST_FIREBALL.get(), pX, pY, pZ, pAccelX, pAccelY, pAccelZ, pWorld);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DAMAGE, 6.0F);
        this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
        this.entityData.define(DATA_ANIMATION, 0);
        this.entityData.define(DATA_HAS_TRAIL, false);
    }

    public boolean hasTrail() {
        return this.entityData.get(DATA_HAS_TRAIL);
    }

    public void setHasTrail(boolean hasTrail) {
        this.entityData.set(DATA_HAS_TRAIL, hasTrail);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getAnimation() < GiantGhastFireballTextures.TEXTURES.size()) {
            this.setAnimation(this.getAnimation() + 1);
        } else {
            this.setAnimation(0);
        }
        if (this.tickCount >= MathHelper.secondsToTicks(10)) {
            this.discard();
        }

        if (!this.level().isClientSide && this.tickCount % 10 == 0) {
            this.spawnRingParticle();
        }

        if (this.level().isClientSide) {
            this.handleClientTick();
        } else {
            if (!this.hasTrail()) {
                this.setHasTrail(true);
            }
        }
    }

    private void spawnRingParticle() {
        Vec3 movement = this.getDeltaMovement();
        if (movement.lengthSqr() < 1.0E-4D) {
            return;
        }
        Vec3 dir = movement.normalize();
        float yaw = (float) Math.atan2(dir.x, dir.z);
        float pitch = (float) Math.atan2(-dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z));
        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new com.k1sak1.goetyawaken.client.particle.RingParticle.RingData(
                            yaw, pitch, 80, 1.0F, 0.0F, 0.0F, 0.8F, 150.0F,
                            false, com.k1sak1.goetyawaken.client.particle.RingParticle.EnumRingBehavior.GROW),
                    this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
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

    public float getDamage() {
        return this.entityData.get(DATA_DAMAGE);
    }

    public void setDamage(float pDamage) {
        this.entityData.set(DATA_DAMAGE, pDamage);
    }

    public float getExtraDamage() {
        return this.entityData.get(DATA_EXTRA_DAMAGE);
    }

    public void setExtraDamage(float extra) {
        this.entityData.set(DATA_EXTRA_DAMAGE, extra);
    }

    public int getAnimation() {
        return this.entityData.get(DATA_ANIMATION);
    }

    public void setAnimation(int pType) {
        this.entityData.set(DATA_ANIMATION, pType);
    }

    public net.minecraft.resources.ResourceLocation getResourceLocation() {
        return GiantGhastFireballTextures.TEXTURES.getOrDefault(this.getAnimation(),
                GiantGhastFireballTextures.TEXTURES.get(0));
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            float damage = this.getDamage() + this.getExtraDamage();
            entity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) entity1), damage);
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity) entity1, entity);
            }
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {
            this.explodeAndDiscard();
        }
    }

    private void explodeAndDiscard() {
        if (!this.level().isClientSide) {
            if (this.hasExploded) {
                return;
            }
            this.hasExploded = true;

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
            this.igniteBlocksInExplosionRange();

            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0xff0000);
                Vec3 vec3 = this.position();
                float explosionRadius = this.explosionPower;
                com.Polarice3.Goety.client.particles.SmashParticleOption smashOption = new com.Polarice3.Goety.client.particles.SmashParticleOption(
                        colorUtil, explosionRadius * 2.0F, explosionRadius, 1.0F, 30);
                serverLevel.sendParticles(smashOption, vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);
                ColorUtil blackColor = new ColorUtil(0x000000);
                com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption verticalCircleOption = new com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption(
                        blackColor.red(), blackColor.green(), blackColor.blue(), explosionRadius * 2.0F, 1);
                serverLevel.sendParticles(verticalCircleOption, vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);
                com.Polarice3.Goety.client.particles.CircleExplodeParticleOption circleOption = new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(
                        blackColor.red(), blackColor.green(), blackColor.blue(), explosionRadius * 2.0F, 1);
                serverLevel.sendParticles(circleOption, vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);
                org.joml.Vector3f dustColor = new org.joml.Vector3f(
                        Vec3.fromRGB24(0x333333).toVector3f());
                com.Polarice3.Goety.client.particles.DustCloudParticleOption dustCloudOption = new com.Polarice3.Goety.client.particles.DustCloudParticleOption(
                        dustColor, explosionRadius);
                for (int i = 0; i < 3; ++i) {
                    com.Polarice3.Goety.utils.ServerParticleUtil.circularParticles(
                            serverLevel, dustCloudOption, vec3.x, vec3.y + 0.5D, vec3.z, 0, 0.14D, 0, explosionRadius);
                }
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                explosionRadius * 2.0F, 1),
                        vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);

                for (int i = 0; i < 100; i++) {
                    double offsetX = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
                    double offsetY = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
                    double offsetZ = (this.random.nextDouble() - 0.5D) * explosionRadius * 2;
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

    private void igniteBlocksInExplosionRange() {
        if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner())) {
            return;
        }
        net.minecraft.core.BlockPos centerPos = this.blockPosition();
        int radius = this.explosionPower;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius) {
                        net.minecraft.core.BlockPos pos = centerPos.offset(x, y, z);
                        net.minecraft.world.level.block.state.BlockState state = this.level().getBlockState(pos);
                        if (state.isAir()) {
                            net.minecraft.core.BlockPos belowPos = pos.below();
                            net.minecraft.world.level.block.state.BlockState belowState = this.level()
                                    .getBlockState(belowPos);
                            if (belowState.isSolidRender(this.level(), belowPos) &&
                                    !belowState.getBlock().equals(net.minecraft.world.level.block.Blocks.TNT) &&
                                    this.random.nextInt(10) < 1) {
                                if (this.level().getBlockState(pos).isAir()) {
                                    this.level().setBlockAndUpdate(pos,
                                            net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null) {
            if (pEntity == this.getOwner()) {
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity) {
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)) {
                    return false;
                }
                if (pEntity instanceof IOwned owned0 && this.getOwner() instanceof IOwned owned1) {
                    return !MobUtil.ownerStack(owned0, owned1);
                }
            }
        }
        return super.canHitEntity(pEntity);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Animation", this.getAnimation());
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putFloat("ExtraDamage", this.getExtraDamage());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setAnimation(pCompound.getInt("Animation"));
        if (pCompound.contains("Damage")) {
            this.setDamage(pCompound.getFloat("Damage"));
        }
        if (pCompound.contains("ExtraDamage", 99)) {
            this.setExtraDamage(pCompound.getFloat("ExtraDamage"));
        }
    }

    public void rotateToMatchMovement() {
        this.updateRotation();
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    protected float getInertia() {
        return 0.95F + this.boltSpeed;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
