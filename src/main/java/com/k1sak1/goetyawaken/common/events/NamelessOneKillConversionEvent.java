package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.utils.ConversionUtil;
import com.k1sak1.goetyawaken.utils.EntityMappingUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken")
public class NamelessOneKillConversionEvent {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();

        Entity killer = getActualKiller(event.getSource());

        if (killer == null) {
            return;
        }

        AbstractNamelessOne namelessOne = getNamelessOneKiller(killer);
        if (namelessOne == null) {
            return;
        }

        if (!EntityMappingUtil.canBeConverted(victim.getType())) {
            return;
        }

        if (!ConversionUtil.canConvert(victim, namelessOne)) {
            return;
        }

        LivingEntity servant = ConversionUtil.convertToServant(victim, namelessOne);

        if (servant != null) {
            event.setCanceled(true);
            victim.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private static Entity getActualKiller(net.minecraft.world.damagesource.DamageSource damageSource) {
        Entity directEntity = damageSource.getDirectEntity();
        if (directEntity instanceof AbstractNamelessOne) {
            return directEntity;
        }

        Entity causingEntity = damageSource.getEntity();
        if (causingEntity instanceof AbstractNamelessOne) {
            return causingEntity;
        }

        if (directEntity != null && isOwnedByNamelessOne(directEntity)) {
            return directEntity;
        }

        if (causingEntity != null && isOwnedByNamelessOne(causingEntity)) {
            return causingEntity;
        }

        return null;
    }

    private static boolean isOwnedByNamelessOne(Entity entity) {
        if (entity instanceof com.Polarice3.Goety.api.entities.IOwned owned) {
            LivingEntity owner = owned.getTrueOwner();
            return owner instanceof AbstractNamelessOne;
        }
        return false;
    }

    private static AbstractNamelessOne getNamelessOneKiller(Entity killer) {
        if (killer instanceof AbstractNamelessOne namelessOne) {
            return namelessOne;
        }

        if (killer instanceof com.Polarice3.Goety.api.entities.IOwned owned) {
            LivingEntity owner = owned.getTrueOwner();
            if (owner instanceof AbstractNamelessOne namelessOne) {
                return namelessOne;
            }
        }

        return null;
    }
}