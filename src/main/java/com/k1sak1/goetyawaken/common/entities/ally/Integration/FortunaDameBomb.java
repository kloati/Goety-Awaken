package com.k1sak1.goetyawaken.common.entities.ally.Integration;

import java.util.UUID;

import javax.annotation.Nullable;

import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class FortunaDameBomb extends Entity {

	private LivingEntity bomber;
	@Nullable
	private LivingEntity owner;
	@Nullable
	private UUID ownerUUID;
	private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(FortunaDameBomb.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> DATA_EXTRA_DAMAGE = SynchedEntityData.defineId(FortunaDameBomb.class,
			EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DATA_LINE_COUNT = SynchedEntityData.defineId(FortunaDameBomb.class,
			EntityDataSerializers.INT);
	private int moveFuse;

	private static final double GRAVITY = -0.08;
	private static final int DEFAULT_LINE_COUNT = 8;

	public FortunaDameBomb(EntityType<? extends FortunaDameBomb> entityTypeIn, Level worldIn) {
		super(entityTypeIn, worldIn);
	}

	public FortunaDameBomb(Level worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {
		this(ModIntegrationRegistry.FORTUNA_DAME_BOMB.get(), worldIn);
		this.setPos(x, y, z);
		xo = x;
		yo = y;
		zo = z;
		bomber = igniter;
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUUID = owner == null ? null : owner.getUUID();
	}

	@Nullable
	public LivingEntity getOwner() {
		if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
			Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
			if (entity instanceof LivingEntity) {
				this.owner = (LivingEntity) entity;
			}
		}
		return this.owner;
	}

	public void setup(int fuse, int moveFuse, double tx, double ty, double tz) {
		setFuse(fuse);
		this.moveFuse = moveFuse;
		double compensation = (-(moveFuse + 1) / 2.0) * GRAVITY;
		double scale = 1.0 / moveFuse;
		setDeltaMovement((tx - getX()) * scale, (ty - getY()) * scale + compensation, (tz - getZ()) * scale);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_FUSE_ID, 80);
		this.entityData.define(DATA_EXTRA_DAMAGE, 0.0F);
		this.entityData.define(DATA_LINE_COUNT, DEFAULT_LINE_COUNT);
	}

	public void setFuse(int value) {
		this.entityData.set(DATA_FUSE_ID, value);
	}

	public int getFuse() {
		return this.entityData.get(DATA_FUSE_ID);
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		if (moveFuse > 0) {
			setDeltaMovement(getDeltaMovement().add(0, GRAVITY, 0));
		}

		move(MoverType.SELF, getDeltaMovement());
		if (moveFuse > 0) {
			moveFuse--;
			if (moveFuse == 0)
				setDeltaMovement(0, 0, 0);
		}

		int remaining = getFuse() - 1;
		setFuse(remaining);

		if (remaining <= 0) {
			remove(RemovalReason.KILLED);
			playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
			if (!level().isClientSide)
				explode();
		} else {
			updateInWaterStateAndDoFluidPushing();
			if (level().isClientSide) {
				level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D,
						0.0D);
			}
		}

	}

	private void explode() {
		if (bomber == null)
			return;
		int count = getLineCount();
		float extraDamage = getExtraDamage();
		double angleStep = 2.0 * Math.PI / count;
		for (int i = 0; i < count; i++) {
			double angle = i * angleStep;
			double dx = Math.cos(angle);
			double dz = Math.sin(angle);
			projectile(dx, dz, extraDamage);
		}
	}

	private void projectile(double dx, double dz, float extraDamage) {
		ModProjectileLineEntity proj = new ModProjectileLineEntity(level(), bomber);
		if (this.getOwner() != null) {
			proj.setOwner(this.getOwner());
		} else {
			proj.setOwner(bomber);
		}
		proj.setPos(position());
		proj.setVariant(ModProjectileLineEntity.VAR_DAME_FORTUNA);
		proj.setExtraDamage(extraDamage);
		proj.setUp(1, dx, 0, dz, getX() + dx * 0.1, getY(), getZ() + dz * 0.1);
		level().addFreshEntity(proj);
	}

	public void setExtraDamage(float extraDamage) {
		this.entityData.set(DATA_EXTRA_DAMAGE, extraDamage);
	}

	public float getExtraDamage() {
		return this.entityData.get(DATA_EXTRA_DAMAGE);
	}

	public void setLineCount(int lineCount) {
		this.entityData.set(DATA_LINE_COUNT, Math.max(1, lineCount));
	}

	public int getLineCount() {
		return this.entityData.get(DATA_LINE_COUNT);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putShort("Fuse", (short) getFuse());
		compound.putShort("MFuse", (short) moveFuse);
		if (this.ownerUUID != null) {
			compound.putUUID("Owner", this.ownerUUID);
		}
		compound.putFloat("ExtraDamage", getExtraDamage());
		compound.putInt("LineCount", getLineCount());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		setFuse(compound.getShort("Fuse"));
		moveFuse = compound.getShort("MFuse");
		if (compound.contains("Owner")) {
			this.ownerUUID = compound.getUUID("Owner");
		}
		if (compound.contains("ExtraDamage")) {
			setExtraDamage(compound.getFloat("ExtraDamage"));
		}
		if (compound.contains("LineCount")) {
			setLineCount(compound.getInt("LineCount"));
		}
	}

	@Override
	protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
		return 0.15F;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
