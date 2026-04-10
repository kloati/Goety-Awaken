package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.common.items.equipment.HammerItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SunGraceItem extends HammerItem {

    public SunGraceItem() {
        super();
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                    (float) Config.sunGraceDamage - 1.0F, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                    (float) Config.sunGraceAttackSpeed - 4.0F, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker,
                (p_220045_0_) -> p_220045_0_.broadcastBreakEvent(net.minecraft.world.entity.EquipmentSlot.MAINHAND));

        if (pAttacker instanceof Player player) {
            float f2 = player.getAttackStrengthScale(0.5F);
            if (f2 > 0.9F) {
                this.attackMobs(pTarget, player, pStack);
                this.smash(pStack, pTarget, player);
                if (player.level() instanceof ServerLevel serverLevel) {
                    BlockPos blockPos = BlockPos.containing(pTarget.getX(), pTarget.getY() - 1.0F, pTarget.getZ());
                    serverLevel.sendParticles(
                            new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(
                                    255, 215, 0, 2.0F, 1),
                            pTarget.getX(),
                            pTarget.getY(),
                            pTarget.getZ(),
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        return true;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

}
