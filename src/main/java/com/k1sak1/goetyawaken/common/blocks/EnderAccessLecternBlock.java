package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.items.magic.AccessFocus;
import com.Polarice3.Goety.api.items.magic.IWand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class EnderAccessLecternBlock extends LecternBlock implements EntityBlock {
    public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;

    public EnderAccessLecternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, Boolean.valueOf(false))
                .setValue(HAS_BOOK, Boolean.valueOf(true)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        ItemStack heldStack = pPlayer.getItemInHand(pHand);
        ItemStack focusStack = ItemStack.EMPTY;
        if (heldStack.getItem() instanceof AccessFocus) {
            focusStack = heldStack;
        } else if (heldStack.getItem() instanceof IWand) {
            ItemStack wandFocus = IWand.getFocus(heldStack);
            if (wandFocus != null && !wandFocus.isEmpty() && wandFocus.getItem() instanceof AccessFocus) {
                focusStack = wandFocus;
            }
        }
        if (!focusStack.isEmpty()) {
            if (!pLevel.isClientSide && !pPlayer.isShiftKeyDown()) {
                final ItemStack finalFocus = focusStack;
                net.minecraft.nbt.CompoundTag tag = finalFocus.getOrCreateTag();
                tag.putLong(AccessFocus.NBT_BOUND_POS, pPos.asLong());
                tag.putString(AccessFocus.NBT_BOUND_DIMENSION, pLevel.dimension().location().toString());
                pPlayer.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable(
                                "item.goetyawaken.access_focus.bound",
                                pPos.getX(), pPos.getY(), pPos.getZ())
                                .withStyle(net.minecraft.ChatFormatting.GREEN),
                        true);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }

        if (!pLevel.isClientSide && pPlayer instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof EnderAccessLecternBlockEntity lecternEntity) {
                if (!lecternEntity.hasSoulEnergy()) {
                    return InteractionResult.FAIL;
                }
                net.minecraftforge.network.NetworkHooks.openScreen(serverPlayer, lecternEntity, buf -> {
                    buf.writeBlockPos(pPos);
                    buf.writeInt(lecternEntity.getSortingDirection());
                    buf.writeInt(lecternEntity.getSortingType());
                    buf.writeInt(lecternEntity.getViewType());
                    buf.writeInt(lecternEntity.getSearchBoxMode());
                });
                pPlayer.awardStat(Stats.INTERACT_WITH_LECTERN);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EnderAccessLecternBlockEntity(pPos, pState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return (lvl, pos, st, blockEntity) -> {
                if (blockEntity instanceof EnderAccessLecternBlockEntity lecternEntity) {
                    EnderAccessLecternBlockEntity.serverTick(lvl, pos, st, lecternEntity);
                }
            };
        }
        return null;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 7;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof EnderAccessLecternBlockEntity lecternEntity) {
                for (int i = 0; i < lecternEntity.getCraftingMatrix().getContainerSize(); i++) {
                    ItemStack stack = lecternEntity.getCraftingMatrix().getItem(i);
                    if (!stack.isEmpty()) {
                        net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
                ItemStack result = lecternEntity.getCraftingResult().getItem(0);
                if (!result.isEmpty()) {
                    net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return 0;
    }
}