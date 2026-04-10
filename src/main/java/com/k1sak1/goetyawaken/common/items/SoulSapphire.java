package com.k1sak1.goetyawaken.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SoulSapphire extends Item {
    public SoulSapphire() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
    }
}