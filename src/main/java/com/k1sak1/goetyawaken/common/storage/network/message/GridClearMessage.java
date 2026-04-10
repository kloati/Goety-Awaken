package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class GridClearMessage {

    public GridClearMessage() {
    }

    public static void encode(GridClearMessage message, FriendlyByteBuf buffer) {

    }

    public static GridClearMessage decode(FriendlyByteBuf buffer) {
        return new GridClearMessage();
    }

    public static void handle(GridClearMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof EnderAccessLecternContainer container) {
                EnderAccessLecternBlockEntity be = container.getBlockEntity();
                if (be != null) {
                    be.onClear(player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
