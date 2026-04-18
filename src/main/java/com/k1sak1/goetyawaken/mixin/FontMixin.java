package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.client.font.GAErosionRenderer;
import com.k1sak1.goetyawaken.client.typography.GATextMetadata;
import com.k1sak1.goetyawaken.client.typography.effects.GAErosionHandler;
import com.k1sak1.goetyawaken.utils.annotation.ExcludeIfModPresent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
@ExcludeIfModPresent("modernui")
public abstract class FontMixin {

    @Shadow
    public boolean filterFishyGlyphs;

    @Unique
    private boolean goetyawaken$hasErosionEffect(FormattedCharSequence text) {
        final boolean[] hasErosion = { false };

        text.accept((index, style, codePoint) -> {
            TextColor color = style.getColor();
            if (color != null && GATextMetadata.hasMetadata(color)) {
                String effectId = GATextMetadata.get(color).effectId();
                if (GAErosionHandler.EFFECT_ID.equals(effectId)) {
                    hasErosion[0] = true;
                    return false;
                }
            }
            return true;
        });

        return hasErosion[0];
    }

    @Inject(method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At("HEAD"), cancellable = true)
    private void onDrawInBatchHead(FormattedCharSequence p_273025_, float p_273121_, float p_272717_,
            int p_273653_, boolean p_273531_, Matrix4f p_273265_, MultiBufferSource p_273560_,
            Font.DisplayMode p_273342_, int p_273373_, int p_273266_, CallbackInfoReturnable<Integer> cir) {
        if (GAErosionRenderer.IS_RENDERING.get()) {
            return;
        }

        if (goetyawaken$hasErosionEffect(p_273025_)) {
            int result = (int) GAErosionRenderer.INSTANCE.drawInBatchF(
                    (Font) (Object) this, p_273025_, p_273121_, p_272717_, p_273653_,
                    p_273531_, p_273265_, p_273560_, p_273342_, p_273373_, p_273266_);
            cir.setReturnValue(result);
            cir.cancel();
        }
    }
}
