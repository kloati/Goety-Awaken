package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.CarrionMaggot;
import com.Polarice3.Goety.common.entities.util.DelayedSummon;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.NoKnockBackDamageSource;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.Pose;
import net.minecraft.util.Mth;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.nbt.CompoundTag;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.fluids.FluidType;
import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.LeavesBlock;
import java.util.EnumSet;
import net.minecraft.world.level.LevelReader;

public class WightServant extends Summoned {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(WightServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HIDE = SynchedEntityData.defineId(WightServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_CLONE = SynchedEntityData.defineId(WightServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(WightServant.class,
            EntityDataSerializers.BYTE);

    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String SMASH = "smash";
    public static String UNLEASH = "unleash";
    public static String SUMMON = "summon";
    public static String SUPER_SMASH = "super_smash";
    public static String WALK = "walk";

    public int attackTick;
    public int teleportCool = 0;
    public int hidingTime = 0;
    public int hidingCooldown = 0;
    public int spawnCool = 100;
    public int screamTick;
    public int deathTime = 0;
    public DamageSource deathBlow = this.damageSources().generic();
    private boolean isMeleeAttacking = false;
    private int remainingPersistentAngerTime;

    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState smashAnimationState = new AnimationState();
    public AnimationState unleashAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState superSmashAnimationState = new AnimationState();

    public WightServant(EntityType<? extends WightServant> type, Level world) {
        super(type, world);
        this.waterNavigation = new WightServantAquaticNavigation(this, world);
        this.groundNavigation = new WightServantNavigation(this, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0);
        this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, 0);
        this.setPathfindingMalus(BlockPathTypes.DOOR_IRON_CLOSED, 0);
        this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(DATA_HIDE, false);
        this.entityData.define(IS_CLONE, false);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new WightServantAttackGoal(this, 1.5D));
        this.goalSelector.addGoal(2, this.createFollowGoal());
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2,
                new NearestAttackableTargetGoal<>(this, com.Polarice3.Goety.common.entities.hostile.Wight.class, true));
    }

    public Goal createFollowGoal() {
        return new WightServant.WightServantFollowOwnerGoal<>(this, 1.0D, 10.0F, 2.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WightHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WightDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0F)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.WIGHT_AMBIENT.get();
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    public boolean isPushedByFluid(FluidType type) {
        return !this.isSwimming();
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 3.0F;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isHiding()) {
            super.playAmbientSound();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.WIGHT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WIGHT_DEATH.get();
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.wightServantLimit;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(ModSounds.WIGHT_AMBIENT.get(), 0.15F, 1.0F);
    }

    @Override
    public EntityDimensions getDimensions(Pose p_21047_) {
        if (this.isHiding()) {
            return EntityDimensions.scalable(0.1F, 0.1F);
        } else if (p_21047_ == Pose.CROUCHING) {
            return super.getDimensions(p_21047_).scale(1.0F, 0.25F);
        } else {
            return super.getDimensions(p_21047_);
        }
    }

    @Override
    public boolean isInvisible() {
        return super.isInvisible() || this.isHiding() || this.isHallucination();
    }

    @Override
    public boolean isInvisibleTo(Player p_20178_) {
        if (this.isHallucination()) {
            return false;
        } else if (this.isHiding()) {
            return true;
        } else {
            return super.isInvisibleTo(p_20178_);
        }
    }

    @Override
    public boolean isAttackable() {
        return super.isAttackable() && !this.isHiding();
    }

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable() || this.isHiding();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource p_20122_) {
        return super.isInvulnerableTo(p_20122_) || this.isHiding();
    }

    public boolean canBeSeenByAnyone() {
        return super.canBeSeenByAnyone() && !this.isHiding();
    }

    private boolean getWightFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setWightFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    public boolean isMeleeAttacking() {
        return this.isMeleeAttacking;
    }

    public void setMeleeAttacking(boolean attacking) {
        this.isMeleeAttacking = attacking;
        this.attackTick = 0;
    }

    public boolean isScreaming() {
        return this.getWightFlag(2) || this.getWightFlag(8);
    }

    public void setScreaming(boolean scream) {
        this.setWightFlags(2, scream);
        this.screamTick = 0;
        if (!this.level().isClientSide) {
            if (scream) {
                this.setAnimationState(UNLEASH);
                this.applyDarkness();
            }
        }
    }

    public boolean isClimbing() {
        return this.getWightFlag(4);
    }

    public void setClimbing(boolean climbing) {
        this.setWightFlags(4, climbing);
    }

    public boolean isSummoning() {
        return this.getWightFlag(8);
    }

