package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.api.client.text.StyleInterface;
import com.k1sak1.goetyawaken.api.client.text.TextColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public class StyleMixin implements StyleInterface {
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
    Boolean goetyawaken$isCentered = null;

    @Override
    public boolean goetyawaken$isCentered() {
        return this.goetyawaken$isCentered == Boolean.TRUE;
    }

    @Override
    public void goetyawaken$withCentered(boolean is) {
        this.goetyawaken$isCentered = is ? Boolean.TRUE : Boolean.FALSE;
    }

    @Inject(method = "applyFormat", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyFormatCentered(ChatFormatting p_131158_, CallbackInfoReturnable<Style> cir) {
        Boolean centered = this.goetyawaken$isCentered;
        if (TextColorUtils.MIDDLE != null && p_131158_ == TextColorUtils.MIDDLE)
            centered = Boolean.TRUE;
        Style style = cir.getReturnValue();
        ((StyleInterface) style).goetyawaken$withCentered(centered == null ? Boolean.FALSE : centered);
        cir.setReturnValue(style);
    }

    @Inject(method = "applyLegacyFormat", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/network/chat/Style;obfuscated:Ljava/lang/Boolean;", shift = At.Shift.AFTER))
    private void applyLegacyFormat(ChatFormatting p_131158_, CallbackInfoReturnable<Style> cir) {
        if (TextColorUtils.MIDDLE != null && p_131158_ == TextColorUtils.MIDDLE) {
            Style s = cir.getReturnValue();
            s.withBold(this.bold);
            s.withItalic(this.italic);
            s.withStrikethrough(this.strikethrough);
            s.withUnderlined(this.underlined);
            s.withObfuscated(this.obfuscated);
            ((StyleInterface) s).goetyawaken$withCentered(true);
            cir.setReturnValue(s);
        } else {
            Style s = cir.getReturnValue();
            ((StyleInterface) s).goetyawaken$withCentered(
                    this.goetyawaken$isCentered == null ? Boolean.FALSE : this.goetyawaken$isCentered);
            cir.setReturnValue(s);
        }
    }

    @Inject(method = "applyTo", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyTo(Style other, CallbackInfoReturnable<Style> cir) {
        Style returnValue = cir.getReturnValue();
        if (returnValue != null && returnValue != Style.EMPTY) {
            StyleInterface otherItf = (StyleInterface) other;
            ((StyleInterface) returnValue)
                    .goetyawaken$withCentered(this.goetyawaken$isCentered != null ? this.goetyawaken$isCentered
                            : otherItf.goetyawaken$isCentered());
            cir.setReturnValue(returnValue);
        }
    }

    @Inject(method = "applyFormats", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void applyFormats(ChatFormatting[] p_131153_, CallbackInfoReturnable<Style> cir) {
        Boolean centered = this.goetyawaken$isCentered;
        if (TextColorUtils.MIDDLE != null) {
            for (ChatFormatting formatting : p_131153_) {
                if (formatting == TextColorUtils.MIDDLE) {
                    centered = Boolean.TRUE;
                    break;
                }
            }
        }
        Style style = cir.getReturnValue();
        ((StyleInterface) style).goetyawaken$withCentered(centered == null ? Boolean.FALSE : centered);
        cir.setReturnValue(style);
    }

    @Inject(method = { "withColor(Lnet/minecraft/network/chat/TextColor;)Lnet/minecraft/network/chat/Style;",
            "withBold", "withItalic", "withUnderlined", "withStrikethrough", "withObfuscated", "withClickEvent",
            "withHoverEvent", "withInsertion", "withFont" }, at = @At("RETURN"))
    private void commonCenteredCheck(CallbackInfoReturnable<Style> cir) {
        ((StyleInterface) cir.getReturnValue()).goetyawaken$withCentered(this.goetyawaken$isCentered());
    }
}
