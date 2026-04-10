package com.k1sak1.goetyawaken.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.ShulkerServant;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerMold implements IMold {

    public boolean conditionsMet(Level worldIn, LivingEntity entityLiving) {
        int count = 0;
        if (worldIn instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof ShulkerServant servant) {
                    if (servant.getTrueOwner() == entityLiving && servant.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        return count < com.k1sak1.goetyawaken.Config.shulkerServantLimit;
    }

    public static boolean canSpawn(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        return isShulkerBoxBlock(blockState.getBlock());
    }

    private static boolean isShulkerBoxBlock(Block block) {
        return block instanceof ShulkerBoxBlock;
    }

    public static void removeShulkerBox(Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.is(Blocks.SHULKER_BOX) || isShulkerBoxBlock(blockState.getBlock())) {
                level.levelEvent(2001, blockPos, Block.getId(level.getBlockState(blockPos)));
                level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                if (blockEntity instanceof ShulkerBoxBlockEntity) {
                    ((ShulkerBoxBlockEntity) blockEntity).clearContent();
                }
            }
        }
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            BlockState blockState = level.getBlockState(blockPos);
            if (isShulkerBoxBlock(blockState.getBlock())) {
                if (conditionsMet(level, player)) {
                    ShulkerServant shulkerServant = ModEntityType.SHULKER_SERVANT.get().create(level);
                    if (shulkerServant != null) {
                        shulkerServant.setTrueOwner(player);
                        shulkerServant.finalizeSpawn((ServerLevelAccessor) level,
                                level.getCurrentDifficultyAt(shulkerServant.blockPosition()), MobSpawnType.MOB_SUMMONED,
                                null, null);

                        shulkerServant.moveTo((double) blockPos.getX(), (double) blockPos.getY(),
                                (double) blockPos.getZ(), 0.0F, 0.0F);
                        shulkerServant.setHealth(AttributesConfig.ShulkerServantHealth.get().floatValue());

                        if (level.addFreshEntity(shulkerServant)) {
                            removeShulkerBox(level, blockPos);
                            if (!player.getAbilities().instabuild) {
                                stack.shrink(1);
                            }

                            if (player instanceof ServerPlayer serverPlayer) {

                            }
                            return true;
                        }
                    }
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
                }
            }
        }
        return false;
    }
}