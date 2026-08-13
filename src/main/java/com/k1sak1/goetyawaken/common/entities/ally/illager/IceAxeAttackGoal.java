package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.List;

public class IceAxeAttackGoal extends Goal {
    private final RampartCaptain rampartCaptain;
    private int attackVariant;

    public IceAxeAttackGoal(RampartCaptain rampartCaptain) {
        this.rampartCaptain = rampartCaptain;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (this.rampartCaptain.isBlowingHorn() || this.rampartCaptain.isRunAttacking() ||
                this.rampartCaptain.isThrowing() || target == null || !target.isAlive()) {
            return false;
        }
        return this.rampartCaptain.distanceToSqr(target) <= 4.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return this.rampartCaptain.attackTick > 0;
    }

    @Override
    public void start() {
        this.attackVariant = this.rampartCaptain.getRandom().nextInt(2) + 1;
        this.rampartCaptain.playSound(com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_SHOUT.get(), 2.0F, 1.0F);
        if (this.attackVariant == 1) {
            this.rampartCaptain.triggerAnimation(RampartCaptain.ATTACK1);
            this.rampartCaptain.isIceAxeAttacking = true;
            this.rampartCaptain.attackTick = 24;
        } else {
            this.rampartCaptain.triggerAnimation(RampartCaptain.ATTACK2);
            this.rampartCaptain.isIceAxeAttacking = true;
            this.rampartCaptain.attackTick = 27;
        }

        this.rampartCaptain.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.rampartCaptain.getTarget();
        if (target != null) {
            this.rampartCaptain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (this.attackVariant == 1 && this.rampartCaptain.attackTick == 13) {
            this.performVerticalSlash();
        }

        if (this.attackVariant == 2 && this.rampartCaptain.attackTick == 15) {
            this.performSweepAttack();
        }
    }

    @Override
    public void stop() {
        this.rampartCaptain.isIceAxeAttacking = false;
        this.rampartCaptain.attackTick = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void performVerticalSlash() {
        Level level = this.rampartCaptain.level();
        if (level.isClientSide)
            return;

        LivingEntity target = this.rampartCaptain.getTarget();
        if (target != null && target.isAlive() && !MobUtil.areAllies(this.rampartCaptain, target)) {
            double distToTarget = this.rampartCaptain.distanceTo(target);
            if (distToTarget <= 3.0D) {
                this.rampartCaptain.doHurtTarget(target);
            }
        }

        float yRot = this.rampartCaptain.getYRot();
        float yRotRad = yRot * ((float) Math.PI / 180F);
        double forwardX = -Mth.sin(yRotRad) * 1.5D;
        double forwardZ = Mth.cos(yRotRad) * 1.5D;
        double centerX = this.rampartCaptain.getX() + forwardX;
        double centerZ = this.rampartCaptain.getZ() + forwardZ;
        double centerY = this.rampartCaptain.getY() + 1.0D;
        AABB attackBox = new AABB(
                centerX - 1.5D, centerY - 1.0D, centerZ - 1.5D,
                centerX + 1.5D, centerY + 2.0D, centerZ + 1.5D);

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class, attackBox,
                entity -> entity.isAlive() && !MobUtil.areAllies(this.rampartCaptain, entity)
                        && entity != this.rampartCaptain);

        for (LivingEntity entity : entities) {
            if (entity != target) {
                this.rampartCaptain.doHurtTarget(entity);
            }
        }
    }

    private void performSweepAttack() {
        Level level = this.rampartCaptain.level();
        if (level.isClientSide)
            return;

        LivingEntity target = this.rampartCaptain.getTarget();
        double radius = 3.0D;
        if (target != null && target.isAlive() && !MobUtil.areAllies(this.rampartCaptain, target)) {
            double distToTarget = this.rampartCaptain.distanceTo(target);
            if (distToTarget <= 3.0D) {
                this.rampartCaptain.doHurtTarget(target);
            }
        }

        AABB searchBox = this.rampartCaptain.getBoundingBox().inflate(radius, 1.0D, radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class, searchBox,
                entity -> entity.isAlive() && !MobUtil.areAllies(this.rampartCaptain, entity)
                        && entity != this.rampartCaptain);
        float captainAngle = this.rampartCaptain.yBodyRot % 360;
        if (captainAngle < 0)
            captainAngle += 360;

        for (LivingEntity entity : entities) {
            if (entity == target)
                continue;

            double dX = entity.getX() - this.rampartCaptain.getX();
            double dZ = entity.getZ() - this.rampartCaptain.getZ();
            float entityAngle = (float) (Math.atan2(dZ, dX) * (180.0D / Math.PI) - 90.0D);
            entityAngle = entityAngle % 360;
            if (entityAngle < 0)
                entityAngle += 360;
            float relativeAngle = entityAngle - captainAngle;
            if (relativeAngle > 180)
                relativeAngle -= 360;
            if (relativeAngle < -180)
                relativeAngle += 360;

            float distance = (float) Math.sqrt(dX * dX + dZ * dZ);
            if (distance <= radius && relativeAngle >= -180.0F && relativeAngle <= 45.0F) {
                this.rampartCaptain.doHurtTarget(entity);
            }
        }
    }
}
