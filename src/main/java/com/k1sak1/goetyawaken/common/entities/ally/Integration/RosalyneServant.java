package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import lykrast.meetyourfight.MeetYourFight;
import lykrast.meetyourfight.registry.MYFSounds;
import lykrast.meetyourfight.registry.MYFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class RosalyneServant extends Summoned implements PowerableMob {
	public static final int HP = 500, DMG = 24;
	private static final EntityDataAccessor<Byte> STATUS = SynchedEntityData.defineId(RosalyneServant.class,
			EntityDataSerializers.BYTE);
	public static final int ENCASED = 0, BREAKING_OUT = 1, PHASE_1 = 2, SUMMONING = 3, PHASE_2 = 4, MADDENING = 5,
			PHASE_3 = 6;
	public static final int ANIM_NEUTRAL = 0, ANIM_ARM_OUT_UP = 1, ANIM_ARM_IN_UP = 2, ANIM_ARM_OUT_DN = 3,
			ANIM_ARM_IN_DN = 4, ANIM_PREPARE_DASH = 5,
			ANIM_BROKE_OUT = 6, ANIM_SUMMONING = 7, ANIM_MADDENING = 8, ANIM_PREPARE_CRASH = 9, ANIM_SWING_CRASH = 10;
	private static final int PHASE_MASK = 0b111, ANIMATION_MASK = ~PHASE_MASK;
	private final TargetingConditions spiritCountTargeting = TargetingConditions.forNonCombat().range(32)
			.ignoreLineOfSight().ignoreInvisibilityTesting();

	public int attackCooldown;
	private int phase;
	private int nextAttack;
	private boolean spiritsCreated;

	public int clientAnim, prevAnim, animProg, animDur;

	public RosalyneServant(EntityType<? extends RosalyneServant> type, Level worldIn) {
		super(type, worldIn);
		moveControl = new VexMovementController(this).slowdown(0.1);
		phase = 0;
		clientAnim = ANIM_NEUTRAL;
		prevAnim = ANIM_NEUTRAL;
		animProg = 1;
		animDur = 1;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new PhaseTransition(this));
		goalSelector.addGoal(2, new AdvanceAndSwingAttack(this));
		goalSelector.addGoal(2, new CircleAndDashAttack(this));
		goalSelector.addGoal(2, new VerticalCrashAttack(this));
		goalSelector.addGoal(7, new MoveFrontOfTarget(this, 0.5));
		goalSelector.addGoal(8, new VexMoveRandomGoal(this, 0.25));
		targetSelector.addGoal(2, new HurtByTargetGoal(this));
	}

	@Override
	public void followGoal() {
		this.goalSelector.addGoal(6, new RosalyneFollowGoal(this, 1.0D, 2.0F, 10.0F, true));
	}

	public static AttributeSupplier.Builder setCustomAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, AttributesConfig.RosalyneServantHealth.get())
				.add(Attributes.ATTACK_DAMAGE, AttributesConfig.RosalyneServantDamage.get())
				.add(Attributes.ARMOR, AttributesConfig.RosalyneServantArmor.get())
				.add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.RosalyneServantArmorToughness.get())
				.add(Attributes.KNOCKBACK_RESISTANCE, 1)
				.add(Attributes.FOLLOW_RANGE, 64);
	}

	@Override
	public void setConfigurableAttributes() {
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
				AttributesConfig.RosalyneServantHealth.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
				AttributesConfig.RosalyneServantDamage.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
				AttributesConfig.RosalyneServantArmor.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
				AttributesConfig.RosalyneServantArmorToughness.get());
	}

	@Override
	public int getSummonLimit(LivingEntity owner) {
		return com.k1sak1.goetyawaken.Config.ROSALYNE_SERVANT_LIMIT.get();
	}

	public static AttributeSupplier.Builder createAttributes() {
		return setCustomAttributes();
	}

	@Override
	public void move(MoverType typeIn, Vec3 pos) {
		super.move(typeIn, pos);
		checkInsideBlocks();
	}

	@Override
	public void tick() {
		noPhysics = true;
		super.tick();
		noPhysics = false;
		setNoGravity(true);

		if (!level().isClientSide() && !spiritsCreated && tickCount > 0) {
			spiritsCreated = true;
			createSpirits();
		}

		if (level().isClientSide()) {
			int newanim = getAnimation();
			if (clientAnim != newanim) {
				prevAnim = clientAnim;
				clientAnim = newanim;
				animProg = 0;
				animDur = 5;
				if (prevAnim == ANIM_BROKE_OUT)
					animDur = 60;
				else if (clientAnim == ANIM_SUMMONING)
					animDur = 10;
				else if (prevAnim == ANIM_SUMMONING)
					animDur = 20;
			} else if (animProg < animDur)
				animProg++;
		}
	}

	public float getAnimProgress(float partial) {
		return Mth.clamp((animProg + partial) / animDur, 0, 1);
	}

	@SuppressWarnings("deprecation")
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
			MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		this.setConfigurableAttributes();
		setHealth(getMaxHealth());
		this.attackCooldown = 100;
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	private void createSpirits() {
		for (int i = 0; i < 4; i++) {
			RoseSpiritServant spirit = com.k1sak1.goetyawaken.common.ModIntegrationRegistry.ROSE_SPIRIT_SERVANT.get()
					.create(level());
			spirit.moveTo(getX() + (i / 2) * 4 - 2, getY(), getZ() + (i % 2) * 4 - 2);
			spirit.setTrueOwner(this);
			if (getTarget() != null)
				spirit.setTarget(getTarget());
			spirit.attackCooldown = 80 + 60 * i;
			ForgeEventFactory.onFinalizeSpawn(spirit, (ServerLevel) level(),
					level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
			level().addFreshEntity(spirit);
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && getPhase() != PHASE_1 && getPhase() != PHASE_3) {
			if (amount > 1)
				playSound(MYFSounds.aceOfIronProc.get(), 1, 1);
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(STATUS, (byte) 0);
	}

	public int getPhase() {
		return entityData.get(STATUS) & PHASE_MASK;
	}

	public void setPhase(int phase) {
		int anim = entityData.get(STATUS) & ANIMATION_MASK;
		entityData.set(STATUS, (byte) (anim | phase));
	}

	public int getAnimation() {
		return (entityData.get(STATUS) & ANIMATION_MASK) >> 3;
	}

	public void setAnimation(int anim) {
		int phase = entityData.get(STATUS) & PHASE_MASK;
		entityData.set(STATUS, (byte) ((anim << 3) | phase));
	}

	@Override
	public boolean isPowered() {
		int phase = getPhase();
		return phase == SUMMONING || phase == PHASE_2 || phase == MADDENING;
	}

	public void swing() {
		for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class,
				getBoundingBox().inflate(2, 0.2, 2))) {
			if (!(MobUtil.areAllies(this, entity)) && entity.isAlive())
				doHurtTarget(entity);
		}
		playSound(MYFSounds.rosalyneSwing.get(), 1, 1);
		switch (getAnimation()) {
			case ANIM_PREPARE_CRASH:
				setAnimation(ANIM_SWING_CRASH);
				break;
			case ANIM_ARM_OUT_DN:
				setAnimation(ANIM_ARM_IN_UP);
				break;
			case ANIM_ARM_IN_UP:
				setAnimation(ANIM_ARM_OUT_UP);
				break;
			case ANIM_ARM_OUT_UP:
				setAnimation(ANIM_ARM_IN_DN);
				break;
			default:
				setAnimation(ANIM_ARM_OUT_DN);
				break;
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if ((phase == ENCASED || phase == PHASE_2) && tickCount % 20 == 0) {
			List<RoseSpiritServant> list = level().getNearbyEntities(RoseSpiritServant.class, spiritCountTargeting,
					this,
					getBoundingBox().inflate(32));
			int ownedCount = 0;
			for (RoseSpiritServant spirit : list) {
				if (spirit.getTrueOwner() == this) {
					ownedCount++;
				} else if (spirit.getTrueOwner() == null) {
					spirit.setTrueOwner(this);
					ownedCount++;
				}
			}
			if (ownedCount > 0 && !isRemoved())
				setHealth(getHealth() + 1);
		}
	}

	@Override
	public void customServerAiStep() {
		if (attackCooldown > 0)
			attackCooldown--;
		if (phase != getPhase())
			phase = getPhase();
		if ((phase == ENCASED || phase == PHASE_2) && tickCount % 10 == 0) {
			List<RoseSpiritServant> nearbySpirits = level()
					.getNearbyEntities(RoseSpiritServant.class, spiritCountTargeting, this,
							getBoundingBox().inflate(32));
			boolean hasOwnSpirits = false;
			for (RoseSpiritServant spirit : nearbySpirits) {
				if (spirit.getTrueOwner() == this) {
					hasOwnSpirits = true;
					break;
				}
			}
			if (!hasOwnSpirits) {
				if (phase == ENCASED) {
					setPhase(BREAKING_OUT);
					phase = BREAKING_OUT;
				} else if (phase == PHASE_2) {
					setPhase(MADDENING);
					phase = MADDENING;
				}
			}
		} else if (phase == PHASE_1 && getHealth() < getMaxHealth() / 2) {
			setPhase(SUMMONING);
			phase = SUMMONING;
		}
		super.customServerAiStep();
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Cooldown"))
			attackCooldown = compound.getInt("Cooldown");
		if (compound.contains("Phase"))
			setPhase(compound.getInt("Phase"));
		if (compound.contains("NxtAt"))
			nextAttack = compound.getInt("NxtAt");
		spiritsCreated = compound.getBoolean("SpiritsCreated");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Cooldown", attackCooldown);
		compound.putInt("Phase", getPhase());
		compound.putInt("NxtAt", nextAttack);
		compound.putBoolean("SpiritsCreated", spiritsCreated);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return MYFSounds.rosalyneHurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MYFSounds.rosalyneDeath.get();
	}

	@Override
	public void die(DamageSource cause) {
		if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
			ItemStack violetBloom = new ItemStack(MYFItems.violetBloom.get());
			if (this.getTrueOwner() != null) {
				FlyingItem flyingItem = new FlyingItem(
						com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
						this.level(),
						this.getX(),
						this.getY() + 1.0D,
						this.getZ());
				flyingItem.setOwner(this.getTrueOwner());
				flyingItem.setItem(violetBloom);
				flyingItem.setParticle(ParticleTypes.ENCHANT);
				flyingItem.setSecondsCool(30);
				this.level().addFreshEntity(flyingItem);
			} else {
				ItemEntity itemEntity = this.spawnAtLocation(violetBloom);
				if (itemEntity != null) {
					itemEntity.setExtendedLifetime();
				}
			}
		}
		super.die(cause);
	}

	@Override
	public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
			EquipmentSlot slot = findEquipSlot(itemstack);
			if (slot != null) {
				this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
				ItemStack currentItem = this.getItemBySlot(slot);
				this.setItemSlot(slot, itemstack.copy());
				this.dropEquipment(slot, currentItem);
				this.setGuaranteedDrop(slot);
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

	@Nullable
	private EquipmentSlot findEquipSlot(ItemStack stack) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
				continue;
			}
			if (stack.canEquip(slot, this)) {
				return slot;
			}
		}
		return null;
	}

	private void rollNextAttack(int ignore) {
		if (ignore >= 0) {
			nextAttack = random.nextInt(2);
			if (nextAttack >= ignore)
				nextAttack++;
		} else
			nextAttack = random.nextInt(3);
	}

	private void cooldownNextAttack() {
		attackCooldown = 60 + random.nextInt(21);
		if (phase == PHASE_2)
			attackCooldown += 20;
		else if (phase == PHASE_3)
			attackCooldown -= 40;
	}

	private class PhaseTransition extends StationaryAttack {
		private RosalyneServant rosalyne;
		private int timer;
		private static final int DURATION = 80;

		public PhaseTransition(RosalyneServant rosalyne) {
			super(rosalyne);
			this.rosalyne = rosalyne;
		}

		@Override
		public void start() {
			super.start();
			timer = 80;
		}

		@Override
		public void tick() {
			super.tick();
			timer--;
			if (timer == DURATION - 10) {
				switch (rosalyne.phase) {
					case BREAKING_OUT:
						rosalyne.setAnimation(ANIM_BROKE_OUT);
						break;
					case SUMMONING:
						rosalyne.setAnimation(ANIM_SUMMONING);
						break;
					case MADDENING:
						rosalyne.setAnimation(ANIM_MADDENING);
						break;
				}
			}
			if (timer <= DURATION - 30 && timer % 20 == 10) {
				switch (rosalyne.phase) {
					case BREAKING_OUT:
						rosalyne.playSound(SoundEvents.STONE_BREAK);
						break;
					case MADDENING:
						rosalyne.playSound(MYFSounds.rosalyneCrack.get());
						break;
				}
			}
			if (timer <= 0) {
				switch (rosalyne.phase) {
					case BREAKING_OUT:
						rosalyne.setPhase(PHASE_1);
						rosalyne.level().explode(rosalyne, rosalyne.getX(), rosalyne.getY(), rosalyne.getZ(), 6,
								Level.ExplosionInteraction.NONE);
						break;
					case SUMMONING:
						rosalyne.setPhase(PHASE_2);
						rosalyne.createSpirits();
						break;
					case MADDENING:
						rosalyne.setPhase(PHASE_3);
						rosalyne.level().explode(rosalyne, rosalyne.getX(), rosalyne.getY(), rosalyne.getZ(), 4,
								Level.ExplosionInteraction.NONE);
						break;
				}
				rosalyne.setAnimation(ANIM_NEUTRAL);
				rosalyne.attackCooldown = 100;
			}
		}

		@Override
		public boolean canUse() {
			return rosalyne.phase == BREAKING_OUT || rosalyne.phase == SUMMONING || rosalyne.phase == MADDENING;
		}

	}

	private static class AdvanceAndSwingAttack extends Goal {
		private RosalyneServant rosalyne;
		private int timer, swingsLeft, attackPhase;
		private double holdx, holdy, holdz;

		public AdvanceAndSwingAttack(RosalyneServant rosalyne) {
			this.rosalyne = rosalyne;
			setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			timer = 100;
			swingsLeft = 3 + rosalyne.random.nextInt(2);
			if (rosalyne.phase == PHASE_3)
				swingsLeft = 8 + rosalyne.random.nextInt(5);
			attackPhase = 0;
			LivingEntity target = rosalyne.getTarget();
			rosalyne.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), 4);
		}

		@Override
		public void tick() {
			timer--;
			LivingEntity target = rosalyne.getTarget();

			if (target == null)
				return;

			if (attackPhase == 0) {
				if (timer <= 0 || rosalyne.distanceToSqr(target) < 2) {
					holdx = rosalyne.getX();
					holdy = target.getY();
					holdz = rosalyne.getZ();
					rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 4);
					attackPhase = 1;
					timer = 25;
					rosalyne.setAnimation(ANIM_ARM_OUT_DN);
				} else {
					rosalyne.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), 4);
				}
			}

			else if (attackPhase == 1) {
				rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 4);
				if (timer <= 0) {
					attackPhase = 2;
					rosalyne.swing();
					swingsLeft--;
					timer = rosalyne.phase == PHASE_3 ? 12 : 20;
				}
			}

			else {
				rosalyne.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.5);
				if (timer <= 0) {
					if (swingsLeft > 0) {
						rosalyne.swing();
						swingsLeft--;
						timer = rosalyne.phase == PHASE_3 ? 12 : 20;
					}
				}
			}
		}

		@Override
		public void stop() {
			rosalyne.cooldownNextAttack();
			rosalyne.setAnimation(ANIM_NEUTRAL);
			rosalyne.rollNextAttack(0);
		}

		@Override
		public boolean canUse() {
			return rosalyne.nextAttack == 0
					&& (rosalyne.phase == PHASE_1 || rosalyne.phase == PHASE_2 || rosalyne.phase == PHASE_3)
					&& rosalyne.getTarget() != null && rosalyne.getTarget().isAlive() && rosalyne.attackCooldown <= 0;
		}

		@Override
		public boolean canContinueToUse() {
			return canUse() && (swingsLeft > 0 || timer > 0);
		}

	}

	private static class CircleAndDashAttack extends Goal {

		private RosalyneServant rosalyne;
		private int timer, swingsLeft, attackPhase;
		private double holdx, holdy, holdz;
		private Vec3 offset;

		public CircleAndDashAttack(RosalyneServant rosalyne) {
			this.rosalyne = rosalyne;
			setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			timer = 100;
			swingsLeft = 1 + rosalyne.random.nextInt(2);
			if (rosalyne.phase == PHASE_3)
				swingsLeft = 3 + rosalyne.random.nextInt(3);
			startCircling();
		}

		private void startCircling() {
			attackPhase = 0;
			LivingEntity target = rosalyne.getTarget();
			offset = new Vec3(rosalyne.getX() - target.getX(), 1, rosalyne.getZ() - target.getZ()).normalize().scale(4);
			rosalyne.moveControl.setWantedPosition(target.getX() + offset.x, target.getY() + offset.y,
					target.getZ() + offset.z, 4);
			rosalyne.setAnimation(ANIM_PREPARE_DASH);
		}

		@Override
		public void tick() {
			timer--;
			LivingEntity target = rosalyne.getTarget();

			if (target == null)
				return;

			if (attackPhase == 0) {
				double tx = target.getX() + offset.x;
				double ty = target.getY() + offset.y;
				double tz = target.getZ() + offset.z;
				if (timer <= 0 || rosalyne.distanceToSqr(tx, ty, tz) < 1) {
					attackPhase = 1;

					timer = 18 + rosalyne.random.nextInt(36);
				}
				rosalyne.moveControl.setWantedPosition(tx, ty, tz, 4);
			}

			else if (attackPhase == 1) {
				offset = offset.yRot(-5 * Mth.DEG_TO_RAD);
				double tx = target.getX() + offset.x;
				double ty = target.getY() + offset.y;
				double tz = target.getZ() + offset.z;
				rosalyne.moveControl.setWantedPosition(tx, ty, tz, 4);
				if (timer <= 0) {
					attackPhase = 2;
					holdx = tx;
					holdy = ty;
					holdz = tz;
					timer = rosalyne.phase == PHASE_3 ? 15 : 20;
					rosalyne.setAnimation(ANIM_ARM_OUT_DN);
					rosalyne.playSound(MYFSounds.rosalyneSwingPrepare.get(), 1, 1);
				}
			}

			else if (attackPhase == 2) {
				if (timer <= 0) {
					attackPhase = 3;
					timer = 20;

					double tx = target.getX();
					double ty = target.getY();
					double tz = target.getZ();
					Vec3 tpos = new Vec3(tx - holdx, ty - holdy, tz - holdz).normalize();
					holdx = tx + 4 * tpos.x;
					holdy = ty + 4 * tpos.y;
					holdz = tz + 4 * tpos.z;
				}
				rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 4);
			}

			else if (attackPhase == 3) {
				if (timer <= 0 || rosalyne.distanceToSqr(target) < 2
						|| rosalyne.distanceToSqr(holdx, holdy, holdz) < 1) {

					rosalyne.swing();
					swingsLeft--;
					holdx = rosalyne.getX();
					holdy = target.getY();
					holdz = rosalyne.getZ();
					attackPhase = 4;
					timer = (swingsLeft > 0 && rosalyne.phase == PHASE_3) ? 10 : 20;
				}
				rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 3);
			}

			else if (attackPhase == 4) {
				if (timer <= 0 && swingsLeft > 0)
					startCircling();
				else
					rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 4);
			}
		}

		@Override
		public void stop() {
			rosalyne.cooldownNextAttack();
			rosalyne.setAnimation(ANIM_NEUTRAL);
			rosalyne.rollNextAttack(1);
		}

		@Override
		public boolean canUse() {
			return rosalyne.nextAttack == 1
					&& (rosalyne.phase == PHASE_1 || rosalyne.phase == PHASE_2 || rosalyne.phase == PHASE_3)
					&& rosalyne.getTarget() != null && rosalyne.getTarget().isAlive() && rosalyne.attackCooldown <= 0;
		}

		@Override
		public boolean canContinueToUse() {
			return canUse() && (swingsLeft > 0 || timer > 0);
		}

	}

	private static class VerticalCrashAttack extends Goal {

		private RosalyneServant rosalyne;
		private int timer, swingsLeft, attackPhase;
		private double holdx, holdy, holdz;
		private Vec3 offset;

		public VerticalCrashAttack(RosalyneServant rosalyne) {
			this.rosalyne = rosalyne;
			setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			swingsLeft = 1 + rosalyne.random.nextInt(2);
			if (rosalyne.phase == PHASE_3)
				swingsLeft = 3 + rosalyne.random.nextInt(3);
			startJump();
		}

		private void startJump() {
			timer = rosalyne.phase == PHASE_3 ? 25 : 40;
			attackPhase = 0;
			LivingEntity target = rosalyne.getTarget();
			offset = new Vec3(rosalyne.getX() - target.getX(), 0, rosalyne.getZ() - target.getZ()).normalize();
			holdx = target.getX() + offset.x * 3;
			holdy = target.getY() + 4;
			holdz = target.getZ() + offset.z * 3;
			rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 1);
			rosalyne.setAnimation(ANIM_PREPARE_CRASH);
		}

		@Override
		public void tick() {
			timer--;
			LivingEntity target = rosalyne.getTarget();

			if (target == null)
				return;

			if (attackPhase == 0) {

				holdy += 0.1;
				if (timer <= 0) {
					attackPhase = 1;
					timer = 20;

					double tx = target.getX();
					double ty = target.getY();
					double tz = target.getZ();
					Vec3 tpos = new Vec3(tx - holdx, ty - holdy, tz - holdz).normalize();
					holdx = tx + 1 * tpos.x;
					holdy = ty;
					holdz = tz + 1 * tpos.z;
				}
				rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 1);
			}

			else if (attackPhase == 1) {
				if (timer <= 0 || rosalyne.distanceToSqr(target) < 2
						|| rosalyne.distanceToSqr(holdx, holdy, holdz) < 1) {

					rosalyne.swing();
					swingsLeft--;
					holdx = rosalyne.getX();
					holdy = target.getY();
					holdz = rosalyne.getZ();
					attackPhase = 2;
					timer = (swingsLeft > 0 && rosalyne.phase == PHASE_3) ? 10 : 20;
				}
				rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 3);
			}

			else if (attackPhase == 2) {
				if (timer <= 0 && swingsLeft > 0)
					startJump();
				else
					rosalyne.moveControl.setWantedPosition(holdx, holdy, holdz, 4);
			}
		}

		@Override
		public void stop() {
			rosalyne.cooldownNextAttack();
			rosalyne.setAnimation(ANIM_NEUTRAL);
			rosalyne.rollNextAttack(2);
		}

		@Override
		public boolean canUse() {
			return rosalyne.nextAttack == 2
					&& (rosalyne.phase == PHASE_1 || rosalyne.phase == PHASE_2 || rosalyne.phase == PHASE_3)
					&& rosalyne.getTarget() != null && rosalyne.getTarget().isAlive() && rosalyne.attackCooldown <= 0;
		}

		@Override
		public boolean canContinueToUse() {
			return canUse() && (swingsLeft > 0 || timer > 0);
		}

	}

	public class VexMovementController extends MoveControl {

		private double slowdown = 0.5;

		public VexMovementController(Mob mob) {
			super(mob);
		}

		public VexMovementController slowdown(double slowdown) {
			this.slowdown = slowdown;
			return this;
		}

		@Override
		public void tick() {
			if (operation == MoveControl.Operation.MOVE_TO) {
				Vec3 vector3d = new Vec3(wantedX - mob.getX(), wantedY - mob.getY(), wantedZ - mob.getZ());
				double d0 = vector3d.length();
				if (d0 < mob.getBoundingBox().getSize()) {
					operation = MoveControl.Operation.WAIT;
					mob.setDeltaMovement(mob.getDeltaMovement().scale(slowdown));
				} else {
					mob.setDeltaMovement(mob.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d0)));
					if (mob.getTarget() == null) {
						Vec3 vector3d1 = mob.getDeltaMovement();
						mob.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
						mob.yBodyRot = mob.getYRot();
					} else {
						double d2 = mob.getTarget().getX() - mob.getX();
						double d1 = mob.getTarget().getZ() - mob.getZ();
						mob.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
						mob.yBodyRot = mob.getYRot();
					}
				}

			}
		}

	}

	public class MoveFrontOfTarget extends Goal {
		private Mob mob;
		private int moveCooldown;
		private double speed;

		public MoveFrontOfTarget(Mob mob, double speed) {
			setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.mob = mob;
			this.speed = speed;
		}

		@Override
		public boolean canUse() {
			return mob.getTarget() != null && !mob.getMoveControl().hasWanted();
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			moveCooldown = 20;

			LivingEntity target = mob.getTarget();
			BlockPos targetP = target.blockPosition();
			Vec3 look = Vec3.directionFromRotation(0, target.getYRot());

			mob.getMoveControl().setWantedPosition(
					targetP.getX() + look.x * 4 - 0.5 + mob.getRandom().nextDouble() * 2,
					targetP.getY() + 2 + mob.getRandom().nextDouble() * 2,
					targetP.getZ() + look.z * 4 - 0.5 + mob.getRandom().nextDouble() * 2,
					speed);
		}

		@Override
		public boolean canContinueToUse() {
			return moveCooldown > 0;
		}

		@Override
		public void tick() {
			moveCooldown--;
		}

	}

	public abstract class StationaryAttack extends Goal {

		private Mob boss;
		protected double stationaryY, offset;

		public StationaryAttack(Mob boss, double offset) {
			setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.boss = boss;
			this.offset = offset;
		}

		public StationaryAttack(Mob boss) {
			this(boss, 1);
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			if (boss.getTarget() != null)
				stationaryY = boss.getTarget().getY() + offset + boss.getRandom().nextDouble() * 2;
			else
				stationaryY = boss.getY();
		}

		@Override
		public void tick() {
			if (!boss.getMoveControl().hasWanted()) {
				if (Math.abs(boss.getY() - stationaryY) >= 1) {
					boss.getMoveControl().setWantedPosition(boss.getX(), stationaryY, boss.getZ(), 1);
				}
			}
		}

	}

	public class VexMoveRandomGoal extends Goal {

		private Mob mob;
		private double speed;

		public VexMoveRandomGoal(Mob mob, double speed) {
			setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.mob = mob;
			this.speed = speed;
		}

		@Override
		public boolean canUse() {
			return !mob.getMoveControl().hasWanted() && mob.getRandom().nextInt(7) == 0;
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}

		@Override
		public void tick() {
			BlockPos blockpos = mob.blockPosition();

			for (int i = 0; i < 3; ++i) {
				BlockPos blockpos1 = blockpos.offset(mob.getRandom().nextInt(15) - 7, mob.getRandom().nextInt(11) - 5,
						mob.getRandom().nextInt(15) - 7);
				if (mob.level().isEmptyBlock(blockpos1)) {
					mob.getMoveControl().setWantedPosition(blockpos1.getX() + 0.5, blockpos1.getY() + 0.5,
							blockpos1.getZ() + 0.5, speed);
					if (mob.getTarget() == null) {
						mob.getLookControl().setLookAt(blockpos1.getX() + 0.5, blockpos1.getY() + 0.5,
								blockpos1.getZ() + 0.5, 180.0F, 20.0F);
					}
					break;
				}
			}

		}

	}

	class RosalyneFollowGoal extends Goal {
		private final RosalyneServant rosalyneServant;
		private LivingEntity owner;
		private final Level level;
		private final double followSpeed;
		private final PathNavigation navigation;
		private int timeToRecalcPath;
		private final float maxDist;
		private final float minDist;
		private float oldWaterCost;
		private final boolean teleportToLeaves;

		public RosalyneFollowGoal(RosalyneServant rosalyneServant, double speed, float minDist, float maxDist,
				boolean teleportToLeaves) {
			this.rosalyneServant = rosalyneServant;
			this.level = rosalyneServant.level();
			this.followSpeed = speed;
			this.navigation = rosalyneServant.getNavigation();
			this.minDist = minDist;
			this.maxDist = maxDist;
			this.teleportToLeaves = teleportToLeaves;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
			if (!(rosalyneServant.getNavigation() instanceof GroundPathNavigation)
					&& !(rosalyneServant.getNavigation() instanceof FlyingPathNavigation)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		public boolean canUse() {
			LivingEntity livingentity = this.rosalyneServant.getTrueOwner();
			if (livingentity == null) {
				return false;
			} else if (livingentity.isSpectator()) {
				return false;
			} else if (livingentity instanceof net.minecraft.world.entity.Mob
					&& !(livingentity instanceof RosalyneServant)) {
				return false;
			} else if (this.rosalyneServant.distanceToSqr(livingentity) < (double) (this.minDist * this.minDist)) {
				return false;
			} else if (!this.rosalyneServant.isFollowing()) {
				return false;
			} else if (this.rosalyneServant.isStaying()) {
				return false;
			} else if (this.rosalyneServant.getTarget() != null) {
				return false;
			} else {
				this.owner = livingentity;
				return true;
			}
		}

		public boolean canContinueToUse() {
			if (this.rosalyneServant.getTarget() != null) {
				return false;
			} else if (this.navigation.isDone()) {
				return false;
			} else {
				return !(this.rosalyneServant.distanceToSqr(this.owner) <= (double) (this.maxDist * this.maxDist));
			}
		}

		public void start() {
			this.timeToRecalcPath = 0;
			this.oldWaterCost = this.rosalyneServant.getPathfindingMalus(BlockPathTypes.WATER);
			this.rosalyneServant.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		}

		public void stop() {
			this.navigation.stop();
			this.rosalyneServant.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
		}

		public void tick() {
			this.rosalyneServant.getLookControl().setLookAt(this.owner, 10.0F,
					(float) this.rosalyneServant.getMaxHeadXRot());
			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10;
				if (this.rosalyneServant.distanceTo(this.owner) > 8.0D) {
					double x = Math.floor(this.owner.getX()) - 2;
					double y = Math.floor(this.owner.getBoundingBox().minY);
					double z = Math.floor(this.owner.getZ()) - 2;
					for (int l = 0; l <= 4; ++l) {
						for (int i1 = 0; i1 <= 4; ++i1) {
							if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
									&& this.validPosition(BlockPos.containing(x + l, y + 2, z + i1))) {
								float a = (float) ((x + l) + 0.5F);
								float b = (float) ((z + i1) + 0.5F);
								this.rosalyneServant.getMoveControl().setWantedPosition(a, y, b, this.followSpeed);
								this.navigation.stop();
							}
						}
					}
				}
				if (this.rosalyneServant.distanceToSqr(this.owner) > 144.0) {
					this.tryToTeleportNearEntity();
				}
			}
		}

		private void tryToTeleportNearEntity() {
			BlockPos blockpos = this.owner.blockPosition();
			for (int i = 0; i < 10; ++i) {
				int j = this.getRandomNumber(-3, 3);
				int k = this.getRandomNumber(-1, 1);
				int l = this.getRandomNumber(-3, 3);
				boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k,
						blockpos.getZ() + l);
				if (flag) {
					return;
				}
			}
		}

		private boolean tryToTeleportToLocation(int x, int y, int z) {
			if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
				return false;
			} else if (!this.isTeleportFriendlyBlock(BlockPos.containing(x, y, z))) {
				return false;
			} else {
				this.rosalyneServant.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D,
						this.rosalyneServant.getYRot(), this.rosalyneServant.getXRot());
				this.navigation.stop();
				return true;
			}
		}

		private boolean isTeleportFriendlyBlock(BlockPos pos) {
			net.minecraft.world.level.pathfinder.BlockPathTypes pathnodetype = net.minecraft.world.level.pathfinder.WalkNodeEvaluator
					.getBlockPathTypeStatic(this.level, pos.mutable());
			if (pathnodetype != net.minecraft.world.level.pathfinder.BlockPathTypes.WALKABLE) {
				return false;
			} else {
				net.minecraft.world.level.block.state.BlockState blockstate = this.level.getBlockState(pos.below());
				if (!this.teleportToLeaves
						&& blockstate.getBlock() instanceof net.minecraft.world.level.block.LeavesBlock) {
					return false;
				} else {
					BlockPos blockpos = pos.subtract(this.rosalyneServant.blockPosition());
					return this.level.noCollision(this.rosalyneServant,
							this.rosalyneServant.getBoundingBox().move(blockpos));
				}
			}
		}

		protected boolean validPosition(BlockPos pos) {
			net.minecraft.world.level.block.state.BlockState blockstate = this.level.getBlockState(pos);
			return (blockstate.canSurvive(this.level, pos) && this.level.isEmptyBlock(pos.above())
					&& this.level.isEmptyBlock(pos.above(2)));
		}

		private int getRandomNumber(int min, int max) {
			return this.rosalyneServant.getRandom().nextInt(max - min + 1) + min;
		}
	}

}
