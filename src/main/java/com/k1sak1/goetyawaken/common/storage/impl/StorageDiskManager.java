package com.k1sak1.goetyawaken.common.storage.impl;

import com.k1sak1.goetyawaken.common.storage.api.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class StorageDiskManager extends SavedData implements IStorageDiskManager {
    public static final String NAME = "goetyawaken_storage";

    private static final String NBT_DISKS = "Disks";
    private static final String NBT_DISK_ID = "Id";
    private static final String NBT_DISK_TYPE = "Type";
    private static final String NBT_DISK_DATA = "Data";

    private final Map<UUID, IStorageDisk> disks = new HashMap<>();
    private final ServerLevel level;

    public StorageDiskManager(ServerLevel level) {
        this.level = level;
    }

    public static StorageDiskManager load(CompoundTag tag, ServerLevel level) {
        StorageDiskManager manager = new StorageDiskManager(level);
        manager.load(tag);
        return manager;
    }

    @Override
    @Nullable
    public IStorageDisk get(UUID id) {
        return disks.get(id);
    }

    @Nullable
    @Override
    public IStorageDisk getByStack(ItemStack disk) {
        if (!(disk.getItem() instanceof IStorageDiskProvider)) {
            return null;
        }

        IStorageDiskProvider provider = (IStorageDiskProvider) disk.getItem();

        if (!provider.isValid(disk)) {
            return null;
        }

        return get(provider.getId(disk));
    }

    @Override
    public Map<UUID, IStorageDisk> getAll() {
        return disks;
    }

    @Override
    public void set(UUID id, IStorageDisk disk) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (disk == null) {
            throw new IllegalArgumentException("Disk cannot be null");
        }

        if (disks.containsKey(id)) {
            throw new IllegalArgumentException("Disks already contains id '" + id + "'");
        }

        disks.put(id, disk);
    }

    @Override
    public void remove(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        disks.remove(id);
    }

    @Override
    public void markForSaving() {
        setDirty();
    }

    public void load(CompoundTag tag) {
        if (tag.contains(NBT_DISKS)) {
            ListTag disksTag = tag.getList(NBT_DISKS, Tag.TAG_COMPOUND);

            for (int i = 0; i < disksTag.size(); ++i) {
                CompoundTag diskTag = disksTag.getCompound(i);

                UUID id = diskTag.getUUID(NBT_DISK_ID);
                CompoundTag data = diskTag.getCompound(NBT_DISK_DATA);
                String type = diskTag.getString(NBT_DISK_TYPE);

                if ("item".equals(type)) {
                    ItemStorageDisk disk = createItemDiskFromNbt(data);
                    disks.put(id, disk);
                }
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag disksTag = new ListTag();

        for (Map.Entry<UUID, IStorageDisk> entry : disks.entrySet()) {
            CompoundTag diskTag = new CompoundTag();

            diskTag.putUUID(NBT_DISK_ID, entry.getKey());
            diskTag.put(NBT_DISK_DATA, entry.getValue().writeToNbt());
            diskTag.putString(NBT_DISK_TYPE, entry.getValue().getFactoryId().getPath());

            disksTag.add(diskTag);
        }

        tag.put(NBT_DISKS, disksTag);

        return tag;
    }

    private ItemStorageDisk createItemDiskFromNbt(CompoundTag tag) {
        ItemStorageDisk disk = new ItemStorageDisk(
                level,
                tag.getInt(ItemStorageDisk.NBT_CAPACITY),
                tag.contains(ItemStorageDisk.NBT_OWNER) ? tag.getUUID(ItemStorageDisk.NBT_OWNER) : null);

        ListTag list = tag.getList(ItemStorageDisk.NBT_ITEMS, Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = deserializeStackFromNbt(list.getCompound(i));

            if (!stack.isEmpty()) {
                disk.getRawStacks().put(stack.getItem(), stack);
            }
        }

        disk.updateItemCount();

        return disk;
    }

    private ItemStack deserializeStackFromNbt(CompoundTag tag) {
        return ItemStack.of(tag);
    }
}
