package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.storage.StorageBookType;
import net.minecraft.world.item.Rarity;

public class EnderStorageBookSapphire extends EnderStorageBookBase {
    public EnderStorageBookSapphire() {
        super(new Properties().rarity(Rarity.EPIC), StorageBookType.SAPPHIRE);
    }
}
