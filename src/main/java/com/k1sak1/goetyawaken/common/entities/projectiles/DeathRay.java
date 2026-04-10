package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashSet;
import java.util.Set;

public class DeathRay extends Entity implements IEntityAdditionalSpawnData {
    public static final int LIFETIME = 8;
    public static final double MAX_RAYTRACE_DISTANCE = 64.0;
    public static final float BEAM_WIDTH = 0.2F;

    public float distance;
    public float extraDamage = 0;
    private int ownerId = -1;
    private LivingEntity cachedOwner;
    private boolean hasDamaged = false;

    public DeathRay(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DeathRay(Level level, Vec3 start, Vec3 end, LivingEntity owner) {
        super(ModEntityType.DEATH_RAY.get(), level);
        this.setPos(start);
        this.distance = (float) Math.min(start.distanceTo(end), MAX_RAYTRACE_DISTANCE);
        this.ownerId = owner.getId();
        this.cachedOwner = owner;
        this.setRot(owner.getYRot(), owner.getXRot());
    }

    public void setExtraDamage(float extraDamage) {
        this.extraDamage = extraDamage;
    }

    @Override
    public void tick() {
        if (this.tickCount == 1) {
            if (this.level().isClientSide) {
                Vec3 forward = this.getForward();
                for (float i = 1.0F; i < this.distance; i += 0.5F) {
                    Vec3 pos = this.position().add(forward.scale(i));
                    this.level().addParticle(ParticleTypes.SMOKE, false, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
                }
            }
        } else if (this.tickCount > LIFETIME) {
            this.discard();
            return;
        }

        if (!this.level().isClientSide() && !hasDamaged && this.tickCount >= 5) {
            this.performDamage();
            this.hasDamaged = true;
        }
    }

    private void performDamage() {
        LivingEntity owner = this.getOwner();
        if (owner == null || !owner.isAlive()) {
            return;
        }

        Set<LivingEntity> entitiesToDamage = new HashSet<>();
        AABB searchBox = new AABB(this.position(), this.position()).inflate(BEAM_WIDTH);

        double distanceTraveled = 0;
        Vec3 viewVector = this.getViewVector(1.0F);
        while (distanceTraveled < this.distance && distanceTraveled < MAX_RAYTRACE_DISTANCE) {
            for (Entity entity : this.level().getEntities(this, searchBox)) {
                if (entity instanceof LivingEntity livingEntity &&
                        livingEntity != owner &&
                        livingEntity.isAlive() &&
                        this.canHitEntity(livingEntity)) {
                    entitiesToDamage.add(livingEntity);
                }
            }

            distanceTraveled += 1.0D;
            Vec3 targetVector = this.position().add(
                    viewVector.x * distanceTraveled,
                    viewVector.y * distanceTraveled,
                    viewVector.z * distanceTraveled);
            searchBox = new AABB(targetVector, targetVector).inflate(BEAM_WIDTH);
        }
        this.damageEntities(entitiesToDamage, owner);
    }

    private boolean canHitEntity(LivingEntity target) {
        LivingEntity owner = this.getOwner();
        if (owner == null)
            return false;

        if (target == owner)
            return false;

        return target.isAlive() && target.isPickable();
    }

    private void damageEntities(Set<LivingEntity> entities, LivingEntity owner) {
        for (LivingEntity entity : entities) {
            entity.invulnerableTime = 0;
            Vec3 deltaMovement = entity.getDeltaMovement();
            entity.hurt(com.Polarice3.Goety.utils.ModDamageSource.indirectEntityDamageSource(this.level(),
                    com.Polarice3.Goety.utils.ModDamageSource.DOOM, this, owner), this.extraDamage);
            entity.invulnerableTime = 0;
            entity.setDeltaMovement(deltaMovement);
        }
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt((int) (distance * 10));
        buffer.writeInt(ownerId);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.distance = additionalData.readInt() / 10f;
        this.ownerId = additionalData.readInt();
    }

    public LivingEntity getOwner() {
        if (this.cachedOwner != null && this.cachedOwner.isAlive()) {
            return this.cachedOwner;
        }

        if (this.level() != null) {
            Entity entity = this.level().getEntity(this.ownerId);
            if (entity instanceof LivingEntity) {
                this.cachedOwner = (LivingEntity) entity;
                return this.cachedOwner;
            }
        }
        return null;
    }
}
