package com.k1sak1.goetyawaken.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class EchoingShardItem extends Item {
    public EchoingShardItem() {
        super(new Item.Properties()
                .stacksTo(64)
                .rarity(Rarity.EPIC));
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
}