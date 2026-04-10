package com.k1sak1.goetyawaken.common.magic.construct;

import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableList;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom;
import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

public class MooshroomGiantMold implements IMold {
    private static final List<BlockPos> STONE_BRICK_LOCATIONS = ImmutableList.of(
            new BlockPos(5, -1, 1), new BlockPos(5, -1, -1), new BlockPos(4, -1, 0), new BlockPos(4, -1, 2),
            new BlockPos(4, -1, 3), new BlockPos(4, -1, -2), new BlockPos(4, -1, -3),
            new BlockPos(3, -1, 0), new BlockPos(3, -1, 1), new BlockPos(3, -1, 2), new BlockPos(3, -1, 3),
            new BlockPos(3, -1, 4), new BlockPos(3, -1, -1), new BlockPos(3, -1, -2), new BlockPos(3, -1, -3),
            new BlockPos(3, -1, -4),
            new BlockPos(2, -1, 0), new BlockPos(2, -1, 1), new BlockPos(2, -1, 2), new BlockPos(2, -1, 3),
            new BlockPos(2, -1, 4), new BlockPos(2, -1, -1), new BlockPos(2, -1, -2), new BlockPos(2, -1, -3),
            new BlockPos(2, -1, -4),
            new BlockPos(1, -1, 1), new BlockPos(1, -1, 2), new BlockPos(1, -1, 3), new BlockPos(1, -1, 5),
            new BlockPos(1, -1, -1), new BlockPos(1, -1, -2), new BlockPos(1, -1, -3), new BlockPos(1, -1, -5),
            new BlockPos(0, -1, 0), new BlockPos(0, -1, 2), new BlockPos(0, -1, 3), new BlockPos(0, -1, 4),
            new BlockPos(0, -1, -2), new BlockPos(0, -1, -3), new BlockPos(0, -1, -4),
            new BlockPos(-1, -1, 1), new BlockPos(-1, -1, 2), new BlockPos(-1, -1, 3), new BlockPos(-1, -1, 5),
            new BlockPos(-1, -1, -1), new BlockPos(-1, -1, -2), new BlockPos(-1, -1, -3), new BlockPos(-1, -1, -5),
            new BlockPos(-2, -1, 0), new BlockPos(-2, -1, 1), new BlockPos(-2, -1, 2), new BlockPos(-2, -1, 3),
            new BlockPos(-2, -1, 4), new BlockPos(-2, -1, -1), new BlockPos(-2, -1, -2), new BlockPos(-2, -1, -3),
            new BlockPos(-2, -1, -4),
            new BlockPos(-3, -1, 0), new BlockPos(-3, -1, 1), new BlockPos(-3, -1, 2), new BlockPos(-3, -1, 3),
            new BlockPos(-3, -1, 4), new BlockPos(-3, -1, -1), new BlockPos(-3, -1, -2), new BlockPos(-3, -1, -3),
            new BlockPos(-3, -1, -4),
            new BlockPos(-4, -1, 0), new BlockPos(-4, -1, 2), new BlockPos(-4, -1, 3), new BlockPos(-4, -1, -2),
            new BlockPos(-4, -1, -3),
            new BlockPos(-5, -1, 1), new BlockPos(-5, -1, -1),
            new BlockPos(-6, 0, 1), new BlockPos(-6, 0, -1), new BlockPos(-5, 0, 3), new BlockPos(-5, 0, -3),
            new BlockPos(-3, 0, 5), new BlockPos(-3, 0, -5), new BlockPos(-1, 0, 6), new BlockPos(-1, 0, -6),
            new BlockPos(1, 0, 6), new BlockPos(1, 0, -6), new BlockPos(3, 0, 5), new BlockPos(3, 0, -5),
            new BlockPos(5, 0, 3), new BlockPos(5, 0, -3), new BlockPos(6, 0, 1), new BlockPos(6, 0, -1));
    private static final List<BlockPos> MUSHROOM_COATED_ALLOY_LOCATIONS = ImmutableList.of(
            new BlockPos(0, -1, 1), new BlockPos(0, -1, 5), new BlockPos(0, -1, -1), new BlockPos(0, -1, -5),
            new BlockPos(1, -1, 0), new BlockPos(1, -1, 4), new BlockPos(1, -1, -4),
            new BlockPos(-1, -1, 0), new BlockPos(-1, -1, 4), new BlockPos(-1, -1, -4), new BlockPos(2, -1, 5),
            new BlockPos(2, -1, -5), new BlockPos(-2, -1, 5), new BlockPos(-2, -1, -5),
            new BlockPos(4, -1, 1), new BlockPos(4, -1, -1), new BlockPos(-4, -1, 1), new BlockPos(-4, -1, -1),
            new BlockPos(5, -1, 0), new BlockPos(5, -1, 2), new BlockPos(5, -1, -2),
            new BlockPos(-5, -1, 0), new BlockPos(-5, -1, 2), new BlockPos(-5, -1, -2), new BlockPos(0, 0, 3),
            new BlockPos(0, 0, 6), new BlockPos(0, 0, -3), new BlockPos(0, 0, -6),
            new BlockPos(1, 0, 3), new BlockPos(1, 0, -3), new BlockPos(-1, 0, 3), new BlockPos(-1, 0, -3),
            new BlockPos(2, 0, 3), new BlockPos(2, 0, 6), new BlockPos(2, 0, -3), new BlockPos(2, 0, -6),
            new BlockPos(-2, 0, 3), new BlockPos(-2, 0, 6), new BlockPos(-2, 0, -3), new BlockPos(-2, 0, -6),
            new BlockPos(3, 0, 0), new BlockPos(3, 0, 1), new BlockPos(3, 0, 2), new BlockPos(3, 0, -1),
            new BlockPos(3, 0, -2),
            new BlockPos(-3, 0, 0), new BlockPos(-3, 0, 1), new BlockPos(-3, 0, 2), new BlockPos(-3, 0, -1),
            new BlockPos(-3, 0, -2), new BlockPos(4, 0, 4), new BlockPos(4, 0, -4), new BlockPos(-4, 0, 4),
            new BlockPos(-4, 0, -4),
            new BlockPos(6, 0, 0), new BlockPos(6, 0, 2), new BlockPos(6, 0, -2), new BlockPos(-6, 0, 0),
            new BlockPos(-6, 0, 2), new BlockPos(-6, 0, -2));

