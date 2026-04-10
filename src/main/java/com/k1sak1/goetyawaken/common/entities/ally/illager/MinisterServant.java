package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class MinisterServant extends SpellcasterIllagerServant implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> HAS_STAFF = SynchedEntityData.defineId(MinisterServant.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(MinisterServant.class,
            EntityDataSerializers.INT);

    public float staffDamage;
    public int coolDown;
    public int staffRecoveryTime;
    public int deathTime = 0;
    public float deathRotation = 0.0F;
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState castAnimationState = new AnimationState();
    public final AnimationState laughAnimationState = new AnimationState();
    public final AnimationState laughTargetAnimationState = new AnimationState();
    public final AnimationState commandAnimationState = new AnimationState();
    public final AnimationState blockAnimationState = new AnimationState();
    public final AnimationState smashedAnimationState = new AnimationState();
    public final AnimationState speechAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    public MinisterServant(EntityType<? extends MinisterServant> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(HAS_STAFF, true);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.MinisterHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.MinisterDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.MinisterHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.MinisterDamage.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MINISTER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MINISTER_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.MINISTER_HURT.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.MINISTER_CELEBRATE.get();
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int pType) {
        if (pType < 0 || pType >= this.OutfitTypeNumber()) {
            pType = this.random.nextInt(this.OutfitTypeNumber());
        }
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public int OutfitTypeNumber() {
        return 3;
    }

    public boolean hasStaff() {
        return this.entityData.get(HAS_STAFF);
    }

    public void setHasStaff(boolean staff) {
        this.entityData.set(HAS_STAFF, staff);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasStaff", this.hasStaff());
        compound.putFloat("StaffDamage", this.staffDamage);
        compound.putInt("Outfit", this.getOutfitType());
        compound.putInt("CoolDown", this.coolDown);
        compound.putInt("StaffRecoveryTime", this.staffRecoveryTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HasStaff")) {
            this.setHasStaff(compound.getBoolean("HasStaff"));
        }
        if (compound.contains("StaffDamage")) {
            this.staffDamage = compound.getFloat("StaffDamage");
        }
        if (compound.contains("Outfit")) {
            this.setOutfitType(compound.getInt("Outfit"));
        }
        if (compound.contains("CoolDown")) {
            this.coolDown = compound.getInt("CoolDown");
        }
        if (compound.contains("StaffRecoveryTime")) {
            this.staffRecoveryTime = compound.getInt("StaffRecoveryTime");
        }
    }

    private boolean isAlliedIllagerServant(LivingEntity livingEntity) {
        if (livingEntity instanceof RaiderServant) {
            if (livingEntity instanceof RaiderServant raider) {
                return raider.getTrueOwner() != null &&
                        raider.getTrueOwner() == this.getTrueOwner();
            }
        }

        if (livingEntity instanceof TamableAnimal tamable) {
            if (tamable.getOwner() != null && this.getTrueOwner() != null) {
                return tamable.getOwner().getUUID().equals(this.getTrueOwner().getUUID());
            }
        }

        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new TeethSpellGoal());
        this.goalSelector.addGoal(3, new SpeechGoal());
        this.goalSelector.addGoal(4, new LaughTargetGoal());
        this.goalSelector.addGoal(5, new CommandGoal());
        this.goalSelector.addGoal(6, new MinisterRangedGoalWithoutIllagers(this, 1.0D, 20, 16.0F));
        this.goalSelector.addGoal(6, new MinisterRangedGoalWithIllagers(this, 1.0D, 20, 60, 16.0F));
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.attackAnimationState.start(this.tickCount);
        } else if (pId == 5) {
            this.castAnimationState.start(this.tickCount);
        } else if (pId == 6) {
            this.laughAnimationState.start(this.tickCount);
        } else if (pId == 7) {
            this.laughTargetAnimationState.start(this.tickCount);
        } else if (pId == 8) {
            this.blockAnimationState.start(this.tickCount);
        } else if (pId == 10) {
            this.deathAnimationState.start(this.tickCount);
            this.deathRotation = this.getYRot();
            this.playSound(ModSounds.MINISTER_DEATH.get(), 4.0F, 1.0F);
        } else if (pId == 11) {
            this.speechAnimationState.start(this.tickCount);
        } else if (pId == 12) {
            this.commandAnimationState.start(this.tickCount);
        } else if (pId == 13) {
            this.smashedAnimationState.start(this.tickCount);
            this.setHasStaff(false);
        } else if (pId == 14) {
            this.setAggressive(true);
        } else if (pId == 15) {
            this.setAggressive(false);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.hasStaff()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    ServerParticleUtil.addAuraParticles(serverLevel, ParticleTypes.ENCHANT, this, 8.0F);
                    for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(8.0F, 4.0F, 8.0F))) {
                        if (living != this && this.isAlliedIllagerServant(living)) {
                            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 0, false, false));
                        }
                    }
                }
            } else {
                ++this.staffRecoveryTime;
                if (this.staffRecoveryTime >= 2400) {
                    this.setHasStaff(true);
                    this.staffRecoveryTime = 0;
                    this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                }
            }

            if (this.coolDown > 0) {
                --this.coolDown;
            }
            this.setAggressive(this.getTarget() != null);
        }

        if (this.isDeadOrDying()) {
            for (AnimationState animationState : new AnimationState[] { this.attackAnimationState,
                    this.castAnimationState,
                    this.laughAnimationState, this.laughTargetAnimationState, this.commandAnimationState,
                    this.blockAnimationState, this.speechAnimationState }) {
                animationState.stop();
            }
            this.deathAnimationState.startIfStopped(this.tickCount);
            this.setYRot(this.deathRotation);
            this.setYBodyRot(this.deathRotation);
        }

        if (this.isCelebrating()) {
            if (this.tickCount % 100 == 0 && this.hurtTime <= 0) {
                this.laughAnimationState.start(this.tickCount);
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
        }
    }

    public boolean hasNearbyIllagers() {
        return !this.getNearbyIllagers().isEmpty();
    }

    public List<AbstractIllagerServant> getNearbyIllagers() {
        Predicate<AbstractIllagerServant> predicate = servant -> servant != this && servant.getTrueOwner() != null &&
                servant.getTrueOwner() == this.getTrueOwner();
        return this.level().getEntitiesOfClass(AbstractIllagerServant.class,
                this.getBoundingBox().inflate(32.0D, 16.0D, 32.0D),
                predicate);
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    public void die(DamageSource p_21014_) {

        if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
            for (Player player : serverLevel.getEntitiesOfClass(Player.class,
                    this.getBoundingBox().inflate(32.0F))) {
                SEHelper.setRestPeriod(player,
                        MathHelper.minecraftDayToTicks(MobsConfig.IllagerAssaultRestMinister.get()));
            }
            if (!this.canRevive(p_21014_)) {
                this.level().broadcastEntityEvent(this, (byte) 10);
                this.deathRotation = this.getYRot();
                ItemStack ominousOrbStack = new ItemStack(com.Polarice3.Goety.common.items.ModItems.OMINOUS_ORB.get());
                if (this.getTrueOwner() != null) {
                    FlyingItem flyingItem = new FlyingItem(
                            ModEntityType.FLYING_ITEM.get(),
                            this.level(),
                            this.getX(),
                            this.getY() + 1.0D,
                            this.getZ());
                    flyingItem.setOwner(this.getTrueOwner());
                    flyingItem.setItem(ominousOrbStack);
                    flyingItem.setParticle(ParticleTypes.SOUL);
                    flyingItem.setSecondsCool(30);

                    this.level().addFreshEntity(flyingItem);
                } else {
                    ItemEntity itemEntity = this.spawnAtLocation(ominousOrbStack);
                    if (itemEntity != null) {
                        itemEntity.setExtendedLifetime();
                    }
                }
            }
        }
        super.die(p_21014_);
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 40) {
            this.spawnAnim();
            this.remove(RemovalReason.KILLED);
        }
        this.setYRot(this.deathRotation);
        this.setYBodyRot(this.deathRotation);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (this.hasStaff()) {
            Vec3 vector3d = this.getViewVector(1.0F);
            double d1 = target.getX() - this.getX();
            double d2 = target.getY(0.5D) - this.getY(0.5D);
            double d3 = target.getZ() - this.getZ();

            com.Polarice3.Goety.common.entities.projectiles.MagicBolt magicBolt = new com.Polarice3.Goety.common.entities.projectiles.MagicBolt(
                    this.level(), this, d1, d2, d3);
            magicBolt.setYRot(this.getYRot());
            magicBolt.setXRot(this.getXRot());
            magicBolt.setPos(this.getX() + vector3d.x / 2, this.getEyeY() - 0.2, this.getZ() + vector3d.z / 2);
            this.playSound(ModSounds.CAST_SPELL.get(), 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level().addFreshEntity(magicBolt);
        } else {
            com.Polarice3.Goety.common.entities.projectiles.IllBomb snowball = new com.Polarice3.Goety.common.entities.projectiles.IllBomb(
                    this, this.level());
            double d0 = target.getEyeY() - (double) 1.1F;
            double d1 = target.getX() - this.getX();
            double d2 = d0 - snowball.getY();
            double d3 = target.getZ() - this.getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double) 0.2F;
            float velocity1 = target.distanceTo(this) >= 10.0F ? 1.0F : 0.5F;
            snowball.shoot(d1, d2 + d4, d3, velocity1, 0.5F);
            this.playSound(SoundEvents.WITCH_THROW, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level().addFreshEntity(snowball);
        }
    }

    class CastingSpellGoal extends SpellcasterCastingSpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (MinisterServant.this.getTarget() != null) {
                MobUtil.instaLook(MinisterServant.this, MinisterServant.this.getTarget());
                MinisterServant.this.getLookControl().setLookAt(MinisterServant.this.getTarget(), 500.0F,
                        (float) MinisterServant.this.getMaxHeadXRot());
            }
        }
    }

    abstract class CastingGoal extends SpellcasterUseSpellGoal {
        public boolean hasCastSound;

        @Override
        public boolean canUse() {
            return super.canUse() && !MinisterServant.this.isCastingSpell() && MinisterServant.this.coolDown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse()
                    && (MinisterServant.this.isCastingSpell() || MinisterServant.this.coolDown > 0);
        }

        public void start() {
            super.start();
            MinisterServant.this.spellCastingTickCount = this.getCastingTime();
            MinisterServant.this.coolDown = 20;
        }

        public void stop() {
            super.stop();
            MinisterServant.this.setIsCastingSpell(IllagerServantSpell.NONE);
            MinisterServant.this.coolDown = 20;
        }

        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                if (this.hasCastSound) {
                    MinisterServant.this.playSound(MinisterServant.this.getCastingSoundEvent(), 1.0F, 1.0F);
                }
            }
        }
    }

    class TeethSpellGoal extends CastingGoal {
        public int teethAmount;

        private TeethSpellGoal() {
            this.hasCastSound = true;
        }

        @Override
        public void start() {
            super.start();
            MinisterServant.this.castAnimationState.start(MinisterServant.this.tickCount);
            MinisterServant.this.level().broadcastEntityEvent(MinisterServant.this, (byte) 5);
        }

        protected int getCastingTime() {
            return 30;
        }

        protected int getCastingInterval() {
            if (MinisterServant.this.hasNearbyIllagers()) {
                return 360;
            } else {
                return 120;
            }
        }

        protected void performSpellCasting() {
            if (MinisterServant.this.getTarget() != null) {
                BlockPos blockPos = MinisterServant.this.getTarget().blockPosition();
                if (MinisterServant.this.getTarget().distanceTo(MinisterServant.this) <= 4.0F) {
                    this.surroundTeeth();
                } else {
                    for (int length = 0; length < 16; length++) {
                        blockPos = blockPos.offset(-2 + MinisterServant.this.getRandom().nextInt(4), 0,
                                -2 + MinisterServant.this.getRandom().nextInt(4));
                        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(blockPos.getX(),
                                blockPos.getY(), blockPos.getZ());

                        while (blockpos$mutable.getY() < blockPos.getY() + 8.0D
                                && !MinisterServant.this.level().getBlockState(blockpos$mutable).blocksMotion()) {
                            blockpos$mutable.move(Direction.UP);
                        }

                        if (MinisterServant.this.level().noCollision(new AABB(blockpos$mutable))) {
                            ++this.teethAmount;
                            com.Polarice3.Goety.common.entities.projectiles.ViciousTooth viciousTooth = new com.Polarice3.Goety.common.entities.projectiles.ViciousTooth(
                                    MinisterServant.this.level(), MinisterServant.this);
                            viciousTooth.setPos(Vec3.atCenterOf(blockpos$mutable));
                            viciousTooth.setOwner(MinisterServant.this);
                            if (MinisterServant.this.level().addFreshEntity(viciousTooth)) {
                                viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                            }
                        }
                    }
                }
                if (this.teethAmount <= 0) {
                    this.surroundTeeth();
                }
            }
        }

        public void surroundTeeth() {
            if (MinisterServant.this.getTarget() != null) {
                BlockPos blockPos = MinisterServant.this.blockPosition();
                BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(blockPos.getX(),
                        blockPos.getY(),
                        blockPos.getZ());

                while (blockpos$mutable.getY() < blockPos.getY() + 8.0D
                        && !MinisterServant.this.level().getBlockState(blockpos$mutable).blocksMotion()) {
                    blockpos$mutable.move(Direction.UP);
                }

                float f = (float) Mth.atan2(MinisterServant.this.getTarget().getZ() - blockPos.getZ(),
                        MinisterServant.this.getTarget().getX() - blockPos.getX());
                for (int i = 0; i < 5; ++i) {
                    float f1 = f + (float) i * (float) Math.PI * 0.4F;
                    com.Polarice3.Goety.common.entities.projectiles.ViciousTooth viciousTooth = new com.Polarice3.Goety.common.entities.projectiles.ViciousTooth(
                            MinisterServant.this.level(), MinisterServant.this);
                    viciousTooth.setPos(blockPos.getX() + (double) Mth.cos(f1) * 1.5D, blockpos$mutable.getY(),
                            blockPos.getZ() + (double) Mth.cos(f1) * 1.5D);
                    viciousTooth.setOwner(MinisterServant.this);
                    if (MinisterServant.this.level().addFreshEntity(viciousTooth)) {
                        viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                    }
                }
                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                    com.Polarice3.Goety.common.entities.projectiles.ViciousTooth viciousTooth = new com.Polarice3.Goety.common.entities.projectiles.ViciousTooth(
                            MinisterServant.this.level(), MinisterServant.this);
                    viciousTooth.setPos(blockPos.getX() + (double) Mth.cos(f2) * 2.5D, blockpos$mutable.getY(),
                            blockPos.getZ() + (double) Mth.sin(f2) * 2.5D);
                    viciousTooth.setOwner(MinisterServant.this);
                    if (MinisterServant.this.level().addFreshEntity(viciousTooth)) {
                        viciousTooth.playSound(ModSounds.TOOTH_SPAWN.get());
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MINISTER_CAST.get();
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }

    class SpeechGoal extends CastingGoal {
        private SpeechGoal() {
            this.hasCastSound = false;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && MinisterServant.this.hasNearbyIllagers();
        }

        @Override
        public void start() {
            super.start();
            MinisterServant.this.speechAnimationState.start(MinisterServant.this.tickCount);
            MinisterServant.this.level().broadcastEntityEvent(MinisterServant.this, (byte) 11);
        }

        protected int getCastingTime() {
            return com.Polarice3.Goety.utils.MathHelper.secondsToTicks(3);
        }

        protected int getCastingInterval() {
            return com.Polarice3.Goety.utils.MathHelper.secondsToTicks(10);
        }

        @Override
        public void tick() {
            super.tick();
            for (AbstractIllagerServant abstractIllager : MinisterServant.this.getNearbyIllagers()) {
                if (abstractIllager.isAlive() && abstractIllager.getMaxHealth() < MinisterServant.this.getMaxHealth()
                        && abstractIllager.getTarget() != MinisterServant.this
                        && (abstractIllager.getLastHurtByMob() == null
                                || !abstractIllager.isAlliedTo(abstractIllager.getLastHurtByMob()))) {
                    abstractIllager.setTarget(null);
                    abstractIllager.setAggressive(false);
                    abstractIllager.getNavigation().stop();
                    MobUtil.instaLook(abstractIllager, MinisterServant.this);
                    abstractIllager.getLookControl().setLookAt(MinisterServant.this, 500.0F,
                            abstractIllager.getMaxHeadXRot());
                }
            }
        }

        protected void performSpellCasting() {
            if (MinisterServant.this.hasNearbyIllagers()) {
                for (AbstractIllagerServant abstractIllager : MinisterServant.this.getNearbyIllagers()) {
                    if (abstractIllager.isAlive() && abstractIllager.getTarget() != MinisterServant.this
                            && abstractIllager.getMaxHealth() < MinisterServant.this.getMaxHealth()) {
                        abstractIllager
                                .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                                        com.Polarice3.Goety.utils.MathHelper.secondsToTicks(30)));
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MINISTER_SPEECH.get();
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.BLINDNESS;
        }
    }

    class CommandGoal extends CastingGoal {
        private CommandGoal() {
            this.hasCastSound = false;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && MinisterServant.this.hasNearbyIllagers()
                    && MinisterServant.this.getLastHurtByMob() != null
                    && MinisterServant.this.getTarget() == MinisterServant.this.getLastHurtByMob();
        }

        @Override
        public void start() {
            super.start();
            MinisterServant.this.commandAnimationState.start(MinisterServant.this.tickCount);
            MinisterServant.this.level().broadcastEntityEvent(MinisterServant.this, (byte) 12);
        }

        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 100;
        }

        protected void performSpellCasting() {
            if (MinisterServant.this.hasNearbyIllagers()) {
                for (AbstractIllagerServant abstractIllager : MinisterServant.this.getNearbyIllagers()) {
                    if (abstractIllager.getMaxHealth() < MinisterServant.this.getMaxHealth()
                            && MinisterServant.this.getTarget() != null
                            && abstractIllager.getTarget() != MinisterServant.this.getTarget()) {
                        abstractIllager.setTarget(MinisterServant.this.getTarget());
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MINISTER_COMMAND.get();
        }

        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.SUMMON_VEX;
        }
    }

    class LaughTargetGoal extends CastingGoal {
        private LaughTargetGoal() {
            this.hasCastSound = false;
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = MinisterServant.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                return !livingentity.hasEffect(MobEffects.WEAKNESS)
                        && livingentity.canBeAffected(new MobEffectInstance(MobEffects.WEAKNESS))
                        && livingentity.distanceTo(MinisterServant.this) <= 16.0F
                        && super.canUse();
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            MinisterServant.this.level().broadcastEntityEvent(MinisterServant.this, (byte) 7);
        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        protected int getCastingInterval() {
            return 300;
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity livingentity = MinisterServant.this.getTarget();
            if (livingentity != null) {
                livingentity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0, false, false),
                        MinisterServant.this);
            }
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.MINISTER_LAUGH.get();
        }

        @Override
        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.WOLOLO;
        }
    }

    public class MinisterRangedGoalWithoutIllagers extends MinisterRangedGoal {
        public MinisterRangedGoalWithoutIllagers(MinisterServant mob, double speed, int attackMin, int attackMax,
                float attackRadius) {
            super(mob, speed, attackMin, attackMax, attackRadius);
        }

        public MinisterRangedGoalWithoutIllagers(MinisterServant mob, double speed, int attackInterval,
                float attackRadius) {
            super(mob, speed, attackInterval, attackRadius);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !MinisterServant.this.hasNearbyIllagers();
        }
    }

    public class MinisterRangedGoalWithIllagers extends MinisterRangedGoal {
        public MinisterRangedGoalWithIllagers(MinisterServant mob, double speed, int attackMin, int attackMax,
                float attackRadius) {
            super(mob, speed, attackMin, attackMax, attackRadius);
        }

        public MinisterRangedGoalWithIllagers(MinisterServant mob, double speed, int attackInterval,
                float attackRadius) {
            super(mob, speed, attackInterval, attackRadius);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && MinisterServant.this.hasNearbyIllagers();
        }
    }

    public class MinisterRangedGoal extends Goal {
        private final MinisterServant mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public MinisterRangedGoal(MinisterServant p_25768_, double speed, int attackInterval, float attackRadius) {
            this(p_25768_, speed, attackInterval, attackInterval, attackRadius);
        }

        public MinisterRangedGoal(MinisterServant mob, double speed, int attackMin, int attackMax, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackIntervalMin = attackMin;
            this.attackIntervalMax = attackMax;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse()
                    || (this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone());
        }

        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null) {
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }

                if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
                    this.mob.getNavigation().stop();
                } else {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                }

                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                --this.attackTime;
                if (this.attackTime == 5) {
                    this.mob.attackAnimationState.start(MinisterServant.this.tickCount);
                    this.mob.level().broadcastEntityEvent(MinisterServant.this, (byte) 4);
                } else if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                    this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin)
                            + (float) this.attackIntervalMin);
                } else if (this.attackTime < 0) {
                    this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius,
                            (double) this.attackIntervalMin, (double) this.attackIntervalMax));
                }
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_,
            MobSpawnType p_37858_, @org.jetbrains.annotations.Nullable SpawnGroupData p_37859_,
            @org.jetbrains.annotations.Nullable CompoundTag p_37860_) {
        this.setOutfitType(this.random.nextInt(this.OutfitTypeNumber()));
        return super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
    }

    @Override
    public boolean hurt(DamageSource p_37849_, float p_37850_) {
        if (!this.level().isClientSide) {
            if (this.hasStaff() && this.isAggressive() && !this.isCastingSpell() && this.coolDown <= 10) {
                if (this.staffDamage >= 64) {
                    this.setHasStaff(false);
                    this.level().broadcastEntityEvent(this, (byte) 13);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 20; ++i) {
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel,
                                    new ItemParticleOption(ParticleTypes.ITEM,
                                            new ItemStack(com.Polarice3.Goety.common.items.ModItems.DARK_FABRIC.get())),
                                    this);
                        }
                    }
                    if (p_37849_.getEntity() != null) {
                        MobUtil.knockBack(this, p_37849_.getEntity(), 4.0D, 0.2D, 4.0D);
                    }
                    this.playSound(SoundEvents.ITEM_BREAK, 4.0F, 1.0F);
                    return false;
                } else if (!p_37849_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                        && !p_37849_.is(DamageTypeTags.BYPASSES_EFFECTS)
                        && !p_37849_.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                        && !p_37849_.is(DamageTypeTags.IS_EXPLOSION)
                        && !p_37849_.isCreativePlayer()
                        && p_37849_.getEntity() != null) {
                    Vec3 vec32 = p_37849_.getSourcePosition();
                    if (vec32 != null) {
                        MobUtil.instaLook(MinisterServant.this, vec32);
                        if (ModDamageSource.toolAttack(p_37849_, item -> item instanceof AxeItem)) {
                            p_37850_ *= 2.0F;
                        }
                        this.staffDamage += p_37850_;
                        this.level().broadcastEntityEvent(this, (byte) 8);
                        this.playSound(SoundEvents.SHIELD_BLOCK);
                        if (this.level() instanceof ServerLevel serverLevel) {
                            for (int i = 0; i < 5; ++i) {
                                ServerParticleUtil.addParticlesAroundSelf(serverLevel,
                                        new ItemParticleOption(ParticleTypes.ITEM,
                                                new ItemStack(
                                                        com.Polarice3.Goety.common.items.ModItems.DARK_FABRIC.get())),
                                        this);
                            }
                        }
                        if (this.level().random.nextFloat() <= 0.05F) {
                            this.playSound(ModSounds.MINISTER_LAUGH.get());
                        }
                        return false;
                    }
                }
            }
        }
        return super.hurt(p_37849_, p_37850_);
    }

    @Override
    public boolean canBeLeader() {
        return true;
    }

    public void warnKill(Player player) {
        this.killChance = 60;
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("info.goety.servant.tryKill", this.getDisplayName()),
                true);
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
    public AbstractIllagerServant.IllagerServantArmPose getArmPose() {
        if (this.isAggressive() || this.isCelebrating() || this.isDeadOrDying() || this.isCastingSpell()) {
            return AbstractIllagerServant.IllagerServantArmPose.NEUTRAL;
        } else {
            return AbstractIllagerServant.IllagerServantArmPose.CROSSED;
        }
    }
}