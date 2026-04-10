package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class TormentorServant extends Summoned {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(TormentorServant.class,
            EntityDataSerializers.BYTE);
    private Mob owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;

    public TormentorServant(EntityType<? extends Summoned> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.moveControl = new MobUtil.MinionMoveControl(this);
        this.xpReward = 6;
    }

    public void move(MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.followGoal();
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(8, new MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new CopyOwnerTargetGoal(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(6, new TormentorFollowGoal(this, 1.0D, 2.0F, 10.0F, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TormentorHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.TormentorArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TormentorDamage.get());
    }

    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.hurt(this.damageSources().starve(), 1.0F);
        }
        if (!this.level().isClientSide()) {
            if (!this.isCharging()) {
                this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 0, false, false));
            } else {
                this.removeEffect(MobEffects.INVISIBILITY);
            }
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    private boolean getVexFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(pCompound.getInt("BoundX"), pCompound.getInt("BoundY"),
                    pCompound.getInt("BoundZ"));
        }

        if (pCompound.contains("LifeTicks")) {
            this.setLimitedLife(pCompound.getInt("LifeTicks"));
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.boundOrigin != null) {
            pCompound.putInt("BoundX", this.boundOrigin.getX());
            pCompound.putInt("BoundY", this.boundOrigin.getY());
            pCompound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.hasLimitedLife) {
            pCompound.putInt("LifeTicks", this.limitedLifeTicks);
        }

    }

    public Mob getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos pBoundOrigin) {
        this.boundOrigin = pBoundOrigin;
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setIsCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    public void setOwner(Mob pOwner) {
        this.owner = pOwner;
    }

    public void setLimitedLife(int pLimitedLifeTicks) {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = pLimitedLifeTicks;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.TORMENTOR_AMBIENT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.TORMENTOR_DEATH.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.TORMENTOR_HURT.get();
    }

    public float getBrightness() {
        return 1.0F;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomSource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(randomSource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.TORMENTOR_CELEBRATE.get();
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (TormentorServant.this.getTarget() != null && !TormentorServant.this.getMoveControl().hasWanted()
                    && TormentorServant.this.random.nextInt(7) == 0) {
                return TormentorServant.this.distanceToSqr(TormentorServant.this.getTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return TormentorServant.this.getMoveControl().hasWanted() && TormentorServant.this.isCharging()
                    && TormentorServant.this.getTarget() != null && TormentorServant.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = TormentorServant.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.position();
                TormentorServant.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                TormentorServant.this.setIsCharging(true);
                TormentorServant.this.playSound(ModSounds.TORMENTOR_CHARGE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            TormentorServant.this.setIsCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = TormentorServant.this.getTarget();
            if (livingentity != null) {
                if (TormentorServant.this.getBoundingBox().inflate(1.0F).intersects(livingentity.getBoundingBox())) {
                    TormentorServant.this.doHurtTarget(livingentity);
                    TormentorServant.this.setIsCharging(false);
                } else {
                    double d0 = TormentorServant.this.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vector3d = livingentity.getEyePosition(1.0F);
                        TormentorServant.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                    }
                }
            }

        }
    }

    class CopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight()
                .ignoreInvisibilityTesting();

        public CopyOwnerTargetGoal(PathfinderMob p_i47231_2_) {
            super(p_i47231_2_, false);
        }

        public boolean canUse() {
            return TormentorServant.this.owner != null && TormentorServant.this.owner.getTarget() != null
                    && this.canAttack(TormentorServant.this.owner.getTarget(), this.copyOwnerTargeting);
        }

        public void start() {
            TormentorServant.this.setTarget(TormentorServant.this.owner.getTarget());
            super.start();
        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return !TormentorServant.this.getMoveControl().hasWanted() && TormentorServant.this.random.nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = TormentorServant.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = TormentorServant.this.blockPosition();
            }

            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(TormentorServant.this.random.nextInt(15) - 7,
                        TormentorServant.this.random.nextInt(11) - 5, TormentorServant.this.random.nextInt(15) - 7);
                if (TormentorServant.this.level().isEmptyBlock(blockpos1)) {
                    TormentorServant.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D,
                            (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    if (TormentorServant.this.getTarget() == null) {
                        TormentorServant.this.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D,
                                (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    class TormentorFollowGoal extends Goal {
        private final TormentorServant tormentorServant;
        private LivingEntity owner;
        private final Level level;
        private final double followSpeed;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float maxDist;
        private final float minDist;
        private float oldWaterCost;
        private final boolean teleportToLeaves;

        public TormentorFollowGoal(TormentorServant tormentorServant, double speed, float minDist, float maxDist,
                boolean teleportToLeaves) {
            this.tormentorServant = tormentorServant;
            this.level = tormentorServant.level();
            this.followSpeed = speed;
            this.navigation = tormentorServant.getNavigation();
            this.minDist = minDist;
            this.maxDist = maxDist;
            this.teleportToLeaves = teleportToLeaves;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(tormentorServant.getNavigation() instanceof GroundPathNavigation)
                    && !(tormentorServant.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity livingentity = this.tormentorServant.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (livingentity instanceof net.minecraft.world.entity.Mob
                    && !(livingentity instanceof TormentorServant)) {
                return false;
            } else if (this.tormentorServant.distanceToSqr(livingentity) < (double) (this.minDist * this.minDist)) {
                return false;
            } else if (!this.tormentorServant.isFollowing()) {
                return false;
            } else if (this.tormentorServant.isStaying()) {
                return false;
            } else if (this.tormentorServant.getTarget() != null) {
                return false;
            } else if (this.tormentorServant.isCharging()) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.tormentorServant.getTarget() != null) {
                return false;
            } else if (this.tormentorServant.isCharging()) {
                return false;
            } else if (this.navigation.isDone()) {
                return false;
            } else {
                return !(this.tormentorServant.distanceToSqr(this.owner) <= (double) (this.maxDist * this.maxDist));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.tormentorServant.getPathfindingMalus(BlockPathTypes.WATER);
            this.tormentorServant.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.navigation.stop();
            this.tormentorServant.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.tormentorServant.getLookControl().setLookAt(this.owner, 10.0F,
                    (float) this.tormentorServant.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (this.tormentorServant.distanceTo(this.owner) > 8.0D) {
                    double x = Math.floor(this.owner.getX()) - 2;
                    double y = Math.floor(this.owner.getBoundingBox().minY);
                    double z = Math.floor(this.owner.getZ()) - 2;
                    for (int l = 0; l <= 4; ++l) {
                        for (int i1 = 0; i1 <= 4; ++i1) {
                            if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
                                    && this.validPosition(BlockPos.containing(x + l, y + 2, z + i1))) {
                                float a = (float) ((x + l) + 0.5F);
                                float b = (float) ((z + i1) + 0.5F);
                                this.tormentorServant.getMoveControl().setWantedPosition(a, y, b, this.followSpeed);
                                this.navigation.stop();
                            }
                        }
                    }
                }
                if (this.tormentorServant.distanceToSqr(this.owner) > 144.0) {
                    this.tryToTeleportNearEntity();
                }
            }
        }

        private void tryToTeleportNearEntity() {
            BlockPos blockpos = this.owner.blockPosition();

            for (int i = 0; i < 10; ++i) {
                int j = this.getRandomNumber(-3, 3);
                int k = this.getRandomNumber(-1, 1);
                int l = this.getRandomNumber(-3, 3);
                boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k,
                        blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }

        }

        private boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(BlockPos.containing(x, y, z))) {
                return false;
            } else {
                this.tormentorServant.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D,
                        this.tormentorServant.getYRot(),
                        this.tormentorServant.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        private boolean isTeleportFriendlyBlock(BlockPos pos) {
            net.minecraft.world.level.pathfinder.BlockPathTypes pathnodetype = net.minecraft.world.level.pathfinder.WalkNodeEvaluator
                    .getBlockPathTypeStatic(this.level, pos.mutable());
            if (pathnodetype != net.minecraft.world.level.pathfinder.BlockPathTypes.WALKABLE) {
                return false;
            } else {
                net.minecraft.world.level.block.state.BlockState blockstate = this.level.getBlockState(pos.below());
                if (!this.teleportToLeaves
                        && blockstate.getBlock() instanceof net.minecraft.world.level.block.LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.tormentorServant.blockPosition());
                    return this.level.noCollision(this.tormentorServant,
                            this.tormentorServant.getBoundingBox().move(blockpos));
                }
            }
        }

        protected boolean validPosition(BlockPos pos) {
            net.minecraft.world.level.block.state.BlockState blockstate = this.level.getBlockState(pos);
            return (blockstate.canSurvive(this.level, pos) && this.level.isEmptyBlock(pos.above())
                    && this.level.isEmptyBlock(pos.above(2)));
        }

        private int getRandomNumber(int min, int max) {
            return this.tormentorServant.getRandom().nextInt(max - min + 1) + min;
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.tormentorServantLimit;
    }
}