package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.client.screen.grid.stack.ItemGridStack;
import com.k1sak1.goetyawaken.client.screen.grid.view.IGridView;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class GridItemUpdateMessage {
    private final List<ItemGridStack> stacks;

    public GridItemUpdateMessage(List<ItemGridStack> stacks) {
        this.stacks = stacks;
    }

    public static void encode(GridItemUpdateMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.stacks.size());
        for (ItemGridStack stack : message.stacks) {
            ItemGridStack.write(buffer, stack);
        }
    }

    public static GridItemUpdateMessage decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<ItemGridStack> stacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            stacks.add(ItemGridStack.read(buffer));
        }
        return new GridItemUpdateMessage(stacks);
    }

    public static void handle(GridItemUpdateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu instanceof EnderAccessLecternContainer container) {
                IGridView view = container.getView();
                if (view != null) {
                    view.setStacks(message.stacks);
                    view.forceSort();
                }
            }
        });
        context.setPacketHandled(true);
    }
}
