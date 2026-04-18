package com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton;

import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.Config;
import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.WindBlowParticleOption;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.entities.neutral.IRavager;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModSwordProjectile;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class VanguardChampion extends AbstractSkeletonServant implements ICustomAttributes {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(VanguardChampion.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(VanguardChampion.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> BANNER = SynchedEntityData.defineId(VanguardChampion.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> PROTECTION_POINTS = SynchedEntityData.defineId(
            VanguardChampion.class,
            EntityDataSerializers.INT);
    public int attackTick;
    public int shieldHealth = 5;
    public AnimationState bobAnimationState = new AnimationState();
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState attack2AnimationState = new AnimationState();
    public AnimationState preAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();
    public int shootTime = 0;
    public boolean isShooting = false;
    public int chargeTick = 0;
    public boolean isCharging = false;
    private int chargeCooldown = 0;
    private int shieldInvulnTime = 0;
    private int currentAttackType = 0;

    public VanguardChampion(EntityType<? extends Summoned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ChargeGoal());
        this.goalSelector.addGoal(2, new MeleeGoal());
        this.goalSelector.addGoal(3, new ShootGoal(this));
        this.goalSelector.addGoal(4, new VanguardAttackGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.MAX_HEALTH, com.k1sak1.goetyawaken.config.AttributesConfig.VanguardChampionHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.VanguardChampionMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.VanguardChampionDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.VanguardChampionAttackKnockback.get())
                .add(Attributes.ARMOR, AttributesConfig.VanguardChampionArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.VanguardChampionArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.VanguardChampionHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.VanguardChampionMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.VanguardChampionDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.VanguardChampionArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.VanguardChampionArmorToughness.get());
    }

    @Override
    public void reassessWeaponGoal() {
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHIELD, true);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(BANNER, ItemStack.EMPTY);
        this.entityData.define(PROTECTION_POINTS, 0);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("hasShield")) {
            this.setShield(pCompound.getBoolean("hasShield"));
        }
        if (pCompound.contains("ShieldHeath")) {
            this.setShieldHealth(pCompound.getInt("ShieldHeath"));
        }
        if (pCompound.contains("ChargeCooldown")) {
            this.chargeCooldown = pCompound.getInt("ChargeCooldown");
        }
        if (pCompound.contains("ShieldInvulnTime")) {
            this.shieldInvulnTime = pCompound.getInt("ShieldInvulnTime");
        }
        if (pCompound.contains("Banner")) {
            this.setBanner(ItemStack.of(pCompound.getCompound("Banner")));
        }
        if (pCompound.contains("ProtectionPoints")) {
            this.setProtectionPoints(pCompound.getInt("ProtectionPoints"));
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("hasShield", this.hasShield());
        pCompound.putInt("ShieldHeath", this.getShieldHealth());
        pCompound.putInt("ChargeCooldown", this.chargeCooldown);
        pCompound.putInt("ShieldInvulnTime", this.shieldInvulnTime);
        if (!this.getBanner().isEmpty()) {
            pCompound.put("Banner", this.getBanner().save(new CompoundTag()));
        }
        pCompound.putInt("ProtectionPoints", this.getProtectionPoints());
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof VanguardChampion;
    }

    @Override
    public boolean isAlliedTo(Entity entityIn) {
        if (super.isAlliedTo(entityIn)) {
            return true;
        }
        if (this.isHostile() && entityIn instanceof Mob mob && mob.getMobType() == MobType.UNDEAD
                && mob.getTarget() != this) {
            if (mob instanceof net.minecraft.world.entity.monster.Enemy) {
                return true;
            }
            if (mob instanceof Summoned && ((Summoned) mob).isHostile()) {
                return true;
            }
        }
        return false;
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return Config.vanguardChampionLimit;
    }

    private boolean getVanguardFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setVanguardFlags(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    public boolean hasShield() {
        return this.entityData.get(HAS_SHIELD);
    }

    public void setShield(boolean shield) {
        this.entityData.set(HAS_SHIELD, shield);
    }

    public int getShieldHealth() {
        return this.shieldHealth;
    }

    public void setShieldHealth(int shieldHealth) {
        this.shieldHealth = shieldHealth;
    }

    private void naturalShieldRecovery() {
        if (!this.hasShield()) {
            this.setShield(true);
            this.setShieldHealth(1);
        } else if (this.getShieldHealth() < 5) {
            this.setShieldHealth(this.getShieldHealth() + 1);
        }
        this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
    }

    public void destroyShield() {
        if (this.hasShield()) {
            if (this.getShieldHealth() > 1) {
                this.setShieldHealth(this.getShieldHealth() - 1);
                this.playSound(SoundEvents.SHIELD_BLOCK);
                this.shieldInvulnTime = 10;
            } else {
                this.setShieldHealth(0);
                this.setShield(false);
                this.playSound(SoundEvents.SHIELD_BREAK);
                if (this.level() instanceof ServerLevel serverLevel) {
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel,
                            new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPRUCE_PLANKS)), this);
                }
                this.applyShieldBreakEffects();
            }
        }
    }

    private void applyShieldBreakEffects() {
        if (this.level() instanceof ServerLevel serverLevel) {
            List<LivingEntity> nearbyEntities = serverLevel.getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(5.0D));

            for (LivingEntity entity : nearbyEntities) {
                if (entity != this && !com.Polarice3.Goety.utils.MobUtil.areAllies(this, entity) && entity.isAlive()) {
                    double dx = entity.getX() - this.getX();
                    double dz = entity.getZ() - this.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    if (distance > 0.1D) {
                        double strength = 1.5D - (distance / 5.0D);
                        if (strength > 0) {
                            entity.push(dx / distance * strength, 0.3D, dz / distance * strength);
                        }
                    }

                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                            com.Polarice3.Goety.utils.MathHelper.secondsToTicks(10), 0));

                    entity.addEffect(new MobEffectInstance(
                            com.Polarice3.Goety.common.effects.GoetyEffects.SAPPED.get(),
                            com.Polarice3.Goety.utils.MathHelper.secondsToTicks(10), 0));
                }
            }
            ColorUtil whiteColor = new ColorUtil(0xFFFFFF);
            ServerParticleUtil.windShockwaveParticle(serverLevel,
                    whiteColor, 0.2f, 0.2f, -1, new Vec3(getX(), getY() + 0.1D, getZ()));
        }
    }

    public boolean isMeleeAttacking() {
        return this.getVanguardFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setVanguardFlags(1, attacking);
        this.attackTick = 0;
        this.currentAttackType = 0;
        this.level().broadcastEntityEvent(this, (byte) 5);
    }

    public void setMeleeAttackingWithAttackType(boolean attacking, int attackType) {
        this.setVanguardFlags(1, attacking);
        this.attackTick = 0;
        this.currentAttackType = attackType;
        if (attackType == 1) {
            this.level().broadcastEntityEvent(this, (byte) 4);
        } else if (attackType == 2) {
            this.level().broadcastEntityEvent(this, (byte) 7);
        }
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.VANGUARD_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.VANGUARD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.VANGUARD_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.VANGUARD_STEP.get();
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.isHostile() && target instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            return true;
        }
        return super.canAttack(target);
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        ItemStack weaponStack = new ItemStack(ModItems.MOONLIGHT_CUT.get());
        if (difficulty.getDifficulty() == Difficulty.HARD) {
            EnchantmentHelper.enchantItem(randomSource, weaponStack, 20, false);
        }
        weaponStack.enchant(Enchantments.BINDING_CURSE, 1);
        weaponStack.enchant(Enchantments.VANISHING_CURSE, 1);
        this.setItemSlot(EquipmentSlot.MAINHAND, weaponStack);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    @Override
    public EntityType<?> getVariant(@Nullable Player player, Level level, BlockPos blockPos) {
        return ModEntityType.VANGUARD_CHAMPION.get();
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.bobAnimationState);
        animationStates.add(this.idleAnimationState);
        animationStates.add(this.walkAnimationState);
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.attack2AnimationState);
        animationStates.add(this.preAnimationState);
        animationStates.add(this.shootAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        this.idleAnimationState.stop();
        this.walkAnimationState.stop();
        this.attackAnimationState.stop();
        this.attack2AnimationState.stop();
        this.preAnimationState.stop();
        this.shootAnimationState.stop();
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.bobAnimationState.startIfStopped(this.tickCount);
            if (this.isAlive()) {
                if (this.isMeleeAttacking()) {
                    if (this.currentAttackType == 2) {
                        this.attack2AnimationState.startIfStopped(this.tickCount);
                        this.attackAnimationState.stop();
                    } else {
                        this.attackAnimationState.startIfStopped(this.tickCount);
                        this.attack2AnimationState.stop();
                    }
                    this.idleAnimationState.stop();
                    this.walkAnimationState.stop();
                } else if (this.isMoving()) {
                    this.walkAnimationState.startIfStopped(this.tickCount);
                } else if (this.isStaying() && !this.isPassenger()) {
                    this.idleAnimationState.startIfStopped(this.tickCount);
                    this.walkAnimationState.stop();
                    this.attackAnimationState.stop();
                    this.attack2AnimationState.stop();
                } else {
                    this.idleAnimationState.startIfStopped(this.tickCount);
                    this.walkAnimationState.stop();
                }
            }
        }
        if (this.isMeleeAttacking()) {
            ++this.attackTick;
        }
        if (this.attackTick > 20) {
            this.setMeleeAttacking(false);
        }

        if (this.isShooting) {
            this.shootTime++;
            if (this.shootTime == 1) {
                this.level().broadcastEntityEvent(this, (byte) 8);
            } else if (this.shootTime == 10) {
                this.level().broadcastEntityEvent(this, (byte) 9);
            } else if (this.shootTime == 11) {
                this.performShoot();
            } else if (this.shootTime >= 25) {
                this.isShooting = false;
                this.shootTime = 0;
            }
        }
        if (this.chargeCooldown > 0) {
            this.chargeCooldown--;
        }
        if (this.shieldInvulnTime > 0) {
            this.shieldInvulnTime--;
        }

        if (!this.level().isClientSide && this.tickCount % 1200 == 0) {
            this.naturalShieldRecovery();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (this.hasShield() && this.shieldInvulnTime > 0) {
                return false;
            }

            if (this.hasShield() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                if (amount < 3.0f && this.random.nextFloat() < 0.05f) {
                    return false;
                }
                this.destroyShield();
                if (source.getEntity() instanceof LivingEntity livingEntity) {
                    if (!MobUtil.areAllies(this, livingEntity) && livingEntity != this.getTrueOwner()) {
                        this.setTarget(livingEntity);
                    }
                }
                return false;
            } else {
                if (this.getTarget() != null) {
                    if (source.getEntity() instanceof LivingEntity livingEntity) {
                        double d0 = this.distanceTo(this.getTarget());
                        double d1 = this.distanceTo(livingEntity);
                        if (MobUtil.ownedCanAttack(this, livingEntity) && livingEntity != this.getTrueOwner()) {
                            if (d0 > d1) {
                                this.setTarget(livingEntity);
                            }
                        }
                    }
                }
            }
        }

        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            amount *= 0.6;
        }

        int protectionPoints = this.getProtectionPoints();
        if (protectionPoints > 0) {
            amount *= (1 - protectionPoints * 0.04f);
            amount = Math.max(amount, 0.0f);
        }

        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {
        if (!this.hasShield()) {
            super.knockback(p_147241_, p_147242_, p_147243_);
        }
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 4) {
            this.currentAttackType = 1;
            this.stopAllAnimations();
            this.attackAnimationState.start(this.tickCount);
        } else if (p_21375_ == 5) {
            this.attackTick = 0;
        } else if (p_21375_ == 6) {
            this.setShield(true);
            this.setShieldHealth(1);
        } else if (p_21375_ == 7) {
            this.currentAttackType = 2;
            this.stopAllAnimations();
            this.attack2AnimationState.start(this.tickCount);
        } else if (p_21375_ == 8) {
            this.stopAllAnimations();
            this.preAnimationState.start(this.tickCount);
        } else if (p_21375_ == 9) {
            this.stopAllAnimations();
            this.shootAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public boolean doHurtTarget(Entity p_21372_) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (p_21372_ instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) p_21372_).getMobType());
            f1 += (float) EnchantmentHelper.getKnockbackBonus(this);
        }

        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            p_21372_.setSecondsOnFire(i * 4);
        }

        boolean flag = p_21372_.hurt(this.getServantAttack(), f);
        if (flag) {
            if (f1 > 0.0F && p_21372_ instanceof LivingEntity living) {
                living.knockback((double) (f1 * 0.5F), (double) Mth.sin(this.getYRot() * ((float) Math.PI / 180F)),
                        (double) (-Mth.cos(this.getYRot() * ((float) Math.PI / 180F))));
            }

            this.doEnchantDamageEffects(this, p_21372_);
            this.setLastHurtMob(p_21372_);
            if (p_21372_ instanceof LivingEntity target) {
                if (target.getVehicle() != null || !target.getPassengers().isEmpty()) {
                    target.stopRiding();
                }
                if (this.random.nextDouble() < 0.05) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                com.Polarice3.Goety.common.effects.GoetyEffects.SAPPED.get(),
                                200,
                                0));
                        MobUtil.disableShield(target, 100);
                    }
                }
                if (target instanceof Player targetPlayer) {
                    double baseSouls = SEHelper.getSoulGiven(targetPlayer);
                    int soulEaterLevel = this.getMainHandItem()
                            .getEnchantmentLevel(
                                    com.Polarice3.Goety.common.enchantments.ModEnchantments.SOUL_EATER.get());
                    int soulEaterMultiplier = Math.max(soulEaterLevel + 1, 1);
                    int soulsToDecrease = (int) (baseSouls * soulEaterMultiplier * 0.1);
                    SEHelper.decreaseSouls(targetPlayer, soulsToDecrease);
                }
            }
        }

        return flag;
    }

    protected double getAttackReachSqr(LivingEntity enemy) {
        if (this.getVehicle() instanceof IRavager) {
            float f = this.getVehicle().getBbWidth() - 0.1F;
            return f * 3.0F * f * 3.0F + enemy.getBbWidth();
        }
        return this.getBbWidth() * 7.0F * this.getBbWidth() * 7.0F + enemy.getBbWidth();
    }

    public boolean targetClose(LivingEntity enemy, double distToEnemySqr) {
        return (distToEnemySqr <= this.getAttackReachSqr(enemy)
                || this.getBoundingBox().intersects(enemy.getBoundingBox())) && this.hasLineOfSight(enemy);
    }

    public ItemStack getBanner() {
        return this.entityData.get(BANNER);
    }

    public void setBanner(ItemStack banner) {
        this.entityData.set(BANNER, banner);
    }

    public boolean hasBanner() {
        return !this.getBanner().isEmpty();
    }

    public int getProtectionPoints() {
        return this.entityData.get(PROTECTION_POINTS);
    }

    public void setProtectionPoints(int points) {
        this.entityData.set(PROTECTION_POINTS, points);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        int basePoints = this.random.nextInt(4) + 1;
        int difficultyBonus = 0;
        switch (pDifficulty.getDifficulty()) {
            case PEACEFUL:
                difficultyBonus = 0;
                break;
            case EASY:
                difficultyBonus = 1;
                break;
            case NORMAL:
                difficultyBonus = 2;
                break;
            case HARD:
                difficultyBonus = 4;
                break;
        }
        int totalPoints = basePoints + difficultyBonus;
        this.setProtectionPoints(totalPoints);
        return spawnGroupData;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD && this.hasBanner()) {
            return this.getBanner();
        }
        return super.getItemBySlot(slot);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (item == Items.BONE && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.SKELETON_STEP, 1.0F, 1.25F);
                this.heal(2.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D),
                                this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (item instanceof BannerItem && !this.hasBanner()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                ItemStack bannerCopy = itemstack.copy();
                bannerCopy.setCount(1);
                this.setBanner(bannerCopy);
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                                this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (itemstack.isEmpty() && pPlayer.isCrouching() && this.hasBanner()) {
                this.spawnAtLocation(this.getBanner());
                this.setBanner(ItemStack.EMPTY);
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    class VanguardAttackGoal extends MeleeAttackGoal {
        private int delayCounter;
        private static final float SPEED = 1.25F;

        public VanguardAttackGoal() {
            super(VanguardChampion.this, SPEED, true);
        }

        @Override
        public boolean canUse() {
            return VanguardChampion.this.getTarget() != null && VanguardChampion.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            VanguardChampion.this.setAggressive(true);
            this.delayCounter = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = VanguardChampion.this.getTarget();
            if (livingentity == null) {
                return;
            }

            VanguardChampion.this.lookControl.setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = VanguardChampion.this.distanceToSqr(livingentity.getX(), livingentity.getY(),
                    livingentity.getZ());

            if (--this.delayCounter <= 0 && !VanguardChampion.this.targetClose(livingentity, d0)) {
                this.delayCounter = 10;
                VanguardChampion.this.getNavigation().moveTo(livingentity, SPEED);
            }

            this.checkAndPerformAttack(livingentity, VanguardChampion.this.distanceToSqr(livingentity.getX(),
                    livingentity.getBoundingBox().minY, livingentity.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (VanguardChampion.this.targetClose(enemy, distToEnemySqr)) {
                if (!VanguardChampion.this.isMeleeAttacking()) {
                    VanguardChampion.this.setMeleeAttacking(true);
                }
            }
        }

        @Override
        protected void resetAttackCooldown() {
        }

        @Override
        public void stop() {
            VanguardChampion.this.getNavigation().stop();
            if (VanguardChampion.this.getTarget() == null) {
                VanguardChampion.this.setAggressive(false);
            }
        }
    }

    class MeleeGoal extends Goal {
        private boolean useAttack2 = false;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return VanguardChampion.this.getTarget() != null && VanguardChampion.this.isMeleeAttacking();
        }

        @Override
        public boolean canContinueToUse() {
            return VanguardChampion.this.isMeleeAttacking() && VanguardChampion.this.attackTick < 20;
        }

        @Override
        public void start() {
            LivingEntity target = VanguardChampion.this.getTarget();
            if (target != null && target.isAlive()) {
                double distance = VanguardChampion.this.distanceTo(target);
                if (distance < 3.0D) {
                    VanguardChampion.this.setMeleeAttackingWithAttackType(true, 2);
                } else {
                    VanguardChampion.this.setMeleeAttackingWithAttackType(true, 1);
                }
            }
        }

        @Override
        public void stop() {
            VanguardChampion.this.setMeleeAttacking(false);
            this.useAttack2 = false;
        }

        @Override
        public void tick() {
            if (VanguardChampion.this.getTarget() != null && VanguardChampion.this.getTarget().isAlive()) {
                LivingEntity livingentity = VanguardChampion.this.getTarget();
                double d0 = VanguardChampion.this.distanceToSqr(livingentity.getX(), livingentity.getY(),
                        livingentity.getZ());
                VanguardChampion.this.getLookControl().setLookAt(livingentity, VanguardChampion.this.getMaxHeadYRot(),
                        VanguardChampion.this.getMaxHeadXRot());
                VanguardChampion.this.setYBodyRot(VanguardChampion.this.getYHeadRot());
                if (VanguardChampion.this.attackTick == 7) {
                    if (VanguardChampion.this.targetClose(livingentity, d0)) {
                        if (VanguardChampion.this.doHurtTarget(livingentity)) {
                            VanguardChampion.this.playSound(ModSounds.VANGUARD_SPEAR.get());

                            if (this.useAttack2) {
                                this.performSweepAttack(livingentity);
                            } else {
                                this.performThrustAttack(livingentity);
                            }
                        }
                    }
                }
            }
        }

        private void performSweepAttack(LivingEntity target) {
            List<LivingEntity> targets = this.getEntitiesInRadius(VanguardChampion.this, 3.0D);
            for (LivingEntity entity : targets) {
                if (entity != target && entity.isAlive()) {
                    if (!entity.isAlliedTo(VanguardChampion.this)
                            && !VanguardChampion.this.isAlliedTo(entity)
                            && (!(entity instanceof ArmorStand) || !((ArmorStand) entity).isMarker())
                            && VanguardChampion.this.canAttack(entity)) {
                        VanguardChampion.this.doHurtTarget(entity);
                    }
                }
            }
        }

        private void performThrustAttack(LivingEntity target) {
            List<Entity> targets = getTargetsInLine(VanguardChampion.this.level(), VanguardChampion.this, 5.0D);
            for (Entity entity : targets) {
                if (entity instanceof LivingEntity living && entity != target) {
                    if (living.isAlive() && VanguardChampion.this.hasLineOfSight(living)) {
                        if (!living.isAlliedTo(VanguardChampion.this)
                                && !VanguardChampion.this.isAlliedTo(living)
                                && (!(target instanceof ArmorStand) || !((ArmorStand) target).isMarker())
                                && VanguardChampion.this.canAttack(living)) {
                            VanguardChampion.this.doHurtTarget(living);
                        }
                    }
                }
            }
        }

        private List<LivingEntity> getEntitiesInRadius(LivingEntity source, double radius) {
            List<LivingEntity> list = new ArrayList<>();
            for (Entity entity : source.level().getEntitiesOfClass(LivingEntity.class,
                    source.getBoundingBox().inflate(radius))) {
                if (entity != source && entity.isAlive()) {
                    list.add((LivingEntity) entity);
                }
            }
            return list;
        }

        public static List<Entity> getTargetsInLine(Level level, LivingEntity pSource, double pRange) {
            List<Entity> list = new ArrayList<>();
            Vec3 lookVec = pSource.getViewVector(1.0F);
            double[] lookRange = new double[] { lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange };
            List<Entity> possibleList = level.getEntities(pSource,
                    pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]));

            for (Entity hit : possibleList) {
                if (hit.isPickable() && hit != pSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR
                        .and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(hit)) {
                    list.add(hit);
                }
            }
            return list;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ShootGoal extends Goal {
        private final VanguardChampion mob;
        private LivingEntity target;
        private int attackInterval = 5;
        private int attackTime = -1;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public ShootGoal(VanguardChampion mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            if (target == null || !target.isAlive() || this.mob.distanceToSqr(target) < 64.0D
                    || this.mob.isMeleeAttacking()) {
                return false;
            }

            double currentDistanceSqr = this.mob.distanceToSqr(target);
            List<LivingEntity> nearbyAllies = this.mob.level().getEntitiesOfClass(LivingEntity.class,
                    this.mob.getBoundingBox().inflate(20.0D),
                    entity -> entity != this.mob && entity.isAlive() && MobUtil.areAllies(this.mob, entity));

            for (LivingEntity ally : nearbyAllies) {
                if (ally.distanceToSqr(target) < currentDistanceSqr) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || !this.mob.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.target = this.mob.getTarget();
            this.seeTime = 0;
            this.attackTime = -1;
            this.strafingTime = -1;
            this.mob.setAggressive(true);
        }

        @Override
        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
            this.strafingTime = -1;
            this.mob.setAggressive(false);
        }

        @Override
        public void tick() {
            double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean flag = this.mob.getSensing().hasLineOfSight(this.target);

            if (flag) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }

            if (!(d0 > 225.0D) && this.seeTime >= 2) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(this.target, 1.0D);
            }

            if (this.mob.isShooting || this.attackTime <= 0) {
                com.Polarice3.Goety.utils.MobUtil.instaLook(this.mob, this.target);
            } else {
                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            }

            if (--this.attackTime == 0) {
                if (!flag) {
                    return;
                }

                float f = (float) Math.sqrt(d0) / 10.0F;
                this.attackTime = Mth.floor(f * (float) (this.attackInterval - 20) + (float) this.attackInterval);

                this.mob.isShooting = true;
                this.mob.shootTime = 0;
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(
                        Math.sqrt(d0) / 10.0F,
                        (float) this.attackInterval,
                        10.0F));
            }
        }
    }

    private void performShoot() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double d0 = this.distanceTo(target);
            double targetHeight = target.getEyeY();
            double d1 = target.getX() - this.getX();
            double d2 = targetHeight - this.getEyeY();
            double d3 = target.getZ() - this.getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3);
            ItemStack glaiveStack = new ItemStack(ModItems.MOONLIGHT_CUT.get());
            EnchantmentHelper.enchantItem(this.random, glaiveStack, this.random.nextInt(11) + 5, false);
            ModSwordProjectile magicGlaive = new ModSwordProjectile(this, this.level(), glaiveStack);
            magicGlaive.setPos(this.getX(), this.getEyeY() - 0.1F, this.getZ());

            float f = (float) d0 * 0.2F;
            magicGlaive.shoot(d1, d2 + (double) f * 0.2F, d3, 2.6F,
                    10.0F - (float) this.level().getDifficulty().getId() * 3.0F);
            magicGlaive.setCritArrow(true);
            this.playSound(SoundEvents.TRIDENT_THROW, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 10; ++i) {
                    double d5 = this.random.nextGaussian() * 0.02D;
                    double d6 = this.random.nextGaussian() * 0.02D;
                    double d7 = this.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ParticleTypes.WITCH,
                            this.getRandomX(1.0D),
                            this.getRandomY() + 0.5D,
                            this.getRandomZ(1.0D),
                            0, d5, d6, d7, 1.0D);
                }
            }

            this.level().addFreshEntity(magicGlaive);
        }
    }

    class ChargeGoal extends Goal {
        private LivingEntity target;

        public ChargeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return VanguardChampion.this.getTarget() != null
                    && VanguardChampion.this.getTarget().isAlive()
                    && !VanguardChampion.this.hasShield()
                    && VanguardChampion.this.chargeCooldown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return VanguardChampion.this.isCharging
                    && VanguardChampion.this.chargeTick < 40;
        }

        @Override
        public void start() {
            this.target = VanguardChampion.this.getTarget();
            VanguardChampion.this.isCharging = true;
            VanguardChampion.this.chargeTick = 0;
            VanguardChampion.this.currentAttackType = 2;
            VanguardChampion.this.level().broadcastEntityEvent(VanguardChampion.this, (byte) 7);
        }

        @Override
        public void stop() {
            VanguardChampion.this.isCharging = false;
            VanguardChampion.this.chargeCooldown = 1200;
            this.target = null;
        }

        @Override
        public void tick() {
            if (this.target != null && this.target.isAlive()) {
                VanguardChampion.this.chargeTick++;

                if (VanguardChampion.this.chargeTick == 7) {
                    if (VanguardChampion.this.targetClose(this.target,
                            VanguardChampion.this.distanceToSqr(this.target))) {
                        if (VanguardChampion.this.doHurtTarget(this.target)) {
                            VanguardChampion.this.playSound(ModSounds.VANGUARD_SPEAR.get());
                        }
                        performSweepAttack(this.target);
                    }
                } else if (VanguardChampion.this.chargeTick == 20) {
                    VanguardChampion.this.level().broadcastEntityEvent(VanguardChampion.this, (byte) 4);
                } else if (VanguardChampion.this.chargeTick == 25) {
                    performLungeMovement(this.target);
                } else if (VanguardChampion.this.chargeTick >= 25 && VanguardChampion.this.chargeTick <= 38) {
                    performChargeAttack();
                } else if (VanguardChampion.this.chargeTick == 27) {
                    performThrustAttack(this.target);
                } else if (VanguardChampion.this.chargeTick >= 40) {
                    VanguardChampion.this.isCharging = false;
                    VanguardChampion.this.currentAttackType = 0;
                }

                VanguardChampion.this.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                VanguardChampion.this.setYBodyRot(VanguardChampion.this.getYHeadRot());
            } else {
                VanguardChampion.this.isCharging = false;
            }
        }

        private void performSweepAttack(LivingEntity target) {
            List<LivingEntity> targets = getEntitiesInRadius(VanguardChampion.this, 3.0D);
            for (LivingEntity entity : targets) {
                if (entity != target && entity.isAlive()) {
                    if (!entity.isAlliedTo(VanguardChampion.this)
                            && !VanguardChampion.this.isAlliedTo(entity)
                            && (!(entity instanceof ArmorStand) || !((ArmorStand) entity).isMarker())
                            && VanguardChampion.this.canAttack(entity)) {
                        VanguardChampion.this.doHurtTarget(entity);
                        if (VanguardChampion.this.level() instanceof ServerLevel serverLevel) {
                            entity.addEffect(new MobEffectInstance(
                                    com.Polarice3.Goety.common.effects.GoetyEffects.SAPPED.get(),
                                    200,
                                    0));
                        }
                    }
                }
            }
        }

        private void performThrustAttack(LivingEntity target) {
            List<Entity> targets = getTargetsInLine(VanguardChampion.this.level(), VanguardChampion.this, 5.0D);
            for (Entity entity : targets) {
                if (entity instanceof LivingEntity living && entity != target) {
                    if (living.isAlive() && VanguardChampion.this.hasLineOfSight(living)) {
                        if (!living.isAlliedTo(VanguardChampion.this)
                                && !VanguardChampion.this.isAlliedTo(living)
                                && (!(target instanceof ArmorStand) || !((ArmorStand) target).isMarker())
                                && VanguardChampion.this.canAttack(living)) {
                            VanguardChampion.this.doHurtTarget(living);
                            if (VanguardChampion.this.level() instanceof ServerLevel serverLevel) {
                                living.addEffect(new MobEffectInstance(
                                        com.Polarice3.Goety.common.effects.GoetyEffects.SAPPED.get(),
                                        200,
                                        0));
                            }
                        }
                    }
                }
            }
        }

        private void performLungeMovement(LivingEntity target) {
            Vec3 direction;
            if (target != null && target.isAlive()) {
                Vec3 targetPos = target.position();
                Vec3 currentPos = VanguardChampion.this.position();
                direction = targetPos.subtract(currentPos).normalize();
            } else {
                direction = VanguardChampion.this.getLookAngle();
            }

            double power = 4.0D;
            VanguardChampion.this.hurtMarked = true;
            VanguardChampion.this.setOnGround(false);
            VanguardChampion.this.setDeltaMovement(direction.x * power, direction.y * power, direction.z * power);
            VanguardChampion.this.hasImpulse = true;
            VanguardChampion.this.fallDistance = 0;
        }

        private void performChargeAttack() {
            if (VanguardChampion.this.level() instanceof ServerLevel serverLevel) {
                int width = serverLevel.getRandom().nextIntBetweenInclusive(1, 4);
                float height = serverLevel.getRandom().nextFloat() * 0.5F;
                Vec3 eyePos = VanguardChampion.this.getEyePosition().offsetRandom(serverLevel.getRandom(), 2.0F);
                Vec3 lookAngle = VanguardChampion.this.getLookAngle().multiply(-1.0D, 1.0D, -1.0D);
                serverLevel.sendParticles(
                        new WindBlowParticleOption(
                                new com.Polarice3.Goety.utils.ColorUtil(net.minecraft.ChatFormatting.AQUA), width,
                                height),
                        eyePos.x, eyePos.y, eyePos.z, 0, lookAngle.x, lookAngle.y, lookAngle.z, 1.0F);
                for (LivingEntity entityHit : serverLevel.getEntitiesOfClass(LivingEntity.class,
                        VanguardChampion.this.getBoundingBox().inflate(2.0F))) {
                    if (!com.Polarice3.Goety.utils.MobUtil.areAllies(VanguardChampion.this, entityHit)
                            && net.minecraft.world.entity.EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entityHit)
                            && entityHit.isAttackable()) {
                        float damage = (float) VanguardChampion.this
                                .getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                        boolean flag = entityHit.hurt(serverLevel.damageSources().mobAttack(VanguardChampion.this),
                                damage);
                        if (entityHit
                                .isDamageSourceBlocked(serverLevel.damageSources().mobAttack(VanguardChampion.this))) {
                            com.Polarice3.Goety.utils.MobUtil.disableShield(entityHit, 100);
                        }
                        if (flag) {
                            if (!entityHit.hasEffect(com.Polarice3.Goety.common.effects.GoetyEffects.SAPPED.get())) {
                                entityHit.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                        com.Polarice3.Goety.common.effects.GoetyEffects.SAPPED.get(),
                                        com.Polarice3.Goety.utils.MathHelper.secondsToTicks(10),
                                        0, false, true));
                            }
                            double d0 = entityHit.getX() - VanguardChampion.this.getX();
                            double d1 = entityHit.getZ() - VanguardChampion.this.getZ();
                            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                            entityHit.push(d0 / d2 * 2.5D, 0.18D, d1 / d2 * 2.2D);
                        }
                    }
                }
            }
        }

        private List<LivingEntity> getEntitiesInRadius(LivingEntity source, double radius) {
            List<LivingEntity> list = new ArrayList<>();
            for (Entity entity : source.level().getEntitiesOfClass(LivingEntity.class,
                    source.getBoundingBox().inflate(radius))) {
                if (entity != source && entity.isAlive()) {
                    list.add((LivingEntity) entity);
                }
            }
            return list;
        }

        public List<Entity> getTargetsInLine(Level level, LivingEntity pSource, double pRange) {
            List<Entity> list = new ArrayList<>();
            Vec3 lookVec = pSource.getViewVector(1.0F);
            double[] lookRange = new double[] { lookVec.x() * pRange, lookVec.y() * pRange, lookVec.z() * pRange };
            List<Entity> possibleList = level.getEntities(pSource,
                    pSource.getBoundingBox().expandTowards(lookRange[0], lookRange[1], lookRange[2]));

            for (Entity hit : possibleList) {
                if (hit.isPickable() && hit != pSource && EntitySelector.NO_CREATIVE_OR_SPECTATOR
                        .and(EntitySelector.LIVING_ENTITY_STILL_ALIVE).test(hit)) {
                    list.add(hit);
                }
            }
            return list;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    @Override
    public Component getName() {
        if (this.isHostile()) {
            return Component.translatable("entity.goetyawaken.hostile_vanguard_champion");
        }
        return super.getName();
    }
}
