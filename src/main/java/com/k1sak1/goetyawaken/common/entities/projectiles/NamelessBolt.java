package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ServantUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NamelessBolt extends SpellHurtingProjectile {
    private LivingEntity homingTarget = null;
    private boolean shouldHoming = false;
    private final float rotSpeed = 0.05F;
    public float roll = (float) Math.random() * ((float) Math.PI * 2F);
    public float oRoll;
    public float getGlow;
    public float glowAmount = 0.05F;

    private static final int MAX_TRAILS = 20;
    private static final EntityDataAccessor<Boolean> DATA_HAS_TRAIL = SynchedEntityData.defineId(NamelessBolt.class,
            EntityDataSerializers.BOOLEAN);

    @OnlyIn(Dist.CLIENT)
    private List<TrailPosition> trailPositions;

    @OnlyIn(Dist.CLIENT)
    private List<TrailPosition> getTrailPositions() {
        if (trailPositions == null) {
            trailPositions = new ArrayList<>();
        }
        return trailPositions;
    }

    @OnlyIn(Dist.CLIENT)
    public List<TrailPosition> getPublicTrailPoints() {
        return getTrailPositions();
    }

    public NamelessBolt(EntityType<? extends SpellHurtingProjectile> p_36833_, Level p_36834_) {
        super(p_36833_, p_36834_);
    }

    public NamelessBolt(double pX, double pY, double pZ, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super((EntityType) ModEntityType.NAMELESS_BOLT.get(), pX, pY, pZ, pXPower, pYPower, pZPower, pLevel);
    }

    public NamelessBolt(LivingEntity pShooter, double pXPower, double pYPower, double pZPower, Level pLevel) {
        super((EntityType) ModEntityType.NAMELESS_BOLT.get(), pShooter, pXPower, pYPower, pZPower, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HAS_TRAIL, false);
    }

    public boolean hasTrail() {
        return this.entityData.get(DATA_HAS_TRAIL);
    }

    public void setHasTrail(boolean hasTrail) {
        this.entityData.set(DATA_HAS_TRAIL, hasTrail);
    }

    public boolean isOnFire() {
        return false;
    }

    protected float getInertia() {
        return 0.9F + this.boltSpeed;
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level().isClientSide) {
            float baseDamage = ((Double) SpellConfig.NecroBoltDamage.get()).floatValue() * WandUtil.damageMultiply();
            Entity entity = pResult.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;
            if (entity1 instanceof LivingEntity livingentity) {
                if (livingentity instanceof Mob mob) {
                    if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null
                            && mob.getAttributeValue(Attributes.ATTACK_DAMAGE) > 0) {
                        baseDamage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    }
                }

                baseDamage += this.getExtraDamage();
                flag = entity.hurt(entity.damageSources().indirectMagic(this, livingentity), baseDamage);
                if (flag) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, entity);
                    } else {
                        ServantUtil.convertZombies(entity, livingentity, true);
                        ServantUtil.convertSkeletons(entity, livingentity, false, true);
                        if (entity instanceof Mob mob) {
                            ServantUtil.infect(mob, livingentity, true, true);
                        }
                    }
                }
            } else {
                flag = entity.hurt(entity.damageSources().magic(), baseDamage);
            }

            if (flag && entity instanceof LivingEntity) {
                double x = this.getX();
                double z = this.getZ();
                if (entity1 != null) {
                    x = entity1.getX();
                    z = entity1.getZ();
                }

                ((LivingEntity) entity).knockback(1.0F, x - entity.getX(), z - entity.getZ());
            }
        }
        if (!this.level().isClientSide) {
            this.createDeathFire();
        }
    }

    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level().isClientSide) {
            this.createDeathFire();
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += (float) Math.PI * this.rotSpeed * 2.0F;
        this.glow();
        if (this.tickCount >= MathHelper.secondsToTicks(5)) {
            this.discard();
        }

        if (this.level().isClientSide) {
            this.handleClientTick();
        } else {
            if (!this.hasTrail()) {
                this.setHasTrail(true);
            }
        }

        if (this.shouldHoming && this.homingTarget != null && !this.homingTarget.isRemoved()) {
            performHoming();
        } else if (this.shouldHoming) {
            this.shouldHoming = false;
        }
    }

    private void handleClientTick() {
        if (this.hasTrail()) {
            this.initializeTrail();
            this.updateTrail();
        }
    }

    private void initializeTrail() {
        if (this.hasTrail() && this.getTrailPositions().isEmpty()) {
            Vec3 centerPos = this.getBoundingBox().getCenter();
            this.getTrailPositions().add(new TrailPosition(centerPos, 0));
        }
    }

    private void updateTrail() {
        if (this.getTrailPositions().size() < MAX_TRAILS) {
            Vec3 centerPos = this.getBoundingBox().getCenter();
            this.getTrailPositions().add(new TrailPosition(centerPos, 0));
        }
    }

    private void performHoming() {
        Vec3 currentMotion = this.getDeltaMovement();
        double speed = currentMotion.length();

        if (speed < 0.1) {
            return;
        }

        Vec3 targetPos = this.homingTarget.getBoundingBox().getCenter();
        Vec3 toTarget = targetPos.subtract(this.position());

        if (currentMotion.dot(toTarget) < 0) {
            this.shouldHoming = false;
            return;
        }

        double distance = toTarget.length();
        double homingStrength = Mth.clamp((distance - 1.0) / 10.0, 0.1, 1.0);

        Vec3 desiredDirection = toTarget.normalize();
        Vec3 newDirection = currentMotion.normalize().scale(1.0 - homingStrength)
                .add(desiredDirection.scale(homingStrength))
                .normalize()
                .scale(speed);

        this.setDeltaMovement(newDirection);

        double d0 = newDirection.horizontalDistance();
        this.setYRot((float) (Mth.atan2(newDirection.x, newDirection.z) * (double) (180F / (float) Math.PI)));
        this.setXRot((float) (Mth.atan2(newDirection.y, d0) * (double) (180F / (float) Math.PI)));
    }

    public void setHomingTarget(LivingEntity target) {
        this.homingTarget = target;
        this.shouldHoming = true;
    }

    public boolean isHoming() {
        return this.shouldHoming && this.homingTarget != null && !this.homingTarget.isRemoved();
    }

    private void createDeathFire() {
        Entity owner = this.getOwner();
        if (owner != null && owner instanceof AbstractNamelessOne namelessone && !namelessone.isEasyMode()) {
            if (owner instanceof LivingEntity livingOwner) {
                DeathFire deathFire = new DeathFire(
                        this.level(),
                        this.blockPosition(),
                        livingOwner);
                deathFire.setExtraDamage(this.getExtraDamage());
                this.level().addFreshEntity(deathFire);
            } else {
                DeathFire deathFire = new DeathFire(
                        this.level(),
                        this.blockPosition(),
                        null);
                deathFire.setExtraDamage(this.getExtraDamage());
                this.level().addFreshEntity(deathFire);
            }
        }
    }

    private void glow() {
        this.getGlow = Mth.clamp(this.getGlow + this.glowAmount, 1.0F, 1.5F);
        if (this.getGlow == 1.0F || this.getGlow == 1.5F) {
            this.glowAmount *= -1.0F;
        }

    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void trailParticle() {

    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}