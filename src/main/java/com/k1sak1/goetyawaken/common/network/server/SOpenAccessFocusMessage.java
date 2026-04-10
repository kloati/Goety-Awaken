package com.k1sak1.goetyawaken.common.network.server;

import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.items.magic.AccessFocus;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SOpenAccessFocusMessage {
    private final int slot;
    @Nullable
    private final String curioSlot;

    public SOpenAccessFocusMessage(int slot, @Nullable String curioSlot) {
        this.slot = slot;
        this.curioSlot = curioSlot;
    }

    public static void encode(SOpenAccessFocusMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
        boolean hasCurioSlot = message.curioSlot != null;
        buffer.writeBoolean(hasCurioSlot);
        if (hasCurioSlot) {
            buffer.writeUtf(message.curioSlot);
        }
    }

    public static SOpenAccessFocusMessage decode(FriendlyByteBuf buffer) {
        int slot = buffer.readInt();
        String curioSlot = null;
        if (buffer.readBoolean()) {
            curioSlot = buffer.readUtf();
        }
        return new SOpenAccessFocusMessage(slot, curioSlot);
    }

    public static void handle(SOpenAccessFocusMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;

            ItemStack stack = ItemStack.EMPTY;

            if (message.curioSlot == null) {
                stack = player.getInventory().getItem(message.slot);
            } else {
                var curiosInventoryOpt = CuriosApi.getCuriosInventory(player).resolve();
                if (curiosInventoryOpt.isPresent()) {
                    var stacksHandlerOpt = curiosInventoryOpt.get().getStacksHandler(message.curioSlot);
                    if (stacksHandlerOpt.isPresent()) {
                        stack = stacksHandlerOpt.get().getStacks().getStackInSlot(message.slot);
                    }
                }
            }

            if (stack.isEmpty() || !(stack.getItem() instanceof AccessFocus))
                return;
            if (!stack.hasTag() || !stack.getTag().contains(AccessFocus.NBT_BOUND_POS))
                return;

            int soulCost = Config.ACCESS_FOCUS_SOUL_COST.get();
            if (soulCost > 0) {
                if (!SEHelper.getSoulsAmount(player, soulCost))
                    return;
                SEHelper.decreaseSouls(player, soulCost);
            }

            AccessFocus.openBoundLectern(stack, player, player.level());
        });
        context.setPacketHandled(true);
    }
}
