package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.utils.AttributeModifierManager;
import com.Polarice3.Goety.common.entities.projectiles.RazorWind;
import com.Polarice3.Goety.common.entities.util.MagicLightningTrap;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ModEffectsEvents {

    private static final UUID SHARPNESS_MODIFIER_UUID = UUID.fromString("5D6F0BA2-1186-46AC-B896-C12AE9BD4B65");

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (entity.hasEffect(ModEffects.ENCHANTMENT_THORNS.get())) {
            handleThornsEffect(entity, source, event.getAmount());
        }

        if (source.getEntity() instanceof LivingEntity attacker &&
                attacker.hasEffect(ModEffects.ENCHANTMENT_SHARPNESS.get())) {
            handleSharpnessEffect(attacker, event);
        }

        if (source.getEntity() instanceof LivingEntity attacker &&
                attacker.hasEffect(ModEffects.CRITICAL_HIT.get())) {
            handleCriticalHitEffect(attacker, event);
        }

        if (source.getEntity() instanceof LivingEntity attacker &&
                attacker.hasEffect(ModEffects.COMMITTED.get())) {
            handleCommittedEffectInEvent(attacker, entity, event);
        }

        if (source.getEntity() instanceof LivingEntity attacker &&
                attacker.hasEffect(ModEffects.VISUAL_DISTURBANCE.get())) {
            handleVisualDisturbanceEffect(attacker, event);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        LivingEntity attacker = event.getEntity();
        Entity target = event.getTarget();

        if (target instanceof LivingEntity livingTarget) {

            if (attacker.hasEffect(ModEffects.CRITICAL_HIT.get())) {
                handleCriticalHit(attacker, livingTarget);
            }

            if (attacker.hasEffect(ModEffects.WEAKENING_HANDS.get())) {
                handleWeakeningHands(attacker, livingTarget);
            }

            if (attacker.hasEffect(ModEffects.CHAINS.get())) {
                handleChainsEffect(attacker, livingTarget);
            }

            if (attacker.hasEffect(ModEffects.SHOCKWAVE.get())) {
                handleShockwaveEffect(attacker, livingTarget);
            }

            if (attacker.hasEffect(ModEffects.ENCHANTMENT_THUNDERING.get())) {
                handleThunderingEffect(attacker, livingTarget);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        Entity target = event.getSource().getEntity();
        Entity directEntity = event.getSource().getDirectEntity();

        // 检查直接攻击者是否为LivingEntity并且拥有药水效果
        if (directEntity instanceof LivingEntity attacker) {
            if (attacker.hasEffect(ModEffects.COMMITTED.get())) {
                handleCommittedEffect(attacker, victim);
            }

            if (attacker.hasEffect(ModEffects.WEAKENING_HANDS.get()) && victim instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) victim;
                handleWeakeningHands(attacker, livingTarget);
            }

            if (victim instanceof LivingEntity && attacker.hasEffect(ModEffects.CHAINS.get())) {
                LivingEntity livingTarget = (LivingEntity) victim;
                handleChainsEffect(attacker, livingTarget);
            }

            if (victim instanceof LivingEntity && attacker.hasEffect(ModEffects.SHOCKWAVE.get())) {
                LivingEntity livingTarget = (LivingEntity) victim;
                handleShockwaveEffect(attacker, livingTarget);
            }

            if (victim instanceof LivingEntity && attacker.hasEffect(ModEffects.ENCHANTMENT_THUNDERING.get())) {
                LivingEntity livingTarget = (LivingEntity) victim;
                handleThunderingEffect(attacker, livingTarget);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker &&
                attacker.hasEffect(ModEffects.RAMPAGING.get())) {
            handleRampagingEffect(attacker);
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ModEffects.RECOVER.get())) {
            handleRecoverEffectOnHeal(entity, event);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ModEffects.FRENZIED.get())) {
            handleFrenziedEffect(entity);
        }

        AttributeModifierManager.checkAndRemoveExpiredRampagingModifiers(entity);
    }

    private static void handleSharpnessEffect(LivingEntity attacker, LivingHurtEvent event) {
        MobEffectInstance sharpnessEffect = attacker.getEffect(ModEffects.ENCHANTMENT_SHARPNESS.get());
        if (sharpnessEffect != null) {
            int amplifier = sharpnessEffect.getAmplifier();
            double damageBonusPercent = (amplifier + 1) * ((amplifier + 1) + 19) / 2.0;
            float originalDamage = event.getAmount();
            float newDamage = originalDamage * (1 + (float) (damageBonusPercent / 100.0));
            event.setAmount(newDamage);
        }
    }

    private static void handleThornsEffect(LivingEntity entity, DamageSource source, float damage) {
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingAttacker) {
            MobEffectInstance thornsEffect = entity.getEffect(ModEffects.ENCHANTMENT_THORNS.get());
            if (thornsEffect != null) {
                int amplifier = thornsEffect.getAmplifier();
                float thornsDamage = damage * (0.1f * (amplifier + 1));
                livingAttacker.hurt(entity.damageSources().thorns(entity), thornsDamage);
            }
        }
    }

    private static void handleCriticalHit(LivingEntity attacker, LivingEntity target) {
        MobEffectInstance criticalHitEffect = attacker.getEffect(ModEffects.CRITICAL_HIT.get());
        if (criticalHitEffect != null) {
            int amplifier = criticalHitEffect.getAmplifier();
            float critChance = (5 + amplifier * 5) / 100.0f;
            if (attacker.getRandom().nextFloat() < critChance) {
            }
        }
    }

    private static void handleCriticalHitEffect(LivingEntity attacker, LivingHurtEvent event) {
        MobEffectInstance criticalHitEffect = attacker.getEffect(ModEffects.CRITICAL_HIT.get());
        if (criticalHitEffect != null) {
            int amplifier = criticalHitEffect.getAmplifier();
            float critChance = (5 + amplifier * 5) / 100.0f;
            if (attacker.getRandom().nextFloat() < critChance) {
                float originalDamage = event.getAmount();
                float criticalDamage = originalDamage * 3.0f;
                event.setAmount(criticalDamage);
            }
        }
    }

    private static void handleWeakeningHands(LivingEntity attacker, LivingEntity target) {
        MobEffectInstance weakeningHandsEffect = attacker.getEffect(ModEffects.WEAKENING_HANDS.get());
        if (weakeningHandsEffect != null) {
            int amplifier = weakeningHandsEffect.getAmplifier();
            int duration = (5 + 5 * amplifier) * 20;
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier));
        }
    }

    private static void handleChainsEffect(LivingEntity attacker, LivingEntity target) {
        if (attacker.getRandom().nextFloat() < 0.3f) {
            MobEffectInstance chainsEffect = attacker.getEffect(ModEffects.CHAINS.get());
            if (chainsEffect != null) {
                int amplifier = chainsEffect.getAmplifier();
                int duration = amplifier * 20;

                Level level = attacker.level();
                AABB aabb = attacker.getBoundingBox().inflate(8.0D, 4.0D, 8.0D);
                List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, aabb,
                        e -> e != attacker && e != target && e instanceof LivingEntity
                                && (attacker.canAttack(e) || e.canAttack(attacker)));

                int count = 0;
                for (LivingEntity entity : nearbyEntities) {
                    if (count >= 2)
                        break;
                    entity.addEffect(new MobEffectInstance(GoetyEffects.TANGLED.get(), duration, 0));
                    count++;
                }
            }
        }
    }

    private static void handleShockwaveEffect(LivingEntity attacker, LivingEntity target) {
        MobEffectInstance shockwaveEffect = attacker.getEffect(ModEffects.SHOCKWAVE.get());
        if (shockwaveEffect != null) {
            int amplifier = shockwaveEffect.getAmplifier();
            float chance = (10 * amplifier) / 100.0f;
            if (attacker.getRandom().nextFloat() < chance) {
                Level level = attacker.level();
                Vec3 direction;

                AABB searchBox = attacker.getBoundingBox().inflate(16.0D, 8.0D, 16.0D);
                List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                        e -> e != attacker &&
                                (attacker.canAttack(e) || e.canAttack(attacker) ||
                                        e.getLastHurtByMob() == attacker || e.getLastHurtMob() == attacker));

                if (!nearbyEntities.isEmpty()) {
                    LivingEntity closestEnemy = null;
                    double closestDistance = Double.MAX_VALUE;

                    for (LivingEntity entity : nearbyEntities) {
                        double distance = attacker.distanceToSqr(entity);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestEnemy = entity;
                        }
                    }

                    if (closestEnemy != null) {
                        direction = closestEnemy.position().subtract(attacker.position());
                        direction = new Vec3(direction.x, 0, direction.z).normalize();
                    } else {
                        direction = attacker.getLookAngle();
                        direction = new Vec3(direction.x, 0, direction.z).normalize();
                    }
                } else {
                    direction = attacker.getLookAngle();
                    direction = new Vec3(direction.x, 0, direction.z).normalize();
                }

                RazorWind windBlade = new RazorWind(level, attacker);
                windBlade.setPos(attacker.getX(), attacker.getY() + 0.2, attacker.getZ());
                windBlade.slash(direction, 0.3F);
                windBlade.setDamage((float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
                windBlade.setMaxLifeSpan(40);
                windBlade.setRadius(0.5F);
                level.addFreshEntity(windBlade);
            }
        }
    }

    private static void handleThunderingEffect(LivingEntity attacker, LivingEntity target) {
        MobEffectInstance thunderingEffect = attacker.getEffect(ModEffects.ENCHANTMENT_THUNDERING.get());
        if (thunderingEffect != null) {
            int amplifier = thunderingEffect.getAmplifier();
            float chance = (10 * amplifier) / 100.0f;
            if (attacker.getRandom().nextFloat() < chance) {
                Level level = attacker.level();
                BlockPos targetPos = target.blockPosition();
                BlockPos spawnPos = targetPos.offset(
                        level.random.nextInt(7) - 3,
                        0,
                        level.random.nextInt(7) - 3);
                MagicLightningTrap lightningTrap = new MagicLightningTrap(level, spawnPos.getX() + 0.5, spawnPos.getY(),
                        spawnPos.getZ() + 0.5);
                lightningTrap.setOwner(attacker);
                lightningTrap.setDamage((float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
                lightningTrap.setDuration(40);
                lightningTrap.setRadius(1.5F);
                level.addFreshEntity(lightningTrap);
            }
        }
    }

    private static void handleCommittedEffect(LivingEntity attacker, LivingEntity target) {
        MobEffectInstance committedEffect = attacker.getEffect(ModEffects.COMMITTED.get());
        if (committedEffect != null) {
            int amplifier = committedEffect.getAmplifier();
            float targetHealthPercent = target.getHealth() / target.getMaxHealth();
            float damageMultiplier = (1 - targetHealthPercent) * (25 + 25 * amplifier) / 100.0f;
        }
    }

    private static void handleCommittedEffectInEvent(LivingEntity attacker, LivingEntity target,
            LivingHurtEvent event) {
        MobEffectInstance committedEffect = attacker.getEffect(ModEffects.COMMITTED.get());
        if (committedEffect != null) {
            int amplifier = committedEffect.getAmplifier();
            float targetHealthPercent = target.getHealth() / target.getMaxHealth();
            float damageMultiplier = (1 - targetHealthPercent) * (25 + 25 * amplifier) / 100.0f;
            float originalDamage = event.getAmount();
            float newDamage = originalDamage * (1 + damageMultiplier);
            event.setAmount(newDamage);
        }
    }

    private static void handleFrenziedEffect(LivingEntity entity) {
        MobEffectInstance frenziedEffect = entity.getEffect(ModEffects.FRENZIED.get());
        if (frenziedEffect != null) {
            int amplifier = frenziedEffect.getAmplifier();
            if (entity.getHealth() < entity.getMaxHealth() / 2) {
                AttributeModifierManager.applyFrenziedModifier(entity, amplifier);
            } else {
                AttributeModifierManager.removeFrenziedModifier(entity);
            }
        }
    }

    private static void handleRampagingEffect(LivingEntity attacker) {
        if (attacker.getRandom().nextFloat() < 0.1f) {
            MobEffectInstance rampagingEffect = attacker.getEffect(ModEffects.RAMPAGING.get());
            if (rampagingEffect != null) {
                int amplifier = rampagingEffect.getAmplifier();
                int duration = (5 * (amplifier + 1)) * 20;
                AttributeModifierManager.applyRampagingModifier(attacker, amplifier);
                AttributeModifierManager.setRampagingModifierEndTime(attacker, duration);
            }
        }
    }

    private static void handleVisualDisturbanceEffect(LivingEntity attacker, LivingHurtEvent event) {
        MobEffectInstance visualDisturbanceEffect = attacker.getEffect(ModEffects.VISUAL_DISTURBANCE.get());
        if (visualDisturbanceEffect != null) {
            int amplifier = visualDisturbanceEffect.getAmplifier();
            float cancelChance = (10 * (amplifier + 1)) / 100.0f;
            if (attacker.getRandom().nextFloat() < cancelChance) {
                event.setAmount(0.0f);
            }
        }
    }

    private static void handleRecoverEffectOnHeal(LivingEntity entity, LivingHealEvent event) {
        MobEffectInstance recoverEffect = entity.getEffect(ModEffects.RECOVER.get());
        if (recoverEffect != null) {
            int amplifier = recoverEffect.getAmplifier();
            float extraHealAmount = amplifier + 1;
            event.setAmount(event.getAmount() + extraHealAmount);
        }
    }
}