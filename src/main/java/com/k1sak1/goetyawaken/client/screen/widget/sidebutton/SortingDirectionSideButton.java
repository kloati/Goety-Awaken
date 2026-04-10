package com.k1sak1.goetyawaken.client.screen.widget.sidebutton;

import com.k1sak1.goetyawaken.client.screen.grid.view.GridViewImpl;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.network.message.GridSettingUpdateMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class SortingDirectionSideButton extends SideButton {
    private final GridViewImpl view;

    public SortingDirectionSideButton(int x, int y, GridViewImpl view) {
        super(x, y, btn -> toggleDirection(view));
        this.view = view;
    }

    private static void toggleDirection(GridViewImpl view) {
        int current = view.getSortingDirection();
        int newDir = current == GridViewImpl.SORTING_DIRECTION_ASCENDING
                ? GridViewImpl.SORTING_DIRECTION_DESCENDING
                : GridViewImpl.SORTING_DIRECTION_ASCENDING;
        view.setSortingDirection(newDir);
        view.forceSort();
        ModNetwork.channel.sendToServer(
                new GridSettingUpdateMessage(GridSettingUpdateMessage.SETTING_SORTING_DIRECTION, newDir));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(ICONS_TEXTURE, x, y, view.getSortingDirection() * 16, 16, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        String key = view.getSortingDirection() == GridViewImpl.SORTING_DIRECTION_ASCENDING
                ? "ascending"
                : "descending";
        return I18n.get("sidebutton.goetyawaken.grid.sorting.direction") + "\n" + ChatFormatting.GRAY +
                I18n.get("sidebutton.goetyawaken.grid.sorting.direction." + key);
    }
}
