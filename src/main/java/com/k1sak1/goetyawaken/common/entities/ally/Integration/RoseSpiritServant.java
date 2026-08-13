package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import java.util.EnumSet;
import javax.annotation.Nullable;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.AbstractWitherNecromancer.SummonFireSurroundGoal;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import lykrast.meetyourfight.MeetYourFight;
import lykrast.meetyourfight.registry.MYFSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class RoseSpiritServant extends Summoned {
	public static final int HP = 40, DMG = 16;
	private static final EntityDataAccessor<Byte> STATUS = SynchedEntityData.defineId(RoseSpiritServant.class,
			EntityDataSerializers.BYTE);
	public static final int HIDING = 0, RISING = 1, OUT = 2, ATTACKING = 3, RETRACTING = 4, HURT = 5,
			RETRACTING_HURT = 6;

	public int attackCooldown;

	public int prevStatus, animDur, animProg;

	public RoseSpiritServant(EntityType<? extends RoseSpiritServant> type, Level worldIn) {
		super(type, worldIn);
		moveControl = new VexMovementController(this).slowdown(0.2);
		xpReward = 5;
		prevStatus = getStatus();
		animDur = 1;
		animProg = 1;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(2, new HideAfterHit(this));
		goalSelector.addGoal(3, new BurstAttack(this));
		goalSelector.addGoal(6, new MoveAroundOwner(this, 0.35));
		goalSelector.addGoal(7, new MoveAroundTarget(this, 0.35));
		goalSelector.addGoal(8, new VexMoveRandomGoal(this, 0.25));
		targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
	}

	public static AttributeSupplier.Builder setCustomAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, AttributesConfig.RoseSpiritServantHealth.get())
				.add(Attributes.ATTACK_DAMAGE, AttributesConfig.RoseSpiritServantDamage.get())
				.add(Attributes.ARMOR, AttributesConfig.RoseSpiritServantArmor.get())
				.add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.RoseSpiritServantArmorToughness.get())
				.add(Attributes.FOLLOW_RANGE, 64);
	}

	@Override
	public void setConfigurableAttributes() {
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
				AttributesConfig.RoseSpiritServantHealth.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
				AttributesConfig.RoseSpiritServantDamage.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
				AttributesConfig.RoseSpiritServantArmor.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
				AttributesConfig.RoseSpiritServantArmorToughness.get());
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
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public void move(MoverType typeIn, Vec3 pos) {
		super.move(typeIn, pos);
		checkInsideBlocks();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && getStatus() == HIDING) {
			if (amount > 1)
				playSound(MYFSounds.aceOfIronProc.get(), 1, 1);
			return false;
		}
		if (super.hurt(source, amount)) {
			if (amount >= 3)
				setStatus(HURT);
			return true;
		}
		return false;
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		noPhysics = true;
		super.tick();
		noPhysics = false;
		setNoGravity(true);

		if (level().isClientSide) {
			if (prevStatus != getStatus()) {
				prevStatus = getStatus();
				animProg = 0;
				if (prevStatus == RISING || prevStatus == RETRACTING || prevStatus == RETRACTING_HURT)
					animDur = 10;

			} else if (animProg < animDur)
				animProg++;
		}
	}

	public float getAnimProgress(float partial) {
		return Mth.clamp((animProg + partial) / animDur, 0, 1);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(STATUS, (byte) 0);
	}

	public int getStatus() {
		return entityData.get(STATUS);
	}

	public void setStatus(int status) {
		entityData.set(STATUS, (byte) status);
	}

	@Override
	public void customServerAiStep() {
		if (attackCooldown > 0)
			attackCooldown--;
		super.customServerAiStep();
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Cooldown"))
			attackCooldown = compound.getInt("Cooldown");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Cooldown", attackCooldown);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MYFSounds.roseSpiritIdle.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return MYFSounds.roseSpiritHurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MYFSounds.roseSpiritDeath.get();
	}

	private ModProjectileLineEntity readyAttack() {
		ModProjectileLineEntity ghost = new ModProjectileLineEntity(level(), this);
		ghost.setOwner(this);
		ghost.setPos(getX(), getY() + 0.625, getZ());
		ghost.setVariant(ModProjectileLineEntity.VAR_ROSALYNE);
		return ghost;
	}

	private static class HideAfterHit extends Goal {
		private RoseSpiritServant mob;
		private int timer;

		public HideAfterHit(RoseSpiritServant mob) {
			this.mob = mob;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			mob.attackCooldown = 100 + mob.random.nextInt(61);
			timer = 60;
			mob.playSound(MYFSounds.roseSpiritHurtBig.get(), 1, 1);
		}

		@Override
		public void tick() {
			timer--;
			if (timer <= 0)
				mob.setStatus(HIDING);
			else if (timer == 10) {
				mob.setStatus(RETRACTING_HURT);
				double sx = mob.getX();
				double sy = mob.getY();
				double sz = mob.getZ();
				Vec3 dir = null;
				if (mob.getTarget() != null)
					dir = new Vec3(mob.getTarget().getX() - sx, mob.getTarget().getY() + 1 - sy,
							mob.getTarget().getZ() - sz);
				else
					dir = new Vec3(1, -0.25, 0);
				dir = dir.normalize();
				for (int i = 0; i < 8; i++) {
					ModProjectileLineEntity ghost = mob.readyAttack();
					ghost.setUp(1, dir.x, dir.y, dir.z, sx, sy, sz);
					mob.level().addFreshEntity(ghost);
					dir = dir.yRot(Mth.HALF_PI / 2);
				}
				mob.playSound(MYFSounds.roseSpiritShoot.get(), 1, 1);
			}
		}

		@Override
		public boolean canUse() {
			return mob.getStatus() == HURT || mob.getStatus() == RETRACTING_HURT;
		}
	}

	private static class MoveAroundOwner extends Goal {
		private RoseSpiritServant mob;
		private double speed;

		public MoveAroundOwner(RoseSpiritServant mob, double speed) {
			setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.mob = mob;
			this.speed = speed;
		}

		@Override
		public boolean canUse() {
			return mob.getTrueOwner() != null && !mob.getMoveControl().hasWanted();
		}

		@Override
		public void start() {
			LivingEntity target = mob.getTrueOwner();
			RandomSource rand = mob.getRandom();
			float angle = (rand.nextInt(4) + 2) * 10f * ((float) Math.PI / 180F);
			Vec3 offset = new Vec3(mob.getX() - target.getX(), 0, mob.getZ() - target.getZ()).normalize().yRot(angle);
			double distance = rand.nextDouble() * 2 + 4;
			double actSpeed = mob.distanceToSqr(mob.getTrueOwner()) > 100 ? 6 : speed;
			mob.getMoveControl().setWantedPosition(target.getX() + offset.x * distance,
					target.getY() + rand.nextDouble() * 2, target.getZ() + offset.z * distance, actSpeed);
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}

	}

	private static class BurstAttack extends Goal {
		private RoseSpiritServant mob;
		private LivingEntity target;
		private int attackRemaining, attackDelay, phase;

		public BurstAttack(RoseSpiritServant mob) {
			this.mob = mob;
		}

		@Override
		public boolean canUse() {
			return mob.attackCooldown <= 0 && mob.getTarget() != null && mob.getTarget().isAlive();
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void start() {
			mob.attackCooldown = 2;
			attackDelay = 10;
			attackRemaining = 2 + mob.random.nextInt(5);
			target = mob.getTarget();
			phase = 0;
			mob.setStatus(RISING);
		}

		@Override
		public void tick() {
			mob.attackCooldown = 2;
			attackDelay--;
			if (attackDelay <= 0) {
				switch (phase) {
					case 0:
						mob.setStatus(OUT);
						phase = 1;
						attackDelay = 20 + mob.random.nextInt(41);
						break;
					case 1:
						mob.setStatus(ATTACKING);
						phase = 2;
						attackDelay = 25;
						mob.playSound(MYFSounds.roseSpiritWarn.get(), 1, 1);
						break;
					case 2:
						attackDelay = 25;
						attackRemaining--;
						performAttack();
						if (attackRemaining <= 0) {
							phase = 3;
							mob.setStatus(OUT);
							attackDelay = 40 + mob.random.nextInt(41);
						}
						break;
					case 3:
						phase = 4;
						attackDelay = 10;
						mob.setStatus(RETRACTING);
						break;
					case 4:
						phase = 5;
						mob.setStatus(HIDING);
						break;
				}
			}
		}

		private void performAttack() {
			double sx = mob.getX();
			double sy = mob.getY();
			double sz = mob.getZ();

			Vec3 dir = new Vec3(target.getX() - sx, target.getY() + 1 - sy, target.getZ() - sz).normalize();
			ModProjectileLineEntity ghost = mob.readyAttack();
			ghost.setUp(1, dir.x, dir.y, dir.z, sx, sy, sz);
			mob.level().addFreshEntity(ghost);
			mob.playSound(MYFSounds.roseSpiritShoot.get(), 1, 1);
		}

		@Override
		public void stop() {
			mob.attackCooldown = 60 + mob.random.nextInt(41);
		}

		@Override
		public boolean canContinueToUse() {
			return phase <= 4 && target.isAlive() && mob.getStatus() != HURT && mob.getStatus() != RETRACTING_HURT;
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

	public class MoveAroundTarget extends Goal {
		private Mob mob;
		private double speed;

		public MoveAroundTarget(Mob mob, double speed) {
			setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.mob = mob;
			this.speed = speed;
		}

		@Override
		public boolean canUse() {
			return mob.getTarget() != null && !mob.getMoveControl().hasWanted();
		}

		@Override
		public void start() {
			LivingEntity target = mob.getTarget();
			RandomSource rand = mob.getRandom();
			float angle = (rand.nextInt(4) + 2) * 10f * Mth.DEG_TO_RAD;
			if (rand.nextBoolean())
				angle *= -1;
			Vec3 offset = new Vec3(mob.getX() - target.getX(), 0, mob.getZ() - target.getZ()).normalize().yRot(angle);
			double distance = rand.nextDouble() * 2 + 4;

			mob.getMoveControl().setWantedPosition(
					target.getX() + offset.x * distance,
					target.getY() + 1 + rand.nextDouble() * 2,
					target.getZ() + offset.z * distance,
					speed);
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}

	}
}
