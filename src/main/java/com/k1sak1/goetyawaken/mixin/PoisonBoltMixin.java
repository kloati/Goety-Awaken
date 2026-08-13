package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.entities.projectiles.PoisonBolt;
import com.Polarice3.Goety.utils.MathHelper;
import com.k1sak1.goetyawaken.api.PoisonBoltAccessor;
import com.k1sak1.goetyawaken.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PoisonBolt.class, remap = false)
public class PoisonBoltMixin implements PoisonBoltAccessor {

    @Unique
    private boolean goetyawaken$poisonPotatoMode;

    @Unique
    public void goetyawaken$setPoisonPotatoMode(boolean mode) {
        this.goetyawaken$poisonPotatoMode = mode;
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", shift = At.Shift.AFTER, remap = true), remap = true)
    private void onHitEntityAddEffect(EntityHitResult pResult, CallbackInfo ci) {
        if (!this.goetyawaken$poisonPotatoMode) {
            return;
        }
        Entity entity = pResult.getEntity();
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(
                    ModEffects.POTENT_VENOM.get(),
                    MathHelper.secondsToTicks(5),
                    0,
                    false,
                    false));
        }
    }
}
