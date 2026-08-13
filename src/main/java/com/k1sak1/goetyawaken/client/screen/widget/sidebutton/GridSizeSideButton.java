package com.k1sak1.goetyawaken.client.screen.widget.sidebutton;

import com.k1sak1.goetyawaken.client.screen.grid.view.GridViewImpl;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.network.message.GridSettingUpdateMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class GridSizeSideButton extends SideButton {
    private final IntSupplier sizeGetter;
    private final IntConsumer sizeSetter;
    private final Runnable onChanged;

    public GridSizeSideButton(int x, int y, IntSupplier sizeGetter, IntConsumer sizeSetter, Runnable onChanged) {
        super(x, y, btn -> {
        });
        this.sizeGetter = sizeGetter;
        this.sizeSetter = sizeSetter;
        this.onChanged = onChanged;
    }

    @Override
    public void onPress() {
        int current = sizeGetter.getAsInt();
        int next = (current + 1) % 4;
        sizeSetter.accept(next);
        ModNetwork.channel.sendToServer(
                new GridSettingUpdateMessage(GridSettingUpdateMessage.SETTING_SIZE, next));
        if (onChanged != null) {
            onChanged.run();
        }
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        int size = sizeGetter.getAsInt();
        int tx;
        switch (size) {
            case GridViewImpl.SIZE_STRETCH -> tx = 48;
            case GridViewImpl.SIZE_SMALL -> tx = 0;
            case GridViewImpl.SIZE_MEDIUM -> tx = 16;
            case GridViewImpl.SIZE_LARGE -> tx = 32;
            default -> tx = 16;
        }
        graphics.blit(ICONS_TEXTURE, x, y, 64 + tx, 64, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        int size = sizeGetter.getAsInt();
        String key = switch (size) {
            case GridViewImpl.SIZE_STRETCH -> "stretch";
            case GridViewImpl.SIZE_SMALL -> "small";
            case GridViewImpl.SIZE_LARGE -> "large";
            default -> "medium";
        };
        return I18n.get("sidebutton.goetyawaken.grid.size") + "\n"
                + ChatFormatting.GRAY + I18n.get("sidebutton.goetyawaken.grid.size." + key);
    }
}
