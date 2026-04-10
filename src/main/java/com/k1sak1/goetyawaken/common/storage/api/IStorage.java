package com.k1sak1.goetyawaken.common.storage.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStorage<T> {

    Comparator<IStorage<?>> COMPARATOR = (left, right) -> {
        int compare = Integer.compare(right.getPriority(), left.getPriority());
        return compare != 0 ? compare : Integer.compare(right.getStored(), left.getStored());
    };

    Collection<T> getStacks();

    @Nonnull
    T insert(@Nonnull T stack, int size, Action action);

    @Nonnull
    T extract(@Nonnull T stack, int size, int flags, Action action);

    int getStored();

    int getPriority();

    AccessType getAccessType();

    int getCacheDelta(int storedPreInsertion, int size, @Nullable T remainder);
}
