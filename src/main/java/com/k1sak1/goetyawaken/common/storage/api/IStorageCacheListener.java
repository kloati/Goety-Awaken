package com.k1sak1.goetyawaken.common.storage.api;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStorageCacheListener<T> {

    void onChanged(StackListResult<T> result);

    void onChangedBulk(java.util.List<StackListResult<T>> changes);

    void onAttached();

    void onInvalidated();
}
