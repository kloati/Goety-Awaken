package com.k1sak1.goetyawaken.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class TerrorSoul extends Item {
    public TerrorSoul() {
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .stacksTo(1));
    }
}