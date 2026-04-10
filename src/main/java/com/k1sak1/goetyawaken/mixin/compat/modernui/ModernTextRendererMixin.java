package com.k1sak1.goetyawaken.mixin.compat.modernui;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.k1sak1.goetyawaken.api.client.text.TextColorUtils;
import com.k1sak1.goetyawaken.api.client.text.mui.IModernTextRendererCall;
import com.k1sak1.goetyawaken.api.client.text.mui.ModernTextRendererCall;
import com.k1sak1.goetyawaken.utils.annotation.ModDependsMixin;
import icyllis.modernui.mc.text.ModernTextRenderer;
import icyllis.modernui.mc.text.TextLayoutEngine;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Inspired by
 * <a href=
 * "https://github.com/Mega32K/ending_library/?tab=readme-ov-file">ending_library</a>
 * project.
 * Original author:
 * </p>
 */
@Mixin(ModernTextRenderer.class)
@ModDependsMixin("modernui")
public abstract class ModernTextRendererMixin {

    @Shadow
    private TextLayoutEngine mEngine;

    @Unique
    private static Int2CharOpenHashMap goetyawaken$getErosionCodes(FormattedCharSequence fcs) {
        Int2CharOpenHashMap map = TextColorUtils.getColorChars(fcs);
        Int2CharOpenHashMap result = new Int2CharOpenHashMap();
        if (map != null) {
            for (var entry : map.int2CharEntrySet()) {
                char c = entry.getCharValue();
                if (ModernTextRendererCall.fastCompletelyReplaceSet.contains(c)) {
                    result.put(entry.getIntKey(), c);
                }
            }
        }
        return result;
    }

    @Inject(method = "drawText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F", at = @At("HEAD"), cancellable = true, remap = false)
    private void onDrawTextHead(FormattedCharSequence text, float x, float y, int color, boolean dropShadow,
            Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode,
            int colorBackground, int packedLight,
            CallbackInfoReturnable<Float> cir,
            @Share("erosionMap") LocalRef<Int2CharOpenHashMap> erosionMapRef) {
        if (text != FormattedCharSequence.EMPTY) {
            Int2CharOpenHashMap map = goetyawaken$getErosionCodes(text);
            if (map != null && !map.isEmpty()) {
                erosionMapRef.set(map);
                IModernTextRendererCall replaceCall = null;
                for (char c : map.values()) {
                    IModernTextRendererCall call = ModernTextRendererCall.calls.get(c);
                    if (call != null && call.completelyReplaceRender()) {
                        replaceCall = call;
                        break;
                    }
                }

                if (replaceCall != null) {
                    boolean isCentered = TextColorUtils.getCenteredTooltipWidth() > 0
                            && TextColorUtils.isCentered(text);
                    if (isCentered) {
                        matrix.translate(
                                (TextColorUtils.getCenteredTooltipWidth() - TextColorUtils.font().width(text)) * 0.5F,
                                0F, 0F);
                    }
                    ModernTextRenderer renderer = (ModernTextRenderer) (Object) this;
                    ModernTextRendererCall call = new ModernTextRendererCall(this.mEngine, x, map);
                    x = replaceCall.drawTextInstead(renderer, this.mEngine, text, x, y, color, dropShadow, matrix,
                            source,
                            displayMode, colorBackground, packedLight, call);
                    if (isCentered) {
                        matrix.translate(
                                (TextColorUtils.getCenteredTooltipWidth() - TextColorUtils.font().width(text)) * -0.5F,
                                0, 0);
                    }
                    cir.setReturnValue(x);
                }
            }
        }
    }

    @Inject(method = "drawText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F", at = @At(value = "RETURN", ordinal = 1), remap = false)
    private void onDrawTextReturn(FormattedCharSequence text, float x, float y, int color, boolean dropShadow,
            Matrix4f matrix, MultiBufferSource source, Font.DisplayMode displayMode,
            int colorBackground, int packedLight,
            CallbackInfoReturnable<Float> cir,
            @Share("erosionMap") LocalRef<Int2CharOpenHashMap> erosionMapRef) {
        Int2CharOpenHashMap map = erosionMapRef.get();
        if (map != null && !map.isEmpty()) {
            ModernTextRendererCall call = new ModernTextRendererCall(this.mEngine, x, map);
            call.drawText(text, y, color, dropShadow, matrix, source, displayMode, colorBackground, packedLight, cir);
            erosionMapRef.set(null);
        }
    }
}
