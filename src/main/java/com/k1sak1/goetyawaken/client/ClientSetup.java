package com.k1sak1.goetyawaken.client;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes.NamelessOneQuoteHandler;
import com.k1sak1.goetyawaken.init.ModKeybindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModKeybindings.init();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(NamelessOneQuoteHandler.INSTANCE);
            if (com.k1sak1.goetyawaken.utils.SafeClass.isModernUILoaded()) {
                com.k1sak1.goetyawaken.client.font.ModernUIErosionRenderer.registerCalls();
            }
        });
    }
}