package com.k1sak1.goetyawaken.common.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SBossBarPacket {
    public static final int RENDER_TYPE_NORMAL = 0;
    public static final int RENDER_TYPE_ANCIENT = 1;
    public static final int RENDER_TYPE_NAMELESS_ONE = 2;
    public static final int RENDER_TYPE_MUSHROOM = 3;

    private final java.util.UUID bar;
    private final int boss;
    private final boolean remove;
    private final int renderType;

    public SBossBarPacket(java.util.UUID bar, int boss, boolean remove, int renderType) {
        this.bar = bar;
        this.boss = boss;
        this.remove = remove;
        this.renderType = renderType;
    }

    public SBossBarPacket(java.util.UUID bar, Mob boss, boolean remove, int renderType) {
        this.bar = bar;
        this.boss = boss.getId();
        this.remove = remove;
        this.renderType = renderType;
    }

    public static void encode(SBossBarPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.bar);
        buffer.writeInt(packet.boss);
        buffer.writeBoolean(packet.remove);
        buffer.writeInt(packet.renderType);
    }

    public static SBossBarPacket decode(FriendlyByteBuf buffer) {
        return new SBossBarPacket(buffer.readUUID(), buffer.readInt(), buffer.readBoolean(), buffer.readInt());
    }

    public static void consume(SBossBarPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                com.k1sak1.goetyawaken.client.events.CustomBossBarHandler.handleBossBarPacket(
                        packet.bar, packet.boss, packet.remove, packet.renderType);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
