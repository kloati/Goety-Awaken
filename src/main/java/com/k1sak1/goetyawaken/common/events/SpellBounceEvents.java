package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.utils.SpellBounceHelper;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpellBounceEvents {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof BlockHitResult blockHit)) {
            return;
        }
        if (SpellBounceHelper.tryBounce(event.getProjectile(), blockHit)) {
            event.setCanceled(true);
        }
    }
}
