package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.common.items.CatacombsReliquaryItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityFireImmunityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (CatacombsReliquaryItem.isFireImmune(entity)) {
            entity.setRemainingFireTicks(-1);
            if (entity.getRemainingFireTicks() > 0) {
                entity.clearFire();
            }
        }
    }
}