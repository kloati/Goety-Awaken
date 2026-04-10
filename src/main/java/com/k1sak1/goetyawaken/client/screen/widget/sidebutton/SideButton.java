package com.k1sak1.goetyawaken.client.screen.widget.sidebutton;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SideButton extends Button {
    public static final ResourceLocation ICONS_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/gui/icons.png");
    private static final int WIDTH = 18;
    private static final int HEIGHT = 18;

    public SideButton(int x, int y, OnPress onPress) {
        super(x, y, WIDTH, HEIGHT, Component.empty(), onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        isHovered = mouseX >= getX() && mouseX < getX() + width && mouseY >= getY() && mouseY < getY() + height;

        graphics.blit(ICONS_TEXTURE, getX(), getY(), 238, isHovered ? 35 : 16, WIDTH, HEIGHT);

        renderButtonIcon(graphics, getX() + 1, getY() + 1);

        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f);
            graphics.blit(ICONS_TEXTURE, getX(), getY(), 238, 54, WIDTH, HEIGHT);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();

            List<Component> tooltipLines = Arrays.stream(getSideButtonTooltip().split("\n"))
                    .map(Component::literal)
                    .collect(Collectors.toList());
            graphics.renderTooltip(net.minecraft.client.Minecraft.getInstance().font,
                    tooltipLines, java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    protected abstract void renderButtonIcon(GuiGraphics graphics, int x, int y);

    protected abstract String getSideButtonTooltip();
}
