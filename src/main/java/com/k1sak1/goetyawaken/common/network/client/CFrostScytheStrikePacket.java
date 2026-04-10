package com.k1sak1.goetyawaken.common.network.client;

import com.k1sak1.goetyawaken.common.items.FrostScytheItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CFrostScytheStrikePacket {
    public static void encode(CFrostScytheStrikePacket packet, FriendlyByteBuf buffer) {
    }

    public static CFrostScytheStrikePacket decode(FriendlyByteBuf buffer) {
        return new CFrostScytheStrikePacket();
    }

    public static void consume(CFrostScytheStrikePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                FrostScytheItem.strike(playerEntity.level(), playerEntity);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}