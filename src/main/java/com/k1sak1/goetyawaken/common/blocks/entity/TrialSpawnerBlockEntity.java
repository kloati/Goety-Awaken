package com.k1sak1.goetyawaken.common.blocks.entity;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.utils.PlayerDetector;
import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.common.blocks.TrialSpawnerBlock;
import com.k1sak1.goetyawaken.common.blocks.entity.trial_spawner.TrialSpawner;
import com.k1sak1.goetyawaken.common.blocks.entity.trial_spawner.TrialSpawnerState;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrialSpawnerBlockEntity extends BlockEntity implements TrialSpawner.StateAccessor {
    private TrialSpawner trialSpawner;

    public TrialSpawnerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.TRIAL_SPAWNER.get(), blockPos, blockState);
        this.trialSpawner = new TrialSpawner(this, PlayerDetector.NO_CREATIVE_PLAYERS,
                PlayerDetector.EntitySelector.SELECT_FROM_LEVEL);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.trialSpawner
                .codec()
                .parse(NbtOps.INSTANCE, compoundTag)
                .resultOrPartial(Goety.LOGGER::error)
                .ifPresent(trialSpawner -> this.trialSpawner = trialSpawner);
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        this.trialSpawner
                .codec()
                .encodeStart(NbtOps.INSTANCE, this.trialSpawner)
                .get()
                .ifLeft(tag -> compoundTag.merge((CompoundTag) tag))
                .ifRight(param0x -> Goety.LOGGER.warn("Failed to encode TrialSpawner {}", param0x.message()));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.trialSpawner.getData().getUpdateTag(this.getBlockState().getValue(TrialSpawnerBlock.STATE));
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public void setEntityId(EntityType<?> entityType, RandomSource randomSource) {
        this.trialSpawner.getData().setEntityId(this.trialSpawner, randomSource, entityType);
        this.setChanged();
    }

    public void setEntityWithNbt(EntityType<?> entityType, CompoundTag nbtTag, RandomSource randomSource) {
        this.trialSpawner.getData().setEntityWithNbt(this.trialSpawner, randomSource, entityType, nbtTag);
        this.setChanged();
    }

    public TrialSpawner getTrialSpawner() {
        return this.trialSpawner;
    }

    @Override
    public TrialSpawnerState getState() {
        return !this.getBlockState()
                .hasProperty(com.k1sak1.goetyawaken.common.blocks.properties.ModStateProperties.TRIAL_SPAWNER_STATE)
                        ? TrialSpawnerState.INACTIVE
                        : this.getBlockState().getValue(
                                com.k1sak1.goetyawaken.common.blocks.properties.ModStateProperties.TRIAL_SPAWNER_STATE);
    }

    @Override
    public void setState(Level level, TrialSpawnerState spawnerState) {
        this.setChanged();
        level.setBlockAndUpdate(this.worldPosition,
                this.getBlockState().setValue(
                        com.k1sak1.goetyawaken.common.blocks.properties.ModStateProperties.TRIAL_SPAWNER_STATE,
                        spawnerState));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.load(pkt.getTag());
        }
        super.onDataPacket(net, pkt);
    }

    @Override
    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }

    }
}
