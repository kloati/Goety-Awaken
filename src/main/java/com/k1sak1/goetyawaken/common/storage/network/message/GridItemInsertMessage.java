package com.k1sak1.goetyawaken.common.storage.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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
public class GridItemInsertMessage {
    private ItemStack stack;
    private boolean shift;

    public GridItemInsertMessage(ItemStack stack, boolean shift) {
        this.stack = stack;
        this.shift = shift;
    }

    public static void encode(GridItemInsertMessage message, FriendlyByteBuf buffer) {
        buffer.writeItem(message.stack);
        buffer.writeBoolean(message.shift);
    }

    public static GridItemInsertMessage decode(FriendlyByteBuf buffer) {
        ItemStack stack = buffer.readItem();
        boolean shift = buffer.readBoolean();
        return new GridItemInsertMessage(stack, shift);
    }

    public static void handle(GridItemInsertMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                handleInsert(player, message.stack, message.shift);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleInsert(ServerPlayer player, ItemStack stack, boolean shift) {
        if (player.containerMenu instanceof com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer container) {
            var gridHandler = container.getItemGridHandler();
            if (gridHandler != null) {
                gridHandler.onInsert(player, stack, shift);
            }
        }
    }
}
