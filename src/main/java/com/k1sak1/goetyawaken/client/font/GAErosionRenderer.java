package com.k1sak1.goetyawaken.client.font;

import com.k1sak1.goetyawaken.client.typography.GATextMetadata;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.awt.Color;

public class GAErosionRenderer {
      public static final GAErosionRenderer INSTANCE = new GAErosionRenderer();
      public static final ThreadLocal<Boolean> IS_RENDERING = ThreadLocal.withInitial(() -> false);

      public float drawInBatchF(Font fontInstance, FormattedCharSequence text, float x, float y, int color,
                  boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode,
                  int overlay,
                  int light) {
            if (this.hasErosionEffect(text)) {
                  String[] erosionParts = FontTextBuilder.splitErosionText(text);

                  IS_RENDERING.set(true);
                  try {
                        return this.renderErosion(fontInstance, erosionParts[0], x, y, color, dropShadow,
                                    matrix, bufferSource, displayMode, overlay, light);
                  } finally {
                        IS_RENDERING.set(false);
                  }
            } else {
                  return (float) fontInstance.drawInBatch(text, x, y, color, dropShadow, matrix, bufferSource,
                              displayMode,
                              overlay, light);
            }
      }

      private boolean hasErosionEffect(FormattedCharSequence text) {
            boolean[] hasErosion = new boolean[] { false };
            text.accept((index, style, codePoint) -> {
                  TextColor textColor = style.getColor();
                  if (textColor != null && GATextMetadata.hasMetadata(textColor)) {
                        String effectId = GATextMetadata.get(textColor).effectId();
                        if ("goetyawaken:erosion".equals(effectId)) {
                              hasErosion[0] = true;
                              return false;
                        }
                  }

                  return true;
            });
            return hasErosion[0];
      }

      private float renderErosion(Font fontInstance, String text, float startX, float startY, int iColor,
                  boolean dropShadow, Matrix4f matrix4f, MultiBufferSource bufferSource, Font.DisplayMode displayMode,
                  int overlay, int light) {
            long milliTime = Util.getMillis();
            float posX = startX;
            float timeSeconds = (float) milliTime * 0.001F;

            long scanLineCycle = 4000L;
            float scanProgress = ((float) (milliTime % scanLineCycle) / (float) scanLineCycle);
            float smoothedProgress = scanProgress * scanProgress * (3.0F - 2.0F * scanProgress);
            float scanLinePos = startX + smoothedProgress * 150.0F;
            float scanLineWidth = 15.0F;
            float saturation = 0.70F;
            float brightness = 0.55F;

            for (int i = 0; i < text.length(); ++i) {
                  char currentChar = text.charAt(i);
                  float charPos = startX + (float) i * 6.0F;

                  float baseHue = 0.25F;
                  float timeHueWave = (float) Math.sin(timeSeconds * 0.15F + (float) i * 0.12F) * 0.08F;
                  float charHue = baseHue + timeHueWave;
                  charHue = Mth.clamp(charHue, 0.15F, 0.35F);

                  Color hsb = Color.getHSBColor(charHue, saturation, brightness);
                  int baseR = hsb.getRed();
                  int baseG = hsb.getGreen();
                  int baseB = hsb.getBlue();

                  float alphaPhase = timeSeconds * 0.30F + (float) i * 0.25F;
                  float alphaWave1 = (float) Math.sin(alphaPhase * 1.0F) * 0.12F + 0.88F;
                  float alphaWave2 = (float) Math.cos(alphaPhase * 0.7F + 0.5F) * 0.08F + 0.92F;
                  float alphaBase = (alphaWave1 + alphaWave2) * 0.5F;

                  float erosionPhase = timeSeconds * 0.35F + (float) i * 0.30F;
                  float erosion1 = (float) Math.sin(erosionPhase * 1.5F + (float) i * 1.2F) * 0.5F + 0.5F;
                  float erosion2 = (float) Math.sin(erosionPhase * 1.3F + (float) i * 1.5F + 1.0F) * 0.5F + 0.5F;
                  float erosion3 = (float) Math.cos(erosionPhase * 1.1F + (float) i * 1.3F + 1.5F) * 0.5F + 0.5F;
                  float combinedErosion = Math.min(erosion1, Math.min(erosion2, erosion3));

                  int alpha;
                  if (combinedErosion < 0.4F) {
                        float erosionStrength = combinedErosion / 0.4F;
                        alpha = (int) (150.0F + Math.pow(erosionStrength, 0.7F) * 80.0F * alphaBase);
                  } else {
                        alpha = (int) (215.0F + 40.0F * alphaBase);
                  }
                  alpha = Mth.clamp(alpha, 150, 255);

                  float distanceToScanLine = Math.abs(charPos - scanLinePos);
                  float scanLineIntensity = 0.0F;

                  if (distanceToScanLine < scanLineWidth) {
                        float normalizedDistance = distanceToScanLine / scanLineWidth;
                        scanLineIntensity = 1.0F - normalizedDistance;
                        scanLineIntensity = scanLineIntensity * scanLineIntensity * (3.0F - 2.0F * scanLineIntensity);
                  }
                  int r = baseR;
                  int g = baseG;
                  int b = baseB;

                  if (scanLineIntensity > 0.01F) {
                        float highlightStrength = scanLineIntensity * 0.65F;

                        r = (int) (baseR + (255 - baseR) * highlightStrength);
                        g = (int) (baseG + (255 - baseG) * highlightStrength);
                        b = (int) (baseB + (255 - baseB) * highlightStrength);

                        alpha = (int) Mth.clamp(alpha + (255 - alpha) * scanLineIntensity * 0.50F, alpha, 255);
                  }

                  int c = alpha << 24 | r << 16 | g << 8 | b;
                  Matrix4f renderMatrix = new Matrix4f(matrix4f);
                  posX = (float) fontInstance.drawInBatch(String.valueOf(currentChar), posX, startY, c,
                              dropShadow,
                              renderMatrix,
                              bufferSource, displayMode, overlay, light);
            }

            return posX;
      }
}
