package com.k1sak1.goetyawaken.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.PaleGolemServant;
import com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers;
import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
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
import net.minecraft.world.level.block.CarvedPumpkinBlock;

import java.util.ArrayList;
import java.util.List;

public class PaleGolemMold implements IMold {
    private static final List<BlockPos> PALE_STEEL_LOCATIONS_X = ImmutableList.of(
            new BlockPos(0, 0, 0), // 中心
            new BlockPos(-1, 0, 0), // 左侧
            new BlockPos(1, 0, 0), // 右侧
            new BlockPos(0, -1, 0) // 下方
    );
    private static final List<BlockPos> PALE_STEEL_LOCATIONS_Z = ImmutableList.of(
            new BlockPos(0, 0, 0), // 中心
            new BlockPos(0, 0, -1), // 前方
            new BlockPos(0, 0, 1), // 后方
            new BlockPos(0, -1, 0) // 下方
    );
    private static final BlockPos HEAD = new BlockPos(0, 1, 0);

    public boolean conditionsMet(Level worldIn, LivingEntity entityLiving) {
        int count = 0;
        if (worldIn instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof PaleGolemServant servant) {
                    if (servant.getTrueOwner() == entityLiving && servant.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        return count < com.k1sak1.goetyawaken.Config.paleGolemLimit;
    }

    private static List<BlockPos> checkPaleSteelX(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : PALE_STEEL_LOCATIONS_X) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.PALE_STEEL_BLOCK.get())) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkPaleSteelZ(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : PALE_STEEL_LOCATIONS_Z) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.PALE_STEEL_BLOCK.get())) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static boolean checkHead(Level level, BlockPos blockPos) {
        BlockPos headPos = blockPos.offset(HEAD);
        return level.getBlockState(headPos).getBlock() instanceof CarvedPumpkinBlock ||
                level.getBlockState(headPos).is(Blocks.JACK_O_LANTERN);
    }

    public static boolean canSpawn(Level level, BlockPos blockPos) {
        if (checkPaleSteelX(level, blockPos).isEmpty()) {
            return checkHead(level, blockPos);
        } else if (checkPaleSteelZ(level, blockPos).isEmpty()) {
            return checkHead(level, blockPos);
        } else {
            return false;
        }
    }

    public static void removeBlocksX(Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            for (BlockPos blockPos1 : PALE_STEEL_LOCATIONS_X) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModBlocks.PALE_STEEL_BLOCK.get())) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            BlockPos headPos = blockPos.offset(HEAD);
            if (level.getBlockState(headPos).getBlock() instanceof CarvedPumpkinBlock ||
                    level.getBlockState(headPos).is(Blocks.JACK_O_LANTERN)) {
                level.levelEvent(2001, headPos, Block.getId(level.getBlockState(headPos)));
                level.setBlockAndUpdate(headPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    public static void removeBlocksZ(Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            for (BlockPos blockPos1 : PALE_STEEL_LOCATIONS_Z) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(ModBlocks.PALE_STEEL_BLOCK.get())) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            BlockPos headPos = blockPos.offset(HEAD);
            if (level.getBlockState(headPos).getBlock() instanceof CarvedPumpkinBlock ||
                    level.getBlockState(headPos).is(Blocks.JACK_O_LANTERN)) {
                level.levelEvent(2001, headPos, Block.getId(level.getBlockState(headPos)));
                level.setBlockAndUpdate(headPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(ModBlocks.PALE_STEEL_BLOCK.get())) {
                if (conditionsMet(level, player)) {
                    if (checkPaleSteelX(level, blockPos).isEmpty()) {
                        PaleGolemServant paleGolem = ModEntityType.PALE_GOLEM_SERVANT.get().create(level);
                        if (paleGolem != null) {
                            paleGolem.setTrueOwner(player);
                            paleGolem.finalizeSpawn((ServerLevelAccessor) level,
                                    level.getCurrentDifficultyAt(paleGolem.blockPosition()), MobSpawnType.MOB_SUMMONED,
                                    null, null);
                            paleGolem.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D,
                                    (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                            paleGolem.setHealth(AttributesConfig.PaleGolemServantHealth.get().floatValue());
                            if (level.addFreshEntity(paleGolem)) {
                                removeBlocksX(level, blockPos);
                                if (!player.getAbilities().instabuild) {
                                    stack.shrink(1);
                                }
                                if (player instanceof ServerPlayer serverPlayer) {
                                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, paleGolem);
                                    ModCriteriaTriggers.SUMMON_PALE_GOLEM.trigger(serverPlayer);
                                }
                                return true;
                            }
                        }
                    } else if (checkPaleSteelZ(level, blockPos).isEmpty()) {
                        PaleGolemServant paleGolem = ModEntityType.PALE_GOLEM_SERVANT.get().create(level);
                        if (paleGolem != null) {
                            paleGolem.setTrueOwner(player);
                            paleGolem.finalizeSpawn((ServerLevelAccessor) level,
                                    level.getCurrentDifficultyAt(paleGolem.blockPosition()), MobSpawnType.MOB_SUMMONED,
                                    null, null);
                            paleGolem.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D,
                                    (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                            paleGolem.setHealth(AttributesConfig.PaleGolemServantHealth.get().floatValue());
                            if (level.addFreshEntity(paleGolem)) {
                                removeBlocksZ(level, blockPos);
                                if (!player.getAbilities().instabuild) {
                                    stack.shrink(1);
                                }
                                if (player instanceof ServerPlayer serverPlayer) {
                                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, paleGolem);
                                    ModCriteriaTriggers.SUMMON_PALE_GOLEM.trigger(serverPlayer);
                                }
                                return true;
                            }
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