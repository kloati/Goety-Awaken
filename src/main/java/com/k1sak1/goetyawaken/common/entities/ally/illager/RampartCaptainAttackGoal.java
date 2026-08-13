package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class RampartCaptainAttackGoal extends Goal {
    private final RampartCaptain rampartCaptain;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;

    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private long lastCanUseCheck;
    private int failedPathFindingPenalty = 0;

    public RampartCaptainAttackGoal(RampartCaptain rampartCaptain, double speedModifier,
            boolean followingTargetEvenIfNotSeen) {
        this.rampartCaptain = rampartCaptain;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long now = this.rampartCaptain.level().getGameTime();
        if (now - this.lastCanUseCheck < 5L) {
            return false;
        }
        this.lastCanUseCheck = now;

        LivingEntity target = this.rampartCaptain.getTarget();
        if (this.isAnyActionBusy() || target == null || !target.isAlive()) {
            return false;
        }

        this.path = this.rampartCaptain.getNavigation().createPath(target, 0);
        if (this.path != null) {
            return true;
        }
        return this.getAttackReachSqr(target) >= this.rampartCaptain.distanceToSqr(
                target.getX(), target.getY(), target.getZ());
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.isAnyActionBusy()) {
            return false;
        }
        if (!this.followingTargetEvenIfNotSeen) {
            return !this.rampartCaptain.getNavigation().isDone();
        }
        if (!this.rampartCaptain.isWithinRestriction(target.blockPosition())) {
            return false;
        }
        return !(target instanceof Player)
                || !target.isSpectator() && !((Player) target).isCreative();
    }

    @Override
    public void start() {
        if (this.path != null) {
            this.rampartCaptain.getNavigation().moveTo(this.path, this.speedModifier);
        }
        this.rampartCaptain.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.pathedTargetX = 0.0D;
        this.pathedTargetY = 0.0D;
        this.pathedTargetZ = 0.0D;
    }

    @Override
    public void stop() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.rampartCaptain.setTarget(null);
        }
        if (this.rampartCaptain.getTarget() == null) {
            this.rampartCaptain.setAggressive(false);
        }
        this.rampartCaptain.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (target == null) {
            return;
        }

        this.rampartCaptain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distSqr = this.rampartCaptain.distanceToSqr(target.getX(), target.getY(), target.getZ());
        this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);

        boolean hasLineOfSight = this.followingTargetEvenIfNotSeen
                || this.rampartCaptain.getSensing().hasLineOfSight(target);
        boolean needRepath = this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D
                || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D
                || this.rampartCaptain.getRandom().nextFloat() < 0.05F;

        if (hasLineOfSight && this.ticksUntilNextPathRecalculation <= 0 && needRepath) {
            this.pathedTargetX = target.getX();
            this.pathedTargetY = target.getY();
            this.pathedTargetZ = target.getZ();
            this.ticksUntilNextPathRecalculation = 4 + this.rampartCaptain.getRandom().nextInt(7);

            if (distSqr > 1024.0D) {
                this.ticksUntilNextPathRecalculation += 10;
            } else if (distSqr > 256.0D) {
                this.ticksUntilNextPathRecalculation += 5;
            }

            if (!this.rampartCaptain.getNavigation().moveTo(target, this.speedModifier)) {
                this.ticksUntilNextPathRecalculation += 15;
                this.failedPathFindingPenalty += 10;
            } else {
                this.failedPathFindingPenalty = Math.max(0, this.failedPathFindingPenalty - 1);
            }

            this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(
                    this.ticksUntilNextPathRecalculation + this.failedPathFindingPenalty);
        }
    }

    private boolean isAnyActionBusy() {
        return this.rampartCaptain.isBlowingHorn()
                || this.rampartCaptain.isThrowing()
                || this.rampartCaptain.isRunAttacking()
                || this.rampartCaptain.isIceAxeAttacking;
    }

    protected double getAttackReachSqr(LivingEntity target) {
        return (double) (this.rampartCaptain.getBbWidth() * 2.0F * this.rampartCaptain.getBbWidth() * 2.0F
                + target.getBbWidth());
    }
}
