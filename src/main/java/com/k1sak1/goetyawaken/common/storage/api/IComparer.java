package com.k1sak1.goetyawaken.common.storage.api;

import net.minecraft.world.item.ItemStack;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface IComparer {

    int COMPARE_NBT = 1;

    int COMPARE_NO_NBT = 0;

    boolean isEqualNoQuantity(ItemStack left, ItemStack right);

    boolean isEqual(ItemStack left, ItemStack right, int flags);

    default boolean isEqual(ItemStack left, ItemStack right) {
        return isEqual(left, right, COMPARE_NBT);
    }
}
