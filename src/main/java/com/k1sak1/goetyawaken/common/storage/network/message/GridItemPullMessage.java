package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class GridItemPullMessage {
    private final UUID id;
    private final int flags;

    public GridItemPullMessage(UUID id, int flags) {
        this.id = id;
        this.flags = flags;
    }

    public static void encode(GridItemPullMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.id);
        buffer.writeInt(message.flags);
    }

    public static GridItemPullMessage decode(FriendlyByteBuf buffer) {
        return new GridItemPullMessage(buffer.readUUID(), buffer.readInt());
    }

    public static void handle(GridItemPullMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof EnderAccessLecternContainer container) {
                var gridHandler = container.getItemGridHandler();
                if (gridHandler != null) {
                    gridHandler.onExtract(player, message.id, message.flags);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
