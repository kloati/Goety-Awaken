package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.damagecap.DamageCapManager;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DamageCapEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        if (event.getEntity() instanceof AbstractNamelessOne namelessOne) {
            DamageCapManager damageCapManager = namelessOne.getDamageCapManager();
            if (damageCapManager != null) {
                float limitedDamage = damageCapManager.enforceHardDamageCap(event.getSource(), event.getAmount());
                event.setAmount(limitedDamage);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamageEvent(LivingDamageEvent event) {
        if (event.getEntity() instanceof AbstractNamelessOne namelessOne) {
            DamageCapManager damageCapManager = namelessOne.getDamageCapManager();

            if (damageCapManager != null) {
                float limitedDamage = damageCapManager.enforceHardDamageCap(event.getSource(), event.getAmount());
                event.setAmount(limitedDamage);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtEventFinal(LivingHurtEvent event) {
        if (event.getEntity() instanceof AbstractNamelessOne namelessOne) {
            DamageCapManager damageCapManager = namelessOne.getDamageCapManager();
            if (damageCapManager != null) {
                float maxAllowedDamage = namelessOne.getMaxHealth() * DamageCapManager.getHealthLossThresholdRatio();
                if (event.getAmount() > maxAllowedDamage) {
                    event.setAmount(maxAllowedDamage);
                    damageCapManager.recordIllegalDamage(event.getAmount() - maxAllowedDamage,
                            event.getSource().getEntity());
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamageEventFinal(LivingDamageEvent event) {
        if (event.getEntity() instanceof AbstractNamelessOne namelessOne) {
            DamageCapManager damageCapManager = namelessOne.getDamageCapManager();
            if (damageCapManager != null) {
                float maxAllowedDamage = namelessOne.getMaxHealth() * DamageCapManager.getHealthLossThresholdRatio();
                if (event.getAmount() > maxAllowedDamage) {
                    event.setAmount(maxAllowedDamage);
                    damageCapManager.recordIllegalDamage(event.getAmount() - maxAllowedDamage,
                            event.getSource().getEntity());
                }
            }
        }
    }
}