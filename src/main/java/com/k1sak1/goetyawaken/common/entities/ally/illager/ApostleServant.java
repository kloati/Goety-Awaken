package com.k1sak1.goetyawaken.common.entities.ally.illager;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.SurroundGoal;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.servants.Damned;
import com.Polarice3.Goety.common.entities.hostile.servants.Inferno;
import com.Polarice3.Goety.common.entities.hostile.servants.Malghast;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.k1sak1.goetyawaken.common.entities.ally.ObsidianMonolithServant;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.projectiles.*;
import com.Polarice3.Goety.common.entities.util.*;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.nether.FireBlastSpell;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SApostleSmitePacket;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.k1sak1.goetyawaken.Config;
import com.Polarice3.Goety.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;

public class ApostleServant extends SpellCastingCultistServant implements RangedAttackMob {
    private int hitTimes;
    private int coolDown;
    private int tornadoCoolDown;
    private int infernoCoolDown;
    private int monolithCoolDown;
    private int damnedCoolDown;
    private int spellCycle;
    private int titleNumber;
    private int stuckTime;
    private Vec3 prevVecPos;
    private final Predicate<Entity> ALIVE = Entity::isAlive;
    private boolean roarParticles;
    private boolean fireArrows;
    private boolean regen;
    private boolean killedPlayer;
    private boolean isApostleUpgraded;
    private MobEffect arrowEffect;
    private static final UUID SPEED_MODIFIER_CASTING_UUID = UUID.fromString("5CF17E52-A78A-13D3-A529-90FDE04C181E");
    private static final AttributeModifier SPEED_MODIFIER_CASTING = new AttributeModifier(SPEED_MODIFIER_CASTING_UUID,
            "Casting speed penalty", -1.0D, AttributeModifier.Operation.ADDITION);
    private static final UUID SPEED_MODIFIER_MONOLITH_UUID = UUID.fromString("ba1294fc-8f77-42aa-89cc-96a28c163fa1");
    private static final AttributeModifier SPEED_MODIFIER_MONOLITH = new AttributeModifier(SPEED_MODIFIER_MONOLITH_UUID,
            "Monoliths speed penalty", -0.05D, AttributeModifier.Operation.ADDITION);
    protected static final EntityDataAccessor<Byte> BOSS_FLAGS = SynchedEntityData.defineId(ApostleServant.class,
            EntityDataSerializers.BYTE);
    public Predicate<Owned> ZOMBIE_MINIONS = (owned) -> {
        return owned instanceof ZPiglinServant && owned.getTrueOwner() == this;
    };
    private final Predicate<LivingEntity> MONOLITHS = (livingEntity) -> {
        return (livingEntity instanceof ObsidianMonolithServant monolithServant
                && monolithServant.getTrueOwner() == this);
    };
    private final Predicate<Owned> MALGHASTS = (owned) -> {
        return owned instanceof Malghast && owned.getTrueOwner() == this;
    };
    private final Predicate<Owned> RANGED_MINIONS = (owned) -> {
        return (owned instanceof Inferno || owned instanceof Malghast) && owned.getTrueOwner() == this;
    };
    private final Predicate<Entity> OWNED_TRAPS = (entity) -> {
        return entity instanceof SpellEntity abstractTrap && abstractTrap.getOwner() == this;
    };
    public int antiRegen;
    public int antiRegenTotal;
    public int deathTime = 0;
    public int moddedInvul = 0;
    public int obsidianInvul = 0;
    public double prevX;
    public double prevY;
    public double prevZ;
    public DamageSource deathBlow = this.damageSources().generic();
    public NetherSpreaderUtil netherSpreaderUtil = NetherSpreaderUtil.createLevelSpreader();

