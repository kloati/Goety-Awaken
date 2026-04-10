package com.k1sak1.goetyawaken.common.storage.impl;

import com.k1sak1.goetyawaken.client.screen.grid.stack.ItemGridStack;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.api.IStorageCacheListener;
import com.k1sak1.goetyawaken.common.storage.api.StackListEntry;
import com.k1sak1.goetyawaken.common.storage.api.StackListResult;
import com.k1sak1.goetyawaken.common.storage.api.IStorageCache;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemDeltaMessage;
import com.k1sak1.goetyawaken.common.storage.network.message.GridItemUpdateMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class ItemGridStorageCacheListener implements IStorageCacheListener<ItemStack> {
    private final ServerPlayer player;
    private final IStorageCache<ItemStack> cache;

    public ItemGridStorageCacheListener(ServerPlayer player, IStorageCache<ItemStack> cache) {
        this.player = player;
        this.cache = cache;
    }

    @Override
    public void onAttached() {

        List<ItemGridStack> gridStacks = new ArrayList<>();
        for (StackListEntry<ItemStack> entry : cache.getList().getStacks()) {
            ItemStack stack = entry.getStack();
            if (!stack.isEmpty()) {
                gridStacks.add(new ItemGridStack(entry.getId(), null, stack.copy(), false));
            }
        }

        ModNetwork.channel.send(
                PacketDistributor.PLAYER.with(() -> player),
                new GridItemUpdateMessage(gridStacks));
    }

    @Override
    public void onChanged(StackListResult<ItemStack> result) {
        ItemStack stack = result.getStack();
        if (stack == null || stack.isEmpty())
            return;

        ItemGridStack gridStack = new ItemGridStack(result.getId(), null, stack.copy(), false);
        int delta = result.getCount();

        ModNetwork.channel.send(
                PacketDistributor.PLAYER.with(() -> player),
                new GridItemDeltaMessage(gridStack, delta));
    }

    @Override
    public void onChangedBulk(List<StackListResult<ItemStack>> changes) {
        for (StackListResult<ItemStack> change : changes) {
            onChanged(change);
        }
    }

    @Override
    public void onInvalidated() {
        onAttached();
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
