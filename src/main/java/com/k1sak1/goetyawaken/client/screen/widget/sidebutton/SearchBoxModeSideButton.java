package com.k1sak1.goetyawaken.client.screen.widget.sidebutton;

import com.k1sak1.goetyawaken.client.screen.widget.SearchWidget;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.network.message.GridSettingUpdateMessage;
import com.k1sak1.goetyawaken.integration.jei.JeiIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class SearchBoxModeSideButton extends SideButton {
    private static final List<Integer> MODE_ROTATION = Arrays.asList(
            SearchWidget.SEARCH_BOX_MODE_NORMAL,
            SearchWidget.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED,
            SearchWidget.SEARCH_BOX_MODE_JEI_SYNCHRONIZED,
            SearchWidget.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED,
            SearchWidget.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY,
            SearchWidget.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED,
            SearchWidget.SEARCH_BOX_MODE_NORMAL);

    private final IntSupplier modeGetter;
    private final IntConsumer modeSetter;
    private final SearchWidget searchWidget;

    public SearchBoxModeSideButton(int x, int y, IntSupplier modeGetter, IntConsumer modeSetter,
            SearchWidget searchWidget) {
        super(x, y, btn -> {
        });
        this.modeGetter = modeGetter;
        this.modeSetter = modeSetter;
        this.searchWidget = searchWidget;
    }

    private static int nextMode(int current) {
        int idx = MODE_ROTATION.indexOf(current);
        if (idx < 0)
            idx = 0;
        return MODE_ROTATION.get(idx + 1);
    }

    @Override
    public void onPress() {
        int mode = nextMode(modeGetter.getAsInt());
        if (SearchWidget.doesSearchBoxModeUseJEI(mode) && !JeiIntegration.isLoaded()) {
            mode = SearchWidget.SEARCH_BOX_MODE_NORMAL;
        }

        modeSetter.accept(mode);
        if (searchWidget != null) {
            searchWidget.setMode(mode);
        }

        ModNetwork.channel.sendToServer(
                new GridSettingUpdateMessage(GridSettingUpdateMessage.SETTING_SEARCH_BOX_MODE, mode));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        int mode = modeGetter.getAsInt();
        graphics.blit(ICONS_TEXTURE, x, y, SearchWidget.isSearchBoxModeWithAutoselection(mode) ? 16 : 0, 96, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        int mode = modeGetter.getAsInt();
        String modeKey = getModeKey(mode);
        return I18n.get("sidebutton.goetyawaken.grid.search_box_mode") + "\n"
                + ChatFormatting.GRAY + I18n.get("sidebutton.goetyawaken.grid.search_box_mode." + modeKey);
    }

    private String getModeKey(int mode) {
        return switch (mode) {
            case SearchWidget.SEARCH_BOX_MODE_NORMAL -> "normal";
            case SearchWidget.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED -> "normal_autoselected";
            case SearchWidget.SEARCH_BOX_MODE_JEI_SYNCHRONIZED -> "jei_synchronized";
            case SearchWidget.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED -> "jei_synchronized_autoselected";
            case SearchWidget.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY -> "jei_synchronized_2way";
            case SearchWidget.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED ->
                "jei_synchronized_2way_autoselected";
            default -> "normal";
        };
    }
}
