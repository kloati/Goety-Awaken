package com.k1sak1.goetyawaken.common.effects;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.ColorUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PotentVenomEffect extends MobEffect {
    private static final ColorUtil PARTICLE_COLOR = new ColorUtil(0x9ACD32);

    public PotentVenomEffect() {
        super(MobEffectCategory.HARMFUL, 0x5B8C3E);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ModParticleTypes.BIG_CULT_SPELL.get(),
                    livingEntity.getRandomX(0.5D),
                    livingEntity.getRandomY(),
                    livingEntity.getRandomZ(0.5D),
                    0,
                    PARTICLE_COLOR.red(),
                    PARTICLE_COLOR.green(),
                    PARTICLE_COLOR.blue(),
                    0.5F);
        }
    }

    @Override
    public boolean isDurationEffectTick(int tick, int amplifier) {
        return tick % 20 == 0;
    }
}
