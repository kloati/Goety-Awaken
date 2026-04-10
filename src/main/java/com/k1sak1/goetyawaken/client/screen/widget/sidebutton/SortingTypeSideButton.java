package com.k1sak1.goetyawaken.client.screen.widget.sidebutton;

import com.k1sak1.goetyawaken.client.screen.grid.view.GridViewImpl;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.network.message.GridSettingUpdateMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class SortingTypeSideButton extends SideButton {
    private final GridViewImpl view;

    public SortingTypeSideButton(int x, int y, GridViewImpl view) {
        super(x, y, btn -> toggleType(view));
        this.view = view;
    }

    private static void toggleType(GridViewImpl view) {
        int current = view.getSortingType();
        int newType = current == GridViewImpl.SORTING_TYPE_NAME
                ? GridViewImpl.SORTING_TYPE_QUANTITY
                : GridViewImpl.SORTING_TYPE_NAME;
        view.setSortingType(newType);
        view.forceSort();
        ModNetwork.channel.sendToServer(
                new GridSettingUpdateMessage(GridSettingUpdateMessage.SETTING_SORTING_TYPE, newType));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        int iconX = view.getSortingType() == GridViewImpl.SORTING_TYPE_QUANTITY ? 0 : 16;
        graphics.blit(ICONS_TEXTURE, x, y, iconX, 32, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        String key = view.getSortingType() == GridViewImpl.SORTING_TYPE_NAME
                ? "name"
                : "quantity";
        return I18n.get("sidebutton.goetyawaken.grid.sorting.type") + "\n" + ChatFormatting.GRAY +
                I18n.get("sidebutton.goetyawaken.grid.sorting.type." + key);
    }
}
