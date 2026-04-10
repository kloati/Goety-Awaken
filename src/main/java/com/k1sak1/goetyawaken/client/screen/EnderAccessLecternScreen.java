package com.k1sak1.goetyawaken.client.screen;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;
import com.k1sak1.goetyawaken.client.screen.grid.view.GridViewImpl;
import com.k1sak1.goetyawaken.client.screen.grid.view.IGridView;
import com.k1sak1.goetyawaken.client.screen.widget.ScrollbarWidget;
import com.k1sak1.goetyawaken.client.screen.widget.SearchWidget;
import com.k1sak1.goetyawaken.client.screen.widget.sidebutton.SearchBoxModeSideButton;
import com.k1sak1.goetyawaken.client.screen.widget.sidebutton.SideButton;
import com.k1sak1.goetyawaken.client.screen.widget.sidebutton.SortingDirectionSideButton;
import com.k1sak1.goetyawaken.client.screen.widget.sidebutton.SortingTypeSideButton;
import com.k1sak1.goetyawaken.client.screen.widget.sidebutton.ViewTypeSideButton;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import com.k1sak1.goetyawaken.common.storage.grid.IItemGridHandler;
import com.k1sak1.goetyawaken.common.storage.network.message.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnderAccessLecternScreen extends AbstractContainerScreen<EnderAccessLecternContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/gui/crafting_grid.png");

    private static final int TOP_HEIGHT = 19;
    private static final int ROW_HEIGHT = 18;
    private static final int BOTTOM_HEIGHT = 156;
    private static final int VISIBLE_ROWS = 5;

    private static final int GRID_X = 8;
    private static final int GRID_Y = TOP_HEIGHT;
    private static final int GRID_COLS = 9;

    private static final int SCROLLBAR_X = 174;

    private static final int CLEAR_BTN_SIZE = 7;

    private ScrollbarWidget scrollbar;
    private SearchWidget searchWidget;
    private final List<SideButton> sideButtons = new ArrayList<>();
    private int sideButtonY;

    public EnderAccessLecternScreen(EnderAccessLecternContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

        int totalHeight = TOP_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT + BOTTOM_HEIGHT;
        this.imageWidth = 193;
        this.imageHeight = totalHeight;
        this.inventoryLabelY = TOP_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT + 73 - 12;
    }

    @Override
    protected void init() {
        super.init();

        int headerAndSlots = TOP_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT;

        scrollbar = new ScrollbarWidget(leftPos + SCROLLBAR_X, topPos + GRID_Y, VISIBLE_ROWS * ROW_HEIGHT);
        scrollbar.setEnabled(true);

        searchWidget = new SearchWidget(font, leftPos + 81, topPos + 7, 82, 10);
        searchWidget.setOnChanged(text -> {
            IGridView view = getMenu().getView();
            if (view instanceof GridViewImpl impl) {
                impl.setSearchQuery(text);
                view.forceSort();
                updateScrollbar();
            }
        });
        addRenderableWidget(searchWidget);

        int savedMode = getMenu().getSearchBoxMode();
        searchWidget.setMode(savedMode);
        if (!SearchWidget.isSearchBoxModeWithAutoselection(savedMode)) {
            searchWidget.setFocused(false);
        }

        IGridView view = getMenu().getView();
        if (view instanceof GridViewImpl impl) {
            impl.setScrollbarUpdater(this::updateScrollbar);
            sideButtonY = 0;
            addSideButton(new SortingDirectionSideButton(0, 0, impl));
            addSideButton(new SortingTypeSideButton(0, 0, impl));
            addSideButton(new ViewTypeSideButton(0, 0, impl));
            addSideButton(new SearchBoxModeSideButton(0, 0,
                    () -> getMenu().getSearchBoxMode(),
                    mode -> getMenu().setSearchBoxMode(mode),
                    searchWidget));
        }

        updateScrollbar();
    }

    private void addSideButton(SideButton button) {
        button.setX(leftPos - button.getWidth() - 2);
        button.setY(topPos + sideButtonY);
        sideButtonY += button.getHeight() + 2;
        sideButtons.add(button);
        addRenderableWidget(button);
    }

    private void updateScrollbar() {
        IGridView view = getMenu().getView();
        if (view != null && scrollbar != null) {
            int totalRows = view.getRows();
            scrollbar.setMaxOffset(Math.max(0, totalRows - VISIBLE_ROWS));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int yy = this.topPos;

        graphics.blit(TEXTURE, x, yy, 0, 0, imageWidth, TOP_HEIGHT);

        for (int i = 0; i < VISIBLE_ROWS; i++) {
            yy += ROW_HEIGHT;

            int yTextureStart = TOP_HEIGHT;
            if (i > 0) {
                if (i == VISIBLE_ROWS - 1) {
                    yTextureStart += ROW_HEIGHT * 2;
                } else {
                    yTextureStart += ROW_HEIGHT;
                }
            }

            graphics.blit(TEXTURE, x, yy, 0, yTextureStart, imageWidth, ROW_HEIGHT);
        }

        yy += ROW_HEIGHT;
        graphics.blit(TEXTURE, x, yy, 0, TOP_HEIGHT + (ROW_HEIGHT * 3), imageWidth, BOTTOM_HEIGHT);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        if (scrollbar != null) {
            scrollbar.render(graphics);
        }

        renderGridItems(graphics, mouseX, mouseY);

        renderClearButton(graphics, mouseX, mouseY);

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.playerInventoryTitle, 7, this.inventoryLabelY, 0x404040, false);
    }

    private void renderGridItems(GuiGraphics graphics, int mouseX, int mouseY) {
        IGridView view = getMenu().getView();
        if (view == null)
            return;

        List<IGridStack> stacks = view.getStacks();
        int offset = scrollbar != null ? scrollbar.getOffset() : 0;

        IGridStack hoveredStack = null;

        for (int row = 0; row < VISIBLE_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int idx = (offset + row) * GRID_COLS + col;
                if (idx >= stacks.size())
                    break;

                IGridStack gridStack = stacks.get(idx);
                int slotX = leftPos + GRID_X + col * ROW_HEIGHT;
                int slotY = topPos + GRID_Y + row * ROW_HEIGHT;

                if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                    graphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0x80FFFFFF);
                    hoveredStack = gridStack;
                }

                gridStack.draw(graphics, slotX, slotY);
            }
        }

        if (hoveredStack != null) {
            List<Component> tooltip = new ArrayList<>(hoveredStack.getTooltip(true));
            if (!hoveredStack.isCraftable()) {
                tooltip.add(Component.literal("\u00a77" + hoveredStack.getFormattedFullQuantity() + " stored"));
            }
            graphics.renderTooltip(font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    private void renderClearButton(GuiGraphics graphics, int mouseX, int mouseY) {
        int headerAndSlots = TOP_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT;
        int btnX = leftPos + 82;
        int btnY = topPos + headerAndSlots + 4;

        boolean hovered = mouseX >= btnX && mouseX < btnX + CLEAR_BTN_SIZE
                && mouseY >= btnY && mouseY < btnY + CLEAR_BTN_SIZE;

        if (hovered) {
            graphics.renderTooltip(font, Component.literal(I18n.get("misc.goetyawaken.clear")), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (searchWidget != null) {
            boolean searchHandled = searchWidget.mouseClicked(mouseX, mouseY, button);
            if (isOverSearchWidget(mouseX, mouseY)) {
                setFocused(searchWidget);
                return true;
            }
            if (searchHandled) {
                return true;
            }
        }

        if (scrollbar != null && scrollbar.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        int headerAndSlots = TOP_HEIGHT + VISIBLE_ROWS * ROW_HEIGHT;
        int btnX = leftPos + 82;
        int btnY = topPos + headerAndSlots + 4;
        if (mouseX >= btnX && mouseX < btnX + CLEAR_BTN_SIZE
                && mouseY >= btnY && mouseY < btnY + CLEAR_BTN_SIZE && button == 0) {
            ModNetwork.channel.sendToServer(new GridClearMessage());
            return true;
        }

        IGridStack clicked = getGridStackAt(mouseX, mouseY);
        if (clicked != null) {
            handleGridClick(clicked, button);
            return true;
        }

        if (isInGridArea(mouseX, mouseY) && hasShiftDown() == false) {
            ItemStack carried = getMenu().getCarried();
            if (!carried.isEmpty()) {
                boolean single = button == 1;
                ModNetwork.channel.sendToServer(new GridItemInsertHeldMessage(single));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isOverSearchWidget(double mouseX, double mouseY) {
        if (searchWidget == null)
            return false;
        return mouseX >= searchWidget.getX() && mouseX < searchWidget.getX() + searchWidget.getWidth()
                && mouseY >= searchWidget.getY() && mouseY < searchWidget.getY() + searchWidget.getHeight();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (scrollbar != null && scrollbar.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbar != null && scrollbar.mouseDragged(mouseX, mouseY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isInGridArea(mouseX, mouseY)) {
            if (scrollbar != null && scrollbar.mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchWidget != null && searchWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchWidget != null && !searchWidget.isFocused()) {
            if (Character.isDefined(codePoint) && !Character.isISOControl(codePoint)) {
                setFocused(searchWidget);
                searchWidget.setFocused(true);
            }
        }
        if (searchWidget != null && searchWidget.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private void handleGridClick(IGridStack gridStack, int button) {
        int flags = 0;
        if (button == 1) {
            flags |= IItemGridHandler.EXTRACT_HALF;
        }
        if (hasShiftDown()) {
            flags |= IItemGridHandler.EXTRACT_SHIFT;
        }

        ModNetwork.channel.sendToServer(new GridItemPullMessage(gridStack.getId(), flags));
    }

    private IGridStack getGridStackAt(double mouseX, double mouseY) {
        IGridView view = getMenu().getView();
        if (view == null)
            return null;

        List<IGridStack> stacks = view.getStacks();
        int offset = scrollbar != null ? scrollbar.getOffset() : 0;

        for (int row = 0; row < VISIBLE_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int idx = (offset + row) * GRID_COLS + col;
                if (idx >= stacks.size())
                    return null;

                int slotX = leftPos + GRID_X + col * ROW_HEIGHT;
                int slotY = topPos + GRID_Y + row * ROW_HEIGHT;

                if (mouseX >= slotX && mouseX < slotX + 16
                        && mouseY >= slotY && mouseY < slotY + 16) {
                    return stacks.get(idx);
                }
            }
        }
        return null;
    }

    private boolean isInGridArea(double mouseX, double mouseY) {
        int gridRight = leftPos + GRID_X + GRID_COLS * ROW_HEIGHT;
        int gridBottom = topPos + GRID_Y + VISIBLE_ROWS * ROW_HEIGHT;
        return mouseX >= leftPos + GRID_X && mouseX < gridRight
                && mouseY >= topPos + GRID_Y && mouseY < gridBottom;
    }
}
