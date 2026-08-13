package com.k1sak1.goetyawaken.client.screen;

import com.k1sak1.goetyawaken.common.network.client.SApostleProgressSyncPacket;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ApostleProgressScreen extends Screen {
    private static final int ROW_COUNT = 12;
    private static final int ROW_HEIGHT = 40;

    private final ApostleUpgradeData data;
    private final double[] targets;
    private int scrollOffset = 0;
    private int visibleRows;

    public ApostleProgressScreen(SApostleProgressSyncPacket packet) {
        super(Component.translatable("screen.goetyawaken.apostle_progress.title", packet.getEntityName()));
        this.data = ApostleUpgradeData.loadNBT(packet.getDataTag());
        this.targets = packet.getTargets();
    }

    @Override
    protected void init() {
        super.init();
        this.visibleRows = Math.max(1, (this.height - 30) / ROW_HEIGHT);
        this.scrollOffset = Mth.clamp(this.scrollOffset, 0, Math.max(0, ROW_COUNT - this.visibleRows));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int maxScroll = Math.max(0, ROW_COUNT - this.visibleRows);
        this.scrollOffset = Mth.clamp(this.scrollOffset - (int) delta, 0, maxScroll);
        return true;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gui);
        gui.drawCenteredString(this.font, this.title, this.width / 2, 6, 0xFFFFFFFF);
        int x = 10;
        int y = 22;
        int rowWidth = this.width - 24;
        int maxScroll = Math.max(0, ROW_COUNT - this.visibleRows);
        if (maxScroll > 0) {
            int scrollBarX = x + rowWidth + 2;
            int scrollBarH = Math.max(10, (this.height - 30) * this.visibleRows / ROW_COUNT);
            int scrollBarY = y + (this.scrollOffset * ((this.height - 30) - scrollBarH) / maxScroll);
            gui.fill(scrollBarX, y, scrollBarX + 2, y + this.height - 30, 0xFF333333);
            gui.fill(scrollBarX, scrollBarY, scrollBarX + 2, scrollBarY + scrollBarH, 0xFF888888);
        }
        for (int i = 0; i < this.visibleRows; i++) {
            int row = i + this.scrollOffset;
            if (row >= ROW_COUNT) {
                break;
            }
            int rowY = y + i * ROW_HEIGHT;
            if (rowY + ROW_HEIGHT > this.height - 8) {
                break;
            }
            renderRow(gui, row, x, rowY, rowWidth);
        }
    }

    private void renderRow(GuiGraphics gui, int row, int x, int rowY, int width) {
        boolean achieved = isAchieved(row);
        gui.fill(x, rowY, x + width, rowY + ROW_HEIGHT, achieved ? 0xFF162A16 : 0xFF202020);
        gui.renderOutline(x, rowY, width, ROW_HEIGHT, achieved ? 0xFF55AA55 : 0xFF000000);

        int progressAreaWidth = Math.min(Math.max(width / 3, 90), 160);
        int textAreaWidth = Math.max(width - progressAreaWidth - 8, 60);

        String title = Component.translatable("title.goety." + row).getString().trim();
        if (achieved) {
            title = title + " " + Component.translatable("gui.goetyawaken.apostle_progress.achieved").getString();
        }
        gui.drawString(this.font, trimText(title, textAreaWidth), x + 4, rowY + 3,
                achieved ? 0xFF55FF55 : 0xFFFFFFFF);

        String desc = descFor(row);
        int descY = rowY + 15;
        int line = 0;
        for (String descLine : wrapText(desc, textAreaWidth)) {
            if (descY + line * 9 > rowY + ROW_HEIGHT - 3) {
                break;
            }
            gui.drawString(this.font, descLine, x + 4, descY + line * 9, 0xFFA0A0A0);
            line++;
        }

        int barX = x + width - progressAreaWidth + 2;
        int barW = progressAreaWidth - 4;
        double current = currentFor(row);
        double target = targetFor(row);
        double ratio = target <= 0 ? 0.0 : Math.min(1.0, current / target);
        gui.drawString(this.font, trimText(formatValue(current) + " / " + formatValue(target), barW),
                barX, rowY + 3, 0xFFC0C0C0);
        gui.fill(barX, rowY + 14, barX + barW, rowY + 22, 0xFF333333);
        gui.fill(barX, rowY + 14, barX + (int) (barW * ratio), rowY + 22, achieved ? 0xFF55AA55 : 0xFFAA8800);
        String sub = subText(row);
        if (!sub.isEmpty()) {
            gui.drawString(this.font, trimText(sub, barW), barX, rowY + 26, 0xFF808080);
        }
    }

    private String descFor(int row) {
        return switch (row) {
            case 3 -> Component.translatable("gui.goetyawaken.apostle_progress.condition.3",
                    formatValue(this.targets[3]), formatValue(this.targets[4])).getString();
            case 4 -> Component.translatable("gui.goetyawaken.apostle_progress.condition.4",
                    formatValue(this.targets[5]), formatValue(this.targets[6])).getString();
            case 5 -> Component.translatable("gui.goetyawaken.apostle_progress.condition.5",
                    formatValue(this.targets[7]), formatValue(this.targets[8])).getString();
            default -> Component.translatable("gui.goetyawaken.apostle_progress.condition." + row,
                    formatValue(targetFor(row))).getString();
        };
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (this.font.width(text) <= maxWidth) {
            lines.add(text);
            return lines;
        }
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String candidate = current.toString() + text.charAt(i);
            if (this.font.width(candidate) > maxWidth && current.length() > 0) {
                lines.add(current.toString());
                current = new StringBuilder();
            }
            current.append(text.charAt(i));
        }
        if (current.length() > 0) {
            lines.add(current.toString());
        }
        return lines;
    }

    private String trimText(String text, int maxWidth) {
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        String result = text;
        while (this.font.width(result) > maxWidth && !result.isEmpty()) {
            result = result.substring(0, result.length() - 1);
        }
        return result + "...";
    }

    private double currentFor(int row) {
        return switch (row) {
            case 0 -> this.data.getHealAmount();
            case 1 -> this.data.getDamageDealt();
            case 2 -> this.data.getBlightKills();
            case 3 -> this.data.getWitherDamage();
            case 4 -> this.data.getWardenDamage();
            case 5 -> this.data.getPositiveEffects();
            case 6 -> this.data.getBlazeKills();
            case 7 -> this.data.getTradingProgress();
            case 8 -> this.data.getFrozenDamage();
            case 9 -> this.data.getSwiftTicks();
            case 10 -> this.data.getCultistFollowers();
            case 11 -> this.data.getVillagerKills();
            default -> 0;
        };
    }

    private double targetFor(int row) {
        return switch (row) {
            case 0 -> this.targets[0];
            case 1 -> this.targets[1];
            case 2 -> this.targets[2];
            case 3 -> this.targets[3];
            case 4 -> this.targets[5];
            case 5 -> this.targets[7];
            case 6 -> this.targets[9];
            case 7 -> this.targets[10];
            case 8 -> this.targets[11];
            case 9 -> this.targets[12];
            case 10 -> this.targets[13];
            case 11 -> this.targets[14];
            default -> 1;
        };
    }

    private boolean isAchieved(int row) {
        return switch (row) {
            case 3 -> this.data.getWitherDamage() >= this.targets[3]
                    && this.data.getWitherKills() >= this.targets[4];
            case 4 -> this.data.getWardenDamage() >= this.targets[5]
                    && this.data.getWardenKills() >= this.targets[6];
            case 5 -> this.data.getPositiveEffects() >= this.targets[7]
                    || this.data.getNegativeEffects() >= this.targets[8];
            default -> currentFor(row) >= targetFor(row);
        };
    }

    private String subText(int row) {
        return switch (row) {
            case 3 -> this.data.getWitherKills() + " / " + formatValue(this.targets[4]);
            case 4 -> this.data.getWardenKills() + " / " + formatValue(this.targets[6]);
            case 5 -> this.data.getNegativeEffects() + " / " + formatValue(this.targets[8]);
            default -> "";
        };
    }

    private String formatValue(double value) {
        if (value >= 1.0E7) {
            return String.format("%.1fM", value / 1.0E6);
        }
        if (value >= 1.0E4) {
            return String.format("%.1fk", value / 1000.0);
        }
        return String.format("%.0f", value);
    }
}
