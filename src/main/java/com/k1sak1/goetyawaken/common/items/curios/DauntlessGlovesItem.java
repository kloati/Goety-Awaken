package com.k1sak1.goetyawaken.common.items.curios;

import com.Polarice3.Goety.common.items.curios.SingleStackItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import javax.annotation.Nullable;
import java.util.List;

public class DauntlessGlovesItem extends SingleStackItem {

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return "hands".equals(slotContext.identifier());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.goetyawaken.dauntless_gloves.description"));
    }

    public static boolean hasDauntlessGlovesItem(LivingEntity entity) {
        return CuriosFinder.hasCurio(entity, ModItems.DAUNTLESS_GLOVES.get());
    }
}