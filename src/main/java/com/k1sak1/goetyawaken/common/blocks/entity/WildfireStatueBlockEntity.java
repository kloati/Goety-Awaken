package com.k1sak1.goetyawaken.common.blocks.entity;

import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class WildfireStatueBlockEntity extends BlockEntity {
    public int tickCount;

    public WildfireStatueBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WILDFIRE_STATUE.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, WildfireStatueBlockEntity pBlockEntity) {
        pBlockEntity.tickCount++;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
