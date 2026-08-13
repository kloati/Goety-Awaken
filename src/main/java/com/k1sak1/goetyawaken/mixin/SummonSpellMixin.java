package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SummonSpell.class, remap = false)
public abstract class SummonSpellMixin {

    @Inject(method = "SummonDown", at = @At("RETURN"))
    private void goetyawaken$reduceSummonDown(LivingEntity entityLiving, CallbackInfo ci) {
        MobEffectInstance effect = entityLiving.getEffect(GoetyEffects.SUMMON_DOWN.get());
        if (effect != null) {
            double multiplier = ModAttributeRegistry.getSummonCooldownMultiplier(entityLiving);
            if (multiplier != 1.0D) {
                int newDuration = (int) Math.max(1.0D, Math.round(effect.getDuration() * multiplier));
                if (newDuration != effect.getDuration()) {
                    entityLiving.removeEffectNoUpdate(GoetyEffects.SUMMON_DOWN.get());
                    entityLiving.addEffect(new MobEffectInstance(effect.getEffect(), newDuration, effect.getAmplifier(),
                            effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                }
            }
        }
    }

    @WrapOperation(method = "conditionsMet", at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/common/magic/SummonSpell;summonLimit()I"))
    private int goetyawaken$expandSpellSummonLimit(SummonSpell instance, Operation<Integer> original,
            @Local(argsOnly = true) LivingEntity caster) {
        int limit = original.call(instance);
        return limit + ModAttributeRegistry.getServantCapacityLevel(caster);
    }
}
