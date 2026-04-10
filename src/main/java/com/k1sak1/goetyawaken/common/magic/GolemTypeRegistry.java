package com.k1sak1.goetyawaken.common.magic;

import com.Polarice3.Goety.api.magic.IMold;
import com.k1sak1.goetyawaken.common.magic.construct.PaleGolemMold;
import com.k1sak1.goetyawaken.common.magic.construct.ShulkerMold;
import com.k1sak1.goetyawaken.common.magic.construct.MooshroomGiantMold;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import java.util.Map;
import com.google.common.collect.Maps;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.MOD)
public class GolemTypeRegistry {
    private static final Map<BlockState, IMold> additionalGolemTypes = Maps.newHashMap();

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            additionalGolemTypes.put(ModBlocks.PALE_STEEL_BLOCK.get().defaultBlockState(), new PaleGolemMold());
            additionalGolemTypes.put(Blocks.SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.WHITE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.ORANGE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.MAGENTA_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.LIGHT_BLUE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.YELLOW_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.LIME_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.PINK_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.GRAY_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.LIGHT_GRAY_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.CYAN_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.PURPLE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.BLUE_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.BROWN_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.GREEN_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.RED_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(Blocks.BLACK_SHULKER_BOX.defaultBlockState(), new ShulkerMold());
            additionalGolemTypes.put(
                    com.k1sak1.goetyawaken.common.blocks.ModBlocks.SOUL_SAPPHIRE_BLOCK.get().defaultBlockState(),
                    new MooshroomGiantMold());
        });
    }

    public static Map<BlockState, IMold> getAdditionalGolemTypes() {
        return additionalGolemTypes;
    }

    public static boolean isAdditionalGolemType(BlockState blockState) {
        return additionalGolemTypes.containsKey(blockState);
    }

    public static IMold getMoldForBlockState(BlockState blockState) {
        return additionalGolemTypes.get(blockState);
    }
}