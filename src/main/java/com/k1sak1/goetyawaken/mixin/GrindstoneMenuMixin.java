package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.common.events.GlowingEmberAnvilHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.GrindstoneMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin {

    @Inject(method = "removeNonCurses", at = @At("RETURN"))
    private void onRemoveNonCurses(ItemStack itemStack, int damage, int count, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (!result.isEmpty() && GlowingEmberAnvilHandler.getEnhancementCount(result) > 0) {
            result.removeTagKey("GlowingEmberEnhancementCount");
        }
    }
}
