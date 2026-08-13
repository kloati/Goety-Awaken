package com.k1sak1.goetyawaken.common.entities.hostile.illager;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ShootIndicatorParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.common.entities.util.ShootIndicatorOwner;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModelSnapshot;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.entities.projectiles.ExplosiveArrow;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.k1sak1.goetyawaken.utils.MobEffectUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.joml.Vector3f;

public class ArchIllusioner extends HuntingIllagerEntity implements IOwned, RangedAttackMob, ShootIndicatorOwner {
    private static final int NUM_ILLUSIONS = 4;
    private static final int ILLUSION_TRANSITION_TICKS = 3;
    private static final int ILLUSION_SPREAD = 3;
    private static final EntityDataAccessor<Boolean> START_TELEPORTING = SynchedEntityData.defineId(
            ArchIllusioner.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(
            ArchIllusioner.class,
            EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> OWNER_CLIENT_ID = SynchedEntityData.defineId(
            ArchIllusioner.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HOSTILE = SynchedEntityData.defineId(
            ArchIllusioner.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Vector3f> SHOOT_INDICATOR_END = SynchedEntityData.defineId(
            ArchIllusioner.class,
            EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Float> SHOOT_INDICATOR_PROGRESS = SynchedEntityData.defineId(
            ArchIllusioner.class,
            EntityDataSerializers.FLOAT);

    private final ModServerBossInfo bossInfo;
    public float clientShootIndicatorProgress, oClientShootIndicatorProgress;
    public Vec3 clientShootIndicatorEnd = Vec3.ZERO, oClientShootIndicatorEnd = Vec3.ZERO;
    private int clientSideIllusionTicks;
    private final Vec3[][] clientSideIllusionOffsets;
    private int stuckTime;
    private Vec3 prevVecPos;
    private double prevX;
    private double prevY;
    private double prevZ;

    private boolean isIllusion = false;
    private int illusionHitCount = 0;
    private int illusionLifetime = 0;
    private int aboutToTeleport = 0;

    public final List<Pair<Vec3, ModelSnapshot>> trailSnapshots = new ArrayList<>(50);
    public float lastTrailTick = 0;

    public boolean shouldAddTrailSnapshot() {
        return this.isCastingSpell() || this.isStartTeleporting();
    }

    public ArchIllusioner(EntityType<? extends HuntingIllagerEntity> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 40;
        this.setPersistenceRequired();
        this.checkHostility();
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.RED, false, false);
        this.clientSideIllusionOffsets = new Vec3[2][4];

        for (int i = 0; i < 4; ++i) {
            this.clientSideIllusionOffsets[0][i] = Vec3.ZERO;
            this.clientSideIllusionOffsets[1][i] = Vec3.ZERO;
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        this.goalSelector.addGoal(1, new TrueMirrorSpellGoal());
        this.goalSelector.addGoal(1, new IllusionerMirrorSpellGoal());
        this.goalSelector.addGoal(1, new IllusionerBlindnessSpellGoal());
        this.goalSelector.addGoal(6, new ArchIllusionerBowAttackGoal(this, 1.0D, 20, 15.0F));
        this.targetSelector.addGoal(0, new IllusionMasterTargetGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ArchIllusionerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.ArchIllusionerServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ArchIllusionerServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ArchIllusionerServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ArchIllusionerServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ArchIllusionerDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.ArchIllusionerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
                AttributesConfig.ArchIllusionerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.ArchIllusionerServantArmorToughness.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.ArchIllusionerServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.ArchIllusionerServantFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.ArchIllusionerDamage.get());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason,
            @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        return pSpawnData;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(START_TELEPORTING, false);
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(OWNER_CLIENT_ID, -1);
        this.entityData.define(HOSTILE, false);
        this.entityData.define(SHOOT_INDICATOR_END, new Vector3f(0.0F, 0.0F, 0.0F));
        this.entityData.define(SHOOT_INDICATOR_PROGRESS, -1.0F);
    }

    public void setStartTeleporting(boolean startTeleporting) {
        this.entityData.set(START_TELEPORTING, startTeleporting);
    }

    public boolean isStartTeleporting() {
        return this.entityData.get(START_TELEPORTING);
    }

    @Nullable
    @Override
    public LivingEntity getTrueOwner() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : EntityFinder.getLivingEntityByUuiD(this.level(), uuid);
        }
        int id = this.getOwnerClientId();
        if (id <= -1) {
            return null;
        }
        return this.level().getEntity(id) instanceof LivingEntity living && living != this ? living : null;
    }

    @Nullable
    @Override
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    @Override
    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    @Override
    public int getOwnerClientId() {
        return this.entityData.get(OWNER_CLIENT_ID);
    }

    @Override
    public void setOwnerClientId(int id) {
        this.entityData.set(OWNER_CLIENT_ID, id);
    }

    @Override
    public void setHostile(boolean hostile) {
        this.entityData.set(HOSTILE, hostile);
    }

    @Override
    public boolean isHostile() {
        return this.entityData.get(HOSTILE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsIllusion", this.isIllusion);
        compound.putInt("IllusionHitCount", this.illusionHitCount);
        compound.putInt("IllusionLifetime", this.illusionLifetime);
        this.saveOwnedData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("IsIllusion")) {
            this.isIllusion = compound.getBoolean("IsIllusion");
        }
        if (compound.contains("IllusionHitCount")) {
            this.illusionHitCount = compound.getInt("IllusionHitCount");
        }
        if (compound.contains("IllusionLifetime")) {
            this.illusionLifetime = compound.getInt("IllusionLifetime");
        }
        this.readOwnedData(compound);
    }

    public boolean isIllusion() {
        return this.isIllusion;
    }

    public void setIllusion(boolean illusion) {
        this.isIllusion = illusion;
    }

    public int getIllusionHitCount() {
        return this.illusionHitCount;
    }

    public void incrementIllusionHitCount() {
        this.illusionHitCount++;
        if (this.illusionHitCount >= 1) {
            this.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public int getIllusionLifetime() {
        return this.illusionLifetime;
    }

    public void incrementIllusionLifetime() {
        this.illusionLifetime++;
        if (this.illusionLifetime >= 200) {
            this.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3.0D, 0.0D, 3.0D);
    }

    public void aiStep() {
        super.aiStep();
        MiscCapHelper.updateMobTarget(this);
        if (!this.level().isClientSide()) {
            this.setAggressive(this.getTarget() != null);
            Vec3 targetPos = this.getViewVector(1.0F);
            if (this.getTarget() != null) {
                targetPos = new Vec3(this.getTarget().getX(), this.getTarget().getY(0.5F), this.getTarget().getZ());
            }
            this.setShootIndicatorEnd(targetPos.toVector3f());
            if (!this.isIllusion() && this.isUsingItem() && this.getTicksUsingItem() >= 10
                    && this.getTicksUsingItem() <= 20) {
                this.setShootIndicatorProgress((this.getTicksUsingItem() - 10F) / 10F);
                if (this.getTicksUsingItem() == 10 && this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new ShootIndicatorParticleOption(this.getId()),
                            this.getX(), this.getY(), this.getZ(), 0, 0.65F, 0.35F, 0.95F, 1.0F);
                }
            } else {
                this.setShootIndicatorProgress(-1.0F);
            }
        }
        if (this.level().isClientSide) {
            this.oClientShootIndicatorProgress = this.clientShootIndicatorProgress;
            this.oClientShootIndicatorEnd = this.clientShootIndicatorEnd;
            this.clientShootIndicatorProgress = this.getShootIndicatorProgress();
            this.clientShootIndicatorEnd = new Vec3(this.getShootIndicatorEnd());
        }
        if (!this.isIllusion() && this.bossInfo != null && this.level() instanceof ServerLevel) {
            this.bossInfo.update();
        }

        if (!this.level().isClientSide()) {
            if (this.isStartTeleporting()) {
                ++this.aboutToTeleport;
                if (this.aboutToTeleport >= 5) {
                    this.setStartTeleporting(false);
                }
            } else if (this.aboutToTeleport > 0) {
                this.aboutToTeleport = 0;
            }
        }

        if (this.isIllusion && !this.level().isClientSide()) {
            this.incrementIllusionLifetime();
            if (this.illusionLifetime >= 200) {
                this.remove(Entity.RemovalReason.DISCARDED);
                return;
            }
        }
        if (!this.level().isClientSide()) {
            if (this.tickCount % 10 == 0) {
                this.prevVecPos = this.position();
            }

            LivingEntity target = this.getTarget();
            if (target != null) {
                if (this.isInWall()
                        || (this.prevVecPos != null && this.prevVecPos.distanceTo(this.position()) <= 0.1D)) {
                    ++this.stuckTime;
                } else {
                    if (this.level().getBlockStates(this.getBoundingBox().inflate(1.0F))
                            .anyMatch(blockState -> blockState
                                    .getBlock() instanceof MovingPistonBlock)) {
                        this.stuckTime += 20;
                        this.teleport();
                    } else {
                        if (this.stuckTime > 0) {
                            --this.stuckTime;
                        }
                    }
                }

                if (this.stuckTime > 50) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 16; ++i) {
                            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                                    this.getX() + (this.random.nextDouble() - 0.5D) * 2.0D,
                                    this.getY() + this.random.nextDouble() * 2.0D,
                                    this.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D,
                                    1, 0.0D, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }

                if (this.stuckTime >= 100) {
                    this.escapeTeleport();
                    this.stuckTime = 0;
                }
            } else {
                this.stuckTime = 0;
            }

            if (target != null && target.onGround()) {
                double distanceSq = target.distanceToSqr(this);
                if ((distanceSq > 1024 || !this.getSensing().hasLineOfSight(target)) && !this.isCastingSpell()) {
                    this.teleportTowardsTarget(target);
                }
            }
        }

        if (this.level().isClientSide && this.isInvisible()) {
            --this.clientSideIllusionTicks;
            if (this.clientSideIllusionTicks < 0) {
                this.clientSideIllusionTicks = 0;
            }

            if (this.hurtTime != 1 && this.tickCount % 1200 != 0) {
                if (this.hurtTime == this.hurtDuration - 1) {
                    this.clientSideIllusionTicks = 3;

                    for (int k = 0; k < 4; ++k) {
                        this.clientSideIllusionOffsets[0][k] = this.clientSideIllusionOffsets[1][k];
                        this.clientSideIllusionOffsets[1][k] = new Vec3(0.0D, 0.0D, 0.0D);
                    }
                }
            } else {
                this.clientSideIllusionTicks = 3;

                for (int j = 0; j < 4; ++j) {
                    this.clientSideIllusionOffsets[0][j] = this.clientSideIllusionOffsets[1][j];
                    this.clientSideIllusionOffsets[1][j] = new Vec3(
                            (double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D,
                            (double) Math.max(0, this.random.nextInt(6) - 4),
                            (double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D);
                }

                for (int l = 0; l < 16; ++l) {
                    this.level().addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getRandomY(),
                            this.getZ(0.5D),
                            0.0D, 0.0D, 0.0D);
                }

                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE,
                        this.getSoundSource(), 1.0F, 1.0F, false);
            }
        }
    }

    public SoundEvent getCelebrateSound() {
        return SoundEvents.ILLUSIONER_AMBIENT;
    }

    @Override
    protected void tickDeath() {
        if (this.isIllusion()) {
            this.remove(Entity.RemovalReason.KILLED);
            return;
        }
        super.tickDeath();
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (!this.level().isClientSide && !this.isIllusion()) {
            List<ArchIllusioner> illusions = this.level().getEntitiesOfClass(ArchIllusioner.class,
                    this.getBoundingBox().inflate(64.0D),
                    entity -> entity.isIllusion() && entity.isAlive() && entity.getTrueOwner() == this);
            for (ArchIllusioner illusion : illusions) {
                illusion.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        if (this.isIllusion()) {
            this.remove(Entity.RemovalReason.KILLED);
            return;
        }
        super.die(pDamageSource);
    }

    public Vec3[] getIllusionOffsets(float pPartialTick) {
        if (this.clientSideIllusionTicks <= 0) {
            return this.clientSideIllusionOffsets[1];
        } else {
            double d0 = (double) (((float) this.clientSideIllusionTicks - pPartialTick) / 3.0F);
            d0 = Math.pow(d0, 0.25D);
            Vec3[] avec3 = new Vec3[4];

            for (int i = 0; i < 4; ++i) {
                avec3[i] = this.clientSideIllusionOffsets[1][i].scale(1.0D - d0)
                        .add(this.clientSideIllusionOffsets[0][i].scale(d0));
            }

            return avec3;
        }
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        }
        return this.isAggressive() ? AbstractIllager.IllagerArmPose.BOW_AND_ARROW
                : AbstractIllager.IllagerArmPose.CROSSED;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ILLUSIONER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ARCH_ILLUSIONER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ILLUSIONER_HURT;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.ILLUSIONER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {
    }

    static class ArchIllusionerBowAttackGoal extends RangedBowAttackGoal<ArchIllusioner> {
        private final ArchIllusioner illusioner;

        public ArchIllusionerBowAttackGoal(ArchIllusioner mob, double speedModifier, int attackInterval,
                float attackRadius) {
            super(mob, speedModifier, attackInterval, attackRadius);
            this.illusioner = mob;
        }

        @Override
        public void tick() {
            this.setMinAttackInterval(this.illusioner.isIllusion() ? 20 : 30);
            super.tick();
        }
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(
                ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        ItemStack bowStack = this.getItemInHand(
                ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem));
        if (this.isIllusion) {
            net.minecraft.world.entity.projectile.Arrow arrow = new net.minecraft.world.entity.projectile.Arrow(
                    this.level(), this);
            arrow.shootFromRotation(this, this.getXRot(), this.getYRot(), 0.0F, 3.0F, 1.0F);

            double d0 = pTarget.getX() - this.getX();
            double d1 = pTarget.getY(0.3333333333333333D) - arrow.getY();
            double d2 = pTarget.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            arrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 3.0F,
                    (float) (14 - this.level().getDifficulty().getId() * 4));

            int powerLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                    net.minecraft.world.item.enchantment.Enchantments.POWER_ARROWS, bowStack);
            if (powerLevel > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
            }

            this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level().addFreshEntity(arrow);
        } else {
            ExplosiveArrow explosiveArrow = new ExplosiveArrow(this.level(), this);
            explosiveArrow.shootFromRotation(this, this.getXRot(), this.getYRot(), 0.0F, 2.0F, 1.0F);

            double d0 = pTarget.getX() - this.getX();
            double d1 = pTarget.getY(0.5D) - this.getY(0.5D);
            double d2 = pTarget.getZ() - this.getZ();
            explosiveArrow.shoot(d0, d1, d2, 2.0F, 1.0F);

            int powerLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                    net.minecraft.world.item.enchantment.Enchantments.POWER_ARROWS, bowStack);
            if (powerLevel > 0) {
                explosiveArrow.setBaseDamage(explosiveArrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
            }

            this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level().addFreshEntity(explosiveArrow);
        }
    }

    public void teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            this.setStartTeleporting(true);

            for (int i = 0; i < 64; ++i) {
                double blockRange = 32.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * blockRange;
                double d2 = this.getY() + (double) (this.random.nextInt(16) - 8);
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * blockRange;

                if (this.randomTeleport(d1, d2, d3, false)) {
                    this.teleportHits();
                    break;
                }
            }
        }
    }

    protected void teleportTowardsTarget(Entity entity) {
        if (!this.level().isClientSide() && this.isAlive()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            this.setStartTeleporting(true);

            for (int i = 0; i < 64; ++i) {
                Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(),
                        this.getZ() - entity.getZ());
                vector3d = vector3d.normalize();
                double d0 = 16.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vector3d.y * d0;
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;

                BlockPos blockPos = BlockPos.containing(d1, d2, d3);
                if (canSeeBlock(entity, blockPos)) {
                    if (this.randomTeleport(d1, d2, d3, false)) {
                        this.teleportHits();
                        break;
                    }
                }
            }
        }
    }

