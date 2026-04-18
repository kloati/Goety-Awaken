package com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer;

import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.WraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.BorderWraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.MuckWraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.ReaperServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.client.particles.MagicSmokeParticleOption;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModTags;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.init.ModSounds;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeMod;
import java.util.function.Predicate;
import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import com.k1sak1.goetyawaken.common.entities.projectiles.GhostFireBolt;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;

public abstract class AbstractWraithNecromancer extends AbstractNecromancer {
    public int teleportCooldown = 0;

    private int floatSoundCooldown = 0;

    static class WraithNecromancerMoveControl extends net.minecraft.world.entity.ai.control.MoveControl {
        private float speed = 0.1F;

        public WraithNecromancerMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (this.mob instanceof AbstractWraithNecromancer wraithNecromancer) {
                if (wraithNecromancer.isSpellCasting() || wraithNecromancer.isShooting()) {
                    return;
                }

                if (this.mob.horizontalCollision) {
                    this.mob.setYRot(this.mob.getYRot() + 180.0F);
                    this.speed = 0.1F;
                }
                LivingEntity target = wraithNecromancer.getTarget();
                if (target != null && target.isAlive()) {
                    double distanceToTarget = this.mob.distanceToSqr(target);
                    if (distanceToTarget > 144.0D) {
                        Vec3 targetPos = target.position();
                        double d0 = targetPos.x - this.mob.getX();
                        double d1 = targetPos.y - this.mob.getY();
                        double d2 = targetPos.z - this.mob.getZ();
                        double distance = Math.sqrt(d0 * d0 + d2 * d2);
                        if (Math.abs(distance) > 1.0E-5F) {
                            double verticalFactor = 1.0D - Math.abs(d1 * 0.7F) / distance;
                            d0 *= verticalFactor;
                            d2 *= verticalFactor;
                            distance = Math.sqrt(d0 * d0 + d2 * d2);
                            double totalDistance = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                            float f1 = (float) Mth.atan2(d2, d0);
                            float f2 = Mth.wrapDegrees(this.mob.getYRot() + 90.0F);
                            float f3 = Mth.wrapDegrees(f1 * (180F / (float) Math.PI));
                            this.mob.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                            this.mob.yBodyRot = this.mob.getYRot();
                            this.speed = Mth.approach(this.speed, 0.6F, 0.005F * (0.6F / this.speed));
                            float pitch = (float) (-(Mth.atan2(-d1, distance) * (180F / (float) Math.PI)));
                            this.mob.setXRot(pitch);
                            float yawOffset = this.mob.getYRot() + 90.0F;
                            double moveX = (double) (this.speed * Mth.cos(yawOffset * ((float) Math.PI / 180F)))
                                    * Math.abs(d0 / totalDistance);
                            double moveY = (double) (this.speed * Mth.sin(pitch * ((float) Math.PI / 180F)))
                                    * Math.abs(d1 / totalDistance);
                            double moveZ = (double) (this.speed * Mth.sin(yawOffset * ((float) Math.PI / 180F)))
                                    * Math.abs(d2 / totalDistance);
                            Vec3 currentMovement = this.mob.getDeltaMovement();
                            this.mob.setDeltaMovement(currentMovement
                                    .add((new Vec3(moveX, moveY, moveZ)).subtract(currentMovement).scale(0.2D)));
                            return;
                        }
                    }
                } else if (wraithNecromancer.getTrueOwner() != null && wraithNecromancer.isFollowing()) {
                    LivingEntity owner = wraithNecromancer.getTrueOwner();
                    double distanceToOwner = this.mob.distanceToSqr(owner);
                    if (distanceToOwner > 144.0D) {
                        Vec3 targetPos = owner.position().add(0, 2, 0);
                        double d0 = targetPos.x - this.mob.getX();
                        double d1 = targetPos.y - this.mob.getY();
                        double d2 = targetPos.z - this.mob.getZ();
                        double distance = Math.sqrt(d0 * d0 + d2 * d2);
                        if (Math.abs(distance) > 1.0E-5F) {
                            double verticalFactor = 1.0D - Math.abs(d1 * 0.7F) / distance;
                            d0 *= verticalFactor;
                            d2 *= verticalFactor;
                            distance = Math.sqrt(d0 * d0 + d2 * d2);
                            double totalDistance = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                            float f1 = (float) Mth.atan2(d2, d0);
                            float f2 = Mth.wrapDegrees(this.mob.getYRot() + 90.0F);
                            float f3 = Mth.wrapDegrees(f1 * (180F / (float) Math.PI));
                            this.mob.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                            this.mob.yBodyRot = this.mob.getYRot();
                            this.speed = Mth.approach(this.speed, 0.6F, 0.005F * (0.6F / this.speed));
                            float pitch = (float) (-(Mth.atan2(-d1, distance) * (180F / (float) Math.PI)));
                            this.mob.setXRot(pitch);
                            float yawOffset = this.mob.getYRot() + 90.0F;
                            double moveX = (double) (this.speed * Mth.cos(yawOffset * ((float) Math.PI / 180F)))
                                    * Math.abs(d0 / totalDistance);
                            double moveY = (double) (this.speed * Mth.sin(pitch * ((float) Math.PI / 180F)))
                                    * Math.abs(d1 / totalDistance);
                            double moveZ = (double) (this.speed * Mth.sin(yawOffset * ((float) Math.PI / 180F)))
                                    * Math.abs(d2 / totalDistance);
                            Vec3 currentMovement = this.mob.getDeltaMovement();
                            this.mob.setDeltaMovement(currentMovement
                                    .add((new Vec3(moveX, moveY, moveZ)).subtract(currentMovement).scale(0.2D)));
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.teleportCooldown > 0) {
            --this.teleportCooldown;
        }
        if (this.getTarget() != null) {
            this.getLookControl().setLookAt(this.getTarget(), 30.0F, 30.0F);
        }
        if (!this.level().isClientSide && this.isAlive()) {
            if (this.floatSoundCooldown > 0) {
                this.floatSoundCooldown--;
            } else {
                if (this.isNoGravity() && this.onGround() == false && this.getTarget() == null
                        && this.getRandom().nextInt(100) < 3) {
                    this.playFloatSound();
                    this.floatSoundCooldown = 100 + this.getRandom().nextInt(100);
                }
            }
        }
    }

    private void playFloatSound() {
        switch (this.getRandom().nextInt(5)) {
            case 0:
                this.playSound(ModSounds.WRAITH_NECROMANCER_FLOAT_1.get(), 1.5F, 1.0F);
                break;
            case 1:
                this.playSound(ModSounds.WRAITH_NECROMANCER_FLOAT_2.get(), 1.5F, 1.0F);
                break;
            case 2:
                this.playSound(ModSounds.WRAITH_NECROMANCER_FLOAT_3.get(), 1.5F, 1.0F);
                break;
            case 3:
                this.playSound(ModSounds.WRAITH_NECROMANCER_FLOAT_4.get(), 1.5F, 1.0F);
                break;
            case 4:
                this.playSound(ModSounds.WRAITH_NECROMANCER_FLOAT_5.get(), 1.5F, 1.0F);
                break;
            default:
                this.playSound(ModSounds.WRAITH_NECROMANCER_FLOAT_1.get(), 1.5F, 1.0F);
                break;
        }
    }

    private static final EntityDataAccessor<Byte> WRAITH_FLAGS = SynchedEntityData.defineId(
            AbstractWraithNecromancer.class,
            EntityDataSerializers.BYTE);

    protected static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(
            AbstractWraithNecromancer.class,
            EntityDataSerializers.INT);
    public static final int IDLE_ANIM = 0;
    public static final int FLY_ANIM = 1;
    public static final int ATTACK_ANIM = 2;
    public static final int SUMMON_ANIM = 3;
    public static final int SPELL_ANIM = 4;
    public static final int ALERT_ANIM = 5;
    public static final int SHOCKWAVE_ANIM = 6;
    protected int currentSkillType = 0;

    public int getAnimationState() {
        return this.entityData.get(ANIM_STATE);
    }

    protected AbstractWraithNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.teleportCooldown = 0;
        this.moveControl = new WraithNecromancerMoveControl(this);
        this.setNoGravity(true);
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.9F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WRAITH_FLAGS, (byte) 0);
        this.entityData.define(ANIM_STATE, 0);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WraithNecromancerHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.3D)
                .add(Attributes.ARMOR, AttributesConfig.WraithNecromancerArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.WraithNecromancerFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WraithNecromancerDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0F);
    }

