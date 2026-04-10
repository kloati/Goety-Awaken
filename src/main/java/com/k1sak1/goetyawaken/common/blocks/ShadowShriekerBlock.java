package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class ShadowShriekerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty SHRIEKING = BlockStateProperties.SHRIEKING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty CAN_SUMMON = BlockStateProperties.CAN_SUMMON;
    protected static final VoxelShape COLLIDER = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final double TOP_Y = COLLIDER.max(Direction.Axis.Y);

    public ShadowShriekerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHRIEKING, Boolean.valueOf(false))
                .setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(CAN_SUMMON, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SHRIEKING);
        pBuilder.add(WATERLOGGED);
        pBuilder.add(CAN_SUMMON);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(SHRIEKING)) {
            pLevel.setBlock(pPos, pState.setValue(SHRIEKING, Boolean.valueOf(false)), 3);
            pLevel.getBlockEntity(pPos, ModBlockEntities.SHADOW_SHRIEKER.get()).ifPresent((blockEntity) -> {
                blockEntity.tryRespond(pLevel);
            });
        }
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
            CollisionContext pContext) {
        return COLLIDER;
    }

    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return COLLIDER;
    }

    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ShadowShriekerBlockEntity(pPos, pState);
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
            LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    public void spawnAfterBreak(BlockState pState, ServerLevel pLevel, BlockPos pPos, ItemStack pStack,
            boolean pDropExperience) {
        super.spawnAfterBreak(pState, pLevel, pPos, pStack, pDropExperience);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource,
            BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return silkTouchLevel == 0 ? 5 : 0;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide ? BaseEntityBlock.createTickerHelper(pBlockEntityType,
                ModBlockEntities.SHADOW_SHRIEKER.get(), (p_281134_, p_281135_, p_281136_, p_281137_) -> {
                    VibrationSystem.Ticker.tick(p_281134_, p_281137_.getVibrationData(), p_281137_.getVibrationUser());
                }) : null;
    }

    @Nullable
    private static ServerPlayer tryGetPlayer(Entity pEntity) {
        if (pEntity instanceof ServerPlayer serverplayer) {
            return serverplayer;
        } else {
            return null;
        }
    }

    @Override
    public net.minecraft.world.InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
            net.minecraft.world.InteractionHand pHand, net.minecraft.world.phys.BlockHitResult pHit) {
        if (pLevel instanceof ServerLevel serverLevel) {
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                if (pPlayer.getItemInHand(pHand)
                        .getItem() == com.k1sak1.goetyawaken.common.items.ModItems.PROFOUND_ECHOING_SHARD.get()) {
                    serverLevel.getBlockEntity(pPos, ModBlockEntities.SHADOW_SHRIEKER.get())
                            .ifPresent((blockEntity) -> {
                                blockEntity.tryShriekByPlayer(serverLevel, serverPlayer);
                            });
                    return net.minecraft.world.InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer,
            ItemStack pStack) {
        if (pLevel instanceof ServerLevel serverLevel) {
            if (pPlacer instanceof ServerPlayer serverPlayer) {
                serverLevel.getBlockEntity(pPos, ModBlockEntities.SHADOW_SHRIEKER.get()).ifPresent((blockEntity) -> {
                    blockEntity.setOwner(serverPlayer);
                });
            }
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        if (pPlayer.getMainHandItem().getItem() instanceof HoeItem) {
            return super.getDestroyProgress(pState, pPlayer, pLevel, pPos) * 2.0F;
        }
        return super.getDestroyProgress(pState, pPlayer, pLevel, pPos);
    }

}