package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class NamelessOneQuoteHandler {
	public static final NamelessOneQuoteHandler INSTANCE = new NamelessOneQuoteHandler();
	private static final RandomSource RANDOM = RandomSource.create();
	private NamelessOneQuote currentQuote = null;
	private long startedPlaying = -1;
	private int delayTicks = -1;
	private boolean shownExperimentalInfo = false;

	private NamelessOneQuoteHandler() {

	}

	private double getPlayTime() {
		long millis = System.currentTimeMillis() - this.startedPlaying;
		return ((double) millis) / 1000;
	}

	public void playQuote(NamelessOneQuote quote, int delayTicks) {
		if (this.currentQuote == null) {
			this.currentQuote = quote;
			this.delayTicks = delayTicks;
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.player == Minecraft.getInstance().player) {
			if (this.delayTicks > 0 && !(Minecraft.getInstance().screen instanceof LevelLoadingScreen)
					&& !(Minecraft.getInstance().screen instanceof ReceivingLevelScreen)) {
				this.delayTicks--;
				if (this.delayTicks == 0) {
					if (this.currentQuote != null && this.currentQuote.getSubtitles() != null) {
						// 莫得配音，留空处理
					}

					this.startedPlaying = System.currentTimeMillis();
				}
			}
		}
	}

	@SubscribeEvent
	public void onOverlayRender(RenderGuiEvent.Post event) {
		if (Minecraft.getInstance().screen != null || this.currentQuote == null || this.delayTicks > 0)
			return;

		this.drawQuote(event.getGuiGraphics(), event.getWindow());
	}

	@SubscribeEvent
	public void onScreenRender(ScreenEvent.Render.Post event) {
		if (this.currentQuote != null && this.delayTicks <= 0) {
			this.drawQuote(event.getGuiGraphics(), Minecraft.getInstance().getWindow());
			Minecraft.getInstance().getSoundManager().resume();
		}
	}

	private void drawQuote(GuiGraphics graphics, Window window) {
		if (this.currentQuote.getSubtitles().getDuration() - this.getPlayTime() <= 0.1) {
			this.currentQuote = null;
			this.startedPlaying = this.delayTicks = -1;
			return;
		}

		if (this.getPlayTime() < 0.05)
			return;

		NamelessOneSubtitles subtitles = this.currentQuote.getSubtitles();

		Font font = Minecraft.getInstance().font;
		String[] text = wrapString(subtitles.getLine(this.getPlayTime()), font, 260);

		int alphaMod = 0xFF;

		if (this.getPlayTime() < 0.5) {
			alphaMod *= this.getPlayTime() / 0.5;
		} else if (this.currentQuote.getSubtitles().getDuration() - this.getPlayTime() < 0.5) {
			alphaMod *= (this.currentQuote.getSubtitles().getDuration() - this.getPlayTime()) / 0.5;
		}

		if (alphaMod < 0) {
			alphaMod = 0xFF;
		}

		PoseStack stack = graphics.pose();
		int width = window.getGuiScaledWidth() / 2 - greatestWidth(font, text) / 2;
		int height = window.getGuiScaledHeight() - 70 - ((font.lineHeight + 2) * (text.length - 1));

		stack.pushPose();
		stack.scale(1F, 1F, 1F);

		int fromX = width, fromY = height, toX = fromX + greatestWidth(font, text),
				toY = fromY + (font.lineHeight * text.length) + 2 * text.length - 1;
		int color1 = 0x000000 | (((int) (alphaMod * 0.266)) << 24);
		int color2 = ChatFormatting.GREEN.getColor() | ((alphaMod) << 24);
		drawGradientRect(graphics, 0, fromX - 4, fromY - 4, toX + 4, toY + 4, color1, color1);
		drawGradientRect(graphics, 0, fromX - 6, fromY - 6, toX + 6, toY + 6, color1, color1);
		drawGradientRect(graphics, 0, fromX - 8, fromY - 8, toX + 8, toY + 8, color1, color1);

		int counter = 0;
		for (String line : text) {
			graphics.drawString(font, line, window.getGuiScaledWidth() / 2 - font.width(line) / 2, height +
					(counter * (font.lineHeight + 2)), color2, true);
			counter++;
		}

		stack.popPose();
	}

	private void drawGradientRect(GuiGraphics graphics, int zLevel, int left, int top, int right, int bottom,
			int startColor, int endColor) {
		graphics.fillGradient(left, top, right, bottom, startColor, endColor);
	}

	private int greatestWidth(Font font, String[] text) {
		int maxWidth = 0;
		for (String line : text) {
			maxWidth = Math.max(maxWidth, font.width(line));
		}
		return maxWidth;
	}

	private String[] wrapString(String str, Font font, int maxWidth) {
		if (str == null)
			return new String[] { "" };

		var list = font.getSplitter().splitLines(str, maxWidth, net.minecraft.network.chat.Style.EMPTY);
		String[] lines = new String[list.size()];

		for (int i = 0; i < lines.length; i++) {
			net.minecraft.network.chat.FormattedText text = list.get(i);
			lines[i] = text.getString();
		}

		return lines;
	}
}