package com.k1sak1.goetyawaken.common.entities.ally;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModShulkerBullet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
import javax.annotation.Nullable;
import org.joml.Vector3f;

public class ShulkerServant extends Summoned implements ICustomAttributes {
    private static final EntityDataAccessor<Direction> DATA_ATTACH_FACE_ID = SynchedEntityData
            .defineId(ShulkerServant.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Byte> DATA_PEEK_ID = SynchedEntityData.defineId(ShulkerServant.class,
            EntityDataSerializers.BYTE);
    private static final int TELEPORT_STEPS = 6;
    private float currentPeekAmountO;
    private float currentPeekAmount;
    private BlockPos clientOldAttachPosition;
    private int clientSideTeleportInterpolation;
    private int teleportCooldown = 0;
    private int peekTickCount = 0;
    private int peekTick = 0;
    private int prevPeekTickCount = 0;
    private boolean isShellClosed = true;
    private int shellToggleCooldown = 0;
    private Direction attachedFace = Direction.DOWN;
    private BlockPos commandPos;
    private int commandTick;
    private int hitCount = 0;
    private int hitResetTimer = 0;

    public ShulkerServant(EntityType<? extends ShulkerServant> type, Level world) {
        super(type, world);
        this.lookControl = new ShulkerServant.ShulkerLookControl(this);
        this.setAttachFace(Direction.DOWN);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ShulkerServant.ShulkerAttackGoal(this));
        this.goalSelector.addGoal(2, new ShulkerServant.ShulkerPeekGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ShulkerServant.ShulkerNearestAttackGoal(this));
        this.targetSelector.addGoal(3, new ShulkerServant.ShulkerDefenseAttackGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ShulkerServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, AttributesConfig.ShulkerServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ShulkerServantArmorToughness.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.ShulkerServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 1.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), 32.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ShulkerServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.ShulkerServantArmorToughness.get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHULKER_AMBIENT;
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setConfigurableAttributes();
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
            MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.setConfigurableAttributes();
        this.setHealth(this.getMaxHealth());
        return pSpawnData;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHULKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return this.isClosed() ? SoundEvents.SHULKER_HURT_CLOSED : SoundEvents.SHULKER_HURT;
    }

    public void playAmbientSound() {
        if (!this.isClosed()) {
            super.playAmbientSound();
        }
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.hitResetTimer > 0) {
            this.hitResetTimer--;
        } else {
            this.hitCount = 0;
        }

        this.prevPeekTickCount = this.peekTickCount;
        if (this.peekTickCount > 0) {
            --this.peekTickCount;
        }

        if (this.updatePeekAmount()) {
            this.onPeekAmountChange();
        }

        if (this.level().isClientSide) {
            if (this.clientSideTeleportInterpolation > 0) {
                --this.clientSideTeleportInterpolation;
            } else {
                this.clientOldAttachPosition = null;
            }
        }

        if (this.shellToggleCooldown > 0) {
            this.shellToggleCooldown--;
        } else {
            if (this.getTarget() == null && this.random.nextInt(500) == 0) {
                this.setPeekTickCount(20 + this.random.nextInt(41));
                this.isShellClosed = false;
                this.shellToggleCooldown = 100 + this.random.nextInt(200);
            } else if (!this.isShellClosed && this.peekTickCount <= 0) {
                this.isShellClosed = true;
            }
        }

        if (!this.level().isClientSide && !this.isPassenger()
                && !this.canStayAt(this.blockPosition(), this.getAttachFace())) {
            this.findNewAttachment();
        }

