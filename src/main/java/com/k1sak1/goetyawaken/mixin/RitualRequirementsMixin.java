package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RitualRequirements.class, remap = false)
public abstract class RitualRequirementsMixin {

    @WrapOperation(method = "canSummon", at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/api/entities/IOwned;getSummonLimit(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private static int goetyawaken$expandSummonLimit(IOwned owned, LivingEntity owner, Operation<Integer> original) {
        int limit = original.call(owned, owner);
        if (owner instanceof Player player) {
            return limit + ModAttributeRegistry.getServantCapacityLevel(player);
        }
        return limit;
    }
}
