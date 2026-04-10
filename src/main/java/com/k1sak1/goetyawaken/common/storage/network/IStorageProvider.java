package com.k1sak1.goetyawaken.common.storage.network;

import com.k1sak1.goetyawaken.common.storage.api.IStorage;

import java.util.List;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStorageProvider {

    void addItemStorages(List<IStorage<net.minecraft.world.item.ItemStack>> storages);
}
