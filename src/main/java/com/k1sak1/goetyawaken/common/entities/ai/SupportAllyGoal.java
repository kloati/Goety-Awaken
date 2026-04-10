package com.k1sak1.goetyawaken.common.entities.ai;

import com.Polarice3.Goety.common.effects.brew.PurifyBrewEffect;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.entities.ally.illager.CroneServant;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class SupportAllyGoal extends Goal {
    private final CroneServant crone;
    private int supportAllyCooldown = 0;
    private static final int COOLDOWN_PERIOD = 60;

    public SupportAllyGoal(CroneServant crone) {
        this.crone = crone;
    }

    @Override
    public boolean canUse() {
        if (supportAllyCooldown > 0) {
            return false;
        }
        return findSupportTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        LivingEntity supportTarget = findSupportTarget();
        if (supportTarget != null) {
            ItemStack brew = createSupportBrew(supportTarget);
            if (!brew.isEmpty()) {
                throwBrewAtTarget(supportTarget, brew);
                supportAllyCooldown = COOLDOWN_PERIOD;
            }
        }
    }

    private void throwBrewAtTarget(LivingEntity target, ItemStack brew) {
        com.k1sak1.goetyawaken.common.entities.projectiles.CroneServantBrew thrownBrew = new com.k1sak1.goetyawaken.common.entities.projectiles.CroneServantBrew(
                crone.level(), crone);
        ItemStack splashBrew = new ItemStack(com.Polarice3.Goety.common.items.ModItems.SPLASH_BREW.get());
        ItemStack finalBrew = com.Polarice3.Goety.utils.BrewUtils.setCustomEffects(splashBrew,
                net.minecraft.world.item.alchemy.PotionUtils.getMobEffects(brew),
                com.Polarice3.Goety.utils.BrewUtils.getBrewEffects(brew));
        finalBrew.getOrCreateTag().putInt("CustomPotionColor", brew.getOrCreateTag().getInt("CustomPotionColor"));
        thrownBrew.setItem(finalBrew);

        Vec3 vec3 = target.getDeltaMovement();
        double d0 = target.getX() + vec3.x - crone.getX();
        double d1 = target.getEyeY() - (double) 1.1F - crone.getY();
        double d2 = target.getZ() + vec3.z - crone.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        float velocity = 0.75F;
        if (target.distanceTo(crone) >= 4.0F) {
            thrownBrew.setXRot(thrownBrew.getXRot() + 20.0F);
        } else {
            thrownBrew.setXRot(thrownBrew.getXRot());
            velocity = 1.0F;
        }
        thrownBrew.shoot(d0, d1 + d3 * 0.2D, d2, velocity, 8.0F);

        if (!crone.isSilent()) {
            crone.level().playSound((Player) null, crone.getX(), crone.getY(), crone.getZ(),
                    net.minecraft.sounds.SoundEvents.WITCH_THROW,
                    crone.getSoundSource(), 1.0F, 0.8F + crone.getRandom().nextFloat() * 0.4F);
        }

        crone.level().addFreshEntity(thrownBrew);
    }

    public int getCooldown() {
        return supportAllyCooldown;
    }

    public void decrementCooldown() {
        if (supportAllyCooldown > 0) {
            supportAllyCooldown--;
        }
    }

    private LivingEntity findSupportTarget() {
        Level level = crone.level();
        List<LivingEntity> allies = level.getEntitiesOfClass(
                LivingEntity.class,
                crone.getBoundingBox().inflate(64.0D),
                entity -> MobUtil.areAllies(crone, entity) &&
                        isValidAlly(entity));

        for (LivingEntity ally : allies) {
            if (hasSevereNegativeEffects(ally)) {
                return ally;
            }
        }

        for (LivingEntity ally : allies) {
            if (ally.isOnFire()) {
                return ally;
            }
        }

        for (LivingEntity ally : allies) {
            if (isInCombat(ally)) {
                return ally;
            }
        }

        for (LivingEntity ally : allies) {
            if (needsHealing(ally)) {
                return ally;
            }
        }

        return null;
    }

    private boolean isValidAlly(LivingEntity entity) {
        return entity.getType() != com.Polarice3.Goety.common.entities.ModEntityType.VEX_SERVANT.get()
                && entity.getType() != com.Polarice3.Goety.common.entities.ModEntityType.IRK_SERVANT.get()
                && entity.getType() != com.k1sak1.goetyawaken.common.entities.ModEntityType.TORMENTOR_SERVANT.get();
    }

    private boolean hasSevereNegativeEffects(LivingEntity ally) {
        int negativeEffectCount = 0;
        int maxAmplifier = 0;

        for (MobEffectInstance effect : ally.getActiveEffects()) {
            if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                if (effect.getDuration() > 20 * 20) {
                    return true;
                }

                if (effect.getAmplifier() > 1) {
                    return true;
                }

                negativeEffectCount++;
                maxAmplifier = Math.max(maxAmplifier, effect.getAmplifier());
            }
        }

        return negativeEffectCount > 2 || maxAmplifier > 1;
    }

    private boolean isInCombat(LivingEntity ally) {
        if (ally instanceof Mob mob) {
            return mob.getTarget() != null;
        }
        return false;
    }

    private boolean needsHealing(LivingEntity ally) {
        return ally.getHealth() < ally.getMaxHealth() * 0.8F;
    }

    private ItemStack createSupportBrew(LivingEntity target) {
        List<MobEffectInstance> mobEffects = new ArrayList<>();
        List<com.Polarice3.Goety.common.effects.brew.BrewEffectInstance> brewEffects = new ArrayList<>();
        if (hasSevereNegativeEffects(target)) {
            com.Polarice3.Goety.common.effects.brew.BrewEffectInstance purifyEffect = new com.Polarice3.Goety.common.effects.brew.BrewEffectInstance(
                    new PurifyBrewEffect("purify_debuff", 0, 0, MobEffectCategory.BENEFICIAL, 0x385858, true));
            brewEffects.add(purifyEffect);
        } else if (target.isOnFire()) {
            mobEffects.add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000));
        } else if (isInCombat(target)) {

            List<MobEffect> combatSupportEffects = Arrays.asList(
                    MobEffects.DAMAGE_BOOST,
                    MobEffects.DAMAGE_RESISTANCE,
                    MobEffects.MOVEMENT_SPEED,
                    MobEffects.REGENERATION,
                    MobEffects.ABSORPTION,
                    com.k1sak1.goetyawaken.init.ModEffects.WEAKENING_HANDS.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.DEFLECTIVE.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.VENOMOUS_HANDS.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.RADIANCE.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.RALLYING.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.SHIELDING.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.PHOTOSYNTHESIS.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.FLAME_HANDS.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.CLIMBING.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.SWIRLING.get(),
                    com.Polarice3.Goety.common.effects.GoetyEffects.LEECHING.get());

            Random random = new Random();
            MobEffect selectedEffect = combatSupportEffects.get(random.nextInt(combatSupportEffects.size()));
            int amplifier = random.nextInt(3);
            int duration = 600 + random.nextInt(900);

            mobEffects.add(new MobEffectInstance(selectedEffect, duration, amplifier));
        } else if (needsHealing(target)) {
            if (isUndeadEntity(target)) {
                mobEffects.add(new MobEffectInstance(MobEffects.HARM, 1, getRandomAmplifier()));
            } else {
                List<MobEffect> healingEffects = Arrays.asList(
                        MobEffects.HEAL,
                        MobEffects.REGENERATION);

                Random random = new Random();
                MobEffect selectedEffect = healingEffects.get(random.nextInt(healingEffects.size()));
                int amplifier = getRandomAmplifier();
                int duration = selectedEffect == MobEffects.REGENERATION ? (600 + random.nextInt(1200)) : 1;

                mobEffects.add(new MobEffectInstance(selectedEffect, duration, amplifier));
            }
        }

        if (!mobEffects.isEmpty() || !brewEffects.isEmpty()) {
            ItemStack brew = BrewUtils.setCustomEffects(new ItemStack(ModItems.SPLASH_BREW.get()), mobEffects,
                    brewEffects);
            int areaLevel = crone.level().random.nextInt(3);
            BrewUtils.setAreaOfEffect(brew, areaLevel);
            brew.getOrCreateTag().putInt("CustomPotionColor", BrewUtils.getColor(mobEffects, brewEffects));
            return brew;
        }

        return ItemStack.EMPTY;
    }

    private boolean isUndeadEntity(LivingEntity entity) {
        return entity.getMobType() == net.minecraft.world.entity.MobType.UNDEAD;
    }

    private int getRandomAmplifier() {
        Random random = new Random();
        double roll = random.nextDouble();
        if (roll < 0.5)
            return 0;
        else if (roll < 0.8)
            return 1;
        else
            return 2;
    }
}