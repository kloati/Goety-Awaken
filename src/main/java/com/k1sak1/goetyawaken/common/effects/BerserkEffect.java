package com.k1sak1.goetyawaken.common.effects;

import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.utils.AttributeModifierManager;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BerserkEffect extends BaseEffect {
    public BerserkEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        AttributeModifierManager.applyBerserkModifier(entity, amplifier);
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
        AttributeModifierManager.removeBerserkModifier(entity);
        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        boolean hasMushroomHat = headItem.getItem() == ModItems.MUSHROOM_HAT.get();
        if (!hasMushroomHat) {
            int level = amplifier;
            int duration = 600;
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, level, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, level, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, level, false, false));
        }
    }
}