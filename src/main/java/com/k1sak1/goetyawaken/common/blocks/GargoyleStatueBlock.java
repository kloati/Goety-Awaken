package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.blocks.entity.GargoyleStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GargoyleStatueBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty Y_OFFSET = IntegerProperty.create("y_offset", 0, 4);
    public static final IntegerProperty X_OFFSET = IntegerProperty.create("x_offset", 0, 4);
    public static final IntegerProperty Z_OFFSET = IntegerProperty.create("z_offset", 0, 3);

    protected static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public GargoyleStatueBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(Y_OFFSET, 0)
                .setValue(X_OFFSET, 2)
                .setValue(Z_OFFSET, 1));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState,
            @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        if (!pLevel.isClientSide) {
            Direction facing = pState.getValue(FACING);
            for (int y = 0; y < 5; y++) {
                for (int x = -2; x <= 2; x++) {
                    for (int z = -1; z <= 2; z++) {
                        if (y == 0 && x == 0 && z == 0) {
                            continue;
                        }
                        BlockPos targetPos = pPos.offset(x, y, z);
                        pLevel.setBlock(targetPos,
                                this.defaultBlockState()
                                        .setValue(FACING, facing)
                                        .setValue(Y_OFFSET, y)
                                        .setValue(X_OFFSET, x + 2)
                                        .setValue(Z_OFFSET, z + 1),
                                3);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        for (int y = 0; y < 5; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -1; z <= 2; z++) {
                    if (y == 0 && x == 0 && z == 0) {
                        continue;
                    }
                    if (!level.getBlockState(pos.offset(x, y, z)).canBeReplaced()) {
                        return null;
                    }
                }
            }
        }

        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(Y_OFFSET, 0)
                .setValue(X_OFFSET, 2)
                .setValue(Z_OFFSET, 1);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide) {
            BlockPos centerPos = getCenterPos(pState, pPos);
            BlockState centerState = pLevel.getBlockState(centerPos);

            if (centerState.getBlock() == this) {
                for (int y = 0; y < 5; y++) {
                    for (int x = -2; x <= 2; x++) {
                        for (int z = -1; z <= 2; z++) {
                            BlockPos breakPos = centerPos.offset(x, y, z);
                            BlockState breakState = pLevel.getBlockState(breakPos);
                            if (breakState.getBlock() == this) {
                                if (y == 0 && x == 0 && z == 0 && !pPlayer.isCreative()) {
                                    popResource(pLevel, breakPos, new ItemStack(this.asItem()));
                                }
                                pLevel.setBlock(breakPos, Blocks.AIR.defaultBlockState(), 35);
                                pLevel.levelEvent(pPlayer, 2001, breakPos, Block.getId(breakState));
                            }
                        }
                    }
                }
            }
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    private BlockPos getCenterPos(BlockState pState, BlockPos pPos) {
        int xOff = pState.getValue(X_OFFSET) - 2;
        int yOff = pState.getValue(Y_OFFSET);
        int zOff = pState.getValue(Z_OFFSET) - 1;
        return pPos.offset(-xOff, -yOff, -zOff);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(Y_OFFSET) == 0 && pState.getValue(X_OFFSET) == 2 && pState.getValue(Z_OFFSET) == 1) {
            return new GargoyleStatueBlockEntity(pPos, pState);
        } else {
            return null;
        }
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel,
            BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.GARGOYLE_STATUE.get(),
                GargoyleStatueBlockEntity::tick);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel,
            BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel,
            BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, Y_OFFSET, X_OFFSET, Z_OFFSET);
    }
}
