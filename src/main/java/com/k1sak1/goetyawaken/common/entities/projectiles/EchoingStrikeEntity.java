package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.events.EchoEffectHandler;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;

public class EchoingStrikeEntity extends AoeEntity {
    public EchoingStrikeEntity(EntityType<EchoingStrikeEntity> entityType, Level level) {
        super(entityType, level);
        this.setCircular();
    }

    private DamageSource originalDamageSource;

    public EchoingStrikeEntity(Level level, LivingEntity owner, float damage, float radius) {
        super(ModEntityType.ECHOING_STRIKE.get(), level);
        this.setOwner(owner);
        this.setRadius(radius);
        this.setDamage(damage);
    }

    public void setOriginalDamageSource(DamageSource damageSource) {
        this.originalDamageSource = damageSource;
    }

    @Override
    public void applyEffect(LivingEntity target) {
    }

    public final int waitTime = 20;

    @Override
    public void tick() {
        if (tickCount == waitTime) {
            this.playSound(com.Polarice3.Goety.init.ModSounds.SOUL_EXPLODE.get(), 0.3F, 1.0F);
            if (!level().isClientSide()) {
                var center = this.getBoundingBox().getCenter();
                if (level() instanceof ServerLevel serverLevel) {
                    com.Polarice3.Goety.client.particles.ShockwaveParticleOption shockwaveOption = new com.Polarice3.Goety.client.particles.ShockwaveParticleOption(
                            1.0f, 1.0f, 1.0f,
                            getRadius() * 2.0f,
                            getRadius(),
                            2,
                            20,
                            true);
                    serverLevel.sendParticles(shockwaveOption, center.x, center.y, center.z, 0, 0.0D, 0.0D, 0.0D, 0);
                    ColorUtil colorUtil = new ColorUtil(0xFFFFFF);
                    serverLevel.sendParticles(
                            new SphereExplodeParticleOption(colorUtil, getRadius() * 0.5F, 1),
                            center.x, center.y + 0.5D, center.z, 1, 0, 0, 0, 0);
                }

                float explosionRadius = getRadius();
                var explosionRadiusSqr = explosionRadius * explosionRadius;
                var entities = level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));

                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity livingEntity) {
                        double distanceSqr = entity.distanceToSqr(center);
                        if (distanceSqr < explosionRadiusSqr && canHitEntity(entity)) {
                            double p = Mth.clamp((1 - distanceSqr / explosionRadiusSqr) + .4f, 0, 1);
                            float damage = (float) (this.damage * p);
                            DamageSource echoDamage;
                            if (this.originalDamageSource != null) {
                                echoDamage = new DamageSource(
                                        this.originalDamageSource.typeHolder(), this,
                                        this.getOwner()) {
                                    @Override
                                    public String getMsgId() {
                                        return EchoEffectHandler.ECHO_DAMAGE_MARKER;
                                    }
                                };
                            } else {
                                echoDamage = new DamageSource(
                                        entity.damageSources().mobAttack((LivingEntity) this.getOwner()).typeHolder(),
                                        this,
                                        (Entity) this.getOwner()) {
                                    @Override
                                    public String getMsgId() {
                                        return EchoEffectHandler.ECHO_DAMAGE_MARKER;
                                    }
                                };
                            }
                            livingEntity.invulnerableTime = 0;
                            livingEntity.hurt(echoDamage, damage);
                        }
                    }
                }
            }
        } else if (tickCount > waitTime) {
            discard();
        }

        if (level().isClientSide() && tickCount < waitTime / 2) {
            Vec3 position = this.getBoundingBox().getCenter();
            for (int i = 0; i < 3; i++) {
                Vec3 vec3 = new Vec3(
                        (random.nextFloat() - 0.5) * 2,
                        (random.nextFloat() - 0.5) * 2,
                        (random.nextFloat() - 0.5) * 2);
                vec3 = vec3.multiply(vec3)
                        .multiply(Mth.sign((float) vec3.x), Mth.sign((float) vec3.y), Mth.sign((float) vec3.z))
                        .scale(this.getRadius()).add(position);
                Vec3 motion = position.subtract(vec3).scale(.125f);
                level().addParticle(ParticleTypes.END_ROD, vec3.x, vec3.y - .5, vec3.z, motion.x, motion.y, motion.z);
            }
        }
    }

    @Override
    protected boolean canHitTargetForGroundContext(LivingEntity target) {
        return true;
    }

    @Override
    public net.minecraft.world.entity.EntityDimensions getDimensions(net.minecraft.world.entity.Pose pPose) {
        return net.minecraft.world.entity.EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    @Override
    public void ambientParticles() {
    }

    @Override
    public float getParticleCount() {
        return 0;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}