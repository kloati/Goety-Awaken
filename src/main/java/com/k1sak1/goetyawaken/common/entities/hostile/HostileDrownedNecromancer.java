package com.k1sak1.goetyawaken.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import javax.annotation.Nullable;

public class HostileDrownedNecromancer extends DrownedNecromancer implements Enemy {
    private final ModServerBossInfo bossInfo;

    public HostileDrownedNecromancer(EntityType<? extends DrownedNecromancer> type, Level level) {
        super(type, level);
        this.setHostile(true);
        this.setPersistenceRequired();
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.BLUE, false, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, (double) 1.0F));
        this.targetSelector.addGoal(1,
                (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[] { ZombifiedPiglin.class }));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
        this.targetSelector.addGoal(5,
                new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
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
    public void tick() {
        super.tick();
        if (this.bossInfo != null && this.level() instanceof net.minecraft.server.level.ServerLevel) {
            this.bossInfo.update();
        }
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (this.bossInfo != null && this.level() instanceof net.minecraft.server.level.ServerLevel) {
            this.bossInfo.update();
        }
        return super.hurt(source, amount);
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (this.bossInfo != null) {
            this.bossInfo.addPlayer(player);
        }
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (this.bossInfo != null) {
            this.bossInfo.removePlayer(player);
        }
    }
}