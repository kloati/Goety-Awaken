package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.common.entities.hostile.WitherNecromancer;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.world.data.GAWorldData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID)
public class WitherNecromancerDeathEvent {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof WitherNecromancer witherNecromancer) {
            Level level = witherNecromancer.level();
            GAWorldData worldData = GAWorldData.get(level, Level.NETHER);
            if (worldData != null) {
                boolean prev = worldData.isWitherNecromancerDefeatedOnce();
                if (!prev) {
                    worldData.setWitherNecromancerDefeatedOnce(true);
                }
            }
        }
    }
}
