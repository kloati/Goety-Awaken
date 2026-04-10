package com.k1sak1.goetyawaken.common.storage.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStackList<T> {

    StackListResult<T> add(@Nonnull T stack, int size);

    StackListResult<T> add(@Nonnull T stack);

    @Nullable
    StackListResult<T> remove(@Nonnull T stack, int size);

    @Nullable
    StackListResult<T> remove(@Nonnull T stack);

    int getCount(@Nonnull T stack, int flags);

    default int getCount(@Nonnull T stack) {
        return getCount(stack, IComparer.COMPARE_NBT);
    }

    @Nullable
    T get(@Nonnull T stack, int flags);

    @Nullable
    default T get(@Nonnull T stack) {
        return get(stack, IComparer.COMPARE_NBT);
    }

    @Nullable
    StackListEntry<T> getEntry(@Nonnull T stack, int flags);

    @Nullable
    T get(UUID id);

    void clear();

    boolean isEmpty();

    @Nonnull
    Collection<StackListEntry<T>> getStacks();

    @Nonnull
    Collection<StackListEntry<T>> getStacks(@Nonnull T stack);

    @Nonnull
    IStackList<T> copy();

    int size();
}
