package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.api.client.text.TextColorUtils;
import com.k1sak1.goetyawaken.client.enums.ModChatFormatting;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/**
 * Inspired by
 * <a href=
 * "https://github.com/Mega32K/ending_library/?tab=readme-ov-file">ending_library</a>
 * project.
 * Original author: MegaDarkness
 * </p>
 */
@Mixin(value = ChatFormatting.class, priority = 1000)
public class ChatFormattingMixin {
    @Shadow(remap = false)
    @Final
    @Mutable
    private static ChatFormatting[] $VALUES;

    ChatFormattingMixin(String id, int ordinal, String name, char code, int colorIndex, @Nullable Integer colorValue) {
        throw new AssertionError("Mixin Failed");
    }

    @Inject(at = {
            @At(value = "FIELD", shift = At.Shift.AFTER, target = "Lnet/minecraft/ChatFormatting;$VALUES:[Lnet/minecraft/ChatFormatting;") }, method = {
                    "<clinit>" })
    private static void goetyAwakenFormatting(CallbackInfo ci) {
        for (ChatFormatting existing : $VALUES) {
            if (existing != null && "GA_EROSION".equals(existing.getName())) {
                return;
            }
        }

        int ordinal = $VALUES.length;
        $VALUES = Arrays.copyOf($VALUES, ordinal + 1);
        TextColorUtils.EROSION = (ChatFormatting) (Object) (new ChatFormattingMixin("GA_EROSION", ordinal, "GA_EROSION",
                '=', 19, 0x9E9E9E));
        $VALUES[ordinal] = TextColorUtils.EROSION;
        ModChatFormatting.EROSION = TextColorUtils.EROSION;
    }
}
