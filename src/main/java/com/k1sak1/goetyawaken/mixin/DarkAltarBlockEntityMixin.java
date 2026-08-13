package com.k1sak1.goetyawaken.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.Polarice3.Goety.common.blocks.entities.CursedCageBlockEntity;
import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.k1sak1.goetyawaken.common.items.magic.GrimoireItem;
import com.k1sak1.goetyawaken.common.items.magic.grimoire.GrimoireAffixHelper;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DarkAltarBlockEntity.class, remap = false)
public abstract class DarkAltarBlockEntityMixin {

    @Unique
    private double goetyawaken$fractionalProgress;

    @Shadow
    public Player castingPlayer;
    @Shadow
    public int currentTime;
    @Shadow
    private CursedCageBlockEntity cursedCageTile;

    @Shadow
    public RitualRecipe getCurrentRitualRecipe() {
        return null;
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lcom/Polarice3/Goety/common/blocks/entities/DarkAltarBlockEntity;currentTime:I", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void goetyawaken$accelerateRitual(CallbackInfo ci) {
        RitualRecipe recipe = this.getCurrentRitualRecipe();
        if (recipe != null && this.castingPlayer != null && this.cursedCageTile != null) {
            int duration = recipe.getDuration();
            if (this.currentTime < duration) {
                double speed = 1.0D + ModAttributeRegistry.getRitualSpeed(this.castingPlayer);
                this.goetyawaken$fractionalProgress += speed;
                int add = (int) this.goetyawaken$fractionalProgress;
                this.goetyawaken$fractionalProgress -= add;
                if (add > 1) {
                    int extra = add - 1;
                    int cap = Math.max(1, duration - 1);
                    int actualExtra = Math.min(extra, cap - this.currentTime);
                    if (actualExtra > 0) {
                        double multiplier = ModAttributeRegistry.getRitualCostMultiplier(this.castingPlayer);
                        long need = (long) Math.max(0.0D,
                                Math.round(actualExtra * (double) recipe.getSoulCost() * multiplier));
                        if (need <= Integer.MAX_VALUE && this.cursedCageTile.getSouls() >= (int) need) {
                            this.cursedCageTile.decreaseSouls((int) need);
                            this.currentTime += actualExtra;
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "clearRitual", at = @At("HEAD"))
    private void goetyawaken$resetFractionalProgress(CallbackInfo ci) {
        this.goetyawaken$fractionalProgress = 0.0D;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/common/crafting/RitualRecipe;getSoulCost()I"))
    private int goetyawaken$wrapSoulCost(RitualRecipe recipe, Operation<Integer> original) {
        int cost = original.call(recipe);
        if (this.castingPlayer != null) {
            double multiplier = ModAttributeRegistry.getRitualCostMultiplier(this.castingPlayer);
            if (multiplier != 1.0D) {
                return (int) Math.max(0.0D, Math.round(cost * multiplier));
            }
        }
        return cost;
    }

    @Inject(method = "activate", at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/common/blocks/entities/DarkAltarBlockEntity;startRitual(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lcom/Polarice3/Goety/common/crafting/RitualRecipe;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void goetyawaken$blockFullGrimoire(Level world, BlockPos pos, Player player, InteractionHand hand,
            Direction face, CallbackInfoReturnable<Boolean> cir, @Local RitualRecipe ritualRecipe) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof GrimoireItem && GrimoireAffixHelper.isAffixRitualRecipe(ritualRecipe)
                && GrimoireAffixHelper.isFull(held)) {
            player.displayClientMessage(
                    Component.translatable("info.goetyawaken.grimoire.affix_full").withStyle(ChatFormatting.RED), true);
            cir.setReturnValue(false);
        }
    }
}
