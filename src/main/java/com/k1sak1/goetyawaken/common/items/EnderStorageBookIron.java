package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.storage.StorageBookType;
import net.minecraft.world.item.Rarity;

public class EnderStorageBookIron extends EnderStorageBookBase {
    public EnderStorageBookIron() {
        super(new Properties().rarity(Rarity.COMMON), StorageBookType.IRON);
    }
}