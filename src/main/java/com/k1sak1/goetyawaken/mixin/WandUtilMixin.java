package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.ISummonSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellCaster;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WandUtil.class, remap = false)
public class WandUtilMixin {

    @Inject(method = "findWand", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onFindWand(LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (livingEntity instanceof SorcererSpellCaster caster && caster.isCastingSpell2()) {
            ItemStack useItem = livingEntity.getUseItem();
            if (!useItem.isEmpty() && useItem.getItem() instanceof IWand) {
                cir.setReturnValue(useItem);
            }
        }
    }

    @ModifyReturnValue(method = "getStats", at = @At("RETURN"))
    private static SpellStat goetyawaken$applySummonPotency(SpellStat original, LivingEntity livingEntity,
            ISpell spell) {
        if (spell instanceof ISummonSpell) {
            int potency = (int) Math.round(ModAttributeRegistry.getSummonPotency(livingEntity));
            if (potency != 0) {
                return original.increasePotency(potency);
            }
        }
        return original;
    }
}
