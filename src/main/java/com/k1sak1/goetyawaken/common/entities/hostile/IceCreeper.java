package com.k1sak1.goetyawaken.common.entities.hostile;

import com.Polarice3.Goety.utils.LootingExplosion;
import com.k1sak1.goetyawaken.common.entities.ally.IceCreeperServant;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;

public class IceCreeper extends IceCreeperServant implements Enemy {

    public IceCreeper(EntityType<? extends IceCreeperServant> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
    }

    @Override
    protected LootingExplosion.BlockInteraction getBlockInteraction() {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return LootingExplosion.BlockInteraction.DESTROY_WITH_DECAY;
        } else {
            return LootingExplosion.BlockInteraction.KEEP;
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1,
                new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.player.Player.class, true));
    }
}