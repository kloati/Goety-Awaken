package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class TabooBookshelfBlock extends Block {
    public TabooBookshelfBlock(Properties p_58123_) {
        super(p_58123_.mapColor(MapColor.STONE));
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return 3.0F;
    }
}
