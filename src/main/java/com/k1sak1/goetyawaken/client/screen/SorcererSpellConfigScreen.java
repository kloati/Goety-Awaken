package com.k1sak1.goetyawaken.client.screen;

import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellConfig;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellEntry;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.network.server.SSaveSpellConfigPacket;
import com.k1sak1.goetyawaken.integration.pinyin.PinyinIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SorcererSpellConfigScreen extends Screen {

    private record IndexedEntry(SorcererSpellEntry entry, int originalIndex) {
    }

    private List<SorcererSpellEntry> currentEntries;
    private int selectedIndex = -1;
    private int scrollOffset = 0;
    private int leftPanelWidth = 146;
    private int rightPanelX;
    private int panelY = 20;
    private int panelHeight;
    private int entryHeight = 28;
    private int rightScrollOffset = 0;
    private boolean rightScrolling = false;
    private int visibleEntries;
    private boolean showAddPopup = false;
    private String searchText = "";
    private int addScrollOffset = 0;
    private boolean showStaffDropdown = false;
    private int staffDropdownScroll = 0;

    private int addBtnX, addBtnW;
    private int resetBtnX, resetBtnW;
    private int saveBtnX, saveBtnW;

    private String weightInputBuffer = "";
    private boolean weightInputFocused = false;
    private int weightCursorPos = 0;
    private int searchCursorPos = 0;

    private long lastCursorBlink;

    public SorcererSpellConfigScreen() {
        super(Component.translatable("screen.goetyawaken.spell_config"));
        List<SorcererSpellEntry> configEntries = SorcererSpellConfig.getSpellEntries();
        this.currentEntries = new ArrayList<>(configEntries.size());
        for (SorcererSpellEntry entry : configEntries) {
            this.currentEntries.add(copy(entry));
        }
        this.lastCursorBlink = System.currentTimeMillis();
    }

    private SorcererSpellEntry copy(SorcererSpellEntry entry) {
        SorcererSpellEntry copy = new SorcererSpellEntry(
                entry.getFocusRegistryName(),
                entry.getMinLevel(),
                entry.getMaxLevel(),
                entry.isLevelIncrease(),
                entry.getUpgradeStaffRegistryName(),
                entry.getUpgradeStaffLevel(),
                entry.getWeight());
        copy.setSpell(entry.getSpell());
        copy.setFocusStack(entry.getFocusStack());
        copy.setUpgradeStaff(entry.getUpgradeStaff());
        return copy;
    }

    @Override
    protected void init() {
        super.init();
        this.rightPanelX = 4 + leftPanelWidth + 4;
        this.panelHeight = this.height - panelY - 36;
        this.visibleEntries = panelHeight / entryHeight;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gui);
        gui.drawCenteredString(this.font, this.title, this.width / 2, 6, 0xFFFFFFFF);
        renderLeftPanel(gui, mouseX, mouseY);
        if (!showAddPopup)
            renderRightPanel(gui, mouseX, mouseY);
        renderBottomBar(gui, mouseX, mouseY);
        if (showAddPopup) {
            renderAddPopup(gui, mouseX, mouseY);
        }
    }

    private void renderLeftPanel(GuiGraphics gui, int mouseX, int mouseY) {
        int x = 4;
        int y = panelY;
        int width = leftPanelWidth;
        int height = panelHeight;
        gui.fill(x, y, x + width, y + height, 0xFF202020);
        gui.renderOutline(x, y, width, height, 0xFF000000);

        List<IndexedEntry> indexed = getFilteredEntries();
        int maxScroll = Math.max(0, indexed.size() - visibleEntries);
        if (maxScroll > 0) {
            int scrollBarX = x + width - 6;
            int scrollBarH = Math.max(10, height * visibleEntries / indexed.size());
            int scrollBarY = y + (scrollOffset * (height - scrollBarH) / maxScroll);
            gui.fill(scrollBarX, y, scrollBarX + 4, y + height, 0xFF333333);
            gui.fill(scrollBarX, scrollBarY, scrollBarX + 4, scrollBarY + scrollBarH, 0xFF888888);
        }

        for (int i = 0; i < visibleEntries && (i + scrollOffset) < indexed.size(); i++) {
            int idx = i + scrollOffset;
            IndexedEntry ie = indexed.get(idx);
            SorcererSpellEntry entry = ie.entry();
            int originIdx = ie.originalIndex();
            int ey = y + i * entryHeight;
            int ex = x + 2;
            if (originIdx == selectedIndex) {
                gui.fill(ex, ey, x + width - 8, ey + entryHeight, 0x40FFFFFF);
            } else if (isMouseIn(mouseX, mouseY, ex, ey, width - 8, entryHeight)) {
                gui.fill(ex, ey, x + width - 8, ey + entryHeight, 0x20FFFFFF);
                renderTooltip(gui, mouseX, mouseY, Component.translatable("tooltip.goetyawaken.spell_config.entry"));
            }
            ItemStack focusStack = entry.getFocusStack();
            if (focusStack != null && !focusStack.isEmpty()) {
                gui.renderItem(focusStack, ex + 2, ey + 6);
                gui.renderItemDecorations(this.font, focusStack, ex + 2, ey + 6);
            }
            String name = focusStack != null ? focusStack.getHoverName().getString() : entry.getFocusRegistryName();
            gui.drawString(this.font, trimStr(name, 20), ex + 22, ey + 4, 0xFFFFFFFF);
            String lvText = "Lv." + entry.getMinLevel() + "-" + entry.getMaxLevel();
            gui.drawString(this.font, lvText, ex + 22, ey + 16, 0xFF808080);
            String weightText = String.valueOf(entry.getWeight());
            int ww = this.font.width(weightText);
            gui.drawString(this.font, weightText, x + width - 10 - ww, ey + 10, 0xFFC0C0C0);
            if (entry.isLevelIncrease()) {
                gui.drawString(this.font, "*", x + width - 20 - ww, ey + 4, 0xFFAA66FF);
            }
            if (!"none".equals(entry.getUpgradeStaffRegistryName()) && !entry.getUpgradeStaffRegistryName().isEmpty()) {
                gui.drawString(this.font, "S", x + width - 32 - ww, ey + 4, 0xFFFFD700);
            }
        }
    }

    private void renderRightPanel(GuiGraphics gui, int mouseX, int mouseY) {
        int x = rightPanelX;
        int y = panelY;
        int width = this.width - x - 4;
        int height = panelHeight;
        gui.fill(x, y, x + width, y + height, 0xFF202020);
        gui.renderOutline(x, y, width, height, 0xFF000000);

        if (selectedIndex < 0 || selectedIndex >= currentEntries.size()) {
            gui.drawCenteredString(this.font, Component.translatable("screen.goetyawaken.spell_config.no_selection"),
                    x + width / 2, y + height / 2 - 8, 0xFF808080);
            return;
        }
        int contentH = rightContentHeight();
        int maxScroll = rightMaxScroll();
        rightScrollOffset = clamp(rightScrollOffset, 0, maxScroll);
        if (maxScroll > 0) {
            int scrollBarX = x + width - 6;
            int scrollBarH = Math.max(10, Math.min(height - 8, height * height / contentH));
            int scrollBarY = y + (rightScrollOffset * (height - scrollBarH) / maxScroll);
            gui.fill(scrollBarX, y, scrollBarX + 4, y + height, 0xFF333333);
            gui.fill(scrollBarX, scrollBarY, scrollBarX + 4, scrollBarY + scrollBarH, 0xFF888888);
        }
        SorcererSpellEntry entry = currentEntries.get(selectedIndex);
        int ex = x + 8;
        int ey = y + 4 - rightScrollOffset;
        int sliderW = Math.max(40, Math.min(80, width - 102));
        int segW = sliderW / 6;
        gui.enableScissor(x, y, x + width, y + height);

        ItemStack focusStack = entry.getFocusStack();
        if (focusStack != null && !focusStack.isEmpty()) {
            gui.renderItem(focusStack, ex, ey);
            gui.renderItemDecorations(this.font, focusStack, ex, ey);
            String name = focusStack.getHoverName().getString();
            gui.drawString(this.font, name, ex + 26, ey + 2, 0xFFFFFFFF);
            String regName = entry.getFocusRegistryName();
            gui.drawString(this.font, regName, ex + 26, ey + 14, 0xFF808080);
        }
        int formY = ey + 32;
        gui.drawString(this.font,
                "- " + Component.translatable("screen.goetyawaken.spell_config.min_level").getString() + " -", ex,
                formY, 0xFF808080);
        formY += 14;
        int minLv = entry.getMinLevel();
        gui.drawString(this.font, String.valueOf(minLv), ex, formY + 2, 0xFFFFFFFF);
        for (int s = 0; s < 6; s++) {
            int sx = ex + 16 + s * segW;
            gui.fill(sx, formY + 5, sx + segW - 1, formY + 9, s < minLv ? 0xFF808080 : 0xFF333333);
        }
        renderSliderLabel(gui, mouseX, mouseY, ex, formY, sliderW, "screen.goetyawaken.spell_config.min_level",
                "tooltip.goetyawaken.spell_config.min_level");

        formY += 14;
        gui.drawString(this.font,
                "- " + Component.translatable("screen.goetyawaken.spell_config.max_level").getString() + " -", ex,
                formY, 0xFF808080);
        formY += 14;
        int maxLv = entry.getMaxLevel();
        gui.drawString(this.font, String.valueOf(maxLv), ex, formY + 2, 0xFFFFFFFF);
        for (int s = 0; s < 6; s++) {
            int sx = ex + 16 + s * segW;
            gui.fill(sx, formY + 5, sx + segW - 1, formY + 9, s < maxLv ? 0xFF808080 : 0xFF333333);
        }
        renderSliderLabel(gui, mouseX, mouseY, ex, formY, sliderW, "screen.goetyawaken.spell_config.max_level",
                "tooltip.goetyawaken.spell_config.max_level");

        formY += 16;
        if (isMouseIn(mouseX, mouseY, ex, formY, width - 16, 10)) {
            renderTooltip(gui, mouseX, mouseY,
                    Component.translatable("tooltip.goetyawaken.spell_config.level_increase"));
        }
        formY += 12;
        String cbText = entry.isLevelIncrease()
                ? "[X] " + Component.translatable("screen.goetyawaken.spell_config.level_increase").getString()
                : "[ ] " + Component.translatable("screen.goetyawaken.spell_config.level_increase").getString();
        gui.drawString(this.font, cbText, ex, formY, entry.isLevelIncrease() ? 0xFFAAFF66 : 0xFF808080);

        formY += 18;
        gui.drawString(this.font,
                "- " + Component.translatable("screen.goetyawaken.spell_config.upgrade_staff").getString() + " -", ex,
                formY, 0xFF808080);
        if (isMouseIn(mouseX, mouseY, ex, formY, width - 16, 10)) {
            renderTooltip(gui, mouseX, mouseY,
                    Component.translatable("tooltip.goetyawaken.spell_config.upgrade_staff"));
        }
        formY += 14;
        int staffClickY = formY;
        ItemStack staffStack = entry.getUpgradeStaff();
        String staffRegName = entry.getUpgradeStaffRegistryName();
        if (staffStack != null && !staffStack.isEmpty()) {
            gui.renderItem(staffStack, ex, formY);
            gui.renderItemDecorations(this.font, staffStack, ex, formY);
            gui.drawString(this.font, staffStack.getHoverName().getString(), ex + 22, formY + 4, 0xFFFFFFFF);
        } else {
            String displayName;
            if ("none".equals(staffRegName) || staffRegName.isEmpty()) {
                displayName = Component.translatable("screen.goetyawaken.spell_config.upgrade_staff.none").getString();
            } else {
                displayName = staffRegName;
            }
            gui.drawString(this.font, displayName, ex, formY + 2, 0xFFFFFFFF);
        }
        if (isMouseIn(mouseX, mouseY, ex, staffClickY, width - 16, 16)) {
            gui.fill(ex, staffClickY, ex + width - 16, staffClickY + 16, 0x20FFFFFF);
        }

        if (showStaffDropdown) {
            renderStaffDropdown(gui, mouseX, mouseY, ex, staffClickY, width, y + height);
        } else {
            formY += 16;
            int staffLv = entry.getUpgradeStaffLevel();
            gui.drawString(this.font, String.valueOf(staffLv), ex, formY + 2, 0xFFFFFFFF);
            for (int s = 0; s < 6; s++) {
                int sx = ex + 16 + s * segW;
                gui.fill(sx, formY + 5, sx + segW - 1, formY + 9, s < staffLv ? 0xFF808080 : 0xFF333333);
            }
            renderSliderLabel(gui, mouseX, mouseY, ex, formY, sliderW, "screen.goetyawaken.spell_config.staff_level",
                    "tooltip.goetyawaken.spell_config.upgrade_staff_level");

            formY += 18;
            gui.drawString(this.font,
                    "- " + Component.translatable("screen.goetyawaken.spell_config.weight").getString() + " -", ex,
                    formY, 0xFF808080);
            if (isMouseIn(mouseX, mouseY, ex, formY, width - 16, 10)) {
                renderTooltip(gui, mouseX, mouseY, Component.translatable("tooltip.goetyawaken.spell_config.weight"));
            }
            formY += 14;
            int wt = entry.getWeight();
            int maxWt = 100;
            int wtSegW = sliderW / 6;
            int wtFilled = (wt - 1) * 6 / 99 + 1;
            for (int s = 0; s < 6; s++) {
                int sx = ex + s * wtSegW;
                gui.fill(sx, formY + 4, sx + wtSegW - 1, formY + 12, s < wtFilled ? 0xFF667F33 : 0xFF333333);
            }
            String pct = (wt * 100 / maxWt) + "%";
            gui.drawString(this.font, pct, ex + sliderW + 4, formY + 4, 0xFFC0C0C0);
            if (isMouseIn(mouseX, mouseY, ex, formY + 4, sliderW, 12)) {
                renderTooltip(gui, mouseX, mouseY,
                        Component.translatable("tooltip.goetyawaken.spell_config.weight_bar"));
            }
            int inputX = ex + sliderW + 50;
            int inputY = formY;
            int inputW = 36;
            int inputH = 14;
            gui.fill(inputX, inputY, inputX + inputW, inputY + inputH, 0xFF000000);
            gui.renderOutline(inputX, inputY, inputW, inputH,
                    weightInputFocused && selectedIndex >= 0 ? 0xFFFFFF00 : 0xFF666666);
            String displayVal = weightInputFocused && selectedIndex >= 0 ? weightInputBuffer
                    : String.valueOf(entry.getWeight());
            gui.drawString(this.font, displayVal, inputX + 4, inputY + 3, 0xFFFFFFFF);
            if (weightInputFocused && selectedIndex >= 0) {
                int cursorX = inputX + 4
                        + this.font.width(displayVal.substring(0, Math.min(weightCursorPos, displayVal.length())));
                if ((System.currentTimeMillis() - lastCursorBlink) % 1000 < 500) {
                    gui.fill(cursorX, inputY + 2, cursorX + 1, inputY + inputH - 2, 0xFFFFFF00);
                }
            }
            if (weightInputFocused && selectedIndex >= 0 && this.font.width(displayVal) > inputW - 6) {
                gui.drawString(this.font, displayVal.substring(Math.max(0, displayVal.length() - 3)), inputX + 4,
                        inputY + 3, 0xFFFFFFFF);
            }

            formY += 20;
            Component moveUpText = Component.translatable("screen.goetyawaken.spell_config.move_up");
            Component moveDownText = Component.translatable("screen.goetyawaken.spell_config.move_down");
            Component deleteText = Component.translatable("screen.goetyawaken.spell_config.delete");
            int btnW = 40;
            gui.fill(ex, formY, ex + btnW, formY + 16, 0xFF444444);
            gui.drawCenteredString(this.font, moveUpText, ex + btnW / 2, formY + 4, 0xFFFFFFFF);
            gui.fill(ex + btnW + 4, formY, ex + btnW * 2 + 4, formY + 16, 0xFF444444);
            gui.drawCenteredString(this.font, moveDownText, ex + btnW + 4 + btnW / 2, formY + 4, 0xFFFFFFFF);
            gui.fill(ex + btnW * 2 + 8, formY, ex + btnW * 2 + 8 + 60, formY + 16, 0xFF662222);
            gui.drawCenteredString(this.font, deleteText, ex + btnW * 2 + 8 + 30, formY + 4, 0xFFFF6666);
            if (isMouseIn(mouseX, mouseY, ex, formY, btnW, 16)) {
                renderTooltip(gui, mouseX, mouseY,
                        Component.translatable("tooltip.goetyawaken.spell_config.move_up"));
            } else if (isMouseIn(mouseX, mouseY, ex + btnW + 4, formY, btnW, 16)) {
                renderTooltip(gui, mouseX, mouseY,
                        Component.translatable("tooltip.goetyawaken.spell_config.move_down"));
            } else if (isMouseIn(mouseX, mouseY, ex + btnW * 2 + 8, formY, 60, 16)) {
                renderTooltip(gui, mouseX, mouseY,
                        Component.translatable("tooltip.goetyawaken.spell_config.delete"));
            }
        }
        gui.disableScissor();
    }

    private int rightContentHeight() {
        return 232;
    }

    private int rightMaxScroll() {
        return Math.max(0, rightContentHeight() - (panelHeight - 4));
    }

    private void renderStaffDropdown(GuiGraphics gui, int mouseX, int mouseY, int ex, int staffClickY, int width,
            int panelBottom) {
        int dropdownX = ex;
        int dropdownY = staffClickY + 18;
        int dropdownW = Math.max(150, width - 16);
        int dropdownH = Math.max(80, Math.min(200, panelBottom - dropdownY - 6));
        gui.fill(dropdownX, dropdownY, dropdownX + dropdownW, dropdownY + dropdownH, 0xFF000000);
        gui.renderOutline(dropdownX, dropdownY, dropdownW, dropdownH, 0xFF808080);

        Map<String, Item> wands = SorcererSpellConfig.getAvailableWandItems();
        List<Map.Entry<String, Item>> wandList = new ArrayList<>(wands.entrySet());
        int itemH = 18;
        int visibleDropdown = dropdownH / itemH;
        int totalItems = wandList.size() + 1;
        int maxWandScroll = Math.max(0, totalItems - visibleDropdown);
        if (staffDropdownScroll > maxWandScroll)
            staffDropdownScroll = maxWandScroll;

        int dRowY = dropdownY + 2;

        if (maxWandScroll > 0) {
            int scrollBarX = dropdownX + dropdownW - 6;
            int scrollBarH = Math.max(10, dropdownH * visibleDropdown / totalItems);
            int scrollBarY = dropdownY + (staffDropdownScroll * (dropdownH - scrollBarH) / maxWandScroll);
            gui.fill(scrollBarX, dropdownY, scrollBarX + 4, dropdownY + dropdownH, 0xFF333333);
            gui.fill(scrollBarX, scrollBarY, scrollBarX + 4, scrollBarY + scrollBarH, 0xFF888888);
        }

        for (int i = 0; i < visibleDropdown && (i + staffDropdownScroll) < totalItems; i++) {
            int idx = i + staffDropdownScroll;
            int dYY = dRowY + i * itemH;
            if (idx == 0) {
                if (isMouseIn(mouseX, mouseY, dropdownX, dYY, dropdownW, itemH)) {
                    gui.fill(dropdownX, dYY, dropdownX + dropdownW, dYY + itemH, 0x40FFFFFF);
                }
                gui.drawString(this.font,
                        Component.translatable("screen.goetyawaken.spell_config.upgrade_staff.none").getString(),
                        dropdownX + 4, dYY + 4, 0xFF808080);
            } else {
                Map.Entry<String, Item> wandEntry = wandList.get(idx - 1);
                if (isMouseIn(mouseX, mouseY, dropdownX, dYY, dropdownW, itemH)) {
                    gui.fill(dropdownX, dYY, dropdownX + dropdownW, dYY + itemH, 0x40FFFFFF);
                }
                gui.renderItem(new ItemStack(wandEntry.getValue()), dropdownX + 2, dYY + 1);
                gui.drawString(this.font, wandEntry.getValue().getDescription().getString(),
                        dropdownX + 22, dYY + 4, 0xFFFFFFFF);
            }
        }
    }

    private void renderBottomBar(GuiGraphics gui, int mouseX, int mouseY) {
        int y = this.height - 32;
        int x = 4;
        int width = this.width - 8;
        gui.fill(x, y, x + width, y + 20, 0xFF101010);
        Component addText = Component.translatable("screen.goetyawaken.spell_config.add");
        Component resetText = Component.translatable("screen.goetyawaken.spell_config.reset");
        Component saveText = Component.translatable("screen.goetyawaken.spell_config.save");
        addBtnW = this.font.width(addText) + 16;
        addBtnX = x + 4;
        gui.fill(addBtnX, y + 2, addBtnX + addBtnW, y + 18, 0xFF444444);
        gui.drawCenteredString(this.font, addText, addBtnX + addBtnW / 2, y + 6, 0xFFFFFFFF);
        if (isMouseIn(mouseX, mouseY, addBtnX, y + 2, addBtnW, 18)) {
            renderTooltip(gui, mouseX, mouseY, Component.translatable("tooltip.goetyawaken.spell_config.add"));
        }
        resetBtnW = this.font.width(resetText) + 16;
        resetBtnX = x + width - 8 - resetBtnW - addBtnW - 8;
        gui.fill(resetBtnX, y + 2, resetBtnX + resetBtnW, y + 18, 0xFF664400);
        gui.drawCenteredString(this.font, resetText, resetBtnX + resetBtnW / 2, y + 6, 0xFFFFCC66);
        if (isMouseIn(mouseX, mouseY, resetBtnX, y + 2, resetBtnW, 18)) {
            renderTooltip(gui, mouseX, mouseY, Component.translatable("tooltip.goetyawaken.spell_config.reset"));
        }
        saveBtnW = this.font.width(saveText) + 16;
        saveBtnX = x + width - 8 - saveBtnW;
        gui.fill(saveBtnX, y + 2, saveBtnX + saveBtnW, y + 18, 0xFF226622);
        gui.drawCenteredString(this.font, saveText, saveBtnX + saveBtnW / 2, y + 6, 0xFF66FF66);
        if (isMouseIn(mouseX, mouseY, saveBtnX, y + 2, saveBtnW, 18)) {
            renderTooltip(gui, mouseX, mouseY, Component.translatable("tooltip.goetyawaken.spell_config.save"));
        }
    }

    private void renderAddPopup(GuiGraphics gui, int mouseX, int mouseY) {
        int px = this.width / 4;
        int py = 30;
        int pw = this.width / 2;
        int ph = this.height - 60;
        gui.fill(px, py, px + pw, py + ph, 0xFF101010);
        gui.renderOutline(px, py, pw, ph, 0xFF808080);
        gui.drawCenteredString(this.font, Component.translatable("screen.goetyawaken.spell_config.select_focus"),
                px + pw / 2, py + 4, 0xFFFFFFFF);
        int searchY = py + 16;
        gui.fill(px + 4, searchY, px + pw - 4, searchY + 14, 0xFF222222);
        gui.renderOutline(px + 4, searchY, pw - 8, 14, 0xFF404040);
        if (searchText.isEmpty()) {
            gui.drawString(this.font, Component.translatable("screen.goetyawaken.spell_config.search"), px + 8,
                    searchY + 4, 0xFF666666);
        } else {
            String beforeCursor = searchText.substring(0, Math.min(searchCursorPos, searchText.length()));
            gui.drawString(this.font, beforeCursor, px + 8, searchY + 4, 0xFFFFFFFF);
            int cursorX = px + 8 + this.font.width(beforeCursor);
            if ((System.currentTimeMillis() - lastCursorBlink) % 1000 < 500) {
                gui.fill(cursorX, searchY + 2, cursorX + 1, searchY + 12, 0xFFFFFF00);
            }
            if (searchCursorPos < searchText.length()) {
                String afterCursor = searchText.substring(searchCursorPos);
                gui.drawString(this.font, afterCursor, cursorX + 1, searchY + 4, 0xFFFFFFFF);
            }
        }
        if (isMouseIn(mouseX, mouseY, px + 4, searchY, pw - 8, 14)) {
            renderTooltip(gui, mouseX, mouseY, Component.translatable("tooltip.goetyawaken.spell_config.search"));
        }
        Map<String, Item> available = SorcererSpellConfig.getAvailableFocusItems();
        List<Map.Entry<String, Item>> filtered = new ArrayList<>();
        for (Map.Entry<String, Item> e : available.entrySet()) {
            if (searchText.isEmpty() || matchesFocus(e.getValue(), searchText)) {
                filtered.add(e);
            }
        }
        int cols = 4;
        int itemSize = 36;
        int gap = (pw - 8 - cols * itemSize) / (cols + 1);
        int gridStartX = px + 4 + gap;
        int gridY = searchY + 20;
        int visibleRows = (ph - 40) / itemSize;
        int maxAddScroll = Math.max(0, (filtered.size() + cols - 1) / cols - visibleRows);
        if (maxAddScroll > 0) {
            int sBarX = px + pw - 8;
            int sBarH = Math.max(10, visibleRows * itemSize * visibleRows / ((filtered.size() + cols - 1) / cols));
            int sBarY = gridY + (addScrollOffset * (visibleRows * itemSize - sBarH) / maxAddScroll);
            gui.fill(sBarX, gridY, sBarX + 4, gridY + visibleRows * itemSize, 0xFF333333);
            gui.fill(sBarX, sBarY, sBarX + 4, sBarY + sBarH, 0xFF888888);
        }
        for (int i = 0; i < visibleRows; i++) {
            for (int c = 0; c < cols; c++) {
                int fi = (i + addScrollOffset) * cols + c;
                if (fi >= filtered.size())
                    break;
                Map.Entry<String, Item> e = filtered.get(fi);
                int ix = gridStartX + c * (itemSize + gap);
                int iy = gridY + i * itemSize;
                gui.renderItem(new ItemStack(e.getValue()), ix + (itemSize - 16) / 2, iy + 2);
                String n = trimStr(e.getValue().getDescription().getString(), 6);
                int tw = this.font.width(n);
                gui.drawString(this.font, n, ix + (itemSize - tw) / 2, iy + 22, 0xFFC0C0C0);
            }
        }
    }

    private boolean matchesFocus(Item focusItem, String query) {
        if (query.isEmpty())
            return true;
        String displayName = focusItem.getDescription().getString();
        String registryName = ForgeRegistries.ITEMS.getKey(focusItem).toString();
        return PinyinIntegration.contains(displayName, query)
                || PinyinIntegration.contains(registryName, query);
    }

    private List<IndexedEntry> getFilteredEntries() {
        List<IndexedEntry> result = new ArrayList<>();
        for (int i = 0; i < currentEntries.size(); i++) {
            SorcererSpellEntry entry = currentEntries.get(i);
            if (searchText.isEmpty()) {
                result.add(new IndexedEntry(entry, i));
            } else {
                ItemStack stack = entry.getFocusStack();
                String name = stack != null ? stack.getHoverName().getString() : entry.getFocusRegistryName();
                if (PinyinIntegration.contains(name, searchText)
                        || PinyinIntegration.contains(entry.getFocusRegistryName(), searchText)) {
                    result.add(new IndexedEntry(entry, i));
                }
            }
        }
        return result;
    }

    private void renderSliderLabel(GuiGraphics gui, int mouseX, int mouseY, int ex, int ey, int sliderW,
            String labelKey, String tooltipKey) {
        Component label = Component.translatable(labelKey);
        int lx = ex + 16 + sliderW + 8;
        if (isMouseIn(mouseX, mouseY, ex, ey, 16 + sliderW + 8 + this.font.width(label), 12)) {
            renderTooltip(gui, mouseX, mouseY, Component.translatable(tooltipKey));
        }
    }

    private void renderTooltip(GuiGraphics gui, int mouseX, int mouseY, Component text) {
        gui.renderTooltip(this.font, text, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (showAddPopup) {
            if (handleAddPopupClick(mouseX, mouseY, button))
                return true;
            if (button == 1) {
                showAddPopup = false;
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (handleLeftPanelClick(mouseX, mouseY, button))
            return true;
        if (handleRightPanelClick(mouseX, mouseY, button))
            return true;
        if (handleBottomBarClick(mouseX, mouseY, button))
            return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleLeftPanelClick(double mX, double mY, int button) {
        if (!isMouseIn((int) mX, (int) mY, 4, panelY, leftPanelWidth, panelHeight))
            return false;
        int relY = (int) mY - panelY;
        int idx = relY / entryHeight + scrollOffset;
        List<IndexedEntry> indexed = getFilteredEntries();
        if (idx >= 0 && idx < indexed.size()) {
            selectedIndex = indexed.get(idx).originalIndex();
            weightInputFocused = false;
            weightInputBuffer = "";
            return true;
        }
        return false;
    }

    private boolean handleRightPanelClick(double mX, double mY, int button) {
        if (selectedIndex < 0 || selectedIndex >= currentEntries.size())
            return false;
        int x = rightPanelX;
        int y = panelY;
        int width = this.width - x - 4;
        int height = panelHeight;
        if (button == 0 && isMouseIn((int) mX, (int) mY, x + width - 6, y, 6, height)) {
            rightScrolling = true;
            return true;
        }
        SorcererSpellEntry entry = currentEntries.get(selectedIndex);
        int ex = x + 8;
        int sliderW = Math.max(40, Math.min(80, width - 102));
        int sliderX = ex + 16;

        if (showStaffDropdown) {
            int staffClickY = y + 36 - rightScrollOffset + 102;
            int dropdownX = ex;
            int dropdownY = staffClickY + 18;
            int dropdownW = Math.max(150, width - 16);
            int dropdownH = Math.max(80, Math.min(200, y + height - dropdownY - 6));
            Map<String, Item> wands = SorcererSpellConfig.getAvailableWandItems();
            List<Map.Entry<String, Item>> wandList = new ArrayList<>(wands.entrySet());
            int itemH = 18;
            int totalItems = wandList.size() + 1;
            int visibleDropdown = dropdownH / itemH;

            for (int i = 0; i < visibleDropdown && (i + staffDropdownScroll) < totalItems; i++) {
                int idx = i + staffDropdownScroll;
                int dYY = dropdownY + 2 + i * itemH;
                if (isMouseIn((int) mX, (int) mY, dropdownX, dYY, dropdownW, itemH)) {
                    if (idx == 0) {
                        entry.setUpgradeStaffRegistryName("none");
                        entry.setUpgradeStaff(ItemStack.EMPTY);
                    } else {
                        Map.Entry<String, Item> wandEntry = wandList.get(idx - 1);
                        entry.setUpgradeStaffRegistryName(wandEntry.getKey());
                        entry.setUpgradeStaff(new ItemStack(wandEntry.getValue()));
                    }
                    showStaffDropdown = false;
                    return true;
                }
            }

            if (!isMouseIn((int) mX, (int) mY, dropdownX, dropdownY, dropdownW, dropdownH)) {
                showStaffDropdown = false;
                return true;
            }
            return true;
        }

        int formY = y + 36 - rightScrollOffset;

        formY += 14;
        if (isMouseIn((int) mX, (int) mY, sliderX, formY, sliderW, 10)) {
            entry.setMinLevel(sliderValue((int) mX, sliderX, sliderW, 5) + 1);
            return true;
        }

        formY += 28;
        if (isMouseIn((int) mX, (int) mY, sliderX, formY, sliderW, 10)) {
            entry.setMaxLevel(sliderValue((int) mX, sliderX, sliderW, 5) + 1);
            return true;
        }

        formY += 16 + 12;
        if (isMouseIn((int) mX, (int) mY, ex, formY, 200, 12)) {
            entry.setLevelIncrease(!entry.isLevelIncrease());
            return true;
        }

        formY += 18 + 14;
        if (isMouseIn((int) mX, (int) mY, ex, formY, width - 16, 16)) {
            showStaffDropdown = !showStaffDropdown;
            staffDropdownScroll = 0;
            return true;
        }

        formY += 16;
        if (isMouseIn((int) mX, (int) mY, sliderX, formY, sliderW, 10)) {
            entry.setUpgradeStaffLevel(sliderValue((int) mX, sliderX, sliderW, 5) + 1);
            return true;
        }

        formY += 18 + 14;
        int wtInputX = ex + sliderW + 50;
        int wtInputW = 36;
        int wtInputH = 14;
        if (isMouseIn((int) mX, (int) mY, wtInputX, formY, wtInputW, wtInputH)) {
            weightInputFocused = true;
            weightInputBuffer = String.valueOf(entry.getWeight());
            weightCursorPos = weightInputBuffer.length();
            return true;
        }
        if (isMouseIn((int) mX, (int) mY, ex, formY + 4, sliderW, 12)) {
            entry.setWeight(sliderValue((int) mX, ex, sliderW, 99) + 1);
            weightInputFocused = false;
            return true;
        }

        formY += 20;
        int btnW = 40;
        if (isMouseIn((int) mX, (int) mY, ex, formY, btnW, 16)) {
            if (selectedIndex > 0) {
                SorcererSpellEntry e = currentEntries.remove(selectedIndex);
                currentEntries.add(selectedIndex - 1, e);
                selectedIndex--;
            }
            return true;
        }
        if (isMouseIn((int) mX, (int) mY, ex + btnW + 4, formY, btnW, 16)) {
            if (selectedIndex < currentEntries.size() - 1) {
                SorcererSpellEntry e = currentEntries.remove(selectedIndex);
                currentEntries.add(selectedIndex + 1, e);
                selectedIndex++;
            }
            return true;
        }
        if (isMouseIn((int) mX, (int) mY, ex + btnW * 2 + 8, formY, 60, 16)) {
            currentEntries.remove(selectedIndex);
            if (selectedIndex >= currentEntries.size())
                selectedIndex = currentEntries.size() - 1;
            return true;
        }
        return false;
    }

    private boolean handleBottomBarClick(double mX, double mY, int button) {
        int y = this.height - 32;
        if (isMouseIn((int) mX, (int) mY, addBtnX, y + 2, addBtnW, 18)) {
            showAddPopup = true;
            addScrollOffset = 0;
            searchText = "";
            searchCursorPos = 0;
            return true;
        }
        if (isMouseIn((int) mX, (int) mY, resetBtnX, y + 2, resetBtnW, 18)) {
            currentEntries.clear();
            List<SorcererSpellEntry> defaults = SorcererSpellConfig.getDefaultEntries();
            SorcererSpellConfig.resolveRuntimeFields(defaults);
            for (SorcererSpellEntry e : defaults) {
                currentEntries.add(copy(e));
            }
            return true;
        }
        if (isMouseIn((int) mX, (int) mY, saveBtnX, y + 2, saveBtnW, 18)) {
            ModNetwork.sendToServer(new SSaveSpellConfigPacket(new ArrayList<>(currentEntries)));
            return true;
        }
        return false;
    }

    private boolean handleAddPopupClick(double mX, double mY, int button) {
        int px = this.width / 4;
        int py = 30;
        int pw = this.width / 2;
        int ph = this.height - 60;
        if (!isMouseIn((int) mX, (int) mY, px, py, pw, ph)) {
            showAddPopup = false;
            return true;
        }
        int searchY = py + 16;
        if (isMouseIn((int) mX, (int) mY, px + 4, searchY, pw - 8, 14)) {
            int relX = (int) mX - (px + 8);
            searchCursorPos = findCursorPos(searchText, relX);
            return true;
        }
        Map<String, Item> available = SorcererSpellConfig.getAvailableFocusItems();
        List<Map.Entry<String, Item>> filtered = new ArrayList<>();
        for (Map.Entry<String, Item> e : available.entrySet()) {
            if (searchText.isEmpty() || matchesFocus(e.getValue(), searchText)) {
                filtered.add(e);
            }
        }
        int cols = 4;
        int itemSize = 36;
        int gap = (pw - 8 - cols * itemSize) / (cols + 1);
        int gridStartX = px + 4 + gap;
        int gridY = searchY + 20;
        int visibleRows = (ph - 40) / itemSize;
        for (int i = 0; i < visibleRows; i++) {
            for (int c = 0; c < cols; c++) {
                int fi = (i + addScrollOffset) * cols + c;
                if (fi >= filtered.size())
                    break;
                Map.Entry<String, Item> e = filtered.get(fi);
                int ix = gridStartX + c * (itemSize + gap);
                int iy = gridY + i * itemSize;
                if (isMouseIn((int) mX, (int) mY, ix, iy, itemSize, itemSize)) {
                    SorcererSpellEntry newEntry = SorcererSpellEntry.createDefault(e.getKey());
                    newEntry.setSpell(com.Polarice3.Goety.common.magic.Spell.class.isInstance(
                            ((com.Polarice3.Goety.api.items.magic.IFocus) e.getValue()).getSpell())
                                    ? (com.Polarice3.Goety.common.magic.Spell) ((com.Polarice3.Goety.api.items.magic.IFocus) e
                                            .getValue()).getSpell()
                                    : null);
                    newEntry.setFocusStack(new ItemStack(e.getValue()));
                    currentEntries.add(newEntry);
                    showAddPopup = false;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (showStaffDropdown) {
            int staffClickY = panelY + 138 - rightScrollOffset;
            int dropdownY = staffClickY + 18;
            int dropdownH = Math.max(80, Math.min(200, panelY + panelHeight - dropdownY - 6));
            int visibleDropdown = Math.max(1, dropdownH / 18);
            Map<String, Item> wands = SorcererSpellConfig.getAvailableWandItems();
            int totalItems = wands.size() + 1;
            int maxWandScroll = Math.max(0, totalItems - visibleDropdown);
            staffDropdownScroll = clamp(staffDropdownScroll - (int) delta, 0, maxWandScroll);
            return true;
        }
        if (showAddPopup) {
            Map<String, Item> available = SorcererSpellConfig.getAvailableFocusItems();
            List<Map.Entry<String, Item>> filtered = new ArrayList<>();
            for (Map.Entry<String, Item> e : available.entrySet()) {
                if (searchText.isEmpty() || matchesFocus(e.getValue(), searchText)) {
                    filtered.add(e);
                }
            }
            int cols = 4;
            int visibleRows = (panelHeight - 40) / 36;
            int maxScroll = Math.max(0, (filtered.size() + cols - 1) / cols - visibleRows);
            addScrollOffset = clamp(addScrollOffset - (int) delta, 0, maxScroll);
            return true;
        }
        List<IndexedEntry> indexed = getFilteredEntries();
        int maxScroll = Math.max(0, indexed.size() - visibleEntries);
        if (isMouseIn((int) mouseX, (int) mouseY, 4, panelY, leftPanelWidth, panelHeight)) {
            scrollOffset = clamp(scrollOffset - (int) delta, 0, maxScroll);
            return true;
        }
        if (isMouseIn((int) mouseX, (int) mouseY, rightPanelX, panelY, this.width - rightPanelX - 4,
                panelHeight)) {
            rightScrollOffset = clamp(rightScrollOffset - (int) delta, 0, rightMaxScroll());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && rightScrolling) {
            if (mouseY < panelY) {
                rightScrollOffset = 0;
            } else if (mouseY > panelY + panelHeight) {
                rightScrollOffset = rightMaxScroll();
            } else {
                int i = panelHeight;
                int j = Math.max(10, Math.min(i - 8, i * i / rightContentHeight()));
                double d1 = Math.max(1.0, (double) rightMaxScroll() / (i - j));
                rightScrollOffset = clamp((int) (rightScrollOffset + dragY * d1), 0, rightMaxScroll());
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        rightScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (weightInputFocused && selectedIndex >= 0) {
            if (codePoint >= '0' && codePoint <= '9') {
                weightInputBuffer = weightInputBuffer.substring(0, weightCursorPos) + codePoint
                        + weightInputBuffer.substring(weightCursorPos);
                weightCursorPos++;
                applyWeightInput();
                return true;
            } else if ((codePoint == 8 || codePoint == 127) && !weightInputBuffer.isEmpty() && weightCursorPos > 0) {
                weightInputBuffer = weightInputBuffer.substring(0, weightCursorPos - 1)
                        + weightInputBuffer.substring(weightCursorPos);
                weightCursorPos--;
                applyWeightInput();
                return true;
            } else if (codePoint == 257 || codePoint == 335) {
                weightInputFocused = false;
                applyWeightInput();
                return true;
            }
            return true;
        }
        if (showAddPopup) {
            if (codePoint == 22) {
                String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
                searchText = searchText.substring(0, searchCursorPos) + clipboard
                        + searchText.substring(searchCursorPos);
                searchCursorPos += clipboard.length();
            } else if ((codePoint == 8 || codePoint == 127) && !searchText.isEmpty() && searchCursorPos > 0) {
                searchText = searchText.substring(0, searchCursorPos - 1) + searchText.substring(searchCursorPos);
                searchCursorPos--;
            } else if (codePoint >= 32 && codePoint != 127) {
                searchText = searchText.substring(0, searchCursorPos) + codePoint
                        + searchText.substring(searchCursorPos);
                searchCursorPos++;
            }
            addScrollOffset = 0;
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (weightInputFocused && selectedIndex >= 0) {
            if (keyCode == 261) {
                if (weightCursorPos < weightInputBuffer.length()) {
                    weightInputBuffer = weightInputBuffer.substring(0, weightCursorPos)
                            + weightInputBuffer.substring(weightCursorPos + 1);
                    applyWeightInput();
                }
                return true;
            } else if (keyCode == 259) {
                if (weightCursorPos > 0 && !weightInputBuffer.isEmpty()) {
                    weightInputBuffer = weightInputBuffer.substring(0, weightCursorPos - 1)
                            + weightInputBuffer.substring(weightCursorPos);
                    weightCursorPos--;
                    applyWeightInput();
                }
                return true;
            } else if (keyCode == 263) {
                if (weightCursorPos > 0)
                    weightCursorPos--;
                return true;
            } else if (keyCode == 262) {
                if (weightCursorPos < weightInputBuffer.length())
                    weightCursorPos++;
                return true;
            } else if (keyCode == 268) {
                weightCursorPos = 0;
                return true;
            } else if (keyCode == 269) {
                weightCursorPos = weightInputBuffer.length();
                return true;
            } else if (keyCode == 65 && Screen.hasControlDown()) {
                weightCursorPos = 0;
                return true;
            } else if (keyCode == 67 && Screen.hasControlDown()) {
                Minecraft.getInstance().keyboardHandler.setClipboard(weightInputBuffer);
                return true;
            } else if (keyCode == 88 && Screen.hasControlDown()) {
                Minecraft.getInstance().keyboardHandler.setClipboard(weightInputBuffer);
                weightInputBuffer = "";
                weightCursorPos = 0;
                applyWeightInput();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (showAddPopup) {
            if (keyCode == 261) {
                if (searchCursorPos < searchText.length()) {
                    searchText = searchText.substring(0, searchCursorPos) + searchText.substring(searchCursorPos + 1);
                }
                addScrollOffset = 0;
                return true;
            } else if (keyCode == 259) {
                if (searchCursorPos > 0 && !searchText.isEmpty()) {
                    searchText = searchText.substring(0, searchCursorPos - 1) + searchText.substring(searchCursorPos);
                    searchCursorPos--;
                }
                addScrollOffset = 0;
                return true;
            } else if (keyCode == 263) {
                if (searchCursorPos > 0)
                    searchCursorPos--;
                return true;
            } else if (keyCode == 262) {
                if (searchCursorPos < searchText.length())
                    searchCursorPos++;
                return true;
            } else if (keyCode == 268) {
                searchCursorPos = 0;
                return true;
            } else if (keyCode == 269) {
                searchCursorPos = searchText.length();
                return true;
            } else if (keyCode == 256) {
                showAddPopup = false;
                return true;
            } else if (keyCode == 65 && Screen.hasControlDown()) {
                searchCursorPos = 0;
                return true;
            } else if (keyCode == 67 && Screen.hasControlDown()) {
                Minecraft.getInstance().keyboardHandler.setClipboard(searchText);
                return true;
            } else if (keyCode == 88 && Screen.hasControlDown()) {
                Minecraft.getInstance().keyboardHandler.setClipboard(searchText);
                searchText = "";
                searchCursorPos = 0;
                addScrollOffset = 0;
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    private boolean isMouseIn(int mX, int mY, int x, int y, int w, int h) {
        return mX >= x && mX <= x + w && mY >= y && mY <= y + h;
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private int sliderValue(int mouseX, int sliderX, int sliderW, int max) {
        int step = (mouseX - sliderX) * (max + 1) / sliderW;
        return clamp(step, 0, max);
    }

    private String trimStr(String s, int maxLen) {
        if (s.length() <= maxLen)
            return s;
        return s.substring(0, maxLen - 1) + ".";
    }

    private int findCursorPos(String text, int relX) {
        for (int i = 0; i <= text.length(); i++) {
            if (this.font.width(text.substring(0, i)) > relX) {
                return Math.max(0, i - 1);
            }
        }
        return text.length();
    }

    private void applyWeightInput() {
        if (selectedIndex < 0 || selectedIndex >= currentEntries.size())
            return;
        if (weightInputBuffer.isEmpty())
            return;
        try {
            int val = Integer.parseInt(weightInputBuffer);
            val = clamp(val, 1, 100);
            currentEntries.get(selectedIndex).setWeight(val);
        } catch (NumberFormatException ignored) {
        }
    }
}
