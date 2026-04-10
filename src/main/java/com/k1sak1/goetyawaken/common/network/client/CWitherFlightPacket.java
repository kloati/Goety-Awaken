package com.k1sak1.goetyawaken.common.network.client;

import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CWitherFlightPacket {
    private final boolean flyUp;
    private final boolean flyDown;

    public CWitherFlightPacket(boolean flyUp, boolean flyDown) {
        this.flyUp = flyUp;
        this.flyDown = flyDown;
    }

    public static void encode(CWitherFlightPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.flyUp);
        buf.writeBoolean(packet.flyDown);
    }

    public static CWitherFlightPacket decode(FriendlyByteBuf buf) {
        return new CWitherFlightPacket(buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(CWitherFlightPacket packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ServerPlayer player = ctx.getSender();

        if (player != null) {
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof WitherServant witherServant) {
                witherServant.handleFlightControl(packet.flyUp, packet.flyDown);
            }
        }

        ctx.setPacketHandled(true);
    }
}