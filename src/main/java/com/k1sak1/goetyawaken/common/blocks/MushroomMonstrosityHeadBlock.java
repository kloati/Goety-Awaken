package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.common.blocks.entity.MushroomMonstrosityHeadBlockEntity;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import com.k1sak1.goetyawaken.common.items.MushroomMonstrosityHeadItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class MushroomMonstrosityHeadBlock extends BaseEntityBlock {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    @Nullable
    private BlockPattern mushroomMonstrosityBase;
    @Nullable
    private BlockPattern mushroomMonstrosityFull;

    public MushroomMonstrosityHeadBlock() {
        super(Properties.of()
                .strength(200.0F, 1000.0F)
                .instrument(NoteBlockInstrument.CUSTOM_HEAD)
                .pushReaction(PushReaction.DESTROY)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(
                this.stateDefinition.any().setValue(ROTATION, 0).setValue(HALF, DoubleBlockHalf.LOWER));
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
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (this.canSpawnGolem(pLevel, pPos)) {
            this.trySpawnGolem(pPlacer, pLevel, pPos, pStack);
        } else {
            BlockEntity tileentity = pLevel.getBlockEntity(pPos);
            if (tileentity instanceof SkullBlockEntity skullEntity) {
                if (skullEntity instanceof MushroomMonstrosityHeadBlockEntity blockEntity) {
                    blockEntity.setOwnerId(MushroomMonstrosityHeadItem.getOwnerID(pStack));
                    if (MushroomMonstrosityHeadItem.getCustomName(pStack) != null) {
                        blockEntity.setCustomName(MushroomMonstrosityHeadItem.getCustomName(pStack));
                    }
                }
            }
        }
        pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public VoxelShape getShape(BlockState p_56331_, BlockGetter p_56332_, BlockPos p_56333_,
            CollisionContext p_56334_) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_56336_, BlockGetter p_56337_, BlockPos p_56338_) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_56321_) {
        return this.defaultBlockState().setValue(ROTATION,
                Integer.valueOf(Mth.floor((double) (p_56321_.getRotation() * 16.0F / 360.0F) + 0.5D) & 15));
    }

    @Override
    public BlockState rotate(BlockState p_56326_, Rotation p_56327_) {
        return p_56326_.setValue(ROTATION, Integer.valueOf(p_56327_.rotate(p_56326_.getValue(ROTATION), 16)));
    }

    @Override
    public BlockState mirror(BlockState p_56323_, Mirror p_56324_) {
        return p_56323_.setValue(ROTATION, Integer.valueOf(p_56324_.mirror(p_56323_.getValue(ROTATION), 16)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ROTATION, HALF);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    private void trySpawnGolem(LivingEntity living, Level p_51379_, BlockPos p_51380_, ItemStack itemStack) {
        BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = this.getOrCreateMushroomMonstrosityFull()
                .find(p_51379_, p_51380_);
        if (blockpattern$blockpatternmatch != null) {
            for (int j = 0; j < this.getOrCreateMushroomMonstrosityFull().getWidth(); ++j) {
                for (int k = 0; k < this.getOrCreateMushroomMonstrosityFull().getHeight(); ++k) {
                    BlockInWorld blockinworld2 = blockpattern$blockpatternmatch.getBlock(j, k, 0);
                    p_51379_.setBlock(blockinworld2.getPos(), Blocks.AIR.defaultBlockState(), 2);
                    p_51379_.levelEvent(2001, blockinworld2.getPos(), Block.getId(blockinworld2.getState()));
                }
            }

            BlockPos blockpos = blockpattern$blockpatternmatch.getBlock(1, 2, 0).getPos();
            MushroomMonstrosity mushroomMonstrosity = ModEntityType.MUSHROOM_MONSTROSITY.get().create(p_51379_);
            if (mushroomMonstrosity != null) {
                if (MushroomMonstrosityHeadItem.getOwnerID(itemStack) != null) {
                    mushroomMonstrosity.setOwnerId(MushroomMonstrosityHeadItem.getOwnerID(itemStack));
                } else if (living != null) {
                    mushroomMonstrosity.setTrueOwner(living);
                }
                String string = MushroomMonstrosityHeadItem.getCustomName(itemStack);
                if (string != null) {
                    mushroomMonstrosity.setCustomName(Component.literal(string));
                }
                mushroomMonstrosity.moveTo((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.05D,
                        (double) blockpos.getZ() + 0.5D, 0.0F, 0.0F);
                if (p_51379_ instanceof ServerLevel serverLevel) {
                    mushroomMonstrosity.finalizeSpawn(serverLevel, p_51379_.getCurrentDifficultyAt(p_51380_),
                            MobSpawnType.MOB_SUMMONED, null, null);
                }
                p_51379_.addFreshEntity(mushroomMonstrosity);
            }

            for (int i1 = 0; i1 < this.getOrCreateMushroomMonstrosityFull().getWidth(); ++i1) {
                for (int j1 = 0; j1 < this.getOrCreateMushroomMonstrosityFull().getHeight(); ++j1) {
                    BlockInWorld blockinworld1 = blockpattern$blockpatternmatch.getBlock(i1, j1, 0);
                    p_51379_.blockUpdated(blockinworld1.getPos(), Blocks.AIR);
                }
            }
        }
    }

    public boolean canSpawnGolem(LevelReader p_51382_, BlockPos p_51383_) {
        return this.getOrCreateMushroomMonstrosityBase().find(p_51382_, p_51383_) != null;
    }

    private BlockPattern getOrCreateMushroomMonstrosityBase() {
        if (this.mushroomMonstrosityBase == null) {
            this.mushroomMonstrosityBase = BlockPatternBuilder.start()
                    .aisle("~~~ ~~~", "D#GGG#D", "J#DHD#J", "~#GGG#~", "~##D##~")
                    .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.REDSTONE_BLOCK)))
                    .where('G',
                            BlockInWorld.hasState(BlockStatePredicate
                                    .forBlock(com.Polarice3.Goety.common.blocks.ModBlocks.DARK_ALLOY_BLOCK.get())))
                    .where('H',
                            BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.SOUL_SAPPHIRE_BLOCK.get())))
                    .where('D',
                            BlockInWorld.hasState(
                                    BlockStatePredicate.forBlock(ModBlocks.MUSHROOM_COATED_ALLOY_BLOCK.get())))
                    .where('J',
                            BlockInWorld.hasState(BlockStatePredicate.forBlock(
                                    com.Polarice3.Goety.common.blocks.ModBlocks.REINFORCED_REDSTONE_BLOCK.get())))
                    .where('~', (p_284869_) -> {
                        return p_284869_.getState().isAir();
                    }).build();
        }

        return this.mushroomMonstrosityBase;
    }

    private BlockPattern getOrCreateMushroomMonstrosityFull() {
        if (this.mushroomMonstrosityFull == null) {
            this.mushroomMonstrosityFull = BlockPatternBuilder.start()
                    .aisle("~~~^~~~", "D#GGG#D", "J#DHD#J", "~#GGG#~", "~##D##~")
                    .where('^', BlockInWorld
                            .hasState(BlockStatePredicate.forBlock(ModBlocks.MOOSHROOM_MONSTROSITY_HEAD.get())
                                    .or(BlockStatePredicate.forBlock(ModBlocks.WALL_MOOSHROOM_MONSTROSITY_HEAD.get()))))
                    .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.REDSTONE_BLOCK)))
                    .where('G',
                            BlockInWorld.hasState(BlockStatePredicate
                                    .forBlock(com.Polarice3.Goety.common.blocks.ModBlocks.DARK_ALLOY_BLOCK.get())))
                    .where('H',
                            BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.SOUL_SAPPHIRE_BLOCK.get())))
                    .where('D',
                            BlockInWorld.hasState(
                                    BlockStatePredicate.forBlock(ModBlocks.MUSHROOM_COATED_ALLOY_BLOCK.get())))
                    .where('J',
                            BlockInWorld.hasState(BlockStatePredicate.forBlock(
                                    com.Polarice3.Goety.common.blocks.ModBlocks.REINFORCED_REDSTONE_BLOCK.get())))
                    .where('~', (p_284869_) -> {
                        return p_284869_.getState().isAir();
                    }).build();
        }

        return this.mushroomMonstrosityFull;
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
                    ? pState.setValue(ROTATION, pFacingState.getValue(ROTATION))
                    : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN
                    && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState()
                            : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }
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
