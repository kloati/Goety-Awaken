package com.k1sak1.goetyawaken.common.blocks.entity;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.SoulCandlestickBlockEntity;
import com.k1sak1.goetyawaken.api.IAcceleratedSoulCandle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DarkSoulCandleBlockEntity extends SoulCandlestickBlockEntity implements IAcceleratedSoulCandle {
    private CursedCageBlockEntity cursedCageTile;
    private static final int ACCELERATION_FACTOR = 8;

    public DarkSoulCandleBlockEntity(BlockPos pPos, BlockState pState) {
        super(pPos, pState);
    }

    @Override
    public void tick() {
        if (this.level != null) {
            boolean flag = this.checkCage() && this.cursedCageTile != null && this.cursedCageTile.getSouls() > 0;
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(
                    com.Polarice3.Goety.common.blocks.NecroBrazierBlock.LIT, flag), 3);
        }
    }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntityType<?> getType() {
        return com.k1sak1.goetyawaken.common.blocks.ModBlockEntities.DARK_SOUL_CANDLE.get();
    }

    @Override
    public int getAccelerationFactor() {
        return ACCELERATION_FACTOR;
    }

    @Override
    public int getSouls() {
        if (this.level != null && this.checkCage()) {
            return this.cursedCageTile.getSouls();
        }
        return 0;
    }

    @Override
    public void drainSouls(int amount, BlockPos blockPos) {
        if (this.level != null && this.checkCage()) {
            if (this.cursedCageTile.getSouls() >= amount) {
                this.cursedCageTile.decreaseSouls(8 * amount);
                double d0 = 0.1 * (double) (blockPos.getX() - this.getBlockPos().getX());
                double d1 = 0.1 * (double) (blockPos.getY() - this.getBlockPos().getY());
                double d2 = 0.1 * (double) (blockPos.getZ() - this.getBlockPos().getZ());
                if (this.level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ModParticleTypes.SOUL_EXPLODE_BITS.get(),
                            this.getBlockPos().getX() + 0.5D + this.level.random.nextDouble() * 0.2D - 0.1D,
                            this.getBlockPos().getY() + 0.75D,
                            this.getBlockPos().getZ() + 0.5D + this.level.random.nextDouble() * 0.2D - 0.1D,
                            0, d0 * 1.5D, d1 * 1.5D, d2 * 1.5D, 0.3F);
                }
            }
        }
    }

    public boolean checkCage() {
        if (this.level == null) {
            return false;
        } else {
            BlockPos pos = new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() - 1,
                    this.getBlockPos().getZ());
            BlockState blockState = this.level.getBlockState(pos);
            if (blockState.is(ModBlocks.CURSED_CAGE_BLOCK.get())) {
                BlockEntity tileentity = this.level.getBlockEntity(pos);
                if (tileentity instanceof CursedCageBlockEntity) {
                    this.cursedCageTile = (CursedCageBlockEntity) tileentity;
                    return !this.cursedCageTile.getItem().isEmpty();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }
}
