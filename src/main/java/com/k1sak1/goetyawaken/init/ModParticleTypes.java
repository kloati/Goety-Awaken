package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.particle.RingParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
            .create(ForgeRegistries.PARTICLE_TYPES, GoetyAwaken.MODID);

    public static final RegistryObject<ParticleType<RingParticle.RingData>> RING = PARTICLE_TYPES
            .register("tower_guard_ring",
                    () -> new ParticleType<>(false, RingParticle.RingData.DESERIALIZER) {
                        @Override
                        public com.mojang.serialization.Codec<RingParticle.RingData> codec() {
                            return RingParticle.RingData.CODEC(RING.get());
                        }
                    });
}
