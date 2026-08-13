package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.init.ModMobType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BurningShield extends Summoned implements IServant {

    public static final float DEFAULT_ORBIT_RADIUS = 2.4F;
    public static final float ORBIT_SPEED = 1.0F;
    public static final int MAX_SHIELDS_PER_OWNER = com.k1sak1.goetyawaken.Config.BURNING_SHIELD_LIMIT.get();
    private static final float SMOOTH_LERP_FACTOR = 0.6F;
    private static final int SMOOTH_TRANSITION_DURATION = 10;
    private static final float DAMAGE_SHARE_PER_SHIELD = 0.2F;
    private static final float MAX_DAMAGE_SHARE = 0.8F;
    private static final float DAMAGE_SHARE_MULTIPLIER = 1.5F;
    private static final double SHIELD_CHECK_RANGE = 4.0D;
    private static final int MAX_OWNER_MISSING_TICKS = 100;
    private int prevShieldCount = 0;
    private int smoothTransitionTicks = 0;
    private int burningLevel = 0;
    private double explosionRadiusLevel = 0.0D;
    private int ownerMissingTicks = 0;

    public BurningShield(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    public int xpReward() {
        return 0;
    }

    @Override
    protected void registerGoals() {

    }

    protected int decreaseAirSupply(int p_28882_) {
        return p_28882_;
    }

    public boolean onClimbable() {
        return false;
    }

    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isInWall() {
        return false;
    }

    protected void checkFallDamage(double p_27419_, boolean p_27420_, BlockState p_27421_, BlockPos p_27422_) {
    }

    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BurningShieldHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return false;
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getTrueOwner();
        if (!this.level().isClientSide) {
            if (owner == null || !owner.isAlive() || owner.isRemoved()) {
                this.ownerMissingTicks++;
                if (this.ownerMissingTicks > MAX_OWNER_MISSING_TICKS) {
                    this.discard();
                }
                super.tick();
                return;
            }
            this.ownerMissingTicks = 0;

            if (owner.level() != this.level()) {
                this.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                return;
            }
            UUID ownerId = this.getOwnerId();
            List<BurningShield> allShields = this.level().getEntitiesOfClass(BurningShield.class,
                    owner.getBoundingBox().inflate(16.0D),
                    s -> s != this && ownerId != null && ownerId.equals(s.getOwnerId()) && s.isAlive());
            if (allShields.size() >= MAX_SHIELDS_PER_OWNER) {
                this.discard();
                return;
            }

            if (this.tickCount % 10 == 0 && (this.isInLava() || this.isOnFire())
                    && this.getHealth() < this.getMaxHealth()) {
                this.heal(1.0F);
            }

            if (this.distanceToSqr(owner) > 1024.0D) {
                this.teleportTo(owner.getX(), owner.getY(), owner.getZ());
            }
        }
        super.tick();
        if (hurtTime > 0 || hurtDuration > 0) {
            this.hurtDuration = 0;
            this.hurtTime = 0;
        }
        if (owner != null && this.level() == owner.level()) {
            this.updateOrbitPosition(owner);
            this.updateFacing(owner);
        }
        if (this.isDeadOrDying()) {
            this.discard();
        }
    }

    private void updateOrbitPosition(LivingEntity owner) {
        List<BurningShield> allShields = this.level().getEntitiesOfClass(BurningShield.class,
                owner.getBoundingBox().inflate(64.0D),
                s -> s.getTrueOwner() == owner && s.isAlive());

        allShields.sort(Comparator.comparing(Entity::getUUID));
        int myIndex = allShields.indexOf(this);
        int total = allShields.size();
        if (total <= 0)
            total = 1;
        if (myIndex < 0)
            myIndex = 0;
        if (total != this.prevShieldCount) {
            this.smoothTransitionTicks = SMOOTH_TRANSITION_DURATION;
            this.prevShieldCount = total;
        }

        float sharedAngle = (this.level().getGameTime() * ORBIT_SPEED * 0.05F) % Mth.TWO_PI;
        if (sharedAngle < 0)
            sharedAngle += Mth.TWO_PI;

        float baseOffset = (Mth.TWO_PI / total) * myIndex;
        float actualAngle = baseOffset + sharedAngle;

        double targetX = owner.getX() + DEFAULT_ORBIT_RADIUS * Math.cos(actualAngle);
        double targetY = owner.getY() + owner.getEyeHeight() * 0.3;
        double targetZ = owner.getZ() + DEFAULT_ORBIT_RADIUS * Math.sin(actualAngle);

        double dx = targetX - this.getX();
        double dy = targetY - this.getY();
        double dz = targetZ - this.getZ();

        if (this.smoothTransitionTicks > 0) {
            this.smoothTransitionTicks--;
            this.setPos(
                    this.getX() + dx * SMOOTH_LERP_FACTOR,
                    this.getY() + dy * SMOOTH_LERP_FACTOR,
                    this.getZ() + dz * SMOOTH_LERP_FACTOR);
        } else {
            this.setPos(targetX, targetY, targetZ);
        }
    }

    private void updateFacing(LivingEntity owner) {
        double dx = this.getX() - owner.getX();
        double dz = this.getZ() - owner.getZ();

        float yaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        this.setYRot(yaw);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.DRY_OUT) || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.STARVE) || source.is(DamageTypes.CRAMMING)
                || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.STING)
                || source.is(DamageTypes.THORNS) || source.is(DamageTypeTags.IS_FALL)
                || source.is(DamageTypeTags.IS_DROWNING)) {
            return false;
        }
        if (source.is(DamageTypeTags.IS_FREEZING)) {
            amount *= 2;
        }
        return super.hurt(source, amount);

    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.WILDFIRE_HURT.get();
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(Math.min(1.0D + this.explosionRadiusLevel, 4.0D)))) {
                if (target != this && target != this.getTrueOwner()
                        && !(target instanceof BurningShield) && !MobUtil.areAllies(this, target)) {
                    Vec3 knockback = target.position().subtract(this.position()).normalize().scale(1.5D);
                    target.push(knockback.x, 0.5D, knockback.z);
                    target.hurtMarked = true;
                    if (this.burningLevel > 0) {
                        target.setRemainingFireTicks(20 * 3 * this.burningLevel);
                    }
                }
            }
            this.playSound(ModSounds.WILDFIRE_SHIELD_BREAK.get(), 1.2F, 1.0F);
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 20; i++) {
                    double dx = (this.random.nextDouble() - 0.5D) * 2.0D;
                    double dy = this.random.nextDouble() * 2.0D;
                    double dz = (this.random.nextDouble() - 0.5D) * 2.0D;
                    serverLevel.sendParticles(ParticleTypes.LAVA,
                            this.getX(), this.getY() + 0.5D, this.getZ(),
                            0, dx, dy, dz, 0.5D);
                }
            }
        }
        super.die(source);
        this.discard();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    protected void doPush(Entity entity) {
        if (this.level().isClientSide) {
            return;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner == null) {
            return;
        }
        if (entity == owner) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity && MobUtil.areAllies(owner, livingEntity)) {
            return;
        }
        if (entity instanceof LivingEntity) {
            double dx = entity.getX() - this.getX();
            double dz = entity.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 0.01D) {
                dx = this.random.nextDouble() - 0.5D;
                dz = this.random.nextDouble() - 0.5D;
                dist = Math.sqrt(dx * dx + dz * dz);
            }
            double pushStrength = 0.15D;
            entity.setDeltaMovement(entity.getDeltaMovement().add(
                    (dx / dist) * pushStrength,
                    0.05D,
                    (dz / dist) * pushStrength));
            entity.hurtMarked = true;
        }
    }

    @Override
    public boolean canCollideWith(Entity other) {
        LivingEntity owner = this.getTrueOwner();
        if (owner == null) {
            return super.canCollideWith(other);
        }
        if (other == owner) {
            return false;
        }
        if (other instanceof LivingEntity livingOther && MobUtil.areAllies(owner, livingOther)) {
            return false;
        }

        return super.canCollideWith(other);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.BURNING_SHIELD_LIMIT.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BurningLevel", this.burningLevel);
        compound.putDouble("ExplosionRadiusLevel", this.explosionRadiusLevel);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BurningLevel")) {
            this.burningLevel = compound.getInt("BurningLevel");
        }
        if (compound.contains("ExplosionRadiusLevel")) {
            this.explosionRadiusLevel = compound.getDouble("ExplosionRadiusLevel");
        }
    }

    @Override
    public boolean canUpdateMove() {
        return false;
    }

    @Override
    public boolean isCommanded() {
        return false;
    }

    @Override
    public boolean canBeCommanded() {
        return false;
    }

    @Override
    public boolean isStaying() {
        return false;
    }

    @Override
    public boolean isWandering() {
        return false;
    }

    @Override
    public void setCommandPosEntity(LivingEntity arg0) {

    }

    @Override
    public void setStaying(boolean arg0) {

    }

    @Override
    public void setWandering(boolean arg0) {

    }

    public int getBurningLevel() {
        return this.burningLevel;
    }

    public void setBurningLevel(int level) {
        this.burningLevel = level;
    }

    public double getExplosionRadiusLevel() {
        return this.explosionRadiusLevel;
    }

    public void setExplosionRadiusLevel(double level) {
        this.explosionRadiusLevel = level;
    }

    public void tryKill(Player player) {
        this.hurt(ModDamageSource.getDamageSource(this.level(), ModDamageSource.DISMISSED, new EntityType[0]),
                Float.MAX_VALUE);
    }

    @Override
    public boolean canSpawnArmor() {
        return false;
    }

    @SubscribeEvent
    public static void onOwnerHurt(LivingHurtEvent event) {
        LivingEntity owner = event.getEntity();

        if (owner instanceof BurningShield) {
            return;
        }

        if (owner.level().isClientSide) {
            return;
        }

        float originalDamage = event.getAmount();
        if (originalDamage <= 0) {
            return;
        }

        UUID ownerUUID = owner.getUUID();

        List<BurningShield> shields = owner.level().getEntitiesOfClass(
                BurningShield.class,
                owner.getBoundingBox().inflate(SHIELD_CHECK_RANGE),
                shield -> {
                    UUID shieldOwnerUUID = shield.getOwnerId();
                    return shieldOwnerUUID != null && shieldOwnerUUID.equals(ownerUUID) && shield.isAlive();
                });

        if (shields.isEmpty()) {
            return;
        }

        int shieldCount = shields.size();
        float absorbPercent = Math.min(shieldCount * DAMAGE_SHARE_PER_SHIELD, MAX_DAMAGE_SHARE);
        float totalAbsorbed = originalDamage * absorbPercent;

        event.setAmount(originalDamage - totalAbsorbed);

        float perShieldDamage = totalAbsorbed * DAMAGE_SHARE_MULTIPLIER / shieldCount;
        DamageSource source = event.getSource();

        for (BurningShield shield : shields) {
            shield.hurt(source, perShieldDamage);
        }
    }
}