    public void setSummoning(boolean scream) {
        this.setWightFlags(8, scream);
        this.screamTick = 0;
        if (!this.level().isClientSide) {
            if (scream) {
                this.setAnimationState(SUMMON);
            }
        }
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDE);
    }

    public void setHide(boolean hide) {
        this.entityData.set(DATA_HIDE, hide);
    }

    public boolean isHallucination() {
        return this.entityData.get(IS_CLONE);
    }

    public void setHallucination(boolean shadowClone) {
        this.entityData.set(IS_CLONE, shadowClone);
    }

    public void spawnHallucination() {
        this.setHallucination(true);
        this.level().broadcastEntityEvent(this, (byte) 6);
    }

    public void applyDarkness() {
        if (this.level() instanceof ServerLevel serverLevel) {
            MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.DARKNESS,
                    MathHelper.secondsToTicks(6), 0, false, false);
            LivingEntity target = this.getTarget();
            if (target != null) {
                if (target.distanceTo(this) <= 20.0D) {
                    target.addEffect(mobeffectinstance);
                }
            }
        }
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.WIGHT_TELEPORT_SCREAM.get(),
                    this.getSoundSource(), this.getSoundVolume() + 1.0F, this.getVoicePitch());
            this.playSound(ModSounds.WIGHT_TELEPORT_SCREAM.get(), this.getSoundVolume() + 1.0F, this.getVoicePitch());
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag;
        if (this.isHallucination()) {
            flag = this.doHurtTarget(0.5F, entityIn);
            if (flag) {
                if (entityIn instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
                }
                this.playSound(ModSounds.WIGHT_SCREAM.get(), 1.0F, this.getVoicePitch());
                this.hallucinateVanish();
            }
        } else {
            flag = super.doHurtTarget(entityIn);
            if (flag) {
                if (entityIn instanceof LivingEntity living) {
                    this.stealSoulEnergy(living);
                    if (entityIn instanceof Player player) {
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
                    } else {
                        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
                    }
                    living.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), 60, 0, false, false));
                    if (entityIn instanceof Player player) {
                        this.maybeDisableShield(player,
                                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_AXE),
                                player.isUsingItem() ? player.getUseItem() : net.minecraft.world.item.ItemStack.EMPTY);
                    }
                }
            }
        }
        return flag;
    }

    public boolean hurt(DamageSource p_32494_, float p_32495_) {
        if (this.isInvulnerableTo(p_32494_)) {
            return false;
        } else {
            if (p_32494_.is(DamageTypes.IN_WALL) || p_32494_.is(DamageTypes.DROWN)) {
                return false;
            }

            if (!this.isMeleeAttacking() && this.attackTick <= 0) {
                if (this.teleportCool <= 0) {
                    if (!(p_32494_ instanceof NoKnockBackDamageSource) && p_32495_ < this.getHealth()) {
                        if (!this.level().isClientSide() && ((p_32495_ < this.getHealth()
                                && p_32494_.getEntity() instanceof LivingEntity)
                                || (!(p_32494_.getEntity() instanceof LivingEntity)
                                        && !p_32494_.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)
                                        && !p_32494_.is(net.minecraft.tags.DamageTypeTags.WITCH_RESISTANT_TO)))) {
                            if (this.teleport()) {
                                this.teleportCool = com.Polarice3.Goety.utils.MathHelper.secondsToTicks(2);
                            }
                        }
                    }
                }
            }

            if (this.isHallucination() && p_32495_ > 0.0F) {
                this.hallucinateVanish();
            }

            return super.hurt(p_32494_, p_32495_);
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

    public void hallucinateVanish() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < this.level().random.nextInt(10) + 10; ++i) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,
                        this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D),
                        0, 0.0D, 0.0D, 0.0D, 0.5F);
            }
        }
        this.discard();
    }

    @Override
    public void die(DamageSource cause) {
        if (this.isHallucination()) {
            this.hallucinateVanish();
        } else if (this.deathTime > 0) {
            super.die(cause);
        } else if (this.getOwner() != null) {
            this.createTerrorSoul();
            super.die(cause);
        } else {
            super.die(cause);
        }
    }

    protected void createTerrorSoul() {
        if (!this.level().isClientSide() && this.getOwner() != null) {
            FlyingItem flyingItem = new FlyingItem(com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                    this.level(), this.getX(), this.getY() + 0.5D, this.getZ());
            flyingItem.setItem(new net.minecraft.world.item.ItemStack(ModItems.TERROR_SOUL.get()));
            flyingItem.setOwner(this.getOwner());
            flyingItem.setSecondsCool(30);
            this.level().addFreshEntity(flyingItem);
        }
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 2; ++i) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(),
                        this.getRandomZ(0.5D), 0, 0.0D, 0.0D, 0.0D, 0.5F);
            }
        }
        this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
        if (this.deathTime == 1) {
            this.die(this.deathBlow);
        }
        if (this.deathTime >= 60) {
            this.remove(RemovalReason.KILLED);
        }
    }

    private void stealSoulEnergy(LivingEntity target) {
        Entity owner = this.getOwner();
        int soulEnergy = com.Polarice3.Goety.config.AttributesConfig.WightSoulAbsorb.get();
        int soulHeal = com.Polarice3.Goety.config.AttributesConfig.WightSoulHeal.get();
        if (target instanceof Player player) {
            if (com.Polarice3.Goety.utils.SEHelper.getSoulsAmount(player, soulEnergy)) {
                com.Polarice3.Goety.utils.SEHelper.decreaseSouls(player, soulEnergy);

            }
        } else {
            int targetSoulValue = com.Polarice3.Goety.utils.SEHelper.getSoulGiven(target);
            int calculatedSoulEnergy = Math.max(1, targetSoulValue / 10);
            soulEnergy = Math.min(calculatedSoulEnergy,
                    com.Polarice3.Goety.config.AttributesConfig.WightSoulAbsorb.get());
            int calculatedSoulHeal = Math.max(1, targetSoulValue / 10);
            soulHeal = Math.min(calculatedSoulHeal, com.Polarice3.Goety.config.AttributesConfig.WightSoulHeal.get());
        }

        this.heal(soulHeal);
        if (owner instanceof Player playerOwner) {
            com.Polarice3.Goety.utils.SEHelper.increaseSouls(playerOwner, soulEnergy);
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; ++i) {
                serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                        this.getRandomX(0.5D),
                        this.getRandomY(),
                        this.getRandomZ(0.5D),
                        0, 0.0D, 0.0D, 0.0D, 0.5F);
            }
        }
    }

    public void maybeDisableShield(Player player, net.minecraft.world.item.ItemStack axe,
            net.minecraft.world.item.ItemStack shield) {
        if (!shield.isEmpty() && shield.getItem() instanceof net.minecraft.world.item.ShieldItem
                && axe.getItem() instanceof net.minecraft.world.item.AxeItem) {
            float f = 0.25F
                    + (float) net.minecraft.world.item.enchantment.EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                player.getCooldowns().addCooldown(shield.getItem(), 100);
                this.level().broadcastEntityEvent(player, (byte) 30);
            }
        }
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, "idle")) {
            return 1;
        } else if (Objects.equals(animation, "attack")) {
            return 2;
        } else if (Objects.equals(animation, "smash")) {
            return 3;
        } else if (Objects.equals(animation, "unleash")) {
            return 4;
        } else if (Objects.equals(animation, "summon")) {
            return 5;
        } else if (Objects.equals(animation, "super_smash")) {
            return 6;
        } else {
            return 0;
        }
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.smashAnimationState);
        animationStates.add(this.unleashAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.superSmashAnimationState);
        return animationStates;
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level().isClientSide) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0:
                        break;
                    case 1:
                        this.idleAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 2:
                        this.attackAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case 3:
                        this.smashAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.smashAnimationState);
                        break;
                    case 4:
                        this.unleashAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.unleashAnimationState);
                        break;
                    case 5:
                        this.summonAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case 6:
                        this.superSmashAnimationState.start(this.tickCount);
                        this.stopMostAnimation(this.superSmashAnimationState);
                        break;
                }
            }
        }

        if (DATA_HIDE.equals(p_219422_)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    public void upgradePower(int sePercent) {
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (health != null && attack != null && speed != null) {
            if (sePercent >= 15) {
                double h = 1.096D;
                double a = 1.12D;
                double d0 = 1.0D;
                double d1 = 0.0D;
                for (int i = 0; i < sePercent; ++i) {
                    if (i > 15) {
                        if (i % 15 == 0) {
                            d0 += 0.1D;
                        }
                        if (i % 30 == 0) {
                            d1 += 0.05D;
                        }
                    }
                }
                health.setBaseValue(AttributesConfig.WightHealth.get() * (h * d0));
                attack.setBaseValue(AttributesConfig.WightDamage.get() * (a * d0));
                speed.setBaseValue(Math.min(0.45D, 0.3D + d1));
                this.setHealth(this.getMaxHealth());
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AttackTick", this.attackTick);
        compound.putInt("TeleportCool", this.teleportCool);
        compound.putInt("HidingTime", this.hidingTime);
        compound.putInt("HidingCooldown", this.hidingCooldown);
        compound.putInt("SpawnCool", this.spawnCool);
        compound.putInt("ScreamTick", this.screamTick);
        compound.putBoolean("Hiding", this.isHiding());
        compound.putBoolean("Clone", this.isHallucination());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("AttackTick")) {
            this.attackTick = compound.getInt("AttackTick");
        }
        if (compound.contains("TeleportCool")) {
            this.teleportCool = compound.getInt("TeleportCool");
        }
        if (compound.contains("HidingTime")) {
            this.hidingTime = compound.getInt("HidingTime");
        }
        if (compound.contains("HidingCooldown")) {
            this.hidingCooldown = compound.getInt("HidingCooldown");
        }
        if (compound.contains("SpawnCool")) {
            this.spawnCool = compound.getInt("SpawnCool");
        }
        if (compound.contains("ScreamTick")) {
            this.screamTick = compound.getInt("ScreamTick");
        }
        if (compound.contains("Hiding")) {
            this.setHide(compound.getBoolean("Hiding"));
        }
        if (compound.contains("Clone")) {
            this.setHallucination(compound.getBoolean("Clone"));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.tickCount == 1 && this.getTrueOwner() instanceof Player player) {
                int seAmount = SEHelper.getSoulAmountInt(player);
                int sePercent = (int) ((seAmount / (double) MainConfig.MaxArcaSouls.get()) * 100);
                this.upgradePower(sePercent);
            }

            if (!this.isDeadOrDying()) {
                if (!this.isMeleeAttacking() && !this.isSummoning() && !this.isScreaming()) {
                    this.setAnimationState(IDLE);
                }
                if (this.isMeleeAttacking()) {
                    ++this.attackTick;
                }

                this.climb((ServerLevel) this.level());

                this.updateSwimming();

                BlockPos standPos0 = this.blockPosition().offset(0, 2, 0);
                BlockPos standPos1 = this.blockPosition().offset(0, 1, 0);

                boolean frontStand = this.level().getBlockState(standPos0.relative(this.getDirection()))
                        .getCollisionShape(this.level(), standPos0.relative(this.getDirection())).isEmpty()
                        && this.level().getBlockState(standPos1.relative(this.getDirection()))
                                .getCollisionShape(this.level(), standPos1.relative(this.getDirection())).isEmpty();

                boolean shouldStand = this.level().getBlockState(standPos0).getCollisionShape(this.level(), standPos0)
                        .isEmpty()
                        && this.level().getBlockState(standPos1).getCollisionShape(this.level(), standPos1).isEmpty();

                if ((frontStand && shouldStand && !this.isInWall()) || this.isClimbing()) {
                    this.setPose(Pose.STANDING);
                } else {
                    this.setPose(Pose.CROUCHING);
                }

                if (this.isHiding()) {
                    ++this.hidingTime;
                    if (this.hidingTime >= MathHelper.secondsToTicks(5)) {
                        this.setHide(false);
                        this.level().broadcastEntityEvent(this, (byte) 5);
                        this.hidingCooldown = MathHelper.secondsToTicks(this.level().random.nextInt(5) + 5);
                        if (this.getTarget() != null) {
                            this.teleportNearTo(this.getTarget());
                            this.teleportHit();
                        } else {
                            this.teleport();
                            this.teleportHit();
                        }
                    }
                } else {
                    this.hidingTime = 0;
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 2; ++i) {
                            serverLevel.sendParticles(ParticleTypes.SMOKE, this.getRandomX(0.5D), this.getRandomY(),
                                    this.getRandomZ(0.5D), 0, 0.0D, 0.0D, 0.0D, 0.5F);
                        }
                    }
                }

                if (this.hidingCooldown > 0) {
                    --this.hidingCooldown;
                }

                if (this.spawnCool > 0 && !this.isHiding()) {
                    --this.spawnCool;
                }

                if (this.teleportCool > 0) {
                    --this.teleportCool;
                }

                if (this.isScreaming()) {
                    if (this.getTarget() != null) {
                        this.getLookControl().setLookAt(this.getTarget(), 10.0F, 10.0F);
                    }
                    this.getNavigation().stop();
                    ++this.screamTick;
                    int total = this.isSummoning() ? 30 : 4;
                    if (this.screamTick >= total) {
                        for (int i = 0; i < 64; ++i) {
                            if (this.teleport()) {
                                this.setScreaming(false);
                                this.setSummoning(false);
                                this.level().broadcastEntityEvent(this, (byte) 9);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (this.isHiding() || this.isScreaming()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
            this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
        }
    }

    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    public void climb(ServerLevel serverLevel) {
        if (this.getTarget() == null) {
            this.setClimbing(false);
            return;
        }

        boolean blocksAbove = haveBlocksAround(serverLevel, this.blockPosition().above(2), 1, 0, 1);
        this.setClimbing(blocksAbove && horizontalDistance(this.position(), this.getTarget().position()) <= 5.0D
                && this.getTarget().getBlockY() > this.getBlockY());

        if (this.isClimbing()) {
            this.setDeltaMovement(0.0D, 0.25D, 0.0D);
            BlockPos blockPos = this.getClimbPos(this.blockPosition());
            if (blockPos != null) {
                this.getLookControl().setLookAt(blockPos.getCenter());
                int height = this.getClimbHeight(blockPos);
                BlockPos blockPos1 = this.getClimbablePos(blockPos, height);
                if (blockPos1 != null) {
                    Vec3 vec3 = blockPos1.above().getCenter();
                    this.setPos(vec3.x(), this.getY(), vec3.z());
                }
            }
        }
    }

    public int getClimbHeight(BlockPos pPos) {
        int y = 0;
        BlockPos blockPos = pPos;

        while (this.level().getBlockState(blockPos).isSolidRender(this.level(), blockPos)) {
            blockPos = blockPos.above();
            ++y;
            if (y > this.level().getMaxBuildHeight()) {
                break;
            }
        }

        if (y > 0) {
            y += 1;
        }

        return y;
    }

    public boolean isClimbable(BlockPos pPos, int pHeight) {
        for (int i = 1; i <= pHeight; i++) {
            BlockPos above = pPos.above(i);
            if (this.level().getBlockState(above).isSolidRender(this.level(), above)) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    public BlockPos getClimbPos(BlockPos pPos) {
        for (int i = -1; i <= 1; ++i) {
            for (int k = -1; k <= 1; ++k) {
                BlockPos blockPos = pPos.offset(i, 0, k);
                if (this.level().getBlockState(blockPos).isSolidRender(this.level(), blockPos)) {
                    return blockPos;
                }
            }
        }
        return null;
    }

    @Nullable
    public BlockPos getClimbablePos(BlockPos pPos, int pHeight) {
        for (int i = -1; i <= 1; ++i) {
            for (int k = -1; k <= 1; ++k) {
                BlockPos blockPos = pPos.offset(i, 0, k);
                if (this.isClimbable(blockPos, pHeight)) {
                    return blockPos;
                }
            }
        }
        return null;
    }

    public static double horizontalDistance(Vec3 firstPos, Vec3 secondPos) {
        Vec2 vec2 = new Vec2((float) firstPos.x, (float) firstPos.z);
        Vec2 vec21 = new Vec2((float) secondPos.x, (float) secondPos.z);
        return Math.sqrt(vec21.distanceToSqr(vec2));
    }

    public static boolean haveBlocksAround(Level level, BlockPos pPos, int rangeX, int rangeY, int rangeZ) {
        for (int i = -rangeX; i <= rangeX; ++i) {
            for (int j = -rangeY; j <= rangeY; ++j) {
                for (int k = -rangeZ; k <= rangeZ; ++k) {
                    BlockPos blockPos = pPos.offset(i, j, k);
                    BlockState blockState = level.getBlockState(blockPos);
                    if (!blockState.getCollisionShape(level, blockPos).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WightServant.WightServantNavigation(this, level);
    }

    protected boolean teleport() {
        if (!this.level().isClientSide() && !this.isHallucination() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 32.0D;
            double d1 = this.getY() + (double) (this.random.nextInt(32) - 16);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 32.0D;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    public boolean teleportNearTo(Entity p_32501_) {
        if (!this.level().isClientSide() && !this.isHallucination() && this.isAlive()) {
            Vec3 vec3 = new Vec3(this.getX() - p_32501_.getX(), this.getY(0.5D) - p_32501_.getEyeY(),
                    this.getZ() - p_32501_.getZ());
            vec3 = vec3.normalize();
            double d0 = 16.0D;
            double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.x * d0;
            double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vec3.y * d0;
            double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3.z * d0;
            return this.teleport(d1, d2, d3);
        } else {
            return false;
        }
    }

    private boolean teleport(double pX, double pY, double pZ) {
        net.minecraft.core.BlockPos.MutableBlockPos blockpos$mutableblockpos = new net.minecraft.core.BlockPos.MutableBlockPos(
                pX, pY, pZ);

        while (blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight()
                && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(net.minecraft.core.Direction.DOWN);
        }

        net.minecraft.world.level.block.state.BlockState blockstate = this.level()
                .getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(net.minecraft.tags.FluidTags.WATER) && !this.isSwimming();
        if (flag && !flag1) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory
                    .onEnderTeleport(this, pX, pY, pZ);
            if (event.isCanceled())
                return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
            if (this.isSwimming() || (this.getTarget() != null && this.getTarget().isInWater())) {
                flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
            }
            if (flag2) {
                if (this.getTarget() != null && !this.isScreaming() && this.getTarget().distanceTo(this) >= 4.0D
                        && this.spawnCool <= 0 && this.level().random.nextFloat() <= 0.25F) {
                    this.setSummoning(true);
                    this.level().broadcastEntityEvent(this, (byte) 8);
                    this.playSound(ModSounds.WIGHT_SUMMON.get(), 3.0F, 1.0F);
                    int amount = 3 + this.level().random.nextInt(1 + this.level().getDifficulty().getId());
                    for (int j = 0; j < amount; ++j) {
                        CarrionMaggot carrionMaggot = new CarrionMaggot(
                                com.Polarice3.Goety.common.entities.ModEntityType.CARRION_MAGGOT.get(), this.level());
                        BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), carrionMaggot, this.level());
                        carrionMaggot.setTrueOwner(this);
                        carrionMaggot.setLimitedLife(MobUtil.getSummonLifespan(this.level()));
                        carrionMaggot.moveTo(blockPos, this.getYRot(), this.getXRot());
                        if (this.level() instanceof ServerLevel serverLevel) {
                            carrionMaggot.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(blockPos),
                                    MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                        }
                        DelayedSummon delayedSummon = new DelayedSummon(this.level(), blockPos, carrionMaggot, true,
                                true, this);
                        delayedSummon.setLifeSpan(MathHelper.secondsToTicks(this.random.nextInt(1 + j)));
                        this.level().addFreshEntity(delayedSummon);
                    }

                    this.spawnCool = MathHelper.secondsToTicks(10);
                } else if ((this.level().random.nextFloat() <= 0.25F && this.hidingCooldown <= 0)
                        || this.isScreaming()) {
                    this.setHide(true);
                    this.level().broadcastEntityEvent(this, (byte) 4);
                    this.applyDarkness();
                } else if (this.level().random.nextFloat() <= 0.25F) {
                    this.applyDarkness();
                    if (this.level().random.nextFloat() <= 0.15F && this.getTarget() != null) {
                        int amount = 4 + this.level().random.nextInt(1 + this.level().getDifficulty().getId());
                        for (int j = 0; j < amount; ++j) {
                            WightServant falseWight = new WightServant(ModEntityType.WIGHT_SERVANT.get(), this.level());
                            BlockPos spawnPos = BlockFinder.SummonRadius(this.blockPosition(), falseWight,
                                    this.level());
                            falseWight.setTrueOwner(this.getTrueOwner());
                            falseWight.setLimitedLife(MathHelper.secondsToTicks(5 + this.level().random.nextInt(10)));
                            falseWight.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                            if (this.getTarget() != null) {
                                double d2 = this.getTarget().getX() - falseWight.getX();
                                double d1 = this.getTarget().getZ() - falseWight.getZ();
                                falseWight.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                                float f = (float) Mth.atan2(d1, d2);
                                float f2 = f + (float) j * (float) Math.PI * 0.25F + 4.0F;
                                Vec3 targetPos = new Vec3(
                                        this.getTarget().getX() + (double) Mth.cos(f2) * 4.0D,
                                        this.getTarget().getY(),
                                        this.getTarget().getZ() + (double) Mth.sin(f2) * 4.0D);
                                falseWight.setPos(targetPos.x, targetPos.y, targetPos.z);
                            }
                            falseWight.setHealth(this.getHealth());
                            falseWight.spawnHallucination();
                            if (this.level() instanceof ServerLevel serverLevel) {
                                falseWight.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(spawnPos),
                                        MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                            }
                            this.level().addFreshEntity(falseWight);
                        }
                    }
                }
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 16; ++i) {
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                                this.xo + (double) this.getBbWidth() * (2.0D * this.random.nextDouble() - 1.0D) * 0.5D,
                                this.yo + (double) this.getBbHeight() * this.random.nextDouble(),
                                this.zo + (double) this.getBbWidth() * (2.0D * this.random.nextDouble() - 1.0D) * 0.5D,
                                0, 0.0D, 0.0D, 0.0D, 0.5F);
                    }
                    serverLevel.sendParticles(
                            new com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption(), this.xo,
                            this.yo + 0.5F, this.zo, 0, 0, 0, 0, 0.5F);
                    if (!this.isHiding()) {
                        this.teleportHit();
                    }
                }
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    this.level().playSound(null, this.xo, this.yo, this.zo, ModSounds.WIGHT_TELEPORT.get(),
                            this.getSoundSource(), 1.0F, 0.5F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    public void teleportHit() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 16; ++i) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(),
                        this.getRandomZ(0.5D), 0, 0.0D, 0.0D, 0.0D, 0.5F);
            }
            serverLevel.sendParticles(new com.Polarice3.Goety.client.particles.TeleportShockwaveParticleOption(10),
                    this.getX(), this.getY() + 0.5F, this.getZ(), 0, 0, 0, 0, 0.5F);
        }
        if (!this.isSilent()) {
            this.playSound(ModSounds.WIGHT_TELEPORT.get(), 1.0F, 0.5F);
        }
    }

    public double getAttackReachSqr(LivingEntity enemy) {
        return (double) (this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + enemy.getBbWidth());
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr) {
        double reach = this.getAttackReachSqr(enemy);
        return distToEnemySqr <= reach || this.getBoundingBox().inflate(1.0D).intersects(enemy.getBoundingBox());
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
    public boolean canCollideWith(Entity p_20303_) {
        if (!this.isStaying()) {
            return super.canCollideWith(p_20303_);
        } else {
            return false;
        }
    }

    @Override
    public net.minecraft.world.entity.MobType getMobType() {
        return net.minecraft.world.entity.MobType.UNDEAD;
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.setHide(true);
        } else if (p_21375_ == 5) {
            this.setHide(false);
        } else if (p_21375_ == 6) {
            this.setHallucination(true);
        } else if (p_21375_ == 7) {
            this.setHallucination(false);
        } else if (p_21375_ == 8) {
            this.setSummoning(true);
        } else if (p_21375_ == 9) {
            this.setSummoning(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return !this.isHallucination() ? super.getDefaultLootTable() : null;
    }

    static class WightServantAttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public WightServantAttackGoal(WightServant wightServant, double moveSpeed) {
            super(wightServant, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null
                    && this.mob.getTarget().isAlive()
                    && !((WightServant) this.mob).isScreaming()
                    && !((WightServant) this.mob).isHiding();
        }

        @Override
        public void start() {
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            ((WightServant) this.mob).setMeleeAttacking(false);
            ((WightServant) this.mob).attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity == null || ((WightServant) this.mob).isHiding()
                    || ((WightServant) this.mob).isScreaming()) {
                return;
            }

            this.mob.getLookControl().setLookAt(livingentity, ((WightServant) this.mob).getMaxHeadYRot(),
                    ((WightServant) this.mob).getMaxHeadXRot());

            if (((WightServant) this.mob).getCurrentAnimation() != ((WightServant) this.mob)
                    .getAnimationState(SUPER_SMASH)) {
                if (--this.delayCounter <= 0) {
                    this.delayCounter = 10;
                    this.mob.getNavigation().moveTo(livingentity, this.moveSpeed);
                }
            } else {
                this.mob.getMoveControl().strafe(0.0F, 0.0F);
                this.mob.getNavigation().stop();
            }

            this.checkAndPerformAttack(livingentity, this.mob.distanceToSqr(livingentity));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            WightServant wightServant = (WightServant) this.mob;

            if (!wightServant.isHiding()) {
                boolean smash = wightServant.random.nextBoolean();
                float chance = com.Polarice3.Goety.utils.MobUtil.healthIsHalved(wightServant) ? 0.25F : 0.0F;
                if (!wightServant.isMeleeAttacking() && wightServant.targetClose(enemy, distToEnemySqr)) {
                    wightServant.setMeleeAttacking(true);
                    float pitch = 0.0F;

                    if (wightServant.level().getRandom().nextFloat() <= chance && !wightServant.isHallucination()) {
                        wightServant.setAnimationState(SUPER_SMASH);
                        pitch = 0.5F;
                    } else if (smash) {
                        wightServant.setAnimationState(SMASH);
                    } else {
                        wightServant.setAnimationState(ATTACK);
                    }
                    wightServant.playSound(ModSounds.WIGHT_PRE_SWING.get(), wightServant.getSoundVolume(),
                            wightServant.getVoicePitch() - pitch);
                }
                if (wightServant.isMeleeAttacking()) {
                    float seconds = wightServant.getCurrentAnimation() == wightServant.getAnimationState(SUPER_SMASH)
                            ? 2.05F
                            : 1.7F;
                    if (wightServant.attackTick < com.Polarice3.Goety.utils.MathHelper.secondsToTicks(seconds)) {
                        wightServant.setYBodyRot(wightServant.getYHeadRot());
                        if (wightServant.getCurrentAnimation() == wightServant.getAnimationState(SUPER_SMASH)) {
                            if (wightServant.attackTick == 24) {
                                wightServant.playSound(ModSounds.WIGHT_SWING.get(), wightServant.getSoundVolume(),
                                        wightServant.getVoicePitch() - 0.5F);
                                if (wightServant.level() instanceof ServerLevel serverLevel) {
                                    net.minecraft.world.Difficulty difficulty = wightServant.level().getDifficulty();
                                    int h = difficulty == net.minecraft.world.Difficulty.HARD ? 8
                                            : difficulty == net.minecraft.world.Difficulty.NORMAL ? 6 : 4;
                                    for (int i = 0; i <= h; ++i) {
                                        surroundTremor(wightServant, i, 3, 0.0F, false, 0.1F);
                                    }
                                    wightServant.level().playSound(null, wightServant.blockPosition(),
                                            ModSounds.WALL_ERUPT.get(), SoundSource.PLAYERS, 1.0F,
                                            0.8F + wightServant.level().random.nextFloat() * 0.4F);
                                    wightServant.level().playSound(null, wightServant.blockPosition(),
                                            ModSounds.DIRT_DEBRIS.get(), SoundSource.PLAYERS, 1.0F,
                                            0.8F + wightServant.level().random.nextFloat() * 0.4F);
                                    wightServant.level().playSound(null, wightServant.blockPosition(),
                                            SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F,
                                            0.8F + wightServant.level().random.nextFloat() * 0.4F);
                                    if (wightServant.targetClose(enemy, distToEnemySqr)) {
                                        wightServant.doHurtTarget(enemy);
                                        if (enemy instanceof Player player) {
                                            wightServant.maybeDisableShield(player,
                                                    new net.minecraft.world.item.ItemStack(
                                                            net.minecraft.world.item.Items.IRON_AXE),
                                                    player.isUsingItem() ? player.getUseItem()
                                                            : net.minecraft.world.item.ItemStack.EMPTY);
                                        }
                                    }
                                }
                            }
                        } else if (wightServant.getCurrentAnimation() == wightServant.getAnimationState(SMASH)) {
                            if (wightServant.attackTick == 20) {
                                wightServant.playSound(ModSounds.WIGHT_SWING.get(), wightServant.getSoundVolume(),
                                        wightServant.getVoicePitch() - 0.5F);
                                if (wightServant.targetClose(enemy, distToEnemySqr)) {
                                    wightServant.doHurtTarget(enemy);
                                    if (enemy instanceof Player player) {
                                        wightServant.maybeDisableShield(player,
                                                new net.minecraft.world.item.ItemStack(
                                                        net.minecraft.world.item.Items.STONE_AXE),
                                                player.isUsingItem() ? player.getUseItem()
                                                        : net.minecraft.world.item.ItemStack.EMPTY);
                                    }
                                }
                            }
                        } else {
                            if (wightServant.attackTick == 14) {
                                wightServant.playSound(ModSounds.WIGHT_SWING.get(), wightServant.getSoundVolume(),
                                        wightServant.getVoicePitch());
                                this.massiveSweep(wightServant, 3.0D, 100.0D);
                            }
                        }
                    } else {
                        wightServant.setMeleeAttacking(false);
                        wightServant.teleport();
                    }
                } else {
                    wightServant.setMeleeAttacking(false);
                }
            }
        }

        public void massiveSweep(LivingEntity source, double range, double arc) {
            List<LivingEntity> hits = MobUtil.getAttackableLivingEntitiesNearby(source, range, 1.0F, range, range);
            for (LivingEntity target : hits) {
                float targetAngle = (float) (((Math.atan2(target.getZ() - source.getZ(), target.getX() - source.getX())
                        * (180 / Math.PI)) - 90) % 360);
                float attackAngle = source.yBodyRot % 360;
                if (targetAngle < 0) {
                    targetAngle += 360;
                }
                if (attackAngle < 0) {
                    attackAngle += 360;
                }
                float relativeAngle = targetAngle - attackAngle;
                float hitDistance = (float) Math.sqrt((target.getZ() - source.getZ()) * (target.getZ() - source.getZ())
                        + (target.getX() - source.getX()) * (target.getX() - source.getX())) - target.getBbWidth() / 2f;
                if (hitDistance <= range && (relativeAngle <= arc / 2 && relativeAngle >= -arc / 2)
                        || (relativeAngle >= 360 - arc / 2 || relativeAngle <= -360 + arc / 2)) {
                    source.doHurtTarget(target);
                }
            }
        }

        public static void surroundTremor(LivingEntity livingEntity, int distance, double topY, float side,
                boolean grab, float airborne) {
            int hitY = Mth.floor(livingEntity.getBoundingBox().minY - 0.5D);
            double spread = Math.PI * (double) 2.0F;
            int arcLen = Mth.ceil((double) distance * spread);
            double minY = livingEntity.getY() - 1.0D;
            double maxY = livingEntity.getY() + topY;

            for (int i = 0; i < arcLen; ++i) {
                double theta = ((double) i / ((double) arcLen - 1.0D) - 0.5D) * spread;
                double vx = Math.cos(theta);
                double vz = Math.sin(theta);
                double px = livingEntity.getX() + vx * (double) distance
                        + (double) side * Math.cos((double) (livingEntity.yBodyRot + 90.0F) * Math.PI / 180.0D);
                double pz = livingEntity.getZ() + vz * (double) distance
                        + (double) side * Math.sin((double) (livingEntity.yBodyRot + 90.0F) * Math.PI / 180.0D);
                float factor = 1.0F - (float) distance / 12.0F;
                int hitX = Mth.floor(px);
                int hitZ = Mth.floor(pz);
                BlockPos blockPos = new BlockPos(hitX, hitY, hitZ);

                BlockState blockState;
                for (blockState = livingEntity.level().getBlockState(blockPos); blockState
                        .getRenderShape() != net.minecraft.world.level.block.RenderShape.MODEL; blockState = livingEntity
                                .level().getBlockState(blockPos)) {
                    blockPos = blockPos.below();
                }
                BlockState blockAbove = livingEntity.level().getBlockState(blockPos.above());

                if (blockState != net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
                        && !blockState.hasBlockEntity() && !blockAbove.blocksMotion()) {
                    com.Polarice3.Goety.common.entities.util.ModFallingBlock fallingBlock = new com.Polarice3.Goety.common.entities.util.ModFallingBlock(
                            livingEntity.level(), Vec3.atCenterOf(blockPos.above()), blockState,
                            (float) (0.2D + livingEntity.getRandom().nextGaussian() * 0.15D));
                    livingEntity.level().addFreshEntity(fallingBlock);
                }

                net.minecraft.world.phys.AABB selection = new net.minecraft.world.phys.AABB(px - 0.5D, minY, pz - 0.5D,
                        px + 0.5D, maxY, pz + 0.5D);
                List<LivingEntity> entities = livingEntity.level().getEntitiesOfClass(LivingEntity.class, selection);
                for (LivingEntity target : entities) {
                    if (!MobUtil.areAllies(target, livingEntity) && target != livingEntity) {
                        boolean flag = livingEntity.doHurtTarget(target);
                        if (flag) {
                            if (grab) {
                                double magnitude = -4.0D;
                                double x = vx * (double) (1.0F - factor) * magnitude;
                                double y = 0.0D;
                                if (target.onGround()) {
                                    y += 0.15D;
                                }

                                double z = vz * (double) (1.0F - factor) * magnitude;
                                MobUtil.push(target, x, y, z);
                            } else {
                                MobUtil.push(target, 0.0D, (double) (airborne * (float) distance)
                                        + livingEntity.getRandom().nextDouble() * 0.15D, 0.0D);
                            }
                        }
                    }
                }
            }
        }
    }

    static class WightServantAquaticNavigation extends WaterBoundPathNavigation {
        public WightServantAquaticNavigation(Mob entitylivingIn, Level worldIn) {
            super(entitylivingIn, worldIn);
        }

        protected PathFinder createPathFinder(int p_179679_1_) {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(true);
            return new PathFinder(this.nodeEvaluator, p_179679_1_);
        }

        protected boolean canUpdatePath() {
            return true;
        }

        protected Vec3 getTempMobPos() {
            return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
        }

        protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
            Vec3 vector3d = new Vec3(posVec32.x, posVec32.y + (double) this.mob.getBbHeight() * 0.5, posVec32.z);
            return this.level.clip(
                    new ClipContext(posVec31, vector3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob))
                    .getType() == HitResult.Type.MISS;
        }

        public boolean isStableDestination(BlockPos pos) {
            return !this.level.getBlockState(pos.below()).isAir();
        }
    }

    static class WightServantNavigation extends WallClimberNavigation {
        public WightServantNavigation(Mob p_33379_, Level p_33380_) {
            super(p_33379_, p_33380_);
        }

        protected PathFinder createPathFinder(int p_33382_) {
            this.nodeEvaluator = new WightServantNodeEvaluator();
            this.nodeEvaluator.setCanFloat(true);
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, p_33382_);
        }
    }

    static class WightServantNodeEvaluator extends WalkNodeEvaluator {

    }

    public static class WightServantFollowOwnerGoal<T extends Mob & IServant> extends Goal {
        public final T summonedEntity;
        public LivingEntity owner;
        public final LevelReader level;
        public final double followSpeed;
        public final PathNavigation navigation;
        public int timeToRecalcPath;
        public final float stopDistance;
        public final float startDistance;
        public float oldWaterCost;

        public WightServantFollowOwnerGoal(T summonedEntity, double speed, float startDistance, float stopDistance) {
            this.summonedEntity = summonedEntity;
            this.level = summonedEntity.level();
            this.followSpeed = speed;
            this.navigation = summonedEntity.getNavigation();
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.summonedEntity.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.summonedEntity.distanceToSqr(livingentity) < (double) (Mth.square(this.startDistance))) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else {
                return !(this.summonedEntity.distanceToSqr(this.owner) <= (double) (Mth.square(this.stopDistance)));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.summonedEntity.getPathfindingMalus(BlockPathTypes.WATER);
            this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            if (this.owner != null) {
                this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F,
                        (float) this.summonedEntity.getMaxHeadXRot());
                if (this.summonedEntity.getControlledVehicle() != null) {
                    this.navigation.moveTo(this.owner, this.followSpeed + 0.25D);
                    if (this.summonedEntity.getControlledVehicle() instanceof Mob mob) {
                        mob.getNavigation().moveTo(this.owner, this.followSpeed + 0.25D);
                    }
                } else if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = 10;
                    if (!this.summonedEntity.isLeashed() && !this.summonedEntity.isPassenger()) {
                        double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                        boolean flag = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
                        if (this.owner instanceof Mob) {
                            flag |= !this.summonedEntity.hasLineOfSight(this.owner)
                                    && this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(8.0D);
                        } else {
                            flag &= this.canTeleport();
                        }
                        if (flag) {
                            this.tryToTeleportNearEntity();
                        } else {
                            // Use the appropriate navigation based on the current state
                            if (this.summonedEntity.isInWater()) {
                                ((WightServant) this.summonedEntity).waterNavigation.moveTo(this.owner,
                                        this.followSpeed);
                            } else {
                                ((WightServant) this.summonedEntity).groundNavigation.moveTo(this.owner,
                                        this.followSpeed);
                            }
                        }
                    }
                }
            }
        }

        protected boolean canTeleport() {
            return com.Polarice3.Goety.config.MobsConfig.ServantTeleport.get();
        }

        protected void tryToTeleportNearEntity() {
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

        protected boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            } else {
                this.summonedEntity.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D,
                        this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
                this.navigation.stop();
                return true;
            }
        }

        protected boolean isTeleportFriendlyBlock(BlockPos pos) {
            BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
            if (pathnodetype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.level.getBlockState(pos.below());
                if (blockstate.getBlock() instanceof LeavesBlock) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                    return this.level.noCollision(this.summonedEntity,
                            this.summonedEntity.getBoundingBox().move(blockpos));
                }
            }
        }

        protected int getRandomNumber(int min, int max) {
            return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }

}
