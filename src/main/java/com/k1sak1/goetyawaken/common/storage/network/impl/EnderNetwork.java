package com.k1sak1.goetyawaken.common.storage.network.impl;

import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import com.k1sak1.goetyawaken.common.blocks.EchoingEnderShelfBlockEntity;
import com.k1sak1.goetyawaken.common.storage.api.*;
import com.k1sak1.goetyawaken.common.storage.network.*;
import com.k1sak1.goetyawaken.common.storage.grid.IItemGridHandler;
import com.k1sak1.goetyawaken.common.storage.grid.impl.ItemGridHandlerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class EnderNetwork implements INetwork {
    private static final Logger LOGGER = LogManager.getLogger(EnderNetwork.class);

    private final BlockPos pos;
    private final net.minecraft.world.level.Level level;
    private final IItemGridHandler itemGridHandler;
    private final INetworkNodeGraph nodeGraph;
    private final IStorageCache<ItemStack> itemStorage;

    public EnderNetwork(net.minecraft.world.level.Level level, BlockPos pos) {
        this.pos = pos;
        this.level = level;
        this.itemGridHandler = new ItemGridHandlerImpl(this, null);
        this.nodeGraph = new NetworkNodeGraphImpl(this);
        this.itemStorage = new ItemStorageCacheImpl(this);
    }

    public EnderNetwork(net.minecraft.world.level.Level level, BlockPos pos, EnderAccessLecternBlockEntity lectern) {
        this.pos = pos;
        this.level = level;
        this.itemGridHandler = new ItemGridHandlerImpl(this, lectern);
        this.nodeGraph = new NetworkNodeGraphImpl(this);
        this.itemStorage = new ItemStorageCacheImpl(this);
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public void update() {
    }

    @Override
    public void onRemoved() {
    }

    @Override
    public INetworkNodeGraph getNodeGraph() {
        return nodeGraph;
    }

    @Override
    public IItemGridHandler getItemGridHandler() {
        return itemGridHandler;
    }

    @Override
    public IStorageCache<ItemStack> getItemStorageCache() {
        return itemStorage;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(@Nonnull ItemStack stack, int size, Action action) {
        if (stack.isEmpty()) {
            return stack;
        }

        if (itemStorage.getStorages().isEmpty()) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        ItemStack remainder = ItemHandlerHelper.copyStackWithSize(stack, size);

        int inserted = 0;

        for (IStorage<ItemStack> storage : itemStorage.getStorages()) {
            if (storage.getAccessType() == AccessType.EXTRACT) {
                continue;
            }

            int storedPre = storage.getStored();

            remainder = storage.insert(remainder, size, action);

            if (action == Action.PERFORM) {
                inserted += storage.getCacheDelta(storedPre, size, remainder);
            }

            if (remainder.isEmpty()) {
                break;
            } else {
                size = remainder.getCount();
            }
        }

        if (action == Action.PERFORM && inserted > 0) {
            itemStorage.add(stack, inserted, false, false);
            notifyShelvesOfChange();
        }

        return remainder;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, Action action,
            Predicate<IStorage<ItemStack>> filter) {
        if (stack.isEmpty()) {
            return stack;
        }

        int requested = size;
        int received = 0;

        ItemStack newStack = ItemStack.EMPTY;

        for (IStorage<ItemStack> storage : itemStorage.getStorages()) {
            ItemStack took = ItemStack.EMPTY;

            if (filter.test(storage) && storage.getAccessType() != AccessType.INSERT) {
                took = storage.extract(stack, requested - received, flags, action);
            }

            if (!took.isEmpty()) {
                if (newStack.isEmpty()) {
                    newStack = took;
                } else {
                    newStack.grow(took.getCount());
                }

                received += took.getCount();
            }

            if (requested == received) {
                break;
            }
        }

        if (newStack.getCount() > 0 && action == Action.PERFORM) {
            itemStorage.remove(newStack, newStack.getCount(), false);
            notifyShelvesOfChange();
        }

        return newStack;
    }

    private void notifyShelvesOfChange() {
        for (INetworkNodeGraphEntry entry : nodeGraph.all()) {
            if (entry.getNode() instanceof EchoingEnderShelfBlockEntity shelf) {
                shelf.notifyOtherNetworks(this);
            }
        }
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public void markDirty() {

    }

    public net.minecraft.world.level.Level getLevel() {
        return level;
    }
}
