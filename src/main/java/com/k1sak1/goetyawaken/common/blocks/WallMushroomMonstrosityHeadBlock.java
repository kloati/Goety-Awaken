package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.blocks.entity.MushroomMonstrosityHeadBlockEntity;
import com.k1sak1.goetyawaken.common.items.MushroomMonstrosityHeadItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class WallMushroomMonstrosityHeadBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.box(0.0D, 4.0D, 6.0D, 16.0D, 20.0D, 16.0D),
            Direction.SOUTH, Block.box(0.0D, 4.0D, 0.0D, 16.0D, 20.0D, 10.0D),
            Direction.EAST, Block.box(0.0D, 4.0D, 0.0D, 10.0D, 20.0D, 16.0D),
            Direction.WEST, Block.box(6.0D, 4.0D, 0.0D, 16.0D, 20.0D, 16.0D)));

    public WallMushroomMonstrosityHeadBlock() {
        super(Properties.of()
                .pushReaction(PushReaction.DESTROY)
                .strength(200.0F, 1000.0F)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
            Player player) {
        ItemStack itemStack = new ItemStack(this);
        if (player.isCrouching()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MushroomMonstrosityHeadBlockEntity) {
                this.setOwner(itemStack, tileEntity);
                this.setModCustomName(itemStack, tileEntity);
            }
        }
        return itemStack;
    }

    public void setOwner(ItemStack itemStack, BlockEntity tileEntity) {
        if (tileEntity instanceof MushroomMonstrosityHeadBlockEntity blockEntity) {
            MushroomMonstrosityHeadItem.setOwner(blockEntity.getPlayer(), itemStack);
        }
    }

    public void setModCustomName(ItemStack itemStack, BlockEntity tileEntity) {
        if (tileEntity instanceof MushroomMonstrosityHeadBlockEntity blockEntity) {
            if (blockEntity.getCustomName() != null && !blockEntity.getCustomName().isEmpty()) {
                MushroomMonstrosityHeadItem.setCustomName(blockEntity.getCustomName(), itemStack);
            }
        }
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe,
            ItemStack pStack) {
        if (!pLevel.isClientSide) {
            ItemStack itemStack = new ItemStack(this);
            if (pTe instanceof MushroomMonstrosityHeadBlockEntity blockEntity) {
                this.setOwner(itemStack, blockEntity);
                this.setModCustomName(itemStack, blockEntity);
            }
            popResource(pLevel, pPos, itemStack);
        }
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer,
            ItemStack pStack) {
        ModBlocks.MOOSHROOM_MONSTROSITY_HEAD.get().setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockPos blockpos = blockPos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? super.canSurvive(blockState, level, blockPos)
                : blockstate.is(this);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
            BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y
                && doubleblockhalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) && pFacingState.getValue(HALF) != doubleblockhalf
                    ? pState.setValue(FACING, pFacingState.getValue(FACING))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN
                    && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState()
                            : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
    }

    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    @Override
    public VoxelShape getShape(BlockState p_58114_, BlockGetter p_58115_, BlockPos p_58116_,
            CollisionContext p_58117_) {
        return AABBS.get(p_58114_.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_58104_) {
        BlockState blockstate = this.defaultBlockState();
        BlockGetter blockgetter = p_58104_.getLevel();
        BlockPos blockpos = p_58104_.getClickedPos();
        Direction[] adirection = p_58104_.getNearestLookingDirections();

        for (Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);
                if (!blockgetter.getBlockState(blockpos.relative(direction)).canBeReplaced(p_58104_)) {
                    return blockstate;
                }
            }
        }

        return null;
    }

    @Override
    public BlockState rotate(BlockState p_58109_, Rotation p_58110_) {
        return p_58109_.setValue(FACING, p_58110_.rotate(p_58109_.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState p_58106_, Mirror p_58107_) {
        return p_58106_.rotate(p_58107_.getRotation(p_58106_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58112_) {
        p_58112_.add(FACING, HALF);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_151996_, BlockState p_151997_) {
        if (p_151997_.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new MushroomMonstrosityHeadBlockEntity(p_151996_, p_151997_);
        } else {
            return null;
        }
    }
}
