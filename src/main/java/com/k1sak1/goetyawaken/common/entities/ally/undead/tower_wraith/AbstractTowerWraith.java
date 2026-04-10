package com.k1sak1.goetyawaken.common.entities.ally.undead.tower_wraith;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.FloatSwimGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.world.level.Level;
import com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.TeleportShockwaveParticleOption;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import javax.annotation.Nullable;

public class AbstractTowerWraith extends Summoned implements ICustomAttributes {
    private static final EntityDataAccessor<String> DATA_MODE_ID = SynchedEntityData.defineId(AbstractTowerWraith.class,
            EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData
            .defineId(AbstractTowerWraith.class, EntityDataSerializers.BOOLEAN);;
    private static final EntityDataAccessor<Byte> FLAGS = SynchedEntityData.defineId(AbstractTowerWraith.class,
            EntityDataSerializers.BYTE);;
    private static final EntityDataAccessor<Integer> DATA_CHANGE_TICK = SynchedEntityData.defineId(
            AbstractTowerWraith.class,
            EntityDataSerializers.INT);
    public int fireTick;
    public int fireCooldown;
    public int teleportCooldown;
    public int teleportTime = 20;
    public int teleportTime2;
    public int postTeleportTime;
    public float interestTime;
    public int modeChangeCooldown;
    public double prevX;
    public double prevY;
    public double prevZ;
    public float capeXRotO;
    public float capeXRot;
    public float capeYRotO;
    public float capeYRot;
    public float capeZRotO;
    public float capeZRot;
    public double prevCapeX;
    public double prevCapeY;
    public double prevCapeZ;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState postTeleportAnimationState = new AnimationState();
    public AnimationState breathingAnimationState = new AnimationState();
    public AnimationState acidAnimationState = new AnimationState();
    public AnimationState modechangeAnimationState = new AnimationState();

    public AbstractTowerWraith(EntityType<? extends Summoned> p_i48553_1_, Level p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
        this.moveControl = new MobUtil.WraithMoveController(this);
        this.fireTick = 0;
        this.fireCooldown = 0;
        this.teleportTime2 = 0;
        this.teleportCooldown = 0;
        this.postTeleportTime = 0;
        this.modeChangeCooldown = 100;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatSwimGoal(this));
        this.goalSelector.addGoal(0, new ModeChangeGoal(this));
        this.goalSelector.addGoal(9, new WraithLookGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new WraithLookGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(10, new WraithLookRandomlyGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, AttributesConfig.TowerWraithHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.TowerWraithArmor.get())
                .add(Attributes.FOLLOW_RANGE, (double) 16.0F).add(Attributes.MOVEMENT_SPEED, (double) 0.25F)
                .add((Attribute) ForgeMod.STEP_HEIGHT_ADDITION.get(), (double) 1.0F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TowerWraithDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.TowerWraithHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.TowerWraithArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.TowerWraithDamage.get());
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setRandomMode();
        this.setCustomAttributes();
        this.setHealth(this.getMaxHealth());
        return spawnGroupData;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_MODE_ID, "ice");
        this.entityData.define(DATA_INTERESTED_ID, false);
        this.entityData.define(FLAGS, (byte) 0);
        this.entityData.define(DATA_CHANGE_TICK, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("Mode", this.getMode());
        pCompound.putInt("fireTick", this.fireTick);
        pCompound.putInt("fireCooldown", this.fireCooldown);
        pCompound.putInt("teleportTime2", this.teleportTime2);
        pCompound.putInt("teleportCooldown", this.teleportCooldown);
        pCompound.putInt("modeChangeCooldown", this.modeChangeCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setMode(pCompound.getString("Mode"));
        this.fireTick = pCompound.getInt("fireTick");
        this.fireCooldown = pCompound.getInt("fireCooldown");
        this.teleportTime2 = pCompound.getInt("teleportTime2");
        this.teleportCooldown = pCompound.getInt("teleportCooldown");
        this.modeChangeCooldown = pCompound.getInt("modeChangeCooldown");
    }

    public Predicate<Entity> summonPredicate() {
        return (entity) -> entity instanceof AbstractTowerWraith;
    }

    public int getSummonLimit(LivingEntity owner) {
        return (Integer) SpellConfig.WraithLimit.get();
    }

    protected boolean getWraithFlags(int mask) {
        int i = (Byte) this.entityData.get(FLAGS);
        return (i & mask) != 0;
    }

    protected void setWraithFlags(int mask, boolean value) {
        int i = (Byte) this.entityData.get(FLAGS);
        if (value) {
            i |= mask;
        } else {
            i &= ~mask;
        }

        this.entityData.set(FLAGS, (byte) (i & 255));
    }

    public boolean isFiring() {
        return this.getWraithFlags(1);
    }

    public void setIsFiring(boolean charging) {
        this.setWraithFlags(1, charging);
    }

    public boolean isTeleporting() {
        return this.getWraithFlags(2);
    }

    public void setIsTeleporting(boolean charging) {
        this.setWraithFlags(2, charging);
    }

    public boolean isBreathing() {
        return this.getWraithFlags(4);
    }

    public void setBreathing(boolean flag) {
        this.setWraithFlags(4, flag);
    }

    public void setIsInterested(boolean pBeg) {
        this.entityData.set(DATA_INTERESTED_ID, pBeg);
    }

    public boolean isInterested() {
        return (Boolean) this.entityData.get(DATA_INTERESTED_ID);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TOWER_WRAITH_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.TOWER_WRAITH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.TOWER_WRAITH_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.TOWER_WRAITH_FLY.get();
    }

    protected SoundEvent getTeleportInSound() {
        return ModSounds.TOWER_WRAITH_TELEPORT_IN.get();
    }

    protected SoundEvent getTeleportOutSound() {
        return ModSounds.TOWER_WRAITH_TELEPORT_OUT.get();
    }

    protected SoundEvent getAttackSound() {
        return ModSounds.TOWER_WRAITH_ATTACK.get();
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        if (this.getStepSound() != null) {
            float volume = Mth.clamp(0.6F + this.random.nextFloat() / 2.0F, 0.6F, 1.0F);
            float pitch = Mth.clamp(0.9F + this.random.nextFloat() / 2.0F, 0.9F, 1.3F);
            this.playSound(this.getStepSound(), volume, pitch);
        }

    }

    protected float nextStep() {
        return this.moveDist + 2.0F;
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource damageSource) {
        return false;
    }

    protected boolean isAffectedByFluids() {
        return false;
    }

    protected float getBlockSpeedFactor() {
        return this.onSoulSpeedBlock() ? 1.0F : super.getBlockSpeedFactor();
    }

    /** @deprecated */
    @Deprecated
    public double getFollowRange() {
        return this.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    /** @deprecated */
    @Deprecated
    public float getFloatFollowRange() {
        return (float) this.getFollowRange();
    }

    public float attackRange() {
        return 12.0F;
    }

    public int xpReward() {
        return 20;
    }

    protected boolean isSunSensitive() {
        return false;
    }

    public void tick() {
        if (this.isAlive()) {
            if (this.isInterested()) {
                --this.interestTime;
            }

            if (this.interestTime <= 0.0F) {
                this.setIsInterested(false);
            }
        }

        if (this.modeChangeCooldown > 0) {
            --this.modeChangeCooldown;
        }

        int changeTick = this.getChangeTick();
        if (changeTick > 0) {
            this.setChangeTick(changeTick - 1);
        }

        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();

        this.updateCapePhysics();

        this.setGravity();
        super.tick();
    }

    public boolean isPostTeleporting() {
        return this.postTeleportTime > 0;
    }

    public void setGravity() {
        this.setNoGravity(this.isUnderWater());
    }

    public void updateCapePhysics() {

        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;

        Vec3 velocity = this.getDeltaMovement();
        this.xCloak = velocity.x * 10.0F;
        this.yCloak = velocity.y * 10.0F;
        this.zCloak = velocity.z * 10.0F;

        this.xCloak = Mth.clamp(this.xCloak, -20.0F, 20.0F);
        this.yCloak = Mth.clamp(this.yCloak, -6.0F, 32.0F);
        this.zCloak = Mth.clamp(this.zCloak, -20.0F, 20.0F);

        float f = (float) this.yBodyRot * ((float) Math.PI / 180F);
        double d3 = Math.sin(f);
        double d4 = -Math.cos(f);

        float targetXRot = (float) (this.xCloak * d3 + this.zCloak * d4) * 0.5F;
        targetXRot += (float) this.yCloak * 0.5F;
        targetXRot = Mth.clamp(targetXRot, -30.0F, 30.0F);

        float targetZRot = (float) (this.xCloak * d4 - this.zCloak * d3) * 0.3F;
        targetZRot = Mth.clamp(targetZRot, -10.0F, 10.0F);

        float targetYRot = -targetZRot * 0.5F;

        this.capeXRotO = this.capeXRot;
        this.capeYRotO = this.capeYRot;
        this.capeZRotO = this.capeZRot;

        this.capeXRot = Mth.rotLerp(0.2F, this.capeXRot, targetXRot);
        this.capeYRot = Mth.rotLerp(0.2F, this.capeYRot, targetYRot);
        this.capeZRot = Mth.rotLerp(0.2F, this.capeZRot, targetZRot);
    }

    public void aiStep() {
        super.aiStep();
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround() && vector3d.y < (double) 0.0F && !this.isNoGravity()) {
            this.setDeltaMovement(vector3d.multiply((double) 1.0F, 0.6, (double) 1.0F));
        }

        if (this.teleportCooldown > 0) {
            --this.teleportCooldown;
        }

        if (this.postTeleportTime > 0) {
            if (this.postTeleportTime == 36) {
                Level var3 = this.level();
                if (var3 instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel) var3;
                    serverLevel.sendParticles(new TeleportShockwaveParticleOption(8, 4, 10), this.getX(),
                            this.getY() + (double) 0.5F, this.getZ(), 0, (double) 0.0F, (double) 0.0F, (double) 0.0F,
                            (double) 0.5F);
                }
            }

            --this.postTeleportTime;
        } else {
            this.level().broadcastEntityEvent(this, (byte) 7);
        }

        if (this.isAlive() && this.getChangeTick() <= 0) {
            this.attackAI();
            this.teleportAI();
        }

    }

    public void teleportAI() {
        if (!this.level().isClientSide) {
            if (this.isTeleporting()) {
                --this.teleportTime;
                if (this.teleportTime == 2) {
                    Level var2 = this.level();
                    if (var2 instanceof ServerLevel) {
                        ServerLevel serverLevel = (ServerLevel) var2;
                        serverLevel.sendParticles(new TeleportInShockwaveParticleOption(), this.getX(),
                                this.getY() + (double) 0.5F, this.getZ(), 0, (double) 0.0F, (double) 0.0F,
                                (double) 0.0F, (double) 0.5F);
                    }
                }

                if (this.teleportTime <= 2) {
                    this.prevX = this.getX();
                    this.prevY = this.getY();
                    this.prevZ = this.getZ();
                }

                if (this.teleportTime <= 0) {
                    this.teleport();
                }
            } else {
                this.teleportTime = 20;
            }
        } else if (this.isTeleporting()) {
            --this.teleportTime;
            ++this.teleportTime2;
            if (this.teleportTime <= 2) {
                this.prevX = this.getX();
                this.prevY = this.getY();
                this.prevZ = this.getZ();
            }
        } else {
            this.teleportTime = 20;
            this.teleportTime2 = 0;
        }

    }

    public void attackAI() {
        if (!this.level().isClientSide) {
            if (this.isPostTeleporting()) {
                this.getNavigation().stop();
            }

            if (this.fireCooldown > 0) {
                --this.fireCooldown;
            }

            if (this.fireTick > 10) {
                ++this.fireTick;
            }

            if (this.fireTick > 54) {
                this.fireCooldown = 80;
                this.fireTick = 0;
                if (this.isFiring()) {
                    this.stopFiring();
                }
            }

            if (this.getTarget() != null && !this.isPostTeleporting()) {
                if (!this.isFiring()) {
                    this.getLookControl().setLookAt(this.getTarget(), 100.0F, (float) this.getMaxHeadXRot());
                }

                if (this.getSensing().hasLineOfSight(this.getTarget())) {
                    if ((this.fireCooldown > 0 || this.isTeleporting()
                            || !(this.getTarget().distanceToSqr(this) < (double) Mth.square(this.attackRange())))
                            && !this.isFiring()) {
                        if (this.fireTick <= 10) {
                            this.fireTick = 0;
                        }

                        this.stopFiring();
                        if (this.canTeleport() && this.getTarget().distanceToSqr(this) <= (double) Mth.square(4.0F)) {
                            this.getNavigation().stop();
                            this.setIsTeleporting(true);
                        } else if (!this.isTeleporting()) {
                            this.movement();
                        }
                    } else {
                        if (this.fireTick <= 10) {
                            ++this.fireTick;
                        }

                        if (this.isFiring()) {
                            this.getNavigation().stop();
                            double d2 = this.getTarget().getX() - this.getX();
                            double d1 = this.getTarget().getZ() - this.getZ();
                            this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                            this.yBodyRot = this.getYRot();
                        }

                        if (this.fireTick > 10) {
                            this.startFiring();
                            this.getNavigation().stop();
                        } else {
                            this.movement();
                            this.stopFiring();
                        }

                        if (this.fireTick == 20) {
                            this.magicFire(this.getTarget());
                        }
                    }
                } else if ((Boolean) MobsConfig.WraithAggressiveTeleport.get() && this.canTeleport()) {
                    this.getNavigation().stop();
                    this.setIsTeleporting(true);
                }
            } else {
                if (this.fireTick <= 10) {
                    this.fireTick = 0;
                }

                this.setIsTeleporting(false);
            }
        }

    }

    public void magicFire(LivingEntity livingEntity) {
        String mode = this.getMode();
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2;
        if ("ice".equals(mode)) {
            if (this.level().random.nextFloat() <= 0.05F) {
                WandUtil.spawnCrossIceBouquet(this.level(), livingEntity.position(), this);
            } else {
                WandUtil.spawnIceBouquet(this.level(), livingEntity.position(), this);
            }
        } else if ("hell".equals(mode)) {
            if (this.level().random.nextFloat() <= 0.05F) {
                this.spawnCrossHellfire(this.level(), livingEntity.position(), this, damage);
            } else {
                this.spawn3x3Hellfire(this.level(), livingEntity.position(), this, damage);
            }
        } else if ("magic".equals(mode)) {
            if (this.level().random.nextFloat() <= 0.05F) {
                this.spawnCrossMagicFire(this.level(), livingEntity.position(), this, damage);
            } else {
                this.spawn3x3MagicFire(this.level(), livingEntity.position(), this, damage);
            }
        } else if ("acid".equals(mode)) {
            this.acidpool(livingEntity);
        }
    }

    public void movement() {
        if (this.getTarget() != null && !this.isStaying() && !this.isPostTeleporting()) {
            Vec3 vector3d2;
            if (this.getTarget().distanceToSqr(this) > (double) Mth.square(this.attackRange())) {
                vector3d2 = this.getTarget().position();
            } else {
                vector3d2 = LandRandomPos.getPos(this, 6, 6);
            }

            if (vector3d2 != null) {
                Path path = this.getNavigation().createPath(vector3d2.x, vector3d2.y, vector3d2.z, 0);
                if (path != null && this.getNavigation().isDone()) {
                    this.getNavigation().moveTo(path, (double) 1.25F);
                }
            }
        }

    }

    public boolean canTeleport() {
        EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, this.getX(), this.getY(),
                this.getZ());
        return !event.isCanceled() && !this.isStaying() && this.teleportCooldown <= 0 && !this.isPostTeleporting();
    }

