package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer;

import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.GoetyAwaken;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import javax.annotation.Nullable;

public class NamelessOne extends AbstractNamelessOne implements Enemy {
    private final ModServerBossInfo bossInfo;

    public NamelessOne(EntityType<? extends AbstractNamelessOne> type, Level level) {
        super(type, level);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.YELLOW, false, false);
        this.setHostile(true);
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        super.registerGoals();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isMirror()) {
            if (this.tickCount % 5 == 0) {
                this.bossInfo.update();
            }
            this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
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
        if (!this.isMirror()) {
            this.bossInfo.addPlayer(pPlayer);
            GoetyAwaken.PROXY.addBossBar(this.bossInfo.getId(), this);
            if (this.getServer() != null) {
                GoetyAwaken.network.sendTo(pPlayer,
                        new com.k1sak1.goetyawaken.common.network.server.SBossBarPacket(
                                this.bossInfo.getId(), this, false,
                                com.k1sak1.goetyawaken.common.network.server.SBossBarPacket.RENDER_TYPE_NAMELESS_ONE));
            }
        }
    }

    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        if (!this.isMirror()) {
            this.bossInfo.removePlayer(pPlayer);
            GoetyAwaken.PROXY.removeBossBar(this.bossInfo.getId(), this);
            if (this.getServer() != null) {
                GoetyAwaken.network.sendTo(pPlayer,
                        new com.k1sak1.goetyawaken.common.network.server.SBossBarPacket(
                                this.bossInfo.getId(), this, true,
                                com.k1sak1.goetyawaken.common.network.server.SBossBarPacket.RENDER_TYPE_NAMELESS_ONE));
            }
        }
    }

    protected boolean canRide(Entity pEntity) {
        return false;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
    }
}