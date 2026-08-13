package com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRampartCaptain;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.List;

public class IceAxeAttackGoal extends Goal {
    private final HostileRampartCaptain hostileRampartCaptain;
    private int attackVariant;

    public IceAxeAttackGoal(HostileRampartCaptain hostileRampartCaptain) {
        this.hostileRampartCaptain = hostileRampartCaptain;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (this.hostileRampartCaptain.isBlowingHorn() || this.hostileRampartCaptain.isRunAttacking() ||
                this.hostileRampartCaptain.isThrowing() || target == null || !target.isAlive()
                || target == this.hostileRampartCaptain) {
            return false;
        }
        return this.hostileRampartCaptain.distanceToSqr(target) <= 4.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return this.hostileRampartCaptain.attackTick > 0;
    }

    @Override
    public void start() {
        this.attackVariant = this.hostileRampartCaptain.getRandom().nextInt(2) + 1;
        this.hostileRampartCaptain.playSound(com.k1sak1.goetyawaken.init.ModSounds.RAMPART_CAPTAIN_SHOUT.get(), 2.0F,
                1.0F);
        if (this.attackVariant == 1) {
            this.hostileRampartCaptain.triggerAnimation(HostileRampartCaptain.ATTACK1);
            this.hostileRampartCaptain.isIceAxeAttacking = true;
            this.hostileRampartCaptain.attackTick = 24;
        } else {
            this.hostileRampartCaptain.triggerAnimation(HostileRampartCaptain.ATTACK2);
            this.hostileRampartCaptain.isIceAxeAttacking = true;
            this.hostileRampartCaptain.attackTick = 27;
        }

        this.hostileRampartCaptain.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (target != null) {
            this.hostileRampartCaptain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (this.attackVariant == 1 && this.hostileRampartCaptain.attackTick == 13) {
            this.performVerticalSlash();
        }

        if (this.attackVariant == 2 && this.hostileRampartCaptain.attackTick == 15) {
            this.performSweepAttack();
        }
    }

    @Override
    public void stop() {
        this.hostileRampartCaptain.isIceAxeAttacking = false;
        this.hostileRampartCaptain.attackTick = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void performVerticalSlash() {
        Level level = this.hostileRampartCaptain.level();
        if (level.isClientSide)
            return;

        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (target != null && target.isAlive() && !MobUtil.areAllies(this.hostileRampartCaptain, target)) {
            double distToTarget = this.hostileRampartCaptain.distanceTo(target);
            if (distToTarget <= 3.0D) {
                this.hostileRampartCaptain.doHurtTarget(target);
            }
        }

        float yRot = this.hostileRampartCaptain.getYRot();
        float yRotRad = yRot * ((float) Math.PI / 180F);
        double forwardX = -Mth.sin(yRotRad) * 1.5D;
        double forwardZ = Mth.cos(yRotRad) * 1.5D;
        double centerX = this.hostileRampartCaptain.getX() + forwardX;
        double centerZ = this.hostileRampartCaptain.getZ() + forwardZ;
        double centerY = this.hostileRampartCaptain.getY() + 1.0D;
        AABB attackBox = new AABB(
                centerX - 1.5D, centerY - 1.0D, centerZ - 1.5D,
                centerX + 1.5D, centerY + 2.0D, centerZ + 1.5D);

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class, attackBox,
                entity -> entity.isAlive() && !MobUtil.areAllies(this.hostileRampartCaptain, entity)
                        && entity != this.hostileRampartCaptain);

        for (LivingEntity entity : entities) {
            if (entity != target) {
                this.hostileRampartCaptain.doHurtTarget(entity);
            }
        }
    }

    private void performSweepAttack() {
        Level level = this.hostileRampartCaptain.level();
        if (level.isClientSide)
            return;

        LivingEntity target = this.hostileRampartCaptain.getTarget();
        double radius = 3.0D;
        if (target != null && target.isAlive() && !MobUtil.areAllies(this.hostileRampartCaptain, target)) {
            double distToTarget = this.hostileRampartCaptain.distanceTo(target);
            if (distToTarget <= 3.0D) {
                this.hostileRampartCaptain.doHurtTarget(target);
            }
        }

        AABB searchBox = this.hostileRampartCaptain.getBoundingBox().inflate(radius, 1.0D, radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class, searchBox,
                entity -> entity.isAlive() && !MobUtil.areAllies(this.hostileRampartCaptain, entity)
                        && entity != this.hostileRampartCaptain);
        float captainAngle = this.hostileRampartCaptain.yBodyRot % 360;
        if (captainAngle < 0)
            captainAngle += 360;

        for (LivingEntity entity : entities) {
            if (entity == target)
                continue;

            double dX = entity.getX() - this.hostileRampartCaptain.getX();
            double dZ = entity.getZ() - this.hostileRampartCaptain.getZ();
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
                this.hostileRampartCaptain.doHurtTarget(entity);
            }
        }
    }
}
