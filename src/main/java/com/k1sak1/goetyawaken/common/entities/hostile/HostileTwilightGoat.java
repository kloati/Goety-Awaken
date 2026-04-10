package com.k1sak1.goetyawaken.common.entities.hostile;

import javax.annotation.Nullable;

import com.Polarice3.Goety.common.entities.ally.TwilightGoat;
import com.Polarice3.Goety.common.entities.neutral.Owned;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class HostileTwilightGoat extends TwilightGoat implements Enemy {

    public HostileTwilightGoat(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
            @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (this.random.nextDouble() < 0.1) {
            this.setUpgraded(true);
        }

        return pSpawnData;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1,
                (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
    }
}