package com.k1sak1.goetyawaken.client.screen.widget.sidebutton;

import com.k1sak1.goetyawaken.client.screen.grid.view.GridViewImpl;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.network.message.GridSettingUpdateMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class ViewTypeSideButton extends SideButton {
    private final GridViewImpl view;

    public ViewTypeSideButton(int x, int y, GridViewImpl view) {
        super(x, y, btn -> toggleViewType(view));
        this.view = view;
    }

    private static void toggleViewType(GridViewImpl view) {
        int current = view.getViewType();
        int newType = current == GridViewImpl.VIEW_TYPE_NORMAL
                ? GridViewImpl.VIEW_TYPE_CRAFTABLES
                : GridViewImpl.VIEW_TYPE_NORMAL;
        view.setViewType(newType);
        view.forceSort();
        ModNetwork.channel.sendToServer(
                new GridSettingUpdateMessage(GridSettingUpdateMessage.SETTING_VIEW_TYPE, newType));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        int iconX = view.getViewType() == GridViewImpl.VIEW_TYPE_NORMAL ? 0 : 16;
        graphics.blit(ICONS_TEXTURE, x, y, iconX, 112, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        String key = view.getViewType() == GridViewImpl.VIEW_TYPE_NORMAL
                ? "normal"
                : "craftables";
        return I18n.get("sidebutton.goetyawaken.grid.view_type") + "\n" + ChatFormatting.GRAY +
                I18n.get("sidebutton.goetyawaken.grid.view_type." + key);
    }
}
