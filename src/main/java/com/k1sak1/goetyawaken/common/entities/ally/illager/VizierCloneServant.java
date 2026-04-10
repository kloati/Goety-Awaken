package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import java.util.List;

import javax.annotation.Nullable;

public class VizierCloneServant extends SpellcasterIllagerServant implements RangedAttackMob {
    private static final EntityDataAccessor<Integer> POSITION = SynchedEntityData.defineId(VizierCloneServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> VIZIER_FLAGS = SynchedEntityData.defineId(VizierCloneServant.class,
            EntityDataSerializers.BYTE);
    public int spellCool = 200;

    public VizierCloneServant(EntityType<? extends VizierCloneServant> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new MobUtil.MinionMoveControl(this);
    }

    @Override
    public void move(net.minecraft.world.entity.MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    @Override
    public void tick() {
        if (this.isEffectiveAi()) {
            if (this.getTrueOwner() == null || !(this.getTrueOwner() instanceof VizierServant)) {
                if (!this.level().isClientSide) {
                    for (int i = 0; i < this.level().random.nextInt(35) + 10; ++i) {
                        ServerLevel serverLevel = (ServerLevel) this.level();
                        serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), 0, 0.0D,
                                0.0D, 0.0D, 0.05D);
                    }
                }
                this.discard();
                return;
            }
            LivingEntity trueOwner = this.getTrueOwner();
            if (trueOwner != null) {
                if (this.distanceTo(trueOwner) > 32.0F) {
                    List<LivingEntity> nearbyMobs = this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(32.0D));
                    boolean ownerNearby = false;
                    for (LivingEntity mob : nearbyMobs) {
                        if (mob == trueOwner) {
                            ownerNearby = true;
                            break;
                        }
                    }

                    if (!ownerNearby) {
                        if (!this.level().isClientSide) {
                            for (int i = 0; i < this.level().random.nextInt(35) + 10; ++i) {
                                ServerLevel serverLevel = (ServerLevel) this.level();
                                serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(),
                                        0, 0.0D,
                                        0.0D, 0.0D, 0.05D);
                            }
                        }
                        this.discard();
                        return;
                    }
                }
            }

