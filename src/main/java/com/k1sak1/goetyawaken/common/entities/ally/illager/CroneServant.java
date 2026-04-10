package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import com.Polarice3.Goety.common.entities.neutral.VampireBat;
import com.Polarice3.Goety.common.entities.projectiles.ThrownBrew;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.k1sak1.goetyawaken.common.entities.projectiles.CroneServantBrew;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.effects.brew.BatsBrewEffect;
import com.Polarice3.Goety.common.effects.brew.BeesBrewEffect;
import com.Polarice3.Goety.common.effects.brew.BlindJumpBrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.LaunchBrewEffect;
import com.Polarice3.Goety.common.effects.brew.PurifyBrewEffect;
import com.Polarice3.Goety.common.effects.brew.StripBrewEffect;
import com.Polarice3.Goety.common.effects.brew.ThornTrapBrewEffect;
import com.Polarice3.Goety.common.effects.brew.TransposeBrewEffect;
import com.Polarice3.Goety.common.effects.brew.WebbedBrewEffect;
import com.Polarice3.Goety.common.effects.brew.block.HarvestBlockEffect;
import com.Polarice3.Goety.common.effects.brew.block.SweetBerriedEffect;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.CultistServant;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.WarlockServant;
import com.k1sak1.goetyawaken.common.entities.ai.NearestHealableAllyTargetGoal;
import com.k1sak1.goetyawaken.common.entities.ai.SupportAllyGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ModLootTables;
import com.Polarice3.Goety.utils.ServantUtil;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import com.Polarice3.Goety.init.ModTags.Items;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

public class CroneServant extends CultistServant implements RangedAttackMob {
    private static final EntityDataAccessor<Integer> DATA_TITLE_INDEX = SynchedEntityData.defineId(CroneServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_NAME_INDEX = SynchedEntityData.defineId(CroneServant.class,
            EntityDataSerializers.INT);
    private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID,
            "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(CroneServant.class,
            EntityDataSerializers.BOOLEAN);
    private int usingTime;
    private int hitTimes;
    private int lastHitTime;
    private int overwhelmed;
    private NearestHealableAllyTargetGoal healAlliesGoal;
    private SupportAllyGoal supportAllyGoal;

