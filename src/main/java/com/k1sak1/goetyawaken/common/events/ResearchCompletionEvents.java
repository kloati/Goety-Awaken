package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.utils.SEHelper;
import com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ResearchCompletionEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer serverPlayer) {

            if (SEHelper.hasResearch(serverPlayer, com.k1sak1.goetyawaken.common.research.ResearchList.ROYAL)) {
                ModCriteriaTriggers.ROYAL_RESEARCH_COMPLETED.trigger(serverPlayer);
            }

        }
    }
}