package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.common.entities.projectiles.EchoingStrikeEntity;
import com.k1sak1.goetyawaken.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EchoEffectHandler {
    public static final String ECHO_DAMAGE_MARKER = "echo_damage";

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getMsgId().equals(ECHO_DAMAGE_MARKER)) {
            return;
        }

        if (event.getSource().getEntity() instanceof LivingEntity attacker &&
                (event.getSource().getDirectEntity() == attacker
                        || event.getSource().getDirectEntity() instanceof AbstractArrow)) {

            MobEffectInstance effect = attacker.getEffect(ModEffects.ECHO.get());
            if (effect != null) {
                float echoDamage = event.getAmount() * 0.2f * (effect.getAmplifier() + 1);
                EchoingStrikeEntity echo = new EchoingStrikeEntity(attacker.level(), attacker, echoDamage, 3.0f);
                echo.setOriginalDamageSource(event.getSource());
                echo.setPos(event.getEntity().getBoundingBox().getCenter().subtract(0, echo.getBbHeight() * .5f, 0));
                attacker.level().addFreshEntity(echo);
            }
        }
    }
}