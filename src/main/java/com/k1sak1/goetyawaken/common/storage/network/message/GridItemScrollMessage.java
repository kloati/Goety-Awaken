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
public class GridItemScrollMessage {
    private final UUID id;
    private final boolean shift;
    private final boolean up;

    public GridItemScrollMessage(UUID id, boolean shift, boolean up) {
        this.id = id;
        this.shift = shift;
        this.up = up;
    }

    public static void encode(GridItemScrollMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.id);
        buffer.writeBoolean(message.shift);
        buffer.writeBoolean(message.up);
    }

    public static GridItemScrollMessage decode(FriendlyByteBuf buffer) {
        return new GridItemScrollMessage(buffer.readUUID(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(GridItemScrollMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof EnderAccessLecternContainer container) {
                var gridHandler = container.getItemGridHandler();
                if (gridHandler != null) {
                    gridHandler.onGridScroll(player, message.id, message.shift, message.up);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
