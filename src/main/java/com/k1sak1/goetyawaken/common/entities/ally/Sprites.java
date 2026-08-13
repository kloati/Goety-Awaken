package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.ai.FloatAroundGoal;
import com.Polarice3.Goety.common.entities.neutral.SummonedFlying;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.spells.storm.ShockingSpell;
import com.Polarice3.Goety.common.magic.spells.frost.FrostNovaSpell;
import com.Polarice3.Goety.common.magic.spells.nether.FireBlastSpell;
import com.Polarice3.Goety.common.magic.spells.necromancy.LeechingSpell;
import com.Polarice3.Goety.common.magic.spells.geomancy.EarthFistSpell;
import com.Polarice3.Goety.common.magic.spells.wild.EntanglingSpell;
import com.Polarice3.Goety.common.magic.spells.void_spells.BanishSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SLightningPacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.magic.spells.abyss.NewGulfTentacleSpell;
import com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Sprites extends SummonedFlying {
    public int attackTime;
    private static final int MAX_TRAIL_POINTS = 12;
    private static final double MIN_TRAIL_SPEED = 0.03D;
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Sprites.class,
            EntityDataSerializers.INT);

    @OnlyIn(Dist.CLIENT)
    private List<TrailPosition> trailPositions;

    @OnlyIn(Dist.CLIENT)
    private int trailUpdateTick;
    private static final String[] VARIANT_TYPES = { "storm", "frost", "nether", "abyss", "necro", "geo", "wild",
            "void" };

    public Sprites(EntityType<? extends SummonedFlying> type, Level worldIn) {
        super(type, worldIn);
        this.attackTime = 0;
        this.setNoGravity(true);
        this.moveControl = new MoveHelperController(this);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, 0);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FloatAroundGoal<>(this, 5.0F, 2, 1.4D));
        this.goalSelector.addGoal(7, new LookAroundGoal(this));
        this.goalSelector.addGoal(7, new ShockAttackGoal(this));
    }

    public void followGoal() {
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SpriteHealth.get())
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SpriteDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SpriteHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SpriteDamage.get());
    }

    public void executeSpellAttack(LivingEntity target, double attackRange, int potency) {
        Spell spell = getSpellByVariant();
        ItemStack castingItem = getCastingItemByVariant();
        int damage = (int) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        SoundEvent soundEvent = spell.CastingSound(this);
        if (soundEvent != null) {
            this.playSound(soundEvent, spell.castingVolume(), spell.castingPitch());
        }
        int radiusincrease = 0;
        if (this.getVariant() == 1 || this.getVariant() == 2) {
            radiusincrease += 2;
        }

        int adjustedRange = (int) attackRange;

        if (this.getVariant() == 3) {
            adjustedRange /= 2;
        }

        if (this.getVariant() == 4) {
            potency += 3;
        }
        potency += Mth.ceil(damage / 2.0F);
        if (this.getVariant() == 7) {
            if (spell instanceof BanishSpell banishSpell) {
                banishSpell.touchResult((ServerLevel) this.level(), this, target, castingItem,
                        WandUtil.getStats(this, spell).setRange(adjustedRange).increasePotency(potency));
            }
        } else {
            spell.mobSpellResult(this, castingItem,
                    WandUtil.getStats(this, spell).setRange(adjustedRange).increasePotency(potency)
                            .increaseRadius(radiusincrease));
        }
        if (this.getVariant() == 7 && this.getRandom().nextFloat() < 0.25F) {
            this.discard();
        }
    }

    private Spell getSpellByVariant() {
        return switch (this.getVariant()) {
            case 0 -> new ShockingSpell();
            case 1 -> new FrostNovaSpell();
            case 2 -> new FireBlastSpell();
            case 3 -> new NewGulfTentacleSpell();
            case 4 -> new LeechingSpell();
            case 5 -> new EarthFistSpell();
            case 6 -> new EntanglingSpell();
            case 7 -> new BanishSpell();
            default -> new ShockingSpell();
        };
    }

    private ItemStack getCastingItemByVariant() {
        return switch (this.getVariant()) {
            case 0 -> this.isUpgraded() ? ModItems.STORM_STAFF.get().getDefaultInstance() : ItemStack.EMPTY;
            case 1 -> this.isUpgraded() ? ModItems.FROST_STAFF.get().getDefaultInstance() : ItemStack.EMPTY;
            case 2 -> this.isUpgraded() ? ModItems.NETHER_STAFF.get().getDefaultInstance() : ItemStack.EMPTY;
            case 3 -> this.isUpgraded() ? ModItems.ABYSS_STAFF.get().getDefaultInstance() : ItemStack.EMPTY;
            case 4 -> this.isUpgraded() ? ModItems.NECRO_STAFF.get().getDefaultInstance() : ItemStack.EMPTY;
            case 5 -> ItemStack.EMPTY;
            case 6 -> ItemStack.EMPTY;
            case 7 -> this.isUpgraded() ? ModItems.VOID_STAFF.get().getDefaultInstance() : ItemStack.EMPTY;
            default -> this.isUpgraded() ? ModItems.STORM_STAFF.get().getDefaultInstance() : ItemStack.EMPTY;
        };
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setSpriteSpawn();
        return pSpawnData;
    }

    public void setSpriteSpawn() {
        if (this.getTrueOwner() == null) {
            this.setBoundPos(this.blockPosition());
            this.setWandering(false);
            this.setStaying(false);
        }
    }

    public boolean onClimbable() {
        return false;
    }

    public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
        return false;
    }

    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    public String getVariantName() {
        return VARIANT_TYPES[this.getVariant()];
    }

    public void setVariant(int variant) {
        int clampedVariant = Math.max(0, Math.min(variant, VARIANT_TYPES.length - 1));
        this.entityData.set(DATA_VARIANT_ID, clampedVariant);
    }

    public static String[] getVariantTypes() {
        return VARIANT_TYPES;
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.tickCount % 5 == 0 || this.attackTime > 0) {
                spawnVariantParticles(serverLevel);
            }
            if (this.isStaying()) {
                this.getMoveControl().strafe(0.0F, 0.0F);
            }
        }
        if (this.level().isClientSide) {
            this.updateTrailPositions();
        }
    }

    public boolean isNoGravity() {
        return true;
    }

    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.level().isClientSide && this.trailPositions != null) {
            this.trailPositions.clear();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updateTrailPositions() {
        if (this.trailPositions == null) {
            this.trailPositions = new ArrayList<>();
        }

        this.trailUpdateTick++;
        if (this.trailUpdateTick < 2) {
            return;
        }
        this.trailUpdateTick = 0;

        Vec3 delta = this.getDeltaMovement();
        double speed = delta.length();

        if (speed >= MIN_TRAIL_SPEED && this.isAlive()) {
            double x = this.getX();
            double y = this.getY() + this.getBbHeight() * 0.5D;
            double z = this.getZ();
            this.trailPositions.add(0, new TrailPosition(new Vec3(x, y, z), 0));

            if (this.trailPositions.size() > MAX_TRAIL_POINTS) {
                this.trailPositions.remove(this.trailPositions.size() - 1);
            }
        } else if (!this.trailPositions.isEmpty()) {
            this.trailPositions.remove(this.trailPositions.size() - 1);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public List<TrailPosition> getPublicTrailPoints() {
        if (this.trailPositions == null) {
            this.trailPositions = new ArrayList<>();
        }
        return this.trailPositions;
    }

    private void spawnVariantParticles(ServerLevel serverLevel) {
        double x = this.getRandomX(0.5D);
        double y = this.getRandomY();
        double z = this.getRandomZ(0.5D);

        switch (this.getVariant()) {
            case 0 ->
                serverLevel.sendParticles(ModParticleTypes.SPELL_ELECTRIC.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            case 1 ->
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            case 2 ->
                serverLevel.sendParticles(ModParticleTypes.BIG_FIRE.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            case 3 ->
                serverLevel.sendParticles(ParticleTypes.FALLING_DRIPSTONE_WATER, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            case 4 ->
                serverLevel.sendParticles(ModParticleTypes.NECRO_FLAME.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            case 5 -> {
                serverLevel.sendParticles(new net.minecraft.core.particles.BlockParticleOption(
                        ParticleTypes.BLOCK, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState()),
                        x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            case 6 ->
                serverLevel.sendParticles(ModParticleTypes.SPELL_SQUARE.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D,
                        0.0D);
            case 7 ->
                serverLevel.sendParticles(ModParticleTypes.SMALL_END_FIRE.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            default ->
                serverLevel.sendParticles(ModParticleTypes.SPELL_ELECTRIC.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return SoundEvents.ALLAY_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public void die(DamageSource pCause) {
        super.die(pCause);
        this.playSound(SoundEvents.GLASS_BREAK, this.getSoundVolume(), this.getVoicePitch());
        if (this.level() instanceof ServerLevel serverLevel) {
            ColorUtil colorUtil = new ColorUtil(0xfef597);
            serverLevel.sendParticles(new SphereExplodeParticleOption(colorUtil, 1.0F, 1), this.getX(), this.getY(),
                    this.getZ(), 1, 0, 0, 0, 0);
            for (int i = 0; i < 16; ++i) {
                Vec3 vec3 = this.position();
                int random1 = this.getRandom().nextIntBetweenInclusive(-2, 2);
                int random2 = this.getRandom().nextIntBetweenInclusive(-2, 2);
                int random3 = this.getRandom().nextIntBetweenInclusive(-2, 2);
                Vec3 vec31 = vec3.add(this.getRandom().nextDouble() * random1, this.getRandom().nextDouble() * random2,
                        this.getRandom().nextDouble() * random3);
                ModNetwork.sendToALL(new SLightningPacket(vec3, vec31, colorUtil, 8));
            }
        }
        this.discard();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Variant")) {
            this.setVariant(pCompound.getInt("Variant"));
        }
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.91F));
            }
        }
        this.calculateEntityAnimation(false);
    }

    protected void checkFallDamage(double p_27419_, boolean p_27420_, BlockState p_27421_, BlockPos p_27422_) {
    }

    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    static class MoveHelperController extends MoveControl {
        private final Sprites sprites;
        private int floatDuration;

        public MoveHelperController(Sprites sprites) {
            super(sprites);
            this.sprites = sprites;
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += this.sprites.getRandom().nextInt(5) + 2;
                    Vec3 vec3 = new Vec3(this.wantedX - this.sprites.getX(), this.wantedY - this.sprites.getY(),
                            this.wantedZ - this.sprites.getZ());
                    double d0 = vec3.length();
                    vec3 = vec3.normalize();
                    if (this.canReach(vec3, Mth.ceil(d0))) {
                        this.sprites.setDeltaMovement(this.sprites.getDeltaMovement().add(vec3.scale(0.15D)));
                    } else {
                        this.operation = Operation.WAIT;
                    }
                }
            }
        }

        private boolean canReach(Vec3 pVec, int pSteps) {
            AABB aabb = this.sprites.getBoundingBox();

            for (int i = 1; i < pSteps; ++i) {
                aabb = aabb.move(pVec);
                if (!this.sprites.level().noCollision(this.sprites, aabb)) {
                    return false;
                }
            }

            return true;
        }
    }

    static class ShockAttackGoal extends Goal {
        private final Sprites sprites;

        public ShockAttackGoal(Sprites p_i45837_1_) {
            this.sprites = p_i45837_1_;
        }

        public boolean canUse() {
            return this.sprites.getTarget() != null;
        }

        public void start() {
            this.sprites.attackTime = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.sprites.getTarget();
            double attackRange = this.sprites.getAttributeValue(Attributes.FOLLOW_RANGE);
            if (livingentity != null && livingentity.distanceTo(this.sprites) < attackRange
                    && this.sprites.hasLineOfSight(livingentity)) {
                ++this.sprites.attackTime;
                if (this.sprites.attackTime == 20) {
                    boolean flag;
                    int potency = 0;
                    Vec3 vec31 = livingentity.getDeltaMovement();
                    if (Math.abs(vec31.x) > 0.1F || Math.abs(vec31.y) > 0.1F || Math.abs(vec31.z) > 0.1F) {
                        flag = this.sprites.getRandom().nextFloat() < 0.4F;
                        potency += 1;
                    } else {
                        flag = this.sprites.getRandom().nextFloat() < 0.66F;
                    }
                    if (flag) {
                        this.sprites.executeSpellAttack(livingentity, attackRange, potency);
                    }
                    this.sprites.attackTime = -20 + this.sprites.getRandom().nextInt(20);
                }
            } else {
                if (this.sprites.attackTime > 0) {
                    --this.sprites.attackTime;
                }
            }
        }
    }

    static class LookAroundGoal extends Goal {
        private final Sprites sprites;

        public LookAroundGoal(Sprites p_i45839_1_) {
            this.sprites = p_i45839_1_;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity toLookAt = null;
            if (this.sprites.getTarget() != null) {
                toLookAt = this.sprites.getTarget();
            }
            if (toLookAt == null) {
                if (this.sprites.getRandom().nextFloat() < 0.02F) {
                    toLookAt = this.sprites.level().getNearestEntity(
                            this.sprites.level().getEntitiesOfClass(LivingEntity.class,
                                    this.sprites.getBoundingBox().inflate(8.0F, 3.0D, 8.0F), (p_148124_) -> true),
                            TargetingConditions.forNonCombat().range(8.0F), this.sprites, this.sprites.getX(),
                            this.sprites.getEyeY(), this.sprites.getZ());
                }
            }
            if (toLookAt == null) {
                Vec3 vector3d = this.sprites.getDeltaMovement();
                this.sprites.setYRot(-((float) Mth.atan2(vector3d.x, vector3d.z)) * (180F / (float) Math.PI));
            } else {
                double d1 = toLookAt.getX() - this.sprites.getX();
                double d2 = toLookAt.getZ() - this.sprites.getZ();
                this.sprites.getLookControl().setLookAt(toLookAt, 10.0F, this.sprites.getMaxHeadXRot());
                this.sprites.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
            }
            this.sprites.yBodyRot = this.sprites.getYRot();

        }
    }

}
