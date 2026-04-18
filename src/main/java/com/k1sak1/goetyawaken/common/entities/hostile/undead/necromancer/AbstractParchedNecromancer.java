package com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer;

import com.Polarice3.Goety.common.entities.projectiles.RazorWind;
import com.Polarice3.Goety.common.entities.util.UpdraftBlast;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.Cyclone;
import com.Polarice3.Goety.common.entities.ally.undead.PhantomServant;
import com.Polarice3.Goety.common.entities.ally.undead.ReaperServant;
import com.Polarice3.Goety.common.entities.ally.undead.WraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.HuskServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags.EntityTypes;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.SoundUtil;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.google.common.base.Predicate;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.ParchedServant;
import net.minecraft.world.entity.Pose;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractParchedNecromancer extends AbstractNecromancer implements ICustomAttributes {
    protected static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData
            .defineId(AbstractParchedNecromancer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData
            .defineId(AbstractParchedNecromancer.class, EntityDataSerializers.INT);
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
    public final AnimationState rapidAnimationState = new AnimationState();
    protected int idleSpellCool = 0;
    protected int rapidSpellCool = 0;
    protected int cycloneCool = 0;
    protected int DesertPlaguescooldown = 0;

    public AbstractParchedNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.LAVA, 8.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, IDLE_ANIM);
        this.entityData.define(LEVEL, 0);
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
    public boolean shouldBeSaved() {
        return true;
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
            attributeInstance.setBaseValue(
                    com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerHealth.get() *
                            Math.max(clampedLevel * 1.25F, 1));
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
        this.rapidAnimationState.stop();
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
        animationStates.add(this.rapidAnimationState);
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
                        this.stopMostAnimation(this.attackAnimationState);
                        break;
                    case SUMMON_ANIM:
                        this.summonAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.summonAnimationState);
                        break;
                    case SPELL_ANIM:
                        this.spellAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.spellAnimationState);
                        break;
                    case ALERT_ANIM:
                        this.alertAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.alertAnimationState);
                        break;
                    case FLY_ANIM:
                        this.flyAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.flyAnimationState);
                        break;
                    case WALK_ANIM:
                        this.walkAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walkAnimationState);
                        break;
                    case WALK2_ANIM:
                        this.walk2AnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.walk2AnimationState);
                        break;
                    case UPDRAFT_ANIM:
                        this.updrafAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.updrafAnimationState);
                        break;
                    case STORM_ANIM:
                        this.stormAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.stormAnimationState);
                        break;
                    case RAPID_ANIM:
                        this.rapidAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.rapidAnimationState);
                        break;
                    default:
                        break;
                }
            }
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    public void projectileGoal(int priority) {
        this.goalSelector.addGoal(priority, new ParchedNecromancerRangedGoal(this, 1.0D, 20, 12.0F));
    }

    @Override
    public void avoidGoal(int priority) {

    }

    @Override
    public void summonSpells(int priority) {
        this.goalSelector.addGoal(priority + 1, new SummonServantSpell());
        this.goalSelector.addGoal(priority + 2, new ParchedSummonUndeadGoal());
        this.goalSelector.addGoal(priority + 3, new RapidSpellGoal());
        this.goalSelector.addGoal(priority + 3, new UpdraftSpellGoal());
        this.goalSelector.addGoal(priority + 3, new CycloneGoal());
        this.goalSelector.addGoal(priority + 3, new DesertPlaguesGoal());
    }

    public class UpdraftSpellGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractParchedNecromancer.this.getTarget();
            return AbstractParchedNecromancer.this.rapidSpellCool <= 0
                    && target != null
                    && target.isAlive()
                    && !AbstractParchedNecromancer.this.isSpellCasting();
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        @Override
        public void start() {
            this.spellTime = 65;
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.UPDRAFT_ANIM);
            AbstractParchedNecromancer.this.setSpellCasting(true);
            AbstractParchedNecromancer.this.setNecromancerSpellType(AbstractNecromancer.NecromancerSpellType.CLOUD);
        }

        @Override
        public void stop() {
            AbstractParchedNecromancer.this.rapidSpellCool = 600;
            AbstractParchedNecromancer.this.setSpellCasting(false);
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.IDLE_ANIM);
            AbstractParchedNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime == 57) {
                executeUpdraftSpell();
            }
            if (this.spellTime == 37) {
                AbstractParchedNecromancer.this.setDeltaMovement(
                        AbstractParchedNecromancer.this.getDeltaMovement().add(0, 1.2D, 0));
            }
            if (this.spellTime == 0) {
                AbstractParchedNecromancer.this.setSpellCasting(false);
                AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.IDLE_ANIM);
                AbstractParchedNecromancer.this.setNecromancerSpellType(
                        com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            }
        }

        private void executeUpdraftSpell() {
            Level level = AbstractParchedNecromancer.this.level();
            if (level instanceof ServerLevel serverLevel) {
                double attackDamage = AbstractParchedNecromancer.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float damage = 4.0F + (float) (attackDamage / 2.0F)
                        + 2 * AbstractParchedNecromancer.this.getNecroLevel();
                createUpdraftBlast(serverLevel,
                        AbstractParchedNecromancer.this.getX(),
                        AbstractParchedNecromancer.this.getY(),
                        AbstractParchedNecromancer.this.getZ(),
                        damage);

                LivingEntity target = AbstractParchedNecromancer.this.getTarget();
                if (target != null) {
                    createUpdraftBlast(serverLevel, target.getX(), target.getY(), target.getZ(), damage);
                }

                List<LivingEntity> nearbyHostiles = level.getEntitiesOfClass(LivingEntity.class,
                        AbstractParchedNecromancer.this.getBoundingBox().inflate(16.0));
                for (LivingEntity entity : nearbyHostiles) {
                    if (entity != AbstractParchedNecromancer.this &&
                            entity instanceof net.minecraft.world.entity.Mob mob &&
                            mob.getTarget() != null &&
                            mob.getTarget() == AbstractParchedNecromancer.this) {
                        createUpdraftBlast(serverLevel, entity.getX(), entity.getY(), entity.getZ(), damage);
                    }
                }
            }
        }

        private void createUpdraftBlast(ServerLevel level, double x, double y, double z, float damage) {
            UpdraftBlast updraftBlast = new UpdraftBlast(level, x, y, z);
            updraftBlast.setOwner(AbstractParchedNecromancer.this);
            updraftBlast.setDamage(damage);
            updraftBlast.setAreaOfEffect(4.0F);
            level.addFreshEntity(updraftBlast);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    public class CycloneGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractParchedNecromancer.this.getTarget();
            return AbstractParchedNecromancer.this.cycloneCool <= 0
                    && target != null
                    && target.isAlive()
                    && !AbstractParchedNecromancer.this.isSpellCasting();
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        @Override
        public void start() {
            this.spellTime = 60;
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.SPELL_ANIM);
            AbstractParchedNecromancer.this.setSpellCasting(true);
            AbstractParchedNecromancer.this.setNecromancerSpellType(AbstractNecromancer.NecromancerSpellType.CLOUD);
        }

        @Override
        public void stop() {
            AbstractParchedNecromancer.this.cycloneCool = 500;
            AbstractParchedNecromancer.this.setSpellCasting(false);
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.IDLE_ANIM);
            AbstractParchedNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime > 5) {
                com.Polarice3.Goety.common.magic.spells.wind.WhirlwindSpell whirlwindSpell = new com.Polarice3.Goety.common.magic.spells.wind.WhirlwindSpell();
                ItemStack windStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.WIND_STAFF.get());
                whirlwindSpell.mobSpellResult(AbstractParchedNecromancer.this, windStaff);
            }
            if (this.spellTime == 5) {
                com.Polarice3.Goety.common.magic.spells.wind.WindHornSpell windHornSpell = new com.Polarice3.Goety.common.magic.spells.wind.WindHornSpell();
                ItemStack windStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.WIND_STAFF.get());
                windHornSpell.mobSpellResult(AbstractParchedNecromancer.this, windStaff);
                this.spawnCyclone();
            }
        }

        private void spawnCyclone() {
            Level level = AbstractParchedNecromancer.this.level();
            if (level instanceof ServerLevel serverLevel) {
                LivingEntity target = AbstractParchedNecromancer.this.getTarget();
                if (target == null) {
                    return;
                }

                double angle = serverLevel.random.nextDouble() * Math.PI * 2;
                double distance = 8.0 + serverLevel.random.nextDouble() * 8.0;
                double x = target.getX() + Math.cos(angle) * distance;
                double z = target.getZ() + Math.sin(angle) * distance;
                double y = target.getY();
                Cyclone cyclone = new Cyclone(com.Polarice3.Goety.common.entities.ModEntityType.CYCLONE.get(),
                        serverLevel);
                cyclone.setPos(x, y, z);
                cyclone.setOwner(AbstractParchedNecromancer.this);
                double attackDamage = AbstractParchedNecromancer.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float damage = 4.0F + (float) (attackDamage / 2.0F)
                        + 2 * AbstractParchedNecromancer.this.getNecroLevel();
                cyclone.setDamage(damage);
                cyclone.setSize(1 + AbstractParchedNecromancer.this.getNecroLevel() / 4);
                cyclone.setTarget(target);

                serverLevel.addFreshEntity(cyclone);
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    public class RapidSpellGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractParchedNecromancer.this.getTarget();
            return target != null
                    && target.isAlive()
                    && AbstractParchedNecromancer.this.idleSpellCool <= 0
                    && !AbstractParchedNecromancer.this.isSpellCasting();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = AbstractParchedNecromancer.this.getTarget();
            return this.spellTime > 0 && target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.spellTime = 88;
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.RAPID_ANIM);
            AbstractParchedNecromancer.this.setSpellCasting(true);
            AbstractParchedNecromancer.this.setNecromancerSpellType(AbstractNecromancer.NecromancerSpellType.CLOUD);
        }

        @Override
        public void stop() {
            AbstractParchedNecromancer.this.idleSpellCool = 600;
            AbstractParchedNecromancer.this.setSpellCasting(false);
            AbstractParchedNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.IDLE_ANIM);
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime == 80 || this.spellTime == 73 || this.spellTime == 67 ||
                    this.spellTime == 60 || this.spellTime == 53 || this.spellTime == 47 ||
                    this.spellTime == 40 || this.spellTime == 33 || this.spellTime == 27 ||
                    this.spellTime == 20 || this.spellTime == 13) {
                LivingEntity target = AbstractParchedNecromancer.this.getTarget();
                if (target != null) {
                    AbstractParchedNecromancer.this.performRangedAttack(target, 1.0F);
                }
            }
            if (this.spellTime == 10) {
                com.Polarice3.Goety.common.magic.spells.wind.WindBlastSpell windBlastSpell = new com.Polarice3.Goety.common.magic.spells.wind.WindBlastSpell();
                ItemStack windStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.WIND_STAFF.get());
                windBlastSpell.mobSpellResult(AbstractParchedNecromancer.this, windStaff);
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,
                        com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerHealth.get())
                .add(Attributes.ARMOR, com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS,
                        com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerArmorToughness.get())
                .add(Attributes.FOLLOW_RANGE,
                        com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE,
                        com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerDamage.get());
    }

    public void setConfigurableAttributes() {
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerHealth.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerArmor.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerArmorToughness.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerFollowRange.get());
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25F);
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 1.0D);
        com.Polarice3.Goety.utils.MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                com.k1sak1.goetyawaken.config.AttributesConfig.ParchedNecromancerDamage.get());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        boolean hasArmor = false;
        for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            if (slot.getType() == net.minecraft.world.entity.EquipmentSlot.Type.ARMOR &&
                    !this.getItemBySlot(slot).isEmpty()) {
                hasArmor = true;
                break;
            }
        }

        if (hasArmor) {
            return getRandomHurtArmoredSound();
        } else {
            Random random = new Random();
            int choice = random.nextInt(3) + 1;
            switch (choice) {
                case 1:
                    return ModSounds.PARCHED_HURT_1_NEW.get();
                case 2:
                    return ModSounds.PARCHED_HURT_2_NEW.get();
                case 3:
                    return ModSounds.PARCHED_HURT_3_NEW.get();
                default:
                    return ModSounds.PARCHED_HURT_1_NEW.get();
            }
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.PARCHED_DEATH_NEW.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        Random random = new Random();
        int choice = random.nextInt(4) + 1;
        switch (choice) {
            case 1:
                return ModSounds.PARCHED_STEP_1.get();
            case 2:
                return ModSounds.PARCHED_STEP_2.get();
            case 3:
                return ModSounds.PARCHED_STEP_3.get();
            case 4:
                return ModSounds.PARCHED_STEP_4.get();
            default:
                return ModSounds.PARCHED_STEP_1.get();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        Random random = new Random();
        int choice = random.nextInt(4) + 1;
        switch (choice) {
            case 1:
                return ModSounds.PARCHED_IDLE_1.get();
            case 2:
                return ModSounds.PARCHED_IDLE_2.get();
            case 3:
                return ModSounds.PARCHED_IDLE_3.get();
            case 4:
                return ModSounds.PARCHED_IDLE_4.get();
            default:
                return ModSounds.PARCHED_IDLE_1.get();
        }
    }

    public int xpReward() {
        return 80;
    }

    public class ParchedNecromancerRangedGoal extends Goal {
        private final AbstractParchedNecromancer necromancer;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackInterval;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public ParchedNecromancerRangedGoal(AbstractParchedNecromancer necromancer, double speed, int attackInterval,
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
                    if (d0 < 36.0D) {
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
                    if (this.necromancer.getAnimationState() != this.necromancer.ATTACK_ANIM) {
                        this.necromancer.setAnimationState(this.necromancer.ATTACK_ANIM);
                    }
                }
                if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.necromancer.performRangedAttack(this.target, f1);
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
        RazorWind razorWind = new RazorWind(this.level(), this);
        double d0 = target.getX() - this.getX();
        double d1 = target.getEyeY() - this.getEyeY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        double attackDamage = this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        razorWind.setPos(this.getX(), this.getEyeY() - 0.3D, this.getZ());
        razorWind.shoot(d0, d1, d2, 1.6F, 1.0F);
        razorWind.setDamage(2 * this.getNecroLevel() + (float) (attackDamage));
        razorWind.setRadius(0.6F + this.getNecroLevel() / 2);
        if (this.level().addFreshEntity(razorWind)) {
            this.playSound(getRandomShootSound(), 1.0F, 1.0F);
            this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.idleSpellCool > 0) {
            --this.idleSpellCool;
        }
        if (this.rapidSpellCool > 0) {
            --this.rapidSpellCool;
        }
        if (this.cycloneCool > 0) {
            --this.cycloneCool;
        }
        if (this.DesertPlaguescooldown > 0) {
            --this.DesertPlaguescooldown;
        }
        if (this.level().isClientSide) {
            if (this.isAlive()) {
                if (this.isSpellCasting()) {
                    this.spellCastParticles();
                }
            }
            this.idleAnimationState.animateWhen(this.getAnimationState() == IDLE_ANIM, this.tickCount);
            this.walkAnimationState.animateWhen(this.getAnimationState() == WALK_ANIM, this.tickCount);
            this.attackAnimationState.animateWhen(this.getAnimationState() == ATTACK_ANIM, this.tickCount);
            this.summonAnimationState.animateWhen(this.getAnimationState() == SUMMON_ANIM, this.tickCount);
            this.spellAnimationState.animateWhen(this.getAnimationState() == SPELL_ANIM, this.tickCount);
            this.alertAnimationState.animateWhen(this.getAnimationState() == ALERT_ANIM, this.tickCount);
            this.flyAnimationState.animateWhen(this.getAnimationState() == FLY_ANIM, this.tickCount);
            this.walk2AnimationState.animateWhen(this.getAnimationState() == WALK2_ANIM, this.tickCount);
            this.updrafAnimationState.animateWhen(this.getAnimationState() == UPDRAFT_ANIM, this.tickCount);
            this.stormAnimationState.animateWhen(this.getAnimationState() == STORM_ANIM, this.tickCount);
            this.rapidAnimationState.animateWhen(this.getAnimationState() == RAPID_ANIM, this.tickCount);
        } else {
            if (!this.isShooting() && !this.isSpellCasting() &&
                    this.getAnimationState() != SUMMON_ANIM &&
                    this.getAnimationState() != SPELL_ANIM &&
                    this.getAnimationState() != ATTACK_ANIM &&
                    this.getAnimationState() != ALERT_ANIM) {
                double speed = this.getDeltaMovement().horizontalDistance();
                if (speed > 0.05D) {
                    int targetAnim = this.onGround() ? WALK_ANIM : WALK2_ANIM;
                    if (this.getAnimationState() != targetAnim) {
                        this.setAnimationState(targetAnim);
                    }
                } else {
                    if (this.getAnimationState() != IDLE_ANIM) {
                        this.setAnimationState(IDLE_ANIM);
                    }
                }
            }
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.isAlive()) {
                ServerParticleUtil.windParticle(serverLevel, ColorUtil.WHITE,
                        0.5F + serverLevel.random.nextFloat() * 1.5F, 0.0F, this.getId(),
                        this.position());
                ColorUtil colorUtil = new ColorUtil(0x8d837d);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.STATION_CULT_SPELL.get(), this,
                        colorUtil.red(), colorUtil.green(), colorUtil.blue(), 0.5F);
            }
        }
    }

    // @Override
    // public void travel(Vec3 travelVector) {
    // if (this.isControlledByLocalInstance()) {
    // if (!this.onGround()) {
    // if (this.isInWater()) {
    // this.moveRelative(0.02F, travelVector);
    // this.move(MoverType.SELF, this.getDeltaMovement());
    // this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
    // } else if (this.isInLava()) {
    // this.moveRelative(0.02F, travelVector);
    // this.move(MoverType.SELF, this.getDeltaMovement());
    // this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
    // } else {
    // this.moveRelative(this.getSpeed(), travelVector);
    // this.move(MoverType.SELF, this.getDeltaMovement());
    // this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.9F));
    // }
    // }
    // }
    // super.travel(travelVector);
    // }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround() && vector3d.y < 0.0D && !this.isNoGravity()) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    public void stopAllAnimationsAndReset() {
        this.stopAllAnimations();
        this.setShooting(false);
        this.setSpellCasting(false);
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    public Summoned getDefaultSummon() {
        float rand = this.level().random.nextFloat();
        if (rand < 0.5F) {
            return new HuskServant((EntityType) ModEntityType.HUSK_SERVANT.get(), this.level());
        } else {
            return new ParchedServant(
                    (EntityType) com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED_SERVANT.get(),
                    this.level());
        }

    }

    public Summoned getSummon() {
        Summoned summoned = getDefaultSummon();
        float regionalDifficulty = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
        if (regionalDifficulty >= 3.0F) {
            if (this.level().random.nextFloat() <= 0.10F) {
                summoned = new BlackguardServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get(), this.level());
            } else if (this.level().random.nextFloat() <= 0.20F) {
                summoned = new VanguardServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get(), this.level());
            }
        }

        if (this.getSummonList().stream()
                .anyMatch(entityType -> entityType.is(com.Polarice3.Goety.init.ModTags.EntityTypes.ZOMBIE_SERVANTS))) {
            if (this.level().random.nextBoolean()) {
                summoned = new HuskServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.HUSK_SERVANT.get(), this.level());
            }
        }
        if (this.getSummonList().stream()
                .anyMatch(
                        entityType -> entityType.is(com.Polarice3.Goety.init.ModTags.EntityTypes.SKELETON_SERVANTS))) {
            if (this.level().random.nextBoolean()) {
                summoned = new ParchedServant(
                        com.k1sak1.goetyawaken.common.entities.ModEntityType.PARCHED_SERVANT.get(), this.level());
            }
        }
        if (this.getSummonList().contains(com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.05F) {
                summoned = new BlackguardServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get(), this.level());
            }
        }
        if (this.getSummonList().contains(com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.15F) {
                summoned = new VanguardServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get(), this.level());
            }
        }
        if (this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get())
                && this.level().random.nextFloat() <= 0.05F) {
            summoned = new WraithServant((EntityType) ModEntityType.WRAITH_SERVANT.get(), this.level());
        }

        if (this.getSummonList().contains(ModEntityType.REAPER_SERVANT.get())
                && this.level().random.nextFloat() <= 0.05F) {
            summoned = new ReaperServant((EntityType) ModEntityType.REAPER_SERVANT.get(), this.level());
        }
        if (this.getSummonList().contains(com.Polarice3.Goety.common.entities.ModEntityType.PHANTOM_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.15F) {
                summoned = new PhantomServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.PHANTOM_SERVANT.get(), this.level());
            }
        }
        return summoned;
    }

    public class SummonServantSpell extends AbstractNecromancer.SummoningSpellGoal {
        @Override
        public boolean canUse() {
            Predicate<Entity> predicate = (entity) -> {
                boolean var10000;
                if (entity.isAlive() && entity instanceof IOwned owned) {
                    if (owned.getTrueOwner() == AbstractParchedNecromancer.this) {
                        var10000 = true;
                        return var10000;
                    }
                }

                var10000 = false;
                return var10000;
            };
            int i = AbstractParchedNecromancer.this.level()
                    .getEntitiesOfClass(LivingEntity.class,
                            AbstractParchedNecromancer.this.getBoundingBox().inflate(64.0, 16.0, 64.0), predicate)
                    .size();
            return super.canUse() && i < 6;
        }

        @Override
        public void start() {
            this.spellTime = this.getCastingTime();
            AbstractParchedNecromancer.this.setSpellCooldown(this.getCastingInterval());
            this.playPrepareSound();
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.SUMMON_ANIM);
            AbstractParchedNecromancer.this.setSpellCasting(true);
            AbstractParchedNecromancer.this.setNecromancerSpellType(this.getNecromancerSpellType());
        }

        @Override
        public void stop() {
            AbstractParchedNecromancer.this.setSpellCasting(false);
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.IDLE_ANIM);
            AbstractParchedNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime == 10) {
                if (this.getCastSound() != null) {
                    AbstractParchedNecromancer.this.playSound(this.getCastSound(), 1.0F, 1.0F);
                }

                AbstractParchedNecromancer.this.playSound(
                        (SoundEvent) getRandomLaughSound(), 2.0F, 0.05F);
                this.castSpell();
                AbstractParchedNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            }

        }

        @Override
        protected void castSpell() {
            Level var2 = AbstractParchedNecromancer.this.level();
            if (var2 instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 3; ++i1) {
                    Summoned summoned = AbstractParchedNecromancer.this.getSummon();
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractParchedNecromancer.this.blockPosition(),
                            summoned, serverLevel);
                    summoned.setTrueOwner(AbstractParchedNecromancer.this);
                    summoned.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summoned);
                    if (!AbstractParchedNecromancer.this.getType().is(EntityTypes.MINI_BOSSES)
                            && (Boolean) MobsConfig.NecromancerSummonsLife.get()) {
                        summoned.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    }

                    summoned.setPersistenceRequired();
                    summoned.finalizeSpawn(serverLevel,
                            serverLevel.getCurrentDifficultyAt(AbstractParchedNecromancer.this.blockPosition()),
                            MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                    if (serverLevel.addFreshEntity(summoned)) {
                        SoundUtil.playNecromancerSummon(summoned);
                        ServerParticleUtil.summonUndeadParticles(serverLevel, summoned, new ColorUtil(16753408),
                                16753408, 16777070);
                    }
                }
            }

        }

        protected int getCastingInterval() {
            return 200;
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }
    }

    public class ParchedSummonUndeadGoal extends AbstractNecromancer.SummonUndeadGoal {
        @Override
        public void playLaughSound() {
            AbstractParchedNecromancer.this.playSound(getRandomLaughSound(), 2.0F,
                    0.05F);
        }

        public void start() {
            super.start();
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.SUMMON_ANIM);
            AbstractParchedNecromancer.this.setSpellCasting(true);
            AbstractParchedNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.ZOMBIE);
        }

        @Override
        public void stop() {
            super.stop();
            AbstractParchedNecromancer.this.setSpellCasting(false);
            if (!AbstractParchedNecromancer.this.isShooting() && !AbstractParchedNecromancer.this.isSpellCasting()) {
                AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.IDLE_ANIM);
            }
            AbstractParchedNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        public void summonUndeadParticles(ServerLevel serverLevel, Entity entity) {
            ServerParticleUtil.summonUndeadParticles(serverLevel, entity, new ColorUtil(16753408), 16753408, 16777070);
        }
    }

    public class DesertPlaguesGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractParchedNecromancer.this.getTarget();
            return AbstractParchedNecromancer.this.DesertPlaguescooldown <= 0
                    && target != null
                    && target.isAlive()
                    && !AbstractParchedNecromancer.this.isSpellCasting();
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        @Override
        public void start() {
            this.spellTime = 50;
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.STORM_ANIM);
            AbstractParchedNecromancer.this.setSpellCasting(true);
            AbstractParchedNecromancer.this.setNecromancerSpellType(AbstractNecromancer.NecromancerSpellType.CLOUD);
        }

        @Override
        public void stop() {
            AbstractParchedNecromancer.this.DesertPlaguescooldown = 1200;
            AbstractParchedNecromancer.this.setSpellCasting(false);
            AbstractParchedNecromancer.this.setAnimationState(AbstractParchedNecromancer.IDLE_ANIM);
            AbstractParchedNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime == 34) {
                com.k1sak1.goetyawaken.common.magic.spells.wind.DesertPlaguesSpell desertPlaguesSpell = new com.k1sak1.goetyawaken.common.magic.spells.wind.DesertPlaguesSpell();
                ItemStack windStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.WIND_STAFF.get());
                windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), 3);
                windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 3);
                windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), 2);
                desertPlaguesSpell.mobSpellResult(AbstractParchedNecromancer.this, windStaff);
            }
            if (this.spellTime == 15) {
                com.Polarice3.Goety.common.magic.spells.wind.WindHornSpell windHornSpell = new com.Polarice3.Goety.common.magic.spells.wind.WindHornSpell();
                ItemStack windStaff = new ItemStack(com.Polarice3.Goety.common.items.ModItems.WIND_STAFF.get());
                windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), 3);
                windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), 3);
                windStaff.enchant(com.Polarice3.Goety.common.enchantments.ModEnchantments.DURATION.get(), 3);
                windHornSpell.mobSpellResult(AbstractParchedNecromancer.this, windStaff);
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return AbstractNecromancer.NecromancerSpellType.CLOUD;
        }
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return p_34192_.getEffect() != MobEffects.WEAKNESS && super.canBeAffected(p_34192_);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    protected void checkFallDamage(double p_20809_, boolean p_20810_, BlockState p_20811_, BlockPos p_20812_) {
    }

    public net.minecraft.world.InteractionResult mobInteract(net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        if (!this.level().isClientSide) {
            net.minecraft.world.item.ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
                if (itemstack.is(com.k1sak1.goetyawaken.common.items.ModItems.PARCHED_NECROMANCER_SOUL_JAR.get())) {
                    if (!itemstack.isEmpty()) {
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        if (this.getNecroLevel() < 2) {
                            this.setNecroLevel(this.getNecroLevel() + 1);
                        }
                        this.heal(this.getMaxHealth());
                        if (this.level() instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 7; ++i) {
                                double d0 = this.random.nextGaussian() * 0.02D;
                                double d1 = this.random.nextGaussian() * 0.02D;
                                double d2 = this.random.nextGaussian() * 0.02D;
                                serverLevel.sendParticles(
                                        net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                                        this.getRandomX(1.0D),
                                        this.getRandomY() + 0.5D,
                                        this.getRandomZ(1.0D),
                                        0, d0, d1, d2, 0.5F);
                            }
                        }
                        this.playSound(getRandomLaughSound(),
                                2.0F, 0.05F);
                        return net.minecraft.world.InteractionResult.SUCCESS;
                    }
                }

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
        compound.putInt("IdleSpellCool", this.idleSpellCool);
        compound.putInt("RapidSpellCool", this.rapidSpellCool);
        compound.putInt("CycloneCool", this.cycloneCool);
        compound.putInt("DesertPlaguescooldown", this.DesertPlaguescooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAnimationState(compound.getInt("AnimState"));
        this.setNecroLevel(compound.getInt("NecroLevel"));
        if (compound.contains("IdleSpellCool")) {
            this.idleSpellCool = compound.getInt("IdleSpellCool");
        }
        if (compound.contains("RapidSpellCool")) {
            this.rapidSpellCool = compound.getInt("RapidSpellCool");
        }
        if (compound.contains("CycloneCool")) {
            this.cycloneCool = compound.getInt("CycloneCool");
        }
        if (compound.contains("DesertPlaguescooldown")) {
            this.DesertPlaguescooldown = compound.getInt("DesertPlaguescooldown");
        }
    }

    private SoundEvent getRandomShootSound() {
        Random random = new Random();
        int choice = random.nextInt(2);
        switch (choice) {
            case 0:
                return ModSounds.PARCHED_SHOOT_1.get();
            case 1:
                return ModSounds.PARCHED_SHOOT_2.get();
            default:
                return ModSounds.PARCHED_SHOOT_1.get();
        }
    }

    private SoundEvent getRandomLaughSound() {
        Random random = new Random();
        int choice = random.nextInt(3);
        switch (choice) {
            case 0:
                return ModSounds.PARCHED_LAUGH_1.get();
            case 1:
                return ModSounds.PARCHED_LAUGH_2.get();
            case 2:
                return ModSounds.PARCHED_LAUGH_3.get();
            default:
                return ModSounds.PARCHED_LAUGH_1.get();
        }
    }

    private SoundEvent getRandomHurtArmoredSound() {
        Random random = new Random();
        int choice = random.nextInt(2);
        switch (choice) {
            case 0:
                return ModSounds.PARCHED_HURT_ARMORED_1.get();
            case 1:
                return ModSounds.PARCHED_HURT_ARMORED_2.get();
            default:
                return ModSounds.PARCHED_HURT_ARMORED_1.get();
        }
    }

    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.ruinsNecromancerLimit;
    }
}