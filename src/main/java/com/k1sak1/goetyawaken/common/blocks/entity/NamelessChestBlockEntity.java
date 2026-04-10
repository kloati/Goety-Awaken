package com.k1sak1.goetyawaken.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;

public class NamelessChestBlockEntity extends ChestBlockEntity {

    public NamelessChestBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.NAMELESS_CHEST.get(), pPos, pBlockState);
    }

}
