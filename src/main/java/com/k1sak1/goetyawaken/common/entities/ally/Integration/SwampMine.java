package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import java.util.UUID;
import javax.annotation.Nullable;

import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class SwampMine extends Entity {
    private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(SwampMine.class,
            EntityDataSerializers.INT);
    private LivingEntity bomber;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private float extraExplosionPower = 0.0F;

    public SwampMine(EntityType<? extends SwampMine> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        blocksBuilding = true;
    }

    public SwampMine(Level worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(com.k1sak1.goetyawaken.common.ModIntegrationRegistry.SWAMP_MINE.get(), worldIn);
        this.setPos(x, y, z);
        double angle = worldIn.random.nextDouble() * Math.PI * 2;
        setDeltaMovement(-Math.sin(angle) * 0.06, 0.05, -Math.cos(angle) * 0.06);
        setFuse(200);
        xo = x;
        yo = y;
        zo = z;
        bomber = igniter;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUUID = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }
        return this.owner;
    }

    public float getExtraExplosionPower() {
        return this.extraExplosionPower;
    }

    public void setExtraExplosionPower(float power) {
        this.extraExplosionPower = power;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @SuppressWarnings("resource")
    @Override
    public void tick() {
        int fuse = getFuse();
        if (fuse > 10) {
            if (!isNoGravity()) {
                setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }

            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.98));

            if (onGround()) {
                setFuse(10);
                setDeltaMovement(0, 0, 0);
            } else
                setFuse(--fuse);
        } else
            setFuse(--fuse);
        if (fuse <= 0) {
            remove(RemovalReason.KILLED);
            if (!level().isClientSide)
                explode();
        } else {
            updateInWaterStateAndDoFluidPushing();
            if (level().isClientSide) {
                level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D,
                        0.0D);
            }
        }

    }

    protected void explode() {
        if (!this.level().isClientSide) {
            float explosionPower = AttributesConfig.SwampjawServantExplosionPower.get().floatValue()
                    + this.getExtraExplosionPower();
            Entity explosionOwner = this.getOwner() != null ? this.getOwner() : (bomber != null ? bomber : this);
            LootingExplosion explosion = new LootingExplosion(this.level(), explosionOwner,
                    this.getX(), this.getY(0.0625D), this.getZ(),
                    explosionPower, false, LootingExplosion.BlockInteraction.KEEP, LootingExplosion.Mode.LOOT);
            explosion.explode();
            explosion.finalizeExplosion(true);
            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0xffffff);
                Vec3 vec3 = this.position();
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(
                                colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                explosionPower * 2.0F, 1),
                        vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(FUSE, 200);
    }

    public void setFuse(int fuse) {
        entityData.set(FUSE, fuse);
    }

    public int getFuse() {
        return entityData.get(FUSE);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putShort("Fuse", (short) getFuse());
        compound.putFloat("ExtraExplosionPower", this.getExtraExplosionPower());
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setFuse(compound.getShort("Fuse"));
        if (compound.contains("ExtraExplosionPower")) {
            this.setExtraExplosionPower(compound.getFloat("ExtraExplosionPower"));
        }
        if (compound.contains("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 0.15F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
