package com.k1sak1.goetyawaken.common.entities.ally;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import net.minecraftforge.event.ForgeEventFactory;

public class ToxifinServant extends Summoned {
   protected static final int ATTACK_TIME = 80;
   private static final EntityDataAccessor DATA_ID_MOVING;
   private static final EntityDataAccessor DATA_ID_ATTACK_TARGET;
   private float animTailCurrent;
   private float animTailPrevious;
   private float animTailDelta;
   private float animSpikesCurrent;
   private float animSpikesPrevious;
   private @Nullable LivingEntity cachedClientAttackTarget;
   private int clientAttackTickCounter;
   private boolean wasOnGround;
   protected @Nullable RandomStrollGoal wanderGoal;
   private int airTimerOutOfWater;
   private int groundJumpCooldown;

   public ToxifinServant(EntityType entityType, Level level) {
      super(entityType, level);
      this.xpReward = 10;
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.moveControl = new WaterMoveController(this);
      this.lookControl = new WaterLookController();
      this.animTailPrevious = this.animTailCurrent = this.random.nextFloat();
   }

   protected void registerGoals() {
      MoveTowardsRestrictionGoal moveTowardsRestrictionGoal = new MoveTowardsRestrictionGoal(this, (double) 1.0F);
      this.wanderGoal = new RandomStrollGoal(this, (double) 1.0F, 80) {
         public boolean canUse() {
            return ToxifinServant.this.isPassenger() ? false : super.canUse();
         }
      };
      this.goalSelector.addGoal(4, new RangedBeamAttackGoal(this));
      this.goalSelector.addGoal(5, moveTowardsRestrictionGoal);
      this.goalSelector.addGoal(7, this.wanderGoal);
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F) {
         public boolean canUse() {
            return ToxifinServant.this.isPassenger() ? false : super.canUse();
         }
      });
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, ToxifinServant.class, 12.0F, 0.01F) {
         public boolean canUse() {
            return ToxifinServant.this.isPassenger() ? false : super.canUse();
         }
      });
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this) {
         public boolean canUse() {
            return ToxifinServant.this.isPassenger() ? false : super.canUse();
         }
      });
      this.goalSelector.addGoal(10, new Goal() {
         private final TargetingConditions RIDE_TARGET = TargetingConditions.forNonCombat().range((double) 3.0F)
               .selector((livingEntity) -> !livingEntity.isVehicle() && !livingEntity.isPassenger()
                     && ToxifinServant.this.isBuddy(livingEntity)
                     && MobUtil.areAllies(ToxifinServant.this, livingEntity));

         public boolean canUse() {
            return ToxifinServant.this.random.nextInt(100) == 0
                  && (!ToxifinServant.this.isVehicle()
                        || !(ToxifinServant.this instanceof PlaguewhaleSlabServant));
         }

         public void start() {
            if (ToxifinServant.this.getVehicle() == null) {
               AABB aABB = ToxifinServant.this.getBoundingBox().inflate((double) 2.0F, (double) 2.0F, (double) 2.0F);
               ToxifinServant guardian = (ToxifinServant) ToxifinServant.this.level().getNearestEntity(
                     ToxifinServant.class, this.RIDE_TARGET,
                     ToxifinServant.this, ToxifinServant.this.getX(), ToxifinServant.this.getY(),
                     ToxifinServant.this.getZ(), aABB);
               if (guardian != null) {
                  ToxifinServant.this.startRiding(guardian);
               }
            } else if (ToxifinServant.this.random.nextInt(10) == 0) {
               ToxifinServant.this.stopRiding();
            }

         }
      });
      this.wanderGoal.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      moveTowardsRestrictionGoal.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      super.registerGoals();
   }

   @Override
   public void followGoal() {
      this.goalSelector.addGoal(4, new FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
   }

   public MobType getMobType() {
      return MobType.WATER;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
            .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ToxifinServantDamage.get())
            .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ToxifinServantMovementSpeed.get())
            .add(Attributes.FOLLOW_RANGE, AttributesConfig.ToxifinServantFollowRange.get())
            .add(Attributes.MAX_HEALTH, AttributesConfig.ToxifinServantHealth.get());
   }

   public static AttributeSupplier.Builder setCustomAttributes() {
      return createAttributes();
   }

   @Override
   public boolean canBeAffected(MobEffectInstance effectInstance) {
      if (effectInstance.getEffect() == MobEffects.POISON) {
         return false;
      }
      return super.canBeAffected(effectInstance);
   }

   public void setConfigurableAttributes() {
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
            AttributesConfig.ToxifinServantHealth.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
            AttributesConfig.ToxifinServantDamage.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
            AttributesConfig.ToxifinServantMovementSpeed.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
            AttributesConfig.ToxifinServantFollowRange.get());
   }

   protected void actuallyHurt(DamageSource damageSource, float f) {
      super.actuallyHurt(damageSource, f);
      List<Entity> list = this.getPassengers();
      list.forEach(Entity::stopRiding);
      Entity entity = this.getVehicle();
      if (entity != null) {
         this.stopRiding();
         list.forEach((entity2) -> entity2.startRiding(entity, true));
      }

   }

   public int getAmbientSoundInterval() {
      return 160;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubble() ? ModSounds.TOXIFIN_AMBIENT.get()
            : ModSounds.TOXIFIN_AMBIENT_LAND.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return this.isInWaterOrBubble() ? ModSounds.TOXIFIN_HURT.get()
            : ModSounds.TOXIFIN_HURT_LAND.get();
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubble() ? ModSounds.TOXIFIN_DEATH.get()
            : ModSounds.TOXIFIN_DEATH_LAND.get();
   }

   protected Entity.MovementEmission getMovementEmission() {
      return MovementEmission.EVENTS;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
      return 0.2125F;
   }

   public @Nullable LivingEntity getControllingPassenger() {
      LivingEntity livingEntity = super.getControllingPassenger();
      return this.isBuddy(livingEntity) ? null : livingEntity;
   }

   protected PathNavigation createNavigation(Level level) {
      return new WaterBoundPathNavigation(this, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_MOVING, false);
      this.entityData.define(DATA_ID_ATTACK_TARGET, 0);
   }

   public boolean isMoving() {
      return (Boolean) this.entityData.get(DATA_ID_MOVING);
   }

   void setMoving(boolean bl) {
      this.entityData.set(DATA_ID_MOVING, bl);
   }

   public int getAttackDuration() {
      return 80;
   }

   void setActiveAttackTarget(int i) {
      this.entityData.set(DATA_ID_ATTACK_TARGET, i);
   }

   public boolean hasActiveAttackTarget() {
      return (Integer) this.entityData.get(DATA_ID_ATTACK_TARGET) != 0;
   }

   public @Nullable LivingEntity getActiveAttackTarget() {
      if (!this.hasActiveAttackTarget()) {
         return null;
      } else if (this.level().isClientSide) {
         if (this.cachedClientAttackTarget != null) {
            return this.cachedClientAttackTarget;
         } else {
            Entity entity = this.level().getEntity((Integer) this.entityData.get(DATA_ID_ATTACK_TARGET));
            if (entity instanceof LivingEntity) {
               this.cachedClientAttackTarget = (LivingEntity) entity;
               return this.cachedClientAttackTarget;
            } else {
               return null;
            }
         }
      } else {
         return this.getTarget();
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      super.onSyncedDataUpdated(entityDataAccessor);
      if (DATA_ID_ATTACK_TARGET.equals(entityDataAccessor)) {
         this.clientAttackTickCounter = 0;
         this.cachedClientAttackTarget = null;
      }

   }

   boolean isBuddy(@Nullable Entity entity) {
      ToxifinServant guardian;
      return entity instanceof ToxifinServant && (guardian = (ToxifinServant) entity).getType() == this.getType();
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return levelReader.getFluidState(blockPos).is(FluidTags.WATER)
            ? 10.0F + levelReader.getPathfindingCostFromLightLevels(blockPos)
            : super.getWalkTargetValue(blockPos, levelReader);
   }

   public boolean checkSpawnRules(LevelAccessor levelAccessor, MobSpawnType mobSpawnType) {
      return true;
   }

   public void aiStep() {
      this.airTimerOutOfWater = !this.isInWaterOrBubble() ? ++this.airTimerOutOfWater : 0;
      if (this.isAlive()) {
         if (this.level().isClientSide) {
            this.animTailPrevious = this.animTailCurrent;
            if (!this.isInWater()) {
               Vec3 velocity = this.getDeltaMovement();
               this.animTailDelta = (float) velocity.length() / 0.5F + 0.1F;
               if (velocity.y > (double) 0.0F && this.wasOnGround && !this.isSilent()) {
                  this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getFlopSound(),
                        this.getSoundSource(), 1.0F, 1.0F, false);
               }

               this.wasOnGround = velocity.y < (double) 0.0F
                     && this.level().loadedAndEntityCanStandOn(this.blockPosition().below(), this);
            } else {
               this.animTailDelta = this.isMoving()
                     ? (this.animTailDelta < 0.5F ? 4.0F
                           : (this.animTailDelta += (0.5F - this.animTailDelta) * 0.1F))
                     : (this.animTailDelta += (0.125F - this.animTailDelta) * 0.2F);
            }

            this.animTailCurrent += this.animTailDelta;
            this.animSpikesPrevious = this.animSpikesCurrent;
            this.animSpikesCurrent = !this.isInWaterOrBubble()
                  ? 0.5F - (float) Math.cos((double) this.airTimerOutOfWater * 0.05 * Math.PI / (double) 2.0F) / 2.0F
                  : (this.isMoving()
                        ? (this.animSpikesCurrent += (0.0F - this.animSpikesCurrent) * 0.25F)
                        : (this.animSpikesCurrent += (1.0F - this.animSpikesCurrent) * 0.06F));
            ToxifinServant linkedServant;
            Entity rider;
            if (this.isPassenger() && (rider = this.getVehicle()) instanceof ToxifinServant
                  && (linkedServant = (ToxifinServant) rider).getType() == this.getType()) {
               this.animSpikesCurrent = linkedServant.animSpikesCurrent;
            }

            if (this.isMoving() && this.isInWater()) {
               Vec3 lookDir = this.getViewVector(0.0F);

               for (int bubbleIdx = 0; bubbleIdx < 2; ++bubbleIdx) {
                  this.level().addParticle(ParticleTypes.BUBBLE,
                        this.getRandomX((double) 0.5F) - lookDir.x * (double) 1.5F,
                        this.getRandomY() - lookDir.y * (double) 1.5F,
                        this.getRandomZ((double) 0.5F) - lookDir.z * (double) 1.5F, (double) 0.0F, (double) 0.0F,
                        (double) 0.0F);
               }
            }

            if (this.hasActiveAttackTarget()) {
               if (this.clientAttackTickCounter < this.getAttackDuration()) {
                  ++this.clientAttackTickCounter;
               }

               LivingEntity activeTarget;
               if ((activeTarget = this.getActiveAttackTarget()) != null) {
                  this.getLookControl().setLookAt(activeTarget, 90.0F, 90.0F);
                  this.getLookControl().tick();
                  double beamWidth = (double) this.getAttackAnimationScale(0.0F);
                  double dirX = activeTarget.getX() - this.getX();
                  double dirY = activeTarget.getY((double) 0.5F) - this.getEyeY();
                  double dirZ = activeTarget.getZ() - this.getZ();
                  double totalDist = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                  dirX /= totalDist;
                  dirY /= totalDist;
                  dirZ /= totalDist;
                  double stepOffset = this.random.nextDouble();

                  while (stepOffset < totalDist) {
                     this.level().addParticle(ParticleTypes.BUBBLE,
                           this.getX() + dirX
                                 * (stepOffset += 1.8 - beamWidth + this.random.nextDouble() * (1.7 - beamWidth)),
                           this.getEyeY() + dirY * stepOffset, this.getZ() + dirZ * stepOffset, (double) 0.0F,
                           (double) 0.0F, (double) 0.0F);
                  }
               }
            }
         }

         LivingEntity activeTarget;
         if (!this.level().isClientSide && this.hasActiveAttackTarget()
               && (activeTarget = this.getActiveAttackTarget()) != null) {
            if (activeTarget.getEffect(MobEffects.POISON) == null) {
               activeTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0), this);
            }

         }

         if (this.isInWaterOrBubble()) {
            this.setAirSupply(300);
         } else if (this.canJump()) {
            this.setDeltaMovement(this.getDeltaMovement().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F),
                  (double) 0.5F, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F)));
            this.setYRot(this.random.nextFloat() * 360.0F);
            this.setOnGround(false);
            this.hasImpulse = true;
         }

         if (this.hasActiveAttackTarget()) {
            this.setYRot(this.yHeadRot);
         }
      }

      super.aiStep();
   }

   private boolean canJump() {
      if (!this.onGround()) {
         return false;
      } else {
         --this.groundJumpCooldown;
         if (this.groundJumpCooldown < 0) {
            this.groundJumpCooldown = this.random.nextInt(40) + 20;
            return true;
         } else {
            return false;
         }
      }
   }

   protected SoundEvent getFlopSound() {
      return ModSounds.TOXIFIN_FLOP.get();
   }

   public float getAttackAnimationScale(float f) {
      return ((float) this.clientAttackTickCounter + f) / (float) this.getAttackDuration();
   }

   public float getTailAnimation(float f) {
      return Mth.lerp(f, this.animTailPrevious, this.animTailCurrent);
   }

   public float getSpikesAnimation(float f) {
      return Mth.lerp(f, this.animSpikesPrevious, this.animSpikesCurrent);
   }

   public float getClientSideAttackTime() {
      return (float) this.clientAttackTickCounter;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return levelReader.isUnobstructed(this);
   }

   public boolean hurt(DamageSource damageSource, float f) {
      if (this.level().isClientSide) {
         return false;
      } else {
         Entity entity;
         if (!this.isMoving() && !damageSource.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
               && !damageSource.is(DamageTypes.THORNS)
               && (entity = damageSource.getDirectEntity()) instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0), this);
         }

         if (this.wanderGoal != null) {
            this.wanderGoal.trigger();
         }

         return super.hurt(damageSource, f);
      }
   }

   public int getMaxHeadXRot() {
      return 180;
   }

   @Override
   public boolean killedEntity(ServerLevel world, LivingEntity killedEntity) {
      boolean flag = super.killedEntity(world, killedEntity);
      if (killedEntity instanceof Mob mobEntity) {
         if (this instanceof PlaguewhaleSlabServant) {
            if (ForgeEventFactory.canLivingConvert(mobEntity,
                  ModEntityType.PLAGUEWHALE_SLAB_SERVANT.get(), (timer) -> {
                  })) {
               PlaguewhaleSlabServant servant = mobEntity.convertTo(
                     ModEntityType.PLAGUEWHALE_SLAB_SERVANT.get(), true);
               if (servant != null) {
                  if (this.getTrueOwner() != null) {
                     servant.setTrueOwner(this.getTrueOwner());
                  }
                  servant.finalizeSpawn(world, world.getCurrentDifficultyAt(servant.blockPosition()),
                        MobSpawnType.CONVERSION, null, null);
                  servant.setLimitedLife(10 * (15 + world.random.nextInt(45)));
                  if (this.isHostile()) {
                     servant.setHostile(true);
                  }
                  ForgeEventFactory.onLivingConvert(mobEntity, servant);
                  this.playSound(ModSounds.POISONOUS_POTATO_ZOMBIE_INFECT.get(), 1.0F, 1.0F);
                  if (!servant.isSilent()) {
                     world.levelEvent(null, 1026, servant.blockPosition(), 0);
                  }
               }
            }
         } else {
            if (ForgeEventFactory.canLivingConvert(mobEntity,
                  ModEntityType.TOXIFIN_SERVANT.get(), (timer) -> {
                  })) {
               ToxifinServant servant = mobEntity.convertTo(
                     ModEntityType.TOXIFIN_SERVANT.get(), true);
               if (servant != null) {
                  if (this.getTrueOwner() != null) {
                     servant.setTrueOwner(this.getTrueOwner());
                  }
                  servant.finalizeSpawn(world, world.getCurrentDifficultyAt(servant.blockPosition()),
                        MobSpawnType.CONVERSION, null, null);
                  servant.setLimitedLife(10 * (15 + world.random.nextInt(45)));
                  if (this.isHostile()) {
                     servant.setHostile(true);
                  }
                  ForgeEventFactory.onLivingConvert(mobEntity, servant);
                  this.playSound(ModSounds.POISONOUS_POTATO_ZOMBIE_INFECT.get(), 1.0F, 1.0F);
                  if (!servant.isSilent()) {
                     world.levelEvent(null, 1026, servant.blockPosition(), 0);
                  }
               }
            }
         }
      }
      return flag;
   }

   static {
      DATA_ID_MOVING = SynchedEntityData.defineId(ToxifinServant.class, EntityDataSerializers.BOOLEAN);
      DATA_ID_ATTACK_TARGET = SynchedEntityData.defineId(ToxifinServant.class, EntityDataSerializers.INT);
   }

   public void travel(Vec3 vec3) {
      if (this.isControlledByLocalInstance() && this.isInWater()) {
         this.moveRelative(0.1F, vec3);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (!this.isMoving() && this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add((double) 0.0F, -0.005, (double) 0.0F));
         }
      } else {
         super.travel(vec3);
      }

   }

   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this) {
         public void clientTick() {
            LivingEntity livingEntity;
            Entity entity;
            if (ToxifinServant.this.isPassenger() && (entity = ToxifinServant.this.getVehicle()) instanceof LivingEntity
                  && (livingEntity = (LivingEntity) entity).getType() == ToxifinServant.this.getType()) {
               ToxifinServant.this.yBodyRot = livingEntity.yBodyRot;
            }

            super.clientTick();
         }
      };
   }

   static class WaterMoveController extends MoveControl {
      private final ToxifinServant servantEntity;

      public WaterMoveController(ToxifinServant entity) {
         super(entity);
         this.servantEntity = entity;
      }

      public void tick() {
         if (this.operation == Operation.MOVE_TO && !this.servantEntity.getNavigation().isDone()) {
            Vec3 displacement = new Vec3(this.wantedX - this.servantEntity.getX(),
                  this.wantedY - this.servantEntity.getY(),
                  this.wantedZ - this.servantEntity.getZ());
            double distance = displacement.length();
            double normX = displacement.x / distance;
            double normY = displacement.y / distance;
            double normZ = displacement.z / distance;
            float targetAngle = (float) (Mth.atan2(displacement.z, displacement.x) * (double) (180F / (float) Math.PI))
                  - 90.0F;
            this.servantEntity.setYRot(this.rotlerp(this.servantEntity.getYRot(), targetAngle, 90.0F));
            this.servantEntity.yBodyRot = this.servantEntity.getYRot();
            float maxSpeed = (float) (this.speedModifier
                  * this.servantEntity.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float currentSpeed = Mth.lerp(0.125F, this.servantEntity.getSpeed(), maxSpeed);
            this.servantEntity.setSpeed(currentSpeed);
            double swayComponentA = Math
                  .sin((double) (this.servantEntity.tickCount + this.servantEntity.getId()) * (double) 0.5F)
                  * 0.05;
            double yawCos = Math.cos((double) (this.servantEntity.getYRot() * ((float) Math.PI / 180F)));
            double yawSin = Math.sin((double) (this.servantEntity.getYRot() * ((float) Math.PI / 180F)));
            double swayComponentB = Math
                  .sin((double) (this.servantEntity.tickCount + this.servantEntity.getId()) * (double) 0.75F)
                  * 0.05;
            this.servantEntity.setDeltaMovement(this.servantEntity.getDeltaMovement().add(swayComponentA * yawCos,
                  swayComponentB * (yawSin + yawCos) * (double) 0.25F + (double) currentSpeed * normY * 0.1,
                  swayComponentA * yawSin));
            LookControl lookCtrl = this.servantEntity.getLookControl();
            double targetLookX = this.servantEntity.getX() + normX * (double) 2.0F;
            double targetLookY = this.servantEntity.getEyeY() + normY / distance;
            double targetLookZ = this.servantEntity.getZ() + normZ * (double) 2.0F;
            double prevLookX = lookCtrl.getWantedX();
            double prevLookY = lookCtrl.getWantedY();
            double prevLookZ = lookCtrl.getWantedZ();
            if (!lookCtrl.isLookingAtTarget()) {
               prevLookX = targetLookX;
               prevLookY = targetLookY;
               prevLookZ = targetLookZ;
            }

            this.servantEntity.getLookControl().setLookAt(Mth.lerp((double) 0.125F, prevLookX, targetLookX),
                  Mth.lerp((double) 0.125F, prevLookY, targetLookY),
                  Mth.lerp((double) 0.125F, prevLookZ, targetLookZ), 10.0F, 40.0F);
            this.servantEntity.setMoving(true);
         } else {
            this.servantEntity.setSpeed(0.0F);
            this.servantEntity.setMoving(false);
         }
      }
   }

   class WaterLookController extends LookControl {
      WaterLookController() {
         super(ToxifinServant.this);
      }

      protected Optional getXRotD() {
         return !ToxifinServant.this.isPassenger() && !ToxifinServant.this.isVehicle() ? super.getXRotD()
               : Optional.empty();
      }

      public void tick() {
         Entity rider = ToxifinServant.this.getVehicle();
         ToxifinServant linkedEntity;
         if (rider instanceof ToxifinServant
               && (linkedEntity = (ToxifinServant) rider).getType() == ToxifinServant.this.getType()) {
            ToxifinServant.this.yHeadRot = linkedEntity.yHeadRot;
         }

         super.tick();
      }
   }

   static class RangedBeamAttackGoal extends Goal {
      private final ToxifinServant ownerEntity;
      private int beamChargeTicks;
      private final boolean isLargeVariant;

      public RangedBeamAttackGoal(ToxifinServant entity) {
         this.ownerEntity = entity;
         this.isLargeVariant = entity instanceof PlaguewhaleSlabServant;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.ownerEntity.getTarget();
         return target != null && target.isAlive();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse()
               && this.ownerEntity.getTarget() != null;
      }

      public void start() {
         this.beamChargeTicks = -10;
         this.ownerEntity.getNavigation().stop();
         LivingEntity target = this.ownerEntity.getTarget();
         if (target != null) {
            this.ownerEntity.getLookControl().setLookAt(target, 90.0F, 90.0F);
         }

         this.ownerEntity.hasImpulse = true;
      }

      public void stop() {
         this.ownerEntity.setActiveAttackTarget(0);
         this.ownerEntity.wanderGoal.trigger();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity target = this.ownerEntity.getTarget();
         if (target != null) {
            this.ownerEntity.getNavigation().stop();
            this.ownerEntity.getLookControl().setLookAt(target, 90.0F, 90.0F);
            if (!this.ownerEntity.hasLineOfSight(target)) {
               this.ownerEntity.setTarget((LivingEntity) null);
            } else {
               ++this.beamChargeTicks;
               if (this.beamChargeTicks == 0) {
                  this.ownerEntity.setActiveAttackTarget(target.getId());
                  if (!this.ownerEntity.isSilent()) {
                     this.ownerEntity.level().broadcastEntityEvent(this.ownerEntity, (byte) 103);
                  }
               } else if (this.beamChargeTicks >= this.ownerEntity.getAttackDuration()) {
                  float damageMultiplier = 1.0F;
                  if (this.ownerEntity.level().getDifficulty() == Difficulty.HARD) {
                     damageMultiplier += 2.0F;
                  }
                  if (this.isLargeVariant) {
                     damageMultiplier += 2.0F;
                  }
                  target.addEffect(new MobEffectInstance(MobEffects.WITHER, 40 + (int) damageMultiplier * 10, 0),
                        this.ownerEntity);
                  target.addEffect(new MobEffectInstance(ModEffects.POTENT_VENOM.get(),
                        MathHelper.secondsToTicks(5), 0, false, false));
                  target.hurt(this.ownerEntity.damageSources().indirectMagic(this.ownerEntity, this.ownerEntity),
                        damageMultiplier);
                  if (this.isLargeVariant) {
                     target.hurt(this.ownerEntity.damageSources().mobAttack(this.ownerEntity),
                           (float) this.ownerEntity.getAttributeValue(Attributes.ATTACK_DAMAGE));
                  } else {
                     target.hurt(this.ownerEntity.getServantAttack(),
                           (float) this.ownerEntity.getAttributeValue(Attributes.ATTACK_DAMAGE));
                  }
                  this.ownerEntity.setActiveAttackTarget(0);
                  this.beamChargeTicks = -10;
               }

               super.tick();
            }
         }
      }
   }

}
