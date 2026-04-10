package com.k1sak1.goetyawaken.common.items;

import com.Polarice3.Goety.common.items.equipment.HammerItem;
import com.Polarice3.Goety.utils.ColorUtil;

import java.util.UUID;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.RegistryObject;

public class MooShroomMaceItem extends HammerItem {

    public MooShroomMaceItem() {
        super();
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                    (float) Config.mooShroomMaceDamage - 1.0F, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                    (float) Config.mooShroomMaceAttackSpeed - 4.0F, AttributeModifier.Operation.ADDITION));
            builder.put(ForgeMod.ENTITY_REACH.get(),
                    new AttributeModifier(UUID.fromString("CB9F11D3-105C-2F21-A419-E31029322224"),
                            "Mace reach modifier", 1.0F, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker,
                (p_220045_0_) -> p_220045_0_.broadcastBreakEvent(net.minecraft.world.entity.EquipmentSlot.MAINHAND));

        if (pAttacker instanceof Player player) {
            float fallDistance = player.fallDistance;

            if (fallDistance > 1.5F) {
                float calculatedMaceDamage = calculateMaceDamage(fallDistance, pTarget,
                        player.damageSources().playerAttack(player));

                if (player.level() instanceof ServerLevel serverLevel) {
                    BlockPos blockPos = BlockPos.containing(pTarget.getX(), pTarget.getY() - 1.0F, pTarget.getZ());
                    ColorUtil colorUtil = new ColorUtil(
                            serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);

                    serverLevel.sendParticles(
                            new com.Polarice3.Goety.client.particles.CircleExplodeParticleOption(
                                    colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.5F, 1),
                            pTarget.getX(),
                            pTarget.getY(),
                            pTarget.getZ(),
                            1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                double baseDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float totalDamage = calculatedMaceDamage + (float) baseDamage;
                pTarget.hurt(player.damageSources().fallingBlock(player), totalDamage);
                player.fallDistance = 0.0F;
                this.calculatedMainAttackDamage = totalDamage;
                com.k1sak1.goetyawaken.utils.MobEffectUtils.forceAdd(pTarget, new MobEffectInstance(
                        com.Polarice3.Goety.common.effects.GoetyEffects.STUNNED.get(), 40, 0), player);
                com.k1sak1.goetyawaken.utils.MobEffectUtils.forceAdd(pTarget, new MobEffectInstance(
                        com.Polarice3.Goety.common.effects.GoetyEffects.CURSED.get(), 100, 0), player);
                applyBlastEffect(player, pTarget, calculatedMaceDamage);
                triggerSurroundTremor(player, pTarget, totalDamage);
            } else {
                double baseDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                pTarget.hurt(player.damageSources().playerAttack(player), (float) baseDamage);
                float f2 = player.getAttackStrengthScale(0.5F);
                if (f2 > 0.9F) {
                    this.attackMobs(pTarget, player, pStack);
                    this.smash(pStack, pTarget, player);
                }
            }
        }

        return true;
    }

    private void applyBlastEffect(Player player, LivingEntity primaryTarget, float damage) {
        int windLevel = (int) (damage / 6.0F);
        int h = 4 + windLevel;
        double y = Math.max(0.0, (double) h);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    8,
                    1.5D, 0.15D, 1.5D,
                    0.0D);

            serverLevel.sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    4,
                    1.8D, 0.15D, 1.8D,
                    0.0D);

            RegistryObject<SoundEvent>[] windBurstSounds = new RegistryObject[] {
                    ModSounds.WIND_BURST_1,
                    ModSounds.WIND_BURST_2,
                    ModSounds.WIND_BURST_3
            };
            RegistryObject<SoundEvent> randomSound = windBurstSounds[player.level().random
                    .nextInt(windBurstSounds.length)];
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    randomSound.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
        }
        Vec3 currentMotion = player.getDeltaMovement();
        double newUpwardVelocity = Math.max(y * 0.15, 0.4);
        player.setDeltaMovement(currentMotion.x, newUpwardVelocity, currentMotion.z);
        player.hasImpulse = true;
        player.setOnGround(false);
        player.fallDistance = 0.0F;
        if (!player.level().isClientSide) {
            player.hurtMarked = true;
        }

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION,
                    player.getX(),
                    player.getY() + 0.5D,
                    player.getZ(),
                    1,
                    0.1D, 0.1D, 0.1D,
                    0.1D);
        }
        applyBlastAreaDamage(player, primaryTarget);
    }

    private float calculatedMainAttackDamage = 0.0F;

    private void applyBlastAreaDamage(Player player, LivingEntity primaryTarget) {
        if (player.level() instanceof ServerLevel serverLevel) {
            int radiusLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), player.getMainHandItem());
            double blastRadius = 4.0D + radiusLevel;
            AABB area = new AABB(
                    player.getX() - blastRadius,
                    player.getY() - blastRadius,
                    player.getZ() - blastRadius,
                    player.getX() + blastRadius,
                    player.getY() + blastRadius,
                    player.getZ() + blastRadius);
            java.util.List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(LivingEntity.class, area,
                    entity -> entity != player && entity != primaryTarget &&
                            entity.distanceToSqr(player) <= blastRadius * blastRadius);
            for (LivingEntity entity : nearbyEntities) {
                double distance = player.distanceTo(entity);
                float distanceFactor = (float) (1.0 - (distance / blastRadius) / 3);
                float blastDamage = calculatedMainAttackDamage * distanceFactor;
                entity.hurt(player.damageSources().fallingBlock(player), Math.max(blastDamage, 0.5F));
                Vec3 knockbackVector = new Vec3(
                        entity.getX() - player.getX(),
                        0.0D,
                        entity.getZ() - player.getZ()).normalize().multiply(0.4D, 0.4D, 0.4D);

                entity.push(knockbackVector.x, 0.4D, knockbackVector.z);
                entity.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 100, 0));
            }
        }
    }

    private float calculateMaceDamage(float fallDistance, LivingEntity target, DamageSource source) {
        float damage = 0.0F;

        if (fallDistance > 0) {
            float remainingDistance = fallDistance;

            if (remainingDistance > 0) {
                float firstThreeBlocks = Math.min(remainingDistance, 3.0F);
                damage += firstThreeBlocks * 4.0F;
                remainingDistance -= firstThreeBlocks;

                if (remainingDistance > 0) {
                    float fourToEightBlocks = Math.min(remainingDistance, 5.0F);
                    damage += fourToEightBlocks * 2.0F;
                    remainingDistance -= fourToEightBlocks;

                    if (remainingDistance > 0) {
                        damage += remainingDistance * 1.0F;
                    }
                }
            }
        }

        damage = CombatRules.getDamageAfterAbsorb(damage, (float) target.getArmorValue(),
                (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        if (target.hasEffect(MobEffects.WEAKNESS)) {
            int weaknessAmplifier = target.getEffect(MobEffects.WEAKNESS).getAmplifier();
            int e = (weaknessAmplifier + 1) * 5;
            damage = Math.max(damage * (25.0F - (float) e) / 25.0F, 0.0F);
        }
        damage = CombatRules.getDamageAfterMagicAbsorb(damage,
                (float) EnchantmentHelper.getDamageProtection(target.getArmorSlots(), source));

        return damage;
    }

    private void triggerSurroundTremor(Player player, LivingEntity target, float totalDamage) {
        int radius = Math.min((int) Math.floor(totalDamage / 20.0F + 2.0F), 8);
        if (player.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i <= radius; ++i) {
                com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity.surroundTremor(target, i, 3, 0.0F,
                        false, 0.1F, new Vec3(target.getX(), target.getY(), target.getZ()));
            }

        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }
}