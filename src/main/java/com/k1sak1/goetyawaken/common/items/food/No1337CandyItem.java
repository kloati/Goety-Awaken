package com.k1sak1.goetyawaken.common.items.food;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import com.Polarice3.Goety.common.effects.GoetyEffects;

public class No1337CandyItem extends Item {
    public No1337CandyItem() {
        super(new Properties().rarity(net.minecraft.world.item.Rarity.EPIC).food(new FoodProperties.Builder()
                .nutrition(13)
                .saturationMod(0.37f)
                .fast()
                .alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.LUCK, 800, 2, false, false), 1.0f)
                .effect(() -> new MobEffectInstance(GoetyEffects.ALTRUISTIC.get(), 400, 0, false, false), 1.0f)
                .build()));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.goetyawaken.no_1337_candy.desc")
                .withStyle(ChatFormatting.GOLD));
    }
}
