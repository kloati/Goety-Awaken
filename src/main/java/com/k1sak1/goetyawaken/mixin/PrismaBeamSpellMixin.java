package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.abyss.PrismaBeamSpell;
import com.Polarice3.Goety.utils.MathHelper;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.init.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PrismaBeamSpell.class, remap = false)
public class PrismaBeamSpellMixin {

    @Inject(method = "useSpell", at = @At("HEAD"), remap = false)
    private void onUseSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime,
            SpellStat spellStat, CallbackInfo ci) {
        if (!staff.is(ModItems.POTATO_STAFF.get())) {
            return;
        }
        PrismaBeamSpell spell = (PrismaBeamSpell) (Object) this;
        LivingEntity target = spell.getTarget(caster);
        if (target != null) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0, false, false), caster);
        }
    }

    @Inject(method = "SpellResult", at = @At("TAIL"), remap = false)
    private void onSpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat,
            CallbackInfo ci) {
        if (!staff.is(ModItems.POTATO_STAFF.get())) {
            return;
        }
        PrismaBeamSpell spell = (PrismaBeamSpell) (Object) this;
        LivingEntity target = spell.getTarget(caster);
        if (target != null) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 50, 0, false, false), caster);
            target.addEffect(new MobEffectInstance(ModEffects.POTENT_VENOM.get(),
                    MathHelper.secondsToTicks(5), 0, false, false), caster);
        }
    }
}
