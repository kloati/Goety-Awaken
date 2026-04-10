package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.common.items.GraveBaneSwordItem;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getDamageBonus", at = @At(value = "HEAD"), cancellable = true)
    private static void onGetDamageBonus(ItemStack stack, MobType creatureType, CallbackInfoReturnable<Float> cir) {
        if (stack.getItem() instanceof GraveBaneSwordItem) {
            float damageBonus = 0.0F;
            for (Enchantment enchantment : stack.getAllEnchantments().keySet()) {
                int level = stack.getEnchantmentLevel(enchantment);
                if (enchantment == Enchantments.SMITE) {
                    level = level * 2;
                }
                damageBonus += enchantment.getDamageBonus(level, creatureType, stack);
            }

            cir.setReturnValue(damageBonus);
        }
    }
}