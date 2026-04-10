package com.k1sak1.goetyawaken.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EnderDispatcherEnchantedBook extends Item {
    public EnderDispatcherEnchantedBook() {
        super(new Item.Properties());
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }
}