package com.k1sak1.goetyawaken.common.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.Config;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.UUID;

import javax.annotation.Nullable;

public class ObsidianClaymoreItem extends SwordItem implements ISoulRepair {

    public ObsidianClaymoreItem() {
        super(Tiers.NETHERITE, 14, -3.0F + 1.0F, new Properties().durability(2048).fireResistant());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                    (float) Config.obsidianClaymoreDamage - 1.0F, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                    (float) Config.obsidianClaymoreAttackSpeed - 4.0F, AttributeModifier.Operation.ADDITION));
            builder.put(ForgeMod.ENTITY_REACH.get(),
                    new AttributeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-E30960792006"),
                            "Obsidian Claymore reach modifier", 2.0F, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_KNOCKBACK,
                    new AttributeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-E30960792007"),
                            "Obsidian Claymore knockback modifier", 1.0F, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        target.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 100, 0));

        if (attacker instanceof Player player) {
            if (player.getAttackStrengthScale(0) > 0.9F) {
                performFullSweepAttack(stack, player);
            }
        }

        return result;
    }

    private void playSweepEffects(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.sweepAttack();
        if (player.level() instanceof ServerLevel serverLevel) {
            double d0 = -Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
            double d1 = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    player.getX() + d0, player.getY(0.5D), player.getZ() + d1,
                    0, d0, 0.0D, d1, 0.0D);
        }
    }

    @Override
    public net.minecraft.world.phys.AABB getSweepHitBox(ItemStack stack, Player player,
            net.minecraft.world.entity.Entity target) {
        double range = 4.5D;
        return target.getBoundingBox().inflate(range, 0.25D, range);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }

    public void performFullSweepAttack(ItemStack stack, Player player) {
        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float enchantmentBonus = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
        float totalBaseDamage = baseDamage + enchantmentBonus;
        int sweepingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, player);
        float sweepRatio = 0.1F * sweepingLevel;
        float damage = totalBaseDamage * (1.0F + sweepRatio);

        double range = 2.5D;
        double entityReach = player.getAttributeValue(ForgeMod.ENTITY_REACH.get());
        AABB sweepBox = player.getBoundingBox().inflate(range, 0.25D, range);
        java.util.List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, sweepBox);
        boolean hitAny = false;

        for (LivingEntity sweepTarget : entities) {

            if (sweepTarget != player &&
                    !(sweepTarget instanceof net.minecraft.world.entity.decoration.ArmorStand armorStand
                            && armorStand.isMarker())
                    &&
                    player.canAttack(sweepTarget) &&
                    !MobUtil.areAllies(player, sweepTarget)) {
                double entityReachSq = Mth.square(range + entityReach);
                double distanceSquared = player.distanceToSqr(sweepTarget);
                if (distanceSquared < entityReachSq) {
                    sweepTarget.knockback(0.4F,
                            Mth.sin(player.getYRot() * ((float) Math.PI / 180F)),
                            -Mth.cos(player.getYRot() * ((float) Math.PI / 180F)));
                    sweepTarget.hurt(player.damageSources().playerAttack(player), damage);
                    sweepTarget.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 100, 0));
                    hitAny = true;
                }
            }
        }
        playSweepEffects(player);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, java.util.List<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, worldIn, tooltip, flag);
        tooltip.add(Component.translatable("item.goetyawaken.claymore.tooltip")
                .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public void repairTick(ItemStack stack, Entity entityIn, boolean isSelected) {
        com.Polarice3.Goety.utils.ItemHelper.repairTick(stack, entityIn, isSelected);
    }
}