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
public class StackListEntry<T> {
    private final UUID id;
    private final T stack;

    public StackListEntry(T stack) {
        this.id = UUID.randomUUID();
        this.stack = stack;
    }

    public StackListEntry(UUID id, T stack) {
        this.id = id;
        this.stack = stack;
    }

    public UUID getId() {
        return id;
    }

    public T getStack() {
        return stack;
    }
}
