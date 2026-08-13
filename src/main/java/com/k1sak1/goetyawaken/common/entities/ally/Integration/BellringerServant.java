package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ModProjectileLineEntity;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import lykrast.meetyourfight.registry.MYFItems;
import lykrast.meetyourfight.registry.MYFSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class BellringerServant extends Summoned {
	public static final int HP = 200, DMG = 10;
	public int attackCooldown;
	private int rageAttacks = 0;
	private int spiritLevel = 0;

	public static AttributeSupplier.Builder setCustomAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, AttributesConfig.BellringerServantHealth.get())
				.add(Attributes.ATTACK_DAMAGE, AttributesConfig.BellringerServantDamage.get())
				.add(Attributes.ARMOR, AttributesConfig.BellringerServantArmor.get())
				.add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.BellringerServantArmorToughness.get())
				.add(Attributes.FOLLOW_RANGE, 32);
	}

	@Override
	public void setConfigurableAttributes() {
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
				AttributesConfig.BellringerServantHealth.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
				AttributesConfig.BellringerServantDamage.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
				AttributesConfig.BellringerServantArmor.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
				AttributesConfig.BellringerServantArmorToughness.get());
	}

	@Override
	public int getSummonLimit(LivingEntity owner) {
		return com.k1sak1.goetyawaken.Config.BELLRINGER_SERVANT_LIMIT.get();
	}

	public BellringerServant(EntityType<? extends BellringerServant> type, Level worldIn) {
		super(type, worldIn);
		moveControl = new VexMovementController(this);
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
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new RageAttack(this));
		goalSelector.addGoal(2, new BurstAttack(this));
		goalSelector.addGoal(7, new MoveFrontOfTarget(this, 1));
		goalSelector.addGoal(8, new VexMoveRandomGoal(this, 0.25));
		targetSelector.addGoal(2, new HurtByTargetGoal(this));
	}

	@Override
	public void followGoal() {
		this.goalSelector.addGoal(6, new BellringerFollowGoal(this, 1.0D, 2.0F, 10.0F, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return setCustomAttributes();
	}

	@SuppressWarnings("deprecation")
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
			MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		this.setConfigurableAttributes();
		setHealth(getMaxHealth());
		this.attackCooldown = 100;
		this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2));
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public void customServerAiStep() {
		if (attackCooldown > 0)
			attackCooldown--;
		super.customServerAiStep();
	}

	private void dingDong() {
		swing(InteractionHand.MAIN_HAND);
		playSound(SoundEvents.BELL_BLOCK, 2, 1);
	}

	public int getSpiritLevel() {
		return this.spiritLevel;
	}

	public void setSpiritLevel(int level) {
		this.spiritLevel = Math.min(level, 20);
	}

	private ModProjectileLineEntity readyAttack() {
		ModProjectileLineEntity ghost = new ModProjectileLineEntity(level(), this);
		ghost.setOwner(this);
		ghost.setPos(getX() - 2 + random.nextDouble() * 4, getY() - 2 + random.nextDouble() * 4,
				getZ() - 2 + random.nextDouble() * 4);
		ghost.setVariant(ModProjectileLineEntity.VAR_BELLRINGER);
		ghost.setExtraDamage(this.spiritLevel);
		return ghost;
	}

	@Override
	protected boolean isSunSensitive() {
		return false;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("AttackCooldown"))
			attackCooldown = compound.getInt("AttackCooldown");
		rageAttacks = compound.getInt("Rage");
		this.spiritLevel = compound.getInt("SpiritLevel");

	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("AttackCooldown", attackCooldown);
		compound.putInt("Rage", rageAttacks);
		compound.putInt("SpiritLevel", this.spiritLevel);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MYFSounds.bellringerIdle.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return MYFSounds.bellringerHurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MYFSounds.bellringerDeath.get();
	}

	@Override
	public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
			if (itemstack.canEquip(EquipmentSlot.CHEST, this)) {
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
			} else if (itemstack.is(MYFItems.phantoplasm.get())) {
				if (this.spiritLevel >= 20) {
					return InteractionResult.FAIL;
				}
				this.heal((float) (this.getMaxHealth() * 0.1));
				this.setSpiritLevel(this.spiritLevel + 1);
				this.playSound(MYFSounds.bellringerIdle.get(), 1.0F, 1.5F);
				if (!pPlayer.getAbilities().instabuild) {
					itemstack.shrink(1);
				}
				for (int i = 0; i < 7; ++i) {
					double d0 = this.random.nextGaussian() * 0.02D;
					double d1 = this.random.nextGaussian() * 0.02D;
					double d2 = this.random.nextGaussian() * 0.02D;
					this.level().addParticle(ModParticleTypes.BIG_SOUL_FIRE.get(), this.getRandomX(1.0D),
							this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.mobInteract(pPlayer, pHand);
	}

	@Override
	public void die(DamageSource cause) {
		if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
			ItemStack phantoplasm = new ItemStack(MYFItems.phantoplasm.get());
			if (this.getTrueOwner() != null) {
				FlyingItem flyingItem = new FlyingItem(
						com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
						this.level(),
						this.getX(),
						this.getY() + 1.0D,
						this.getZ());
				flyingItem.setOwner(this.getTrueOwner());
				flyingItem.setItem(phantoplasm);
				flyingItem.setParticle(ModParticleTypes.BIG_SOUL_FIRE.get());
				flyingItem.setSecondsCool(30);

				this.level().addFreshEntity(flyingItem);
			} else {
				ItemEntity itemEntity = this.spawnAtLocation(phantoplasm);
				if (itemEntity != null) {
					itemEntity.setExtendedLifetime();

				}
			}
		}
		super.die(cause);
	}

	private static class BurstAttack extends Goal {
		private BellringerServant ringer;
		private LivingEntity target;
		private int attackRemaining, attackDelay, chosenAttack;

		public BurstAttack(BellringerServant ringer) {
			this.ringer = ringer;
		}

		@Override
		public boolean canUse() {
			return ringer.attackCooldown <= 0 && ringer.getTarget() != null && ringer.getTarget().isAlive();
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			ringer.attackCooldown = 2;
			attackDelay = 20;
			attackRemaining = 3 + ringer.rageAttacks;
			target = ringer.getTarget();
			chosenAttack = ringer.random.nextInt(2);
		}

		@Override
		public void tick() {
			ringer.attackCooldown = 2;
			attackDelay--;
			if (attackDelay <= 0) {
				attackDelay = 20;
				attackRemaining--;

				ringer.dingDong();

				performAttack();

				if (attackRemaining <= 0)
					stop();
			}
		}

		private void performAttack() {
			BlockPos tgt = target.blockPosition();
			double tx = tgt.getX() + 0.5;
			double tz = tgt.getZ() + 0.5;
			double ty = target.getY() + 0.1;
			if (!target.onGround() && !target.isInWater()) {
				Vec3 from = new Vec3(tx, ty, tz);
				BlockHitResult res = ringer.level().clip(new ClipContext(from, from.add(0, -1, 0),
						ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target));
				if (res.getType() != HitResult.Type.MISS)
					ty = res.getLocation().y;
				else
					ty -= 1;
			}
			switch (chosenAttack) {
				default:
				case 0:
					BlockPos self = ringer.blockPosition();
					double sx = self.getX();
					double sz = self.getZ();
					Direction dir = Direction.getNearest(tx - sx, 0, tz - sz);
					double cx = dir.getStepX();
					double cz = dir.getStepZ();

					for (int i = -4; i <= 4; i++) {
						ModProjectileLineEntity ghost = ringer.readyAttack();
						ghost.setUp(20, cx, 0, cz, tx - 7 * cx + i * cz, ty, tz - 7 * cz + i * cx);
						ringer.level().addFreshEntity(ghost);
					}
					break;
				case 1:
					for (int x = -1; x <= 1; x++) {
						for (int z = -1; z <= 1; z++) {
							ModProjectileLineEntity ghost = ringer.readyAttack();
							ghost.setUp(20, 0, -1, 0, tx + x, ty + 7, tz + z);
							ringer.level().addFreshEntity(ghost);
						}
					}
					break;
			}
		}

		@Override
		public void stop() {
			ringer.attackCooldown = 40 + ringer.random.nextInt(21);
		}

		@Override
		public boolean canContinueToUse() {
			return attackRemaining > 0 && target.isAlive();
		}

	}

	private static class RageAttack extends Goal {
		private BellringerServant ringer;
		private LivingEntity target;
		private int attackRemaining, attackDelay;
		private Direction dir;

		public RageAttack(BellringerServant ringer) {
			this.ringer = ringer;
		}

		@Override
		public boolean canUse() {
			return ringer.attackCooldown <= 0 && ringer.rageAttacks == 0
					&& ringer.getHealth() <= ringer.getMaxHealth() / 2 && ringer.getTarget() != null
					&& ringer.getTarget().isAlive();
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			ringer.attackCooldown = 2;
			ringer.rageAttacks = 1;
			attackDelay = 30;
			attackRemaining = 16;
			target = ringer.getTarget();

			BlockPos self = ringer.blockPosition();
			double sx = self.getX();
			double sz = self.getZ();
			BlockPos tgt = target.blockPosition();
			double tx = tgt.getX();
			double tz = tgt.getZ();
			dir = Direction.getNearest(tx - sx, 0, tz - sz);

			List<Entity> list = ringer.level().getEntities(ringer, ringer.getBoundingBox().inflate(16),
					e -> e instanceof LivingEntity && e.isAlive() && e.canChangeDimensions());
			list.add(target);
			for (Entity e : list) {
				if (!MobUtil.areAllies(ringer, (LivingEntity) e)) {
					((LivingEntity) e).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 1));
				}
			}
			ringer.dingDong();
			ringer.playSound(SoundEvents.BELL_RESONATE, 2, 1);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void tick() {
			ringer.attackCooldown = 2;
			attackDelay--;
			if (attackDelay <= 0) {
				attackDelay = 16;
				attackRemaining--;
				ringer.dingDong();
				BlockPos tgt = target.blockPosition();
				double tx = tgt.getX() + 0.5;
				double tz = tgt.getZ() + 0.5;
				double ty = tgt.getY() + 0.1;
				if (!target.onGround() && !target.isInWater()
						&& !ringer.level().getBlockState(tgt.below()).blocksMotion())
					ty -= 1;

				double cx = dir.getStepX();
				double cz = dir.getStepZ();

				int off = attackRemaining % 2 == 0 ? 1 : -1;
				for (int i = -5; i <= 5; i++) {
					ModProjectileLineEntity ghost = ringer.readyAttack();
					ghost.setUp(15 + off * i, cx, 0, cz, tx - 7 * cx + i * cz, ty, tz - 7 * cz + i * cx);
					ringer.level().addFreshEntity(ghost);
				}

				if (attackRemaining <= 0)
					stop();
			}
		}

		@Override
		public void stop() {
			ringer.attackCooldown = 40 + ringer.random.nextInt(21);
		}

		@Override
		public boolean canContinueToUse() {
			return attackRemaining > 0 && target.isAlive();
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
			return !mob.getMoveControl().hasWanted() && !BellringerServant.this.isStaying()
					&& mob.getRandom().nextInt(7) == 0;
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
			return mob.getTarget() != null && !mob.getMoveControl().hasWanted() && !BellringerServant.this.isStaying();
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

	class BellringerFollowGoal extends Goal {
		private final BellringerServant bellringerServant;
		private LivingEntity owner;
		private final Level level;
		private final double followSpeed;
		private final PathNavigation navigation;
		private int timeToRecalcPath;
		private final float maxDist;
		private final float minDist;
		private float oldWaterCost;
		private final boolean teleportToLeaves;

		public BellringerFollowGoal(BellringerServant bellringerServant, double speed, float minDist, float maxDist,
				boolean teleportToLeaves) {
			this.bellringerServant = bellringerServant;
			this.level = bellringerServant.level();
			this.followSpeed = speed;
			this.navigation = bellringerServant.getNavigation();
			this.minDist = minDist;
			this.maxDist = maxDist;
			this.teleportToLeaves = teleportToLeaves;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
			if (!(bellringerServant.getNavigation() instanceof GroundPathNavigation)
					&& !(bellringerServant.getNavigation() instanceof FlyingPathNavigation)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		public boolean canUse() {
			LivingEntity livingentity = this.bellringerServant.getTrueOwner();
			if (livingentity == null) {
				return false;
			} else if (livingentity.isSpectator()) {
				return false;
			} else if (livingentity instanceof net.minecraft.world.entity.Mob
					&& !(livingentity instanceof BellringerServant)) {
				return false;
			} else if (this.bellringerServant.distanceToSqr(livingentity) < (double) (this.minDist * this.minDist)) {
				return false;
			} else if (!this.bellringerServant.isFollowing()) {
				return false;
			} else if (this.bellringerServant.isStaying()) {
				return false;
			} else if (this.bellringerServant.getTarget() != null) {
				return false;
			} else {
				this.owner = livingentity;
				return true;
			}
		}

		public boolean canContinueToUse() {
			if (this.bellringerServant.getTarget() != null) {
				return false;
			} else if (this.navigation.isDone()) {
				return false;
			} else {
				return !(this.bellringerServant.distanceToSqr(this.owner) <= (double) (this.maxDist * this.maxDist));
			}
		}

		public void start() {
			this.timeToRecalcPath = 0;
			this.oldWaterCost = this.bellringerServant.getPathfindingMalus(BlockPathTypes.WATER);
			this.bellringerServant.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		}

		public void stop() {
			this.navigation.stop();
			this.bellringerServant.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
		}

		public void tick() {
			this.bellringerServant.getLookControl().setLookAt(this.owner, 10.0F,
					(float) this.bellringerServant.getMaxHeadXRot());
			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10;
				if (this.bellringerServant.distanceTo(this.owner) > 8.0D) {
					double x = Math.floor(this.owner.getX()) - 2;
					double y = Math.floor(this.owner.getBoundingBox().minY);
					double z = Math.floor(this.owner.getZ()) - 2;
					for (int l = 0; l <= 4; ++l) {
						for (int i1 = 0; i1 <= 4; ++i1) {
							if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
									&& this.validPosition(BlockPos.containing(x + l, y + 2, z + i1))) {
								float a = (float) ((x + l) + 0.5F);
								float b = (float) ((z + i1) + 0.5F);
								this.bellringerServant.getMoveControl().setWantedPosition(a, y, b, this.followSpeed);
								this.navigation.stop();
							}
						}
					}
				}
				if (this.bellringerServant.distanceToSqr(this.owner) > 144.0) {
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
				this.bellringerServant.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D,
						this.bellringerServant.getYRot(),
						this.bellringerServant.getXRot());
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
					BlockPos blockpos = pos.subtract(this.bellringerServant.blockPosition());
					return this.level.noCollision(this.bellringerServant,
							this.bellringerServant.getBoundingBox().move(blockpos));
				}
			}
		}

		protected boolean validPosition(BlockPos pos) {
			net.minecraft.world.level.block.state.BlockState blockstate = this.level.getBlockState(pos);
			return (blockstate.canSurvive(this.level, pos) && this.level.isEmptyBlock(pos.above())
					&& this.level.isEmptyBlock(pos.above(2)));
		}

		private int getRandomNumber(int min, int max) {
			return this.bellringerServant.getRandom().nextInt(max - min + 1) + min;
		}
	}

}
