package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.items.magic.GrimoireItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber
public class GrimoireRenameHandler {
    private static final Random RANDOM = new Random();

    private static final String LV2NAME1_PREFIX = "grimoire.lv2.name1.";
    private static final int LV2NAME1_COUNT = 23;

    private static final String LV2NAME2_PREFIX = "grimoire.lv2.name2.";
    private static final int LV2NAME2_COUNT = 14;

    private static final String LV3NAME1_PREFIX = "grimoire.lv3.name1.";
    private static final int LV3NAME1_COUNT = 12;

    private static final String LV3NAME2_PREFIX = "grimoire.lv3.name2.";
    private static final int LV3NAME2_COUNT = 17;

    private static final String LV4NAME1_PREFIX = "grimoire.lv4.name1.";
    private static final int LV4NAME1_COUNT = 15;

    private static final String LV4NAME2_PREFIX = "grimoire.lv4.name2.";
    private static final int LV4NAME2_COUNT = 18;

    private static final String LV5NAME1_PREFIX = "grimoire.lv5.name1.";
    private static final int LV5NAME1_COUNT = 30;

    private static final String LV5NAME2_PREFIX = "grimoire.lv5.name2.";
    private static final int LV5NAME2_COUNT = 21;

    private static final String LV6NAME1_PREFIX = "grimoire.lv6.name1.";
    private static final int LV6NAME1_COUNT = 20;

    private static final String LV6NAME2_PREFIX = "grimoire.lv6.name2.";
    private static final int LV6NAME2_COUNT = 21;

    @SubscribeEvent
    public static void onItemStacked(ItemStackedOnOtherEvent event) {
        ItemStack carriedItem = event.getCarriedItem();
        if (carriedItem.getItem() instanceof GrimoireItem) {
            if (!carriedItem.hasCustomHoverName()) {
                GrimoireItem grimoire = (GrimoireItem) carriedItem.getItem();
                int level = grimoire.getLevel();
                renameGrimoire(carriedItem, level);
            }
        }
    }

    public static void renameGrimoire(ItemStack stack, int level) {
        if (stack.hasCustomHoverName()) {
            return;
        }

        String name1Prefix = "";
        String name2Prefix = "";
        int name1Count = 0;
        int name2Count = 0;

        switch (level) {
            case 2:
                name1Prefix = LV2NAME1_PREFIX;
                name2Prefix = LV2NAME2_PREFIX;
                name1Count = LV2NAME1_COUNT;
                name2Count = LV2NAME2_COUNT;
                break;
            case 3:
                name1Prefix = LV3NAME1_PREFIX;
                name2Prefix = LV3NAME2_PREFIX;
                name1Count = LV3NAME1_COUNT;
                name2Count = LV3NAME2_COUNT;
                break;
            case 4:
                name1Prefix = LV4NAME1_PREFIX;
                name2Prefix = LV4NAME2_PREFIX;
                name1Count = LV4NAME1_COUNT;
                name2Count = LV4NAME2_COUNT;
                break;
            case 5:
                name1Prefix = LV5NAME1_PREFIX;
                name2Prefix = LV5NAME2_PREFIX;
                name1Count = LV5NAME1_COUNT;
                name2Count = LV5NAME2_COUNT;
                break;
            case 6:
                name1Prefix = LV6NAME1_PREFIX;
                name2Prefix = LV6NAME2_PREFIX;
                name1Count = LV6NAME1_COUNT;
                name2Count = LV6NAME2_COUNT;
                break;
        }

        if (name1Count > 0 && name2Count > 0) {
            int name1Index = RANDOM.nextInt(name1Count);
            int name2Index = RANDOM.nextInt(name2Count);

            String name1Key = name1Prefix + name1Index;
            String name2Key = name2Prefix + name2Index;

            MutableComponent name1Component = Component.translatable(name1Key);
            MutableComponent name2Component = Component.translatable(name2Key);

            MutableComponent newName = Component.literal("《")
                    .append(name1Component)
                    .append(name2Component)
                    .append("》");
            ChatFormatting color = ChatFormatting.WHITE;
            switch (level) {
                case 2:
                    color = ChatFormatting.GRAY;
                    break;
                case 3:
                    color = ChatFormatting.YELLOW;
                    break;
                case 4:
                    color = ChatFormatting.GREEN;
                    break;
                case 5:
                    color = ChatFormatting.AQUA;
                    break;
                case 6:
                    color = ChatFormatting.RED;
                    break;
            }

            newName.withStyle(color);
            stack.setHoverName(newName);
        }
    }
}