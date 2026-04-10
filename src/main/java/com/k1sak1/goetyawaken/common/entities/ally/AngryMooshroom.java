package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ai.ChargeGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.DifficultyInstance;
import javax.annotation.Nullable;

import java.util.UUID;

public class AngryMooshroom extends AnimalSummon implements com.Polarice3.Goety.api.entities.ICharger {
    private static final EntityDataAccessor<String> DATA_TYPE = SynchedEntityData.defineId(AngryMooshroom.class,
            EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_IS_SCREAMING_MOOSHROOM = SynchedEntityData
            .defineId(AngryMooshroom.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CHARGING = SynchedEntityData.defineId(AngryMooshroom.class,
            EntityDataSerializers.BOOLEAN);
    private static final AttributeModifier CHARGE_SPEED_BOOST = new AttributeModifier(
            UUID.fromString("1eaf83ad-340f-43f0-92c5-0f1e3c8e3b3d"), "Charge speed boost", 1.0D,
            AttributeModifier.Operation.MULTIPLY_BASE);
    private boolean isLoweringHead;
    private int lowerHeadTick;
    private int longJumpCool;

    public AngryMooshroom(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
        this.setConfigurableAttributes();
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        GroundPathNavigation groundpathnavigation = new GroundPathNavigation(this, pLevel);
        groundpathnavigation.setCanOpenDoors(false);
        groundpathnavigation.setCanFloat(true);
        groundpathnavigation.setCanPassDoors(true);
        return groundpathnavigation;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new ChargeGoal(this, 1.0F, 20) {
            @Override
            public boolean canUse() {
                if (this.charger.getRandom().nextInt(20) != 0) {
                    return false;
                } else {
                    this.chargeTarget = this.charger.getTarget();
                    if (this.chargeTarget == null) {
                        return false;
                    } else if (!this.chargeTarget.isAlive()) {
                        return false;
                    } else {
                        this.chargePos = this.findChargePoint(this.charger, this.chargeTarget);
                        return this.charger.distanceToSqr(this.chargePos) > 4.0D;
                    }
                }
            }

            @Override
            public void start() {
                super.start();
                this.charger.level().broadcastEntityEvent(this.charger, (byte) 58);
            }

            @Override
            public void stop() {
                super.stop();
                this.charger.level().broadcastEntityEvent(this.charger, (byte) 59);
            }

            @Override
            protected net.minecraft.world.phys.Vec3 findChargePoint(Entity attacker, Entity target) {
                double vecx = target.getX() - attacker.getX();
                double vecz = target.getZ() - attacker.getZ();
                float rangle = (float) (Math.atan2(vecz, vecx));

                double distance = net.minecraft.util.Mth.sqrt((float) (vecx * vecx + vecz * vecz));
                double overshoot = 2.1D;

                double dx = net.minecraft.util.Mth.cos(rangle) * (distance + overshoot);
                double dz = net.minecraft.util.Mth.sin(rangle) * (distance + overshoot);

                return new net.minecraft.world.phys.Vec3(attacker.getX() + dx, target.getY(), attacker.getZ() + dz);
            }
        });
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.AngryMooshroomHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.AngryMooshroomMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.AngryMooshroomArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.AngryMooshroomDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.AngryMooshroomHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.AngryMooshroomArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.AngryMooshroomDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.AngryMooshroomMovementSpeed.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE, "red");
        this.entityData.define(DATA_IS_SCREAMING_MOOSHROOM, false);
        this.entityData.define(DATA_CHARGING, false);
    }

    public void addAdditionalSaveData(CompoundTag p_149385_) {
        super.addAdditionalSaveData(p_149385_);
        p_149385_.putString("Type", this.getVariant());
        p_149385_.putBoolean("IsScreamingMooshroom", this.isScreamingMooshroom());
    }

