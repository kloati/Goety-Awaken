package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.api.client.text.GATextStyleUtils;
import com.k1sak1.goetyawaken.api.client.typography.GAStyleExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public abstract class StyleMixin implements GAStyleExtension {
    @Shadow
    @Final
    @Nullable
    TextColor color;
    @Shadow
    @Final
    @Nullable
    Boolean bold;
    @Shadow
    @Final
    @Nullable
    Boolean italic;
    @Shadow
    @Final
    @Nullable
    Boolean strikethrough;
    @Shadow
    @Final
    @Nullable
    Boolean underlined;
    @Shadow
    @Final
    @Nullable
    Boolean obfuscated;

    @Unique
    @Nullable
    private Boolean goetyawaken$centered = null;

    @Unique
    public Boolean goetyawaken$getCentered() {
        return this.goetyawaken$centered;
    }

    @Unique
    public void goetyawaken$setCentered(Boolean centered) {
        this.goetyawaken$centered = centered;
    }

    @Inject(method = "applyFormat", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void propagateCenteredOnApplyFormat(ChatFormatting formatting, CallbackInfoReturnable<Style> cir) {
        Boolean centered = this.goetyawaken$centered;
        if (GATextStyleUtils.MIDDLE != null && formatting == GATextStyleUtils.MIDDLE) {
            centered = Boolean.TRUE;
        }

        Style result = cir.getReturnValue();
        ((GAStyleExtension) result).goetyawaken$setCentered(centered == null ? Boolean.FALSE : centered);
    }

    @Inject(method = "applyTo", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void propagateCenteredOnApplyTo(Style other, CallbackInfoReturnable<Style> cir) {
        Style result = cir.getReturnValue();
        if (result != null && result != Style.EMPTY) {
            Boolean thisCentered = this.goetyawaken$centered;
            Boolean otherCentered = ((GAStyleExtension) other).goetyawaken$getCentered();

            Boolean mergedCentered = thisCentered != null ? thisCentered : otherCentered;
            ((GAStyleExtension) result)
                    .goetyawaken$setCentered(mergedCentered != null ? mergedCentered : Boolean.FALSE);
        }
    }

    @Inject(method = "applyFormats", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void propagateCenteredOnApplyFormats(ChatFormatting[] formatings, CallbackInfoReturnable<Style> cir) {
        Boolean centered = this.goetyawaken$centered;

        if (GATextStyleUtils.MIDDLE != null) {
            for (ChatFormatting f : formatings) {
                if (f == GATextStyleUtils.MIDDLE) {
                    centered = Boolean.TRUE;
                    break;
                }
            }
        }

        Style result = cir.getReturnValue();
        ((GAStyleExtension) result).goetyawaken$setCentered(centered == null ? Boolean.FALSE : centered);
    }

    @Inject(method = {
            "withColor(Lnet/minecraft/network/chat/TextColor;)Lnet/minecraft/network/chat/Style;",
            "withBold", "withItalic", "withUnderlined", "withStrikethrough",
            "withObfuscated", "withClickEvent", "withHoverEvent", "withInsertion", "withFont"
    }, at = @At("RETURN"))
    private void propagateCenteredOnWithMethods(CallbackInfoReturnable<Style> cir) {
        Style result = cir.getReturnValue();
        if (result != null) {
            ((GAStyleExtension) result).goetyawaken$setCentered(
                    this.goetyawaken$centered != null ? this.goetyawaken$centered : Boolean.FALSE);
        }
    }
}
