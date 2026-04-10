package com.k1sak1.goetyawaken.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.PedestalBlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Function;

public class AdvancedBlockFinder {

    public static boolean getNearbyBlocks(Level level, BlockPos blockPos, Predicate<BlockState> predicate, int range,
            int totalCount) {
        return getNearbyBlocks(level, blockPos, predicate, range, range, range, totalCount);
    }

    public static boolean getNearbyBlocks(Level level, BlockPos blockPos, Predicate<BlockState> predicate, int xRange,
            int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos blockpos1 = blockPos.offset(i, j, k);
                    BlockState blockstate = level.getBlockState(blockpos1);
                    if (predicate.test(blockstate)) {
                        ++currentCount;
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public static boolean getNearbyEnchantPower(Level level, BlockPos blockPos, int range, int totalPower) {
        return getNearbyEnchantPower(level, blockPos, range, range, range, totalPower);
    }

    public static boolean getNearbyEnchantPower(Level level, BlockPos blockPos, int xRange, int yRange, int zRange,
            int totalPower) {
        int currentPower = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos blockpos1 = blockPos.offset(i, j, k);
                    BlockState blockstate = level.getBlockState(blockpos1);
                    currentPower += blockstate.getEnchantPowerBonus(level, blockpos1);
                }
            }
        }

