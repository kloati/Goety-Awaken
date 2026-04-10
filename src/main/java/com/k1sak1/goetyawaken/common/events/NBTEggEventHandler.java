package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.items.NBTEntitySpawnEggItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class NBTEggEventHandler {

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        Entity targetEntity = event.getTarget();
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (heldItem.getItem() instanceof NBTEntitySpawnEggItem nbtEggItem) {
                if (player.isCreative() && !NBTEntitySpawnEggItem.hasStoredEntityData(heldItem)) {
                    boolean captured = nbtEggItem.tryCaptureEntityFromEntity(targetEntity, player, heldItem);

                    if (captured) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }
}