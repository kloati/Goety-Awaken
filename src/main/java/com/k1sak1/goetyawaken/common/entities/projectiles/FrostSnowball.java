package com.k1sak1.goetyawaken.common.entities.projectiles;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class FrostSnowball extends Snowball {
    private LivingEntity targetEntity;

    public FrostSnowball(Level pLevel, LivingEntity pShooter, LivingEntity target) {
        super(pLevel, pShooter);
        this.targetEntity = target;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            Entity entity = pResult.getEntity();
            Entity owner = this.getOwner();
            if (entity instanceof LivingEntity livingEntity && entity == this.targetEntity) {
                livingEntity.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        60,
                        0,
                        false,
                        false));
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity pEntity) {
        if (this.targetEntity != null) {
            return pEntity == this.targetEntity && super.canHitEntity(pEntity);
        }
        return false;
    }
}
