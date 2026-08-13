package com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRampartCaptain;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.IceAxeItem;
import com.k1sak1.goetyawaken.common.entities.projectiles.FlyingAxeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ThrowGoal extends Goal {
    private final HostileRampartCaptain hostileRampartCaptain;
    private int variant;

    public ThrowGoal(HostileRampartCaptain hostileRampartCaptain) {
        this.hostileRampartCaptain = hostileRampartCaptain;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (this.hostileRampartCaptain.getThrowCooldown() > 0 ||
                this.hostileRampartCaptain.isIceAxeAttacking || this.hostileRampartCaptain.isBlowingHorn() ||
                this.hostileRampartCaptain.isRunAttacking() ||
                target == null || !target.isAlive() ||
                target == this.hostileRampartCaptain) {
            return false;
        }

        double distance = this.hostileRampartCaptain.distanceTo(target);
        long timeSinceLastDamage = this.hostileRampartCaptain.level().getGameTime()
                - this.hostileRampartCaptain.getLastDamageTime();
        boolean shouldThrow = distance > 10.0D || timeSinceLastDamage > 160;

        return shouldThrow;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.hostileRampartCaptain.getThrowTick() > 0;
    }

    @Override
    public void start() {
        this.hostileRampartCaptain.setIsThrowing(true);
        this.variant = this.hostileRampartCaptain.getRandom().nextInt(2) + 1;

        if (this.variant == 1) {
            this.hostileRampartCaptain.setThrowTick(30);
            this.hostileRampartCaptain.triggerAnimation(HostileRampartCaptain.THROW);
        }

        this.hostileRampartCaptain.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.hostileRampartCaptain.getTarget();
        if (target != null) {
            this.hostileRampartCaptain.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (this.variant == 1 && this.hostileRampartCaptain.getThrowTick() == 20) {
            this.throwAxe(target);
        }
    }

    @Override
    public void stop() {
        this.hostileRampartCaptain.setThrowCooldown(200);
        this.hostileRampartCaptain.setIsThrowing(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void throwAxe(LivingEntity target) {
        if (this.hostileRampartCaptain.level().isClientSide) {
            return;
        }
        ItemStack mainHandItem = this.hostileRampartCaptain.getMainHandItem();
        ItemStack throwItem;

        if (mainHandItem.getItem() instanceof IceAxeItem || mainHandItem.getItem() instanceof AxeItem) {
            throwItem = mainHandItem;
        } else {
            throwItem = new ItemStack(ModItems.DIAMOND_ICE_AXE.get());
        }

        FlyingAxeEntity flyingAxe = new FlyingAxeEntity(this.hostileRampartCaptain, this.hostileRampartCaptain.level(),
                throwItem);
        Vec3 targetPos = target.position().add(0.0D, target.getEyeHeight() * 0.5D, 0.0D);
        Vec3 startPos = new Vec3(flyingAxe.getX(), flyingAxe.getY(), flyingAxe.getZ());

        double dx = targetPos.x - startPos.x;
        double dy = targetPos.y - startPos.y;
        double dz = targetPos.z - startPos.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double speed = 1.6D;
        double flightTime = horizontalDistance / speed;
        double gravity = 0.05D;
        double verticalOffset = 0.5D * gravity * flightTime * flightTime;
        Vec3 correctedTargetPos = targetPos.add(0.0D, verticalOffset, 0.0D);
        Vec3 direction = correctedTargetPos.subtract(startPos).normalize();

        flyingAxe.setDeltaMovement(direction.multiply(speed, speed, speed));

        this.hostileRampartCaptain.level().addFreshEntity(flyingAxe);

        this.hostileRampartCaptain.setLastDamageTime(this.hostileRampartCaptain.level().getGameTime());
    }
}
