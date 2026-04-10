package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.k1sak1.goetyawaken.common.entities.ally.SilverfishServant;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SilverfishEggEntity extends ThrowableProjectile {
    private int radiusLevel = 0;
    private int powerLevel = 0;
    private int speedLevel = 0;
    private int durationLevel = 0;
    private float extraDamage = 0.0F;

    public SilverfishEggEntity(EntityType<? extends SilverfishEggEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SilverfishEggEntity(EntityType<? extends SilverfishEggEntity> entityType, Level level,
            LivingEntity shooter) {
        super(entityType, shooter, level);
    }

    public SilverfishEggEntity(EntityType<? extends SilverfishEggEntity> entityType, double x, double y, double z,
            Level level) {
        super(entityType, x, y, z, level);
    }

    public void setRadiusLevel(int level) {
        this.radiusLevel = level;
    }

    public void setPowerLevel(int level) {
        this.powerLevel = level;
    }

    public void setSpeedLevel(int level) {
        this.speedLevel = level;
    }

    public void setDurationLevel(int level) {
        this.durationLevel = level;
    }

    public int getRadiusLevel() {
        return this.radiusLevel;
    }

    public int getPowerLevel() {
        return this.powerLevel;
    }

    public int getSpeedLevel() {
        return this.speedLevel;
    }

    public int getDurationLevel() {
        return this.durationLevel;
    }

    public void setExtraDamage(float damage) {
        this.extraDamage = damage;
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.hurt(this.damageSources().thrown(this, this.getOwner()), this.extraDamage);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            float explosionRadius = 0.5F + (0.25F * this.radiusLevel);
            if (explosionRadius > 0) {
                this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(explosionRadius))
                        .forEach(entity -> {
                            if (entity != this.getOwner()) {
                                entity.hurt(this.damageSources().thrown(this, this.getOwner()), this.extraDamage);
                            }
                        });
            }
            this.spawnSilverfish();
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(ParticleTypes.CRIT,
                        this.getX(), this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.1D,
                        this.random.nextGaussian() * 0.1D,
                        this.random.nextGaussian() * 0.1D);
            }

            this.discard();
        }
    }

    private void spawnSilverfish() {
        if (!this.level().isClientSide) {
            int count = 1 + this.random.nextInt(2);

            for (int i = 0; i < count; i++) {
                SilverfishServant silverfish = new SilverfishServant(ModEntityType.SILVERFISH_SERVANT.get(),
                        this.level());
                silverfish.setPos(this.getX(), this.getY(), this.getZ());
                if (this.getOwner() instanceof LivingEntity) {
                    silverfish.setTrueOwner((LivingEntity) this.getOwner());
                }
                int lifespan = 1200 + (this.durationLevel * 200);
                silverfish.setLimitedLife(lifespan);
                this.level().addFreshEntity(silverfish);
            }
        }
    }

    @Override
    protected float getGravity() {
        return 0.03F;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float adjustedVelocity = velocity + (0.1F * this.speedLevel);
        super.shoot(x, y, z, adjustedVelocity, inaccuracy);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}