        return currentPower >= totalPower;
    }

    public static boolean getNearbyCauldrons(Level level, BlockPos blockPos, String fluidType, int range,
            int totalCount) {
        return getNearbyCauldrons(level, blockPos, fluidType, range, range, range, totalCount);
    }

    public static boolean getNearbyCauldrons(Level level, BlockPos blockPos, String fluidType, int xRange, int yRange,
            int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos blockpos1 = blockPos.offset(i, j, k);
                    BlockState blockstate = level.getBlockState(blockpos1);
                    if (isCauldronWithFluid(blockstate, fluidType)) {
                        ++currentCount;
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    private static boolean isCauldronWithFluid(BlockState blockState, String fluidType) {
        Block block = blockState.getBlock();
        if (block == Blocks.CAULDRON) {
            return fluidType.equals("EMPTY");
        } else if (block == Blocks.WATER_CAULDRON) {
            if (fluidType.equals("WATER")) {
                return blockState.getValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL) > 0;
            }
        } else if (block == Blocks.LAVA_CAULDRON) {
            return fluidType.equals("LAVA");
        } else if (block == Blocks.POWDER_SNOW_CAULDRON) {
            if (fluidType.equals("POWDER_SNOW")) {
                return blockState.getValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL) > 0;
            }
        } else if (block == com.Polarice3.Goety.common.blocks.ModBlocks.VOID_CAULDRON.get()) {
            return fluidType.equals("VOID");
        }

        return false;
    }

    public static boolean getNearbyEntities(Level level, BlockPos blockPos, EntityType<?> entityType, int range,
            int totalCount) {
        return getNearbyEntities(level, blockPos, entityType, range, range, range, totalCount);
    }

    public static boolean getNearbyEntities(Level level, BlockPos blockPos, EntityType<?> entityType, int xRange,
            int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        AABB aabb = new AABB(
                blockPos.getX() - xRange, blockPos.getY() - yRange, blockPos.getZ() - zRange,
                blockPos.getX() + xRange + 1, blockPos.getY() + yRange + 1, blockPos.getZ() + zRange + 1);

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb, entity -> entity.getType() == entityType);
        currentCount = entities.size();

        return currentCount >= totalCount;
    }

    public static boolean getNearbyEntities(Level level, BlockPos blockPos, Predicate<Entity> predicate, int range,
            int totalCount) {
        return getNearbyEntities(level, blockPos, predicate, range, range, range, totalCount);
    }

    public static boolean getNearbyEntities(Level level, BlockPos blockPos, Predicate<Entity> predicate, int xRange,
            int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        AABB aabb = new AABB(
                blockPos.getX() - xRange, blockPos.getY() - yRange, blockPos.getZ() - zRange,
                blockPos.getX() + xRange + 1, blockPos.getY() + yRange + 1, blockPos.getZ() + zRange + 1);

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb, predicate);
        currentCount = entities.size();

        return currentCount >= totalCount;
    }

    public static boolean getNearbyArmorStands(Level level, BlockPos blockPos, Predicate<ArmorStand> predicate,
            int range,
            int totalCount) {
        return getNearbyArmorStands(level, blockPos, predicate, range, range, range, totalCount);
    }

    public static boolean getNearbyArmorStands(Level level, BlockPos blockPos, Predicate<ArmorStand> predicate,
            int xRange,
            int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        AABB aabb = new AABB(
                blockPos.getX() - xRange, blockPos.getY() - yRange, blockPos.getZ() - zRange,
                blockPos.getX() + xRange + 1, blockPos.getY() + yRange + 1, blockPos.getZ() + zRange + 1);

        List<ArmorStand> entities = level.getEntitiesOfClass(ArmorStand.class, aabb, predicate);
        currentCount = entities.size();

        return currentCount >= totalCount;
    }

    public static boolean getNearbyNonEmptyCursedCages(Level level, BlockPos blockPos, int range, int totalCount) {
        return getNearbyNonEmptyCursedCages(level, blockPos, range, range, range, totalCount);
    }

    public static boolean getNearbyNonEmptyCursedCages(Level level, BlockPos blockPos, int xRange, int yRange,
            int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos checkPos = blockPos.offset(i, j, k);
                    BlockState blockState = level.getBlockState(checkPos);
                    if (blockState.is(com.Polarice3.Goety.common.blocks.ModBlocks.CURSED_CAGE_BLOCK.get())) {
                        BlockEntity blockEntity = level.getBlockEntity(checkPos);
                        if (blockEntity instanceof CursedCageBlockEntity cursedCage) {
                            if (!cursedCage.getItem().isEmpty()) {
                                ++currentCount;
                            }
                        }
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public static boolean getNearbyNonEmptyLecterns(Level level, BlockPos blockPos, int range, int totalCount) {
        return getNearbyNonEmptyLecterns(level, blockPos, range, range, range, totalCount);
    }

    public static boolean getNearbyNonEmptyLecterns(Level level, BlockPos blockPos, int xRange, int yRange,
            int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos checkPos = blockPos.offset(i, j, k);
                    BlockState blockState = level.getBlockState(checkPos);
                    if (blockState.is(Blocks.LECTERN)) {
                        BlockEntity blockEntity = level.getBlockEntity(checkPos);
                        if (blockEntity instanceof net.minecraft.world.level.block.entity.LecternBlockEntity lectern) {
                            if (!lectern.getBook().isEmpty()) {
                                ++currentCount;
                            }
                        }
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public static boolean getNearbyNonEmptyChiseledBookshelves(Level level, BlockPos blockPos, int range,
            int totalCount) {
        return getNearbyNonEmptyChiseledBookshelves(level, blockPos, range, range, range, totalCount);
    }

    public static boolean getNearbyNonEmptyChiseledBookshelves(Level level, BlockPos blockPos, int xRange, int yRange,
            int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos checkPos = blockPos.offset(i, j, k);
                    BlockState blockState = level.getBlockState(checkPos);
                    if (blockState.is(Blocks.CHISELED_BOOKSHELF)) {
                        BlockEntity blockEntity = level.getBlockEntity(checkPos);
                        if (blockEntity instanceof net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity bookshelf) {
                            for (int slot = 0; slot < bookshelf.getContainerSize(); slot++) {
                                if (!bookshelf.getItem(slot).isEmpty()) {
                                    ++currentCount;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public static boolean getNearbyBlockEntitiesWithNBT(Level level, BlockPos blockPos,
            Function<BlockEntity, Boolean> blockEntityCheck, int range, int totalCount) {
        return getNearbyBlockEntitiesWithNBT(level, blockPos, blockEntityCheck, range, range, range, totalCount);
    }

    public static boolean getNearbyBlockEntitiesWithNBT(Level level, BlockPos blockPos,
            Function<BlockEntity, Boolean> blockEntityCheck, int xRange, int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos checkPos = blockPos.offset(i, j, k);
                    BlockEntity blockEntity = level.getBlockEntity(checkPos);
                    if (blockEntity != null && blockEntityCheck.apply(blockEntity)) {
                        ++currentCount;
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public static boolean getNearbyPedestalsWithItem(Level level, BlockPos blockPos,
            Function<ItemStack, Boolean> itemCheck, int range, int totalCount) {
        return getNearbyPedestalsWithItem(level, blockPos, itemCheck, range, range, range, totalCount);
    }

    public static boolean getNearbyPedestalsWithItem(Level level, BlockPos blockPos,
            Function<ItemStack, Boolean> itemCheck, int xRange, int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        for (int i = -xRange; i <= xRange; ++i) {
            for (int j = -yRange; j <= yRange; ++j) {
                for (int k = -zRange; k <= zRange; ++k) {
                    BlockPos checkPos = blockPos.offset(i, j, k);
                    BlockState blockState = level.getBlockState(checkPos);
                    if (blockState.is(com.Polarice3.Goety.common.blocks.ModBlocks.PEDESTAL.get())) {
                        BlockEntity blockEntity = level.getBlockEntity(checkPos);
                        if (blockEntity instanceof PedestalBlockEntity pedestal) {
                            final int[] localCount = { 0 };
                            pedestal.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                                if (handler.getSlots() > 0) {
                                    ItemStack stack = handler.getStackInSlot(0);
                                    if (!stack.isEmpty() && itemCheck.apply(stack)) {
                                        localCount[0]++;
                                    }
                                }
                            });
                            currentCount += localCount[0];
                        }
                    }
                }
            }
        }

        return currentCount >= totalCount;
    }

    public static boolean getNearbyEntitiesWithNBT(Level level, BlockPos blockPos,
            Function<Entity, Boolean> entityCheck, int range, int totalCount) {
        return getNearbyEntitiesWithNBT(level, blockPos, entityCheck, range, range, range, totalCount);
    }

    public static boolean getNearbyEntitiesWithNBT(Level level, BlockPos blockPos,
            Function<Entity, Boolean> entityCheck, int xRange, int yRange, int zRange, int totalCount) {
        int currentCount = 0;

        AABB aabb = new AABB(
                blockPos.getX() - xRange, blockPos.getY() - yRange, blockPos.getZ() - zRange,
                blockPos.getX() + xRange + 1, blockPos.getY() + yRange + 1, blockPos.getZ() + zRange + 1);

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb, entity -> entityCheck.apply(entity));
        currentCount = entities.size();

        return currentCount >= totalCount;
    }
}