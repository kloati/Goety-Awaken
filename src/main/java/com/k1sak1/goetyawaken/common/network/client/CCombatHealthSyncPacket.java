package com.k1sak1.goetyawaken.common.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CCombatHealthSyncPacket {
    private final int entityId;
    private final float currentCombatHealth;
    private final float peakCombatHealth;

    public CCombatHealthSyncPacket(int entityId, float currentCombatHealth, float peakCombatHealth) {
        this.entityId = entityId;
        this.currentCombatHealth = currentCombatHealth;
        this.peakCombatHealth = peakCombatHealth;
    }

    public static void encode(CCombatHealthSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityId);
        buffer.writeFloat(packet.currentCombatHealth);
        buffer.writeFloat(packet.peakCombatHealth);
    }

    public static CCombatHealthSyncPacket decode(FriendlyByteBuf buffer) {
        int entityId = buffer.readInt();
        float currentCombatHealth = buffer.readFloat();
        float peakCombatHealth = buffer.readFloat();
        return new CCombatHealthSyncPacket(entityId, currentCombatHealth, peakCombatHealth);
    }

    public static void handle(CCombatHealthSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        });
        ctx.get().setPacketHandled(true);
    }
}
