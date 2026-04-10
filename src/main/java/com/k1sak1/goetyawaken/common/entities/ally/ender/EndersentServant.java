package com.k1sak1.goetyawaken.common.entities.ally.ender;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.MagicSmokeParticle;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EndersentServant extends com.Polarice3.Goety.common.entities.neutral.ender.AbstractEnderling {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(EndersentServant.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> EYE_TYPE = SynchedEntityData.defineId(EndersentServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(EndersentServant.class,
            EntityDataSerializers.INT);
    private final ModServerBossInfo bossInfo;
    private int killChance = 0;
    private static final UUID TELEPORT_ATTACK_MODIFIER_UUID = UUID.fromString("0134e91f-a8c4-4d38-94d6-7201dae924b7");
    private static final AttributeModifier TELEPORT_ATTACK_MODIFIER = new AttributeModifier(
            TELEPORT_ATTACK_MODIFIER_UUID, "Teleport Attack Bonus", 1.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final UUID DE_ATTACK_MODIFIER_UUID = UUID.fromString("b9788c05-ee1b-4c1d-9701-f681c5d89fd1");
    private static final AttributeModifier DE_ATTACK_MODIFIER = new AttributeModifier(DE_ATTACK_MODIFIER_UUID,
            "Deadly Escape Attack Bonus", 1.85D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final UUID ENHANCED_HEALTH_MODIFIER_UUID = UUID.fromString("a2a74a08-1f15-4c0e-9e6d-3b0c3c7a7a8b");
    private static final UUID ENHANCED_DAMAGE_MODIFIER_UUID = UUID.fromString("b3b85b19-2f26-5d1f-0f7e-4d1d4d8b8b9c");
    private static final UUID ENHANCED_ARMOR_MODIFIER_UUID = UUID.fromString("c4c96c20-3f37-6e2f-1f8f-5e2e5e9c9c0d");
    private static final AttributeModifier ENHANCED_HEALTH_MODIFIER = new AttributeModifier(
            ENHANCED_HEALTH_MODIFIER_UUID, "Enhanced Health Bonus", 0.15D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ENHANCED_DAMAGE_MODIFIER = new AttributeModifier(
            ENHANCED_DAMAGE_MODIFIER_UUID, "Enhanced Damage Bonus", 0.15D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ENHANCED_ARMOR_MODIFIER = new AttributeModifier(
            ENHANCED_ARMOR_MODIFIER_UUID, "Enhanced Armor Bonus", 0.15D, AttributeModifier.Operation.MULTIPLY_BASE);
    public static String IDLE = "idle";
    public static String ATTACK = "attack";
    public static String SWIPE = "swipe";
    public static String DEADLY_ESCAPE = "deadly_escape";
    public static String TELEPORT_IN = "teleport_in";
    public static String TELEPORT_OUT = "teleport_out";
    public static String DEATH = "death";
    public static int SEARING_EYE = 1;
    public static int HALLOWED_EYE = 2;
    public static int TWISTED_EYE = 3;
    public static int DREADFUL_EYE = 4;
    public static int SPIKED_EYE = 5;
    public static int SAVAGE_EYE = 6;
    public static int BLIGHT_EYE = 7;
    public static int BINDING_EYE = 8;
    public static int REAPING_EYE = 9;
    public static int RAVENOUS_EYE = 10;
    public int idleTime = 0;
    public int attackTick;
    public boolean isEnhanced = false;
    public int preHidingTime = 0;
    public int hidingTime = 0;
    public int teleportCool;
    public int postTeleportTick;
    public int deadlyEscapeCool;
    public int recentHitTime;
    public int projectileHit;
    public int meleeHit;
    public int teleportHideTime = MathHelper.secondsToTicks(5);
    public int mobHurtTime = 0;
    public int deathTime = 0;
    public DamageSource deathBlow = this.damageSources().generic();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState swipeAnimationState = new AnimationState();
    public AnimationState deadlyEscapeAnimationState = new AnimationState();
    public AnimationState teleportInAnimationState = new AnimationState();
    public AnimationState teleportOutAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public EndersentServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 20;
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.PURPLE, false, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.teleportCool > 0) {
            --this.teleportCool;
        }
        if (this.postTeleportTick > 0) {
            --this.postTeleportTick;
            this.getNavigation().stop();
        }
        if (this.mobHurtTime > 0) {
            --this.mobHurtTime;
        }
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(
                    !this.isDeadOrDying() && !this.walkAnimation.isMoving() && this.isCurrentAnimation(IDLE),
                    this.tickCount);
            if (this.isCurrentAnimation(DEADLY_ESCAPE)) {
                this.stopMostAnimation(this.deadlyEscapeAnimationState);
            }
        }
        if (this.isHostile() && this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        if (this.isHostile()) {
            this.bossInfo.setProgress(this.getMaxHealth() > 0 ? this.getHealth() / this.getMaxHealth() : 0);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new AttackGoal(1.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.EndersentHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.EndersentDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.EndersentArmor.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(net.minecraftforge.common.ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.EndersentHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.EndersentArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.EndersentDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(EYE_TYPE, 0);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HidingTime")) {
            this.hidingTime = compound.getInt("HidingTime");
        }
        if (compound.contains("TeleportCool")) {
            this.teleportCool = compound.getInt("TeleportCool");
        }
        if (compound.contains("DeadlyEscapeCool")) {
            this.deadlyEscapeCool = compound.getInt("DeadlyEscapeCool");
        }
        if (compound.contains("EyeType")) {
            this.setEyeType(compound.getInt("EyeType"));
        }
        if (compound.contains("IdleTime")) {
            this.idleTime = compound.getInt("IdleTime");
        }
        if (compound.contains("IsEnhanced")) {
            this.isEnhanced = compound.getBoolean("IsEnhanced");
            if (this.isEnhanced) {
                this.applyEnhancedModifiers();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("HidingTime", this.hidingTime);
        compound.putInt("TeleportCool", this.teleportCool);
        compound.putInt("DeadlyEscapeCool", this.deadlyEscapeCool);
        compound.putInt("EyeType", this.getEyeType());
        compound.putInt("IdleTime", this.idleTime);
        compound.putBoolean("IsEnhanced", this.isEnhanced);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            ItemEntity itementity2 = this
                    .spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.VOID_SHARD.get());
            if (itementity2 != null) {
                itementity2.setGlowingTag(true);
                itementity2.setExtendedLifetime();
            }
            if (this.hasEye()) {
                ItemEntity itementity = this.spawnAtLocation(Items.ENDER_EYE);
                if (itementity != null) {
                    itementity.setExtendedLifetime();
                }
            }
            if (this.isHostile()) {
                ItemEntity treasureBagEntity = this
                        .spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.TREASURE_POUCH.get());
                if (treasureBagEntity != null) {
                    treasureBagEntity.setExtendedLifetime();
                }
                int pearlCount = this.level().getRandom().nextIntBetweenInclusive(4, 8);
                ItemEntity pearlEntity = this.spawnAtLocation(new ItemStack(Items.ENDER_PEARL, pearlCount));
                if (pearlEntity != null) {
                    pearlEntity.setExtendedLifetime();
                }
            }
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        if (pReason != MobSpawnType.TRIGGERED) {
            this.setEyeType(0);
            this.isEnhanced = false;
        }
        return data;
    }

    @Override
    public Component getName() {
        if (this.getEyeType() > 0) {
            return Component.translatable("name.goety.endersent." + this.getEyeType());
        }
        return super.getName();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ENDERSENT_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return ModSounds.ENDERSENT_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENDERSENT_DEATH.get();
    }

    @Override
    public void stepSound() {
        this.playSound(ModSounds.ENDERSENT_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == ModItems.ENDERSENT_ENCHANTED_BOOK.get() && !this.isEnhanced) {
            if (!this.level().isClientSide) {
                int eyeType = this.level().getRandom().nextIntBetweenInclusive(1, 10);
                this.setEyeType(eyeType);
                this.isEnhanced = true;
                AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
                if (health != null) {
                    float healthRatio = this.getMaxHealth() > 0 ? this.getHealth() / (float) this.getMaxHealth() : 0;
                    health.removeModifier(ENHANCED_HEALTH_MODIFIER);
                    health.addTransientModifier(ENHANCED_HEALTH_MODIFIER);
                    this.setHealth(healthRatio * (float) this.getMaxHealth());
                }

                AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
                if (damage != null) {
                    damage.removeModifier(ENHANCED_DAMAGE_MODIFIER);
                    damage.addTransientModifier(ENHANCED_DAMAGE_MODIFIER);
                }

                AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
                if (armor != null) {
                    armor.removeModifier(ENHANCED_ARMOR_MODIFIER);
                    armor.addTransientModifier(ENHANCED_ARMOR_MODIFIER);
                }
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                this.playSound(ModSounds.VOID_FRAME_UNLOCK.get(), 1.0F, 1.0F);
                this.refreshDimensions();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.getTrueOwner() == player && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                float healAmount = this.getHealAmount(itemstack);
                if (healAmount > 0) {
                    float oldHealth = this.getHealth();
                    this.heal(healAmount);
                    float newHealth = this.getHealth();
                    if (newHealth > oldHealth) {
                        if (!player.isCreative()) {
                            itemstack.shrink(1);
                            if (itemstack.getItem() == com.Polarice3.Goety.common.items.ModItems.VOID_BOTTLE.get()) {
                                player.getInventory().placeItemBackInInventory(
                                        new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE));
                            } else if (itemstack.getItem() == com.Polarice3.Goety.common.items.ModItems.VOID_BUCKET
                                    .get()) {
                                player.getInventory().placeItemBackInInventory(
                                        new ItemStack(net.minecraft.world.item.Items.BUCKET));
                            }
                        }
                        this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private float getHealAmount(ItemStack stack) {
        net.minecraft.world.item.Item item = stack.getItem();
        if (item == com.Polarice3.Goety.common.items.ModItems.VOID_BOTTLE.get()) {
            return this.getMaxHealth() * 0.01F;
        } else if (item == com.Polarice3.Goety.common.items.ModItems.VOID_BUCKET.get()) {
            return this.getMaxHealth() * 0.05F;
        } else if (item == com.Polarice3.Goety.common.blocks.ModBlocks.VOID_BLOCK.get().asItem()) {
            return this.getMaxHealth() * 0.05F;
        } else if (item == com.Polarice3.Goety.common.items.ModItems.VOID_SHARD.get()) {
            return this.getMaxHealth() * 0.15F;
        } else if (item == com.Polarice3.Goety.common.items.ModItems.VOID_ECHO.get()) {
            return this.getMaxHealth() * 0.30F;
        }
        return 0.0F;
    }

    public void aiStep() {
        super.aiStep();
        if (!this.isHiding() && !this.isDeadOrDying()) {
            if (this.level().isClientSide) {
                for (int i = 0; i < 2; ++i) {
                    this.level().addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D,
                            this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
                            (this.random.nextDouble() - 0.5D) * 2.0D);
                }
            }
        }
        if (this.deadlyEscapeCool > 0) {
            if (this.isDeadlyEscape()) {
                this.setDeadlyEscape(false);
            }
            --this.deadlyEscapeCool;
        }
        if (this.recentHitTime > 0) {
            --this.recentHitTime;
        }
        this.idleTick();
        if (!this.level().isClientSide) {
            if (!this.isDeadOrDying()) {
                this.setAggressive(this.getTarget() != null);
                if (this.getEyeType() > 0 && !this.isHiding()) {
                    this.eyeTypeEffects();
                }
                if (this.isMeleeAttacking()) {
                    ++this.attackTick;
                } else if (this.isAttacking()) {
                    if (this.isDeadlyEscape()) {
                        this.setAnimationState(DEADLY_ESCAPE);
                    } else {
                        this.setAnimationState(IDLE);
                    }
                }
                if (this.isCurrentAnimation(TELEPORT_IN)) {
                    if (this.postTeleportTick <= 0) {
                        this.setAnimationState(IDLE);
                    } else if (this.postTeleportTick == (MathHelper.secondsToTicks(1.92F - 0.44F))) {
                        this.frontSmash();
                    }
                }
                AttributeInstance instance = this.getAttribute(Attributes.ATTACK_DAMAGE);
                if (instance != null) {
                    if (this.isCurrentAnimation(TELEPORT_IN)) {
                        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                            instance.removeModifier(TELEPORT_ATTACK_MODIFIER);
                            instance.addTransientModifier(TELEPORT_ATTACK_MODIFIER);
                        }
                    } else {
                        if (instance.hasModifier(TELEPORT_ATTACK_MODIFIER)) {
                            instance.removeModifier(TELEPORT_ATTACK_MODIFIER);
                        }
                    }
                    if (this.isCurrentAnimation(DEADLY_ESCAPE)) {
                        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                            instance.removeModifier(DE_ATTACK_MODIFIER);
                            instance.addTransientModifier(DE_ATTACK_MODIFIER);
                        }
                    } else {
                        if (instance.hasModifier(DE_ATTACK_MODIFIER)) {
                            instance.removeModifier(DE_ATTACK_MODIFIER);
                        }
                    }
                }
                if (!this.isHiding()) {
                    if (this.getTarget() != null) {
                        if (!this.isTeleporting()
                                && !this.isDeadlyEscape()
                                && this.postTeleportTick <= 0) {
                            if (this.getTarget().distanceTo(this) <= 8.0F
                                    && this.level().getRandom().nextBoolean()
                                    && this.recentHitTime > 0
                                    && this.deadlyEscapeCool <= 0
                                    && this.watchlingNumber() < 3) {
                                if (!this.isMeleeAttacking()
                                        && !this.isAttacking()) {
                                    this.setAnimationState(DEADLY_ESCAPE);
                                    this.setDeadlyEscape(true);
                                }
                            } else if (!this.isWithinMeleeAttackRange(this.getTarget())
                                    && !this.isMeleeAttacking()
                                    && !this.isAttacking()
                                    && this.recentHitTime > 0
                                    && this.level().getRandom().nextBoolean()
                                    && this.teleportCool <= 0) {
                                this.teleportHideTime = MathHelper
                                        .secondsToTicks(this.level().getRandom().nextIntBetweenInclusive(3, 5));
                                this.setTeleporting(true);
                                this.setAnimationState(TELEPORT_OUT);
                            }
                        }
                    } else if (this.tickCount % 100 == 0 && this.level().getRandom().nextFloat() <= 0.1F
                            && !this.isTeleporting() && !this.isDeadlyEscape() && this.postTeleportTick <= 0) {
                        this.teleportHideTime = MathHelper
                                .secondsToTicks(this.level().getRandom().nextIntBetweenInclusive(3, 5));
                        this.setTeleporting(true);
                        this.setAnimationState(TELEPORT_OUT);
                    }
                    if (this.isDeadlyEscape() && this.isCurrentAnimation(DEADLY_ESCAPE)) {
                        this.setTeleporting(false);
                        this.deadlyEscape();
                    } else if (this.isTeleporting()) {
                        this.setDeadlyEscape(false);
                        this.specialTeleport();
                    }
                }
            }
        }
    }

    @Override
    public boolean isHiding() {
        return super.isHiding();
    }

    @Override
    public void startHide() {
        super.startHide();
        this.setAnimationState(TELEPORT_OUT);
    }

    @Override
    public void stopHide() {
        super.stopHide();
        this.setAnimationState(IDLE);
    }

    public int getHidingDuration() {
        if (this.isTeleporting()) {
            return this.teleportHideTime;
        }
        return MathHelper.secondsToTicks(5);
    }

    @Override
    public boolean shouldStopHiding() {
        return this.isDeadlyEscape() && this.watchlingNumber() <= 0;
    }

    public void teleportAfterHiding() {
        if (!this.isTeleporting()) {
            this.teleport(7.0D);
        } else {
            if (this.getTarget() != null) {
                this.teleportTowards(this.getTarget(), 4.0D);
            } else {
                this.teleportIn();
            }
            this.teleportCool = MathHelper.secondsToTicks(5);
            this.setTeleporting(false);
        }
        if (this.isDeadlyEscape()) {
            this.deadlyEscapeCool = MathHelper.secondsToTicks(10);
            this.setDeadlyEscape(false);
        }
        if (this.getTarget() != null) {
            if (!this.level().isClientSide) {
                MobUtil.instaLook(this, this.getTarget());
                com.Polarice3.Goety.common.network.ModNetwork.sentToTrackingEntity(this,
                        new com.Polarice3.Goety.common.network.server.SInstaLookPacket(this.getId(),
                                this.getTarget().getId()));
            }
        }
    }

    @Override
    public void teleportIn() {
        super.teleportIn();
        this.setAnimationState(TELEPORT_IN);
        this.playSound(ModSounds.ENDERSENT_TELEPORT_SMASH.get(), this.getSoundVolume(), this.getVoicePitch());
        this.postTeleportTick = MathHelper.secondsToTicks(1.92F);
        if (this.isTeleporting()) {
            this.setTeleporting(false);
        }
    }

    protected boolean teleport(double range) {
        if (!this.level().isClientSide() && this.isAlive()) {
            for (int i = 0; i < 128; ++i) {
                boolean flag = true;
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * range;
                double d4 = this.getY() + (this.getRandom().nextInt(Mth.floor(range)) - (range / 2.0D));
                if (this.getTarget() != null) {
                    d4 = this.getTarget().getY();
                }
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * range;
                BlockPos blockPos = BlockPos.containing(d3, d4, d5);
                if (this.getTarget() != null && i < 64) {
                    flag = BlockFinder.canSeeBlock(this.getTarget(), blockPos);
                }
                net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory
                        .onEnderTeleport(this, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (event.isCanceled()) {
                    this.teleportIn();
                    return false;
                }
                if (flag) {
                    if (this.ownedTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
                        return true;
                    } else if (i == 127) {
                        this.teleportIn();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void teleportTowards(Entity entity, double range) {
        if (!this.level().isClientSide() && this.isAlive()) {
            if (entity == null) {
                this.teleportIn();
                return;
            }
            try {
                for (int i = 0; i < 128; ++i) {
                    int range2 = Mth.floor(range);
                    double d1 = entity.getX() + this.level().getRandom().nextIntBetweenInclusive(-range2, range2);
                    double d2 = entity.getY();
                    double d3 = entity.getZ() + this.level().getRandom().nextIntBetweenInclusive(-range2, range2);
                    net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory
                            .onEnderTeleport(this, d1, d2, d3);
                    if (event.isCanceled()) {
                        if (this.getHidingDuration() > 0) {
                            this.teleportIn();
                        } else {
                            this.teleportHits();
                        }
                        break;
                    }
                    if (this.ownedTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
                        MobUtil.instaLook(this, entity);
                        break;
                    } else if (i == 127) {
                        MobUtil.instaLook(this, entity);
                        if (this.getHidingDuration() > 0) {
                            this.teleportIn();
                        } else {
                            this.teleportHits();
                        }
                        break;
                    }
                }
            } catch (NullPointerException exception) {
                this.teleportIn();
            }
        }
    }

    @Override
    public void teleportHits() {
        this.level().broadcastEntityEvent(this, (byte) 46);
        if (!this.isSilent()) {
            this.level().playSound(null, this.xo, this.yo, this.zo, ModSounds.ENDERLING_TELEPORT_OUT.get(),
                    this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(ModSounds.ENDERLING_TELEPORT_IN.get(), 1.0F, 1.0F);
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            AABB aabb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, aabb);

            for (LivingEntity target : entities) {
                if (target != this && !MobUtil.areAllies(target, this)) {
                    this.doHurtTarget(target);
                    double d0 = target.getX() - this.getX();
                    double d1 = target.getZ() - this.getZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                    target.push(d0 / d2 * 0.5D, 0.2D, d1 / d2 * 0.5D);
                }
            }
            for (int i = 0; i < 32; ++i) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                        this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(),
                        1, this.random.nextGaussian(), 0.0D, this.random.nextGaussian(), 0);
            }
        }
    }

    public boolean isTeleporting() {
        return this.getFlag(4);
    }

    public void setTeleporting(boolean teleporting) {
        this.setFlags(4, teleporting);
    }

    public boolean isDeadlyEscape() {
        return this.getFlag(8);
    }

    public void setDeadlyEscape(boolean deadlyEscape) {
        this.setFlags(8, deadlyEscape);
    }

    public void deadlyEscape() {
        ++this.preHidingTime;
        this.getNavigation().stop();
        this.getMoveControl().strafe(0.0F, 0.0F);
        if (this.getTarget() != null) {
            MobUtil.instaLook(this, this.getTarget());
        }
        if (this.preHidingTime == 1) {
            this.playSound(ModSounds.ENDERSENT_DEADLY_ESCAPE.get(), this.getSoundVolume(), this.getVoicePitch());
        }
        if (this.preHidingTime == MathHelper.secondsToTicks(1.6F)) {
            this.frontSmash();
            this.summonWatchlings();
        }
        if (this.preHidingTime >= MathHelper.secondsToTicks(2)) {
            if (this.getTarget() != null) {
                MobUtil.instaLook(this, this.getTarget());
            }
            this.startHide();
            this.preHidingTime = 0;
        }
    }

    public void summonWatchlings() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 6; ++i) {
                if (i < 3 || serverLevel.getRandom().nextBoolean()) {
                    com.Polarice3.Goety.common.entities.ally.ender.WatchlingServant servant = new com.Polarice3.Goety.common.entities.ally.ender.WatchlingServant(
                            com.Polarice3.Goety.common.entities.ModEntityType.WATCHLING_SERVANT.get(), serverLevel);
                    BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), servant, serverLevel);
                    servant.setTrueOwner(this);
                    servant.moveTo(blockPos, 0.0F, 0.0F);
                    servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                            MobSpawnType.MOB_SUMMONED, null, null);
                    serverLevel.addFreshEntity(servant);
                } else {
                    break;
                }
            }
        }
    }

    public void specialTeleport() {
        ++this.preHidingTime;
        this.getNavigation().stop();
        this.getMoveControl().strafe(0.0F, 0.0F);
        if (this.getTarget() != null) {
            MobUtil.instaLook(this, this.getTarget());
        }
        if (this.preHidingTime % 2 == 0) {
            this.playSound(ModSounds.ENDERLING_TELEPORT_OUT.get(), 1.0F, 1.0F);
        }
        if (this.preHidingTime >= MathHelper.secondsToTicks(1.92F)) {
            this.startHide();
            this.preHidingTime = 0;
        }
    }

    public void frontSmash() {
        this.leftSmash();
        this.rightSmash();
        BlockPos blockPos = BlockPos.containing(this.getXFront(), this.getY() - 1.0F, this.getZFront());
        float size = this.isDeadlyEscape() ? 5.0F : 2.5F;
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target != this && !MobUtil.areAllies(target, this)) {
                this.doHurtTarget(target);
            }
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.isDeadlyEscape()) {
                ColorUtil colorUtil = new ColorUtil(0xf169e9);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(),
                        colorUtil.green(), colorUtil.blue(), size, 1), this.getXFront(),
                        BlockFinder.moveDownToGround(this), this.getZFront(), 1, 0.0D, 0.0D, 0.0D,
                        0);
                serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(colorUtil.red(), colorUtil.green(),
                        colorUtil.blue(), size, 1), this.getXFront(),
                        BlockFinder.moveDownToGround(this), this.getZFront(), 1, 0.0D, 0.0D, 0.0D,
                        0);
                ServerParticleUtil.circularParticles(serverLevel,
                        ModParticleTypes.BIG_CULT_SPELL.get(), this.getXFront(),
                        BlockFinder.moveDownToGround(this) + 1.0D, this.getZFront(), colorUtil.red(),
                        colorUtil.green(), colorUtil.blue(), size);
            } else {
                BlockState blockState = serverLevel.getBlockState(blockPos);
                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(), blockPos.getY() + 1.0F,
                        blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
                ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel,
                        blockPos).col);
                serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(),
                        colorUtil.green(), colorUtil.blue(), size, 1), this.getXFront(),
                        BlockFinder.moveDownToGround(this), this.getZFront(), 1, 0.0D, 0.0D, 0.0D,
                        0);
                ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
                ServerParticleUtil.circularParticles(serverLevel,
                        ModParticleTypes.BIG_CULT_SPELL.get(), blockPos.getX(), blockPos.getY() +
                                1.0F,
                        blockPos.getZ(), colorUtil1.red(), colorUtil1.green(),
                        colorUtil1.blue(), size);
            }
        }
    }

    public void leftSmash() {
        double x = this.getXLeft();
        double z = this.getZLeft();
        float size = this.isDeadlyEscape() ? 5.0F : 2.5F;
        BlockPos blockPos = BlockPos.containing(x, this.getY() - 1.0F, z);
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target != this && !MobUtil.areAllies(target, this)) {
                this.doHurtTarget(target);
            }
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockState blockState = serverLevel.getBlockState(blockPos);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(), blockPos.getY() + 1.0F,
                    blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
            ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel,
                    blockPos).col);
            serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(),
                    colorUtil.green(), colorUtil.blue(), size, 1), x,
                    BlockFinder.moveDownToGround(this), z, 1, 0.0D, 0.0D, 0.0D,
                    0);
            ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
            ServerParticleUtil.circularParticles(serverLevel,
                    ModParticleTypes.BIG_CULT_SPELL.get(), blockPos.getX(), blockPos.getY() +
                            1.0F,
                    blockPos.getZ(), colorUtil1.red(), colorUtil1.green(),
                    colorUtil1.blue(), size);
        }
    }

    public void rightSmash() {
        double x = this.getXRight();
        double z = this.getZRight();
        float size = this.isDeadlyEscape() ? 5.0F : 2.5F;
        BlockPos blockPos = BlockPos.containing(x, this.getY() - 1.0F, z);
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target != this && !MobUtil.areAllies(target, this)) {
                this.doHurtTarget(target);
            }
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockState blockState = serverLevel.getBlockState(blockPos);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(), blockPos.getY() + 1.0F,
                    blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
            ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel,
                    blockPos).col);
            serverLevel.sendParticles(new CircleExplodeParticleOption(colorUtil.red(),
                    colorUtil.green(), colorUtil.blue(), size, 1), x,
                    BlockFinder.moveDownToGround(this), z, 1, 0.0D, 0.0D, 0.0D, 0);
            ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
            ServerParticleUtil.circularParticles(serverLevel,
                    ModParticleTypes.BIG_CULT_SPELL.get(), blockPos.getX(), blockPos.getY() +
                            1.0F,
                    blockPos.getZ(), colorUtil1.red(), colorUtil1.green(),
                    colorUtil1.blue(), size);
        }
    }

    public double getXLeft() {
        return this.getXFront() + MobUtil.getHorizontalLeftLookAngle(this).x;
    }

    public double getZLeft() {
        return this.getZFront() + MobUtil.getHorizontalLeftLookAngle(this).z;
    }

    public double getXRight() {
        return this.getXFront() + MobUtil.getHorizontalRightLookAngle(this).x;
    }

    public double getZRight() {
        return this.getZFront() + MobUtil.getHorizontalRightLookAngle(this).z;
    }

    public double getXFront() {
        return this.getX() + this.getHorizontalLookAngle().x;
    }

    public double getZFront() {
        return this.getZ() + this.getHorizontalLookAngle().z;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_219422_) {
        if (ANIM_STATE.equals(p_219422_)) {
            if (this.level().isClientSide) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0:
                        this.stopMostAnimation(this.idleAnimationState);
                        break;
                    case 1:
                        this.stopMostAnimation(this.attackAnimationState);
                        this.attackAnimationState.start(this.tickCount);
                        break;
                    case 2:
                        this.stopMostAnimation(this.swipeAnimationState);
                        this.swipeAnimationState.start(this.tickCount);
                        break;
                    case 3:
                        this.stopMostAnimation(this.deadlyEscapeAnimationState);
                        this.deadlyEscapeAnimationState.start(this.tickCount);
                        break;
                    case 4:
                        this.stopMostAnimation(this.teleportInAnimationState);
                        this.teleportInAnimationState.start(this.tickCount);
                        break;
                    case 5:
                        this.stopMostAnimation(this.teleportOutAnimationState);
                        this.teleportOutAnimationState.start(this.tickCount);
                        break;
                    case 6:
                        this.stopMostAnimation(this.deathAnimationState);
                        this.deathAnimationState.start(this.tickCount);
                        break;
                }
            }
        }

        super.onSyncedDataUpdated(p_219422_);
    }

    public int getAnimationState(String animation) {
        if (animation.equals(IDLE)) {
            return 0;
        } else if (animation.equals(ATTACK)) {
            return 1;
        } else if (animation.equals(SWIPE)) {
            return 2;
        } else if (animation.equals(DEADLY_ESCAPE)) {
            return 3;
        } else if (animation.equals(TELEPORT_IN)) {
            return 4;
        } else if (animation.equals(TELEPORT_OUT)) {
            return 5;
        } else if (animation.equals(DEATH)) {
            return 6;
        } else {
            return 0;
        }
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    public void idleTick() {
        if (this.getTarget() != null && this.isCurrentAnimation(IDLE) && !this.isHiding()
                && !this.walkAnimation.isMoving()) {
            ++this.idleTime;
            ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
            float size = 2.5F;
            if (this.idleTime == 5) {
                BlockPos blockPos = BlockPos.containing(this.getXRight(), this.getY() - 1.0F, this.getZRight());
                if (this.level() instanceof ServerLevel serverLevel) {
                    BlockState blockState = serverLevel.getBlockState(blockPos);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                    ColorUtil colorUtil = new ColorUtil(
                            serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                    this.playSound(ModSounds.ENDERSENT_AMBIENT_SMASH.get(), this.getSoundVolume(),
                            this.getVoicePitch());
                    serverLevel.sendParticles(
                            new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size,
                                    1),
                            this.getXRight(), BlockFinder.moveDownToGround(this), this.getZRight(), 1, 0.0D, 0.0D, 0.0D,
                            0);
                    ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(),
                            blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), colorUtil1.red(),
                            colorUtil1.green(), colorUtil1.blue(), size);
                    ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(), blockPos.getY() + 1.0F,
                            blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
                }
            }
            if (this.idleTime == 63) {
                this.playSound(ModSounds.ENDERSENT_AMBIENT_SMASH.get(), this.getSoundVolume(), this.getVoicePitch());
            }
            if (this.idleTime == 67) {
                BlockPos blockPos = BlockPos.containing(this.getXLeft(), this.getY() - 1.0F, this.getZLeft());
                if (this.level() instanceof ServerLevel serverLevel) {
                    BlockState blockState = serverLevel.getBlockState(blockPos);
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                    ColorUtil colorUtil = new ColorUtil(
                            serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                    serverLevel.sendParticles(
                            new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size,
                                    1),
                            this.getXLeft(), BlockFinder.moveDownToGround(this), this.getZLeft(), 1, 0.0D, 0.0D, 0.0D,
                            0);
                    ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(),
                            blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), colorUtil1.red(),
                            colorUtil1.green(), colorUtil1.blue(), size);
                    ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(), blockPos.getY() + 1.0F,
                            blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
                }
            }
            if (this.idleTime >= 119) {
                this.idleTime = 0;
            }
        } else {
            this.idleTime = 0;
        }
    }

    public int getEyeType() {
        return this.entityData.get(EYE_TYPE);
    }

    public void setEyeType(int eyeType) {
        this.entityData.set(EYE_TYPE, Math.min(eyeType, 10));
    }

    public boolean hasEye() {
        return this.getEyeType() > 0;
    }

    public boolean isAttacking() {
        return this.isCurrentAnimation(ATTACK) || this.isCurrentAnimation(SWIPE);
    }

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }

    public int watchlingNumber() {
        return this.level().getEntitiesOfClass(com.Polarice3.Goety.common.entities.ally.ender.WatchlingServant.class,
                this.getBoundingBox().inflate(64.0D),
                predicate -> predicate.getTrueOwner() == this).size();
    }

    public boolean isWithinMeleeAttackRange(LivingEntity livingentity) {
        double d0 = this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        return d0 <= this.getMeleeAttackRangeSqr(livingentity);
    }

    public double getMeleeAttackRangeSqr(LivingEntity livingEntity) {
        return this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + livingEntity.getBbWidth();
    }

    public void eyeTypeEffects() {
        if (this.getEyeType() == SEARING_EYE) {
            this.addEyeEffects(MobEffects.FIRE_RESISTANCE);
            this.addEyeEffects(GoetyEffects.FIERY_AURA.get());
            this.addEyeEffects(GoetyEffects.RALLYING.get());
        } else if (this.getEyeType() == HALLOWED_EYE) {
            this.addEyeEffects(GoetyEffects.ALTRUISTIC.get());
            this.addEyeEffects(GoetyEffects.RADIANCE.get());
            this.addEyeEffects(GoetyEffects.SHIELDING.get());
        } else if (this.getEyeType() == TWISTED_EYE) {
            this.addEyeEffects(MobEffects.MOVEMENT_SPEED);
            this.addEyeEffects(GoetyEffects.ELECTRIFIED.get());
            this.addEyeEffects(GoetyEffects.SWIRLING.get());
        } else if (this.getEyeType() == DREADFUL_EYE) {
            this.addEyeEffects(GoetyEffects.GRAVITY_PULSE.get());
            this.addEyeEffects(GoetyEffects.DEFLECTIVE.get());
            this.addEyeEffects(GoetyEffects.FROSTY_AURA.get());
        } else if (this.getEyeType() == SPIKED_EYE) {
            this.addEyeEffects(ModEffects.ENCHANTMENT_SHARPNESS.get(), 3);
            this.addEyeEffects(ModEffects.ENCHANTMENT_THORNS.get(), 2);
            this.addEyeEffects(GoetyEffects.LEECHING.get());
        } else if (this.getEyeType() == SAVAGE_EYE) {
            this.addEyeEffects(ModEffects.FRENZIED.get(), 2);
            this.addEyeEffects(ModEffects.CRITICAL_HIT.get(), 2);
            this.addEyeEffects(MobEffects.DAMAGE_RESISTANCE);
        } else if (this.getEyeType() == BLIGHT_EYE) {
            this.addEyeEffects(ModEffects.WEAKENING_HANDS.get());
            this.addEyeEffects(GoetyEffects.VENOMOUS_HANDS.get());
            this.addEyeEffects(MobEffects.REGENERATION);
        } else if (this.getEyeType() == BINDING_EYE) {
            this.addEyeEffects(ModEffects.CHAINS.get(), 4);
            this.addEyeEffects(ModEffects.ECHO.get(), 2);
            this.addEyeEffects(GoetyEffects.CHILL_HIDE.get());
        } else if (this.getEyeType() == REAPING_EYE) {
            this.addEyeEffects(ModEffects.ENCHANTMENT_THUNDERING.get(), 7);
            this.addEyeEffects(ModEffects.SHOCKWAVE.get(), 7);
            this.addEyeEffects(MobEffects.DAMAGE_BOOST);
        } else if (this.getEyeType() == RAVENOUS_EYE) {
            this.addEyeEffects(ModEffects.COMMITTED.get(), 2);
            this.addEyeEffects(ModEffects.RAMPAGING.get(), 2);
            this.addEyeEffects(MobEffects.DIG_SPEED);
        } else {
            this.addEyeEffects(MobEffects.DAMAGE_BOOST);
            this.addEyeEffects(MobEffects.DAMAGE_RESISTANCE);
        }
    }

    public void addEyeEffects(MobEffect effect) {
        this.addEyeEffects(effect, 0);
    }

    public void addEyeEffects(MobEffect effect, int level) {
        this.addEffect(new MobEffectInstance(effect, -1, level, false, false));
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.swipeAnimationState);
        animationStates.add(this.deadlyEscapeAnimationState);
        animationStates.add(this.teleportInAnimationState);
        animationStates.add(this.teleportOutAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        for (AnimationState state : this.getAnimations()) {
            state.stop();
        }
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    class AttackGoal extends MeleeAttackGoal {
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(double moveSpeed) {
            super(EndersentServant.this, moveSpeed, true);
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            return EndersentServant.this.getTarget() != null
                    && EndersentServant.this.getTarget().isAlive()
                    && !EndersentServant.this.isHiding()
                    && !EndersentServant.this.isTeleporting()
                    && !EndersentServant.this.isDeadlyEscape()
                    && EndersentServant.this.postTeleportTick <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse()
                    && !EndersentServant.this.isHiding()
                    && !EndersentServant.this.isTeleporting()
                    && !EndersentServant.this.isDeadlyEscape()
                    && EndersentServant.this.postTeleportTick <= 0;
        }

        @Override
        public void start() {
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            EndersentServant.this.setMeleeAttacking(false);
            EndersentServant.this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = EndersentServant.this.getTarget();
            if (livingentity == null) {
                this.stop();
                return;
            }

            EndersentServant.this.getLookControl().setLookAt(livingentity, EndersentServant.this.getMaxHeadYRot(),
                    EndersentServant.this.getMaxHeadXRot());

            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                EndersentServant.this.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.checkAndPerformAttack(livingentity,
                    EndersentServant.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            boolean smash = EndersentServant.this.level().getRandom().nextBoolean();

            if (!EndersentServant.this.isMeleeAttacking() && distToEnemySqr <= d0) {
                EndersentServant.this.setMeleeAttacking(true);
                if (smash) {
                    EndersentServant.this.setAnimationState(SWIPE);
                    EndersentServant.this.playSound(ModSounds.ENDERSENT_SWING.get(),
                            EndersentServant.this.getSoundVolume(), EndersentServant.this.getVoicePitch());
                } else {
                    EndersentServant.this.setAnimationState(ATTACK);
                    EndersentServant.this.playSound(ModSounds.ENDERSENT_ATTACK.get(),
                            EndersentServant.this.getSoundVolume(), EndersentServant.this.getVoicePitch());
                }
            }
            if (EndersentServant.this.isMeleeAttacking()) {
                float seconds = EndersentServant.this.isCurrentAnimation(ATTACK) ? 2.44F : 2.84F;
                if (EndersentServant.this.attackTick < MathHelper.secondsToTicks(seconds)) {
                    MobUtil.instaLook(EndersentServant.this, enemy);
                    if (EndersentServant.this.isCurrentAnimation(SWIPE)) {
                        if (EndersentServant.this.attackTick == MathHelper.secondsToTicks(1)) {
                            EndersentServant.this.massiveSweep(EndersentServant.this, 4.5D, 200.0D);
                        }
                    } else if (EndersentServant.this.isCurrentAnimation(ATTACK)) {
                        if (EndersentServant.this.attackTick == MathHelper.secondsToTicks(1)) {
                            double x = EndersentServant.this.getXFront();
                            double z = EndersentServant.this.getZFront();
                            float size = 2.5F;
                            BlockPos blockPos = BlockPos.containing(x, EndersentServant.this.getY() - 1.0F, z);
                            AABB aabb = new AABB(blockPos).inflate(size);
                            for (LivingEntity target : EndersentServant.this.level()
                                    .getEntitiesOfClass(LivingEntity.class, aabb)) {
                                if (target != EndersentServant.this
                                        && !MobUtil.areAllies(target, EndersentServant.this)) {
                                    EndersentServant.this.doHurtTarget(target);
                                }
                            }
                            CameraShake.cameraShake(EndersentServant.this.level(), blockPos.getCenter(), 15.0F, 0.1F, 0,
                                    10);
                            if (EndersentServant.this.level() instanceof ServerLevel serverLevel) {
                                BlockState blockState = serverLevel.getBlockState(blockPos);
                                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                                ServerParticleUtil.circularParticles(serverLevel, option, blockPos.getX(),
                                        blockPos.getY() + 1.0F, blockPos.getZ(), 0.0F, 0.0F, 0.0F, size);
                                ColorUtil colorUtil = new ColorUtil(
                                        serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos).col);
                                serverLevel.sendParticles(
                                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(),
                                                colorUtil.blue(), size, 1),
                                        x, BlockFinder.moveDownToGround(EndersentServant.this), z, 1, 0.0D, 0.0D, 0.0D,
                                        0);
                                ColorUtil colorUtil1 = new ColorUtil(0x3c3c3e);
                                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(),
                                        blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), colorUtil1.red(),
                                        colorUtil1.green(), colorUtil1.blue(), size);
                            }
                        }
                    }
                } else {
                    if (!EndersentServant.this.isHiding() && !EndersentServant.this.isDeadlyEscape()) {
                        if (EndersentServant.this.level().getRandom().nextFloat() <= 0.3F) {
                            EndersentServant.this.startHide();
                            if (EndersentServant.this.level().getRandom().nextBoolean()
                                    && EndersentServant.this.watchlingNumber() <= 3) {
                                EndersentServant.this.summonWatchlings();
                            }
                        }
                    }
                    EndersentServant.this.setMeleeAttacking(false);
                    EndersentServant.this.recentHitTime = MathHelper.secondsToTicks(1);
                }
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
                this.doHurtTarget(target);
            }
        }
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_DROWNING)) {
            return false;
        }

        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (source.getEntity() != null) {
                if (!MobUtil.areAllies(this, source.getEntity())) {
                    float threshold = (this.getMaxHealth() * 0.025F);
                    if (!ModDamageSource.physicalAttacks(source)) {
                        amount /= 2.0F;
                        if (amount >= threshold) {
                            this.projectileHit += 1 + Mth.floor(amount / threshold);
                        }
                        ++this.projectileHit;
                        if (this.projectileHit >= 7) {
                            this.projectileHit = 0;
                            this.recentHitTime = MathHelper.secondsToTicks(5);
                        }
                    } else {
                        ++this.meleeHit;
                        if (this.level().getRandom().nextBoolean() || this.meleeHit >= 2) {
                            if (this.level().getRandom().nextBoolean() || this.meleeHit >= 3) {
                                this.meleeHit = 0;
                                this.recentHitTime = MathHelper.secondsToTicks(4);
                            }
                        }
                    }
                }
                if (source.getEntity() instanceof LivingEntity
                        || source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
                    this.mobHurtTime = 10;
                }
            }
            boolean flag = source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.ThrownPotion;

            if (!this.isHiding()) {
                if (flag
                        || source.is(net.minecraft.tags.DamageTypeTags.IS_DROWNING)
                        || source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
                    if (this.teleportHurt()) {
                        return true;
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    public boolean teleportHurt() {
        if (!this.isStaying()) {
            if (this.teleportCool <= 0) {
                for (int i = 0; i < 64; ++i) {
                    if (this.teleport()) {
                        this.teleportCool = MathHelper.secondsToTicks(10);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (this.isHostile()) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
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

    public void warnKill(Player player) {
        this.killChance = 60;
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("info.goety.servant.tryKill", this.getDisplayName()),
                true);
    }

    public void setHostile(boolean hostile) {
        super.setHostile(hostile);
        if (hostile) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public void applyEnhancedModifiers() {
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            double currentHealthRatio = this.getHealth() / this.getMaxHealth();
            health.removeModifier(ENHANCED_HEALTH_MODIFIER);
            health.addTransientModifier(ENHANCED_HEALTH_MODIFIER);
            this.setHealth((float) (this.getMaxHealth() * currentHealthRatio));
        }

        AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(ENHANCED_DAMAGE_MODIFIER);
            damage.addTransientModifier(ENHANCED_DAMAGE_MODIFIER);
        }

        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.removeModifier(ENHANCED_ARMOR_MODIFIER);
            armor.addTransientModifier(ENHANCED_ARMOR_MODIFIER);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (this.level().getRandom().nextBoolean() || this.isDeadlyEscape()
                || this.getCurrentAnimation() == this.getAnimationState(TELEPORT_IN)) {
            if (entityIn instanceof Player player && player.isBlocking()) {
                player.disableShield(true);
            }
        }
        if (entityIn instanceof Mob) {
            MobUtil.disableShield(entityIn);
        }
        return flag;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == com.Polarice3.Goety.common.effects.GoetyEffects.VOID_TOUCHED.get()) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    @Override
    public void die(DamageSource cause) {
        if (this.deathTime > 0) {
            super.die(cause);
        }
    }

    protected void tickDeath() {
        ++this.deathTime;
        this.setAnimationState(DEATH);
        if (this.deathTime < MathHelper.secondsToTicks(2)) {
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 16; ++i) {
                    serverLevel.sendParticles(
                            new MagicSmokeParticle.Option(0, 0,
                                    this.level().getRandom().nextIntBetweenInclusive(40, 80), 0.25F),
                            this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0,
                            this.level().getRandom().nextBoolean() ? 0.01D : -0.01D, 0.1D,
                            this.level().getRandom().nextBoolean() ? 0.01D : -0.01D, 0.5F);
                }
            }
        }
        this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
        if (this.deathTime == 1) {
            this.die(this.deathBlow);
        }
        if (this.deathTime >= MathHelper.secondsToTicks(4)) {
            this.remove(RemovalReason.KILLED);
        }
    }
}