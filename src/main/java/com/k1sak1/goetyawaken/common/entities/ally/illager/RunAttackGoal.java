package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.List;

public class RunAttackGoal extends Goal {
    private final RampartCaptain rampartCaptain;
    private int phase = 0;
    private int runDuration = 0;
    private static final double RUN_SPEED_MULTIPLIER = 1.25D;
    private boolean shouldLoop = false;

    public RunAttackGoal(RampartCaptain rampartCaptain) {
        this.rampartCaptain = rampartCaptain;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (this.rampartCaptain.getRunAttackCooldown() > 0 ||
                this.rampartCaptain.isIceAxeAttacking ||
                this.rampartCaptain.isBlowingHorn() ||
                this.rampartCaptain.isThrowing() ||
                this.rampartCaptain.isRunAttacking() ||
                target == null || !target.isAlive()) {
            return false;
        }

        long timeSinceLastDamage = this.rampartCaptain.level().getGameTime() - this.rampartCaptain.getLastDamageTime();
        double distanceToTarget = this.rampartCaptain.distanceTo(target);
        return timeSinceLastDamage > 320 || distanceToTarget > 15.0D;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.phase == 1) {
            return this.runDuration < 500;
        }

        if (this.phase == 2) {
            return this.rampartCaptain.getRunAttackTick() > 0;
        }

        return false;
    }

    @Override
    public void start() {
        this.phase = 1;
        this.runDuration = 0;
        this.shouldLoop = false;
        this.rampartCaptain.setIsRun(true);
        this.rampartCaptain.setIsRunAttacking(true);
        this.rampartCaptain.setRunAttackTick(200);
        this.rampartCaptain.triggerAnimation(RampartCaptain.RUN);
        this.rampartCaptain.getNavigation().moveTo(this.rampartCaptain.getTarget(), RUN_SPEED_MULTIPLIER);
    }

    @Override
    public void tick() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (target == null) {
            return;
        }

        this.rampartCaptain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.runDuration++;
        if (this.phase == 1) {
            double distanceToTarget = this.rampartCaptain.distanceTo(target);
            this.rampartCaptain.getNavigation().moveTo(target, RUN_SPEED_MULTIPLIER);
            if (distanceToTarget <= 2.0D || this.runDuration >= 500) {
                this.startPhase2();
            }
        }
        if (this.phase == 2) {
            this.rampartCaptain.getNavigation().stop();
            if (this.rampartCaptain.getRunAttackTick() == 17) {
                this.performSweepAttack();
            }
            if (this.rampartCaptain.getRunAttackTick() <= 10 && this.shouldLoop) {
                this.loopToPhase1();
            }
        }
    }

    @Override
    public void stop() {
        this.rampartCaptain.setRunAttackCooldown(300);
        this.rampartCaptain.setIsRunAttacking(false);
        this.rampartCaptain.setIsRun(false);
        this.phase = 0;
        this.runDuration = 0;
        this.shouldLoop = false;
        this.rampartCaptain.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void startPhase2() {
        this.phase = 2;
        this.rampartCaptain.setRunAttackTick(28);
        this.rampartCaptain.setIsRun(false);
        this.rampartCaptain.triggerAnimation(RampartCaptain.RUNATTACK);
        this.rampartCaptain.getNavigation().stop();
        this.rampartCaptain.playSound(com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_SHOUT.get(), 2.0F, 1.0F);
        this.shouldLoop = this.rampartCaptain.getRandom()
                .nextFloat() < AttributesConfig.RampartCaptainRunAttackLoopChance.get();
    }

    private void loopToPhase1() {
        this.phase = 1;
        this.runDuration = 0;
        this.rampartCaptain.setIsRun(true);
        this.rampartCaptain.setRunAttackTick(200);
        this.rampartCaptain.triggerAnimation(RampartCaptain.RUN);
        this.shouldLoop = false;
    }

    private void performSweepAttack() {
        Level level = this.rampartCaptain.level();
        if (level.isClientSide) {
            return;
        }

        LivingEntity target = this.rampartCaptain.getTarget();
        double radius = 3.0D;
        if (target != null && target.isAlive() && !MobUtil.areAllies(this.rampartCaptain, target)) {
            double distToTarget = this.rampartCaptain.distanceTo(target);
            if (distToTarget <= 3.0D) {
                this.rampartCaptain.doHurtTarget(target);
                this.rampartCaptain.setLastDamageTime(this.rampartCaptain.level().getGameTime());
            }
        }

        AABB searchBox = this.rampartCaptain.getBoundingBox().inflate(radius, 1.0D, radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class, searchBox,
                entity -> entity.isAlive() && !MobUtil.areAllies(this.rampartCaptain, entity)
                        && entity != this.rampartCaptain);

        float captainAngle = this.rampartCaptain.yBodyRot % 360;
        if (captainAngle < 0) {
            captainAngle += 360;
        }

        for (LivingEntity entity : entities) {
            if (entity == target) {
                continue;
            }

            double dX = entity.getX() - this.rampartCaptain.getX();
            double dZ = entity.getZ() - this.rampartCaptain.getZ();
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
                this.rampartCaptain.doHurtTarget(entity);
                this.rampartCaptain.setLastDamageTime(this.rampartCaptain.level().getGameTime());
            }
        }
    }
}
