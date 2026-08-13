package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import java.util.EnumSet;
import javax.annotation.Nullable;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.Polarice3.Goety.utils.MobUtil;
import lykrast.meetyourfight.registry.MYFItems;
import lykrast.meetyourfight.registry.MYFSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class DameFortunaServant extends Summoned implements PowerableMob {
	public static final int HP = 300, DMG = 16;

	private static final float REVEAL_CORRECT_CHANCE = 0.5F;

	private static final EntityDataAccessor<Byte> STATUS = SynchedEntityData.defineId(DameFortunaServant.class,
			EntityDataSerializers.BYTE);

	public static final int PHASE_1 = 0, SHUFFLE_1 = 1, PHASE_2 = 2, SHUFFLE_2 = 3, PHASE_3 = 4, SHUFFLE_3 = 5,
			DEATH = 6;

	private static final float TRESHOLD_1 = 2f / 3, TRESHOLD_2 = 1f / 3, TRESHOLD_3 = 1f / 10;

	private static final float RESET_1 = (1 + TRESHOLD_1) / 2, RESET_2 = (TRESHOLD_1 + TRESHOLD_2) / 2,
			RESET_3 = (TRESHOLD_2 + TRESHOLD_3) / 2;

	private static final int ATK_DICE = 0, ATK_SPIN = 1, ATK_CHIPS_CIRCLE = 2, ATK_CHIPS_STRAFE = 3;

	public static final int ANIM_IDLE = 0, ANIM_CHIPS_WINDUP = 1, ANIM_CHIPS_LAUNCH = 2, ANIM_DICE_WINDUP = 3,
			ANIM_DICE_LAUNCH = 4, ANIM_SPIN = 5, ANIM_SPIN_POSE = 6,
			ANIM_SNAP_PRE = 7, ANIM_SNAP_POST = 8, ANIM_CARD_WAIT = 9, ANIM_FINALE = 10, ANIM_CLAP = 11;
	private static final int PHASE_MASK = 0b111, ANIMATION_MASK = ~PHASE_MASK;
	private int attackCooldown, nextAttack, shuffleAttackWait;
	private int phase;
	private boolean hasSpawnedShuffle = false;

	public int headTargetPitch, headTargetYaw, headTargetRoll;
	public int headRotationTimer;
	public float headRotationProgress, headRotationProgressLast;
	public int clientAnim, prevAnim, animProg, animDur, spinTime, headRegrowTime;
	public float spinAngle, spinPrev;

	public DameFortunaServant(EntityType<? extends DameFortunaServant> type, Level worldIn) {
		super(type, worldIn);
		moveControl = new VexMovementController(this);
		phase = 0;
		headRotationTimer = 30;
		headTargetPitch = 0;
		headTargetYaw = 0;
		headTargetRoll = 0;
		headRotationProgress = 1;
		headRotationProgressLast = 1;
		clientAnim = ANIM_IDLE;
		prevAnim = ANIM_IDLE;
		animProg = 1;
		animDur = 1;
		spinAngle = 0;
		spinPrev = 0;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new DoTheShuffle(this));
		goalSelector.addGoal(2, new WaitShuffle(this));
		goalSelector.addGoal(3, new EndPose(this));
		goalSelector.addGoal(4, new SpinAttack(this));
		goalSelector.addGoal(5, new DiceAttack(this));
		goalSelector.addGoal(6, new ChipsAttack(this));
		goalSelector.addGoal(7, new MoveAroundTarget(this, 1));
		goalSelector.addGoal(8, new VexMoveRandomGoal(this, 0.25));
		targetSelector.addGoal(2, new HurtByTargetGoal(this));
	}

	@Override
	public void followGoal() {
		this.goalSelector.addGoal(5, new FortunaFollowGoal(this, 1.0D, 2.0F, 10.0F, true));
	}

	public static AttributeSupplier.Builder setCustomAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, AttributesConfig.DameFortunaServantHealth.get())
				.add(Attributes.ATTACK_DAMAGE, AttributesConfig.DameFortunaServantDamage.get())
				.add(Attributes.ARMOR, AttributesConfig.DameFortunaServantArmor.get())
				.add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.DameFortunaServantArmorToughness.get())
				.add(Attributes.FOLLOW_RANGE, 64);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return setCustomAttributes();
	}

	@Override
	public void setConfigurableAttributes() {
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
				AttributesConfig.DameFortunaServantHealth.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
				AttributesConfig.DameFortunaServantDamage.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR),
				AttributesConfig.DameFortunaServantArmor.get());
		MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
				AttributesConfig.DameFortunaServantArmorToughness.get());
	}

	@Override
	public int getSummonLimit(LivingEntity owner) {
		return com.k1sak1.goetyawaken.Config.DAME_FORTUNA_SERVANT_LIMIT.get();
	}

	@Override
	public void move(MoverType typeIn, Vec3 pos) {
		super.move(typeIn, pos);
		checkInsideBlocks();
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		noPhysics = true;
		super.tick();
		noPhysics = false;
		setNoGravity(true);

		if (level().isClientSide) {
			int newanim = getAnimation();
			if (clientAnim != newanim) {
				prevAnim = clientAnim;
				clientAnim = newanim;
				animProg = 0;
				animDur = 10;
				if (clientAnim == ANIM_DICE_LAUNCH) {
					animDur = 4;
					headRegrowTime = 11;
					headRotationTimer = 0;
				} else if (clientAnim == ANIM_CHIPS_LAUNCH)
					animDur = 4;
				else if (clientAnim == ANIM_DICE_WINDUP)
					animDur = 8;
				else if (clientAnim == ANIM_SNAP_POST || clientAnim == ANIM_CLAP)
					animDur = 2;
			} else if (animProg < animDur)
				animProg++;

			spinPrev = spinAngle;
			if (clientAnim == ANIM_SPIN) {
				if (spinTime < 20) {
					spinTime++;
				} else {
					spinAngle += 36;
					if (spinAngle >= 360)
						spinAngle = 0;
				}
			} else {
				if (spinAngle > 0) {
					spinAngle += 36;
					if (spinAngle >= 360)
						spinAngle = 0;
				} else if (spinTime > 0) {
					spinTime--;
				}
			}

			if (headRegrowTime > 0)
				headRegrowTime--;
			if (clientAnim != ANIM_DICE_WINDUP)
				headRotationTimer--;
			if (headRotationTimer <= 0) {
				switch (getPhase()) {
					default:
					case PHASE_1:
					case SHUFFLE_1:
						headRotationTimer = 20 + random.nextInt(21);
						break;
					case PHASE_2:
					case SHUFFLE_2:
						headRotationTimer = 15 + random.nextInt(11);
						break;
					case PHASE_3:
					case SHUFFLE_3:
					case DEATH:
						headRotationTimer = 5 + random.nextInt(11);
				}
				rotateHead();
				headRotationProgress = 0;
				headRotationProgressLast = 0;
			} else {
				headRotationProgressLast = headRotationProgress;
				headRotationProgress = Math.min(1, headRotationProgress + 0.07f);
			}
		}
	}

	public float getAnimProgress(float partial) {
		return Mth.clamp((animProg + partial) / animDur, 0, 1);
	}

	public float getHeadRotationProgress(float partial) {
		return Mth.lerp(partial, headRotationProgressLast, headRotationProgress);
	}

	private float getEasedSpin(float progress) {

		return 0.9f * progress * progress;
	}

	public float getSpinAngle(float partial) {
		if (clientAnim == ANIM_SPIN) {

			if (spinTime < 20)
				return getEasedSpin(spinTime + partial);

			else
				return Mth.rotLerp(partial, spinPrev, spinAngle);
		}

		else {

			if (spinAngle > 0)
				return Mth.rotLerp(partial, spinPrev, spinAngle);

			else if (spinTime > 0)
				return 360 - getEasedSpin(spinTime - partial);
			else
				return 0;
		}
	}

	private void rotateHead() {
		boolean reverse = random.nextBoolean();
		int axis = random.nextInt(3);
		switch (axis) {
			case 0:
				if (reverse) {
					if (headTargetPitch <= 0)
						headTargetPitch = 3;
					else
						headTargetPitch--;
				} else
					headTargetPitch = (headTargetPitch + 1) % 4;
				break;
			case 1:
				if (reverse) {
					if (headTargetYaw <= 0)
						headTargetYaw = 3;
					else
						headTargetYaw--;
				} else
					headTargetYaw = (headTargetYaw + 1) % 4;
				break;
			case 2:
				if (reverse) {
					if (headTargetRoll <= 0)
						headTargetRoll = 3;
					else
						headTargetRoll--;
				} else
					headTargetRoll = (headTargetRoll + 1) % 4;
				break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
			MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		this.setConfigurableAttributes();
		setHealth(getMaxHealth());
		this.attackCooldown = 100;
		this.nextAttack = ATK_CHIPS_CIRCLE;
		this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 2));
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(STATUS, (byte) 0);
	}

	public int getAnimation() {
		return (entityData.get(STATUS) & ANIMATION_MASK) >> 3;
	}

	public void setAnimation(int animation) {
		int phase = entityData.get(STATUS) & PHASE_MASK;
		entityData.set(STATUS, (byte) ((animation << 3) | phase));
	}

	public int getPhase() {
		return entityData.get(STATUS) & PHASE_MASK;
	}

	public void setPhase(int phase) {
		int animation = entityData.get(STATUS) & ANIMATION_MASK;
		entityData.set(STATUS, (byte) (phase | animation));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && isPowered()) {
			if (amount > 1)
				playSound(MYFSounds.aceOfIronProc.get(), 1, 1);

			if (hasSpawnedShuffle) {
				java.util.List<FortunaDameCardEntity> cards = level().getEntitiesOfClass(
						FortunaDameCardEntity.class, getBoundingBox().inflate(32),
						FortunaDameCardEntity::isInActivePhase);
				if (!cards.isEmpty()) {
					FortunaDameCardEntity chosen = null;
					for (FortunaDameCardEntity card : cards) {
						if (card.isCorrect()) {
							chosen = card;
							break;
						}
					}
					if (chosen == null || random.nextFloat() >= REVEAL_CORRECT_CHANCE) {
						chosen = cards.get(random.nextInt(cards.size()));
					}
					LivingEntity revealer = source.getEntity() instanceof LivingEntity le ? le : this;
					chosen.revealCard(revealer);
				}
			}

			return false;
		} else if (amount > 1 && getPhase() == DEATH)
			return super.hurt(source, Math.max(getHealth() * 2, amount));
		else
			return super.hurt(source, amount);
	}

	@Override
	public boolean isPowered() {

		return getPhase() == SHUFFLE_1 || getPhase() == SHUFFLE_2 || getPhase() == SHUFFLE_3;
	}

	@Override
	public void customServerAiStep() {
		if (attackCooldown > 0)
			attackCooldown--;
		if (shuffleAttackWait > 0)
			shuffleAttackWait--;
		if (phase != getPhase())
			phase = getPhase();

		if (isShuffling() && tickCount % 10 == 0) {
			if (hasSpawnedShuffle
					&& level().getEntitiesOfClass(FortunaDameCardEntity.class, getBoundingBox().inflate(32))
							.isEmpty()) {

				if (phase == SHUFFLE_1) {
					setHealth(getMaxHealth() * RESET_1);
					setPhase(PHASE_1);
					phase = PHASE_1;
				} else if (phase == SHUFFLE_2) {
					setHealth(getMaxHealth() * RESET_2);
					setPhase(PHASE_2);
					phase = PHASE_2;
				} else if (phase == SHUFFLE_3) {
					setHealth(getMaxHealth() * RESET_3);
					setPhase(PHASE_3);
					phase = PHASE_3;
				}
			}
		}

		else if (tickCount > 10) {
			if (phase == PHASE_1 && getHealth() < getMaxHealth() * TRESHOLD_1) {
				setPhase(SHUFFLE_1);
				phase = SHUFFLE_1;
				hasSpawnedShuffle = false;
			} else if (phase == PHASE_2 && getHealth() < getMaxHealth() * TRESHOLD_2) {
				setPhase(SHUFFLE_2);
				phase = SHUFFLE_2;
				hasSpawnedShuffle = false;
			} else if (phase == PHASE_3 && getHealth() < getMaxHealth() * TRESHOLD_3) {
				setPhase(SHUFFLE_3);
				phase = SHUFFLE_3;
				hasSpawnedShuffle = false;
			}
		}
		super.customServerAiStep();
	}

	public void progressShuffle() {
		if (phase == SHUFFLE_1) {
			setPhase(PHASE_2);
			phase = PHASE_2;
		} else if (phase == SHUFFLE_2) {
			setPhase(PHASE_3);
			phase = PHASE_3;
		} else if (phase == SHUFFLE_3) {
			setPhase(DEATH);
			phase = DEATH;
		}
		attackCooldown = 20;
	}

	public boolean isAttackPhase() {
		return phase == PHASE_1 || phase == PHASE_2 || phase == PHASE_3;
	}

	public boolean isShuffling() {
		return phase == SHUFFLE_1 || phase == SHUFFLE_2 || phase == SHUFFLE_3;
	}

	private void rollNextAttack(int ignore) {
		int max = ATK_CHIPS_CIRCLE;
		if (phase == PHASE_2 || phase == PHASE_3)
			max = ATK_CHIPS_STRAFE;
		if (ignore >= 0) {
			nextAttack = random.nextInt(max);
			if (nextAttack >= ignore)
				nextAttack++;
		} else
			nextAttack = random.nextInt(max + 1);
	}

	private void cooldownNextAttack() {
		attackCooldown = 50 + random.nextInt(21);
	}
	/*
	 * private ProjectileLineEntity readyLine() {
	 * ProjectileLineEntity proj = new ProjectileLineEntity(level, this);
	 * proj.setOwner(this);
	 * proj.setPos(getX(), getEyeY() + 1, getZ());
	 * proj.setVariant(ProjectileLineEntity.VAR_DAME_FORTUNA);
	 * return proj;
	 * }
	 */

	private ModProjectileTargetedEntity readyTargeted() {
		ModProjectileTargetedEntity proj = new ModProjectileTargetedEntity(level(), this);
		proj.setOwner(this);
		proj.setPos(getX(), getEyeY() + 1, getZ());
		return proj;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Phase"))
			setPhase(compound.getByte("Phase"));
		if (compound.contains("AttackCooldown"))
			attackCooldown = compound.getInt("AttackCooldown");
		if (compound.contains("NxtAt"))
			nextAttack = compound.getInt("NxtAt");
		if (compound.contains("ChipsCooldown"))
			shuffleAttackWait = compound.getInt("ChipsCooldown");
		if (compound.contains("HasShuffled"))
			hasSpawnedShuffle = compound.getBoolean("HasShuffled");
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("Phase", (byte) getPhase());
		compound.putInt("AttackCooldown", attackCooldown);
		compound.putInt("NxtAt", nextAttack);
		compound.putInt("ChipsCooldown", shuffleAttackWait);
		compound.putBoolean("HasShuffled", hasSpawnedShuffle);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MYFSounds.dameFortunaIdle.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return MYFSounds.dameFortunaHurt.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MYFSounds.dameFortunaDeath.get();
	}

	@Override
	public void die(DamageSource cause) {
		if (this.level() instanceof ServerLevel serverLevel && !this.level().isClientSide) {
			ItemStack fortunesFavor = new ItemStack(MYFItems.fortunesFavor.get());
			if (this.getTrueOwner() != null) {
				FlyingItem flyingItem = new FlyingItem(
						com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
						this.level(),
						this.getX(),
						this.getY() + 1.0D,
						this.getZ());
				flyingItem.setOwner(this.getTrueOwner());
				flyingItem.setItem(fortunesFavor);
				flyingItem.setParticle(ParticleTypes.ENCHANT);
				flyingItem.setSecondsCool(30);
				this.level().addFreshEntity(flyingItem);
			} else {
				ItemEntity itemEntity = this.spawnAtLocation(fortunesFavor);
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

	private static class WaitShuffle extends StationaryAttack {

		private DameFortunaServant dame;

		public WaitShuffle(DameFortunaServant dame) {
			super(dame, 4);
			this.dame = dame;
		}

		@Override
		public void start() {
			super.start();
			dame.setAnimation(ANIM_CARD_WAIT);
		}

		@Override
		public void stop() {
			dame.setAnimation(ANIM_IDLE);
			dame.attackCooldown = 20;
		}

		@Override
		public boolean canContinueToUse() {
			return dame.isShuffling();
		}

		@Override
		public boolean canUse() {
			return dame.isShuffling() && dame.hasSpawnedShuffle;
		}

	}

	private static class DoTheShuffle extends StationaryAttack {

		private DameFortunaServant dame;
		private LivingEntity target;
		private int timer;

		public DoTheShuffle(DameFortunaServant dame) {
			super(dame, 4);
			this.dame = dame;
		}

		@Override
		public void start() {
			super.start();
			dame.attackCooldown = 2;
			target = dame.getTarget();
			dame.setAnimation(ANIM_SNAP_PRE);
			dame.playSound(MYFSounds.dameFortunaAttack.get(), dame.getSoundVolume(), dame.getVoicePitch());

			timer = Math.max(40, dame.shuffleAttackWait + 30);
		}

		@Override
		public void tick() {
			super.tick();
			dame.attackCooldown = 2;
			timer--;
			if (timer <= 0 && !dame.hasSpawnedShuffle) {
				dame.hasSpawnedShuffle = true;
				Direction dir = Direction.getNearest(target.getX() - dame.getX(), 0, target.getZ() - dame.getZ());
				Direction side = dir.getClockWise();
				int cards = 2;
				if (dame.phase == SHUFFLE_2)
					cards = 3;
				else if (dame.phase == SHUFFLE_3)
					cards = 4;

				int[] shuffled = new int[cards];
				for (int i = 0; i < cards; i++)
					shuffled[i] = i;
				shuffle(shuffled);

				int correct = dame.random.nextInt(cards);

				BlockPos center = BlockPos.containing(dame.getX(), target.getY() + 1, dame.getZ());
				Vec3 start = new Vec3(center.getX() - side.getStepX() * 1.5 * (cards - 1), center.getY(),
						center.getZ() - side.getStepZ() * 1.5 * (cards - 1));
				int sus = dame.random.nextInt(1000) == 0 ? dame.random.nextInt(cards) : -1;
				for (int i = 0; i < cards; i++) {
					FortunaDameCardEntity card = new FortunaDameCardEntity(dame.level(),
							start.x + 3 * i * side.getStepX(),
							start.y, start.z + 3 * i * side.getStepZ());
					card.setYRot(dir.toYRot());

					int angleOffset = (i * (360 / cards) + 360 - (int) dir.toYRot()) % 360;
					card.setup(i == sus ? 4 : i, correct == sus ? 4 : correct, i == correct, i * 10 + 5,
							center.getX(), center.getY() + 3, center.getZ(), angleOffset,
							start.x + 3 * shuffled[i] * side.getStepX(), start.y,
							start.z + 3 * shuffled[i] * side.getStepZ());
					dame.level().addFreshEntity(card);
				}

				timer = FortunaDameCardEntity.START_TIME;
			} else if (timer == 10 && !dame.hasSpawnedShuffle) {

				dame.playSound(MYFSounds.dameFortunaSnap.get(), 2.0F,
						(dame.random.nextFloat() - dame.random.nextFloat()) * 0.1F + 1.0F);
				dame.setAnimation(ANIM_SNAP_POST);
			} else if (timer == (FortunaDameCardEntity.START_TIME - 20) && dame.hasSpawnedShuffle) {
				dame.setAnimation(ANIM_IDLE);
			}
		}

		private void shuffle(int[] arr) {
			for (int i = 0; i < arr.length - 1; i++) {
				int j = dame.random.nextInt(i, arr.length);
				int swap = arr[i];
				arr[i] = arr[j];
				arr[j] = swap;
			}
		}

		@Override
		public boolean canUse() {
			return dame.isShuffling() && !dame.hasSpawnedShuffle && dame.getTarget() != null
					&& dame.getTarget().isAlive();
		}

		@Override
		public boolean canContinueToUse() {
			return !dame.hasSpawnedShuffle || timer > 0;
		}
	}

	private static class DiceAttack extends StationaryAttack {
		private DameFortunaServant dame;
		private LivingEntity target;
		private int attackRemaining, attackDelay;

		public DiceAttack(DameFortunaServant dame) {
			super(dame);
			this.dame = dame;
		}

		@Override
		public boolean canUse() {
			return dame.nextAttack == ATK_DICE && dame.isAttackPhase() && dame.attackCooldown <= 0
					&& dame.getTarget() != null && dame.getTarget().isAlive();
		}

		@Override
		public boolean canContinueToUse() {
			return dame.nextAttack == ATK_DICE && (attackDelay > 0 || attackRemaining > 0) && target.isAlive()
					&& dame.isAttackPhase();
		}

		@Override
		public void start() {
			super.start();
			target = dame.getTarget();
			dame.setAnimation(ANIM_DICE_WINDUP);
			attackDelay = 30;
			attackRemaining = getAttackCount();
			dame.playSound(MYFSounds.dameFortunaAttack.get(), dame.getSoundVolume(), dame.getVoicePitch());
		}

		private int getAttackCount() {
			if (dame.phase == PHASE_2)
				return 4 + dame.random.nextInt(3);
			else if (dame.phase == PHASE_3)
				return 8 + dame.random.nextInt(4);
			return 2 + dame.random.nextInt(2);
		}

		@Override
		public void tick() {
			super.tick();
			attackDelay--;
			if (attackDelay <= 0 && attackRemaining > 0) {
				attackRemaining--;
				dame.setAnimation(ANIM_DICE_LAUNCH);
				performAttack();
			}
			if (attackDelay == (dame.phase == PHASE_3 ? 8 : 10) && attackRemaining > 0)
				dame.setAnimation(ANIM_DICE_WINDUP);
		}

		private void performAttack() {
			double tx = target.getX();
			double ty = target.getY();
			double tz = target.getZ();
			attackDelay = attackRemaining == 0 ? 40 : dame.phase == PHASE_3 ? 12 : 20;

			dame.shuffleAttackWait = Math.max(dame.shuffleAttackWait, 30);
			Vec3 offset = new Vec3(dame.getX() - tx, 0, dame.getZ() - tz).normalize()
					.yRot(Mth.wrapDegrees(dame.random.nextFloat() * 60 - 30) * Mth.DEG_TO_RAD);
			double bombX = tx + offset.x * 3;
			double bombY = ty + 0.5;
			double bombZ = tz + offset.z * 3;
			if (target.onGround())
				bombY += 0.75;
			FortunaDameBomb bomb = new FortunaDameBomb(dame.level(), dame.getX(), dame.getY() + 2, dame.getZ(),
					dame);
			int dettime = dame.phase == PHASE_1 ? 0 : dame.random.nextInt(11);
			bomb.setup(25 + dettime, 15 + dettime, bombX, bombY, bombZ);
			dame.level().addFreshEntity(bomb);
			dame.playSound(MYFSounds.dameFortunaShoot.get(), 2.0F,
					(dame.random.nextFloat() - dame.random.nextFloat()) * 0.2F + 1.0F);
		}

		@Override
		public void stop() {
			dame.cooldownNextAttack();
			dame.rollNextAttack(ATK_DICE);
			if (dame.isAttackPhase())
				dame.setAnimation(ANIM_IDLE);
		}

	}

	private static class ChipsAttack extends StationaryAttack {
		private DameFortunaServant dame;
		private LivingEntity target;
		private int attackRemaining, attackDelay, chosenPattern, circleDelay, circleDirection;
		private int clapTime, midStrafe;

		public ChipsAttack(DameFortunaServant dame) {
			super(dame);
			this.dame = dame;
		}

		@Override
		public boolean canUse() {
			return dame.nextAttack >= ATK_CHIPS_CIRCLE && dame.nextAttack <= ATK_CHIPS_STRAFE && dame.isAttackPhase()
					&& dame.attackCooldown <= 0 && dame.getTarget() != null && dame.getTarget().isAlive();
		}

		@Override
		public boolean canContinueToUse() {
			return dame.nextAttack >= ATK_CHIPS_CIRCLE && dame.nextAttack <= ATK_CHIPS_STRAFE
					&& (attackDelay > 0 || attackRemaining > 0) && target.isAlive() && dame.isAttackPhase();
		}

		@Override
		public void start() {
			super.start();
			target = dame.getTarget();
			dame.setAnimation(ANIM_CHIPS_WINDUP);
			attackDelay = 20;
			dame.playSound(MYFSounds.dameFortunaAttack.get(), dame.getSoundVolume(), dame.getVoicePitch());
			chosenPattern = dame.nextAttack;
			switch (chosenPattern) {
				default:
				case ATK_CHIPS_CIRCLE:
					midStrafe = 0;
					if (dame.phase == PHASE_3) {
						attackRemaining = 3 + dame.random.nextInt(2);
						midStrafe = 4 + dame.random.nextInt(2);
						attackRemaining += midStrafe;
					} else if (dame.phase == PHASE_2)
						attackRemaining = 3 + dame.random.nextInt(2);
					else
						attackRemaining = 3;

					circleDelay = 15 * (midStrafe > 0 ? attackRemaining - midStrafe : attackRemaining) + 5;
					circleDirection = dame.random.nextBoolean() ? 1 : -1;

					clapTime = midStrafe > 0 ? 7 : 32;
					break;
				case ATK_CHIPS_STRAFE:
					attackRemaining = 2;
					clapTime = 17;
					break;
				case 0:

					attackRemaining = 2;
					break;
			}
		}

		@Override
		public void tick() {
			super.tick();
			attackDelay--;
			if (attackRemaining > 0 && attackRemaining != midStrafe) {
				if (attackDelay <= 0) {
					attackRemaining--;
					dame.setAnimation(ANIM_CHIPS_LAUNCH);
					performAttack();
				} else if (attackDelay == 15)
					dame.setAnimation(ANIM_CHIPS_WINDUP);
			} else {

				if (attackDelay == clapTime)
					dame.setAnimation(ANIM_CLAP);
				else if (attackDelay == clapTime - 2)
					dame.playSound(MYFSounds.dameFortunaClap.get(), 2.0F,
							(dame.random.nextFloat() - dame.random.nextFloat()) * 0.1F + 1.0F);

				else if (attackDelay <= 0 && attackRemaining > 0 && attackRemaining == midStrafe) {
					attackRemaining--;
					midStrafe = 0;
					circleDelay = 15 * attackRemaining + 5;
					clapTime = 32;
					attackDelay = 20;
					rotateAroundTarget();
				}
			}
		}

		private void performAttack() {
			attackDelay = 45;
			if (chosenPattern == ATK_CHIPS_CIRCLE && attackRemaining > 0)
				attackDelay = 20;

			switch (chosenPattern) {
				default:
				case ATK_CHIPS_CIRCLE:
					fireChipsCircle(8, 1,
							circleDelay + 5 * (midStrafe > 0 ? attackRemaining - midStrafe : attackRemaining));
					if (attackRemaining > 0 && attackRemaining != midStrafe)
						rotateAroundTarget();
					break;
				case ATK_CHIPS_STRAFE:
					if (attackRemaining == 1)
						fireChipsStack(dame.phase == PHASE_3 ? 20 : 16);
					else
						fireChipsCircle(8, (dame.phase == PHASE_3 ? 5 : 3) + dame.random.nextInt(2), 35);
					break;
				case 0:

					attackRemaining = 2;
					break;
			}
		}

		private void rotateAroundTarget() {

			float angle = (dame.random.nextInt(4) + 4) * 10f * Mth.DEG_TO_RAD * circleDirection;
			Vec3 offset = new Vec3(dame.getX() - target.getX(), 0, dame.getZ() - target.getZ()).normalize().yRot(angle);
			double distance = dame.random.nextDouble() * 2 + 4;

			dame.getMoveControl().setWantedPosition(
					target.getX() + offset.x * distance,
					target.getY() + 1 + dame.random.nextDouble() * 2,
					target.getZ() + offset.z * distance,
					3);
		}

		private void fireChipsStack(int number) {
			Vec3 perp = dame.getLookAngle().cross(new Vec3(0, 1, 0)).normalize();
			double sy = dame.getY() + 1;

			dame.shuffleAttackWait = Math.max(dame.shuffleAttackWait, 38 + number * 6);

			for (int dir = -1; dir <= 1; dir += 2) {
				double sx = dame.getX() + perp.x * dir;
				double sz = dame.getZ() + perp.z * dir;
				int intialdelay = dir == -1 ? 35 : 38;

				for (int i = 0; i < number; i++) {
					ModProjectileTargetedEntity proj = dame.readyTargeted();
					proj.setPos(sx, sy + i * 0.125, sz);
					proj.setUp(intialdelay + (number - i - 1) * 6, 15, target, 0.75, sx, sy + i * 0.25 + 0.25, sz,
							dir * -20);
					dame.level().addFreshEntity(proj);
				}
			}

			dame.playSound(MYFSounds.dameFortunaChipsStart.get(), 2.0F,
					(dame.random.nextFloat() - dame.random.nextFloat()) * 0.2F + 1.0F);
		}

		private void fireChipsCircle(int chips, int circles, int delay) {

			Vec3 perp = dame.getLookAngle().cross(new Vec3(0, 1, 0)).normalize();
			double sy = dame.getY() + 1;
			float angle = Mth.TWO_PI / chips;

			dame.shuffleAttackWait = Math.max(dame.shuffleAttackWait, 35 + 15 * (circles - 1));

			double damex = dame.getX();
			double damez = dame.getZ();
			double sx = damex;
			double sz = damez;

			for (int c = 0; c < circles; c++) {
				Vec3 offset = perp;
				for (int i = 0; i < chips; i++) {
					ModProjectileTargetedEntity proj = dame.readyTargeted();
					proj.setPos(sx, sy + (c * chips + i) * 0.125, sz);
					proj.setUp(delay + 15 * c, 15, target, 1, damex + 2 * offset.x, sy + 1 + c, damez + 2 * offset.z);
					dame.level().addFreshEntity(proj);
					offset = offset.yRot(angle);
				}
			}

			dame.playSound(MYFSounds.dameFortunaChipsStart.get(), 2.0F,
					(dame.random.nextFloat() - dame.random.nextFloat()) * 0.2F + 1.0F);
		}

		@Override
		public void stop() {
			dame.cooldownNextAttack();
			dame.rollNextAttack(chosenPattern);

			if (dame.isAttackPhase())
				dame.setAnimation(ANIM_IDLE);
		}

	}

	private static class SpinAttack extends Goal {
		private DameFortunaServant dame;
		private int timer, chipsLeft, attackPhase;
		private double holdx, holdy, holdz;

		public SpinAttack(DameFortunaServant dame) {
			this.dame = dame;
			setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public boolean canUse() {
			return dame.nextAttack == ATK_SPIN && dame.isAttackPhase() && dame.attackCooldown <= 0
					&& dame.getTarget() != null && dame.getTarget().isAlive();
		}

		@Override
		public boolean canContinueToUse() {
			return canUse() && (chipsLeft > 0 || timer > 0);
		}

		@Override
		public void start() {
			timer = 20;
			chipsLeft = 5 + dame.random.nextInt(3);
			if (dame.phase == PHASE_2)
				chipsLeft = 10 + dame.random.nextInt(5);
			else if (dame.phase == PHASE_3)
				chipsLeft = 20 + dame.random.nextInt(9);
			attackPhase = 0;
			holdx = dame.getX();
			holdy = dame.getTarget().getY() + 1;
			holdz = dame.getZ();
			dame.moveControl.setWantedPosition(holdx, holdy, holdz, 1);
			dame.setAnimation(ANIM_SPIN);
			dame.playSound(MYFSounds.dameFortunaSpinStart.get(), 2, 1);
			dame.playSound(MYFSounds.dameFortunaAttack.get(), dame.getSoundVolume(), dame.getVoicePitch());
		}

		@Override
		public void tick() {
			timer--;
			LivingEntity target = dame.getTarget();

			if (target == null)
				return;

			if (attackPhase == 0) {
				dame.moveControl.setWantedPosition(holdx, holdy, holdz, 1);
				if (timer <= 0) {
					attackPhase = 1;
					timer = 10;
				}
			}

			else if (attackPhase == 1) {
				dame.moveControl.setWantedPosition(target.getX(), target.getY() + 1, target.getZ(), 0.4);
				if (timer <= 0) {
					fireChips(target);
					chipsLeft--;
					if (chipsLeft > 0) {
						timer = dame.phase == PHASE_3 ? 6 : dame.phase == PHASE_2 ? 12 : 20;
					} else {
						attackPhase = 2;
						timer = 30;
						holdx = dame.getX();
						holdy = target.getY() + 1;
						holdz = dame.getZ();
						dame.moveControl.setWantedPosition(holdx, holdy, holdz, 1);
						dame.setAnimation(ANIM_SPIN_POSE);
						dame.playSound(MYFSounds.dameFortunaSpinStop.get(), 2, 1);
					}
				}
			}

			else {
				dame.moveControl.setWantedPosition(holdx, holdy, holdz, 1);
			}
		}

		private void fireChips(LivingEntity target) {

			Vec3 perp = dame.getLookAngle().cross(new Vec3(0, 1, 0)).normalize();
			perp = perp.yRot(dame.random.nextFloat() * Mth.TWO_PI);
			double sy = dame.getY() + 1;

			dame.shuffleAttackWait = Math.max(dame.shuffleAttackWait, 10);

			double damex = dame.getX();
			double damez = dame.getZ();
			ModProjectileTargetedEntity proj = dame.readyTargeted();
			proj.setPos(damex, sy, damez);
			proj.setUp(10, 15, target, 1, damex + 1 * perp.x, sy + 1, damez + 1 * perp.z);
			dame.level().addFreshEntity(proj);
		}

		@Override
		public void stop() {
			dame.cooldownNextAttack();
			dame.rollNextAttack(ATK_SPIN);
			if (dame.isAttackPhase())
				dame.setAnimation(ANIM_IDLE);
		}

	}

	private static class EndPose extends StationaryAttack {
		private DameFortunaServant dame;
		private int patience;

		public EndPose(DameFortunaServant dame) {
			super(dame);
			this.dame = dame;
		}

		@Override
		public void start() {
			super.start();
			dame.setAnimation(ANIM_FINALE);

			patience = 30 * 20;
			if (dame.getTarget() != null)
				stationaryY = dame.getTarget().getY() + 1;
		}

		@Override
		public void tick() {
			super.tick();
			patience--;
			if (patience <= 0) {
				dame.setHealth(dame.getMaxHealth() * RESET_3);
				dame.setPhase(PHASE_3);
				dame.phase = PHASE_3;
				dame.attackCooldown = 20;
				dame.setAnimation(ANIM_IDLE);
			}
		}

		@Override
		public boolean canUse() {
			return dame.phase == DEATH;
		}
	}

	public static abstract class StationaryAttack extends Goal {

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

	class FortunaFollowGoal extends Goal {
		private final DameFortunaServant dameFortunaServant;
		private LivingEntity owner;
		private final Level level;
		private final double followSpeed;
		private final PathNavigation navigation;
		private int timeToRecalcPath;
		private final float maxDist;
		private final float minDist;
		private float oldWaterCost;
		private final boolean teleportToLeaves;

		public FortunaFollowGoal(DameFortunaServant dameFortunaServant, double speed, float minDist, float maxDist,
				boolean teleportToLeaves) {
			this.dameFortunaServant = dameFortunaServant;
			this.level = dameFortunaServant.level();
			this.followSpeed = speed;
			this.navigation = dameFortunaServant.getNavigation();
			this.minDist = minDist;
			this.maxDist = maxDist;
			this.teleportToLeaves = teleportToLeaves;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
			if (!(dameFortunaServant.getNavigation() instanceof GroundPathNavigation)
					&& !(dameFortunaServant.getNavigation() instanceof FlyingPathNavigation)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		public boolean canUse() {
			LivingEntity livingentity = this.dameFortunaServant.getTrueOwner();
			if (livingentity == null) {
				return false;
			} else if (livingentity.isSpectator()) {
				return false;
			} else if (livingentity instanceof net.minecraft.world.entity.Mob
					&& !(livingentity instanceof DameFortunaServant)) {
				return false;
			} else if (this.dameFortunaServant.distanceToSqr(livingentity) < (double) (this.minDist * this.minDist)) {
				return false;
			} else if (!this.dameFortunaServant.isFollowing()) {
				return false;
			} else if (this.dameFortunaServant.isStaying()) {
				return false;
			} else if (this.dameFortunaServant.getTarget() != null) {
				return false;
			} else {
				this.owner = livingentity;
				return true;
			}
		}

		public boolean canContinueToUse() {
			if (this.dameFortunaServant.getTarget() != null) {
				return false;
			} else if (this.navigation.isDone()) {
				return false;
			} else {
				return !(this.dameFortunaServant.distanceToSqr(this.owner) <= (double) (this.maxDist * this.maxDist));
			}
		}

		public void start() {
			this.timeToRecalcPath = 0;
			this.oldWaterCost = this.dameFortunaServant.getPathfindingMalus(BlockPathTypes.WATER);
			this.dameFortunaServant.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		}

		public void stop() {
			this.navigation.stop();
			this.dameFortunaServant.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
		}

		public void tick() {
			this.dameFortunaServant.getLookControl().setLookAt(this.owner, 10.0F,
					(float) this.dameFortunaServant.getMaxHeadXRot());
			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10;
				if (this.dameFortunaServant.distanceTo(this.owner) > 8.0D) {
					double x = Math.floor(this.owner.getX()) - 2;
					double y = Math.floor(this.owner.getBoundingBox().minY);
					double z = Math.floor(this.owner.getZ()) - 2;
					for (int l = 0; l <= 4; ++l) {
						for (int i1 = 0; i1 <= 4; ++i1) {
							if ((l < 1 || i1 < 1 || l > 3 || i1 > 3)
									&& this.validPosition(BlockPos.containing(x + l, y + 2, z + i1))) {
								float a = (float) ((x + l) + 0.5F);
								float b = (float) ((z + i1) + 0.5F);
								this.dameFortunaServant.getMoveControl().setWantedPosition(a, y, b, this.followSpeed);
								this.navigation.stop();
							}
						}
					}
				}
				if (this.dameFortunaServant.distanceToSqr(this.owner) > 144.0) {
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
				this.dameFortunaServant.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D,
						this.dameFortunaServant.getYRot(), this.dameFortunaServant.getXRot());
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
					BlockPos blockpos = pos.subtract(this.dameFortunaServant.blockPosition());
					return this.level.noCollision(this.dameFortunaServant,
							this.dameFortunaServant.getBoundingBox().move(blockpos));
				}
			}
		}

		protected boolean validPosition(BlockPos pos) {
			net.minecraft.world.level.block.state.BlockState blockstate = this.level.getBlockState(pos);
			return (blockstate.canSurvive(this.level, pos) && this.level.isEmptyBlock(pos.above())
					&& this.level.isEmptyBlock(pos.above(2)));
		}

		private int getRandomNumber(int min, int max) {
			return this.dameFortunaServant.getRandom().nextInt(max - min + 1) + min;
		}
	}

}
