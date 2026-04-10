package com.k1sak1.goetyawaken.common.blocks;

import com.Polarice3.Goety.common.blocks.SoulCandlestickBlock;
import com.k1sak1.goetyawaken.common.blocks.entity.DarkSoulCandleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DarkSoulCandleBlock extends SoulCandlestickBlock {

    public DarkSoulCandleBlock() {
        super();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DarkSoulCandleBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
        if (pBlockEntityType == com.k1sak1.goetyawaken.common.blocks.ModBlockEntities.DARK_SOUL_CANDLE.get()) {
            return (world, pos, state, blockEntity) -> {
                if (blockEntity instanceof DarkSoulCandleBlockEntity darkSoulCandle)
                    darkSoulCandle.tick();
            };
        }
        return null;
    }
}
