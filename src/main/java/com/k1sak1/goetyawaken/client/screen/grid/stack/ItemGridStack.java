package com.k1sak1.goetyawaken.client.screen.grid.stack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ItemGridStack implements IGridStack {
    private static final Logger LOGGER = LogManager.getLogger(ItemGridStack.class);
    private static final String ERROR_PLACEHOLDER = "<Error>";

    private final ItemStack stack;
    private UUID id;
    @Nullable
    private UUID otherId;
    private boolean craftable;
    private boolean zeroed;

    private Set<String> cachedTags;
    private String cachedName;
    private String cachedModId;
    private List<Component> cachedTooltip;

    public ItemGridStack(ItemStack stack) {
        this.stack = stack;
        this.id = UUID.randomUUID();
    }

    public ItemGridStack(UUID id, @Nullable UUID otherId, ItemStack stack, boolean craftable) {
        this.id = id;
        this.otherId = otherId;
        this.stack = stack;
        this.craftable = craftable;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Nullable
    @Override
    public UUID getOtherId() {
        return otherId;
    }

    @Override
    public void updateOtherId(@Nullable UUID otherId) {
        this.otherId = otherId;
    }

    @Override
    public String getName() {
        if (cachedName == null) {
            try {
                cachedName = stack.getHoverName().getString();
            } catch (Throwable t) {
                LOGGER.warn("Could not retrieve item name of {}", BuiltInRegistries.ITEM.getKey(stack.getItem()));
                cachedName = ERROR_PLACEHOLDER;
            }
        }
        return cachedName;
    }

    @Override
    public String getModId() {
        if (cachedModId == null) {
            cachedModId = stack.getItem().getCreatorModId(stack);
            if (cachedModId == null) {
                cachedModId = ERROR_PLACEHOLDER;
            }
            cachedModId = cachedModId.toLowerCase().replace(" ", "");
        }
        return cachedModId;
    }

    @Override
    public Set<String> getTags() {
        if (cachedTags == null) {
            cachedTags = BuiltInRegistries.ITEM.getResourceKey(stack.getItem())
                    .flatMap(k -> BuiltInRegistries.ITEM.getHolder(k)
                            .map(holder -> holder.tags()
                                    .map(TagKey::location)
                                    .map(ResourceLocation::getPath)
                                    .collect(Collectors.toSet())))
                    .orElse(Collections.emptySet());
        }
        return cachedTags;
    }

    @Override
    public List<Component> getTooltip(boolean bypassCache) {
        if (bypassCache || cachedTooltip == null) {
            try {
                cachedTooltip = stack.getTooltipLines(
                        Minecraft.getInstance().player,
                        net.minecraft.world.item.TooltipFlag.Default.NORMAL);
            } catch (Throwable t) {
                LOGGER.warn("Could not retrieve item tooltip of {}", BuiltInRegistries.ITEM.getKey(stack.getItem()));
                cachedTooltip = new ArrayList<>();
                cachedTooltip.add(Component.literal(ERROR_PLACEHOLDER));
            }
        }
        return cachedTooltip;
    }

    @Override
    public int getQuantity() {
        return isCraftable() || zeroed ? 0 : stack.getCount();
    }

    @Override
    public void setQuantity(int amount) {
        if (amount <= 0) {
            zeroed = true;
        } else {
            zeroed = false;
            stack.setCount(amount);
        }
    }

    @Override
    public String getFormattedFullQuantity() {
        return String.valueOf(stack.getCount());
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, "");

        String text;
        int color;
        if (isCraftable()) {
            text = "Craft";
            color = 0xFFFFFF;
        } else if (zeroed) {
            text = "0";
            color = 0xFF4040;
        } else {
            int qty = stack.getCount();
            if (qty >= 1_000_000) {
                text = String.format("%.1fM", qty / 1_000_000.0);
            } else if (qty >= 10_000) {
                text = String.format("%.0fK", qty / 1_000.0);
            } else if (qty >= 1_000) {
                text = String.format("%.1fK", qty / 1_000.0);
            } else if (qty > 1) {
                text = String.valueOf(qty);
            } else {
                text = null;
            }
            color = 0xFFFFFF;
        }

        if (text != null) {
            var font = Minecraft.getInstance().font;
            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 300);
            graphics.pose().scale(0.5F, 0.5F, 1);
            graphics.drawString(font, text, 30 - font.width(text), 22, color);
            graphics.pose().popPose();
        }
    }

    @Override
    public ItemStack getIngredient() {
        return stack;
    }

    @Override
    public boolean isCraftable() {
        return craftable;
    }

    public void setZeroed(boolean zeroed) {
        this.zeroed = zeroed;
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
}
