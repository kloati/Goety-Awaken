package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.k1sak1.goetyawaken.common.init.GoetyAwakenDataSerializers;
import com.k1sak1.goetyawaken.utils.GoetyAwakenNBTUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
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
    protected static final EntityDataAccessor<Map<BlockPos, BlockState>> BLOCKS;
    private static final EntityDataAccessor<List<CompoundTag>> TILE_DATA;
    private static final EntityDataAccessor<BlockPos> START_POS;
    private static final EntityDataAccessor<Vec2> ROTATION_DELTA;
    private static final EntityDataAccessor<Boolean> PHYSICS;
    private static final EntityDataAccessor<Boolean> FORCE_RENDER;
    private static final EntityDataAccessor<Float> X_SIZE;
    private static final EntityDataAccessor<Float> Y_SIZE;
    private static final EntityDataAccessor<Float> Z_SIZE;
    private static final EntityDataAccessor<Integer> SHAKE_TIME;
    protected static final EntityDataAccessor<Optional<BlockPos>> FADE_POINT;
    private static final EntityDataAccessor<Float> FADE_STRENGTH;
    private static final EntityDataAccessor<Integer> FADE_DISTANCE_OFFSET;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private boolean leftOwner;
    private boolean hasBeenShot;

    private static final float DEFAULT_INERTIA = 0.99F;
    private static final float DEFAULT_GRAVITY = 0.03F;

    public int time;
    public boolean dropItems;
    public boolean resetGravityOnLoad;
    private int shakeTime;
    @Nonnull
    public Vec2 shakeO;
    @Nonnull
    public Vec2 shake;
    private int sink;
    private boolean antiStacking;
    private boolean shouldCrumble;
    private boolean shouldntCountToConsumedEntities;
    private float xClusterRot;
    private float xClusterRotO;
    private float yClusterRot;
    private float yClusterRotO;
    private boolean createdFromBeam;
    private boolean createdFromFallingBlock;
    private int headCreatedFrom;
    private double tractorBeamDistanceThreshold;

    public BlockClusterEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);
        this.dropItems = true;
        this.resetGravityOnLoad = true;
        this.shakeO = Vec2.ZERO;
        this.shake = Vec2.ZERO;
        this.headCreatedFrom = -1;
    }

    public BlockClusterEntity(EntityType<?> entityType, Level world, LivingEntity owner) {
        super(entityType, world);
        this.dropItems = true;
        this.resetGravityOnLoad = true;
        this.shakeO = Vec2.ZERO;
        this.shake = Vec2.ZERO;
        this.headCreatedFrom = -1;
        this.setOwner(owner);
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
            boolean dropItems) {

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
        cluster.dropItems = dropItems;
        cluster.shouldCrumble = placeBlocks;

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
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            this.cachedOwner = serverLevel.getEntity(this.ownerUUID);
            return this.cachedOwner;
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

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(START_POS, BlockPos.ZERO);
        this.entityData.define(BLOCKS, new HashMap<>());
        this.entityData.define(TILE_DATA, new ArrayList<>());
        this.entityData.define(ROTATION_DELTA, new Vec2(0.0F, 0.0F));
        this.entityData.define(PHYSICS, true);
        this.entityData.define(FORCE_RENDER, false);
        this.entityData.define(X_SIZE, 1.0F);
        this.entityData.define(Y_SIZE, 1.0F);
        this.entityData.define(Z_SIZE, 1.0F);
        this.entityData.define(SHAKE_TIME, 0);
        this.entityData.define(FADE_POINT, Optional.empty());
        this.entityData.define(FADE_STRENGTH, 10.0F);
        this.entityData.define(FADE_DISTANCE_OFFSET, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("StartPos")) {
            this.setStartPos(NbtUtils.readBlockPos(compound.getCompound("StartPos")));
        }

        if (compound.contains("Blocks")) {
            this.setBlocks(com.k1sak1.goetyawaken.utils.GoetyAwakenNBTUtil.readBlockStatePosMap(
                    this.level().holderLookup(Registries.BLOCK),
                    compound.getList("Blocks", 10)));
        }

        if (compound.contains("TileData")) {
            this.setTileData(GoetyAwakenNBTUtil.readCompoundList(compound.getList("TileData", 10)));
        }

        if (compound.contains("RotationDelta")) {
            CompoundTag deltaCompound = compound.getCompound("RotationDelta");
            this.setRotationDelta(GoetyAwakenNBTUtil.readVector2f(deltaCompound));
        }

        if (compound.contains("Width")) {
            this.entityData.set(X_SIZE, compound.getFloat("Width"));
            this.entityData.set(Z_SIZE, compound.getFloat("Width"));
        } else {
            this.entityData.set(X_SIZE, compound.getFloat("XSize"));
            this.entityData.set(Z_SIZE, compound.getFloat("ZSize"));
        }

        if (compound.contains("Height")) {
            this.entityData.set(Y_SIZE, compound.getFloat("Height"));
        } else {
            this.entityData.set(Y_SIZE, compound.getFloat("YSize"));
        }

        this.refreshDimensions();
        this.time = compound.getInt("Time");
        this.dropItems = compound.getBoolean("DropItems");
        this.resetGravityOnLoad = compound.getBoolean("ResetGravity");
        if (this.resetGravityOnLoad) {
            this.setNoGravity(false);
        }

        this.setForceRender(compound.getBoolean("ForceRender"));
        this.setShakeTime(compound.getInt("ShakeTime"));
        this.setSink(compound.getInt("GroundSink"));
        this.setAntiStacking(compound.getBoolean("AntiStacking"));
        if (compound.contains("StaticFadePos")) {
            this.entityData.set(FADE_POINT, Optional.of(NbtUtils.readBlockPos(compound.getCompound("StaticFadePos"))));
        }

        this.shouldCrumble = compound.getBoolean("ShouldCrumble");
        this.shouldntCountToConsumedEntities = compound.getBoolean("ShouldntCountToConsumedEntities");
        this.createdFromBeam = compound.getBoolean("CreatedFromBeam");
        this.createdFromFallingBlock = compound.getBoolean("CreatedFromFallingBlock");
        this.headCreatedFrom = compound.getInt("HeadCreatedFrom");
        this.tractorBeamDistanceThreshold = compound.getDouble("TractorBeamDistanceThreshold");
        if (compound.contains("HasPhysics")) {
            this.setPhysics(compound.getBoolean("HasPhysics"));
        }

        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
            this.cachedOwner = null;
        }
        this.leftOwner = compound.getBoolean("LeftOwner");
        this.hasBeenShot = compound.getBoolean("HasBeenShot");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("StartPos", NbtUtils.writeBlockPos(this.getStartPos()));
        compound.put("Blocks", GoetyAwakenNBTUtil.writeBlockStatePosMap(this.getBlocks()));
        compound.put("TileData", GoetyAwakenNBTUtil.writeCompoundList(this.getTileData()));
        compound.putFloat("XSize", this.entityData.get(X_SIZE));
        compound.putFloat("YSize", this.entityData.get(Y_SIZE));
        compound.putFloat("ZSize", this.entityData.get(Z_SIZE));
        compound.putInt("Time", this.time);
        compound.putBoolean("DropItems", this.dropItems);
        compound.put("RotationDelta", GoetyAwakenNBTUtil.writeVector2f(this.getRotationDelta()));
        compound.putBoolean("ResetGravity", this.resetGravityOnLoad);
        compound.putBoolean("ForceRender", this.forceRender());
        compound.putInt("ShakeTime", this.shakeTime);
        compound.putInt("GroundSink", this.getSink());
        compound.putBoolean("AntiStacking", this.antiStacking());
        this.entityData.get(FADE_POINT).ifPresent((pos) -> compound.put("StaticFadePos", NbtUtils.writeBlockPos(pos)));
        compound.putBoolean("ShouldCrumble", this.shouldCrumble);
        compound.putBoolean("ShouldntCountToConsumedEntities", this.shouldntCountToConsumedEntities);
        compound.putBoolean("CreatedFromBeam", this.createdFromBeam);
        compound.putBoolean("CreatedFromFallingBlock", this.createdFromFallingBlock);
        compound.putInt("HeadCreatedFrom", this.headCreatedFrom);
        compound.putDouble("TractorBeamDistanceThreshold", this.tractorBeamDistanceThreshold);
        compound.putBoolean("HasPhysics", this.physicsEnabled());

        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
        compound.putBoolean("LeftOwner", this.leftOwner);
        compound.putBoolean("HasBeenShot", this.hasBeenShot);
    }

    @Override
    public void tick() {
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        this.shakeO = new Vec2(this.shake.x, this.shake.y);
        if (this.shakeTime > 0) {
            float shakeTime = (float) this.getShakeTime();
            float x = Mth.cos(shakeTime * 4.5F) * 0.05F + (this.random.nextFloat() - 0.5F) * 0.05F;
            float z = Mth.sin(shakeTime * 3.5F) * 0.15F + (this.random.nextFloat() - 0.5F) * 0.2F;
            this.shake = new Vec2(x, z);
            --this.shakeTime;
            if (this.shakeTime == 0) {
                this.setShakeTime(0);
            }
        } else {
            this.shake = new Vec2(0.0F, 0.0F);
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

            Map<BlockPos, BlockState> blocks = this.getBlocks();
            boolean isAir = true;

            for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
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
                        || this.time > 600) {
                    if (this.dropItems && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
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
        ++this.time;
        this.noPhysics = !this.physicsEnabled();
        super.tick();
        this.xClusterRotO = this.xClusterRot;
        this.yClusterRotO = this.yClusterRot;
        if (this.getShakeTime() <= 0) {
            this.xClusterRot += this.getRotationDelta().x;
            this.yClusterRot += this.getRotationDelta().y;
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
        return this.isNoGravity() ? 0.0F : DEFAULT_GRAVITY;
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
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

        Entity entity = this.getOwner();
        if (entity == null) {
            return true;
        }

        if (this.leftOwner) {
            return true;
        }

        if (entity.isPassengerOfSameVehicle(pTarget)) {
            return false;
        }

        Entity owner = this.getOwner();
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
        Map<BlockPos, BlockState> blocks = this.getBlocks();
        Level level = this.level();
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
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
        if (this.antiStacking()) {
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
            } else if (this.dropItems && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
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
        this.entityData.set(ROTATION_DELTA, rotation);
    }

    public Vec2 getRotationDelta() {
        return this.entityData.get(ROTATION_DELTA);
    }

    public Map<BlockPos, BlockState> getBlocks() {
        return this.entityData.get(BLOCKS);
    }

    public void addBlock(BlockState state, BlockPos relativePosition) {
        Map<BlockPos, BlockState> map = Maps.newHashMap(this.getBlocks());
        map.put(relativePosition, state);
        this.entityData.set(BLOCKS, map);
    }

    public void setBlocks(Map<BlockPos, BlockState> blocks) {
        this.entityData.set(BLOCKS, blocks);
    }

    public void setStartPos(BlockPos pos) {
        this.entityData.set(START_POS, pos);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(START_POS);
    }

    public List<CompoundTag> getTileData() {
        return this.entityData.get(TILE_DATA);
    }

    public void addTileData(CompoundTag compound) {
        List<CompoundTag> list = Lists.newArrayList(this.getTileData());
        list.add(compound);
        this.entityData.set(TILE_DATA, list, true);
    }

    public void setTileData(List<CompoundTag> list) {
        this.entityData.set(TILE_DATA, list);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(
                Math.max(this.entityData.get(X_SIZE), this.entityData.get(Z_SIZE)),
                this.entityData.get(Y_SIZE));
    }

    @SuppressWarnings("unchecked")
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public int getSize() {
        return this.getBlocks().size();
    }

    public boolean physicsEnabled() {
        return this.entityData.get(PHYSICS);
    }

    public void setPhysics(boolean physics) {
        this.entityData.set(PHYSICS, physics);
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
        if (!parameter.equals(X_SIZE) && !parameter.equals(Y_SIZE) && !parameter.equals(Z_SIZE)) {
            if (parameter.equals(SHAKE_TIME)) {
                this.shakeTime = this.entityData.get(SHAKE_TIME);
            }
        } else {
            this.refreshDimensions();
        }
    }

    public boolean forceRender() {
        return this.entityData.get(FORCE_RENDER);
    }

    public void setForceRender(boolean flag) {
        this.entityData.set(FORCE_RENDER, flag);
    }

    public void setSize(float x, float y, float z) {
        this.entityData.set(X_SIZE, x);
        this.entityData.set(Y_SIZE, y);
        this.entityData.set(Z_SIZE, z);
        this.refreshDimensions();
    }

    @Override
    protected AABB makeBoundingBox() {
        float x = this.entityData.get(X_SIZE);
        float y = this.entityData.get(Y_SIZE);
        float z = this.entityData.get(Z_SIZE);
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

    public void setShakeTime(int time) {
        this.shakeTime = time;
        this.entityData.set(SHAKE_TIME, time);
    }

    public int getShakeTime() {
        return this.shakeTime;
    }

    public void setSink(int sink) {
        this.sink = sink;
    }

    public int getSink() {
        return this.sink;
    }

    public void setAntiStacking(boolean flag) {
        this.antiStacking = flag;
    }

    public boolean antiStacking() {
        return this.antiStacking;
    }

    @Nullable
    public BlockPos getFadePos() {
        return this.entityData.get(FADE_POINT).orElse(null);
    }

    public void setFadePos(@Nullable BlockPos pos) {
        this.entityData.set(FADE_POINT, Optional.ofNullable(pos));
    }

    public void setFadeStrength(float strength) {
        this.entityData.set(FADE_STRENGTH, strength);
    }

    public float getFadeStrength() {
        return this.entityData.get(FADE_STRENGTH);
    }

    public int getFadeDistanceOffset() {
        return this.entityData.get(FADE_DISTANCE_OFFSET);
    }

    public void setFadeDistanceOffset(int offset) {
        this.entityData.set(FADE_DISTANCE_OFFSET, offset);
    }

    public void setShouldCrumble(boolean flag) {
        this.shouldCrumble = flag;
    }

    public boolean shouldCrumble() {
        return this.shouldCrumble;
    }

    public void setShouldntCountToConsumedEntities(boolean flag) {
        this.shouldntCountToConsumedEntities = flag;
    }

    public boolean shouldntCountToConsumedEntities() {
        return this.shouldntCountToConsumedEntities;
    }

    public float getClusterXRot(float partialTicks) {
        return Mth.lerp(partialTicks, this.xClusterRotO, this.xClusterRot);
    }

    public float getClusterYRot(float partialTicks) {
        return Mth.lerp(partialTicks, this.yClusterRotO, this.yClusterRot);
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
        BLOCKS = SynchedEntityData.defineId(BlockClusterEntity.class, GoetyAwakenDataSerializers.BLOCK_STATE_POS_MAP);
        TILE_DATA = SynchedEntityData.defineId(BlockClusterEntity.class, GoetyAwakenDataSerializers.COMPOUND_LIST);
        START_POS = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BLOCK_POS);
        ROTATION_DELTA = SynchedEntityData.defineId(BlockClusterEntity.class, GoetyAwakenDataSerializers.VECTOR_2F);
        PHYSICS = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BOOLEAN);
        FORCE_RENDER = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BOOLEAN);
        X_SIZE = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        Y_SIZE = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        Z_SIZE = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        SHAKE_TIME = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.INT);
        FADE_POINT = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
        FADE_STRENGTH = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        FADE_DISTANCE_OFFSET = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.INT);
    }
}
