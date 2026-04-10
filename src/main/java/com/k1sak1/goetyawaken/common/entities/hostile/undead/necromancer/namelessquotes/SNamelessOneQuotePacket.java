package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SNamelessOneQuotePacket {
	private NamelessOneQuote quote;
	private int delay;

	public SNamelessOneQuotePacket(NamelessOneQuote quote, int delay) {
		this.quote = quote;
		this.delay = delay;
	}

	public static void encode(SNamelessOneQuotePacket msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.quote.getID());
		buf.writeInt(msg.delay);
	}

	public static SNamelessOneQuotePacket decode(FriendlyByteBuf buf) {
		return new SNamelessOneQuotePacket(NamelessOneQuote.getByID(buf.readInt()), buf.readInt());
	}

	public static void handle(SNamelessOneQuotePacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			msg.quote.play(msg.delay);
		});
		ctx.get().setPacketHandled(true);
	}
}