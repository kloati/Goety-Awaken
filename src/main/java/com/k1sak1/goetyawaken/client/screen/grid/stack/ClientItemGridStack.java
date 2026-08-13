package com.k1sak1.goetyawaken.client.screen.grid.stack;

import com.k1sak1.goetyawaken.common.storage.grid.stack.ItemGridStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ClientItemGridStack implements IGridStack {
    private static final Logger LOGGER = LogManager.getLogger(ClientItemGridStack.class);
    private static final String ERROR_PLACEHOLDER = "<Error>";

    private final ItemGridStack wrapped;
    private Set<String> cachedTags;
    private String cachedName;
    private String cachedModId;
    private List<Component> cachedTooltip;

    public ClientItemGridStack(ItemGridStack wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public UUID getId() {
        return wrapped.getId();
    }

    @Nullable
    @Override
    public UUID getOtherId() {
        return wrapped.getOtherId();
    }

    @Override
    public void updateOtherId(@Nullable UUID otherId) {
        wrapped.updateOtherId(otherId);
    }

    @Override
    public String getName() {
        if (cachedName == null) {
            try {
                cachedName = wrapped.getStack().getHoverName().getString();
            } catch (Throwable t) {
                cachedName = ERROR_PLACEHOLDER;
            }
        }
        return cachedName;
    }

    @Override
    public String getModId() {
        if (cachedModId == null) {
            cachedModId = wrapped.getStack().getItem().getCreatorModId(wrapped.getStack());
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
            cachedTags = BuiltInRegistries.ITEM.getResourceKey(wrapped.getStack().getItem())
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
                cachedTooltip = wrapped.getStack().getTooltipLines(
                        Minecraft.getInstance().player,
                        net.minecraft.world.item.TooltipFlag.Default.NORMAL);
            } catch (Throwable t) {
                cachedTooltip = new ArrayList<>();
                cachedTooltip.add(Component.literal(ERROR_PLACEHOLDER));
            }
        }
        return cachedTooltip;
    }

    @Override
    public int getQuantity() {
        return wrapped.getQuantity();
    }

    @Override
    public void setQuantity(int amount) {
        wrapped.setQuantity(amount);
    }

    @Override
    public String getFormattedFullQuantity() {
        return String.valueOf(wrapped.getStack().getCount());
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        graphics.renderItem(wrapped.getStack(), x, y);
        graphics.renderItemDecorations(Minecraft.getInstance().font, wrapped.getStack(), x, y, "");

        String text;
        int color;
        if (isCraftable()) {
            text = "Craft";
            color = 0xFFFFFF;
        } else if (wrapped.getQuantity() <= 0) {
            text = "0";
            color = 0xFF4040;
        } else {
            int qty = wrapped.getStack().getCount();
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
        return wrapped.getStack();
    }

    @Override
    public boolean isCraftable() {
        return wrapped.isCraftable();
    }

    public ItemGridStack getWrapped() {
        return wrapped;
    }
}
