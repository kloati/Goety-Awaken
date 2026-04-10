package com.k1sak1.goetyawaken.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.List;

public class GildedIngotItem extends Item {
    public GildedIngotItem() {
        super(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE).stacksTo(64).fireResistant());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.goetyawaken.gilded_ingot.description")
                .withStyle(net.minecraft.ChatFormatting.GOLD));
    }
}
