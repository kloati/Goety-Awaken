package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedstoneClusterSmallBlock extends Block {
    private static final VoxelShape SHAPE = createShape();
    private final int signalStrength;

    public RedstoneClusterSmallBlock(int signalStrength) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F)
                .lightLevel(state -> signalStrength > 0 ? 3 : 0));
        this.signalStrength = signalStrength;
    }

    private static VoxelShape createShape() {
        VoxelShape core = Block.box(4, 0, 4, 9, 9, 9);
        VoxelShape out1 = Block.box(3, 1, 2, 5, 5, 4);
        VoxelShape out2 = Block.box(3, 3, 6, 5, 5, 8);
        VoxelShape out3 = Block.box(2, 0, 10, 6, 5, 14);
        VoxelShape out4 = Block.box(10, 0, 6, 13, 7, 9);
        VoxelShape out5 = Block.box(9, 0, 12, 11, 2, 14);

        VoxelShape result = core;
        result = Shapes.join(result, out1, BooleanOp.OR);
        result = Shapes.join(result, out2, BooleanOp.OR);
        result = Shapes.join(result, out3, BooleanOp.OR);
        result = Shapes.join(result, out4, BooleanOp.OR);
        result = Shapes.join(result, out5, BooleanOp.OR);
        return result;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction side) {
        return signalStrength;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction side) {
        return signalStrength;
    }
}
