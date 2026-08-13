package com.k1sak1.goetyawaken.common.entities.hostile.illager;

import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.k1sak1.goetyawaken.common.entities.ai.SorcererCastingGoal;
import com.k1sak1.goetyawaken.common.entities.ai.SorcererSpellAttackGoal;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellCaster;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.nbt.CompoundTag;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.entity.EquipmentSlot;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import javax.annotation.Nullable;

public class RubySorcerer extends HuntingIllagerEntity implements SorcererSpellCaster {
    protected static final EntityDataAccessor<Byte> IS_CASTING_SPELL = SynchedEntityData.defineId(RubySorcerer.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(RubySorcerer.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(RubySorcerer.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(RubySorcerer.class,
            EntityDataSerializers.INT);
    protected static final EntityDataAccessor<String> CURRENT_SPELL_NAME = SynchedEntityData.defineId(
            RubySorcerer.class,
            EntityDataSerializers.STRING);
    private final ModServerBossInfo bossInfo;
    public SorcererSpellData spellData = new SorcererSpellData();
    public boolean hasSpawned;
    public static int MIN_LEVEL = 1;
    public static int MAX_LEVEL = 6;

    public SorcererSpellData getSpellData() {
        return spellData;
    }

    public Mob self() {
        return this;
    }

    public void setCurrentSpellName(String name) {
        this.entityData.set(CURRENT_SPELL_NAME, name);
    }

    public String getCurrentSpellName() {
        return this.entityData.get(CURRENT_SPELL_NAME);
    }

    public RubySorcerer(EntityType<? extends RubySorcerer> type, Level worldIn) {
        super(type, worldIn);
        this.setPersistenceRequired();
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
    }

    public boolean shouldReduceCastTime() {
        return this.getSorcererLevel() >= 6;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
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
        this.entityData.define(LEVEL, 6);
        this.entityData.define(CURRENT_SPELL_NAME, "");
    }

    public IllagerArmPose getArmPose() {
        if (this.isShoot()) {
            return IllagerArmPose.CROSSBOW_HOLD;
        } else if (this.isCharging()) {
            return IllagerArmPose.ATTACKING;
        } else if (this.isCastingSpell2()) {
            return IllagerArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating()
                    ? IllagerArmPose.CELEBRATING
                    : IllagerArmPose.CROSSED;
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

    public boolean isCastingSpell() {
        return isCastingSpell2();
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
        com.Polarice3.Goety.utils.MiscCapHelper.updateMobTarget(this);
        if (!this.level().isClientSide()) {
            this.setAggressive(this.getTarget() != null);
        }
        if (!this.hasSpawned) {
            this.hasSpawned = true;
        }
        if (this.bossInfo != null && this.level() instanceof net.minecraft.server.level.ServerLevel) {
            this.bossInfo.update();
        }
        if (!this.level().isClientSide) {
            spellData.serverTick(this);
        }
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (this.bossInfo != null) {
            this.bossInfo.addPlayer(player);
        }
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (this.bossInfo != null) {
            this.bossInfo.removePlayer(player);
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean result = super.hurt(pSource, pAmount);
        if (this.bossInfo != null && this.level() instanceof net.minecraft.server.level.ServerLevel) {
            this.bossInfo.update();
        }
        return result;
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

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("SorcererLevel")) {
            boolean heal = !pCompound.getBoolean("HasSpawned");
            this.setSorcererLevel(pCompound.getInt("SorcererLevel"), heal);
        } else if (pCompound.contains("Level")) {
            boolean heal = !pCompound.getBoolean("HasSpawned");
            this.setSorcererLevel(pCompound.getInt("Level"), heal);
        }
        spellData.castingTime = pCompound.getInt("SorcererSpellTicks");
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("SorcererLevel", this.getSorcererLevel());
        pCompound.putInt("SorcererSpellTicks", spellData.castingTime);
        pCompound.putBoolean("HasSpawned", this.hasSpawned);
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
        this.setSorcererLevel(6, true);
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
    public void applyRaidBuffs(int arg0, boolean arg1) {

    }
}