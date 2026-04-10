package com.k1sak1.goetyawaken.common.storage.network;

import com.k1sak1.goetyawaken.common.storage.api.IStorage;
import com.k1sak1.goetyawaken.common.storage.api.IStorageCache;
import com.k1sak1.goetyawaken.common.storage.grid.IItemGridHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface INetwork {

    boolean canRun();

    void update();

    void onRemoved();

    INetworkNodeGraph getNodeGraph();

    IItemGridHandler getItemGridHandler();

    IStorageCache<ItemStack> getItemStorageCache();

    @Nonnull
    ItemStack insertItem(@Nonnull ItemStack stack, int size, com.k1sak1.goetyawaken.common.storage.api.Action action);

    @Nonnull
    ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags,
            com.k1sak1.goetyawaken.common.storage.api.Action action,
            Predicate<IStorage<ItemStack>> filter);

    BlockPos getPosition();

    void markDirty();
}
