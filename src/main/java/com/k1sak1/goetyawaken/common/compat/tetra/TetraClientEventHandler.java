// package com.k1sak1.goetyawaken.common.compat.tetra;

// import com.k1sak1.goetyawaken.common.network.ModNetwork;
// import com.k1sak1.goetyawaken.common.network.client.CBlueMoonSlashPacket;
// import net.minecraft.client.Minecraft;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.item.ItemStack;
// import net.minecraftforge.api.distmarker.Dist;
// import net.minecraftforge.api.distmarker.OnlyIn;
// import net.minecraftforge.client.event.InputEvent;
// import net.minecraftforge.eventbus.api.EventPriority;
// import net.minecraftforge.eventbus.api.SubscribeEvent;
// import se.mickelus.tetra.items.modular.ItemModularHandheld;

// @OnlyIn(Dist.CLIENT)
// public class TetraClientEventHandler {

// @SubscribeEvent(priority = EventPriority.LOWEST)
// public void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
// Minecraft mc = Minecraft.getInstance();
// Player player = mc.player;

// if (player == null) {
// return;
// }

// ItemStack itemStack = player.getMainHandItem();
// if (!(itemStack.getItem() instanceof ItemModularHandheld)) {
// return;
// }

// ItemModularHandheld item = (ItemModularHandheld) itemStack.getItem();

// if (event.isAttack() && !event.isCanceled()) {
// int blueMoonLevel = item.getEffectLevel(itemStack, BlueMoonEffect.BLUE_MOON);
// if (blueMoonLevel > 0) {
// ModNetwork.sendToServer(new CBlueMoonSlashPacket());
// }
// }
// }
// }
