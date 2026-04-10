package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.common.storage.StorageBookType;
import com.k1sak1.goetyawaken.common.storage.api.IStorageDisk;
import com.k1sak1.goetyawaken.common.storage.api.IStorageDiskProvider;
import com.k1sak1.goetyawaken.common.storage.api.StorageDiskSyncData;
import com.k1sak1.goetyawaken.common.storage.impl.StorageAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class EnderStorageBookBase extends Item implements IStorageDiskProvider {
    private static final String NBT_ID = "Id";

    private final StorageBookType type;

    public EnderStorageBookBase(Properties properties, StorageBookType type) {
        super(properties.stacksTo(1));
        this.type = type;
    }

    public StorageBookType getStorageType() {
        return type;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide && !stack.hasTag()
                && entity instanceof net.minecraft.world.entity.player.Player player) {
            UUID id = UUID.randomUUID();

            ServerLevel serverLevel = (ServerLevel) level;
            IStorageDisk<ItemStack> disk = StorageAPI.instance().createItemDisk(serverLevel, type, player);
            StorageAPI.instance().getStorageDiskManager(serverLevel).set(id, disk);
            StorageAPI.instance().getStorageDiskManager(serverLevel).markForSaving();

            setId(stack, id);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (isValid(stack)) {
            UUID id = getId(stack);
            StorageAPI.instance().getStorageDiskSync().sendRequest(id);

            StorageDiskSyncData data = StorageAPI.instance().getStorageDiskSync().getData(id);
            if (data != null) {
                if (data.getCapacity() == -1) {
                    tooltip.add(Component.translatable("tooltip.goetyawaken.storage.stored",
                            formatQuantity(data.getStored())).withStyle(style -> style.withColor(0x808080)));
                } else {
                    tooltip.add(Component.translatable("tooltip.goetyawaken.storage.stored_capacity",
                            formatQuantity(data.getStored()),
                            formatQuantity(data.getCapacity())).withStyle(style -> style.withColor(0x808080)));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.goetyawaken.storage.stored_capacity",
                        "0",
                        formatQuantity(type.getCapacity())).withStyle(style -> style.withColor(0x808080)));
            }

            if (flag.isAdvanced()) {
                tooltip.add(Component.literal(id.toString()).withStyle(style -> style.withColor(0x606060)));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.goetyawaken.storage.empty")
                    .withStyle(style -> style.withColor(0x808080)));
        }
    }

    private String formatQuantity(int amount) {
        return String.valueOf(amount);
    }

    @Override
    public UUID getId(ItemStack disk) {
        if (disk.hasTag() && disk.getTag().hasUUID(NBT_ID)) {
            return disk.getTag().getUUID(NBT_ID);
        }
        return null;
    }

    @Override
    public void setId(ItemStack disk, UUID id) {
        if (!disk.hasTag()) {
            disk.setTag(new CompoundTag());
        }
        disk.getTag().putUUID(NBT_ID, id);
    }

    @Override
    public boolean isValid(ItemStack disk) {
        return disk.hasTag() && disk.getTag().hasUUID(NBT_ID);
    }

    @Override
    public int getCapacity(ItemStack disk) {
        return type.getCapacity();
    }

    @Override
    public StorageType getType() {
        return StorageType.ITEM;
    }

    @Override
    public int getEntityLifespan(ItemStack stack, Level level) {
        return Integer.MAX_VALUE;
    }
}
