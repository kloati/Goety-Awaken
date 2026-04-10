package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.items.magic.AccessFocus;
import com.k1sak1.goetyawaken.common.network.server.SOpenAccessFocusMessage;
import com.k1sak1.goetyawaken.init.ModKeybindings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, value = Dist.CLIENT)
public class AccessFocusKeyHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !mc.isWindowActive())
            return;

        if (ModKeybindings.keyBindings.length <= 17 || ModKeybindings.keyBindings[17] == null)
            return;
        if (!ModKeybindings.keyBindings[17].isDown())
            return;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isValidAccessFocus(stack)) {
                GoetyAwaken.network.sendToServer(new SOpenAccessFocusMessage(i, null));
                return;
            }
        }

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger foundSlot = new AtomicInteger(-1);
        AtomicReference<String> foundSlotId = new AtomicReference<>(null);

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.getCurios().forEach((slotId, stacksHandler) -> {
                if (found.get())
                    return;

                var stacks = stacksHandler.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    if (isValidAccessFocus(stacks.getStackInSlot(i))) {
                        foundSlot.set(i);
                        foundSlotId.set(slotId);
                        found.set(true);
                        break;
                    }
                }
            });
        });

        if (found.get()) {
            GoetyAwaken.network.sendToServer(
                    new SOpenAccessFocusMessage(foundSlot.get(), foundSlotId.get()));
        }
    }

    private static boolean isValidAccessFocus(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof AccessFocus
                && stack.hasTag()
                && stack.getTag().contains(AccessFocus.NBT_BOUND_POS);
    }
}
