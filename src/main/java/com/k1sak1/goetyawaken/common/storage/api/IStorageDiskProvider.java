package com.k1sak1.goetyawaken.common.storage.api;

import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStorageDiskProvider {

    UUID getId(ItemStack disk);

    void setId(ItemStack disk, UUID id);

    boolean isValid(ItemStack disk);

    int getCapacity(ItemStack disk);

    StorageType getType();

    enum StorageType {
        ITEM,
        FLUID
    }
}
