package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer;

import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.MainConfig;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.AbstractWraithNecromancer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.List;
import javax.annotation.Nullable;

public class WraithNecromancer extends AbstractWraithNecromancer implements Enemy {
    private final ModServerBossInfo bossInfo;
    public int teleportTime = 17;

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public int xpReward() {
        return 40;
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    public double prevX;
    public double prevY;
    public double prevZ;
    private static final EntityDataAccessor<Byte> WRAITH_FLAGS = SynchedEntityData.defineId(WraithNecromancer.class,
            EntityDataSerializers.BYTE);
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState flyAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState spellAnimationState = new AnimationState();
    public AnimationState alertAnimationState = new AnimationState();
    public AnimationState shockwaveAnimationState = new AnimationState();
    private AnimationState currentActiveAnimation = null;

    public WraithNecromancer(EntityType<? extends AbstractWraithNecromancer> type, Level level) {
        super(type, level);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, false, false);
        this.cantDo = 0;
        this.setHostile(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WRAITH_FLAGS, (byte) 0);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        this.bossInfo.removePlayer(pPlayer);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public int getAnimationState() {
        return this.entityData.get(ANIM_STATE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33609_) {
        if (LEVEL.equals(p_33609_)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
        }
        if (ANIM_STATE.equals(p_33609_)) {
            if (this.level().isClientSide) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0:
                        this.stopAllAnimations();
                        break;
                    case ATTACK_ANIM:
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case SUMMON_ANIM:
                        this.summonAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case SPELL_ANIM:
                        this.spellAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.spellAnimationState);
                        break;
                    case ALERT_ANIM:
                        this.alertAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.alertAnimationState);
                        break;
                    case SHOCKWAVE_ANIM:
                        this.shockwaveAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.shockwaveAnimationState);
                        break;
                    default:
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_33609_);
    }

    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        if (this.getNecroLevel() > 0) {
            float f1 = (float) this.getNecroLevel();
            float size = 1.0F + Math.max(f1 * 0.10F, 0);
            return super.getDimensions(p_33597_).scale(size);
        } else {
            return super.getDimensions(p_33597_);
        }
    }

    public boolean isFlying() {
        return this.isNoGravity();
    }

    public boolean isIdleOrNoAnimation() {
        return this.getAnimationState() == IDLE_ANIM || this.getAnimationState() == 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getMaxHealth() > 0 ? this.getHealth() / this.getMaxHealth() : 0);

        if (this.level().isClientSide) {
            if (this.isAlive()) {
                if (this.isSpellCasting()) {
                    this.spellCastParticles();
                }
            }
            this.idleAnimationState.animateWhen(this.getAnimationState() == IDLE_ANIM, this.tickCount);
            this.flyAnimationState.animateWhen(this.getAnimationState() == FLY_ANIM, this.tickCount);
            this.attackAnimationState.animateWhen(this.getAnimationState() == ATTACK_ANIM, this.tickCount);
            this.summonAnimationState.animateWhen(this.getAnimationState() == SUMMON_ANIM, this.tickCount);
            this.spellAnimationState.animateWhen(this.getAnimationState() == SPELL_ANIM, this.tickCount);
            this.alertAnimationState.animateWhen(this.getAnimationState() == ALERT_ANIM, this.tickCount);
            this.shockwaveAnimationState.animateWhen(this.getAnimationState() == SHOCKWAVE_ANIM, this.tickCount);
        } else {
            if (!this.isShooting() && !this.isSpellCasting() &&
                    this.getAnimationState() != SUMMON_ANIM &&
                    this.getAnimationState() != SPELL_ANIM &&
                    this.getAnimationState() != ATTACK_ANIM &&
                    this.getAnimationState() != ALERT_ANIM &&
                    this.getAnimationState() != SHOCKWAVE_ANIM) {
                double speed = this.getDeltaMovement().horizontalDistance();
                if (speed > 0.3D) {
                    if (this.getAnimationState() != FLY_ANIM) {
                        this.setAnimationState(FLY_ANIM);
                    }
                } else {
                    if (this.getAnimationState() != IDLE_ANIM) {
                        this.setAnimationState(IDLE_ANIM);
                    }
                }
            }
        }
    }

    public void stopAllAnimationsAndReset() {
        this.stopAllAnimations();
        this.resetSkillType();
        this.setShooting(false);
        this.setSpellCasting(false);
        this.currentActiveAnimation = null;
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new java.util.ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.flyAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.spellAnimationState);
        animationStates.add(this.alertAnimationState);
        animationStates.add(this.shockwaveAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        for (AnimationState animationState : this.getAnimations()) {
            animationState.stop();
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
            MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (dataTag != null && dataTag.contains("NecroLevel")) {
            int level = dataTag.getInt("NecroLevel");
            this.setNecroLevel(level);
        }
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        this.addSummon(com.Polarice3.Goety.common.entities.ModEntityType.WRAITH_SERVANT.get());
        this.addSummon(com.Polarice3.Goety.common.entities.ModEntityType.MUCK_WRAITH_SERVANT.get());
        this.addSummon(com.Polarice3.Goety.common.entities.ModEntityType.BORDER_WRAITH_SERVANT.get());
        this.addSummon(com.Polarice3.Goety.common.entities.ModEntityType.REAPER_SERVANT.get());
        this.setPersistenceRequired();

        return spawnDataIn;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int looting, boolean killedByPlayer) {
        super.dropCustomDeathLoot(damageSource, looting, killedByPlayer);
        this.spawnAtLocation(com.k1sak1.goetyawaken.common.items.ModItems.GLACIAL_WRAITH_ESSENCE.get());
        this.spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.TREASURE_POUCH.get());
    }
}
