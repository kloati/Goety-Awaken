package com.k1sak1.goetyawaken.client.audio;

import com.Polarice3.Goety.utils.ControlledAnimation;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.init.ModTags;
import com.k1sak1.goetyawaken.client.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

public class BossLoopMusic extends AbstractTickableSoundInstance {
    protected final Mob mobEntity;
    private final float trueVolume;
    private int ticksExisted = 0;
    private int timeUntilFade;
    private ControlledAnimation volumeControl;
    protected SoundEvent postBossMusic;
    private boolean hasPlayedPostMusic = false;

    public BossLoopMusic(SoundEvent soundEvent, Mob mobEntity) {
        this(soundEvent, null, mobEntity, 1.0F, 1.0F);
    }

    public BossLoopMusic(SoundEvent soundEvent, SoundEvent postBossMusic, Mob mobEntity, float volume, float pitch) {
        super(soundEvent, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.mobEntity = mobEntity;
        this.postBossMusic = postBossMusic;
        this.x = mobEntity.getX();
        this.y = mobEntity.getY();
        this.z = mobEntity.getZ();
        this.looping = true;
        this.delay = 0;
        this.volumeControl = new ControlledAnimation(40);
        this.volumeControl.setTimer(20);
        this.volume = this.volumeControl.getAnimationFraction();
        this.trueVolume = volume;
        this.pitch = pitch;
        this.timeUntilFade = 80;
    }

    public Mob getMobEntity() {
        return this.mobEntity;
    }

    public boolean canPlaySound() {
        return ClientEvents.BOSS_MUSIC == this;
    }

    @Override
    public void tick() {
        if (!com.Polarice3.Goety.config.MainConfig.BossMusic.get()) {
            ClientEvents.BOSS_MUSIC = null;
            this.stop();
            return;
        }
        boolean target = isBossInCombat();

        if (!target || this.mobEntity.isRemoved() || this.mobEntity.isDeadOrDying() || !this.mobEntity.isAlive()) {
            if (this.mobEntity.isDeadOrDying() && !hasPlayedPostMusic) {
                this.timeUntilFade = 0;
                if (this.mobEntity.level().isClientSide && this.postBossMusic != null) {
                    Minecraft minecraft = Minecraft.getInstance();
                    SoundManager soundHandler = minecraft.getSoundManager();
                    if (!this.isStopped()) {
                        soundHandler.queueTickingSound(new com.Polarice3.Goety.client.audio.PostBossMusic(
                                this.postBossMusic, mobEntity, this.trueVolume, this.pitch));
                        hasPlayedPostMusic = true;
                    }
                }
            }

            if (this.timeUntilFade > 0) {
                this.timeUntilFade--;
            } else {
                this.volumeControl.decreaseTimer();
            }
        } else {
            this.volumeControl.increaseTimer();
            this.timeUntilFade = 60;
        }

        this.x = this.mobEntity.getX();
        this.y = this.mobEntity.getY();
        this.z = this.mobEntity.getZ();

        this.volume = this.volumeControl.getAnimationFraction() * this.trueVolume;

        if (this.volumeControl.getAnimationFraction() < 0.025) {
            ClientEvents.BOSS_MUSIC = null;
            this.stop();
            return;
        }

        if (this.ticksExisted % 100 == 0) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }
        this.ticksExisted++;
    }

    private boolean isBossInCombat() {
        if (this.mobEntity.isNoAi() || !this.mobEntity.isAggressive()) {
            return false;
        }

        var target = MiscCapHelper.getMobTarget(this.mobEntity);
        boolean targetingPlayer = target instanceof Player;
        boolean targetingPlayerPet = target instanceof OwnableEntity ownable && ownable.getOwner() instanceof Player;
        boolean globalMusicBoss = this.mobEntity.getType().is(ModTags.EntityTypes.GLOBAL_MUSIC_BOSS);

        return targetingPlayer || targetingPlayerPet || globalMusicBoss;
    }
}