    protected void teleport() {
        if (!this.level().isClientSide() && this.isAlive() && this.getTarget() != null) {
            if (this.getSensing().hasLineOfSight(this.getTarget())) {
                for (int i = 0; i < 128; ++i) {
                    double d3 = this.getTarget().getX()
                            + (this.getRandom().nextDouble() - (double) 0.5F) * (double) 20.0F;
                    double d4 = this.getTarget().getY();
                    double d5 = this.getTarget().getZ()
                            + (this.getRandom().nextDouble() - (double) 0.5F) * (double) 20.0F;
                    BlockPos blockPos1 = BlockPos.containing(d3, d4, d5);
                    if (!MobUtil.isFireImmune(this) && BlockFinder.hasSunlight(this.level(), blockPos1)) {
                        if (i == 127) {
                            this.setIsTeleporting(false);
                            break;
                        }
                    } else if (BlockFinder.canSeeBlock(this.getTarget(), blockPos1)
                            && this.randomTeleport(d3, d4, d5, false)) {
                        this.teleportHits();
                        this.setIsTeleporting(false);
                        MobUtil.instaLook(this, this.getTarget());
                        break;
                    }
                }
            } else {
                this.teleportTowardsEntity(this.getTarget());
            }
        }

    }

    public void teleportTowardsEntity(LivingEntity livingEntity) {
        for (int i = 0; i < 128; ++i) {
            Vec3 vector3d = new Vec3(this.getX() - livingEntity.getX(),
                    this.getY((double) 0.5F) - livingEntity.getEyeY(), this.getZ() - livingEntity.getZ());
            vector3d = vector3d.normalize();
            double d0 = (double) 16.0F;
            double d1 = this.getX() + (this.random.nextDouble() - (double) 0.5F) * (double) 8.0F - vector3d.x * d0;
            double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vector3d.y * d0;
            double d3 = this.getZ() + (this.random.nextDouble() - (double) 0.5F) * (double) 8.0F - vector3d.z * d0;
            if (this.randomTeleport(d1, d2, d3, false)) {
                this.teleportHits();
                this.teleportCooldown = 100;
                this.setIsTeleporting(false);
                MobUtil.instaLook(this, livingEntity);
                break;
            }
        }

    }

