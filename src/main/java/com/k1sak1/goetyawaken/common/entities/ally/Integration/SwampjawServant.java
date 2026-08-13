package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import lykrast.meetyourfight.MeetYourFight;
import lykrast.meetyourfight.registry.MYFItems;
import lykrast.meetyourfight.registry.MYFSounds;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class SwampjawServant extends Summoned {
	private static final EntityDataAccessor<Byte> ANIMATION = SynchedEntityData.defineId(SwampjawServant.class,
			EntityDataSerializers.BYTE);
	public static final int ANIM_NEUTRAL = 0, ANIM_SWOOP = 1, ANIM_STUN = 2, ANIM_SWIPE = 3;
	public static final int HP = 100, DMG_CHARGE = 12;
	private int behavior;
	private int attackDelay;
	private static final int CIRCLE = 0, BOMB = 1, SWOOP = 2, STUNNED = 3, SWIPING = 4;
	private Vec3 orbitOffset = Vec3.ZERO;
	private BlockPos orbitPosition = BlockPos.ZERO;
	public float tailYaw, tailPitch;
	public int clientAnim, prevAnim, animProg, animDur;

	public SwampjawServant(EntityType<? extends SwampjawServant> type, Level worldIn) {
		super(type, worldIn);
		moveControl = new MoveHelperController(this);
		tailYaw = getYRot();
		tailPitch = getXRot();
		clientAnim = ANIM_NEUTRAL;
		prevAnim = ANIM_NEUTRAL;
		animProg = 1;
		animDur = 1;
	}

	public void travel(Vec3 p_20818_) {
		if (this.isControlledByLocalInstance()) {
			if (this.isInWater()) {
				this.moveRelative(0.02F, p_20818_);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.8F));
			} else if (this.isInLava()) {
				this.moveRelative(0.02F, p_20818_);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
			} else {
				BlockPos ground = getBlockPosBelowThatAffectsMyMovement();
				float f = 0.91F;
				if (this.onGround()) {
					f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
				}

				float f1 = 0.16277137F / (f * f * f);
				f = 0.91F;
				if (this.onGround()) {
					f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
				}

				this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, p_20818_);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale((double) f));
			}
		}

		this.calculateEntityAnimation(false);
	}

	public boolean onClimbable() {
		return false;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return setCustomAttributes();
	}

	public boolean causeFallDamage(float p_146828_, float p_146829_, DamageSource p_146830_) {
		return false;
	}

	protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
	}

	protected void playStepSound(BlockPos pPos, BlockState pBlock) {
	}

	public double getPassengersRidingOffset() {
		return this.getEyeHeight();
	}

	public static AttributeSupplier.Builder setCustomAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, AttributesConfig.SwampjawServantHealth.get())
				.add(Attributes.ATTACK_DAMAGE, AttributesConfig.SwampjawServantDamage.get())
				.add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.SwampjawServantKnockbackResistance.get())
				.add(Attributes.ARMOR, AttributesConfig.SwampjawServantArmor.get())
				.add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.SwampjawServantArmorToughness.get());
	}

	@Override
	public void setConfigurableAttributes() {
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
				AttributesConfig.SwampjawServantHealth.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
				AttributesConfig.SwampjawServantDamage.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE),
				AttributesConfig.SwampjawServantKnockbackResistance.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
				AttributesConfig.SwampjawServantArmor.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
				AttributesConfig.SwampjawServantArmorToughness.get());
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(1, new PickAttackGoal(this));
		goalSelector.addGoal(2, new SweepAttackGoal(this));
		goalSelector.addGoal(3, new BombMovementGoal(this));
		goalSelector.addGoal(4, new OrbitPointGoal(this));
		goalSelector.addGoal(8, new LookWhenNotStunned(this, Player.class, 16));
	}

	@Override
	public void followGoal() {
		this.goalSelector.addGoal(1, new SwampjawFollowOwnerGoal(this));
	}

	@Override
	public void targetSelectGoal() {
		this.targetSelector.addGoal(1, new SwampjawServantTargetGoal(this, 64.0D));
	}

	@Override
	public void tick() {
		noPhysics = true;
		super.tick();
		noPhysics = false;

		if (level().isClientSide()) {
			int newanim = getAnimation();
			if (clientAnim != newanim) {
				prevAnim = clientAnim;
				clientAnim = newanim;
				animProg = 0;
				animDur = 10;
				if (clientAnim == ANIM_NEUTRAL && prevAnim == ANIM_SWOOP)
					animDur = 5;
			} else if (animProg < animDur)
				animProg++;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
			MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		orbitPosition = this.blockPosition().above(5);
		orbitOffset = this.position();
		this.setConfigurableAttributes();
		setHealth(getMaxHealth());
		this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2));
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public int getSummonLimit(LivingEntity owner) {
		return com.k1sak1.goetyawaken.Config.SWAMPJAW_SERVANT_LIMIT.get();
	}

	@Override
	public boolean canAttackType(EntityType<?> typeIn) {
		return true;
	}

	@Override
	public boolean canStay() {
		return false;
	}

	@Override
	protected boolean isSunSensitive() {
		return false;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(ANIMATION, (byte) 0);
	}

	public int getAnimation() {
		return entityData.get(ANIMATION);
	}

	public void setAnimation(int anim) {
		entityData.set(ANIMATION, (byte) anim);
	}

	public float getAnimProgress(float partial) {
		return Mth.clamp((animProg + partial) / animDur, 0, 1);
	}

	public float getTailYaw(float partialTick) {
		return Mth.approachDegrees(tailYaw, getYRot(), 6 * partialTick);
	}

	public float getTailPitch(float partialTick) {
		return Mth.approachDegrees(tailPitch, getXRot(), 2 * partialTick);
	}

	@SuppressWarnings("resource")
	@Override
	public void aiStep() {
		super.aiStep();
		if (level().isClientSide) {
			tailYaw = Mth.approachDegrees(tailYaw, getYRot(), 6);
			tailPitch = Mth.approachDegrees(tailPitch, getXRot(), 2);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("AX"))
			orbitPosition = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
		if (compound.contains("Delay"))
			attackDelay = compound.getInt("Delay");
		if (compound.contains("Behavior"))
			behavior = compound.getInt("Behavior");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("AX", orbitPosition.getX());
		compound.putInt("AY", orbitPosition.getY());
		compound.putInt("AZ", orbitPosition.getZ());
		compound.putInt("Delay", attackDelay);
		compound.putInt("Behavior", behavior);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MYFSounds.swampjawIdle.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return MYFSounds.swampjawHurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MYFSounds.swampjawDeath.get();
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	public void die(DamageSource cause) {
		if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
			ItemStack mossyTooth = new ItemStack(MYFItems.mossyTooth.get());
			if (this.getTrueOwner() != null) {
				FlyingItem flyingItem = new FlyingItem(
						com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
						this.level(),
						this.getX(),
						this.getY() + 1.0D,
						this.getZ());
				flyingItem.setOwner(this.getTrueOwner());
				flyingItem.setItem(mossyTooth);
				flyingItem.setParticle(ModParticleTypes.CULT_SPELL.get());
				flyingItem.setSecondsCool(30);
				this.level().addFreshEntity(flyingItem);
			} else {
				ItemEntity itemEntity = this.spawnAtLocation(mossyTooth);
				if (itemEntity != null) {
					itemEntity.setExtendedLifetime();

				}
			}
		}
		super.die(cause);
	}

	private void swipeAttack() {
		playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 10.0F, 0.95F + random.nextFloat() * 0.1F);
		for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class,
				getBoundingBox().inflate(1.75, 1, 1.75))) {
			if (target.isAlive() && !target.isInvulnerable() && target != this) {
				if (doHurtTarget(target)) {
					double mult = Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
					Vec3 knockback = new Vec3(target.getX() - getX(), 0, target.getZ() - getZ()).normalize()
							.add(0, 0.2, 0).scale(2 * mult);
					target.setDeltaMovement(target.getDeltaMovement().add(knockback));
				}
			}
		}
	}

	private static class MoveHelperController extends MoveControl {
		private float speedFactor = 0.1F;
		private SwampjawServant swampjaw;

		public MoveHelperController(SwampjawServant entityIn) {
			super(entityIn);
			swampjaw = entityIn;
		}

		@Override
		public void tick() {
			if (swampjaw.behavior == STUNNED || swampjaw.behavior == SWIPING) {
				swampjaw.setDeltaMovement(swampjaw.getDeltaMovement().scale(0.9));
				return;
			}

			float targetX = (float) (swampjaw.orbitOffset.x - swampjaw.getX());
			float targetY = (float) (swampjaw.orbitOffset.y - swampjaw.getY());
			float targetZ = (float) (swampjaw.orbitOffset.z - swampjaw.getZ());
			double horizontalDist = (double) Mth.sqrt(targetX * targetX + targetZ * targetZ);
			double verticalAdjust = 1.0D - (double) Mth.abs(targetY * 0.7F) / horizontalDist;
			targetX = (float) ((double) targetX * verticalAdjust);
			targetZ = (float) ((double) targetZ * verticalAdjust);
			horizontalDist = (double) Mth.sqrt(targetX * targetX + targetZ * targetZ);
			double totalDist = (double) Mth.sqrt(targetX * targetX + targetZ * targetZ + targetY * targetY);
			float prevYaw = swampjaw.getYRot();
			float targetYaw = (float) Mth.atan2((double) targetZ, (double) targetX);
			float startYaw = Mth.wrapDegrees(swampjaw.getYRot() + 90.0F);
			targetYaw = Mth.wrapDegrees(targetYaw * (180F / (float) Math.PI));
			boolean isFastBomb = swampjaw.behavior == BOMB && swampjaw.attackDelay <= 10;
			swampjaw.setYRot(Mth.approachDegrees(startYaw, targetYaw, isFastBomb ? 20 : 10) - 90.0F);
			swampjaw.yBodyRot = swampjaw.getYRot();
			if (isFastBomb || Mth.degreesDifferenceAbs(prevYaw, swampjaw.getYRot()) < 3.0F) {
				float maxSpeed = swampjaw.behavior != CIRCLE ? 3F : 1.2F;
				float multiplier = speedFactor > maxSpeed ? 10 : maxSpeed / speedFactor;
				speedFactor = Mth.approach(speedFactor, maxSpeed, 0.005F * multiplier);
			}

			else
				speedFactor = Mth.approach(speedFactor, swampjaw.behavior == BOMB ? 0.7F : 0.4F, 0.05F);

			float finalPitch = (float) (-(Mth.atan2(-targetY, horizontalDist) * (180F / (float) Math.PI)));
			swampjaw.setXRot(finalPitch);
			float adjustedYaw = swampjaw.getYRot() + 90.0F;
			double finalX = (double) (speedFactor * Mth.cos(adjustedYaw * ((float) Math.PI / 180F)))
					* Math.abs((double) targetX / totalDist);
			double finalZ = (double) (speedFactor * Mth.sin(adjustedYaw * ((float) Math.PI / 180F)))
					* Math.abs((double) targetZ / totalDist);
			double finalY = (double) (speedFactor * Mth.sin(finalPitch * ((float) Math.PI / 180F)))
					* Math.abs((double) targetY / totalDist);
			Vec3 vector3d = swampjaw.getDeltaMovement();
			swampjaw.setDeltaMovement(vector3d.add((new Vec3(finalX, finalY, finalZ)).subtract(vector3d).scale(0.2)));
		}
	}

	private static abstract class BaseMoveGoal extends Goal {
		protected SwampjawServant swampjaw;

		public BaseMoveGoal(SwampjawServant swampjaw) {
			this.swampjaw = swampjaw;
			setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		protected boolean isCloseToOffset() {
			return swampjaw.orbitOffset.distanceToSqr(swampjaw.getX(), swampjaw.getY(), swampjaw.getZ()) < 4;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}
	}

	private static class OrbitPointGoal extends BaseMoveGoal {
		private float angle;
		private float radius;
		private float height;
		private float direction;

		public OrbitPointGoal(SwampjawServant swampjaw) {
			super(swampjaw);
		}

		@Override
		public boolean canUse() {
			return swampjaw.getTarget() == null || swampjaw.behavior == CIRCLE;
		}

		@Override
		public void start() {
			radius = 6 + swampjaw.random.nextFloat() * 6;
			height = -4.0F + swampjaw.random.nextFloat() * 6.0F;
			direction = swampjaw.random.nextBoolean() ? 1.0F : -1.0F;
			updateOffset();
		}

		@Override
		public void tick() {
			if (swampjaw.random.nextInt(350) == 0) {
				height = -4.0F + swampjaw.random.nextFloat() * 6.0F;
			}

			if (swampjaw.random.nextInt(250) == 0) {
				--radius;
				if (radius < 6) {
					radius = 12;
					direction = -direction;
				}
			}

			if (swampjaw.random.nextInt(450) == 0) {
				angle = swampjaw.random.nextFloat() * 2.0F * (float) Math.PI;
				updateOffset();
			}

			if (isCloseToOffset()) {
				updateOffset();
			}

			if (swampjaw.orbitOffset.y < swampjaw.getY()
					&& !swampjaw.level().isEmptyBlock(swampjaw.blockPosition().below(1))) {
				height = Math.max(1, height);
				updateOffset();
			}

			if (swampjaw.orbitOffset.y > swampjaw.getY()
					&& !swampjaw.level().isEmptyBlock(swampjaw.blockPosition().above(1))) {
				height = Math.min(-1, height);
				updateOffset();
			}
		}

		private void updateOffset() {
			if (BlockPos.ZERO.equals(swampjaw.orbitPosition))
				swampjaw.orbitPosition = swampjaw.blockPosition();

			angle += direction * 20 * ((float) Math.PI / 180F);
			swampjaw.orbitOffset = Vec3.atLowerCornerOf(swampjaw.orbitPosition).add(radius * Mth.cos(angle),
					-4.0F + height, radius * Mth.sin(this.angle));
		}
	}

	private static class BombMovementGoal extends BaseMoveGoal {
		public BombMovementGoal(SwampjawServant swampjaw) {
			super(swampjaw);
		}

		@Override
		public boolean canUse() {
			return swampjaw.getTarget() != null && swampjaw.behavior == BOMB;
		}

		@Override
		public void start() {
			updateOffset();
		}

		@Override
		public void tick() {
			LivingEntity target = swampjaw.getTarget();
			if (target == null)
				return;
			if (isCloseToOffset())
				updateOffset();
			else if (swampjaw.attackDelay <= 10) {

				Vec3 swamp = swampjaw.position();
				Vec3 destination = swampjaw.orbitOffset;
				Vec3 targetPos = target.position();

				double distance = (destination.z - swamp.z) * targetPos.x - (destination.x - swamp.x) * targetPos.z
						+ destination.x * swamp.z - destination.z * swamp.x;
				distance = (distance * distance) / ((destination.z - swamp.z) * (destination.z - swamp.z)
						+ (destination.x - swamp.x) * (destination.x - swamp.x));
				if (distance > 10)
					updateOffset();
			}
		}

		private void updateOffset() {
			if (BlockPos.ZERO.equals(swampjaw.orbitPosition))
				swampjaw.orbitPosition = swampjaw.blockPosition();
			LivingEntity target = swampjaw.getTarget();
			if (target != null) {
				double difX = target.getX() - swampjaw.orbitOffset.x;
				double difZ = target.getZ() - swampjaw.orbitOffset.z;
				Vec3 overshoot = new Vec3(difX, 0, difZ).normalize();
				Vec3 vec = target.position();
				swampjaw.orbitOffset = new Vec3(vec.x + overshoot.x * 10, swampjaw.orbitPosition.getY() - 4,
						vec.z + overshoot.z * 10);
			}
		}
	}

	private static class SweepAttackGoal extends BaseMoveGoal {
		public SweepAttackGoal(SwampjawServant swampjaw) {
			super(swampjaw);
		}

		@Override
		public boolean canUse() {
			return swampjaw.getTarget() != null && swampjaw.behavior == SWOOP;
		}

		@Override
		public boolean canContinueToUse() {
			LivingEntity livingentity = swampjaw.getTarget();
			if (livingentity == null || !livingentity.isAlive()) {
				return false;
			} else {
				return canUse();
			}
		}

		@Override
		public void stop() {
			if (swampjaw.behavior == SWOOP) {
				swampjaw.behavior = CIRCLE;
				swampjaw.setAnimation(ANIM_NEUTRAL);
			}
		}

		@Override
		public void tick() {
			LivingEntity livingentity = swampjaw.getTarget();
			if (livingentity == null)
				return;
			swampjaw.orbitOffset = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
			if (swampjaw.hurtTime > 0) {
				swampjaw.playSound(MYFSounds.swampjawStun.get(), 10.0F, 0.95F + swampjaw.random.nextFloat() * 0.1F);
				swampjaw.attackDelay = 50;
				swampjaw.behavior = STUNNED;
				swampjaw.setAnimation(ANIM_STUN);
			} else if (swampjaw.getBoundingBox().intersects(livingentity.getBoundingBox())
					&& swampjaw.distanceToSqr(livingentity.getX(), swampjaw.getY(), livingentity.getZ()) <= 4) {

				swampjaw.doHurtTarget(livingentity);
				swampjaw.behavior = CIRCLE;
				swampjaw.setAnimation(ANIM_NEUTRAL);

			}
		}
	}

	private static class PickAttackGoal extends Goal {
		private int bombLeft;
		private SwampjawServant swampjaw;

		public PickAttackGoal(SwampjawServant swampjaw) {
			this.swampjaw = swampjaw;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public boolean canUse() {
			LivingEntity livingentity = swampjaw.getTarget();
			return swampjaw.behavior == STUNNED || swampjaw.behavior == SWIPING
					|| (livingentity != null
							? swampjaw.canAttack(swampjaw.getTarget(),
									TargetingConditions.forCombat().ignoreLineOfSight())
							: false);
		}

		@Override
		public void start() {
			swampjaw.attackDelay = 100;
			bombLeft = 3;
			swampjaw.behavior = CIRCLE;
			updateOrbit();
		}

		@Override
		public void stop() {
			// swampjaw.orbitPosition =
			// swampjaw.world.getHeight(Heightmap.Type.MOTION_BLOCKING,
			// swampjaw.orbitPosition).up(10 + swampjaw.rand.nextInt(20));
		}

		@Override
		public void tick() {
			if (swampjaw.behavior == CIRCLE || swampjaw.behavior == BOMB || swampjaw.behavior == STUNNED
					|| swampjaw.behavior == SWIPING) {
				--swampjaw.attackDelay;
				if (swampjaw.attackDelay <= 0) {
					if (swampjaw.behavior == STUNNED) {
						swampjaw.behavior = SWIPING;
						swampjaw.setAnimation(ANIM_SWIPE);
						swampjaw.attackDelay = 10;
					} else if (swampjaw.behavior == SWIPING) {
						swampjaw.behavior = CIRCLE;
						swampjaw.setAnimation(ANIM_NEUTRAL);
						swampjaw.attackDelay = (2 + swampjaw.random.nextInt(3)) * 20;
					} else if (bombLeft <= 0) {
						bombLeft = 3;
						swampjaw.behavior = SWOOP;
						swampjaw.setAnimation(ANIM_SWOOP);
						updateOrbit();
						swampjaw.attackDelay = (4 + swampjaw.random.nextInt(4)) * 20;
						swampjaw.playSound(MYFSounds.swampjawCharge.get(), 10.0F,
								0.95F + swampjaw.random.nextFloat() * 0.1F);
					} else if (swampjaw.behavior == CIRCLE) {
						swampjaw.behavior = BOMB;
						swampjaw.attackDelay = 20;
					} else if (swampjaw.attackDelay <= -120 || isTargetClose()) {
						bombLeft--;
						if (bombLeft <= 0)
							swampjaw.attackDelay = 30 + swampjaw.random.nextInt(30);
						else
							swampjaw.attackDelay = 30;
						updateOrbit();
						swampjaw.playSound(MYFSounds.swampjawBomb.get(), 10.0F,
								0.95F + swampjaw.random.nextFloat() * 0.1F);
						SwampMine tntentity = new SwampMine(swampjaw.level(), swampjaw.getX() + 0.5,
								swampjaw.getY(), swampjaw.getZ() + 0.5, swampjaw);
						tntentity.setOwner(swampjaw);
						Vec3 motion = swampjaw.getDeltaMovement();
						tntentity.setDeltaMovement(tntentity.getDeltaMovement().add(motion.x * 0.5, 0, motion.z * 0.5));
						swampjaw.level().addFreshEntity(tntentity);
					}
				} else if (swampjaw.behavior == STUNNED && swampjaw.attackDelay == 10) {
					swampjaw.setAnimation(ANIM_NEUTRAL);
				} else if (swampjaw.behavior == SWIPING && swampjaw.attackDelay == 5) {
					swampjaw.swipeAttack();
				}
			}
		}

		private boolean isTargetClose() {
			LivingEntity target = swampjaw.getTarget();
			if (target == null)
				return false;
			double dx = target.getX() - (swampjaw.getX() + swampjaw.getDeltaMovement().x);
			double dz = target.getZ() - (swampjaw.getZ() + swampjaw.getDeltaMovement().z);
			return (dx * dx + dz * dz) < 12;
		}

		private void updateOrbit() {
			LivingEntity target = swampjaw.getTarget();
			if (target != null) {
				swampjaw.orbitPosition = target.blockPosition().above(14 + swampjaw.random.nextInt(6));
			}
		}
	}

	private static class LookWhenNotStunned extends LookAtPlayerGoal {
		private SwampjawServant swampjaw;

		public LookWhenNotStunned(SwampjawServant swampjaw, Class<? extends LivingEntity> target, float range) {
			super(swampjaw, target, range);
			this.swampjaw = swampjaw;
		}

		@Override
		public boolean canUse() {
			return swampjaw.behavior != STUNNED && swampjaw.behavior != SWIPING && super.canUse();
		}

		@Override
		public boolean canContinueToUse() {
			return swampjaw.behavior != STUNNED && swampjaw.behavior != SWIPING && super.canContinueToUse();
		}

	}

	private static class SwampjawFollowOwnerGoal extends Goal {
		private final SwampjawServant swampjaw;

		public SwampjawFollowOwnerGoal(SwampjawServant swampjaw) {
			this.swampjaw = swampjaw;
		}

		@Override
		public boolean canUse() {
			LivingEntity owner = swampjaw.getTrueOwner();
			return owner != null && swampjaw.isFollowing() && swampjaw.getTarget() == null
					&& swampjaw.behavior != STUNNED && swampjaw.behavior != SWIPING;
		}

		@Override
		public boolean canContinueToUse() {
			return swampjaw.getTrueOwner() != null && swampjaw.isFollowing() && swampjaw.getTarget() == null
					&& swampjaw.behavior != STUNNED && swampjaw.behavior != SWIPING;
		}

		@Override
		public void start() {
			if (swampjaw.getTrueOwner() != null) {
				swampjaw.orbitPosition = swampjaw.getTrueOwner().blockPosition()
						.above(14 + swampjaw.random.nextInt(6));
			}
		}

		@Override
		public void stop() {
			if (!swampjaw.isGuardingArea()) {
				swampjaw.orbitPosition = swampjaw.blockPosition().above(5);
			}
		}

		@Override
		public void tick() {
			if (swampjaw.getTrueOwner() != null) {
				swampjaw.orbitPosition = swampjaw.getTrueOwner().blockPosition()
						.above(14 + swampjaw.random.nextInt(6));
			}
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}
	}

	private static class SwampjawServantTargetGoal extends Goal {
		private final SwampjawServant swampjaw;
		private final TargetingConditions attackTargeting;
		private int nextScanTick;

		public SwampjawServantTargetGoal(SwampjawServant swampjaw, double range) {
			this.swampjaw = swampjaw;
			this.attackTargeting = TargetingConditions.forCombat()
					.range(range)
					.ignoreLineOfSight()
					.selector(SummonTargetGoal.predicate(swampjaw));
			this.nextScanTick = reducedTickDelay(20);
		}

		@Override
		public boolean canUse() {
			if (this.nextScanTick > 0) {
				--this.nextScanTick;
				return false;
			} else {
				this.nextScanTick = reducedTickDelay(60);
				List<LivingEntity> list = swampjaw.level().getNearbyEntities(LivingEntity.class,
						this.attackTargeting, swampjaw,
						swampjaw.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
				if (!list.isEmpty()) {
					list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());
					for (LivingEntity livingEntity : list) {
						if (!MobUtil.areAllies(swampjaw, livingEntity)) {
							swampjaw.setTarget(livingEntity);
							return true;
						}
					}
				}
				return false;
			}
		}

		@Override
		public boolean canContinueToUse() {
			LivingEntity livingentity = swampjaw.getTarget();
			return livingentity != null && livingentity.isAlive();
		}
	}
}
