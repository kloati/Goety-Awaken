package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.blocks.entity.VillagerStatueBlockEntity;
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

public class VillagerStatueBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty Y_OFFSET = IntegerProperty.create("y_offset", 0, 1);

    protected static final VoxelShape SHAPE_BASE = Block.box(3, 0, 3, 13, 16, 13);
    protected static final VoxelShape SHAPE_TOP = Block.box(4, 0, 4, 12, 10, 12);

    public VillagerStatueBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(Y_OFFSET, 0));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState,
            @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        if (!pLevel.isClientSide) {
            Direction facing = pState.getValue(FACING);
            BlockPos abovePos = pPos.above();
            pLevel.setBlock(abovePos,
                    this.defaultBlockState().setValue(FACING, facing).setValue(Y_OFFSET, 1), 3);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        if (!level.getBlockState(pos.above()).canBeReplaced()) {
            return null;
        }

        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(Y_OFFSET, 0);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide) {
            BlockPos basePos = getBasePos(pState, pPos);
            BlockState baseState = pLevel.getBlockState(basePos);

            if (baseState.getBlock() == this) {
                for (int i = 0; i < 2; i++) {
                    BlockPos breakPos = basePos.above(i);
                    BlockState breakState = pLevel.getBlockState(breakPos);
                    if (breakState.getBlock() == this) {
                        if (i == 0 && !pPlayer.isCreative()) {
                            popResource(pLevel, breakPos, new ItemStack(this.asItem()));
                        }
                        pLevel.setBlock(breakPos, Blocks.AIR.defaultBlockState(), 35);
                        pLevel.levelEvent(pPlayer, 2001, breakPos, Block.getId(breakState));
                    }
                }
            }
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    private BlockPos getBasePos(BlockState pState, BlockPos pPos) {
        return pPos.below(pState.getValue(Y_OFFSET));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(Y_OFFSET) == 0) {
            return new VillagerStatueBlockEntity(pPos, pState);
        } else {
            return null;
        }
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel,
            BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.VILLAGER_STATUE.get(),
                VillagerStatueBlockEntity::tick);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel,
            BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(Y_OFFSET) == 0 ? SHAPE_BASE : SHAPE_TOP;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel,
            BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(Y_OFFSET) == 0 ? SHAPE_BASE : SHAPE_TOP;
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
        pBuilder.add(FACING, Y_OFFSET);
    }
}
