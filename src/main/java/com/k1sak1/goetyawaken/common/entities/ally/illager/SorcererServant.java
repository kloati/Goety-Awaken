package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.BoundSorcerer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
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
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
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
import com.k1sak1.goetyawaken.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.entities.ModEntityType;
import net.minecraft.core.particles.ParticleTypes;
import com.k1sak1.goetyawaken.common.upgrades.SpecialServantHandlers;

public class SorcererServant extends SpellcasterIllagerServant implements Merchant, SorcererSpellCaster {
    protected static final EntityDataAccessor<Byte> IS_CASTING_SPELL = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.INT);
    protected static final EntityDataAccessor<String> CURRENT_SPELL_NAME = SynchedEntityData.defineId(
            SorcererServant.class,
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
    protected static final EntityDataAccessor<Boolean> IS_TRADING = SynchedEntityData.defineId(SorcererServant.class,
            EntityDataSerializers.BOOLEAN);
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

    private boolean isCurrentlyTrading = false;
    private int moneyAmount = 0;
    private int tradingProgress = 0;
    private int tradingDelay = 0;
    private java.util.List<net.minecraft.world.item.ItemStack> tradeItems = new java.util.ArrayList<>();

    @Nullable
    private Player tradingPlayer;
    private MerchantOffers offers = new MerchantOffers();
    private int villagerXp = 0;
    private int savedLevel = -1;

    private long lastRestockGameTime = 0;
    private int numberOfRestocksToday = 0;
    private long lastRestockCheckDayTime = 0;

    public int coolDown = 0;
    public int castTimeCounter;
    public boolean hasSpawned;
    public static int MIN_LEVEL = 1;
    public static int MAX_LEVEL = 6;

    public SorcererServant(EntityType<? extends SorcererServant> type, Level worldIn) {
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
        // this.goalSelector.addGoal(1, new SorcererTradeGoal(this));
        this.goalSelector.addGoal(2, new SorcererSpellAttackGoal(this, this));
        this.goalSelector.addGoal(3, new SorcererCastingGoal(this, this));
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
        return isCastingSpell2();
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
        spellData.decrementCastingTime();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasSpawned) {
            this.hasSpawned = true;
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
        if (compound.contains("Offers", 10)) {
            this.offers = new MerchantOffers(compound.getCompound("Offers"));
        }
        this.villagerXp = compound.getInt("SorcererXp");
        this.savedLevel = compound.getInt("SavedLevel");

        this.lastRestockGameTime = compound.getLong("LastRestockGameTime");
        this.numberOfRestocksToday = compound.getInt("NumberOfRestocksToday");
        this.lastRestockCheckDayTime = compound.getLong("LastRestockCheckDayTime");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SorcererLevel", this.getSorcererLevel());
        compound.putInt("SorcererSpellTicks", spellData.castingTime);
        compound.putBoolean("HasSpawned", this.hasSpawned);
        if (!this.offers.isEmpty()) {
            compound.put("Offers", this.offers.createTag());
        }
        compound.putInt("SorcererXp", this.villagerXp);
        compound.putInt("SavedLevel", this.savedLevel);

        compound.putLong("LastRestockGameTime", this.lastRestockGameTime);
        compound.putInt("NumberOfRestocksToday", this.numberOfRestocksToday);
        compound.putLong("LastRestockCheckDayTime", this.lastRestockCheckDayTime);
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
        int oldLevel = this.entityData.get(LEVEL);
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
        if (oldLevel != i && !this.level().isClientSide && this.savedLevel != i) {
            this.generateOffers();
        }
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
            if (SorcererServant.this.currentSpell != null) {
                Spell sp = SorcererServant.this.currentSpell.getSpell();
                sp.stopSpell((ServerLevel) SorcererServant.this.level(), SorcererServant.this,
                        SorcererServant.this.currentSpell.resolveUpgradeStaff(SorcererServant.this.getSorcererLevel()),
                        SorcererServant.this.currentSpell.getFocusStack(), SorcererServant.this.castTimeCounter,
                        WandUtil.getStats(SorcererServant.this, sp));
            }
            SorcererServant.this.setIsCastingSpell(0);
            SorcererServant.this.entityData.set(CURRENT_SPELL_NAME, "");
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
            return livingentity != null && livingentity.isAlive()
                    && (this.attackWarmupDelay > 0 || SorcererServant.this.isCastingSpell2());
        }

        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            SorcererServant.this.castingTime = this.getAdjustedCastingTime();
            Integer idx = SorcererServant.this.focusNameToIndex.get(this.getSpell().getFocusRegistryName());
            if (idx != null) {
                SorcererServant.this.spellCoolDown[idx] = this.getCastingInterval();
            }
            this.nextAttackTickCount = SorcererServant.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                SorcererServant.this.playSound(soundevent, 1.0F, 1.0F);
            }
            SorcererServant.this.setIsCastingSpell(1);
            SorcererServant.this.currentSpell = this.getSpell();
            Spell spell = this.getSpell().getSpell();
            SpellStat spellStat = WandUtil.getStats(SorcererServant.this, spell);
            if (this.getSpell().isLevelIncrease()) {
                spellStat.setPotency(SorcererServant.this.getSorcererLevel() - this.getSpell().getMinLevel());
            }
            spell.startSpell((ServerLevel) SorcererServant.this.level(), SorcererServant.this,
                    this.getSpell().resolveUpgradeStaff(SorcererServant.this.getSorcererLevel()), spellStat);
            SorcererServant.this.castTimeCounter = 0;
            SorcererServant.this.entityData.set(CURRENT_SPELL_NAME, this.getSpell().getFocusRegistryName());
        }

        public void stop() {
            super.stop();
            SorcererServant.this.setIsCastingSpell(0);
            SorcererServant.this.entityData.set(CURRENT_SPELL_NAME, "");
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

        protected abstract SorcererSpellEntry getSpell();
    }

    class SpellGoal extends SorcererUseSpellGoal {
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
                if (!spell.conditionsMet(SorcererServant.this.level(), SorcererServant.this))
                    continue;
                if (spellCoolDown[i] > 0)
                    continue;
                if (spell instanceof SummonSpell
                        && SorcererServant.this.hasEffect(GoetyEffects.SUMMON_DOWN.get()))
                    continue;
                spells.add(entry);
                weights.add(spellWeights[i]);
                totalWeight += spellWeights[i];
            }

            if (!spells.isEmpty() && totalWeight > 0) {
                int randomValue = SorcererServant.this.random.nextInt(totalWeight);
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
            SorcererServant.this.castTimeCounter++;
            Spell spell = spellEntry.getSpell();
            SpellStat spellStat = WandUtil.getStats(SorcererServant.this, spell);
            if (spellEntry.isLevelIncrease()) {
                spellStat.setPotency(SorcererServant.this.getSorcererLevel() - spellEntry.getMinLevel());
            }
            spell.useSpell((ServerLevel) SorcererServant.this.level(), SorcererServant.this,
                    spellEntry.resolveUpgradeStaff(SorcererServant.this.getSorcererLevel()),
                    SorcererServant.this.castTimeCounter, spellStat);
            if (spell instanceof IChargingSpell chargingSpell) {
                if (!spell.conditionsMet(SorcererServant.this.level(), SorcererServant.this)) {
                    cancelSpell();
                    return;
                }
                --this.chargeTicks;
                if (this.shotCooldown > 0) {
                    --this.shotCooldown;
                }
                if (this.chargeTicks <= 0 && this.shotCooldown <= 0) {
                    if (spell.conditionsMet(SorcererServant.this.level(), SorcererServant.this)) {
                        SpellStat chargeStat = WandUtil.getStats(SorcererServant.this, spell);
                        if (spellEntry.isLevelIncrease()) {
                            chargeStat.setPotency(getSorcererLevel() - spellEntry.getMinLevel());
                        }
                        SorcererSpellCaster.castSpell(SorcererServant.this, spellEntry, chargeStat);
                        if (spell instanceof IBreathingSpell breathingSpell) {
                            if (getTarget() != null)
                                MobUtil.instaLook(SorcererServant.this, getTarget());
                            breathingSpell.showWandBreath(SorcererServant.this,
                                    WandUtil.getStats(SorcererServant.this, breathingSpell));
                        }
                        Integer idx = focusNameToIndex.get(spellEntry.getFocusRegistryName());
                        if (idx != null)
                            spellWeights[idx] = spellEntry.getWeight();
                        this.shotCooldown = chargingSpell.Cooldown();
                        if (chargingSpell.everCharge()) {
                            this.chargeTicks = chargingSpell.shotsNumber(SorcererServant.this, ItemStack.EMPTY);
                            if (this.chargeTicks <= 0)
                                this.chargeTicks = 10;
                        }
                    } else {
                        cancelSpell();
                        return;
                    }
                }
                level().broadcastEntityEvent(SorcererServant.this, (byte) 4);
            }
            spell.useParticle(SorcererServant.this.level(), SorcererServant.this, ItemStack.EMPTY);
        }

        public void cancelSpell() {
            if (spellEntry != null && !spellStopped) {
                Spell spell = spellEntry.getSpell();
                spell.stopSpell((ServerLevel) SorcererServant.this.level(), SorcererServant.this,
                        spellEntry.resolveUpgradeStaff(SorcererServant.this.getSorcererLevel()),
                        spellEntry.getFocusStack(), SorcererServant.this.castTimeCounter,
                        WandUtil.getStats(SorcererServant.this, spell));
                spellStopped = true;
                SorcererServant.this.currentSpell = null;
            }
            this.attackWarmupDelay = 0;
            SorcererServant.this.castingTime = 0;
            setIsCastingSpell(0);
            SorcererServant.this.entityData.set(CURRENT_SPELL_NAME, "");
            level().broadcastEntityEvent(SorcererServant.this, (byte) 5);
            SorcererServant.this.coolDown = 20;
        }

        @Override
        protected void performSpellCasting() {
            if (spellEntry.getSpell() instanceof IChargingSpell)
                return;
            if (getTarget() != null) {
                Spell spell = spellEntry.getSpell();
                SpellStat spellStat = WandUtil.getStats(SorcererServant.this, spell);
                if (spellEntry.isLevelIncrease()) {
                    spellStat.setPotency(spellStat.getPotency() + (getSorcererLevel() - spellEntry.getMinLevel()));
                }
                SorcererSpellCaster.castSpell(SorcererServant.this, spellEntry, spellStat);
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
                warmupTime = chargingSpell.castUp(SorcererServant.this, ItemStack.EMPTY);
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
                    int shots = chargingSpell.shotsNumber(SorcererServant.this, ItemStack.EMPTY);
                    if (shots <= 0)
                        shots = 200;
                    return Math.min(shots * 4, 200);
                } else {
                    return Math.min(
                            chargingSpell.Cooldown() * 5 + chargingSpell.castUp(SorcererServant.this, ItemStack.EMPTY),
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
            return spellEntry.getSpell().CastingSound(SorcererServant.this);
        }

        @Override
        protected SorcererSpellEntry getSpell() {
            return spellEntry;
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
        boolean isOwner = this.getTrueOwner() != null && pPlayer == this.getTrueOwner();

        if ((itemstack.isEmpty() || itemstack.getItem() == Items.EMERALD) && pHand == InteractionHand.MAIN_HAND) {
            if (isOwner && !this.isAggressive()) {
                if (!this.level().isClientSide) {
                    if (this.shouldRestock()) {
                        this.restock();
                    }
                    this.generateOffers();
                    this.setTradingPlayer(pPlayer);
                    this.openTradingScreen(pPlayer, this.getDisplayName(), this.getSorcererLevel());
                    pPlayer.awardStat(Stats.TALKED_TO_VILLAGER);
                }
                return net.minecraft.world.InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        } else if (isOwner) {
            return com.Polarice3.Goety.utils.ServantUtil.equipServantArmor(pPlayer, this, itemstack,
                    super.mobInteract(pPlayer, pHand));
        }

        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        if (this.isHostile() && entityIn instanceof Raider) {
            return true;
        }
        return super.isAlliedTo(entityIn);
    }

    @Override
    public void setTradingPlayer(@Nullable Player pPlayer) {
        this.tradingPlayer = pPlayer;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public MerchantOffers getOffers() {
        return this.offers;
    }

    @Override
    public void overrideOffers(MerchantOffers pOffers) {

    }

    @Override
    public void notifyTrade(MerchantOffer pOffer) {
        pOffer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();

        this.villagerXp += pOffer.getXp();

        if (this.getTrueOwner() != null) {
            int emeraldCost = pOffer.getCostA().getCount();
            SpecialServantHandlers.handleSorcererTrade(this, emeraldCost);
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack pStack) {
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            this.playSound(this.getNotifyTradeSound(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public int getVillagerXp() {
        return this.villagerXp;
    }

    @Override
    public void overrideXp(int pXp) {
        this.villagerXp = pXp;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return ModSounds.SORCERER_AMBIENT.get();
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide;
    }

    private void generateOffers() {
        int currentLevel = this.getSorcererLevel();

        if (this.savedLevel == currentLevel && !this.offers.isEmpty()) {
            return;
        }

        int tradeCount = currentLevel * 2;
        this.offers = SorcererTradeManager.generateOffersForLevel(currentLevel, this.level().random, tradeCount);
        this.savedLevel = currentLevel;
        if (this.tradingPlayer != null && this.tradingPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendMerchantOffers(
                    serverPlayer.containerMenu.containerId,
                    this.offers,
                    currentLevel,
                    this.villagerXp,
                    this.showProgressBar(),
                    this.canRestock());
        }
    }

    public boolean canRestock() {
        return this.hasBed();
    }

    public boolean hasBed() {
        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            java.util.Optional<net.minecraft.core.BlockPos> optional = this.getVacantBedPosition(serverLevel);
            return optional.isPresent() && this.canReachBed(optional.get());
        }
        return false;
    }

    public java.util.Optional<net.minecraft.core.BlockPos> getVacantBedPosition(
            net.minecraft.server.level.ServerLevel serverLevel) {
        return serverLevel.getPoiManager().find(
                holder -> holder.is(net.minecraft.world.entity.ai.village.poi.PoiTypes.HOME),
                blockPos -> true,
                this.blockPosition(),
                8,
                net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy.HAS_SPACE);
    }

    private boolean canReachBed(net.minecraft.core.BlockPos bedPos) {
        net.minecraft.world.level.pathfinder.Path path = this.getNavigation().createPath(bedPos, 1);
        return path != null && path.canReach();
    }

    private boolean needsToRestock() {
        for (MerchantOffer offer : this.getOffers()) {
            if (offer.needsRestock()) {
                return true;
            }
        }
        return false;
    }

    private boolean allowedToRestock() {
        return this.numberOfRestocksToday == 0 ||
                (this.numberOfRestocksToday < Config.sorcererServantRestockLimit &&
                        this.level().getGameTime() > this.lastRestockGameTime + Config.sorcererServantRestockInterval);
    }

    public boolean shouldRestock() {
        if (!this.hasBed()) {
            return false;
        }
        long i = this.lastRestockGameTime + Config.sorcererServantRestockCooldown;
        long j = this.level().getGameTime();
        boolean flag = j > i;

        long k = this.level().getDayTime();
        if (this.lastRestockCheckDayTime > 0L) {
            long l = this.lastRestockCheckDayTime / 24000L;
            long i1 = k / 24000L;
            flag |= i1 > l;
        }

        this.lastRestockCheckDayTime = k;
        if (flag) {
            this.lastRestockGameTime = j;
            this.numberOfRestocksToday = 0;
        }

        return this.allowedToRestock() && this.needsToRestock();
    }

    public void restock() {
        if (!this.level().isClientSide && this.hasBed()) {
            for (MerchantOffer offer : this.getOffers()) {
                int currentPrice = offer.getCostA().getCount();
                int basePrice = offer.getBaseCostA().getCount();
                if (currentPrice > basePrice) {
                    offer.addToSpecialPriceDiff(basePrice - currentPrice);
                }
                offer.resetUses();
            }

            this.lastRestockGameTime = this.level().getGameTime();
            ++this.numberOfRestocksToday;

            if (this.tradingPlayer instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendMerchantOffers(
                        serverPlayer.containerMenu.containerId,
                        this.offers,
                        this.getSorcererLevel(),
                        this.villagerXp,
                        this.showProgressBar(),
                        this.canRestock());
            }
        }
    }
}