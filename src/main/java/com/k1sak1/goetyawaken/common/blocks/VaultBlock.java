package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.blocks.entity.VaultBlockEntity;
import com.k1sak1.goetyawaken.common.blocks.properties.ModStateProperties;
import com.k1sak1.goetyawaken.common.blocks.entity.vault.VaultState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class VaultBlock extends BaseEntityBlock {
    public static final EnumProperty<VaultState> STATE = ModStateProperties.VAULT_STATE;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public VaultBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .lightLevel(l -> l.getValue(VaultBlock.STATE).getLuminance())
                .strength(50.0F)
                .sound(SoundType.METAL)
                .isViewBlocking(ModBlocks::never)
                .noOcclusion());
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(STATE, VaultState.INACTIVE));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (itemStack.isEmpty() || pState.getValue(STATE) != VaultState.ACTIVE) {
            return InteractionResult.PASS;
        } else if (pLevel instanceof ServerLevel serverLevel) {
            if (serverLevel.getBlockEntity(pPos) instanceof VaultBlockEntity vaultBlockEntity) {
                VaultBlockEntity.Server.tryUnlock(serverLevel, pPos, pState, vaultBlockEntity.getConfig(),
                        vaultBlockEntity.getServerData(), vaultBlockEntity.getSharedData(), pPlayer, itemStack);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        } else {
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState p_53068_, Rotation p_53069_) {
        return p_53068_.setValue(FACING, p_53069_.rotate(p_53068_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_53065_, Mirror p_53066_) {
        return p_53065_.rotate(p_53066_.getRotation(p_53065_.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VaultBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
            BlockEntityType<T> type) {
        return level instanceof ServerLevel serverLevel
                ? createTickerHelper(type, com.k1sak1.goetyawaken.common.blocks.ModBlockEntities.VAULT.get(),
                        (level1, blockPos, state, block) -> VaultBlockEntity.Server.tick(serverLevel, blockPos, state,
                                block.getConfig(), block.getServerData(), block.getSharedData()))
                : createTickerHelper(type, com.k1sak1.goetyawaken.common.blocks.ModBlockEntities.VAULT.get(),
                        (level1, blockPos, state, block) -> VaultBlockEntity.Client.tick(level1, blockPos, state,
                                block.getClientData(), block.getSharedData()));
    }
}
