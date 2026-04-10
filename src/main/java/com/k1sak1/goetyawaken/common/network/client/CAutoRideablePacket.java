package com.k1sak1.goetyawaken.common.network.client;

import com.Polarice3.Goety.api.entities.IAutoRideable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CAutoRideablePacket {
    public static void encode(CAutoRideablePacket packet, FriendlyByteBuf buffer) {
    }

    public static CAutoRideablePacket decode(FriendlyByteBuf buffer) {
        return new CAutoRideablePacket();
    }

    public static void handle(CAutoRideablePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer playerEntity = ctx.get().getSender();

            if (playerEntity != null) {
                if (playerEntity.getVehicle() instanceof IAutoRideable rideable) {
                    rideable.setAutonomous(!rideable.isAutonomous());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}