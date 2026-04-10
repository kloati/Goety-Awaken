package com.k1sak1.goetyawaken.common.blocks.entity;

import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class AllyPithosBlockEntity extends BlockEntity {
    private UUID ownerUUID;
    private String ownerName;
    private boolean activated = true;

    public AllyPithosBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALLY_PITHOS.get(), pos, state);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Nullable
    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
        this.setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("OwnerUUID")) {
            this.ownerUUID = tag.getUUID("OwnerUUID");
        }
        if (tag.contains("OwnerName")) {
            this.ownerName = tag.getString("OwnerName");
        }
        if (tag.contains("Activated")) {
            this.activated = tag.getBoolean("Activated");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.ownerUUID != null) {
            tag.putUUID("OwnerUUID", this.ownerUUID);
        }
        if (this.ownerName != null) {
            tag.putString("OwnerName", this.ownerName);
        }
        tag.putBoolean("Activated", this.activated);
    }

    public CompoundTag getBlockEntityTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }
}