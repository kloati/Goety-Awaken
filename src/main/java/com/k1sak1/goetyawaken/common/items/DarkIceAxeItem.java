package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.common.items.equipment.IceAxeItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DarkIceAxeItem extends IceAxeItem implements ISoulRepair {

    public DarkIceAxeItem() {
        super(ModTiers.DARK);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            Multimap<Attribute, AttributeModifier> multimap = HashMultimap
                    .create(super.getDefaultAttributeModifiers(equipmentSlot));
            multimap.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 12.0F,
                            AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", 0.9F - 4.0F,
                            AttributeModifier.Operation.ADDITION));
            return multimap;
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 60));
        stack.hurtAndBreak(1, attacker, (p_41007_) -> {
            p_41007_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });

        return true;
    }

    @Override
    public void repairTick(ItemStack stack, Entity entityIn, boolean isSelected) {
        com.Polarice3.Goety.utils.ItemHelper.repairTick(stack, entityIn, isSelected);
    }
}