package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.ritual.CraftItemRitual;
import com.k1sak1.goetyawaken.common.items.magic.grimoire.GrimoireAffixHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftItemRitual.class, remap = false)
public abstract class CraftItemRitualMixin {

    @Inject(method = "finish", at = @At("TAIL"))
    private void goetyawaken$injectAffix(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity,
            Player castingPlayer, ItemStack activationItem, CallbackInfo ci) {
        RitualRecipe recipe = ((CraftItemRitual) (Object) this).getRecipe();
        if (!GrimoireAffixHelper.isAffixRitualRecipe(recipe)) {
            return;
        }
        IItemHandler handler = tileEntity.itemStackHandler.orElseThrow(RuntimeException::new);
        ItemStack inSlot = handler.getStackInSlot(0);
        if (!(inSlot.getItem() instanceof com.k1sak1.goetyawaken.common.items.magic.GrimoireItem)) {
            return;
        }
        ItemStack newStack = inSlot.copy();
        if (activationItem.getTag() != null) {
            newStack.setTag(activationItem.getTag().copy());
        }
        newStack.setCount(1);
        long worldSeed = world instanceof ServerLevel serverLevel ? serverLevel.getSeed() : 0L;
        long seed = worldSeed ^ GrimoireAffixHelper.hashInput(newStack, tileEntity.consumedIngredients);
        GrimoireAffixHelper.rollAndAppend(newStack, tileEntity.consumedIngredients, seed, 0.2D, true);
        handler.extractItem(0, 64, false);
        handler.insertItem(0, newStack, false);
    }
}
