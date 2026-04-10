package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer;

import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.api.IAncientGlint;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

import com.Polarice3.Goety.config.MainConfig;

public class ParchedNecromancer extends AbstractParchedNecromancer implements Enemy {
    private final ModServerBossInfo bossInfo;

    public ParchedNecromancer(EntityType<? extends AbstractParchedNecromancer> type, Level level) {
        super(type, level);
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.YELLOW, false, false);
        this.setHostile(true);
    }

    @Override
    public int xpReward() {
        return 40;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag pCompound) {
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

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        int fragmentCount = 1 + this.random.nextInt(4);
        for (int i = 0; i < fragmentCount; i++) {
            this.spawnAtLocation(new ItemStack(com.k1sak1.goetyawaken.common.items.ModItems.TABOO_FRAGMENT.get()));
        }
        this.spawnAtLocation(new ItemStack(com.Polarice3.Goety.common.items.ModItems.TREASURE_POUCH.get()));
        if (this.random.nextFloat() < 0.25f) {
            this.spawnAtLocation(
                    new ItemStack(com.k1sak1.goetyawaken.common.items.ModItems.MUSIC_DISC_RUINS_NECR.get()));
        }
    }
}