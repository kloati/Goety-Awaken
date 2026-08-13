package com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRampartCaptain;

import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.List;

public class RunAttackGoal extends Goal {
    private final HostileRampartCaptain hostileRampartCaptain;
    private int phase = 0;
    private int runDuration = 0;
    private static final double RUN_SPEED_MULTIPLIER = 1.25D;
    private boolean shouldLoop = false;

    public RunAttackGoal(HostileRampartCaptain hostileRampartCaptain) {
        this.hostileRampartCaptain = hostileRampartCaptain;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (this.hostileRampartCaptain.getRunAttackCooldown() > 0 ||
                this.hostileRampartCaptain.isIceAxeAttacking ||
                this.hostileRampartCaptain.isBlowingHorn() ||
                this.hostileRampartCaptain.isThrowing() ||
                this.hostileRampartCaptain.isRunAttacking() ||
                target == null || !target.isAlive() ||
                target == this.hostileRampartCaptain) {
            return false;
        }

        long timeSinceLastDamage = this.hostileRampartCaptain.level().getGameTime()
                - this.hostileRampartCaptain.getLastDamageTime();
        double distanceToTarget = this.hostileRampartCaptain.distanceTo(target);
        return timeSinceLastDamage > 320 || distanceToTarget > 15.0D;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.phase == 1) {
            return this.runDuration < 500;
        }

        if (this.phase == 2) {
            return this.hostileRampartCaptain.getRunAttackTick() > 0;
        }

        return false;
    }

    @Override
    public void start() {
        this.phase = 1;
        this.runDuration = 0;
        this.shouldLoop = false;
        this.hostileRampartCaptain.setIsRun(true);
        this.hostileRampartCaptain.setIsRunAttacking(true);
        this.hostileRampartCaptain.setRunAttackTick(200);
        this.hostileRampartCaptain.triggerAnimation(HostileRampartCaptain.RUN);
        this.hostileRampartCaptain.getNavigation().moveTo(this.hostileRampartCaptain.getTarget(), RUN_SPEED_MULTIPLIER);
    }

    @Override
    public void tick() {
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (target == null) {
            return;
        }

        this.hostileRampartCaptain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.runDuration++;
        if (this.phase == 1) {
            double distanceToTarget = this.hostileRampartCaptain.distanceTo(target);
            this.hostileRampartCaptain.getNavigation().moveTo(target, RUN_SPEED_MULTIPLIER);
            if (distanceToTarget <= 2.0D || this.runDuration >= 500) {
                this.startPhase2();
            }
        }
        if (this.phase == 2) {
            this.hostileRampartCaptain.getNavigation().stop();
            if (this.hostileRampartCaptain.getRunAttackTick() == 17) {
                this.performSweepAttack();
            }
            if (this.hostileRampartCaptain.getRunAttackTick() <= 10 && this.shouldLoop) {
                this.loopToPhase1();
            }
        }
    }

    @Override
    public void stop() {
        this.hostileRampartCaptain.setRunAttackCooldown(300);
        this.hostileRampartCaptain.setIsRunAttacking(false);
        this.hostileRampartCaptain.setIsRun(false);
        this.phase = 0;
        this.runDuration = 0;
        this.shouldLoop = false;
        this.hostileRampartCaptain.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void startPhase2() {
        this.phase = 2;
        this.hostileRampartCaptain.setRunAttackTick(28);
        this.hostileRampartCaptain.setIsRun(false);
        this.hostileRampartCaptain.triggerAnimation(HostileRampartCaptain.RUNATTACK);
        this.hostileRampartCaptain.getNavigation().stop();
        this.hostileRampartCaptain.playSound(com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_SHOUT.get(), 2.0F,
                1.0F);
        this.shouldLoop = this.hostileRampartCaptain.getRandom()
                .nextFloat() < AttributesConfig.RampartCaptainRunAttackLoopChance.get();
    }

    private void loopToPhase1() {
        this.phase = 1;
        this.runDuration = 0;
        this.hostileRampartCaptain.setIsRun(true);
        this.hostileRampartCaptain.setRunAttackTick(200);
        this.hostileRampartCaptain.triggerAnimation(HostileRampartCaptain.RUN);
        this.shouldLoop = false;
    }

    private void performSweepAttack() {
        Level level = this.hostileRampartCaptain.level();
        if (level.isClientSide) {
            return;
        }

        LivingEntity target = this.hostileRampartCaptain.getTarget();
        double radius = 3.0D;
        if (target != null && target.isAlive() && !MobUtil.areAllies(this.hostileRampartCaptain, target)) {
            double distToTarget = this.hostileRampartCaptain.distanceTo(target);
            if (distToTarget <= 3.0D) {
                this.hostileRampartCaptain.doHurtTarget(target);
                this.hostileRampartCaptain.setLastDamageTime(this.hostileRampartCaptain.level().getGameTime());
            }
        }

        AABB searchBox = this.hostileRampartCaptain.getBoundingBox().inflate(radius, 1.0D, radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class, searchBox,
                entity -> entity.isAlive() && !MobUtil.areAllies(this.hostileRampartCaptain, entity)
                        && entity != this.hostileRampartCaptain);

        float captainAngle = this.hostileRampartCaptain.yBodyRot % 360;
        if (captainAngle < 0) {
            captainAngle += 360;
        }

        for (LivingEntity entity : entities) {
            if (entity == target) {
                continue;
            }

            double dX = entity.getX() - this.hostileRampartCaptain.getX();
            double dZ = entity.getZ() - this.hostileRampartCaptain.getZ();
            float entityAngle = (float) (Math.atan2(dZ, dX) * (180.0D / Math.PI) - 90.0D);
            entityAngle = entityAngle % 360;
            if (entityAngle < 0) {
                entityAngle += 360;
            }
            float relativeAngle = entityAngle - captainAngle;
            if (relativeAngle > 180) {
                relativeAngle -= 360;
            }
            if (relativeAngle < -180) {
                relativeAngle += 360;
            }

            float distance = (float) Math.sqrt(dX * dX + dZ * dZ);
            if (distance <= radius && relativeAngle >= -180.0F && relativeAngle <= 45.0F) {
                this.hostileRampartCaptain.doHurtTarget(entity);
                this.hostileRampartCaptain.setLastDamageTime(this.hostileRampartCaptain.level().getGameTime());
            }
        }
    }
}
