package com.k1sak1.goetyawaken.common.blocks;

import com.Polarice3.Goety.client.particles.MagicSmokeParticle;
import com.Polarice3.Goety.client.particles.MagicSmokeParticleOption;
import com.k1sak1.goetyawaken.common.blocks.entity.GorgeousUrnBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class GorgeousUrnBlock extends BaseEntityBlock {
    public static final ResourceLocation AWAKENED_EMERALD_ID = ResourceLocation.fromNamespaceAndPath("goety",
            "magic_emerald");
    public static final DirectionProperty FACING = DirectionProperty.create("facing",
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    public GorgeousUrnBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
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
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GorgeousUrnBlockEntity(pos, state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof GorgeousUrnBlockEntity gorgeousUrn) {
            if (!pLevel.isClientSide && pPlayer.isCreative() && !gorgeousUrn.isEmpty()) {
                ItemStack itemstack = new ItemStack(this);
                gorgeousUrn.saveToItem(itemstack);
                Vec3 vec3 = Vec3.atCenterOf(pPos);
                ItemEntity itementity = new ItemEntity(pLevel, vec3.x, vec3.y, vec3.z, itemstack);
                itementity.setDefaultPickUpDelay();
                pLevel.addFreshEntity(itementity);
            }
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
            @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide) {
            ItemStack mainHandItem = player.getMainHandItem();

            if (EnchantmentHelper.hasSilkTouch(mainHandItem)) {
                ItemStack blockStack = new ItemStack(this.asItem());
                if (blockEntity instanceof GorgeousUrnBlockEntity gorgeousUrn) {
                    gorgeousUrn.saveToItem(blockStack);
                }
                Vec3 vec3 = Vec3.atCenterOf(pos);
                ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, blockStack);
                level.addFreshEntity(itemEntity);
            } else {
                dropAwakenedEmerald(level, pos);
            }
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            net.minecraft.world.phys.BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof GorgeousUrnBlockEntity gorgeousUrn) {
                MenuProvider menuprovider = gorgeousUrn;
                pPlayer.openMenu(menuprovider);
            }
            return InteractionResult.CONSUME;
        }
    }

    private void dropAwakenedEmerald(Level level, BlockPos pos) {
        if (level instanceof ServerLevel) {
            Item item = BuiltInRegistries.ITEM.get(AWAKENED_EMERALD_ID);
            ItemStack emeraldStack = new ItemStack(item, 9);

            Vec3 vec3 = Vec3.atCenterOf(pos);
            ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, emeraldStack);
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRand) {
        if (pLevel.getBlockState(pPos.above()).isAir()) {
            double d0 = (double) pPos.getX() + 0.5D;
            double d1 = (double) pPos.getY() + 1.0D;
            double d2 = (double) pPos.getZ() + 0.5D;
            pLevel.addParticle(new MagicSmokeParticleOption(0x97F56A, 0x20C631, 40 + pRand.nextInt(20), 0.2F, 0.0F),
                    d0, d1, d2, pRand.nextBoolean() ? 0.01D : -0.01D, 0.025D, pRand.nextBoolean() ? 0.01D : -0.01D);
        }
    }
}
