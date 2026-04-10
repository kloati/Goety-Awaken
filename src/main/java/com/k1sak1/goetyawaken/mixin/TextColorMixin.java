package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.api.client.text.TextColorInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Inspired by
 * <a href=
 * "https://github.com/Mega32K/ending_library/?tab=readme-ov-file">ending_library</a>
 * project.
 * Original author: MegaDarkness
 * </p>
 */
@Mixin(TextColor.class)
public class TextColorMixin implements TextColorInterface {
    @Unique
    private char goetyawaken$chatCode = ' ';

    @Override
    public char goetyawaken$getCode() {
        return goetyawaken$chatCode;
    }

    @Override
    public void goetyawaken$setCode(char code) {
        this.goetyawaken$chatCode = code;
    }

    @Inject(method = "<init>(ILjava/lang/String;)V", at = @At("RETURN"))
    private void init(int p_131263_, String p_131264_, CallbackInfo ci) {
        ChatFormatting chatFormatting;
        if ((chatFormatting = ChatFormatting.getByName(p_131264_)) != null)
            this.goetyawaken$setCode(chatFormatting.getChar());
    }
}
