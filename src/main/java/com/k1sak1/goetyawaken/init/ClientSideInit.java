package com.k1sak1.goetyawaken.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public class ClientSideInit extends SidedInit {

    public void init() {
        MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.client.ClientEvents.class);
        MinecraftForge.EVENT_BUS.register(ClientInitEvents.class);
    }

}