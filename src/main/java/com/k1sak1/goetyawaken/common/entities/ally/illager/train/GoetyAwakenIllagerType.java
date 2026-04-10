package com.k1sak1.goetyawaken.common.entities.ally.illager.train;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.k1sak1.goetyawaken.utils.AdvancedBlockFinder;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.BlockTags;
import com.Polarice3.Goety.common.items.ModItems;
import java.util.function.Function;

public class GoetyAwakenIllagerType implements ITrainIllager {

        @Override
        public boolean canSpawn(Level level, BlockPos blockPos, int range) {
                return this.getIllager(level, blockPos, range) != null;
        }

        @Override
        public boolean mobCanTrainTo(Mob mob, Level level, BlockPos blockPos, int range) {
                EntityType<?> entityType = this.getIllager(level, blockPos, range);
                if (entityType == ModEntityType.ENVIOKER_SERVANT.get()) {
                        return mob.getType() == com.Polarice3.Goety.common.entities.ModEntityType.EVOKER_SERVANT.get();
                } else if (entityType == ModEntityType.ROYALGUARD_SERVANT.get()) {
                        return mob.getType() == com.Polarice3.Goety.common.entities.ModEntityType.VINDICATOR_SERVANT
                                        .get();
                } else if (entityType == ModEntityType.SORCERER_SERVANT.get()) {
                        if (mob.getType() == com.Polarice3.Goety.common.entities.ModEntityType.NEOLLAGER.get()) {
                                if (mob instanceof com.Polarice3.Goety.common.entities.ally.illager.Neollager neollager) {
                                        return neollager.isMagic();
                                }
                        } else if (mob.getType() == ModEntityType.SORCERER_SERVANT.get()) {
                                if (mob instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant sorcerer) {
                                        int currentLevel = sorcerer.getSorcererLevel();
                                        switch (currentLevel) {
                                                case 1:
                                                        return checkSorcererLevel2Environment(level, blockPos, range);
                                                case 2:
                                                        return checkSorcererLevel3Environment(level, blockPos, range);
                                                case 3:
                                                        return checkSorcererLevel4Environment(level, blockPos, range);
                                                case 4:
                                                        return checkSorcererLevel5Environment(level, blockPos, range);
                                                case 5:
                                                        return false;
                                                default:
                                                        return false;
                                        }
                                }
                        }
                } else if (entityType == ModEntityType.ILLUSIONER_SERVANT.get()) {
                        if (mob instanceof com.Polarice3.Goety.common.entities.ally.illager.Neollager neollager) {
                                return neollager.isMagic();
                        }
                } else if (entityType == ModEntityType.PREACHER_SERVANT.get()) {
                        if (mob instanceof com.Polarice3.Goety.common.entities.ally.illager.Neollager neollager) {
                                return neollager.isMagic();
                        }
                }
                return false;
        }

