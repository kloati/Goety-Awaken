package com.k1sak1.goetyawaken.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HostileAngryMooshroom extends AngryMooshroom implements Enemy {

    public HostileAngryMooshroom(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1,
                (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
    }
}