    private void summonArmoredZombiePiglin(LivingEntity target) {
        if (!Config.apostleSummonArmoredZombiePiglin)
            return;

        ServerLevel serverLevel = (ServerLevel) this.level();
        RandomSource random = this.random;
        boolean isInNether = this.isInNether();
        boolean isPhase2 = this.isSecondPhase();
        boolean isHardDifficulty = this.level().getDifficulty() == Difficulty.HARD;
        EntityType<? extends com.Polarice3.Goety.common.entities.neutral.ZPiglinServant> entityType = isPhase2
                ? com.Polarice3.Goety.common.entities.ModEntityType.ZPIGLIN_BRUTE_SERVANT.get()
                : com.Polarice3.Goety.common.entities.ModEntityType.ZPIGLIN_SERVANT.get();
        com.Polarice3.Goety.common.entities.neutral.ZPiglinServant zombiePiglin = entityType.create(serverLevel);
        if (zombiePiglin != null) {
            BlockPos.MutableBlockPos blockPos = this.blockPosition().mutable();
            blockPos.move(
                    random.nextInt(6) - random.nextInt(6),
                    0,
                    random.nextInt(6) - random.nextInt(6));
            BlockPos finalPos = BlockFinder.SummonRadius(blockPos, zombiePiglin, serverLevel, 5);
            zombiePiglin.moveTo(finalPos, 0.0F, 0.0F);
            if (zombiePiglin instanceof com.Polarice3.Goety.api.entities.IOwned) {
                ((com.Polarice3.Goety.api.entities.IOwned) zombiePiglin).setTrueOwner(this);
            }
            EquipmentSlot[] armorSlots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
                    EquipmentSlot.FEET };

            for (EquipmentSlot slot : armorSlots) {
                ItemStack armor;
                if (isHardDifficulty) {
                    switch (slot) {
                        case HEAD -> armor = new ItemStack(net.minecraft.world.item.Items.NETHERITE_HELMET);
                        case CHEST -> armor = new ItemStack(net.minecraft.world.item.Items.NETHERITE_CHESTPLATE);
                        case LEGS -> armor = new ItemStack(net.minecraft.world.item.Items.NETHERITE_LEGGINGS);
                        case FEET -> armor = new ItemStack(net.minecraft.world.item.Items.NETHERITE_BOOTS);
                        default -> armor = ItemStack.EMPTY;
                    }
                } else {
                    switch (slot) {
                        case HEAD -> armor = new ItemStack(net.minecraft.world.item.Items.GOLDEN_HELMET);
                        case CHEST -> armor = new ItemStack(net.minecraft.world.item.Items.GOLDEN_CHESTPLATE);
                        case LEGS -> armor = new ItemStack(net.minecraft.world.item.Items.GOLDEN_LEGGINGS);
                        case FEET -> armor = new ItemStack(net.minecraft.world.item.Items.GOLDEN_BOOTS);
                        default -> armor = ItemStack.EMPTY;
                    }
                }
                if (!armor.isEmpty()) {
                    EnchantmentHelper.enchantItem(serverLevel.random, armor, 15 + serverLevel.random.nextInt(10),
                            false);
                    armor.enchant(Enchantments.BINDING_CURSE, 1);
                    armor.enchant(Enchantments.VANISHING_CURSE, 1);
                }
                zombiePiglin.setItemSlot(slot, armor);
            }
            zombiePiglin.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false, true));
            zombiePiglin.finalizeSpawn(serverLevel,
                    serverLevel.getCurrentDifficultyAt(finalPos),
                    MobSpawnType.MOB_SUMMONED, null, null);
            if (isPhase2 && isHardDifficulty) {
                ItemStack weapon = new ItemStack(Items.NETHERITE_AXE);
                EnchantmentHelper.enchantItem(serverLevel.random, weapon, 15 + serverLevel.random.nextInt(10), false);
                weapon.enchant(Enchantments.BINDING_CURSE, 1);
                weapon.enchant(Enchantments.VANISHING_CURSE, 1);
                zombiePiglin.setItemSlot(EquipmentSlot.MAINHAND, weapon);
                zombiePiglin.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            }

            if (target != null) {
                zombiePiglin.setTarget(target);
            }
            com.Polarice3.Goety.common.entities.util.SummonCircle summonCircle = new com.Polarice3.Goety.common.entities.util.SummonCircle(
                    this.level(), finalPos, zombiePiglin, true, true, this);
            this.level().addFreshEntity(summonCircle);
        }
    }

    public ApostleServant(EntityType<? extends SpellCastingCultistServant> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 0;
        this.coolDown = 100;
        this.spellCycle = 0;
        this.hitTimes = 0;
        this.antiRegen = 0;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(9, new ApostleConvertVillagerGoal(this));
        this.goalSelector.addGoal(1, new SecondPhaseIndicator());
        this.goalSelector.addGoal(1, new StrafeCastGoal<>(this));
        this.goalSelector.addGoal(2, new ApostleServantBowGoal<>(this, 30.0F));
        this.goalSelector.addGoal(3, new CastingSpellGoal());
        this.goalSelector.addGoal(3, new FireballSpellGoal());
        this.goalSelector.addGoal(3, new DamnedSpellGoal());
        this.goalSelector.addGoal(3, new MonolithSpellGoal());
        this.goalSelector.addGoal(3, new FireRainSpellGoal());
        this.goalSelector.addGoal(3, new RangedSummonSpellGoal());
        this.goalSelector.addGoal(3, new FireTornadoSpellGoal());
        this.goalSelector.addGoal(3, new RoarSpellGoal());
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ApostleHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ARMOR, AttributesConfig.ApostleArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ApostleToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.ApostleHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ApostleArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.ApostleToughness.get());
    }

    protected PathNavigation createNavigation(Level p_33913_) {
        return new ApostleServantPathNavigation(this, p_33913_);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BOSS_FLAGS, (byte) 0);
    }

    private boolean getBossFlag(int mask) {
        int i = this.entityData.get(BOSS_FLAGS);
        return (i & mask) != 0;
    }

    private void setBossFlag(int mask, boolean value) {
        int i = this.entityData.get(BOSS_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(BOSS_FLAGS, (byte) (i & 255));
    }

    public boolean canStandOnFluid(FluidState p_204067_) {
        return p_204067_.is(FluidTags.LAVA);
    }

    private void floatApostleServant() {
        if (this.isInLava()) {
            CollisionContext collisioncontext = CollisionContext.of(this);
            if (collisioncontext.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true)
                    && !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }

    }

    public float getWalkTargetValue(BlockPos p_33895_, LevelReader p_33896_) {
        if (p_33896_.getBlockState(p_33895_).getFluidState().is(FluidTags.LAVA)) {
            return 10.0F;
        } else {
            return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    public int getAmbientSoundInterval() {
        return 200;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.APOSTLE_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.APOSTLE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.APOSTLE_PREDEATH.get();
    }

    protected SoundEvent getTrueDeathSound() {
        return ModSounds.APOSTLE_DEATH.get();
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return pPotioneffect.getEffect() != GoetyEffects.BURN_HEX.get()
                && pPotioneffect.getEffect() != MobEffects.WITHER && super.canBeAffected(pPotioneffect);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("coolDown", this.coolDown);
        pCompound.putInt("tornadoCoolDown", this.tornadoCoolDown);
        pCompound.putInt("infernoCoolDown", this.infernoCoolDown);
        pCompound.putInt("monolithCoolDown", this.monolithCoolDown);
        pCompound.putInt("damnedCoolDown", this.damnedCoolDown);
        pCompound.putInt("spellCycle", this.spellCycle);
        pCompound.putInt("hitTimes", this.hitTimes);
        pCompound.putInt("antiRegen", this.antiRegen);
        pCompound.putInt("antiRegenTotal", this.antiRegenTotal);
        pCompound.putInt("titleNumber", this.titleNumber);
        pCompound.putInt("moddedInvul", this.moddedInvul);
        pCompound.putInt("obsidianInvul", this.obsidianInvul);
        pCompound.putBoolean("fireArrows", this.fireArrows);
        pCompound.putBoolean("secondPhase", this.isSecondPhase());
        pCompound.putBoolean("settingSecondPhase", this.isSettingUpSecond());
        pCompound.putBoolean("regen", this.regen);
        pCompound.putBoolean("upgraded", this.isApostleUpgraded);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.coolDown = pCompound.getInt("coolDown");
        this.tornadoCoolDown = pCompound.getInt("tornadoCoolDown");
        this.infernoCoolDown = pCompound.getInt("infernoCoolDown");
        this.monolithCoolDown = pCompound.getInt("monolithCoolDown");
        this.damnedCoolDown = pCompound.getInt("damnedCoolDown");
        this.spellCycle = pCompound.getInt("spellCycle");
        this.hitTimes = pCompound.getInt("hitTimes");
        this.antiRegen = pCompound.getInt("antiRegen");
        this.antiRegenTotal = pCompound.getInt("antiRegenTotal");
        this.titleNumber = pCompound.getInt("titleNumber");
        this.moddedInvul = pCompound.getInt("moddedInvul");
        this.obsidianInvul = pCompound.getInt("obsidianInvul");
        this.fireArrows = pCompound.getBoolean("fireArrows");
        this.regen = pCompound.getBoolean("regen");
        this.isApostleUpgraded = pCompound.getBoolean("upgraded");
        this.setTitleNumber(this.titleNumber);
        this.TitleEffect(this.titleNumber);
        this.setSecondPhase(pCompound.getBoolean("secondPhase"));
        this.setSettingUpSecond(pCompound.getBoolean("settingSecondPhase"));
    }

    protected boolean isAffectedByFluids() {
        return false;
    }

    // public boolean isAlliedTo(Entity entityIn) {
    // if (entityIn.getType().is(ModTags.EntityTypes.APOSTLE_OTHER_ALLIES)) {
    // return this.getTeam() == null && entityIn.getTeam() == null;
    // } else {
    // return super.isAlliedTo(entityIn);
    // }
    // }

    public void die(DamageSource cause) {
        if (this.deathTime > 0) {
            if (!this.canRevive(cause)) {
                if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
                    ItemStack obsidianTearStack = new ItemStack(
                            com.k1sak1.goetyawaken.common.items.ModItems.OBSIDIAN_TEAR.get());
                    if (this.getTrueOwner() != null) {
                        com.Polarice3.Goety.common.entities.projectiles.FlyingItem flyingItem = new com.Polarice3.Goety.common.entities.projectiles.FlyingItem(
                                com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                                this.level(),
                                this.getX(),
                                this.getY() + 1.0D,
                                this.getZ());
                        flyingItem.setOwner(this.getTrueOwner());
                        flyingItem.setItem(obsidianTearStack);
                        flyingItem.setParticle(ParticleTypes.SOUL);
                        flyingItem.setSecondsCool(30);

                        this.level().addFreshEntity(flyingItem);
                    } else {
                        net.minecraft.world.entity.item.ItemEntity itemEntity = this.spawnAtLocation(obsidianTearStack);
                        if (itemEntity != null) {
                            itemEntity.setExtendedLifetime();
                        }
                    }
                }
            }
            super.die(cause);
        }
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 1) {
            this.antiRegen = 0;
            this.antiRegenTotal = 0;
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        if (MobsConfig.FancierApostleDeath.get() || this.isInNether()) {
            this.setNoGravity(true);
            if (this.getKillCredit() instanceof Player) {
                this.lastHurtByPlayerTime = 100;
            }
            if (this.deathTime < 180) {
                if (this.deathTime > 20) {
                    this.move(MoverType.SELF, new Vec3(0.0D, 0.1D, 0.0D));
                }
                ExplosionUtil.lootExplode(this.level(), this, this.getRandomX(1.0D), this.getRandomY(),
                        this.getRandomZ(1.0D), 0.0F, false, Explosion.BlockInteraction.KEEP,
                        LootingExplosion.Mode.LOOT);
            } else if (this.deathTime != 200) {
                this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
            }
            if (this.deathTime >= 200) {
                this.move(MoverType.SELF, new Vec3(0.0D, -4.0D, 0.0D));
                if (this.onGround() || this.getY() <= this.level().getMinBuildHeight()) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        if (serverLevel.getLevelData().isThundering()
                                && Config.APOSTLE_SECOND_PHASE_THUNDER_STORM.get()) {
                            serverLevel.setWeatherParameters(6000, 0, false, false);
                        }
                        for (int k = 0; k < 200; ++k) {
                            float f2 = random.nextFloat() * 4.0F;
                            float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                            double d1 = Mth.cos(f1) * f2;
                            double d2 = 0.01D + random.nextDouble() * 0.5D;
                            double d3 = Mth.sin(f1) * f2;
                            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX() + d1 * 0.1D,
                                    this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                            serverLevel.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D,
                                    this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                        }
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(),
                                this.getZ(), 0, 1.0F, 0.0F, 0.0F, 0.5F);
                    }
                    this.playSound(SoundEvents.GENERIC_EXPLODE, 4.0F,
                            (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
                    this.playSound(this.getTrueDeathSound(), 5.0F,
                            (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.die(this.deathBlow);
                    this.remove(RemovalReason.KILLED);
                }
            }
        } else {
            this.move(MoverType.SELF, new Vec3(0.0D, 0.0D, 0.0D));
            if (this.deathTime == 1) {
                if (!this.level().isClientSide) {
                    ServerLevel ServerLevel = (ServerLevel) this.level();
                    if (ServerLevel.getLevelData().isThundering() && Config.APOSTLE_SECOND_PHASE_THUNDER_STORM.get()) {
                        ServerLevel.setWeatherParameters(6000, 0, false, false);
                    }
                    for (int k = 0; k < 200; ++k) {
                        float f2 = random.nextFloat() * 4.0F;
                        float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                        double d1 = Mth.cos(f1) * f2;
                        double d2 = 0.01D + random.nextDouble() * 0.5D;
                        double d3 = Mth.sin(f1) * f2;
                        ServerLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX() + d1 * 0.1D,
                                this.getY() + 0.3D, this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                        ServerLevel.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D,
                                this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.5F);
                    }
                    for (int l = 0; l < 16; ++l) {
                        ServerLevel.sendParticles(ParticleTypes.FLAME, this.getRandomX(1.0F), this.getRandomY() - 0.25F,
                                this.getRandomZ(1.0F), 1, 0, 0, 0, 0);
                    }
                }
                this.die(this.deathBlow);
                this.playSound(this.getTrueDeathSound(), 5.0F,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }
            if (this.deathTime >= 30) {
                this.remove(RemovalReason.KILLED);
            }
        }

    }

    @Override
    public void remove(RemovalReason p_146834_) {
        if (!this.level().isClientSide) {
            ServerLevel ServerLevel = (ServerLevel) this.level();
            if (ServerLevel.getLevelData().isThundering() && Config.APOSTLE_SECOND_PHASE_THUNDER_STORM.get()) {
                ServerLevel.setWeatherParameters(6000, 0, false, false);
            }
            for (AbstractTrap trapEntity : this.level().getEntitiesOfClass(AbstractTrap.class,
                    this.getBoundingBox().inflate(64))) {
                if (trapEntity.getOwner() == this) {
                    trapEntity.discard();
                }
            }
            for (SpellEntity trapEntity : this.level().getEntitiesOfClass(SpellEntity.class,
                    this.getBoundingBox().inflate(64))) {
                if (trapEntity.getOwner() == this) {
                    trapEntity.discard();
                }
            }
            for (FireTornado fireTornadoEntity : this.level().getEntitiesOfClass(FireTornado.class,
                    this.getBoundingBox().inflate(64))) {
                fireTornadoEntity.discard();
            }
        }
        super.remove(p_146834_);
    }

    protected float getDamageAfterMagicAbsorb(DamageSource source, float damage) {
        damage = super.getDamageAfterMagicAbsorb(source, damage);
        if (source.getEntity() == this) {
            damage = 0.0F;
        }
        if (this.level().getDifficulty() == Difficulty.HARD) {
            if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
                damage = (float) ((double) damage * 0.15D);
            }
        }
        return damage;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
            MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        if (!this.hasCustomName()) {
            int random = this.random.nextInt(18);
            int random2 = this.random.nextInt(12);
            this.setTitleNumber(random2);
            this.TitleEffect(random2);
            Component component = Component.translatable("name.goety.apostle." + random);
            Component component1 = Component.translatable("title.goety." + random2);
            if (random2 == 1) {
                this.setHealth(this.getMaxHealth());
            }
            this.setCustomName(Component.translatable(component.getString() + component1.getString()));
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void setTitleNumber(Integer integer) {
        this.titleNumber = integer;
    }

    public int getTitleNumber() {
        return this.titleNumber;
    }

    public void TitleEffect(Integer integer) {
        switch (integer) {
            case 0 -> this.setRegen(true);
            case 1 -> this.setArrowEffect(MobEffects.HARM);
            case 2 -> this.setArrowEffect(MobEffects.POISON);
            case 3 -> this.setArrowEffect(MobEffects.WITHER);
            case 4 -> this.setArrowEffect(MobEffects.DARKNESS);
            case 5 -> this.setArrowEffect(MobEffects.WEAKNESS);
            case 6 -> {
                this.setFireArrow(true);
                this.setArrowEffect(GoetyEffects.BURN_HEX.get());
                if (this.isApostleUpgraded()) {
                    this.addEffect(new MobEffectInstance(GoetyEffects.FIERY_AURA.get(), -1, 0, false, false), this);
                }
            }
            case 7 -> this.setArrowEffect(MobEffects.HUNGER);
            case 8 -> {
                this.setArrowEffect(MobEffects.MOVEMENT_SLOWDOWN);
                if (this.isApostleUpgraded()) {
                    this.addEffect(new MobEffectInstance(GoetyEffects.FROSTY_AURA.get(), -1, 0, false, false), this);
                }
            }
            case 9 -> this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false), this);
            case 10 -> {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 0, false, false), this);
                if (this.isApostleUpgraded()) {
                    this.addEffect(new MobEffectInstance(GoetyEffects.IRON_HIDE.get(), -1, 4, false, false), this);
                }
            }
            case 11 -> this.setArrowEffect(GoetyEffects.SAPPED.get());
        }
    }

    public boolean addEffect(MobEffectInstance p_182397_, @Nullable Entity p_182398_) {
        if (p_182398_ == this) {
            return super.addEffect(p_182397_, p_182398_);
        } else {
            return p_182397_.getEffect().isBeneficial();
        }
    }

    public void setRegen(boolean regen) {
        this.regen = regen;
    }

    public boolean Regen() {
        return this.regen;
    }

    public boolean isApostleUpgraded() {
        return this.isApostleUpgraded;
    }

    public void setApostleUpgraded(boolean upgraded) {
        this.isApostleUpgraded = upgraded;
    }

    public void setFireArrow(boolean fireArrow) {
        this.fireArrows = fireArrow;
    }

    public boolean getFireArrow() {
        return this.fireArrows;
    }

    public void setArrowEffect(MobEffect effect) {
        this.arrowEffect = effect;
    }

    public MobEffect getArrowEffect() {
        return this.arrowEffect;
    }

    public void setSecondPhase(boolean secondPhase) {
        this.setBossFlag(1, secondPhase);
    }

    public boolean isSecondPhase() {
        return this.getBossFlag(1);
    }

    public void setSettingUpSecond(boolean settingupSecond) {
        this.setBossFlag(2, settingupSecond);
    }

    public boolean isSettingUpSecond() {
        return this.getBossFlag(2);
    }

    public void setCasting(boolean casting) {
        this.setBossFlag(4, casting);
    }

    public boolean isCasting() {
        return this.getBossFlag(4);
    }

    public void setMonolithPower(boolean monolithPower) {
        this.setBossFlag(8, monolithPower);
    }

    public boolean isMonolithPower() {
        return this.getBossFlag(8);
    }

    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    public CultistServantArmPose getArmPose() {
        if (this.isDeadOrDying()) {
            return CultistServantArmPose.DYING;
        } else if (this.getMainHandItem().getItem() instanceof BowItem) {
            if (this.isAggressive() && !this.isSpellcasting() && !this.isSettingUpSecond()) {
                return CultistServantArmPose.BOW_AND_ARROW;
            } else if (this.isSpellcasting()) {
                return CultistServantArmPose.SPELL_AND_WEAPON;
            } else if (this.isSettingUpSecond()) {
                return CultistServantArmPose.SPELL_AND_WEAPON;
            } else {
                return CultistServantArmPose.CROSSED;
            }
        } else {
            return CultistServantArmPose.CROSSED;
        }
    }

    public boolean isFiring() {
        return this.roarParticles;
    }

    public void setFiring(boolean firing) {
        this.roarParticles = firing;
    }

    public int hitTimeTeleport() {
        return this.isSecondPhase() ? 2 : 4;
    }

    public void resetHitTime() {
        this.hitTimes = 0;
    }

    public void increaseHitTime() {
        ++this.hitTimes;
    }

    public int getHitTimes() {
        return this.hitTimes;
    }

    public void resetCoolDown() {
        this.setCoolDown(0);
    }

    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    public int getCoolDown() {
        return this.coolDown;
    }

    public void setTornadoCoolDown(int coolDown) {
        this.tornadoCoolDown = coolDown;
    }

    public int getTornadoCoolDown() {
        return this.tornadoCoolDown;
    }

    public void setInfernoCoolDown(int coolDown) {
        this.infernoCoolDown = coolDown;
    }

    public int getInfernoCoolDown() {
        return this.infernoCoolDown;
    }

    public void setMonolithCoolDown(int coolDown) {
        this.monolithCoolDown = coolDown;
    }

    public int getMonolithCoolDown() {
        return this.monolithCoolDown;
    }

    public void setDamnedCoolDown(int coolDown) {
        this.damnedCoolDown = coolDown;
    }

    public int getDamnedCoolDown() {
        return this.damnedCoolDown;
    }

    public int getAntiRegen() {
        return this.antiRegen;
    }

    public int getAntiRegenTotal() {
        return this.antiRegenTotal;
    }

    public boolean isSmited() {
        return this.antiRegen > 0;
    }

    public boolean monolithWeakened() {
        return this.obsidianInvul > 0 || this.monolithCoolDown > MathHelper.secondsToTicks(45);
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        LivingEntity livingEntity = this.getTarget();
        if (!this.level().isClientSide) {
            if (livingEntity != null && pSource.getEntity() instanceof LivingEntity) {
                this.increaseHitTime();
            }
        }

        if (pSource.getDirectEntity() instanceof LivingEntity living) {
            int smite = EnchantmentHelper.getEnchantmentLevel(Enchantments.SMITE, living);
            if (smite > 0) {
                int smite2 = Mth.clamp(smite, 1, 5);
                int duration = MathHelper.secondsToTicks(smite2);
                this.antiRegenTotal = duration;
                this.antiRegen = duration;
                if (this.level() instanceof ServerLevel) {
                    ModNetwork.sendToALL(new SApostleSmitePacket(this.getId(), duration));
                    if (this.getCoolDown() < this.coolDownLimit()) {
                        this.coolDown += 10;
                    }
                }
            }
        }

        if (pSource.is(DamageTypes.LIGHTNING_BOLT) || pSource.is(DamageTypes.FALL) || pSource.is(DamageTypes.IN_WALL)) {
            return false;
        }

        if (pSource.getDirectEntity() instanceof AbstractArrow
                && ((AbstractArrow) pSource.getDirectEntity()).getOwner() == this) {
            return false;
        }

        if (this.isSettingUpSecond()) {
            return false;
        }

        if (pSource.getDirectEntity() instanceof NetherMeteor || pSource.getEntity() instanceof NetherMeteor) {
            return false;
        }

        if (this.moddedInvul > 0 || this.obsidianInvul > 0) {
            return false;
        }

        float trueAmount = this.isInNether() ? pAmount / 2 : pAmount;

        if (this.getHitTimes() >= this.hitTimeTeleport()) {
            trueAmount = trueAmount / 2;
            this.teleport();
        } else if (pSource.getEntity() == null) {
            trueAmount = trueAmount / 2;
            if (this.level().getRandom().nextBoolean()) {
                this.teleport();
            }
        }

        if (!this.level().getNearbyPlayers(TargetingConditions.forCombat().selector(MobUtil.NO_CREATIVE_OR_SPECTATOR),
                this, this.getBoundingBox().inflate(32)).isEmpty()) {
            if (!(pSource.getEntity() instanceof Player)) {
                trueAmount = trueAmount / 2;
            }
        }

        if (this.isDeadOrDying()) {
            this.deathBlow = pSource;
        }

        return super.hurt(pSource, trueAmount);
    }

    protected void actuallyHurt(DamageSource source, float amount) {
        float initialAmount = amount;
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount = Math.min(initialAmount, AttributesConfig.ApostleDamageCap.get().floatValue());
        }
        if (this.moddedInvul <= 0) {
            super.actuallyHurt(source, amount);
            if (source.getEntity() != null) {
                this.moddedInvul = MobsConfig.BossInvulnerabilityTime.get();
            }
        }
    }

    public void heal(float p_21116_) {
        if (!this.isSmited()) {
            super.heal(p_21116_);
        }
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource damageSource) {
        return false;
    }

    protected void teleport() {
        if (!this.level().isClientSide() && this.isAlive() && !this.isSettingUpSecond() && !this.isCasting()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            for (int i = 0; i < 128; ++i) {
                boolean flag = true;
                double d3 = this.getX() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                double d4 = this.getY();
                if (this.getTarget() != null) {
                    d4 = this.getTarget().getY();
                }
                double d5 = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * 32.0D;
                BlockPos blockPos = BlockPos.containing(d3, d4, d5);
                if (this.getTarget() != null && i < 64) {
                    flag = BlockFinder.canSeeBlock(this.getTarget(), blockPos);
                }
                if (flag) {
                    if (this.randomTeleport(d3, d4, d5, false)) {
                        this.teleportHits();
                        this.resetHitTime();
                        break;
                    }
                }
            }
        }
    }

    private void teleportTowardstarget(Entity entity) {
        if (!this.level().isClientSide() && this.isAlive() && !this.isSettingUpSecond()) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            for (int i = 0; i < 128; ++i) {
                Vec3 vector3d = new Vec3(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(),
                        this.getZ() - entity.getZ());
                vector3d = vector3d.normalize();
                double d0 = 16.0D;
                double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.x * d0;
                double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vector3d.y * d0;
                double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vector3d.z * d0;
                BlockPos blockPos1 = BlockPos.containing(d1, d2, d3);
                if (BlockFinder.canSeeBlock(entity, blockPos1)) {
                    if (this.randomTeleport(d1, d2, d3, false)) {
                        this.teleportHits();
                        break;
                    }
                }
            }
        }
    }

    protected void escapeTeleport() {
        if (!this.level().isClientSide() && this.isAlive() && !this.isSettingUpSecond() && !this.isCasting()) {
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
                    this.level().gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
                    if (!this.isSilent()) {
                        this.level().playSound((Player) null, this.prevX, this.prevY, this.prevZ,
                                ModSounds.APOSTLE_TELEPORT.get(), this.getSoundSource(), 1.0F, 1.0F);
                        this.playSound(ModSounds.APOSTLE_TELEPORT.get(), 1.0F, 1.0F);
                        this.level().playSound((Player) null, this.prevX, this.prevY, this.prevZ,
                                ModSounds.ROAR_SPELL.get(), this.getSoundSource(), 3.0F, 0.25F);
                    }
                    this.resetHitTime();
                    break;
                }
            }
        }
    }

    public void teleportHits() {
        this.stuckTime = 0;
        this.level().broadcastEntityEvent(this, (byte) 100);
        this.level().gameEvent(GameEvent.TELEPORT, this.position(), GameEvent.Context.of(this));
        if (this.isSecondPhase()) {
            if (this.getTarget() != null) {
                if (this.level().getDifficulty() == Difficulty.HARD || this.isInNether()) {
                    FireBlastTrap fireBlastTrap = new FireBlastTrap(this.level(), this.prevX, this.prevY + 0.25D,
                            this.prevZ);
                    fireBlastTrap.setOwner(this);
                    fireBlastTrap.setAreaOfEffect(3.0F);
                    this.level().addFreshEntity(fireBlastTrap);
                } else {
                    FireBlastTrap fireBlastTrap = new FireBlastTrap(this.level(), this.prevX, this.prevY + 0.25D,
                            this.prevZ);
                    fireBlastTrap.setOwner(this);
                    fireBlastTrap.setAreaOfEffect(1.5F);
                    this.level().addFreshEntity(fireBlastTrap);
                }
            }
        }
        if (!this.isSilent()) {
            this.level().playSound((Player) null, this.prevX, this.prevY, this.prevZ,
                    ModSounds.APOSTLE_TELEPORT.get(),
                    this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(ModSounds.APOSTLE_TELEPORT.get(), 1.0F, 1.0F);
        }
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 100) {
            int i = 128;

            for (int j = 0; j < i; ++j) {
                double d0 = (double) j / (i - 1);
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, this.prevX, this.getX())
                        + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                double d2 = Mth.lerp(d0, this.prevY, this.getY())
                        + this.random.nextDouble() * (double) this.getBbHeight();
                double d3 = Mth.lerp(d0, this.prevZ, this.getZ())
                        + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth() * 2.0D;
                this.level().addParticle(ParticleTypes.SMOKE, d1, d2, d3, (double) f, (double) f1, (double) f2);
            }
        } else if (pId == 101) {
            this.setMonolithPower(true);
        } else if (pId == 102) {
            this.setMonolithPower(false);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return ModSounds.APOSTLE_CAST_SPELL.get();
    }

    public void setSpellCycle(int spellCycle) {
        this.spellCycle = spellCycle;
    }

    public int getSpellCycle() {
        return this.spellCycle;
    }

    protected boolean canRide(Entity pEntity) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.floatApostleServant();
    }

    public boolean isInNether() {
        return this.level().dimension() == Level.NETHER;
    }

    public void aiStep() {
        super.aiStep();
        if (this.moddedInvul > 0) {
            --this.moddedInvul;
        }
        if (this.obsidianInvul > 0) {
            --this.obsidianInvul;
        }
        if (this.level().isClientSide) {
            if (this.isSettingUpSecond()) {
                for (int i = 0; i < 40; ++i) {
                    double d0 = this.random.nextGaussian() * 0.2D;
                    double d1 = this.random.nextGaussian() * 0.2D;
                    double d2 = this.random.nextGaussian() * 0.2D;
                    this.level().addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5,
                            this.getZ(), d0, d1, d2);
                }
            }
        }
        if (!this.level().isClientSide) {
            AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attributeinstance != null) {
                if (attributeinstance.hasModifier(SPEED_MODIFIER_CASTING)) {
                    attributeinstance.removeModifier(SPEED_MODIFIER_CASTING);
                }
                if (this.isCasting() && !this.isInNether()) {
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_CASTING);
                }
                if (attributeinstance.hasModifier(SPEED_MODIFIER_MONOLITH)) {
                    attributeinstance.removeModifier(SPEED_MODIFIER_MONOLITH);
                }
                if (this.monolithWeakened()) {
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_MONOLITH);
                }
            }
            if (this.obsidianInvul > 0) {
                this.level().broadcastEntityEvent(this, (byte) 101);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 102);
            }
            this.netherSpreaderUtil.clear();

        }
        if (this.isSettingUpSecond()) {
            this.antiRegen = 0;
            this.antiRegenTotal = 0;
            this.setFiring(false);
            if (this.tickCount % 5 == 0) {
                this.heal(0.015625F * this.getMaxHealth());
            }
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(3.0D),
                    ALIVE)) {
                if (!MobUtil.areAllies(this, entity)) {
                    this.barrier(entity, this);
                }
            }

            if (!this.level().isClientSide) {
                this.resetHitTime();
                this.resetCoolDown();
                this.setSpellCycle(0);
                if (this.level() instanceof ServerLevel serverLevel) {
                    ServerParticleUtil.windParticle(serverLevel, new ColorUtil(ChatFormatting.BLACK), 2.0F, 1.5F,
                            this.getId(), this.position());
                    ServerParticleUtil.windParticle(serverLevel, new ColorUtil(ChatFormatting.BLACK), 4.0F, 0.5F,
                            this.getId(), this.position());
                }
            }
            if (this.getHealth() >= this.getMaxHealth()) {
                if (!this.level().isClientSide) {
                    ServerLevel serverLevel = (ServerLevel) this.level();
                    if (!serverLevel.isThundering() && Config.APOSTLE_SECOND_PHASE_THUNDER_STORM.get()) {
                        serverLevel.setWeatherParameters(0, 6000, true, true);
                    }
                    for (int k = 0; k < 60; ++k) {
                        float f2 = random.nextFloat() * 4.0F;
                        float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                        double d1 = Mth.cos(f1) * f2;
                        double d2 = 0.01D + random.nextDouble() * 0.5D;
                        double d3 = Mth.sin(f1) * f2;
                        serverLevel.sendParticles(ParticleTypes.FLAME, this.getX() + d1 * 0.1D, this.getY() + 0.3D,
                                this.getZ() + d3 * 0.1D, 0, d1, d2, d3, 0.25F);
                    }
                }
                this.setSettingUpSecond(false);
                this.setSecondPhase(true);
            }
        } else {
            if (this.tickCount % 10 == 0) {
                this.prevVecPos = this.position();
            }
            if (this.getTarget() != null && !this.isCasting()) {
                if (this.tickCount % 40 == 0) {
                    if (this.getTarget().isVisuallyCrawling() && !this.getSensing().hasLineOfSight(this.getTarget())) {
                        FireBlastTrap fireBlastTrap = new FireBlastTrap(this.level(), this.getTarget().getX(),
                                this.getTarget().getY() + 0.25D, this.getTarget().getZ());
                        fireBlastTrap.setOwner(this);
                        fireBlastTrap.setAreaOfEffect(2.0F);
                        this.level().addFreshEntity(fireBlastTrap);
                    }
                }
                if (this.isInWall()
                        || (this.prevVecPos != null && this.prevVecPos.distanceTo(this.position()) <= 0.1D)) {
                    ++this.stuckTime;
                } else {
                    if (this.level().getBlockStates(this.getBoundingBox().inflate(1.0F))
                            .anyMatch(blockState1 -> blockState1.getBlock() instanceof MovingPistonBlock)) {
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
                        ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.LARGE_SMOKE, this);
                    }
                }
                if (this.stuckTime >= 100) {
                    this.escapeTeleport();
                    this.stuckTime = 0;
                }
            } else {
                this.stuckTime = 0;
            }
        }
        LivingEntity target = this.getTarget();
        if (this.getMainHandItem().isEmpty() && this.isAlive()) {
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
        }
        if (this.isSmited()) {
            --this.antiRegen;
        }
        if (this.getTornadoCoolDown() > 0) {
            --this.tornadoCoolDown;
        }
        if (this.getInfernoCoolDown() > 0) {
            --this.infernoCoolDown;
        }
        if (this.getMonolithCoolDown() > 0) {
            --this.monolithCoolDown;
        }
        if (this.getDamnedCoolDown() > 0) {
            --this.damnedCoolDown;
        }
        if (!this.isSmited()) {
            int count = this.isSecondPhase() ? 20 : 40;
            if (this.isInNether()) {
                if (this.Regen()) {
                    if (this.tickCount % (count / 2) == 0) {
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.heal(1.0F);
                        }
                    }
                } else {
                    if (this.tickCount % count == 0) {
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.heal(1.0F);
                        }
                    }
                }
            } else {
                if (this.Regen()) {
                    if (this.tickCount % count == 0) {
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.heal(1.0F);
                        }
                    }
                }
            }
            if (this.obsidianInvul > 0) {
                if (this.tickCount % (count * 2) == 0) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(1.0F);
                    }
                }
            }
            if (this.titleNumber == 0 && this.isApostleUpgraded()) {
                int monolithCount = this.level()
                        .getEntitiesOfClass(ObsidianMonolithServant.class, this.getBoundingBox().inflate(64.0D),
                                entity -> entity.getTrueOwner() == this)
                        .size();
                if (this.tickCount % 20 == 0 && monolithCount > 0 && this.getHealth() < this.getMaxHealth()) {
                    this.heal(monolithCount * 1.0F);
                }
            }
        }
        if (this.titleNumber == 10 && this.isApostleUpgraded()) {
            if (this.tickCount % 100 == 0) {
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(32.0D))) {
                    if (entity instanceof IOwned owned && owned.getTrueOwner() == this && entity != this) {
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 2, false, false),
                                this);
                    }
                }
            }
        }

        if (this.titleNumber == 6) {
            if (this.isApostleUpgraded()) {
                this.addEffect(new MobEffectInstance(GoetyEffects.FIERY_AURA.get(), 120, 0, false, false), this);
            }
        }
        if (this.titleNumber == 9) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1, false, false), this);
        }
        if (this.titleNumber == 10) {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 0, false, false), this);
            if (this.isApostleUpgraded()) {
                this.addEffect(new MobEffectInstance(GoetyEffects.IRON_HIDE.get(), 120, 4, false, false), this);
            }
        }

        if (this.isSecondPhase()) {
            if (this.tickCount % 100 == 0 && !this.isDeadOrDying()) {
                if (!this.level().isClientSide) {
                    ServerLevel ServerLevel = (ServerLevel) this.level();
                    if (Config.APOSTLE_SECOND_PHASE_THUNDER_STORM.get()) {
                        ServerLevel.setWeatherParameters(0, 6000, true, true);
                    }
                }
            }
            if (this.tickCount % 20 == 0) {
                BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(this.getRandomX(0.2), this.getY(),
                        this.getRandomZ(0.2));

                while (blockPos.getY() < this.getY() + 64.0D
                        && !this.level().getBlockState(blockPos).isSolidRender(this.level(), blockPos)) {
                    blockPos.move(Direction.UP);
                }
                if (blockPos.getY() > this.getY() + 32.0D) {
                    NetherMeteor fireball = this.getNetherMeteor();
                    fireball.setDangerous(
                            ForgeEventFactory.getMobGriefingEvent(this.level(), this)
                                    && MobsConfig.ApocalypseMode.get());
                    fireball.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    this.level().addFreshEntity(fireball);
                }
            }
        }
        if (!this.level().isClientSide) {
            if (this.getCoolDown() < this.coolDownLimit()) {
                ++this.coolDown;
            } else {
                if (!this.isSecondPhase() || this.getDamnedCoolDown() <= 0) {
                    this.setSpellCycle(0);
                } else {
                    this.setSpellCycle(1);
                }
            }
            if (this.getSpellCycle() == 1) {
                if (this.level().random.nextBoolean()) {
                    this.setSpellCycle(2);
                } else {
                    this.setSpellCycle(3);
                }
            }
        }
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(32))) {
            if (MobsConfig.ApostleBoilsWater.get()) {
                if (!(living instanceof Cultist) && !(living instanceof Witch)
                        && !(living instanceof IOwned && ((IOwned) living).getTrueOwner() == this)
                        && this.getTarget() == living) {
                    if (living.isInWater()) {
                        living.hurt(living.damageSources().hotFloor(), 1.0F);
                    }
                }
            }
            if (living instanceof Player player && this.getTarget() == player && !MobUtil.areAllies(this, player)) {
                player.getAbilities().flying &= player.isCreative();
            }
        }
        if (target == null) {
            if (this.getHitTimes() > 0) {
                this.resetHitTime();
            }
            boolean flag = true;
            for (Mob mob : this.level().getEntitiesOfClass(Mob.class,
                    this.getBoundingBox().inflate(this.getAttributeValue(Attributes.FOLLOW_RANGE)),
                    EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if (mob.getTarget() == this && this.getTarget() == null) {
                    this.setTarget(mob);
                }
            }
        } else {
            if (this.tickCount % 100 == 0 && !this.isSettingUpSecond()) {
                if (!this.level().isClientSide) {
                    if (this.isInNether()) {
                        for (ZombifiedPiglin zombifiedPiglin : this.level().getEntitiesOfClass(ZombifiedPiglin.class,
                                this.getBoundingBox().inflate(16))) {
                            if (zombifiedPiglin.getTarget() != this.getTarget()) {
                                zombifiedPiglin.setTarget(this.getTarget());
                            }
                        }
                    }
                }
            }
            if (this.isSecondPhase()) {
                if (this.getHealth() <= this.getMaxHealth() / 8) {
                    if (this.tickCount % 100 == 0) {
                        this.teleport();
                    }
                }
                if (MobUtil.isInRain(target)) {
                    int count = 100 * (this.random.nextInt(5) + 1);
                    if (this.tickCount % count == 0) {
                        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(target.getX(),
                                target.getY(), target.getZ());

                        while (blockpos$mutable.getY() > this.level().getMinBuildHeight()
                                && !this.level().getBlockState(blockpos$mutable).blocksMotion()) {
                            blockpos$mutable.move(Direction.DOWN);
                        }

                        LightningTrap lightningTrap = new LightningTrap(this.level(), blockpos$mutable.getX(),
                                blockpos$mutable.getY() + 1, blockpos$mutable.getZ());
                        lightningTrap.setOwner(this);
                        lightningTrap.setDuration(50);
                        this.level().addFreshEntity(lightningTrap);
                    }
                }
            }
            if ((target.distanceToSqr(this) > 1024 || !this.getSensing().hasLineOfSight(target)) && target.onGround()
                    && !this.isSettingUpSecond()) {
                this.teleportTowardstarget(target);
            }
        }
        if (this.isFiring()) {
            if (!this.isSettingUpSecond()) {
                FireBlastSpell spell = new FireBlastSpell();
                SpellStat stat = WandUtil.getStats(this, spell);
                spell.mobSpellResult(this, new ItemStack(ModItems.NETHER_STAFF.get()),
                        stat.setRadius(stat.getRadius() + 1.0D));
                if (this.teleportChance()) {
                    this.teleport();
                }
            }
            this.setFiring(false);
        }
        if (this.isInWater() || this.isInLava() || this.isInFluidType() || this.isInWall()) {
            this.teleport();
        }
        if (this.isInNether()) {
            if (target != null) {
                target.addEffect(new MobEffectInstance(GoetyEffects.BURN_HEX.get(), 100));
            }
        }

        if (target == null && this.tickCount % 100 == 0) {
            if (this.getTrueOwner() != null && this.isFollowing()) {
                if (this.getTrueOwner() instanceof Player) {
                    double distanceToOwner = this.distanceToSqr(this.getTrueOwner());
                    if (distanceToOwner > 1024) {
                        this.teleportTowardstarget(this.getTrueOwner());
                    }
                }
            }
        }
    }

    @NotNull
    private NetherMeteor getNetherMeteor() {
        int range = this.getHealth() < this.getMaxHealth() / 2 ? 450 : 900;
        int trueRange = this.getHealth() < this.getHealth() / 4 ? range / 2 : range;
        RandomSource random = this.level().random;
        double d = (random.nextBoolean() ? 1 : -1);
        double e = (random.nextBoolean() ? 1 : -1);
        double d2 = (random.nextInt(trueRange) * d);
        double d3 = -900.0D;
        double d4 = (random.nextInt(trueRange) * e);
        return new NetherMeteor(this.level(), this, d2, d3, d4);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSettingUpSecond();
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return !this.isSettingUpSecond() && super.hasLineOfSight(p_149755_);
    }

    private void barrier(Entity p_213688_1_, LivingEntity livingEntity) {
        double d0 = p_213688_1_.getX() - livingEntity.getX();
        double d1 = p_213688_1_.getZ() - livingEntity.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        MobUtil.forcePush(p_213688_1_, d0 / d2 * 2.0D, 0.1D, d1 / d2 * 2.0D);
    }

    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        ItemStack itemstack = this.getProjectile(
                this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        AbstractArrow abstractarrowentity = this.getArrow(itemstack,
                pDistanceFactor * AttributesConfig.ApostleBowDamage.get());
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrowentity = ((BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
        }
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.5D) - this.getY(0.5D);
        double d2 = pTarget.getZ() - this.getZ();
        float speed = this.isInNether() ? 3.2F : 2.4F;
        float accuracy = this.isInNether() ? 1.0F : 8.0F;
        abstractarrowentity.shoot(d0, d1, d2, speed, accuracy);
        this.playSound(ModSounds.APOSTLE_SHOOT.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrowentity);
        if (!this.level().isClientSide()) {
            this.consumeArrowFromOffhand(itemstack);
        }
    }

    private void consumeArrowFromOffhand(ItemStack originalArrow) {
        if (!originalArrow.isEmpty()) {
            ItemStack offhandStack = this.getOffhandItem();
            if (!offhandStack.isEmpty() && ItemStack.isSameItemSameTags(originalArrow, offhandStack)) {
                offhandStack.shrink(1);
                if (offhandStack.isEmpty()) {
                    this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
            }
        }
    }

    public AbstractArrow getArrow(ItemStack pArrowStack, float pDistanceFactor) {
        com.k1sak1.goetyawaken.common.entities.projectiles.DeathArrow deathArrow = new com.k1sak1.goetyawaken.common.entities.projectiles.DeathArrow(
                this.level(), this);
        deathArrow.setEffectsFromItem(pArrowStack);
        deathArrow.setEnchantmentEffectsFromEntity(this, pDistanceFactor);
        if (this.getArrowEffect() != null) {
            MobEffect mobEffect = this.getArrowEffect();
            int amp;
            if (this.isSecondPhase() && this.getArrowEffect() != MobEffects.HARM) {
                amp = 1;
            } else {
                amp = 0;
            }
            if (this.titleNumber == 1 && this.isApostleUpgraded()) {
                amp = 3;
            }

            if (this.getTarget() != null && this.getArrowEffect() == MobEffects.HARM
                    && this.getTarget().isInvertedHealAndHarm()) {
                mobEffect = MobEffects.HEAL;
            }
            if (this.isSecondPhase()) {
                if (this.getArrowEffect() == MobEffects.POISON) {
                    mobEffect = GoetyEffects.ACID_VENOM.get();
                }
                if (this.getArrowEffect() == MobEffects.DARKNESS) {
                    mobEffect = MobEffects.BLINDNESS;
                }
            }
            deathArrow.addEffect(new MobEffectInstance(mobEffect, mobEffect.isInstantenous() ? 1 : 200, amp));
        }
        if (this.getFireArrow()) {
            deathArrow.setRemainingFireTicks(100);
        }
        if (this.isInNether()) {
            deathArrow.setCritArrow(true);
        } else {
            float critChance = 0.05F;
            if (this.level().getDifficulty() == Difficulty.HARD) {
                critChance += 0.25F;
            }
            if (this.isSecondPhase()) {
                critChance += 0.1F;
            }
            if (this.isSecondPhase() && this.getHealth() <= this.getMaxHealth() / 4) {
                critChance += 0.25F;
            }
            if (this.level().random.nextFloat() <= critChance) {
                deathArrow.setCritArrow(true);
            }
        }
        return deathArrow;
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
        return p_230280_1_ instanceof BowItem;
    }

    public ItemStack getProjectile(ItemStack shootable) {
        if (shootable.getItem() instanceof ProjectileWeaponItem) {
            Predicate<ItemStack> predicate = ((ProjectileWeaponItem) shootable.getItem()).getSupportedHeldProjectiles();
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
            return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public boolean teleportChance() {
        return this.level().random.nextFloat() <= 0.25F;
    }

    public int spellStart() {
        return this.isSecondPhase() && this.getHealth() <= this.getMaxHealth() / 4 ? 12
                : this.isSecondPhase() ? 20 : 40;
    }

    public int coolDownLimit() {
        return this.isSecondPhase() && this.getHealth() <= this.getMaxHealth() / 4 ? 20
                : this.isSecondPhase() ? 25 : 50;
    }

    public void postSpellCast() {
        if (this.teleportChance()) {
            this.teleport();
        }
        this.resetCoolDown();
        this.setSpellCycle(0);
    }

    class CastingSpellGoal extends CastingASpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (ApostleServant.this.getTarget() != null) {
                ApostleServant.this.getLookControl().setLookAt(ApostleServant.this.getTarget(),
                        (float) ApostleServant.this.getMaxHeadYRot(),
                        (float) ApostleServant.this.getMaxHeadXRot());
            }
        }
    }

    abstract class CastingGoal extends UseSpellGoal {

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !ApostleServant.this.isSettingUpSecond();
        }

        public void start() {
            super.start();
            ApostleServant.this.setCasting(true);
        }

        public void stop() {
            super.stop();
            ApostleServant.this.setCasting(false);
        }

        @Override
        protected float castingVolume() {
            return 2.0F;
        }

        protected int getCastingInterval() {
            return 0;
        }

    }

    class FireballSpellGoal extends CastingGoal {
        private FireballSpellGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = ApostleServant.this.getTarget();
            if (!super.canUse()) {
                return false;
            } else if (livingentity == null) {
                return false;
            } else {
                return ApostleServant.this.getSpellCycle() == 0
                        && !ApostleServant.this.isSettingUpSecond()
                        && !ApostleServant.this.isSecondPhase()
                        && ApostleServant.this.getSensing().hasLineOfSight(livingentity);
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        public void castSpell() {
            LivingEntity livingentity = ApostleServant.this.getTarget();
            if (livingentity != null) {
                double d1 = livingentity.getX() - ApostleServant.this.getX();
                double d2 = livingentity.getY(0.5D) - ApostleServant.this.getY(0.5D);
                double d3 = livingentity.getZ() - ApostleServant.this.getZ();
                AbstractHurtingProjectile fireballEntity;
                if (ApostleServant.this.level().getDifficulty() != Difficulty.EASY) {
                    fireballEntity = new HellBlast(ApostleServant.this, d1, d2, d3, ApostleServant.this.level());
                } else {
                    fireballEntity = new Lavaball(ApostleServant.this.level(), ApostleServant.this, d1, d2, d3);
                }
                if (fireballEntity instanceof ExplosiveProjectile fireball) {
                    fireball.setDangerous(
                            ForgeEventFactory.getMobGriefingEvent(ApostleServant.this.level(), ApostleServant.this));
                }
                fireballEntity.setPos(fireballEntity.getX(), ApostleServant.this.getY(0.5), fireballEntity.getZ());
                ApostleServant.this.level().addFreshEntity(fireballEntity);
                if (!ApostleServant.this.isSilent()) {
                    ApostleServant.this.level().levelEvent(null, 1016, ApostleServant.this.blockPosition(), 0);
                }
                if (ApostleServant.this.teleportChance()) {
                    ApostleServant.this.teleport();
                }
                ApostleServant.this.resetCoolDown();
                ApostleServant.this.setSpellCycle(1);
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SPELL.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.FIRE;
        }
    }

    class DamnedSpellGoal extends CastingGoal {

        private DamnedSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int j = ApostleServant.this.level()
                    .getEntitiesOfClass(Owned.class, ApostleServant.this.getBoundingBox().inflate(64.0D), MALGHASTS)
                    .size();
            int h = ApostleServant.this.level()
                    .getEntitiesOfClass(Damned.class, ApostleServant.this.getBoundingBox().inflate(64.0D))
                    .size();
            LivingEntity livingentity = ApostleServant.this.getTarget();
            if (!super.canUse()) {
                return false;
            } else if (livingentity == null) {
                return false;
            } else {
                int cool = ApostleServant.this.spellStart();
                return ApostleServant.this.getCoolDown() >= cool
                        && ApostleServant.this.getDamnedCoolDown() <= 0
                        && ApostleServant.this.getSpellCycle() == 0
                        && !ApostleServant.this.isSettingUpSecond()
                        && ApostleServant.this.isSecondPhase()
                        && ApostleServant.this.getSensing().hasLineOfSight(livingentity)
                        && j < 2 && h < 1;
            }
        }

        protected int getCastingTime() {
            return 30;
        }

        public void tick() {
            --this.spellWarmup;
            if (ApostleServant.this.level() instanceof ServerLevel serverLevel) {
                LivingEntity livingentity = ApostleServant.this.getTarget();
                int time = ApostleServant.this.isInNether() ? 7 : 10;
                if (this.spellWarmup % time == 0) {
                    Damned damned = new Damned(ModEntityType.DAMNED.get(), ApostleServant.this.level());
                    BlockPos blockPos0 = ApostleServant.this.blockPosition().offset(
                            serverLevel.random.nextIntBetweenInclusive(-3, 3), 0,
                            serverLevel.random.nextIntBetweenInclusive(-3, 3));
                    BlockPos blockPos = BlockFinder.SummonPosition(ApostleServant.this, blockPos0);
                    damned.moveTo(blockPos.below(2), ApostleServant.this.getYHeadRot(), ApostleServant.this.getXRot());
                    damned.setTrueOwner(ApostleServant.this);
                    damned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPos.below(2)),
                            MobSpawnType.MOB_SUMMONED, null, null);
                    if (livingentity != null) {
                        damned.setTarget(livingentity);
                    }
                    damned.setLimitedLife(100);
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), damned);
                    ApostleServant.this.level().addFreshEntity(damned);
                }
            }
            if (this.spellWarmup <= 0) {
                ApostleServant.this.resetCoolDown();
                ApostleServant.this.setSpellCycle(1);
                ApostleServant.this.setDamnedCoolDown(MathHelper.secondsToTicks(10));
                ApostleServant.this.setSpellType(SpellType.NONE);
            }
        }

        public void castSpell() {
        }

        protected int getCastWarmupTime() {
            return 30;
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.FIRE;
        }
    }

    class MonolithSpellGoal extends CastingGoal {
        private MonolithSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int j = ApostleServant.this.level()
                    .getEntitiesOfClass(Owned.class, ApostleServant.this.getBoundingBox().inflate(64.0D), MONOLITHS)
                    .size();
            if (!super.canUse()) {
                return false;
            } else {
                int cool = ApostleServant.this.spellStart();
                return ApostleServant.this.getCoolDown() >= cool
                        && ApostleServant.this.getSpellCycle() == 2
                        && ApostleServant.this.getMonolithCoolDown() <= 0
                        && !ApostleServant.this.isSettingUpSecond()
                        && j < 4;
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        public void castSpell() {
            if (!ApostleServant.this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) ApostleServant.this.level();
                LivingEntity livingentity = ApostleServant.this.getTarget();
                RandomSource randomSource = serverLevel.random;
                if (livingentity != null) {
                    int p0 = ApostleServant.this.isSecondPhase() ? randomSource.nextInt(2) + 1 : 1;
                    for (int p = 0; p < p0; ++p) {
                        int k = (12 + randomSource.nextInt(12)) * (randomSource.nextBoolean() ? -1 : 1);
                        int l = (12 + randomSource.nextInt(12)) * (randomSource.nextBoolean() ? -1 : 1);
                        BlockPos.MutableBlockPos blockpos$mutable = ApostleServant.this.blockPosition().mutable().move(
                                k, 0,
                                l);
                        ObsidianMonolithServant summonedentity = new ObsidianMonolithServant(
                                com.k1sak1.goetyawaken.common.entities.ModEntityType.OBSIDIAN_MONOLITH_SERVANT.get(),
                                serverLevel);
                        BlockPos blockPos = BlockFinder.SummonRadiusSight(blockpos$mutable, ApostleServant.this,
                                summonedentity, serverLevel, 5);
                        summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                        summonedentity.setTrueOwner(ApostleServant.this);
                        summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockpos$mutable),
                                MobSpawnType.MOB_SUMMONED, null, null);
                        serverLevel.addFreshEntity(summonedentity);
                        ApostleServant.this.postSpellCast();
                    }
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.ZOMBIE;
        }
    }

    class FireRainSpellGoal extends CastingGoal {
        private FireRainSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int i2 = ApostleServant.this.level()
                    .getEntitiesOfClass(SpellEntity.class, ApostleServant.this.getBoundingBox().inflate(64.0D),
                            OWNED_TRAPS)
                    .size();
            if (!super.canUse()) {
                return false;
            } else {
                int cool = ApostleServant.this.spellStart();
                return ApostleServant.this.getCoolDown() >= cool
                        && ApostleServant.this.getSpellCycle() == 2
                        && !ApostleServant.this.isSettingUpSecond()
                        && i2 < 2;
            }
        }

        protected int getCastingTime() {
            return 20;
        }

        public void castSpell() {
            if (!ApostleServant.this.level().isClientSide) {
                LivingEntity livingentity = ApostleServant.this.getTarget();
                if (livingentity != null) {
                    HellCloud hellCloud = new HellCloud(ApostleServant.this.level(), ApostleServant.this, livingentity);
                    if (ApostleServant.this.isSecondPhase()) {
                        hellCloud.setRadius(4.0F);
                    } else {
                        hellCloud.setRadius(2.0F);
                    }
                    hellCloud.setLifeSpan(1200);
                    ApostleServant.this.level().addFreshEntity(hellCloud);
                    ApostleServant.this.postSpellCast();
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.CLOUD;
        }
    }

    class RangedSummonSpellGoal extends CastingGoal {
        private RangedSummonSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int i = ApostleServant.this.level()
                    .getEntitiesOfClass(Owned.class, ApostleServant.this.getBoundingBox().inflate(64.0D),
                            RANGED_MINIONS)
                    .size();
            if (!super.canUse()) {
                return false;
            } else {
                int cool = ApostleServant.this.spellStart();
                return ApostleServant.this.getCoolDown() >= cool
                        && ApostleServant.this.getSpellCycle() == 3
                        && !ApostleServant.this.isSettingUpSecond()
                        && ApostleServant.this.getInfernoCoolDown() <= 0
                        && i < 2;
            }
        }

        protected int getCastingTime() {
            return 60;
        }

        public void castSpell() {
            if (!ApostleServant.this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) ApostleServant.this.level();
                LivingEntity livingentity = ApostleServant.this.getTarget();
                RandomSource r = ApostleServant.this.random;
                if (livingentity != null) {
                    if (r.nextBoolean()) {
                        if (ApostleServant.this.isSecondPhase() && r.nextFloat() <= 0.25F) {
                            int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            BlockPos.MutableBlockPos blockpos$mutable = ApostleServant.this.blockPosition().mutable()
                                    .move(k,
                                            0, l);
                            blockpos$mutable.setX(blockpos$mutable.getX() + r.nextInt(5) - r.nextInt(5));
                            blockpos$mutable.setY((int) BlockFinder.moveDownToGround(ApostleServant.this));
                            blockpos$mutable.setZ(blockpos$mutable.getZ() + r.nextInt(5) - r.nextInt(5));
                            Malghast summonedentity = new Malghast(ModEntityType.MALGHAST.get(),
                                    ApostleServant.this.level());
                            if (serverLevel.noCollision(summonedentity,
                                    summonedentity.getBoundingBox().move(blockpos$mutable.above(2)).inflate(0.25F))) {
                                summonedentity.setPos(blockpos$mutable.getX(), blockpos$mutable.getY() + 2,
                                        blockpos$mutable.getZ());
                            } else {
                                blockpos$mutable = ApostleServant.this.blockPosition().mutable();
                                summonedentity.setPos(blockpos$mutable.getX(), blockpos$mutable.getY() + 2,
                                        blockpos$mutable.getZ());
                            }
                            summonedentity.setTrueOwner(ApostleServant.this);
                            summonedentity.setLimitedLife(60 * (90 + ApostleServant.this.level().random.nextInt(180)));
                            SummonCircle summonCircle = new SummonCircle(ApostleServant.this.level(), blockpos$mutable,
                                    summonedentity, true, false, ApostleServant.this);
                            ApostleServant.this.level().addFreshEntity(summonCircle);
                        } else {
                            BlockPos blockpos = ApostleServant.this.blockPosition();
                            Inferno summonedentity = new Inferno(ModEntityType.INFERNO.get(),
                                    ApostleServant.this.level());
                            summonedentity.moveTo(blockpos, 0.0F, 0.0F);
                            summonedentity.setTrueOwner(ApostleServant.this);
                            summonedentity.setLimitedLife(60 * (90 + ApostleServant.this.level().random.nextInt(180)));
                            summonedentity.setUpgraded(true);
                            SummonCircle summonCircle = new SummonCircle(ApostleServant.this.level(), blockpos,
                                    summonedentity,
                                    true, true, ApostleServant.this);
                            ApostleServant.this.level().addFreshEntity(summonCircle);
                            ApostleServant.this.setInfernoCoolDown(MathHelper.secondsToTicks(45));
                        }
                    } else {
                        int p0 = ApostleServant.this.isInNether() ? 2 : 1;
                        for (int p = 0; p < p0 + r.nextInt(1 + p0); ++p) {
                            int k = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            int l = (12 + r.nextInt(12)) * (r.nextBoolean() ? -1 : 1);
                            Inferno summonedentity = new Inferno(ModEntityType.INFERNO.get(),
                                    ApostleServant.this.level());
                            BlockPos.MutableBlockPos blockpos$mutable = ApostleServant.this.blockPosition().mutable()
                                    .move(k,
                                            0, l);
                            BlockPos blockPos = BlockFinder.SummonRadius(blockpos$mutable, summonedentity,
                                    ApostleServant.this.level(), 5);
                            summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                            summonedentity.setTrueOwner(ApostleServant.this);
                            summonedentity.setLimitedLife(60 * (90 + ApostleServant.this.level().random.nextInt(180)));
                            SummonCircle summonCircle = new SummonCircle(ApostleServant.this.level(), blockpos$mutable,
                                    summonedentity, true, true, ApostleServant.this);
                            ApostleServant.this.level().addFreshEntity(summonCircle);
                        }
                        ApostleServant.this.setInfernoCoolDown(MathHelper.secondsToTicks(45));
                    }
                    if (Config.apostleSummonArmoredZombiePiglin) {
                        ApostleServant.this.summonArmoredZombiePiglin(livingentity);
                        if (ApostleServant.this.isInNether()) {
                            int extraSummons = 1 + r.nextInt(3);
                            for (int i = 0; i < extraSummons; i++) {
                                ApostleServant.this.summonArmoredZombiePiglin(livingentity);
                            }
                        }
                    }

                    ApostleServant.this.postSpellCast();
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.RANGED;
        }
    }

    class FireTornadoSpellGoal extends CastingGoal {
        private FireTornadoSpellGoal() {
        }

        @Override
        public boolean canUse() {
            int i = ApostleServant.this.level()
                    .getEntitiesOfClass(Owned.class, ApostleServant.this.getBoundingBox().inflate(64.0D),
                            RANGED_MINIONS)
                    .size();
            int i2 = ApostleServant.this.level()
                    .getEntitiesOfClass(Entity.class, ApostleServant.this.getBoundingBox().inflate(64.0D),
                            entity -> entity instanceof FireTornado || entity instanceof FireTornadoTrap)
                    .size();
            int cool = ApostleServant.this.spellStart();
            if (!super.canUse()) {
                return false;
            } else if (ApostleServant.this.isSettingUpSecond()) {
                return false;
            } else if (ApostleServant.this.getHitTimes() >= 6) {
                return i2 < 1;
            } else {
                return ApostleServant.this.getCoolDown() >= cool
                        && ApostleServant.this.getTornadoCoolDown() <= 0
                        && ApostleServant.this.getSpellCycle() == 3
                        && i >= 2
                        && i2 < 1;
            }
        }

        protected int getCastingTime() {
            return 60;
        }

        public void castSpell() {
            if (!ApostleServant.this.level().isClientSide) {
                LivingEntity livingentity = ApostleServant.this.getTarget();
                if (livingentity != null) {
                    BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(livingentity.getX(),
                            livingentity.getY(), livingentity.getZ());

                    while (blockpos$mutable.getY() > ApostleServant.this.level().getMinBuildHeight()
                            && !ApostleServant.this.level().getBlockState(blockpos$mutable).blocksMotion()) {
                        blockpos$mutable.move(Direction.DOWN);
                    }

                    FireTornadoTrap fireTornadoTrapEntity = new FireTornadoTrap(ModEntityType.FIRE_TORNADO_TRAP.get(),
                            ApostleServant.this.level());
                    fireTornadoTrapEntity.setPos(blockpos$mutable.getX(), blockpos$mutable.getY() + 1,
                            blockpos$mutable.getZ());
                    fireTornadoTrapEntity.setOwner(ApostleServant.this);
                    fireTornadoTrapEntity.setDuration(60);
                    ApostleServant.this.level().addFreshEntity(fireTornadoTrapEntity);
                    ApostleServant.this.postSpellCast();
                }
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SUMMON.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.TORNADO;
        }
    }

    class RoarSpellGoal extends CastingGoal {
        private RoarSpellGoal() {
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else if (ApostleServant.this.getTarget() == null) {
                return false;
            } else
                return ApostleServant.this.distanceTo(ApostleServant.this.getTarget()) < 4.0F
                        && !ApostleServant.this.isSettingUpSecond();
        }

        protected int getCastingTime() {
            return 200;
        }

        protected int getCastingInterval() {
            return 120;
        }

        public void castSpell() {
            ApostleServant ApostleServant = ApostleServant.this;
            ApostleServant.resetHitTime();
            if (!ApostleServant.isSecondPhase()) {
                ApostleServant.setFiring(true);
                ApostleServant.coolDown = 0;
            } else {
                FireBlastTrap fireBlastTrap = new FireBlastTrap(ApostleServant.level(), ApostleServant.getX(),
                        ApostleServant.getY() + 0.25D,
                        ApostleServant.getZ());
                fireBlastTrap.setOwner(ApostleServant);
                if (ApostleServant.getHealth() < ApostleServant.getMaxHealth() / 2) {
                    fireBlastTrap.setAreaOfEffect(6.0F);
                } else if (ApostleServant.getHealth() < ApostleServant.getMaxHealth() / 4) {
                    fireBlastTrap.setAreaOfEffect(4.5F);
                } else {
                    fireBlastTrap.setAreaOfEffect(3.0F);
                }
                ApostleServant.level().addFreshEntity(fireBlastTrap);
            }
        }

        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.APOSTLE_PREPARE_SPELL.get();
        }

        @Override
        protected SpellType getSpellType() {
            return SpellType.ROAR;
        }
    }

    class SecondPhaseIndicator extends Goal {
        private SecondPhaseIndicator() {
        }

        @Override
        public boolean canUse() {
            return ApostleServant.this.getHealth() <= ApostleServant.this.getMaxHealth() / 2
                    && ApostleServant.this.getTarget() != null
                    && !ApostleServant.this.isSecondPhase()
                    && !ApostleServant.this.isSettingUpSecond();
        }

        @Override
        public void tick() {
            ApostleServant.this.setSettingUpSecond(true);
        }
    }

    class StrafeCastGoal<T extends ApostleServant> extends SurroundGoal<T> {

        public StrafeCastGoal(T p_25792_) {
            super(p_25792_, 1.0F, 32.0F);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && ApostleServant.this.isCasting() && ApostleServant.this.isInNether();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && ApostleServant.this.isCasting() && ApostleServant.this.isInNether();
        }
    }

    static class ApostleServantBowGoal<T extends ApostleServant> extends Goal {
        private final T mob;
        private final float attackRadiusSqr;
        private int attackTime = -1;
        private int seeTime;
        private boolean strafingClockwise;
        private boolean strafingBackwards;
        private int strafingTime = -1;

        public ApostleServantBowGoal(T p_25792_, float p_25795_) {
            this.mob = p_25792_;
            this.attackRadiusSqr = p_25795_ * p_25795_;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null && this.HaveBow() && !this.mob.isSettingUpSecond();
        }

        public boolean canContinueToUse() {
            return (this.canUse() || !this.mob.getNavigation().isDone()) && this.HaveBow() && !this.mob.isCasting();
        }

        public void start() {
            super.start();
            this.mob.setAggressive(true);
        }

        public void stop() {
            super.stop();
            this.mob.setAggressive(false);
            this.seeTime = 0;
            this.attackTime = -1;
            this.mob.stopUsingItem();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null) {
                double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
                boolean flag1 = this.seeTime > 0;
                if (flag != flag1) {
                    this.seeTime = 0;
                }

                if (flag) {
                    ++this.seeTime;
                } else {
                    --this.seeTime;
                }

                if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 20) {
                    this.mob.getNavigation().stop();
                    ++this.strafingTime;
                } else {
                    this.mob.getNavigation().moveTo(livingentity, 1.0F);
                    this.strafingTime = -1;
                }

                if (this.strafingTime >= 20) {
                    if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }

                    if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }

                    this.strafingTime = 0;
                }

                if (this.strafingTime > -1) {
                    if (d0 > (double) (this.attackRadiusSqr * 0.75F)) {
                        this.strafingBackwards = false;
                    } else if (d0 < (double) (this.attackRadiusSqr * 0.25F)) {
                        this.strafingBackwards = true;
                    }

                    this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F,
                            this.strafingClockwise ? 0.5F : -0.5F);
                    this.mob.lookAt(livingentity, 30.0F, 30.0F);
                } else {
                    this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
                }

                if (this.mob.isUsingItem()) {
                    if (!flag && this.seeTime < -60) {
                        this.mob.stopUsingItem();
                    } else if (flag) {
                        int i = this.mob.getTicksUsingItem();
                        int bowChargeTime = 20;
                        if (this.mob.getTitleNumber() == 9 && this.mob.isApostleUpgraded()) {
                            bowChargeTime = 5;
                        }
                        if (i >= bowChargeTime) {
                            this.mob.stopUsingItem();
                            this.mob.performRangedAttack(livingentity, BowItem.getPowerForTime(30));
                            Difficulty difficulty = this.mob.level().getDifficulty();
                            int attackIntervalMin = difficulty != Difficulty.HARD ? 20 : 10;
                            if (this.mob.monolithWeakened()) {
                                attackIntervalMin = 40;
                            } else if (this.mob.isInNether()) {
                                attackIntervalMin = 5;
                            }
                            if (this.mob.getTitleNumber() == 9 && this.mob.isApostleUpgraded()) {
                                attackIntervalMin /= 2;
                            }

                            this.attackTime = attackIntervalMin;
                        }
                    }
                } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
                    this.mob.startUsingItem(
                            ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof BowItem));
                }

            }
        }

        protected boolean HaveBow() {
            return this.mob.isHolding(item -> item.getItem() instanceof BowItem);
        }
    }

    static class ApostleServantPathNavigation extends GroundPathNavigation {
        ApostleServantPathNavigation(ApostleServant p_33969_, Level p_33970_) {
            super(p_33969_, p_33970_);
        }

        protected PathFinder createPathFinder(int p_33972_) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, p_33972_);
        }

        protected boolean hasValidPathType(BlockPathTypes p_33974_) {
            return p_33974_ == BlockPathTypes.LAVA || p_33974_ == BlockPathTypes.DAMAGE_FIRE
                    || p_33974_ == BlockPathTypes.DANGER_FIRE || super.hasValidPathType(p_33974_);
        }

        public boolean isStableDestination(BlockPos p_33976_) {
            return this.level.getBlockState(p_33976_).is(Blocks.LAVA) || super.isStableDestination(p_33976_);
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return Config.apostleServantLimit;
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
    public boolean canBeLeader() {
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
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
            } else if (itemstack.is(ModItems.UNHOLY_BLOOD.get())) {
                if (this.isSecondPhase()) {
                    this.setSecondPhase(false);
                }
                float healAmount = this.getMaxHealth() * 0.5F;
                this.heal(healAmount);
                this.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0D),
                            this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else if (itemstack.canEquip(EquipmentSlot.CHEST, this)) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                ItemStack currentChestItem = this.getItemBySlot(EquipmentSlot.CHEST);
                this.setItemSlot(EquipmentSlot.CHEST, itemstack.copy());
                this.dropEquipment(EquipmentSlot.CHEST, currentChestItem);
                this.setGuaranteedDrop(EquipmentSlot.CHEST);
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
            } else if (itemstack.canEquip(EquipmentSlot.HEAD, this)) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                ItemStack currentHeadItem = this.getItemBySlot(EquipmentSlot.HEAD);
                this.setItemSlot(EquipmentSlot.HEAD, itemstack.copy());
                this.dropEquipment(EquipmentSlot.HEAD, currentHeadItem);
                this.setGuaranteedDrop(EquipmentSlot.HEAD);
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
            } else if (itemstack.canEquip(EquipmentSlot.LEGS, this)) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                ItemStack currentLegsItem = this.getItemBySlot(EquipmentSlot.LEGS);
                this.setItemSlot(EquipmentSlot.LEGS, itemstack.copy());
                this.dropEquipment(EquipmentSlot.LEGS, currentLegsItem);
                this.setGuaranteedDrop(EquipmentSlot.LEGS);
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
            } else if (itemstack.canEquip(EquipmentSlot.FEET, this)) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                ItemStack currentFeetItem = this.getItemBySlot(EquipmentSlot.FEET);
                this.setItemSlot(EquipmentSlot.FEET, itemstack.copy());
                this.dropEquipment(EquipmentSlot.FEET, currentFeetItem);
                this.setGuaranteedDrop(EquipmentSlot.FEET);
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
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }
}
