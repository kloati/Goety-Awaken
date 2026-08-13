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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.AnimationState;
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
import net.minecraft.world.phys.AABB;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;

public abstract class AbstractWraithNecromancer extends AbstractNecromancer {
    public int teleportCooldown = 0;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState flyAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState summonAnimationState = new AnimationState();
    public final AnimationState spellAnimationState = new AnimationState();
    public final AnimationState alertAnimationState = new AnimationState();
    public final AnimationState shockwaveAnimationState = new AnimationState();
    public int baseAnimTransitionTick = 0;
    public static final int BASE_ANIM_TRANSITION_DURATION = 5;
    public String transitionFromKey = "";
    public String transitionToKey = "";
    private String currentAnimKey = "";

    public String getCurrentAnimKey() {
        return this.currentAnimKey;
    }

    private int floatSoundCooldown = 0;
    protected int summonScanTimer = -999;
    protected int summonCount = 0;

    @Override
    public void tick() {
        super.tick();
        this.setGravity();
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
        if (this.level().isClientSide) {
            this.tickAnimationTransitions();
        }
    }

    private String computeDesiredAnimKey() {
        switch (this.getAnimationState()) {
            case IDLE_ANIM:
                return "idle";
            case FLY_ANIM:
                return "fly";
            case ATTACK_ANIM:
                return "attack";
            case SUMMON_ANIM:
                return "summon";
            case SPELL_ANIM:
                return "spell";
            case ALERT_ANIM:
                return "alert";
            case SHOCKWAVE_ANIM:
                return "shockwave";
            default:
                return "";
        }
    }

    private void startAnimationForKey(String key) {
        switch (key) {
            case "idle":
                this.idleAnimationState.startIfStopped(this.tickCount);
                break;
            case "fly":
                this.flyAnimationState.startIfStopped(this.tickCount);
                break;
            case "attack":
                this.attackAnimationState.startIfStopped(this.tickCount);
                break;
            case "summon":
                this.summonAnimationState.startIfStopped(this.tickCount);
                break;
            case "spell":
                this.spellAnimationState.startIfStopped(this.tickCount);
                break;
            case "alert":
                this.alertAnimationState.startIfStopped(this.tickCount);
                break;
            case "shockwave":
                this.shockwaveAnimationState.startIfStopped(this.tickCount);
                break;
        }
    }

    private void stopAnimationsNotForKey(String key) {
        if (!key.equals("idle"))
            this.idleAnimationState.stop();
        if (!key.equals("fly"))
            this.flyAnimationState.stop();
        if (!key.equals("attack"))
            this.attackAnimationState.stop();
        if (!key.equals("summon"))
            this.summonAnimationState.stop();
        if (!key.equals("spell"))
            this.spellAnimationState.stop();
        if (!key.equals("alert"))
            this.alertAnimationState.stop();
        if (!key.equals("shockwave"))
            this.shockwaveAnimationState.stop();
    }

    private void tickAnimationTransitions() {
        if (this.baseAnimTransitionTick > 0) {
            this.baseAnimTransitionTick--;
            String midDesired = this.computeDesiredAnimKey();
            if (!midDesired.isEmpty() && !midDesired.equals(this.transitionToKey)) {
                this.startAnimationForKey(midDesired);
                this.transitionFromKey = this.transitionToKey;
                this.transitionToKey = midDesired;
                this.baseAnimTransitionTick = BASE_ANIM_TRANSITION_DURATION;
                this.currentAnimKey = midDesired;
            } else if (this.baseAnimTransitionTick == 0) {
                this.stopAnimationsNotForKey(this.transitionToKey);
            }
        } else {
            String desiredKey = this.computeDesiredAnimKey();
            if (!desiredKey.isEmpty() && !desiredKey.equals(this.currentAnimKey)) {
                if (!this.currentAnimKey.isEmpty()) {
                    this.startAnimationForKey(desiredKey);
                    this.transitionFromKey = this.currentAnimKey;
                    this.transitionToKey = desiredKey;
                    this.baseAnimTransitionTick = BASE_ANIM_TRANSITION_DURATION;
                } else {
                    this.startAnimationForKey(desiredKey);
                    this.stopAnimationsNotForKey(desiredKey);
                }
                this.currentAnimKey = desiredKey;
            } else {
                this.startAnimationForKey(desiredKey);
                this.stopAnimationsNotForKey(desiredKey);
                this.currentAnimKey = desiredKey;
            }
        }
    }

