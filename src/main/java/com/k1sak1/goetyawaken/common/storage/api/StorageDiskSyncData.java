package com.k1sak1.goetyawaken.common.storage.api;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class StorageDiskSyncData {
    private final int stored;
    private final int capacity;

    public StorageDiskSyncData(int stored, int capacity) {
        this.stored = stored;
        this.capacity = capacity;
    }

    public int getStored() {
        return stored;
    }

    public int getCapacity() {
        return capacity;
    }
}