    public void teleportHits() {
        this.postTeleportTime = 38;
        this.level().broadcastEntityEvent(this, (byte) 6);
        this.level().broadcastEntityEvent(this, (byte) 100);
        this.level().broadcastEntityEvent(this, (byte) 101);
        this.level().gameEvent(GameEvent.TELEPORT, this.position(), Context.of(this));
        if (!this.isSilent()) {
            this.level().playSound((Player) null, this.prevX, this.prevY, this.prevZ, this.getTeleportInSound(),
                    this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(this.getTeleportOutSound(), 1.0F, 1.0F);
        }

    }

    public void startFiring() {
        if (!this.isFiring()) {
            this.setIsFiring(true);
            this.level().broadcastEntityEvent(this, (byte) 4);
            this.firingParticles();
            Level var2 = this.level();
            if (var2 instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel) var2;
                serverLevel.sendParticles(new TeleportShockwaveParticleOption(8, 4, 10), this.getX(),
                        this.getY() + (double) 0.5F, this.getZ(), 0, (double) 0.0F, (double) 0.0F, (double) 0.0F,
                        (double) 0.5F);
            }

            this.playAttackSound();
        }

    }

    public void firingParticles() {
        this.level().broadcastEntityEvent(this, (byte) 100);
    }

    public void stopFiring() {
        if (this.isFiring()) {
            this.setIsFiring(false);
            this.level().broadcastEntityEvent(this, (byte) 5);
        }

    }

