package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.effects.brew.PurifyBrewEffect;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = PurifyBrewEffect.class, remap = false)
public abstract class PurifyBrewEffectMixin {

    @Shadow
    public boolean removeDebuff;

    @Inject(method = "applyEntityEffect", at = @At("HEAD"), cancellable = true)
    private void onApplyEntityEffect(LivingEntity pTarget, @Nullable Entity pSource, @Nullable Entity pIndirectSource,
            int pAmplifier, CallbackInfo ci) {
        Level level = pTarget.level();
        if (!level.isClientSide) {
            boolean hasAncientGlint = false;
            if (pTarget instanceof IAncientGlint ancientGlintEntity) {
                hasAncientGlint = ancientGlintEntity.hasAncientGlint();
            }
            for (MobEffect mobEffect : ForgeRegistries.MOB_EFFECTS) {
                boolean shouldRemove;

                if (this.removeDebuff) {
                    shouldRemove = !mobEffect.isBeneficial();
                } else {
                    if (hasAncientGlint) {
                        shouldRemove = false;
                    } else {
                        shouldRemove = mobEffect.isBeneficial();
                    }
                }
                if (shouldRemove && !mobEffect.getCurativeItems().isEmpty()) {
                    pTarget.removeEffect(mobEffect);
                }
            }
            com.Polarice3.Goety.common.network.ModNetwork.sentToTrackingEntity(
                    pTarget,
                    new com.Polarice3.Goety.common.network.server.SPurifyEffectPacket(pTarget.getId(),
                            this.removeDebuff));
            ci.cancel();
        }
    }
}
