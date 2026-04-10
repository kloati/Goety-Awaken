package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.storage.StorageBookType;
import net.minecraft.world.item.Rarity;

public class EnderStorageBookEmerald extends EnderStorageBookBase {
    public EnderStorageBookEmerald() {
        super(new Properties().rarity(Rarity.RARE), StorageBookType.EMERALD);
    }
}