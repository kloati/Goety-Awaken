package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TruthseekerItem extends TieredItem implements Vanishable, ISoulRepair {

    public TruthseekerItem() {
        super(ModTiers.DEATH, (new Properties()).rarity(Rarity.RARE).durability(1000));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Truthseeker modifier",
                            (float) Config.truthSeekerDamage - 1.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Truthseeker modifier",
                            (float) Config.truthSeekerAttackSpeed - 4.0F,
                            AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, java.util.List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.goetyawaken.truth_seeker.tooltip"));
    }

    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return !p_43294_.isCreative();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        boolean result = super.hurtEnemy(pStack, pTarget, pAttacker);
        if (pTarget != null && pAttacker instanceof Player player) {
            double baseSouls = SEHelper.getSoulGiven(pTarget);
            int soulEaterLevel = pStack
                    .getEnchantmentLevel(com.Polarice3.Goety.common.enchantments.ModEnchantments.SOUL_EATER.get());
            int soulEaterMultiplier = Math.max(soulEaterLevel + 1, 1);
            int bonusSouls = (int) (baseSouls * soulEaterMultiplier * 0.05);
            SEHelper.increaseSouls(player, bonusSouls);
        }
        return result;
    }

    public boolean mineBlock(ItemStack p_43282_, Level p_43283_, BlockState p_43284_, BlockPos p_43285_,
            LivingEntity p_43286_) {
        if (p_43284_.getDestroySpeed(p_43283_, p_43285_) != 0.0F) {
            p_43282_.hurtAndBreak(2, p_43286_, (p_43276_) -> {
                p_43276_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return (enchantment.category == EnchantmentCategory.WEAPON
                || enchantment.category == EnchantmentCategory.BREAKABLE
                || enchantment.category == EnchantmentCategory.VANISHABLE || enchantment == Enchantments.MOB_LOOTING)
                && !(enchantment instanceof SweepingEdgeEnchantment);
    }
}