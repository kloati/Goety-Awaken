package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.init.ModEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffectInstance;

public class MucilageItem extends Item {
    public MucilageItem() {
        super(new Item.Properties()
                .rarity(net.minecraft.world.item.Rarity.UNCOMMON)
                .stacksTo(64)
                .fireResistant()
                .food(new FoodProperties.Builder()
                        .nutrition(1)
                        .saturationMod(0.1f)
                        .alwaysEat()
                        .effect(() -> new MobEffectInstance(ModEffects.MUCILAGE_POSSESSION.get(), 30 * 20, 0), 1.0f)
                        .effect(() -> new MobEffectInstance(ModEffects.RECOVER.get(), 5 * 20, 0, false, false, false),
                                1.0f)
                        .build()));
    }

}
