package com.k1sak1.goetyawaken.common.entities.hostile.illager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.phys.Vec3;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IHeretic;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.CorruptedSlime;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.Optional;

public class TowerWitch extends Raider implements RangedAttackMob, ICustomAttributes, IHeretic {
   private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID,
         "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
   private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(TowerWitch.class,
         EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> CHANTING = SynchedEntityData.defineId(TowerWitch.class,
         EntityDataSerializers.BOOLEAN);
   private final DynamicGameEventListener<GameEventListener> gameEventListener;
   private int usingTime;
   public List<Vec3> convokePos = new ArrayList<>();
   public int chantCoolDown;
   public int chantTimes;
   public int castCoolDown;
   private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
   private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

   public TowerWitch(EntityType<? extends TowerWitch> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.gameEventListener = new DynamicGameEventListener<>(new GameEventListener() {
         public PositionSource getListenerSource() {
            return new BlockPositionSource(TowerWitch.this.blockPosition());
         }

         public int getListenerRadius() {
            return 32;
         }

         public GameEventListener.DeliveryMode getDeliveryMode() {
            return GameEventListener.DeliveryMode.BY_DISTANCE;
         }

         public boolean handleGameEvent(ServerLevel serverLevel, GameEvent p_282184_, GameEvent.Context p_283014_,
               Vec3 p_282350_) {
            if (!TowerWitch.this.isRemoved()) {
               if (p_282184_ == GameEvent.ENTITY_DIE) {
                  Entity sourceEntity = p_283014_.sourceEntity();
                  if (sourceEntity instanceof LivingEntity livingEntity
                        && !(livingEntity instanceof TowerWitch)
                        && !(livingEntity instanceof CorruptedSlime)) {
                     TowerWitch.this.getConvokePos().add(livingEntity.position());
                     return true;
                  }
               }

            }
            return false;
         }
      });
   }

   protected void registerGoals() {
      super.registerGoals();
      this.healRaidersGoal = new NearestHealableRaiderTargetGoal<>(this, Raider.class, true, (p_289462_) -> {
         return p_289462_ != null && this.hasActiveRaid() && p_289462_.getType() != EntityType.WITCH;
      });
      this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false,
            (Predicate<LivingEntity>) null);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new TowerWitchCastingGoal(this));
      this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.0D, 60, 10.0F));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class));
      this.targetSelector.addGoal(2, this.healRaidersGoal);
      this.targetSelector.addGoal(3, this.attackPlayersGoal);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_USING_ITEM, false);
      this.getEntityData().define(CHANTING, false);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITCH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.WITCH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITCH_DEATH;
   }

   public void setUsingItem(boolean pUsingItem) {
      this.getEntityData().set(DATA_USING_ITEM, pUsingItem);
   }

   public boolean isDrinkingPotion() {
      return this.getEntityData().get(DATA_USING_ITEM);
   }

   public void setChanting(boolean chanting) {
      this.getEntityData().set(CHANTING, chanting);
   }

   public boolean isChanting() {
      return this.getEntityData().get(CHANTING);
   }

   public int getChantCoolDown() {
      return this.chantCoolDown;
   }

   public void setChantCoolDown(int chantCoolDown) {
      this.chantCoolDown = chantCoolDown;
   }

   public int getChantTimes() {
      return this.chantTimes;
   }

   public void setChantTimes(int chantTimes) {
      this.chantTimes = chantTimes;
   }

   public int getCastCoolDown() {
      return this.castCoolDown;
   }

   public void setCastCoolDown(int castCoolDown) {
      this.castCoolDown = castCoolDown;
   }

   public List<Vec3> getConvokePos() {
      return this.convokePos;
   }

   public static AttributeSupplier.Builder setCustomAttributes() {
      return Mob.createMobAttributes()
            .add(Attributes.FOLLOW_RANGE, 48)
            .add(Attributes.MAX_HEALTH, com.k1sak1.goetyawaken.config.AttributesConfig.TowerWitchHealth.get())
            .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TowerWitchMovementSpeed.get())
            .add(Attributes.ARMOR, AttributesConfig.TowerWitchArmor.get())
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D);
   }

   public void setConfigurableAttributes() {
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
            AttributesConfig.TowerWitchHealth.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
            AttributesConfig.TowerWitchMovementSpeed.get());
      MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.TowerWitchArmor.get());

   }

   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
         MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
      SpawnGroupData spawnGroupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
      this.setConfigurableAttributes();
      this.setHealth(this.getMaxHealth());
      return spawnGroupData;
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("ChantCoolDown", this.getChantCoolDown());
      pCompound.putInt("ChantTimes", this.getChantTimes());
      pCompound.putInt("CastCoolDown", this.getCastCoolDown());
      if (!this.getConvokePos().isEmpty()) {
         ListTag listTag = new ListTag();
         for (Vec3 vec3 : this.getConvokePos()) {
            CompoundTag tag = new CompoundTag();
            tag.putDouble("PosX", vec3.x);
            tag.putDouble("PosY", vec3.y);
            tag.putDouble("PosZ", vec3.z);
            listTag.add(tag);
         }
         pCompound.put("ConvokePos", listTag);
      }
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.contains("ChantCoolDown")) {
         this.setChantCoolDown(pCompound.getInt("ChantCoolDown"));
      }
      if (pCompound.contains("ChantTimes")) {
         this.setChantTimes(pCompound.getInt("ChantTimes"));
      }
      if (pCompound.contains("CastCoolDown")) {
         this.setCastCoolDown(pCompound.getInt("CastCoolDown"));
      }
      if (pCompound.contains("ConvokePos")) {
         ListTag listTag = pCompound.getList("ConvokePos", 10);
         for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag tag = listTag.getCompound(i);
            Vec3 vec3 = new Vec3(tag.getDouble("PosX"), tag.getDouble("PosY"), tag.getDouble("PosZ"));
            this.getConvokePos().add(vec3);
         }
      }
   }

   public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> p_218348_) {
      Level level = this.level();
      if (level instanceof ServerLevel serverlevel) {
         p_218348_.accept(this.gameEventListener, serverlevel);
      }

   }

   @Override
   public void handleEntityEvent(byte pId) {
      if (pId == 4) {
         this.setChanting(true);
      } else if (pId == 5) {
         this.setChanting(false);
      } else if (pId == 15) {
         for (int i = 0; i < this.random.nextInt(35) + 10; ++i) {
            this.level().addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double) 0.13F,
                  this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * (double) 0.13F,
                  this.getZ() + this.random.nextGaussian() * (double) 0.13F, 0.0D, 0.0D, 0.0D);
         }
      } else {
         super.handleEntityEvent(pId);
      }

   }

   public void aiStep() {
      if (!this.level().isClientSide && this.isAlive()) {
         if (this.chantCoolDown > 0) {
            --this.chantCoolDown;
         }
         if (this.castCoolDown > 0) {
            --this.castCoolDown;
         }

         this.healRaidersGoal.decrementCooldown();
         if (this.healRaidersGoal.getCooldown() <= 0) {
            this.attackPlayersGoal.setCanAttack(true);
         } else {
            this.attackPlayersGoal.setCanAttack(false);
         }

         if (this.isDrinkingPotion()) {
            if (this.usingTime-- <= 0) {
               this.setUsingItem(false);
               ItemStack itemstack = this.getMainHandItem();
               this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
               if (itemstack.is(Items.POTION)) {
                  List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
                  if (list != null) {
                     for (MobEffectInstance mobeffectinstance : list) {
                        this.addEffect(new MobEffectInstance(mobeffectinstance));
                     }
                  }
               }

               this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
            }
         } else {
            Potion potion = null;
            if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER)
                  && !this.hasEffect(MobEffects.WATER_BREATHING)) {
               potion = Potions.WATER_BREATHING;
            } else if (this.random.nextFloat() < 0.15F
                  && (this.isOnFire()
                        || this.getLastDamageSource() != null && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE))
                  && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
               potion = Potions.FIRE_RESISTANCE;
            } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
               if (this.random.nextFloat() < 0.6F && !this.hasEffect(MobEffects.REGENERATION)) {
                  potion = Potions.REGENERATION;
               } else if (!this.hasEffect(MobEffects.HEAL)) {
                  potion = Potions.HEALING;
               }
            } else if (this.random.nextFloat() < 0.5F && this.getTarget() != null
                  && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
               potion = Potions.SWIFTNESS;
            }

            if (potion != null) {
               this.setItemSlot(EquipmentSlot.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
               this.usingTime = this.getMainHandItem().getUseDuration();
               this.setUsingItem(true);
               if (!this.isSilent()) {
                  this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK,
                        this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
               }

               AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
               attributeinstance.removeModifier(SPEED_MODIFIER_DRINKING);
               attributeinstance.addTransientModifier(SPEED_MODIFIER_DRINKING);
            }
         }

         if (this.random.nextFloat() < 7.5E-4F) {
            this.level().broadcastEntityEvent(this, (byte) 15);
         }
      }

      super.aiStep();
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.WITCH_CELEBRATE;
   }

   protected float getDamageAfterMagicAbsorb(DamageSource pSource, float pDamage) {
      pDamage = super.getDamageAfterMagicAbsorb(pSource, pDamage);
      if (pSource.getEntity() == this) {
         pDamage = 0.0F;
      }

      if (pSource.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
         pDamage *= 0.15F;
      }

      return pDamage;
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      if (!this.level().isClientSide) {
         Entity entity = pSource.getEntity();
         if (entity != null && entity != this && !pSource.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)) {
            if (entity instanceof LivingEntity livingEntity) {
               livingEntity.hurt(this.damageSources().thorns(this), 1.0F);
            }
         }
      }

      return super.hurt(pSource, pAmount);
   }

   public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
      if (!this.isDrinkingPotion()) {
         Vec3 vec3 = pTarget.getDeltaMovement();
         double d0 = pTarget.getX() + vec3.x - this.getX();
         double d1 = pTarget.getEyeY() - (double) 1.1F - this.getY();
         double d2 = pTarget.getZ() + vec3.z - this.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         Potion potion = Potions.HARMING;
         if (pTarget instanceof Raider) {
            if (pTarget.getHealth() <= 4.0F) {
               potion = Potions.HEALING;
            } else {
               potion = Potions.REGENERATION;
            }

            this.setTarget((LivingEntity) null);
         } else {
            boolean isUndead = pTarget.getMobType() == MobType.UNDEAD;

            if (isUndead) {
               if (d3 >= 8.0D && !pTarget.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                  potion = Potions.SLOWNESS;
               } else if (d3 <= 3.0D && !pTarget.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                  potion = Potions.WEAKNESS;
               } else {
                  potion = Potions.HEALING;
               }
            } else {
               if (d3 >= 8.0D && !pTarget.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                  potion = Potions.SLOWNESS;
               } else if (pTarget.getHealth() >= 8.0F && !pTarget.hasEffect(MobEffects.POISON)) {
                  potion = Potions.POISON;
               } else if (d3 <= 3.0D && !pTarget.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                  potion = Potions.WEAKNESS;
               }
            }
         }

         ThrownPotion thrownpotion = new ThrownPotion(this.level(), this);
         ItemStack potionStack = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
         if (this.shouldEnhancePotion()) {
            this.enhancePotion(potionStack);
         }
         thrownpotion.setItem(potionStack);
         thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
         thrownpotion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
         if (!this.isSilent()) {
            this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW,
                  this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
         }

         this.level().addFreshEntity(thrownpotion);
      }
   }

   private boolean shouldEnhancePotion() {
      float baseChance = 0.20F;
      switch (this.level().getDifficulty()) {
         case EASY:
            return this.random.nextFloat() < baseChance * 0.5F;
         case NORMAL:
            return this.random.nextFloat() < baseChance;
         case HARD:
            return this.random.nextFloat() < baseChance * 2.0F;
         default:
            return false;
      }
   }

   private void enhancePotion(ItemStack potionStack) {
      var effects = PotionUtils.getMobEffects(potionStack);
      for (var effect : effects) {
         int newAmplifier = effect.getAmplifier() + 1;
         int newDuration = effect.getDuration() + 600;

         var enhancedEffect = new MobEffectInstance(
               effect.getEffect(),
               newDuration,
               newAmplifier,
               effect.isAmbient(),
               effect.isVisible(),
               effect.showIcon());

         PotionUtils.setPotion(potionStack, Potions.WATER);
         PotionUtils.setCustomEffects(potionStack, java.util.List.of(enhancedEffect));
      }
   }

   protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
      return 1.62F;
   }

   public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {
   }

   public boolean canBeLeader() {
      return false;
   }

   public static class TowerWitchCastingGoal extends Goal {
      public TowerWitch towerWitch;
      public int movingTime;
      public int castingTime;
      public Vec3 targetPos = null;
      public static int TOTAL_CAST_TIME = 30;

      public TowerWitchCastingGoal(TowerWitch towerWitch) {
         this.towerWitch = towerWitch;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         LivingEntity target = this.towerWitch.getTarget();
         return target != null && target.isAlive() && !this.towerWitch.getConvokePos().isEmpty()
               && this.towerWitch.getCastCoolDown() <= 0;
      }

      @Override
      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.movingTime <= 100 && this.castingTime <= TOTAL_CAST_TIME;
      }

      @Override
      public boolean requiresUpdateEveryTick() {
         return true;
      }

      @Override
      public void start() {
         super.start();
         this.movingTime = 0;
         this.castingTime = 0;
         this.findTargetPos();
      }

      @Override
      public void stop() {
         super.stop();
         this.towerWitch.setChanting(false);
         this.towerWitch.level().broadcastEntityEvent(this.towerWitch, (byte) 7);
      }

      public void findTargetPos() {
         double hitDist = 0.0D;
         if (!this.towerWitch.getConvokePos().isEmpty()) {
            for (Vec3 vec31 : this.towerWitch.getConvokePos()) {
               if (this.towerWitch.getNavigation().isStableDestination(BlockPos.containing(vec31))) {
                  Optional<Vec3> interceptPos = this.towerWitch.getBoundingBox().clip(vec31,
                        vec31.add(1.0D, 1.0D, 1.0D));
                  if (this.towerWitch.getBoundingBox()
                        .inflate(this.towerWitch.getAttributeValue(Attributes.FOLLOW_RANGE)).contains(vec31)) {
                     if (0.0D <= hitDist) {
                        this.targetPos = vec31;
                        hitDist = 0.0D;
                     }
                  } else if (interceptPos.isPresent()) {
                     double possibleDist = vec31.distanceTo(interceptPos.get());

                     if (possibleDist < hitDist || hitDist == 0.0D) {
                        this.targetPos = vec31;
                        hitDist = possibleDist;
                     }
                  }
               }
            }
            try {
               if (this.targetPos != null) {
                  try {
                     for (TowerWitch towerWitch1 : this.towerWitch.level().getEntitiesOfClass(TowerWitch.class,
                           this.towerWitch.getBoundingBox()
                                 .inflate(this.towerWitch.getAttributeValue(Attributes.FOLLOW_RANGE)))) {
                        if (towerWitch1 != this.towerWitch) {
                           if (!towerWitch1.getConvokePos().isEmpty()) {
                              towerWitch1.getConvokePos().remove(this.targetPos);
                           }
                        }
                     }
                  } catch (java.util.ConcurrentModificationException ignored) {
                  }
               }
            } catch (NullPointerException ignored) {
            }
         } else {
            this.targetPos = null;
         }
      }

      @Override
      public void tick() {
         if (this.targetPos != null) {
            if (this.towerWitch.level() instanceof ServerLevel serverLevel) {
               ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
               ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.RISING_ENCHANT.get(),
                     this.targetPos.x, this.targetPos.y, this.targetPos.z, colorUtil.red, colorUtil.green,
                     colorUtil.blue, 1.0F);
            }
            if (this.towerWitch.distanceToSqr(this.targetPos) > Mth.square(4.0D)) {
               ++this.movingTime;
               this.towerWitch.getNavigation().moveTo(this.targetPos.x, this.targetPos.y, this.targetPos.z, 1.0F);
            } else {
               MobUtil.instaLook(this.towerWitch, this.targetPos);
               ++this.castingTime;
               if (!this.towerWitch.isChanting()) {
                  this.towerWitch.setChanting(true);
                  this.towerWitch.level().broadcastEntityEvent(this.towerWitch, (byte) 6);
               }
               if (this.castingTime == TOTAL_CAST_TIME) {
                  CorruptedSlime corruptedSlime = new CorruptedSlime(ModEntityType.CORRUPTED_SLIME.get(),
                        this.towerWitch.level());
                  corruptedSlime.moveTo(this.targetPos);
                  corruptedSlime.setTrueOwner(this.towerWitch);
                  if (this.towerWitch.level() instanceof ServerLevel serverLevel) {
                     corruptedSlime.finalizeSpawn(serverLevel,
                           this.towerWitch.level().getCurrentDifficultyAt(BlockPos.containing(this.targetPos)),
                           MobSpawnType.MOB_SUMMONED, null, null);
                     com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel,
                           net.minecraft.core.particles.ParticleTypes.FLAME, corruptedSlime);
                  }
                  if (this.towerWitch.getTarget() != null) {
                     corruptedSlime.setTarget(this.towerWitch.getTarget());
                  }
                  corruptedSlime.setLimitedLife(6000);
                  if (this.towerWitch.level().addFreshEntity(corruptedSlime)) {
                     this.towerWitch.getConvokePos().remove(this.targetPos);
                     this.towerWitch.setCastCoolDown(100);
                  }
               }
            }
         } else {
            this.findTargetPos();
         }
         super.tick();
      }
   }
}