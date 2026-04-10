package com.k1sak1.goetyawaken.common.effects;

import com.k1sak1.goetyawaken.utils.AttributeModifierManager;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RampagingEffect extends BaseEffect {
    public RampagingEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity,
            net.minecraft.world.entity.ai.attributes.AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        AttributeModifierManager.removeRampagingModifier(entity);
    }
}