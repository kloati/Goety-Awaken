package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabEventHandler {

    @SubscribeEvent
    public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        event.getEntries().remove(new ItemStack(ModItems.MARBLE_FOCUS.get()));
        event.getEntries().remove(new ItemStack(ModItems.NO_1337_CANDY.get()));
        event.getEntries().remove(new ItemStack(ModItems.PULSE_PIE.get()));
    }
}