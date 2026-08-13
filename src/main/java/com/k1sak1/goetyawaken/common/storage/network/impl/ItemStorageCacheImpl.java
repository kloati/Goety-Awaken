package com.k1sak1.goetyawaken.common.storage.network.impl;

import com.k1sak1.goetyawaken.common.storage.api.*;
import com.k1sak1.goetyawaken.common.storage.impl.ItemStackList;
import com.k1sak1.goetyawaken.common.storage.impl.StorageAPI;
import com.k1sak1.goetyawaken.common.storage.network.INetworkNodeGraphEntry;
import com.k1sak1.goetyawaken.common.storage.network.IStorageProvider;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class ItemStorageCacheImpl implements IStorageCache<ItemStack> {

    private final EnderNetwork network;
    private final CopyOnWriteArrayList<IStorage<ItemStack>> storages = new CopyOnWriteArrayList<>();
    private final IStackList<ItemStack> list = StorageAPI.instance().createItemStackList();
    private final List<IStorageCacheListener<ItemStack>> listeners = new ArrayList<>();

    public ItemStorageCacheImpl(EnderNetwork network) {
        this.network = network;
    }

    @Override
    public void invalidate(InvalidateCause cause) {

        List<StackListEntry<ItemStack>> oldEntries = new ArrayList<>(list.getStacks());

        storages.clear();
        for (INetworkNodeGraphEntry entry : network.getNodeGraph().all()) {
            if (entry.getNode().isActive() && entry.getNode() instanceof IStorageProvider) {
                ((IStorageProvider) entry.getNode()).addItemStorages(storages);
            }
        }

        sort();

        List<ItemStack> newStacks = new ArrayList<>();
        for (IStorage<ItemStack> storage : storages) {
            if (storage.getAccessType() == AccessType.INSERT) {
                continue;
            }
            for (ItemStack stack : storage.getStacks()) {
                if (!stack.isEmpty()) {
                    newStacks.add(stack);
                }
            }
        }

        ((ItemStackList) list).rebuildPreservingIds(oldEntries, newStacks);

        listeners.forEach(IStorageCacheListener::onInvalidated);
    }

    @Override
    public void add(@Nonnull ItemStack stack, int size, boolean rebuilding, boolean batched) {
        StackListResult<ItemStack> result = list.add(stack, size);

        if (!rebuilding) {
            if (!batched) {
                listeners.forEach(l -> l.onChanged(result));
            }
        }
    }

    @Override
    public void remove(@Nonnull ItemStack stack, int size, boolean batched) {
        StackListResult<ItemStack> result = list.remove(stack, size);

        if (result != null) {
            if (!batched) {
                listeners.forEach(l -> l.onChanged(result));
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void addListener(IStorageCacheListener<ItemStack> listener) {
        listeners.add(listener);
        listener.onAttached();
    }

    @Override
    public void removeListener(IStorageCacheListener<ItemStack> listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
        list.clear();
    }

    @Override
    public void reAttachListeners() {
        listeners.forEach(IStorageCacheListener::onAttached);
    }

    @Override
    public void sort() {
        storages.sort(IStorage.COMPARATOR);
    }

    @Override
    public IStackList<ItemStack> getList() {
        return list;
    }

    @Override
    public IStackList<ItemStack> getCraftablesList() {
        return StorageAPI.instance().createItemStackList();
    }

    @Override
    public List<IStorage<ItemStack>> getStorages() {
        return storages;
    }
}
