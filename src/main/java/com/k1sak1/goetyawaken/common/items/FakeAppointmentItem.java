package com.k1sak1.goetyawaken.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class FakeAppointmentItem extends Item {
    public FakeAppointmentItem() {
        super(new Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public boolean hasCraftingRemainingItem(net.minecraft.world.item.ItemStack stack) {
        return true;
    }

    @Override
    public net.minecraft.world.item.ItemStack getCraftingRemainingItem(net.minecraft.world.item.ItemStack itemStack) {
        return itemStack.copy();
    }
}