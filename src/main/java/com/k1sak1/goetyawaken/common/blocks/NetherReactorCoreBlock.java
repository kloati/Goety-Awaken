package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherReactorCoreBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public NetherReactorCoreBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            Direction foundFacing = null;
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                if (checkMultiblockStructure(pLevel, pPos, facing)) {
                    foundFacing = facing;
                    break;
                }
            }

            if (foundFacing != null) {
                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    if (!canSummonWitherServant(serverPlayer)) {
                        serverPlayer.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
                        return InteractionResult.FAIL;
                    }
                }

                removeMultiblockStructure(pLevel, pPos, foundFacing);
                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    summonWitherServant(pLevel, pPos, serverPlayer);
                    com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers.SUMMON_WITHER_SERVANT
                            .trigger(serverPlayer);
                }
                pLevel.playSound(null, pPos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                pLevel.destroyBlock(pPos, false);

                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    private boolean checkMultiblockStructure(Level level, BlockPos corePos, Direction facing) {
        BlockPos head1Pos = corePos.above().relative(facing.getClockWise());
        BlockPos head2Pos = corePos.above();
        BlockPos head3Pos = corePos.above().relative(facing.getCounterClockWise());

        if (!isWitherSkeletonSkull(level, head1Pos) ||
                !isWitherSkeletonSkull(level, head2Pos) ||
                !isWitherSkeletonSkull(level, head3Pos)) {
            return false;
        }
        BlockPos leftPos = corePos.relative(facing.getClockWise());
        BlockPos rightPos = corePos.relative(facing.getCounterClockWise());

        if (!isSoulSand(level, leftPos) || !isSoulSand(level, rightPos)) {
            return false;
        }
        BlockPos frontPos = corePos.relative(facing.getOpposite());
        BlockPos backPos = corePos.relative(facing);

        if (!level.isEmptyBlock(frontPos) || !isSoulSand(level, corePos.below()) || !level.isEmptyBlock(backPos)) {
            return false;
        }

        return true;
    }

    private void removeMultiblockStructure(Level level, BlockPos corePos, Direction facing) {
        BlockPos head1Pos = corePos.above().relative(facing.getClockWise());
        BlockPos head2Pos = corePos.above();
        BlockPos head3Pos = corePos.above().relative(facing.getCounterClockWise());
        level.destroyBlock(head1Pos, false);
        level.destroyBlock(head2Pos, false);
        level.destroyBlock(head3Pos, false);
        BlockPos leftPos = corePos.relative(facing.getClockWise());
        BlockPos rightPos = corePos.relative(facing.getCounterClockWise());

        level.destroyBlock(leftPos, false);
        level.destroyBlock(rightPos, false);
        level.destroyBlock(corePos.below(), false);
    }

    private boolean isWitherSkeletonSkull(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() == Blocks.WITHER_SKELETON_SKULL ||
                level.getBlockState(pos).getBlock() == Blocks.WITHER_SKELETON_WALL_SKULL;
    }

    private boolean isSoulSand(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() == Blocks.SOUL_SAND ||
                level.getBlockState(pos).getBlock() == Blocks.SOUL_SOIL;
    }

    private boolean canSummonWitherServant(ServerPlayer player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            int count = 0;
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof WitherServant witherServant) {
                    if (witherServant.getTrueOwner() == player && entity.isAlive()) {
                        ++count;
                    }
                }
            }
            return count < Config.witherServantLimit;
        }
        return true;
    }

    private void summonWitherServant(Level level, BlockPos pos, ServerPlayer player) {
        if (level instanceof ServerLevel serverLevel) {
            WitherServant witherServant = ModEntityType.WITHER_SERVANT.get().create(serverLevel);
            if (witherServant != null) {
                witherServant.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                witherServant.setTrueOwner(player);
                witherServant.setConfigurableAttributes();
                witherServant.setHealth(witherServant.getMaxHealth() / 2.0F);
                witherServant.makeInvulnerable();
                serverLevel.addFreshEntity(witherServant);
            }
        }
    }
}