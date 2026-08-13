package com.k1sak1.goetyawaken.common.items;

import net.minecraft.world.item.Item;

public class GloomyTears extends Item {

    public GloomyTears() {
        super(new Properties()
                .stacksTo(64)
                .rarity(net.minecraft.world.item.Rarity.UNCOMMON)
                .fireResistant());
    }
}
