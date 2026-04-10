package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import java.util.ArrayList;
import java.util.List;

public class DeathArrow extends com.Polarice3.Goety.common.entities.projectiles.DeathArrow {

    public DeathArrow(EntityType<? extends Arrow> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
    }

    public DeathArrow(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity livingEntity) {
        super.doPostHurtEffects(livingEntity);

        if (this.getOwner() instanceof ApostleServant apostleServant && apostleServant.isApostleUpgraded()
                && apostleServant.getTitleNumber() == 5) {
            if (this.level().random.nextFloat() <= 0.25F) {
                applyRandomNegativeEffect(livingEntity);
            }
        }

        if (this.getOwner() instanceof ApostleServant apostleServant && apostleServant.isApostleUpgraded()
                && apostleServant.getTitleNumber() == 4) {
            livingEntity.hurt(livingEntity.damageSources().sonicBoom(this.getOwner()), 4.0F);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();

        if (this.getOwner() instanceof ApostleServant apostleServant && apostleServant.isApostleUpgraded()
                && apostleServant.getTitleNumber() == 7) {
            if (entity instanceof LivingEntity livingEntity) {
                removeRandomPositiveEffect(livingEntity);
            }
        }
    }

    private void applyRandomNegativeEffect(LivingEntity target) {
        List<MobEffect> negativeEffects = new ArrayList<>();
        negativeEffects.add(net.minecraft.world.effect.MobEffects.WEAKNESS);
        negativeEffects.add(net.minecraft.world.effect.MobEffects.POISON);
        negativeEffects.add(net.minecraft.world.effect.MobEffects.WITHER);
        negativeEffects.add(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
        negativeEffects.add(net.minecraft.world.effect.MobEffects.BLINDNESS);
        negativeEffects.add(net.minecraft.world.effect.MobEffects.DARKNESS);
        negativeEffects.add(net.minecraft.world.effect.MobEffects.HUNGER);
        negativeEffects.add(net.minecraft.world.effect.MobEffects.HARM);
        negativeEffects.add(GoetyEffects.CURSED.get());
        negativeEffects.add(GoetyEffects.FREEZING.get());
        negativeEffects.add(GoetyEffects.NYCTOPHOBIA.get());
        negativeEffects.add(GoetyEffects.SUN_ALLERGY.get());
        negativeEffects.add(GoetyEffects.WANE.get());
        negativeEffects.add(GoetyEffects.TRIPPING.get());
        negativeEffects.add(GoetyEffects.ARROWMANTIC.get());
        negativeEffects.add(GoetyEffects.FLAMMABLE.get());
        negativeEffects.add(GoetyEffects.PLUNGE.get());
        negativeEffects.add(GoetyEffects.ENDER_GROUND.get());
        if (!negativeEffects.isEmpty()) {
            net.minecraft.util.RandomSource random = this.level().random;
            MobEffect randomEffect = negativeEffects.get(random.nextInt(negativeEffects.size()));
            int amplifier = random.nextInt(4);
            int duration = 200 + random.nextInt(1000);

            target.addEffect(new MobEffectInstance(randomEffect, duration, amplifier));
        }
    }

    private void removeRandomPositiveEffect(LivingEntity target) {
        List<MobEffectInstance> positiveEffects = new ArrayList<>();
        for (MobEffectInstance effect : target.getActiveEffects()) {
            if (effect.getEffect().isBeneficial()) {
                positiveEffects.add(effect);
            }
        }

        if (!positiveEffects.isEmpty()) {
            MobEffectInstance randomEffect = positiveEffects.get(this.level().random.nextInt(positiveEffects.size()));
            target.removeEffect(randomEffect.getEffect());
        }
    }
}