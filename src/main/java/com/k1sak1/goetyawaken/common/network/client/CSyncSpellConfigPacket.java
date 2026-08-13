package com.k1sak1.goetyawaken.common.network.client;

import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellConfig;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CSyncSpellConfigPacket {

    private final List<SorcererSpellEntry> entries;

    public CSyncSpellConfigPacket(List<SorcererSpellEntry> entries) {
        this.entries = entries;
    }

    public static void encode(CSyncSpellConfigPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entries.size());
        for (SorcererSpellEntry entry : packet.entries) {
            buf.writeNbt(entry.toNbt());
        }
    }

    public static CSyncSpellConfigPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<SorcererSpellEntry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CompoundTag tag = buf.readNbt();
            if (tag != null) {
                entries.add(SorcererSpellEntry.fromNbt(tag));
            }
        }
        return new CSyncSpellConfigPacket(entries);
    }

    public static void handle(CSyncSpellConfigPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> SorcererSpellConfig.applySyncData(packet.entries));
        ctx.get().setPacketHandled(true);
    }
}
