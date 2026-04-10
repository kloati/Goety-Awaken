package com.k1sak1.goetyawaken.common.entities.hostile;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.world.data.GAWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import net.minecraft.world.BossEvent;

public class HostileWildfire extends Wildfire implements Enemy {
    private final ModServerBossInfo bossInfo;
    private boolean isNaturalSpawn = false;

    public HostileWildfire(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.setHostile(true);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1,
                (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this,
                com.Polarice3.Goety.common.entities.hostile.WitherNecromancer.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this,
                com.Polarice3.Goety.common.entities.ally.undead.skeleton.WitherNecromancerServant.class, false));
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNaturalSpawn && this.bossInfo != null && this.level() instanceof ServerLevel) {
            this.bossInfo.update();
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isNaturalSpawn && this.bossInfo != null && this.level() instanceof ServerLevel) {
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
        if (!this.isNaturalSpawn && this.bossInfo != null) {
            this.bossInfo.addPlayer(player);
        }
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (!this.isNaturalSpawn && this.bossInfo != null) {
            this.bossInfo.removePlayer(player);
        }
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        if (worldIn instanceof ServerLevel serverLevel) {
            GAWorldData data = GAWorldData.get(serverLevel, Level.NETHER);
            return data != null && data.isWitherNecromancerDefeatedOnce();
        }
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
            MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnData,
            @javax.annotation.Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.NATURAL) {
            this.isNaturalSpawn = true;
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnData, dataTag);
    }

    @Override
    public int xpReward() {
        return 40;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean checkWildfireSpawnRules(EntityType entityType, ServerLevelAccessor serverLevelAccessor,
            MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (serverLevelAccessor instanceof ServerLevel serverLevel) {
            GAWorldData data = GAWorldData.get(serverLevel, Level.NETHER);
            if (data == null || !data.isWitherNecromancerDefeatedOnce()) {
                return false;
            }
        }
        return Monster.checkMonsterSpawnRules((EntityType<? extends Monster>) entityType, serverLevelAccessor,
                spawnType, pos, random);
    }
}