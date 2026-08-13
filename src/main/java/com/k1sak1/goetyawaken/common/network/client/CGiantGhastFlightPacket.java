package com.k1sak1.goetyawaken.common.network.client;

import com.k1sak1.goetyawaken.common.entities.hostile.GiantGhast;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CGiantGhastFlightPacket {
    private final boolean flyUp;
    private final boolean flyDown;

    public CGiantGhastFlightPacket(boolean flyUp, boolean flyDown) {
        this.flyUp = flyUp;
        this.flyDown = flyDown;
    }

    public static void encode(CGiantGhastFlightPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.flyUp);
        buf.writeBoolean(packet.flyDown);
    }

    public static CGiantGhastFlightPacket decode(FriendlyByteBuf buf) {
        return new CGiantGhastFlightPacket(buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(CGiantGhastFlightPacket packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ServerPlayer player = ctx.getSender();

        if (player != null) {
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof GiantGhast giantGhast) {
                giantGhast.handleFlightControl(packet.flyUp, packet.flyDown);
            }
        }

        ctx.setPacketHandled(true);
    }
}
