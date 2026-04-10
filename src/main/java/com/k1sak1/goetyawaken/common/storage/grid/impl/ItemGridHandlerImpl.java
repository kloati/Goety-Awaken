package com.k1sak1.goetyawaken.common.storage.grid.impl;

import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.k1sak1.goetyawaken.common.blocks.EnderAccessLecternBlockEntity;
import com.k1sak1.goetyawaken.common.storage.api.*;
import com.k1sak1.goetyawaken.common.storage.grid.IItemGridHandler;
import com.k1sak1.goetyawaken.common.storage.impl.StorageAPI;
import com.k1sak1.goetyawaken.common.storage.network.impl.EnderNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

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
public class ItemGridHandlerImpl implements IItemGridHandler {
    private final EnderNetwork network;
    @Nullable
    private final EnderAccessLecternBlockEntity lectern;

    public ItemGridHandlerImpl(EnderNetwork network, @Nullable EnderAccessLecternBlockEntity lectern) {
        this.network = network;
        this.lectern = lectern;
    }

    private void consumeSoulEnergy() {
        if (lectern == null)
            return;
        CursedCageBlockEntity cage = lectern.getCursedCage();
        if (cage != null && cage.getSouls() > 0) {
            cage.decreaseSouls(1);
        }
    }

    @Override
    public ItemStack onInsert(ServerPlayer player, ItemStack stack, boolean shift) {
        if (stack.isEmpty()) {
            return stack;
        }

        int size = shift ? stack.getCount() : 1;
        ItemStack remainder = network.insertItem(stack, size, Action.PERFORM);
        if (remainder.getCount() < size) {
            consumeSoulEnergy();
        }
        return remainder;
    }

    @Override
    public void onExtract(ServerPlayer player, @Nullable UUID id, int flags) {
        if (network.getItemStorageCache().getList().isEmpty()) {
            return;
        }

        ItemStack item = id != null ? network.getItemStorageCache().getList().get(id) : null;
        if (item == null || item.isEmpty()) {
            return;
        }

        int itemSize = item.getCount();
        int maxItemSize = item.getItem().getMaxStackSize(item.copy());

        boolean single = (flags & EXTRACT_SINGLE) == EXTRACT_SINGLE;

        ItemStack held = player.containerMenu.getCarried();

        if (single) {
            if (!held.isEmpty() && (!StorageAPI.instance().getComparer().isEqualNoQuantity(item, held)
                    || held.getCount() + 1 > held.getMaxStackSize())) {
                return;
            }
        } else if (!held.isEmpty()) {
            if ((flags & EXTRACT_SHIFT) == 0) {
                return;
            }
        }

        int size = 64;

        if ((flags & EXTRACT_HALF) == EXTRACT_HALF && itemSize > 1) {
            size = itemSize / 2;
            if (size > maxItemSize / 2 && maxItemSize != 1) {
                size = maxItemSize / 2;
            }
        } else if (single) {
            size = 1;
        }

        size = Math.min(size, maxItemSize);

        ItemStack took = network.extractItem(item, size, IComparer.COMPARE_NBT, Action.SIMULATE, s -> true);
        if (took.isEmpty()) {
            return;
        }

        if ((flags & EXTRACT_SHIFT) != 0) {
            if (ItemHandlerHelper.insertItemStacked(
                    new PlayerMainInvWrapper(player.getInventory()), took, true).isEmpty()) {
                took = network.extractItem(item, size, IComparer.COMPARE_NBT, Action.PERFORM, s -> true);
                ItemHandlerHelper.insertItemStacked(
                        new PlayerMainInvWrapper(player.getInventory()), took, false);
                if (!took.isEmpty()) {
                    consumeSoulEnergy();
                }
            }
        } else {
            took = network.extractItem(item, size, IComparer.COMPARE_NBT, Action.PERFORM, s -> true);

            if (!took.isEmpty()) {
                if (single && !held.isEmpty()) {
                    held.grow(took.getCount());
                } else {
                    player.containerMenu.setCarried(took);
                }
                consumeSoulEnergy();
            }
        }
    }

    @Override
    public void onInsertHeldItem(ServerPlayer player, boolean single) {
        ItemStack held = player.containerMenu.getCarried();
        if (held.isEmpty()) {
            return;
        }

        int size = single ? 1 : held.getCount();

        ItemStack simRemainder = network.insertItem(held, size, Action.SIMULATE);
        if (simRemainder.getCount() == size) {
            return;
        }

        ItemStack remainder = network.insertItem(held, size, Action.PERFORM);

        boolean inserted = false;
        if (single) {
            if (remainder.getCount() < size) {
                held.shrink(size - remainder.getCount());
                player.containerMenu.setCarried(held.isEmpty() ? ItemStack.EMPTY : held);
                inserted = true;
            }
        } else {
            player.containerMenu.setCarried(remainder);
            inserted = remainder.getCount() < size;
        }

        if (inserted) {
            consumeSoulEnergy();
        }
    }

    @Override
    public void onExtractToCursor(ServerPlayer player, @Nullable UUID id, int flags) {
        onExtract(player, id, flags);
    }

    @Override
    public void onGridScroll(ServerPlayer player, @Nullable UUID id, boolean shift, boolean up) {
        if (id == null) {
            return;
        }

        if (up) {
            onExtract(player, id, EXTRACT_SINGLE | EXTRACT_SHIFT);
        } else {
            ItemStack stack = network.getItemStorageCache().getList().get(id);
            if (stack == null || stack.isEmpty()) {
                return;
            }

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (!invStack.isEmpty() && StorageAPI.instance().getComparer().isEqualNoQuantity(invStack, stack)) {
                    ItemStack toInsertStack = ItemHandlerHelper.copyStackWithSize(invStack, 1);
                    ItemStack remainder = network.insertItem(toInsertStack, 1, Action.PERFORM);
                    if (remainder.isEmpty()) {
                        invStack.shrink(1);
                        consumeSoulEnergy();
                    }
                    break;
                }
            }
        }
    }
}
