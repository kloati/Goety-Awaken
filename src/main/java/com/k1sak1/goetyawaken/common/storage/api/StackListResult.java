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
public class StackListResult<T> {
    private final T stack;
    private final UUID id;
    private final int count;

    public StackListResult(T stack, UUID id, int count) {
        this.stack = stack;
        this.id = id;
        this.count = count;
    }

    public T getStack() {
        return stack;
    }

    public UUID getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
