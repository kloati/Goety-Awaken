package com.k1sak1.goetyawaken.mixin;

import com.k1sak1.goetyawaken.common.entities.ally.BurningShield;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Projectile.class)
public abstract class ProjectileCanHitMixin {

    @Shadow
    @Nullable
    public abstract Entity getOwner();

    @Inject(method = "canHitEntity", at = @At("HEAD"), cancellable = true)
    private void goetyawaken$burningShieldProjectilePassThrough(Entity pTarget, CallbackInfoReturnable<Boolean> cir) {
        if (pTarget instanceof BurningShield shield) {
            LivingEntity shieldOwner = shield.getTrueOwner();
            Entity projectileOwner = this.getOwner();
            if (shieldOwner != null && projectileOwner != null) {
                if (projectileOwner == shieldOwner || MobUtil.areAllies(shieldOwner, projectileOwner)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
