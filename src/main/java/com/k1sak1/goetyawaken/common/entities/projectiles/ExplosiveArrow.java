package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.utils.MobEffectUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;

public class ExplosiveArrow extends AbstractArrow {
    private static final int EXPLOSION_RADIUS = 2;
    private static final int MAX_TRAILS = 48;

    private static final EntityDataAccessor<Boolean> DATA_HAS_TRAIL = SynchedEntityData.defineId(ExplosiveArrow.class,
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

    public ExplosiveArrow(EntityType<? extends ExplosiveArrow> entityType, Level level) {
        super(entityType, level);
        this.setBaseDamage(6.0D);
    }

    public ExplosiveArrow(Level level, LivingEntity shooter) {
        super(ModEntityType.EXPLOSIVE_ARROW.get(), shooter, level);
        this.setBaseDamage(6.0D);
    }

    public ExplosiveArrow(Level level, double x, double y, double z) {
        super(ModEntityType.EXPLOSIVE_ARROW.get(), x, y, z, level);
        this.setBaseDamage(6.0D);
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

    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null) {
            if (pEntity == this.getOwner()) {
                return false;
            }
            if (this.getOwner() instanceof Mob mob && mob.getTarget() == pEntity) {
                return super.canHitEntity(pEntity);
            } else {
                if (MobUtil.areAllies(this.getOwner(), pEntity)) {
                    return false;
                }
            }
        }
        return super.canHitEntity(pEntity);
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
    protected void onHitEntity(EntityHitResult result) {
        this.explode();
        super.onHitEntity(result);
    }

    protected void onHit(HitResult p_37628_) {
        super.onHit(p_37628_);
        if (!this.level().isClientSide) {
            this.explode();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.explode();
        super.onHitBlock(result);
    }

    private void explode() {
        if (!this.level().isClientSide) {
            double magicDamageRadius = 2.0D;
            float baseMagicDamage = 2.0F;
            float ownerAttackDamage = 0.0F;

            if (this.getOwner() != null && this.getOwner() instanceof LivingEntity livingOwner) {
                ownerAttackDamage = (float) livingOwner
                        .getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
            }

            float magicDamage = baseMagicDamage + (ownerAttackDamage / 2.0F);

            java.util.List<net.minecraft.world.entity.LivingEntity> entities = this.level().getEntitiesOfClass(
                    net.minecraft.world.entity.LivingEntity.class,
                    this.getBoundingBox().inflate(magicDamageRadius));

            for (net.minecraft.world.entity.LivingEntity entity : entities) {
                if (entity != this.getOwner() && entity.isAlive()) {
                    if (this.canHitEntity(entity)) {
                        entity.invulnerableTime = 0;
                        entity.hurt(com.k1sak1.goetyawaken.utils.ModDamageSource.explosiveArrow(this, this.getOwner()),
                                magicDamage);
                        applyVisualDisturbanceEffect(entity);
                    }
                }
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                ColorUtil colorUtil = new ColorUtil(0x00a8ff);
                Vec3 vec3 = this.position();
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                EXPLOSION_RADIUS * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                EXPLOSION_RADIUS, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                EXPLOSION_RADIUS * 2, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                EXPLOSION_RADIUS, 1),
                        vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(),
                                EXPLOSION_RADIUS * 2.0F, 1),
                        vec3.x, vec3.y + 0.5D, vec3.z, 1, 0, 0, 0, 0);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    getRandomIllusionerArrowSound(), this.getSoundSource(), 1.5F, 1.5F);
            this.discard();
        }
    }

    private SoundEvent getRandomIllusionerArrowSound() {
        SoundEvent[] sounds = {
                com.k1sak1.goetyawaken.init.ModSounds.ILLUSIONER_ARROW1.get(),
                com.k1sak1.goetyawaken.init.ModSounds.ILLUSIONER_ARROW2.get(),
                com.k1sak1.goetyawaken.init.ModSounds.ILLUSIONER_ARROW3.get(),
                com.k1sak1.goetyawaken.init.ModSounds.ILLUSIONER_ARROW4.get()
        };

        return sounds[this.random.nextInt(sounds.length)];
    }

    private void applyVisualDisturbanceEffect(LivingEntity target) {
        if (this.getOwner() != null) {
            if (target == this.getOwner() || com.Polarice3.Goety.utils.MobUtil.areAllies(this.getOwner(), target)) {
                return;
            }
        }
        net.minecraft.world.effect.MobEffectInstance currentEffect = target
                .getEffect(ModEffects.VISUAL_DISTURBANCE.get());

        int newAmplifier = 0;
        int newDuration = 200;

        if (currentEffect != null) {
            newAmplifier = Math.min(currentEffect.getAmplifier() + 1, 4);
            newDuration = 200;
        }
        MobEffectUtils.forceAdd(target, new net.minecraft.world.effect.MobEffectInstance(
                ModEffects.VISUAL_DISTURBANCE.get(),
                newDuration,
                newAmplifier), this.getOwner());
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (!this.hasTrail()) {
                this.setHasTrail(true);
            }
        } else {
            this.handleClientTick();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClientTick() {
        if (this.hasTrail()) {
            this.initializeTrail();
            this.updateTrail();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void initializeTrail() {
        if (this.hasTrail() && this.getTrailPositions().isEmpty()) {
            this.getTrailPositions().add(new TrailPosition(this.position(), 0));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updateTrail() {
        if (this.getTrailPositions().size() < MAX_TRAILS) {
            this.getTrailPositions().add(new TrailPosition(this.position(), 0));
        }
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityType.EXPLOSIVE_ARROW.get();
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasTrail", this.hasTrail());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HasTrail")) {
            this.setHasTrail(compound.getBoolean("HasTrail"));
        }
    }
}