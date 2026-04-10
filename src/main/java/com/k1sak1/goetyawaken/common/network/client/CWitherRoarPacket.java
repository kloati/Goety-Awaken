package com.k1sak1.goetyawaken.common.network.client;

import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CWitherRoarPacket {
    public CWitherRoarPacket() {
    }

    public static void encode(CWitherRoarPacket packet, FriendlyByteBuf buf) {
    }

    public static CWitherRoarPacket decode(FriendlyByteBuf buf) {
        return new CWitherRoarPacket();
    }

    public static void handle(CWitherRoarPacket packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ServerPlayer player = ctx.getSender();

        if (player != null) {
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof WitherServant witherServant) {
                if (witherServant.getShootCooldown() <= 0) {
                    Vec3 lookVec = player.getLookAngle();
                    double targetX = witherServant.getX() + lookVec.x * 10.0D;
                    double targetY = witherServant.getEyeY() + lookVec.y * 10.0D;
                    double targetZ = witherServant.getZ() + lookVec.z * 10.0D;
                    witherServant.shootSkullAtDirection(targetX, targetY, targetZ);
                    witherServant.setShootCooldown(20);
                }
            }
        }

        ctx.setPacketHandled(true);
    }
}