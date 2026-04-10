package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.api.client.text.TextColorUtils;
import com.k1sak1.goetyawaken.client.enums.ModChatFormatting;
import com.k1sak1.goetyawaken.client.font.ErosionFontRenderer;
import com.k1sak1.goetyawaken.utils.annotation.NoModDependsMixin;
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
 * Original author: MegaDarkness
 * </p>
 */
@Mixin(Font.class)
@NoModDependsMixin("modernui")
public abstract class FontMixin {

    @Shadow
    public boolean filterFishyGlyphs;

    @Unique
    private static ThreadLocal<Int2CharOpenHashMap> goetyawaken$erosionMap = ThreadLocal.withInitial(() -> null);

    @Unique
    private boolean goetyawaken$hasErosion(Int2CharOpenHashMap map) {
        if (map == null)
            return false;
        for (char c : map.values()) {
            if (c == ModChatFormatting.EROSION.getChar()) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At("HEAD"), cancellable = true)
    private void onDrawInBatchHead(FormattedCharSequence p_273025_, float p_273121_, float p_272717_,
            int p_273653_, boolean p_273531_, Matrix4f p_273265_, MultiBufferSource p_273560_,
            Font.DisplayMode p_273342_, int p_273373_, int p_273266_, CallbackInfoReturnable<Integer> cir) {
        if (goetyawaken$erosionMap.get() != null) {
            return;
        }

        Int2CharOpenHashMap map = TextColorUtils.getColorChars(p_273025_);
        if (goetyawaken$hasErosion(map)) {
            goetyawaken$erosionMap.set(map);

            int result = (int) ErosionFontRenderer.INSTANCE.drawInBatchF(
                    (Font) (Object) this, p_273025_, p_273121_, p_272717_, p_273653_,
                    p_273531_, p_273265_, p_273560_, p_273342_, p_273373_, p_273266_, map);
            cir.setReturnValue(result);
            cir.cancel();
        }
    }

    @Inject(method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At("RETURN"))
    private void onDrawInBatchReturn(FormattedCharSequence p_273025_, float p_273121_, float p_272717_,
            int p_273653_, boolean p_273531_, Matrix4f p_273265_, MultiBufferSource p_273560_,
            Font.DisplayMode p_273342_, int p_273373_, int p_273266_, CallbackInfoReturnable<Integer> cir) {
        if (goetyawaken$erosionMap.get() != null) {
            goetyawaken$erosionMap.set(null);
        }
    }
}