    public void setGravity() {
        this.setNoGravity(this.isUnderWater());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround() && vector3d.y < 0.0D && !this.isNoGravity()) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    private void playFloatSound() {
        this.playSound(ModSounds.WRAITH_NECROMANCER_FLOAT.get(), 1.5F, 1.0F);
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
        this.setPersistenceRequired();
        this.moveControl = new MobUtil.WraithMoveController(this);
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

    @Override
    public void commandMode() {
        if (!this.isCommanded()) {
            return;
        }
        BlockPos targetPos = this.getCommandPos();
        Entity targetEntity = this.getCommandPosEntity();
        this.setCommandTick(this.getCommandTick() - 1);

        if (targetEntity != null) {
            this.getMoveControl().setWantedPosition(targetEntity.getX(), targetEntity.getEyeY(), targetEntity.getZ(),
                    this.getCommandSpeed());
        } else if (targetPos != null) {
            this.getMoveControl().setWantedPosition(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D,
                    this.getCommandSpeed());
        }

        AABB aabb = targetPos != null ? new AABB(targetPos) : null;
        Entity entity = this.getControlledVehicle() != null ? this.getControlledVehicle() : this;
        if (this.getCommandTick() <= 0) {
            this.setCommandPosEntity(null);
            this.setCommandPos(null);
        } else if (aabb != null && entity.getBoundingBox().inflate(0.5F).intersects(aabb)) {
            if (this.getCommandPosEntity() != null &&
                    this.getBoundingBox().inflate(1.25D).intersects(this.getCommandPosEntity().getBoundingBox())) {
                if (this.isAbleToRide(this.getCommandPosEntity())) {
                    if (this.startRiding(this.getCommandPosEntity())) {
                        if (this.getTrueOwner() instanceof Player player) {
                            player.displayClientMessage(
                                    net.minecraft.network.chat.Component.translatable("info.goety.servant.dismount"),
                                    true);
                        }
                    }
                }
                this.setCommandPosEntity(null);
            }
            if (this.isGuardingArea()) {
                this.setBoundPos(targetPos);
            }
            this.getNavigation().stop();
            this.getMoveControl().strafe(0.0F, 0.0F);
            this.moveTo(targetPos, this.getYRot(), this.getXRot());
            this.setCommandPos(null);
        }
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
                    this.playSound(ModSounds.WRAITH_NECROMANCER_ATTACK.get(), 1.8F, 1.0F);
                    this.playSound(com.Polarice3.Goety.init.ModSounds.HELL_BOLT_SHOOT.get());
                    this.swing(InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    public class WraithSummoningSpell extends AbstractNecromancer.SummoningSpellGoal {
        public boolean canUse() {
            int currentTick = AbstractWraithNecromancer.this.tickCount;
            if (currentTick - AbstractWraithNecromancer.this.summonScanTimer >= 20) {
                AbstractWraithNecromancer.this.summonScanTimer = currentTick;
                Predicate<Entity> predicate = entity -> entity.isAlive()
                        && entity instanceof IOwned owned
                        && owned.getTrueOwner() == AbstractWraithNecromancer.this;
                AbstractWraithNecromancer.this.summonCount = AbstractWraithNecromancer.this.level()
                        .getEntitiesOfClass(LivingEntity.class,
                                AbstractWraithNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D), predicate)
                        .size();
            }
            return super.canUse() && AbstractWraithNecromancer.this.summonCount < 6;
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
                    spellStat.setRadius(4.0D).setPotency(1).setDuration(3);
                    weakeningSpell.SpellResult(serverLevel, AbstractWraithNecromancer.this,
                            net.minecraft.world.item.ItemStack.EMPTY, spellStat);
                    com.Polarice3.Goety.common.magic.spells.SoulHealSpell soulHealSpell = new com.Polarice3.Goety.common.magic.spells.SoulHealSpell();
                    com.Polarice3.Goety.common.magic.SpellStat healStat = new com.Polarice3.Goety.common.magic.SpellStat(
                            0, 0, 0, 0.0D, 0, 0.0F);
                    healStat.setRadius(2.0D).setPotency(1);
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
                            2, 2, 0, 2.0D, 2, 2.0F);
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
        return ModSounds.WRAITH_NECROMANCER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.WRAITH_NECROMANCER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WRAITH_NECROMANCER_DEATH.get();
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
        net.minecraft.world.entity.ai.navigation.GroundPathNavigation groundPathNavigation = new net.minecraft.world.entity.ai.navigation.GroundPathNavigation(
                this, level) {
            public boolean isStableDestination(net.minecraft.core.BlockPos blockPos) {
                return !this.level.getBlockState(blockPos.below()).isAir();
            }
        };
        groundPathNavigation.setCanOpenDoors(false);
        groundPathNavigation.setCanFloat(true);
        groundPathNavigation.setCanPassDoors(true);
        return groundPathNavigation;
    }
}