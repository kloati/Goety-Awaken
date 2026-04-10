package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.network.server.SBossBarPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityAncientBossBarMixin {

    private UUID goetyawaken$ancientBossBarUUID = null;
    private ModServerBossInfo goetyawaken$ancientBossInfo = null;

    @Unique
    private static final Class<?>[] BOSS_EVENT_TYPES = {
            ServerBossEvent.class,
            ModServerBossInfo.class
    };

    @Unique
    private Boolean goetyawaken$hasNativeBossBarCache = null;

    @Unique
    private boolean goetyawaken$hasNativeBossBar(Entity entity) {
        if (this.goetyawaken$hasNativeBossBarCache != null) {
            return this.goetyawaken$hasNativeBossBarCache;
        }

        Class<?> clazz = entity.getClass();
        boolean hasBossBar = false;

        for (Class<?> bossEventType : BOSS_EVENT_TYPES) {
            if (goetyawaken$searchForBossEventField(clazz, bossEventType)) {
                hasBossBar = true;
                break;
            }
        }

        this.goetyawaken$hasNativeBossBarCache = hasBossBar;
        return hasBossBar;
    }

    @Unique
    private static boolean goetyawaken$searchForBossEventField(Class<?> clazz, Class<?> bossEventType) {
        Class<?> current = clazz;
        while (current != null && current != Entity.class && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (bossEventType.isAssignableFrom(field.getType())) {
                    return true;
                }
            }
            current = current.getSuperclass();
        }
        return false;
    }

    @Inject(method = "startSeenByPlayer", at = @At("HEAD"))
    private void onStartSeenByPlayer(ServerPlayer pServerPlayer, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof Mob mob)) {
            return;
        }

        if (self instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())
                && !this.goetyawaken$hasNativeBossBar(self)) {
            if (this.goetyawaken$ancientBossInfo == null) {
                this.goetyawaken$ancientBossInfo = new ModServerBossInfo(
                        mob,
                        BossEvent.BossBarColor.WHITE,
                        true,
                        true);
                this.goetyawaken$ancientBossBarUUID = this.goetyawaken$ancientBossInfo.getId();
            }

            this.goetyawaken$ancientBossInfo.addPlayer(pServerPlayer);
            GoetyAwaken.PROXY.addBossBar(this.goetyawaken$ancientBossBarUUID, mob);

            if (pServerPlayer.server != null && this.goetyawaken$ancientBossBarUUID != null) {
                GoetyAwaken.network.sendTo(pServerPlayer,
                        new SBossBarPacket(
                                this.goetyawaken$ancientBossBarUUID, mob, false, SBossBarPacket.RENDER_TYPE_ANCIENT));
            }
        }
    }

    @Inject(method = "stopSeenByPlayer", at = @At("HEAD"))
    private void onStopSeenByPlayer(ServerPlayer pServerPlayer, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof Mob mob)) {
            return;
        }

        if (self instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())
                && !this.goetyawaken$hasNativeBossBar(self)) {
            if (this.goetyawaken$ancientBossInfo != null) {
                this.goetyawaken$ancientBossInfo.removePlayer(pServerPlayer);
            }
            if (this.goetyawaken$ancientBossBarUUID != null) {
                GoetyAwaken.PROXY.removeBossBar(this.goetyawaken$ancientBossBarUUID, mob);

                if (pServerPlayer.server != null) {
                    GoetyAwaken.network.sendTo(pServerPlayer,
                            new SBossBarPacket(
                                    this.goetyawaken$ancientBossBarUUID, mob, true,
                                    SBossBarPacket.RENDER_TYPE_ANCIENT));
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof Mob mob)) {
            return;
        }

        if (this.goetyawaken$ancientBossInfo != null && mob.tickCount % 5 == 0) {
            this.goetyawaken$ancientBossInfo.setProgress(
                    mob.getMaxHealth() > 0 ? mob.getHealth() / mob.getMaxHealth() : 0);
        }
    }
}
