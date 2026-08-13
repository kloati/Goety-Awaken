package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.common.items.CatacombsReliquaryItem;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobSunBurnTickMixin {

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true, remap = true)
    private void checkCatacombsImmunity(CallbackInfoReturnable<Boolean> cir) {
        Mob self = (Mob) (Object) this;
        if (CatacombsReliquaryItem.isFireImmune(self)) {
            cir.setReturnValue(false);
        }
    }
}
