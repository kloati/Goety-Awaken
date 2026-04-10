package com.k1sak1.goetyawaken.mixin.client;

import com.k1sak1.goetyawaken.common.mobenchant.IMobEnchantable;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantCapability;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantEventHandler;
import com.k1sak1.goetyawaken.common.mobenchant.MobEnchantType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;scale(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;F)V", shift = At.Shift.AFTER))
    public void onRenderScale(LivingEntity livingEntity, float p_115309_, float p_115310_, PoseStack poseStack,
            MultiBufferSource p_115312_, int p_115313_, CallbackInfo ci) {
        if (livingEntity instanceof IMobEnchantable enchantable) {
            MobEnchantCapability cap = MobEnchantEventHandler.getCapability(livingEntity);
            if (cap != null && cap.hasMobEnchantment()) {
                int hugeLevel = cap.getMobEnchantLevel(MobEnchantType.HUGE);
                if (hugeLevel > 0) {
                    float scaleMultiplier = 1.0F + 0.15F * (float) hugeLevel;
                    poseStack.scale(scaleMultiplier, scaleMultiplier, scaleMultiplier);
                }
            }
        }
    }
}
