package com.k1sak1.goetyawaken.common.network.server;

import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.network.client.SApostleProgressSyncPacket;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeData;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CApostleProgressRequestPacket {
    private final int entityId;

    public CApostleProgressRequestPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(CApostleProgressRequestPacket message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
    }

    public static CApostleProgressRequestPacket decode(FriendlyByteBuf buffer) {
        return new CApostleProgressRequestPacket(buffer.readInt());
    }

    public static void handle(CApostleProgressRequestPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            Entity entity = player.level().getEntity(message.entityId);
            if (!(entity instanceof LivingEntity livingEntity)) {
                return;
            }
            if (livingEntity.distanceToSqr(player) > 4096.0) {
                return;
            }
            if (!ApostleUpgradeManager.isMarkedForUpgrade(livingEntity)) {
                return;
            }
            ApostleUpgradeData data = ApostleUpgradeManager.getUpgradeData(livingEntity);
            if (data.getMarkedBy() == null || !data.getMarkedBy().equals(player.getUUID())) {
                return;
            }
            ModNetwork.sendTo(player, new SApostleProgressSyncPacket(livingEntity));
        });
        context.setPacketHandled(true);
    }
}
