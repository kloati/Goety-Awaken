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
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.api.magic.IChargingSpell;
import com.Polarice3.Goety.api.magic.IBreathingSpell;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellCaster;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellConfig;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellEntry;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellData;
import com.k1sak1.goetyawaken.common.entities.ai.SorcererCastingGoal;
import com.k1sak1.goetyawaken.common.entities.ai.SorcererSpellAttackGoal;
import java.util.Map;
import java.util.HashMap;
import com.Polarice3.Goety.common.items.ModItems;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BoundSorcerer extends AbstractBoundIllager implements SorcererSpellCaster {
    protected static final EntityDataAccessor<Byte> IS_CASTING_SPELL = SynchedEntityData.defineId(BoundSorcerer.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(BoundSorcerer.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(BoundSorcerer.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(BoundSorcerer.class,
            EntityDataSerializers.INT);
    protected static final EntityDataAccessor<String> CURRENT_SPELL_NAME = SynchedEntityData.defineId(
            BoundSorcerer.class,
            EntityDataSerializers.STRING);
    protected int castingTime;
    private List<SorcererSpellEntry> spellEntries = List.of();
    private Map<String, Integer> focusNameToIndex = Map.of();
    private int[] spellCoolDown = new int[0];
    private int[] spellWeights = new int[0];
    private SorcererSpellEntry currentSpell;
    private boolean needsSpellReload = true;
    private static final int MAX_WEIGHT = 1000;
    private static final int WEIGHT_RECOVERY = 20;

    public int coolDown = 0;
    public int castTimeCounter;
    public boolean hasSpawned;
    public SorcererSpellData spellData = new SorcererSpellData();

    public SorcererSpellData getSpellData() {
        return spellData;
    }

    public Mob self() {
        return this;
    }

    public void setCurrentSpellName(String name) {
        this.entityData.set(CURRENT_SPELL_NAME, name);
    }

    public static int MIN_LEVEL = 1;
    public static int MAX_LEVEL = 6;

    public BoundSorcerer(EntityType<? extends AbstractBoundIllager> type, Level worldIn) {
        super(type, worldIn);
    }

    public boolean shouldReduceCastTime() {
        return this.getSorcererLevel() >= 6;
    }

    private void reloadSpellData() {
        List<SorcererSpellEntry> entries = SorcererSpellConfig.getSpellEntries();
        this.spellEntries = entries;
        Map<String, Integer> indexMap = new HashMap<>();
        int[] cooldowns = new int[entries.size()];
        int[] weights = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            indexMap.put(entries.get(i).getFocusRegistryName(), i);
            weights[i] = entries.get(i).getWeight();
        }
        this.focusNameToIndex = indexMap;
        this.spellCoolDown = cooldowns;
        this.spellWeights = weights;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SorcererSpellAttackGoal(this, this));
        this.goalSelector.addGoal(2, new SorcererCastingGoal(this, this));
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
        this.entityData.define(CURRENT_SPELL_NAME, "");
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

    @Override
    public boolean isUsingItem() {
        return isCastingSpell2() && !spellData.virtualWand.isEmpty();
    }

    @Override
    public ItemStack getUseItem() {
        if (isCastingSpell2() && !spellData.virtualWand.isEmpty()) {
            return spellData.virtualWand;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getUseItemRemainingTicks() {
        if (isCastingSpell2()) {
            return Math.max(0, spellData.spellUseTimeRemaining);
        }
        return 0;
    }

    public boolean isCastingSpell2() {
        if (this.level().isClientSide) {
            return this.entityData.get(IS_CASTING_SPELL) > 0;
        } else {
            return spellData.castingTime > 0;
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

    public String getCurrentSpellName() {
        return this.entityData.get(CURRENT_SPELL_NAME);
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
        spellData.decrementCastingTime();
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
            spellData.serverTick(this);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return super.hurt(pSource, pAmount);
    }

    protected int getSpellCastingTime() {
        return spellData.castingTime;
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
        spellData.castingTime = compound.getInt("SorcererSpellTicks");
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
            if (BoundSorcerer.this.currentSpell != null) {
                Spell sp = BoundSorcerer.this.currentSpell.getSpell();
                sp.stopSpell((ServerLevel) BoundSorcerer.this.level(), BoundSorcerer.this,
                        BoundSorcerer.this.currentSpell.resolveUpgradeStaff(BoundSorcerer.this.getSorcererLevel()),
                        BoundSorcerer.this.currentSpell.getFocusStack(), BoundSorcerer.this.castTimeCounter,
                        WandUtil.getStats(BoundSorcerer.this, sp));
            }
            BoundSorcerer.this.setIsCastingSpell(0);
            BoundSorcerer.this.entityData.set(CURRENT_SPELL_NAME, "");
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
            return livingentity != null && livingentity.isAlive()
                    && (this.attackWarmupDelay > 0 || BoundSorcerer.this.isCastingSpell2());
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            BoundSorcerer.this.castingTime = this.getAdjustedCastingTime();
            Integer idx = BoundSorcerer.this.focusNameToIndex.get(this.getSpell().getFocusRegistryName());
            if (idx != null) {
                BoundSorcerer.this.spellCoolDown[idx] = this.getCastingInterval();
            }
            this.nextAttackTickCount = BoundSorcerer.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                BoundSorcerer.this.playSound(soundevent, 1.0F, 1.0F);
            }
            BoundSorcerer.this.setIsCastingSpell(1);
            BoundSorcerer.this.currentSpell = this.getSpell();
            Spell spell = this.getSpell().getSpell();
            SpellStat spellStat = WandUtil.getStats(BoundSorcerer.this, spell);
            if (this.getSpell().isLevelIncrease()) {
                spellStat.setPotency(BoundSorcerer.this.getSorcererLevel() - this.getSpell().getMinLevel());
            }
            spell.startSpell((ServerLevel) BoundSorcerer.this.level(), BoundSorcerer.this,
                    this.getSpell().resolveUpgradeStaff(BoundSorcerer.this.getSorcererLevel()), spellStat);
            BoundSorcerer.this.castTimeCounter = 0;
            BoundSorcerer.this.entityData.set(CURRENT_SPELL_NAME, this.getSpell().getFocusRegistryName());
        }

        public void stop() {
            super.stop();
            BoundSorcerer.this.setIsCastingSpell(0);
            BoundSorcerer.this.entityData.set(CURRENT_SPELL_NAME, "");
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

        protected abstract SorcererSpellEntry getSpell();
    }

    class SpellGoal extends BoundSorcererUseSpellGoal {
        public SorcererSpellEntry spellEntry;
        public int chargeTicks;
        public int shotCooldown;
        public boolean spellStopped;

        @Override
        public boolean canUse() {
            List<SorcererSpellEntry> entries = spellEntries;
            if (entries == null || entries.isEmpty())
                return false;

            List<SorcererSpellEntry> spells = new ArrayList<>();
            List<Integer> weights = new ArrayList<>();
            int totalWeight = 0;
            int level = getSorcererLevel();

            for (int i = 0; i < entries.size(); i++) {
                SorcererSpellEntry entry = entries.get(i);
                if (level < entry.getMinLevel() || level > entry.getMaxLevel())
                    continue;
                Spell spell = entry.getSpell();
                if (spell == null)
                    continue;
                if (!spell.conditionsMet(BoundSorcerer.this.level(), BoundSorcerer.this))
                    continue;
                if (spellCoolDown[i] > 0)
                    continue;
                if (spell instanceof SummonSpell
                        && BoundSorcerer.this.hasEffect(GoetyEffects.SUMMON_DOWN.get()))
                    continue;
                spells.add(entry);
                weights.add(spellWeights[i]);
                totalWeight += spellWeights[i];
            }

            if (!spells.isEmpty() && totalWeight > 0) {
                int randomValue = BoundSorcerer.this.random.nextInt(totalWeight);
                int currentWeight = 0;
                for (int i = 0; i < spells.size(); i++) {
                    currentWeight += weights.get(i);
                    if (randomValue < currentWeight) {
                        this.spellEntry = spells.get(i);
                        break;
                    }
                }
            } else {
                this.spellEntry = null;
            }

            if (this.spellEntry != null && this.spellEntry.getSpell() instanceof IChargingSpell) {
                this.chargeTicks = 20;
                this.shotCooldown = 0;
            }
            this.spellStopped = false;
            return this.spellEntry != null && super.canUse();
        }

        public void tick() {
            super.tick();
            if (spellEntry != null && !SorcererSpellCaster.isSpellStillValid(spellEntry)) {
                cancelSpell();
                return;
            }
            BoundSorcerer.this.castTimeCounter++;
            Spell spell = spellEntry.getSpell();
            SpellStat spellStat = WandUtil.getStats(BoundSorcerer.this, spell);
            if (spellEntry.isLevelIncrease()) {
                spellStat.setPotency(BoundSorcerer.this.getSorcererLevel() - spellEntry.getMinLevel());
            }
            spell.useSpell((ServerLevel) BoundSorcerer.this.level(), BoundSorcerer.this,
                    spellEntry.resolveUpgradeStaff(BoundSorcerer.this.getSorcererLevel()),
                    BoundSorcerer.this.castTimeCounter, spellStat);
            if (spell instanceof IChargingSpell chargingSpell) {
                if (!spell.conditionsMet(BoundSorcerer.this.level(), BoundSorcerer.this)) {
                    cancelSpell();
                    return;
                }
                --this.chargeTicks;
                if (this.shotCooldown > 0) {
                    --this.shotCooldown;
                }
                if (this.chargeTicks <= 0 && this.shotCooldown <= 0) {
                    if (spell.conditionsMet(BoundSorcerer.this.level(), BoundSorcerer.this)) {
                        SpellStat chargeStat = WandUtil.getStats(BoundSorcerer.this, spell);
                        if (spellEntry.isLevelIncrease()) {
                            chargeStat.setPotency(getSorcererLevel() - spellEntry.getMinLevel());
                        }
                        SorcererSpellCaster.castSpell(BoundSorcerer.this, spellEntry, chargeStat);
                        if (spell instanceof IBreathingSpell breathingSpell) {
                            if (getTarget() != null)
                                MobUtil.instaLook(BoundSorcerer.this, getTarget());
                            breathingSpell.showWandBreath(BoundSorcerer.this,
                                    WandUtil.getStats(BoundSorcerer.this, breathingSpell));
                        }
                        Integer idx = focusNameToIndex.get(spellEntry.getFocusRegistryName());
                        if (idx != null)
                            spellWeights[idx] = spellEntry.getWeight();
                        this.shotCooldown = chargingSpell.Cooldown();
                        if (chargingSpell.everCharge()) {
                            this.chargeTicks = chargingSpell.shotsNumber(BoundSorcerer.this, ItemStack.EMPTY);
                            if (this.chargeTicks <= 0)
                                this.chargeTicks = 10;
                        }
                    } else {
                        cancelSpell();
                        return;
                    }
                }
                level().broadcastEntityEvent(BoundSorcerer.this, (byte) 4);
            }
            spell.useParticle(BoundSorcerer.this.level(), BoundSorcerer.this, ItemStack.EMPTY);
        }

        public void cancelSpell() {
            if (spellEntry != null && !spellStopped) {
                Spell spell = spellEntry.getSpell();
                spell.stopSpell((ServerLevel) BoundSorcerer.this.level(), BoundSorcerer.this,
                        spellEntry.resolveUpgradeStaff(BoundSorcerer.this.getSorcererLevel()),
                        spellEntry.getFocusStack(), BoundSorcerer.this.castTimeCounter,
                        WandUtil.getStats(BoundSorcerer.this, spell));
                spellStopped = true;
                BoundSorcerer.this.currentSpell = null;
            }
            this.attackWarmupDelay = 0;
            BoundSorcerer.this.castingTime = 0;
            setIsCastingSpell(0);
            BoundSorcerer.this.entityData.set(CURRENT_SPELL_NAME, "");
            level().broadcastEntityEvent(BoundSorcerer.this, (byte) 5);
            BoundSorcerer.this.coolDown = 20;
        }

        @Override
        protected void performSpellCasting() {
            if (spellEntry.getSpell() instanceof IChargingSpell)
                return;
            if (getTarget() != null) {
                Spell spell = spellEntry.getSpell();
                SpellStat spellStat = WandUtil.getStats(BoundSorcerer.this, spell);
                if (spellEntry.isLevelIncrease()) {
                    spellStat.setPotency(spellStat.getPotency() + (getSorcererLevel() - spellEntry.getMinLevel()));
                }
                SorcererSpellCaster.castSpell(BoundSorcerer.this, spellEntry, spellStat);
                Integer idx = focusNameToIndex.get(spellEntry.getFocusRegistryName());
                if (idx != null)
                    spellWeights[idx] = spellEntry.getWeight();
            }
        }

        @Override
        protected int getCastWarmupTime() {
            Spell spell = spellEntry.getSpell();
            int warmupTime;
            if (spell instanceof IChargingSpell chargingSpell) {
                warmupTime = chargingSpell.castUp(BoundSorcerer.this, ItemStack.EMPTY);
            } else {
                warmupTime = Math.max(1, spell.defaultCastDuration());
            }
            if (shouldReduceCastTime())
                warmupTime = warmupTime / 2;
            return Math.max(1, warmupTime);
        }

        @Override
        protected int getCastingTime() {
            Spell spell = spellEntry.getSpell();
            if (spell instanceof IChargingSpell chargingSpell) {
                if (chargingSpell.everCharge()) {
                    int shots = chargingSpell.shotsNumber(BoundSorcerer.this, ItemStack.EMPTY);
                    if (shots <= 0)
                        shots = 200;
                    return Math.min(shots * 4, 200);
                } else {
                    return Math.min(
                            chargingSpell.Cooldown() * 5 + chargingSpell.castUp(BoundSorcerer.this, ItemStack.EMPTY),
                            100);
                }
            }
            return Math.max(1, spell.defaultCastDuration());
        }

        @Override
        protected int getCastingInterval() {
            Spell spell = spellEntry.getSpell();
            if (spell instanceof IChargingSpell chargingSpell) {
                return chargingSpell.defaultSpellCooldown() * 2;
            }
            return spell.defaultSpellCooldown();
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return spellEntry.getSpell().CastingSound(BoundSorcerer.this);
        }

        @Override
        protected SorcererSpellEntry getSpell() {
            return spellEntry;
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
