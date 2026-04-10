package com.k1sak1.goetyawaken.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.LootingExplosion;
import com.k1sak1.goetyawaken.common.entities.ally.SpiderCreeder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class HostileSpiderCreeder extends SpiderCreeder implements Enemy {

    public HostileSpiderCreeder(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
    }

    @Override
    public void targetSelectGoal() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
    }

    @Override
    protected LootingExplosion.BlockInteraction getBlockInteraction() {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return LootingExplosion.BlockInteraction.DESTROY_WITH_DECAY;
        } else {
            return LootingExplosion.BlockInteraction.KEEP;
        }
    }
}