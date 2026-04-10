package com.k1sak1.goetyawaken.api.client.text.mui;

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
public interface IModernTextRendererCall {

    default boolean completelyReplaceRender() {
        return false;
    }

    default void drawText(Object mEngine, FormattedCharSequence text, float x, float y, int color,
            boolean dropShadow, Matrix4f matrix, MultiBufferSource source,
            Font.DisplayMode displayMode, int colorBackground, int packedLight,
            CallbackInfoReturnable<Float> cir, ModernTextRendererCall call) {

    }

    default float drawTextInstead(Object modernTextRenderer, Object mEngine, FormattedCharSequence text,
            float x, float y, int color, boolean dropShadow, Matrix4f matrix,
            MultiBufferSource source, Font.DisplayMode displayMode, int colorBackground,
            int packedLight, ModernTextRendererCall call) {
        return x;
    }
}
