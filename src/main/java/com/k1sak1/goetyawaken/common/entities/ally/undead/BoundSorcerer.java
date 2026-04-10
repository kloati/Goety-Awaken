package com.k1sak1.goetyawaken.common.entities.ally.undead;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import oshi.util.tuples.Pair;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.*;
import com.Polarice3.Goety.common.magic.spells.abyss.*;
import com.Polarice3.Goety.common.magic.spells.frost.*;
import com.Polarice3.Goety.common.magic.spells.geomancy.*;
import com.Polarice3.Goety.common.magic.spells.necromancy.*;
import com.Polarice3.Goety.common.magic.spells.nether.*;
import com.Polarice3.Goety.common.magic.spells.storm.*;
import com.Polarice3.Goety.common.magic.spells.wild.*;
import com.Polarice3.Goety.common.magic.spells.wind.*;
import com.Polarice3.Goety.common.items.ModItems;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BoundSorcerer extends AbstractBoundIllager {
    protected static final EntityDataAccessor<Byte> IS_CASTING_SPELL = SynchedEntityData.defineId(BoundSorcerer.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(BoundSorcerer.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BoundSorcerer.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(BoundSorcerer.class,
            EntityDataSerializers.INT);
    protected int castingTime;
    protected int[] spellCoolDown = new int[SorcererSpell.values().length + 1];
    protected int[] spellWeights = new int[SorcererSpell.values().length + 1];

    public int coolDown = 0;
    public boolean hasSpawned;
    public static int MIN_LEVEL = 1;
    public static int MAX_LEVEL = 6;
    private SorcererSpell currentSpell = SorcererSpell.FLAMES;

    public BoundSorcerer(EntityType<? extends AbstractBoundIllager> type, Level worldIn) {
        super(type, worldIn);
        for (int i = 0; i < this.spellWeights.length; i++) {
            this.spellWeights[i] = 20;
        }
    }

    public boolean shouldReduceCastTime() {
        return this.getSorcererLevel() >= 6;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new SpellGoal());
        this.goalSelector.addGoal(3, new com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal<>(this,
                LivingEntity.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new com.Polarice3.Goety.common.entities.ai.SurroundGoal<>(this, 1.0F, 8.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CASTING_SPELL, (byte) 0);
        this.entityData.define(CHARGING, false);
        this.entityData.define(SHOOT, false);
        this.entityData.define(LEVEL, 1);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.BoundSorcererFollowRange.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoundSorcererHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.BoundSorcererArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.15D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoundSorcererHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BoundSorcererArmor.get());
    }

    public com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager.BoundArmPose getArmPose() {
        if (this.isShoot()) {
            return com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager.BoundArmPose.CROSSBOW_HOLD;
        } else if (this.isCharging()) {
            return com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager.BoundArmPose.ATTACKING;
        } else if (this.isCastingSpell2()) {
            return com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager.BoundArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating()
                    ? com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager.BoundArmPose.CELEBRATING
                    : com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager.BoundArmPose.CROSSED;
        }
    }

    public boolean isCastingSpell2() {
        if (this.level().isClientSide) {
            return this.entityData.get(IS_CASTING_SPELL) > 0;
        } else {
            return this.castingTime > 0;
        }
    }

    public void setIsCastingSpell(int id) {
        this.entityData.set(IS_CASTING_SPELL, (byte) id);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }

    public boolean isShoot() {
        return this.entityData.get(SHOOT);
    }

    public void setShoot(boolean shoot) {
        this.entityData.set(SHOOT, shoot);
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.setCharging(true);
        } else if (p_21375_ == 5) {
            this.setCharging(false);
        } else if (p_21375_ == 6) {
            this.setShoot(true);
        } else if (p_21375_ == 7) {
            this.setShoot(false);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.castingTime > 0) {
            --this.castingTime;
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

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getY() + 0.5D,
                        this.getRandomZ(0.5D), (0.5D - this.random.nextDouble()) * 0.15D, 0.01F,
                        (0.5D - this.random.nextDouble()) * 0.15D);
            }
        }
        if (!this.level().isClientSide) {
            for (SorcererSpell spell : SorcererSpell.values()) {
                if (this.spellCoolDown[spell.trueId] > 0) {
                    --this.spellCoolDown[spell.trueId];
                }
            }
            if (this.coolDown > 0) {
                --this.coolDown;
            }
            if (this.tickCount % 20 == 0) {
                for (int i = 0; i < this.spellWeights.length; i++) {
                    this.spellWeights[i] = Math.min(this.spellWeights[i] + 20, 1000);
                }
            }
            if (this.tickCount % 20 == 0) {
                this.updateHealSpellWeights();
                this.updateRepelSpellWeights();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean result = super.hurt(pSource, pAmount);
        if (result) {
            for (SorcererSpell spell : SorcererSpell.values()) {
                if (spell == SorcererSpell.IRON_HIDE || spell == SorcererSpell.BULWARK
                        || spell == SorcererSpell.CHILL_HIDE) {
                    this.spellWeights[spell.trueId] = Math.min(this.spellWeights[spell.trueId] + 40, 1000);
                }
            }
        }
        return result;
    }

    public void updateHealSpellWeights() {
        float healthRatio = this.getMaxHealth() > 0 ? this.getHealth() / this.getMaxHealth() : 0;
        float lostHealthPercent = (1.0f - healthRatio) * 100;
        int healWeightIncrease = (int) (2 * lostHealthPercent);

        for (SorcererSpell spell : SorcererSpell.values()) {
            if (spell == SorcererSpell.HEAL || spell == SorcererSpell.HEAL2) {
                this.spellWeights[spell.trueId] = Math.min(this.spellWeights[spell.trueId] + healWeightIncrease, 1000);
            }
        }
    }

    public void updateRepelSpellWeights() {
        LivingEntity target = this.getTarget();
        if (target != null) {
            double distance = this.distanceTo(target);
            if (distance < 8.0) {
                for (SorcererSpell spell : SorcererSpell.values()) {
                    if (spell == SorcererSpell.ICE_STORM || spell == SorcererSpell.TIDAL
                            || spell == SorcererSpell.FROST_NOVA ||
                            spell == SorcererSpell.DISCHARGE || spell == SorcererSpell.WIND_HORN) {
                        this.spellWeights[spell.trueId] = Math.min(this.spellWeights[spell.trueId] + 10, 1000);
                    }
                }
            }
        }
    }

    public void resetSpellWeight(SorcererSpell spell) {
        this.spellWeights[spell.trueId] = 20;
    }

    protected int getSpellCastingTime() {
        return this.castingTime;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SORCERER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SORCERER_DEATH.get();
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SorcererLevel")) {
            boolean heal = !compound.getBoolean("HasSpawned");
            this.setSorcererLevel(compound.getInt("SorcererLevel"), heal);
        } else if (compound.contains("Level")) {
            boolean heal = !compound.getBoolean("HasSpawned");
            this.setSorcererLevel(compound.getInt("Level"), heal);
        }
        this.castingTime = compound.getInt("SorcererSpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SorcererLevel", this.getSorcererLevel());
        compound.putInt("SorcererSpellTicks", this.castingTime);
        compound.putBoolean("HasSpawned", this.hasSpawned);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.SORCERER_HURT.get();
    }

    public int getSorcererLevel() {
        return this.entityData.get(LEVEL);
    }

    public void setSorcererLevel(int level, boolean heal) {
        int i = net.minecraft.util.Mth.clamp(level, 1, 6);
        this.entityData.set(LEVEL, i);
        if (com.Polarice3.Goety.config.MobsConfig.SorcererHPIncrease.get()) {
            net.minecraft.world.entity.ai.attributes.AttributeInstance health = this
                    .getAttribute(Attributes.MAX_HEALTH);
            if (health != null && i > 1) {
                float increase = (i - 1) * 1.25F;
                health.setBaseValue(AttributesConfig.BoundSorcererHealth.get() * increase);
            }
            if (heal) {
                this.setHealth(this.getMaxHealth());
            }
        }

        this.xpReward = i * 8;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
            MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.CONVERSION) {
            if (this.entityData.get(LEVEL) <= 1) {
                this.setSorcererLevel(1, true);
            }
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return ModSounds.CAST_SPELL.get();
    }

    public ItemStack getBanner() {
        ItemStack headItem = this.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.getItem() instanceof BannerItem) {
            return headItem;
        }
        return ItemStack.EMPTY;
    }

    public int getBoundSorcererLevel() {
        return this.entityData.get(LEVEL);
    }

    public void setBoundSorcererLevel(int level) {
        int i = net.minecraft.util.Mth.clamp(level, 1, 6);
        this.entityData.set(LEVEL, i);
        if (com.Polarice3.Goety.config.MobsConfig.SorcererHPIncrease.get()) {
            net.minecraft.world.entity.ai.attributes.AttributeInstance health = this
                    .getAttribute(Attributes.MAX_HEALTH);
            if (health != null && i > 1) {
                float increase = (i - 1) * 1.25F;
                health.setBaseValue(AttributesConfig.BoundSorcererHealth.get() * increase);
            }
            this.setHealth(this.getMaxHealth());
        }
        this.xpReward = i * 8;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.SORCERER_AMBIENT.get();
    }

    @Override
    public void die(DamageSource pCause) {
        this.playSound(ModSounds.DEAD_MOAN.get(), 2.0F, 1.0F);
        super.die(pCause);
    }

    @Override
    public float getVoicePitch() {
        return 0.45F;
    }

    class CastingSpellGoal extends Goal {
        private CastingSpellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return BoundSorcerer.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            BoundSorcerer.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            BoundSorcerer.this.setIsCastingSpell(0);
            BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 5);
            BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 7);
            BoundSorcerer.this.coolDown = 20;
        }

        public void tick() {
            if (BoundSorcerer.this.getTarget() != null) {
                MobUtil.instaLook(BoundSorcerer.this, BoundSorcerer.this.getTarget());
            }
            BoundSorcerer.this.getNavigation().stop();
            BoundSorcerer.this.getMoveControl().strafe(0.0F, 0.0F);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    protected abstract class BoundSorcererUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        public boolean canUse() {
            LivingEntity livingentity = BoundSorcerer.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                return !BoundSorcerer.this.isCastingSpell2() && BoundSorcerer.this.hasLineOfSight(livingentity)
                        && BoundSorcerer.this.coolDown <= 0;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = BoundSorcerer.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            BoundSorcerer.this.castingTime = this.getAdjustedCastingTime();
            BoundSorcerer.this.spellCoolDown[this.getSpell().trueId] = this.getCastingInterval();
            this.nextAttackTickCount = BoundSorcerer.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                BoundSorcerer.this.playSound(soundevent, 1.0F, 1.0F);
            }
            BoundSorcerer.this.setIsCastingSpell(this.getSpell().trueId);
            BoundSorcerer.this.currentSpell = this.getSpell();
        }

        public void stop() {
            super.stop();
            BoundSorcerer.this.setIsCastingSpell(0);
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected int getAdjustedCastingTime() {
            int castingTime = this.getCastingTime();
            if (BoundSorcerer.this.shouldReduceCastTime()) {
                castingTime = castingTime / 2;
            }
            return castingTime;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract SorcererSpell getSpell();
    }

    class SpellGoal extends BoundSorcererUseSpellGoal {
        public SorcererSpell spell;
        public int chargeTicks;

        @Override
        public boolean canUse() {
            List<SorcererSpell> spells = new ArrayList<>();
            List<Integer> weights = new ArrayList<>();
            int totalWeight = 0;

            for (SorcererSpell spell1 : SorcererSpell.values()) {
                if (BoundSorcerer.this.getSorcererLevel() >= spell1.minLevel
                        && BoundSorcerer.this.getSorcererLevel() <= spell1.maxLevel) {
                    if (spell1.getSpell().conditionsMet(BoundSorcerer.this.level(), BoundSorcerer.this)) {
                        if (BoundSorcerer.this.spellCoolDown[spell1.trueId] <= 0) {
                            if (spell1.getSpell() instanceof com.Polarice3.Goety.common.magic.SummonSpell
                                    && !BoundSorcerer.this.hasEffect(
                                            com.Polarice3.Goety.common.effects.GoetyEffects.SUMMON_DOWN.get())) {
                                spells.add(spell1);
                                int weight = BoundSorcerer.this.spellWeights[spell1.trueId];
                                weights.add(weight);
                                totalWeight += weight;
                            } else if (!(spell1.getSpell() instanceof com.Polarice3.Goety.common.magic.SummonSpell)) {
                                spells.add(spell1);
                                int weight = BoundSorcerer.this.spellWeights[spell1.trueId];
                                weights.add(weight);
                                totalWeight += weight;
                            }
                        }
                    }
                }
            }

            if (!spells.isEmpty() && totalWeight > 0) {
                int randomValue = BoundSorcerer.this.random.nextInt(totalWeight);
                int currentWeight = 0;

                for (int i = 0; i < spells.size(); i++) {
                    currentWeight += weights.get(i);
                    if (randomValue < currentWeight) {
                        this.spell = spells.get(i);
                        break;
                    }
                }
            } else {
                this.spell = null;
            }

            if (this.spell != null && this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IChargingSpell) {
                this.chargeTicks = 20;
            }
            return this.spell != null && super.canUse();
        }

        public void tick() {
            super.tick();
            if (this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IChargingSpell) {
                if (!this.spell.getSpell().conditionsMet(BoundSorcerer.this.level(), BoundSorcerer.this)) {
                    this.cancelSpell();
                }
                --this.chargeTicks;
                if (this.chargeTicks <= 0) {
                    com.Polarice3.Goety.common.magic.Spell spell1 = this.spell.getSpell();
                    com.Polarice3.Goety.common.magic.SpellStat spellStat = com.Polarice3.Goety.utils.WandUtil
                            .getStats(BoundSorcerer.this, spell1);
                    if (this.spell.levelIncrease) {
                        spellStat.setPotency(BoundSorcerer.this.getSorcererLevel() - this.spell.minLevel);
                    }
                    spell1.mobSpellResult(BoundSorcerer.this,
                            BoundSorcerer.this.getSorcererLevel() >= this.spell.upgradeStaff.getB()
                                    ? this.spell.upgradeStaff.getA()
                                    : ItemStack.EMPTY,
                            spellStat);
                    if (this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IBreathingSpell breathingSpell) {
                        if (BoundSorcerer.this.getTarget() != null) {
                            com.Polarice3.Goety.utils.MobUtil.instaLook(BoundSorcerer.this,
                                    BoundSorcerer.this.getTarget());
                        }
                        breathingSpell.showWandBreath(BoundSorcerer.this,
                                com.Polarice3.Goety.utils.WandUtil.getStats(BoundSorcerer.this, breathingSpell));
                    }
                    BoundSorcerer.this.resetSpellWeight(this.spell);
                }
                BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 4);
            } else {
                BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 5);
                if (this.spell.throwingSpell()) {
                    BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 6);
                } else {
                    BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 7);
                }
            }
            this.spell.getSpell().useParticle(BoundSorcerer.this.level(), BoundSorcerer.this, ItemStack.EMPTY);
        }

        public void cancelSpell() {
            this.attackWarmupDelay = 0;
            BoundSorcerer.this.castingTime = 0;
            BoundSorcerer.this.setIsCastingSpell(0);
            BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 5);
            BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 7);
            BoundSorcerer.this.coolDown = 20;
        }

        @Override
        protected void performSpellCasting() {
            if (BoundSorcerer.this.getTarget() != null) {
                Spell spell1 = this.spell.getSpell();
                SpellStat spellStat = com.Polarice3.Goety.utils.WandUtil.getStats(BoundSorcerer.this, spell1);
                if (this.spell.levelIncrease) {
                    spellStat.setPotency(
                            spellStat.getPotency() + (BoundSorcerer.this.getSorcererLevel() - this.spell.minLevel));
                }
                if (this.spell.throwingSpell()) {
                    BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 6);
                } else {
                    BoundSorcerer.this.level().broadcastEntityEvent(BoundSorcerer.this, (byte) 7);
                }
                spell1.mobSpellResult(BoundSorcerer.this,
                        BoundSorcerer.this.getSorcererLevel() >= this.spell.upgradeStaff.getB()
                                ? this.spell.upgradeStaff.getA()
                                : ItemStack.EMPTY,
                        spellStat);
                BoundSorcerer.this.resetSpellWeight(this.spell);
            }
        }

        @Override
        protected int getCastWarmupTime() {
            if (this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IChargingSpell chargingSpell) {
                int warmupTime = chargingSpell.shotsNumber(BoundSorcerer.this, ItemStack.EMPTY);
                if (BoundSorcerer.this.shouldReduceCastTime()) {
                    warmupTime = warmupTime / 2;
                }
                return warmupTime;
            }
            int warmupTime = this.spell.getSpell().defaultCastDuration() + 5;
            if (BoundSorcerer.this.shouldReduceCastTime()) {
                warmupTime = warmupTime / 2;
            }
            return warmupTime;
        }

        @Override
        protected int getCastingTime() {
            if (this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IChargingSpell chargingSpell) {
                return chargingSpell.shotsNumber(BoundSorcerer.this, ItemStack.EMPTY);
            }
            int castingTime = this.spell.getSpell().defaultCastDuration() + 5;
            return castingTime;
        }

        @Override
        protected int getCastingInterval() {
            if (this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IChargingSpell chargingSpell) {
                int interval = chargingSpell.defaultSpellCooldown() * 2;
                return interval;
            }
            int interval = this.spell.getSpell().defaultSpellCooldown();
            return interval;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return this.spell.getSpell().CastingSound(BoundSorcerer.this);
        }

        @Override
        protected SorcererSpell getSpell() {
            return this.spell;
        }

    }

    protected enum SorcererSpell {
        FLAMES(new FireBreathSpell(), nextID(), 1, 3),
        IRON_HIDE(new IronHideSpell(), nextID(), 1, 6, true),
        SUMMON_HOUND(new HuntingSpell(), nextID(), 1, 1),
        HEAL(new SoulHealSpell(), nextID(), 1, 6, true),
        HEAL2(new SoulHealSpell(), nextID(), 6, 6, true),
        FROST(new FrostBreathSpell(), nextID(), 2, 3),
        SUMMON_BEAR(new MaulingSpell(), nextID(), 2, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.WILD_STAFF.get()), 6)),
        FANGS(new FangSpell(), nextID(), 2, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.OMINOUS_STAFF.get()), 6)),
        SUMMON_ICE_GOLEM(new IceGolemSpell(), nextID(), 3, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.FROST_STAFF.get()), 6)),
        ICE_SPIKE(new IceSpikeSpell(), nextID(), 3, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.FROST_STAFF.get()), 6)),
        THUNDERBOLT(new ThunderboltSpell(), nextID(), 3, 6, true,
                new Pair<>(new ItemStack(ModItems.STORM_STAFF.get()), 4)),
        SCATTER(new ScatterSpell(), nextID(), 3, 5, true),
        ICE_STORM(new IceStormSpell(), nextID(), 4, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.FROST_STAFF.get()), 6)),
        BULWARK(new BulwarkSpell(), nextID(), 4, 6, true),
        ELECTRO(new ElectroOrbSpell(), nextID(), 4, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.STORM_STAFF.get()), 6)),
        BOUNCY_BUBBLE(new BouncyBubbleSpell(), nextID(), 4, 5, true),
        ARROW_RAIN(new ArrowRainSpell(), nextID(), 4, 5, true),
        VEX(new VexSpell(), nextID(), 4, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.OMINOUS_STAFF.get()), 6)),
        CYCLONE(new CycloneSpell(), nextID(), 5, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.WIND_STAFF.get()), 6)),
        ERUPTION(new EruptionSpell(), nextID(), 5, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.GEO_STAFF.get()), 5)),
        TIDAL(new TidalSpell(), nextID(), 5, 6, true),
        BIOMINE(new BioMineSpell(), nextID(), 5, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.ABYSS_STAFF.get()), 6)),
        BLOSSOM(new BlossomSpell(), nextID(), 6, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.WILD_STAFF.get()), 6)),
        MAGMA_BOMB(new MagmaSpell(), nextID(), 6, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.NETHER_STAFF.get()), 6)),
        RAZOR_WIND(new RazorWindSpell(), nextID(), 6, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.WIND_STAFF.get()), 6)),
        WEAKENING(new WeakeningSpell(), nextID(), 5, 6, true),
        MAGIC_BOLT(new MagicBoltSpell(), nextID(), 6, 6),
        ENTANGLING(new EntanglingSpell(), nextID(), 5, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.WILD_STAFF.get()), 6)),
        POISON_DART(new PoisonDartSpell(), nextID(), 1, 3, true),
        SOUL_BOLT(new SoulBoltSpell(), nextID(), 1, 2, true),
        CHILL_HIDE(new ChillHideSpell(), nextID(), 3, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.FROST_STAFF.get()), 6)),
        FROST_NOVA(new FrostNovaSpell(), nextID(), 5, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.FROST_STAFF.get()), 6)),
        ICE_BOUQUET(new IceBouquetSpell(), nextID(), 4, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.NECRO_STAFF.get()), 6)),
        WIND_HORN(new WindHornSpell(), nextID(), 3, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.WIND_STAFF.get()), 6)),
        DISCHARGE(new DischargeSpell(), nextID(), 6, 6, true,
                new Pair<ItemStack, Integer>(new ItemStack(ModItems.STORM_STAFF.get()), 6));

        final Spell spell;
        private static int id = 0;
        final int trueId;
        final int minLevel;
        final int maxLevel;
        final boolean levelIncrease;
        final Pair<ItemStack, Integer> upgradeStaff;

        public static int nextID() {
            return id++;
        }

        SorcererSpell(Spell spell, int id, int minLevel, int maxLevel) {
            this(spell, id, minLevel, maxLevel, false, new Pair<>(ItemStack.EMPTY, 0));
        }

        SorcererSpell(Spell spell, int id, int minLevel, int maxLevel, boolean levelIncrease) {
            this(spell, id, minLevel, maxLevel, levelIncrease, new Pair<>(ItemStack.EMPTY, 0));
        }

        SorcererSpell(Spell spell, int id, int minLevel, int maxLevel, boolean levelIncrease,
                Pair<ItemStack, Integer> upgradeStaff) {
            this.spell = spell;
            this.trueId = id;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.levelIncrease = levelIncrease;
            this.upgradeStaff = upgradeStaff;
        }

        public Spell getSpell() {
            return this.spell;
        }

        public boolean throwingSpell() {
            return this == ICE_SPIKE || this == THUNDERBOLT || this == ELECTRO || this == CYCLONE || this == SOUL_BOLT
                    || this == POISON_DART;
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (this.isHostile()) {
            int level = this.getSorcererLevel();
            int emeraldMin = Math.max(0, level - 1);
            int emeraldMax = 2 + level;
            int emeraldCount = this.random.nextInt(emeraldMax - emeraldMin + 1) + emeraldMin;
            if (emeraldCount > 0) {
                for (int i = 0; i < emeraldCount; i++) {
                    this.spawnAtLocation(Items.EMERALD);
                }
            }
            int awakenedEmeraldMin = 3;
            int awakenedEmeraldMax = 4 + level;
            int awakenedEmeraldCount = this.random.nextInt(awakenedEmeraldMax - awakenedEmeraldMin + 1)
                    + awakenedEmeraldMin;
            if (awakenedEmeraldCount > 0) {
                for (int i = 0; i < awakenedEmeraldCount; i++) {
                    this.spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.MAGIC_EMERALD.get());
                }
            }
            if (this.random.nextFloat() < 0.5f) {
                int emptyFocusMin = Math.max(0, level - 1);
                int emptyFocusMax = level;
                int emptyFocusCount = this.random.nextInt(emptyFocusMax - emptyFocusMin + 1) + emptyFocusMin;
                if (emptyFocusCount > 0) {
                    for (int i = 0; i < emptyFocusCount; i++) {
                        this.spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.EMPTY_FOCUS.get());
                    }
                }
            }
            if (this.random.nextFloat() < (0.05f * level)) {
                int animationCoreMin = Math.max(0, level - 1);
                int animationCoreMax = level;
                int animationCoreCount = this.random.nextInt(animationCoreMax - animationCoreMin + 1)
                        + animationCoreMin;
                if (animationCoreCount > 0) {
                    for (int i = 0; i < animationCoreCount; i++) {
                        this.spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.ANIMATION_CORE.get());
                    }
                }
            }
            if (this.random.nextFloat() < (0.05f * level)) {
                int mysticCoreMin = Math.max(0, level - 1);
                int mysticCoreMax = level;
                int mysticCoreCount = this.random.nextInt(mysticCoreMax - mysticCoreMin + 1) + mysticCoreMin;
                if (mysticCoreCount > 0) {
                    for (int i = 0; i < mysticCoreCount; i++) {
                        this.spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.MYSTIC_CORE.get());
                    }
                }
            }
            if (this.random.nextFloat() < (0.05f * level)) {
                int hungerCoreMin = Math.max(0, level - 1);
                int hungerCoreMax = level;
                int hungerCoreCount = this.random.nextInt(hungerCoreMax - hungerCoreMin + 1) + hungerCoreMin;
                if (hungerCoreCount > 0) {
                    for (int i = 0; i < hungerCoreCount; i++) {
                        this.spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.HUNGER_CORE.get());
                    }
                }
            }
            if (this.random.nextFloat() < (0.05f * level)) {
                int windCoreMin = Math.max(0, level - 1);
                int windCoreMax = level;
                int windCoreCount = this.random.nextInt(windCoreMax - windCoreMin + 1) + windCoreMin;
                if (windCoreCount > 0) {
                    for (int i = 0; i < windCoreCount; i++) {
                        this.spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.WIND_CORE.get());
                    }
                }
            }
            if (this.random.nextFloat() < (0.05f * level)) {
                int concentratedEmeraldMin = Math.max(0, level - 1);
                int concentratedEmeraldMax = level;
                int concentratedEmeraldCount = this.random.nextInt(concentratedEmeraldMax - concentratedEmeraldMin + 1)
                        + concentratedEmeraldMin;
                if (concentratedEmeraldCount > 0) {
                    for (int i = 0; i < concentratedEmeraldCount; i++) {
                        this.spawnAtLocation(com.Polarice3.Goety.common.items.ModItems.SOUL_EMERALD.get());
                    }
                }
            }
        }
        if (!this.isHostile()) {
            if (this.getSorcererLevel() >= 6) {
                ItemStack grimoireStack = new ItemStack(
                        com.k1sak1.goetyawaken.common.items.ModItems.RUBY_GRIMOIRE.get());
                if (this.getTrueOwner() != null) {
                    FlyingItem flyingItem = new FlyingItem(
                            ModEntityType.FLYING_ITEM.get(),
                            this.level(),
                            this.getX(),
                            this.getY() + 1.0D,
                            this.getZ());

                    flyingItem.setOwner(this.getTrueOwner());
                    flyingItem.setItem(grimoireStack);
                    flyingItem.setParticle(ParticleTypes.SOUL);
                    flyingItem.setSecondsCool(30);

                    this.level().addFreshEntity(flyingItem);
                } else {
                    ItemEntity itemEntity = this.spawnAtLocation(grimoireStack);
                    if (itemEntity != null) {
                        itemEntity.setExtendedLifetime();
                    }
                }
            }
        }
    }

    @Override
    public boolean canBeLeader() {
        return true;
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        if (super.isAlliedTo(entityIn)) {
            return true;
        }
        if (this.isHostile() && entityIn instanceof AbstractIllager) {
            return true;
        }
        return false;
    }
}
