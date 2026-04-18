package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.utils.MobEffectUtils;
import javax.annotation.Nullable;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import com.k1sak1.goetyawaken.common.entities.projectiles.ExplosiveArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.HumanoidArm;
import com.k1sak1.goetyawaken.init.ModSounds;

public class ArchIllusionerServant extends SpellcasterIllagerServant implements RangedAttackMob {
    private static final int NUM_ILLUSIONS = 4;
    private static final int ILLUSION_TRANSITION_TICKS = 3;
    private static final int ILLUSION_SPREAD = 3;
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

    public ArchIllusionerServant(EntityType<? extends ArchIllusionerServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.clientSideIllusionOffsets = new Vec3[2][4];

        for (int i = 0; i < 4; ++i) {
            this.clientSideIllusionOffsets[0][i] = Vec3.ZERO;
            this.clientSideIllusionOffsets[1][i] = Vec3.ZERO;
        }

    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SpellcasterIllagerServant.SpellcasterCastingSpellGoal());
        this.goalSelector.addGoal(1, new ArchIllusionerServant.TrueMirrorSpellGoal());
        this.goalSelector.addGoal(1, new ArchIllusionerServant.IllusionerMirrorSpellGoal());
        this.goalSelector.addGoal(1, new ArchIllusionerServant.IllusionerBlindnessSpellGoal());
        this.goalSelector.addGoal(6, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ArchIllusionerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.ArchIllusionerServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ArchIllusionerServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ArchIllusionerServantMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ArchIllusionerServantFollowRange.get());
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason,
            @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsIllusion", this.isIllusion);
        compound.putInt("IllusionHitCount", this.illusionHitCount);
        compound.putInt("IllusionLifetime", this.illusionLifetime);
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
            this.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        }
    }

    public int getIllusionLifetime() {
        return this.illusionLifetime;
    }

    public void incrementIllusionLifetime() {
        this.illusionLifetime++;
        if (this.illusionLifetime >= 200) {
            this.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        }
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3.0D, 0.0D, 3.0D);
    }

    public void aiStep() {
        super.aiStep();

        if (this.isIllusion && !this.level().isClientSide()) {
            this.incrementIllusionLifetime();
            if (this.illusionLifetime >= 200) {
                this.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
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
                                    .getBlock() instanceof net.minecraft.world.level.block.piston.MovingPistonBlock)) {
                        this.stuckTime += 20;
                        this.teleport();
                    } else {
                        if (this.stuckTime > 0) {
                            --this.stuckTime;
                        }
                    }
                }

                if (this.stuckTime > 50) {
                    if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
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

            if (target == null && this.tickCount % 100 == 0) {
                if (this.getTrueOwner() != null && this.isFollowing()) {
                    if (this.getTrueOwner() instanceof Player player) {
                        double distanceToOwner = this.distanceToSqr(player);
                        if (distanceToOwner > 1024) {
                            this.teleportTowardsTarget(player);
                        }
                    }
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
                float f = -6.0F;
                int i = 13;

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
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ILLUSIONER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.random.nextBoolean() ? ModSounds.ARCH_ILLUSIONER_DEATH_1.get()
                : ModSounds.ARCH_ILLUSIONER_DEATH_2.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ILLUSIONER_HURT;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.ILLUSIONER_CAST_SPELL;
    }

    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {
    }

    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(
                ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem)));
        ItemStack bowStack = this.getItemInHand(
                ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem));
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
            double d1 = pTarget.getY(0.3333333333333333D) - explosiveArrow.getY();
            double d2 = pTarget.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            explosiveArrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 2.0F,
                    (float) (14 - this.level().getDifficulty().getId() * 4));

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

            for (int i = 0; i < 64; ++i) {
                Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(),
                        this.getZ() - entity.getZ());
                vector3d = vector3d.normalize();
                double d0 = 16.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vector3d.y * d0;
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;

                net.minecraft.core.BlockPos blockPos = net.minecraft.core.BlockPos.containing(d1, d2, d3);
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

            for (int i = 0; i < 128; ++i) {
                double blockRange = 128.0D;
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * blockRange;
                double d4 = this.getY() + (this.getRandom().nextDouble() - 0.5D) * (blockRange / 2.0D);
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * blockRange;

                if (this.randomTeleport(d3, d4, d5, false)) {
                    this.stuckTime = 0;
                    this.level().broadcastEntityEvent(this, (byte) 100);
                    this.level().gameEvent(net.minecraft.world.level.gameevent.GameEvent.TELEPORT, this.position(),
                            net.minecraft.world.level.gameevent.GameEvent.Context.of(this));

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
        this.stuckTime = 0;
        this.level().broadcastEntityEvent(this, (byte) 100);
        this.level().gameEvent(net.minecraft.world.level.gameevent.GameEvent.TELEPORT, this.position(),
                net.minecraft.world.level.gameevent.GameEvent.Context.of(this));

        if (!this.isSilent()) {
            this.level().playSound((Player) null, this.prevX, this.prevY, this.prevZ,
                    SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    public boolean teleportChance() {
        return this.level().random.nextFloat() <= 0.25F;
    }

    private boolean canSeeBlock(Entity entity, net.minecraft.core.BlockPos blockPos) {
        net.minecraft.world.phys.BlockHitResult hitresult = this.level().clip(
                new net.minecraft.world.level.ClipContext(
                        this.getEyePosition(),
                        net.minecraft.world.phys.Vec3.atCenterOf(blockPos),
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE,
                        this));
        return hitresult.getType() == net.minecraft.world.phys.HitResult.Type.MISS ||
                hitresult.getBlockPos().equals(blockPos);
    }

    public AbstractIllagerServant.IllagerServantArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllagerServant.IllagerServantArmPose.SPELLCASTING;
        } else {
            return this.isAggressive() ? AbstractIllagerServant.IllagerServantArmPose.BOW_AND_ARROW
                    : AbstractIllagerServant.IllagerServantArmPose.CROSSED;
        }
    }

    class IllusionerBlindnessSpellGoal extends SpellcasterIllagerServant.SpellcasterUseSpellGoal {
        private int lastTargetId;

        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else if (ArchIllusionerServant.this.isIllusion()) {
                return false;
            } else if (ArchIllusionerServant.this.getTarget() == null) {
                return false;
            } else if (ArchIllusionerServant.this.getTarget().getId() == this.lastTargetId) {
                return false;
            } else {
                return ArchIllusionerServant.this.level()
                        .getCurrentDifficultyAt(ArchIllusionerServant.this.blockPosition())
                        .isHarderThan((float) Difficulty.NORMAL.ordinal());
            }
        }

        public void start() {
            super.start();
            LivingEntity livingentity = ArchIllusionerServant.this.getTarget();
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
                    ArchIllusionerServant.this.getTarget(),
                    new MobEffectInstance(GoetyEffects.SENSE_LOSS.get(), 800, 1),
                    ArchIllusionerServant.this);
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
        }

        protected SpellcasterIllagerServant.IllagerServantSpell getSpell() {
            return SpellcasterIllagerServant.IllagerServantSpell.BLINDNESS;
        }
    }

    class IllusionerMirrorSpellGoal extends SpellcasterIllagerServant.SpellcasterUseSpellGoal {
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else if (ArchIllusionerServant.this.isIllusion()) {
                return false;
            } else {
                return !ArchIllusionerServant.this.hasEffect(MobEffects.INVISIBILITY);
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 340;
        }

        protected void performSpellCasting() {
            ArchIllusionerServant.this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
        }

        @Nullable
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
        }

        protected SpellcasterIllagerServant.IllagerServantSpell getSpell() {
            return SpellcasterIllagerServant.IllagerServantSpell.DISAPPEAR;
        }
    }

    @Override
    public HumanoidArm getMainArm() {
        return this.isIllusion() ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean result = super.hurt(pSource, pAmount);
        if (result && this.isIllusion) {
            this.incrementIllusionHitCount();
        }
        return result;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (this.isIllusion) {
            return InteractionResult.PASS;
        }

        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        ItemStack currentMainHandItem = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (itemstack.getItem() instanceof BowItem) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                this.dropEquipment(EquipmentSlot.MAINHAND, currentMainHandItem);
                this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                            this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else if (itemstack.is(ItemTags.ARROWS)) {
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                ItemStack offhandItem = this.getOffhandItem();
                if (offhandItem.isEmpty()) {
                    this.setItemSlot(EquipmentSlot.OFFHAND, itemstack.split(itemstack.getCount()));
                } else if (offhandItem.is(itemstack.getItem())
                        && offhandItem.getCount() < offhandItem.getMaxStackSize()) {
                    int needed = offhandItem.getMaxStackSize() - offhandItem.getCount();
                    int toAdd = Math.min(needed, itemstack.getCount());
                    offhandItem.grow(toAdd);
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(toAdd);
                    }
                } else {
                    this.spawnAtLocation(offhandItem);
                    this.setItemSlot(EquipmentSlot.OFFHAND, itemstack.split(itemstack.getCount()));
                }
                this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D),
                            this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                if (!pPlayer.getAbilities().instabuild) {
                    if (!itemstack.isEmpty()) {
                        itemstack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    class TrueMirrorSpellGoal extends SpellcasterIllagerServant.SpellcasterUseSpellGoal {
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }

            if (ArchIllusionerServant.this.isIllusion()) {
                return false;
            }

            java.util.List<ArchIllusionerServant> nearbyIllusions = ArchIllusionerServant.this.level()
                    .getEntitiesOfClass(ArchIllusionerServant.class,
                            ArchIllusionerServant.this.getBoundingBox().inflate(32.0D),
                            entity -> entity.isIllusion() && entity.isAlive());

            return nearbyIllusions.size() <= 0;
        }

        protected int getCastingTime() {
            return 60;
        }

        protected int getCastingInterval() {
            return 200;
        }

        protected void performSpellCasting() {
            ArchIllusionerServant.this.teleport();
            this.spawnIllusions();
        }

        private void spawnIllusions() {
            LivingEntity target = ArchIllusionerServant.this.getTarget();
            int illusionCount = 7 + ArchIllusionerServant.this.level().random.nextInt(7);

            for (int i = 0; i < illusionCount; i++) {
                double angle = ArchIllusionerServant.this.level().random.nextDouble() * 2 * Math.PI;
                double distance = 1.0 + ArchIllusionerServant.this.level().random.nextDouble() * 3.0;
                double x = ArchIllusionerServant.this.getX() + Math.cos(angle) * distance;
                double y = ArchIllusionerServant.this.getY();
                double z = ArchIllusionerServant.this.getZ() + Math.sin(angle) * distance;
                if (target != null && target.isAlive() && ArchIllusionerServant.this.level().random.nextBoolean()) {
                    double targetAngle = ArchIllusionerServant.this.level().random.nextDouble() * 2 * Math.PI;
                    double targetDistance = 1.0 + ArchIllusionerServant.this.level().random.nextDouble() * 3.0;
                    x = target.getX() + Math.cos(targetAngle) * targetDistance;
                    y = target.getY();
                    z = target.getZ() + Math.sin(targetAngle) * targetDistance;
                }

                net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(x, y, z);
                pos = ArchIllusionerServant.this.level()
                        .getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, pos);

                if (ArchIllusionerServant.this.level().noCollision(
                        ArchIllusionerServant.this.getType().getAABB(
                                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) {

                    ArchIllusionerServant illusion = (ArchIllusionerServant) ArchIllusionerServant.this.getType()
                            .create(ArchIllusionerServant.this.level());

                    illusion.setIllusion(true);
                    illusion.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    illusion.setTarget(ArchIllusionerServant.this.getTarget());
                    if (i == illusionCount - 1 && target != null && target.isAlive() && !(target instanceof Player)) {
                        illusion.setTarget(target);
                        if (target instanceof net.minecraft.world.entity.Mob mobTarget) {
                            mobTarget.setTarget(illusion);
                        }
                    }

                    if (ArchIllusionerServant.this.getTrueOwner() != null) {
                        illusion.setTrueOwner(ArchIllusionerServant.this);
                    }

                    illusion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        if (slot != EquipmentSlot.MAINHAND) {
                            illusion.setItemSlot(slot, ItemStack.EMPTY);
                        }
                    }

                    ArchIllusionerServant.this.level().addFreshEntity(illusion);
                }
            }
        }

        @Nullable
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
        }

        protected SpellcasterIllagerServant.IllagerServantSpell getSpell() {
            return SpellcasterIllagerServant.IllagerServantSpell.SUMMON_VEX;
        }
    }
}
