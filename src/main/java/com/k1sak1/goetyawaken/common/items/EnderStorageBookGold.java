package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.storage.StorageBookType;
import net.minecraft.world.item.Rarity;

public class EnderStorageBookGold extends EnderStorageBookBase {
    public EnderStorageBookGold() {
        super(new Properties().rarity(Rarity.UNCOMMON), StorageBookType.GOLD);
    }
}