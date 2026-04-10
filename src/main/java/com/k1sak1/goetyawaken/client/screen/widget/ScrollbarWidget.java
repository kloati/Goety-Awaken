package com.k1sak1.goetyawaken.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class ScrollbarWidget {
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;

    private int x;
    private int y;
    private int height;

    private boolean enabled = false;
    private int offset = 0;
    private int maxOffset = 0;
    private boolean clicked = false;

    public ScrollbarWidget(int x, int y, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = Mth.clamp(offset, 0, maxOffset);
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = Math.max(0, maxOffset);
        if (offset > this.maxOffset) {
            offset = this.maxOffset;
        }
    }

    public int getMaxOffset() {
        return maxOffset;
    }

    public void render(GuiGraphics graphics) {
        graphics.fill(x, y, x + SCROLLER_WIDTH, y + height, 0xFF2B2B2B);

        if (enabled && maxOffset > 0) {
            int scrollAreaHeight = height - SCROLLER_HEIGHT;
            float ratio = (float) offset / (float) maxOffset;
            int scrollerY = y + (int) (ratio * scrollAreaHeight);
            graphics.fill(x, scrollerY, x + SCROLLER_WIDTH, scrollerY + SCROLLER_HEIGHT, 0xFFCCCCCC);
            graphics.fill(x, scrollerY, x + SCROLLER_WIDTH - 1, scrollerY + SCROLLER_HEIGHT - 1, 0xFFFFFFFF);
            graphics.fill(x + 1, scrollerY + 1, x + SCROLLER_WIDTH - 1, scrollerY + SCROLLER_HEIGHT - 1, 0xFF8B8B8B);
        } else {
            graphics.fill(x, y, x + SCROLLER_WIDTH, y + SCROLLER_HEIGHT, 0xFF6B6B6B);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && enabled && isInBounds(mouseX, mouseY)) {
            clicked = true;
            mouseDragged(mouseX, mouseY);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (clicked) {
            clicked = false;
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY) {
        if (clicked && enabled && maxOffset > 0) {
            int scrollAreaHeight = height - SCROLLER_HEIGHT;
            float relY = (float) (mouseY - y - SCROLLER_HEIGHT / 2.0) / (float) scrollAreaHeight;
            relY = Mth.clamp(relY, 0, 1);
            offset = Math.round(relY * maxOffset);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (enabled && maxOffset > 0) {
            setOffset(offset - (int) Math.signum(delta));
            return true;
        }
        return false;
    }

    private boolean isInBounds(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + SCROLLER_WIDTH && mouseY >= y && mouseY < y + height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }
}
