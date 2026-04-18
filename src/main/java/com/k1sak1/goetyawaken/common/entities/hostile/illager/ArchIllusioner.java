package com.k1sak1.goetyawaken.common.entities.hostile.illager;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;

import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.ArchIllusionerServant;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class ArchIllusioner extends ArchIllusionerServant implements Enemy {
    private final ModServerBossInfo bossInfo;

    public ArchIllusioner(EntityType<? extends ArchIllusionerServant> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
        this.setPersistenceRequired();
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.isIllusion() && this.bossInfo != null && this.level() instanceof ServerLevel) {
            this.bossInfo.update();
        }
    }

    @Override
    public int xpReward() {
        return 40;
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isIllusion() && this.bossInfo != null && this.level() instanceof ServerLevel) {
            this.bossInfo.update();
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        return super.isAlliedTo(entityIn) || (entityIn instanceof Raider)
                || (entityIn instanceof RaiderServant hostileraiderservant && hostileraiderservant.isHostile());
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);

        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (!this.isIllusion() && this.bossInfo != null) {
            this.bossInfo.addPlayer(player);
        }
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (!this.isIllusion() && this.bossInfo != null) {
            this.bossInfo.removePlayer(player);
        }
    }
}
