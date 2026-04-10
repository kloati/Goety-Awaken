package com.k1sak1.goetyawaken.client.screen.grid.stack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IGridStack {
    UUID getId();

    @Nullable
    UUID getOtherId();

    void updateOtherId(@Nullable UUID otherId);

    String getName();

    String getModId();

    Set<String> getTags();

    List<Component> getTooltip(boolean bypassCache);

    int getQuantity();

    void setQuantity(int amount);

    String getFormattedFullQuantity();

    void draw(GuiGraphics graphics, int x, int y);

    ItemStack getIngredient();

    boolean isCraftable();
}
