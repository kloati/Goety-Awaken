package com.k1sak1.goetyawaken.common.blocks.entity;

import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class MushroomMonstrosityHeadBlockEntity extends SkullBlockEntity {
    private UUID ownerUUID;
    private String customName;

    public MushroomMonstrosityHeadBlockEntity(BlockPos p_155731_, BlockState p_155732_) {
        super(p_155731_, p_155732_);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.MOOSHROOM_MONSTROSITY_HEAD.get();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (this.getOwnerId() != null) {
            tag.putUUID("Owner", this.getOwnerId());
        }
        if (this.getCustomName() != null && !this.getCustomName().isEmpty()) {
            tag.putString("mod_custom_name", this.getCustomName());
        }
        return tag;
    }

    public void load(CompoundTag nbt) {
        if (nbt.hasUUID("Owner")) {
            this.setOwnerId(nbt.getUUID("Owner"));
        }
        if (nbt.contains("mod_custom_name")) {
            this.setCustomName(nbt.getString("mod_custom_name"));
        }
        super.load(nbt);
    }

    public void saveAdditional(CompoundTag compound) {
        if (this.getOwnerId() != null) {
            compound.putUUID("Owner", this.getOwnerId());
        }
        if (this.getCustomName() != null && !this.getCustomName().isEmpty()) {
            compound.putString("mod_custom_name", this.getCustomName());
        }
        super.saveAdditional(compound);
    }

    public void setOwnerId(@Nullable UUID uuid) {
        this.ownerUUID = uuid;
        this.setChanged();
    }

    @Nullable
    public UUID getOwnerId() {
        return this.ownerUUID;
    }

    @Nullable
    public LivingEntity getPlayer() {
        if (this.ownerUUID == null) {
            return null;
        } else {
            return this.level.getPlayerByUUID(this.ownerUUID);
        }
    }

    public void setCustomName(@Nullable String name) {
        this.customName = name;
        this.setChanged();
    }

    @Nullable
    public String getCustomName() {
        return this.customName;
    }
}
