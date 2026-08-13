package com.k1sak1.goetyawaken.common.entities.hostile.illager;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.ai.ModMeleeAttackGoal;
import com.Polarice3.Goety.common.entities.ai.path.ModClimberNavigation;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.equipment.IceAxeItem;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.world.Difficulty;

public class Mountaineer extends HuntingIllagerEntity implements ICustomAttributes {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Mountaineer.class,
            EntityDataSerializers.BYTE);

    private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (difficulty) -> {
        return difficulty == Difficulty.HARD;
    };

    public Mountaineer(EntityType<? extends HuntingIllagerEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.miscGoal();
        this.goalSelector.addGoal(1, new MountaineerBreakDoorGoal(this));
        this.goalSelector.addGoal(4, new MountaineerMeleeAttackGoal(this));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new MountaineerWanderGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    static class MountaineerWanderGoal extends RandomStrollGoal {
        public MountaineerWanderGoal(AbstractIllager p_25983_, double p_25984_) {
            super(p_25983_, p_25984_, 120, false);
        }
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.MountaineerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.MountaineerServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MountaineerServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.MountaineerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.MountaineerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.MountaineerServantDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    protected PathNavigation createNavigation(Level level) {
        if (MobsConfig.MountaineerClimb.get()) {
            return new ModClimberNavigation(this, level);
        }
        return super.createNavigation(level);
    }

    public boolean canOpenDoors() {
        return true;
    }

    public IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerArmPose.ATTACKING;
        } else {
            return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.NEUTRAL;
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34088_, DifficultyInstance p_34089_,
            MobSpawnType p_34090_, @Nullable SpawnGroupData p_34091_, @Nullable CompoundTag p_34092_) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(p_34088_, p_34089_, p_34090_, p_34091_, p_34092_);
        RandomSource randomsource = p_34088_.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, p_34089_);
        this.populateDefaultEquipmentEnchantments(randomsource, p_34089_);
        return spawngroupdata;
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_219149_, DifficultyInstance p_219150_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.IRON_ICE_AXE.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.MOUNTAINEER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource p_34103_) {
        return ModSounds.MOUNTAINEER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.MOUNTAINEER_DEATH.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.MOUNTAINEER_CELEBRATE.get();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setClimbing(boolean p_33820_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_33820_) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    public boolean isClimbableBlock(BlockPos blockPos) {
        BlockState blockState = this.level().getBlockState(blockPos);
        return (blockState.is(BlockTags.ICE)
                || blockState.is(Tags.Blocks.STONE)
                || blockState.is(Tags.Blocks.COBBLESTONE)
                || blockState.is(BlockTags.DIRT)
                || blockState.is(BlockTags.SNOW))
                && blockState.isSolidRender(this.level(), blockPos);
    }

    public boolean onClimbable() {
        if (MobsConfig.MountaineerClimb.get()) {
            return this.isClimbing();
        }
        return super.onClimbable();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            boolean shouldClimb = this.horizontalCollision
                    && !this.isStuckAtCeiling()
                    && MobsConfig.MountaineerClimb.get();
            this.setClimbing(shouldClimb);
        }
    }

    private boolean isStuckAtCeiling() {
        BlockPos above = this.blockPosition().above(2);
        return this.level().getBlockState(above).isSolidRender(this.level(), above)
                && this.getDeltaMovement().y <= 0.01D;
    }

    public boolean isMainWeapon(ItemStack itemStack) {
        return itemStack.getItem() instanceof IceAxeItem || itemStack.is(ModTags.Items.MOUNTAINEER_WEAPONS);
    }

    static class MountaineerBreakDoorGoal extends BreakDoorGoal {
        public MountaineerBreakDoorGoal(Mob p_34112_) {
            super(p_34112_, 6, Mountaineer.DOOR_BREAKING_PREDICATE);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canContinueToUse() {
            Mountaineer vindicator = (Mountaineer) this.mob;
            return vindicator.hasActiveRaid() && super.canContinueToUse();
        }

        public boolean canUse() {
            Mountaineer vindicator = (Mountaineer) this.mob;
            return vindicator.hasActiveRaid() && vindicator.random.nextInt(reducedTickDelay(10)) == 0 && super.canUse();
        }

        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }

    static class MountaineerMeleeAttackGoal extends ModMeleeAttackGoal {
        public MountaineerMeleeAttackGoal(Mountaineer p_34123_) {
            super(p_34123_, 1.0D, false);
        }
    }

    @Override
    public boolean canJoinRaid() {
        return true;
    }

    @Override
    public void applyRaidBuffs(int arg0, boolean arg1) {

    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EMPTY;
    }
}
