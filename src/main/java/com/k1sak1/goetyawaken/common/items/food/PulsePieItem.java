package com.k1sak1.goetyawaken.common.items.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import com.Polarice3.Goety.common.effects.GoetyEffects;

public class PulsePieItem extends Item {
    public PulsePieItem() {
        super(new Properties().rarity(net.minecraft.world.item.Rarity.EPIC).food(new FoodProperties.Builder()
                .nutrition(30)
                .saturationMod(10f)
                .effect(() -> new MobEffectInstance(GoetyEffects.GRAVITY_PULSE.get(), 400, 4, false, false), 1.0f)
                .effect(() -> new MobEffectInstance(GoetyEffects.SHADOW_WALK.get(), 200, 1, false, false), 1.0f)
                .effect(() -> new MobEffectInstance(MobEffects.WITHER, 200, 1, false, false), 1.0f)
                .build()));
    }
}
