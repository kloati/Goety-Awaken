package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.storage.StorageBookType;
import net.minecraft.world.item.Rarity;

public class EnderStorageBookDiamond extends EnderStorageBookBase {
    public EnderStorageBookDiamond() {
        super(new Properties().rarity(Rarity.EPIC), StorageBookType.DIAMOND);
    }
}