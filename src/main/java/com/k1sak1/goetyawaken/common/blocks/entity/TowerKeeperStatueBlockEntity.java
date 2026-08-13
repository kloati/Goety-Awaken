package com.k1sak1.goetyawaken.common.blocks.entity;

import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TowerKeeperStatueBlockEntity extends BlockEntity {
    public int tickCount;

    public TowerKeeperStatueBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TOWER_KEEPER_STATUE.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, TowerKeeperStatueBlockEntity pBlockEntity) {
        pBlockEntity.tickCount++;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
