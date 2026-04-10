package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.utils.SEHelper;
import com.k1sak1.goetyawaken.common.items.DarkNetheriteBowItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DarkNetheriteBowEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            if (weapon.getItem() instanceof DarkNetheriteBowItem) {
                LivingEntity victim = event.getEntity();
                double baseSouls = SEHelper.getSoulGiven(victim);
                int extraSouls = (int) (baseSouls * 0.5);
                SEHelper.increaseSouls(player, extraSouls);
            }
        } else if (event.getSource().getDirectEntity() != null) {
            Entity directEntity = event.getSource().getDirectEntity();
            if (directEntity instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
                Entity shooter = arrow.getOwner();
                if (shooter instanceof Player player) {
                    ItemStack weapon = player.getMainHandItem();
                    if (weapon.getItem() instanceof DarkNetheriteBowItem) {
                        LivingEntity victim = event.getEntity();
                        double baseSouls = SEHelper.getSoulGiven(victim);
                        int extraSouls = (int) (baseSouls * 0.5);

                        SEHelper.increaseSouls(player, extraSouls);
                    }
                }
            }
        }
    }
}