package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.client.screen.grid.stack.ClientItemGridStack;
import com.k1sak1.goetyawaken.client.screen.grid.view.IGridView;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import com.k1sak1.goetyawaken.common.storage.grid.stack.ItemGridStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemDeltaMessage {
    private final ItemGridStack stack;
    private final int delta;

    public GridItemDeltaMessage(ItemGridStack stack, int delta) {
        this.stack = stack;
        this.delta = delta;
    }

    public static void encode(GridItemDeltaMessage message, FriendlyByteBuf buffer) {
        ItemGridStack.write(buffer, message.stack);
        buffer.writeInt(message.delta);
    }

    public static GridItemDeltaMessage decode(FriendlyByteBuf buffer) {
        ItemGridStack stack = ItemGridStack.read(buffer);
        int delta = buffer.readInt();
        return new GridItemDeltaMessage(stack, delta);
    }

    public static void handle(GridItemDeltaMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu instanceof EnderAccessLecternContainer container) {
                Object viewObj = container.getView();
                if (viewObj instanceof IGridView view) {
                    ClientItemGridStack clientStack = new ClientItemGridStack(message.stack);
                    view.postChange(clientStack, message.delta);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
