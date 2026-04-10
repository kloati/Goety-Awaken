package com.k1sak1.goetyawaken.common.entities.ally.undead;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.Polarice3.Goety.common.magic.spells.ShockwaveSpell;
import com.Polarice3.Goety.common.magic.spells.storm.ElectroOrbSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import com.k1sak1.goetyawaken.common.blocks.AllyPithosBlock;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.blocks.entity.AllyPithosBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SkullLordServant extends Summoned implements ICustomAttributes {
    protected static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(SkullLordServant.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> BONE_LORD = SynchedEntityData.defineId(
            SkullLordServant.class,
            EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> BONE_LORD_CLIENT_ID = SynchedEntityData.defineId(
            SkullLordServant.class,
            EntityDataSerializers.INT);
    private BlockPos boundOrigin;
    private int spawnDelay = 100;
    private int spawnNumber = 0;
    private int chargeTime = 0;
    private int shockWaveCool = 0;
    private int oldSwell;
    private int swell;
    public float explosionRadius = 2.0F;
    public int boneLordRegen;
    private int hitTimes;
    private int stuckTime = 0;

    public SkullLordServant(EntityType<? extends SkullLordServant> p_i50190_1_, Level p_i50190_2_) {
        super(p_i50190_1_, p_i50190_2_);
        this.navigation = this.createNavigation(p_i50190_2_);
        this.moveControl = new MoveHelperController(this);
        this.hitTimes = 0;
        this.setPersistenceRequired();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(4, new SoulSkullGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(4, new ElectroOrbAttackGoal());
        this.goalSelector.addGoal(4, new ShockwaveAttackGoal());
        this.goalSelector.addGoal(8, new MoveRandomGoal());
        this.goalSelector.addGoal(7, new LookAroundGoal(this));
        super.registerGoals();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkullLordDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkullLordHealth.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SkullLordHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SkullLordDamage.get());
    }

    public void move(MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanPassDoors(false);
        return flyingpathnavigator;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerable()) {
            return false;
        } else {
            ++this.hitTimes;
            if (this.hitTimes > 3 || pAmount >= 20) {
                pAmount /= 2;
            }
            return super.hurt(pSource, pAmount);
        }
    }

    public void checkDespawn() {
        this.setIsDespawn(true);
        super.checkDespawn();
    }

    @Override
    public boolean fireImmune() {
        return this.isInvulnerable();
    }

    protected boolean isAffectedByFluids() {
        return false;
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos pBoundOrigin) {
        this.boundOrigin = pBoundOrigin;
    }

    static class MoveHelperController extends MoveControl {
        private final SkullLordServant skullLordServant;
        private int floatDuration;

        public MoveHelperController(SkullLordServant p_i45838_1_) {
            super(p_i45838_1_);
            this.skullLordServant = p_i45838_1_;
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.skullLordServant.getRandom().nextInt(5) + 2;
                    Vec3 vector3d = new Vec3(this.wantedX - this.skullLordServant.getX(),
                            this.wantedY - this.skullLordServant.getY(),
                            this.wantedZ - this.skullLordServant.getZ());
                    double d0 = vector3d.length();
                    vector3d = vector3d.normalize();
                    if (this.canReach(vector3d, Mth.ceil(d0))) {
                        this.skullLordServant
                                .setDeltaMovement(this.skullLordServant.getDeltaMovement().add(vector3d.scale(0.1D)));
                    } else {
                        this.operation = Operation.WAIT;
                    }
                }

            }
        }

        private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
            AABB axisalignedbb = this.skullLordServant.getBoundingBox();

            for (int i = 1; i < p_220673_2_; ++i) {
                axisalignedbb = axisalignedbb.move(p_220673_1_);
                if (!this.skullLordServant.level().noCollision(this.skullLordServant, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
        int delay = switch (this.level().getDifficulty()) {
            case NORMAL -> 300;
            case HARD -> 150;
            default -> 400;
        };
        Vec3 vector3d = this.getDeltaMovement();
        double d0 = this.getX() + vector3d.x;
        double d1 = this.getY() + vector3d.y;
        double d2 = this.getZ() + vector3d.z;
        ParticleOptions particleData = ParticleTypes.SMOKE;
        if (this.isSpawning()) {
            particleData = ParticleTypes.FLAME;
        } else if (this.isInvulnerable()) {
            particleData = ParticleTypes.POOF;
        } else if (this.isShockWave()) {
            particleData = ParticleTypes.SOUL_FIRE_FLAME;
        } else if (this.isElectroOrb()) {
            particleData = ModParticleTypes.ELECTRIC.get();
        }
        this.level().addParticle(particleData, d0 + this.random.nextGaussian() * (double) 0.3F,
                d1 + this.random.nextGaussian() * (double) 0.3F, d2 + this.random.nextGaussian() * (double) 0.3F, 0.0D,
                0.0D, 0.0D);
        if (this.isInvulnerable()) {
            int healFreq = switch (this.level().getDifficulty()) {
                case NORMAL -> 40;
                case HARD -> 20;
                default -> 60;
            };
            if (this.tickCount % healFreq == 0) {
                this.heal(1.0F);
            }
        }
        if (this.isOnFire()) {
            if (this.tickCount % 100 == 0 || this.isInvulnerable()) {
                this.clearFire();
            }
        }
        // if (this.isFollowing() && this.getTrueOwner() != null) {
        // this.boundOrigin = this.getTrueOwner().blockPosition().above(2);
        // BlockPos blockPos = this.boundOrigin;
        // if (this.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) >
        // 1024) {
        // this.moveTo(blockPos, 0, 0);
        // if (this.getBoneLord() != null) {
        // this.getBoneLord().moveTo(blockPos, 0, 0);
        // }
        // }
        // }
        if (this.getTarget() != null) {
            if (this.getTarget().isDeadOrDying() || this.getTarget().isRemoved()) {
                this.setTarget(null);
            }
        }
        if (this.isShockWave()) {
            this.oldSwell = this.swell;
            this.swell += 1;
        } else {
            this.oldSwell = 0;
            this.swell = 0;
        }
        if (!this.level().isClientSide) {
            BoneLordServant connectedBoneLord = this.getBoneLord();
            if (connectedBoneLord != null && connectedBoneLord.getId() != this.getBoneLordClientId()) {
                this.setBoneLordClientId(connectedBoneLord.getId());
            }
            ServerLevel serverWorld = (ServerLevel) this.level();
            int i = this.blockPosition().getX();
            int j = this.blockPosition().getY();
            int k = this.blockPosition().getZ();
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class,
                    (new AABB(i, j, k, i, j - 4, k)).inflate(8.0D, 8.0D, 8.0D),
                    (t -> t instanceof IOwned owned && owned.getTrueOwner() == this));
            if (list.size() < 8 && this.getTarget() != null && (this.isNotAbility() || this.isSpawning())) {
                int warn = this.isHalfHealth() ? 20 : 40;
                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    if (this.spawnDelay == warn) {
                        this.playSound(ModSounds.PREPARE_SUMMON.get());
                    }
                    if (this.spawnDelay <= warn) {
                        this.setDeltaMovement(Vec3.ZERO);
                        this.setSpawning(true);
                    } else {
                        this.setSpawning(false);
                    }
                } else {
                    this.spawnDelay = this.level().random.nextInt(delay) + delay;
                    this.setSpawning(false);
                    this.spawnMobs();
                }
            }
            if (this.getBoneLord() == null || (this.getBoneLord() != null && this.getBoneLord().isDeadOrDying())) {
                --this.boneLordRegen;
                this.setIsInvulnerable(false);
                this.level().broadcastEntityEvent(this, (byte) 5);
                for (BoneLordServant boneLord : this.level().getEntitiesOfClass(BoneLordServant.class,
                        this.getBoundingBox().inflate(32))) {
                    if (boneLord.getSkullLord() == this) {
                        this.setBoneLord(boneLord);
                    }
                }
                if (this.boneLordRegen <= 0 && this.isNotAbility()) {
                    BoneLordServant boneLord = com.k1sak1.goetyawaken.common.entities.ModEntityType.BONE_LORD_SERVANT
                            .get().create(this.level());
                    if (boneLord != null) {
                        LivingEntity trueOwner = this.getTrueOwner();
                        if (trueOwner instanceof Player) {
                            boneLord.setTrueOwner(trueOwner);
                        }
                        boneLord.finalizeSpawn(serverWorld, this.level().getCurrentDifficultyAt(this.blockPosition()),
                                MobSpawnType.MOB_SUMMONED, null, null);
                        boneLord.setPos(this.getX(), this.getY(), this.getZ());
                        boneLord.setSkullLord(this);
                        this.setBoneLord(boneLord);
                        this.level().addFreshEntity(boneLord);
                    }
                }
            } else {
                if (this.getBoneLord() != null) {
                    this.drawAttachParticleBeam(this, this.getBoneLord());
                    if (this.distanceToSqr(this.getBoneLord()) > Mth.square(16)) {
                        this.moveTo(this.getBoneLord().position());
                    }
                    this.setIsInvulnerable(true);
                    this.level().broadcastEntityEvent(this, (byte) 4);
                    this.hitTimes = 0;
                    this.boneLordRegen = delay * 2;
                    this.stopAttackersFromAttacking();
                }
            }
            if (this.isCharging()) {
                ++this.chargeTime;
                for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(2.0F))) {
                    if (!(livingEntity instanceof BoneLordServant) && livingEntity != this
                            && livingEntity != this.getTarget()) {
                        if (this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                            this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius,
                                    Level.ExplosionInteraction.NONE);
                            if (this.random.nextFloat() < 0.25F) {
                                this.setIsCharging(false);
                            }
                        }
                    }
                }
                if (this.horizontalCollision || this.verticalCollision) {
                    this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius,
                            Level.ExplosionInteraction.NONE);
                    this.setIsCharging(false);
                }
                if (this.chargeTime >= 100) {
                    this.setIsCharging(false);
                }
            } else {
                this.chargeTime = 0;
            }
            if (this.shockWaveCool > 0) {
                --this.shockWaveCool;
            }
        }
    }

    public void stopAttackersFromAttacking() {
        List<Mob> list = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(100.0F));
        if (this.getBoneLord() != null) {
            for (Mob attacker : list) {
                if (attacker.getLastHurtByMob() == this) {
                    attacker.setLastHurtByMob(this.getBoneLord());
                }

                if (attacker.getTarget() == this) {
                    attacker.setTarget(this.getBoneLord());
                }

                if (attacker instanceof Warden warden) {
                    if (warden.getTarget() == this) {
                        warden.increaseAngerAt(this.getBoneLord(), AngerLevel.ANGRY.getMinimumAnger() + 100, false);
                        warden.setAttackTarget(this.getBoneLord());
                    }
                } else {
                    if (attacker.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                            && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()
                            && attacker.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get() == this) {
                        attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, this.getBoneLord().getUUID(),
                                600L);
                        attacker.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, this.getBoneLord(),
                                600L);
                    }
                }
            }
        }

    }

    public float getSwelling(float p_32321_) {
        return Mth.lerp(p_32321_, (float) this.oldSwell, (float) this.swell) / 28.0F;
    }

    private void drawAttachParticleBeam(LivingEntity pSource, LivingEntity pTarget) {
        double d0 = pTarget.getX() - pSource.getX();
        double d1 = pTarget.getEyeY() - pSource.getY();
        double d2 = pTarget.getZ() - pSource.getZ();
        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        d0 = d0 / d3;
        d1 = d1 / d3;
        d2 = d2 / d3;
        double d4 = pSource.level().random.nextDouble();
        if (!pSource.level().isClientSide) {
            ServerLevel serverWorld = (ServerLevel) pSource.level();
            while (d4 < d3) {
                d4 += 1.0D;
                serverWorld.sendParticles(ModParticleTypes.BONE.get(), pSource.getX() + d0 * d4,
                        pSource.getY() + d1 * d4, pSource.getZ() + d2 * d4, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void spawnMobs() {
        int spawnRange = 2;
        boolean random = this.level().random.nextBoolean();
        if (this.level() instanceof ServerLevel serverLevel) {
            double d0 = (double) this.blockPosition().getX() + this.level().random.nextDouble();
            double d1 = (double) this.blockPosition().getY() + this.level().random.nextDouble();
            double d2 = (double) this.blockPosition().getZ() + this.level().random.nextDouble();
            for (int p = 0; p < 4; ++p) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0, 0, 0, 0);
                serverLevel.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0.0D, 5.0E-4D, 0.0D, 5.0E-4D);
            }
            SoundUtil.playNecromancerSummon(this);
            for (int i = 0; i < 2 + (serverLevel.random.nextInt(2) * serverLevel.random.nextInt(1)); ++i) {
                double d3 = (double) this.blockPosition().getX()
                        + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * (double) spawnRange
                        + 0.5D;
                double d4 = (double) (this.blockPosition().getY() + serverLevel.random.nextInt(3));
                double d5 = (double) this.blockPosition().getZ()
                        + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * (double) spawnRange
                        + 0.5D;
                Summoned summoned;
                if (this.isUnderWater()) {
                    if (random) {
                        summoned = ModEntityType.DROWNED_SERVANT.get().create(serverLevel);
                    } else {
                        summoned = ModEntityType.SUNKEN_SKELETON_SERVANT.get().create(serverLevel);
                    }
                } else {
                    if (random) {
                        if (serverLevel.random.nextFloat() <= 0.8F) {
                            summoned = ModEntityType.FROZEN_ZOMBIE_SERVANT.get().create(serverLevel);
                        } else {
                            summoned = ModEntityType.ZOMBIE_SERVANT.get().create(serverLevel);
                        }
                    } else {
                        if (serverLevel.random.nextFloat() <= 0.8F) {
                            summoned = ModEntityType.STRAY_SERVANT.get().create(serverLevel);
                        } else {
                            summoned = ModEntityType.SKELETON_SERVANT.get().create(serverLevel);
                        }
                    }
                    if (serverLevel.random.nextFloat() <= 0.15F) {
                        summoned = ModEntityType.BORDER_WRAITH_SERVANT.get().create(serverLevel);
                    }
                }
                if (summoned != null) {
                    BlockPos blockPos = BlockPos.containing(d3, d4, d5);
                    summoned.setTrueOwner(this);
                    summoned.setUpgraded(true);
                    summoned.moveTo(BlockFinder.SummonPosition(summoned, blockPos), this.getYRot(), this.getXRot());
                    summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(summoned.blockPosition()),
                            MobSpawnType.MOB_SUMMONED, null, null);
                    serverLevel.addFreshEntityWithPassengers(summoned);
                }
            }
        }
    }

    public void die(DamageSource cause) {
        LivingEntity owner = this.getTrueOwner();
        super.die(cause);
        for (int i = 0; i < this.level().random.nextInt(35) + 10; ++i) {
            float f11 = (this.random.nextFloat() - 0.5F);
            float f13 = (this.random.nextFloat() - 0.5F);
            float f14 = (this.random.nextFloat() - 0.5F);
            this.level().addParticle(ParticleTypes.EXPLOSION, this.getX() + (double) f11,
                    this.getY() + 2.0D + (double) f13, this.getZ() + (double) f14, 0.0D, 0.0D, 0.0D);
        }
        if (!this.level().isClientSide) {
            if (this.getBoneLord() != null) {
                this.getBoneLord().die(cause);
            }
            if (owner instanceof Player player && this.level() instanceof ServerLevel serverLevel) {
                BlockPos spawnPos = this.findSuitableSurfacePosition();
                BlockState pithosState = ModBlocks.ALLY_PITHOS.get().defaultBlockState()
                        .setValue(AllyPithosBlock.LOCKED, false)
                        .setValue(AllyPithosBlock.TRIGGERED, false);
                serverLevel.setBlock(spawnPos, pithosState, 3);
                if (serverLevel.getBlockEntity(spawnPos) instanceof AllyPithosBlockEntity pithosBE) {
                    pithosBE.setOwnerUUID(player.getUUID());
                    pithosBE.setOwnerName(player.getName().getString());
                }
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (!this.level().isClientSide) {
            if (this.getBoneLord() != null) {
                this.getBoneLord().discard();
            }
        }
    }

    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
        if (!pState.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(pState, pMotionMultiplier);
        }

    }

    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.SKULL_LORD_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.SKULL_LORD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.SKULL_LORD_DEATH.get();
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLAGS, (byte) 0);
        this.entityData.define(BONE_LORD, Optional.empty());
        this.entityData.define(BONE_LORD_CLIENT_ID, -1);
    }

    private boolean geFlags(int mask) {
        int i = this.entityData.get(FLAGS);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(FLAGS, (byte) (i & 255));
    }

    @Nullable
    public BoneLordServant getBoneLord() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getBoneLordUUID();
            return EntityFinder.getLivingEntityByUuiD(uuid) instanceof BoneLordServant boneLord ? boneLord : null;
        } else {
            int id = this.getBoneLordClientId();
            return id <= -1 ? null : this.level().getEntity(id) instanceof BoneLordServant boneLord ? boneLord : null;
        }
    }

    @Nullable
    public UUID getBoneLordUUID() {
        return this.entityData.get(BONE_LORD).orElse(null);
    }

    public void setBoneLordUUID(UUID uuid) {
        this.entityData.set(BONE_LORD, Optional.ofNullable(uuid));
    }

    public int getBoneLordClientId() {
        return this.entityData.get(BONE_LORD_CLIENT_ID);
    }

    public void setBoneLordClientId(int id) {
        this.entityData.set(BONE_LORD_CLIENT_ID, id);
    }

    public void setBoneLord(BoneLordServant boneLord) {
        this.setBoneLordUUID(boneLord.getUUID());
        this.setBoneLordClientId(boneLord.getId());
        LivingEntity owner = this.getTrueOwner();
        if (owner != null) {
            boneLord.setTrueOwner(owner);
        } else {
            boneLord.setTrueOwner(this);
        }
    }

    public boolean isCharging() {
        return this.geFlags(1);
    }

    public void setIsCharging(boolean charging) {
        this.setFlags(1, charging);
    }

    public boolean isInvulnerable() {
        return this.geFlags(2);
    }

    public void setIsInvulnerable(boolean invulnerable) {
        this.setFlags(2, invulnerable);
    }

    public boolean isDespawn() {
        return this.geFlags(4);
    }

    public void setIsDespawn(boolean despawn) {
        this.setFlags(4, despawn);
    }

    public boolean isElectroOrb() {
        return this.geFlags(8);
    }

    public void setElectroOrb(boolean electroOrb) {
        this.setFlags(8, electroOrb);
    }

    public boolean isSpawning() {
        return this.geFlags(16);
    }

    public void setSpawning(boolean spawning) {
        this.setFlags(16, spawning);
    }

    public boolean isShockWave() {
        return this.geFlags(32);
    }

    public void setShockWave(boolean shockWave) {
        this.setFlags(32, shockWave);
    }

    @Nullable
    private BlockPos findSuitableSurfacePosition() {
        BlockPos entityPos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        Level level = this.level();

        int searchRadius = 5;

        if (this.isPositionValid(entityPos, level)) {
            return entityPos;
        }

        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int z = -searchRadius; z <= searchRadius; z++) {
                BlockPos checkPos = entityPos.offset(x, 0, z);
                if (this.isPositionValid(checkPos, level)) {
                    return checkPos;
                }
                for (int y = entityPos.getY() - 1; y > level.getMinBuildHeight(); y--) {
                    BlockPos pos = new BlockPos(checkPos.getX(), y, checkPos.getZ());
                    if (this.isPositionValid(pos, level)) {
                        return pos;
                    }
                }
                for (int y = entityPos.getY() + 1; y < level.getMaxBuildHeight(); y++) {
                    BlockPos pos = new BlockPos(checkPos.getX(), y, checkPos.getZ());
                    if (this.isPositionValid(pos, level)) {
                        return pos;
                    }
                }
            }
        }

        return entityPos;
    }

    private boolean isPositionValid(BlockPos pos, Level level) {
        BlockState state = level.getBlockState(pos);
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return (state.isAir() || state.getFluidState().isSource())
                && !belowState.isAir();
    }

    public boolean isNotAbility() {
        return !this.isCharging() && !this.isElectroOrb() && !this.isSpawning() && !this.isShockWave();
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(pCompound.getInt("BoundX"), pCompound.getInt("BoundY"),
                    pCompound.getInt("BoundZ"));
        }
        UUID uuid;
        if (pCompound.hasUUID("boneLord")) {
            uuid = pCompound.getUUID("boneLord");
        } else {
            String s = pCompound.getString("boneLord");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) {
            try {
                this.setBoneLordUUID(uuid);
            } catch (Throwable ignored) {
            }
        }

        if (pCompound.contains("BoneLordClient")) {
            this.setBoneLordClientId(pCompound.getInt("BoneLordClient"));
        }
        this.hitTimes = pCompound.getInt("hitTimes");
        this.boneLordRegen = pCompound.getInt("boneLordRegen");
        this.spawnDelay = pCompound.getInt("spawnDelay");
        this.spawnNumber = pCompound.getInt("spawnNumber");
        this.shockWaveCool = pCompound.getInt("shockWaveCool");
        this.stuckTime = pCompound.getInt("stuckTime");
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.boundOrigin != null) {
            pCompound.putInt("BoundX", this.boundOrigin.getX());
            pCompound.putInt("BoundY", this.boundOrigin.getY());
            pCompound.putInt("BoundZ", this.boundOrigin.getZ());
        }
        if (this.getBoneLordUUID() != null) {
            pCompound.putUUID("boneLord", this.getBoneLordUUID());
        }
        if (this.getBoneLordClientId() > -1) {
            pCompound.putInt("BoneLordClient", this.getBoneLordClientId());
        }
        pCompound.putInt("hitTimes", this.hitTimes);
        pCompound.putInt("boneLordRegen", this.boneLordRegen);
        pCompound.putInt("spawnDelay", this.spawnDelay);
        pCompound.putInt("spawnNumber", this.spawnNumber);
        pCompound.putInt("shockWaveCool", this.shockWaveCool);
        pCompound.putInt("stuckTime", this.stuckTime);
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public boolean isHalfHealth() {
        return this.getHealth() <= this.getMaxHealth() / 2;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.populateDefaultEquipmentSlots(pLevel.getRandom(), pDifficulty);
        this.populateDefaultEquipmentEnchantments(pLevel.getRandom(), pDifficulty);
        if (this.getTrueOwner() != null) {
            BoneLordServant boneLord = com.k1sak1.goetyawaken.common.entities.ModEntityType.BONE_LORD_SERVANT.get()
                    .create((Level) pLevel);
            if (boneLord != null) {
                boneLord.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
                boneLord.setPos(this.getX(), this.getY(), this.getZ());
                boneLord.setSkullLord(this);
                boneLord.setTrueOwner(this.getTrueOwner());
                this.setBoneLord(boneLord);
                pLevel.addFreshEntity(boneLord);
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.setIsInvulnerable(true);
        } else if (p_21375_ == 5) {
            this.setIsInvulnerable(false);
        } else if (p_21375_ == 6) {
            this.setShockWave(true);
        } else if (p_21375_ == 7) {
            this.setShockWave(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    class SoulSkullGoal extends Goal {
        public int shootTime;

        public boolean canUse() {
            if (SkullLordServant.this.getTarget() != null
                    && SkullLordServant.this.isNotAbility()
                    && SkullLordServant.this.hasLineOfSight(SkullLordServant.this.getTarget())
                    && SkullLordServant.this.isInvulnerable()) {
                return !MobUtil.areAllies(SkullLordServant.this.getTarget(), SkullLordServant.this);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLordServant.this.getTarget() != null
                    && SkullLordServant.this.isNotAbility()
                    && SkullLordServant.this.hasLineOfSight(SkullLordServant.this.getTarget())
                    && SkullLordServant.this.getTarget().isAlive()
                    && SkullLordServant.this.getBoneLord() != null;
        }

        public void tick() {
            LivingEntity livingentity = SkullLordServant.this.getTarget();
            if (livingentity != null) {
                int shoot = 45;
                if (SkullLordServant.this.isHalfHealth()) {
                    shoot = 30;
                }
                if (--this.shootTime <= 0) {
                    double d1 = livingentity.getX() - SkullLordServant.this.getX();
                    double d2 = livingentity.getY(0.5D) - SkullLordServant.this.getY(0.5D);
                    double d3 = livingentity.getZ() - SkullLordServant.this.getZ();
                    HauntedSkullProjectile soulSkull = new HauntedSkullProjectile(SkullLordServant.this, d1, d2,
                            d3,
                            SkullLordServant.this.level());
                    soulSkull.setPos(soulSkull.getX(), SkullLordServant.this.getY(0.75D), soulSkull.getZ());
                    soulSkull.setYRot(SkullLordServant.this.getYRot());
                    soulSkull.setXRot(SkullLordServant.this.getXRot());
                    this.shootTime = shoot;
                    SkullLordServant.this.level().addFreshEntity(soulSkull);
                    SkullLordServant.this.playSound(ModSounds.SKULL_LORD_SHOOT.get(), 1.0F, 1.0F);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ChargeAttackGoal extends Goal {
        private Vec3 chargePos;

        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (SkullLordServant.this.getTarget() != null
                    && SkullLordServant.this.isNotAbility()
                    && SkullLordServant.this.hasLineOfSight(SkullLordServant.this.getTarget())
                    && SkullLordServant.this.getBoneLord() == null
                    && SkullLordServant.this.distanceTo(SkullLordServant.this.getTarget()) <= 8) {
                if (!SkullLordServant.this.isHalfHealth()) {
                    return SkullLordServant.this.random.nextInt(60) == 0;
                } else {
                    return SkullLordServant.this.random.nextInt(30) == 0;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLordServant.this.isCharging()
                    && SkullLordServant.this.getTarget() != null
                    && SkullLordServant.this.getBoneLord() == null
                    && SkullLordServant.this.distanceTo(SkullLordServant.this.getTarget()) <= 8
                    && SkullLordServant.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = SkullLordServant.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.getEyePosition(1.0F);
                double dx = SkullLordServant.this.getX() - vector3d.x();
                double dy = SkullLordServant.this.getY() - vector3d.y();
                double dz = SkullLordServant.this.getZ() - vector3d.z();
                double d0 = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double velocity = 2.0D;
                if (SkullLordServant.this.isHalfHealth()) {
                    velocity = 2.5D;
                }
                double xPower = -(dx / d0 * velocity * 0.2D);
                double yPower = -(dy / d0 * velocity * 0.2D);
                double zPower = -(dz / d0 * velocity * 0.2D);
                this.chargePos = new Vec3(xPower, yPower, zPower);
                SkullLordServant.this.setIsCharging(true);
                SkullLordServant.this.playSound(ModSounds.SKULL_LORD_CHARGE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            SkullLordServant.this.setIsCharging(false);
            SkullLordServant.this.setDeltaMovement(Vec3.ZERO);
        }

        public void tick() {
            SkullLordServant skullLordServant = SkullLordServant.this;
            LivingEntity livingentity = SkullLordServant.this.getTarget();
            if (this.chargePos != null) {
                SkullLordServant.this.setDeltaMovement(this.chargePos);
            }
            if (livingentity != null) {
                if (skullLordServant.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    if (livingentity.hurt(
                            skullLordServant.damageSources().indirectMagic(skullLordServant, skullLordServant),
                            (float) skullLordServant.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                        if (skullLordServant.isOnFire()) {
                            livingentity.setSecondsOnFire(5);
                        }
                        if (skullLordServant.isHalfHealth()) {
                            livingentity.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 100));
                        }
                        skullLordServant.level().explode(skullLordServant, skullLordServant.getX(),
                                skullLordServant.getY(), skullLordServant.getZ(),
                                skullLordServant.explosionRadius, Level.ExplosionInteraction.NONE);
                        skullLordServant.setIsCharging(false);
                    }
                }
            }

        }
    }

    class ElectroOrbAttackGoal extends Goal {
        private int electroTime;

        public ElectroOrbAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            if (SkullLordServant.this.getTarget() != null
                    && SkullLordServant.this.isNotAbility()
                    && SkullLordServant.this.getBoneLord() == null
                    && SkullLordServant.this.hasLineOfSight(SkullLordServant.this.getTarget())) {
                if (!SkullLordServant.this.isHalfHealth()) {
                    return SkullLordServant.this.random.nextInt(400) == 0;
                } else {
                    return SkullLordServant.this.random.nextInt(200) == 0;
                }
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLordServant.this.isElectroOrb()
                    && SkullLordServant.this.getTarget() != null
                    && SkullLordServant.this.getBoneLord() == null
                    && this.electroTime > 0
                    && SkullLordServant.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = SkullLordServant.this.getTarget();
            if (livingentity != null) {
                int time = 100;
                if (SkullLordServant.this.isHalfHealth()) {
                    time = 80;
                }
                this.electroTime = time;
                SkullLordServant.this.setElectroOrb(true);
                SkullLordServant.this.playSound(ModSounds.ZAP.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            this.electroTime = 0;
            SkullLordServant.this.setElectroOrb(false);
            SkullLordServant.this.setDeltaMovement(Vec3.ZERO);
        }

        public void tick() {
            SkullLordServant skullLordServant = SkullLordServant.this;
            LivingEntity livingentity = SkullLordServant.this.getTarget();
            if (livingentity != null && skullLordServant.level() instanceof ServerLevel serverLevel) {
                if (this.electroTime > 0) {
                    --this.electroTime;
                    if (!skullLordServant.hasLineOfSight(livingentity)) {
                        skullLordServant.setDeltaMovement(0.0D, 0.08D, 0.0D);
                    } else {
                        skullLordServant.setDeltaMovement(Vec3.ZERO);
                    }
                    double d1 = livingentity.getX() - skullLordServant.getX();
                    double d2 = livingentity.getZ() - skullLordServant.getZ();
                    skullLordServant.getLookControl().setLookAt(livingentity, 10.0F, skullLordServant.getMaxHeadXRot());
                    skullLordServant.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    skullLordServant.yBodyRot = skullLordServant.getYRot();
                    if (this.electroTime > 60) {
                        ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel,
                                ModParticleTypes.BIG_ELECTRIC.get(), skullLordServant);
                    }
                    if (this.electroTime <= 60 && this.electroTime % 10 == 0) {
                        new ElectroOrbSpell().mobSpellResult(skullLordServant, ItemStack.EMPTY);
                    }
                }
            }

        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ShockwaveAttackGoal extends Goal {
        private int shockWave;

        public ShockwaveAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            if (SkullLordServant.this.getTarget() != null
                    && SkullLordServant.this.isNotAbility()
                    && SkullLordServant.this.getBoneLord() == null
                    && SkullLordServant.this.getTarget().distanceTo(SkullLordServant.this) <= 4.0D
                    && SkullLordServant.this.hasLineOfSight(SkullLordServant.this.getTarget())) {
                return SkullLordServant.this.shockWaveCool <= 0;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return SkullLordServant.this.isShockWave()
                    && SkullLordServant.this.getTarget() != null
                    && SkullLordServant.this.getBoneLord() == null
                    && this.shockWave > 0
                    && SkullLordServant.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = SkullLordServant.this.getTarget();
            if (livingentity != null) {
                int time = 60;
                if (SkullLordServant.this.isHalfHealth()) {
                    time = 50;
                }
                this.shockWave = time;
                SkullLordServant.this.setShockWave(true);
                SkullLordServant.this.level().broadcastEntityEvent(SkullLordServant.this, (byte) 6);
                SkullLordServant.this.playSound(ModSounds.BOMB_FUSE.get(), 3.0F, 1.0F);
                SkullLordServant.this.playSound(ModSounds.SKULL_LORD_CHARGE.get(), 3.0F, 0.25F);
            }
        }

        public void stop() {
            this.shockWave = 0;
            if (SkullLordServant.this.isHalfHealth()) {
                SkullLordServant.this.shockWaveCool = 100;
            } else {
                SkullLordServant.this.shockWaveCool = 200;
            }
            SkullLordServant.this.setShockWave(false);
            SkullLordServant.this.level().broadcastEntityEvent(SkullLordServant.this, (byte) 7);
            SkullLordServant.this.setDeltaMovement(Vec3.ZERO);
        }

        public void tick() {
            SkullLordServant skullLordServant = SkullLordServant.this;
            LivingEntity livingentity = SkullLordServant.this.getTarget();
            if (livingentity != null && skullLordServant.level() instanceof ServerLevel serverLevel) {
                if (this.shockWave > 0) {
                    --this.shockWave;
                    skullLordServant.setDeltaMovement(Vec3.ZERO);
                    double d1 = livingentity.getX() - skullLordServant.getX();
                    double d2 = livingentity.getZ() - skullLordServant.getZ();
                    skullLordServant.getLookControl().setLookAt(livingentity, 10.0F, skullLordServant.getMaxHeadXRot());
                    skullLordServant.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    skullLordServant.yBodyRot = skullLordServant.getYRot();
                    ServerParticleUtil.gatheringParticles(ModParticleTypes.LASER_GATHER.get(), skullLordServant,
                            serverLevel);
                    if (this.shockWave == 10) {
                        new ShockwaveSpell().mobSpellResult(skullLordServant, ItemStack.EMPTY);
                    }
                }
            }

        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class MoveRandomGoal extends Goal {
        public MoveRandomGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return SkullLordServant.this.isNotAbility();
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            int distance = 8;
            BlockPos blockPos = null;
            if (SkullLordServant.this.getTrueOwner() != null) {
                SkullLordServant.this.boundOrigin = SkullLordServant.this.getTrueOwner().blockPosition().above(2);
            }
            if (SkullLordServant.this.getBoneLord() != null) {
                blockPos = SkullLordServant.this.getBoneLord().blockPosition().above(2);
            } else if (SkullLordServant.this.getTarget() != null) {
                blockPos = SkullLordServant.this.getTarget().blockPosition().above(2);
            } else if (SkullLordServant.this.getBoundOrigin() != null) {
                blockPos = SkullLordServant.this.getBoundOrigin();
            }

            for (int i = 0; i < 64; ++i) {
                if (blockPos != null) {
                    Vec3 vector3d = Vec3.atCenterOf(blockPos);
                    double X = vector3d.x +
                            SkullLordServant.this.random.nextIntBetweenInclusive(-distance, distance);
                    double Y = vector3d.y +
                            SkullLordServant.this.random.nextIntBetweenInclusive(-distance, distance);
                    double Z = vector3d.z +
                            SkullLordServant.this.random.nextIntBetweenInclusive(-distance, distance);

                    BlockPos blockPos1 = BlockPos.containing(X, Y, Z);
                    if (SkullLordServant.this.level().isEmptyBlock(blockPos1)) {
                        SkullLordServant.this.getMoveControl().setWantedPosition(X, Y, Z, 0.05D);
                        break;
                    }
                } else {
                    distance /= 2;
                    double d0 = SkullLordServant.this.getX()
                            + SkullLordServant.this.random.nextIntBetweenInclusive(-distance, distance);
                    double d1 = SkullLordServant.this.getY()
                            + SkullLordServant.this.random.nextIntBetweenInclusive(-distance, distance);
                    double d2 = SkullLordServant.this.getZ()
                            + SkullLordServant.this.random.nextIntBetweenInclusive(-distance, distance);
                    BlockPos blockPos1 = BlockPos.containing(d0, d1, d2);
                    if (SkullLordServant.this.level().isEmptyBlock(blockPos1)) {
                        SkullLordServant.this.getMoveControl().setWantedPosition(d0, d1, d2, 0.05D);
                        break;
                    }
                }
            }
        }
    }

    static class LookAroundGoal extends Goal {
        private final SkullLordServant skullLordServant;

        public LookAroundGoal(SkullLordServant p_i45839_1_) {
            this.skullLordServant = p_i45839_1_;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (this.skullLordServant.getTarget() == null) {
                Vec3 vector3d = this.skullLordServant.getDeltaMovement();
                this.skullLordServant.setYRot(-((float) Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float) Math.PI));
            } else {
                LivingEntity livingentity = this.skullLordServant.getTarget();
                double d1 = livingentity.getX() - this.skullLordServant.getX();
                double d2 = livingentity.getZ() - this.skullLordServant.getZ();
                this.skullLordServant.getLookControl().setLookAt(livingentity, 10.0F,
                        this.skullLordServant.getMaxHeadXRot());
                this.skullLordServant.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
            }
            this.skullLordServant.yBodyRot = this.skullLordServant.getYRot();

        }
    }

    public void warnKill(Player player) {
        this.killChance = 60;
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("info.goety.servant.tryKill", this.getDisplayName()),
                true);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

}
