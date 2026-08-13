package com.k1sak1.goetyawaken.common.network.server;

import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellConfig;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellEntry;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.network.client.CSyncSpellConfigPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SSaveSpellConfigPacket {

    private final List<SorcererSpellEntry> entries;

    public SSaveSpellConfigPacket(List<SorcererSpellEntry> entries) {
        this.entries = entries;
    }

    public static void encode(SSaveSpellConfigPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entries.size());
        for (SorcererSpellEntry entry : packet.entries) {
            buf.writeNbt(entry.toNbt());
        }
    }

    public static SSaveSpellConfigPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<SorcererSpellEntry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CompoundTag tag = buf.readNbt();
            if (tag != null) {
                entries.add(SorcererSpellEntry.fromNbt(tag));
            }
        }
        return new SSaveSpellConfigPacket(entries);
    }

    public static void handle(SSaveSpellConfigPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.hasPermissions(2)) {
                SorcererSpellConfig.saveToConfig(packet.entries);
                ModNetwork.sendToALL(new CSyncSpellConfigPacket(SorcererSpellConfig.getSpellEntries()));
                player.displayClientMessage(
                        Component.translatable("message.goetyawaken.spell_config.saved"), false);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
