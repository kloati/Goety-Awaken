package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TabooLampEngravedBlock extends TabooLampBlock {
    protected static final VoxelShape SHAPE = Block.box(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D);

    public TabooLampEngravedBlock(Properties p_58123_) {
        super(p_58123_);
    }

    @Override
    public VoxelShape getShape(BlockState p_58152_, BlockGetter p_58153_, BlockPos p_58154_,
            CollisionContext p_58155_) {
        return SHAPE;
    }
}
