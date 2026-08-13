package com.k1sak1.goetyawaken.common.storage.grid.stack;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href="https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public class ItemGridStack {
    private final ItemStack stack;
    private UUID id;
    @Nullable
    private UUID otherId;
    private boolean craftable;
    private boolean zeroed;

    public ItemGridStack(UUID id, @Nullable UUID otherId, ItemStack stack, boolean craftable) {
        this.id = id;
        this.otherId = otherId;
        this.stack = stack;
        this.craftable = craftable;
    }

    public ItemStack getStack() {
        return stack;
    }

    public UUID getId() {
        return id;
    }

    @Nullable
    public UUID getOtherId() {
        return otherId;
    }

    public void updateOtherId(@Nullable UUID otherId) {
        this.otherId = otherId;
    }

    public int getQuantity() {
        return isCraftable() || zeroed ? 0 : stack.getCount();
    }

    public void setQuantity(int amount) {
        if (amount <= 0) {
            zeroed = true;
        } else {
            zeroed = false;
            stack.setCount(amount);
        }
    }

    public boolean isCraftable() {
        return craftable;
    }

    public void setCraftable(boolean craftable) {
        this.craftable = craftable;
    }

    public void setZeroed(boolean zeroed) {
        this.zeroed = zeroed;
    }

    public static void write(FriendlyByteBuf buf, ItemGridStack gridStack) {
        buf.writeUUID(gridStack.id);
        buf.writeBoolean(gridStack.otherId != null);
        if (gridStack.otherId != null) {
            buf.writeUUID(gridStack.otherId);
        }
        writeItemStack(buf, gridStack.stack);
        buf.writeBoolean(gridStack.craftable);
    }

    public static ItemGridStack read(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        UUID otherId = buf.readBoolean() ? buf.readUUID() : null;
        ItemStack stack = readItemStack(buf);
        boolean craftable = buf.readBoolean();
        return new ItemGridStack(id, otherId, stack, craftable);
    }

    private static void writeItemStack(FriendlyByteBuf buf, ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeId(BuiltInRegistries.ITEM, stack.getItem());
            buf.writeInt(stack.getCount());
            buf.writeNbt(stack.getItem().getShareTag(stack));
        }
    }

    private static ItemStack readItemStack(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            Item item = buf.readById(BuiltInRegistries.ITEM);
            int count = buf.readInt();
            ItemStack stack = new ItemStack(item, count);
            item.readShareTag(stack, buf.readNbt());
            return stack;
        }
    }
}
