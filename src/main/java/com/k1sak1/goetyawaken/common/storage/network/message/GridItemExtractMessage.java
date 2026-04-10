package com.k1sak1.goetyawaken.common.storage.network.message;

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
public class GridItemExtractMessage {
    private UUID itemId;
    private int flags;

    public GridItemExtractMessage(UUID itemId, int flags) {
        this.itemId = itemId;
        this.flags = flags;
    }

    public static void encode(GridItemExtractMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.itemId);
        buffer.writeInt(message.flags);
    }

    public static GridItemExtractMessage decode(FriendlyByteBuf buffer) {
        UUID itemId = buffer.readUUID();
        int flags = buffer.readInt();
        return new GridItemExtractMessage(itemId, flags);
    }

    public static void handle(GridItemExtractMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                handleExtract(player, message.itemId, message.flags);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleExtract(ServerPlayer player, UUID itemId, int flags) {
        // TODO: 实现物品提取逻辑
        // 需要获取玩家当前打开的容器并处理提取
        if (player.containerMenu instanceof com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer container) {
            var gridHandler = container.getItemGridHandler();
            if (gridHandler != null) {
                gridHandler.onExtract(player, itemId, flags);
            }
        }
    }
}
