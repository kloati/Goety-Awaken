package com.k1sak1.goetyawaken.common.entities.ally.undead;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BoneLordServant extends AbstractSkeletonServant implements ICustomAttributes {
    private static final EntityDataAccessor<Optional<UUID>> SKULL_LORD = SynchedEntityData.defineId(
            BoneLordServant.class,
            EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> SKULL_LORD_CLIENT_ID = SynchedEntityData.defineId(
            BoneLordServant.class,
            EntityDataSerializers.INT);

    public BoneLordServant(EntityType<? extends AbstractSkeletonServant> type, Level p_i48555_2_) {
        super(type, p_i48555_2_);
        this.setPersistenceRequired();
    }

    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new FollowHeadGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoneLordHealth.get())
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BoneLordDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoneLordHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BoneLordDamage.get());
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(randomSource, pDifficulty);
        if (pDifficulty.isHarderThan(Difficulty.EASY.ordinal())) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.FROZEN_BLADE.get()));
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        }
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.CURSED_PALADIN_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.CURSED_PALADIN_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.CURSED_PALADIN_BOOTS.get()));
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.CURSED_PALADIN_HELMET.get()));
    }

    protected void populateDefaultEquipmentEnchantments(DifficultyInstance pDifficulty) {
        if (pDifficulty.getDifficulty() != Difficulty.PEACEFUL && pDifficulty.getDifficulty() != Difficulty.EASY) {
            for (EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
                if (equipmentslottype.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack itemstack = this.getItemBySlot(equipmentslottype);
                    if (!itemstack.isEmpty()) {
                        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
                        switch (pDifficulty.getDifficulty()) {
                            case NORMAL:
                                map.putIfAbsent(Enchantments.ALL_DAMAGE_PROTECTION, 2);
                            case HARD:
                                map.putIfAbsent(Enchantments.ALL_DAMAGE_PROTECTION, 3);
                        }
                        map.putIfAbsent(Enchantments.BINDING_CURSE, 1);
                        map.putIfAbsent(Enchantments.VANISHING_CURSE, 1);
                        EnchantmentHelper.setEnchantments(map, itemstack);
                        this.setItemSlot(equipmentslottype, itemstack);
                    }
                }
            }
            if (pDifficulty.getDifficulty() == Difficulty.HARD) {
                ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
                if (!itemstack.isEmpty()) {
                    this.setItemSlot(EquipmentSlot.MAINHAND,
                            EnchantmentHelper.enchantItem(this.random, this.getMainHandItem(), 30, false));
                }
            }
        }

        if (this.getTrueOwner() instanceof Player player) {
            if (CuriosFinder.hasNamelessSet(player)) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack equipment = this.getItemBySlot(slot);
                    if (!equipment.isEmpty()) {
                        this.setItemSlot(slot, EnchantmentHelper.enchantItem(this.random, equipment, 30, false));
                    }
                }
            } else if (CuriosFinder.hasNecroSet(player)) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack equipment = this.getItemBySlot(slot);
                    if (!equipment.isEmpty()) {
                        this.setItemSlot(slot, EnchantmentHelper.enchantItem(this.random, equipment, 20, false));
                    }
                }
            }
        }
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getSkullLord() == null || this.getSkullLord().isDeadOrDying()) {
                // if (this.tickCount % 100 == 0 && this.tickCount > 100) {
                // this.discard();
                // }
            } else {
                AttributeInstance knockResist = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                if (this.getSkullLord().isHalfHealth()) {
                    if (knockResist != null) {
                        knockResist.setBaseValue(1.0D);
                    }
                } else {
                    if (knockResist != null) {
                        if (knockResist.getBaseValue() > 0.0D) {
                            knockResist.setBaseValue(0.0D);
                        }
                    }
                }
                if (this.isInWall()) {
                    this.moveTo(this.getSkullLord().position());
                }
                if (this.getSkullLord().getTarget() != null) {
                    this.setTarget(this.getSkullLord().getTarget());
                }
            }
        }
    }

    protected boolean isSunBurnTick() {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() == this.getSkullLord() && pSource.getDirectEntity() instanceof HauntedSkullProjectile) {
            return false;
        } else {
            return super.hurt(pSource, pAmount);
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.populateDefaultEquipmentSlots(pLevel.getRandom(), pDifficulty);
        this.populateDefaultEquipmentEnchantments(pDifficulty);
        this.setCanPickUpLoot(false);
        for (EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
            this.setDropChance(equipmentslottype, 0.0F);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
        if (!pState.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(pState, pMotionMultiplier);
        }

    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKULL_LORD, Optional.empty());
        this.entityData.define(SKULL_LORD_CLIENT_ID, -1);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.hasUUID("skullLord")) {
            this.setSkullLordUUID(pCompound.getUUID("skullLord"));
        }
        if (pCompound.contains("SkullLordClient")) {
            this.setSkullLordClientId(pCompound.getInt("SkullLordClient"));
        }
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getSkullLordUUID() != null) {
            pCompound.putUUID("skullLord", this.getSkullLordUUID());
        }
        if (this.getSkullLordClientId() > -1) {
            pCompound.putInt("SkullLordClient", this.getSkullLordClientId());
        }
    }

    @Nullable
    public SkullLordServant getSkullLord() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getSkullLordUUID();
            return EntityFinder.getLivingEntityByUuiD(uuid) instanceof SkullLordServant skullLord ? skullLord : null;
        } else {
            int id = this.getSkullLordClientId();
            return id <= -1 ? null
                    : this.level().getEntity(id) instanceof SkullLordServant skullLord ? skullLord : null;
        }
    }

    @Nullable
    public UUID getSkullLordUUID() {
        return this.entityData.get(SKULL_LORD).orElse(null);
    }

    public void setSkullLordUUID(UUID uuid) {
        this.entityData.set(SKULL_LORD, Optional.ofNullable(uuid));
    }

    public int getSkullLordClientId() {
        return this.entityData.get(SKULL_LORD_CLIENT_ID);
    }

    public void setSkullLordClientId(int id) {
        this.entityData.set(SKULL_LORD_CLIENT_ID, id);
    }

    public void setSkullLord(SkullLordServant skullLord) {
        this.setSkullLordUUID(skullLord.getUUID());
        this.setSkullLordClientId(skullLord.getId());
        if (skullLord.getTrueOwner() != null) {
            this.setTrueOwner(skullLord.getTrueOwner());
        }
    }

    @Override
    public void die(DamageSource p_21014_) {
        if (this.level() instanceof ServerLevel) {
            if (this.getSkullLord() != null) {
                if (p_21014_.getEntity() instanceof Mob mob && mob.getTarget() == this) {
                    mob.setTarget(this.getSkullLord());
                }
            }
        }
        super.die(p_21014_);
    }

    public static class FollowHeadGoal extends Goal {
        private final BoneLordServant boneLordServant;
        private LivingEntity owner;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private float oldWaterCost;

        public FollowHeadGoal(BoneLordServant boneLordServant) {
            this.boneLordServant = boneLordServant;
            this.navigation = boneLordServant.getNavigation();
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.boneLordServant.getSkullLord();
            if (livingentity == null) {
                return false;
            } else if (this.boneLordServant.distanceToSqr(livingentity) < (double) (100)) {
                return false;
            } else if (this.boneLordServant.isAggressive()) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.boneLordServant.isAggressive()) {
                return false;
            } else {
                return !(this.boneLordServant.distanceToSqr(this.owner) <= (double) (4));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.boneLordServant.getPathfindingMalus(BlockPathTypes.WATER);
            this.boneLordServant.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.boneLordServant.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        }

        public void tick() {
            this.boneLordServant.getLookControl().setLookAt(this.owner, 10.0F,
                    (float) this.boneLordServant.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                if (!this.boneLordServant.isPassenger()) {
                    this.navigation.moveTo(this.owner, 1.0F);
                }
            }
        }

    }
}
