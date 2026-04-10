package com.k1sak1.goetyawaken.common.entities.ally.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServant;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServantAi;
import net.minecraft.util.Unit;

public class RoarServant extends Behavior<WardenServant> {
    private static final int TICKS_BEFORE_PLAYING_ROAR_SOUND = 25;
    private static final int ROAR_ANGER_INCREASE = 20;

    public RoarServant() {
        super(ImmutableMap.of(MemoryModuleType.ROAR_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET,
                MemoryStatus.VALUE_ABSENT, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryStatus.REGISTERED,
                MemoryModuleType.ROAR_SOUND_DELAY, MemoryStatus.REGISTERED), WardenServantAi.ROAR_DURATION);
    }

    protected void start(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
        pEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_DELAY, Unit.INSTANCE, 25L);
        pEntity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        LivingEntity livingentity = pEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).get();
        pEntity.lookAt(livingentity, 10.0F, 10.0F);
        pEntity.setPose(net.minecraft.world.entity.Pose.ROARING);
        pEntity.increaseAngerAt(livingentity, 20, false);
    }

    protected boolean canStillUse(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
        return true;
    }

    protected void tick(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
        if (!pEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_DELAY)
                && !pEntity.getBrain().hasMemoryValue(MemoryModuleType.ROAR_SOUND_COOLDOWN)) {
            pEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ROAR_SOUND_COOLDOWN, Unit.INSTANCE,
                    (long) (WardenServantAi.ROAR_DURATION - 25));
            pEntity.playSound(SoundEvents.WARDEN_ROAR, 3.0F, 1.0F);
        }
    }

    protected void stop(ServerLevel pLevel, WardenServant pEntity, long pGameTime) {
        if (pEntity.hasPose(net.minecraft.world.entity.Pose.ROARING)) {
            pEntity.setPose(net.minecraft.world.entity.Pose.STANDING);
        }

        pEntity.getBrain().getMemory(MemoryModuleType.ROAR_TARGET).ifPresent(pEntity::setAttackTarget);
        pEntity.getBrain().eraseMemory(MemoryModuleType.ROAR_TARGET);
    }
}