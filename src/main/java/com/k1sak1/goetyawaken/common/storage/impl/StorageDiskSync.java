package com.k1sak1.goetyawaken.common.storage.impl;

import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.api.*;
import com.k1sak1.goetyawaken.common.storage.network.message.StorageDiskSizeRequestMessage;

import java.util.HashMap;
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
public class StorageDiskSync implements IStorageDiskSync {
    private final Map<UUID, StorageDiskSyncData> data = new HashMap<>();

    @Override
    public void sendRequest(UUID id) {
        ModNetwork.channel.sendToServer(new StorageDiskSizeRequestMessage(id));
    }

    @Override
    public StorageDiskSyncData getData(UUID id) {
        return data.get(id);
    }

    @Override
    public void setData(UUID id, int stored, int capacity) {
        data.put(id, new StorageDiskSyncData(stored, capacity));
    }

    public void clear() {
        data.clear();
    }
}