    protected void escapeTeleport() {
        if (!this.level().isClientSide() && this.isAlive() && !this.isCastingSpell()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            this.setStartTeleporting(true);

            for (int i = 0; i < 128; ++i) {
                double blockRange = 128.0D;
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * blockRange;
                double d4 = this.getY() + (this.getRandom().nextDouble() - 0.5D) * (blockRange / 2.0D);
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * blockRange;

                if (this.randomTeleport(d3, d4, d5, false)) {
                    this.spawnTeleportParticles();
                    this.stuckTime = 0;
                    this.level().broadcastEntityEvent(this, (byte) 100);
                    this.level().gameEvent(GameEvent.TELEPORT, this.position(),
                            GameEvent.Context.of(this));

                    if (!this.isSilent()) {
                        this.level().playSound((Player) null, this.prevX, this.prevY, this.prevZ,
                                SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    }
                    break;
                }
            }
        }
    }

    public void teleportHits() {
        this.spawnTeleportParticles();
        this.stuckTime = 0;
        this.level().broadcastEntityEvent(this, (byte) 100);
        this.level().gameEvent(GameEvent.TELEPORT, this.position(),
                GameEvent.Context.of(this));

        if (!this.isSilent()) {
            this.level().playSound((Player) null, this.prevX, this.prevY, this.prevZ,
                    SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    public boolean teleportChance() {
        return this.level().random.nextFloat() <= 0.25F;
    }

    private void spawnTeleportParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 from = new Vec3(this.prevX, this.prevY, this.prevZ);
            Vec3 to = this.position();
            Vec3 direction = to.subtract(from).normalize();
            for (int i = 0; i < 20; ++i) {
                serverLevel.sendParticles(ParticleTypes.WITCH,
                        from.x + (this.random.nextDouble() - 0.5D) * 1.2D,
                        from.y + 1.0D + this.random.nextDouble() * 1.0D,
                        from.z + (this.random.nextDouble() - 0.5D) * 1.2D,
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            for (int i = 0; i < 18; ++i) {
                double speed = 0.15D + this.random.nextDouble() * 0.3D;
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        from.x + (this.random.nextDouble() - 0.5D) * 0.8D,
                        from.y + 1.0D + this.random.nextDouble() * 1.2D,
                        from.z + (this.random.nextDouble() - 0.5D) * 0.8D,
                        0, direction.x * speed, direction.y * speed, direction.z * speed, 1.0D);
            }
            this.spawnTeleportRings(serverLevel, from, to);
        }
    }

    private void spawnTeleportRings(ServerLevel serverLevel, Vec3 from, Vec3 to) {
        com.k1sak1.goetyawaken.client.particle.RingParticle.EnumRingBehavior shrink = com.k1sak1.goetyawaken.client.particle.RingParticle.EnumRingBehavior.SHRINK;
        com.k1sak1.goetyawaken.client.particle.RingParticle.EnumRingBehavior grow = com.k1sak1.goetyawaken.client.particle.RingParticle.EnumRingBehavior.GROW;
        for (float scale : new float[] { 20.0F, 30.0F, 40.0F }) {
            serverLevel.sendParticles(
                    new com.k1sak1.goetyawaken.client.particle.RingParticle.RingData(
                            0.0F, (float) Math.PI / 2, 80, 0.75F, 0.65F, 0.85F, 0.8F, scale,
                            false, shrink),
                    from.x, from.y + 1.0D, from.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(
                    new com.k1sak1.goetyawaken.client.particle.RingParticle.RingData(
                            0.0F, (float) Math.PI / 2, 80, 0.75F, 0.65F, 0.85F, 0.8F, scale,
                            false, grow),
                    to.x, to.y + 1.0D, to.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private boolean canSeeBlock(Entity entity, BlockPos blockPos) {
        BlockHitResult hitresult = this.level().clip(
                new net.minecraft.world.level.ClipContext(
                        this.getEyePosition(),
                        Vec3.atCenterOf(blockPos),
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE,
                        this));
        return hitresult.getType() == HitResult.Type.MISS ||
                hitresult.getBlockPos().equals(blockPos);
    }

    @Override
    public HumanoidArm getMainArm() {
        return this.isIllusion() ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() instanceof LivingEntity attacker && this.isSameCamp(attacker)) {
            return false;
        }
        if (!this.isIllusion() && this.bossInfo != null && this.level() instanceof ServerLevel) {
            this.bossInfo.update();
        }
        boolean result = super.hurt(pSource, pAmount);
        if (result && this.isIllusion) {
            this.incrementIllusionHitCount();
        }
        return result;
    }

    private boolean isSameCamp(LivingEntity other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ArchIllusioner otherIllusion)) {
            return false;
        }
        LivingEntity master = this.getMasterOwner();
        if (master != null) {
            return otherIllusion == master || otherIllusion.getMasterOwner() == master;
        }
        return otherIllusion.getMasterOwner() == this;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (this.isIllusion()) {
            return InteractionResult.PASS;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public Vec3 getShootIndicatorStart(float partialTicks) {
        if (!this.shouldUpdateShootIndicator()) {
            return this.getShootIndicatorEnd(partialTicks);
        }
        return new Vec3(
                Mth.lerp(partialTicks, this.xo, this.getX()),
                Mth.lerp(partialTicks, this.yo, this.getY()) + this.getEyeHeight(),
                Mth.lerp(partialTicks, this.zo, this.getZ()));
    }

    @Override
    public Vec3 getShootIndicatorEnd(float partialTicks) {
        return this.oClientShootIndicatorEnd.lerp(this.clientShootIndicatorEnd, partialTicks);
    }

    @Override
    public float getShootIndicatorProgress(float partialTicks) {
        return Mth.lerp(partialTicks, this.oClientShootIndicatorProgress, this.clientShootIndicatorProgress);
    }

    @Override
    public boolean shouldUpdateShootIndicator() {
        return this.entityData.get(SHOOT_INDICATOR_PROGRESS) >= 0;
    }

    public void setShootIndicatorEnd(Vector3f vector3f) {
        this.entityData.set(SHOOT_INDICATOR_END, vector3f);
    }

    public Vector3f getShootIndicatorEnd() {
        return this.entityData.get(SHOOT_INDICATOR_END);
    }

    public void setShootIndicatorProgress(float progress) {
        this.entityData.set(SHOOT_INDICATOR_PROGRESS, progress);
    }

    public float getShootIndicatorProgress() {
        return this.entityData.get(SHOOT_INDICATOR_PROGRESS);
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (!this.isIllusion() && this.bossInfo != null) {
            this.bossInfo.addPlayer(player);
        }
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (this instanceof IAncientGlint glint && glint.hasAncientGlint()
                && "ancient".equals(glint.getGlintTextureType())) {
            return;
        }
        if (!this.isIllusion() && this.bossInfo != null) {
            this.bossInfo.removePlayer(player);
        }
    }

    @Override
    protected void dropAllDeathLoot(DamageSource source) {
        if (this.isIllusion()) {
            return;
        }
        super.dropAllDeathLoot(source);
    }

    class IllusionerBlindnessSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        private int lastTargetId;

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else if (ArchIllusioner.this.isIllusion()) {
                return false;
            } else if (ArchIllusioner.this.getTarget() == null) {
                return false;
            } else if (ArchIllusioner.this.getTarget().getId() == this.lastTargetId) {
                return false;
            } else {
                return ArchIllusioner.this.level()
                        .getCurrentDifficultyAt(ArchIllusioner.this.blockPosition())
                        .isHarderThan((float) Difficulty.NORMAL.ordinal());
            }
        }

        public void start() {
            super.start();
            LivingEntity livingentity = ArchIllusioner.this.getTarget();
            if (livingentity != null) {
                this.lastTargetId = livingentity.getId();
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 360;
        }

        protected void performSpellCasting() {
            MobEffectUtils.forceAdd(
                    ArchIllusioner.this.getTarget(),
                    new MobEffectInstance(GoetyEffects.SENSE_LOSS.get(), 300, 1),
                    ArchIllusioner.this);
        }

        @Nullable
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
        }

        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.BLINDNESS;
        }
    }

    class IllusionerMirrorSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else if (ArchIllusioner.this.isIllusion()) {
                return false;
            } else {
                return !ArchIllusioner.this.hasEffect(MobEffects.INVISIBILITY);
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void performSpellCasting() {
            ArchIllusioner.this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
        }

        @Nullable
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
        }

        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.DISAPPEAR;
        }
    }

    class TrueMirrorSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }

