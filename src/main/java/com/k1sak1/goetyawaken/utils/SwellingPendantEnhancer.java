package com.k1sak1.goetyawaken.utils;

import com.Polarice3.Goety.common.entities.ally.SlimeServant;
import com.k1sak1.goetyawaken.common.items.curios.SwellingPendantItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class SwellingPendantEnhancer {

    private static final String ENHANCED_KEY = "swelling_pendant_enhanced";
    private static final UUID ATTACK_BONUS_UUID = UUID.fromString("a291c3d4-e5f6-7890-abcd-ec1198049160");
    private static final UUID HEALTH_BONUS_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f11629114031");

    public static void tryEnhance(SlimeServant slime, LivingEntity owner) {
        if (slime == null || owner == null)
            return;
        if (slime.level().isClientSide())
            return;
        if (slime.getPersistentData().getBoolean(ENHANCED_KEY))
            return;

        ItemStack pendantStack = SwellingPendantItem.findPendantStack(owner);
        if (pendantStack.isEmpty())
            return;

        int slimeValue = SwellingPendantItem.getSlimeValue(pendantStack);
        if (slimeValue <= 0)
            return;

        int remaining = slimeValue;
        boolean enhanced = false;

        for (int n = 3; n >= 1; n--) {
            int cost = Math.max(n * n * 2 - 1, 1);
            if (remaining >= cost) {
                slime.setSize(slime.getSize() + n, true);
                remaining -= cost;
                enhanced = true;
                break;
            }
        }

        for (int x = 5; x >= 1; x--) {
            int cost = 2 * x;
            if (remaining >= cost) {
                var attackAttr = slime.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attackAttr != null) {
                    attackAttr.addPermanentModifier(new AttributeModifier(
                            ATTACK_BONUS_UUID, "Swelling pendant bonus",
                            x, AttributeModifier.Operation.ADDITION));
                }
                remaining -= cost;
                enhanced = true;
                break;
            }
        }

        for (int a = 20; a >= 1; a--) {
            if (remaining >= a) {
                var healthAttr = slime.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    healthAttr.addPermanentModifier(new AttributeModifier(
                            HEALTH_BONUS_UUID, "Swelling pendant bonus",
                            a, AttributeModifier.Operation.ADDITION));
                    slime.setHealth(slime.getMaxHealth());
                }
                remaining -= a;
                enhanced = true;
                break;
            }
        }

        if (enhanced) {
            int consumed = slimeValue - remaining;
            SwellingPendantItem.setSlimeValue(pendantStack, remaining);
            slime.getPersistentData().putBoolean(ENHANCED_KEY, true);
            if (owner instanceof ServerPlayer player) {
                player.level().playSound(null, player, SoundEvents.SLIME_SQUISH,
                        SoundSource.PLAYERS, 0.8F, 0.8F);
            }
        }
    }
}
