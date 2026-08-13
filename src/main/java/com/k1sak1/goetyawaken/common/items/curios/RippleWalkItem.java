package com.k1sak1.goetyawaken.common.items.curios;

import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import javax.annotation.Nullable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import java.util.List;

public class RippleWalkItem extends SingleStackItem {

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("info.goetyawaken.ripplewalk"));
    }

    public static boolean hasRippleWalkItem(LivingEntity entity) {
        return CuriosFinder.hasCurio(entity, ModItems.RIPPLE_WALK.get());
    }

    public static boolean shouldEnableFluidWalking(LivingEntity player) {
        return canCollideWithFluid(player);
    }

    public static boolean canWalkOnFluid(LivingEntity player, FluidState fluidState) {
        if (shouldEnableFluidWalking(player)) {
            if (fluidState.is(FluidTags.LAVA) && !player.fireImmune() && !EnchantmentHelper.hasFrostWalker(player)) {
                player.hurt(player.damageSources().hotFloor(), 1);
            }
            return true;
        }
        return false;
    }

    private static boolean canCollideWithFluid(LivingEntity player) {
        return hasRippleWalkItem(player)
                && player.isSprinting()
                && !player.isUsingItem()
                && !player.isCrouching();
    }

}