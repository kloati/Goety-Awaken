package com.k1sak1.goetyawaken.common.mobenchant;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken")
public class HugeMobEnchant {

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        LivingEntity livingEntity = event.getEntity();
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            MobEnchantCapability cap = MobEnchantEventHandler.getCapability(attacker);
            if (cap != null && cap.hasMobEnchantment()) {
                int hugeLevel = cap.getMobEnchantLevel(MobEnchantType.HUGE);
                if (hugeLevel > 0 && event.getAmount() > 0.0F) {
                    float newDamage = getDamageIncrease(event.getAmount(), hugeLevel);
                    event.setAmount(newDamage);
                }
            }
        }
    }

    public static float getDamageIncrease(float damage, int hugeLevel) {
        if (hugeLevel > 0) {
            damage *= 1.0F + (float) hugeLevel * 0.15F;
        }
        return damage;
    }
}
