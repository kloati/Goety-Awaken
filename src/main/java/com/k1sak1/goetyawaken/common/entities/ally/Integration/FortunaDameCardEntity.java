package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import java.util.List;
import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import lykrast.meetyourfight.registry.MYFSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class FortunaDameCardEntity extends Entity {
	private static final EntityDataAccessor<Byte> VARIANT = SynchedEntityData.defineId(FortunaDameCardEntity.class,
			EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> VARIANT_QUESTION = SynchedEntityData.defineId(
			FortunaDameCardEntity.class,
			EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> ANIMATION = SynchedEntityData.defineId(FortunaDameCardEntity.class,
			EntityDataSerializers.BYTE);
	private int phase, timer = 20, hideTime;
	private boolean correct;
	private static final int PHASE_START = 0, PHASE_SPIN = 1, PHASE_GOTODEST = 2, PHASE_ACTIVE = 3, PHASE_REVEAL = 4;
	public static final int START_TIME = 5 * 20 + 10, GOTODEST_TIME = 35, SPIN_TIME = 4 * 20, REVEAL_TIME = 3 * 20;
	private double spinX, spinY, spinZ, destX, destY, destZ;
	private int spinOffset;
	private static final Vec3 SPINVEC = new Vec3(3, 0, 0);
	public int clientAnim, animTimer;
	public static final int ANIM_NOTHERE = 0, ANIM_APPEAR = 1, ANIM_IDLE_SHOW = 2, ANIM_HIDE = 3, ANIM_IDLE_HIDDEN = 4,
			ANIM_IDLE_QUESTION = 5, ANIM_REVEAL = 6, ANIM_HINT = 7;
	public static final int ANIM_APPEAR_DUR = 10, ANIM_HIDE_DUR = 10, ANIM_REVEAL_DUR = 20, ANIM_HINT_DUR = 30;

	public FortunaDameCardEntity(EntityType<? extends FortunaDameCardEntity> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
	}

	public FortunaDameCardEntity(Level worldIn, double x, double y, double z) {
		this(ModIntegrationRegistry.FORTUNA_DAME_CARD.get(), worldIn);
		setPos(x, y, z);
		xo = x;
		yo = y;
		zo = z;
	}

	public void setup(int variant, int correctVariant, boolean correct, int preflipTime, double spinX, double spinY,
			double spinZ, int spinOffset, double destX, double destY, double destZ) {
		setVariant(variant);
		setVariantQuestion(correctVariant);
		this.correct = correct;
		phase = PHASE_START;
		timer = START_TIME;
		hideTime = preflipTime;
		this.spinX = spinX;
		this.spinY = spinY;
		this.spinZ = spinZ;
		this.spinOffset = spinOffset;
		this.destX = destX;
		this.destY = destY;
		this.destZ = destZ;
		setAnimation(ANIM_NOTHERE);
		noPhysics = true;
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(VARIANT, (byte) 0);
		entityData.define(VARIANT_QUESTION, (byte) 0);
		entityData.define(ANIMATION, (byte) 0);
	}

	public void setVariant(int value) {
		entityData.set(VARIANT, (byte) value);
	}

	public int getVariant() {
		return entityData.get(VARIANT);
	}

	public void setVariantQuestion(int value) {
		entityData.set(VARIANT_QUESTION, (byte) value);
	}

	public int getVariantQuestion() {
		return entityData.get(VARIANT_QUESTION);
	}

	public void setAnimation(int value) {
		entityData.set(ANIMATION, (byte) value);
	}

	public int getAnimation() {
		return entityData.get(ANIMATION);
	}

	public boolean isCorrect() {
		return isAlive() && correct;
	}

	public boolean isInActivePhase() {
		return phase == PHASE_ACTIVE;
	}

	public void revealCard(LivingEntity revealer) {
		if (phase == PHASE_ACTIVE) {
			phase = PHASE_REVEAL;
			timer = REVEAL_TIME;
			List<FortunaDameCardEntity> others = level().getEntitiesOfClass(FortunaDameCardEntity.class,
					getBoundingBox().inflate(32), (card) -> card != this);
			for (var other : others)
				other.remove(RemovalReason.KILLED);
			setYRot(lookToward(revealer.getX(), revealer.getZ()));
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		if (!level().isClientSide) {
			timer--;
			if (timer <= 0) {
				switch (phase) {
					case PHASE_START:
						phase = PHASE_SPIN;
						timer = SPIN_TIME;
						break;
					case PHASE_SPIN:
						setAnimation(ANIM_IDLE_HIDDEN);
						phase = PHASE_GOTODEST;
						timer = GOTODEST_TIME;
						break;
					case PHASE_GOTODEST:
						phase = PHASE_ACTIVE;
						setAnimation(ANIM_IDLE_QUESTION);
						timer = 10 * 20;
						break;
					case PHASE_ACTIVE:
					case PHASE_REVEAL:
						remove(RemovalReason.KILLED);
						break;
				}
			}
			if (phase == PHASE_START) {
				if (timer == START_TIME - hideTime) {
					setAnimation(ANIM_APPEAR);
					playSound(MYFSounds.dameFortunaCardStart.get(), 1, 1);
				} else if (timer == START_TIME - hideTime - ANIM_APPEAR_DUR || (timer == 30 && isCorrect()))
					setAnimation(ANIM_IDLE_SHOW);
				else if (timer == 30 + ANIM_HINT_DUR && isCorrect())
					setAnimation(ANIM_HINT);
				else if (timer == 20)
					setAnimation(ANIM_HIDE);
				else if (timer == 20 - ANIM_HIDE_DUR)
					setAnimation(ANIM_IDLE_HIDDEN);
			} else if (phase == PHASE_REVEAL) {
				if (timer == REVEAL_TIME - 5)
					setAnimation(ANIM_REVEAL);
				if (timer == REVEAL_TIME - ANIM_REVEAL_DUR - 5) {
					setAnimation(ANIM_IDLE_SHOW);
					playSound(correct ? MYFSounds.dameFortunaCardRight.get() : MYFSounds.dameFortunaCardWrong.get(), 1,
							1);
					if (correct) {
						List<DameFortunaServant> dames = level().getEntitiesOfClass(DameFortunaServant.class,
								getBoundingBox().inflate(32), (dame) -> dame.isAlive());
						for (var d : dames)
							d.progressShuffle();
					}
				}
			}
			if (phase == PHASE_SPIN) {
				int spintimer = SPIN_TIME - timer;

				float angle = 0;
				if (spintimer > 50)
					angle = 80 - spintimer;
				else if (spintimer > 20)
					angle = spintimer - 20;
				angle = 0.5f * angle * angle;
				if (spintimer > 50)
					angle = 900 - angle;
				Vec3 offset = SPINVEC.yRot(((angle + spinOffset) % 360) * Mth.DEG_TO_RAD);
				double tx = spinX + offset.x;
				double tz = spinZ + offset.z;
				if (spintimer <= 20) {
					Vec3 speed = new Vec3(tx - getX(), spinY - getY(), tz - getZ());
					double len = speed.lengthSqr();
					double maxSpeed = (SPIN_TIME - timer) * 0.05;
					if (len > maxSpeed * maxSpeed)
						speed = speed.normalize().scale(maxSpeed);
					setDeltaMovement(speed);
				} else
					setDeltaMovement(tx - getX(), spinY - getY(), tz - getZ());
			} else if (phase == PHASE_GOTODEST) {
				int timeOffset = spinOffset / 36 + 1;
				Vec3 speed = new Vec3(destX - getX(), destY - getY(), destZ - getZ());
				if (timer <= timeOffset) {
					setDeltaMovement(speed);
				} else if (timer <= timeOffset + 10) {
					setDeltaMovement(speed.scale(1.0 / (timer - timeOffset)));
				} else
					setDeltaMovement(Vec3.ZERO);
			} else
				setDeltaMovement(Vec3.ZERO);
		}

		move(MoverType.SELF, getDeltaMovement());

		if (level().isClientSide)
			updateClientAnimation();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (phase == PHASE_ACTIVE && source.getEntity() != null && source.getEntity() instanceof LivingEntity) {
			phase = PHASE_REVEAL;
			timer = REVEAL_TIME;
			List<FortunaDameCardEntity> others = level().getEntitiesOfClass(FortunaDameCardEntity.class,
					getBoundingBox().inflate(32), (card) -> card.phase != PHASE_REVEAL);
			for (var other : others)
				other.remove(RemovalReason.KILLED);
			setYRot(lookToward(source.getEntity().getX(), source.getEntity().getZ()));
			return true;
		} else
			return false;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public float getPickRadius() {
		return 1;
	}

	protected float lookToward(double wantedX, double wantedZ) {
		double d0 = wantedX - getX();
		double d1 = wantedZ - getZ();
		return (float) (Mth.atan2(d1, d0) * (180 / Math.PI) - 90);
	}

	private void updateClientAnimation() {
		if (clientAnim != getAnimation()) {
			clientAnim = getAnimation();
			switch (clientAnim) {
				case ANIM_APPEAR:
					animTimer = ANIM_APPEAR_DUR;
					break;
				case ANIM_HIDE:
					animTimer = ANIM_HIDE_DUR;
					break;
				case ANIM_REVEAL:
					animTimer = ANIM_REVEAL_DUR;
					break;
				case ANIM_HINT:
					animTimer = ANIM_HINT_DUR;
					break;
			}
		} else if (animTimer > 0)
			animTimer--;
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putByte("Variant", (byte) getVariant());
		compound.putByte("VariantAsk", (byte) getVariantQuestion());
		compound.putInt("Phase", phase);
		compound.putInt("Timer", timer);
		compound.putInt("HTime", hideTime);
		compound.putBoolean("Correct", correct);
		compound.putDouble("spinX", spinX);
		compound.putDouble("spinY", spinY);
		compound.putDouble("spinZ", spinZ);
		compound.putInt("spinOff", spinOffset);
		compound.putDouble("destX", destX);
		compound.putDouble("destY", destY);
		compound.putDouble("destZ", destZ);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		setVariant(compound.getByte("Variant"));
		setVariantQuestion(compound.getByte("VariantAsk"));
		phase = compound.getInt("Phase");
		timer = compound.getInt("Timer");
		hideTime = compound.getInt("HTime");
		correct = compound.getBoolean("Correct");
		spinX = compound.getDouble("spinX");
		spinY = compound.getDouble("spinY");
		spinZ = compound.getDouble("spinZ");
		spinOffset = compound.getInt("spinOff");
		destX = compound.getDouble("destX");
		destY = compound.getDouble("destY");
		destZ = compound.getDouble("destZ");
	}

	@Override
	protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return 1f;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
