package com.k1sak1.goetyawaken.client.font;

import com.k1sak1.goetyawaken.client.typography.GATextMetadata;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class GAErosionRenderer {
      public static final GAErosionRenderer INSTANCE = new GAErosionRenderer();
      public static final ThreadLocal<Boolean> IS_RENDERING = ThreadLocal.withInitial(() -> false);

      private static final int TRIG_TABLE_SIZE = 1024;
      private static final float TRIG_SCALE = (float) (TRIG_TABLE_SIZE / (Math.PI * 2.0));
      private static final float[] SIN_TABLE = new float[TRIG_TABLE_SIZE];

      static {
            for (int i = 0; i < TRIG_TABLE_SIZE; i++) {
                  SIN_TABLE[i] = (float) Math.sin((double) i / TRIG_TABLE_SIZE * Math.PI * 2.0);
            }
      }

      private static float fastSin(float radians) {
            int index = (int) (radians * TRIG_SCALE) & (TRIG_TABLE_SIZE - 1);
            return SIN_TABLE[index];
      }

      private static float fastCos(float radians) {
            return fastSin(radians + (float) (Math.PI / 2.0));
      }

      private static float fract(float f) {
            return f - (float) Math.floor((double) f);
      }

      private static int hsvToRgb24(float h, float s, float v) {
            if (s <= 0.0F) {
                  int c = Mth.clamp((int) (v * 255.0F), 0, 255);
                  return c << 16 | c << 8 | c;
            }
            h = fract(h) * 6.0F;
            int sector = (int) h;
            float f = h - (float) sector;
            float p = v * (1.0F - s);
            float q = v * (1.0F - s * f);
            float t = v * (1.0F - s * (1.0F - f));
            float r, g, b;
            switch (sector) {
                  case 0:
                        r = v;
                        g = t;
                        b = p;
                        break;
                  case 1:
                        r = q;
                        g = v;
                        b = p;
                        break;
                  case 2:
                        r = p;
                        g = v;
                        b = t;
                        break;
                  case 3:
                        r = p;
                        g = q;
                        b = v;
                        break;
                  case 4:
                        r = t;
                        g = p;
                        b = v;
                        break;
                  default:
                        r = v;
                        g = p;
                        b = q;
                        break;
            }
            int ir = Mth.clamp((int) (r * 255.0F), 0, 255);
            int ig = Mth.clamp((int) (g * 255.0F), 0, 255);
            int ib = Mth.clamp((int) (b * 255.0F), 0, 255);
            return ir << 16 | ig << 8 | ib;
      }

      public float drawInBatchF(Font fontInstance, FormattedCharSequence text, float x, float y, int color,
                  boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode,
                  int overlay, int light) {
            if (this.hasErosionEffect(text)) {
                  String[] erosionParts = FontTextBuilder.splitErosionText(text);
                  IS_RENDERING.set(true);
                  try {
                        float result = this.renderErosion(fontInstance, erosionParts[0], x, y, color, dropShadow,
                                    matrix, bufferSource, displayMode, overlay, light);
                        String normalText = erosionParts[1];
                        if (!normalText.isBlank()) {
                              fontInstance.drawInBatch(normalText, x, y, color, dropShadow, matrix, bufferSource,
                                          displayMode, overlay, light);
                        }
                        return result;
                  } finally {
                        IS_RENDERING.set(false);
                  }
            } else {
                  return (float) fontInstance.drawInBatch(text, x, y, color, dropShadow, matrix, bufferSource,
                              displayMode, overlay, light);
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
            float timeSeconds = (float) milliTime * 0.001F;
            final int textLen = text.length();
            if (textLen == 0)
                  return startX;

            final float[] measPositions = new float[textLen];
            for (int i = 0; i < textLen; ++i) {
                  measPositions[i] = startX + fontInstance.width(text.substring(0, i));
            }

            final float totalWidth = startX + fontInstance.width(text);

            float brightnessBreath = fastSin(timeSeconds * 0.7854F) * 0.5F + 0.5F;

            float satBase = 0.65F + fastSin(timeSeconds * 0.25F) * 0.06F;

            final float scanWidth = 14.0F;

            final float textPixelWidth = totalWidth - startX + 20.0F;

            float scan1Progress = fract(timeSeconds * 0.2F);
            float scan1Pos = startX - 10.0F + scan1Progress * textPixelWidth;

            float scan2Progress = fract(timeSeconds * 0.2857F + 0.5F);
            float scan2Pos = startX - 10.0F + scan2Progress * textPixelWidth;

            float scan3Progress = fract(timeSeconds * 0.1428F + 0.25F);
            float scan3Pos = startX - 10.0F + scan3Progress * textPixelWidth;

            for (int i = 0; i < textLen; ++i) {
                  char currentChar = text.charAt(i);

                  float charPos = measPositions[i];
                  float charNorm = (float) i / (float) Math.max(1, textLen - 1);

                  float hueWave1 = fastSin(timeSeconds * 0.12F + i * 0.10F) * 0.045F;
                  float hueWave2 = fastSin(timeSeconds * 0.08F + i * 0.14F + 1.5F) * 0.03F;
                  float hue = 0.265F + hueWave1 + hueWave2;

                  float valueBase = 0.42F + brightnessBreath * 0.16F;
                  float valueCharMod = fastSin(timeSeconds * 0.28F + i * 0.18F) * 0.05F;
                  float value = Mth.clamp(valueBase + valueCharMod, 0.35F, 0.68F);

                  float satCharMod = fastSin(timeSeconds * 0.38F + i * 0.22F + 2.0F) * 0.04F;
                  float sat = Mth.clamp(satBase + satCharMod, 0.50F, 0.85F);

                  int rgb = hsvToRgb24(hue, sat, value);
                  int baseR = (rgb >> 16) & 0xFF;
                  int baseG = (rgb >> 8) & 0xFF;
                  int baseB = rgb & 0xFF;

                  float alphaBase = fastSin(timeSeconds * 0.35F + i * 0.28F) * 0.10F + 0.90F;

                  float erosionPhase = timeSeconds * 0.30F + charNorm * 7.0F;
                  float eros1 = fastSin(erosionPhase * 1.1F + i * 1.0F) * 0.5F + 0.5F;
                  float eros2 = fastSin(erosionPhase * 0.95F + i * 1.2F + 1.0F) * 0.5F + 0.5F;
                  float eros3 = fastCos(erosionPhase * 0.85F + i * 1.1F + 1.5F) * 0.5F + 0.5F;
                  float combinedErosion = Math.min(eros1, Math.min(eros2, eros3));

                  int alpha;
                  if (combinedErosion < 0.4F) {
                        float erodeStr = combinedErosion / 0.4F;
                        alpha = (int) (135.0F + Math.pow(erodeStr, 0.65F) * 90.0F * alphaBase);
                  } else {
                        alpha = (int) (195.0F + 60.0F * alphaBase);
                  }
                  alpha = Mth.clamp(alpha, 135, 255);

                  float scanIntensity = 0.0F;

                  float dist1 = Math.abs(charPos - scan1Pos);
                  if (dist1 < scanWidth) {
                        float nd = dist1 / scanWidth;
                        scanIntensity += (1.0F - nd) * (1.0F - nd) * 0.55F;
                  }
                  float dist2 = Math.abs(charPos - scan2Pos);
                  if (dist2 < scanWidth * 0.85F) {
                        float nd = dist2 / (scanWidth * 0.85F);
                        scanIntensity += (1.0F - nd) * (1.0F - nd) * 0.38F;
                  }
                  float dist3 = Math.abs(charPos - scan3Pos);
                  if (dist3 < scanWidth * 1.15F) {
                        float nd = dist3 / (scanWidth * 1.15F);
                        scanIntensity += (1.0F - nd) * (1.0F - nd) * 0.32F;
                  }
                  scanIntensity = Mth.clamp(scanIntensity, 0.0F, 1.0F);

                  int r = baseR;
                  int g = baseG;
                  int b = baseB;

                  if (scanIntensity > 0.008F) {
                        float hl = scanIntensity * 0.72F;
                        r = (int) (baseR + (255 - baseR) * hl);
                        g = (int) (baseG + (255 - baseG) * hl);
                        b = (int) (baseB + (255 - baseB) * hl);
                        alpha = (int) Mth.clamp(alpha + (255 - alpha) * scanIntensity * 0.55F, alpha, 255);
                  }

                  int c = alpha << 24 | r << 16 | g << 8 | b;

                  fontInstance.drawInBatch(String.valueOf(currentChar), charPos, startY, c,
                              dropShadow, matrix4f, bufferSource, displayMode, overlay, light);
            }

            return totalWidth;
      }
}