    public void projectileGoal(int priority) {
        this.goalSelector.addGoal(priority, new WraithNecromancerRangedGoal(this, 1.0D, 20, 12.0F));
    }

    public void avoidGoal(int priority) {
    }

    public void summonSpells(int priority) {
        this.goalSelector.addGoal(priority + 3, new WraithSummoningSpell());
        this.goalSelector.addGoal(priority + 2, new WraithSummonUndeadGoal());
        this.goalSelector.addGoal(priority + 1, new SpecialSpellGoal());
        this.goalSelector.addGoal(priority, new WraithShockwaveGoal());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(7, new WraithNecromancerRandomMoveGoal(this));
    }

    static class WraithNecromancerRandomMoveGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final AbstractWraithNecromancer wraithNecromancer;

        public WraithNecromancerRandomMoveGoal(AbstractWraithNecromancer pWraithNecromancer) {
            this.wraithNecromancer = pWraithNecromancer;
            this.setFlags(EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !this.wraithNecromancer.getMoveControl().hasWanted()
                    && this.wraithNecromancer.random.nextInt(reducedTickDelay(40)) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = this.wraithNecromancer.blockPosition();

            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(this.wraithNecromancer.random.nextInt(15) - 7,
                        this.wraithNecromancer.random.nextInt(11) - 5, this.wraithNecromancer.random.nextInt(15) - 7);
                if (this.wraithNecromancer.level().isEmptyBlock(blockpos1)) {
                    this.wraithNecromancer.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D,
                            (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.5D);
                    if (this.wraithNecromancer.getTarget() == null) {
                        this.wraithNecromancer.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D,
                                (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof AbstractWraithNecromancer;
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.WraithNecromancerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.WraithNecromancerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.WraithNecromancerFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.WraithNecromancerDamage.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.wraithNecromancerLimit;
    }

    @Override
    public int xpReward() {
        return 80;
    }

    public void setNecroLevel(int shot) {
        int i = Mth.clamp(shot, 0, 2);
        this.entityData.set(LEVEL, i);
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(AttributesConfig.WraithNecromancerHealth.get() * Math.max(i * 1.25F, 1));
        }
        this.reapplyPosition();
        this.refreshDimensions();
    }

    public Summoned getDefaultSummon() {
        float rand = this.level().random.nextFloat();
        if (rand < 0.25F) {
            return new ReaperServant(
                    com.Polarice3.Goety.common.entities.ModEntityType.REAPER_SERVANT.get(), this.level());
        } else if (rand < 0.5F) {
            return new BorderWraithServant(
                    com.Polarice3.Goety.common.entities.ModEntityType.BORDER_WRAITH_SERVANT.get(), this.level());
        } else if (rand < 0.75F) {
            return new MuckWraithServant(
                    com.Polarice3.Goety.common.entities.ModEntityType.MUCK_WRAITH_SERVANT.get(), this.level());
        } else {
            return new WraithServant(
                    com.Polarice3.Goety.common.entities.ModEntityType.WRAITH_SERVANT.get(), this.level());
        }
    }

    public Summoned getSummon() {
        Summoned summoned = getDefaultSummon();
        if (this.getSummonList().stream()
                .anyMatch(entityType -> entityType.is(com.Polarice3.Goety.init.ModTags.EntityTypes.ZOMBIE_SERVANTS))) {
            if (this.level().random.nextBoolean()) {
                summoned = new ZombieServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.ZOMBIE_SERVANT.get(), this.level());
            }
        }

        if (this.getSummonList().stream()
                .anyMatch(
                        entityType -> entityType.is(com.Polarice3.Goety.init.ModTags.EntityTypes.SKELETON_SERVANTS))) {
            if (this.level().random.nextBoolean()) {
                summoned = new SkeletonServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.SKELETON_SERVANT.get(), this.level());
            }
        }

        if (this.getSummonList().contains(com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.05F) {
                summoned = new BlackguardServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.BLACKGUARD_SERVANT.get(), this.level());
            }
        }

        if (this.getSummonList().contains(com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.15F) {
                summoned = new VanguardServant(
                        com.Polarice3.Goety.common.entities.ModEntityType.VANGUARD_SERVANT.get(), this.level());
            }
        }
        return summoned;
    }

