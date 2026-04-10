package com.k1sak1.goetyawaken.client.screen.widget;

import com.k1sak1.goetyawaken.integration.jei.GoetyAwakenJeiPlugin;
import com.k1sak1.goetyawaken.integration.jei.JeiIntegration;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SearchWidget extends EditBox {
    public static final int SEARCH_BOX_MODE_NORMAL = 0;
    public static final int SEARCH_BOX_MODE_NORMAL_AUTOSELECTED = 1;
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED = 2;
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED = 3;
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY = 4;
    public static final int SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED = 5;

    private static final List<String> HISTORY = new ArrayList<>();

    private int mode = SEARCH_BOX_MODE_NORMAL;
    private int historyIndex = -1;
    private boolean myCanLoseFocus = true;
    private Consumer<String> onChanged;

    public SearchWidget(Font font, int x, int y, int width, int height) {
        super(font, x, y, width, height, Component.empty());
        this.setBordered(false);
        this.setMaxLength(128);
        this.setTextColor(0x404040);
    }

    public void setOnChanged(Consumer<String> onChanged) {
        this.onChanged = onChanged;
        this.setResponder(text -> {
            if (onChanged != null) {
                onChanged.accept(text);
            }
            updateJei();
        });
    }

    public void updateJei() {
        if (canSyncToJEI()) {
            GoetyAwakenJeiPlugin.syncSearchText(getValue());
        }
    }

    private boolean canSyncToJEI() {
        return doesSearchBoxModeUseJEI(this.mode) && JeiIntegration.isLoaded();
    }

    private boolean canSyncFromJEI() {
        return (this.mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY ||
                this.mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED)
                && JeiIntegration.isLoaded();
    }

    public static boolean doesSearchBoxModeUseJEI(int mode) {
        return mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED;
    }

    public static boolean isSearchBoxModeWithAutoselection(int mode) {
        return mode == SEARCH_BOX_MODE_NORMAL_AUTOSELECTED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ||
                mode == SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED;
    }

    public void setMode(int mode) {
        this.mode = mode;
        boolean loseFocus = !isSearchBoxModeWithAutoselection(mode);
        this.myCanLoseFocus = loseFocus;
        this.setCanLoseFocus(loseFocus);
        this.setFocused(isSearchBoxModeWithAutoselection(mode));

        if (canSyncFromJEI()) {
            setTextFromJEI();
        }
    }

    public int getMode() {
        return mode;
    }

    private void setTextFromJEI() {
        try {
            var runtime = GoetyAwakenJeiPlugin.getRuntime();
            if (runtime != null) {
                String filterText = runtime.getIngredientFilter().getFilterText();
                if (!getValue().equals(filterText)) {
                    setValue(filterText);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        boolean clickedWidget = mouseX >= this.getX() && mouseX < this.getX() + this.width
                && mouseY >= this.getY() && mouseY < this.getY() + this.height;

        if (clickedWidget && button == 1) {
            if (isFocused()) {
                saveHistory();
            }
            setValue("");
            setFocused(true);
        }

        if (!clickedWidget && isFocused()) {
            saveHistory();
            setFocused(false);
        }

        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (super.keyPressed(keyCode, scanCode, modifier)) {
            return true;
        }

        if (isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_UP) {
                updateHistory(-1);
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                updateHistory(1);
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                saveHistory();
                if (myCanLoseFocus) {
                    setFocused(false);
                }
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                saveHistory();
                if (!myCanLoseFocus) {
                    setFocused(false);
                    return false;
                } else {
                    setFocused(false);
                    return true;
                }
            }
        }

        return isFocused() && canConsumeInput() && keyCode != GLFW.GLFW_KEY_ESCAPE;
    }

    private void updateHistory(int delta) {
        if (HISTORY.isEmpty()) {
            return;
        }

        if (historyIndex == -1) {
            historyIndex = HISTORY.size();
        }

        historyIndex += delta;

        if (historyIndex < 0) {
            historyIndex = 0;
        } else if (historyIndex > HISTORY.size() - 1) {
            historyIndex = HISTORY.size() - 1;
            if (delta == 1) {
                setValue("");
                return;
            }
        }

        setValue(HISTORY.get(historyIndex));
    }

    private void saveHistory() {
        if (!HISTORY.isEmpty() && HISTORY.get(HISTORY.size() - 1).equals(getValue())) {
            return;
        }
        if (!getValue().trim().isEmpty()) {
            HISTORY.add(getValue());
        }
        historyIndex = -1;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (canSyncFromJEI()) {
            try {
                var runtime = GoetyAwakenJeiPlugin.getRuntime();
                if (runtime != null && runtime.getIngredientListOverlay().hasKeyboardFocus()) {
                    setTextFromJEI();
                }
            } catch (Exception ignored) {
            }
        }
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }
}
