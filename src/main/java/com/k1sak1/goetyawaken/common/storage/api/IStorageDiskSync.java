package com.k1sak1.goetyawaken.common.storage.api;

import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStorageDiskSync {

    void sendRequest(UUID id);

    StorageDiskSyncData getData(UUID id);

    void setData(UUID id, int stored, int capacity);
}