            if (ArchIllusioner.this.isIllusion()) {
                return false;
            }

            List<ArchIllusioner> nearbyIllusions = ArchIllusioner.this.level()
                    .getEntitiesOfClass(ArchIllusioner.class,
                            ArchIllusioner.this.getBoundingBox().inflate(32.0D),
                            entity -> entity.isIllusion() && entity.isAlive()
                                    && MobUtil.areAllies(ArchIllusioner.this, entity));

            return nearbyIllusions.size() <= 0;
        }

        protected int getCastingTime() {
            return 60;
        }

        protected int getCastingInterval() {
            return 200;
        }

        protected void performSpellCasting() {
            ArchIllusioner.this.teleport();
            this.spawnIllusions();
        }

        private void spawnIllusions() {
            LivingEntity target = ArchIllusioner.this.getTarget();
            int illusionCount = 7 + ArchIllusioner.this.level().random.nextInt(7);

            for (int i = 0; i < illusionCount; i++) {
                double angle = ArchIllusioner.this.level().random.nextDouble() * 2 * Math.PI;
                double distance = 1.0 + ArchIllusioner.this.level().random.nextDouble() * 3.0;
                double x = ArchIllusioner.this.getX() + Math.cos(angle) * distance;
                double y = ArchIllusioner.this.getY();
                double z = ArchIllusioner.this.getZ() + Math.sin(angle) * distance;
                if (target != null && target.isAlive() && ArchIllusioner.this.level().random.nextBoolean()) {
                    double targetAngle = ArchIllusioner.this.level().random.nextDouble() * 2 * Math.PI;
                    double targetDistance = 1.0 + ArchIllusioner.this.level().random.nextDouble() * 3.0;
                    x = target.getX() + Math.cos(targetAngle) * targetDistance;
                    y = target.getY();
                    z = target.getZ() + Math.sin(targetAngle) * targetDistance;
                }

                BlockPos pos = BlockPos.containing(x, y, z);
                pos = ArchIllusioner.this.level()
                        .getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, pos);

                if (ArchIllusioner.this.level().noCollision(
                        ArchIllusioner.this.getType().getAABB(
                                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) {

                    ArchIllusioner illusion = (ArchIllusioner) ArchIllusioner.this.getType()
                            .create(ArchIllusioner.this.level());

                    illusion.setIllusion(true);
                    illusion.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    illusion.setTarget(ArchIllusioner.this.getTarget());
                    if (i == illusionCount - 1 && target != null && target.isAlive() && !(target instanceof Player)) {
                        illusion.setTarget(target);
                        if (target instanceof Mob mobTarget) {
                            mobTarget.setTarget(illusion);
                        }
                    }

                    illusion.setTrueOwner(ArchIllusioner.this);

                    illusion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        if (slot != EquipmentSlot.MAINHAND) {
                            illusion.setItemSlot(slot, ItemStack.EMPTY);
                        }
                    }

                    ArchIllusioner.this.level().addFreshEntity(illusion);
                }
            }
        }

        @Nullable
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
        }

        protected SpellcasterIllager.IllagerSpell getSpell() {
            return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
        }
    }

    class IllusionMasterTargetGoal extends TargetGoal {
        private int lastHurtMobTimestamp;
        private int lastHurtByTimestamp;

        public IllusionMasterTargetGoal() {
            super(ArchIllusioner.this, false);
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        public boolean canUse() {
            if (!ArchIllusioner.this.isIllusion()) {
                return false;
            }
            LivingEntity master = ArchIllusioner.this.getTrueOwner();
            if (master == null || !master.isAlive()) {
                return false;
            }
            if (master instanceof Mob masterMob) {
                LivingEntity masterTarget = masterMob.getTarget();
                if (this.isValidTarget(masterTarget)) {
                    this.targetMob = masterTarget;
                    return true;
                }
            }
            LivingEntity hurtMob = master.getLastHurtMob();
            if (hurtMob != null && hurtMob.isAlive()
                    && master.getLastHurtMobTimestamp() != this.lastHurtMobTimestamp
                    && this.isValidTarget(hurtMob)) {
                this.targetMob = hurtMob;
                this.lastHurtMobTimestamp = master.getLastHurtMobTimestamp();
                return true;
            }
            LivingEntity hurtByMob = master.getLastHurtByMob();
            if (hurtByMob != null && hurtByMob.isAlive()
                    && master.getLastHurtByMobTimestamp() != this.lastHurtByTimestamp
                    && this.isValidTarget(hurtByMob)) {
                this.targetMob = hurtByMob;
                this.lastHurtByTimestamp = master.getLastHurtByMobTimestamp();
                return true;
            }
            return false;
        }

        private boolean isValidTarget(@Nullable LivingEntity target) {
            if (target == null || !target.isAlive() || target == ArchIllusioner.this.getTrueOwner()) {
                return false;
            }
            return this.canAttack(target, TargetingConditions.DEFAULT);
        }

        public void start() {
            ArchIllusioner.this.setTarget(this.targetMob);
            super.start();
        }
    }
}
