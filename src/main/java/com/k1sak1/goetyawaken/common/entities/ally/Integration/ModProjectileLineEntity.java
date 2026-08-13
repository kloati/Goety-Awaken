package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.k1sak1.goetyawaken.Config;
import lykrast.meetyourfight.entity.ProjectileLineEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ModProjectileLineEntity extends ProjectileLineEntity {
    private static final EntityDataAccessor<Float> EXTRA_DAMAGE = SynchedEntityData.defineId(
            ModProjectileLineEntity.class, EntityDataSerializers.FLOAT);
    private static final String TAG_EXTRA_DAMAGE = "ExtraDamage";

    public ModProjectileLineEntity(EntityType<? extends ProjectileLineEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public ModProjectileLineEntity(Level worldIn, LivingEntity owner) {
        super(worldIn, owner);
    }

    public void setExtraDamage(float damage) {
        this.entityData.set(EXTRA_DAMAGE, damage);
    }

    public float getExtraDamage() {
        return this.entityData.get(EXTRA_DAMAGE);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EXTRA_DAMAGE, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat(TAG_EXTRA_DAMAGE, this.getExtraDamage());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(TAG_EXTRA_DAMAGE)) {
            this.setExtraDamage(compound.getFloat(TAG_EXTRA_DAMAGE));
        }
    }

    @Override
    protected float getDamage(LivingEntity target, Entity source) {
        return Config.GHOST_MISSILE_BASE_DAMAGE.get().floatValue() + this.getExtraDamage();
    }
}
