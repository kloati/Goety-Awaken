package com.k1sak1.goetyawaken.client.audio;

import com.k1sak1.goetyawaken.common.entities.ally.JITBZombieServant;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class JITBZombieMusicLoop extends AbstractTickableSoundInstance {
    private final JITBZombieServant entity;

    public JITBZombieMusicLoop(SoundEvent soundEvent, JITBZombieServant entity) {
        super(soundEvent, SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;
    }

    @Override
    public void tick() {
        if (this.entity.isRemoved() || !this.entity.isAlive() || !this.entity.isAggressive()) {
            this.stop();
            return;
        }
        this.x = this.entity.getX();
        this.y = this.entity.getY();
        this.z = this.entity.getZ();
    }
}
