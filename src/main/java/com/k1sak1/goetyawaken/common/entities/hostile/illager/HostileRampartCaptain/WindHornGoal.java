package com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRampartCaptain;

import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.wind.WindHornSpell;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class WindHornGoal extends Goal {
    private final HostileRampartCaptain hostileRampartCaptain;
    private int variant;
    private boolean isBlowingHorn = false;

    public WindHornGoal(HostileRampartCaptain hostileRampartCaptain) {
        this.hostileRampartCaptain = hostileRampartCaptain;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.hostileRampartCaptain.getWindHornCooldown() > 0) {
            return false;
        }
        if (this.hostileRampartCaptain.isIceAxeAttacking || this.hostileRampartCaptain.isBlowingHorn()
                || this.hostileRampartCaptain.isRunAttacking() || this.hostileRampartCaptain.isThrowing()) {
            return false;
        }

        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (target == null || !target.isAlive() || target == this.hostileRampartCaptain) {
            return false;
        }
        boolean canUseVariant1 = this.hostileRampartCaptain.distanceToSqr(target) <= 64.0D;
        boolean canUseVariant2 = false;
        List<AbstractIllager> nearbyAllies = this.hostileRampartCaptain.level().getEntitiesOfClass(
                AbstractIllager.class,
                this.hostileRampartCaptain.getBoundingBox().inflate(24.0D),
                ally -> ally != this.hostileRampartCaptain);
        if (!nearbyAllies.isEmpty()) {
            canUseVariant2 = true;
        }
        if (canUseVariant1 && canUseVariant2) {
            this.variant = this.hostileRampartCaptain.getRandom().nextInt(2) + 1;
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
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.hostileRampartCaptain.getWindHornTick() > 0;
    }

    @Override
    public void start() {
        this.isBlowingHorn = true;
        this.hostileRampartCaptain.setIsBlowingHorn(true);

        if (this.variant == 1) {
            this.hostileRampartCaptain.setWindHornTick(25);
            this.hostileRampartCaptain.triggerAnimation(HostileRampartCaptain.WINDHORN);
        } else {
            this.hostileRampartCaptain.setWindHornTick(35);
            this.hostileRampartCaptain.triggerAnimation(HostileRampartCaptain.SUMMON);
        }

        this.hostileRampartCaptain.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.variant == 1 && this.hostileRampartCaptain.getWindHornTick() == 11) {
            this.performWindHornAttack();
        }
        if (this.variant == 1 && this.hostileRampartCaptain.getWindHornTick() == 15) {
            this.playHornSound();
        }
        if (this.variant == 2 && this.hostileRampartCaptain.getWindHornTick() == 6) {
            this.performSummonBuff();
        }
        if (this.variant == 2 && this.hostileRampartCaptain.getWindHornTick() == 25) {
            this.playHornSound();
        }
    }

    @Override
    public void stop() {
        this.hostileRampartCaptain.setWindHornCooldown(400);
        this.hostileRampartCaptain.setIsBlowingHorn(false);
        this.isBlowingHorn = false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void performWindHornAttack() {
        if (this.hostileRampartCaptain.level() instanceof ServerLevel serverLevel) {
            WindHornSpell windHornSpell = new WindHornSpell();
            SpellStat spellStat = new SpellStat(0, 0, 0, 0.0D, 0, 0.0F);
            spellStat.setPotency(4).setRadius(5.0D).setDuration(5);
            windHornSpell.SpellResult(serverLevel, this.hostileRampartCaptain, ItemStack.EMPTY, spellStat);
        }
    }

    private void performSummonBuff() {
        if (this.hostileRampartCaptain.level() instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(0xffffff);
            serverLevel.sendParticles(
                    new SmashParticleOption(colorUtil, 12.0F, 2.0F, 25),
                    this.hostileRampartCaptain.getX(),
                    this.hostileRampartCaptain.getY() + 1.0D,
                    this.hostileRampartCaptain.getZ(),
                    1, 0, 0, 0, 0);

            List<AbstractIllager> nearbyAllies = serverLevel.getEntitiesOfClass(
                    AbstractIllager.class,
                    this.hostileRampartCaptain.getBoundingBox().inflate(32.0D, 16.0D, 32.0D),
                    ally -> ally != this.hostileRampartCaptain);

            if (this.hostileRampartCaptain.getTarget() != null && this.hostileRampartCaptain.getTarget().isAlive()) {
                LivingEntity captainTarget = this.hostileRampartCaptain.getTarget();
                for (AbstractIllager ally : nearbyAllies) {
                    if (ally.isAlive() && ally.getTarget() != captainTarget) {
                        ally.setTarget(captainTarget);
                    }
                }
            }

            List<AbstractIllagerServant> hostileServants = serverLevel.getEntitiesOfClass(
                    AbstractIllagerServant.class,
                    this.hostileRampartCaptain.getBoundingBox().inflate(24.0D),
                    servant -> !servant.equals(this.hostileRampartCaptain) && servant.isHostile());

            List<AbstractIllager> huntingIllagers = serverLevel.getEntitiesOfClass(
                    AbstractIllager.class,
                    this.hostileRampartCaptain.getBoundingBox().inflate(24.0D),
                    hunting -> !hunting.equals(this.hostileRampartCaptain));

            MobEffect chosenEffect = this.hostileRampartCaptain.getRandom().nextBoolean()
                    ? MobEffects.MOVEMENT_SPEED
                    : MobEffects.DAMAGE_BOOST;

            for (AbstractIllagerServant servant : hostileServants) {
                servant.addEffect(new MobEffectInstance(chosenEffect, 400, 0, false, false));
                servant.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1, false, false));
                ServerParticleUtil.summonUndeadParticles(
                        serverLevel,
                        servant,
                        new ColorUtil(0xffffff),
                        0xffffff,
                        0xffffff);
            }

            for (AbstractIllager hunting : huntingIllagers) {
                hunting.addEffect(new MobEffectInstance(chosenEffect, 400, 0, false, false));
                hunting.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1, false, false));
                ServerParticleUtil.summonUndeadParticles(
                        serverLevel,
                        hunting,
                        new ColorUtil(0xffffff),
                        0xffffff,
                        0xffffff);
            }
        }
    }

    private void playHornSound() {
        this.hostileRampartCaptain.level().playSound(
                null,
                this.hostileRampartCaptain.getX(),
                this.hostileRampartCaptain.getY(),
                this.hostileRampartCaptain.getZ(),
                net.minecraft.sounds.SoundEvents.GOAT_HORN_PLAY,
                this.hostileRampartCaptain.getSoundSource(),
                2.0F,
                1.0F);
    }
}
