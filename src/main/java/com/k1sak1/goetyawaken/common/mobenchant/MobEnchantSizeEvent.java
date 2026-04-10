package com.k1sak1.goetyawaken.common.mobenchant;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobEnchantSizeEvent {

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        Entity entity = event.getEntity();

        if (entity instanceof LivingEntity living) {
            MobEnchantCapability cap = MobEnchantEventHandler.getCapabilityFromCache(living);
            if (cap != null && cap.hasMobEnchantment()) {
                int hugeLevel = cap.getMobEnchantLevel(MobEnchantType.HUGE);
                if (hugeLevel > 0) {
                    float scale = 1.0F + (float) hugeLevel * 0.15F;
                    EntityDimensions originalSize = event.getNewSize();
                    float totalWidth = originalSize.width * scale;
                    float totalHeight = originalSize.height * scale;
                    event.setNewSize(EntityDimensions.fixed(totalWidth, totalHeight), true);
                    event.setNewEyeHeight(totalHeight * 0.85f);
                }
            }
        }
    }
}
