package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CoinPileBlock extends HorizontalDirectionalBlock {
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 4);

    protected static final VoxelShape[][] SHAPE_BY_LAYER = new VoxelShape[][] {
            {
                    Shapes.box(0.4375, 0.0, 0.5, 0.625, 0.5, 0.6875),
                    Shapes.box(0.4375, 0.0, 0.75, 0.625, 0.375, 0.9375),
                    Shapes.box(0.8125, 0.0, 0.75, 1.0, 0.375, 0.9375),
                    Shapes.box(0.0625, 0.0, 0.5625, 0.25, 0.25, 0.75),
                    Shapes.box(0.6875, 0.0, 0.5, 0.875, 0.125, 0.6875),
                    Shapes.box(0.0625, 0.0, 0.125, 0.25, 0.125, 0.3125),
                    Shapes.box(0.75, 0.0, 0.0625, 0.9375, 0.125, 0.25)
            },
            {
                    Shapes.box(0.25, 0.0, 0.1875, 0.4375, 0.875, 0.375),
                    Shapes.box(0.6875, 0.0, 0.25, 0.875, 0.5, 0.4375),
                    Shapes.box(0.4375, 0.0, 0.75, 0.625, 0.375, 0.9375),
                    Shapes.box(0.0625, 0.0, 0.5625, 0.25, 0.25, 0.75),
                    Shapes.box(0.6875, 0.0, 0.5625, 0.875, 0.125, 0.75)
            },
            {
                    Shapes.box(0.0625, 0.0, 0.1875, 0.25, 0.625, 0.375),
                    Shapes.box(0.4375, 0.0, 0.125, 0.625, 0.5, 0.3125),
                    Shapes.box(0.4375, 0.0, 0.4375, 0.625, 0.3125, 0.625),
                    Shapes.box(0.75, 0.0, 0.75, 0.9375, 0.75, 0.9375),
                    Shapes.box(0.1875, 0.0, 0.625, 0.375, 1.0, 0.8125),
                    Shapes.box(0.6875, 0.0, 0.1875, 0.875, 0.125, 0.375)
            },
            {
                    Shapes.box(0.75, 0.0, 0.1875, 0.9375, 1.125, 0.375),
                    Shapes.box(0.25, 0.0, 0.3125, 0.4375, 1.25, 0.5),
                    Shapes.box(0.5, 0.0, 0.5, 0.6875, 0.5, 0.6875),
                    Shapes.box(0.25, 0.0, 0.6875, 0.4375, 0.875, 0.875),
                    Shapes.box(0.0, 0.0, 0.75, 0.1875, 0.375, 0.9375),
                    Shapes.box(0.5, 0.0, 0.0, 0.6875, 0.5, 0.1875),
                    Shapes.box(0.0625, 0.0, 0.0625, 0.25, 0.25, 0.25),
                    Shapes.box(0.75, 0.0, 0.6875, 0.9375, 0.625, 0.875)
            }
    };

    public CoinPileBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LAYERS, 1));
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int layer = pState.getValue(LAYERS) - 1;
        Direction facing = pState.getValue(FACING);
        VoxelShape[] shapes = SHAPE_BY_LAYER[layer];

        VoxelShape result = Shapes.empty();
        for (VoxelShape shape : shapes) {
            result = Shapes.or(result, rotateShape(shape, facing));
        }
        return result;
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
            CollisionContext pContext) {
        return getShape(pState, pLevel, pPos, pContext);
    }

    private VoxelShape rotateShape(VoxelShape shape, Direction facing) {
        net.minecraft.world.phys.AABB box = shape.toAabbs().get(0);
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        switch (facing) {
            case SOUTH:
                return Shapes.box(1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ);
            case EAST:
                return Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX);
            case WEST:
                return Shapes.box(minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX);
            case NORTH:
            default:
                return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos());
        if (blockstate.is(this)) {
            int i = blockstate.getValue(LAYERS);
            if (i < 4) {
                return blockstate.setValue(LAYERS, i + 1);
            }
        }
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pContext) {
        int i = pState.getValue(LAYERS);
        if (pContext.getItemInHand().is(this.asItem()) && i < 4) {
            if (pContext.replacingClickedOnBlock()) {
                return pContext.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
            BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos)
                ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
                : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LAYERS);
    }
}
