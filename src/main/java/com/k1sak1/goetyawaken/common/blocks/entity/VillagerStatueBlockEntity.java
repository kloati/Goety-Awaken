package com.k1sak1.goetyawaken.common.blocks.entity;

import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class VillagerStatueBlockEntity extends BlockEntity {
    public int tickCount;

    public VillagerStatueBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.VILLAGER_STATUE.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VillagerStatueBlockEntity pBlockEntity) {
        pBlockEntity.tickCount++;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
