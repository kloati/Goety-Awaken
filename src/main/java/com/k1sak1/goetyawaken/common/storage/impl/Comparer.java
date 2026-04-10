package com.k1sak1.goetyawaken.common.storage.impl;

import com.k1sak1.goetyawaken.common.storage.api.IComparer;
import net.minecraft.world.item.ItemStack;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class Comparer implements IComparer {
    @Override
    public boolean isEqualNoQuantity(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        if (left.getItem() != right.getItem()) {
            return false;
        }
        return ItemStack.isSameItemSameTags(left, right);
    }

    @Override
    public boolean isEqual(ItemStack left, ItemStack right, int flags) {
        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            return isEqualNoQuantity(left, right);
        }

        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        return left.getItem() == right.getItem();
    }
}
