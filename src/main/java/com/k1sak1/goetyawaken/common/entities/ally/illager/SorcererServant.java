package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.BoundSorcerer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.nbt.CompoundTag;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.particles.ParticleTypes;

public class SorcererServant extends SpellcasterIllagerServant {
    protected static final EntityDataAccessor<Byte> IS_CASTING_SPELL = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.INT);
    protected int castingTime;
    protected int[] spellCoolDown = new int[SorcererSpell.values().length + 1];
    protected int[] spellWeights = new int[SorcererSpell.values().length + 1];
    protected static final EntityDataAccessor<Boolean> IS_TRADING = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.BOOLEAN);
    private boolean isCurrentlyTrading = false;
    private int moneyAmount = 0;
    private int tradingProgress = 0;
    private int tradingDelay = 0;
    private java.util.List<net.minecraft.world.item.ItemStack> tradeItems = new java.util.ArrayList<>();

    public int coolDown = 0;
    public boolean hasSpawned;
    public static int MIN_LEVEL = 1;
    public static int MAX_LEVEL = 6;
    private SorcererSpell currentSpell = SorcererSpell.FLAMES;

    public SorcererServant(EntityType<? extends SorcererServant> type, Level worldIn) {
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
        this.goalSelector.addGoal(1, new SorcererTradeGoal(this));
        this.goalSelector.addGoal(2, new CastingSpellGoal());
        this.goalSelector.addGoal(3, new SpellGoal());
        this.goalSelector.addGoal(4, new com.Polarice3.Goety.common.entities.ai.SurroundGoal<>(this, 1.0F, 8.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CASTING_SPELL, (byte) 0);
        this.entityData.define(CHARGING, false);
        this.entityData.define(SHOOT, false);
        this.entityData.define(LEVEL, 1);
        this.entityData.define(IS_TRADING, false);
    }

    public com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant.IllagerServantArmPose getArmPose() {
        if (this.isShoot()) {
            return com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant.IllagerServantArmPose.CROSSBOW_HOLD;
        } else if (this.isCharging()) {
            return com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant.IllagerServantArmPose.ATTACKING;
        } else if (this.isCastingSpell2()) {
            return com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant.IllagerServantArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating()
                    ? com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant.IllagerServantArmPose.CELEBRATING
                    : com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant.IllagerServantArmPose.CROSSED;
        }
    }

    public boolean isCastingSpell() {
        return false;
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

    public boolean isCurrentlyTrading() {
        return this.isCurrentlyTrading;
    }

    public void setIsCurrentlyTrading(boolean trading) {
        this.isCurrentlyTrading = trading;
        this.entityData.set(IS_TRADING, trading);
    }

    public boolean getIsCurrentlyTrading() {
        return this.entityData.get(IS_TRADING);
    }

    public int getMoneyAmount() {
        return this.moneyAmount;
    }

    public void setMoneyAmount(int money) {
        this.moneyAmount = money;
    }

    public int getTradingProgress() {
        return this.tradingProgress;
    }

    public void setTradingProgress(int progress) {
        this.tradingProgress = progress;
    }

    public int getTradingDelay() {
        return this.tradingDelay;
    }

    public void setTradingDelay(int delay) {
        this.tradingDelay = delay;
    }

    public java.util.List<net.minecraft.world.item.ItemStack> getTradeItems() {
        return this.tradeItems;
    }

    public void clearTradeItems() {
        this.tradeItems.clear();
    }

    public void addTradeItem(net.minecraft.world.item.ItemStack item) {
        this.tradeItems.add(item);
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

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.SorcererHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SorcererArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SorcererDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SorcererHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SorcererArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SorcererDamage.get());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.castingTime > 0) {
            --this.castingTime;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasSpawned) {
            this.hasSpawned = true;
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

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.SORCERER_AMBIENT.get();
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
                health.setBaseValue(AttributesConfig.SorcererHealth.get() * increase);
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

    @Override
    public void die(DamageSource pCause) {
        if (!this.level().isClientSide) {
            if (this.getIdol() == null) {
                if (this.getTrueOwner() != null) {
                    if (CuriosFinder.hasNamelessSet(this.getTrueOwner())) {
                        BoundSorcerer boundSorcerer = this.convertTo(
                                com.k1sak1.goetyawaken.common.entities.ModEntityType.BOUND_SORCERER.get(),
                                true);
                        if (boundSorcerer != null) {
                            boundSorcerer.setTrueOwner(this.getTrueOwner());
                            int currentLevel = this.getSorcererLevel();
                            boundSorcerer.setBoundSorcererLevel(currentLevel);
                            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, boundSorcerer);
                            if (!this.isSilent()) {
                                this.level().levelEvent((Player) null, 1026, this.blockPosition(), 0);
                            }
                        }
                    }
                }
            }
        }
        super.die(pCause);
    }

    @Nullable
    @Override
    public <T extends Mob> T convertTo(EntityType<T> entityType, boolean keepEquipment) {
        int currentLevel = this.getSorcererLevel();
        T converted = super.convertTo(entityType, keepEquipment);
        if (converted instanceof SorcererServant sorcererServant) {
            int newLevel = Math.min(currentLevel + 1, MAX_LEVEL);
            sorcererServant.setSorcererLevel(newLevel, true);
        }

        return converted;
    }

    class CastingSpellGoal extends Goal {
        private CastingSpellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return SorcererServant.this.getSpellCastingTime() > 0;
        }

        public void start() {
            super.start();
            SorcererServant.this.navigation.stop();
        }

        public void stop() {
            super.stop();
            SorcererServant.this.setIsCastingSpell(0);
            SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 5);
            SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 7);
            SorcererServant.this.coolDown = 20;
        }

        public void tick() {
            if (SorcererServant.this.getTarget() != null) {
                MobUtil.instaLook(SorcererServant.this, SorcererServant.this.getTarget());
            }
            SorcererServant.this.getNavigation().stop();
            SorcererServant.this.getMoveControl().strafe(0.0F, 0.0F);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    protected abstract class SorcererUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        public boolean canUse() {
            LivingEntity livingentity = SorcererServant.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                return !SorcererServant.this.isCastingSpell2() && SorcererServant.this.hasLineOfSight(livingentity)
                        && SorcererServant.this.coolDown <= 0;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = SorcererServant.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            SorcererServant.this.castingTime = this.getAdjustedCastingTime();
            SorcererServant.this.spellCoolDown[this.getSpell().trueId] = this.getCastingInterval();
            this.nextAttackTickCount = SorcererServant.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                SorcererServant.this.playSound(soundevent, 1.0F, 1.0F);
            }
            SorcererServant.this.setIsCastingSpell(this.getSpell().trueId);
            SorcererServant.this.currentSpell = this.getSpell();
        }

        public void stop() {
            super.stop();
            SorcererServant.this.setIsCastingSpell(0);
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
            if (SorcererServant.this.shouldReduceCastTime()) {
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

    class SpellGoal extends SorcererUseSpellGoal {
        public SorcererSpell spell;
        public int chargeTicks;

        @Override
        public boolean canUse() {
            List<SorcererSpell> spells = new ArrayList<>();
            List<Integer> weights = new ArrayList<>();
            int totalWeight = 0;

            for (SorcererSpell spell1 : SorcererSpell.values()) {
                if (SorcererServant.this.getSorcererLevel() >= spell1.minLevel
                        && SorcererServant.this.getSorcererLevel() <= spell1.maxLevel) {
                    if (spell1.getSpell().conditionsMet(SorcererServant.this.level(), SorcererServant.this)) {
                        if (SorcererServant.this.spellCoolDown[spell1.trueId] <= 0) {
                            if (spell1.getSpell() instanceof com.Polarice3.Goety.common.magic.SummonSpell
                                    && !SorcererServant.this.hasEffect(
                                            com.Polarice3.Goety.common.effects.GoetyEffects.SUMMON_DOWN.get())) {
                                spells.add(spell1);
                                int weight = SorcererServant.this.spellWeights[spell1.trueId];
                                weights.add(weight);
                                totalWeight += weight;
                            } else if (!(spell1.getSpell() instanceof com.Polarice3.Goety.common.magic.SummonSpell)) {
                                spells.add(spell1);
                                int weight = SorcererServant.this.spellWeights[spell1.trueId];
                                weights.add(weight);
                                totalWeight += weight;
                            }
                        }
                    }
                }
            }

            if (!spells.isEmpty() && totalWeight > 0) {
                int randomValue = SorcererServant.this.random.nextInt(totalWeight);
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
                if (!this.spell.getSpell().conditionsMet(SorcererServant.this.level(), SorcererServant.this)) {
                    this.cancelSpell();
                }
                --this.chargeTicks;
                if (this.chargeTicks <= 0) {
                    com.Polarice3.Goety.common.magic.Spell spell1 = this.spell.getSpell();
                    com.Polarice3.Goety.common.magic.SpellStat spellStat = com.Polarice3.Goety.utils.WandUtil
                            .getStats(SorcererServant.this, spell1);
                    if (this.spell.levelIncrease) {
                        spellStat.setPotency(SorcererServant.this.getSorcererLevel() - this.spell.minLevel);
                    }
                    spell1.mobSpellResult(SorcererServant.this,
                            SorcererServant.this.getSorcererLevel() >= this.spell.upgradeStaff.getB()
                                    ? this.spell.upgradeStaff.getA()
                                    : ItemStack.EMPTY,
                            spellStat);
                    if (this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IBreathingSpell breathingSpell) {
                        if (SorcererServant.this.getTarget() != null) {
                            com.Polarice3.Goety.utils.MobUtil.instaLook(SorcererServant.this,
                                    SorcererServant.this.getTarget());
                        }
                        breathingSpell.showWandBreath(SorcererServant.this,
                                com.Polarice3.Goety.utils.WandUtil.getStats(SorcererServant.this, breathingSpell));
                    }
                    SorcererServant.this.resetSpellWeight(this.spell);
                }
                SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 4);
            } else {
                SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 5);
                if (this.spell.throwingSpell()) {
                    SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 6);
                } else {
                    SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 7);
                }
            }
            this.spell.getSpell().useParticle(SorcererServant.this.level(), SorcererServant.this, ItemStack.EMPTY);
        }

        public void cancelSpell() {
            this.attackWarmupDelay = 0;
            SorcererServant.this.castingTime = 0;
            SorcererServant.this.setIsCastingSpell(0);
            SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 5);
            SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 7);
            SorcererServant.this.coolDown = 20;
        }

        @Override
        protected void performSpellCasting() {
            if (SorcererServant.this.getTarget() != null) {
                Spell spell1 = this.spell.getSpell();
                SpellStat spellStat = com.Polarice3.Goety.utils.WandUtil.getStats(SorcererServant.this, spell1);
                if (this.spell.levelIncrease) {
                    spellStat.setPotency(
                            spellStat.getPotency() + (SorcererServant.this.getSorcererLevel() - this.spell.minLevel));
                }
                if (this.spell.throwingSpell()) {
                    SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 6);
                } else {
                    SorcererServant.this.level().broadcastEntityEvent(SorcererServant.this, (byte) 7);
                }
                spell1.mobSpellResult(SorcererServant.this,
                        SorcererServant.this.getSorcererLevel() >= this.spell.upgradeStaff.getB()
                                ? this.spell.upgradeStaff.getA()
                                : ItemStack.EMPTY,
                        spellStat);
                SorcererServant.this.resetSpellWeight(this.spell);
            }
        }

        @Override
        protected int getCastWarmupTime() {
            if (this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IChargingSpell chargingSpell) {
                int warmupTime = chargingSpell.shotsNumber(SorcererServant.this, ItemStack.EMPTY);
                if (SorcererServant.this.shouldReduceCastTime()) {
                    warmupTime = warmupTime / 2;
                }
                return warmupTime;
            }
            int warmupTime = this.spell.getSpell().defaultCastDuration() + 5;
            if (SorcererServant.this.shouldReduceCastTime()) {
                warmupTime = warmupTime / 2;
            }
            return warmupTime;
        }

        @Override
        protected int getCastingTime() {
            if (this.spell.getSpell() instanceof com.Polarice3.Goety.api.magic.IChargingSpell chargingSpell) {
                return chargingSpell.shotsNumber(SorcererServant.this, ItemStack.EMPTY);
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
            return this.spell.getSpell().CastingSound(SorcererServant.this);
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
    protected void pickUpItem(ItemEntity pItemEntity) {
        ItemStack itemstack = pItemEntity.getItem();
        if (itemstack.getItem() == Items.TOTEM_OF_UNDYING) {
            if (this.getInventory().canAddItem(itemstack)) {
                this.onItemPickup(pItemEntity);
                this.getInventory().addItem(itemstack);
                this.take(pItemEntity, itemstack.getCount());
                pItemEntity.discard();
            } else {
                super.pickUpItem(pItemEntity);
            }
        } else {
            super.pickUpItem(pItemEntity);
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
    public Component getName() {
        if (this.isHostile()) {
            return Component.translatable("entity.goety.sorcerer");
        } else {
            return super.getName();
        }
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(net.minecraft.world.entity.player.Player pPlayer,
            net.minecraft.world.InteractionHand pHand) {
        net.minecraft.world.item.ItemStack itemstack = pPlayer.getItemInHand(pHand);
        net.minecraft.world.item.Item item = itemstack.getItem();
        boolean isOwner = this.getTrueOwner() != null && pPlayer == this.getTrueOwner();
        if (itemstack.getItem() == net.minecraft.world.item.Items.EMERALD
                && pHand == net.minecraft.world.InteractionHand.MAIN_HAND) {
            if (isOwner) {
                if (!this.isAggressive() && !this.isCurrentlyTrading()) {
                    this.playSound(this.getCelebrateSound());
                    int emeraldCount = itemstack.getCount();
                    this.setMoneyAmount(emeraldCount);
                    this.setIsCurrentlyTrading(true);
                    this.clearTradeItems();
                    if (!pPlayer.isCreative()) {
                        itemstack.shrink(emeraldCount);
                    }

                    return net.minecraft.world.InteractionResult.SUCCESS;
                }
            }
        }

        if (isOwner) {
            return com.Polarice3.Goety.utils.ServantUtil.equipServantArmor(pPlayer, this, itemstack,
                    super.mobInteract(pPlayer, pHand));
        }

        return super.mobInteract(pPlayer, pHand);
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