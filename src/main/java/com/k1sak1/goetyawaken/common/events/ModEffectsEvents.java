package com.k1sak1.goetyawaken.common.events;

import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.utils.AttributeModifierManager;
import com.Polarice3.Goety.common.entities.projectiles.RazorWind;
import com.Polarice3.Goety.common.entities.util.MagicLightningTrap;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.k1sak1.goetyawaken.common.entities.projectiles.EchoingStrikeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEffectsEvents {

    private static final UUID SHARPNESS_MODIFIER_UUID = UUID.fromString("5D6F0BA2-1186-46AC-B896-C12AE9BD4B65");

    private static final String TAG_POTENT_VENOM_DOWNGRADE = "ga:potent_venom_downgrade";
    private static final String TAG_POTENT_VENOM_DOWNGRADE_LEVEL = "ga:potent_venom_downgrade_level";

    public static final String ECHO_DAMAGE_MARKER = "echo_damage";

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
        AttributeModifierManager.removeRampagingModifier(entity);
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

        handlePotentVenomDowngrade(entity);
    }

    @SubscribeEvent
    public static void onPotentVenomAdded(MobEffectEvent.Added event) {
        if (!ModEffects.POTENT_VENOM.isPresent()) {
            return;
        }
        if (event.getEffectInstance().getEffect() != ModEffects.POTENT_VENOM.get()) {
            return;
        }

        LivingEntity entity = event.getEntity();
        MobEffectInstance newInstance = event.getEffectInstance();
        MobEffectInstance existing = entity.getEffect(ModEffects.POTENT_VENOM.get());

        if (existing != null && existing != newInstance) {
            int existingLevel = existing.getAmplifier();
            if (existingLevel < 2 && entity.getRandom().nextFloat() < 0.25F) {
                int newLevel = existingLevel + 1;
                int currentDuration = existing.getDuration();
                int minDuration = 100;
                int duration = Math.max(currentDuration, minDuration);
                entity.removeEffect(ModEffects.POTENT_VENOM.get());
                entity.addEffect(new MobEffectInstance(ModEffects.POTENT_VENOM.get(),
                        duration, newLevel,
                        existing.isAmbient(), existing.isVisible(), existing.showIcon()));
            } else {
                int currentDuration = existing.getDuration();
                entity.removeEffect(ModEffects.POTENT_VENOM.get());
                entity.addEffect(new MobEffectInstance(ModEffects.POTENT_VENOM.get(),
                        currentDuration, existingLevel,
                        existing.isAmbient(), existing.isVisible(), existing.showIcon()));
            }
        }
    }

    @SubscribeEvent
    public static void onPotentVenomExpired(MobEffectEvent.Expired event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null) {
            return;
        }
        if (!ModEffects.POTENT_VENOM.isPresent()) {
            return;
        }
        if (instance.getEffect() != ModEffects.POTENT_VENOM.get()) {
            return;
        }

        LivingEntity entity = event.getEntity();
        if (entity == null) {
            return;
        }

        int level = instance.getAmplifier() + 1;
        float damage = 3.0F * level * level;
        entity.hurt(entity.damageSources().magic(), damage);
        if (level > 1) {
            entity.getPersistentData().putBoolean(TAG_POTENT_VENOM_DOWNGRADE, true);
            entity.getPersistentData().putInt(TAG_POTENT_VENOM_DOWNGRADE_LEVEL, level - 2);
        }
    }

    @SubscribeEvent
    public static void onEchoHurt(LivingHurtEvent event) {
        if (event.getSource().getMsgId().equals(ECHO_DAMAGE_MARKER)) {
            return;
        }

        if (event.getSource().getEntity() instanceof LivingEntity attacker &&
                (event.getSource().getDirectEntity() == attacker
                        || event.getSource().getDirectEntity() instanceof AbstractArrow)) {

            MobEffectInstance effect = attacker.getEffect(ModEffects.ECHO.get());
            if (effect != null) {
                float echoDamage = event.getAmount() * 0.2f * (effect.getAmplifier() + 1);
                EchoingStrikeEntity echo = new EchoingStrikeEntity(attacker.level(), attacker, echoDamage, 3.0f);
                echo.setOriginalDamageSource(event.getSource());
                echo.setPos(event.getEntity().getBoundingBox().getCenter().subtract(0, echo.getBbHeight() * .5f, 0));
                attacker.level().addFreshEntity(echo);
            }
        }
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

    private static void handlePotentVenomDowngrade(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }

        if (!entity.isAlive()) {
            CompoundTag data = entity.getPersistentData();
            data.remove(TAG_POTENT_VENOM_DOWNGRADE);
            return;
        }

        if (!ModEffects.POTENT_VENOM.isPresent()) {
            return;
        }

        CompoundTag data = entity.getPersistentData();
        if (data.getBoolean(TAG_POTENT_VENOM_DOWNGRADE)) {
            data.remove(TAG_POTENT_VENOM_DOWNGRADE);
            int downgradeLevel = data.getInt(TAG_POTENT_VENOM_DOWNGRADE_LEVEL);
            entity.addEffect(new MobEffectInstance(
                    ModEffects.POTENT_VENOM.get(),
                    100,
                    downgradeLevel,
                    false,
                    true,
                    true));
        }
    }
}
