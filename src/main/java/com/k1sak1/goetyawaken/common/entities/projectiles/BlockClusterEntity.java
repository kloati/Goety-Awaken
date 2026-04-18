package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.k1sak1.goetyawaken.common.init.GoetyAwakenDataSerializers;
import com.k1sak1.goetyawaken.utils.GoetyAwakenNBTUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class BlockClusterEntity extends Entity {
    protected static final EntityDataAccessor<Map<BlockPos, BlockState>> BLOCK_STATE_MAP;
    private static final EntityDataAccessor<List<CompoundTag>> BLOCK_ENTITY_DATA;
    private static final EntityDataAccessor<BlockPos> ORIGIN_POSITION;
    private static final EntityDataAccessor<Vec2> ROTATION_INCREMENT;
    private static final EntityDataAccessor<Boolean> ENABLE_PHYSICS;
    private static final EntityDataAccessor<Boolean> ALWAYS_RENDER;
    private static final EntityDataAccessor<Float> WIDTH_DIMENSION;
    private static final EntityDataAccessor<Float> HEIGHT_DIMENSION;
    private static final EntityDataAccessor<Float> DEPTH_DIMENSION;
    private static final EntityDataAccessor<Integer> OSCILLATION_DURATION;
    protected static final EntityDataAccessor<Optional<BlockPos>> FADE_ORIGIN;
    private static final EntityDataAccessor<Float> FADE_INTENSITY;
    private static final EntityDataAccessor<Integer> FADE_RANGE_BUFFER;

    @Nullable
    private UUID launcherUUID;
    @Nullable
    private Entity cachedLauncher;
    private boolean hasLeftLauncher;
    private boolean hasFired;

    private static final float MOTION_DAMPING = 0.99F;
    private static final float GRAVITY_STRENGTH = 0.03F;

    public int lifetime;
    public boolean shouldDropItems;
    public boolean resetGravityFlag;
    private int oscillationTimer;
    @Nonnull
    public Vec2 oscillationOffset;
    @Nonnull
    public Vec2 currentOscillation;
    private int groundPenetration;
    private boolean preventOverlap;
    private boolean placeOnImpact;
    private boolean excludeFromConsumedCount;
    private float clusterPitchAngle;
    private float prevClusterPitchAngle;
    private float clusterYawAngle;
    private float prevClusterYawAngle;
    private boolean spawnedFromBeam;
    private boolean spawnedFromFallingBlock;
    private int spawnHeadIndex;
    private double tractorBeamRange;

    public BlockClusterEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);
        this.shouldDropItems = true;
        this.resetGravityFlag = true;
        this.oscillationOffset = Vec2.ZERO;
        this.currentOscillation = Vec2.ZERO;
        this.spawnHeadIndex = -1;
    }

    public BlockClusterEntity(EntityType<?> entityType, Level world, LivingEntity launcher) {
        super(entityType, world);
        this.shouldDropItems = true;
        this.resetGravityFlag = true;
        this.oscillationOffset = Vec2.ZERO;
        this.currentOscillation = Vec2.ZERO;
        this.spawnHeadIndex = -1;
        this.setOwner(launcher);
    }

    public static BlockClusterEntity createSphericalCluster(
            EntityType<?> entityType,
            Level world,
            @Nullable LivingEntity owner,
            BlockState block,
            float radius,
            Vec3 pos,
            Vec3 velocity,
            Vec2 rotationDelta,
            boolean noGravity,
            boolean glowing,
            boolean placeBlocks,
            boolean shouldDropItems) {

        BlockClusterEntity cluster = new BlockClusterEntity(entityType, world, owner);

        Map<BlockPos, BlockState> states = Maps.newHashMap();
        int minX = Mth.floor(-radius);
        int maxX = Mth.ceil(radius);
        int minY = Mth.floor(-radius);
        int maxY = Mth.ceil(radius);
        int minZ = Mth.floor(-radius);
        int maxZ = Mth.ceil(radius);

        float radiusSquared = radius * radius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    float distSq = x * x + y * y + z * z;
                    if (distSq <= radiusSquared) {
                        BlockPos relativePos = new BlockPos(x, y, z);
                        states.put(relativePos, block);
                    }
                }
            }
        }

        cluster.setPos(pos.x, pos.y, pos.z);
        cluster.populate(states);

        cluster.setDeltaMovement(velocity);
        cluster.setRotationDelta(rotationDelta);
        cluster.setNoGravity(noGravity);
        cluster.setGlowingTag(glowing);
        cluster.setPhysics(true);
        cluster.shouldDropItems = shouldDropItems;
        cluster.placeOnImpact = placeBlocks;

        if (velocity.lengthSqr() > 0.0D) {
            double d0 = velocity.horizontalDistance();
            cluster.setXRot((float) (Mth.atan2(velocity.y, d0) * (double) (180F / (float) Math.PI)));
            cluster.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * (double) (180F / (float) Math.PI)));
            cluster.xRotO = cluster.getXRot();
            cluster.yRotO = cluster.getYRot();
        }

        return cluster;
    }

    public static BlockClusterEntity createSphericalCluster(
            EntityType<?> entityType,
            Level world,
            @Nullable LivingEntity owner,
            BlockState block,
            float radius,
            Vec3 pos,
            Vec3 direction,
            float speed) {

        Vec3 velocity = direction.normalize().scale(speed);
        return createSphericalCluster(
                entityType, world, owner, block, radius, pos, velocity,
                new Vec2(0.0F, 0.0F), false, false, true, true);
    }

    public static BlockClusterEntity createSphericalCluster(
            EntityType<?> entityType,
            Level world,
            @Nullable LivingEntity owner,
            BlockState block,
            float radius,
            Vec3 pos,
            Vec3 direction,
            float speed,
            Vec2 rotationDelta) {

        Vec3 velocity = direction.normalize().scale(speed);
        return createSphericalCluster(
                entityType, world, owner, block, radius, pos, velocity,
                rotationDelta, false, false, true, true);
    }

    public void setOwner(@Nullable Entity pOwner) {
        if (pOwner != null) {
            this.launcherUUID = pOwner.getUUID();
            this.cachedLauncher = pOwner;
        }
    }

    @Nullable
    public Entity getLauncher() {
        if (this.cachedLauncher != null && !this.cachedLauncher.isRemoved()) {
            return this.cachedLauncher;
        } else if (this.launcherUUID != null && this.level() instanceof ServerLevel serverLevel) {
            this.cachedLauncher = serverLevel.getEntity(this.launcherUUID);
            return this.cachedLauncher;
        } else {
            return null;
        }
    }

    public void populate(Map<BlockPos, BlockState> states) {
        if (states.size() > 0) {
            int minX = 0;
            int minY = 0;
            int minZ = 0;
            int maxX = 0;
            int maxY = 0;
            int maxZ = 0;

            for (Map.Entry<BlockPos, BlockState> entry : states.entrySet()) {
                BlockPos pos = entry.getKey();
                if (pos.getX() < minX) {
                    minX = pos.getX();
                }

                if (pos.getY() < minY) {
                    minY = pos.getY();
                }

                if (pos.getZ() < minZ) {
                    minZ = pos.getZ();
                }

                if (pos.getX() > maxX) {
                    maxX = pos.getX();
                }

                if (pos.getY() > maxY) {
                    maxY = pos.getY();
                }

                if (pos.getZ() > maxZ) {
                    maxZ = pos.getZ();
                }
            }

            float x = (float) (maxX - minX);
            float y = (float) (maxY - minY);
            float z = (float) (maxZ - minZ);
            this.setSize(Math.abs(x) + 1.0F, Math.abs(y) + 1.0F, Math.abs(z) + 1.0F);
            this.setStartPos(BlockPos.containing(
                    (double) minX + (double) x / (double) 2.0F,
                    (double) minY + (double) y / (double) 2.0F,
                    (double) minZ + (double) z / (double) 2.0F));
            this.setBlocks(states);
        }
    }

    public void populate(BlockPos start, BlockPos end, Predicate<BlockState> filter) {
        float x = (float) Mth.floor((float) (end.getX() - start.getX()));
        float y = (float) Mth.floor((float) (end.getY() - start.getY()));
        float z = (float) Mth.floor((float) (end.getZ() - start.getZ()));
        Vec3 clusterPos = Vec3.atLowerCornerOf(start).add(
                (double) x / (double) 2.0F + (double) 0.5F,
                Math.min((double) y, (double) 0.0F),
                (double) z / (double) 2.0F + (double) 0.5F);
        this.setPos(clusterPos.x, clusterPos.y, clusterPos.z);
        this.setSize(Math.abs(x) + 1.0F, Math.abs(y) + 1.0F, Math.abs(z) + 1.0F);
        this.setStartPos(start.offset(
                (int) ((double) x / (double) 2.0F),
                (int) ((double) y / (double) 2.0F),
                (int) ((double) z / (double) 2.0F)));

        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            BlockState state = this.level().getBlockState(pos);
            if (!state.isAir() && filter.test(state)) {
                if (state.hasBlockEntity()) {
                    BlockEntity tile = this.level().getBlockEntity(pos);
                    if (tile != null) {
                        this.addTileData(tile.serializeNBT());
                        this.level().removeBlockEntity(pos);
                    }
                }

                BlockPos relative = pos.subtract(this.getStartPos());
                this.addBlock(state, relative);
            }
        }

        for (Map.Entry<BlockPos, BlockState> entry : this.getBlocks().entrySet()) {
            BlockPos pos = this.getStartPos().offset((Vec3i) entry.getKey());
            this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public void populateWithRadius(BlockPos start, float radius, Predicate<BlockState> filter) {
        this.setSize(
                (float) (Mth.ceil(radius) * 2 - 1),
                (float) (Mth.ceil(radius) * 2 - 1),
                (float) (Mth.ceil(radius) * 2 - 1));
        this.setStartPos(start);
        this.setPos(
                (double) ((float) start.getX() + 0.5F),
                (double) start.getY() - this.getBoundingBox().getCenter().y + (double) 0.5F,
                (double) ((float) start.getZ() + 0.5F));

        for (int x = -Mth.floor(radius); x < Mth.ceil(radius); ++x) {
            for (int y = -Mth.floor(radius); y < Mth.ceil(radius); ++y) {
                for (int z = -Mth.floor(radius); z < Mth.ceil(radius); ++z) {
                    if (Mth.sqrt((float) (x * x + y * y + z * z)) < radius) {
                        BlockPos currentPos = new BlockPos(x + start.getX(), y + start.getY(), z + start.getZ());
                        BlockPos relativePos = new BlockPos(x, y, z);
                        BlockState state = this.level().getBlockState(currentPos);
                        if (!state.isAir() && filter.test(state)) {
                            if (state.hasBlockEntity()) {
                                BlockEntity tile = this.level().getBlockEntity(currentPos);
                                if (tile != null) {
                                    this.addTileData(tile.serializeNBT());
                                    this.level().removeBlockEntity(currentPos);
                                }
                            }

                            this.addBlock(state, relativePos);
                        }
                    }
                }
            }
        }

        for (Map.Entry<BlockPos, BlockState> entry : this.getBlocks().entrySet()) {
            BlockPos pos = start.offset((Vec3i) entry.getKey());
            this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public void setTime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ORIGIN_POSITION, BlockPos.ZERO);
        this.entityData.define(BLOCK_STATE_MAP, new HashMap<>());
        this.entityData.define(BLOCK_ENTITY_DATA, new ArrayList<>());
        this.entityData.define(ROTATION_INCREMENT, new Vec2(0.0F, 0.0F));
        this.entityData.define(ENABLE_PHYSICS, true);
        this.entityData.define(ALWAYS_RENDER, false);
        this.entityData.define(WIDTH_DIMENSION, 1.0F);
        this.entityData.define(HEIGHT_DIMENSION, 1.0F);
        this.entityData.define(DEPTH_DIMENSION, 1.0F);
        this.entityData.define(OSCILLATION_DURATION, 0);
        this.entityData.define(FADE_ORIGIN, Optional.empty());
        this.entityData.define(FADE_INTENSITY, 10.0F);
        this.entityData.define(FADE_RANGE_BUFFER, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("StartPos")) {
            this.setStartPos(NbtUtils.readBlockPos(compound.getCompound("StartPos")));
        }

        if (compound.contains("BLOCK_STATE_MAP")) {
            this.setBlocks(com.k1sak1.goetyawaken.utils.GoetyAwakenNBTUtil.readBlockStatePosMap(
                    this.level().holderLookup(Registries.BLOCK),
                    compound.getList("BLOCK_STATE_MAP", 10)));
        }

        if (compound.contains("TileData")) {
            this.setTileData(GoetyAwakenNBTUtil.readCompoundList(compound.getList("TileData", 10)));
        }

        if (compound.contains("RotationDelta")) {
            CompoundTag deltaCompound = compound.getCompound("RotationDelta");
            this.setRotationDelta(GoetyAwakenNBTUtil.readVector2f(deltaCompound));
        }

        if (compound.contains("Width")) {
            this.entityData.set(WIDTH_DIMENSION, compound.getFloat("Width"));
            this.entityData.set(DEPTH_DIMENSION, compound.getFloat("Width"));
        } else {
            this.entityData.set(WIDTH_DIMENSION, compound.getFloat("XSize"));
            this.entityData.set(DEPTH_DIMENSION, compound.getFloat("ZSize"));
        }

        if (compound.contains("Height")) {
            this.entityData.set(HEIGHT_DIMENSION, compound.getFloat("Height"));
        } else {
            this.entityData.set(HEIGHT_DIMENSION, compound.getFloat("YSize"));
        }

        this.refreshDimensions();
        this.lifetime = compound.getInt("lifetime");
        this.shouldDropItems = compound.getBoolean("shouldDropItems");
        this.resetGravityFlag = compound.getBoolean("ResetGravity");
        if (this.resetGravityFlag) {
            this.setNoGravity(false);
        }

        this.setForceRender(compound.getBoolean("ForceRender"));
        this.setShakeTime(compound.getInt("oscillationTimer"));
        this.setSink(compound.getInt("GroundSink"));
        this.setAntiStacking(compound.getBoolean("preventOverlap"));
        if (compound.contains("StaticFadePos")) {
            this.entityData.set(FADE_ORIGIN, Optional.of(NbtUtils.readBlockPos(compound.getCompound("StaticFadePos"))));
        }

        this.placeOnImpact = compound.getBoolean("placeOnImpact");
        this.excludeFromConsumedCount = compound.getBoolean("excludeFromConsumedCount");
        this.spawnedFromBeam = compound.getBoolean("spawnedFromBeam");
        this.spawnedFromFallingBlock = compound.getBoolean("spawnedFromFallingBlock");
        this.spawnHeadIndex = compound.getInt("spawnHeadIndex");
        this.tractorBeamRange = compound.getDouble("tractorBeamRange");
        if (compound.contains("HasPhysics")) {
            this.setPhysics(compound.getBoolean("HasPhysics"));
        }

        if (compound.hasUUID("Owner")) {
            this.launcherUUID = compound.getUUID("Owner");
            this.cachedLauncher = null;
        }
        this.hasLeftLauncher = compound.getBoolean("hasLeftLauncher");
        this.hasFired = compound.getBoolean("hasFired");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("StartPos", NbtUtils.writeBlockPos(this.getStartPos()));
        compound.put("BLOCK_STATE_MAP", GoetyAwakenNBTUtil.writeBlockStatePosMap(this.getBlocks()));
        compound.put("TileData", GoetyAwakenNBTUtil.writeCompoundList(this.getTileData()));
        compound.putFloat("XSize", this.entityData.get(WIDTH_DIMENSION));
        compound.putFloat("YSize", this.entityData.get(HEIGHT_DIMENSION));
        compound.putFloat("ZSize", this.entityData.get(DEPTH_DIMENSION));
        compound.putInt("lifetime", this.lifetime);
        compound.putBoolean("shouldDropItems", this.shouldDropItems);
        compound.put("RotationDelta", GoetyAwakenNBTUtil.writeVector2f(this.getRotationDelta()));
        compound.putBoolean("ResetGravity", this.resetGravityFlag);
        compound.putBoolean("ForceRender", this.forceRender());
        compound.putInt("oscillationTimer", this.oscillationTimer);
        compound.putInt("GroundSink", this.getSink());
        compound.putBoolean("preventOverlap", this.preventOverlap());
        this.entityData.get(FADE_ORIGIN).ifPresent((pos) -> compound.put("StaticFadePos", NbtUtils.writeBlockPos(pos)));
        compound.putBoolean("placeOnImpact", this.placeOnImpact);
        compound.putBoolean("excludeFromConsumedCount", this.excludeFromConsumedCount);
        compound.putBoolean("spawnedFromBeam", this.spawnedFromBeam);
        compound.putBoolean("spawnedFromFallingBlock", this.spawnedFromFallingBlock);
        compound.putInt("spawnHeadIndex", this.spawnHeadIndex);
        compound.putDouble("tractorBeamRange", this.tractorBeamRange);
        compound.putBoolean("HasPhysics", this.physicsEnabled());

        if (this.launcherUUID != null) {
            compound.putUUID("Owner", this.launcherUUID);
        }
        compound.putBoolean("hasLeftLauncher", this.hasLeftLauncher);
        compound.putBoolean("hasFired", this.hasFired);
    }

    @Override
    public void tick() {
        if (!this.hasFired) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getLauncher());
            this.hasFired = true;
        }

        if (!this.hasLeftLauncher) {
            this.hasLeftLauncher = this.checkHasLeftLauncher();
        }

        this.oscillationOffset = new Vec2(this.currentOscillation.x, this.currentOscillation.y);
        if (this.oscillationTimer > 0) {
            float oscillationTimer = (float) this.getShakeTime();
            float x = Mth.cos(oscillationTimer * 4.5F) * 0.05F + (this.random.nextFloat() - 0.5F) * 0.05F;
            float z = Mth.sin(oscillationTimer * 3.5F) * 0.15F + (this.random.nextFloat() - 0.5F) * 0.2F;
            this.currentOscillation = new Vec2(x, z);
            --this.oscillationTimer;
            if (this.oscillationTimer == 0) {
                this.setShakeTime(0);
            }
        } else {
            this.currentOscillation = new Vec2(0.0F, 0.0F);
        }

        if (!this.level().isClientSide) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS) {
                this.onHit(hitresult);
            }

            BlockPos pos = this.blockPosition();
            if (this.getBlocks().isEmpty()) {
                this.discard();
            }

            Map<BlockPos, BlockState> BLOCK_STATE_MAP = this.getBlocks();
            boolean isAir = true;

            for (Map.Entry<BlockPos, BlockState> entry : BLOCK_STATE_MAP.entrySet()) {
                BlockState state = entry.getValue();
                if (isAir) {
                    isAir = state.isAir();
                }
            }

            if (isAir) {
                this.discard();
            }

            if (!this.onGround()) {
                if ((float) pos.getY() + this.getBbHeight() <= (float) this.level().getMinBuildHeight()
                        || this.lifetime > 600) {
                    if (this.shouldDropItems && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        for (Map.Entry<BlockPos, BlockState> entry : BLOCK_STATE_MAP.entrySet()) {
                            BlockState state = entry.getValue();
                            BlockPos position = pos.offset((Vec3i) entry.getKey());
                            this.spawnAtSpecificLocation(state.getBlock().asItem(), position);
                        }
                    }

                    this.discard();
                }
            } else {
                this.place();
            }
        } else {
            this.refreshDimensions();
            this.reapplyPosition();
        }
        this.travel();
        ++this.lifetime;
        this.noPhysics = !this.physicsEnabled();
        super.tick();
        this.prevClusterPitchAngle = this.clusterPitchAngle;
        this.prevClusterYawAngle = this.clusterYawAngle;
        if (this.getShakeTime() <= 0) {
            this.clusterPitchAngle += this.getRotationDelta().x;
            this.clusterYawAngle += this.getRotationDelta().y;
        }
    }

    protected void travel() {
        Vec3 vec3 = this.getDeltaMovement();
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        this.updateClusterRotation();
        if (this.isInWater()) {
            float waterResistance = 0.8F;
            this.setDeltaMovement(vec3.scale((double) waterResistance));
        }

        if (!this.isNoGravity()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, vec31.y - (double) this.getGravity(), vec31.z);
        }

        this.setPos(d2, d0, d1);
    }

    protected void updateClusterRotation() {
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = vec3.horizontalDistance();
        this.setXRot(lerpRotation(this.xRotO, (float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI))));
        this.setYRot(lerpRotation(this.yRotO, (float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))));
    }

    protected static float lerpRotation(float pCurrentRotation, float pTargetRotation) {
        while (pTargetRotation - pCurrentRotation < -180.0F) {
            pCurrentRotation -= 360.0F;
        }
        while (pTargetRotation - pCurrentRotation >= 180.0F) {
            pCurrentRotation += 360.0F;
        }
        return Mth.lerp(0.2F, pCurrentRotation, pTargetRotation);
    }

    protected float getGravity() {
        return this.isNoGravity() ? 0.0F : GRAVITY_STRENGTH;
    }

    private boolean checkHasLeftLauncher() {
        Entity entity = this.getLauncher();
        if (entity != null) {
            for (Entity entity1 : this.level().getEntities(this,
                    this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
                    (p_37272_) -> !p_37272_.isSpectator() && p_37272_.isPickable())) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canHitEntity(Entity pTarget) {
        if (!pTarget.canBeHitByProjectile()) {
            return false;
        }

        Entity entity = this.getLauncher();
        if (entity == null) {
            return true;
        }

        if (this.hasLeftLauncher) {
            return true;
        }

        if (entity.isPassengerOfSameVehicle(pTarget)) {
            return false;
        }

        Entity owner = this.getLauncher();
        if (owner != null && pTarget != owner) {
            if (MobUtil.areAllies(owner, pTarget)) {
                return false;
            }
        }

        return true;
    }

    public void onHit(HitResult pResult) {
        HitResult.Type hitresult$type = pResult.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) pResult);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(),
                    GameEvent.Context.of(this, (BlockState) null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) pResult);
            BlockPos blockpos = ((BlockHitResult) pResult).getBlockPos();
            BlockState blockstate = this.level().getBlockState(blockpos);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, blockstate));
        }
    }

    public void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        float baseDamage = this.calculateTotalHardness() / 2;
        float speed = (float) this.getDeltaMovement().length();
        float damage = baseDamage * (0.1F + speed);

        DamageSource damagesource = this.damageSources().flyIntoWall();

        if (entity.hurt(damagesource, damage)) {
            this.knockbackTarget(entity, pResult);
        }
    }

    public void onHitBlock(BlockHitResult pResult) {
        this.place();
    }

    private float calculateTotalHardness() {
        float totalHardness = 0.0F;
        Map<BlockPos, BlockState> BLOCK_STATE_MAP = this.getBlocks();
        Level level = this.level();
        for (Map.Entry<BlockPos, BlockState> entry : BLOCK_STATE_MAP.entrySet()) {
            BlockState state = entry.getValue();
            if (!state.isAir()) {
                BlockPos worldPos = this.getStartPos().offset(entry.getKey());
                totalHardness += state.getDestroySpeed(level, worldPos);
            }
        }
        return Math.max(totalHardness, 1.0F);
    }

    private void knockbackTarget(Entity target, EntityHitResult pResult) {
        double d0 = target.getX() - this.getX();
        double d1 = target.getZ() - this.getZ();

        if (target instanceof LivingEntity living) {
            living.knockback(0.5F, d0, d1);
        } else {
            double dist = Math.sqrt(d0 * d0 + d1 * d1);
            if (dist > 0.0D) {
                target.setDeltaMovement(target.getDeltaMovement().add(
                        d0 / dist * 0.5D,
                        0.2D,
                        d1 / dist * 0.5D));
            }
        }
    }

    public void place() {
        this.discard();
        BlockPos pos = this.blockPosition();
        if (this.preventOverlap()) {
            BlockPos currentPos = this.blockPosition();
            BlockState current = this.level().getBlockState(currentPos);

            for (int i = 0; i < 50 && current.isAir(); ++i) {
                currentPos = currentPos.below();
                current = this.level().getBlockState(currentPos);
            }

            pos = pos.atY(currentPos.getY());
        }

        for (Map.Entry<BlockPos, BlockState> entry : this.getBlocks().entrySet()) {
            BlockState state = entry.getValue();
            BlockPos relativePos = entry.getKey();
            BlockPos basePos = pos.offset(relativePos.getX(), relativePos.getY() - this.getSink(), relativePos.getZ());
            BlockPos placementPos = basePos
                    .above(Mth.floor(this.getBoundingBox().getYsize() / (double) 2.0F - (double) 0.5F));
            if (this.level().getBlockEntity(placementPos) == null
                    && !this.level().getBlockState(placementPos).is(BlockTags.DRAGON_IMMUNE)
                    && this.level().setBlock(placementPos, state, 3)) {
                if (state.hasBlockEntity()) {
                    CompoundTag tileData = this.getTileDataFromOffsetPos(relativePos);
                    if (tileData != null) {
                        BlockEntity tile = this.level().getBlockEntity(placementPos);
                        if (tile != null) {
                            tileData.putInt("x", placementPos.getX());
                            tileData.putInt("y", placementPos.getY());
                            tileData.putInt("z", placementPos.getZ());
                            tile.load(tileData);
                            tile.setChanged();
                        }
                    }
                }

                this.level().updateNeighborsAt(placementPos, state.getBlock());
            } else if (this.shouldDropItems && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.spawnAtSpecificLocation(state.getBlock().asItem(), placementPos);
            }
        }
    }

    @Nullable
    public CompoundTag getTileDataFromOffsetPos(BlockPos pos) {
        BlockPos actualPos = this.getStartPos().offset(pos);

        for (CompoundTag data : this.getTileData()) {
            if (data.getInt("x") == actualPos.getX() && data.getInt("y") == actualPos.getY()
                    && data.getInt("z") == actualPos.getZ()) {
                return data;
            }
        }

        return null;
    }

    public void spawnAtSpecificLocation(ItemLike item, BlockPos position) {
        ItemStack stack = new ItemStack(item);
        ItemEntity itemEntity = new ItemEntity(this.level(), (double) position.getX(), (double) position.getY(),
                (double) position.getZ(), stack);
        itemEntity.setDefaultPickUpDelay();
        this.level().addFreshEntity(itemEntity);
    }

    public void setRotationDelta(Vec2 rotation) {
        this.entityData.set(ROTATION_INCREMENT, rotation);
    }

    public Vec2 getRotationDelta() {
        return this.entityData.get(ROTATION_INCREMENT);
    }

    public Map<BlockPos, BlockState> getBlocks() {
        return this.entityData.get(BLOCK_STATE_MAP);
    }

    public void addBlock(BlockState state, BlockPos relativePosition) {
        Map<BlockPos, BlockState> map = Maps.newHashMap(this.getBlocks());
        map.put(relativePosition, state);
        this.entityData.set(BLOCK_STATE_MAP, map);
    }

    public void setBlocks(Map<BlockPos, BlockState> blocks) {
        this.entityData.set(BLOCK_STATE_MAP, blocks);
    }

    public void setStartPos(BlockPos pos) {
        this.entityData.set(ORIGIN_POSITION, pos);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(ORIGIN_POSITION);
    }

    public List<CompoundTag> getTileData() {
        return this.entityData.get(BLOCK_ENTITY_DATA);
    }

    public void addTileData(CompoundTag compound) {
        List<CompoundTag> list = Lists.newArrayList(this.getTileData());
        list.add(compound);
        this.entityData.set(BLOCK_ENTITY_DATA, list, true);
    }

    public void setTileData(List<CompoundTag> list) {
        this.entityData.set(BLOCK_ENTITY_DATA, list);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(
                Math.max(this.entityData.get(WIDTH_DIMENSION), this.entityData.get(DEPTH_DIMENSION)),
                this.entityData.get(HEIGHT_DIMENSION));
    }

    @SuppressWarnings("unchecked")
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public int getSize() {
        return this.getBlocks().size();
    }

    public boolean physicsEnabled() {
        return this.entityData.get(ENABLE_PHYSICS);
    }

    public void setPhysics(boolean physics) {
        this.entityData.set(ENABLE_PHYSICS, physics);
        this.noPhysics = !physics;
    }

    public boolean containsBlock(Block block) {
        for (Map.Entry<BlockPos, BlockState> entry : this.getBlocks().entrySet()) {
            if (entry.getValue().is(block)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> parameter) {
        super.onSyncedDataUpdated(parameter);
        if (!parameter.equals(WIDTH_DIMENSION) && !parameter.equals(HEIGHT_DIMENSION)
                && !parameter.equals(DEPTH_DIMENSION)) {
            if (parameter.equals(OSCILLATION_DURATION)) {
                this.oscillationTimer = this.entityData.get(OSCILLATION_DURATION);
            }
        } else {
            this.refreshDimensions();
        }
    }

    public boolean forceRender() {
        return this.entityData.get(ALWAYS_RENDER);
    }

    public void setForceRender(boolean flag) {
        this.entityData.set(ALWAYS_RENDER, flag);
    }

    public void setSize(float x, float y, float z) {
        this.entityData.set(WIDTH_DIMENSION, x);
        this.entityData.set(HEIGHT_DIMENSION, y);
        this.entityData.set(DEPTH_DIMENSION, z);
        this.refreshDimensions();
    }

    @Override
    protected AABB makeBoundingBox() {
        float x = this.entityData.get(WIDTH_DIMENSION);
        float y = this.entityData.get(HEIGHT_DIMENSION);
        float z = this.entityData.get(DEPTH_DIMENSION);
        return new AABB(
                this.getX() - (double) (x / 2.0F),
                this.getY(),
                this.getZ() - (double) z / (double) 2.0F,
                this.getX() + (double) x / (double) 2.0F,
                this.getY() + (double) y,
                this.getZ() + (double) z / (double) 2.0F);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public void setShakeTime(int lifetime) {
        this.oscillationTimer = lifetime;
        this.entityData.set(OSCILLATION_DURATION, lifetime);
    }

    public int getShakeTime() {
        return this.oscillationTimer;
    }

    public void setSink(int groundPenetration) {
        this.groundPenetration = groundPenetration;
    }

    public int getSink() {
        return this.groundPenetration;
    }

    public void setAntiStacking(boolean flag) {
        this.preventOverlap = flag;
    }

    public boolean preventOverlap() {
        return this.preventOverlap;
    }

    @Nullable
    public BlockPos getFadePos() {
        return this.entityData.get(FADE_ORIGIN).orElse(null);
    }

    public void setFadePos(@Nullable BlockPos pos) {
        this.entityData.set(FADE_ORIGIN, Optional.ofNullable(pos));
    }

    public void setFadeStrength(float strength) {
        this.entityData.set(FADE_INTENSITY, strength);
    }

    public float getFadeStrength() {
        return this.entityData.get(FADE_INTENSITY);
    }

    public int getFadeDistanceOffset() {
        return this.entityData.get(FADE_RANGE_BUFFER);
    }

    public void setFadeDistanceOffset(int offset) {
        this.entityData.set(FADE_RANGE_BUFFER, offset);
    }

    public void setShouldCrumble(boolean flag) {
        this.placeOnImpact = flag;
    }

    public boolean placeOnImpact() {
        return this.placeOnImpact;
    }

    public void setShouldntCountToConsumedEntities(boolean flag) {
        this.excludeFromConsumedCount = flag;
    }

    public boolean excludeFromConsumedCount() {
        return this.excludeFromConsumedCount;
    }

    public float getClusterXRot(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevClusterPitchAngle, this.clusterPitchAngle);
    }

    public float getClusterYRot(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevClusterYawAngle, this.clusterYawAngle);
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    static {
        BLOCK_STATE_MAP = SynchedEntityData.defineId(BlockClusterEntity.class,
                GoetyAwakenDataSerializers.BLOCK_STATE_POS_MAP);
        BLOCK_ENTITY_DATA = SynchedEntityData.defineId(BlockClusterEntity.class,
                GoetyAwakenDataSerializers.COMPOUND_LIST);
        ORIGIN_POSITION = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BLOCK_POS);
        ROTATION_INCREMENT = SynchedEntityData.defineId(BlockClusterEntity.class, GoetyAwakenDataSerializers.VECTOR_2F);
        ENABLE_PHYSICS = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BOOLEAN);
        ALWAYS_RENDER = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BOOLEAN);
        WIDTH_DIMENSION = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        HEIGHT_DIMENSION = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        DEPTH_DIMENSION = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        OSCILLATION_DURATION = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.INT);
        FADE_ORIGIN = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
        FADE_INTENSITY = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        FADE_RANGE_BUFFER = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.INT);
    }
}
