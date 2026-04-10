package com.k1sak1.goetyawaken.common.blocks.entity;

import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class PoisonousMushroomBlockEntity extends BlockEntity implements INBTSerializable<CompoundTag> {
    public static final BlockEntityType<PoisonousMushroomBlockEntity> TYPE = ModBlockEntities.POISONOUS_MUSHROOM.get();

    private UUID ownerUUID;
    private LivingEntity cachedOwner;
    private int ownerUpdateCounter;
    private int existenceTimer;

    public PoisonousMushroomBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(TYPE, pWorldPosition, pBlockState);
        this.ownerUpdateCounter = 0;
        this.existenceTimer = 0;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
        } else {
            this.ownerUUID = null;
            this.cachedOwner = null;
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.level == null || this.ownerUUID == null) {
            return null;
        }

        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()
                && this.cachedOwner.getUUID().equals(this.ownerUUID)) {
            return this.cachedOwner;
        }

        if (this.ownerUpdateCounter-- <= 0) {
            this.ownerUpdateCounter = 100;
            this.cachedOwner = (LivingEntity) this.level.getPlayerByUUID(this.ownerUUID);

            if (this.cachedOwner == null || this.cachedOwner.isRemoved()) {
                net.minecraft.world.phys.AABB searchBox = new net.minecraft.world.phys.AABB(
                        this.worldPosition.getX() - 32, this.worldPosition.getY() - 32, this.worldPosition.getZ() - 32,
                        this.worldPosition.getX() + 32, this.worldPosition.getY() + 32, this.worldPosition.getZ() + 32);
                List<Entity> entities = this.level.getEntities(null, searchBox);
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity livingEntity && entity.getUUID().equals(this.ownerUUID)) {
                        this.cachedOwner = livingEntity;
                        break;
                    }
                }
            }
        }

        return this.cachedOwner;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.hasUUID("OwnerUUID")) {
            this.ownerUUID = pTag.getUUID("OwnerUUID");
        } else if (pTag.contains("Owner")) {
            this.ownerUUID = pTag.getUUID("Owner");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.ownerUUID != null) {
            pTag.putUUID("OwnerUUID", this.ownerUUID);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.load(nbt);
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T t) {
        if (t instanceof PoisonousMushroomBlockEntity mushroomBE) {
            mushroomBE.serverTick();
        }
    }

    private void serverTick() {
        this.existenceTimer++;
        if (this.existenceTimer >= 1200) {
            LivingEntity owner = getOwner();
            if (owner != null && !owner.isDeadOrDying() && Config.ALLOW_POISONOUS_MUSHROOM_HEAL_SPECIAL_OWNERS.get()) {
                float maxHealth = owner.getMaxHealth();
                float healAmount = maxHealth * 0.001F;
                if (owner.getHealth() < owner.getMaxHealth()) {
                    owner.heal(healAmount);
                }
            }
            if (this.level != null) {
                this.level.removeBlock(this.worldPosition, false);
            }
            return;
        }
        if (this.level != null && !this.level.isClientSide && this.level.getGameTime() % 100 == 0) {
            LivingEntity owner = getOwner();
            if (owner != null) {
                boolean isSpecialOwner = owner instanceof com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom ||
                        owner instanceof com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity ||
                        owner instanceof com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;

                if (isSpecialOwner && Config.ALLOW_POISONOUS_MUSHROOM_HEAL_SPECIAL_OWNERS.get()) {
                    float maxHealth = owner.getMaxHealth();
                    float healAmount = maxHealth * 0.0005F;
                    if (owner.getHealth() < owner.getMaxHealth()) {
                        owner.heal(healAmount);
                    }
                }
            }
        }
    }
}