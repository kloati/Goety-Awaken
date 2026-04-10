package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers;
import com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant;
import com.k1sak1.goetyawaken.common.items.magic.GrimoireItem;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GrimoireEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() == null || event.getTarget() == null) {
            return;
        }

        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        if (itemStack != null && itemStack.getItem() instanceof GrimoireItem grimoire) {
            if (event.getTarget() instanceof SorcererServant sorcererServant) {
                if (sorcererServant.getTrueOwner() == player) {
                    int grimoireLevel = grimoire.getLevel();
                    int currentLevel = sorcererServant.getSorcererLevel();
                    if (currentLevel < grimoireLevel) {
                        if (currentLevel == 5 && grimoireLevel == 6) {
                            if (player instanceof ServerPlayer serverPlayer) {
                                ModCriteriaTriggers.UPGRADE_WIZARD.trigger(serverPlayer);
                            }
                        }
                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                        sorcererServant.setSorcererLevel(currentLevel + 1, true);
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                    } else {
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.FAIL);
                    }
                } else {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                }
            } else if (event.getTarget() instanceof Neollager neollager) {
                if (neollager.getTrueOwner() == player && !neollager.isMagic()) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    neollager.setMagic(true);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                } else {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                }
            }
        }
    }
}