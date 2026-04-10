package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
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
public class GridTransferMessage {
    private final ItemStack[][] recipe;

    public GridTransferMessage(ItemStack[][] recipe) {
        this.recipe = recipe;
    }

    public static void encode(GridTransferMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.recipe.length);
        for (ItemStack[] stacks : message.recipe) {
            buf.writeInt(stacks.length);
            for (ItemStack stack : stacks) {
                buf.writeItem(stack);
            }
        }
    }

    public static GridTransferMessage decode(FriendlyByteBuf buf) {
        int slots = buf.readInt();
        ItemStack[][] recipe = new ItemStack[slots][];
        for (int i = 0; i < slots; i++) {
            int count = buf.readInt();
            recipe[i] = new ItemStack[count];
            for (int j = 0; j < count; j++) {
                recipe[i][j] = buf.readItem();
            }
        }
        return new GridTransferMessage(recipe);
    }

    public static void handle(GridTransferMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof EnderAccessLecternContainer container) {
                EnderAccessLecternBlockEntity be = container.getBlockEntity();
                if (be != null) {
                    be.onRecipeTransfer(player, message.recipe);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
