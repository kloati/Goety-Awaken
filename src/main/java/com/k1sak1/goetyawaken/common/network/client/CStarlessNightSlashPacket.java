package com.k1sak1.goetyawaken.common.network.client;

import com.k1sak1.goetyawaken.common.items.StarlessNightItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CStarlessNightSlashPacket {
    public static void encode(CStarlessNightSlashPacket packet, FriendlyByteBuf buffer) {
    }

    public static CStarlessNightSlashPacket decode(FriendlyByteBuf buffer) {
        return new CStarlessNightSlashPacket();
    }

    public static void consume(CStarlessNightSlashPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                StarlessNightItem.fireVoidSlashOnServer(playerEntity.level(), playerEntity);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}