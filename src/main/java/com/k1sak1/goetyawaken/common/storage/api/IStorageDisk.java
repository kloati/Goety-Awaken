package com.k1sak1.goetyawaken.common.storage.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IStorageDisk<T> extends IStorage<T> {

    int getCapacity();

    @Nullable
    UUID getOwner();

    void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context);

    CompoundTag writeToNbt();

    ResourceLocation getFactoryId();
}