            if (trueOwner != null && trueOwner.isDeadOrDying()) {
                if (!this.level().isClientSide) {
                    for (int i = 0; i < this.level().random.nextInt(35) + 10; ++i) {
                        ServerLevel serverLevel = (ServerLevel) this.level();
                        serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getEyeY(), this.getZ(), 0, 0.0D,
                                0.0D, 0.0D, 0.05D);
                    }
                }
                this.discard();
            } else {
                if (trueOwner != null) {
                    if (this.distanceTo(trueOwner) > 32.0F) {
                        LivingEntity target = this.getTarget();
                        if (target != null && this.distanceTo(target) > 8.0F) {
                            this.moveTo(Vec3.atCenterOf(com.Polarice3.Goety.utils.BlockFinder.SummonPosition(this,
                                    trueOwner.blockPosition())));
                        } else if (target == null) {
                            this.moveTo(Vec3.atCenterOf(com.Polarice3.Goety.utils.BlockFinder.SummonPosition(this,
                                    trueOwner.blockPosition())));
                        }
                    }
                    if (trueOwner instanceof VizierServant vizierOwner && vizierOwner.isSpellcasting()) {
                        double x;
                        double z;
                        if (this.getVizierPosition() == 0) { // LEFT
                            x = com.Polarice3.Goety.utils.MobUtil.getHorizontalLeftLookAngle(trueOwner).x * 4;
                            z = com.Polarice3.Goety.utils.MobUtil.getHorizontalLeftLookAngle(trueOwner).z * 4;
                        } else {
                            x = com.Polarice3.Goety.utils.MobUtil.getHorizontalRightLookAngle(trueOwner).x * 4; // RIGHT
                            z = com.Polarice3.Goety.utils.MobUtil.getHorizontalRightLookAngle(trueOwner).z * 4;
                        }
                        Vec3 vector3d = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                        if (!this.level().isClientSide) {
                            double d0 = vector3d.y;
                            if (this.getY() < trueOwner.getY() + 1.0D) {
                                d0 = Math.max(0.0D, d0);
                                d0 = d0 + (0.3D - d0 * (double) 0.6F);
                            }
                            vector3d = new Vec3(vector3d.x, d0, vector3d.z);
                            Vec3 vector3d1 = new Vec3((trueOwner.getX() + x) - this.getX(), 0.0D,
                                    (trueOwner.getZ() + z) - this.getZ());
                            if (getHorizontalDistanceSqr(vector3d1) > 9.0D) {
                                Vec3 vector3d2 = vector3d1.normalize();
                                vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D,
                                        vector3d2.z * 0.3D - vector3d.z * 0.6D);
                            }
                        }
                        this.setDeltaMovement(vector3d);
                        LivingEntity currentTarget = this.getTarget();
                        if (currentTarget != null) {
                            if (this.distanceTo(currentTarget) > 3.0F) {
                                com.Polarice3.Goety.utils.MobUtil.instaLook(this, currentTarget);
                            }
                        } else {
                            if (vizierOwner.getTarget() != null) {
                                com.Polarice3.Goety.utils.MobUtil.instaLook(this, vizierOwner.getTarget());
                            }
                        }
                    }
                }
            }
        }
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);

        if (this.spellCool > 0) {
            --this.spellCool;
        }
    }

    public static double getHorizontalDistanceSqr(Vec3 pVector) {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ShootSpellGoal());
        this.goalSelector.addGoal(1, new MoveRandomGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(9, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this,
                net.minecraft.world.entity.player.Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this,
                net.minecraft.world.entity.Mob.class, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && VizierCloneServant.this.getTarget() == null;
            }
        });
        this.targetSelector.addGoal(0, new CopyOwnerTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.VizierHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.VizierHealth.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.VIZIER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.VIZIER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.VIZIER_HURT.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.VIZIER_CELEBRATE.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_,
            MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        this.populateDefaultEquipmentSlots(p_37856_.getRandom(), p_37857_);
        this.populateDefaultEquipmentEnchantments(p_37856_.getRandom(), p_37857_);
        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource randomSource,
            DifficultyInstance difficulty) {
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
    }

    public boolean isCharging() {
        return (this.entityData.get(VIZIER_FLAGS) & 1) != 0;
    }

    public void setCharging(boolean charging) {
        byte b0 = this.entityData.get(VIZIER_FLAGS);
        if (charging) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }
        this.entityData.set(VIZIER_FLAGS, b0);
    }

    public void setSpellcasting(boolean spellcasting) {
        byte b0 = this.entityData.get(VIZIER_FLAGS);
        if (spellcasting) {
            b0 = (byte) (b0 | 2);
        } else {
            b0 = (byte) (b0 & -3);
        }
        this.entityData.set(VIZIER_FLAGS, b0);
    }

    public boolean isSpellcasting() {
        return (this.entityData.get(VIZIER_FLAGS) & 2) != 0;
    }

    public int getVizierPosition() {
        return this.entityData.get(POSITION);
    }

    public void setVizierPosition(int type) {
        this.entityData.set(POSITION, type);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(POSITION, 0);
        this.entityData.define(VIZIER_FLAGS, (byte) 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Position")) {
            this.setVizierPosition(compound.getInt("Position"));
        }
        if (compound.contains("SpellCool")) {
            this.spellCool = compound.getInt("SpellCool");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getVizierPosition() > -1 && this.getVizierPosition() < 2) {
            compound.putInt("Position", this.getVizierPosition());
        }
        compound.putInt("SpellCool", this.spellCool);
    }

    @Override
    public IllagerServantArmPose getArmPose() {
        if (this.isCharging()) {
            return IllagerServantArmPose.ATTACKING;
        } else if (this.isSpellcasting()) {
            return IllagerServantArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating() ? IllagerServantArmPose.CELEBRATING : IllagerServantArmPose.NEUTRAL;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isEffectiveAi()) {
            return super.hurt(source, amount);
        }
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(source, amount);
        }

        return false;
    }

    class ShootSpellGoal extends SpellcasterUseSpellGoal {
        int duration;
        int duration2;

        @Override
        public boolean canUse() {
            boolean canUse = VizierCloneServant.this.getTrueOwner() != null
                    && VizierCloneServant.this.getTarget() != null
                    && VizierCloneServant.this.spellCool <= 0
                    && !VizierCloneServant.this.isCharging()
                    && VizierCloneServant.this.getTrueOwner() instanceof VizierServant
                    && ((VizierServant) VizierCloneServant.this.getTrueOwner()).isSpellcasting();

            return canUse;
        }

        @Override
        public void start() {
            super.start();
            VizierCloneServant.this.setSpellcasting(true);
            VizierCloneServant.this.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
        }

        @Override
        public void stop() {
            super.stop();
            VizierCloneServant.this.setSpellcasting(false);
            VizierCloneServant.this.spellCool = 100 + VizierCloneServant.this.random.nextInt(100);
            this.duration2 = 0;
            this.duration = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = VizierCloneServant.this.getTarget();
            if (livingentity != null) {
                ++this.duration;
                ++this.duration2;
                if (!VizierCloneServant.this.level().isClientSide) {
                    ServerLevel serverWorld = (ServerLevel) VizierCloneServant.this.level();
                    for (int i = 0; i < 5; ++i) {
                        double d0 = serverWorld.random.nextGaussian() * 0.02D;
                        double d1 = serverWorld.random.nextGaussian() * 0.02D;
                        double d2 = serverWorld.random.nextGaussian() * 0.02D;
                        serverWorld.sendParticles(ParticleTypes.ENCHANT, VizierCloneServant.this.getRandomX(1.0D),
                                VizierCloneServant.this.getRandomY() + 1.0D, VizierCloneServant.this.getRandomZ(1.0D),
                                0, d0, d1, d2, 0.5F);
                    }
                }
                int time = 20;
                if (this.duration >= time) {
                    this.duration = 0;
                    this.attack(livingentity);
                }
                if (this.duration2 >= 160) {
                    VizierCloneServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
                    VizierCloneServant.this.setSpellcasting(false);
                    VizierCloneServant.this.spellCool = 100 + VizierCloneServant.this.random.nextInt(100);
                    this.duration2 = 0;
                    this.duration = 0;
                }
            } else {
                stop();
            }
        }

        private void attack(LivingEntity livingEntity) {
            com.Polarice3.Goety.common.entities.projectiles.SwordProjectile swordProjectile = new com.Polarice3.Goety.common.entities.projectiles.SwordProjectile(
                    VizierCloneServant.this, VizierCloneServant.this.level(),
                    VizierCloneServant.this.getMainHandItem());
            double d0 = livingEntity.getX() - VizierCloneServant.this.getX();
            double d1 = livingEntity.getY(0.3333333333333333D) - swordProjectile.getY();
            double d2 = livingEntity.getZ() - VizierCloneServant.this.getZ();
            double d3 = (double) Math.sqrt((float) (d0 * d0 + d2 * d2));
            swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            swordProjectile.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F,
                    (float) (14 - VizierCloneServant.this.level().getDifficulty().getId() * 4));
            if (!VizierCloneServant.this.getSensing().hasLineOfSight(livingEntity)) {
                swordProjectile.setNoPhysics(true);
            }
            VizierCloneServant.this.level().addFreshEntity(swordProjectile);
            if (!VizierCloneServant.this.isSilent()) {
                VizierCloneServant.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
            }
        }

        @Override
        protected void performSpellCasting() {
        }

        @Override
        protected int getCastWarmupTime() {
            return 20;
        }

        @Override
        protected int getCastingTime() {
            return 160;
        }

        @Override
        protected int getCastingInterval() {
            return 200;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }

    class MoveRandomGoal extends net.minecraft.world.entity.ai.goal.Goal {
        public boolean canUse() {
            if (VizierCloneServant.this.getTrueOwner() instanceof VizierServant owner && owner.isStaying()) {
                return false;
            }
            return !VizierCloneServant.this.getMoveControl().hasWanted()
                    && VizierCloneServant.this.random.nextInt(7) == 0
                    && !VizierCloneServant.this.isCharging();
        }

        public void tick() {
            double speed = 0.25D;
            net.minecraft.core.BlockPos blockpos = VizierCloneServant.this.blockPosition();
            if (VizierCloneServant.this.getTarget() != null) {
                speed = 1.0D;
                blockpos = VizierCloneServant.this.getTarget().blockPosition();
            }

            for (int i = 0; i < 3; ++i) {
                net.minecraft.core.BlockPos blockpos1 = blockpos.offset(VizierCloneServant.this.random.nextInt(8) - 4,
                        VizierCloneServant.this.random.nextInt(6) - 2, VizierCloneServant.this.random.nextInt(8) - 4);
                if (VizierCloneServant.this.level().isEmptyBlock(blockpos1)) {
                    VizierCloneServant.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D,
                            (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, speed);
                    if (VizierCloneServant.this.getTarget() == null) {
                        VizierCloneServant.this.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D,
                                (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }

    class ChargeAttackGoal extends net.minecraft.world.entity.ai.goal.Goal {
        public boolean canUse() {
            if (VizierCloneServant.this.getTrueOwner() instanceof VizierServant owner && owner.isStaying()) {
                return false;
            }
            LivingEntity target = VizierCloneServant.this.getTarget();
            if (target != null
                    && !VizierCloneServant.this.getMoveControl().hasWanted()
                    && !VizierCloneServant.this.isCastingSpell()) {
                return VizierCloneServant.this.distanceToSqr(target) > 8.0D
                        || VizierCloneServant.this.random.nextInt(reducedTickDelay(100)) == 0;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity target = VizierCloneServant.this.getTarget();
            return VizierCloneServant.this.getMoveControl().hasWanted()
                    && VizierCloneServant.this.isCharging()
                    && !VizierCloneServant.this.isCastingSpell()
                    && target != null
                    && target.isAlive();
        }

        public void start() {
            LivingEntity livingentity = VizierCloneServant.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.position();
                VizierCloneServant.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                VizierCloneServant.this.setCharging(true);
                VizierCloneServant.this.playSound(ModSounds.VIZIER_CELEBRATE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            VizierCloneServant.this.setCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = VizierCloneServant.this.getTarget();
            if (livingentity != null) {
                VizierCloneServant.this.getLookControl().setLookAt(livingentity.position());
                if (VizierCloneServant.this.getBoundingBox().inflate(1.0D).intersects(livingentity.getBoundingBox())) {
                    VizierCloneServant.this.doHurtTarget(livingentity);
                    VizierCloneServant.this.setCharging(false);
                } else {
                    double d0 = VizierCloneServant.this.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vector3d = livingentity.getEyePosition();
                        VizierCloneServant.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                    }
                }
            }
        }
    }

    class CopyOwnerTargetGoal extends net.minecraft.world.entity.ai.goal.target.TargetGoal {
        private final net.minecraft.world.entity.ai.targeting.TargetingConditions copyOwnerTargeting = net.minecraft.world.entity.ai.targeting.TargetingConditions
                .forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public CopyOwnerTargetGoal(net.minecraft.world.entity.PathfinderMob p_34056_) {
            super(p_34056_, false);
        }

        public boolean canUse() {
            net.minecraft.world.entity.LivingEntity owner = VizierCloneServant.this.getTrueOwner();
            if (owner != null && owner instanceof net.minecraft.world.entity.Mob mobOwner) {
                net.minecraft.world.entity.LivingEntity ownerTarget = mobOwner.getTarget();
                return ownerTarget != null
                        && this.canAttack(ownerTarget, this.copyOwnerTargeting);
            }
            return false;
        }

        public boolean canContinueToUse() {
            return this.canUse();
        }

        public void start() {
            net.minecraft.world.entity.LivingEntity owner = VizierCloneServant.this.getTrueOwner();
            if (owner != null && owner instanceof net.minecraft.world.entity.Mob mobOwner) {
                net.minecraft.world.entity.LivingEntity ownerTarget = mobOwner.getTarget();
                if (ownerTarget != null
                        && this.canAttack(ownerTarget, this.copyOwnerTargeting)) {
                    VizierCloneServant.this.setTarget(ownerTarget);
                }
            }
            super.start();
        }

        public void tick() {
            super.tick();
        }
    }
}