    public ParticleOptions getFireParticles() {
        return (ParticleOptions) ModParticleTypes.WRAITH.get();
    }

    public ParticleOptions getBurstParticles() {
        return (ParticleOptions) ModParticleTypes.WRAITH_BURST.get();
    }

    public void handleEntityEvent(byte pId) {
        super.handleEntityEvent(pId);
        if (pId == 4) {
            this.setIsFiring(true);
            this.attackAnimationState.start(this.tickCount);
        }

        if (pId == 5) {
            this.setIsFiring(false);
            this.attackAnimationState.stop();
        }

        if (pId == 6) {
            this.postTeleportAnimationState.start(this.tickCount);
            this.postTeleportTime = 38;
        }

        if (pId == 7) {
            this.postTeleportAnimationState.stop();
        }

        if (pId == 8) {
            this.modechangeAnimationState.start(this.tickCount);
            this.setChangeTick(36);
        }

        if (pId == 100) {
            for (int j = 0; j < 8; ++j) {
                double d1 = this.getX()
                        + (this.random.nextDouble() - (double) 0.5F) * (double) this.getBbWidth() * (double) 2.0F;
                double d2 = this.getY() + this.random.nextDouble() + (double) 0.5F;
                double d3 = this.getZ()
                        + (this.random.nextDouble() - (double) 0.5F) * (double) this.getBbWidth() * (double) 2.0F;
                this.level().addParticle(this.getFireParticles(), d1, d2, d3, (double) 0.0F, (double) 0.0F,
                        (double) 0.0F);
                this.level().addParticle(this.getBurstParticles(), d1, d2, d3, (double) 0.0F, (double) 0.0F,
                        (double) 0.0F);
            }
        }

        if (pId == 101 && !this.isSilent()) {
            this.level().playSound((Player) null, this.prevX, this.prevY, this.prevZ, this.getTeleportInSound(),
                    this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(this.getTeleportOutSound(), 1.0F, 1.0F);
        }

        if (pId == 102) {
            this.setIsInterested(true);
            this.interestTime = 40.0F;
            this.playSound(this.getAmbientSound() != null ? this.getAmbientSound()
                    : (SoundEvent) ModSounds.WRAITH_AMBIENT.get(), 1.0F, 2.0F);
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        }

    }

    protected void addParticlesAroundSelf(ParticleOptions pParticleData) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(pParticleData, this.getRandomX((double) 1.0F), this.getRandomY() + (double) 1.0F,
                    this.getRandomZ((double) 1.0F), d0, d1, d2);
        }

    }

    public float getAnimationProgress(float pPartialTicks) {
        if (this.teleportTime <= 12 && this.isAlive()) {
            int i = this.teleportTime - 2;
            return i <= 0 ? 1.0F : 1.0F - ((float) i - pPartialTicks) / 20.0F;
        } else {
            return 0.0F;
        }
    }

    public void playAttackSound() {
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), this.getAttackSound(),
                    this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    private void spawnHellfire(Level level, Vec3 pos, LivingEntity owner, float damage) {
        com.Polarice3.Goety.common.entities.projectiles.Hellfire hellfire = new com.Polarice3.Goety.common.entities.projectiles.Hellfire(
                level, pos.x(), pos.y(), pos.z(), owner);
        hellfire.setExtraDamage(damage);
        MobUtil.moveDownToGround(hellfire);
        level.addFreshEntity(hellfire);
    }

    private void spawn3x3Hellfire(Level level, Vec3 pos, LivingEntity owner, float damage) {
        this.spawnHellfire(level, pos, owner, damage);
        this.spawnHellfire(level, pos.add(1.0D, 0.0D, 0.0D), owner, damage);
        this.spawnHellfire(level, pos.add(-1.0D, 0.0D, 0.0D), owner, damage);
        this.spawnHellfire(level, pos.add(0.0D, 0.0D, 1.0D), owner, damage);
        this.spawnHellfire(level, pos.add(0.0D, 0.0D, -1.0D), owner, damage);
        this.spawnHellfire(level, pos.add(1.0D, 0.0D, 1.0D), owner, damage);
        this.spawnHellfire(level, pos.add(1.0D, 0.0D, -1.0D), owner, damage);
        this.spawnHellfire(level, pos.add(-1.0D, 0.0D, 1.0D), owner, damage);
        this.spawnHellfire(level, pos.add(-1.0D, 0.0D, -1.0D), owner, damage);
    }

    private void spawnCrossHellfire(Level level, Vec3 pos, LivingEntity owner, float damage) {
        this.spawnHellfire(level, pos, owner, damage);
        this.spawnHellfire(level, pos.add(1.0D, 0.0D, 0.0D), owner, damage);
        this.spawnHellfire(level, pos.add(-1.0D, 0.0D, 0.0D), owner, damage);
        this.spawnHellfire(level, pos.add(0.0D, 0.0D, 1.0D), owner, damage);
        this.spawnHellfire(level, pos.add(0.0D, 0.0D, -1.0D), owner, damage);
    }

    private void spawnMagicFire(Level level, Vec3 pos, LivingEntity owner, float damage) {
        com.Polarice3.Goety.common.entities.projectiles.MagicFire magicFire = new com.Polarice3.Goety.common.entities.projectiles.MagicFire(
                level, pos.x(), pos.y(), pos.z(), owner);
        magicFire.setExtraDamage(damage);
        MobUtil.moveDownToGround(magicFire);
        level.addFreshEntity(magicFire);
    }

    private void spawn3x3MagicFire(Level level, Vec3 pos, LivingEntity owner, float damage) {
        this.spawnMagicFire(level, pos, owner, damage);
        this.spawnMagicFire(level, pos.add(1.0D, 0.0D, 0.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(-1.0D, 0.0D, 0.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(0.0D, 0.0D, 1.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(0.0D, 0.0D, -1.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(1.0D, 0.0D, 1.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(1.0D, 0.0D, -1.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(-1.0D, 0.0D, 1.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(-1.0D, 0.0D, -1.0D), owner, damage);
    }

    private void spawnCrossMagicFire(Level level, Vec3 pos, LivingEntity owner, float damage) {
        this.spawnMagicFire(level, pos, owner, damage);
        this.spawnMagicFire(level, pos.add(1.0D, 0.0D, 0.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(-1.0D, 0.0D, 0.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(0.0D, 0.0D, 1.0D), owner, damage);
        this.spawnMagicFire(level, pos.add(0.0D, 0.0D, -1.0D), owner, damage);
    }

    public void acidpool(LivingEntity livingEntity) {
        AcidPool acidPool = new AcidPool(ModEntityType.ACID_POOL.get(),
                this.level());
        acidPool.setColor(0xec67eb);
        acidPool.setWarmupColor(0xfdd4fb);
        acidPool.setPos(livingEntity.position());
        acidPool.setRadius(2.0F);
        acidPool.setDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) *
                1.5F);
        acidPool.setWarmupDelayTicks(MathHelper.secondsToTicks(0.7F));
        acidPool.setDuration(MathHelper.secondsToTicks(3.0F));
        acidPool.setOwner(this);
        acidPool.setSoundEvent(ModSounds.TOWER_WRAITH_ACID_VOCAL.get().getLocation().toString());
        if (!livingEntity.isInWater()) {
            MobUtil.moveDownToGround(acidPool);
        }
        if (this.level().addFreshEntity(acidPool)) {
            acidPool.playSound(ModSounds.TOWER_WRAITH_ACID.get(), 1.0F,
                    this.getVoicePitch());
        }
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (itemstack.is((Item) ModItems.ECTOPLASM.get()) && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.playSound((SoundEvent) ModSounds.WRAITH_AMBIENT.get(), 1.0F, 1.25F);
                this.heal(2.0F);
                Level var5 = this.level();
                if (var5 instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel) var5;

                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02;
                        double d1 = this.random.nextGaussian() * 0.02;
                        double d2 = this.random.nextGaussian() * 0.02;
                        serverLevel.sendParticles((SimpleParticleType) ModParticleTypes.HEAL_EFFECT.get(),
                                this.getRandomX((double) 1.0F), this.getRandomY() + (double) 0.5F,
                                this.getRandomZ((double) 1.0F), 0, d0, d1, d2, (double) 0.5F);
                    }
                }

                pPlayer.swing(pHand);
                return InteractionResult.SUCCESS;
            }

            if ((itemstack.isEmpty() || itemstack == ItemStack.EMPTY) && !this.isInterested()) {
                InteractionResult actionresulttype = super.mobInteract(pPlayer, pHand);
                if (!actionresulttype.consumesAction()) {
                    this.setIsInterested(true);
                    this.interestTime = 40.0F;
                    this.level().broadcastEntityEvent(this, (byte) 102);
                    this.playSound((SoundEvent) ModSounds.WRAITH_AMBIENT.get(), 1.0F, 2.0F);
                    this.heal(1.0F);
                    return InteractionResult.SUCCESS;
                }

                return actionresulttype;
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }

    public static class WraithLookGoal extends LookAtPlayerGoal {
        public AbstractTowerWraith wraith;

        public WraithLookGoal(AbstractTowerWraith p_i1631_1_, Class<? extends LivingEntity> p_i1631_2_,
                float p_i1631_3_) {
            super(p_i1631_1_, p_i1631_2_, p_i1631_3_);
            this.wraith = p_i1631_1_;
        }

        public WraithLookGoal(AbstractTowerWraith p_i1632_1_, Class<? extends LivingEntity> p_i1632_2_,
                float p_i1632_3_,
                float p_i1632_4_) {
            super(p_i1632_1_, p_i1632_2_, p_i1632_3_, p_i1632_4_);
            this.wraith = p_i1632_1_;
        }

        public boolean canUse() {
            return super.canUse() && this.wraith.fireTick < 0 && this.wraith.getTarget() == null;
        }
    }

    public static class WraithLookRandomlyGoal extends RandomLookAroundGoal {
        public AbstractTowerWraith wraith;

        public WraithLookRandomlyGoal(AbstractTowerWraith p_i1631_1_) {
            super(p_i1631_1_);
            this.wraith = p_i1631_1_;
        }

        public boolean canUse() {
            return super.canUse() && this.wraith.fireTick < 0 && this.wraith.getTarget() == null;
        }
    }

    public String getMode() {
        return this.entityData.get(DATA_MODE_ID);
    }

    public void setMode(String mode) {
        this.entityData.set(DATA_MODE_ID, mode);
    }

    public int getChangeTick() {
        return this.entityData.get(DATA_CHANGE_TICK);
    }

    public void setChangeTick(int tick) {
        this.entityData.set(DATA_CHANGE_TICK, tick);
    }

    private void setRandomMode() {
        String[] modes = new String[] { "ice", "hell", "magic", "acid" };
        String randomMode = modes[this.random.nextInt(modes.length)];
        this.setMode(randomMode);
    }

    public void changeToDifferentMode() {
        String currentMode = this.getMode();
        String[] modes = new String[] { "ice", "hell", "magic", "acid" };
        String newMode;
        do {
            newMode = modes[this.random.nextInt(modes.length)];
        } while (newMode.equals(currentMode));

        this.setMode(newMode);
        this.spawnModeChangeParticles(newMode);
    }

    private void spawnModeChangeParticles(String mode) {
        if (this.level().isClientSide) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) this.level();
        double x = this.getX();
        double y = this.getY() + (double) 0.5F;
        double z = this.getZ();

        switch (mode) {
            case "ice":
                for (int i = 0; i < 20; ++i) {
                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
                            x + (this.random.nextDouble() - 0.5) * 2.0,
                            y + (this.random.nextDouble() - 0.5) * 2.0,
                            z + (this.random.nextDouble() - 0.5) * 2.0,
                            1, 0.0, 0.0, 0.0, 0.1);
                }
                break;
            case "magic":
                for (int i = 0; i < 20; ++i) {
                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.PORTAL,
                            x + (this.random.nextDouble() - 0.5) * 2.0,
                            y + (this.random.nextDouble() - 0.5) * 2.0,
                            z + (this.random.nextDouble() - 0.5) * 2.0,
                            1, 0.0, 0.0, 0.0, 0.1);
                }
                break;
            case "hell":
                for (int i = 0; i < 20; ++i) {
                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.LAVA,
                            x + (this.random.nextDouble() - 0.5) * 2.0,
                            y + (this.random.nextDouble() - 0.5) * 2.0,
                            z + (this.random.nextDouble() - 0.5) * 2.0,
                            1, 0.0, 0.0, 0.0, 0.1);
                }
                break;
            case "acid":
                for (int i = 0; i < 20; ++i) {
                    serverLevel.sendParticles(
                            net.minecraft.core.particles.ParticleTypes.EFFECT,
                            x + (this.random.nextDouble() - 0.5) * 2.0,
                            y + (this.random.nextDouble() - 0.5) * 2.0,
                            z + (this.random.nextDouble() - 0.5) * 2.0,
                            1, 0.0, 0.0, 0.0, 0.1);
                }
                break;
        }
    }

    public static class ModeChangeGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final AbstractTowerWraith wraith;
        private static final int COOLDOWN_TICKS = 100;
        private static final int CHANGE_TICK_START = 36;

        public ModeChangeGoal(AbstractTowerWraith wraith) {
            this.wraith = wraith;
        }

        @Override
        public boolean canUse() {
            return wraith.modeChangeCooldown <= 0
                    && wraith.isAlive()
                    && wraith.getTarget() != null
                    && !wraith.getTarget().isDeadOrDying()
                    && !wraith.isFiring()
                    && !wraith.isTeleporting()
                    && !wraith.isPostTeleporting();
        }

        @Override
        public boolean canContinueToUse() {
            return wraith.getChangeTick() > 0;
        }

        @Override
        public void start() {
            if (!wraith.level().isClientSide) {
                wraith.level().broadcastEntityEvent(wraith, (byte) 8);
            }
            wraith.setChangeTick(CHANGE_TICK_START);
        }

        @Override
        public void tick() {
            int changeTick = wraith.getChangeTick();
            if (changeTick > 0) {
                if (changeTick == 18) {
                    wraith.changeToDifferentMode();
                    wraith.playSound((SoundEvent) ModSounds.TOWER_WRAITH_AMBIENT.get(), 1.0F, 2.0F);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            wraith.modechangeAnimationState.stop();
            wraith.modeChangeCooldown = COOLDOWN_TICKS;
            wraith.setChangeTick(0);
        }
    }
}
