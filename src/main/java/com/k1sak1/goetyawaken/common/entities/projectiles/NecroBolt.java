package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.utils.MathHelper;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

public class NecroBolt extends com.Polarice3.Goety.common.entities.projectiles.NecroBolt {
    private LivingEntity homingTarget = null;
    private boolean shouldHoming = false;

    public NecroBolt(EntityType<? extends NecroBolt> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public NecroBolt(double pX, double pY, double pZ, double pXPower, double pYPower, double pZPower,
            Level pLevel) {
        super(pX, pY, pZ, pXPower, pYPower, pZPower, pLevel);
    }

    public NecroBolt(LivingEntity pShooter, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super(pShooter, pXPower, pYPower, pZPower, pLevel);
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            this.createDeathFire();
        }
    }

    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level().isClientSide) {
            this.createDeathFire();
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= MathHelper.secondsToTicks(5)) {
            this.discard();
        }
        if (this.shouldHoming && this.homingTarget != null
                && !this.homingTarget.isRemoved()) {
            performHoming();
        } else if (this.shouldHoming) {
            this.shouldHoming = false;
        }
    }

    private void performHoming() {
        Vec3 currentMotion = this.getDeltaMovement();
        double speed = currentMotion.length();

        if (speed < 0.1) {
            return;
        }

        Vec3 targetPos = this.homingTarget.getBoundingBox().getCenter();
        Vec3 toTarget = targetPos.subtract(this.position());

        if (currentMotion.dot(toTarget) < 0) {
            this.shouldHoming = false;
            return;
        }

        double distance = toTarget.length();
        double homingStrength = Mth.clamp((distance - 1.0) / 10.0, 0.1, 1.0);

        Vec3 desiredDirection = toTarget.normalize();
        Vec3 newDirection = currentMotion.normalize().scale(1.0 - homingStrength)
                .add(desiredDirection.scale(homingStrength))
                .normalize()
                .scale(speed);

        this.setDeltaMovement(newDirection);

        double d0 = newDirection.horizontalDistance();
        this.setYRot((float) (Mth.atan2(newDirection.x, newDirection.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(newDirection.y, d0) * (double) (180F / (float) Math.PI)));
    }

    public void setHomingTarget(LivingEntity target) {
        this.homingTarget = target;
        this.shouldHoming = true;
    }

    public boolean isHoming() {
        return this.shouldHoming && this.homingTarget != null && !this.homingTarget.isRemoved();
    }

    private void createDeathFire() {
        Entity owner = this.getOwner();
        if (owner != null && owner instanceof AbstractNamelessOne namelessone && !namelessone.isEasyMode()) {
            if (owner instanceof LivingEntity livingOwner) {
                DeathFire deathFire = new DeathFire(
                        this.level(),
                        this.blockPosition(),
                        livingOwner);
                deathFire.setExtraDamage(this.getExtraDamage());
                this.level().addFreshEntity(deathFire);
            } else {
                DeathFire deathFire = new DeathFire(
                        this.level(),
                        this.blockPosition(),
                        null);
                deathFire.setExtraDamage(this.getExtraDamage());
                this.level().addFreshEntity(deathFire);
            }
        }
    }
}