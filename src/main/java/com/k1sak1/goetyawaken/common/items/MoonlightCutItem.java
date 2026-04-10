package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModSwordProjectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.Random;
import java.util.UUID;

public class MoonlightCutItem extends SwordItem {
    private static final Random RANDOM = new Random();
    private static final UUID BASE_ATTACK_RANGE_UUID = UUID.fromString("CB1F98D3-645C-4F60-A473-E09768679213");

    public MoonlightCutItem() {
        super(Tiers.NETHERITE, 7, -2.8F, new Properties().durability(1048));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Moonlight Cut modifier",
                            (float) Config.moonlightCutDamage - 1.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED,
                    new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Moonlight Cut modifier",
                            (float) Config.moonlightCutAttackSpeed - 4.0F,
                            AttributeModifier.Operation.ADDITION));
            builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(
                    BASE_ATTACK_RANGE_UUID,
                    "Moonlight Cut modifier", 1.0F, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 30;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (target.getVehicle() != null || !target.getPassengers().isEmpty()) {
            target.stopRiding();
        }

        if (RANDOM.nextDouble() < 0.05) {
            target.addEffect(new MobEffectInstance(com.Polarice3.Goety.common.effects.GoetyEffects.SAPPED.get(),
                    200, 0));
            MobUtil.disableShield(target, 100);
        }

        if (target instanceof Player targetPlayer) {
            double baseSouls = SEHelper.getSoulGiven(targetPlayer);
            int soulEaterLevel = stack
                    .getEnchantmentLevel(com.Polarice3.Goety.common.enchantments.ModEnchantments.SOUL_EATER.get());
            int soulEaterMultiplier = Math.max(soulEaterLevel + 1, 1);
            int soulsToDecrease = (int) (baseSouls * soulEaterMultiplier * 0.1);
            SEHelper.decreaseSouls(targetPlayer, soulsToDecrease);
        }

        if (attacker instanceof Player player) {
            double baseSouls = SEHelper.getSoulGiven(target);
            int soulEaterLevel = stack
                    .getEnchantmentLevel(com.Polarice3.Goety.common.enchantments.ModEnchantments.SOUL_EATER.get());
            int soulEaterMultiplier = Math.max(soulEaterLevel + 1, 1);
            int bonusSouls = (int) (baseSouls * soulEaterMultiplier * 0.05);
            SEHelper.increaseSouls(player, bonusSouls);
        }

        if (!target.level().isClientSide) {
            if (RANDOM.nextDouble() < 0.05) {
                ItemStack ectoplasmStack = new ItemStack(com.Polarice3.Goety.common.items.ModItems.ECTOPLASM.get());
                spawnItemAtLocation(target, ectoplasmStack);
            }
            if (RANDOM.nextDouble() < 0.025) {
                ItemStack graveDustStack = new ItemStack(
                        com.Polarice3.Goety.common.items.ModItems.GRAVE_DUST.get());
                spawnItemAtLocation(target, graveDustStack);
            }
        }

        return result;
    }

    private void spawnItemAtLocation(LivingEntity entity, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack);
        itemEntity.setPickUpDelay(40);
        entity.level().addFreshEntity(itemEntity);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            return InteractionResultHolder.fail(itemStack);
        }

        InteractionHand offHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;
        ItemStack offHandStack = player.getItemInHand(offHand);
        if (!offHandStack.isEmpty() && offHandStack.getItem() instanceof com.Polarice3.Goety.api.items.magic.IWand) {
            return InteractionResultHolder.pass(itemStack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10) {
                if (!level.isClientSide) {
                    stack.hurtAndBreak(1, player, (p_43388_) -> {
                        p_43388_.broadcastBreakEvent(player.getUsedItemHand());
                    });

                    performShoot(level, player, stack);
                }

                player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
            }
        }
    }

    private void performShoot(Level level, Player player, ItemStack moonlightCutStack) {
        double d0 = player.getLookAngle().x;
        double d1 = player.getLookAngle().y;
        double d2 = player.getLookAngle().z;
        ItemStack glaiveStack = moonlightCutStack.copy();
        ModSwordProjectile magicGlaive = new ModSwordProjectile(player, level, glaiveStack);
        magicGlaive.setPos(player.getX(), player.getEyeY() - 0.1F, player.getZ());
        float f = 0.2F;
        magicGlaive.shoot(d0, d1 + (double) f * 0.2F, d2, 2.6F,
                10.0F - (float) level.getDifficulty().getId() * 3.0F);
        magicGlaive.setCritArrow(true);
        level.playSound(null, player, SoundEvents.TRIDENT_THROW, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 10; ++i) {
                double d5 = player.getRandom().nextGaussian() * 0.02D;
                double d6 = player.getRandom().nextGaussian() * 0.02D;
                double d7 = player.getRandom().nextGaussian() * 0.02D;
                serverLevel.sendParticles(ParticleTypes.WITCH,
                        player.getRandomX(1.0D),
                        player.getRandomY() + 0.5D,
                        player.getRandomZ(1.0D),
                        0, d5, d6, d7, 1.0D);
            }
        }

        level.addFreshEntity(magicGlaive);
    }
}