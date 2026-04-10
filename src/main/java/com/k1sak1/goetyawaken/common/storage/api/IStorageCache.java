package com.k1sak1.goetyawaken.common.storage.api;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStorageCache<T> {

    void invalidate(InvalidateCause cause);

    void add(@Nonnull T stack, int size, boolean rebuilding, boolean batched);

    void remove(@Nonnull T stack, int size, boolean batched);

    void flush();

    void addListener(IStorageCacheListener<T> listener);

    void removeListener(IStorageCacheListener<T> listener);

    void reAttachListeners();

    void sort();

    IStackList<T> getList();

    IStackList<T> getCraftablesList();

    List<IStorage<T>> getStorages();
}
