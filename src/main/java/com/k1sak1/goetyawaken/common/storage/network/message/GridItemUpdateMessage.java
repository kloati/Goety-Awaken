package com.k1sak1.goetyawaken.common.storage.network.message;

import com.k1sak1.goetyawaken.client.screen.grid.stack.ClientItemGridStack;
import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;
import com.k1sak1.goetyawaken.client.screen.grid.view.IGridView;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import com.k1sak1.goetyawaken.common.storage.grid.stack.ItemGridStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Supplier;

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
                Object viewObj = container.getView();
                if (viewObj instanceof IGridView view) {
                    List<ClientItemGridStack> clientStacks = message.stacks.stream()
                            .map(ClientItemGridStack::new)
                            .collect(Collectors.toList());
                    view.setStacks(clientStacks);
                    view.forceSort();
                }
            }
        });
        context.setPacketHandled(true);
    }
}
