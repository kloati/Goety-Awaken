package com.k1sak1.goetyawaken.common.entities.ally.undead;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.ai.MinionFollowGoal;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.Polarice3.Goety.common.items.ModItems;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ScarletVex extends Minion implements ICustomAttributes {
    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> TARGET_CLIENT_ID = net.minecraft.network.syncher.SynchedEntityData
            .defineId(ScarletVex.class,
                    net.minecraft.network.syncher.EntityDataSerializers.INT);
    private int seeTime;

    public ScarletVex(EntityType<? extends ScarletVex> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.navigation = this.createNavigation(p_i50190_2_);
        this.seeTime = 0;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean canSee(LivingEntity target) {
        if (target == null) {
            return false;
        }
        Vec3 vexPos = this.getEyePosition(1.0F);
        Vec3 targetPos = target.getEyePosition(1.0F);
        net.minecraft.world.phys.HitResult hitResult = this.level()
                .clip(new net.minecraft.world.level.ClipContext(vexPos, targetPos,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE, this));
        return hitResult.getType() == net.minecraft.world.phys.HitResult.Type.MISS;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.syncTargetToClient();
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double d2 = target.getX() - ScarletVex.this.getX();
            double d1 = target.getZ() - ScarletVex.this.getZ();
            ScarletVex.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
            ScarletVex.this.yBodyRot = ScarletVex.this.getYRot();
            if (this.canSee(target)) {
                ScarletVex.this.seeTime++;
                if (ScarletVex.this.seeTime >= 60) {
                    Level level = ScarletVex.this.level();
                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        float attackDamage = (float) ScarletVex.this
                                .getAttributeValue(
                                        net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                        net.minecraft.world.damagesource.DamageSource damageSource = com.Polarice3.Goety.utils.ModDamageSource
                                .lifeLeech(ScarletVex.this, ScarletVex.this.getTrueOwner());
                        target.hurt(damageSource, attackDamage);
                        ScarletVex.this.setIsCharging(false);
                        ScarletVex.this.seeTime = 0;
                    }
                }
                if (!this.level().isClientSide && target != null && target.isAlive()) {
                    net.minecraft.world.effect.MobEffectInstance slowEffect = new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false);
                    net.minecraft.world.effect.MobEffectInstance weakEffect = new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.WEAKNESS, 40, 0, false, false);
                    target.addEffect(slowEffect, this.getTrueOwner());
                    target.addEffect(weakEffect, this.getTrueOwner());
                    double pullX = ScarletVex.this.getX() - target.getX();
                    double pullZ = ScarletVex.this.getZ() - target.getZ();
                    double distance = Math.sqrt(pullX * pullX + pullZ * pullZ);
                    if (distance > 0) {
                        double pullStrength = 0.05;
                        Vec3 currentMotion = target.getDeltaMovement();
                        target.setDeltaMovement(
                                currentMotion.x + (pullX / distance) * pullStrength,
                                currentMotion.y,
                                currentMotion.z + (pullZ / distance) * pullStrength);
                    }
                }
            }
        }
        LivingEntity owner = this.getTrueOwner();
        if (this.getTrueOwner() != null) {
            if (this.getTrueOwner().isDeadOrDying()) {
                this.kill();
            }
        }
        if (MobsConfig.ServantOwnedServantPlayerBenefit.get()) {
            owner = this.getMasterOwner();
        }
        if (owner != null) {
            boolean crown = CuriosFinder.hasCurio(owner, ModItems.NAMELESS_CROWN.get());
            if (!crown) {
                if (this.getLifespan() > 0) {
                    this.setHasLifespan(true);
                }
            } else {
                this.setHasLifespan(false);
            }
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OutofBoundsGoal());
        this.goalSelector.addGoal(2, new MinionFollowGoal(this, 0.5D, 6.0f, 3.0f, true));
        this.goalSelector.addGoal(8, new MoveRandomGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ScarletVexHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ScarletVexDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ScarletVexHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.ScarletVexDamage.get());
    }

    public void die(DamageSource cause) {
        if (cause.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.heal(2.0F);
        }
        super.die(cause);
    }

    public int getSeeTime() {
        return this.seeTime;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_CLIENT_ID, -1);
    }

    @Nullable
    public LivingEntity getTargetClient() {
        if (!this.level().isClientSide) {
            return this.getTarget();
        } else {
            int id = this.getTargetClientId();
            return id <= -1 ? null : (LivingEntity) this.level().getEntity(id);
        }
    }

    public int getTargetClientId() {
        return this.entityData.get(TARGET_CLIENT_ID);
    }

    public void setTargetClientId(int id) {
        this.entityData.set(TARGET_CLIENT_ID, id);
    }

    public void syncTargetToClient() {
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.setTargetClientId(target.getId());
        } else {
            this.setTargetClientId(-1);
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.seeTime = compound.getInt("seeTime");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("seeTime", this.seeTime);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.VEX_HURT;
    }

    class OutofBoundsGoal extends Goal {
        public OutofBoundsGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return ScarletVex.this.isInWall() && !ScarletVex.this.getMoveControl().hasWanted();
        }

        public boolean canContinueToUse() {
            return ScarletVex.this.isInWall() && !ScarletVex.this.getMoveControl().hasWanted();
        }

        public void tick() {
            BlockPos.MutableBlockPos blockpos$mutable = ScarletVex.this.blockPosition().mutable();
            blockpos$mutable.setY(ScarletVex.this.level()
                    .getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
            ScarletVex.this.getMoveControl().setWantedPosition(blockpos$mutable.getX(), blockpos$mutable.getY(),
                    blockpos$mutable.getZ(), 1.0F);
        }

    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (ScarletVex.this.getTarget() != null && !ScarletVex.this.getMoveControl().hasWanted()) {
                return !ScarletVex.this.getTarget().isAlliedTo(ScarletVex.this);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return ScarletVex.this.getMoveControl().hasWanted()
                    && ScarletVex.this.isCharging()
                    && ScarletVex.this.getTarget() != null
                    && ScarletVex.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = ScarletVex.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.getEyePosition(1.0F);
                if (ScarletVex.this.distanceTo(livingentity) > 4.0F) {
                    ScarletVex.this.getMoveControl().setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0F);
                }
            }
        }

        public void stop() {
            ScarletVex.this.setIsCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = ScarletVex.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d0 = livingentity.getEyePosition(1.0F);
                Vec3 vector3d = ScarletVex.this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                double d0 = vector3d.y;
                if (ScarletVex.this.getY() < vector3d0.y) {
                    d0 = Math.max(0.0D, d0);
                    d0 = d0 + (0.3D - d0 * (double) 0.6F);
                }
                vector3d = new Vec3(vector3d.x, d0, vector3d.z);
                Vec3 vector3d1 = new Vec3(vector3d0.x - ScarletVex.this.getX(), 0.0D,
                        vector3d0.z - ScarletVex.this.getZ());
                if (getHorizontalDistanceSqr(vector3d1) > 9.0D) {
                    Vec3 vector3d2 = vector3d1.normalize();
                    vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D,
                            vector3d2.z * 0.3D - vector3d.z * 0.6D);
                }
                ScarletVex.this.setDeltaMovement(vector3d);
                double d2 = ScarletVex.this.getTarget().getX() - ScarletVex.this.getX();
                double d1 = ScarletVex.this.getTarget().getZ() - ScarletVex.this.getZ();
                ScarletVex.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                ScarletVex.this.yBodyRot = ScarletVex.this.getYRot();
            } else {
                ScarletVex.this.setIsCharging(false);
            }
        }
    }

    public static double getHorizontalDistanceSqr(Vec3 pVector) {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !ScarletVex.this.getMoveControl().hasWanted()
                    && ScarletVex.this.random.nextInt(7) == 0
                    && !ScarletVex.this.isCharging();
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = ScarletVex.this.blockPosition();

            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(ScarletVex.this.random.nextInt(8) - 4,
                        ScarletVex.this.random.nextInt(6) - 2, ScarletVex.this.random.nextInt(8) - 4);
                if (ScarletVex.this.level().isEmptyBlock(blockpos1)) {
                    ScarletVex.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D,
                            (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    if (ScarletVex.this.getTarget() == null) {
                        ScarletVex.this.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D,
                                (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
