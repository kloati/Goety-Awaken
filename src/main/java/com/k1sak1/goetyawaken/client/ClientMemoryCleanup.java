package com.k1sak1.goetyawaken.client;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.events.AncientBossBarEvent;
import com.k1sak1.goetyawaken.client.events.CustomBossBarHandler;
import com.k1sak1.goetyawaken.client.events.MushroomBossBarEvent;
import com.k1sak1.goetyawaken.client.events.NamelessOneBossBarEvent;
import com.k1sak1.goetyawaken.client.events.MasqueraderBossBarEvent;
import com.k1sak1.goetyawaken.client.events.ClientTrailHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, value = Dist.CLIENT)
public class ClientMemoryCleanup {

    @SubscribeEvent
    public static void onClientPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        performCleanup();

        if (event.getPlayer() != null) {
            try {
                event.getPlayer().invalidateCaps();
            } catch (Exception e) {
            }
        }
    }

    @SubscribeEvent
    public static void onClientPlayerRespawn(ClientPlayerNetworkEvent.Clone event) {
        performCleanup();
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            performCleanup();
        }
    }

    private static void performCleanup() {
        CustomBossBarHandler.clearAll();
        AncientBossBarEvent.clearAll();
        NamelessOneBossBarEvent.clearAll();
        MushroomBossBarEvent.clearAll();
        MasqueraderBossBarEvent.clearAll();
        ClientEvents.clearAllBossMusic();
        ClientTrailHandler.clearAllTrailManagers();

        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() != null) {
            mc.getSoundManager().stop();
        }
    }
}
