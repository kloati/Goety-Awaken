package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class GraveBaneSwordItem extends SwordItem {
    private static final UUID BASE_ATTACK_RANGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-E30960792003");

    public GraveBaneSwordItem() {
        super(Tiers.NETHERITE, 7, -2.8F, new Properties().durability(1000));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "GraveBane modifier",
                            (float) Config.graveBaneDamage - 1.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "GraveBane modifier",
                            (float) Config.graveBaneAttackSpeed - 4.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(
                    BASE_ATTACK_RANGE_UUID,
                    "GraveBane modifier", 1.0F, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.SMITE) {
            int originalLevel = EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
            return originalLevel * 2;
        }
        return super.getEnchantmentLevel(stack, enchantment);
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = super.getAllEnchantments(stack);
        if (enchantments.containsKey(Enchantments.SMITE)) {
            int originalLevel = enchantments.get(Enchantments.SMITE);
            enchantments.put(Enchantments.SMITE, originalLevel * 2);
        }
        return enchantments;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.goetyawaken.grave_bane.tooltip")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}