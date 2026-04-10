package com.k1sak1.goetyawaken.utils;

import com.Polarice3.Goety.api.magic.GolemType;
import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.magic.construct.PaleGolemMold;
import com.k1sak1.goetyawaken.common.magic.construct.ShulkerMold;
import com.k1sak1.goetyawaken.common.magic.construct.MooshroomGiantMold;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GolemTypeHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<BlockState, IMold> enhancedGolemList = new ConcurrentHashMap<>();

    private static boolean initialized = false;

    public static void initialize() {
        if (!initialized) {
            try {
                Map<BlockState, IMold> originalList = GolemType.getGolemList();
                enhancedGolemList.putAll(originalList);

                BlockState paleSteelBlockState = ModBlocks.PALE_STEEL_BLOCK.get().defaultBlockState();
                PaleGolemMold paleGolemMold = new PaleGolemMold();
                enhancedGolemList.put(paleSteelBlockState, paleGolemMold);
                enhancedGolemList.put(Blocks.SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.WHITE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.ORANGE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.MAGENTA_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.LIGHT_BLUE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.YELLOW_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.LIME_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.PINK_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.GRAY_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.LIGHT_GRAY_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.CYAN_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.PURPLE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.BLUE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.BROWN_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.GREEN_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.RED_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(Blocks.BLACK_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
                enhancedGolemList.put(
                        com.k1sak1.goetyawaken.common.blocks.ModBlocks.SOUL_SAPPHIRE_BLOCK.get().defaultBlockState(),
                        new MooshroomGiantMold());

                initialized = true;
            } catch (Exception e) {
                LOGGER.error("初始化GolemTypeHelper时出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static Map<BlockState, IMold> getEnhancedGolemList() {
        if (!initialized) {
            initialize();
        }
        return enhancedGolemList;
    }

    public static boolean isSupportedGolemType(BlockState blockState) {
        if (!initialized) {
            initialize();
        }
        return enhancedGolemList.containsKey(blockState);
    }

    public static IMold getMoldForBlockState(BlockState blockState) {
        if (!initialized) {
            initialize();
        }
        return enhancedGolemList.get(blockState);
    }

    public static int getEnhancedGolemListSize() {
        return getEnhancedGolemList().size();
    }

    public static Map<BlockState, IMold> getGolemList() {
        return getEnhancedGolemList();
    }
}