        if (this.isFollowing() && this.getTrueOwner() != null) {
            double distanceSqr = this.distanceToSqr(this.getTrueOwner());
            if (distanceSqr >= 256.0D && this.teleportCooldown <= 0) {
                this.teleportTowards(this.getTrueOwner(), 16.0D);
                this.teleportCooldown = 100;
            }

            if (this.teleportCooldown > 0) {
                this.teleportCooldown--;
            }
        }
    }

    private boolean updatePeekAmount() {
        this.currentPeekAmountO = this.currentPeekAmount;
        float f = (float) this.getRawPeekAmount() * 0.01F;
        if (this.currentPeekAmount == f) {
            return false;
        } else {
            if (this.currentPeekAmount > f) {
                this.currentPeekAmount = Mth.clamp(this.currentPeekAmount - 0.05F, f, 1.0F);
            } else {
                this.currentPeekAmount = Mth.clamp(this.currentPeekAmount + 0.05F, 0.0F, f);
            }

            return true;
        }
    }

    private void onPeekAmountChange() {
        this.reapplyPosition();
        float f = this.getPhysicalPeek(this.currentPeekAmount);
        float f1 = this.getPhysicalPeek(this.currentPeekAmountO);
        Direction direction = this.getAttachFace().getOpposite();
        float f2 = f - f1;
        if (!(f2 <= 0.0F)) {
            for (Entity entity : this.level().getEntities(this, Shulker.getProgressDeltaAabb(direction, f1, f)
                    .move(this.getX() - 0.5D, this.getY(), this.getZ() - 0.5D),
                    EntitySelector.NO_SPECTATORS.and((p_149771_) -> {
                        return !p_149771_.isPassengerOfSameVehicle(this);
                    }))) {
                if (!(entity instanceof Shulker) && !entity.noPhysics) {
                    entity.move(net.minecraft.world.entity.MoverType.SHULKER,
                            new Vec3((double) (f2 * (float) direction.getStepX()),
                                    (double) (f2 * (float) direction.getStepY()),
                                    (double) (f2 * (float) direction.getStepZ())));
                }
            }
        }
    }

    private float getPhysicalPeek(float pPeek) {
        return 0.5F - Mth.sin((0.5F + pPeek) * (float) Math.PI) * 0.5F;
    }

    boolean canStayAt(BlockPos pPos, Direction pFacing) {
        if (this.isPositionBlocked(pPos)) {
            return false;
        } else {
            Direction direction = pFacing.getOpposite();
            if (!this.level().loadedAndEntityCanStandOnFace(pPos.relative(pFacing), this, direction)) {
                return false;
            } else {
                AABB aabb = Shulker.getProgressAabb(direction, 1.0F).move(pPos).deflate(1.0E-6D);
                return this.level().noCollision(this, aabb);
            }
        }
    }

    private boolean isPositionBlocked(BlockPos pPos) {
        BlockState blockstate = this.level().getBlockState(pPos);
        if (blockstate.isAir()) {
            return false;
        } else {
            boolean flag = blockstate.is(Blocks.MOVING_PISTON) && pPos.equals(this.blockPosition());
            return !flag;
        }
    }

    private void findNewAttachment() {
        Direction direction = this.findAttachableSurface(this.blockPosition());
        if (direction != null) {
            this.setAttachFace(direction);
        } else {
            this.teleportSomewhere();
        }
    }

    private Direction findAttachableSurface(BlockPos pPos) {
        for (Direction direction : Direction.values()) {
            if (this.canStayAt(pPos, direction)) {
                return direction;
            }
        }

        return null;
    }

    protected boolean teleportSomewhere() {
        if (!this.isNoAi() && this.isAlive()) {
            BlockPos blockpos = this.blockPosition();

            for (int i = 0; i < 5; ++i) {
                BlockPos blockpos1 = blockpos.offset(Mth.randomBetweenInclusive(this.random, -8, 8),
                        Mth.randomBetweenInclusive(this.random, -8, 8), Mth.randomBetweenInclusive(this.random, -8, 8));
                if (blockpos1.getY() > this.level().getMinBuildHeight() && this.level().isEmptyBlock(blockpos1)
                        && this.level().getWorldBorder().isWithinBounds(blockpos1)
                        && this.level().noCollision(this, (new AABB(blockpos1)).deflate(1.0E-6D))) {
                    Direction direction = this.findAttachableSurface(blockpos1);
                    if (direction != null) {
                        this.unRide();
                        this.setAttachFace(direction);
                        this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
                        this.setPos((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY(),
                                (double) blockpos1.getZ() + 0.5D);
                        this.entityData.set(DATA_PEEK_ID, (byte) 0);
                        this.setTarget((LivingEntity) null);
                        return true;
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)
                || source.is(net.minecraft.world.damagesource.DamageTypes.LAVA)) {
            return false;
        }

        if (this.isClosed()) {
            Entity entity = source.getDirectEntity();
            if (entity instanceof net.minecraft.world.entity.projectile.AbstractArrow) {
                return false;
            }
        }

        if (!super.hurt(source, amount)) {
            return false;
        } else {
            this.hitCount++;
            this.hitResetTimer = 60;

            if (this.hitCount >= 2) {
                this.teleportSomewhere();
                this.hitCount = 0;
            } else if ((double) this.getHealth() < (double) this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
                this.teleportSomewhere();
            } else if (source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
                Entity entity1 = source.getDirectEntity();
                if (entity1 != null && entity1.getType() == net.minecraft.world.entity.EntityType.SHULKER_BULLET) {
                    this.hitByShulkerBullet();
                }
            }

            return true;
        }
    }

    private void hitByShulkerBullet() {
        this.teleportSomewhere();
    }

    private boolean isClosed() {
        return this.getRawPeekAmount() == 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACH_FACE_ID, Direction.DOWN);
        this.entityData.define(DATA_PEEK_ID, (byte) 0);
    }

    public Direction getAttachFace() {
        return this.entityData.get(DATA_ATTACH_FACE_ID);
    }

    private void setAttachFace(Direction pAttachFace) {
        this.entityData.set(DATA_ATTACH_FACE_ID, pAttachFace);
    }

    private int getRawPeekAmount() {
        return this.entityData.get(DATA_PEEK_ID);
    }

    public void setRawPeekAmount(int pPeekAmount) {
        if (!this.level().isClientSide) {
            if (pPeekAmount == 0) {
                this.getAttribute(Attributes.ARMOR).setBaseValue(20.0D);
                this.playSound(SoundEvents.SHULKER_CLOSE, 1.0F, 1.0F);
                this.level().gameEvent(net.minecraft.world.level.gameevent.GameEvent.CONTAINER_CLOSE,
                        this.blockPosition(), net.minecraft.world.level.gameevent.GameEvent.Context.of(this));
            } else {
                this.getAttribute(Attributes.ARMOR).setBaseValue(0.0D);
                this.playSound(SoundEvents.SHULKER_OPEN, 1.0F, 1.0F);
                this.level().gameEvent(net.minecraft.world.level.gameevent.GameEvent.CONTAINER_OPEN,
                        this.blockPosition(), net.minecraft.world.level.gameevent.GameEvent.Context.of(this));
            }
        }

        this.entityData.set(DATA_PEEK_ID, (byte) pPeekAmount);
    }

    public void setPeekTickCount(int pPeekTickCount) {
        this.peekTickCount = pPeekTickCount;
    }

    public int getRawPeekTickCount() {
        return this.peekTickCount;
    }

    public float getClientPeekAmount(float pPartialTicks) {
        return Mth.lerp(pPartialTicks, this.currentPeekAmountO, this.currentPeekAmount);
    }

    public Optional<Vec3> getRenderPosition(float pPartial) {
        if (this.clientOldAttachPosition != null && this.clientSideTeleportInterpolation > 0) {
            double d0 = (double) ((float) this.clientSideTeleportInterpolation - pPartial) / 6.0D;
            d0 *= d0;
            BlockPos blockpos = this.blockPosition();
            double d1 = (double) (blockpos.getX() - this.clientOldAttachPosition.getX()) * d0;
            double d2 = (double) (blockpos.getY() - this.clientOldAttachPosition.getY()) * d0;
            double d3 = (double) (blockpos.getZ() - this.clientOldAttachPosition.getZ()) * d0;
            return Optional.of(new Vec3(-d1, -d2, -d3));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void move(net.minecraft.world.entity.MoverType pType, Vec3 pPos) {
        if (pType == net.minecraft.world.entity.MoverType.SHULKER_BOX) {
            this.teleportSomewhere();
        } else {
            super.move(pType, pPos);
        }
    }

    public void setPos(double pX, double pY, double pZ) {
        BlockPos blockpos = this.blockPosition();
        if (this.isPassenger()) {
            super.setPos(pX, pY, pZ);
        } else {
            super.setPos((double) Mth.floor(pX) + 0.5D, (double) Mth.floor(pY + 0.5D), (double) Mth.floor(pZ) + 0.5D);
        }

        if (this.tickCount != 0) {
            BlockPos blockpos1 = this.blockPosition();
            if (!blockpos1.equals(blockpos)) {
                this.entityData.set(DATA_PEEK_ID, (byte) 0);
                this.hasImpulse = true;
                if (this.level().isClientSide() && !this.isPassenger()
                        && !blockpos1.equals(this.clientOldAttachPosition)) {
                    this.clientOldAttachPosition = blockpos;
                    this.clientSideTeleportInterpolation = 6;
                    this.xOld = this.getX();
                    this.yOld = this.getY();
                    this.zOld = this.getZ();
                }
            }

        }
    }

    public Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    public void setDeltaMovement(Vec3 pDeltaMovement) {
    }

    @Override
    public boolean canUpdateMove() {
        return true;
    }

    @Override
    public void updateMoveMode(Player player) {
        boolean flag = false;
        if (!this.isWandering() && !this.isStaying() && !this.isGuardingArea() && this.canWander()) {
            this.setBoundPos(null);
            this.setWandering(true);
            this.setStaying(false);
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("info.goety.servant.wander",
                    this.getDisplayName()), true);
            flag = true;
        } else if (!this.isStaying() && !this.isGuardingArea() && this.canStay()) {
            this.setBoundPos(null);
            this.setWandering(false);
            this.setStaying(true);
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("info.goety.servant.staying",
                    this.getDisplayName()), true);
            flag = true;
        } else if (!this.isGuardingArea() && this.canGuardArea()) {
            this.setBoundPos(this.blockPosition());
            this.setWandering(false);
            this.setStaying(false);
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("info.goety.servant.guard",
                    this.getDisplayName()), true);
            flag = true;
        } else if (this.canFollow()) {
            this.setFollowing();
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("info.goety.servant.follow",
                    this.getDisplayName()), true);
            flag = true;
        }
        if (flag) {
            this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean canWander() {
        return true;
    }

    @Override
    public boolean canStay() {
        return true;
    }

    @Override
    public boolean canGuardArea() {
        return true;
    }

    @Override
    public boolean canFollow() {
        return true;
    }

    @Override
    public boolean canBeCommanded() {
        return true;
    }

    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public void push(Entity pEntity) {
    }

    public float getPickRadius() {
        return 0.0F;
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
        return 0.5F;
    }

    public int getMaxHeadXRot() {
        return 180;
    }

    public int getMaxHeadYRot() {
        return 180;
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        this.yBodyRot = 0.0F;
        this.yBodyRotO = 0.0F;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_ATTACH_FACE_ID.equals(pKey)) {
            this.setBoundingBox(this.makeBoundingBox());
        }

        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public boolean isCommanded() {
        return this.commandPos != null;
    }

    public boolean isWandering() {
        return this.getServantFlag(1);
    }

    public void setWandering(boolean wandering) {
        this.setServantFlags(1, wandering);
    }

    public boolean isStaying() {
        return this.getServantFlag(2) && !this.isCommanded() && this.getControllingPassenger() == null;
    }

    public void setStaying(boolean staying) {
        this.setServantFlags(2, staying);
    }

    private boolean getServantFlag(int mask) {
        int i = this.entityData.get(SUMMONED_FLAGS);
        return (i & mask) != 0;
    }

    private void setServantFlags(int mask, boolean value) {
        int i = this.entityData.get(SUMMONED_FLAGS);
        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.entityData.set(SUMMONED_FLAGS, (byte) (i & 255));
    }

    public boolean isShellClosed() {
        return this.isShellClosed;
    }

    public Direction getAttachFaceDirection() {
        return this.attachedFace;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new ShulkerServant.ShulkerBodyRotationControl(this);
    }

    @Override
    protected AABB makeBoundingBox() {
        float f = this.getPhysicalPeek(this.currentPeekAmount);
        Direction direction = this.getAttachFace().getOpposite();
        float f1 = this.getType().getWidth() / 2.0F;
        return Shulker.getProgressAabb(direction, f).move(this.getX() - (double) f1, this.getY(),
                this.getZ() - (double) f1);
    }

    static class ShulkerBodyRotationControl extends BodyRotationControl {
        public ShulkerBodyRotationControl(Mob pMob) {
            super(pMob);
        }

        public void clientTick() {
        }
    }

    static class ShulkerLookControl extends LookControl {
        public ShulkerLookControl(Mob pMob) {
            super(pMob);
        }

        @Override
        protected void clampHeadRotationToBody() {
        }

        @Override
        protected Optional<Float> getYRotD() {
            Direction direction = ((ShulkerServant) this.mob).getAttachFace().getOpposite();
            Vector3f vector3f = direction.getRotation().transform(new Vector3f(0.0F, 0.0F, 1.0F));
            Vec3i vec3i = direction.getNormal();
            Vector3f vector3f1 = new Vector3f((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
            vector3f1.cross(vector3f);
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getEyeY();
            double d2 = this.wantedZ - this.mob.getZ();
            Vector3f vector3f2 = new Vector3f((float) d0, (float) d1, (float) d2);
            float f = vector3f1.dot(vector3f2);
            float f1 = vector3f.dot(vector3f2);
            return !(Math.abs(f) > 1.0E-5F) && !(Math.abs(f1) > 1.0E-5F) ? Optional.empty()
                    : Optional.of((float) (Mth.atan2((double) (-f), (double) f1) * (double) (180F / (float) Math.PI)));
        }

        @Override
        protected Optional<Float> getXRotD() {
            return Optional.of(0.0F);
        }
    }

    static class ShulkerAttackGoal extends Goal {
        private final ShulkerServant shulker;
        private int attackTime;

        public ShulkerAttackGoal(ShulkerServant pShulker) {
            this.shulker = pShulker;
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.shulker.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                return this.shulker.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL &&
                        this.shulker.distanceToSqr(livingentity) <= 256.0D;
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            this.attackTime = 20;
            this.shulker.setRawPeekAmount(100);
        }

        @Override
        public void stop() {
            this.shulker.setRawPeekAmount(0);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.shulker.level().isClientSide) {
                return;
            }

            --this.attackTime;
            LivingEntity target = this.shulker.getTarget();
            if (target != null) {
                this.shulker.getLookControl().setLookAt(target, 180.0F, 180.0F);

                if (this.attackTime <= 0 && this.shulker.getRawPeekAmount() > 0) {
                    Vec3 vec3 = this.shulker.getViewVector(1.0F);

                    ModShulkerBullet bullet = new ModShulkerBullet(this.shulker.level(), this.shulker, target,
                            this.shulker.getAttachFace().getAxis());
                    bullet.setCustomDamage(4);
                    bullet.setEffectType(new ResourceLocation("minecraft", "levitation"));
                    bullet.setEffectDuration(200);
                    bullet.setEffectAmplifier(0);
                    bullet.setFlightSpeed(0.5D);
                    if (this.shulker.getTrueOwner() != null) {
                        bullet.setOwnerUUID(this.shulker.getTrueOwner().getUUID());
                    }
                    bullet.setPos(this.shulker.getX() + vec3.x * 2.0D, this.shulker.getY(0.5D) + vec3.y * 2.0D,
                            this.shulker.getZ() + vec3.z * 2.0D);
                    this.shulker.level().addFreshEntity(bullet);
                    this.shulker.playSound(SoundEvents.SHULKER_SHOOT, 2.0F,
                            (this.shulker.random.nextFloat() - this.shulker.random.nextFloat()) * 0.2F + 1.0F);
                    this.attackTime = 20 + this.shulker.random.nextInt(91);
                }
            }
        }
    }

    static class ShulkerPeekGoal extends Goal {
        private final ShulkerServant shulker;
        private int peekTime;

        public ShulkerPeekGoal(ShulkerServant pShulker) {
            this.shulker = pShulker;
        }

        @Override
        public boolean canUse() {
            return this.shulker.getTarget() == null && this.shulker.random.nextInt(40) == 0 &&
                    this.shulker.canStayAt(this.shulker.blockPosition(), this.shulker.getAttachFace());
        }

        @Override
        public boolean canContinueToUse() {
            return this.peekTime >= 0;
        }

        @Override
        public void start() {
            this.peekTime = 20 * (1 + this.shulker.random.nextInt(3));
            this.shulker.setRawPeekAmount(30);
        }

        @Override
        public void stop() {
            this.shulker.setRawPeekAmount(0);
        }

        @Override
        public void tick() {
            --this.peekTime;
        }
    }

    static class ShulkerNearestAttackGoal extends NearestAttackableTargetGoal<Player> {
        private final ShulkerServant shulker;

        public ShulkerNearestAttackGoal(ShulkerServant pShulker) {
            super(pShulker, Player.class, true);
            this.shulker = pShulker;
        }

        @Override
        public boolean canUse() {
            return this.shulker.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL &&
                    this.shulker.getTrueOwner() != null && super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double pTargetDistance) {
            Direction direction = this.shulker.getAttachFace();
            if (direction.getAxis() == Direction.Axis.X) {
                return this.shulker.getBoundingBox().inflate(4.0D, pTargetDistance, pTargetDistance);
            } else {
                return direction.getAxis() == Direction.Axis.Z
                        ? this.shulker.getBoundingBox().inflate(pTargetDistance, pTargetDistance, 4.0D)
                        : this.shulker.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
            }
        }
    }

    static class ShulkerDefenseAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
        private final ShulkerServant shulker;

        public ShulkerDefenseAttackGoal(ShulkerServant pShulker) {
            super(pShulker, LivingEntity.class, 10, true, false, (p_33501_) -> {
                return p_33501_ instanceof Enemy;
            });
            this.shulker = pShulker;
        }

        @Override
        public boolean canUse() {
            return this.shulker.getTrueOwner() != null && this.shulker.getTrueOwner().getTeam() == null ? false
                    : super.canUse();
        }

        @Override
        protected AABB getTargetSearchArea(double pTargetDistance) {
            Direction direction = this.shulker.getAttachFace();
            if (direction.getAxis() == Direction.Axis.X) {
                return this.shulker.getBoundingBox().inflate(4.0D, pTargetDistance, pTargetDistance);
            } else {
                return direction.getAxis() == Direction.Axis.Z
                        ? this.shulker.getBoundingBox().inflate(pTargetDistance, pTargetDistance, 4.0D)
                        : this.shulker.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
            }
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return com.k1sak1.goetyawaken.Config.shulkerServantLimit;
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == com.Polarice3.Goety.common.effects.GoetyEffects.VOID_TOUCHED.get()) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }
}