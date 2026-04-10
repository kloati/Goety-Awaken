package com.k1sak1.goetyawaken.common.effects;

import com.k1sak1.goetyawaken.utils.AttributeModifierManager;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FrenziedEffect extends BaseEffect {
    public FrenziedEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth() / 2) {
            AttributeModifierManager.applyFrenziedModifier(entity, amplifier);
        } else {
            AttributeModifierManager.removeFrenziedModifier(entity);
        }
        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity,
            net.minecraft.world.entity.ai.attributes.AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        AttributeModifierManager.removeFrenziedModifier(entity);
    }
}