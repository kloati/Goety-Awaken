package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.wind.WindHornSpell;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class WindHornGoal extends Goal {
    private final RampartCaptain rampartCaptain;
    private int variant;
    private boolean isBlowingHorn = false;

    public WindHornGoal(RampartCaptain rampartCaptain) {
        this.rampartCaptain = rampartCaptain;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.rampartCaptain.getWindHornCooldown() > 0) {
            return false;
        }
        if (this.rampartCaptain.isIceAxeAttacking || this.rampartCaptain.isBlowingHorn()
                || this.rampartCaptain.isRunAttacking() || this.rampartCaptain.isThrowing()) {
            return false;
        }

        LivingEntity target = this.rampartCaptain.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        boolean canUseVariant1 = this.rampartCaptain.distanceToSqr(target) <= 64.0D;
        boolean canUseVariant2 = false;
        List<AbstractIllagerServant> nearbyAllies = this.rampartCaptain.level().getEntitiesOfClass(
                AbstractIllagerServant.class,
                this.rampartCaptain.getBoundingBox().inflate(24.0D),
                ally -> ally != this.rampartCaptain && MobUtil.areAllies(this.rampartCaptain, ally));
        if (!nearbyAllies.isEmpty()) {
            canUseVariant2 = true;
        }
        if (canUseVariant1 && canUseVariant2) {
            this.variant = this.rampartCaptain.getRandom().nextInt(2) + 1;
            return true;
        } else if (canUseVariant1) {
            this.variant = 1;
            return true;
        } else if (canUseVariant2) {
            this.variant = 2;
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.rampartCaptain.getWindHornTick() > 0;
    }

    @Override
    public void start() {
        this.isBlowingHorn = true;
        this.rampartCaptain.setIsBlowingHorn(true);

        if (this.variant == 1) {
            this.rampartCaptain.setWindHornTick(25);
            this.rampartCaptain.triggerAnimation(RampartCaptain.WINDHORN);
        } else {
            this.rampartCaptain.setWindHornTick(35);
            this.rampartCaptain.triggerAnimation(RampartCaptain.SUMMON);
        }

        this.rampartCaptain.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.variant == 1 && this.rampartCaptain.getWindHornTick() == 11) {
            this.performWindHornAttack();
        }
        if (this.variant == 1 && this.rampartCaptain.getWindHornTick() == 15) {
            this.playHornSound();
        }
        if (this.variant == 2 && this.rampartCaptain.getWindHornTick() == 6) {
            this.performSummonBuff();
        }
        if (this.variant == 2 && this.rampartCaptain.getWindHornTick() == 25) {
            this.playHornSound();
        }
    }

    @Override
    public void stop() {
        this.rampartCaptain.setWindHornCooldown(400);
        this.rampartCaptain.setIsBlowingHorn(false);
        this.isBlowingHorn = false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void performWindHornAttack() {
        if (this.rampartCaptain.level() instanceof ServerLevel serverLevel) {
            WindHornSpell windHornSpell = new WindHornSpell();
            SpellStat spellStat = new SpellStat(0, 0, 0, 0.0D, 0, 0.0F);
            spellStat.setPotency(4).setRadius(5.0D).setDuration(5);
            windHornSpell.SpellResult(serverLevel, this.rampartCaptain, ItemStack.EMPTY, spellStat);
        }
    }

    private void performSummonBuff() {
        if (this.rampartCaptain.level() instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(0xffffff);
            serverLevel.sendParticles(
                    new SmashParticleOption(colorUtil, 12.0F, 2.0F, 25),
                    this.rampartCaptain.getX(),
                    this.rampartCaptain.getY() + 1.0D,
                    this.rampartCaptain.getZ(),
                    1, 0, 0, 0, 0);
            List<AbstractIllagerServant> nearbyAllies = serverLevel.getEntitiesOfClass(
                    AbstractIllagerServant.class,
                    this.rampartCaptain.getBoundingBox().inflate(24.0D),
                    ally -> ally != this.rampartCaptain && MobUtil.areAllies(this.rampartCaptain, ally));

            MobEffect chosenEffect = this.rampartCaptain.getRandom().nextBoolean()
                    ? MobEffects.MOVEMENT_SPEED
                    : MobEffects.DAMAGE_BOOST;

            for (AbstractIllagerServant ally : nearbyAllies) {
                ally.addEffect(new MobEffectInstance(chosenEffect, 400, 0, false, false));
                if (ally instanceof com.Polarice3.Goety.common.entities.ally.illager.WindCallerServant ||
                        ally instanceof com.Polarice3.Goety.common.entities.ally.illager.MountaineerServant ||
                        ally instanceof com.Polarice3.Goety.common.entities.ally.illager.StormCasterServant) {
                    ally.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1, false, false));
                }
                ServerParticleUtil.summonUndeadParticles(
                        serverLevel,
                        ally,
                        new ColorUtil(0xffffff),
                        0xffffff,
                        0xffffff);
            }
        }
    }

    private void playHornSound() {
        this.rampartCaptain.level().playSound(
                null,
                this.rampartCaptain.getX(),
                this.rampartCaptain.getY(),
                this.rampartCaptain.getZ(),
                net.minecraft.sounds.SoundEvents.GOAT_HORN_PLAY,
                this.rampartCaptain.getSoundSource(),
                2.0F,
                1.0F);
    }
}