    public boolean doHurtTarget(Entity p_34169_) {
        if (!super.doHurtTarget(p_34169_)) {
            return false;
        } else {
            if (p_34169_ instanceof LivingEntity) {
                ((LivingEntity) p_34169_).addEffect(
                        new MobEffectInstance(com.Polarice3.Goety.common.effects.GoetyEffects.FREEZING.get(), 200),
                        this);
            }
            return true;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        Vec3 targetVec = new Vec3(target.getX() - this.getX(),
                target.getEyeY() - this.getEyeY(),
                target.getZ() - this.getZ());
        targetVec = targetVec.normalize();
        for (int i = -1; i <= 1; i++) {
            float yawOffset = i * 15.0F;
            double yawRad = Math.toRadians(yawOffset);
            double rotatedX = targetVec.x * Math.cos(yawRad) - targetVec.z * Math.sin(yawRad);
            double rotatedZ = targetVec.x * Math.sin(yawRad) + targetVec.z * Math.cos(yawRad);
            Vec3 rotatedVec = new Vec3(rotatedX, targetVec.y, rotatedZ);
            GhostFireBolt ghostFireBolt = new GhostFireBolt(this, rotatedVec.x, rotatedVec.y, rotatedVec.z,
                    this.level());
            ghostFireBolt.setOwner(this);
            ghostFireBolt.setPos(this.getX() + rotatedVec.x, this.getEyeY() + rotatedVec.y, this.getZ() + rotatedVec.z);
            ghostFireBolt.rotateToMatchMovement();
            if (this.level().addFreshEntity(ghostFireBolt)) {
                if (i == -1) {
                    RandomSource random = this.getRandom();
                    switch (random.nextInt(3)) {
                        case 0:
                            this.playSound(ModSounds.WRAITH_NECROMANCER_ATTACK_1.get(), 1.8F, 1.0F);
                            break;
                        case 1:
                            this.playSound(ModSounds.WRAITH_NECROMANCER_ATTACK_2.get(), 1.8F, 1.0F);
                            break;
                        case 2:
                            this.playSound(ModSounds.WRAITH_NECROMANCER_ATTACK_3.get(), 1.8F, 1.0F);
                            break;
                        default:
                            this.playSound(ModSounds.WRAITH_NECROMANCER_ATTACK_1.get(), 1.8F, 1.0F);
                            break;
                    }
                    this.playSound(com.Polarice3.Goety.init.ModSounds.HELL_BOLT_SHOOT.get());
                    this.swing(InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    public class WraithSummoningSpell extends AbstractNecromancer.SummoningSpellGoal {
        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive()
                    && entity instanceof IOwned owned
                    && owned.getTrueOwner() == AbstractWraithNecromancer.this;
            int i = AbstractWraithNecromancer.this.level()
                    .getEntitiesOfClass(LivingEntity.class,
                            AbstractWraithNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D), predicate)
                    .size();
            return super.canUse() && i < 6;
        }

        public void start() {
            this.spellTime = 20;
            AbstractWraithNecromancer.this.setSpellCooldown(this.getCastingInterval());
            this.playPrepareSound();

            AbstractWraithNecromancer.this.playSound(ModSounds.WRAITH_NECROMANCER_SUMMON_START.get(), 1.5F, 1.0F);
            AbstractWraithNecromancer.this.setAnimationState(AbstractWraithNecromancer.SUMMON_ANIM);
            AbstractWraithNecromancer.this.setSpellCasting(true);
            AbstractWraithNecromancer.this.setNecromancerSpellType(this.getNecromancerSpellType());
        }

        @Override
        public void tick() {
            --this.spellTime;
            if (this.spellTime == 10) {
                if (this.getCastSound() != null) {
                    AbstractWraithNecromancer.this.playSound(this.getCastSound(), 1.0F, 1.0F);
                }
                AbstractWraithNecromancer.this.playSound(com.Polarice3.Goety.init.ModSounds.NECROMANCER_LAUGH.get(),
                        2.0F, 0.05F);
                this.castSpell();
                AbstractWraithNecromancer.this.setNecromancerSpellType(
                        com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            }
        }

        @Override
        public void stop() {
            super.stop();
            AbstractWraithNecromancer.this.setSpellCasting(false);
            if (!AbstractWraithNecromancer.this.isShooting() && !AbstractWraithNecromancer.this.isSpellCasting()) {
                AbstractWraithNecromancer.this.setAnimationState(AbstractWraithNecromancer.IDLE_ANIM);
            }
            AbstractWraithNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
        }

        @Override
        protected void castSpell() {
            if (AbstractWraithNecromancer.this.level() instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 2; ++i1) {
                    Summoned summoned = AbstractWraithNecromancer.this.getSummon();
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractWraithNecromancer.this.blockPosition(),
                            summoned, serverLevel);
                    summoned.setTrueOwner(AbstractWraithNecromancer.this);
                    summoned.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summoned);
                    if (!AbstractWraithNecromancer.this.getType().is(ModTags.EntityTypes.MINI_BOSSES)) {
                        if (MobsConfig.NecromancerSummonsLife.get()) {
                            summoned.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                        }
                    }
                    summoned.setPersistenceRequired();
                    summoned.finalizeSpawn(serverLevel,
                            serverLevel.getCurrentDifficultyAt(AbstractWraithNecromancer.this.blockPosition()),
                            MobSpawnType.MOB_SUMMONED, null, null);
                    if (serverLevel.addFreshEntity(summoned)) {
                        AbstractWraithNecromancer.this.playSound(ModSounds.WRAITH_NECROMANCER_SUMMON_FINISH.get(), 1.5F,
                                1.0F);
                        SoundUtil.playNecromancerSummon(summoned);
                        ColorUtil colorUtil = new ColorUtil(0x2ac9cf);
                        ServerParticleUtil.windShockwaveParticle(serverLevel, colorUtil, 0.1F, 0.1F, 0.05F, -1,
                                summoned.position());
                        for (int i2 = 0; i2 < serverLevel.getRandom().nextInt(10) + 10; ++i2) {
                            serverLevel.sendParticles(
                                    new MagicSmokeParticleOption(0x17b0e0, 0xffffff,
                                            10 + serverLevel.getRandom().nextInt(10), 0.2F),
                                    summoned.getRandomX(1.5D), summoned.getRandomY(),
                                    summoned.getRandomZ(1.5D), 0, 0.0F, 0.0F, 0.0F, 1.0F);
                        }
                    }
                }
            }
        }

        @Override
        protected int getCastingInterval() {
            return 200;
        }

        @Override
        protected com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType getNecromancerSpellType() {
            return com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.ZOMBIE;
        }
    }

    public class WraithSummonUndeadGoal extends AbstractNecromancer.SummonUndeadGoal {
        @Override
        public void playLaughSound() {
            AbstractWraithNecromancer.this.playSound(com.Polarice3.Goety.init.ModSounds.NECROMANCER_LAUGH.get(), 2.0F,
                    0.05F);
        }

        public void start() {
            super.start();
            AbstractWraithNecromancer.this.setAnimationState(AbstractWraithNecromancer.SUMMON_ANIM);
            AbstractWraithNecromancer.this.setSpellCasting(true);
            AbstractWraithNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.ZOMBIE);
        }

        @Override
        public void stop() {
            super.stop();
            AbstractWraithNecromancer.this.setSpellCasting(false);
            if (!AbstractWraithNecromancer.this.isShooting() && !AbstractWraithNecromancer.this.isSpellCasting()) {
                AbstractWraithNecromancer.this.setAnimationState(AbstractWraithNecromancer.IDLE_ANIM);
            }
            AbstractWraithNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractWraithNecromancer.this.idleSpellCool = com.Polarice3.Goety.utils.MathHelper.secondsToTicks(3);
        }
    }

    public class SpecialSpellGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractWraithNecromancer.this.getTarget();
            if (AbstractWraithNecromancer.this.isSpellCasting()) {
                return false;
            } else {
                return target != null
                        && target.isAlive()
                        && AbstractWraithNecromancer.this.random.nextBoolean()
                        && AbstractWraithNecromancer.this.idleSpellCool <= 0;
            }
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        public void start() {
            this.spellTime = 60;
            AbstractWraithNecromancer.this.setSpellCooldown(AbstractWraithNecromancer.this.getSpellCooldown() + 60);
            AbstractWraithNecromancer.this.playSound(com.Polarice3.Goety.init.ModSounds.RUMBLE.get(), 1.8F, 1.0F);
            AbstractWraithNecromancer.this.setSpellCasting(true);
            AbstractWraithNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.CLOUD);
            AbstractWraithNecromancer.this.setAnimationState(AbstractWraithNecromancer.SPELL_ANIM);
        }

