package com.k1sak1.goetyawaken.common.storage.grid;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IItemGridHandler {

    int EXTRACT_HALF = 1 << 0;

    int EXTRACT_SHIFT = 1 << 1;

    int EXTRACT_SINGLE = 1 << 2;

    ItemStack onInsert(ServerPlayer player, ItemStack stack, boolean shift);

    void onExtract(ServerPlayer player, @Nullable java.util.UUID id, int flags);

    void onInsertHeldItem(ServerPlayer player, boolean single);

    void onExtractToCursor(ServerPlayer player, @Nullable java.util.UUID id, int flags);

    void onGridScroll(ServerPlayer player, @Nullable java.util.UUID id, boolean shift, boolean up);
}
