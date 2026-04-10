package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import com.Polarice3.Goety.config.MobsConfig;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import net.minecraft.world.entity.EquipmentSlot;
import javax.annotation.Nullable;
import net.minecraft.world.entity.AnimationState;

import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MiscCapHelper;
import net.minecraft.world.phys.AABB;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import net.minecraft.world.entity.monster.AbstractIllager;
import com.Polarice3.Goety.api.entities.IOwned;
import net.minecraft.world.entity.Mob;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.tags.ItemTags;

public class VizierServant extends SpellcasterIllagerServant implements net.minecraft.world.entity.PowerableMob {
    private static final EntityDataAccessor<String> VIZIER_NAME = SynchedEntityData.defineId(VizierServant.class,
            EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<Byte> VIZIER_FLAGS = SynchedEntityData.defineId(VizierServant.class,
            EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Integer> CAST_TIMES = SynchedEntityData.defineId(VizierServant.class,
            EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> CASTING = SynchedEntityData.defineId(VizierServant.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CONFUSED = SynchedEntityData.defineId(VizierServant.class,
            EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(VizierServant.class,
            EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> STAYING = SynchedEntityData.defineId(VizierServant.class,
            EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> FANGS_ENHANCED = SynchedEntityData.defineId(VizierServant.class,
            EntityDataSerializers.BOOLEAN);

    private static final String[] VIZIER_NAME_KEYS = {
            "azmodan", "belia", "calamity", "cirno", "kalif", "malak",
            "mephisto", "praetor", "reiziv", "samaier", "sophos", "morven"
    };

    public String getNameTranslationKey() {
        return "name.goety.vizier." + this.getVizierName();
    }

    @Override
    public Component getName() {
        String name = this.getVizierName();
        if (!name.isEmpty()) {
            return Component.translatable(this.getNameTranslationKey());
        }
        return super.getName();
    }

    public float oBob;
    public float bob;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    public boolean flyWarn;
    public int airBound;
    public int deathTime = 0;
    public int moddedInvul = 0;

    public boolean spawnClones = false;
    private long lastCloneSpawnTime = 0;
    private static final long CLONE_SPAWN_COOLDOWN = 30 * 60 * 20;
    private int potionEffectTimer = 0;
    private static final int POTION_EFFECT_INTERVAL = 200;
    private int teleportSkillCooldown = 0;
    private static final int TELEPORT_SKILL_COOLDOWN = 400;
    private static final String TAG_FANGS_ENHANCED = "FangsEnhanced";

    public boolean isFangsEnhanced() {
        return this.entityData.get(FANGS_ENHANCED);
    }

    public void setFangsEnhanced(boolean enhanced) {
        this.entityData.set(FANGS_ENHANCED, enhanced);
    }

    public VizierServant(EntityType<? extends VizierServant> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new com.Polarice3.Goety.utils.MobUtil.MinionMoveControl(this);
        this.xpReward = 50;
    }

    public AnimationState introAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public java.util.List<AnimationState> getAllAnimations() {
        java.util.List<AnimationState> list = new java.util.ArrayList<>();
        list.add(this.introAnimationState);
        list.add(this.deathAnimationState);
        return list;
    }

    public void stopAnimations() {
        for (AnimationState state : this.getAllAnimations()) {
            state.stop();
        }
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAllAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(net.minecraft.network.syncher.EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor)) {
            if (this.level().isClientSide) {
                switch (this.entityData.get(ANIM_STATE)) {
                    case 0 -> this.stopAnimations();
                    case 1 -> {
                        this.introAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.introAnimationState);
                    }
                    case 2 -> {
                        this.deathAnimationState.startIfStopped(this.tickCount);
                        this.stopMostAnimation(this.deathAnimationState);
                    }
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    public void move(net.minecraft.world.entity.MoverType typeIn, Vec3 pos) {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    @Override
    public void tick() {
        if (this.getInvulnerableTicks() > 0) {
            this.setAnimationState("intro");
            this.setDeltaMovement(Vec3.ZERO);
            int j1 = this.getInvulnerableTicks() - 1;
            if (j1 == 30) {
                for (int i = 0; i < 5; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(com.Polarice3.Goety.client.particles.ModParticleTypes.CONFUSED.get(),
                            this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                this.playSound(com.Polarice3.Goety.init.ModSounds.VIZIER_CONFUSE.get(), 1.0F, 1.0F);
            }
            if (j1 == 10) {
                for (int i = 0; i < 5; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER,
                            this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                this.playSound(com.Polarice3.Goety.init.ModSounds.VIZIER_RAGE.get(), 1.0F, 1.0F);
            }
            this.setInvulnerableTicks(j1);
            if (!this.level().isClientSide) {
                this.level().broadcastEntityEvent(this, (byte) 5);
            }
        } else if (!this.isDeadOrDying()) {
            this.setAnimationState(0);
        }
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        this.oBob = this.bob;

        if (this.moddedInvul > 0) {
            --this.moddedInvul;
        }

        float f = Math.min(0.1F,
                net.minecraft.util.Mth.sqrt((float) getHorizontalDistanceSqr(this.getDeltaMovement())));

        this.bob += (f - this.bob) * 0.4F;
        if (!this.isSpellcasting()) {
            this.setCasting(this.getCasting() + 1);
        } else {
            if (this.getCastTimes() == 3) {
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                Vec3 vector3d = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);
                if (!this.level().isClientSide) {
                    if (this.getTarget() != null) {
                        double d0 = vector3d.y;
                        if (this.getY() < this.getTarget().getY() + 3.0D) {
                            d0 = Math.max(0.0D, d0);
                            d0 = d0 + (0.3D - d0 * (double) 0.6F);
                        }

                        vector3d = new Vec3(vector3d.x, d0, vector3d.z);
                        Vec3 vector3d1 = new Vec3(this.getTarget().getX() - this.getX(), 0.0D,
                                this.getTarget().getZ() - this.getZ());
                        if (getHorizontalDistanceSqr(vector3d1) > 9.0D) {
                            Vec3 vector3d2 = vector3d1.normalize();
                            vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D,
                                    vector3d2.z * 0.3D - vector3d.z * 0.6D);
                        }
                    }
                }
                this.setDeltaMovement(vector3d);
                if (getHorizontalDistanceSqr(vector3d) > 0.05D) {
                    this.setYRot((float) net.minecraft.util.Mth.atan2(vector3d.z, vector3d.x) * (180F / (float) Math.PI)
                            - 90.0F);
                }
            }
        }
        if (this.getCasting() >= 300) {
            this.setCasting(0);
        }

        int i = 0;
        if (!MobsConfig.VizierMinion.get()) {

            i = this.level()
                    .getEntitiesOfClass(com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk.class,
                            this.getBoundingBox().inflate(64))
                    .size();

            i += this.level()
                    .getEntitiesOfClass(com.Polarice3.Goety.common.entities.hostile.Irk.class,
                            this.getBoundingBox().inflate(64))
                    .size();
        } else {

            i = this.level()
                    .getEntitiesOfClass(com.Polarice3.Goety.common.entities.ally.illager.raider.AllyVex.class,
                            this.getBoundingBox().inflate(64))
                    .size();

            i += this.level()
                    .getEntitiesOfClass(net.minecraft.world.entity.monster.Vex.class,
                            this.getBoundingBox().inflate(64))
                    .size();
        }
        if (this.getCastTimes() == 1) {
            if (i >= 2) {
                if (this.level().random.nextBoolean()) {
                    this.setCastTimes(2);
                } else {
                    this.setCastTimes(3);
                }
            } else {
                this.setCastTimes(3);
            }
        }

        if (this.getHealth() <= this.getMaxHealth() / 2 && !this.spawnClones) {
            this.setCastTimes(0);
            this.setCasting(200);
        }

        this.moveCloak();
        MiscCapHelper.updateMobTarget(this);
        this.servantTick();

        this.applyPotionEffectsToAllies();

        this.teleportAlliesIfNeeded();

        if (this.getTarget() == null) {

        } else {

            if (!this.isFollowing()) {
                if (this.getTarget() == null) {

                    if (this.tickCount % 100 == 0) {
                    }

                    for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(32.0F),
                            net.minecraft.world.entity.EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {

                        if ((livingEntity instanceof net.minecraft.world.entity.player.Player ||
                                livingEntity instanceof net.minecraft.world.entity.npc.AbstractVillager ||
                                livingEntity instanceof net.minecraft.world.entity.animal.IronGolem ||
                                livingEntity instanceof net.minecraft.world.entity.monster.Monster) &&
                                this.canAttack(livingEntity) &&
                                !this.isAlliedTo(livingEntity)) {

                            net.minecraft.world.entity.LivingEntity owner = this.getTrueOwner();
                            if (owner instanceof net.minecraft.world.entity.player.Player player) {
                                if (!SEHelper.getAllyEntities(player).contains(livingEntity) &&
                                        !SEHelper.getAllyEntityTypes(player).contains(livingEntity.getType())) {
                                    this.setTarget(livingEntity);
                                    break;
                                }
                            } else {

                                this.setTarget(livingEntity);
                                break;
                            }
                        }
                    }
                } else {

                    if (this.tickCount % 100 == 0) {
                    }

                    if (!this.getTarget().onGround()) {
                        ++this.airBound;
                    } else {
                        this.airBound = 0;
                    }

                    if (this.tickCount % 50 == 0) {
                    }
                }
            }

            if (!this.getTarget().onGround()) {
                ++this.airBound;
            } else {
                this.airBound = 0;
            }
        }

        if (this.isStaying() && this.getTarget() == null) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
        }

    }

    public static double getHorizontalDistanceSqr(Vec3 pVector) {
        return pVector.x * pVector.x + pVector.z * pVector.z;
    }

    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0D;
        if (d0 > d3) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > d3) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > d3) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -d3) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -d3) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -d3) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25D;
        this.zCloak += d2 * 0.25D;
        this.yCloak += d1 * 0.25D;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(this));
        this.goalSelector.addGoal(0, new SpawnClonesGoal());
        this.goalSelector.addGoal(1, new FangsSpellGoal());
        this.goalSelector.addGoal(1, new HealGoal());
        this.goalSelector.addGoal(1, new SpikesGoal());
        this.goalSelector.addGoal(1, new MoveRandomGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(9, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this,
                net.minecraft.world.entity.player.Player.class, 3.0F, 1.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && VizierServant.this.getTarget() == null;
            }
        });
        this.goalSelector.addGoal(10, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this,
                net.minecraft.world.entity.Mob.class, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && VizierServant.this.getTarget() == null;
            }
        });
        super.registerGoals();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.VizierHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.VizierDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.VizierHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.VizierDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VIZIER_NAME, "");
        this.entityData.define(VIZIER_FLAGS, (byte) 0);
        this.entityData.define(CAST_TIMES, 0);
        this.entityData.define(CASTING, 0);
        this.entityData.define(CONFUSED, 0);
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(STAYING, false);
        this.entityData.define(FANGS_ENHANCED, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setInvulnerableTicks(compound.getInt("Confused"));
        this.setCasting(compound.getInt("Casting"));
        this.setCastTimes(compound.getInt("CastTimes"));
        if (compound.contains("ModdedInvul")) {
            this.moddedInvul = compound.getInt("ModdedInvul");
        }
        this.flyWarn = compound.getBoolean("FlyWarn");
        if (compound.contains("VizierName")) {
            this.setVizierName(compound.getString("VizierName"));
        }
        if (compound.contains("SpawnClones")) {
            this.spawnClones = compound.getBoolean("SpawnClones");
        }
        if (compound.contains("LastCloneSpawnTime")) {
            this.lastCloneSpawnTime = compound.getLong("LastCloneSpawnTime");
        }
        if (compound.contains("Staying")) {
            this.setStaying(compound.getBoolean("Staying"));
        }
        if (compound.contains(TAG_FANGS_ENHANCED)) {
            this.setFangsEnhanced(compound.getBoolean(TAG_FANGS_ENHANCED));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Confused", this.getInvulnerableTicks());
        compound.putInt("Casting", this.getCasting());
        compound.putInt("CastTimes", this.getCastTimes());
        compound.putInt("ModdedInvul", this.moddedInvul);
        compound.putBoolean("FlyWarn", this.flyWarn);
        if (!this.getVizierName().isEmpty()) {
            compound.putString("VizierName", this.getVizierName());
        }
        compound.putBoolean("SpawnClones", this.spawnClones);
        compound.putLong("LastCloneSpawnTime", this.lastCloneSpawnTime);
        compound.putBoolean("Staying", this.isStaying());
        compound.putBoolean(TAG_FANGS_ENHANCED, this.isFangsEnhanced());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if ("cirno".equals(this.getVizierName())) {
            return com.k1sak1.goetyawaken.init.ModSounds.BAKA.get();
        } else {
            return ModSounds.VIZIER_AMBIENT.get();
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.VIZIER_DEATH.get();
    }

    private void applyPotionEffectsToAllies() {
        this.potionEffectTimer++;
        if (this.potionEffectTimer >= POTION_EFFECT_INTERVAL && this.getTarget() != null) {
            this.potionEffectTimer = 0;
            LivingEntity owner = this.getTrueOwner();
            if (owner == null)
                return;
            List<Mob> allies = this.level().getEntitiesOfClass(Mob.class,
                    this.getBoundingBox().inflate(32.0D),
                    entity -> {
                        if (entity instanceof AbstractIllager ||
                                entity instanceof AbstractIllagerServant ||
                                entity instanceof RaiderServant ||
                                entity instanceof SpellcasterIllagerServant) {
                            if (entity instanceof IOwned owned) {
                                return owned.getTrueOwner() == owner;
                            }
                        }
                        return false;
                    });
            for (Mob ally : allies) {
                int effectType = this.random.nextInt(3);
                MobEffectInstance randomEffect = null;

                switch (effectType) {
                    case 0:
                        randomEffect = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0, false, false);
                        break;
                    case 1:
                        randomEffect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false);
                        break;
                    case 2:
                        randomEffect = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, false);
                        break;
                }

                if (randomEffect != null) {
                    ally.addEffect(randomEffect);
                }

                ally.addEffect(new MobEffectInstance(GoetyEffects.RALLYING.get(), 200, 0, false, false));
                ally.addEffect(new MobEffectInstance(GoetyEffects.SHIELDING.get(), 200, 0, false, false));
            }
        }
    }

    private void teleportAlliesIfNeeded() {
        if (this.teleportSkillCooldown > 0) {
            this.teleportSkillCooldown--;
            return;
        }

        if (this.getTarget() != null) {
            LivingEntity owner = this.getTrueOwner();
            if (owner == null)
                return;

            List<Mob> allies = this.level().getEntitiesOfClass(Mob.class,
                    this.getBoundingBox().inflate(32.0D),
                    entity -> {
                        if (entity != this && (entity instanceof AbstractIllager ||
                                entity instanceof AbstractIllagerServant ||
                                entity instanceof RaiderServant ||
                                entity instanceof SpellcasterIllagerServant)) {
                            if (entity instanceof IOwned owned) {
                                return owned.getTrueOwner() == owner;
                            }
                        }
                        return false;
                    });
            if (!allies.isEmpty()) {
                for (Mob ally : allies) {
                    if (!ally.level().isClientSide()) {
                        ally.setTarget(this.getTarget());
                    }
                }

                if (VizierServant.this.getTarget() != null) {
                    VizierServant.this.getTarget()
                            .addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2, false, false));
                }
                this.teleportSkillCooldown = TELEPORT_SKILL_COOLDOWN;
            }
        }
    }

    private boolean isHoldingItem(LivingEntity entity, Item item) {
        return entity.getMainHandItem().is(item) || entity.getOffhandItem().is(item);
    }

    @Override
    public void die(DamageSource cause) {
        this.playSound(ModSounds.VIZIER_SCREAM.get(), 4.0F, 1.0F);

        if (!this.level().isClientSide) {
            java.util.List<VizierCloneServant> clones = this.level().getEntitiesOfClass(VizierCloneServant.class,
                    this.getBoundingBox().inflate(64.0D));
            for (VizierCloneServant clone : clones) {
                if (clone.getTrueOwner() == this) {
                    for (int i = 0; i < this.level().random.nextInt(35) + 10; ++i) {
                        ServerLevel serverLevel = (ServerLevel) this.level();
                        serverLevel.sendParticles(ParticleTypes.POOF, clone.getX(), clone.getEyeY(), clone.getZ(),
                                0, 0.0D, 0.0D, 0.0D, 0.05D);
                    }
                    clone.discard();
                }
            }
            if (!this.canRevive(cause)) {
                ItemStack fakeAppointment = new ItemStack(
                        com.k1sak1.goetyawaken.common.items.ModItems.FAKE_APPOINTMENT.get());
                if (this.getTrueOwner() != null) {
                    com.Polarice3.Goety.common.entities.projectiles.FlyingItem flyingItem = new com.Polarice3.Goety.common.entities.projectiles.FlyingItem(
                            com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                            this.level(),
                            this.getX(),
                            this.getY() + 1.0D,
                            this.getZ());
                    flyingItem.setOwner(this.getTrueOwner());
                    flyingItem.setItem(fakeAppointment);
                    flyingItem.setParticle(ParticleTypes.SOUL);
                    flyingItem.setSecondsCool(30);
                    this.level().addFreshEntity(flyingItem);
                } else {
                    net.minecraft.world.entity.item.ItemEntity itemEntity = this.spawnAtLocation(fakeAppointment);
                    if (itemEntity != null) {
                        itemEntity.setExtendedLifetime();
                    }
                }
            }
        }

        super.die(cause);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime > 0) {
            this.setAnimationState("death");
            if (!this.level().isClientSide) {
                net.minecraft.server.level.ServerLevel serverWorld = (net.minecraft.server.level.ServerLevel) this
                        .level();
                for (int p = 0; p < 8; ++p) {
                    double d0 = (double) this.getX() + this.level().random.nextDouble();
                    double d1 = (double) this.getY() + this.level().random.nextDouble();
                    double d2 = (double) this.getZ() + this.level().random.nextDouble();
                    serverWorld.sendParticles(com.Polarice3.Goety.client.particles.ModParticleTypes.BULLET_EFFECT.get(),
                            d0, d1, d2, 0, 0.45, 0.45, 0.45, 0.5F);
                }
            }
        }
        if (this.deathTime == 40) {
            this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, 2.0F,
                    (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            if (!this.level().isClientSide) {
                net.minecraft.server.level.ServerLevel serverWorld = (net.minecraft.server.level.ServerLevel) this
                        .level();
                serverWorld.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER, this.getX(),
                        this.getY(),
                        this.getZ(), 1, 0, 0, 0, 0);
                for (int p = 0; p < 32; ++p) {
                    double d0 = (double) this.getX() + this.level().random.nextDouble();
                    double d1 = (double) this.getY() + this.level().random.nextDouble();
                    double d2 = (double) this.getZ() + this.level().random.nextDouble();
                    this.level().addParticle(com.Polarice3.Goety.client.particles.ModParticleTypes.BULLET_EFFECT.get(),
                            d0, d1, d2, 0.45, 0.45, 0.45);
                    serverWorld.sendParticles(com.Polarice3.Goety.client.particles.ModParticleTypes.BULLET_EFFECT.get(),
                            d0, d1, d2, 0, 0.45, 0.45, 0.45, 0.5F);
                }
            }
            this.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        if ("cirno".equals(this.getVizierName())) {
            return com.k1sak1.goetyawaken.init.ModSounds.BAKA.get();
        } else {
            return ModSounds.VIZIER_HURT.get();
        }
    }

    public int getInvulnerableTicks() {
        return this.entityData.get(CONFUSED);
    }

    public void setInvulnerableTicks(int pTime) {
        this.entityData.set(CONFUSED, pTime);
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if ("intro".equals(animation)) {
            return 1;
        } else if ("death".equals(animation)) {
            return 2;
        } else {
            return 0;
        }
    }

    public int getCastTimes() {
        return this.entityData.get(CAST_TIMES);
    }

    public void setCastTimes(int pTime) {
        this.entityData.set(CAST_TIMES, pTime);
    }

    public int getCasting() {
        return this.entityData.get(CASTING);
    }

    public void setCasting(int pTime) {
        this.entityData.set(CASTING, pTime);
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(40);
    }

    public boolean isStaying() {
        return this.entityData.get(STAYING);
    }

    public void setStaying(boolean staying) {
        this.entityData.set(STAYING, staying);
    }

    @Override
    public boolean canBeLeader() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        LivingEntity livingEntity = this.getTarget();
        if (this.getInvulnerableTicks() > 0
                && !pSource.is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD)) {
            return false;
        }
        if (livingEntity != null) {
            if (pSource.getEntity() instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk irk
                    && irk.getTrueOwner() == this) {
                return false;
            } else {
                if (!MobsConfig.VizierMinion.get()) {
                    int irks = this.level()
                            .getEntitiesOfClass(com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk.class,
                                    this.getBoundingBox().inflate(32))
                            .size();
                    if ((this.level().random.nextBoolean() || this.getHealth() < this.getMaxHealth() / 2)
                            && irks < 16) {
                        if (!this.level().isClientSide) {
                            com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk irk = new com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk(
                                    com.Polarice3.Goety.common.entities.ModEntityType.IRK_SERVANT.get(), this.level());
                            irk.setPos(this.getX(), this.getY(), this.getZ());
                            irk.setLimitedLife(com.Polarice3.Goety.utils.MobUtil.getSummonLifespan(this.level()));
                            irk.setTrueOwner(this);
                            irk.finalizeSpawn((ServerLevelAccessor) this.level(),
                                    this.level().getCurrentDifficultyAt(this.blockPosition()),
                                    MobSpawnType.MOB_SUMMONED,
                                    null,
                                    null);
                            this.level().addFreshEntity(irk);
                        }
                    }
                } else {
                    int vexes = this.level()
                            .getEntitiesOfClass(com.Polarice3.Goety.common.entities.ally.illager.raider.AllyVex.class,
                                    this.getBoundingBox().inflate(32))
                            .size();
                    if ((this.level().random.nextBoolean() || this.getHealth() < this.getMaxHealth() / 2)
                            && vexes < 16) {
                        if (!this.level().isClientSide) {
                            com.Polarice3.Goety.common.entities.ally.illager.raider.AllyVex vex = new com.Polarice3.Goety.common.entities.ally.illager.raider.AllyVex(
                                    com.Polarice3.Goety.common.entities.ModEntityType.VEX_SERVANT.get(), this.level());
                            vex.setPos(this.getX(), this.getY(), this.getZ());
                            vex.setLimitedLife(com.Polarice3.Goety.utils.MobUtil.getSummonLifespan(this.level()));
                            vex.setTrueOwner(this);
                            vex.finalizeSpawn((ServerLevelAccessor) this.level(),
                                    this.level().getCurrentDifficultyAt(this.blockPosition()),
                                    MobSpawnType.MOB_SUMMONED,
                                    null,
                                    null);
                            this.level().addFreshEntity(vex);
                        }
                    }
                }
            }
        }

        if (this.moddedInvul > 0) {
            return false;
        }

        if (this.isSpellcasting() && !pSource.is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD)) {
            return super.hurt(pSource, pAmount / 2);
        } else {
            return super.hurt(pSource, pAmount);
        }
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        if (!source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount = Math.min(amount, AttributesConfig.VizierDamageCap.get().floatValue());
        }
        if (this.moddedInvul <= 0) {
            super.actuallyHurt(source, amount);
            if (source.getEntity() != null) {
                this.moddedInvul = MobsConfig.BossInvulnerabilityTime.get();
            }
        }
    }

    private boolean getVizierFlag(int mask) {
        int i = this.entityData.get(VIZIER_FLAGS);
        return (i & mask) != 0;
    }

    private void setVizierFlag(int mask, boolean value) {
        int i = this.entityData.get(VIZIER_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(VIZIER_FLAGS, (byte) (i & 255));
    }

    public boolean isCharging() {
        return this.getVizierFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVizierFlag(1, charging);
    }

    public boolean isSpellcasting() {
        return this.getVizierFlag(2);
    }

    public void setSpellcasting(boolean spellcasting) {
        this.setVizierFlag(2, spellcasting);
    }

    @Override
    public com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant.IllagerServantArmPose getArmPose() {
        if (this.isCharging()) {
            return com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant.IllagerServantArmPose.ATTACKING;
        } else if (this.isSpellcasting()) {
            return com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant.IllagerServantArmPose.SPELLCASTING;
        } else {
            return this.isCelebrating()
                    ? com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant.IllagerServantArmPose.CELEBRATING
                    : com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant.IllagerServantArmPose.CROSSED;
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_37856_, DifficultyInstance p_37857_,
            MobSpawnType p_37858_, @Nullable SpawnGroupData p_37859_, @Nullable CompoundTag p_37860_) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(p_37856_, p_37857_, p_37858_, p_37859_, p_37860_);
        this.populateDefaultEquipmentSlots(p_37856_.getRandom(), p_37857_);
        this.populateDefaultEquipmentEnchantments(p_37856_.getRandom(), p_37857_);
        if (this.getVizierName().isEmpty()) {
            this.setVizierName(VIZIER_NAME_KEYS[this.random.nextInt(VIZIER_NAME_KEYS.length)]);
        }
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        if (currentDate.getMonthValue() == 12 && currentDate.getDayOfMonth() == 25) {
            throwCakeToOwner();
        }
        if ("cirno".equals(this.getVizierName())) {
            playBakamusic();
        }

        return spawnGroupData;
    }

    @Override
    protected void populateDefaultEquipmentSlots(net.minecraft.util.RandomSource randomSource,
            DifficultyInstance difficulty) {
        this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.0F);
    }

    public void applyRaidBuffs(int wave, boolean p_213660_2_) {
    }

    @Override
    protected boolean canRide(net.minecraft.world.entity.Entity pEntity) {
        return false;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.VIZIER_CELEBRATE.get();
    }

    public boolean isPowered() {
        return this.isSpellcasting();
    }

    @Override
    public void handleEntityEvent(byte p_21375_) {
        if (p_21375_ == 5) {
            int j1 = this.getInvulnerableTicks() - 1;
            this.setInvulnerableTicks(j1);
        } else {
            super.handleEntityEvent(p_21375_);
        }
    }

    public String getVizierName() {
        return this.entityData.get(VIZIER_NAME);
    }

    public void setVizierName(String name) {
        this.entityData.set(VIZIER_NAME, name);
    }

    abstract class SpellGoal extends net.minecraft.world.entity.ai.goal.Goal {
        protected int duration;
        protected int duration2;
        protected int duration3;

        public void stop() {
            VizierServant.this.setSpellcasting(false);
            VizierServant.this.setCasting(0);
            VizierServant.this.setCastTimes(1);
            this.duration2 = 0;
            this.duration = 0;
            this.duration3 = 0;
        }
    }

    class FangsSpellGoal extends SpellGoal {
        private FangsSpellGoal() {
        }

        public boolean canUse() {
            return VizierServant.this.getCasting() >= 200
                    && VizierServant.this.getTarget() != null
                    && !VizierServant.this.isCharging()
                    && VizierServant.this.getCastTimes() == 0;
        }

        public void start() {
            VizierServant.this.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
            VizierServant.this.setSpellcasting(true);
            VizierServant.this.airBound = 0;
        }

        public void stop() {
            VizierServant.this.setSpellcasting(false);
            VizierServant.this.setCasting(0);
            VizierServant.this.setCastTimes(1);
            this.duration2 = 0;
            this.duration = 0;
        }

        public void tick() {
            LivingEntity livingentity = VizierServant.this.getTarget();
            if (livingentity != null) {
                ++this.duration;
                ++this.duration2;
                if (VizierServant.this.airBound > 20) {
                    if (!VizierServant.this.level().isClientSide) {
                        ServerLevel serverWorld = (ServerLevel) VizierServant.this.level();
                        for (int i = 0; i < 5; ++i) {
                            double d0 = serverWorld.random.nextGaussian() * 0.02D;
                            double d1 = serverWorld.random.nextGaussian() * 0.02D;
                            double d2 = serverWorld.random.nextGaussian() * 0.02D;
                            serverWorld.sendParticles(ParticleTypes.ENCHANT, VizierServant.this.getRandomX(1.0D),
                                    VizierServant.this.getRandomY() + 1.0D, VizierServant.this.getRandomZ(1.0D), 0, d0,
                                    d1, d2, 0.5F);
                        }
                    }
                }

                int time = VizierServant.this.getHealth() <= VizierServant.this.getMaxHealth() / 2 ? 5 : 10;
                time = VizierServant.this.airBound > 20 ? time * 2 : time;
                if (this.duration >= time) {
                    this.duration = 0;
                    if (VizierServant.this.airBound > 20 && !VizierServant.this.flyWarn) {
                        VizierServant.this.playSound(ModSounds.VIZIER_CELEBRATE.get(), 1.0F, 1.5F);
                        VizierServant.this.flyWarn = true;
                    } else {
                        this.attack(livingentity);
                    }
                }
                if (this.duration2 >= 160) {
                    VizierServant.this.setSpellcasting(false);
                    VizierServant.this.setCasting(0);
                    VizierServant.this.setCastTimes(1);
                    this.duration2 = 0;
                    this.duration = 0;
                }
            } else {
                stop();
            }
        }

        private void attack(LivingEntity livingEntity) {
            if (VizierServant.this.isFangsEnhanced()) {
                if (VizierServant.this.airBound < 40) {
                    AABB searchBox = VizierServant.this.getBoundingBox().inflate(16.0D);
                    List<LivingEntity> targets = VizierServant.this.level().getEntitiesOfClass(LivingEntity.class,
                            searchBox,
                            entity -> entity != VizierServant.this &&
                                    entity.isAlive() &&
                                    !MobUtil.areAllies(entity, VizierServant.this) &&
                                    entity != VizierServant.this.getTrueOwner());

                    for (LivingEntity target : targets) {
                        this.spawnFangs(target.getX(), target.getZ(), target.getY(), target.getY() + 1.0D, 0.0F, 0);
                        for (int i1 = 0; i1 < 5; ++i1) {
                            float f = (float) Mth.atan2(target.getZ() - VizierServant.this.getZ(),
                                    target.getX() - VizierServant.this.getX());
                            float f1 = f + (float) i1 * (float) Math.PI * 0.4F;
                            this.spawnFangs(target.getX() + (double) Mth.cos(f1) * 1.5D,
                                    target.getZ() + (double) Mth.sin(f1) * 1.5D, target.getY(), target.getY() + 1.0D,
                                    f1,
                                    0);
                        }
                    }
                } else {
                    com.Polarice3.Goety.common.entities.projectiles.SwordProjectile swordProjectile = new com.Polarice3.Goety.common.entities.projectiles.SwordProjectile(
                            VizierServant.this, VizierServant.this.level(), VizierServant.this.getMainHandItem());
                    double d0 = livingEntity.getX() - VizierServant.this.getX();
                    double d1 = livingEntity.getY(0.3333333333333333D) - swordProjectile.getY();
                    double d2 = livingEntity.getZ() - VizierServant.this.getZ();
                    double d3 = (double) Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    swordProjectile.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, 1.0F);
                    if (!VizierServant.this.getSensing().hasLineOfSight(livingEntity)) {
                        swordProjectile.setNoPhysics(true);
                    }
                    VizierServant.this.level().addFreshEntity(swordProjectile);
                    if (!VizierServant.this.isSilent()) {
                        VizierServant.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
                    }
                }

            } else {
                if (VizierServant.this.airBound < 40) {
                    BlockPos blockPos = BlockFinder.SummonPosition(livingEntity, livingEntity.blockPosition()).below();
                    float f = (float) Mth.atan2(livingEntity.getZ() - VizierServant.this.getZ(),
                            livingEntity.getX() - VizierServant.this.getX());
                    this.spawnFangs(livingEntity.getX(), livingEntity.getZ(), blockPos.getY(), blockPos.getY() + 1.0D,
                            f, 1);
                    if (MobUtil.healthIsHalved(VizierServant.this)
                            || VizierServant.this.level().getDifficulty() != Difficulty.EASY) {
                        for (int i = 0; i < 5; ++i) {
                            float f1 = f + (float) i * (float) Math.PI * 0.4F;
                            this.spawnFangs(livingEntity.getX() + (double) Mth.cos(f1) * 1.5D,
                                    livingEntity.getZ() + (double) Mth.sin(f1) * 1.5D, blockPos.getY(),
                                    blockPos.getY() + 1.0D, f1, 1);
                        }
                    }
                } else {
                    com.Polarice3.Goety.common.entities.projectiles.SwordProjectile swordProjectile = new com.Polarice3.Goety.common.entities.projectiles.SwordProjectile(
                            VizierServant.this, VizierServant.this.level(), VizierServant.this.getMainHandItem());
                    double d0 = livingEntity.getX() - VizierServant.this.getX();
                    double d1 = livingEntity.getY(0.3333333333333333D) - swordProjectile.getY();
                    double d2 = livingEntity.getZ() - VizierServant.this.getZ();
                    double d3 = (double) Mth.sqrt((float) (d0 * d0 + d2 * d2));
                    swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    swordProjectile.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, 1.0F);
                    if (!VizierServant.this.getSensing().hasLineOfSight(livingEntity)) {
                        swordProjectile.setNoPhysics(true);
                    }
                    VizierServant.this.level().addFreshEntity(swordProjectile);
                    if (!VizierServant.this.isSilent()) {
                        VizierServant.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
                    }
                }
            }
        }

        private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_,
                float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = BlockPos.containing(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = VizierServant.this.level().getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(VizierServant.this.level(), blockpos1, Direction.UP)) {
                    if (!VizierServant.this.level().isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = VizierServant.this.level().getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(VizierServant.this.level(), blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while (blockpos.getY() >= Mth.floor(p_190876_5_) - 1);

            if (flag) {
                VizierServant.this.level()
                        .addFreshEntity(new net.minecraft.world.entity.projectile.EvokerFangs(
                                VizierServant.this.level(), p_190876_1_, (double) blockpos.getY() + d0, p_190876_3_,
                                p_190876_9_, p_190876_10_, VizierServant.this));
            }
        }
    }

    class HealGoal extends SpellGoal {
        private HealGoal() {
        }

        public boolean canUse() {
            return VizierServant.this.getTarget() != null
                    && !VizierServant.this.isCharging()
                    && VizierServant.this.getCasting() >= 100
                    && VizierServant.this.getCastTimes() == 2;
        }

        public void start() {
            int i = 0;
            for (net.minecraft.world.entity.Mob ally : VizierServant.this.level().getEntitiesOfClass(
                    net.minecraft.world.entity.Mob.class, VizierServant.this.getBoundingBox().inflate(64.0D),
                    field_213690_b)) {
                if ((ally instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk irk
                        && irk.getTrueOwner() == VizierServant.this)
                        || (ally instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.AllyVex vex
                                && vex.getTrueOwner() == VizierServant.this)
                        || (ally instanceof net.minecraft.world.entity.monster.Vex vex
                                && vex.getOwner() == VizierServant.this)) {
                    ++i;
                }
            }

            if (i >= 2) {
                VizierServant.this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 1.0F);
                VizierServant.this.playSound(ModSounds.VIZIER_CELEBRATE.get(), 1.0F, 1.0F);
                VizierServant.this.setSpellcasting(true);
            } else {
                VizierServant.this.setCastTimes(0);
                VizierServant.this.setCasting(0);
                this.duration3 = 0;
            }
        }

        public void stop() {
            super.stop();
        }

        public void tick() {
            int i = 0;
            ++this.duration3;
            if (this.duration3 >= 60) {
                float totalHealth = 0.0F;
                java.util.List<net.minecraft.world.entity.Mob> alliesToHeal = new java.util.ArrayList<>();
                for (net.minecraft.world.entity.Mob ally : VizierServant.this.level().getEntitiesOfClass(
                        net.minecraft.world.entity.Mob.class, VizierServant.this.getBoundingBox().inflate(64.0D),
                        field_213690_b)) {
                    if ((ally instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.AllyIrk irk
                            && irk.getTrueOwner() == VizierServant.this)
                            || (ally instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.AllyVex vex
                                    && vex.getTrueOwner() == VizierServant.this)
                            || (ally instanceof net.minecraft.world.entity.monster.Vex vex
                                    && vex.getOwner() == VizierServant.this)) {
                        totalHealth += ally.getHealth();
                        alliesToHeal.add(ally);
                        ++i;
                    }
                }
                if (i >= 2) {
                    for (net.minecraft.world.entity.Mob ally : alliesToHeal) {
                        ally.hurt(ally.damageSources().starve(), 200.0F);
                    }
                    VizierServant.this.heal(totalHealth);

                    if (i != 0) {
                        VizierServant.this.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                    }
                }

                this.duration3 = 0;
                VizierServant.this.setSpellcasting(false);
                VizierServant.this.setCastTimes(0);
                VizierServant.this.setCasting(0);
            }
        }
    }

    class SpikesGoal extends SpellGoal {
        @Override
        public boolean canUse() {
            return VizierServant.this.getTarget() != null
                    && !VizierServant.this.isCharging()
                    && VizierServant.this.getCasting() >= 100
                    && VizierServant.this.getCastTimes() == 3;
        }

        public void start() {
            VizierServant.this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 0.5F);
            VizierServant.this.setSpellcasting(true);
        }

        public void stop() {
            VizierServant.this.setSpellcasting(false);
            VizierServant.this.setCastTimes(0);
            VizierServant.this.setCasting(0);
            this.duration = 0;
        }

        public void tick() {
            if (!VizierServant.this.level().isClientSide) {
                LivingEntity livingentity = VizierServant.this.getTarget();
                if (livingentity != null) {
                    VizierServant.this.getLookControl().setLookAt(livingentity, VizierServant.this.getMaxHeadXRot(),
                            VizierServant.this.getMaxHeadYRot());
                    double d0 = Math.min(livingentity.getY(), VizierServant.this.getY());
                    double d1 = Math.max(livingentity.getY(), VizierServant.this.getY()) + 1.0D;
                    float f = (float) Mth.atan2(livingentity.getZ() - VizierServant.this.getZ(),
                            livingentity.getX() - VizierServant.this.getX());
                    ++this.duration;
                    if (VizierServant.this.airBound > 40) {
                        if (!VizierServant.this.level().isClientSide) {
                            ServerLevel serverWorld = (ServerLevel) VizierServant.this.level();
                            for (int i = 0; i < 5; ++i) {
                                double d3 = serverWorld.random.nextGaussian() * 0.02D;
                                double d4 = serverWorld.random.nextGaussian() * 0.02D;
                                double d2 = serverWorld.random.nextGaussian() * 0.02D;
                                serverWorld.sendParticles(ParticleTypes.ENCHANT, VizierServant.this.getRandomX(1.0D),
                                        VizierServant.this.getRandomY() + 1.0D, VizierServant.this.getRandomZ(1.0D), 0,
                                        d3, d4, d2, 0.5F);
                            }
                        }
                    }
                    if (this.duration >= 40) {
                        if (VizierServant.this.airBound < 40) {
                            for (int l = 0; l < 16; ++l) {
                                double d2 = 1.25D * (double) (l + 1);
                                this.createSpellEntity(VizierServant.this.getX() + (double) Mth.cos(f) * d2,
                                        VizierServant.this.getZ() + (double) Mth.sin(f) * d2, d0, d1, f, l * 2);
                                if (MobUtil.healthIsHalved(VizierServant.this)) {
                                    float fleft = f + 0.2F;
                                    float fright = f - 0.2F;
                                    this.createSpellEntity(VizierServant.this.getX() + (double) Mth.cos(fleft) * d2,
                                            VizierServant.this.getZ() + (double) Mth.sin(fleft) * d2, d0, d1, fleft, l);
                                    this.createSpellEntity(VizierServant.this.getX() + (double) Mth.cos(fright) * d2,
                                            VizierServant.this.getZ() + (double) Mth.sin(fright) * d2, d0, d1, fright,
                                            l);
                                }
                            }
                        } else {
                            for (int j = 0; j < 3; ++j) {
                                com.Polarice3.Goety.common.entities.projectiles.SwordProjectile swordProjectile = new com.Polarice3.Goety.common.entities.projectiles.SwordProjectile(
                                        VizierServant.this, VizierServant.this.level(),
                                        VizierServant.this.getMainHandItem());
                                double d4 = livingentity.getX() - VizierServant.this.getX();
                                double d5 = livingentity.getY(0.3333333333333333D) - swordProjectile.getY();
                                double d2 = livingentity.getZ() - VizierServant.this.getZ();
                                double d3 = (double) Mth.sqrt((float) (d4 * d4 + d2 * d2));
                                swordProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                swordProjectile.shoot(d4 + VizierServant.this.random.nextGaussian(),
                                        d5 + d3 * (double) 0.2F, d2 + VizierServant.this.random.nextGaussian(), 1.6F,
                                        0.6F);
                                if (!VizierServant.this.getSensing().hasLineOfSight(livingentity)) {
                                    swordProjectile.setNoPhysics(true);
                                }
                                VizierServant.this.level().addFreshEntity(swordProjectile);
                            }
                            if (!VizierServant.this.isSilent()) {
                                VizierServant.this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F);
                            }
                        }
                        this.duration = 0;
                        VizierServant.this.setSpellcasting(false);
                        VizierServant.this.setCastTimes(0);
                        VizierServant.this.setCasting(0);
                        VizierServant.this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.5F);
                    }
                } else {
                    stop();
                }
            }
        }

        private void createSpellEntity(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_,
                float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = BlockPos.containing(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0D;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = VizierServant.this.level().getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(VizierServant.this.level(), blockpos1, Direction.UP)) {
                    if (!VizierServant.this.level().isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = VizierServant.this.level().getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(VizierServant.this.level(), blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while (blockpos.getY() >= Mth.floor(p_190876_5_) - 1);

            if (flag) {
                com.Polarice3.Goety.common.entities.projectiles.Spike spikeEntity = new com.Polarice3.Goety.common.entities.projectiles.Spike(
                        VizierServant.this.level(), p_190876_1_, (double) blockpos.getY() + d0, p_190876_3_,
                        p_190876_9_, p_190876_10_, VizierServant.this);
                spikeEntity.setOwner(VizierServant.this);
                VizierServant.this.level().addFreshEntity(spikeEntity);
            }
        }
    }

    class ChargeAttackGoal extends net.minecraft.world.entity.ai.goal.Goal {
        public ChargeAttackGoal() {
            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            LivingEntity target = VizierServant.this.getTarget();
            if (target != null
                    && !VizierServant.this.getMoveControl().hasWanted()
                    && !VizierServant.this.isSpellcasting()
                    && !VizierServant.this.isCharging()
                    && VizierServant.this.random.nextInt(7) == 0) {
                return VizierServant.this.distanceToSqr(target) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return VizierServant.this.getMoveControl().hasWanted()
                    && VizierServant.this.isCharging()
                    && !VizierServant.this.isSpellcasting()
                    && VizierServant.this.getTarget() != null
                    && VizierServant.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = VizierServant.this.getTarget();
            if (livingentity != null) {
                Vec3 vector3d = livingentity.position();
                VizierServant.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                VizierServant.this.setCharging(true);
                VizierServant.this.playSound(ModSounds.VIZIER_CELEBRATE.get(), 1.0F, 1.0F);
            }
        }

        public void stop() {
            VizierServant.this.setCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = VizierServant.this.getTarget();
            if (livingentity != null) {
                VizierServant.this.getLookControl().setLookAt(livingentity.position());
                if (VizierServant.this.getBoundingBox().inflate(1.0D).intersects(livingentity.getBoundingBox())) {
                    VizierServant.this.doHurtTarget(livingentity);
                    VizierServant.this.setCharging(false);
                } else {
                    double d0 = VizierServant.this.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vector3d = livingentity.getEyePosition(1.0F);
                        VizierServant.this.moveControl.setWantedPosition(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                    }
                }
            }
        }
    }

    class MoveRandomGoal extends net.minecraft.world.entity.ai.goal.Goal {
        public MoveRandomGoal() {
            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            if (VizierServant.this.isStaying()) {
                return false;
            }
            return !VizierServant.this.getMoveControl().hasWanted()
                    && VizierServant.this.random.nextInt(7) == 0
                    && !VizierServant.this.isCharging()
                    && VizierServant.this.getTarget() == null;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = VizierServant.this.blockPosition();
            if (VizierServant.this.getTarget() != null) {
                blockpos = VizierServant.this.getTarget().blockPosition();
            }

            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(VizierServant.this.random.nextInt(8) - 4,
                        VizierServant.this.random.nextInt(6) - 2, VizierServant.this.random.nextInt(8) - 4);
                if (VizierServant.this.level().isEmptyBlock(blockpos1)) {
                    VizierServant.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D,
                            (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    if (VizierServant.this.getTarget() == null) {
                        VizierServant.this.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D,
                                (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }

    class CopyOwnerTargetGoal extends net.minecraft.world.entity.ai.goal.target.TargetGoal {
        private final net.minecraft.world.entity.ai.targeting.TargetingConditions copyOwnerTargeting = net.minecraft.world.entity.ai.targeting.TargetingConditions
                .forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public CopyOwnerTargetGoal(net.minecraft.world.entity.PathfinderMob p_34056_) {
            super(p_34056_, false);
        }

        public boolean canUse() {
            net.minecraft.world.entity.LivingEntity owner = VizierServant.this.getTrueOwner();
            if (owner instanceof net.minecraft.world.entity.Mob mobOwner) {
                return mobOwner.getTarget() != null
                        && this.canAttack(mobOwner.getTarget(), this.copyOwnerTargeting);
            }
            return false;
        }

        public void start() {
            net.minecraft.world.entity.LivingEntity owner = VizierServant.this.getTrueOwner();
            if (owner instanceof net.minecraft.world.entity.Mob mobOwner && mobOwner.getTarget() != null
                    && this.canAttack(mobOwner.getTarget(), this.copyOwnerTargeting)) {
                VizierServant.this.setTarget(mobOwner.getTarget());
            }
            super.start();
        }
    }

    class SpawnClonesGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private int duration;
        private int castWarmupDelay;
        private int nextCastTickCount;

        @Override
        public boolean canUse() {
            long currentTime = VizierServant.this.level().getGameTime();
            boolean cooldownExpired = (currentTime - VizierServant.this.lastCloneSpawnTime) >= CLONE_SPAWN_COOLDOWN;

            return VizierServant.this.getHealth() <= VizierServant.this.getMaxHealth() / 2
                    && VizierServant.this.getTarget() != null
                    && (!VizierServant.this.spawnClones || cooldownExpired);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = VizierServant.this.getTarget();
            return target != null && target.isAlive() && this.castWarmupDelay > 0;
        }

        @Override
        public void start() {
            this.castWarmupDelay = this.getCastWarmupTime();
            this.nextCastTickCount = VizierServant.this.tickCount + this.getCastingInterval();
            VizierServant.this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 0.5F);
            VizierServant.this.setSpellcasting(true);
        }

        @Override
        public void stop() {
            VizierServant.this.setSpellcasting(false);
            VizierServant.this.setCastTimes(0);
            VizierServant.this.setCasting(0);
            this.duration = 0;
        }

        @Override
        public void tick() {
            --this.castWarmupDelay;
            if (this.castWarmupDelay == 0) {
                this.performSpellCasting();
                VizierServant.this.playSound(VizierServant.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }

            if (!VizierServant.this.level().isClientSide) {
                VizierServant.this.invulnerableTime = 20;
                LivingEntity livingentity = VizierServant.this.getTarget();
                if (livingentity != null) {
                    VizierServant.this.getLookControl().setLookAt(livingentity, VizierServant.this.getMaxHeadXRot(),
                            VizierServant.this.getMaxHeadYRot());
                    ++this.duration;
                    if (this.duration >= 20) {
                        int x = (int) (com.Polarice3.Goety.utils.MobUtil
                                .getHorizontalLeftLookAngle(VizierServant.this).x * 4);
                        int z = (int) (com.Polarice3.Goety.utils.MobUtil
                                .getHorizontalLeftLookAngle(VizierServant.this).z * 4);
                        BlockPos left = new BlockPos(VizierServant.this.blockPosition().offset(x, 0, z));
                        VizierCloneServant vizierClone = new VizierCloneServant(
                                ModEntityType.VIZIER_CLONE_SERVANT.get(), VizierServant.this.level());
                        vizierClone.setPos(left.getX(), VizierServant.this.getY(), left.getZ());
                        vizierClone.setTrueOwner(VizierServant.this);
                        vizierClone.setVizierPosition(0);
                        vizierClone.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SWORD.getDefaultInstance());
                        if (!VizierServant.this.level().isClientSide) {
                            ServerLevel serverLevel = (ServerLevel) VizierServant.this.level();
                            for (int i = 0; i < VizierServant.this.level().random.nextInt(35) + 10; ++i) {
                                serverLevel.sendParticles(ParticleTypes.POOF, vizierClone.getX(), vizierClone.getEyeY(),
                                        vizierClone.getZ(), 0, 0.0D, 0.0D, 0.0D, 0.05D);
                            }
                        }
                        VizierServant.this.level().addFreshEntity(vizierClone);
                        int x1 = (int) (com.Polarice3.Goety.utils.MobUtil
                                .getHorizontalRightLookAngle(VizierServant.this).x * 4);
                        int z1 = (int) (com.Polarice3.Goety.utils.MobUtil
                                .getHorizontalRightLookAngle(VizierServant.this).z * 4);
                        BlockPos right = new BlockPos(VizierServant.this.blockPosition().offset(x1, 0, z1));
                        VizierCloneServant vizierClone1 = new VizierCloneServant(
                                ModEntityType.VIZIER_CLONE_SERVANT.get(), VizierServant.this.level());
                        vizierClone1.setPos(right.getX(), VizierServant.this.getY(), right.getZ());
                        vizierClone1.setTrueOwner(VizierServant.this);
                        vizierClone1.setVizierPosition(1);
                        vizierClone1.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SWORD.getDefaultInstance());
                        if (!VizierServant.this.level().isClientSide) {
                            ServerLevel serverLevel = (ServerLevel) VizierServant.this.level();
                            for (int i = 0; i < VizierServant.this.level().random.nextInt(35) + 10; ++i) {
                                serverLevel.sendParticles(ParticleTypes.POOF, vizierClone1.getX(),
                                        vizierClone1.getEyeY(), vizierClone1.getZ(), 0, 0.0D, 0.0D, 0.0D, 0.05D);
                            }
                        }
                        VizierServant.this.level().addFreshEntity(vizierClone1);

                        this.duration = 0;
                        VizierServant.this.setSpellcasting(false);
                        VizierServant.this.setCastTimes(0);
                        VizierServant.this.setCasting(0);
                        VizierServant.this.spawnClones = true;
                        VizierServant.this.lastCloneSpawnTime = VizierServant.this.level().getGameTime();
                        VizierServant.this.playSound(ModSounds.VANGUARD_SUMMON.get(), 2.0F, 0.75F);
                    }
                } else {
                    stop();
                }
            }
        }

        protected void performSpellCasting() {
        }

        protected int getCastWarmupTime() {
            return 20;
        }

        protected int getCastingTime() {
            return 160;
        }

        protected int getCastingInterval() {
            return 200;
        }

        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }
    }

    class StayingAwareRandomStrollGoal extends net.minecraft.world.entity.ai.goal.RandomStrollGoal {
        public StayingAwareRandomStrollGoal(net.minecraft.world.entity.PathfinderMob p_25974_, double p_25975_) {
            super(p_25974_, p_25975_);
        }

        @Override
        public boolean canUse() {
            return !VizierServant.this.isStaying() && super.canUse();
        }
    }

    class StayingAwareMoveRandomGoal extends MoveRandomGoal {
        public StayingAwareMoveRandomGoal() {
            super();
        }

        @Override
        public boolean canUse() {
            return !VizierServant.this.isStaying() && super.canUse();
        }
    }

    private static final java.util.function.Predicate<net.minecraft.world.entity.Entity> field_213690_b = (
            p_213685_0_) -> {
        return p_213685_0_.isAlive() && !(p_213685_0_ instanceof VizierServant);
    };

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(6,
                new com.k1sak1.goetyawaken.common.entities.ai.VizierServantFollowGoal(this, 1.0D, 2.0F, 10.0F, true));
    }

    public void warnKill(Player player) {
        this.killChance = 60;
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("info.goety.servant.tryKill", this.getDisplayName()),
                true);
    }

    private void throwCakeToOwner() {
        if (!this.level().isClientSide && this.getTrueOwner() != null) {
            com.Polarice3.Goety.common.entities.projectiles.FlyingItem flyingItem = new com.Polarice3.Goety.common.entities.projectiles.FlyingItem(
                    com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                    this.level(),
                    this.getX(),
                    this.getY() + 1.0D,
                    this.getZ());
            flyingItem.setOwner(this.getTrueOwner());
            flyingItem.setItem(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.CAKE));
            flyingItem.setParticle(net.minecraft.core.particles.ParticleTypes.SOUL);
            flyingItem.setSecondsCool(30);
            this.level().addFreshEntity(flyingItem);
        }
    }

    private void playBakamusic() {
        if (!this.level().isClientSide) {
            this.playSound(com.k1sak1.goetyawaken.init.ModSounds.BAKASMUSIC.get(), 1.0F, 1.0F);
        }
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
    public boolean canBeCommanded() {
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        ItemStack itemstack2 = this.getMainHandItem();
        if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
            if (!(pPlayer.getOffhandItem().getItem() instanceof com.Polarice3.Goety.api.items.magic.IWand)) {
                if (item instanceof SwordItem || itemstack.is(ItemTags.SWORDS)) {
                    this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copy());
                    this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                    this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level().addParticle(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                                this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            if (itemstack.getItem() == com.Polarice3.Goety.common.items.ModItems.FEAST_FOCUS.get()) {
                if (!this.isFangsEnhanced()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.setFangsEnhanced(true);
                    this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY() + 1.0D, this.getZ(),
                                30,
                                0.5D, 0.5D, 0.5D, 0.1D);
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }
        }

        return super.mobInteract(pPlayer, pHand);
    }
}