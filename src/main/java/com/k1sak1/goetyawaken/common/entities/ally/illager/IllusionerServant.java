package com.k1sak1.goetyawaken.common.entities.ally.illager;

import javax.annotation.Nullable;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class IllusionerServant extends SpellcasterIllagerServant implements RangedAttackMob {
   private static final int NUM_ILLUSIONS = 4;
   private static final int ILLUSION_TRANSITION_TICKS = 3;
   private static final int ILLUSION_SPREAD = 3;
   private int clientSideIllusionTicks;
   private final Vec3[][] clientSideIllusionOffsets;

   public IllusionerServant(EntityType<? extends IllusionerServant> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.clientSideIllusionOffsets = new Vec3[2][4];

      for (int i = 0; i < 4; ++i) {
         this.clientSideIllusionOffsets[0][i] = Vec3.ZERO;
         this.clientSideIllusionOffsets[1][i] = Vec3.ZERO;
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SpellcasterIllagerServant.SpellcasterCastingSpellGoal());
      this.goalSelector.addGoal(4, new IllusionerServant.IllusionerMirrorSpellGoal());
      this.goalSelector.addGoal(5, new IllusionerServant.IllusionerBlindnessSpellGoal());
      this.goalSelector.addGoal(6, new RangedBowAttackGoal<>(this, 0.5D, 20, 15.0F));
   }

   public static AttributeSupplier.Builder setCustomAttributes() {
      return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, com.k1sak1.goetyawaken.config.AttributesConfig.IllusionerServantHealth.get())
            .add(Attributes.ARMOR, com.k1sak1.goetyawaken.config.AttributesConfig.IllusionerServantArmor.get())
            .add(Attributes.ARMOR_TOUGHNESS,
                  com.k1sak1.goetyawaken.config.AttributesConfig.IllusionerServantArmorToughness.get())
            .add(Attributes.MOVEMENT_SPEED,
                  com.k1sak1.goetyawaken.config.AttributesConfig.IllusionerServantMovementSpeed.get())
            .add(Attributes.FOLLOW_RANGE,
                  com.k1sak1.goetyawaken.config.AttributesConfig.IllusionerServantFollowRange.get());
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason,
         @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(3.0D, 0.0D, 3.0D);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level().isClientSide && this.isInvisible()) {
         --this.clientSideIllusionTicks;
         if (this.clientSideIllusionTicks < 0) {
            this.clientSideIllusionTicks = 0;
         }

         if (this.hurtTime != 1 && this.tickCount % 1200 != 0) {
            if (this.hurtTime == this.hurtDuration - 1) {
               this.clientSideIllusionTicks = 3;

               for (int k = 0; k < 4; ++k) {
                  this.clientSideIllusionOffsets[0][k] = this.clientSideIllusionOffsets[1][k];
                  this.clientSideIllusionOffsets[1][k] = new Vec3(0.0D, 0.0D, 0.0D);
               }
            }
         } else {
            this.clientSideIllusionTicks = 3;
            float f = -6.0F;
            int i = 13;

            for (int j = 0; j < 4; ++j) {
               this.clientSideIllusionOffsets[0][j] = this.clientSideIllusionOffsets[1][j];
               this.clientSideIllusionOffsets[1][j] = new Vec3(
                     (double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D,
                     (double) Math.max(0, this.random.nextInt(6) - 4),
                     (double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D);
            }

            for (int l = 0; l < 16; ++l) {
               this.level().addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getRandomY(), this.getZ(0.5D),
                     0.0D, 0.0D, 0.0D);
            }

            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE,
                  this.getSoundSource(), 1.0F, 1.0F, false);
         }
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   public Vec3[] getIllusionOffsets(float pPartialTick) {
      if (this.clientSideIllusionTicks <= 0) {
         return this.clientSideIllusionOffsets[1];
      } else {
         double d0 = (double) (((float) this.clientSideIllusionTicks - pPartialTick) / 3.0F);
         d0 = Math.pow(d0, 0.25D);
         Vec3[] avec3 = new Vec3[4];

         for (int i = 0; i < 4; ++i) {
            avec3[i] = this.clientSideIllusionOffsets[1][i].scale(1.0D - d0)
                  .add(this.clientSideIllusionOffsets[0][i].scale(d0));
         }

         return avec3;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ILLUSIONER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.ILLUSIONER_HURT;
   }

   protected SoundEvent getCastingSoundEvent() {
      return SoundEvents.ILLUSIONER_CAST_SPELL;
   }

   public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {
   }

   public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
      ItemStack itemstack = this.getProjectile(this.getItemInHand(
            ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem)));
      AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack, pDistanceFactor);
      if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem)
         abstractarrow = ((net.minecraft.world.item.BowItem) this.getMainHandItem().getItem())
               .customArrow(abstractarrow);
      double d0 = pTarget.getX() - this.getX();
      double d1 = pTarget.getY(0.3333333333333333D) - abstractarrow.getY();
      double d2 = pTarget.getZ() - this.getZ();
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      abstractarrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F,
            (float) (14 - this.level().getDifficulty().getId() * 4));
      this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level().addFreshEntity(abstractarrow);
   }

   public AbstractIllagerServant.IllagerServantArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllagerServant.IllagerServantArmPose.SPELLCASTING;
      } else {
         return this.isAggressive() ? AbstractIllagerServant.IllagerServantArmPose.BOW_AND_ARROW
               : AbstractIllagerServant.IllagerServantArmPose.CROSSED;
      }
   }

   class IllusionerBlindnessSpellGoal extends SpellcasterIllagerServant.SpellcasterUseSpellGoal {
      private int lastTargetId;

      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else if (IllusionerServant.this.getTarget() == null) {
            return false;
         } else if (IllusionerServant.this.getTarget().getId() == this.lastTargetId) {
            return false;
         } else {
            return IllusionerServant.this.level().getCurrentDifficultyAt(IllusionerServant.this.blockPosition())
                  .isHarderThan((float) Difficulty.NORMAL.ordinal());
         }
      }

      public void start() {
         super.start();
         LivingEntity livingentity = IllusionerServant.this.getTarget();
         if (livingentity != null) {
            this.lastTargetId = livingentity.getId();
         }

      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 180;
      }

      protected void performSpellCasting() {
         IllusionerServant.this.getTarget().addEffect(new MobEffectInstance(GoetyEffects.SENSE_LOSS.get(), 400),
               IllusionerServant.this);
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
      }

      protected SpellcasterIllagerServant.IllagerServantSpell getSpell() {
         return SpellcasterIllagerServant.IllagerServantSpell.BLINDNESS;
      }
   }

   class IllusionerMirrorSpellGoal extends SpellcasterIllagerServant.SpellcasterUseSpellGoal {
      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else {
            return !IllusionerServant.this.hasEffect(MobEffects.INVISIBILITY);
         }
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void performSpellCasting() {
         IllusionerServant.this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
      }

      @Nullable
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
      }

      protected SpellcasterIllagerServant.IllagerServantSpell getSpell() {
         return SpellcasterIllagerServant.IllagerServantSpell.DISAPPEAR;
      }
   }

   @Override
   public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      ItemStack currentMainHandItem = this.getMainHandItem();
      if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
         if (itemstack.getItem() instanceof BowItem) {
            this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
            this.dropEquipment(EquipmentSlot.MAINHAND, currentMainHandItem);
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            for (int i = 0; i < 7; ++i) {
               double d0 = this.random.nextGaussian() * 0.02D;
               double d1 = this.random.nextGaussian() * 0.02D;
               double d2 = this.random.nextGaussian() * 0.02D;
               this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                     this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
            if (!pPlayer.getAbilities().instabuild) {
               itemstack.shrink(1);
            }
            return InteractionResult.SUCCESS;
         } else if (itemstack.is(ItemTags.ARROWS)) {
            this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
            ItemStack offhandItem = this.getOffhandItem();
            if (offhandItem.isEmpty()) {
               this.setItemSlot(EquipmentSlot.OFFHAND, itemstack.split(itemstack.getCount()));
            } else if (offhandItem.is(itemstack.getItem())
                  && offhandItem.getCount() < offhandItem.getMaxStackSize()) {
               int needed = offhandItem.getMaxStackSize() - offhandItem.getCount();
               int toAdd = Math.min(needed, itemstack.getCount());
               offhandItem.grow(toAdd);
               if (!pPlayer.getAbilities().instabuild) {
                  itemstack.shrink(toAdd);
               }
            } else {
               this.spawnAtLocation(offhandItem);
               this.setItemSlot(EquipmentSlot.OFFHAND, itemstack.split(itemstack.getCount()));
            }
            this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
            for (int i = 0; i < 7; ++i) {
               double d0 = this.random.nextGaussian() * 0.02D;
               double d1 = this.random.nextGaussian() * 0.02D;
               double d2 = this.random.nextGaussian() * 0.02D;
               this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                     this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
            }
            if (!pPlayer.getAbilities().instabuild) {
               if (!itemstack.isEmpty()) {
                  itemstack.shrink(1);
               }
            }
            return InteractionResult.SUCCESS;
         }
      }
      return super.mobInteract(pPlayer, pHand);
   }
}
