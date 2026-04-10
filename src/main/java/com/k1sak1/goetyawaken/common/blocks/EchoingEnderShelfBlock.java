package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.storage.api.IStorageDiskProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class EchoingEnderShelfBlock extends BaseEntityBlock {
    public static final int MAX_BOOKS_IN_STORAGE = 6;
    public static final int BOOKS_PER_ROW = 3;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of(
            BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED,
            BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED,
            BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED,
            BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED,
            BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED,
            BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED);

    public EchoingEnderShelfBlock(Properties properties) {
        super(properties);
        BlockState blockstate = this.stateDefinition.any().setValue(FACING, Direction.NORTH);

        for (BooleanProperty booleanproperty : SLOT_OCCUPIED_PROPERTIES) {
            blockstate = blockstate.setValue(booleanproperty, Boolean.valueOf(false));
        }

        this.registerDefaultState(blockstate);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof EchoingEnderShelfBlockEntity echoingEnderShelfBlockEntity) {
            Optional<Vec2> optional = getRelativeHitCoordinatesForBlockFace(pHit, pState.getValue(FACING));
            if (optional.isEmpty()) {
                return InteractionResult.PASS;
            } else {
                int i = getHitSlot(optional.get());
                if (pState.getValue(SLOT_OCCUPIED_PROPERTIES.get(i))) {
                    removeBook(pLevel, pPos, pPlayer, echoingEnderShelfBlockEntity, i);
                    return InteractionResult.sidedSuccess(pLevel.isClientSide);
                } else {
                    ItemStack itemstack = pPlayer.getItemInHand(pHand);
                    if (isValidBook(itemstack)) {
                        addBook(pLevel, pPos, pPlayer, echoingEnderShelfBlockEntity, itemstack, i);
                        return InteractionResult.sidedSuccess(pLevel.isClientSide);
                    } else {
                        return InteractionResult.CONSUME;
                    }
                }
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult pHitResult, Direction pFace) {
        Direction direction = pHitResult.getDirection();
        if (pFace != direction) {
            return Optional.empty();
        } else {
            BlockPos blockpos = pHitResult.getBlockPos().relative(direction);
            Vec3 vec3 = pHitResult.getLocation().subtract((double) blockpos.getX(), (double) blockpos.getY(),
                    (double) blockpos.getZ());
            double d0 = vec3.x();
            double d1 = vec3.y();
            double d2 = vec3.z();
            Optional optional;
            switch (direction) {
                case NORTH:
                    optional = Optional.of(new Vec2((float) (1.0D - d0), (float) d1));
                    break;
                case SOUTH:
                    optional = Optional.of(new Vec2((float) d0, (float) d1));
                    break;
                case WEST:
                    optional = Optional.of(new Vec2((float) d2, (float) d1));
                    break;
                case EAST:
                    optional = Optional.of(new Vec2((float) (1.0D - d2), (float) d1));
                    break;
                case DOWN:
                case UP:
                    optional = Optional.empty();
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return optional;
        }
    }

    private static int getHitSlot(Vec2 pHitPos) {
        int i = pHitPos.y >= 0.5F ? 0 : 1;
        int j = getSection(pHitPos.x);
        return j + i * 3;
    }

    private static int getSection(float pX) {
        float f = 0.0625F;
        float f1 = 0.375F;
        if (pX < 0.375F) {
            return 0;
        } else {
            float f2 = 0.6875F;
            return pX < 0.6875F ? 1 : 2;
        }
    }

    private static void addBook(Level pLevel, BlockPos pPos, Player pPlayer, EchoingEnderShelfBlockEntity pBlockEntity,
            ItemStack pBookStack, int pSlot) {
        if (!pLevel.isClientSide) {
            pPlayer.awardStat(Stats.ITEM_USED.get(pBookStack.getItem()));
            SoundEvent soundevent = SoundEvents.CHISELED_BOOKSHELF_INSERT;
            pBlockEntity.setItem(pSlot, pBookStack.split(1));
            pLevel.playSound((Player) null, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (pPlayer.isCreative()) {
                pBookStack.grow(1);
            }

            pLevel.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, pPos);
        }
    }

    private static void removeBook(Level pLevel, BlockPos pPos, Player pPlayer,
            EchoingEnderShelfBlockEntity pBlockEntity, int pSlot) {
        if (!pLevel.isClientSide) {
            ItemStack itemstack = pBlockEntity.removeItem(pSlot, 1);
            pLevel.playSound((Player) null, pPos, SoundEvents.CHISELED_BOOKSHELF_PICKUP, SoundSource.BLOCKS, 1.0F,
                    1.0F);
            if (!pPlayer.getInventory().add(itemstack)) {
                pPlayer.drop(itemstack, false);
            }

            pLevel.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, pPos);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EchoingEnderShelfBlockEntity(pPos, pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        SLOT_OCCUPIED_PROPERTIES.forEach((p_261456_) -> {
            pBuilder.add(p_261456_);
        });
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, net.minecraft.world.level.block.Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, net.minecraft.world.level.block.Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        if (pLevel.isClientSide()) {
            return 0;
        } else {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof EchoingEnderShelfBlockEntity echoingEnderShelfBlockEntity) {
                return echoingEnderShelfBlockEntity.getLastInteractedSlot() + 1;
            } else {
                return 0;
            }
        }
    }

    private boolean isValidBook(ItemStack stack) {
        return stack.getItem() instanceof IStorageDiskProvider;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 7;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof EchoingEnderShelfBlockEntity echoingEnderShelfBlockEntity) {
                if (!echoingEnderShelfBlockEntity.isEmpty()) {
                    for (int i = 0; i < 6; ++i) {
                        ItemStack itemstack = echoingEnderShelfBlockEntity.getItem(i);
                        if (!itemstack.isEmpty()) {
                            net.minecraft.world.Containers.dropItemStack(pLevel, (double) pPos.getX(),
                                    (double) pPos.getY(), (double) pPos.getZ(), itemstack);
                        }
                    }

                    echoingEnderShelfBlockEntity.clearContent();
                    pLevel.updateNeighbourForOutputSignal(pPos, this);
                }
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        if (level instanceof net.minecraft.world.level.Level lvl) {
            if (lvl.getBlockEntity(pos) instanceof EchoingEnderShelfBlockEntity blockEntity) {
                int emptySlots = 0;
                for (int i = 0; i < blockEntity.getContainerSize(); i++) {
                    if (blockEntity.getItem(i).isEmpty()) {
                        emptySlots++;
                    }
                }
                return switch (emptySlots) {
                    case 6 -> 0.0F;
                    case 5, 4 -> 1.0F;
                    case 3, 2 -> 2.0F;
                    case 1, 0 -> 3.0F;
                    default -> 0.0F;
                };
            }
        }
        return 0.0F;
    }

}