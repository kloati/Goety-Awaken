package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.network.server.SBossBarPacket;
import com.k1sak1.goetyawaken.utils.annotation.RequiresModPresent;
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


@RequiresModPresent("masquerader_mod")
@Mixin(Entity.class)
public abstract class MasqueraderBossBarMixin {

    @Unique
    private UUID goetyawaken$masqueraderBossBarUUID = null;

    @Unique
    private ModServerBossInfo goetyawaken$masqueraderBossInfo = null;

    @Unique
    private boolean goetyawaken$isMasquerader(Entity entity) {
        try {
            Class<?> masqueraderClass = Class.forName("net.random_something.masquerader_mod.entity.Masquerader");
            boolean isInstance = masqueraderClass.isInstance(entity);
            if (isInstance) {
                Class<?> cloneClass = Class.forName("net.random_something.masquerader_mod.entity.MasqueraderClone");
                if (cloneClass.isInstance(entity)) {
                    return false;
                }
                return true;
            }
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    @Unique
    private ServerBossEvent goetyawaken$getMasqueraderBossEvent(Entity entity) {
        try {
            Class<?> current = entity.getClass();
            while (current != null && current != Object.class) {
                for (Field field : current.getDeclaredFields()) {
                    if (ServerBossEvent.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        return (ServerBossEvent) field.get(entity);
                    }
                }
                current = current.getSuperclass();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Inject(method = "startSeenByPlayer", at = @At("HEAD"))
    private void onStartSeenByPlayer(ServerPlayer pServerPlayer, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof Mob mob)) {
            return;
        }

        if (!this.goetyawaken$isMasquerader(self)) {
            return;
        }

        ServerBossEvent nativeBossEvent = this.goetyawaken$getMasqueraderBossEvent(self);
        UUID bossBarUUID;

        if (nativeBossEvent != null) {
            bossBarUUID = nativeBossEvent.getId();
        } else {
            if (this.goetyawaken$masqueraderBossInfo == null) {
                this.goetyawaken$masqueraderBossInfo = new ModServerBossInfo(
                        mob,
                        BossEvent.BossBarColor.WHITE,
                        true,
                        false);
                this.goetyawaken$masqueraderBossInfo.setVisible(true);
            }
            this.goetyawaken$masqueraderBossInfo.addPlayer(pServerPlayer);
            bossBarUUID = this.goetyawaken$masqueraderBossInfo.getId();
        }

        this.goetyawaken$masqueraderBossBarUUID = bossBarUUID;
        GoetyAwaken.PROXY.addBossBar(bossBarUUID, mob);

        if (pServerPlayer.server != null && bossBarUUID != null) {
            GoetyAwaken.network.sendTo(pServerPlayer,
                    new SBossBarPacket(
                            bossBarUUID, mob, false, SBossBarPacket.RENDER_TYPE_MASQUERADER));
        }
    }

    @Inject(method = "stopSeenByPlayer", at = @At("HEAD"))
    private void onStopSeenByPlayer(ServerPlayer pServerPlayer, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof Mob mob)) {
            return;
        }

        if (!this.goetyawaken$isMasquerader(self)) {
            return;
        }

        if (this.goetyawaken$masqueraderBossInfo != null) {
            this.goetyawaken$masqueraderBossInfo.removePlayer(pServerPlayer);
        }

        if (this.goetyawaken$masqueraderBossBarUUID != null) {
            GoetyAwaken.PROXY.removeBossBar(this.goetyawaken$masqueraderBossBarUUID, mob);

            if (pServerPlayer.server != null) {
                GoetyAwaken.network.sendTo(pServerPlayer,
                        new SBossBarPacket(
                                this.goetyawaken$masqueraderBossBarUUID, mob, true,
                                SBossBarPacket.RENDER_TYPE_MASQUERADER));
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof Mob mob)) {
            return;
        }

        if (!this.goetyawaken$isMasquerader(self)) {
            return;
        }

        if (this.goetyawaken$masqueraderBossInfo != null && mob.tickCount % 5 == 0) {
            this.goetyawaken$masqueraderBossInfo.setProgress(
                    mob.getMaxHealth() > 0 ? mob.getHealth() / mob.getMaxHealth() : 0);
        }
    }

    @Inject(method = "setCustomName", at = @At("TAIL"))
    private void onSetCustomName(net.minecraft.network.chat.Component name, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!this.goetyawaken$isMasquerader(self)) {
            return;
        }

        if (this.goetyawaken$masqueraderBossInfo != null && self instanceof Mob mob) {
            this.goetyawaken$masqueraderBossInfo.setName(mob.getDisplayName());
        }
    }
}
