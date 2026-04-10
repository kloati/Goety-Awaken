package com.k1sak1.goetyawaken.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.blocks.entity.PoisonousMushroomBlockEntity;
import com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom;
import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import net.minecraftforge.common.extensions.IForgeBlock;

public class PoisonousMushroomBlock extends MushroomBlock
        implements EntityBlock, IForgeBlock, net.minecraft.world.level.block.LiquidBlockContainer {
    protected static final VoxelShape SHAPE = net.minecraft.world.phys.shapes.Shapes.box(4.0D / 16.0D, 0.0D,
            4.0D / 16.0D, 12.0D / 16.0D, 12.0D / 16.0D, 12.0D / 16.0D);

    public PoisonousMushroomBlock(BlockBehaviour.Properties pProperties,
            ResourceKey<ConfiguredFeature<?, ?>> pFeature) {
        super(pProperties, pFeature);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.isFaceSturdy(pLevel, pPos, Direction.UP);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        BlockState blockstate = pLevel.getBlockState(blockpos);
        return blockstate.isFaceSturdy(pLevel, blockpos, Direction.UP);
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pContext) {
        return false;
    }

    @Override
    public void performBonemeal(net.minecraft.server.level.ServerLevel pLevel, RandomSource pRandom, BlockPos pPos,
            BlockState pState) {
        spreadFromBonemeal(pLevel, pPos, pState, pRandom);
    }

    private void spreadFromBonemeal(net.minecraft.server.level.ServerLevel pLevel, BlockPos originPos,
            BlockState originState, RandomSource pRandom) {
        int spreadAttempts = 10;

        for (int i = 0; i < spreadAttempts; ++i) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            mutablePos.set(originPos);
            int spreadDistance = 0;
            for (int j = 0; j < i / 16; ++j) {
                mutablePos.move(
                        pRandom.nextInt(3) - 1,
                        (pRandom.nextInt(3) - 1) * pRandom.nextInt(3) / 2,
                        pRandom.nextInt(3) - 1);
            }
            if (pLevel.isEmptyBlock(mutablePos) && canSurvive(originState, pLevel, mutablePos)) {
                BlockEntity originalBE = pLevel.getBlockEntity(originPos);
                if (pLevel.setBlock(mutablePos, originState, 2)) {
                    BlockEntity newBE = pLevel.getBlockEntity(mutablePos);
                    if (newBE instanceof PoisonousMushroomBlockEntity newMushroomBE &&
                            originalBE instanceof PoisonousMushroomBlockEntity originalMushroomBE) {
                        newMushroomBE.setOwner(originalMushroomBE.getOwner());
                    }
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextInt(10) == 0) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof PoisonousMushroomBlockEntity poisonousMushroomBE) {
                double x = (double) pPos.getX() + 0.5D;
                double y = (double) pPos.getY() + 0.5D;
                double z = (double) pPos.getZ() + 0.5D;

                pLevel.addParticle(ParticleTypes.SMOKE,
                        x + pRandom.nextDouble() * 0.2D - 0.1D,
                        y + pRandom.nextDouble() * 0.2D,
                        z + pRandom.nextDouble() * 0.2D - 0.1D,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide && pEntity instanceof LivingEntity livingEntity) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof PoisonousMushroomBlockEntity poisonousMushroomBE) {
                LivingEntity owner = poisonousMushroomBE.getOwner();
                boolean isProtected = false;
                if (owner != null && livingEntity == owner) {
                    isProtected = true;
                } else if (livingEntity instanceof AngryMooshroom ||
                        livingEntity instanceof MushroomMonstrosity ||
                        livingEntity instanceof MushroomMonstrosityHostile) {
                    isProtected = true;
                } else if (owner != null && MobUtil.areAllies(owner, livingEntity)) {
                    isProtected = true;
                }
                if (!isProtected) {
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 40, 0));
                    livingEntity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 40, 0));
                    pLevel.destroyBlock(pPos, true);
                }
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PoisonousMushroomBlockEntity(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null
                : createTickerHelper(pBlockEntityType, PoisonousMushroomBlockEntity.TYPE,
                        PoisonousMushroomBlockEntity::serverTick);
    }

    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> actual, BlockEntityType<E> required, BlockEntityTicker<? super E> ticker) {
        return required == actual ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState,
            net.minecraft.world.level.material.Fluid pFluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        return false;
    }
}