        @Override
        public EntityType<? extends Mob> getIllager(Level level, BlockPos blockPos, int range) {
                if (AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                blockState -> blockState.is(ModBlocks.SOUL_RUBY_BLOCK.get()), range, 1)
                                && AdvancedBlockFinder.getNearbyEnchantPower(level, blockPos, range, 48)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState
                                                                .getBlock() instanceof com.Polarice3.Goety.common.blocks.TallSkullBlock
                                                                || blockState.getBlock() instanceof com.Polarice3.Goety.common.blocks.WallTallSkullBlock,
                                                range, 4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.ENCHANTING_TABLE), range, 2)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(
                                                                com.Polarice3.Goety.common.blocks.ModBlocks.SOUL_CANDLESTICK
                                                                                .get()),
                                                range, 8)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(
                                                                com.Polarice3.Goety.common.blocks.ModBlocks.STASH_URN
                                                                                .get()),
                                                range,
                                                8)) {
                        return ModEntityType.ENVIOKER_SERVANT.get();
                } else if (AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                blockState -> blockState
                                                .is(com.Polarice3.Goety.common.blocks.ModBlocks.PALE_STEEL_BLOCK.get()),
                                range,
                                16)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState
                                                                .is(com.Polarice3.Goety.common.blocks.ModBlocks.CURSED_METAL_BLOCK
                                                                                .get()),
                                                range, 16)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.GOLD_BLOCK),
                                                range, 8)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.SMITHING_TABLE), range, 4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.BLAST_FURNACE), range, 4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.ANVIL),
                                                range, 4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(BlockTags.BANNERS),
                                                range, 8)
                                && AdvancedBlockFinder.getNearbyArmorStands(level, blockPos, armorStand -> true, range,
                                                4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(ModBlocks.SOUL_SAPPHIRE_BLOCK.get()), range,
                                                1)) {
                        return ModEntityType.ROYALGUARD_SERVANT.get();
                } else if (checkSorcererLevel1Environment(level, blockPos, range)) {
                        return ModEntityType.SORCERER_SERVANT.get();
                } else if (checkSorcererLevel2Environment(level, blockPos, range)) {
                        return ModEntityType.SORCERER_SERVANT.get();
                } else if (checkSorcererLevel3Environment(level, blockPos, range)) {
                        return ModEntityType.SORCERER_SERVANT.get();
                } else if (checkSorcererLevel4Environment(level, blockPos, range)) {
                        return ModEntityType.SORCERER_SERVANT.get();
                } else if (checkSorcererLevel5Environment(level, blockPos, range)) {
                        return ModEntityType.SORCERER_SERVANT.get();
                } else if (checkPreacherEnvironment(level, blockPos, range)) {
                        return ModEntityType.PREACHER_SERVANT.get();
                } else if (checkIllusionerEnvironment(level, blockPos, range)) {
                        return ModEntityType.ILLUSIONER_SERVANT.get();
                }

                return null;
        }

        private boolean checkSorcererLevel1Environment(Level level, BlockPos blockPos, int range) {
                boolean level1Env = AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                blockState -> blockState.is(Blocks.STONE), range, 32)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.SOUL_CAMPFIRE), range, 2)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.CAMPFIRE),
                                                range, 4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(
                                                                com.Polarice3.Goety.common.blocks.ModBlocks.DARK_ALTAR
                                                                                .get()),
                                                range, 4)
                                && checkPedestalsWithCores(level, blockPos, range)
                                && AdvancedBlockFinder.getNearbyNonEmptyCursedCages(level, blockPos, range, 1)
                                && AdvancedBlockFinder.getNearbyArmorStands(level, blockPos, armorStand -> true, range,
                                                4);

                return level1Env;
        }

        private boolean checkSorcererLevel2Environment(Level level, BlockPos blockPos, int range) {
                boolean level2Env = checkSorcererLevel1Environment(level, blockPos, range)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.IRON_BLOCK),
                                                range, 24)
                                && AdvancedBlockFinder.getNearbyCauldrons(level, blockPos, "WATER", range, 1)
                                && AdvancedBlockFinder.getNearbyCauldrons(level, blockPos, "LAVA", range, 1)
                                && AdvancedBlockFinder.getNearbyCauldrons(level, blockPos, "POWDER_SNOW", range, 1)
                                && AdvancedBlockFinder.getNearbyCauldrons(level, blockPos, "VOID", range, 1)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.BUDDING_AMETHYST), range, 4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(
                                                                com.Polarice3.Goety.common.blocks.ModBlocks.OVERGROWN_ROOTS
                                                                                .get()),
                                                range, 8)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.HONEY_BLOCK),
                                                range, 4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.HONEYCOMB_BLOCK), range, 2);

                return level2Env;
        }

        private boolean checkSorcererLevel3Environment(Level level, BlockPos blockPos, int range) {
                boolean level3Env = checkSorcererLevel2Environment(level, blockPos, range)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.GOLD_BLOCK),
                                                range, 20)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState
                                                                .is(com.Polarice3.Goety.common.blocks.ModBlocks.RESONANCE_CRYSTAL
                                                                                .get()),
                                                range, 1)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.BLUE_ICE),
                                                range, 16)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.LIGHTNING_ROD), range, 8);

                return level3Env;
        }

        private boolean checkSorcererLevel4Environment(Level level, BlockPos blockPos, int range) {
                boolean level4Env = checkSorcererLevel3Environment(level, blockPos, range)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.EMERALD_BLOCK), range, 8)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.FLETCHING_TABLE), range, 2)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.SEA_LANTERN),
                                                range, 2)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(
                                                                com.Polarice3.Goety.common.blocks.ModBlocks.JADE_BLOCK
                                                                                .get()),
                                                range, 4);

                return level4Env;
        }

        private boolean checkSorcererLevel5Environment(Level level, BlockPos blockPos, int range) {
                boolean level5Env = checkSorcererLevel4Environment(level, blockPos, range)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.DIAMOND_BLOCK), range, 8)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.REDSTONE_BLOCK), range, 16)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.MAGMA_BLOCK),
                                                range, 4);

                return level5Env;
        }

        private boolean checkPedestalsWithCores(Level level, BlockPos blockPos, int range) {
                Function<ItemStack, Boolean> hungerCoreCheck = stack -> stack.getItem() == ModItems.HUNGER_CORE.get();
                Function<ItemStack, Boolean> animationCoreCheck = stack -> stack.getItem() == ModItems.ANIMATION_CORE
                                .get();
                Function<ItemStack, Boolean> mysticCoreCheck = stack -> stack.getItem() == ModItems.MYSTIC_CORE.get();
                Function<ItemStack, Boolean> windCoreCheck = stack -> stack.getItem() == ModItems.WIND_CORE.get();
                return AdvancedBlockFinder.getNearbyPedestalsWithItem(level, blockPos, hungerCoreCheck, range, 1)
                                && AdvancedBlockFinder.getNearbyPedestalsWithItem(level, blockPos, animationCoreCheck,
                                                range, 1)
                                && AdvancedBlockFinder.getNearbyPedestalsWithItem(level, blockPos, mysticCoreCheck,
                                                range, 1)
                                && AdvancedBlockFinder.getNearbyPedestalsWithItem(level, blockPos, windCoreCheck, range,
                                                1);
        }

        private boolean checkPreacherEnvironment(Level level, BlockPos blockPos, int range) {
                return AdvancedBlockFinder.getNearbyNonEmptyLecterns(level, blockPos, range, 20)
                                && AdvancedBlockFinder.getNearbyNonEmptyChiseledBookshelves(level, blockPos, range, 16)
                                && AdvancedBlockFinder.getNearbyCauldrons(level, blockPos, "WATER", range, 4);
        }

        private boolean checkIllusionerEnvironment(Level level, BlockPos blockPos, int range) {
                Function<ItemStack, Boolean> mirrorFocusCheck = stack -> stack
                                .getItem() == com.Polarice3.Goety.common.items.ModItems.ILLUSION_FOCUS.get();
                return AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                blockState -> blockState.is(Blocks.TARGET), range, 4)
                                && AdvancedBlockFinder.getNearbyBlocks(level, blockPos,
                                                blockState -> blockState.is(Blocks.WHITE_WOOL)
                                                                || blockState.is(Blocks.ORANGE_WOOL)
                                                                || blockState.is(Blocks.MAGENTA_WOOL)
                                                                || blockState.is(Blocks.LIGHT_BLUE_WOOL)
                                                                || blockState.is(Blocks.YELLOW_WOOL)
                                                                || blockState.is(Blocks.LIME_WOOL)
                                                                || blockState.is(Blocks.PINK_WOOL)
                                                                || blockState.is(Blocks.GRAY_WOOL)
                                                                || blockState.is(Blocks.LIGHT_GRAY_WOOL)
                                                                || blockState.is(Blocks.CYAN_WOOL)
                                                                || blockState.is(Blocks.PURPLE_WOOL)
                                                                || blockState.is(Blocks.BLUE_WOOL)
                                                                || blockState.is(Blocks.BROWN_WOOL)
                                                                || blockState.is(Blocks.GREEN_WOOL)
                                                                || blockState.is(Blocks.RED_WOOL)
                                                                || blockState.is(Blocks.BLACK_WOOL),
                                                range, 24)
                                && AdvancedBlockFinder.getNearbyPedestalsWithItem(level, blockPos, mirrorFocusCheck,
                                                range, 1);
        }
}