    public CroneServant(EntityType<? extends CultistServant> type, Level worldIn) {
        super(type, worldIn);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.healAlliesGoal = new NearestHealableAllyTargetGoal(this, true, (target) -> {
            return target != null
                    && MobUtil.areAllies(this, target)
                    && target.getType() != com.Polarice3.Goety.common.entities.ModEntityType.VEX_SERVANT.get()
                    && target.getType() != com.Polarice3.Goety.common.entities.ModEntityType.IRK_SERVANT.get()
                    && target.getType() != com.k1sak1.goetyawaken.common.entities.ModEntityType.TORMENTOR_SERVANT.get();
        });
        this.supportAllyGoal = new SupportAllyGoal(this);
        this.goalSelector.addGoal(1, this.supportAllyGoal);
        this.goalSelector.addGoal(2, new BrewThrowsGoal(this));
        this.goalSelector.addGoal(2, new FastBrewThrowsGoal(this));
        this.goalSelector.addGoal(1, new WitchServantBarterGoal(this));
        this.goalSelector.addGoal(1, new CroneTeleportGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.targetSelector.addGoal(2, this.healAlliesGoal);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.CroneHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.CroneHealth.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TITLE_INDEX, 0);
        this.entityData.define(DATA_NAME_INDEX, 0);
        this.getEntityData().define(DATA_USING_ITEM, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("hitTimes", this.hitTimes);
        pCompound.putInt("lastHitTime", this.lastHitTime);
        pCompound.putInt("overwhelmed", this.overwhelmed);
        pCompound.putInt("TitleIndex", this.entityData.get(DATA_TITLE_INDEX));
        pCompound.putInt("NameIndex", this.entityData.get(DATA_NAME_INDEX));
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.hitTimes = pCompound.getInt("hitTimes");
        this.lastHitTime = pCompound.getInt("lastHitTime");
        this.overwhelmed = pCompound.getInt("overwhelmed");
        if (pCompound.contains("TitleIndex")) {
            this.entityData.set(DATA_TITLE_INDEX, pCompound.getInt("TitleIndex"));
        }
        if (pCompound.contains("NameIndex")) {
            this.entityData.set(DATA_NAME_INDEX, pCompound.getInt("NameIndex"));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
            MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (this.entityData.get(DATA_TITLE_INDEX) == 0 && this.entityData.get(DATA_NAME_INDEX) == 0) {
            int randomTitle = this.random.nextInt(4);
            int randomName;
            if (randomTitle == 0) {
                randomName = 12 + this.random.nextInt(6);
            } else {
                randomName = this.random.nextInt(12);
            }
            this.entityData.set(DATA_TITLE_INDEX, randomTitle + 1);
            this.entityData.set(DATA_NAME_INDEX, randomName + 1);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public Component getName() {
        int titleIndex = this.entityData.get(DATA_TITLE_INDEX) - 1;
        int nameIndex = this.entityData.get(DATA_NAME_INDEX) - 1;
        if (titleIndex >= 0 && titleIndex < 4 && nameIndex >= 0) {
            Component component = Component.translatable("title.goety.crone." + titleIndex);
            Component component1 = Component.translatable("name.goety.crone." + nameIndex);
            return Component.translatable(component.getString() + " " + component1.getString());
        }
        return super.getName();
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.CRONE_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_34154_) {
        return SoundEvents.WITCH_HURT;
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.CRONE_DEATH.get();
    }

    public void setUsingItem(boolean p_34164_) {
        this.getEntityData().set(DATA_USING_ITEM, p_34164_);
    }

    public boolean isDrinkingPotion() {
        return this.getEntityData().get(DATA_USING_ITEM);
    }

    @Override
    public void die(DamageSource p_37847_) {
        if (p_37847_.getEntity() != null && p_37847_.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), MathHelper.minutesToTicks(1)));
        }
        if (!this.level().isClientSide) {
            if (!this.canRevive(p_37847_)) {
                ItemStack croneHat = new ItemStack(com.Polarice3.Goety.common.items.ModItems.CRONE_HAT.get());
                if (this.getTrueOwner() != null) {
                    com.Polarice3.Goety.common.entities.projectiles.FlyingItem flyingItem = new com.Polarice3.Goety.common.entities.projectiles.FlyingItem(
                            com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                            this.level(),
                            this.getX(),
                            this.getY() + 1.0D,
                            this.getZ());

                    flyingItem.setOwner(this.getTrueOwner());
                    flyingItem.setItem(croneHat);
                    flyingItem.setParticle(net.minecraft.core.particles.ParticleTypes.SOUL);
                    flyingItem.setSecondsCool(30);
                    this.level().addFreshEntity(flyingItem);
                } else {
                    net.minecraft.world.entity.item.ItemEntity itemEntity = this.spawnAtLocation(croneHat);
                    if (itemEntity != null) {
                        itemEntity.setExtendedLifetime();
                    }
                }
            }
        }
        super.die(p_37847_);
    }

    public void aiStep() {
        if (!this.level().isClientSide && this.isAlive()) {
            if (this.lastHitTime > 0) {
                --this.lastHitTime;
            }
            this.healAlliesGoal.decrementCooldown();
            this.supportAllyGoal.decrementCooldown();

            if (this.isDrinkingPotion()) {
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false);
                    ItemStack itemstack = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    if (itemstack.is(ModItems.BREW.get())) {
                        List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
                        for (MobEffectInstance mobeffectinstance : list) {
                            this.addEffect(new MobEffectInstance(mobeffectinstance));
                        }
                        List<BrewEffectInstance> list1 = BrewUtils.getBrewEffects(itemstack);
                        for (BrewEffectInstance brewEffectInstance : list1) {
                            brewEffectInstance.getEffect().drinkBlockEffect(this, this, this,
                                    brewEffectInstance.getAmplifier(), BrewUtils.getAreaOfEffect(itemstack));
                        }
                    }

                    this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
                }
            } else {
                int amp = 0;
                if (this.level().random.nextFloat() <= 0.05F && this.level().getDifficulty() == Difficulty.NORMAL) {
                    amp = 2;
                } else if (this.level().random.nextFloat() <= 0.25F) {
                    amp = 1;
                }
                List<MobEffectInstance> mobEffectInstance = new ArrayList<>();
                List<BrewEffectInstance> brewEffectInstance = new ArrayList<>();
                if (this.random.nextFloat() < 0.15F && (this.isInWall() || (this.getLastDamageSource() != null
                        && this.getLastDamageSource().is(DamageTypes.IN_WALL)))) {
                    brewEffectInstance.add(new BrewEffectInstance(new BlindJumpBrewEffect(0), 1, amp));
                } else if (this.random.nextFloat() < 0.15F && this.getLastDamageSource() != null
                        && (this.getLastDamageSource().is(DamageTypes.CACTUS)
                                || this.getLastDamageSource().is(DamageTypes.SWEET_BERRY_BUSH))) {
                    brewEffectInstance.add(new BrewEffectInstance(new HarvestBlockEffect()));
                } else if (this.random.nextFloat() < 0.15F && this.getHealth() < this.getMaxHealth()
                        && (this.getTarget() == null || this.lastHitTime == 0)) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.HEAL, 1, amp));
                } else if (this.random.nextFloat() < 0.15F && this.isEyeInFluidType(ForgeMod.WATER_TYPE.get())
                        && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.WATER_BREATHING, 3600));
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.SWIFT_SWIM.get(), 3600));
                } else if (this.random.nextFloat() < 0.15F
                        && (this.isOnFire() || this.getLastDamageSource() != null
                                && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE))
                        && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600));
                } else if (this.random.nextFloat() < 0.15F && this.getLastDamageSource() != null
                        && this.getLastDamageSource().is(DamageTypeTags.IS_FALL)
                        && !this.hasEffect(MobEffects.SLOW_FALLING)) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.SLOW_FALLING, 3600));
                } else if (this.random.nextFloat() < 0.15F && this.getLastDamageSource() != null
                        && ModDamageSource.physicalAttacks(this.getLastDamageSource())
                        && !this.hasEffect(GoetyEffects.REPULSIVE.get())) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.REPULSIVE.get(), 1800 / (amp + 1), amp));
                } else if (this.random.nextFloat() < 0.15F && this.getTarget() != null) {
                    if ((this.random.nextFloat() <= 0.15F && this.getTarget().distanceTo(this) <= 4.0F)
                            || this.getHealth() <= 15.0F) {
                        brewEffectInstance.add(new BrewEffectInstance(new BlindJumpBrewEffect(0), 1, amp));
                    } else if (this.random.nextFloat() <= 0.15F && !this.hasEffect(MobEffects.REGENERATION)) {
                        mobEffectInstance.add(new MobEffectInstance(MobEffects.REGENERATION, 900 / (amp + 1), amp));
                        if (this.random.nextFloat() < 0.25F && MobUtil.isInSunlight(this)
                                && !this.hasEffect(GoetyEffects.PHOTOSYNTHESIS.get())) {
                            mobEffectInstance.add(new MobEffectInstance(GoetyEffects.PHOTOSYNTHESIS.get(), 1800));
                        }
                    } else if (this.random.nextFloat() < 0.05F && !this.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                        mobEffectInstance.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800));
                    } else if (this.random.nextFloat() < 0.05F && !this.hasEffect(GoetyEffects.FROSTY_AURA.get())
                            && !this.hasEffect(GoetyEffects.FIERY_AURA.get())) {
                        if (this.random.nextBoolean()) {
                            if (!this.getTarget().hasEffect(GoetyEffects.FREEZING.get())
                                    && !this.getTarget().hasEffect(MobEffects.FIRE_RESISTANCE)
                                    && !this.getTarget().fireImmune()) {
                                mobEffectInstance.add(
                                        new MobEffectInstance(GoetyEffects.FIERY_AURA.get(), 1800 / (amp + 1), amp));
                            }
                        } else {
                            if (!this.getTarget().isOnFire() && this.getTarget().canFreeze()) {
                                mobEffectInstance.add(
                                        new MobEffectInstance(GoetyEffects.FROSTY_AURA.get(), 1800 / (amp + 1), amp));
                            }
                        }
                    }
                } else if (this.random.nextFloat() <= 0.15F && MobUtil.isInWeb(this)
                        && !this.hasEffect(GoetyEffects.CLIMBING.get())) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.CLIMBING.get(), 3600));
                } else if (this.random.nextFloat() < 0.05F && MobUtil.hasLongNegativeEffects(this)) {
                    brewEffectInstance.add(new BrewEffectInstance(
                            new PurifyBrewEffect("purify_debuff", 0, 0, MobEffectCategory.BENEFICIAL, 0x385858, true)));
                }

                if (!mobEffectInstance.isEmpty() || !brewEffectInstance.isEmpty()) {
                    ItemStack brew = BrewUtils.setCustomEffects(new ItemStack(ModItems.BREW.get()), mobEffectInstance,
                            brewEffectInstance);
                    BrewUtils.setAreaOfEffect(brew, this.level().random.nextInt(amp + 1));
                    brew.getOrCreateTag().putInt("CustomPotionColor",
                            BrewUtils.getColor(mobEffectInstance, brewEffectInstance));
                    this.setItemSlot(EquipmentSlot.MAINHAND, brew);
                    this.usingTime = this.overwhelmed > 0 ? this.getMainHandItem().getUseDuration() / 2
                            : this.getMainHandItem().getUseDuration();
                    this.setUsingItem(true);
                    if (!this.isSilent()) {
                        this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F,
                                0.8F + this.random.nextFloat() * 0.4F);
                    }

                    AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                    attributeinstance.removeModifier(SPEED_MODIFIER_DRINKING);
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_DRINKING);
                } else if (this.getMainHandItem().getItem() instanceof BrewItem) {
                    this.usingTime = this.overwhelmed > 0 ? this.getMainHandItem().getUseDuration() / 2
                            : this.getMainHandItem().getUseDuration();
                    this.setUsingItem(true);
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
        return ModSounds.CRONE_LAUGH.get();
    }

    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 15) {
            for (int i = 0; i < this.random.nextInt(35) + 10; ++i) {
                this.level().addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double) 0.13F,
                        this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * (double) 0.13F,
                        this.getZ() + this.random.nextGaussian() * (double) 0.13F, 0.0D, 0.0D, 0.0D);
            }
        } else if (p_34138_ == 46) {
            int i = 128;

            for (int j = 0; j < i; ++j) {
                double d0 = (double) j / 127.0D;
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, this.xo, this.getX())
                        + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.yo, this.getY()) + this.random.nextDouble() * (double) this.getBbHeight();
                double d3 = Mth.lerp(d0, this.zo, this.getZ())
                        + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                this.level().addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double) f, (double) f1, (double) f2);
            }
        } else {
            super.handleEntityEvent(p_34138_);
        }
    }

    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
        if (p_34149_.getEntity() == this) {
            p_34150_ = 0.0F;
        }

        if (p_34149_.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            p_34150_ *= 0.15F;
        }

        return p_34150_;
    }

    public void performRangedAttack(LivingEntity target, float p_34144_) {
        if (!this.isDrinkingPotion()) {
            boolean grief = ForgeEventFactory.getMobGriefingEvent(this.level(), this);
            Vec3 vec3 = target.getDeltaMovement();
            double d0 = target.getX() + vec3.x - this.getX();
            double d1 = target.getEyeY() - (double) 1.1F - this.getY();
            double d2 = target.getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            int amp = 0;
            if (this.level().random.nextFloat() <= 0.05F && this.level().getDifficulty() != Difficulty.EASY) {
                amp = 2;
            } else if (this.level().random.nextFloat() <= 0.25F) {
                amp = 1;
            }
            List<MobEffectInstance> mobEffectInstance = new ArrayList<>();
            List<BrewEffectInstance> brewEffectInstance = new ArrayList<>();
            if (MobUtil.areAllies(this, target)
                    && target.getType() != com.Polarice3.Goety.common.entities.ModEntityType.VEX_SERVANT.get()
                    && target.getType() != com.Polarice3.Goety.common.entities.ModEntityType.IRK_SERVANT.get()
                    && target.getType() != com.k1sak1.goetyawaken.common.entities.ModEntityType.TORMENTOR_SERVANT.get()
                    && (!(target instanceof Mob mob) || mob.getTarget() != this)) {
                if (target.getHealth() <= 4.0F) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.HEAL, 1));
                } else {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.REGENERATION, 900));
                    if (this.random.nextFloat() <= 0.05F) {
                        mobEffectInstance.add(new MobEffectInstance(MobEffects.ABSORPTION, 1800));
                    }
                }
                this.setTarget(null);
            } else if (d3 >= 8.0D && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                mobEffectInstance.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800 / (amp + 1), amp));
                if (this.random.nextFloat() <= 0.25F && target.onGround() && grief
                        && this.noAlliesExceptOwner(target)) {
                    brewEffectInstance.add(new BrewEffectInstance(new SweetBerriedEffect(), 1, amp));
                }
                if (this.random.nextFloat() <= 0.25F && this.noBrewMinions(target) && grief) {
                    brewEffectInstance.add(new BrewEffectInstance(new WebbedBrewEffect(0, 0), 1, amp));
                } else if (this.random.nextFloat() <= 0.5F) {
                    if (this.random.nextBoolean()) {
                        brewEffectInstance.add(new BrewEffectInstance(new TransposeBrewEffect()));
                    }
                }
            } else if (target.getHealth() >= 8.0F && !target.hasEffect(MobEffects.POISON)
                    && target.canBeAffected(new MobEffectInstance(MobEffects.POISON))) {
                if (!target.hasEffect(MobEffects.POISON)
                        && target.canBeAffected(new MobEffectInstance(MobEffects.POISON))) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.POISON, 900 / (amp + 1), amp));
                }
                if (this.random.nextFloat() <= 0.25F && !this.hasEffect(GoetyEffects.FIERY_AURA.get())
                        && !target.hasEffect(GoetyEffects.FREEZING.get())
                        && !target.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.FREEZING.get(), 900 / (amp + 1), amp));
                } else if (this.random.nextFloat() <= 0.25F && !this.hasEffect(GoetyEffects.FROSTY_AURA.get())
                        && !target.hasEffect(GoetyEffects.FLAMMABLE.get()) && !target.fireImmune()) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.FLAMMABLE.get(), 900 / (amp + 1), amp));
                } else if (this.random.nextFloat() <= 0.05F && !target.hasEffect(GoetyEffects.TRIPPING.get())) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.TRIPPING.get(), 1800 / (amp + 1), amp));
                }
            } else if (target.hasEffect(MobEffects.REGENERATION) && !target.hasEffect(GoetyEffects.CURSED.get())) {
                mobEffectInstance.add(new MobEffectInstance(GoetyEffects.CURSED.get(), 600, 0));
            } else if (this.getLastDamageSource() != null
                    && ModDamageSource.physicalAttacks(this.getLastDamageSource())
                    && !target.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                mobEffectInstance.add(new MobEffectInstance(MobEffects.WEAKNESS, 1800 / (amp + 1), amp));
                if (this.random.nextFloat() <= 0.25F) {
                    brewEffectInstance.add(new BrewEffectInstance(new TransposeBrewEffect(), 1, amp));
                } else if (this.random.nextFloat() <= 0.5F
                        && !target.fireImmune()
                        && MobUtil.isInSunlight(target)
                        && !target.hasEffect(MobEffects.FIRE_RESISTANCE)
                        && !target.hasEffect(GoetyEffects.SUN_ALLERGY.get())) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.SUN_ALLERGY.get(), 3600 / (amp + 1), amp));
                } else if (this.random.nextFloat() <= 0.75F
                        && target.level().getMaxLocalRawBrightness(target.blockPosition()) < 2
                        && !target.hasEffect(GoetyEffects.NYCTOPHOBIA.get())) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.NYCTOPHOBIA.get(), 1800 / (amp + 1), amp));
                } else if (!target.hasEffect(GoetyEffects.SAPPED.get())) {
                    mobEffectInstance.add(new MobEffectInstance(GoetyEffects.SAPPED.get(), 1800 / (amp + 1), amp));
                }
            } else {
                if (this.random.nextFloat() <= 0.05F) {
                    brewEffectInstance.add(new BrewEffectInstance(new StripBrewEffect(0, 0)));
                } else if (this.random.nextFloat() <= 0.25F
                        && (target.onGround() || (!target.hasEffect(MobEffects.LEVITATION)
                                && !target.hasEffect(GoetyEffects.PLUNGE.get())))) {
                    if (target.onGround() && grief && this.noAlliesExceptOwner(target)) {
                        brewEffectInstance.add(new BrewEffectInstance(new ThornTrapBrewEffect(0), 1, amp));
                    } else if (!target.hasEffect(MobEffects.LEVITATION)
                            && !target.hasEffect(GoetyEffects.PLUNGE.get())) {
                        mobEffectInstance.add(new MobEffectInstance(GoetyEffects.PLUNGE.get(), 900 / (amp + 1), amp));
                    }
                } else if (this.random.nextFloat() <= 0.35F && !target.hasEffect(MobEffects.BLINDNESS)
                        && !MobUtil.isInWeb(target) && target.getMaxHealth() > 10.0F && this.noBrewMinions(target)
                        && this.noAlliesExceptOwner(target)) {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.BLINDNESS, MathHelper.secondsToTicks(5)));
                    brewEffectInstance.add(new BrewEffectInstance(new BatsBrewEffect(0, 0)));
                } else if (this.random.nextFloat() <= 0.55F) {
                    brewEffectInstance.add(new BrewEffectInstance(new LaunchBrewEffect(), 1));
                } else if (this.random.nextFloat() <= 0.75F && !MobUtil.isInWeb(target) && target.getMaxHealth() > 10.0F
                        && this.noBrewMinions(target) && this.noAlliesExceptOwner(target)) {
                    brewEffectInstance.add(new BrewEffectInstance(new BeesBrewEffect(0, 0)));
                } else {
                    mobEffectInstance.add(new MobEffectInstance(MobEffects.HARM, 1, amp));
                }
            }

            if (!mobEffectInstance.isEmpty() || !brewEffectInstance.isEmpty()) {
                ItemStack brew0;
                if (com.k1sak1.goetyawaken.Config.croneServantAllowGasBrew &&
                        this.level().random.nextFloat() <= 0.15F &&
                        this.level().getDifficulty() == Difficulty.HARD) {
                    brew0 = new ItemStack(ModItems.GAS_BREW.get());
                } else {
                    brew0 = new ItemStack(ModItems.SPLASH_BREW.get());
                }
                ItemStack brew = BrewUtils.setCustomEffects(brew0, mobEffectInstance, brewEffectInstance);
                BrewUtils.setAreaOfEffect(brew, this.level().random.nextInt(amp + 1));
                brew.getOrCreateTag().putInt("CustomPotionColor",
                        BrewUtils.getColor(mobEffectInstance, brewEffectInstance));

                ThrowableItemProjectile thrownBrew;
                if (brew0.is(ModItems.SPLASH_BREW.get())) {
                    thrownBrew = new CroneServantBrew(this.level(), this);
                } else {
                    thrownBrew = new ThrownBrew(this.level(), this);
                }
                thrownBrew.setItem(brew);
                float velocity = 0.75F;
                if (target.distanceTo(this) >= 4.0F) {
                    thrownBrew.setXRot(thrownBrew.getXRot() + 20.0F);
                } else {
                    thrownBrew.setXRot(thrownBrew.getXRot());
                    velocity = 1.0F;
                }
                thrownBrew.shoot(d0, d1 + d3 * 0.2D, d2, velocity, 8.0F);
                if (!this.isSilent()) {
                    this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.WITCH_THROW,
                            this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
                }

                this.level().addFreshEntity(thrownBrew);
            }
        }
    }

    public boolean noBrewMinions(LivingEntity livingEntity) {
        return this.level().getEntitiesOfClass(VampireBat.class, livingEntity.getBoundingBox().inflate(2.0D)).isEmpty()
                && this.level().getEntitiesOfClass(Bee.class, livingEntity.getBoundingBox().inflate(2.0D)).isEmpty();
    }

    public boolean noAlliesExceptOwner(LivingEntity target) {
        List<LivingEntity> allies = this.level().getEntitiesOfClass(LivingEntity.class,
                target.getBoundingBox().inflate(32.0D),
                entity -> entity != this && entity != this.getOwner() && MobUtil.areAllies(this, entity));
        return allies.isEmpty();
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.getHealth() <= 10.0F) {
            if (pSource.is(DamageTypes.CACTUS) || pSource.is(DamageTypes.SWEET_BERRY_BUSH)
                    || pSource.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
                return false;
            }
        }

        if (pSource.getEntity() instanceof LivingEntity livingentity && livingentity != this) {
            this.lastHitTime = MathHelper.secondsToTicks(15);
            if (MobsConfig.CroneThornDefense.get()) {
                if (!pSource.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS) && !pSource.is(DamageTypes.THORNS)) {
                    float thorn = 2.0F;
                    if (this.level().getDifficulty() == Difficulty.HARD) {
                        thorn *= 2.0F;
                    }
                    livingentity.hurt(this.damageSources().thorns(this), thorn);
                }
            }
            if (pAmount >= 15) {
                this.overwhelmed = MathHelper.secondsToTicks(15);
            }
        }

        return super.hurt(pSource, pAmount);
    }

    protected void teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            for (int i = 0; i < 128; ++i) {
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                double d4 = this.getY();
                if (this.getTarget() != null) {
                    d4 = this.getTarget().getY();
                }
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                if (this.getHealth() <= 0.0F) {
                    break;
                }
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory
                        .onEnderTeleport(this, d3, d4, d5);
                if (event.isCanceled()) {
                    break;
                }
                if (this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false)) {
                    this.teleportHits();
                    break;
                }
            }
        }
    }

    private boolean CanteleportTowards(Entity entity) {
        if (!this.level().isClientSide() && this.isAlive()) {
            for (int i = 0; i < 128; ++i) {
                Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(),
                        this.getZ() - entity.getZ());
                vector3d = vector3d.normalize();
                double d0 = 16.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vector3d.y * d0;
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
                if (this.getHealth() <= 0.0F) {
                    return false;
                }
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory
                        .onEnderTeleport(this, d1, d2, d3);
                if (event.isCanceled()) {
                    return false;
                }
                if (this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                    this.teleportHits();
                    return true;
                }
            }
        }
        return false;
    }

    public void teleportHits() {
        this.level().broadcastEntityEvent(this, (byte) 46);
        this.level().gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
        if (!this.isSilent()) {
            this.level().playSound((Player) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT,
                    this.getSoundSource(), 1.0F, 0.75F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.75F);
        }
    }

    static class CroneTeleportGoal extends Goal {
        private final CroneServant crone;
        private int teleportTime;

        public CroneTeleportGoal(CroneServant p_32573_) {
            this.crone = p_32573_;
        }

        public boolean canUse() {
            return this.crone.getTarget() != null;
        }

        public void start() {
            super.start();
            this.teleportTime = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.crone.getTarget() != null;
        }

        public void tick() {
            super.tick();
            if (this.crone.getTarget() != null && !this.crone.isPassenger()) {
                if ((this.crone.getTarget().distanceToSqr(this.crone) > 256
                        || !MobUtil.hasVisualLineOfSight(this.crone, this.crone.getTarget()))
                        && this.teleportTime++ >= this.adjustedTickDelay(30)
                        && this.crone.CanteleportTowards(this.crone.getTarget())) {
                    this.teleportTime = 0;
                }
            }
        }
    }

    static class BrewThrowsGoal extends RangedAttackGoal {
        public CroneServant crone;

        public BrewThrowsGoal(CroneServant p_25773_) {
            super(p_25773_, 1.0D, 20, 40, 10.0F);
            this.crone = p_25773_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && (this.crone.getHealth() >= (this.crone.getMaxHealth() / 4));
        }
    }

    static class FastBrewThrowsGoal extends RangedAttackGoal {
        public CroneServant crone;

        public FastBrewThrowsGoal(CroneServant p_25773_) {
            super(p_25773_, 1.0D, 15, 30, 10.0F);
            this.crone = p_25773_;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.crone.getHealth() < (this.crone.getMaxHealth() / 4);
        }
    }

    public class WitchServantBarterGoal extends Goal {
        private int progress = 100;
        public RaiderServant witch;

        public WitchServantBarterGoal(RaiderServant witch) {
            this.witch = witch;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
        }

        public boolean isInterruptable() {
            return false;
        }

        public void tick() {
            this.witch.setTarget((LivingEntity) null);
            LivingEntity trader = this.witch.getTrader();
            if (--this.progress > 0) {
                this.witch.getNavigation().stop();
                if (trader != null && this.witch.distanceTo(trader) <= 16.0F) {
                    this.witch.getLookControl().setLookAt(trader);
                }
            }

            if (this.progress <= 0) {
                Vec3 vec3 = trader != null ? trader.position() : this.witch.position();
                if (!this.witch.level().isClientSide && this.witch.level().getServer() != null) {
                    float luck = 0.0F;
                    if (this.witch.getMainHandItem().is(Items.WITCH_BETTER_CURRENCY)) {
                        luck = 1.0F;
                    }

                    LootTable loottable = this.witch.level().getServer().getLootData()
                            .getLootTable(ModLootTables.WITCH_BARTER);
                    if (this.witch instanceof WarlockServant) {
                        loottable = this.witch.level().getServer().getLootData()
                                .getLootTable(ModLootTables.WARLOCK_BARTER);
                    }

                    List<ItemStack> list = loottable
                            .getRandomItems((new LootParams.Builder((ServerLevel) this.witch.level()))
                                    .withParameter(LootContextParams.THIS_ENTITY, this.witch)
                                    .withParameter(LootContextParams.ORIGIN, this.witch.position()).withLuck(luck)
                                    .create(LootContextParamSets.GIFT));
                    Iterator var6 = list.iterator();

                    while (var6.hasNext()) {
                        ItemStack itemstack = (ItemStack) var6.next();
                        BehaviorUtils.throwItem(this.witch, itemstack, vec3.add(0.0, 1.0, 0.0));
                    }
                }

                this.clearTrade();
            }

            if (this.witch.hurtTime != 0
                    && (this.witch.getItemInHand(InteractionHand.MAIN_HAND).is(Items.WITCH_CURRENCY)
                            || this.witch.getItemInHand(InteractionHand.MAIN_HAND).is(Items.WITCH_BETTER_CURRENCY))) {
                this.witch.spawnAtLocation(this.witch.getItemInHand(InteractionHand.MAIN_HAND));
                this.clearTrade();
            }

        }

        protected void addParticlesAroundSelf(ParticleOptions p_35288_) {
            if (!this.witch.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.witch.level();

                for (int i = 0; i < 5; ++i) {
                    double d0 = this.witch.getRandom().nextGaussian() * 0.02;
                    double d1 = this.witch.getRandom().nextGaussian() * 0.02;
                    double d2 = this.witch.getRandom().nextGaussian() * 0.02;
                    serverLevel.sendParticles(p_35288_, this.witch.getRandomX(1.0), this.witch.getRandomY() + 1.0,
                            this.witch.getRandomZ(1.0), 0, d0, d1, d2, 0.5);
                }
            }

        }

        public boolean canUse() {
            return this.witch.getMainHandItem().is(Items.WITCH_CURRENCY)
                    || this.witch.getMainHandItem().is(Items.WITCH_BETTER_CURRENCY);
        }

        public void start() {
            super.start();
            this.progress = 100;
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        }

        public void clearTrade() {
            this.witch.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            this.witch.setTrader((LivingEntity) null);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        boolean isOwner = this.getTrueOwner() != null && pPlayer == this.getTrueOwner();
        if (this.getMainHandItem().isEmpty() && pHand == InteractionHand.MAIN_HAND
                && itemstack.is(ModTags.Items.WITCH_CURRENCY)) {
            if (isOwner) {
                if (!this.isAggressive()) {
                    this.playSound(this.getCelebrateSound());
                    ItemStack itemstack1;
                    if (pPlayer.isCreative()) {
                        itemstack1 = itemstack;
                    } else {
                        itemstack1 = itemstack.split(1);
                    }
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack1);
                    this.setTrader(pPlayer);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (isOwner) {
            return ServantUtil.equipServantArmor(pPlayer, this, itemstack, super.mobInteract(pPlayer, pHand));
        }

        return super.mobInteract(pPlayer, pHand);
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
