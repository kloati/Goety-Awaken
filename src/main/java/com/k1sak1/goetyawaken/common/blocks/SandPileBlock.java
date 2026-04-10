package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SandPileBlock extends FallingBlock {
    public static final int MAX_HEIGHT = 8;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[] {
            Shapes.empty(),
            net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            net.minecraft.world.level.block.Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    };

    public SandPileBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[Math.max(0, state.getValue(LAYERS) - 1)];
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
    }

    @Override

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(this) || !below.isAir();
    }

    @Override

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
            BlockPos CurrentPos, BlockPos facingPos) {
        return state.canSurvive(level, CurrentPos)
                ? super.updateShape(state, facing, facingState, level, CurrentPos, facingPos)
                : Blocks.AIR.defaultBlockState();

    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getValue(LAYERS) >= MAX_HEIGHT) {
            level.setBlockAndUpdate(pos, Blocks.SAND.defaultBlockState());
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) {
        int stackLayers = state.getValue(LAYERS);
        if (context.getItemInHand().is(asItem()) && stackLayers < MAX_HEIGHT) {
            if (context.replacingClickedOnBlock()) {
                return context.getClickedFace() == Direction.UP;
            }
            return true;
        }
        return stackLayers == 1;
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(this) && state.getValue(LAYERS) < MAX_HEIGHT) {
            return state.cycle(LAYERS);
        }
        return super.getStateForPlacement(context);
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(LAYERS);
    }
}
