package com.k1sak1.goetyawaken.common.entities.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class NearestHealableAllyTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private static final int DEFAULT_COOLDOWN = 200;
    private int cooldown = 0;

    public NearestHealableAllyTargetGoal(Mob pMob, boolean pMustSee,
            @Nullable Predicate<LivingEntity> pTargetPredicate) {
        super(pMob, LivingEntity.class, 500, pMustSee, false, pTargetPredicate);
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void decrementCooldown() {
        --this.cooldown;
    }

    public boolean canUse() {
        if (this.cooldown <= 0 && this.mob.getRandom().nextBoolean()) {
            this.findTarget();
            if (this.target != null && this.target.getHealth() < this.target.getMaxHealth()) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public void start() {
        this.cooldown = reducedTickDelay(200);
        super.start();
    }
}