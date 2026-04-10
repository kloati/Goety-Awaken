package com.k1sak1.goetyawaken.common.blocks;

import com.Polarice3.Goety.common.entities.util.SummonCircleBoss;
import net.minecraft.nbt.CompoundTag;
import com.k1sak1.goetyawaken.common.blocks.entity.AllyPithosBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

public class AllyPithosBlock extends BaseEntityBlock {
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public AllyPithosBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(LOCKED, Boolean.TRUE).setValue(TRIGGERED, Boolean.FALSE));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AllyPithosBlockEntity allyPithos) {
                if (allyPithos.getOwnerUUID() != null && allyPithos.getOwnerUUID().equals(player.getUUID())) {
                    ItemStack itemInHand = player.getItemInHand(hand);
                    if (!state.getValue(LOCKED) && itemInHand.is(Items.RESPAWN_ANCHOR)) {
                        if (!player.getAbilities().instabuild) {
                            itemInHand.shrink(1);
                        }
                        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0F,
                                1.0F);
                        level.setBlock(pos, state.setValue(LOCKED, Boolean.TRUE), 3);
                        allyPithos.setActivated(true);
                        return InteractionResult.CONSUME;
                    } else if (state.getValue(LOCKED) && !state.getValue(TRIGGERED)) {
                        if (!level.isClientSide) {
                            com.k1sak1.goetyawaken.common.entities.ally.undead.SkullLordServant skullLordServant = new com.k1sak1.goetyawaken.common.entities.ally.undead.SkullLordServant(
                                    com.k1sak1.goetyawaken.common.entities.ModEntityType.SKULL_LORD_SERVANT.get(),
                                    level);

                            skullLordServant.setOwnerId(player.getUUID());
                            BlockPos spawnPos = pos.above();
                            Vec3 spawnVec = Vec3.atBottomCenterOf(spawnPos);
                            skullLordServant.setPos(spawnVec.x, spawnVec.y, spawnVec.z);

                            SummonCircleBoss summonCircle = new SummonCircleBoss(level, spawnVec, skullLordServant);
                            level.addFreshEntity(summonCircle);
                            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 3);
                            level.destroyBlock(pos, false);
                        }
                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AllyPithosBlockEntity allyPithos && placer instanceof Player player) {
            allyPithos.setOwnerUUID(player.getUUID());
            allyPithos.setOwnerName(player.getName().getString());
            if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
                CompoundTag blockEntityTag = stack.getTag().getCompound("BlockEntityTag");
                allyPithos.load(blockEntityTag);
                if (blockEntityTag.contains("Locked")) {
                    boolean locked = blockEntityTag.getBoolean("Locked");
                    level.setBlock(pos, state.setValue(LOCKED, locked), 3);
                }
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AllyPithosBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOCKED, TRIGGERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(LOCKED, Boolean.TRUE);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
            @Nullable BlockEntity blockEntity, ItemStack stack) {
        if (!level.isClientSide && !state.getValue(TRIGGERED)) {
            if (blockEntity instanceof AllyPithosBlockEntity allyPithosBlockEntity) {
                ItemStack itemStack = new ItemStack(this);
                CompoundTag tag = allyPithosBlockEntity.getBlockEntityTag();
                tag.putBoolean("Locked", state.getValue(LOCKED));
                itemStack.addTagElement("BlockEntityTag", tag);
                popResource(level, pos, itemStack);
            } else {
                super.playerDestroy(level, player, pos, state, blockEntity, stack);
            }
        } else {
            super.playerDestroy(level, player, pos, state, blockEntity, stack);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(TRIGGERED)) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.75D;
            double z = pos.getZ() + 0.5D;
            level.addParticle(ParticleTypes.ENCHANT, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}