package com.k1sak1.goetyawaken.client.font;

import com.k1sak1.goetyawaken.client.enums.ModChatFormatting;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class ErosionFontRenderer {
    public static final ErosionFontRenderer INSTANCE = new ErosionFontRenderer();

    public float drawInBatchF(Font fontInstance, FormattedCharSequence text, float x, float y, int color,
            boolean dropShadow,
            Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode,
            int overlay, int light, Int2CharOpenHashMap foundCodes) {
        if (isErosion(foundCodes)) {
            String[] erosionParts = FontTextBuilder.formattedCharSequenceToStringErosion(text);
            float erosionWidth = renderErosion(fontInstance, erosionParts[0], x, y, color, dropShadow, matrix,
                    bufferSource, displayMode, overlay, light);
            return erosionWidth;
        }
        return fontInstance.drawInBatch(text, x, y, color, dropShadow, matrix, bufferSource, displayMode, overlay,
                light);
    }

    private boolean isErosion(Int2CharOpenHashMap map) {
        for (char c : map.values()) {
            if (c == ModChatFormatting.EROSION.getChar())
                return true;
        }
        return false;
    }

    private float renderErosion(Font fontInstance, String text, float startX, float startY, int iColor,
            boolean dropShadow, Matrix4f matrix4f, MultiBufferSource bufferSource,
            Font.DisplayMode displayMode, int overlay, int light) {
        long milliTime = System.currentTimeMillis();
        float posX = startX;
        long scanLineCycle = 3500;
        boolean showScanLine = (milliTime % (scanLineCycle * 2)) < scanLineCycle;

        long[] charSeeds = new long[text.length()];
        for (int i = 0; i < text.length(); i++) {
            charSeeds[i] = (long) (Math.sin(milliTime * 0.001 + i * 132.154) * 100000) % 100000;
        }

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            long charSeed = charSeeds[i];
            float ultraFastPhase = (float) milliTime * 0.03F;
            float fastPhase1 = (float) milliTime * 0.012F + i * 0.5F;
            float fastPhase2 = (float) milliTime * 0.015F - i * 0.6F;
            float rWave1 = (float) Math.sin(ultraFastPhase * 1.7F + i * 0.3F) * 0.5F + 0.5F;
            float rWave2 = (float) Math.cos(fastPhase1 * 2.1F) * 0.5F + 0.5F;
            float rWave3 = (float) Math.sin(fastPhase2 * 1.9F + 1.0F) * 0.5F + 0.5F;
            float rCombined = (rWave1 + rWave2 + rWave3) / 3.0F;

            float gWave1 = (float) Math.sin(ultraFastPhase * 1.5F + i * 0.4F + 2.094F) * 0.5F + 0.5F;
            float gWave2 = (float) Math.cos(fastPhase1 * 2.3F + 0.5F) * 0.5F + 0.5F;
            float gWave3 = (float) Math.sin(fastPhase2 * 2.0F + 1.5F) * 0.5F + 0.5F;
            float gCombined = (gWave1 + gWave2 + gWave3) / 3.0F;

            float bWave1 = (float) Math.sin(ultraFastPhase * 1.6F + i * 0.5F + 4.188F) * 0.5F + 0.5F;
            float bWave2 = (float) Math.cos(fastPhase1 * 2.2F + 1.0F) * 0.5F + 0.5F;
            float bWave3 = (float) Math.sin(fastPhase2 * 1.8F + 2.0F) * 0.5F + 0.5F;
            float bCombined = (bWave1 + bWave2 + bWave3) / 3.0F;
            float quantumPhase = (float) milliTime * 0.008F;
            float quantumR = (float) (Math.sin(quantumPhase * 3.7F) > 0.3 ? 1.0 : 0.4);
            float quantumG = (float) (Math.sin(quantumPhase * 4.1F + 1.0F) > 0.2 ? 1.0 : 0.5);
            float quantumB = (float) (Math.sin(quantumPhase * 3.9F + 2.0F) > 0.1 ? 1.0 : 0.6);
            float rainbowPhase = (float) milliTime * 0.004F;
            float rainbowR = (float) (Math.sin(rainbowPhase) * 0.5F + 0.5F);
            float rainbowG = (float) (Math.sin(rainbowPhase + 2.094F) * 0.5F + 0.5F);
            float rainbowB = (float) (Math.sin(rainbowPhase + 4.188F) * 0.5F + 0.5F);
            int baseR = (int) (80 + rCombined * 100 * quantumR + rainbowR * 100);
            int baseG = (int) (80 + gCombined * 100 * quantumG + rainbowG * 100);
            int baseB = (int) (80 + bCombined * 100 * quantumB + rainbowB * 100);
            float brightnessPhase1 = (float) milliTime * 0.04F;
            float brightnessPhase2 = (float) milliTime * 0.025F;
            float brightnessPhase3 = (float) milliTime * 0.018F;
            float brightness1 = (float) Math.sin(brightnessPhase1 * 2.3F + i * 0.7F) * 0.3F + 0.45F;
            float brightness2 = (float) Math.cos(brightnessPhase2 * 1.9F - i * 0.5F) * 0.25F + 0.5F;
            float brightness3 = (float) Math.sin(brightnessPhase3 * 2.1F + i * 0.6F + 1.0F) * 0.25F + 0.5F;
            float combinedBrightness = (brightness1 + brightness2 + brightness3) / 3.0F;
            float pulsePhase = (float) milliTime * 0.012F;
            float pulse1 = (float) Math.pow(Math.abs(Math.sin(pulsePhase * 2.5F)), 2.5F);
            float pulse2 = (float) Math.pow(Math.abs(Math.cos(pulsePhase * 2.0F + i * 0.3F)), 2.0F);
            float pulseCombined = Math.max(pulse1, pulse2);
            float finalBrightness = combinedBrightness * 0.55F + pulseCombined * 0.35F;
            int finalR = Mth.clamp((int) (baseR * finalBrightness), 40, 180);
            int finalG = Mth.clamp((int) (baseG * finalBrightness), 40, 180);
            int finalB = Mth.clamp((int) (baseB * finalBrightness), 40, 180);
            float alphaPhase = (float) milliTime * 0.025F + i * 0.5F;
            float alphaWave1 = (float) Math.sin(alphaPhase * 4.0F) * 0.3F + 0.7F;
            float alphaWave2 = (float) Math.cos(alphaPhase * 3.5F + 0.5F) * 0.3F + 0.7F;
            float alphaBase = (alphaWave1 + alphaWave2) * 0.5F;
            float pixelErosion1 = (float) (Math.sin(alphaPhase * 6.0F + i * 2.0F) * 0.5F + 0.5F);
            float pixelErosion2 = (float) (Math.sin(alphaPhase * 5.5F + i * 2.3F + 1.0F) * 0.5F + 0.5F);
            float pixelErosion3 = (float) (Math.cos(alphaPhase * 5.0F + i * 2.1F + 1.5F) * 0.5F + 0.5F);
            float combinedErosion = Math.min(pixelErosion1, Math.min(pixelErosion2, pixelErosion3));

            int alpha = 255;
            if (combinedErosion < 0.4F) {
                float erosionStrength = combinedErosion / 0.4F;
                alpha = (int) (120 + Math.pow(erosionStrength, 0.4F) * 135 * alphaBase);
            } else {
                alpha = (int) (200 + 55 * alphaBase);
            }
            alpha = Mth.clamp(alpha, 120, 255);

            float slowXOffset = Mth.cos((float) milliTime * 0.00025F) * 0.25F;
            float slowYOffset = Mth.sin((float) milliTime * 0.0002F) * 0.25F;
            float randomY1 = (float) (Math.sin(charSeed * 0.000025F + milliTime * 0.000006F) * 0.3F);
            float randomY2 = (float) (Math.cos(charSeed * 0.000035F + milliTime * 0.000005F) * 0.25F);
            float randomY3 = (float) (Math.sin(charSeed * 0.000045F + milliTime * 0.000008F + 1.0F) * 0.15F);
            float randomX1 = (float) (Math.sin(charSeed * 0.000028F + milliTime * 0.000007F + i * 0.03F)
                    * 0.22F);
            float randomX2 = (float) (Math.cos(charSeed * 0.000038F + milliTime * 0.0000055F) * 0.18F);
            float randomX3 = (float) (Math.sin(charSeed * 0.000048F + milliTime * 0.000009F + 0.5F) * 0.1F);
            float waveY = Mth.sin((i * 0.35F + (float) milliTime * 0.0004F)) * 0.25F;
            float waveX = Mth.cos((i * 0.2F + (float) milliTime * 0.00035F)) * 0.2F;
            float jitterPhase = (float) milliTime * 0.018F + i;
            float jitterX = (float) (Math.sin(jitterPhase * 2.2F) * 0.05F);
            float jitterY = (float) (Math.cos(jitterPhase * 1.9F) * 0.05F);
            float totalXOffset = slowXOffset + randomX1 + randomX2 + randomX3 + waveX + jitterX;
            float totalYOffset = slowYOffset + randomY1 + randomY2 + randomY3 + waveY + jitterY;

            Matrix4f renderMatrix = new Matrix4f(matrix4f);
            renderMatrix.translate(totalXOffset, totalYOffset, 0);
            int r = finalR;
            int g = finalG;
            int b = finalB;

            if (showScanLine) {
                float scanProgress = (float) (milliTime % scanLineCycle) / scanLineCycle;
                float smoothedProgress = scanProgress * scanProgress * (3 - 2 * scanProgress);
                float scanLinePos = smoothedProgress * (startX + 150);
                float charPos = startX + i * 6.0F;
                if (Math.abs(charPos - scanLinePos) < 20.0F) {
                    float intensity = 1.0F - Math.abs(charPos - scanLinePos) / 20.0F;
                    r = (int) (r + (255 - r) * intensity);
                    g = (int) (g + (255 - g) * intensity);
                    b = (int) (b + (255 - b) * intensity);
                    alpha = 255;
                }
            }

            int c = (alpha << 24) | (r << 16) | (g << 8) | b;
            posX = fontInstance.drawInBatch(String.valueOf(currentChar), posX, startY, c, dropShadow,
                    renderMatrix, bufferSource, displayMode, overlay, light);
        }

        return posX;
    }
}
