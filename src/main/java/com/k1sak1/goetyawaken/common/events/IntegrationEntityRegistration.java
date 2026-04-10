package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.compat.touhoulittlemaid.TouhouLittleMaidLoaded;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class IntegrationEntityRegistration {

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        if (TouhouLittleMaidLoaded.TOUHOULITTLEMAID.isLoaded()) {
            event.put(com.k1sak1.goetyawaken.common.entities.ModEntityType.MAID_FAIRY_SERVANT.get(), MaidFairyServant.createFairyAttributes().build());
        }
    }
}