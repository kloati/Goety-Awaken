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
        int newType;
        if (current == GridViewImpl.VIEW_TYPE_NORMAL) {
            newType = GridViewImpl.VIEW_TYPE_NON_CRAFTABLES;
        } else if (current == GridViewImpl.VIEW_TYPE_NON_CRAFTABLES) {
            newType = GridViewImpl.VIEW_TYPE_CRAFTABLES;
        } else {
            newType = GridViewImpl.VIEW_TYPE_NORMAL;
        }
        view.setViewType(newType);
        view.forceSort();
        ModNetwork.channel.sendToServer(
                new GridSettingUpdateMessage(GridSettingUpdateMessage.SETTING_VIEW_TYPE, newType));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        int type = view.getViewType();
        if (!GridViewImpl.isValidViewType(type)) {
            type = GridViewImpl.VIEW_TYPE_NORMAL;
        }
        graphics.blit(ICONS_TEXTURE, x, y, type * 16, 112, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        int type = view.getViewType();
        String key;
        if (type == GridViewImpl.VIEW_TYPE_NON_CRAFTABLES) {
            key = "non_craftables";
        } else if (type == GridViewImpl.VIEW_TYPE_CRAFTABLES) {
            key = "craftables";
        } else {
            key = "normal";
        }
        return I18n.get("sidebutton.goetyawaken.grid.view_type") + "\n" + ChatFormatting.GRAY +
                I18n.get("sidebutton.goetyawaken.grid.view_type." + key);
    }
}
