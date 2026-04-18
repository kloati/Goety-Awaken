package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.client.typography.GATextPipeline;
import com.k1sak1.goetyawaken.client.typography.effects.GAErosionHandler;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextColor.class)
public class TextColorMixin {
    @Inject(method = "<init>(ILjava/lang/String;)V", at = @At("RETURN"))
    private void registerMetadata(int p_131263_, String p_131264_, CallbackInfo ci) {
        if ("ga_erosion".equals(p_131264_) || "GA_EROSION".equals(p_131264_)) {
            try {
                TextColor self = (TextColor) (Object) this;
                String className = self.getClass().getName();
                if (className.contains("prism") || className.contains("DynamicColor")) {
                    return;
                }
                GATextPipeline.GATextEffectData metadata = GATextPipeline.GATextEffectData
                        .of(GAErosionHandler.EFFECT_ID);
                com.k1sak1.goetyawaken.client.typography.GATextMetadata.register(self, metadata);
            } catch (Exception e) {

            }
        }
    }
}
