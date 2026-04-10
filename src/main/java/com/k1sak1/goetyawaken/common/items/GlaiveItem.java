package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.common.items.ModTiers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class GlaiveItem extends SwordItem {
    private static final UUID BASE_ATTACK_RANGE_UUID = UUID.fromString("CB1F55D3-645C-4F38-A497-E91668612003");

    public GlaiveItem() {
        super(ModTiers.SPECIAL, 3, -2.8F, new Item.Properties().durability(1562));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Glaive modifier",
                            (float) Config.glaiveDamage - 1.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Glaive modifier",
                            (float) Config.glaiveAttackSpeed - 4.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(
                    BASE_ATTACK_RANGE_UUID,
                    "Glaive modifier", 1.0F, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }
}