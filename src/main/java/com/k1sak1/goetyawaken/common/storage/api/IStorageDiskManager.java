package com.k1sak1.goetyawaken.common.storage.api;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStorageDiskManager {

    @Nullable
    IStorageDisk get(UUID id);

    @Nullable
    IStorageDisk getByStack(ItemStack disk);

    Map<UUID, IStorageDisk> getAll();

    void set(UUID id, IStorageDisk disk);

    void remove(UUID id);

    void markForSaving();
}
