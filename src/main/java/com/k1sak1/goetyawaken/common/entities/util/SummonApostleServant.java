package com.k1sak1.goetyawaken.common.entities.util;

import com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.entity.LivingEntity;
import javax.annotation.Nullable;
import java.util.UUID;

public class SummonApostleServant extends Entity implements IOwned {
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private boolean leftOwner;

    public SummonApostleServant(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.noPhysics = true;
    }

    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public LivingEntity getTrueOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved() && this.cachedOwner instanceof LivingEntity) {
            return (LivingEntity) this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.cachedOwner = entity;
                return (LivingEntity) entity;
            }
        }
        return null;
    }

    @Override
    public UUID getOwnerId() {
        return this.ownerUUID;
    }

    @Override
    public void setOwnerId(@Nullable UUID uuid) {
        this.ownerUUID = uuid;
    }

    private boolean hostile = false;

    @Override
    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }

    @Override
    public boolean isHostile() {
        return this.hostile;
    }

    protected boolean ownedBy(Entity pEntity) {
        return pEntity.getUUID().equals(this.ownerUUID);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
            this.cachedOwner = null;
        }

        this.leftOwner = pCompound.getBoolean("LeftOwner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }

        if (this.leftOwner) {
            pCompound.putBoolean("LeftOwner", true);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount == 150) {
            this.playSound(SoundEvents.AMBIENT_NETHER_WASTES_MOOD.get(), 1.0F, 1.0F);
            for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(32))) {
                player.displayClientMessage(Component.translatable("info.goety.apostle.summon"), true);
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                Warden.applyDarknessAround(serverLevel, this.position(), (Entity) null, 32);
            }
        }
        if (this.tickCount == 300) {
            this.playSound(ModSounds.APOSTLE_AMBIENT.get(), 1.0F, 1.0F);
        }
        if (this.tickCount == 450) {
            this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
        }
        if (!this.level().isClientSide) {
            ServerLevel serverWorld = (ServerLevel) this.level();
            for (int i = 0; i < 2; ++i) {
                serverWorld.sendParticles(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() + 1.0D,
                        this.getRandomZ(0.5D), 0, (serverWorld.random.nextDouble() - 0.5D) * 2.0D,
                        -serverWorld.random.nextDouble(), (serverWorld.random.nextDouble() - 0.5D) * 2.0D, 0.5D);
            }
            if (serverWorld.dimension() == Level.NETHER) {
                ServerParticleUtil.gatheringParticles(ParticleTypes.ENCHANT, this, serverWorld);
            }
            if (this.tickCount >= 300) {
                serverWorld.sendParticles(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY() + 1.0D,
                        this.getRandomZ(0.5D), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (this.tickCount == 450) {
                for (int k = 0; k < 200; ++k) {
                    float f2 = random.nextFloat() * 4.0F;
                    float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                    double d1 = Mth.cos(f1) * f2;
                    double d2 = 0.01D + random.nextDouble() * 0.5D;
                    double d3 = Mth.sin(f1) * f2;
                    serverWorld.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D,
                            this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                }
                ApostleServant apostleEntity = new ApostleServant(
                        com.k1sak1.goetyawaken.common.entities.ModEntityType.APOSTLE_SERVANT.get(), this.level());
                apostleEntity.setPos(this.getX(), this.getY(), this.getZ());
                apostleEntity.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(this.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                Entity owner = this.getOwner();
                if (owner instanceof net.minecraft.world.entity.player.Player playerOwner) {
                    com.Polarice3.Goety.utils.MobUtil.summonTame(apostleEntity, playerOwner);
                }

                serverWorld.addFreshEntity(apostleEntity);
                this.discard();
            }
        }
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        // No synched data needed for this entity
    }

}
