package com.k1sak1.goetyawaken.common.storage.impl;

import com.k1sak1.goetyawaken.common.storage.StorageBookType;
import com.k1sak1.goetyawaken.common.storage.api.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class StorageAPI {
    private static final StorageAPI INSTANCE = new StorageAPI();

    private final IComparer comparer = new Comparer();
    private final IStorageDiskSync storageDiskSync = new StorageDiskSync();

    public static StorageAPI instance() {
        return INSTANCE;
    }

    public IComparer getComparer() {
        return comparer;
    }

    public IStorageDiskManager getStorageDiskManager(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();

        return overworld.getDataStorage().computeIfAbsent(
                tag -> StorageDiskManager.load(tag, overworld),
                () -> new StorageDiskManager(overworld),
                StorageDiskManager.NAME);
    }

    public IStorageDiskSync getStorageDiskSync() {
        return storageDiskSync;
    }

    @Nonnull
    public IStackList<ItemStack> createItemStackList() {
        return new ItemStackList();
    }

    @Nonnull
    public IStorageDisk<ItemStack> createDefaultItemDisk(ServerLevel level, int capacity, @Nullable Player owner) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }

        return new ItemStorageDisk(level, capacity, owner == null ? null : owner.getGameProfile().getId());
    }

    @Nonnull
    public IStorageDisk<ItemStack> createItemDisk(ServerLevel level, StorageBookType type, @Nullable Player owner) {
        return createDefaultItemDisk(level, type.getCapacity(), owner);
    }

    public StorageBookType getStorageBookType(int capacity) {
        return StorageBookType.fromCapacity(capacity);
    }
}
