package com.k1sak1.goetyawaken.api.client.text.mui;

import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Inspired by
 * <a href=
 * "https://github.com/Mega32K/ending_library/?tab=readme-ov-file">ending_library</a>
 * project.
 * Original author: MegaDarkness
 * </p>
 */
public class ModernTextRendererCall {
    public static final Char2ObjectOpenHashMap<IModernTextRendererCall> calls = new Char2ObjectOpenHashMap<>();
    public static final CharSet fastCompletelyReplaceSet = new CharOpenHashSet();

    private final Object textLayoutEngine;
    private final float x;
    private final Int2CharOpenHashMap map;

    public ModernTextRendererCall(Object textLayoutEngine, float x, Int2CharOpenHashMap map) {
        this.textLayoutEngine = textLayoutEngine;
        this.x = x;
        this.map = map;
    }

    public static void registerCall(ChatFormatting cf, IModernTextRendererCall call) {
        calls.put(cf.getChar(), call);
        if (call.completelyReplaceRender()) {
            fastCompletelyReplaceSet.add(cf.getChar());
        }
    }

    public void drawText(FormattedCharSequence text, float y, int color, boolean dropShadow,
            Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode,
            int colorBackground, int packedLight, CallbackInfoReturnable<Float> cir) {
        if (text != FormattedCharSequence.EMPTY && map != null) {
            for (char c : map.values()) {
                IModernTextRendererCall rendererCall = calls.get(c);
                if (rendererCall != null) {
                    rendererCall.drawText(textLayoutEngine, text, x, y, color, dropShadow, matrix,
                            source, displayMode, colorBackground, packedLight, cir, this);
                }
            }
        }
    }

    public void drawText(Object renderer, FormattedCharSequence text, float x, float y, int color,
            boolean dropShadow, Matrix4f matrix, MultiBufferSource source,
            Font.DisplayMode displayMode, int colorBackground, int packedLight,
            CallbackInfoReturnable<Float> cir, ModernTextRendererCall call) {
        if (map != null) {
            for (char c : map.values()) {
                IModernTextRendererCall rendererCall = calls.get(c);
                if (rendererCall != null && rendererCall.completelyReplaceRender()) {
                    rendererCall.drawTextInstead(renderer, textLayoutEngine, text, x, y, color, dropShadow, matrix,
                            source, displayMode, colorBackground, packedLight, call);
                }
            }
        }
    }
}