        @Override
        public void stop() {
            super.stop();
            AbstractWraithNecromancer.this.setSpellCasting(false);
            if (!AbstractWraithNecromancer.this.isShooting() && !AbstractWraithNecromancer.this.isSpellCasting()) {
                AbstractWraithNecromancer.this.setAnimationState(AbstractWraithNecromancer.IDLE_ANIM);
            }
            AbstractWraithNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractWraithNecromancer.this.idleSpellCool = com.Polarice3.Goety.utils.MathHelper.secondsToTicks(10);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 5) {
                if (AbstractWraithNecromancer.this
                        .level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    com.Polarice3.Goety.common.magic.spells.WeakeningSpell weakeningSpell = new com.Polarice3.Goety.common.magic.spells.WeakeningSpell();
                    com.Polarice3.Goety.common.magic.SpellStat spellStat = new com.Polarice3.Goety.common.magic.SpellStat(
                            0, 0, 0, 0.0D, 0, 0.0F);
                    spellStat.setRadius(5.0D).setPotency(3).setDuration(5);
                    weakeningSpell.SpellResult(serverLevel, AbstractWraithNecromancer.this,
                            net.minecraft.world.item.ItemStack.EMPTY, spellStat);
                    com.Polarice3.Goety.common.magic.spells.SoulHealSpell soulHealSpell = new com.Polarice3.Goety.common.magic.spells.SoulHealSpell();
                    com.Polarice3.Goety.common.magic.SpellStat healStat = new com.Polarice3.Goety.common.magic.SpellStat(
                            0, 0, 0, 0.0D, 0, 0.0F);
                    healStat.setRadius(3.0D).setPotency(3);
                    soulHealSpell.SpellResult(serverLevel, AbstractWraithNecromancer.this,
                            net.minecraft.world.item.ItemStack.EMPTY, healStat);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public class WraithShockwaveGoal extends Goal {
        protected int spellTime;

        @Override
        public boolean canUse() {
            LivingEntity target = AbstractWraithNecromancer.this.getTarget();
            if (AbstractWraithNecromancer.this.isSpellCasting()) {
                return false;
            } else {
                return target != null
                        && target.isAlive()
                        && AbstractWraithNecromancer.this.random.nextBoolean()
                        && AbstractWraithNecromancer.this.idleSpellCool <= 0;
            }
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        public void start() {
            this.spellTime = 36;
            AbstractWraithNecromancer.this.setSpellCooldown(AbstractWraithNecromancer.this.getSpellCooldown() + 60);
            AbstractWraithNecromancer.this.setAnimationState(AbstractWraithNecromancer.SUMMON_ANIM);
            AbstractWraithNecromancer.this.setSpellCasting(true);
            AbstractWraithNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.ZOMBIE);
        }

        @Override
        public void stop() {
            super.stop();
            AbstractWraithNecromancer.this.setSpellCasting(false);
            if (!AbstractWraithNecromancer.this.isShooting() && !AbstractWraithNecromancer.this.isSpellCasting()) {
                AbstractWraithNecromancer.this.setAnimationState(AbstractWraithNecromancer.IDLE_ANIM);
            }
            AbstractWraithNecromancer.this.setNecromancerSpellType(
                    com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer.NecromancerSpellType.NONE);
            AbstractWraithNecromancer.this.idleSpellCool = com.Polarice3.Goety.utils.MathHelper.secondsToTicks(10);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 16) {
                if (AbstractWraithNecromancer.this.level() instanceof ServerLevel serverLevel) {
                    com.Polarice3.Goety.common.magic.spells.ShockwaveSpell shockwaveSpell = new com.Polarice3.Goety.common.magic.spells.ShockwaveSpell();
                    com.Polarice3.Goety.common.magic.SpellStat spellStat = new com.Polarice3.Goety.common.magic.SpellStat(
                            3, 3, 0, 4.0D, 3, 3.0F);
                    shockwaveSpell.SpellResult(serverLevel, AbstractWraithNecromancer.this,
                            net.minecraft.world.item.ItemStack.EMPTY, spellStat);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public class WraithNecromancerRangedGoal extends net.minecraft.world.entity.ai.goal.Goal {
        @javax.annotation.Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackInterval;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public WraithNecromancerRangedGoal(AbstractNecromancer mob, double speed, int attackInterval,
                float attackRadius) {
            this.speedModifier = speed;
            this.attackInterval = attackInterval;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(java.util.EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE,
                    net.minecraft.world.entity.ai.goal.Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = AbstractWraithNecromancer.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return !AbstractWraithNecromancer.this.isSpellCasting()
                        && AbstractWraithNecromancer.this.hasLineOfSight(livingentity);
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.target.isAlive()
                    && !AbstractWraithNecromancer.this.getNavigation().isDone()
                    && !AbstractWraithNecromancer.this.isSpellCasting());
        }

        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
            AbstractWraithNecromancer.this.setShooting(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null && !AbstractWraithNecromancer.this.isSpellCasting()) {
                double d0 = AbstractWraithNecromancer.this.distanceToSqr(this.target.getX(), this.target.getY(),
                        this.target.getZ());
                boolean flag = AbstractWraithNecromancer.this.getSensing().hasLineOfSight(this.target);
                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }
                AbstractWraithNecromancer.this.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                if (AbstractWraithNecromancer.this.isShooting()) {
                    AbstractWraithNecromancer.this.getNavigation().stop();
                } else {
                    if (d0 < 36.0D) {
                        AbstractWraithNecromancer.this.setShooting(false);
                    } else if (this.seeTime >= 5) {
                        AbstractWraithNecromancer.this.getNavigation().stop();
                    }
                }

                int speed = Mth.floor(Math.max(AbstractWraithNecromancer.this.getAttackSpeed(), 1.0F));
                AbstractWraithNecromancer.this.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                int attackIntervalMin = this.attackInterval / speed;
                --this.attackTime;

                if (this.attackTime <= 5) {
                    AbstractWraithNecromancer.this.setShooting(true);
                    if ((AbstractWraithNecromancer.this)
                            .getAnimationState() != ATTACK_ANIM) {
                        AbstractWraithNecromancer.this.setAnimationState(ATTACK_ANIM);
                    }
                }

                if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }
                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    AbstractWraithNecromancer.this.performRangedAttack(this.target, f1);
                    this.attackTime = attackIntervalMin;
                } else if (this.attackTime < 0) {
                    AbstractWraithNecromancer.this.setShooting(false);
                    this.attackTime = attackIntervalMin;
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (itemstack.getItem() == com.k1sak1.goetyawaken.common.items.ModItems.WRAITH_NECROMANCER_SOUL_JAR
                        .get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (this.getNecroLevel() < 2) {
                        this.setNecroLevel(this.getNecroLevel() + 1);
                    }
                    this.heal(AttributesConfig.WraithNecromancerHealth.get().floatValue());
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                                    this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                                    0, d0, d1, d2, 0.5F);
                        }
                    }
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (itemstack.is(com.Polarice3.Goety.common.items.ModItems.SOUL_JAR.get())) {
                    return InteractionResult.PASS;
                } else {
                    return super.mobInteract(pPlayer, pHand);
                }
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public boolean canPerformSkill() {
        return this.currentSkillType == 0 &&
                !this.isSpellCasting() &&
                !this.isShooting();
    }

    public void setCurrentSkillType(int skillType) {
        this.currentSkillType = skillType;
    }

    public void resetSkillType() {
        this.currentSkillType = 0;
    }

    public void setAnimationState(String animation) {
    }

    public void setAnimationState(int animation) {
        this.entityData.set(ANIM_STATE, animation);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        RandomSource random = this.getRandom();
        switch (random.nextInt(5)) {
            case 0:
                return ModSounds.WRAITH_NECROMANCER_AMBIENT_1.get();
            case 1:
                return ModSounds.WRAITH_NECROMANCER_AMBIENT_2.get();
            case 2:
                return ModSounds.WRAITH_NECROMANCER_AMBIENT_3.get();
            case 3:
                return ModSounds.WRAITH_NECROMANCER_AMBIENT_4.get();
            case 4:
                return ModSounds.WRAITH_NECROMANCER_AMBIENT_5.get();
            default:
                return ModSounds.WRAITH_NECROMANCER_AMBIENT_1.get();
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        RandomSource random = this.getRandom();
        switch (random.nextInt(3)) {
            case 0:
                return ModSounds.WRAITH_NECROMANCER_HURT_1.get();
            case 1:
                return ModSounds.WRAITH_NECROMANCER_HURT_2.get();
            case 2:
                return ModSounds.WRAITH_NECROMANCER_HURT_3.get();
            default:
                return ModSounds.WRAITH_NECROMANCER_HURT_1.get();
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        RandomSource random = this.getRandom();
        return random.nextBoolean() ? ModSounds.WRAITH_NECROMANCER_DEATH_1.get()
                : ModSounds.WRAITH_NECROMANCER_DEATH_2.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.9F;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.75F;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return super.isPushable() && this.getTarget() == null;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        float f1 = (float) this.getNecroLevel();
        float size = 1.0F + Math.max(f1 * 0.15F, 0);
        return 2.523F * size;
    }

    private boolean getWraithFlags(int mask) {
        int i = this.entityData.get(WRAITH_FLAGS);
        return (i & mask) != 0;
    }

    private void setWraithFlags(int mask, boolean value) {
        int i = this.entityData.get(WRAITH_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(WRAITH_FLAGS, (byte) (i & 255));
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        net.minecraft.world.entity.ai.navigation.FlyingPathNavigation flyingPathNavigation = new net.minecraft.world.entity.ai.navigation.FlyingPathNavigation(
                this, level) {
            public boolean isStableDestination(net.minecraft.core.BlockPos blockPos) {
                return !this.level.getBlockState(blockPos.below()).isAir();
            }

            public void tick() {
                super.tick();
            }
        };
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(true);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
    }
}