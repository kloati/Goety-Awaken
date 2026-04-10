package com.k1sak1.goetyawaken.common.storage.network.impl;

import com.k1sak1.goetyawaken.common.storage.network.*;
import net.minecraft.core.BlockPos;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class NetworkNodeGraphImpl implements INetworkNodeGraph {
    private final EnderNetwork network;
    private final List<INetworkNodeGraphEntry> entries = new CopyOnWriteArrayList<>();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public NetworkNodeGraphImpl(EnderNetwork network) {
        this.network = network;
    }

    @Override
    public Collection<INetworkNodeGraphEntry> all() {
        return entries;
    }

    @Override
    public void invalidate() {
        entries.clear();
        scanForNodes();
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    private void scanForNodes() {
        BlockPos center = network.getPosition();
        int range = 2;

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;

                    BlockPos checkPos = center.offset(x, y, z);
                    if (network.getLevel().getBlockEntity(checkPos) instanceof IStorageProvider provider) {
                        entries.add(new NetworkNodeGraphEntryImpl(provider));
                    }
                }
            }
        }
    }

    @Override
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private static class NetworkNodeGraphEntryImpl implements INetworkNodeGraphEntry {
        private final IStorageProvider provider;

        public NetworkNodeGraphEntryImpl(IStorageProvider provider) {
            this.provider = provider;
        }

        @Override
        public INetworkNode getNode() {
            return (INetworkNode) provider;
        }
    }
}