    private static final List<BlockPos> DIAMOND_BLOCK_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 0), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, -1));

    private static final List<BlockPos> DARK_METAL_BLOCK_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 2), new BlockPos(0, 0, -2), new BlockPos(1, 0, 2), new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, 2), new BlockPos(-1, 0, -2),
            new BlockPos(2, 0, 0), new BlockPos(2, 0, 1), new BlockPos(2, 0, -1), new BlockPos(-2, 0, 0),
            new BlockPos(-2, 0, 1), new BlockPos(-2, 0, -1));

    private static final List<BlockPos> REINFORCED_REDSTONE_BLOCK_LOCATIONS = ImmutableList.of(
            new BlockPos(2, 0, 2), new BlockPos(2, 0, -2), new BlockPos(-2, 0, 2), new BlockPos(-2, 0, -2));

    private static final List<BlockPos> REDSTONE_BLOCK_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 4), new BlockPos(0, 0, 5), new BlockPos(0, 0, -4), new BlockPos(0, 0, -5),
            new BlockPos(1, 0, 4), new BlockPos(1, 0, 5), new BlockPos(1, 0, -4), new BlockPos(1, 0, -5),
            new BlockPos(-1, 0, 4), new BlockPos(-1, 0, 5), new BlockPos(-1, 0, -4), new BlockPos(-1, 0, -5),
            new BlockPos(2, 0, 4), new BlockPos(2, 0, 5), new BlockPos(2, 0, -4), new BlockPos(2, 0, -5),
            new BlockPos(-2, 0, 4), new BlockPos(-2, 0, 5), new BlockPos(-2, 0, -4), new BlockPos(-2, 0, -5),
            new BlockPos(3, 0, 3), new BlockPos(3, 0, 4), new BlockPos(3, 0, -3), new BlockPos(3, 0, -4),
            new BlockPos(-3, 0, 3), new BlockPos(-3, 0, 4), new BlockPos(-3, 0, -3), new BlockPos(-3, 0, -4),
            new BlockPos(4, 0, 0), new BlockPos(4, 0, 1), new BlockPos(4, 0, 2), new BlockPos(4, 0, 3),
            new BlockPos(4, 0, -1), new BlockPos(4, 0, -2), new BlockPos(4, 0, -3), new BlockPos(-4, 0, 0),
            new BlockPos(-4, 0, 1), new BlockPos(-4, 0, 2), new BlockPos(-4, 0, 3),
            new BlockPos(-4, 0, -1), new BlockPos(-4, 0, -2), new BlockPos(-4, 0, -3), new BlockPos(5, 0, 0),
            new BlockPos(5, 0, 1), new BlockPos(5, 0, 2), new BlockPos(5, 0, -1), new BlockPos(5, 0, -2),
            new BlockPos(-5, 0, 0), new BlockPos(-5, 0, 1), new BlockPos(-5, 0, 2), new BlockPos(-5, 0, -1),
            new BlockPos(-5, 0, -2));

    private static final List<BlockPos> LAVA_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 7),
            new BlockPos(0, 0, -7),

            new BlockPos(1, 0, 7),
            new BlockPos(1, 0, -7),

            new BlockPos(-1, 0, 7),
            new BlockPos(-1, 0, -7),

            new BlockPos(2, 0, 7),
            new BlockPos(2, 0, -7),

            new BlockPos(-2, 0, 7),
            new BlockPos(-2, 0, -7),

            new BlockPos(3, 0, 6),
            new BlockPos(3, 0, -6),

            new BlockPos(-3, 0, 6),
            new BlockPos(-3, 0, -6),

            new BlockPos(4, 0, 5),
            new BlockPos(4, 0, -5),

            new BlockPos(-4, 0, 5),
            new BlockPos(-4, 0, -5),

            new BlockPos(5, 0, 4),
            new BlockPos(5, 0, -4),

            new BlockPos(-5, 0, 4),
            new BlockPos(-5, 0, -4),

            new BlockPos(6, 0, 3),
            new BlockPos(6, 0, -3),

            new BlockPos(-6, 0, 3),
            new BlockPos(-6, 0, -3),

            new BlockPos(7, 0, 0),
            new BlockPos(7, 0, 1),
            new BlockPos(7, 0, -1),
            new BlockPos(7, 0, 2),
            new BlockPos(7, 0, -2),

            new BlockPos(-7, 0, 0),
            new BlockPos(-7, 0, 1),
            new BlockPos(-7, 0, -1),
            new BlockPos(-7, 0, 2),
            new BlockPos(-7, 0, -2),
            new BlockPos(0, -1, 6),
            new BlockPos(0, -1, -6),

            new BlockPos(1, -1, 6),
            new BlockPos(1, -1, -6),

            new BlockPos(-1, -1, 6),
            new BlockPos(-1, -1, -6),

            new BlockPos(2, -1, 6),
            new BlockPos(2, -1, -6),

            new BlockPos(-2, -1, 6),
            new BlockPos(-2, -1, -6),

            new BlockPos(3, -1, 5),
            new BlockPos(3, -1, -5),

            new BlockPos(-3, -1, 5),
            new BlockPos(-3, -1, -5),

            new BlockPos(4, -1, 4),
            new BlockPos(4, -1, -4),

            new BlockPos(-4, -1, 4),
            new BlockPos(-4, -1, -4),

            new BlockPos(5, -1, 3),
            new BlockPos(5, -1, -3),

            new BlockPos(-5, -1, 3),
            new BlockPos(-5, -1, -3),

            new BlockPos(6, -1, 0),
            new BlockPos(6, -1, 1),
            new BlockPos(6, -1, -1),
            new BlockPos(6, -1, 2),
            new BlockPos(6, -1, -2),

            new BlockPos(-6, -1, 0),
            new BlockPos(-6, -1, 1),
            new BlockPos(-6, -1, -1),
            new BlockPos(-6, -1, 2),
            new BlockPos(-6, -1, -2)

    );

    private static final List<BlockPos> CONSUME_LOCATIONS = ImmutableList.of(
            new BlockPos(0, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, 2), new BlockPos(0, 0, 3),
            new BlockPos(0, 0, 4), new BlockPos(0, 0, 5), new BlockPos(0, 0, 7), new BlockPos(0, 0, -1),
            new BlockPos(0, 0, -2), new BlockPos(0, 0, -3), new BlockPos(0, 0, -4), new BlockPos(0, 0, -5),
            new BlockPos(0, 0, -7),
            new BlockPos(1, 0, 0), new BlockPos(1, 0, 1), new BlockPos(1, 0, 2), new BlockPos(1, 0, 3),
            new BlockPos(1, 0, 4), new BlockPos(1, 0, 5), new BlockPos(1, 0, 7), new BlockPos(1, 0, -1),
            new BlockPos(1, 0, -2), new BlockPos(1, 0, -3), new BlockPos(1, 0, -4), new BlockPos(1, 0, -5),
            new BlockPos(1, 0, -7),
            new BlockPos(-1, 0, 0), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, 2), new BlockPos(-1, 0, 3),
            new BlockPos(-1, 0, 4), new BlockPos(-1, 0, 5), new BlockPos(-1, 0, 7), new BlockPos(-1, 0, -1),
            new BlockPos(-1, 0, -2), new BlockPos(-1, 0, -3), new BlockPos(-1, 0, -4), new BlockPos(-1, 0, -5),
            new BlockPos(-1, 0, -7),
            new BlockPos(2, 0, 0), new BlockPos(2, 0, 1), new BlockPos(2, 0, 2), new BlockPos(2, 0, 3),
            new BlockPos(2, 0, 4), new BlockPos(2, 0, 5), new BlockPos(2, 0, 7), new BlockPos(2, 0, -1),
            new BlockPos(2, 0, -2), new BlockPos(2, 0, -3), new BlockPos(2, 0, -4), new BlockPos(2, 0, -5),
            new BlockPos(2, 0, -7),
            new BlockPos(-2, 0, 0), new BlockPos(-2, 0, 1), new BlockPos(-2, 0, 2), new BlockPos(-2, 0, 3),
            new BlockPos(-2, 0, 4), new BlockPos(-2, 0, 5), new BlockPos(-2, 0, 7), new BlockPos(-2, 0, -1),
            new BlockPos(-2, 0, -2), new BlockPos(-2, 0, -3), new BlockPos(-2, 0, -4), new BlockPos(-2, 0, -5),
            new BlockPos(-2, 0, -7),
            new BlockPos(3, 0, 0), new BlockPos(3, 0, 1), new BlockPos(3, 0, 2), new BlockPos(3, 0, 3),
            new BlockPos(3, 0, 4), new BlockPos(3, 0, 6), new BlockPos(3, 0, -1), new BlockPos(3, 0, -2),
            new BlockPos(3, 0, -3), new BlockPos(3, 0, -4), new BlockPos(3, 0, -6),
            new BlockPos(-3, 0, 0), new BlockPos(-3, 0, 1), new BlockPos(-3, 0, 2), new BlockPos(-3, 0, 3),
            new BlockPos(-3, 0, 4), new BlockPos(-3, 0, 6), new BlockPos(-3, 0, -1), new BlockPos(-3, 0, -2),
            new BlockPos(-3, 0, -3), new BlockPos(-3, 0, -4), new BlockPos(-3, 0, -6),
            new BlockPos(4, 0, 0), new BlockPos(4, 0, 1), new BlockPos(4, 0, 2), new BlockPos(4, 0, 3),
            new BlockPos(4, 0, 5), new BlockPos(4, 0, -1), new BlockPos(4, 0, -2), new BlockPos(4, 0, -3),
            new BlockPos(4, 0, -5),
            new BlockPos(-4, 0, 0), new BlockPos(-4, 0, 1), new BlockPos(-4, 0, 2), new BlockPos(-4, 0, 3),
            new BlockPos(-4, 0, 5), new BlockPos(-4, 0, -1), new BlockPos(-4, 0, -2), new BlockPos(-4, 0, -3),
            new BlockPos(-4, 0, -5),
            new BlockPos(5, 0, 0), new BlockPos(5, 0, 1), new BlockPos(5, 0, 2), new BlockPos(5, 0, 4),
            new BlockPos(5, 0, -1), new BlockPos(5, 0, -2), new BlockPos(5, 0, -4),
            new BlockPos(-5, 0, 0), new BlockPos(-5, 0, 1), new BlockPos(-5, 0, 2), new BlockPos(-5, 0, 4),
            new BlockPos(-5, 0, -1), new BlockPos(-5, 0, -2), new BlockPos(-5, 0, -4),
            new BlockPos(6, 0, 3), new BlockPos(6, 0, -3), new BlockPos(-6, 0, 3), new BlockPos(-6, 0, -3),
            new BlockPos(7, 0, 0), new BlockPos(7, 0, 1), new BlockPos(7, 0, 2), new BlockPos(7, 0, -1),
            new BlockPos(7, 0, -2),
            new BlockPos(-7, 0, 0), new BlockPos(-7, 0, 1), new BlockPos(-7, 0, 2), new BlockPos(-7, 0, -1),
            new BlockPos(-7, 0, -2),
            new BlockPos(0, -1, 6), new BlockPos(0, -1, -6), new BlockPos(1, -1, 6), new BlockPos(1, -1, -6),
            new BlockPos(-1, -1, 6), new BlockPos(-1, -1, -6),
            new BlockPos(2, -1, 6), new BlockPos(2, -1, -6), new BlockPos(-2, -1, 6), new BlockPos(-2, -1, -6),
            new BlockPos(3, -1, 5), new BlockPos(3, -1, -5),
            new BlockPos(-3, -1, 5), new BlockPos(-3, -1, -5), new BlockPos(4, -1, 4), new BlockPos(4, -1, -4),
            new BlockPos(-4, -1, 4), new BlockPos(-4, -1, -4),
            new BlockPos(5, -1, 3), new BlockPos(5, -1, -3), new BlockPos(-5, -1, 3), new BlockPos(-5, -1, -3),
            new BlockPos(6, -1, 0), new BlockPos(6, -1, 1), new BlockPos(6, -1, 2),
            new BlockPos(6, -1, -1), new BlockPos(6, -1, -2), new BlockPos(-6, -1, 0), new BlockPos(-6, -1, 1),
            new BlockPos(-6, -1, 2), new BlockPos(-6, -1, -1), new BlockPos(-6, -1, -2));

    private static List<BlockPos> checkStones(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : STONE_BRICK_LOCATIONS) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            BlockState blockState = level.getBlockState(blockPos2);
            if (!blockState.getBlock().getDescriptionId().contains("bricks")) {
                invalid.add(blockPos1);
            }
            if (!blockState.isCollisionShapeFullBlock(level, blockPos2)) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkMushroomCoatedAlloy(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : MUSHROOM_COATED_ALLOY_LOCATIONS) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(ModBlocks.MUSHROOM_COATED_ALLOY_BLOCK.get())) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkDiamondBlocks(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : DIAMOND_BLOCK_LOCATIONS) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.DIAMOND_BLOCK)) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkDarkMetalBlocks(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : DARK_METAL_BLOCK_LOCATIONS) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2)
                    .is(com.Polarice3.Goety.common.blocks.ModBlocks.DARK_ALLOY_BLOCK.get())) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkReinforcedRedstoneBlocks(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : REINFORCED_REDSTONE_BLOCK_LOCATIONS) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2)
                    .is(com.Polarice3.Goety.common.blocks.ModBlocks.REINFORCED_REDSTONE_BLOCK.get())) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkRedstoneBlocks(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : REDSTONE_BLOCK_LOCATIONS) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    private static List<BlockPos> checkLava(Level level, BlockPos blockPos) {
        List<BlockPos> invalid = new ArrayList<>();
        for (BlockPos blockPos1 : LAVA_LOCATIONS) {
            BlockPos blockPos2 = blockPos.offset(blockPos1);
            if (!level.getBlockState(blockPos2).is(Blocks.LAVA)) {
                invalid.add(blockPos1);
            }
        }
        return invalid;
    }

    public static boolean checkBlocks(Level level, BlockPos blockPos) {
        return checkStones(level, blockPos).isEmpty() &&
                checkMushroomCoatedAlloy(level, blockPos).isEmpty() &&
                checkDiamondBlocks(level, blockPos).isEmpty() &&
                checkDarkMetalBlocks(level, blockPos).isEmpty() &&
                checkReinforcedRedstoneBlocks(level, blockPos).isEmpty() &&
                checkRedstoneBlocks(level, blockPos).isEmpty() &&
                checkLava(level, blockPos).isEmpty();
    }

    public static boolean conditionsMet(Level worldIn, LivingEntity entityLiving) {
        int count = 0;
        if (worldIn instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof AngryMooshroom servant) {
                    if (servant.getTrueOwner() == entityLiving && servant.isAlive()) {
                        ++count;
                    }
                }
            }
        }
        return count < Config.MUSHROOM_MONSTROSITY_LIMIT.get();
    }

    @Override
    public boolean spawnServant(Player player, ItemStack stack, Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            if (level.getBlockState(blockPos).is(ModBlocks.SOUL_SAPPHIRE_BLOCK.get())) {
                if (checkBlocks(level, blockPos)) {
                    if (SEHelper.hasResearch(player, ResearchList.TERMINUS)) {
                        if (conditionsMet(level, player)) {
                            MushroomMonstrosity mooshroomGiant = ModEntityType.MUSHROOM_MONSTROSITY.get().create(level);
                            if (mooshroomGiant != null) {
                                mooshroomGiant.setTrueOwner(player);
                                mooshroomGiant.finalizeSpawn((ServerLevelAccessor) level,
                                        level.getCurrentDifficultyAt(mooshroomGiant.blockPosition()),
                                        MobSpawnType.MOB_SUMMONED, null, null);
                                mooshroomGiant.moveTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.05D,
                                        (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                                if (level.addFreshEntity(mooshroomGiant)) {
                                    removeBlocks(level, blockPos);
                                    stack.shrink(1);
                                    if (player instanceof ServerPlayer serverPlayer) {
                                        net.minecraft.advancements.CriteriaTriggers.SUMMONED_ENTITY
                                                .trigger(serverPlayer, mooshroomGiant);
                                        com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers.MUSHROOM_MONSTROSITY
                                                .trigger(serverPlayer);
                                    }
                                    return true;
                                }
                            }
                        } else {
                            player.displayClientMessage(Component.translatable("info.goety.summon.limit"), true);
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("info.goety.research.fail"), true);
                    }
                } else {
                    player.displayClientMessage(Component.translatable("info.goety.block.fail"), true);
                }
            }
        }
        return false;
    }

    public static void removeBlocks(Level level, BlockPos blockPos) {
        if (!level.isClientSide) {
            for (BlockPos blockPos1 : CONSUME_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                BlockState blockState = level.getBlockState(blockPos2);
                if (blockState.is(ModBlocks.SOUL_SAPPHIRE_BLOCK.get()) ||
                        blockState.is(Blocks.DIAMOND_BLOCK) ||
                        blockState.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE) ||
                        blockState.is(com.Polarice3.Goety.common.blocks.ModBlocks.DARK_ALLOY_BLOCK.get()) ||
                        blockState.is(com.Polarice3.Goety.common.blocks.ModBlocks.REINFORCED_REDSTONE_BLOCK.get()) ||
                        blockState.is(ModBlocks.MUSHROOM_COATED_ALLOY_BLOCK.get())) {

                    level.levelEvent(2001, blockPos2, Block.getId(blockState));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
            for (BlockPos blockPos1 : LAVA_LOCATIONS) {
                BlockPos blockPos2 = blockPos.offset(blockPos1);
                if (level.getBlockState(blockPos2).is(Blocks.LAVA)) {
                    level.levelEvent(2001, blockPos2, Block.getId(level.getBlockState(blockPos2)));
                    level.setBlockAndUpdate(blockPos2, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }
}