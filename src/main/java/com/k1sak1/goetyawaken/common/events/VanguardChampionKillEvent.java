package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.init.ModTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VanguardChampionKillEvent {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();

        if (killer instanceof VanguardChampion vanguardchampion && !vanguardchampion.isHostile()) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide) {
                if (victim.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
                    victim.spawnAtLocation(
                            new ItemStack(com.Polarice3.Goety.common.items.ModItems.GRAVE_DUST.get(), 1));
                    victim.spawnAtLocation(new ItemStack(com.Polarice3.Goety.common.items.ModItems.ECTOPLASM.get(), 1));
                    if (victim.getType().is(ModTags.EntityTypes.HIGHER_NECROMANCER)) {
                        victim.spawnAtLocation(new ItemStack(ModItems.MUCILAGE.get(), 1));
                    }
                }
            }
        }
    }
}