    public void readAdditionalSaveData(CompoundTag p_149373_) {
        super.readAdditionalSaveData(p_149373_);
        this.setConfigurableAttributes();
        this.setVariant(p_149373_.getString("Type"));
        this.setScreamingMooshroom(p_149373_.getBoolean("IsScreamingMooshroom"));
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    public void setVariant(String pVariant) {
        this.entityData.set(DATA_TYPE, pVariant);
    }

    public String getVariant() {
        return this.entityData.get(DATA_TYPE);
    }

    public boolean isBrownVariant() {
        return "brown".equals(this.getVariant());
    }

    public boolean isScreamingMooshroom() {
        return this.entityData.get(DATA_IS_SCREAMING_MOOSHROOM);
    }

    public void setScreamingMooshroom(boolean screaming) {
        this.entityData.set(DATA_IS_SCREAMING_MOOSHROOM, screaming);
    }

    public float getRammingXHeadRot() {
        return (float) this.lowerHeadTick / 20.0F * 30.0F * ((float) Math.PI / 180F);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isLoweringHead) {
            ++this.lowerHeadTick;
        } else {
            this.lowerHeadTick -= 2;
        }

        this.lowerHeadTick = Mth.clamp(this.lowerHeadTick, 0, 20);

        if (this.longJumpCool > 0) {
            --this.longJumpCool;
        }

        if (this.isCharging()) {
            AttributeInstance movementSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                if (!movementSpeed.hasModifier(CHARGE_SPEED_BOOST)) {
                    movementSpeed.addTransientModifier(CHARGE_SPEED_BOOST);
                }
            }
        } else {
            AttributeInstance movementSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                if (movementSpeed.hasModifier(CHARGE_SPEED_BOOST)) {
                    movementSpeed.removeModifier(CHARGE_SPEED_BOOST);
                }
            }
        }
    }

    @Override
    protected void ageBoundaryReached() {
        AttributeInstance instance = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null) {
            if (this.isBaby()) {
                instance.setBaseValue(AttributesConfig.AngryMooshroomDamage.get() / 2.0D);
            } else {
                instance.setBaseValue(AttributesConfig.AngryMooshroomDamage.get());
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        if (this.isUpgraded() && pEntity instanceof LivingEntity target) {
            if (this.random.nextBoolean()) {
                target.addEffect(new MobEffectInstance(
                        com.Polarice3.Goety.common.effects.GoetyEffects.CURSED.get(), 100, 0), this);
            } else {
                target.addEffect(new MobEffectInstance(
                        com.Polarice3.Goety.common.effects.GoetyEffects.SAPPED.get(), 100, 0), this);
            }
        }
        return super.doHurtTarget(pEntity);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.COW_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.COW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COW_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.playSound(SoundEvents.GOAT_HORN_BREAK, 1.0F, 1.0F);
        } else if (pId == 58) {
            this.isLoweringHead = true;
        } else if (pId == 59) {
            this.isLoweringHead = false;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isCharging() {
        return this.entityData.get(DATA_CHARGING);
    }

    @Override
    public void setCharging(boolean flag) {
        this.entityData.set(DATA_CHARGING, flag);
    }

    @Override
    public void setUpgraded(boolean upgraded) {
        super.setUpgraded(upgraded);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null && armor != null && attack != null) {
            if (upgraded) {
                health.setBaseValue(AttributesConfig.AngryMooshroomHealth.get() * 1.5D);
                armor.setBaseValue(AttributesConfig.AngryMooshroomArmor.get() + 4.0D);
                attack.setBaseValue(AttributesConfig.AngryMooshroomDamage.get() + 2.0D);
            } else {
                health.setBaseValue(AttributesConfig.AngryMooshroomHealth.get());
                armor.setBaseValue(AttributesConfig.AngryMooshroomArmor.get());
                attack.setBaseValue(AttributesConfig.AngryMooshroomDamage.get());
            }
        }
        this.setHealth(this.getMaxHealth());
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (itemstack.isEdible() && itemstack.getFoodProperties(this) != null
                    && this.getHealth() < this.getMaxHealth()) {
                this.heal(2.0F);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setHealth(this.getMaxHealth());
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.ANGRY_MOOSHROOM_LIMIT.get();
    }
}