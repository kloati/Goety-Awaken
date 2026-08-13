package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.blocks.entities.PedestalBlockEntity;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.Polarice3.Goety.utils.ItemHelper;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = Ritual.class, remap = false)
public abstract class RitualMixin {

    @Unique
    private double goetyawaken$materialReturnChance;

    private boolean goetyawaken$isAffixRitual() {
        return com.k1sak1.goetyawaken.common.items.magic.grimoire.GrimoireAffixHelper
                .isAffixRitualRecipe(((Ritual) (Object) this).getRecipe());
    }

    @Inject(method = "consumeAdditionalIngredients", at = @At("HEAD"))
    private void goetyawaken$storeMaterialReturnChance(Level world, BlockPos darkAltarPos, Player player,
            List<Ingredient> remainingAdditionalIngredients, int time, List<ItemStack> consumedIngredients,
            CallbackInfoReturnable<Boolean> cir) {
        this.goetyawaken$materialReturnChance = player != null
                ? ModAttributeRegistry.getRitualMaterialReturnChance(player)
                : 0.0D;
    }

    @WrapOperation(method = "consumeAdditionalIngredients", at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/common/ritual/Ritual;consumeAdditionalIngredient(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;Lnet/minecraft/world/item/crafting/Ingredient;Ljava/util/List;)Z"))
    private boolean goetyawaken$wrapConsumeIngredient(Ritual instance, Level world, BlockPos darkAltarPos,
            List<PedestalBlockEntity> pedestals, Ingredient ingredient, List<ItemStack> consumedIngredients,
            Operation<Boolean> original) {
        if (this.goetyawaken$materialReturnChance > 0.0D) {
            for (PedestalBlockEntity pedestal : pedestals) {
                if (pedestal.itemStackHandler.map(handler -> {
                    ItemStack stack = handler.getStackInSlot(0);
                    if (!stack.isEmpty() && ingredient.test(stack)
                            && world.random.nextFloat() < this.goetyawaken$materialReturnChance) {
                        ItemStack extracted = handler.extractItem(0, 1, false);
                        if (!extracted.isEmpty()) {
                            ItemHelper.addItemEntity(world, pedestal.getBlockPos().above(), extracted.copy());
                            consumedIngredients.add(extracted);
                            handler.setStackInSlot(0, ItemStack.EMPTY);
                            return true;
                        }
                    }
                    return false;
                }).orElse(false)) {
                    return true;
                }
            }
        }
        return original.call(instance, world, darkAltarPos, pedestals, ingredient, consumedIngredients);
    }

    @Inject(method = "matchesAdditionalIngredients", at = @At("HEAD"), cancellable = true)
    private void goetyawaken$allowMultipleMaterials(Player player, List<Ingredient> additionalIngredients,
            List<ItemStack> items, CallbackInfoReturnable<Boolean> cir) {
        if (!goetyawaken$isAffixRitual() || additionalIngredients.size() != 1 || items.isEmpty()) {
            return;
        }
        Ingredient ingredient = additionalIngredients.get(0);
        for (ItemStack stack : items) {
            if (!ingredient.test(stack)) {
                cir.setReturnValue(false);
                return;
            }
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "consumeAdditionalIngredients", at = @At("HEAD"), cancellable = true)
    private void goetyawaken$consumeMultipleMaterials(Level world, BlockPos darkAltarPos, Player player,
            List<Ingredient> remainingAdditionalIngredients, int time, List<ItemStack> consumedIngredients,
            CallbackInfoReturnable<Boolean> cir) {
        if (!goetyawaken$isAffixRitual() || remainingAdditionalIngredients.isEmpty()) {
            return;
        }
        Ingredient ingredient = remainingAdditionalIngredients.get(0);
        List<PedestalBlockEntity> pedestals = ((Ritual) (Object) this).getPedestals(world, darkAltarPos);
        int totalMaterials = consumedIngredients.size();
        for (PedestalBlockEntity pedestal : pedestals) {
            if (pedestal.itemStackHandler.map(handler -> {
                ItemStack stack = handler.getStackInSlot(0);
                return !stack.isEmpty() && ingredient.test(stack);
            }).orElse(false)) {
                totalMaterials++;
            }
        }
        if (totalMaterials == 0) {
            remainingAdditionalIngredients.clear();
            cir.setReturnValue(true);
            return;
        }
        int duration = ((Ritual) (Object) this).getRecipe().getDuration();
        int shouldConsume = duration > 0 ? (int) Math.floor((long) time * totalMaterials / (double) duration)
                : totalMaterials;
        int toConsumeNow = shouldConsume - consumedIngredients.size();
        if (toConsumeNow > 0) {
            double returnChance = ModAttributeRegistry.getRitualMaterialReturnChance(player);
            for (PedestalBlockEntity pedestal : pedestals) {
                if (toConsumeNow <= 0) {
                    break;
                }
                if (pedestal.itemStackHandler.map(handler -> {
                    ItemStack stack = handler.getStackInSlot(0);
                    if (!stack.isEmpty() && ingredient.test(stack)) {
                        ItemStack extracted = handler.extractItem(0, 1, false);
                        consumedIngredients.add(extracted);
                        if (returnChance > 0.0D && world.random.nextFloat() < returnChance) {
                            ItemHelper.addItemEntity(world, pedestal.getBlockPos().above(), extracted.copy());
                        }
                        handler.setStackInSlot(0, ItemStack.EMPTY);
                        return true;
                    }
                    return false;
                }).orElse(false)) {
                    toConsumeNow--;
                }
            }
        }
        boolean stillHas = false;
        for (PedestalBlockEntity pedestal : pedestals) {
            if (pedestal.itemStackHandler.map(handler -> {
                ItemStack stack = handler.getStackInSlot(0);
                return !stack.isEmpty() && ingredient.test(stack);
            }).orElse(false)) {
                stillHas = true;
                break;
            }
        }
        if (!stillHas) {
            remainingAdditionalIngredients.clear();
        }
        cir.setReturnValue(true);
    }
}
