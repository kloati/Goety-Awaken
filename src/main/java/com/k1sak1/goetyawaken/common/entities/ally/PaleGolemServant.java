package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.api.entities.IGolem;
import com.Polarice3.Goety.common.entities.ally.golem.RaiderGolemServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.core.particles.ParticleTypes;

import javax.annotation.Nullable;

public class PaleGolemServant extends RaiderGolemServant implements IGolem {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(PaleGolemServant.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Integer> ATTACK_TICK_ID = SynchedEntityData.defineId(
            PaleGolemServant.class,
            EntityDataSerializers.INT);
    private int attackAnimationTick;

    public PaleGolemServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(ATTACK_TICK_ID, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.miscGoal();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public void miscGoal() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PaleGolemServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PaleGolemServantDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.PaleGolemServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 1.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.PaleGolemServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_KNOCKBACK), 1.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), 32.0D);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        return pSpawnData;
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    public double getAttackReachSqr(LivingEntity pEnemy) {
        return 3.0D;
    }

    @Override
    public boolean canAnimateMove() {
        return !this.isImmobile();
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    private boolean getFlag(int mask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & mask) != 0;
    }

    private void setFlag(int mask, boolean value) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte) (i & 255));
    }

    public int getAttackTick() {
        return this.entityData.get(ATTACK_TICK_ID);
    }

    public void setAttackTick(int attackTick) {
        this.entityData.set(ATTACK_TICK_ID, attackTick);
    }

    public int getAttackAnimationTick() {
        return this.attackAnimationTick;
    }

    public IronGolem.Crackiness getCrackiness() {
        return IronGolem.Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.attackAnimationTick = 10;
        this.level().broadcastEntityEvent(this, (byte) 4);
        float f = this.getAttackDamage();
        float f1 = (int) f > 0 ? f / 2.0F + (float) this.random.nextInt((int) f) : f;
        boolean flag = pEntity.hurt(this.damageSources().mobAttack(this), f1);
        if (flag) {
            double d2;
            if (pEntity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) pEntity;
                d2 = livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            } else {
                d2 = 0.0D;
            }

            double d0 = d2;
            double d1 = Math.max(0.0D, 1.0D - d0);
            pEntity.setDeltaMovement(pEntity.getDeltaMovement().add(0.0D, (double) 0.4F * d1, 0.0D));
            this.doEnchantDamageEffects(this, pEntity);
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    private float getAttackDamage() {
        return (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 4) {
            this.attackAnimationTick = 10;
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick;
        }

        if (this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F && this.random.nextInt(5) == 0) {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY() - (double) 0.2F);
            int k = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.level().getBlockState(pos);
            if (!blockstate.isAir()) {
                this.level().addParticle(
                        new net.minecraft.core.particles.BlockParticleOption(
                                net.minecraft.core.particles.ParticleTypes.BLOCK, blockstate).setPos(pos),
                        this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + 0.1D,
                        this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(),
                        4.0D * ((double) this.random.nextFloat() - 0.5D),
                        0.5D,
                        ((double) this.random.nextFloat() - 0.5D) * 4.0D);
            }
        }

        if (this.level().isClientSide && this.tickCount % 10 == 0) {
            double x = this.getX();
            double y = this.getY() + this.getBbHeight() * 0.6;
            double z = this.getZ();
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + (this.random.nextDouble() - 0.5) * 0.4,
                    y + (this.random.nextDouble() - 0.5) * 0.4,
                    z + (this.random.nextDouble() - 0.5) * 0.4,
                    (this.random.nextDouble() - 0.5) * 0.02,
                    this.random.nextDouble() * 0.02,
                    (this.random.nextDouble() - 0.5) * 0.02);
        }

        if (!this.level().isClientSide) {
            this.setAttackTick(this.attackAnimationTick);
        } else {
            this.attackAnimationTick = this.getAttackTick();
        }
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack itemstack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (itemstack.is(ModItems.PALE_STEEL_INGOT.get())) {
                if (this.getHealth() < this.getMaxHealth()) {
                    float healAmount = this.getMaxHealth() * 0.20F;
                    this.heal(healAmount);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
                    if (this.level().isClientSide) {
                        for (int i = 0; i < 15; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            this.level().addParticle(ParticleTypes.HEART,
                                    this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                                            - (double) this.getBbWidth(),
                                    this.getY() + 0.5D + (double) (this.random.nextFloat() * this.getBbHeight()),
                                    this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F)
                                            - (double) this.getBbWidth(),
                                    d0, d1, d2);
                        }
                    }

                    return net.minecraft.world.InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }
}