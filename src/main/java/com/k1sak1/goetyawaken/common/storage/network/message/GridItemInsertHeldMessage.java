package com.k1sak1.goetyawaken.common.storage.network.message;

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
public class GridItemInsertHeldMessage {
    private final boolean single;

    public GridItemInsertHeldMessage(boolean single) {
        this.single = single;
    }

    public static void encode(GridItemInsertHeldMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.single);
    }

    public static GridItemInsertHeldMessage decode(FriendlyByteBuf buffer) {
        return new GridItemInsertHeldMessage(buffer.readBoolean());
    }

    public static void handle(GridItemInsertHeldMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof EnderAccessLecternContainer container) {
                var gridHandler = container.getItemGridHandler();
                if (gridHandler != null) {
                    gridHandler.onInsertHeldItem(player, message.single);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
