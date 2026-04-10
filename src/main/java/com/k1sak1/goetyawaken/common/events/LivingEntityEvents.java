package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEntityEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EnderKeeper enderKeeper) {
            if (event.getSource().getEntity() != null
                    && event.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player) {
                if (enderKeeper.getRandom().nextFloat() < 0.075F) {
                    ItemStack eyeOfOverwatchStack = new ItemStack(ModItems.EYE_OF_OVERWATCH.get());
                    ItemEntity itemEntity = enderKeeper.spawnAtLocation(eyeOfOverwatchStack);
                    if (itemEntity != null) {
                        itemEntity.setExtendedLifetime();
                    }
                }
            }
        }
        AbstractNamelessOne namelessOne = getNamelessOneFromKiller(event);
        if (namelessOne != null) {
            if (!namelessOne.level().isClientSide) {
                float healAmount = (float) (namelessOne.getMaxHealth() * 0.007);
                namelessOne.heal(healAmount);
            }
        }
    }

    private static AbstractNamelessOne getNamelessOneFromKiller(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        if (killer instanceof AbstractNamelessOne namelessOne) {
            return namelessOne;
        }
        if (killer instanceof IOwned owned) {
            LivingEntity trueOwner = owned.getTrueOwner();
            if (trueOwner instanceof AbstractNamelessOne namelessOne) {
                return namelessOne;
            }
            LivingEntity masterOwner = owned.getMasterOwner();
            if (masterOwner instanceof AbstractNamelessOne namelessOne) {
                return namelessOne;
            }
        }
        return null;
    }
}