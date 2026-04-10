package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import com.k1sak1.goetyawaken.common.compat.touhoulittlemaid.IHasPowerPointProxy;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class MaidFairyServant extends Summoned implements RangedAttackMob, FlyingAnimal, IHasPowerPointProxy {
    public static final String RICK = "rick";

    private static final String FAIRY_TYPE_TAG_NAME = "FairyType";
    private static final EntityDataAccessor<Integer> FAIRY_TYPE = SynchedEntityData.defineId(MaidFairyServant.class,
            EntityDataSerializers.INT);

    public MaidFairyServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new FlyingMoveControl(this, 15, true);
    }

    public static AttributeSupplier.Builder createFairyAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 1.5)
                .add(Attributes.ARMOR, 1.)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0, 20, 15.0f));
        goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FAIRY_TYPE, 0);
    }

    @Override
    public int getPowerPoint() {
        try {
            Class<?> miscConfigClass = Class
                    .forName("com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig");
            java.lang.reflect.Field maidFairyPowerPointField = miscConfigClass.getField("MAID_FAIRY_POWER_POINT");
            Object configEntry = maidFairyPowerPointField.get(null);
            java.lang.reflect.Method getMethod = configEntry.getClass().getMethod("get");
            Object value = getMethod.invoke(configEntry);
            double configValue = Double.parseDouble(value.toString());
            return (int) (configValue * 100);
        } catch (Exception e) {
            return 50;
        }
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        try {
            Class<?> entityFairyClass = Class
                    .forName("com.github.tartaricacid.touhoulittlemaid.entity.monster.EntityFairy");
            java.lang.reflect.Method dropPowerPointMethod = entityFairyClass.getMethod("dropPowerPoint",
                    net.minecraft.world.entity.LivingEntity.class);
            dropPowerPointMethod.invoke(null, this);
        } catch (Exception e) {
            dropPowerPoint(this);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        try {
            Class<?> danmakuShootClass = Class
                    .forName("com.github.tartaricacid.touhoulittlemaid.entity.projectile.DanmakuShoot");
            java.lang.reflect.Method createMethod = danmakuShootClass.getMethod("create");

            Object danmakuInstance = createMethod.invoke(null);
            java.lang.reflect.Method setWorldMethod = danmakuShootClass.getMethod("setWorld", Level.class);
            java.lang.reflect.Method setThrowerMethod = danmakuShootClass.getMethod("setThrower", LivingEntity.class);
            java.lang.reflect.Method setTargetMethod = danmakuShootClass.getMethod("setTarget", LivingEntity.class);
            java.lang.reflect.Method setRandomColorMethod = danmakuShootClass.getMethod("setRandomColor");
            java.lang.reflect.Method setRandomTypeMethod = danmakuShootClass.getMethod("setRandomType");
            java.lang.reflect.Method setDamageMethod = danmakuShootClass.getMethod("setDamage", float.class);
            java.lang.reflect.Method setGravityMethod = danmakuShootClass.getMethod("setGravity", float.class);
            java.lang.reflect.Method setVelocityMethod = danmakuShootClass.getMethod("setVelocity", float.class);
            java.lang.reflect.Method setInaccuracyMethod = danmakuShootClass.getMethod("setInaccuracy", float.class);
            java.lang.reflect.Method setFanNumMethod = danmakuShootClass.getMethod("setFanNum", int.class);
            java.lang.reflect.Method setYawTotalMethod = danmakuShootClass.getMethod("setYawTotal", double.class);
            java.lang.reflect.Method aimedShotMethod = danmakuShootClass.getMethod("aimedShot");
            java.lang.reflect.Method fanShapedShotMethod = danmakuShootClass.getMethod("fanShapedShot");
            danmakuInstance = setWorldMethod.invoke(danmakuInstance, level());
            danmakuInstance = setThrowerMethod.invoke(danmakuInstance, this);
            danmakuInstance = setTargetMethod.invoke(danmakuInstance, target);
            danmakuInstance = setRandomColorMethod.invoke(danmakuInstance);
            danmakuInstance = setRandomTypeMethod.invoke(danmakuInstance);

            float damageBase = 1.0f;
            if (target.level().getDifficulty() == Difficulty.NORMAL) {
                damageBase = 1.5f;
            } else if (target.level().getDifficulty() == Difficulty.HARD) {
                damageBase = 2.0f;
            }

            danmakuInstance = setDamageMethod.invoke(danmakuInstance, distanceFactor + damageBase);
            danmakuInstance = setGravityMethod.invoke(danmakuInstance, 0f);
            danmakuInstance = setVelocityMethod.invoke(danmakuInstance, 0.2f * (distanceFactor + 1));
            danmakuInstance = setInaccuracyMethod.invoke(danmakuInstance, 0.2f);
            if (this.random.nextFloat() <= 0.9f) {
                aimedShotMethod.invoke(danmakuInstance);
            } else {
                danmakuInstance = setDamageMethod.invoke(danmakuInstance, distanceFactor + damageBase + 0.5f);
                danmakuInstance = setFanNumMethod.invoke(danmakuInstance, 3);
                danmakuInstance = setYawTotalMethod.invoke(danmakuInstance, Math.PI / 6);
                fanShapedShotMethod.invoke(danmakuInstance);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation navigator = new FlyingPathNavigation(this, worldIn);
        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(true);
        navigator.setCanPassDoors(true);
        return navigator;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
            MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        try {
            Class<?> fairyTypeClass = Class
                    .forName("com.github.tartaricacid.touhoulittlemaid.entity.monster.FairyType");
            java.lang.reflect.Method valuesMethod = fairyTypeClass.getMethod("values");
            Object[] fairyTypeValues = (Object[]) valuesMethod.invoke(null);
            java.lang.reflect.Method ordinalMethod = fairyTypeClass.getMethod("ordinal");

            int randomIndex = random.nextInt(fairyTypeValues.length);
            int ordinal = (Integer) ordinalMethod.invoke(fairyTypeValues[randomIndex]);
            this.setFairyTypeOrdinal(ordinal);
        } catch (Exception e) {
            this.setFairyTypeOrdinal(0);
        }

        if (random.nextInt(20) == 0) {
            this.setCustomName(Component.literal(RICK));
            this.setCustomNameVisible(true);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(FAIRY_TYPE_TAG_NAME, getFairyTypeOrdinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(FAIRY_TYPE_TAG_NAME, Tag.TAG_INT)) {
            setFairyTypeOrdinal(compound.getInt(FAIRY_TYPE_TAG_NAME));
        }
    }

    public int getFairyTypeOrdinal() {
        return this.entityData.get(FAIRY_TYPE);
    }

    public void setFairyTypeOrdinal(int ordinal) {
        this.entityData.set(FAIRY_TYPE, ordinal);
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        try {
            Class<?> initSoundsClass = Class.forName("com.github.tartaricacid.touhoulittlemaid.init.InitSounds");
            java.lang.reflect.Field ambientField = initSoundsClass.getField("FAIRY_AMBIENT");
            Object soundSupplier = ambientField.get(null);
            java.lang.reflect.Method getMethod = soundSupplier.getClass().getMethod("get");
            return (SoundEvent) getMethod.invoke(soundSupplier);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        try {
            Class<?> initSoundsClass = Class.forName("com.github.tartaricacid.touhoulittlemaid.init.InitSounds");
            java.lang.reflect.Field hurtField = initSoundsClass.getField("FAIRY_HURT");
            Object soundSupplier = hurtField.get(null);
            java.lang.reflect.Method getMethod = soundSupplier.getClass().getMethod("get");
            return (SoundEvent) getMethod.invoke(soundSupplier);
        } catch (Exception e) {
            return super.getHurtSound(pDamageSource);
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        try {
            Class<?> initSoundsClass = Class.forName("com.github.tartaricacid.touhoulittlemaid.init.InitSounds");
            java.lang.reflect.Field deathField = initSoundsClass.getField("FAIRY_DEATH");
            Object soundSupplier = deathField.get(null);
            java.lang.reflect.Method getMethod = soundSupplier.getClass().getMethod("get");
            return (SoundEvent) getMethod.invoke(soundSupplier);
        } catch (Exception e) {
            return super.getDeathSound();
        }
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    @Override
    public boolean canWander() {
        return true;
    }

    @Override
    public boolean canStay() {
        return true;
    }

    @Override
    public boolean canGuardArea() {
        return true;
    }

    @Override
    public boolean canFollow() {
        return true;
    }

    @Override
    public boolean canBeCommanded() {
        return true;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.MAID_FAIRY_SERVANT_LIMIT.get();
    }
}