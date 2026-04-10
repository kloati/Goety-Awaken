package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.*;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.PhantomServant;
import com.Polarice3.Goety.common.entities.ally.undead.ReaperServant;
import com.Polarice3.Goety.common.entities.ally.undead.WraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.BorderWraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.MossySkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.RattledServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonPillagerServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.StrayServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SunkenSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.HuskServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.FrozenZombieServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.JungleZombieServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.DrownedServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.FrayedServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVindicatorServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.util.MagicLightningTrap;
import com.Polarice3.Goety.common.network.server.SThunderBoltPacket;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.common.magic.spells.storm.ThunderboltSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.items.ModItems;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.google.common.base.Predicate;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.ParchedServant;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.damagecap.DamageCapManager;
import com.k1sak1.goetyawaken.utils.ModDamageSource;
import com.Polarice3.Goety.common.entities.neutral.Wartling;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.MathHelper;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes.KillSpecialEnemyQuoteHandler;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes.KillSpecialEnemyQuoteLoader;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes.NamelessOneQuote;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes.NamelessOneSubtitles;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public abstract class AbstractNamelessOne extends AbstractNecromancer implements ICustomAttributes {
    protected static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData
            .defineId(AbstractNamelessOne.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData
            .defineId(AbstractNamelessOne.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_MIRROR = SynchedEntityData
            .defineId(AbstractNamelessOne.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> MIRROR_HIT_COUNT = SynchedEntityData
            .defineId(AbstractNamelessOne.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MIRROR_LIFETIME = SynchedEntityData
            .defineId(AbstractNamelessOne.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SELECTED_SPELL = SynchedEntityData
            .defineId(AbstractNamelessOne.class, EntityDataSerializers.INT);
    private DamageCapManager damageCapManager;
    public static final int IDLE_ANIM = 0;
    public static final int WALK_ANIM = 1;
    public static final int ATTACK_ANIM = 2;
    public static final int SUMMON_ANIM = 3;
    public static final int SPELL_ANIM = 4;
    public static final int ALERT_ANIM = 5;
    public static final int FLY_ANIM = 6;
    public static final int WALK2_ANIM = 7;
    public static final int UPDRAFT_ANIM = 8;
    public static final int STORM_ANIM = 9;
    public static final int RAPID_ANIM = 10;
    public static final int HEART_OF_THE_NIGHT_ANIM = 11;
    public static final int TELEPORTOUT_ANIM = 12;
    public static final int TELEPORTIN_ANIM = 13;
    public static final int RANGE_SPELL_ATTACK_ANIM = 14;
    public static final int WAKE_ANIM = 15;
    public static final int AVADA_ANIM = 16;
    public static final int QUAKE1_ANIM = 17;
    public static final int QUAKE2_ANIM = 18;
    public static final int SLOW_SPELL_ANIM = 19;
    public static final int STAB_ANIM = 20;
    public static final int BREATHE_ANIM = 21;
    public static final int DEAD_ANIM = 22;
    public static final int STORM2_ANIM = 23;
    public static final int LEECHING_SPELL_ANIM = 24;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState summonAnimationState = new AnimationState();
    public final AnimationState spellAnimationState = new AnimationState();
    public final AnimationState alertAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState walk2AnimationState = new AnimationState();
    public final AnimationState updrafAnimationState = new AnimationState();
    public final AnimationState stormAnimationState = new AnimationState();
    public final AnimationState storm2AnimationState = new AnimationState();
    public final AnimationState rapidAnimationState = new AnimationState();
    public final AnimationState heartofthenightAnimationState = new AnimationState();
    public final AnimationState teleportoutAnimationState = new AnimationState();
    public final AnimationState teleportinAnimationState = new AnimationState();
    public final AnimationState rangeSpellAttackAnimationState = new AnimationState();
    public final AnimationState wakeAnimationState = new AnimationState();
    public final AnimationState avadaAnimationState = new AnimationState();
    public final AnimationState quake1AnimationState = new AnimationState();
    public final AnimationState quake2AnimationState = new AnimationState();
    public final AnimationState slowSpellAnimationState = new AnimationState();
    public final AnimationState leechingSpellAnimationState = new AnimationState();
    public final AnimationState stabAnimationState = new AnimationState();
    public final AnimationState breatheAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    protected int mirrorSpellCool = 0;
    protected int soldierSpellCool = 0;
    protected int necromancerSpellCool = 0;
    protected int rangespellattackCool = 0;
    protected int thunderstormCool = 0;
    protected int desertPlaguesCool = 0;
    protected int avadaCool = 0;
    protected int quakespellcool = 0;
    protected int breathespellcool = 0;
    protected int scarletVexSummonCool = 0;
    protected int wartlingSpellCool = 0;
    protected int stuckTime = 0;
    protected int hitTimes = 0;
    protected double prevX;
    protected double prevY;
    protected double prevZ;
    protected double attackDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    protected Vec3 prevVecPos;
    protected int fireDamageSuppressTimer = 0;
    protected int teleportCooldown = 0;
    public int deathTime = 0;
    public float deathRotation = 0.0F;
    private boolean hasTriggeredSpawnQuote = false;
    private boolean hasTriggeredDiscoverEnemyQuote = false;

    public AbstractNamelessOne(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.damageCapManager = new DamageCapManager(this);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.LAVA, 8.0F);
        this.setAnimationState(HEART_OF_THE_NIGHT_ANIM);
        this.prevVecPos = this.position();
        this.setPersistenceRequired();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        return spawnGroupData;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, IDLE_ANIM);
        this.entityData.define(LEVEL, 0);
        this.entityData.define(IS_MIRROR, false);
        this.entityData.define(MIRROR_HIT_COUNT, 0);
        this.entityData.define(MIRROR_LIFETIME, 0);
        this.entityData.define(SELECTED_SPELL, 0);
        if (this.damageCapManager == null) {
            this.damageCapManager = new DamageCapManager(this);
        }
        this.damageCapManager.defineSynchedData();
    }

    public int getAnimationState() {
        return this.entityData.get(ANIM_STATE);
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIM_STATE, state);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        float f1 = (float) this.getNecroLevel();
        float size = 1.0F + Math.max(f1 * 0.15F, 0.0F);
        return 2.523F * size;
    }

    public int getNecroLevel() {
        return this.entityData.get(LEVEL);
    }

    public void setNecroLevel(int level) {
        int clampedLevel = Mth.clamp(level, 0, 2);
        this.entityData.set(LEVEL, clampedLevel);
        net.minecraft.world.entity.ai.attributes.AttributeInstance attributeInstance = this
                .getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null) {
            double newMaxHealth = com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneHealth.get() *
                    Math.max(0.5 * clampedLevel + 0.75F, 1);
            attributeInstance.setBaseValue(newMaxHealth);
            if (this.damageCapManager != null) {
                float maxProgress = (float) newMaxHealth;
                float healthRatio = this.getHealth() / this.getMaxHealth();
                float newHealth = maxProgress * healthRatio;
                this.damageCapManager.setMaxCombatProgress(maxProgress);
                this.damageCapManager.setCombatProgress(newHealth);
                this.setVanillaHealth(newHealth);
            }
        }
        this.reapplyPosition();
        this.refreshDimensions();
    }

    public void stopAllAnimations() {
        this.walkAnimationState.stop();
        this.attackAnimationState.stop();
        this.summonAnimationState.stop();
        this.spellAnimationState.stop();
        this.alertAnimationState.stop();
        this.flyAnimationState.stop();
        this.walk2AnimationState.stop();
        this.updrafAnimationState.stop();
        this.stormAnimationState.stop();
        this.storm2AnimationState.stop();
        this.rapidAnimationState.stop();
        this.teleportoutAnimationState.stop();
        this.teleportinAnimationState.stop();
        this.rangeSpellAttackAnimationState.stop();
        this.wakeAnimationState.stop();
        this.avadaAnimationState.stop();
        this.quake1AnimationState.stop();
        this.quake2AnimationState.stop();
        this.slowSpellAnimationState.stop();
        this.leechingSpellAnimationState.stop();
        this.stabAnimationState.stop();
        this.breatheAnimationState.stop();
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new java.util.ArrayList<>();
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.spellAnimationState);
        animationStates.add(this.alertAnimationState);
        animationStates.add(this.flyAnimationState);
        animationStates.add(this.walk2AnimationState);
        animationStates.add(this.updrafAnimationState);
        animationStates.add(this.stormAnimationState);
        animationStates.add(this.storm2AnimationState);
        animationStates.add(this.rapidAnimationState);
        animationStates.add(this.heartofthenightAnimationState);
        animationStates.add(this.teleportoutAnimationState);
        animationStates.add(this.teleportinAnimationState);
        animationStates.add(this.rangeSpellAttackAnimationState);
        animationStates.add(this.wakeAnimationState);
        animationStates.add(this.avadaAnimationState);
        animationStates.add(this.quake1AnimationState);
        animationStates.add(this.quake2AnimationState);
        animationStates.add(this.slowSpellAnimationState);
        animationStates.add(this.leechingSpellAnimationState);
        animationStates.add(this.stabAnimationState);
        animationStates.add(this.breatheAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    @Override
    public void onSyncedDataUpdated(net.minecraft.network.syncher.EntityDataAccessor<?> dataAccessor) {
        if (LEVEL.equals(dataAccessor)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
        }

        if (ANIM_STATE.equals(dataAccessor)) {
            if (this.level().isClientSide) {
                switch (this.getAnimationState()) {
                    case 0:
                        this.stopAllAnimations();
                        break;
                    case ATTACK_ANIM:
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.attackAnimationState);
                        break;
                    case SUMMON_ANIM:
                        this.summonAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.summonAnimationState);
                        break;
                    case SPELL_ANIM:
                        this.spellAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.spellAnimationState);
                        break;
                    case ALERT_ANIM:
                        this.alertAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.alertAnimationState);
                        break;
                    case FLY_ANIM:
                        this.flyAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.flyAnimationState);
                        break;
                    case WALK_ANIM:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.walkAnimationState);
                        break;
                    case WALK2_ANIM:
                        this.walk2AnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.walk2AnimationState);
                        break;
                    case UPDRAFT_ANIM:
                        this.updrafAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.updrafAnimationState);
                        break;
                    case STORM_ANIM:
                        this.stormAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.stormAnimationState);
                        break;
                    case STORM2_ANIM:
                        this.storm2AnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.storm2AnimationState);
                        break;
                    case RAPID_ANIM:
                        this.rapidAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.rapidAnimationState);
                        break;
                    case TELEPORTOUT_ANIM:
                        this.teleportoutAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.teleportoutAnimationState);
                        break;
                    case TELEPORTIN_ANIM:
                        this.teleportinAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.teleportinAnimationState);
                        break;
                    case RANGE_SPELL_ATTACK_ANIM:
                        this.rangeSpellAttackAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.rangeSpellAttackAnimationState);
                        break;
                    case WAKE_ANIM:
                        this.wakeAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.wakeAnimationState);
                        break;
                    case AVADA_ANIM:
                        this.avadaAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.avadaAnimationState);
                        break;
                    case QUAKE1_ANIM:
                        this.quake1AnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.quake1AnimationState);
                        break;
                    case QUAKE2_ANIM:
                        this.quake2AnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.quake2AnimationState);
                        break;
                    case SLOW_SPELL_ANIM:
                        this.slowSpellAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.slowSpellAnimationState);
                        break;
                    case LEECHING_SPELL_ANIM:
                        this.leechingSpellAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.leechingSpellAnimationState);
                        break;
                    case STAB_ANIM:
                        this.stabAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.stabAnimationState);
                        break;
                    case BREATHE_ANIM:
                        this.breatheAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.breatheAnimationState);
                        break;
                    case DEAD_ANIM:
                        this.deathAnimationState.startIfStopped(this.tickCount);
                        this.stopOtherAnimations(this.deathAnimationState);
                        break;
                    default:
                        break;
                }
            }
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    public void projectileGoal(int priority) {
        List<Goal> projectileGoals = java.util.Arrays.asList(
                // new ChargingSpellGoal(),
                new QuakeOneSpellGoal(),
                new RangeSpellAttackGoal());
        this.goalSelector.addGoal(priority, new RandomGoalWrapper(projectileGoals, this));
        this.goalSelector.addGoal(priority + 1, new NamelessOneRangedGoal(this, 1.0D, 20, 12.0F));
    }

    @Override
    public void summonSpells(int priority) {
        List<Goal> summonGoals = java.util.Arrays.asList(
                new SummonServantSpell(),
                new SummonSoldierGoal(),
                new NamelessOneSummonUndeadGoal(),
                new DesertPlaguesGoal(),
                new AvadaGoal(),
                new ThunderstormGoal());
        this.goalSelector.addGoal(priority, new RandomGoalWrapper(summonGoals, this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,
                        com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneHealth.get())
                .add(Attributes.ARMOR, com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS,
                        com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneArmorToughness.get())
                .add(Attributes.FOLLOW_RANGE,
                        com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE,
                        com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneDamage.get());
    }

    public void setConfigurableAttributes() {
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneHealth.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneArmor.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneArmorToughness.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneFollowRange.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25F);
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 1.0D);
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                com.k1sak1.goetyawaken.config.AttributesConfig.NamelessOneDamage.get());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        Random random = new Random();
        int choice = random.nextInt(4) + 1;
        switch (choice) {
            case 1:
                return ModSounds.NAMELESS_ONE_HURT_1.get();
            case 2:
                return ModSounds.NAMELESS_ONE_HURT_2.get();
            case 3:
                return ModSounds.NAMELESS_ONE_HURT_3.get();
            case 4:
                return ModSounds.NAMELESS_ONE_HURT_4.get();
            default:
                return ModSounds.NAMELESS_ONE_HURT_1.get();
        }

    }

    protected void trySummonScarletVex() {
        if (this.getHealth() <= this.getMaxHealth() * 0.5D) {
            if (this.scarletVexSummonCool <= 0 && this.tickCount % 50 == 0) {
                List<com.k1sak1.goetyawaken.common.entities.ally.undead.ScarletVex> existingScarletVexes = this.level()
                        .getEntitiesOfClass(
                                com.k1sak1.goetyawaken.common.entities.ally.undead.ScarletVex.class,
                                this.getBoundingBox().inflate(64.0D),
                                vex -> vex.isAlive() && this.equals(vex.getTrueOwner()));

                if (existingScarletVexes.size() >= 5) {
                    return;
                }

                LivingEntity target = this.getTarget();
                if (target != null && target.isAlive()) {
                    ServerLevel serverLevel = (ServerLevel) this.level();
                    int summonCount = this.random.nextInt(2) + 1;
                    List<BlockPos> spawnPositions = this.findValidSpawnPositionsAroundTarget(target, 8, summonCount);
                    if (!spawnPositions.isEmpty()) {
                        for (BlockPos spawnPos : spawnPositions) {
                            Vec3 spawnVec = new Vec3(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                            ColorUtil colorUtil = new ColorUtil(0xFF0000);
                            ServerParticleUtil.gatheringParticles(
                                    new GatherTrailParticle.Option(colorUtil, spawnVec.add(0, 1, 0)),
                                    this,
                                    serverLevel, 4);

                            com.k1sak1.goetyawaken.common.entities.ally.undead.ScarletVex scarletVex = new com.k1sak1.goetyawaken.common.entities.ally.undead.ScarletVex(
                                    com.k1sak1.goetyawaken.common.entities.ModEntityType.SCARLET_VEX.get(),
                                    this.level());
                            scarletVex.moveTo(spawnVec.x, spawnVec.y, spawnVec.z, this.getYRot(), this.getXRot());

                            scarletVex.setTrueOwner(this);
                            scarletVex.setLifespan(1200);
                            scarletVex.setHasLifespan(true);
                            int buffLevel = this.random.nextInt(4) + 3;
                            net.minecraft.world.effect.MobEffectInstance buffEffect = new net.minecraft.world.effect.MobEffectInstance(
                                    com.Polarice3.Goety.common.effects.GoetyEffects.BUFF.get(),
                                    -1,
                                    buffLevel - 1,
                                    false,
                                    false);
                            scarletVex.addEffect(buffEffect);
                            com.Polarice3.Goety.common.entities.util.SummonCircle summonCircle = new com.Polarice3.Goety.common.entities.util.SummonCircle(
                                    this.level(),
                                    spawnPos,
                                    scarletVex,
                                    true,
                                    true,
                                    this);
                            this.level().addFreshEntity(summonCircle);
                        }
                        this.scarletVexSummonCool = 200;
                    }
                }
            }
        }
    }

    protected List<BlockPos> findValidSpawnPositionsAroundTarget(LivingEntity target, int radius, int count) {
        List<BlockPos> validPositions = new java.util.ArrayList<>();
        BlockPos targetPos = target.blockPosition();

        for (int i = 0; i < count * 3 && validPositions.size() < count; i++) {
            int offsetX = this.random.nextInt(radius * 2 + 1) - radius;
            int offsetZ = this.random.nextInt(radius * 2 + 1) - radius;
            if (Math.abs(offsetX) < 2 && Math.abs(offsetZ) < 2) {
                continue;
            }

            BlockPos potentialPos = targetPos.offset(offsetX, 0, offsetZ);
            if (this.level().isEmptyBlock(potentialPos) &&
                    this.level().getBlockState(potentialPos.below()).isSolidRender(this.level(),
                            potentialPos.below())) {
                validPositions.add(potentialPos);
            }
        }

        return validPositions;
    }

    @Override
    public void avoidGoal(int priority) {

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(1, new SummonMirrorGoal());
        this.summonSpells(2);
        this.projectileGoal(4);
        super.registerGoals();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.NAMELESS_ONE_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        Random random = new Random();
        int choice = random.nextInt(4) + 1;
        switch (choice) {
            case 1:
                return ModSounds.NAMELESS_ONE_FLY_1.get();
            case 2:
                return ModSounds.NAMELESS_ONE_FLY_2.get();
            case 3:
                return ModSounds.NAMELESS_ONE_FLY_3.get();
            case 4:
                return ModSounds.NAMELESS_ONE_FLY_4.get();
            default:
                return ModSounds.NAMELESS_ONE_FLY_1.get();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        Random random = new Random();
        int choice = random.nextInt(6) + 1;
        switch (choice) {
            case 1:
                return ModSounds.NAMELESS_ONE_IDLE_1.get();
            case 2:
                return ModSounds.NAMELESS_ONE_IDLE_2.get();
            case 3:
                return ModSounds.NAMELESS_ONE_IDLE_3.get();
            case 4:
                return ModSounds.NAMELESS_ONE_LAUGH_SHORT_1.get();
            case 5:
                return ModSounds.NAMELESS_ONE_LAUGH_SHORT_2.get();
            case 6:
                return ModSounds.NAMELESS_ONE_LAUGH_SHORT_3.get();
            default:
                return ModSounds.NAMELESS_ONE_IDLE_1.get();
        }
    }

    public int xpReward() {
        return 444;
    }

    public class NamelessOneRangedGoal extends Goal {
        private final AbstractNamelessOne necromancer;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackInterval;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public NamelessOneRangedGoal(AbstractNamelessOne necromancer, double speed, int attackInterval,
                float attackRadius) {
            this.necromancer = necromancer;
            this.speedModifier = speed;
            this.attackInterval = attackInterval;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.necromancer.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return !this.necromancer.isSpellCasting() &&
                        this.necromancer.hasLineOfSight(livingentity);
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.target.isAlive()
                    && !this.necromancer.getNavigation().isDone() && !this.necromancer.isSpellCasting());
        }

        @Override
        public void start() {
            super.start();
            this.attackTime = -1;
            this.seeTime = 0;
        }

        @Override
        public void stop() {
            this.necromancer.setShooting(false);
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.target != null && !this.necromancer.isSpellCasting()) {
                double d0 = this.necromancer.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.necromancer.getSensing().hasLineOfSight(this.target);
                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }

                this.necromancer.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                if (this.necromancer.isShooting()) {
                    this.necromancer.getNavigation().stop();
                } else {
                    if (d0 < 16.0D) {
                        this.necromancer.setShooting(false);
                    } else if (this.seeTime >= 5) {
                        this.necromancer.getNavigation().stop();
                    } else {
                        this.necromancer.getNavigation().moveTo(this.target, this.speedModifier);
                    }
                }

                int speed = Mth.floor(Math.max(this.necromancer.getAttackSpeed(), 1.0F));
                int attackIntervalMin = this.attackInterval / speed;

                if (this.attackTime < 0) {
                    this.attackTime = attackIntervalMin;
                }

                --this.attackTime;
                if (this.attackTime <= 5) {
                    this.necromancer.setShooting(true);
                    double distanceToTarget = this.necromancer.distanceToSqr(this.target);
                    if (this.necromancer.isEasyMode()) {
                        if (this.necromancer.getAnimationState() != this.necromancer.ATTACK_ANIM) {
                            this.necromancer.setAnimationState(this.necromancer.ATTACK_ANIM);
                        }
                    } else {
                        if (distanceToTarget > 36.0D) {
                            if (this.necromancer.getAnimationState() != this.necromancer.ATTACK_ANIM) {
                                this.necromancer.setAnimationState(this.necromancer.ATTACK_ANIM);
                            }
                        } else {
                            if (this.necromancer.getAnimationState() != this.necromancer.STAB_ANIM) {
                                this.necromancer.setAnimationState(this.necromancer.STAB_ANIM);
                            }
                        }
                    }

                }
                if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    double distanceToTarget = this.necromancer.distanceToSqr(this.target);

                    if (this.necromancer.isEasyMode()) {
                        this.necromancer.performRangedAttack(this.target, f1);
                    } else {
                        if (distanceToTarget < 36.0D) {
                            this.necromancer.performStabAttack(this.target, f1);
                        } else {
                            this.necromancer.performRangedAttack(this.target, f1);
                        }
                    }
                    this.attackTime = attackIntervalMin;
                } else if (this.attackTime < 0) {
                    this.necromancer.setShooting(false);
                    this.attackTime = attackIntervalMin;
                }
            }
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        Vec3 targetVec = new Vec3(target.getX() - this.getX(),
                target.getEyeY() - this.getEyeY(),
                target.getZ() - this.getZ());
        targetVec = targetVec.normalize();

        int startAngle = 0;
        int endAngle = 0;

        if (this.isMirror()) {
            startAngle = 0;
            endAngle = 0;
        } else {
            float healthPercentage = this.getHealth() / this.getMaxHealth();
            float damageTakenPercentage = 1.0F - healthPercentage;
            float random = this.level().random.nextFloat();
            if (random < damageTakenPercentage) {
                startAngle = -2;
                endAngle = 2;
            } else if (random < damageTakenPercentage * 2) {
                startAngle = -1;
                endAngle = 1;
            } else {
                startAngle = 0;
                endAngle = 0;
            }
        }
        boolean enableHoming = false;
        if (!AbstractNamelessOne.this.isEasyMode()) {
            enableHoming = this.getHealth() < this.getMaxHealth() * 0.4F;
        } else {
            startAngle = 0;
            endAngle = 0;
        }

        for (int i = startAngle; i <= endAngle; i++) {
            float angleOffset = i * 10.0F;
            double yawRad = Math.toRadians(angleOffset);
            double rotatedX = targetVec.x * Math.cos(yawRad) - targetVec.z * Math.sin(yawRad);
            double rotatedZ = targetVec.x * Math.sin(yawRad) + targetVec.z * Math.cos(yawRad);
            Vec3 rotatedVec = new Vec3(rotatedX, targetVec.y, rotatedZ);
            com.k1sak1.goetyawaken.common.entities.projectiles.NecroBolt necroBolt = new com.k1sak1.goetyawaken.common.entities.projectiles.NecroBolt(
                    this, rotatedVec.x, rotatedVec.y, rotatedVec.z, this.level());

            necroBolt.setPos(this.getX(), this.getEyeY() - 0.3D, this.getZ());
            necroBolt.setOwner(this);
            float healthbasedamage = 0;
            if (target != null && !AbstractNamelessOne.this.isEasyMode()) {
                healthbasedamage = (float) (0.01 * target.getMaxHealth());
            }

            necroBolt.setExtraDamage(2 * this.getNecroLevel() + (float) (attackDamage) / 4 + healthbasedamage);
            necroBolt.setBoltSpeed((int) (0.9F + 0.2F * this.getNecroLevel()));
            if (enableHoming) {
                necroBolt.setHomingTarget(target);
            }

            if (this.level().addFreshEntity(necroBolt)) {
                if (i == startAngle) {
                    this.playSound(getRandomShootSound(), 1.0F, 1.0F);
                    this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    public void performStabAttack(LivingEntity target, float distanceFactor) {
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel) {
            java.util.List<Entity> targets = getTargets(serverLevel, this, 4.0D);
            float mobAttackDamage = (float) (attackDamage * 0.5);
            float deathDamage = (float) (attackDamage * 0.5);

            for (Entity targetEntity : targets) {
                if (targetEntity instanceof LivingEntity livingTarget) {
                    if (!com.Polarice3.Goety.utils.MobUtil.areAllies(this, livingTarget)) {
                        livingTarget.invulnerableTime = 0;
                        livingTarget.hurt(level.damageSources().mobAttack(this), mobAttackDamage);
                        float deathDamageWithPercentage = deathDamage + (livingTarget.getMaxHealth() * 0.04F);
                        livingTarget.invulnerableTime = 0;
                        livingTarget.hurt(
                                com.Polarice3.Goety.utils.ModDamageSource.deathCurse(this),
                                deathDamageWithPercentage);
                        livingTarget.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                com.Polarice3.Goety.common.effects.GoetyEffects.FREEZING.get(), 45 * 20, 0));
                        livingTarget.invulnerableTime = 0;
                    }
                }
            }
        }
    }

    public static java.util.List<Entity> getTargets(Level level, LivingEntity pSource, double pRange) {
        java.util.List<Entity> list = new java.util.ArrayList<>();
        net.minecraft.world.phys.Vec3 lookVec = pSource.getViewVector(1.0F);
        double[] lookRange = new double[] { lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange };
        java.util.List<Entity> possibleList = level.getEntities(pSource,
                pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]));

        java.util.function.Predicate<Entity> selector = entity -> entity.isPickable() && entity != pSource
                && entity instanceof LivingEntity;
        for (Entity hit : possibleList) {
            if (selector.test(hit)) {
                list.add(hit);
            }
        }
        return list;
    }

    @Override
    public void tick() {
        super.tick();
        this.damageCapManager.tick();
        if (!AbstractNamelessOne.this.isEasyMode()) {
            this.lowHealthSpellPushEntities();
            this.handleTeleportationLogic();
        }
        if (this.isOnFire() && !AbstractNamelessOne.this.isEasyMode()) {
            this.clearFire();
        }
        this.damageCapManager.validateCombatProgress();
        if (!this.level().isClientSide && !this.isMirror() && !AbstractNamelessOne.this.isEasyMode()) {
            if (this.scarletVexSummonCool > 0) {
                this.scarletVexSummonCool--;
            }
            this.trySummonScarletVex();
        }
        if (!this.level().isClientSide) {
            if (this.isHostile()) {
                if (!this.hasTriggeredSpawnQuote && this.tickCount % 20 == 0) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        List<net.minecraft.server.level.ServerPlayer> nearbyPlayers = serverLevel.getEntitiesOfClass(
                                net.minecraft.server.level.ServerPlayer.class,
                                this.getBoundingBox().inflate(64.0D));
                        if (!nearbyPlayers.isEmpty()) {
                            this.triggerSpawnQuote();
                            this.hasTriggeredSpawnQuote = true;
                        }
                    }
                }
            }
            if (this.tickCount % AttributesConfig.NamelessOneHealInterval.get() == 0) {
                double regenRate = AttributesConfig.NamelessOneHealAmount.get();
                if (regenRate > 0 && this.getHealth() < this.getMaxHealth() && this.fireDamageSuppressTimer <= 0) {
                    float healAmount = (float) (regenRate) * (this.getNecroLevel() + 1);
                    this.heal(healAmount);
                }
            }

            if (this.tickCount % 100 == 0) {
                this.healNearbyUndeadServants();
            }

            java.util.Collection<net.minecraft.world.effect.MobEffectInstance> effects = new java.util.ArrayList<>(
                    this.getActiveEffects());
            for (net.minecraft.world.effect.MobEffectInstance effect : effects) {
                if (!effect.getEffect().isBeneficial()) {
                    this.removeEffect(effect.getEffect());
                    if (this.level() instanceof ServerLevel serverLevel && this.wartlingSpellCool <= 0) {
                        Wartling wartling = new Wartling(ModEntityType.WARTLING.get(), this.level());
                        wartling.setTrueOwner(this);
                        wartling.setLimitedLife(MathHelper.secondsToTicks(9));
                        wartling.moveTo(this.blockPosition(), this.getYRot(), this.getXRot());
                        wartling.setStoredEffect(effect);
                        wartling.finalizeSpawn(serverLevel,
                                serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                                net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED, null, null);
                        this.level().addFreshEntity(wartling);
                        this.wartlingSpellCool = 60;
                    }
                }
            }

            this.checkchatQuotes();
        }

        if (this.fireDamageSuppressTimer > 0) {
            --this.fireDamageSuppressTimer;
        }
        if (this.mirrorSpellCool > 0) {
            --this.mirrorSpellCool;
        }
        if (this.soldierSpellCool > 0) {
            --this.soldierSpellCool;
        }
        if (this.necromancerSpellCool > 0) {
            --this.necromancerSpellCool;
        }
        if (this.wartlingSpellCool > 0) {
            --this.wartlingSpellCool;
        }
        if (this.rangespellattackCool > 0) {
            --this.rangespellattackCool;
        }
        if (this.thunderstormCool > 0) {
            --this.thunderstormCool;
        }
        if (this.desertPlaguesCool > 0) {
            --this.desertPlaguesCool;
        }
        if (this.avadaCool > 0) {
            --this.avadaCool;
        }
        if (this.quakespellcool > 0) {
            --this.quakespellcool;
        }
        if (this.breathespellcool > 0) {
            --this.breathespellcool;
        }
        if (this.isMirror() && !this.level().isClientSide()) {
            this.incrementMirrorLifetime();
        }

        if (this.level().isClientSide) {
            if (this.isAlive()) {
                if (this.isSpellCasting()) {
                    this.spellCastParticles();
                }
            }
            this.heartofthenightAnimationState.startIfStopped(this.tickCount);
            switch (this.getAnimationState()) {
                case IDLE_ANIM:
                    this.idleAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.idleAnimationState);
                    break;
                case WALK_ANIM:
                    this.walkAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.walkAnimationState);
                    break;
                case ATTACK_ANIM:
                    this.attackAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.attackAnimationState);
                    break;
                case SUMMON_ANIM:
                    this.summonAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.summonAnimationState);
                    break;
                case SPELL_ANIM:
                    this.spellAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.spellAnimationState);
                    break;
                case ALERT_ANIM:
                    this.alertAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.alertAnimationState);
                    break;
                case FLY_ANIM:
                    this.flyAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.flyAnimationState);
                    break;
                case WALK2_ANIM:
                    this.walk2AnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.walk2AnimationState);
                    break;
                case UPDRAFT_ANIM:
                    this.updrafAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.updrafAnimationState);
                    break;
                case STORM_ANIM:
                    this.stormAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.stormAnimationState);
                    break;
                case STORM2_ANIM:
                    this.storm2AnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.storm2AnimationState);
                    break;
                case RAPID_ANIM:
                    this.rapidAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.rapidAnimationState);
                    break;
                case TELEPORTOUT_ANIM:
                    this.teleportoutAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.teleportoutAnimationState);
                    break;
                case TELEPORTIN_ANIM:
                    this.teleportinAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.teleportinAnimationState);
                    break;
                case RANGE_SPELL_ATTACK_ANIM:
                    this.rangeSpellAttackAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.rangeSpellAttackAnimationState);
                    break;
                case WAKE_ANIM:
                    this.wakeAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.wakeAnimationState);
                    break;
                case AVADA_ANIM:
                    this.avadaAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.avadaAnimationState);
                    break;
                case QUAKE1_ANIM:
                    this.quake1AnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.quake1AnimationState);
                    break;
                case QUAKE2_ANIM:
                    this.quake2AnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.quake2AnimationState);
                    break;
                case SLOW_SPELL_ANIM:
                    this.slowSpellAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.slowSpellAnimationState);
                    break;
                case LEECHING_SPELL_ANIM:
                    this.leechingSpellAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.leechingSpellAnimationState);
                    break;
                case STAB_ANIM:
                    this.stabAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.stabAnimationState);
                    break;
                case BREATHE_ANIM:
                    this.breatheAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.breatheAnimationState);
                    break;
                case DEAD_ANIM:
                    this.deathAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.deathAnimationState);
                    break;
                default:
                    this.idleAnimationState.startIfStopped(this.tickCount);
                    this.stopOtherAnimations(this.idleAnimationState);
                    break;
            }
            if (this.isDeadOrDying()) {
                this.deathAnimationState.startIfStopped(this.tickCount);
                for (AnimationState animationState : new AnimationState[] {
                        this.idleAnimationState, this.walkAnimationState, this.attackAnimationState,
                        this.summonAnimationState, this.spellAnimationState, this.alertAnimationState,
                        this.flyAnimationState, this.walk2AnimationState, this.updrafAnimationState,
                        this.stormAnimationState, this.storm2AnimationState, this.rapidAnimationState,
                        this.teleportoutAnimationState,
                        this.teleportinAnimationState, this.rangeSpellAttackAnimationState,
                        this.wakeAnimationState, this.avadaAnimationState, this.quake1AnimationState,
                        this.quake2AnimationState, this.slowSpellAnimationState, this.leechingSpellAnimationState,
                        this.stabAnimationState,
                        this.breatheAnimationState
                }) {
                    animationState.stop();
                }
            }
        } else {
            if (!this.isShooting() && !this.isSpellCasting() &&
                    this.getAnimationState() != SUMMON_ANIM &&
                    this.getAnimationState() != SPELL_ANIM &&
                    this.getAnimationState() != STORM_ANIM &&
                    this.getAnimationState() != STORM2_ANIM &&
                    this.getAnimationState() != UPDRAFT_ANIM &&
                    this.getAnimationState() != RAPID_ANIM &&
                    this.getAnimationState() != RANGE_SPELL_ATTACK_ANIM &&
                    this.getAnimationState() != WAKE_ANIM &&
                    this.getAnimationState() != AVADA_ANIM &&
                    this.getAnimationState() != QUAKE1_ANIM &&
                    this.getAnimationState() != QUAKE2_ANIM &&
                    this.getAnimationState() != SLOW_SPELL_ANIM &&
                    this.getAnimationState() != LEECHING_SPELL_ANIM &&
                    this.getAnimationState() != ATTACK_ANIM &&
                    this.getAnimationState() != ALERT_ANIM &&
                    this.getAnimationState() != TELEPORTOUT_ANIM &&
                    this.getAnimationState() != TELEPORTIN_ANIM &&
                    this.getAnimationState() != STAB_ANIM &&
                    this.getAnimationState() != BREATHE_ANIM) {
                double speed = this.getDeltaMovement().horizontalDistance();
                if (speed > 0.01D) {
                    if (this.getAnimationState() != WALK2_ANIM) {
                        this.setAnimationState(WALK2_ANIM);
                    }
                } else {
                    if (this.getAnimationState() != IDLE_ANIM) {
                        this.setAnimationState(IDLE_ANIM);
                    }
                }
            }
            this.entityData.set(ANIM_STATE, this.getAnimationState());
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.isAlive()) {
                // ServerParticleUtil.windParticle(serverLevel, new ColorUtil(0x00FF00),
                // 0.5F + serverLevel.random.nextFloat() * 2.5F, 0.0F,
                // AbstractNamelessOne.this.getId(),
                // AbstractNamelessOne.this.position());
                ColorUtil colorUtil = new ColorUtil(0x00FF00);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.STATION_CULT_SPELL.get(), this,
                        colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);

                if (this.getTarget() == null || !this.getTarget().isAlive()) {
                    this.hasTriggeredDiscoverEnemyQuote = false;
                }
            }
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    @Override
    public boolean shouldChunkLoad() {
        return true;
    }

    public void stopAllAnimationsAndReset() {
        this.stopAllAnimations();
        this.setShooting(false);
        this.setSpellCasting(false);
    }

    private void healNearbyUndeadServants() {
        if (this.level() instanceof ServerLevel serverLevel) {
            List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(32.0D),
                    entity -> entity instanceof Summoned &&
                            MobUtil.areAllies(this, entity) &&
                            entity.getHealth() < entity.getMaxHealth());

            for (LivingEntity entity : nearbyEntities) {
                if (entity instanceof Summoned summoned && summoned.getTrueOwner() == this) {
                    entity.heal(1.0F);
                }
            }
        }
    }

    public void stopOtherAnimations(AnimationState exception) {
        for (AnimationState state : this.getAnimations()) {
            if (state != exception && state != this.heartofthenightAnimationState) {
                state.stop();
            }
        }
    }

    public Summoned getDefaultSummon() {
        int randomValue = this.level().random.nextInt(100);
        if (randomValue < 40) {
            return new BlackguardServant(
                    com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get(), this.level());
        } else if (randomValue < 80) {
            return new VanguardServant(
                    com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get(), this.level());
        } else {
            int remainingType = this.level().random.nextInt(3);
            switch (remainingType) {
                case 0:
                    int zombieType = this.level().random.nextInt(6);
                    switch (zombieType) {
                        case 0:
                            return new FrozenZombieServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.FROZEN_ZOMBIE_SERVANT.get(),
                                    this.level());
                        case 1:
                            return new JungleZombieServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.JUNGLE_ZOMBIE_SERVANT.get(),
                                    this.level());
                        case 2:
                            return new DrownedServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.DROWNED_SERVANT.get(),
                                    this.level());
                        case 3:
                            return new HuskServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.HUSK_SERVANT.get(), this.level());
                        case 4:
                            return new FrayedServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.FRAYED_SERVANT.get(),
                                    this.level());
                        case 5:
                            return new ZombieVindicatorServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.ZOMBIE_VINDICATOR_SERVANT.get(),
                                    this.level());
                    }
                case 1:
                    int skeletonType = this.level().random.nextInt(6);
                    switch (skeletonType) {
                        case 0:
                            return new MossySkeletonServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.MOSSY_SKELETON_SERVANT.get(),
                                    this.level());
                        case 1:
                            return new RattledServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.RATTLED_SERVANT.get(),
                                    this.level());
                        case 2:
                            return new SkeletonPillagerServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.SKELETON_PILLAGER_SERVANT.get(),
                                    this.level());
                        case 3:
                            return new StrayServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.STRAY_SERVANT.get(),
                                    this.level());
                        case 4:
                            return new SunkenSkeletonServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.SUNKEN_SKELETON_SERVANT.get(),
                                    this.level());
                        case 5:
                            return new ParchedServant(
                                    com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED_SERVANT.get(),
                                    this.level());
                    }
                case 2:
                    int wraithType = this.level().random.nextInt(3);
                    switch (wraithType) {
                        case 0:
                            return new BorderWraithServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.BORDER_WRAITH_SERVANT.get(),
                                    this.level());
                        case 1:
                            return new ReaperServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.REAPER_SERVANT.get(),
                                    this.level());
                        case 2:
                            return new WraithServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.WRAITH_SERVANT.get(),
                                    this.level());
                    }
                default:
                    return new BlackguardServant(
                            com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get(), this.level());
            }
        }
    }

    public Summoned getSummon() {
        Summoned summoned = getDefaultSummon();

        if (this.getSummonList().contains(com.Polarice3.Goety.common.entities.ModEntityType.PHANTOM_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.05F) {
                summoned = new PhantomServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.PHANTOM_SERVANT.get(), this.level());
            }
        }
        return summoned;
    }

    public class SummonServantSpell extends AbstractNecromancer.SummoningSpellGoal {
        @Override
        public boolean canUse() {
            if (AbstractNamelessOne.this.isMirror()) {
                return false;
            }
            Predicate<Entity> predicate = (entity) -> {
                boolean var10000;
                if (entity.isAlive() && entity instanceof IOwned owned) {
                    if (owned.getTrueOwner() == AbstractNamelessOne.this) {
                        var10000 = true;
                        return var10000;
                    }
                }

                var10000 = false;
                return var10000;
            };
            int i = AbstractNamelessOne.this.level()
                    .getEntitiesOfClass(LivingEntity.class,
                            AbstractNamelessOne.this.getBoundingBox().inflate(64.0, 16.0, 64.0), predicate)
                    .size();
            return super.canUse() && i < 8;
        }

        @Override
        public void start() {
            this.spellTime = this.getCastingTime();
            AbstractNamelessOne.this.setSpellCooldown(this.getCastingInterval());
            this.playPrepareSound();
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.SUMMON_ANIM);
            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setNecromancerSpellType(this.getNecromancerSpellType());
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime == 10) {
                if (this.getCastSound() != null) {
                    AbstractNamelessOne.this.playSound(this.getCastSound(), 1.0F, 1.0F);
                }

                AbstractNamelessOne.this.playSound(
                        (SoundEvent) getRandomLaughSound(), 2.0F, 0.05F);
                this.castSpell();
                AbstractNamelessOne.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            }

        }

        @Override
        protected void castSpell() {
            Level var2 = AbstractNamelessOne.this.level();
            int potency = AbstractNamelessOne.this.getNecroLevel();
            potency += AbstractNamelessOne.this.level().random.nextInt(1, 5);
            if (var2 instanceof ServerLevel serverLevel) {
                int summonCount = 2 + serverLevel.random.nextInt(6);
                for (int i1 = 0; i1 < summonCount; ++i1) {
                    Summoned summoned = AbstractNamelessOne.this.getSummon();
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractNamelessOne.this.blockPosition(),
                            summoned, serverLevel);
                    summoned.setTrueOwner(AbstractNamelessOne.this);
                    summoned.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summoned);
                    summoned.setPersistenceRequired();
                    if (!AbstractNamelessOne.this.isEasyMode()) {
                        this.buffSummon(AbstractNamelessOne.this, summoned, potency);
                    }
                    summoned.finalizeSpawn(serverLevel,
                            serverLevel.getCurrentDifficultyAt(AbstractNamelessOne.this.blockPosition()),
                            MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                    if (serverLevel.addFreshEntity(summoned)) {
                        SoundUtil.playNecromancerSummon(summoned);
                        ServerParticleUtil.summonUndeadParticles(serverLevel, summoned, new ColorUtil(16753408),
                                16753408, 16777070);
                    }
                }
            }

        }

        private void buffSummon(LivingEntity caster, LivingEntity summoned, int potency) {
            if (potency > 0) {
                int boost = Mth.clamp(potency - 1, 0, 10);
                summoned.addEffect(new MobEffectInstance((MobEffect) GoetyEffects.BUFF.get(),
                        EffectsUtil.infiniteEffect(), boost, false, false));
            }

            Level level = caster.level();
            if (level instanceof ServerLevel serverLevel) {
                int bonusCount = 1;
                if (caster instanceof AbstractNamelessOne namelessOne) {
                    bonusCount += namelessOne.getNecroLevel();
                    if (caster.getHealth() < caster.getMaxHealth() * 0.5F) {
                        bonusCount += 1;
                    }
                }

                java.util.List<MobEffect> beneficialEffects = new java.util.ArrayList<>();
                beneficialEffects.add(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST);
                beneficialEffects.add(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED);
                beneficialEffects.add(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE);
                beneficialEffects.add(com.Polarice3.Goety.common.effects.GoetyEffects.RALLYING.get());
                beneficialEffects.add(com.Polarice3.Goety.common.effects.GoetyEffects.SHIELDING.get());
                beneficialEffects.add(com.Polarice3.Goety.common.effects.GoetyEffects.SWIRLING.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ECHO.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.COMMITTED.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.CRITICAL_HIT.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ENCHANTMENT_THORNS.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ENCHANTMENT_SHARPNESS.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.CHAINS.get());
                for (int i = 0; i < bonusCount && !beneficialEffects.isEmpty(); i++) {
                    MobEffect selectedEffect = beneficialEffects
                            .get(serverLevel.random.nextInt(beneficialEffects.size()));
                    int randomLevel = serverLevel.random.nextInt(2);
                    summoned.addEffect(new MobEffectInstance(selectedEffect, EffectsUtil.infiniteEffect(),
                            randomLevel, false, false, false));
                }
            }
        }

        protected int getCastingInterval() {
            return 300;
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }
    }

    public boolean isMirror() {
        return this.entityData.get(IS_MIRROR);
    }

    public void setMirror(boolean isMirror) {
        this.entityData.set(IS_MIRROR, isMirror);
    }

    public boolean isEasyMode() {
        return com.k1sak1.goetyawaken.Config.NAMELESS_ONE_EASY_MODE.get();
    }

    public int getMirrorHitCount() {
        return this.entityData.get(MIRROR_HIT_COUNT);
    }

    public void incrementMirrorHitCount() {
        int count = this.entityData.get(MIRROR_HIT_COUNT) + 1;
        this.entityData.set(MIRROR_HIT_COUNT, count);
        if (count >= 5) {
            this.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        }
    }

    public int getMirrorLifetime() {
        return this.entityData.get(MIRROR_LIFETIME);
    }

    public void resetHitTime() {
        if (this.level().isClientSide())
            return;
        this.hitTimes = 0;
    }

    public void increaseHitTime() {
        if (this.level().isClientSide())
            return;
        ++this.hitTimes;
    }

    public int getHitTimes() {
        return this.hitTimes;
    }

    public int hitTimeTeleport() {
        return 3;
    }

    public void incrementMirrorLifetime() {
        int lifetime = this.entityData.get(MIRROR_LIFETIME) + 1;
        this.entityData.set(MIRROR_LIFETIME, lifetime);
        if (!AbstractNamelessOne.this.isEasyMode()) {
            if (lifetime >= 1200) {
                this.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
        } else {
            if (lifetime >= 100) {
                this.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
        }

    }

    public class SummonMirrorGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            if (AbstractNamelessOne.this.isSpellCasting() || AbstractNamelessOne.this.isMirror()) {
                return false;
            }

            java.util.function.Predicate<Entity> predicate = entity -> entity.isAlive()
                    && entity instanceof AbstractNamelessOne namelessOne
                    && namelessOne.isMirror()
                    && entity != AbstractNamelessOne.this;

            int nearbyMirrors = AbstractNamelessOne.this.level()
                    .getEntitiesOfClass(AbstractNamelessOne.class,
                            AbstractNamelessOne.this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D), predicate)
                    .size();

            return nearbyMirrors <= 0 && AbstractNamelessOne.this.mirrorSpellCool <= 0
                    && AbstractNamelessOne.this.getTarget() != null
                    && AbstractNamelessOne.this.getTarget().isAlive();
        }

        public boolean canContinueToUse() {
            return this.spellTime >= 0;
        }

        @Override
        public void start() {
            this.spellTime = 60;
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.SPELL_ANIM);
            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD);
            AbstractNamelessOne.this.playSound(com.Polarice3.Goety.init.ModSounds.VANGUARD_SPELL.get(), 1.0F, 1.0F);
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.mirrorSpellCool = 1200;
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        @Override
        public void tick() {
            --this.spellTime;
            Level worldIn = AbstractNamelessOne.this.level();
            if (worldIn instanceof ServerLevel serverLevel) {
                if (this.spellTime > 5) {
                    double radius = AbstractNamelessOne.this.getBoundingBox().getSize() * 2.0F;
                    ColorUtil colorUtil = new ColorUtil(ChatFormatting.GREEN);
                    ServerParticleUtil.gatheringParticles(
                            new GatherTrailParticle.Option(colorUtil, AbstractNamelessOne.this.position().add(0, 1, 0)),
                            AbstractNamelessOne.this,
                            serverLevel, 4);
                    ServerParticleUtil.windParticle(serverLevel, colorUtil, (float) radius, 1.0F,
                            AbstractNamelessOne.this.getId(),
                            AbstractNamelessOne.this.position());
                }
            }
            if (this.spellTime == 5) {
                this.spawnMirrors();
            }
        }

        private void spawnMirrors() {
            Level level = AbstractNamelessOne.this.level();
            if (level instanceof ServerLevel serverLevel) {
                LivingEntity target = AbstractNamelessOne.this.getTarget();
                if (target == null) {
                    return;
                }
                int mirrorCount = 7;
                double radius = 8.0D;
                for (int i = 0; i < mirrorCount; ++i) {
                    double angle = (2 * Math.PI * i) / mirrorCount;
                    double x = target.getX() + Math.cos(angle) * radius;
                    double y = target.getY();
                    double z = target.getZ() + Math.sin(angle) * radius;
                    BlockPos pos = BlockPos.containing(x, y, z);
                    pos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                            pos);
                    if (level.noCollision(AbstractNamelessOne.this.getType().getAABB(
                            pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) {
                        AbstractNamelessOne mirror = (AbstractNamelessOne) AbstractNamelessOne.this.getType()
                                .create(level);
                        if (mirror != null) {
                            mirror.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                            mirror.setMirror(true);
                            mirror.setNecroLevel(AbstractNamelessOne.this.getNecroLevel());
                            mirror.setTarget(target);
                            mirror.setTrueOwner(AbstractNamelessOne.this);
                            mirror.quakespellcool = AbstractNamelessOne.this.random.nextInt(101);
                            mirror.rangespellattackCool = AbstractNamelessOne.this.random.nextInt(101);
                            mirror.breathespellcool = AbstractNamelessOne.this.random.nextInt(101);
                            if (serverLevel.addFreshEntity(mirror)) {
                                ServerParticleUtil.summonUndeadParticles(serverLevel, mirror,
                                        new ColorUtil(0x00FF00), 0x00FF00, 0x00FFFF);
                            }
                        }
                    }
                }
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    public class NamelessOneSummonUndeadGoal extends AbstractNecromancer.SummonUndeadGoal {
        @Override
        public void playLaughSound() {
            AbstractNamelessOne.this.playSound(getRandomLaughSound(), 2.0F,
                    0.05F);
        }

        @Override
        public boolean canUse() {
            if (AbstractNamelessOne.this.isMirror()) {
                return false;
            }
            return super.canUse();
        }

        public void start() {
            super.start();
            AbstractNamelessOne.this.triggerSummonServantQuote();
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.SUMMON_ANIM);
            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.ZOMBIE);
        }

        @Override
        public void stop() {
            super.stop();
            AbstractNamelessOne.this.setSpellCasting(false);
            if (!AbstractNamelessOne.this.isShooting() && !AbstractNamelessOne.this.isSpellCasting()) {
                AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            }
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        public void summonUndeadParticles(ServerLevel serverLevel, Entity entity) {
            ServerParticleUtil.summonUndeadParticles(serverLevel, entity, new ColorUtil(16753408), 16753408, 16777070);
        }
    }

    public void lowHealthSpellPushEntities() {
        if (this.level() instanceof ServerLevel serverLevel) {
            float healthPercentage = this.getHealth() / this.getMaxHealth();
            if (healthPercentage >= 0.5F) {
                return;
            }

            if (this.isSpellCasting()) {
                return;
            }

            float radius = 3.0f;
            for (Entity entity : serverLevel.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(radius))) {
                if (entity != this && (this.getVehicle() == null || this.getVehicle() != entity)) {
                    boolean flag;
                    if (!(entity instanceof LivingEntity)) {
                        if (entity instanceof net.minecraft.world.entity.projectile.AbstractArrow) {
                            net.minecraft.world.entity.projectile.AbstractArrow arrow = (net.minecraft.world.entity.projectile.AbstractArrow) entity;
                            if (arrow.getOwner() == null) {
                                flag = false;
                            } else {
                                if (com.Polarice3.Goety.utils.MobUtil.areAllies(AbstractNamelessOne.this,
                                        arrow.getOwner())) {
                                    flag = false;
                                } else {
                                    flag = true;
                                }
                            }
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = true;
                    }
                    if (flag && entity instanceof LivingEntity) {
                        if (com.Polarice3.Goety.utils.MobUtil.areAllies(AbstractNamelessOne.this, entity)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        Vec3 vec31 = new Vec3(this.getX(), this.getY(), this.getZ());
                        Vec3 vec32 = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                        double distance = vec31.distanceTo(vec32) + 0.1;
                        Vec3 vec33 = new Vec3(vec32.x - vec31.x, vec32.y - vec31.y, vec32.z - vec31.z);
                        com.Polarice3.Goety.utils.MobUtil.push(entity, vec33.x / radius / distance,
                                vec33.y / radius / distance, vec33.z / radius / distance);

                        if (entity instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
                            if (!arrow.onGround()) {
                                arrow.setOwner(null);
                                double d0 = arrow.getX() - this.getX();
                                double d1 = arrow.getY() - this.getY();
                                double d2 = arrow.getZ() - this.getZ();
                                double d3 = (double) Mth.sqrt((float) (d0 * d0 + d2 * d2));
                                arrow.shoot(d0, d1 + d3 * 0.2F, d2, 1.0F, 10.0F);
                            }
                        }
                    }
                }
            }
            ColorUtil color = new ColorUtil(16777215);
            ServerParticleUtil.windParticle(serverLevel, color, radius - 1.0F, -0.5F, AbstractNamelessOne.this.getId(),
                    AbstractNamelessOne.this.position());
            ServerParticleUtil.windParticle(serverLevel, color, radius - 1.0F, 1.0F, AbstractNamelessOne.this.getId(),
                    AbstractNamelessOne.this.position());
            ServerParticleUtil.windParticle(serverLevel, color, radius, 0.5F, AbstractNamelessOne.this.getId(),
                    AbstractNamelessOne.this.position());
        }
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(net.minecraft.world.damagesource.DamageTypes.GENERIC_KILL)
                && amount > this.getMaxHealth() * 5F
                && source.getEntity() == null) {
            this.remove(RemovalReason.KILLED);
            this.removeAllMinions();
            this.triggerDeathQuote();
            return false;
        }
        if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.FALL) || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.DRY_OUT) || source.is(DamageTypes.SWEET_BERRY_BUSH)) {
            return false;
        }
        if (source.is(com.Polarice3.Goety.utils.ModDamageSource.DEATH) ||
                (source.is(net.minecraft.world.damagesource.DamageTypes.INDIRECT_MAGIC) &&
                        source.getDirectEntity() instanceof com.Polarice3.Goety.common.entities.projectiles.NecroBolt)) {
            return false;
        }
        if (!this.level().isClientSide()) {
            this.increaseHitTime();
        }
        if (damageCapManager != null) {
            if (amount >= damageCapManager.getMaxAllowedDamage()) {
                amount = damageCapManager.getMaxAllowedDamage();
            }
        }
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingAttacker &&
                livingAttacker.getMobType() != MobType.UNDEAD) {
            this.triggerUndeadProtection(livingAttacker);
        }
        if (source.getEntity() == null) {
            amount *= 0.1F;
        }
        boolean canProceed = this.damageCapManager.handleHurt(source, amount);
        if (!canProceed) {
            return false;
        }

        if (!this.level().isClientSide()) {
            this.hurtTime = 10;
            this.hurtDuration = 10;
            this.playHurtSound(source);
            this.level().broadcastEntityEvent(this, (byte) 2);
        }

        boolean result = super.hurt(source, amount);

        if (!this.isEasyMode() && result && this.getHitTimes() >= this.hitTimeTeleport() && !this.isSpellCasting()) {
            this.teleport();
        }

        if (result && (source.is(net.minecraft.world.damagesource.DamageTypes.ON_FIRE) ||
                source.is(net.minecraft.world.damagesource.DamageTypes.IN_FIRE) ||
                source.is(net.minecraft.world.damagesource.DamageTypes.LAVA) ||
                source.is(net.minecraft.world.damagesource.DamageTypes.FIREBALL) ||
                source.is(net.minecraft.world.damagesource.DamageTypes.UNATTRIBUTED_FIREBALL))) {
            this.fireDamageSuppressTimer = 100;
        }

        if (result && source.getEntity() instanceof LivingEntity livingAttacker) {
            int soulEaterLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SOUL_EATER.get(),
                    livingAttacker);
            if (soulEaterLevel > 0) {
                this.fireDamageSuppressTimer = Math.min(this.fireDamageSuppressTimer + 30, 100);
            }
        }

        if (result && this.isMirror()) {
            this.incrementMirrorHitCount();
        } else if (result && amount > 4.0F && !this.isMirror()) {
            this.removeRandomMirror();
        }
        return result;
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        boolean canProceed = this.damageCapManager.handleActuallyHurt(source, amount);
        if (!canProceed) {
            return;
        }
        if (damageCapManager != null) {
            if (amount >= damageCapManager.getMaxAllowedDamage()) {
                amount = damageCapManager.getMaxAllowedDamage();
            }
        }
        if (source.is(net.minecraft.world.damagesource.DamageTypes.LIGHTNING_BOLT)) {
            amount = amount * 1.3F;
        }
        float maxAllowedDamage = this.getMaxHealth() * DamageCapManager.getDamageThresholdPercent();
        float cappedAmount = Math.min(amount, maxAllowedDamage);
        float resistedAmount = cappedAmount;
        float servantDamageReduction = this.calculateServantDamageReduction();
        resistedAmount = resistedAmount * (1.0F - servantDamageReduction);
        float healthBasedDamageReduction = this.calculateHealthBasedDamageReduction();
        resistedAmount = resistedAmount * (1.0F - healthBasedDamageReduction);

        if (source.is(net.minecraft.world.damagesource.DamageTypes.MOB_ATTACK) ||
                source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK) ||
                source.is(net.minecraft.world.damagesource.DamageTypes.MOB_ATTACK_NO_AGGRO)) {
            resistedAmount = resistedAmount * 0.75F;
        }
        if (source.is(net.minecraft.world.damagesource.DamageTypes.FREEZE)) {
            resistedAmount = resistedAmount * 0.5F;
        }
        this.damageCapManager.hurtFinal(source, resistedAmount);
        this.damageCapManager.setHurtCall(false);
    }

    public void setVanillaHealth(float health) {
        super.setHealth(health);
    }

    @Override
    public void setHealth(float health) {
        if (this.damageCapManager == null) {
            super.setHealth(health);
            return;
        }
        if (!this.damageCapManager.handleSetHealth(health)) {
            return;
        }
        if (this.level().isClientSide()) {
            this.damageCapManager.setCombatProgress(health);
            return;
        }
        this.damageCapManager.setCombatProgress(health);
    }

    @Override
    public float getHealth() {
        return getCombatProgress();
    }

    public float getCombatProgress() {
        if (this.damageCapManager == null) {
            return super.getHealth();
        }
        return this.damageCapManager.getCombatProgress();
    }

    public float getVanillaHealth() {
        return super.getHealth();
    }

    @Override
    public boolean isDeadOrDying() {
        if (this.damageCapManager == null) {
            return super.isDeadOrDying();
        }
        return this.damageCapManager.getCombatProgress() <= 0.0F;
    }

    @Override
    public boolean isAlive() {
        if (this.isRemoved()) {
            return false;
        }
        if (this.damageCapManager == null) {
            return super.isAlive();
        }
        return this.damageCapManager.getCombatProgress() > 0.0F;
    }

    @Override
    protected boolean isImmobile() {
        if (this.damageCapManager == null) {
            return super.isImmobile();
        }
        return this.damageCapManager.getCombatProgress() <= 0.0F;
    }

    public SynchedEntityData getEntityDataAccessor() {
        return this.entityData;
    }

    private void removeRandomMirror() {
        if (this.level() instanceof ServerLevel serverLevel) {
            java.util.List<AbstractNamelessOne> mirrors = serverLevel.getEntitiesOfClass(
                    AbstractNamelessOne.class,
                    this.getBoundingBox().inflate(64.0D, 32.0D, 64.0D),
                    entity -> entity.isMirror() && entity.isAlive());

            if (!mirrors.isEmpty()) {
                AbstractNamelessOne mirrorToRemove = mirrors.get(this.level().random.nextInt(mirrors.size()));
                mirrorToRemove.transferServantsToOwner();
                mirrorToRemove.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
        }
    }

    private void transferServantsToOwner() {
        if (this.level() instanceof ServerLevel serverLevel && this.isMirror()) {
            LivingEntity master = this.getTrueOwner();
            if (master != null) {
                List<LivingEntity> servants = serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        this.getBoundingBox().inflate(64.0D, 32.0D, 64.0D),
                        entity -> entity instanceof IOwned owned &&
                                owned.getTrueOwner() == this &&
                                entity != this);
                for (LivingEntity servant : servants) {
                    if (servant instanceof IOwned owned) {
                        owned.setTrueOwner(master);
                    }
                }
            }
        }
    }

    public void removeAllMinions() {
        if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
            List<LivingEntity> minions = serverLevel.getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(64.0D, 32.0D, 64.0D),
                    entity -> entity instanceof IOwned owned &&
                            owned.getTrueOwner() == this &&
                            entity != this &&
                            entity.isAlive());

            for (LivingEntity minion : minions) {
                minion.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void onRemovedFromWorld() {
        this.removeAllMinions();
        if (this.isMirror()) {
            this.transferServantsToOwner();
        }
        super.onRemovedFromWorld();
    }

    @Override
    public void remove(Entity.RemovalReason p_276115_) {
        this.removeAllMinions();
        super.remove(p_276115_);
    }

    @Override
    public void die(DamageSource pCause) {
        if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 10);
            this.deathRotation = this.getYRot();
            this.triggerDeathQuote();
        }
        this.setAnimationState(DEAD_ANIM);
        this.removeAllMinions();
        super.die(pCause);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime > 0) {
            if (this.getAnimationState() != this.DEAD_ANIM) {
                this.setAnimationState(DEAD_ANIM);
            }
        }

        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
        this.setYHeadRot(this.deathRotation);

        if (this.deathTime == 21) {
            if (!this.level().isClientSide()) {
                this.playSound(ModSounds.NAMELESS_ONE_LAUGH_SHORT_1.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        } else if (this.deathTime == 50) {
            if (!this.level().isClientSide()) {
                this.playSound(ModSounds.NAMELESS_ONE_LAUGH_LONG_1.get(), this.getSoundVolume(), this.getVoicePitch());
            }
        }

        if (this.deathTime >= 200) {
            if (!this.level().isClientSide()) {
                BlockPos chestPos = this.blockPosition();
                BlockState chestState = ModBlocks.NAMELESS_CHEST.get()
                        .defaultBlockState();
                this.createLootChest(chestState, chestPos, this.damageSources().generic());
            }
            this.remove(RemovalReason.KILLED);

        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return p_34192_.getEffect().isBeneficial() && super.canBeAffected(p_34192_);
    }

    @Override
    public boolean addEffect(MobEffectInstance pPotioneffect, @Nullable Entity entity) {
        if (entity == this) {
            return super.addEffect(pPotioneffect, entity);
        } else {
            return pPotioneffect.getEffect().isBeneficial() && super.addEffect(pPotioneffect, entity);
        }
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    protected void checkFallDamage(double p_20809_, boolean p_20810_, BlockState p_20811_, BlockPos p_20812_) {
    }

    protected float calculateServantDamageReduction() {
        float result = 0.0f;
        if (this.level().isClientSide()) {
            return result;
        }
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return result;
        }

        List<VanguardChampion> vanguardChampions = serverLevel.getEntitiesOfClass(
                VanguardChampion.class,
                this.getBoundingBox().inflate(48.0D, 16.0D, 48.0D),
                vanguard -> vanguard.isAlive() &&
                        vanguard.getTrueOwner() == this);
        result += vanguardChampions.size() * 0.2F;

        List<LivingEntity> nearbyServants = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D),
                entity -> entity instanceof IOwned owned &&
                        this.equals(owned.getTrueOwner()) &&
                        entity.isAlive() &&
                        entity != this);

        int servantCount = nearbyServants.size();
        result += servantCount * 0.04F;
        float damageReduction = Math.min(result, 0.75F);
        return damageReduction;
    }

    protected float calculateHealthBasedDamageReduction() {
        float healthPercentage = this.getHealth() / this.getMaxHealth();

        if (healthPercentage >= 0.5F) {
            return 0.0F;
        }
        float damageReduction = 1.0F - healthPercentage;
        return damageReduction / 2;
    }

    public net.minecraft.world.InteractionResult mobInteract(net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        if (!this.level().isClientSide) {
            net.minecraft.world.item.ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
                // if
                // (itemstack.is(com.k1sak1.goetyawaken.common.items.ModItems.PARCHED_NECROMANCER_SOUL_JAR.get()))
                // {
                // if (!itemstack.isEmpty()) {
                // if (!player.getAbilities().instabuild) {
                // itemstack.shrink(1);
                // }
                // if (this.getNecroLevel() < 2) {
                // this.setNecroLevel(this.getNecroLevel() + 1);
                // }
                // this.heal(this.getMaxHealth());
                // if (this.level() instanceof ServerLevel serverLevel) {
                // for (int i = 0; i < 7; ++i) {
                // double d0 = this.random.nextGaussian() * 0.02D;
                // double d1 = this.random.nextGaussian() * 0.02D;
                // double d2 = this.random.nextGaussian() * 0.02D;
                // serverLevel.sendParticles(
                // net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                // this.getRandomX(1.0D),
                // this.getRandomY() + 0.5D,
                // this.getRandomZ(1.0D),
                // 0, d0, d1, d2, 0.5F);
                // }
                // }
                // this.playSound(getRandomLaughSound(),
                // 2.0F, 0.05F);
                // return net.minecraft.world.InteractionResult.SUCCESS;
                // }
                // }

                if (!itemstack.is(com.Polarice3.Goety.common.items.ModItems.SOUL_JAR.get())) {
                    return super.mobInteract(player, hand);
                }

            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AnimState", this.getAnimationState());
        compound.putInt("NecroLevel", this.getNecroLevel());
        compound.putInt("MirrorSpellCool", this.mirrorSpellCool);
        compound.putInt("SoldierSpellCool", this.soldierSpellCool);
        compound.putInt("NecromancerSpellCool", this.necromancerSpellCool);
        compound.putInt("RangeSpellAttackCool", this.rangespellattackCool);
        compound.putInt("ThunderstormCool", this.thunderstormCool);
        compound.putInt("DesertPlaguesCool", this.desertPlaguesCool);
        compound.putInt("AvadaCool", this.avadaCool);
        compound.putInt("QuakeSpellCool", this.quakespellcool);
        compound.putInt("BreathSpellCool", this.breathespellcool);
        compound.putInt("WartlingSpellCool", this.wartlingSpellCool);
        compound.putInt("TeleportCooldown", this.teleportCooldown);
        compound.putBoolean("IsMirror", this.isMirror());
        compound.putInt("MirrorHitCount", this.getMirrorHitCount());
        compound.putInt("MirrorLifetime", this.getMirrorLifetime());
        compound.putBoolean("HasTriggeredSpawnQuote", this.hasTriggeredSpawnQuote);
        compound.putBoolean("HasTriggeredDiscoverEnemyQuote", this.hasTriggeredDiscoverEnemyQuote);
        compound.putFloat("CombatProgress", this.getCombatProgress());
        compound.putFloat("MaxCombatProgress", this.damageCapManager.getMaxCombatProgress());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAnimationState(compound.getInt("AnimState"));
        this.setNecroLevel(compound.getInt("NecroLevel"));
        if (compound.contains("MirrorSpellCool")) {
            this.mirrorSpellCool = compound.getInt("MirrorSpellCool");
        }
        if (compound.contains("SoldierSpellCool")) {
            this.soldierSpellCool = compound.getInt("SoldierSpellCool");
        }
        if (compound.contains("NecromancerSpellCool")) {
            this.necromancerSpellCool = compound.getInt("NecromancerSpellCool");
        }
        if (compound.contains("RangeSpellAttackCool")) {
            this.rangespellattackCool = compound.getInt("RangeSpellAttackCool");
        }
        if (compound.contains("ThunderstormCool")) {
            this.thunderstormCool = compound.getInt("ThunderstormCool");
        }
        if (compound.contains("DesertPlaguesCool")) {
            this.desertPlaguesCool = compound.getInt("DesertPlaguesCool");
        }
        if (compound.contains("AvadaCool")) {
            this.avadaCool = compound.getInt("AvadaCool");
        }
        if (compound.contains("QuakeSpellCool")) {
            this.quakespellcool = compound.getInt("QuakeSpellCool");
        }
        if (compound.contains("BreathSpellCool")) {
            this.breathespellcool = compound.getInt("BreathSpellCool");
        }
        if (compound.contains("WartlingSpellCool")) {
            this.wartlingSpellCool = compound.getInt("WartlingSpellCool");
        }
        if (compound.contains("TeleportCooldown")) {
            this.teleportCooldown = compound.getInt("TeleportCooldown");
        }
        if (compound.contains("IsMirror")) {
            this.setMirror(compound.getBoolean("IsMirror"));
        }
        if (compound.contains("MirrorHitCount")) {
            this.entityData.set(MIRROR_HIT_COUNT, compound.getInt("MirrorHitCount"));
        }
        if (compound.contains("MirrorLifetime")) {
            this.entityData.set(MIRROR_LIFETIME, compound.getInt("MirrorLifetime"));
        }

        if (compound.contains("HasTriggeredSpawnQuote")) {
            this.hasTriggeredSpawnQuote = compound.getBoolean("HasTriggeredSpawnQuote");
        }
        if (compound.contains("HasTriggeredDiscoverEnemyQuote")) {
            this.hasTriggeredDiscoverEnemyQuote = compound.getBoolean("HasTriggeredDiscoverEnemyQuote");
        }
        if (compound.contains("CombatProgress")) {
            float combatProgress = compound.getFloat("CombatProgress");
            this.damageCapManager.setCombatProgress(combatProgress);
            if (compound.contains("MaxCombatProgress")) {
                float maxCombatProgress = compound.getFloat("MaxCombatProgress");
                this.damageCapManager.setMaxCombatProgress(maxCombatProgress);
            }
            this.setVanillaHealth(combatProgress);
        }
    }

    private SoundEvent getRandomShootSound() {
        Random random = new Random();
        int choice = random.nextInt(3);
        switch (choice) {
            case 0:
                return ModSounds.NAMELESS_ONE_SHOOT_1.get();
            case 1:
                return ModSounds.NAMELESS_ONE_SHOOT_2.get();
            case 2:
                return ModSounds.NAMELESS_ONE_SHOOT_3.get();
            default:
                return ModSounds.NAMELESS_ONE_SHOOT_1.get();
        }
    }

    private SoundEvent getRandomLaughSound() {
        Random random = new Random();
        int choice = random.nextInt(6);
        switch (choice) {
            case 0:
                return ModSounds.NAMELESS_ONE_LAUGH_SHORT_1.get();
            case 1:
                return ModSounds.NAMELESS_ONE_LAUGH_SHORT_2.get();
            case 2:
                return ModSounds.NAMELESS_ONE_LAUGH_SHORT_3.get();
            case 3:
                return ModSounds.NAMELESS_ONE_LAUGH_LONG_1.get();
            case 4:
                return ModSounds.NAMELESS_ONE_LAUGH_LONG_2.get();
            case 5:
                return ModSounds.NAMELESS_ONE_LAUGH_LONG_3.get();
            default:
                return ModSounds.NAMELESS_ONE_LAUGH_SHORT_1.get();
        }
    }

    public class SummonSoldierGoal extends Goal {
        private int spelltick;

        @Override
        public boolean canUse() {
            if (AbstractNamelessOne.this.isMirror() || AbstractNamelessOne.this.soldierSpellCool > 0) {
                return false;
            }

            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned &&
                    owned.getTrueOwner() == AbstractNamelessOne.this;

            int servantCount = AbstractNamelessOne.this.level()
                    .getEntitiesOfClass(LivingEntity.class,
                            AbstractNamelessOne.this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D),
                            predicate)
                    .size();
            LivingEntity target = AbstractNamelessOne.this.getTarget();
            return servantCount < 7 && target != null
                    && target.isAlive()
                    && !AbstractNamelessOne.this.isShooting()
                    && !AbstractNamelessOne.this.isSpellCasting();
        }

        public boolean canContinueToUse() {
            return this.spelltick >= 0;
        }

        @Override
        public void start() {
            this.spelltick = 91;
            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.SLOW_SPELL_ANIM);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.ZOMBIE);
            AbstractNamelessOne.this.playSound(com.Polarice3.Goety.init.ModSounds.VANGUARD_SPELL.get(), 1.0F, 1.0F);
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractNamelessOne.this.soldierSpellCool = 600;
        }

        @Override
        public void tick() {
            --this.spelltick;

            Level worldIn = AbstractNamelessOne.this.level();
            if (worldIn instanceof ServerLevel serverLevel) {
                if (this.spelltick > 8) {
                    double radius = AbstractNamelessOne.this.getBoundingBox().getSize() * 2.0F;
                    ColorUtil colorUtil = new ColorUtil(ChatFormatting.GREEN);
                    ServerParticleUtil.gatheringParticles(
                            new GatherTrailParticle.Option(colorUtil, AbstractNamelessOne.this.position().add(0, 1, 0)),
                            AbstractNamelessOne.this,
                            serverLevel, 4);
                    ServerParticleUtil.windParticle(serverLevel, colorUtil, (float) radius, 1.0F,
                            AbstractNamelessOne.this.getId(),
                            AbstractNamelessOne.this.position());
                } else if (this.spelltick == 8) {
                    this.SummonSoldierSpell(serverLevel, AbstractNamelessOne.this);
                }
            }
        }

        private void SummonSoldierSpell(ServerLevel worldIn, AbstractNamelessOne caster) {
            int vanguardChampionCount = 0;
            for (Entity entity : caster.level().getEntitiesOfClass(
                    VanguardChampion.class,
                    caster.getBoundingBox().inflate(64.0D, 16.0D, 64.0D))) {
                if (entity.isAlive() && entity instanceof IOwned) {
                    IOwned owned = (IOwned) entity;
                    if (owned.getTrueOwner() == caster) {
                        vanguardChampionCount++;
                    }
                }
            }

            int potency = caster.getNecroLevel();
            net.minecraft.world.Difficulty difficulty = worldIn.getDifficulty();
            int difficultyBonus = switch (difficulty) {
                case PEACEFUL -> 0;
                case EASY -> 1;
                case NORMAL -> 2;
                case HARD -> 3;
            };
            potency += difficultyBonus + 2;
            Vec3 vec3 = caster.position();
            Direction direction = caster.getDirection();
            double stepX = direction.getStepX();
            double stepZ = direction.getStepZ();
            int summonType;
            if (!AbstractNamelessOne.this.isEasyMode()) {
                summonType = vanguardChampionCount >= 3 ? worldIn.random.nextInt(1) : 2;
            } else {
                summonType = 1;
            }

            for (int i1 = -3; i1 <= 3; ++i1) {
                if (summonType == 2 && (i1 != -2 && i1 != 2)) {
                    continue;
                }
                Summoned summonedentity;
                switch (summonType) {
                    case 0:
                        summonedentity = new BlackguardServant(
                                com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get(), worldIn);
                        break;
                    case 1:
                        summonedentity = new VanguardServant(
                                com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get(), worldIn);
                        break;
                    default:
                        summonedentity = new VanguardChampion(
                                com.k1sak1.goetyawaken.common.entities.ModEntityType.VANGUARD_CHAMPION.get(), worldIn);
                        break;
                }

                summonedentity.setTrueOwner(caster);
                Vec3 vec32 = new Vec3(((2 * stepX) + (i1 * stepZ)) + vec3.x(), vec3.y(),
                        ((2 * stepZ) + (i1 * stepX)) + vec3.z());

                if (!worldIn.noCollision(summonedentity, summonedentity.getBoundingBox().move(vec32))) {
                    vec32 = Vec3.atCenterOf(
                            BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, caster.level()));
                }
                summonedentity.setPos(vec32);
                MobUtil.moveDownToGround(summonedentity);
                summonedentity.setPersistenceRequired();

                summonedentity.finalizeSpawn(worldIn,
                        caster.level().getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, summonedentity, potency, summonType);
                summonedentity.setYHeadRot(caster.getYHeadRot());
                summonedentity.setYRot(caster.getYRot());
                this.setTarget(caster, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    worldIn.sendParticles(com.Polarice3.Goety.client.particles.ModParticleTypes.LICH.get(),
                            summonedentity.getX(), summonedentity.getY(), summonedentity.getZ(), 1, 0, 0, 0, 0.0F);
                    ServerParticleUtil.summonPowerfulUndeadParticles(worldIn, summonedentity);
                    this.playSound(worldIn, summonedentity, com.Polarice3.Goety.init.ModSounds.SOUL_EXPLODE.get(),
                            0.25F + (worldIn.random.nextFloat() / 2.0F), 1.0F);
                    this.playSound(worldIn, summonedentity, net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                            0.25F + (worldIn.random.nextFloat() / 2.0F), 1.0F);
                }
            }
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return AbstractNecromancer.NecromancerSpellType.ZOMBIE;
        }

        public void playSound(ServerLevel worldIn, Entity entity, SoundEvent sound, float volume, float pitch) {
            worldIn.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound,
                    net.minecraft.sounds.SoundSource.HOSTILE, volume, pitch);
        }

        private void buffSummon(LivingEntity caster, LivingEntity summoned, int potency, int summonType) {
            if (potency > 0) {
                int boost = Mth.clamp(potency - 1, 0, 10);
                summoned.addEffect(new MobEffectInstance((MobEffect) GoetyEffects.BUFF.get(),
                        EffectsUtil.infiniteEffect(), boost, false, false));
            }

            if (summonType == 2
                    && summoned instanceof com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion vanguardChampion) {
                int currentPoints = vanguardChampion.getProtectionPoints();
                if (!AbstractNamelessOne.this.isEasyMode()) {
                    vanguardChampion.setProtectionPoints(currentPoints + 4);
                }
                if (vanguardChampion instanceof IAncientGlint glint) {
                    glint.setAncientGlint(true);
                    glint.setGlintTextureType("enchant");
                }
            }
            Level level = caster.level();
            if (level instanceof ServerLevel serverLevel) {
                int bonusCount = 1;
                if (caster instanceof AbstractNamelessOne namelessOne) {
                    bonusCount += namelessOne.getNecroLevel();
                    if (caster.getHealth() < caster.getMaxHealth() * 0.5F) {
                        bonusCount += 1;
                    }
                }

                java.util.List<MobEffect> beneficialEffects = new java.util.ArrayList<>();
                beneficialEffects.add(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST);
                beneficialEffects.add(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED);
                beneficialEffects.add(com.Polarice3.Goety.common.effects.GoetyEffects.RALLYING.get());
                beneficialEffects.add(com.Polarice3.Goety.common.effects.GoetyEffects.SWIRLING.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ECHO.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.COMMITTED.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.CRITICAL_HIT.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ENCHANTMENT_THORNS.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ENCHANTMENT_SHARPNESS.get());
                beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.CHAINS.get());

                for (int i = 0; i < bonusCount && !beneficialEffects.isEmpty(); i++) {
                    MobEffect selectedEffect = beneficialEffects
                            .get(serverLevel.random.nextInt(beneficialEffects.size()));
                    int randomLevel = serverLevel.random.nextInt(2);
                    summoned.addEffect(new MobEffectInstance(selectedEffect, EffectsUtil.infiniteEffect(),
                            randomLevel, false, false, false));
                }
            }
        }

        private void setTarget(AbstractNamelessOne caster, Summoned summoned) {
            if (caster.getTarget() != null) {
                summoned.setTarget(caster.getTarget());
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public class SummonNecromancerGoal extends Goal {
        private int spelltick;

        @Override
        public boolean canUse() {
            if (AbstractNamelessOne.this.isMirror() || AbstractNamelessOne.this.necromancerSpellCool > 0) {
                return false;
            }

            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned &&
                    owned.getTrueOwner() == AbstractNamelessOne.this;

            int servantCount = AbstractNamelessOne.this.level()
                    .getEntitiesOfClass(LivingEntity.class,
                            AbstractNamelessOne.this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D),
                            predicate)
                    .size();
            LivingEntity target = AbstractNamelessOne.this.getTarget();
            return servantCount < 2 && target != null
                    && target.isAlive()
                    && !AbstractNamelessOne.this.isShooting()
                    && !AbstractNamelessOne.this.isSpellCasting();
        }

        public boolean canContinueToUse() {
            return this.spelltick >= 0;
        }

        @Override
        public void start() {
            this.spelltick = 60;
            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.SPELL_ANIM);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.ZOMBIE);
            AbstractNamelessOne.this.playSound(com.Polarice3.Goety.init.ModSounds.VANGUARD_SPELL.get(), 1.0F, 1.0F);
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractNamelessOne.this.necromancerSpellCool = 4800;
        }

        @Override
        public void tick() {
            --this.spelltick;

            Level worldIn = AbstractNamelessOne.this.level();
            if (worldIn instanceof ServerLevel serverLevel) {
                if (this.spelltick > 5) {
                    double radius = AbstractNamelessOne.this.getBoundingBox().getSize() * 2.0F;
                    ColorUtil colorUtil = new ColorUtil(ChatFormatting.DARK_PURPLE);
                    ServerParticleUtil.gatheringParticles(
                            new GatherTrailParticle.Option(colorUtil, AbstractNamelessOne.this.position().add(0, 1, 0)),
                            AbstractNamelessOne.this,
                            serverLevel, 4);
                    ServerParticleUtil.windParticle(serverLevel, colorUtil, (float) radius * 2, 1.0F,
                            AbstractNamelessOne.this.getId(),
                            AbstractNamelessOne.this.position());
                } else if (this.spelltick == 5) {
                    this.SummonNecromancerSpell(serverLevel, AbstractNamelessOne.this);
                }
            }
        }

        private void SummonNecromancerSpell(ServerLevel worldIn, AbstractNamelessOne caster) {
            net.minecraft.world.Difficulty difficulty = worldIn.getDifficulty();
            int summonCount = 1;
            if (difficulty == net.minecraft.world.Difficulty.NORMAL) {
                summonCount = worldIn.random.nextFloat() < 0.5F ? 1 : 2;
            } else if (difficulty == net.minecraft.world.Difficulty.HARD) {
                summonCount = 2;
            }

            EntityType<? extends com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer> servantType;
            int randomChoice = worldIn.random.nextInt(3);
            switch (randomChoice) {
                case 0 -> servantType = com.Polarice3.Goety.common.entities.ModEntityType.NECROMANCER_SERVANT.get();
                case 1 ->
                    servantType = com.Polarice3.Goety.common.entities.ModEntityType.MOSSY_NECROMANCER_SERVANT.get();
                default ->
                    servantType = com.Polarice3.Goety.common.entities.ModEntityType.NECROMANCER_SERVANT.get();
            }

            Vec3 vec3 = caster.position();
            Direction direction = caster.getDirection();
            double stepX = direction.getStepX();
            double stepZ = direction.getStepZ();

            for (int i = 0; i < summonCount; i++) {
                com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer summonedEntity = servantType
                        .create(worldIn);
                if (summonedEntity != null) {
                    summonedEntity.setTrueOwner(caster);
                    Vec3 spawnPos = new Vec3(
                            ((2 * stepX) + (i * stepZ)) + vec3.x(),
                            vec3.y(),
                            ((2 * stepZ) + (i * stepX)) + vec3.z());
                    if (!worldIn.noCollision(summonedEntity, summonedEntity.getBoundingBox().move(spawnPos))) {
                        spawnPos = Vec3.atCenterOf(
                                BlockFinder.SummonRadius(caster.blockPosition(), summonedEntity, caster.level()));
                    }

                    summonedEntity.setPos(spawnPos);
                    MobUtil.moveDownToGround(summonedEntity);
                    summonedEntity.setPersistenceRequired();
                    int necroLevel = 0;
                    if (difficulty == net.minecraft.world.Difficulty.NORMAL) {
                        float rand = worldIn.random.nextFloat();
                        if (rand < 0.2F) {
                            necroLevel = 1;
                        } else if (rand < 0.3F) {
                            necroLevel = 2;
                        }
                    } else if (difficulty == net.minecraft.world.Difficulty.HARD) {
                        float rand = worldIn.random.nextFloat();
                        if (rand < 0.4F) {
                            necroLevel = 1;
                        } else if (rand < 0.6F) {
                            necroLevel = 2;
                        }
                    }
                    summonedEntity.setNecroLevel(necroLevel);
                    summonedEntity.finalizeSpawn(
                            worldIn,
                            caster.level().getCurrentDifficultyAt(caster.blockPosition()),
                            MobSpawnType.MOB_SUMMONED,
                            null,
                            null);

                    summonedEntity.setYHeadRot(caster.getYHeadRot());
                    summonedEntity.setYRot(caster.getYRot());
                    this.setTarget(caster, summonedEntity);
                    if (worldIn.addFreshEntity(summonedEntity)) {
                        worldIn.sendParticles(
                                com.Polarice3.Goety.client.particles.ModParticleTypes.LICH.get(),
                                summonedEntity.getX(),
                                summonedEntity.getY(),
                                summonedEntity.getZ(),
                                1, 0, 0, 0, 0.0F);
                        ServerParticleUtil.summonPowerfulUndeadParticles(worldIn, summonedEntity);
                        this.playSound(
                                worldIn,
                                summonedEntity,
                                com.Polarice3.Goety.init.ModSounds.SOUL_EXPLODE.get(),
                                0.25F + (worldIn.random.nextFloat() / 2.0F),
                                1.0F);
                        this.playSound(
                                worldIn,
                                summonedEntity,
                                net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                                0.25F + (worldIn.random.nextFloat() / 2.0F),
                                1.0F);
                    }
                }
            }
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return AbstractNecromancer.NecromancerSpellType.ZOMBIE;
        }

        public void playSound(ServerLevel worldIn, Entity entity, SoundEvent sound, float volume, float pitch) {
            worldIn.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound,
                    net.minecraft.sounds.SoundSource.HOSTILE, volume, pitch);
        }

        private void setTarget(AbstractNamelessOne caster, Summoned summoned) {
            if (caster.getTarget() != null) {
                summoned.setTarget(caster.getTarget());
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return !this.isDeadOrDying();
    }

    private void triggerUndeadProtection(LivingEntity attacker) {
        if (this.level() instanceof ServerLevel serverLevel) {
            List<Mob> nearbyUndead = serverLevel.getEntitiesOfClass(
                    Mob.class,
                    this.getBoundingBox().inflate(16.0D, 8.0D, 16.0D),
                    entity -> this.isNamelessOneFriendlyUndead(entity));
            for (Mob undead : nearbyUndead) {
                if (undead != attacker && undead.getTarget() != this) {
                    if (undead instanceof IOwned owned) {
                        LivingEntity owner = owned.getTrueOwner();
                        if (owner == null) {
                            undead.setLastHurtByMob(attacker);
                            undead.setTarget(attacker);
                        }
                    } else {
                        undead.setLastHurtByMob(attacker);
                        undead.setTarget(attacker);
                    }
                }
            }
        }
    }

    private boolean isNamelessOneFriendlyUndead(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return false;
        }
        if (mob.getMobType() != MobType.UNDEAD) {
            return false;
        }
        if (mob.getType().is(com.Polarice3.Goety.init.ModTags.EntityTypes.MINI_BOSSES) ||
                mob.getType().is(net.minecraftforge.common.Tags.EntityTypes.BOSSES)) {
            return false;
        }
        if (mob.getMaxHealth() > 50.0F) {
            return false;
        }
        if (entity instanceof IOwned owned) {
            LivingEntity owner = owned.getTrueOwner();
            if (owner != null && owner != this) {
                return false;
            }
        }
        return true;
    }

    public DamageCapManager getDamageCapManager() {
        return this.damageCapManager;
    }

    public class RangeSpellAttackGoal extends Goal {
        private int spelltick;

        @Override
        public boolean canUse() {
            return !AbstractNamelessOne.this.isSpellCasting()
                    && !AbstractNamelessOne.this.isShooting()
                    && AbstractNamelessOne.this.getTarget() != null
                    && AbstractNamelessOne.this.getTarget().isAlive()
                    && !AbstractNamelessOne.this.isEasyMode()
                    && AbstractNamelessOne.this.rangespellattackCool <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.spelltick >= 0;
        }

        @Override
        public void start() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.RANGE_SPELL_ATTACK_ANIM);
            AbstractNamelessOne.this.setSpellCasting(true);
            this.spelltick = 20;
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractNamelessOne.this.rangespellattackCool = 40;
        }

        @Override
        public void tick() {
            --this.spelltick;
            if (this.spelltick == 10) {
                LivingEntity target = AbstractNamelessOne.this.getTarget();
                if (target != null && target.isAlive()) {
                    this.executeRandomSpell();
                }
            }
        }

        private void executeRandomSpell() {
            Level level = AbstractNamelessOne.this.level();
            if (!(level instanceof ServerLevel serverLevel))
                return;

            LivingEntity target = AbstractNamelessOne.this.getTarget();
            if (target == null || !target.isAlive())
                return;

            int spellChoice = serverLevel.random.nextInt(5) + 1;
            int potency = 5;

            Vec3 eyePos = AbstractNamelessOne.this.getEyePosition();
            Vec3 targetPos = target.position();
            double d0 = targetPos.x - eyePos.x;
            double d1 = targetPos.y + target.getBbHeight() * 0.5D - eyePos.y;
            double d2 = targetPos.z - eyePos.z;
            double distance = Math.sqrt(d0 * d0 + d2 * d2);

            switch (spellChoice) {
                case 1:
                    com.Polarice3.Goety.common.entities.projectiles.MagicBolt magicBolt = new com.Polarice3.Goety.common.entities.projectiles.MagicBolt(
                            serverLevel, AbstractNamelessOne.this, d0, d1, d2);
                    magicBolt.setPos(eyePos.x, eyePos.y, eyePos.z);
                    magicBolt.setExtraDamage(potency + (float) attackDamage);
                    magicBolt.setExtraDuration(3);
                    serverLevel.addFreshEntity(magicBolt);
                    break;

                case 2:
                    com.Polarice3.Goety.common.entities.projectiles.RazorWind razorWind = new com.Polarice3.Goety.common.entities.projectiles.RazorWind(
                            serverLevel, AbstractNamelessOne.this);
                    razorWind.setPos(eyePos.x, eyePos.y - 0.3D, eyePos.z);
                    razorWind.shoot(d0, d1, d2, 1.6F, 1.0F);
                    razorWind.setDamage(2.0F + potency);
                    razorWind.setRadius(0.3F + 3.0F);
                    serverLevel.addFreshEntity(razorWind);
                    break;

                case 3:
                    for (int i = -1; i <= 1; i++) {
                        double angleOffset = Math.toRadians(i * 15.0);
                        double cos = Math.cos(angleOffset);
                        double sin = Math.sin(angleOffset);
                        double rotatedD0 = d0 * cos - d2 * sin;
                        double rotatedD2 = d0 * sin + d2 * cos;

                        com.Polarice3.Goety.common.entities.projectiles.IceSpear iceSpear = new com.Polarice3.Goety.common.entities.projectiles.IceSpear(
                                AbstractNamelessOne.this, serverLevel);
                        iceSpear.setPos(eyePos.x, eyePos.y, eyePos.z);
                        iceSpear.shoot(rotatedD0, d1 + distance * 0.2F, rotatedD2, 1.6F, 1.0F);
                        iceSpear.setExtraDamage(potency + (float) attackDamage);
                        serverLevel.addFreshEntity(iceSpear);
                    }
                    break;

                case 4:
                    for (int i = -2; i <= 2; i++) {
                        double angleOffset = Math.toRadians(i * 15.0);
                        double cos = Math.cos(angleOffset);
                        double sin = Math.sin(angleOffset);
                        double rotatedD0 = d0 * cos - d2 * sin;
                        double rotatedD2 = d0 * sin + d2 * cos;

                        com.Polarice3.Goety.common.entities.projectiles.PoisonBolt poisonBolt = new com.Polarice3.Goety.common.entities.projectiles.PoisonBolt(
                                eyePos.x, eyePos.y, eyePos.z, rotatedD0, d1, rotatedD2, serverLevel);
                        poisonBolt.setOwner(AbstractNamelessOne.this);
                        poisonBolt.setExtraDamage(potency + (float) attackDamage);
                        serverLevel.addFreshEntity(poisonBolt);
                    }
                    break;

                case 5:
                    com.Polarice3.Goety.common.entities.projectiles.ElectroOrb electroOrb = new com.Polarice3.Goety.common.entities.projectiles.ElectroOrb(
                            serverLevel, AbstractNamelessOne.this, target);
                    electroOrb.setPos(eyePos.x, eyePos.y, eyePos.z);
                    electroOrb.setExtraDamage(potency + (float) attackDamage);
                    serverLevel.addFreshEntity(electroOrb);
                    break;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE;
        }
    }

    public class ThunderstormGoal extends Goal {
        private int spelltick;
        private boolean useUpdraftVariant = false;

        @Override
        public boolean canUse() {
            if (AbstractNamelessOne.this.isMirror()) {
                return false;
            }
            return !AbstractNamelessOne.this.isSpellCasting()
                    && AbstractNamelessOne.this.getTarget() != null
                    && AbstractNamelessOne.this.getTarget().isAlive()
                    && AbstractNamelessOne.this.thunderstormCool <= 0
                    && !AbstractNamelessOne.this.isEasyMode()
                    && !AbstractNamelessOne.this.isShooting();
        }

        @Override
        public boolean canContinueToUse() {
            return this.spelltick >= 0;
        }

        @Override
        public void start() {
            Level level = AbstractNamelessOne.this.level();
            if (level instanceof ServerLevel serverLevel) {
                this.useUpdraftVariant = serverLevel.getRandom().nextBoolean();
            } else {
                this.useUpdraftVariant = false;
            }

            if (this.useUpdraftVariant) {
                AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.UPDRAFT_ANIM);
                this.spelltick = 65;
            } else {
                AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.STORM2_ANIM);
                this.spelltick = 50;
            }

            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD);
            if (AbstractNamelessOne.this.getLaughSound() != null) {
                AbstractNamelessOne.this.playSound(AbstractNamelessOne.this.getLaughSound(),
                        2.0F, AbstractNamelessOne.this.getVoicePitch());
            }
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractNamelessOne.this.thunderstormCool = 350;
        }

        @Override
        public void tick() {
            --this.spelltick;
            Level level = AbstractNamelessOne.this.level();
            if (!(level instanceof ServerLevel serverLevel))
                return;

            if (this.useUpdraftVariant) {
                if (this.spelltick <= 52) {
                    if (this.spelltick % 2 == 0) {
                        int elapsedTicks = 65 - this.spelltick;
                        int tickIndex = (elapsedTicks - 13) / 2;
                        float baseRadius = 2.0f;
                        float radius = baseRadius + (tickIndex * 3.5f);
                        float targetArcLength = (float) (2 * Math.PI * baseRadius);
                        int count = Math.max(3, (int) Math.ceil(2 * Math.PI * radius / targetArcLength));

                        for (int i = 0; i < count; i++) {
                            double angle = 2 * Math.PI * i / count;
                            double x = AbstractNamelessOne.this.getX() + radius * Math.cos(angle);
                            double z = AbstractNamelessOne.this.getZ() + radius * Math.sin(angle);
                            double y = AbstractNamelessOne.this.getY();

                            com.k1sak1.goetyawaken.common.entities.projectiles.PureLightEntity light = new com.k1sak1.goetyawaken.common.entities.projectiles.PureLightEntity(
                                    serverLevel);
                            light.setOwner(AbstractNamelessOne.this);
                            light.setPos(x, y, z);
                            light.setExtraDamage(
                                    (AbstractNamelessOne.this.getNecroLevel() + 1) * 6.0F + (float) attackDamage / 4);
                            light.setRenderColor(0, 255, 0);

                            serverLevel.addFreshEntity(light);
                        }
                    }
                } else {
                    ColorUtil goldColor = new ColorUtil(0xFFD700);
                    ServerParticleUtil.summonUndeadParticles(serverLevel, AbstractNamelessOne.this, goldColor, 0xFFD700,
                            0xFFFFFF);
                    ServerParticleUtil.windParticle(serverLevel, goldColor, 4.0F, 1.0F,
                            AbstractNamelessOne.this.getId(), AbstractNamelessOne.this.position());
                }
            } else {
                if (this.spelltick >= 15) {
                    double radius = 8.0D;
                    ColorUtil colorUtil = new ColorUtil(0x0000FF);
                    ServerParticleUtil.windParticle(serverLevel, colorUtil, (float) radius, 1.0F,
                            AbstractNamelessOne.this.getId(),
                            AbstractNamelessOne.this.position());
                }
                if (this.spelltick >= 15) {
                    for (int i = 0; i < 2; ++i) {
                        int potency = AbstractNamelessOne.this.getNecroLevel();
                        net.minecraft.world.Difficulty difficulty = serverLevel.getDifficulty();

                        int difficultyBonus = switch (difficulty) {
                            case PEACEFUL -> 0;
                            case EASY -> 1;
                            case NORMAL -> 2;
                            case HARD -> 3;
                        };
                        potency += difficultyBonus + 2;
                        BlockPos blockPos = AbstractNamelessOne.this.blockPosition();
                        LivingEntity target = AbstractNamelessOne.this.getTarget();
                        if (target != null) {
                            blockPos = target.blockPosition();
                        }
                        BlockPos blockPos1 = blockPos.offset(serverLevel.getRandom().nextInt(-16, 16), 0,
                                serverLevel.getRandom().nextInt(-16, 16));
                        BlockPos blockPos2 = blockPos.offset(serverLevel.getRandom().nextInt(-16, 16), 0,
                                serverLevel.getRandom().nextInt(-16, 16));
                        Vec3 vec3 = Vec3.atBottomCenterOf(blockPos1);
                        Vec3 vec32 = Vec3.atBottomCenterOf(blockPos2);

                        com.Polarice3.Goety.common.entities.util.MagicLightningTrap trap = new com.Polarice3.Goety.common.entities.util.MagicLightningTrap(
                                serverLevel, vec3.x, vec3.y, vec3.z);

                        trap.setColor(0xa7fc3e);
                        trap.setOwner(AbstractNamelessOne.this);
                        trap.setDuration(40);
                        trap.setDamage(trap.getDamage() + 3 * potency);
                        trap.setRadius((float) (trap.radius()));
                        if (!serverLevel.getEntitiesOfClass(MagicLightningTrap.class, new AABB(blockPos1)).isEmpty()) {
                            trap.setPos(vec32.x(), vec32.y(), vec32.z());
                        }
                        com.Polarice3.Goety.utils.MobUtil.moveDownToGround(trap);
                        serverLevel.addFreshEntity(trap);
                    }
                }
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    public class DesertPlaguesGoal extends Goal {
        private int spelltick;
        private boolean useLeechingVariant = false;

        @Override
        public boolean canUse() {
            if (AbstractNamelessOne.this.isMirror() || AbstractNamelessOne.this.isEasyMode()) {
                return false;
            }
            boolean hasServants = false;
            if (AbstractNamelessOne.this.level() instanceof ServerLevel serverLevel) {
                java.util.List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        AbstractNamelessOne.this.getBoundingBox().inflate(16.0D));

                for (LivingEntity entity : nearbyEntities) {
                    if (entity != AbstractNamelessOne.this &&
                            com.Polarice3.Goety.utils.MobUtil.areAllies(AbstractNamelessOne.this, entity) &&
                            !(entity instanceof AbstractNamelessOne)) {
                        hasServants = true;
                        break;
                    }
                }
            }
            boolean lowHealth = AbstractNamelessOne.this.getHealth() < AbstractNamelessOne.this.getMaxHealth() * 0.5F;
            boolean baseConditionsMet = !AbstractNamelessOne.this.isSpellCasting()
                    && AbstractNamelessOne.this.getTarget() != null
                    && AbstractNamelessOne.this.getTarget().isAlive()
                    && AbstractNamelessOne.this.desertPlaguesCool <= 0
                    && !AbstractNamelessOne.this.isShooting();
            float healthPercentage = AbstractNamelessOne.this.getHealth() / AbstractNamelessOne.this.getMaxHealth();
            float leechingChance = 1.0F - healthPercentage;
            if (baseConditionsMet && hasServants && lowHealth
                    && AbstractNamelessOne.this.random.nextFloat() < leechingChance) {
                this.useLeechingVariant = true;
                return true;
            } else {
                this.useLeechingVariant = false;
                return baseConditionsMet;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.spelltick >= 0;
        }

        @Override
        public void start() {
            if (this.useLeechingVariant) {
                this.spelltick = 91;
                AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.LEECHING_SPELL_ANIM);
            } else {
                this.spelltick = 50;
                AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.STORM_ANIM);
            }
            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD);
            if (AbstractNamelessOne.this.getLaughSound() != null) {
                AbstractNamelessOne.this.playSound(AbstractNamelessOne.this.getLaughSound(),
                        2.0F, AbstractNamelessOne.this.getVoicePitch());
            }
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            if (this.useLeechingVariant) {
                AbstractNamelessOne.this.desertPlaguesCool = 2000;
            } else {
                AbstractNamelessOne.this.desertPlaguesCool = 1000;
            }

        }

        @Override
        public void tick() {
            --this.spelltick;

            if (this.useLeechingVariant) {
                if (this.spelltick <= 81 && this.spelltick >= 8) {
                    Level worldIn = AbstractNamelessOne.this.level();
                    if (worldIn instanceof ServerLevel serverLevel) {
                        java.util.List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(
                                LivingEntity.class,
                                AbstractNamelessOne.this.getBoundingBox().inflate(16.0D));

                        for (LivingEntity target : nearbyEntities) {
                            if (target != AbstractNamelessOne.this &&
                                    ((target instanceof Mob mob && mob.getTarget() == AbstractNamelessOne.this)
                                            || (target instanceof Summoned summoned
                                                    && summoned.getTrueOwner() == AbstractNamelessOne.this)
                                            || (AbstractNamelessOne.this.getTarget() != null
                                                    && target == AbstractNamelessOne.this.getTarget()))
                                    &&
                                    !(target instanceof AbstractNamelessOne)) {

                                float potency = 5 + AbstractNamelessOne.this.getNecroLevel();

                                if (target != null) {
                                    com.Polarice3.Goety.utils.ColorUtil colorUtil = new com.Polarice3.Goety.utils.ColorUtil(
                                            net.minecraft.ChatFormatting.DARK_RED);
                                    net.minecraft.world.phys.Vec3 targetVec = new net.minecraft.world.phys.Vec3(
                                            target.getX(), target.getY() + (target.getBbHeight() / 2.0F),
                                            target.getZ());
                                    net.minecraft.world.phys.Vec3 casterVec = new net.minecraft.world.phys.Vec3(
                                            AbstractNamelessOne.this.getRandomX(1.0F),
                                            AbstractNamelessOne.this.getEyeY(),
                                            AbstractNamelessOne.this.getRandomZ(1.0F));

                                    serverLevel.sendParticles(
                                            new com.Polarice3.Goety.client.particles.GatherTrailParticle.Option(
                                                    colorUtil, casterVec),
                                            targetVec.x, targetVec.y,
                                            targetVec.z, 0, 0.0F, 0.0F, 0.0F, 0.5F);

                                    for (int i = 0; i < 8; ++i) {
                                        targetVec = new net.minecraft.world.phys.Vec3(target.getRandomX(1.0F),
                                                target.getRandomY(), target.getRandomZ(1.0F));
                                        serverLevel.sendParticles(
                                                new com.Polarice3.Goety.client.particles.AbsorbTrailParticleOption(
                                                        casterVec, 11141120, 10),
                                                targetVec.x, targetVec.y,
                                                targetVec.z, 1, 0.0, 0.0, 0.0, 0.0);
                                    }

                                    if (target.hurt(ModDamageSource.lifeLeech(serverLevel), 2 * potency)) {
                                        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                                net.minecraft.world.effect.MobEffects.WEAKNESS, 60, 0));
                                        if (AbstractNamelessOne.this.fireDamageSuppressTimer > 0) {
                                            AbstractNamelessOne.this.heal(potency / 6);
                                        } else {
                                            AbstractNamelessOne.this.heal(potency / 3);
                                        }
                                        serverLevel.playSound(null, AbstractNamelessOne.this.getX(),
                                                AbstractNamelessOne.this.getY(), AbstractNamelessOne.this.getZ(),
                                                com.Polarice3.Goety.init.ModSounds.SOUL_EAT.get(),
                                                net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 1.0F);
                                    }
                                }
                            }
                        }
                        com.Polarice3.Goety.utils.ColorUtil colorUtil = new com.Polarice3.Goety.utils.ColorUtil(
                                net.minecraft.ChatFormatting.RED);
                        com.Polarice3.Goety.utils.ServerParticleUtil.gatheringParticles(
                                new com.Polarice3.Goety.client.particles.GatherTrailParticle.Option(colorUtil,
                                        AbstractNamelessOne.this.position().add(0, 1, 0)),
                                AbstractNamelessOne.this,
                                serverLevel, 4);
                    }
                }
            } else {
                if (this.spelltick == 34) {
                    Level level = AbstractNamelessOne.this.level();
                    if (level instanceof ServerLevel serverLevel) {
                        com.k1sak1.goetyawaken.common.magic.spells.wind.DesertPlaguesSpell desertPlaguesSpell = new com.k1sak1.goetyawaken.common.magic.spells.wind.DesertPlaguesSpell();
                        ItemStack windStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.WIND_STAFF.get());
                        windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), 3);
                        windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 5);
                        windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), 3);
                        desertPlaguesSpell.mobSpellResult(AbstractNamelessOne.this, windStaff);
                    }
                }
                if (this.spelltick == 15) {
                    Level level = AbstractNamelessOne.this.level();
                    if (level instanceof ServerLevel serverLevel) {
                        com.Polarice3.Goety.common.magic.spells.wind.WindHornSpell windHornSpell = new com.Polarice3.Goety.common.magic.spells.wind.WindHornSpell();
                        ItemStack windStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.WIND_STAFF.get());
                        windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), 3);
                        windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 5);
                        windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), 3);
                        windHornSpell.mobSpellResult(AbstractNamelessOne.this, windStaff);
                    }
                }
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    protected void handleHitTimeTeleport() {
        if (this.level().isClientSide())
            return;

        int currentHitTimes = this.getHitTimes();
        int requiredHitTimes = this.hitTimeTeleport();

        if (currentHitTimes >= requiredHitTimes && this.teleportCooldown <= 0) {
            this.teleport();
            this.resetHitTime();
        }

        if (this.getTarget() == null && currentHitTimes > 0) {
            this.resetHitTime();
        }
    }

    protected void handleTeleportationLogic() {
        if (this.tickCount % 10 == 0) {
            this.prevVecPos = this.position();
        }
        if (this.teleportCooldown > 0) {
            this.teleportCooldown--;
        }

        LivingEntity target = this.getTarget();
        this.handleHitTimeTeleport();
        if (target != null) {
            if (this.isInWall()) {
                ++this.stuckTime;
            } else {
                if (this.level().getBlockStates(this.getBoundingBox().inflate(1.0F))
                        .anyMatch(blockState1 -> blockState1
                                .getBlock() instanceof net.minecraft.world.level.block.piston.MovingPistonBlock)) {
                    this.stuckTime += 20;
                    if (this.teleportCooldown <= 0) {
                        this.teleport();
                    }
                } else {
                    if (this.stuckTime > 0) {
                        --this.stuckTime;
                    }
                }
            }

            if (this.stuckTime > 50) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel,
                            net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, this);
                }
            }

            if (this.stuckTime >= 100) {
                if (this.teleportCooldown <= 0) {
                    this.escapeTeleport();
                }
                this.stuckTime = 0;
            }
        } else {
            this.stuckTime = 0;
        }

        if ((this.isInWater() || this.isInLava() || this.isInFluidType() || this.isInWall())
                && this.teleportCooldown <= 0) {
            this.teleport();
        }
        if (target == null) {
            if (this.tickCount % 100 == 0) {
                if (this.getTrueOwner() != null && this.isFollowing()) {
                    if (this.getTrueOwner() instanceof net.minecraft.world.entity.player.Player) {
                        double distanceToOwner = this.distanceToSqr(this.getTrueOwner());
                        if (distanceToOwner > 1024 && this.teleportCooldown <= 0) {
                            this.teleportTowardsTarget(this.getTrueOwner());
                        }
                    }
                }
            }
        } else {
            double distanceToTarget = this.distanceToSqr(target);
            if ((distanceToTarget > 1024 || !this.getSensing().hasLineOfSight(target)) && target.onGround()
                    && !this.isSpellCasting() && this.teleportCooldown <= 0) {
                this.teleportTowardsTarget(target);
            }
        }
    }

    protected void teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {

            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();

            boolean teleported = false;
            for (int i = 0; i < 128; ++i) {
                boolean flag = true;
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                double d4 = this.getY();
                if (this.getTarget() != null) {
                    d4 = this.getTarget().getY();
                }
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                BlockPos blockPos = BlockPos.containing(d3, d4, d5);

                if (this.getTarget() != null && i < 64) {
                    flag = com.Polarice3.Goety.utils.BlockFinder.canSeeBlock(this.getTarget(), blockPos);
                }
                if (flag) {
                    if (this.randomTeleport(d3, d4, d5, false)) {
                        this.teleportHits();
                        this.teleportCooldown = 60;
                        teleported = true;
                        break;
                    }
                }
            }
        }
    }

    private void teleportTowardsTarget(Entity entity) {
        if (!this.level().isClientSide() && this.isAlive()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();

            double distanceToTarget = this.distanceToSqr(entity);
            double maxDistanceSqr = 1024.0D;
            if (distanceToTarget > maxDistanceSqr) {
                for (int i = 0; i < 128; ++i) {
                    Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(),
                            this.getZ() - entity.getZ());
                    vector3d = vector3d.normalize();
                    double teleportDistance = 16.0D;
                    double d1 = entity.getX() + (this.random.nextDouble() - 0.5D) * 8.0D
                            - vector3d.x * teleportDistance;
                    double d2 = entity.getY() + (double) (this.random.nextInt(16) - 8) - vector3d.y * teleportDistance;
                    double d3 = entity.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D
                            - vector3d.z * teleportDistance;
                    BlockPos blockPos1 = BlockPos.containing(d1, d2, d3);
                    if (com.Polarice3.Goety.utils.BlockFinder.canSeeBlock(entity, blockPos1)) {
                        if (this.randomTeleport(d1, d2, d3, false)) {
                            this.teleportHits();
                            this.teleportCooldown = 60;
                            break;
                        }
                    }
                }
            }
        }
    }

    protected void escapeTeleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            for (int i = 0; i < 128; ++i) {
                double blockRange = 128.0D;
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * blockRange;
                double d4 = this.getY() + (this.getRandom().nextDouble() - 0.5D) * (blockRange / 2.0D);
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * blockRange;
                if (this.randomTeleport(d3, d4, d5, false)) {
                    this.stuckTime = 0;
                    this.resetHitTime();
                    this.level().broadcastEntityEvent(this, (byte) 100);
                    this.level().gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
                    this.teleportCooldown = 60;
                    break;
                }
            }
        }
    }

    public void teleportHits() {
        this.stuckTime = 0;
        this.resetHitTime();
        this.level().broadcastEntityEvent(this, (byte) 100);
        this.level().gameEvent(net.minecraft.world.level.gameevent.GameEvent.TELEPORT, this.position(),
                net.minecraft.world.level.gameevent.GameEvent.Context.of(this));
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 10) {
            this.deathAnimationState.start(this.tickCount);
        } else if (pId == 100) {
            int i = 128;

            for (int j = 0; j < i; ++j) {
                double d0 = (double) j / (i - 1);
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, this.prevX, this.getX())
                        + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.prevY, this.getY())
                        + this.random.nextDouble() * (double) this.getBbHeight();
                double d3 = Mth.lerp(d0, this.prevZ, this.getZ())
                        + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, d1, d2, d3, (double) f,
                        (double) f1, (double) f2);
            }
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public class AvadaGoal extends Goal {
        private int spellTime;

        @Override
        public boolean canUse() {
            return !AbstractNamelessOne.this.isMirror()
                    && !AbstractNamelessOne.this.isSpellCasting()
                    && AbstractNamelessOne.this.getTarget() != null
                    && AbstractNamelessOne.this.getTarget().isAlive()
                    && AbstractNamelessOne.this.avadaCool <= 0
                    && !AbstractNamelessOne.this.isEasyMode()
                    && !AbstractNamelessOne.this.isShooting();
        }

        @Override
        public boolean canContinueToUse() {
            return this.spellTime >= 0;
        }

        @Override
        public void start() {
            this.spellTime = 80;
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.AVADA_ANIM);
            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD);
            AbstractNamelessOne.this.playSound(AbstractNamelessOne.this.getRandomLaughSound(), 2.0F, 0.05F);
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractNamelessOne.this.avadaCool = 3000;
        }

        @Override
        public void tick() {
            --this.spellTime;

            Level level = AbstractNamelessOne.this.level();
            if (!(level instanceof ServerLevel serverLevel))
                return;
            if (this.spellTime >= 33) {
                double radius = 4.0D;
                ColorUtil colorUtil = new ColorUtil(0xFF0000);
                ServerParticleUtil.windParticle(serverLevel, colorUtil, (float) radius, 1.0F,
                        AbstractNamelessOne.this.getId(),
                        AbstractNamelessOne.this.position());
            }

            if (this.spellTime == 33) {
                LivingEntity target = AbstractNamelessOne.this.getTarget();
                Vec3 vec3 = AbstractNamelessOne.this.getEyePosition();
                if (target != null && target.isAlive()) {
                    ColorUtil colorUtil = new ColorUtil(14226710);
                    Vec3 vec31 = new Vec3(target.getX(), target.getY() + (double) (target.getBbHeight() / 2.0F),
                            target.getZ());
                    DamageSource damageSource = com.Polarice3.Goety.utils.ModDamageSource
                            .deathCurse(AbstractNamelessOne.this);
                    float damage = target.getHealth();

                    if (target.hurt(damageSource, damage)
                            && !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(AbstractNamelessOne.this)) {
                        for (int i = 0; i < 8; ++i) {
                            Vec3 vector3d = new Vec3(target.getX(), target.getEyeY(), target.getZ());
                            Vec3 vector3d1 = vector3d.offsetRandom(target.getRandom(), 8.0F);
                            serverLevel.sendParticles(new GatherTrailParticle.Option(colorUtil, vector3d1), vector3d.x,
                                    vector3d.y, vector3d.z, 0, 0.0, 0.0, 0.0, 0.5);
                            ServerParticleUtil.windParticle(serverLevel, colorUtil, 1.0F, 0.0F, target.getId(),
                                    target.position());
                        }

                        com.Polarice3.Goety.common.network.ModNetwork
                                .sendToALL(new SThunderBoltPacket(vec3, vec31, colorUtil, 10));
                        this.playSound(serverLevel, AbstractNamelessOne.this,
                                (SoundEvent) com.Polarice3.Goety.init.ModSounds.THUNDERBOLT.get(),
                                3.0F, 0.75F);
                        this.playSound(serverLevel, AbstractNamelessOne.this, SoundEvents.LIGHTNING_BOLT_IMPACT, 3.0F,
                                0.75F);

                        ThunderboltSpell thunderboltSpell = new ThunderboltSpell();
                        SpellStat spellStat = new SpellStat(5, 0, 5, 0, 0, 0)
                                .setPotency(5)
                                .setRange(5);
                        ItemStack stormStaff = new ItemStack(ModItems.STORM_STAFF.get());
                        thunderboltSpell.SpellResult(serverLevel, AbstractNamelessOne.this, stormStaff, spellStat);
                    }
                }
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD;
        }

        public void playSound(ServerLevel worldIn, Entity entity, SoundEvent sound, float volume, float pitch) {
            worldIn.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound,
                    net.minecraft.sounds.SoundSource.HOSTILE, volume, pitch);
        }
    }

    public class QuakeOneSpellGoal extends Goal {
        private int spellTime;
        private boolean useVariant = false;

        @Override
        public boolean canUse() {
            return !AbstractNamelessOne.this.isSpellCasting()
                    && AbstractNamelessOne.this.getTarget() != null
                    && AbstractNamelessOne.this.getTarget().isAlive()
                    && AbstractNamelessOne.this.quakespellcool <= 0
                    && !AbstractNamelessOne.this.isEasyMode()
                    && !AbstractNamelessOne.this.isShooting();
        }

        @Override
        public boolean canContinueToUse() {
            return this.spellTime >= 0;
        }

        @Override
        public void start() {
            Level level = AbstractNamelessOne.this.level();
            if (level instanceof ServerLevel serverLevel) {
                this.useVariant = serverLevel.random.nextBoolean();
            } else {
                this.useVariant = false;
            }

            if (this.useVariant) {
                this.spellTime = 68;
                AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.QUAKE2_ANIM);
            } else {
                this.spellTime = 81;
                AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.QUAKE1_ANIM);
            }

            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD);
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractNamelessOne.this.quakespellcool = 650;
        }

        @Override
        public void tick() {
            --this.spellTime;
            Level level = AbstractNamelessOne.this.level();

            if (!(level instanceof ServerLevel serverLevel))
                return;

            if (!this.useVariant) {
                if (this.spellTime >= 57) {
                    double radius = 4.0D;
                    ColorUtil colorUtil = new ColorUtil(0xFFFFFF);
                    ServerParticleUtil.windParticle(serverLevel, colorUtil, (float) radius, 1.0F,
                            AbstractNamelessOne.this.getId(),
                            AbstractNamelessOne.this.position());
                    ServerParticleUtil.gatheringParticles(
                            new GatherTrailParticle.Option(colorUtil, AbstractNamelessOne.this.position().add(0, 1, 0)),
                            AbstractNamelessOne.this,
                            serverLevel, 4);
                }

                if (this.spellTime == 57) {
                    this.executeRandomSpell(serverLevel);
                }
            } else {
                if (this.spellTime >= 43) {
                    double radius = 4.0D;
                    ColorUtil colorUtil = new ColorUtil(0x00FF00);
                    ServerParticleUtil.windParticle(serverLevel, colorUtil, (float) radius, 1.0F,
                            AbstractNamelessOne.this.getId(),
                            AbstractNamelessOne.this.position());
                    ServerParticleUtil.gatheringParticles(
                            new GatherTrailParticle.Option(colorUtil, AbstractNamelessOne.this.position().add(0, 1, 0)),
                            AbstractNamelessOne.this,
                            serverLevel, 4);
                }

                if (this.spellTime == 31) {
                    this.executeVariantBehavior(serverLevel);
                }
            }
        }

        private void executeRandomSpell(ServerLevel serverLevel) {
            LivingEntity target = AbstractNamelessOne.this.getTarget();
            if (target == null || !target.isAlive())
                return;

            double distanceToTarget = AbstractNamelessOne.this.distanceToSqr(target);
            boolean targetInRange = distanceToTarget <= 64.0D;
            boolean targetInRange6 = distanceToTarget <= 36.0D;
            boolean hasAlliesNearby = false;
            if (AbstractNamelessOne.this.isMirror()) {
                List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        AbstractNamelessOne.this.getBoundingBox().inflate(8.0D),
                        entity -> entity != AbstractNamelessOne.this &&
                                com.Polarice3.Goety.utils.MobUtil.areAllies(AbstractNamelessOne.this, entity));
                hasAlliesNearby = !nearbyEntities.isEmpty();
            }

            java.util.List<Integer> availableSpells = new java.util.ArrayList<>();

            if (targetInRange) {
                availableSpells.add(1);
            }

            if (AbstractNamelessOne.this.isMirror() && hasAlliesNearby) {
                availableSpells.add(2);
            } else if (!AbstractNamelessOne.this.isMirror()) {
                availableSpells.add(2);
            }

            availableSpells.add(3);
            if (targetInRange) {
                availableSpells.add(4);
            }

            if (AbstractNamelessOne.this.isMirror() && hasAlliesNearby) {
                availableSpells.add(5);
            } else if (!AbstractNamelessOne.this.isMirror()) {
                availableSpells.add(5);
            }

            if (targetInRange) {
                availableSpells.add(6);
            }

            if (targetInRange) {
                availableSpells.add(7);
            }

            if (targetInRange6) {
                availableSpells.add(8);
            }

            availableSpells.add(9);

            if (availableSpells.isEmpty()) {
                return;
            }

            int selectedSpell = availableSpells.get(serverLevel.random.nextInt(availableSpells.size()));
            ColorUtil colorUtil = new ColorUtil(0xFFFFFF);
            serverLevel.sendParticles(
                    new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 16.0F, 1),
                    AbstractNamelessOne.this.getX(),
                    AbstractNamelessOne.this.getY() + 0.5F,
                    AbstractNamelessOne.this.getZ(),
                    0, 0, 0, 0, 0);

            this.castSelectedSpell(serverLevel, selectedSpell);
        }

        private void executeVariantBehavior(ServerLevel serverLevel) {
            ColorUtil greenColor = new ColorUtil(0x00FF00);
            serverLevel.sendParticles(
                    new CircleExplodeParticleOption(greenColor.red(), greenColor.green(), greenColor.blue(), 16.0F, 1),
                    AbstractNamelessOne.this.getX(),
                    AbstractNamelessOne.this.getY() + 0.5F,
                    AbstractNamelessOne.this.getZ(),
                    0, 0, 0, 0, 0);

            AABB area = new AABB(
                    AbstractNamelessOne.this.getX() - 32,
                    AbstractNamelessOne.this.getY() - 32,
                    AbstractNamelessOne.this.getZ() - 32,
                    AbstractNamelessOne.this.getX() + 32,
                    AbstractNamelessOne.this.getY() + 32,
                    AbstractNamelessOne.this.getZ() + 32);

            List<LivingEntity> allies = serverLevel.getEntitiesOfClass(
                    LivingEntity.class,
                    area,
                    entity -> entity != AbstractNamelessOne.this &&
                            entity instanceof com.Polarice3.Goety.api.entities.IOwned owned &&
                            AbstractNamelessOne.this.equals(owned.getTrueOwner()) &&
                            entity.isAlive());
            int necroLevel = AbstractNamelessOne.this.getNecroLevel();
            net.minecraft.world.Difficulty difficulty = serverLevel.getDifficulty();
            int difficultyCoefficient = switch (difficulty) {
                case PEACEFUL -> 0;
                case EASY -> 1;
                case NORMAL -> 1;
                case HARD -> 2;
            };

            int duration = 30 * (1 + necroLevel + difficultyCoefficient);
            int amplifier = difficultyCoefficient;
            java.util.List<MobEffect> beneficialEffects = new java.util.ArrayList<>();
            beneficialEffects.add(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST);
            beneficialEffects.add(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED);
            beneficialEffects.add(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE);
            beneficialEffects.add(com.Polarice3.Goety.common.effects.GoetyEffects.RALLYING.get());
            beneficialEffects.add(com.Polarice3.Goety.common.effects.GoetyEffects.SHIELDING.get());
            beneficialEffects.add(com.Polarice3.Goety.common.effects.GoetyEffects.SWIRLING.get());
            beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ECHO.get());
            beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.COMMITTED.get());
            beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.CRITICAL_HIT.get());
            beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ENCHANTMENT_THORNS.get());
            beneficialEffects.add(com.k1sak1.goetyawaken.init.ModEffects.ENCHANTMENT_SHARPNESS.get());
            if (!beneficialEffects.isEmpty()) {
                MobEffect selectedEffect = beneficialEffects.get(serverLevel.random.nextInt(beneficialEffects.size()));
                ColorUtil purpleColor = new ColorUtil(0x800080);
                for (LivingEntity ally : allies) {
                    ally.addEffect(
                            new MobEffectInstance(selectedEffect, duration * 20, amplifier, false, false, false));
                    ServerParticleUtil.summonUndeadParticles(serverLevel, ally, purpleColor, 0x800080, 0xFFFFFF);
                    if (ally instanceof IAncientGlint glint) {
                        glint.setAncientGlint(true);
                        glint.setGlintTextureType("enchant");
                    }
                }
            }
        }

        private void castSelectedSpell(ServerLevel serverLevel, int spellId) {
            ItemStack staff;
            int potency = 5;
            int radius = 3;
            int duration = 3;
            int burn = 3;
            int range = 3;
            com.Polarice3.Goety.common.magic.SpellStat spellStat = new com.Polarice3.Goety.common.magic.SpellStat(5, 5,
                    5, 3, 3, 3);
            switch (spellId) {
                case 1:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.NAMELESS_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), radius);
                    new com.Polarice3.Goety.common.magic.spells.ShockwaveSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff, spellStat);
                    break;

                case 2:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.NAMELESS_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), radius);
                    new com.Polarice3.Goety.common.magic.spells.SoulHealSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff, spellStat);
                    break;

                case 3:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.NAMELESS_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), duration);
                    new com.Polarice3.Goety.common.magic.spells.BulwarkSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff);
                    break;

                case 4:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.NAMELESS_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), radius);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), duration);
                    new com.Polarice3.Goety.common.magic.spells.WeakeningSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff, spellStat);
                    break;

                case 5:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.FROST_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency - 2);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), radius - 2);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), duration - 2);
                    new com.Polarice3.Goety.common.magic.spells.frost.ChillHideSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff, spellStat);
                    break;

                case 6:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.FROST_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), radius);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), duration);
                    new com.Polarice3.Goety.common.magic.spells.frost.FrostNovaSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff, spellStat);
                    break;

                case 7:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.STORM_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), radius);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), duration);
                    new com.Polarice3.Goety.common.magic.spells.storm.DischargeSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff, spellStat);
                    break;

                case 8:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.NAMELESS_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), radius);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.BURNING.get(), burn);
                    new com.Polarice3.Goety.common.magic.spells.nether.FireBlastSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff, spellStat);
                    break;

                case 9:
                    staff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.GEO_STAFF.get());
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), potency);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), radius);
                    staff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RANGE.get(), range);
                    new com.Polarice3.Goety.common.magic.spells.geomancy.QuakingSpell()
                            .mobSpellResult(AbstractNamelessOne.this, staff, spellStat);
                    break;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    public class ChargingSpellGoal extends Goal {
        private int spellTime;
        private int selectedSpell = 0;

        @Override
        public boolean canUse() {
            return !AbstractNamelessOne.this.isSpellCasting()
                    && AbstractNamelessOne.this.getTarget() != null
                    && AbstractNamelessOne.this.getTarget().isAlive()
                    && AbstractNamelessOne.this.breathespellcool <= 0
                    && !AbstractNamelessOne.this.isEasyMode()
                    && !AbstractNamelessOne.this.isShooting();
        }

        @Override
        public boolean canContinueToUse() {
            return this.spellTime >= 0;
        }

        @Override
        public void start() {
            this.spellTime = 77;
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.BREATHE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(true);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD);
            this.selectRandomSpell();
        }

        @Override
        public void stop() {
            AbstractNamelessOne.this.setAnimationState(AbstractNamelessOne.IDLE_ANIM);
            AbstractNamelessOne.this.setSpellCasting(false);
            AbstractNamelessOne.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractNamelessOne.this.breathespellcool = 400;
        }

        @Override
        public void tick() {
            --this.spellTime;

            LivingEntity target = AbstractNamelessOne.this.getTarget();
            if (target != null && target.isAlive()) {
                AbstractNamelessOne.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
                if (this.spellTime <= 63 && this.spellTime >= 13) {
                    this.executeSelectedSpell();
                }
            }
        }

        private void selectRandomSpell() {
            Level level = AbstractNamelessOne.this.level();
            if (level instanceof ServerLevel serverLevel) {
                int spellChoice = serverLevel.random.nextInt(6) + 1;
                this.selectedSpell = spellChoice;
                AbstractNamelessOne.this.entityData.set(SELECTED_SPELL, spellChoice);
            }
        }

        private void executeSelectedSpell() {
            Level level = AbstractNamelessOne.this.level();
            if (!(level instanceof ServerLevel serverLevel))
                return;

            LivingEntity target = AbstractNamelessOne.this.getTarget();
            if (target == null || !target.isAlive())
                return;

            int selectedSpell = this.selectedSpell;
            com.Polarice3.Goety.common.magic.SpellStat spellStat = new com.Polarice3.Goety.common.magic.SpellStat(5, 5,
                    5, 3, 3, 1);

            switch (selectedSpell) {
                case 1:
                    ItemStack netherStaff = new ItemStack(
                            com.Polarice3.Goety.common.items.ModItems.NETHER_STAFF.get());
                    netherStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 5);
                    netherStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RANGE.get(), 5);
                    new com.Polarice3.Goety.common.magic.spells.FireBreathSpell()
                            .mobSpellResult(AbstractNamelessOne.this, netherStaff, spellStat);
                    break;

                case 2:
                    ItemStack stormStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.STORM_STAFF.get());
                    stormStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 5);
                    stormStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RANGE.get(), 5);
                    new com.Polarice3.Goety.common.magic.spells.storm.ShockingSpell()
                            .mobSpellResult(AbstractNamelessOne.this, stormStaff, spellStat);
                    break;

                case 3:
                    ItemStack frostStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.FROST_STAFF.get());
                    frostStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 5);
                    frostStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.VELOCITY.get(), 1);
                    new com.Polarice3.Goety.common.magic.spells.frost.BlizzardSpell()
                            .mobSpellResult(AbstractNamelessOne.this, frostStaff, spellStat);
                    break;

                case 4:
                    ItemStack wildStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.WILD_STAFF.get());
                    wildStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 5);
                    wildStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RANGE.get(), 5);
                    wildStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), 3);
                    new com.Polarice3.Goety.common.magic.spells.wild.SwarmSpell()
                            .mobSpellResult(AbstractNamelessOne.this, wildStaff, spellStat);
                    break;

                case 5:
                    ItemStack frostStaff2 = new ItemStack(com.Polarice3.Goety.common.items.ModItems.FROST_STAFF.get());
                    frostStaff2.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 5);
                    frostStaff2.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RANGE.get(), 5);
                    frostStaff2.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), 3);
                    new com.Polarice3.Goety.common.magic.spells.frost.FrostBreathSpell()
                            .mobSpellResult(AbstractNamelessOne.this, frostStaff2, spellStat);
                    break;

                case 6:
                    ItemStack abyssStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.ABYSS_STAFF.get());
                    abyssStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 5);
                    abyssStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.VELOCITY.get(), 3);
                    new com.Polarice3.Goety.common.magic.spells.abyss.SteamSpell()
                            .mobSpellResult(AbstractNamelessOne.this, abyssStaff, spellStat);
                    break;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    protected void triggerQuoteForNearbyPlayers(java.util.List<NamelessOneQuote> quoteList) {
        if (this.level() instanceof ServerLevel serverLevel) {
            NamelessOneQuote quote = NamelessOneQuote.getRandom(quoteList);
            if (quote != null) {
                List<net.minecraft.server.level.ServerPlayer> players = serverLevel.players();
                for (net.minecraft.server.level.ServerPlayer player : players) {
                    if (this.distanceToSqr(player) <= 64.0D * 64.0D) {
                        quote.play(player);
                    }
                }
            }
        }
    }

    protected void triggerQuoteForPlayer(net.minecraft.world.entity.player.Player player,
            java.util.List<NamelessOneQuote> quoteList) {
        if (this.level() instanceof ServerLevel serverLevel && player != null) {
            NamelessOneQuote quote = NamelessOneQuote.getRandom(quoteList);
            if (quote != null) {
                quote.play((net.minecraft.server.level.ServerPlayer) player);
            }
        }
    }

    public void triggerQuoteForOwner(java.util.List<NamelessOneQuote> quoteList) {
        LivingEntity owner = this.getTrueOwner();
        if (owner instanceof net.minecraft.world.entity.player.Player) {
            this.triggerQuoteForPlayer((net.minecraft.world.entity.player.Player) owner, quoteList);
        }
    }

    protected void checkchatQuotes() {
        if (this.isMirror() || this.isHostile()) {
            return;
        }
        if (this.tickCount % (3 * 60 * 20) == 0) {
            if (this.level() instanceof ServerLevel serverLevel) {
                List<net.minecraft.world.entity.player.Player> nearbyPlayers = serverLevel.getEntitiesOfClass(
                        net.minecraft.world.entity.player.Player.class,
                        this.getBoundingBox().inflate(64.0D));

                if (!nearbyPlayers.isEmpty() && serverLevel.random.nextDouble() < 0.5) {
                    this.triggerQuoteForNearbyPlayers(NamelessOneQuote.CHAT_QUOTES);
                }
            }
        }
    }

    protected void triggerDeathQuote() {
        if (this.isMirror()) {
            return;
        }
        this.triggerQuoteForNearbyPlayers(NamelessOneQuote.DEATH_QUOTES);
    }

    protected void triggerSummonServantQuote() {
        if (this.isMirror()) {
            return;
        }
        this.triggerQuoteForOwner(NamelessOneQuote.SUMMON_SERVANT_QUOTES);
    }

    public void triggerDiscoverEnemyQuote() {
        if (this.isMirror()) {
            return;
        }
        if (!this.hasTriggeredDiscoverEnemyQuote && this.level().random.nextDouble() < 0.01) {
            this.triggerQuoteForOwner(NamelessOneQuote.DISCOVER_ENEMY_QUOTES);
            this.hasTriggeredDiscoverEnemyQuote = true;
        }
    }

    public void triggerKillEnemyQuote() {
        if (this.isMirror()) {
            return;
        }
        if (this.level().random.nextDouble() < 0.05) {
            this.triggerQuoteForOwner(NamelessOneQuote.KILL_ENEMY_QUOTES);
        }
    }

    public void triggerKillBossQuote(LivingEntity target) {
        if (this.isMirror()) {
            return;
        }
        if (target != null) {
            java.util.List<NamelessOneQuote> quoteType = null;
            if (target.getType() == net.minecraft.world.entity.EntityType.ENDER_DRAGON) {
                quoteType = NamelessOneQuote.KILL_ENDER_DRAGON_QUOTES;
            } else if (target.getType() == net.minecraft.world.entity.EntityType.WITHER) {
                quoteType = NamelessOneQuote.KILL_WITHER_QUOTES;
            } else if (target.getType() == net.minecraft.world.entity.EntityType.WARDEN) {
                quoteType = NamelessOneQuote.KILL_WARDEN_QUOTES;
            } else if (target.getType() == net.minecraft.world.entity.EntityType.ELDER_GUARDIAN) {
                quoteType = NamelessOneQuote.KILL_ELDER_GUARDIAN_QUOTES;
            }
            if (quoteType != null) {
                this.triggerQuoteForOwner(quoteType);
            }
        }
    }

    public void triggerKillPlayerQuote(net.minecraft.world.entity.player.Player player) {
        if (this.isMirror()) {
            return;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner != null && owner != player) {
            this.triggerQuoteForOwner(NamelessOneQuote.KILL_PLAYER_QUOTES);
        }
    }

    public boolean triggerKillSpecialEnemyQuote(LivingEntity target) {
        if (this.isMirror()) {
            return false;
        }
        net.minecraft.resources.ResourceLocation entityId = net.minecraft.world.entity.EntityType
                .getKey(target.getType());
        if (entityId == null) {
            return false;
        }
        List<KillSpecialEnemyQuoteLoader.SpecialEnemyQuoteConfig> configs = KillSpecialEnemyQuoteHandler
                .getQuotesForEntity(entityId);
        if (configs.isEmpty()) {
            return false;
        }
        KillSpecialEnemyQuoteLoader.SpecialEnemyQuoteConfig config = configs
                .get(this.level().random.nextInt(configs.size()));
        this.triggerKillSpecialEnemyQuotePlay(config.key, config.subtitles);
        return true;
    }

    private void triggerKillSpecialEnemyQuotePlay(String quoteKey, float subtitles) {
        LivingEntity owner = this.getTrueOwner();
        if (owner instanceof net.minecraft.server.level.ServerPlayer player) {
            NamelessOneQuote tempQuote = new NamelessOneQuote(quoteKey);
            tempQuote.addSubtitles(new NamelessOneSubtitles(subtitles));
            tempQuote.play(player);
        }
    }

    public void triggerPlayerDeathQuote(net.minecraft.world.entity.player.Player player) {
        if (this.isMirror()) {
            return;
        }
        if (player != null) {
            this.triggerQuoteForPlayer(player, NamelessOneQuote.PLAYER_DEATH_QUOTES);
        }
    }

    public void triggerSpawnQuote() {
        if (this.isMirror() || this.isHostile()) {
            return;
        }
        this.triggerQuoteForNearbyPlayers(NamelessOneQuote.SPAWN_QUOTES);
    }

    protected void createLootChest(BlockState blockState, BlockPos blockPos, DamageSource cause) {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.level().setBlockAndUpdate(blockPos, blockState);
            net.minecraft.world.level.storage.loot.LootParams.Builder lootParamsBuilder = new net.minecraft.world.level.storage.loot.LootParams.Builder(
                    serverLevel)
                    .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY,
                            this)
                    .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN,
                            this.position())
                    .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.DAMAGE_SOURCE,
                            cause)
                    .withOptionalParameter(
                            net.minecraft.world.level.storage.loot.parameters.LootContextParams.KILLER_ENTITY,
                            cause.getEntity())
                    .withOptionalParameter(
                            net.minecraft.world.level.storage.loot.parameters.LootContextParams.DIRECT_KILLER_ENTITY,
                            cause.getDirectEntity());

            if (this.getKillCredit() instanceof net.minecraft.world.entity.player.Player player) {
                lootParamsBuilder = lootParamsBuilder
                        .withParameter(
                                net.minecraft.world.level.storage.loot.parameters.LootContextParams.LAST_DAMAGE_PLAYER,
                                player)
                        .withLuck(player.getLuck());
            }

            net.minecraft.world.level.storage.loot.LootParams lootParams = lootParamsBuilder
                    .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.ENTITY);
            net.minecraft.resources.ResourceLocation lootTableId = new net.minecraft.resources.ResourceLocation(
                    "goetyawaken", "entities/boss_nameless_one");
            net.minecraft.world.level.storage.loot.LootTable table = serverLevel.getServer().getLootData()
                    .getLootTable(lootTableId);
            it.unimi.dsi.fastutil.objects.ObjectArrayList<net.minecraft.world.item.ItemStack> lootItems = table
                    .getRandomItems(lootParams);

            java.util.List<Integer> availableSlots = getAvailableSlots(this.random);
            com.Polarice3.Goety.utils.ModLootTables.shuffleAndSplitItems(lootItems, availableSlots.size(), this.random);

            net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> finalLoot = net.minecraft.core.NonNullList
                    .withSize(27, net.minecraft.world.item.ItemStack.EMPTY);
            for (net.minecraft.world.item.ItemStack itemstack : lootItems) {
                if (!availableSlots.isEmpty()) {
                    if (itemstack.isEmpty()) {
                        finalLoot.set(availableSlots.remove(availableSlots.size() - 1),
                                net.minecraft.world.item.ItemStack.EMPTY);
                    } else {
                        finalLoot.set(availableSlots.remove(availableSlots.size() - 1), itemstack);
                    }
                }
            }
            if (this.level().getBlockEntity(blockPos) instanceof net.minecraft.world.Container container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    container.setItem(i, finalLoot.get(i));
                }
            }
        }
    }

    protected static java.util.List<Integer> getAvailableSlots(net.minecraft.util.RandomSource random) {
        it.unimi.dsi.fastutil.objects.ObjectArrayList<Integer> arrayList = new it.unimi.dsi.fastutil.objects.ObjectArrayList<>();
        for (int i = 0; i < 27; ++i) {
            arrayList.add(i);
        }
        net.minecraft.Util.shuffle(arrayList, random);
        return arrayList;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.namelessOneLimit;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public boolean canStandOnFluid(FluidState p_204067_) {
        return p_204067_.is(FluidTags.LAVA);
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        return super.isAlliedTo(entityIn) || (entityIn instanceof AbstractNamelessOne);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.isHostile() && target instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            return true;
        }
        return super.canAttack(target);
    }

    public static class RandomGoalWrapper extends Goal {
        private final List<Goal> goals;
        private final net.minecraft.world.entity.Mob mob;
        private Goal currentGoal;

        public RandomGoalWrapper(List<Goal> goals, net.minecraft.world.entity.Mob mob) {
            this.goals = goals;
            this.mob = mob;
            this.currentGoal = null;
        }

        @Override
        public boolean canUse() {
            if (currentGoal != null && currentGoal.canContinueToUse()) {
                return true;
            }

            List<Goal> availableGoals = new java.util.ArrayList<>();
            for (Goal goal : goals) {
                if (goal.canUse()) {
                    availableGoals.add(goal);
                }
            }

            if (availableGoals.isEmpty()) {
                currentGoal = null;
                return false;
            }

            currentGoal = availableGoals.get(mob.getRandom().nextInt(availableGoals.size()));
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return currentGoal != null && currentGoal.canContinueToUse();
        }

        @Override
        public void start() {
            if (currentGoal != null) {
                currentGoal.start();
            }
        }

        @Override
        public void stop() {
            if (currentGoal != null) {
                currentGoal.stop();
                currentGoal = null;
            }
        }

        @Override
        public void tick() {
            if (currentGoal != null) {
                currentGoal.tick();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return currentGoal != null && currentGoal.requiresUpdateEveryTick();
        }

        @Override
        public EnumSet<Flag> getFlags() {
            if (currentGoal != null) {
                return currentGoal.getFlags();
            }
            return EnumSet.noneOf(Flag.class);
        }
    }
}