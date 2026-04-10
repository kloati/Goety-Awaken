package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.api.IAncientGlint;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityAncientHuntMixin {

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void onGetName(CallbackInfoReturnable<Component> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof LivingEntity living && living instanceof IAncientGlint glint) {
            int huntNumber = glint.getAncientHuntNumber();
            if (huntNumber > 0) {
                String translationKey = "goetyawaken.ancient_hunt.name." + huntNumber;
                cir.setReturnValue(Component.translatable(translationKey));
            }

        }
    }
}
