package com.k1sak1.goetyawaken.common.storage.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.storage.api.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class ItemStorageDisk implements IStorageDisk<ItemStack> {
    public static final String NBT_VERSION = "Version";
    public static final String NBT_CAPACITY = "Capacity";
    public static final String NBT_ITEMS = "Items";
    public static final String NBT_OWNER = "Owner";
    public static final int VERSION = 1;
    public static final ResourceLocation FACTORY_ID = new ResourceLocation(GoetyAwaken.MODID, "item");

    @Nullable
    private final ServerLevel level;
    private final int capacity;
    private final Multimap<Item, ItemStack> stacks = ArrayListMultimap.create();
    private final UUID owner;
    private int itemCount;

    @Nullable
    private IStorageDiskListener listener;
    private IStorageDiskContainerContext context;

    public ItemStorageDisk(@Nullable ServerLevel level, int capacity, @Nullable UUID owner) {
        this.level = level;
        this.capacity = capacity;
        this.owner = owner;
    }

    @Override
    public CompoundTag writeToNbt() {
        CompoundTag tag = new CompoundTag();

        ListTag list = new ListTag();

        for (ItemStack stack : stacks.values()) {
            list.add(serializeStackToNbt(stack));
        }

        tag.putInt(NBT_VERSION, VERSION);
        tag.put(NBT_ITEMS, list);
        tag.putInt(NBT_CAPACITY, capacity);

        if (owner != null) {
            tag.putUUID(NBT_OWNER, owner);
        }

        return tag;
    }

    private CompoundTag serializeStackToNbt(ItemStack stack) {
        return stack.save(new CompoundTag());
    }

    @Override
    public ResourceLocation getFactoryId() {
        return FACTORY_ID;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return stacks.values();
    }

    @Override
    @Nonnull
    public ItemStack insert(@Nonnull ItemStack stack, int size, Action action) {
        if (stack.isEmpty() || itemCount == capacity) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (StorageAPI.instance().getComparer().isEqualNoQuantity(otherStack, stack)) {
                if (getCapacity() != -1 && getStored() + size > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (action == Action.PERFORM) {
                        otherStack.grow(remainingSpace);
                        itemCount += remainingSpace;
                        onChanged();
                    }

                    return ItemHandlerHelper.copyStackWithSize(otherStack, size - remainingSpace);
                } else {
                    int maxConsumable = Math.min(size, Integer.MAX_VALUE - otherStack.getCount());
                    if (action == Action.PERFORM) {
                        otherStack.grow(maxConsumable);
                        itemCount += maxConsumable;

                        onChanged();
                    }

                    return ItemHandlerHelper.copyStackWithSize(otherStack, size - maxConsumable);
                }
            }
        }

        if (getCapacity() != -1 && getStored() + size > getCapacity()) {
            int remainingSpace = getCapacity() - getStored();

            if (remainingSpace <= 0) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            if (action == Action.PERFORM) {
                stacks.put(stack.getItem(), ItemHandlerHelper.copyStackWithSize(stack, remainingSpace));
                itemCount += remainingSpace;
                onChanged();
            }

            return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
        } else {
            if (action == Action.PERFORM) {
                stacks.put(stack.getItem(), ItemHandlerHelper.copyStackWithSize(stack, size));
                itemCount += size;

                onChanged();
            }

            return ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, Action action) {
        if (stack.isEmpty()) {
            return stack;
        }

        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (StorageAPI.instance().getComparer().isEqual(otherStack, stack, flags)) {
                if (size > otherStack.getCount()) {
                    size = otherStack.getCount();
                }

                if (action == Action.PERFORM) {
                    if (otherStack.getCount() - size == 0) {
                        stacks.remove(otherStack.getItem(), otherStack);
                    } else {
                        otherStack.shrink(size);
                    }

                    itemCount -= size;

                    onChanged();
                }

                return ItemHandlerHelper.copyStackWithSize(otherStack, size);
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getStored() {
        return itemCount;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public AccessType getAccessType() {
        return context != null ? context.getAccessType() : AccessType.INSERT_EXTRACT;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Nullable
    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        if (getAccessType() == AccessType.INSERT) {
            return 0;
        }

        return remainder == null ? size : (size - remainder.getCount());
    }

    public Multimap<Item, ItemStack> getRawStacks() {
        return stacks;
    }

    private void onChanged() {
        if (listener != null) {
            listener.onChanged();
        }

        if (level != null) {
            StorageAPI.instance().getStorageDiskManager(level).markForSaving();
        }
    }

    public void updateItemCount() {
        itemCount = stacks.values().stream().mapToInt(ItemStack::getCount).sum();
    }
}
