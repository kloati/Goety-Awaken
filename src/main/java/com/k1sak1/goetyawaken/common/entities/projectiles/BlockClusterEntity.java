package com.k1sak1.goetyawaken.common.entities.projectiles;

import com.Polarice3.Goety.utils.MobUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.k1sak1.goetyawaken.init.GoetyAwakenDataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class BlockClusterEntity extends Entity {

    protected static final EntityDataAccessor<Map<BlockPos, BlockState>> CLUSTER_BLOCKS;
    private static final EntityDataAccessor<List<CompoundTag>> CLUSTER_TILE_DATA;
    private static final EntityDataAccessor<BlockPos> CLUSTER_ANCHOR;
    private static final EntityDataAccessor<Vec2> CLUSTER_SPIN;
    private static final EntityDataAccessor<Boolean> CLUSTER_HAS_PHYSICS;
    private static final EntityDataAccessor<Boolean> CLUSTER_FORCE_VISIBLE;
    private static final EntityDataAccessor<Float> CLUSTER_BOUNDS_X;
    private static final EntityDataAccessor<Float> CLUSTER_BOUNDS_Y;
    private static final EntityDataAccessor<Float> CLUSTER_BOUNDS_Z;
    private static final EntityDataAccessor<Integer> CLUSTER_WOBBLE_TICKS;
    protected static final EntityDataAccessor<Optional<BlockPos>> CLUSTER_FADE_CENTER;
    private static final EntityDataAccessor<Float> CLUSTER_FADE_POWER;
    private static final EntityDataAccessor<Integer> CLUSTER_FADE_MARGIN;

    @Nullable
    private UUID launcherUUID;
    @Nullable
    private Entity cachedLauncher;
    private boolean hasLeftLauncher;
    private boolean hasFired;

    private static final float AIR_FRICTION = 0.99F;
    private static final float GRAVITY_ACCEL = 0.03F;

    public int lifetime;
    public boolean shouldDropItems;
    public boolean resetGravityFlag;
    private int wobbleTimer;
    @Nonnull
    public Vec2 previousWobble;
    @Nonnull
    public Vec2 currentWobble;
    private int groundBuryDepth;
    private boolean avoidBlockOverlap;
    private boolean shouldPlaceOnImpact;
    private boolean ignoreConsumedCount;
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
        this.previousWobble = Vec2.ZERO;
        this.currentWobble = Vec2.ZERO;
        this.spawnHeadIndex = -1;
    }

    public BlockClusterEntity(EntityType<?> entityType, Level world, LivingEntity launcher) {
        super(entityType, world);
        this.shouldDropItems = true;
        this.resetGravityFlag = true;
        this.previousWobble = Vec2.ZERO;
        this.currentWobble = Vec2.ZERO;
        this.spawnHeadIndex = -1;
        this.setOwner(launcher);
    }

    public static BlockClusterEntity createSphericalCluster(
            EntityType<?> entityType, Level world, @Nullable LivingEntity owner,
            BlockState block, float radius, Vec3 pos, Vec3 velocity,
            Vec2 spinDelta, boolean noGravity, boolean glowing,
            boolean placeBlocks, boolean shouldDropItems) {

        BlockClusterEntity cluster = new BlockClusterEntity(entityType, world, owner);
        Map<BlockPos, BlockState> states = Maps.newHashMap();
        float radiusSq = radius * radius;
        int bound = Mth.ceil(radius);

        for (int x = -bound; x <= bound; x++) {
            for (int y = -bound; y <= bound; y++) {
                for (int z = -bound; z <= bound; z++) {
                    if ((float) (x * x + y * y + z * z) <= radiusSq) {
                        states.put(new BlockPos(x, y, z), block);
                    }
                }
            }
        }

        cluster.setPos(pos.x, pos.y, pos.z);
        cluster.loadBlocksFromMap(states);
        cluster.setDeltaMovement(velocity);
        cluster.setSpinDelta(spinDelta);
        cluster.setNoGravity(noGravity);
        cluster.setGlowingTag(glowing);
        cluster.setPhysicsEnabled(true);
        cluster.shouldDropItems = shouldDropItems;
        cluster.shouldPlaceOnImpact = placeBlocks;

        if (velocity.lengthSqr() > 0.0D) {
            double horizDist = velocity.horizontalDistance();
            cluster.setXRot((float) (Mth.atan2(velocity.y, horizDist) * (180F / Math.PI)));
            cluster.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * (180F / Math.PI)));
            cluster.xRotO = cluster.getXRot();
            cluster.yRotO = cluster.getYRot();
        }
        return cluster;
    }

    public static BlockClusterEntity createSphericalCluster(
            EntityType<?> entityType, Level world, @Nullable LivingEntity owner,
            BlockState block, float radius, Vec3 pos, Vec3 direction, float speed) {
        return createSphericalCluster(entityType, world, owner, block, radius, pos,
                direction.normalize().scale(speed), Vec2.ZERO, false, false, true, true);
    }

    public static BlockClusterEntity createSphericalCluster(
            EntityType<?> entityType, Level world, @Nullable LivingEntity owner,
            BlockState block, float radius, Vec3 pos, Vec3 direction,
            float speed, Vec2 spinDelta) {
        return createSphericalCluster(entityType, world, owner, block, radius, pos,
                direction.normalize().scale(speed), spinDelta, false, false, true, true);
    }

    public void setOwner(@Nullable Entity owner) {
        if (owner != null) {
            this.launcherUUID = owner.getUUID();
            this.cachedLauncher = owner;
        }
    }

    @Nullable
    public Entity getLauncher() {
        if (this.cachedLauncher != null && !this.cachedLauncher.isRemoved()) {
            return this.cachedLauncher;
        } else if (this.launcherUUID != null && this.level() instanceof ServerLevel sl) {
            this.cachedLauncher = sl.getEntity(this.launcherUUID);
            return this.cachedLauncher;
        }
        return null;
    }

    public void loadBlocksFromMap(Map<BlockPos, BlockState> states) {
        if (states.isEmpty())
            return;
        int[] bounds = computeBounds(states);
        float spanX = (float) (bounds[1] - bounds[0]);
        float spanY = (float) (bounds[3] - bounds[2]);
        float spanZ = (float) (bounds[5] - bounds[4]);
        this.setClusterDimensions(Math.abs(spanX) + 1.0F, Math.abs(spanY) + 1.0F, Math.abs(spanZ) + 1.0F);
        this.setAnchorPosition(new BlockPos(
                (int) (bounds[0] + spanX / 2.0F),
                (int) (bounds[2] + spanY / 2.0F),
                (int) (bounds[4] + spanZ / 2.0F)));
        this.setClusterBlocks(states);
    }

    private static int[] computeBounds(Map<BlockPos, BlockState> states) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos pos : states.keySet()) {
            if (pos.getX() < minX)
                minX = pos.getX();
            if (pos.getY() < minY)
                minY = pos.getY();
            if (pos.getZ() < minZ)
                minZ = pos.getZ();
            if (pos.getX() > maxX)
                maxX = pos.getX();
            if (pos.getY() > maxY)
                maxY = pos.getY();
            if (pos.getZ() > maxZ)
                maxZ = pos.getZ();
        }
        return new int[] { minX, maxX, minY, maxY, minZ, maxZ };
    }

    public void captureRegion(BlockPos corner1, BlockPos corner2, Predicate<BlockState> filter) {
        int spanX = Mth.floor((float) (corner2.getX() - corner1.getX()));
        int spanY = Mth.floor((float) (corner2.getY() - corner1.getY()));
        int spanZ = Mth.floor((float) (corner2.getZ() - corner1.getZ()));
        this.setPos(Vec3.atLowerCornerOf(corner1).x + (double) spanX / 2.0D + 0.5D,
                Vec3.atLowerCornerOf(corner1).y + Math.min((double) spanY, 0.0D),
                Vec3.atLowerCornerOf(corner1).z + (double) spanZ / 2.0D + 0.5D);
        this.setClusterDimensions(Math.abs(spanX) + 1.0F, Math.abs(spanY) + 1.0F, Math.abs(spanZ) + 1.0F);
        this.setAnchorPosition(corner1.offset(spanX / 2, spanY / 2, spanZ / 2));

        for (BlockPos pos : BlockPos.betweenClosed(corner1, corner2)) {
            BlockState state = this.level().getBlockState(pos);
            if (!state.isAir() && filter.test(state)) {
                if (state.hasBlockEntity()) {
                    BlockEntity tile = this.level().getBlockEntity(pos);
                    if (tile != null) {
                        this.addTileEntityData(tile.serializeNBT());
                        this.level().removeBlockEntity(pos);
                    }
                }
                this.addSingleBlock(state, pos.subtract(this.getAnchorPosition()));
            }
        }
        for (var entry : this.getClusterBlocks().entrySet()) {
            this.level().setBlock(this.getAnchorPosition().offset(entry.getKey()),
                    Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public void captureSphericalRegion(BlockPos center, float radius, Predicate<BlockState> filter) {
        int diameter = Mth.ceil(radius) * 2 - 1;
        this.setClusterDimensions((float) diameter, (float) diameter, (float) diameter);
        this.setAnchorPosition(center);
        this.setPos((double) (center.getX() + 0.5F),
                center.getY() - this.getBoundingBox().getCenter().y + 0.5D,
                (double) (center.getZ() + 0.5F));

        int lo = Mth.floor(radius), hi = Mth.ceil(radius);
        for (int x = -lo; x < hi; x++) {
            for (int y = -lo; y < hi; y++) {
                for (int z = -lo; z < hi; z++) {
                    if (Mth.sqrt((float) (x * x + y * y + z * z)) < radius) {
                        BlockPos wPos = new BlockPos(x + center.getX(), y + center.getY(), z + center.getZ());
                        BlockState state = this.level().getBlockState(wPos);
                        if (!state.isAir() && filter.test(state)) {
                            if (state.hasBlockEntity()) {
                                BlockEntity tile = this.level().getBlockEntity(wPos);
                                if (tile != null) {
                                    this.addTileEntityData(tile.serializeNBT());
                                    this.level().removeBlockEntity(wPos);
                                }
                            }
                            this.addSingleBlock(state, new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }
        for (var entry : this.getClusterBlocks().entrySet()) {
            this.level().setBlock(center.offset(entry.getKey()), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CLUSTER_ANCHOR, BlockPos.ZERO);
        this.entityData.define(CLUSTER_BLOCKS, new HashMap<>());
        this.entityData.define(CLUSTER_TILE_DATA, new ArrayList<>());
        this.entityData.define(CLUSTER_SPIN, new Vec2(0.0F, 0.0F));
        this.entityData.define(CLUSTER_HAS_PHYSICS, true);
        this.entityData.define(CLUSTER_FORCE_VISIBLE, false);
        this.entityData.define(CLUSTER_BOUNDS_X, 1.0F);
        this.entityData.define(CLUSTER_BOUNDS_Y, 1.0F);
        this.entityData.define(CLUSTER_BOUNDS_Z, 1.0F);
        this.entityData.define(CLUSTER_WOBBLE_TICKS, 0);
        this.entityData.define(CLUSTER_FADE_CENTER, Optional.empty());
        this.entityData.define(CLUSTER_FADE_POWER, 10.0F);
        this.entityData.define(CLUSTER_FADE_MARGIN, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("AnchorPos"))
            this.setAnchorPosition(NbtUtils.readBlockPos(tag.getCompound("AnchorPos")));
        if (tag.contains("ClusterBlocks"))
            this.setClusterBlocks(readBlockStateMap(
                    this.level().holderLookup(Registries.BLOCK), tag.getList("ClusterBlocks", 10)));
        if (tag.contains("ClusterTiles"))
            this.setTileEntityData(readCompoundTagList(tag.getList("ClusterTiles", 10)));
        if (tag.contains("SpinDelta"))
            this.setSpinDelta(readVec2(tag.getCompound("SpinDelta")));

        if (tag.contains("ClusterWidth")) {
            this.entityData.set(CLUSTER_BOUNDS_X, tag.getFloat("ClusterWidth"));
            this.entityData.set(CLUSTER_BOUNDS_Z, tag.getFloat("ClusterWidth"));
        } else if (tag.contains("Width")) {
            this.entityData.set(CLUSTER_BOUNDS_X, tag.getFloat("Width"));
            this.entityData.set(CLUSTER_BOUNDS_Z, tag.getFloat("Width"));
        } else {
            this.entityData.set(CLUSTER_BOUNDS_X, tag.getFloat("XSize"));
            this.entityData.set(CLUSTER_BOUNDS_Z, tag.getFloat("ZSize"));
        }
        if (tag.contains("ClusterHeight")) {
            this.entityData.set(CLUSTER_BOUNDS_Y, tag.getFloat("ClusterHeight"));
        } else if (tag.contains("Height")) {
            this.entityData.set(CLUSTER_BOUNDS_Y, tag.getFloat("Height"));
        } else {
            this.entityData.set(CLUSTER_BOUNDS_Y, tag.getFloat("YSize"));
        }

        this.refreshDimensions();
        this.lifetime = tag.getInt("Age");
        this.shouldDropItems = tag.getBoolean("DropsEnabled");
        this.resetGravityFlag = tag.getBoolean("AutoResetGravity");
        if (this.resetGravityFlag)
            this.setNoGravity(false);
        this.setForceVisible(tag.getBoolean("ForceVisible"));
        this.setWobbleTicks(tag.getInt("WobbleTicks"));
        this.setGroundBuryDepth(tag.getInt("BuryDepth"));
        this.setAvoidBlockOverlap(tag.getBoolean("AvoidOverlap"));
        if (tag.contains("FadeCenterPos"))
            this.entityData.set(CLUSTER_FADE_CENTER,
                    Optional.of(NbtUtils.readBlockPos(tag.getCompound("FadeCenterPos"))));
        this.shouldPlaceOnImpact = tag.getBoolean("PlaceOnImpact");
        this.ignoreConsumedCount = tag.getBoolean("SkipConsumedCount");
        this.spawnedFromBeam = tag.getBoolean("FromBeam");
        this.spawnedFromFallingBlock = tag.getBoolean("FromFalling");
        this.spawnHeadIndex = tag.getInt("SpawnHeadId");
        this.tractorBeamRange = tag.getDouble("BeamRange");
        if (tag.contains("PhysicsEnabled"))
            this.setPhysicsEnabled(tag.getBoolean("PhysicsEnabled"));
        if (tag.hasUUID("Launcher")) {
            this.launcherUUID = tag.getUUID("Launcher");
            this.cachedLauncher = null;
        }
        this.hasLeftLauncher = tag.getBoolean("HasLeftLauncher");
        this.hasFired = tag.getBoolean("HasFired");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("AnchorPos", NbtUtils.writeBlockPos(this.getAnchorPosition()));
        tag.put("ClusterBlocks", writeBlockStateMap(this.getClusterBlocks()));
        tag.put("ClusterTiles", writeCompoundTagList(this.getTileEntityData()));
        tag.putFloat("XSize", this.entityData.get(CLUSTER_BOUNDS_X));
        tag.putFloat("YSize", this.entityData.get(CLUSTER_BOUNDS_Y));
        tag.putFloat("ZSize", this.entityData.get(CLUSTER_BOUNDS_Z));
        tag.putInt("Age", this.lifetime);
        tag.putBoolean("DropsEnabled", this.shouldDropItems);
        tag.put("SpinDelta", writeVec2(this.getSpinDelta()));
        tag.putBoolean("AutoResetGravity", this.resetGravityFlag);
        tag.putBoolean("ForceVisible", this.isForceVisible());
        tag.putInt("WobbleTicks", this.wobbleTimer);
        tag.putInt("BuryDepth", this.getGroundBuryDepth());
        tag.putBoolean("AvoidOverlap", this.isAvoidBlockOverlap());
        this.entityData.get(CLUSTER_FADE_CENTER).ifPresent(
                p -> tag.put("FadeCenterPos", NbtUtils.writeBlockPos(p)));
        tag.putBoolean("PlaceOnImpact", this.shouldPlaceOnImpact);
        tag.putBoolean("SkipConsumedCount", this.ignoreConsumedCount);
        tag.putBoolean("FromBeam", this.spawnedFromBeam);
        tag.putBoolean("FromFalling", this.spawnedFromFallingBlock);
        tag.putInt("SpawnHeadId", this.spawnHeadIndex);
        tag.putDouble("BeamRange", this.tractorBeamRange);
        tag.putBoolean("PhysicsEnabled", this.isPhysicsEnabled());
        if (this.launcherUUID != null)
            tag.putUUID("Launcher", this.launcherUUID);
        tag.putBoolean("HasLeftLauncher", this.hasLeftLauncher);
        tag.putBoolean("HasFired", this.hasFired);
    }

    @Override
    public void tick() {
        if (!this.hasFired) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getLauncher());
            this.hasFired = true;
        }
        if (!this.hasLeftLauncher)
            this.hasLeftLauncher = this.checkHasLeftLauncher();

        this.previousWobble = new Vec2(this.currentWobble.x, this.currentWobble.y);
        if (this.wobbleTimer > 0) {
            float t = (float) this.getWobbleTicks();
            this.currentWobble = new Vec2(
                    Mth.cos(t * 4.5F) * 0.05F + (this.random.nextFloat() - 0.5F) * 0.05F,
                    Mth.sin(t * 3.5F) * 0.15F + (this.random.nextFloat() - 0.5F) * 0.2F);
            this.wobbleTimer--;
            if (this.wobbleTimer == 0)
                this.setWobbleTicks(0);
        } else {
            this.currentWobble = Vec2.ZERO;
        }

        if (!this.level().isClientSide) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hit.getType() != HitResult.Type.MISS)
                this.onHit(hit);

            if (this.getClusterBlocks().isEmpty()) {
                this.discard();
                return;
            }
            if (this.getClusterBlocks().values().stream().allMatch(BlockState::isAir)) {
                this.discard();
                return;
            }

            BlockPos pos = this.blockPosition();
            if (!this.onGround()) {
                if ((float) pos.getY() + this.getBbHeight() <= (float) this.level().getMinBuildHeight()
                        || this.lifetime > 600) {
                    if (this.shouldDropItems && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        for (var e : this.getClusterBlocks().entrySet())
                            this.spawnAtSpecificLocation(e.getValue().getBlock().asItem(), pos.offset(e.getKey()));
                    }
                    this.discard();
                }
            } else {
                this.placeBlocksInWorld();
            }
        } else {
            this.refreshDimensions();
            this.reapplyPosition();
        }

        this.travel();
        this.lifetime++;
        this.noPhysics = !this.isPhysicsEnabled();
        super.tick();

        this.prevClusterPitchAngle = this.clusterPitchAngle;
        this.prevClusterYawAngle = this.clusterYawAngle;
        if (this.getWobbleTicks() <= 0) {
            this.clusterPitchAngle += this.getSpinDelta().x;
            this.clusterYawAngle += this.getSpinDelta().y;
        }
    }

    protected void travel() {
        Vec3 motion = this.getDeltaMovement();
        double nx = this.getX() + motion.x, ny = this.getY() + motion.y, nz = this.getZ() + motion.z;
        this.updateClusterRotation();
        if (this.isInWater())
            this.setDeltaMovement(motion.scale(0.8D));
        if (!this.isNoGravity())
            this.setDeltaMovement(motion.x, motion.y - (double) this.getGravityForce(), motion.z);
        this.setPos(nx, ny, nz);
    }

    protected void updateClusterRotation() {
        Vec3 m = this.getDeltaMovement();
        double h = m.horizontalDistance();
        this.setXRot(lerpAngle(this.xRotO, (float) (Mth.atan2(m.y, h) * (180F / Math.PI))));
        this.setYRot(lerpAngle(this.yRotO, (float) (Mth.atan2(m.x, m.z) * (180F / Math.PI))));
    }

    protected static float lerpAngle(float cur, float tgt) {
        float diff = tgt - cur;
        while (diff < -180.0F)
            diff += 360.0F;
        while (diff >= 180.0F)
            diff -= 360.0F;
        return cur + diff * 0.2F;
    }

    protected float getGravityForce() {
        return this.isNoGravity() ? 0.0F : GRAVITY_ACCEL;
    }

    private boolean checkHasLeftLauncher() {
        Entity l = this.getLauncher();
        if (l != null) {
            for (Entity e : this.level().getEntities(this,
                    this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D),
                    en -> !en.isSpectator() && en.isPickable())) {
                if (e.getRootVehicle() == l.getRootVehicle())
                    return false;
            }
        }
        return true;
    }

    public boolean canHitEntity(Entity target) {
        if (!target.canBeHitByProjectile())
            return false;
        Entity l = this.getLauncher();
        if (l == null)
            return true;
        if (!this.hasLeftLauncher) {
            if (l.isPassengerOfSameVehicle(target))
                return false;
            if (MobUtil.areAllies(l, target))
                return false;
        }
        return true;
    }

    public void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(),
                    GameEvent.Context.of(this, (BlockState) null));
        } else if (result.getType() == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) result);
        }
    }

    public void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        float hardness = this.computeBlockHardnessSum();
        float speed = (float) this.getDeltaMovement().length();
        if (target.hurt(this.damageSources().flyIntoWall(), hardness * (0.1F + speed)))
            this.pushTargetAway(target);
    }

    public void onHitBlock(BlockHitResult result) {
        this.placeBlocksInWorld();
    }

    private float computeBlockHardnessSum() {
        float sum = 0.0F;
        for (var e : this.getClusterBlocks().entrySet()) {
            if (!e.getValue().isAir())
                sum += e.getValue().getDestroySpeed(this.level(), this.getAnchorPosition().offset(e.getKey()));
        }
        return Math.max(sum, 1.0F);
    }

    private void pushTargetAway(Entity target) {
        double dx = target.getX() - this.getX(), dz = target.getZ() - this.getZ();
        if (target instanceof LivingEntity living) {
            living.knockback(0.5F, dx, dz);
        } else {
            double d = Math.sqrt(dx * dx + dz * dz);
            if (d > 0.0D)
                target.setDeltaMovement(target.getDeltaMovement().add(dx / d * 0.5D, 0.2D, dz / d * 0.5D));
        }
    }

    public void placeBlocksInWorld() {
        this.discard();
        BlockPos pos = this.blockPosition();
        if (this.isAvoidBlockOverlap()) {
            BlockPos scan = this.blockPosition();
            for (int i = 0; i < 50 && this.level().getBlockState(scan).isAir(); i++)
                scan = scan.below();
            pos = pos.atY(scan.getY());
        }
        for (var e : this.getClusterBlocks().entrySet()) {
            BlockState state = e.getValue();
            BlockPos rel = e.getKey();
            BlockPos placePos = pos.offset(rel.getX(), rel.getY() - this.getGroundBuryDepth(), rel.getZ())
                    .above(Mth.floor(this.getBoundingBox().getYsize() / 2.0D - 0.5D));
            if (this.level().getBlockEntity(placePos) == null
                    && !this.level().getBlockState(placePos).is(BlockTags.DRAGON_IMMUNE)
                    && this.level().setBlock(placePos, state, 3)) {
                if (state.hasBlockEntity()) {
                    CompoundTag td = this.getTileDataForRelativePos(rel);
                    if (td != null) {
                        BlockEntity tile = this.level().getBlockEntity(placePos);
                        if (tile != null) {
                            td.putInt("x", placePos.getX());
                            td.putInt("y", placePos.getY());
                            td.putInt("z", placePos.getZ());
                            tile.load(td);
                            tile.setChanged();
                        }
                    }
                }
                this.level().updateNeighborsAt(placePos, state.getBlock());
            } else if (this.shouldDropItems && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.spawnAtSpecificLocation(state.getBlock().asItem(), placePos);
            }
        }
    }

    @Nullable
    public CompoundTag getTileDataForRelativePos(BlockPos rel) {
        BlockPos actual = this.getAnchorPosition().offset(rel);
        for (CompoundTag data : this.getTileEntityData())
            if (data.getInt("x") == actual.getX() && data.getInt("y") == actual.getY()
                    && data.getInt("z") == actual.getZ())
                return data;
        return null;
    }

    public void spawnAtSpecificLocation(ItemLike item, BlockPos pos) {
        ItemEntity entity = new ItemEntity(this.level(), pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                new ItemStack(item));
        entity.setDefaultPickUpDelay();
        this.level().addFreshEntity(entity);
    }

    public void setSpinDelta(Vec2 spin) {
        this.entityData.set(CLUSTER_SPIN, spin);
    }

    public Vec2 getSpinDelta() {
        return this.entityData.get(CLUSTER_SPIN);
    }

    public Map<BlockPos, BlockState> getClusterBlocks() {
        return this.entityData.get(CLUSTER_BLOCKS);
    }

    public void addSingleBlock(BlockState state, BlockPos relPos) {
        Map<BlockPos, BlockState> m = new HashMap<>(this.getClusterBlocks().size() + 1);
        m.putAll(this.getClusterBlocks());
        m.put(relPos, state);
        this.entityData.set(CLUSTER_BLOCKS, m);
    }

    public void setClusterBlocks(Map<BlockPos, BlockState> blocks) {
        this.entityData.set(CLUSTER_BLOCKS, blocks);
    }

    public void setAnchorPosition(BlockPos pos) {
        this.entityData.set(CLUSTER_ANCHOR, pos);
    }

    public BlockPos getAnchorPosition() {
        return this.entityData.get(CLUSTER_ANCHOR);
    }

    public List<CompoundTag> getTileEntityData() {
        return this.entityData.get(CLUSTER_TILE_DATA);
    }

    public void addTileEntityData(CompoundTag tag) {
        List<CompoundTag> list = Lists.newArrayList(this.getTileEntityData());
        list.add(tag);
        this.entityData.set(CLUSTER_TILE_DATA, list, true);
    }

    public void setTileEntityData(List<CompoundTag> list) {
        this.entityData.set(CLUSTER_TILE_DATA, list);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        float w = Math.max(this.entityData.get(CLUSTER_BOUNDS_X), this.entityData.get(CLUSTER_BOUNDS_Z));
        return EntityDimensions.scalable(w, this.entityData.get(CLUSTER_BOUNDS_Y));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public int getBlockCount() {
        return this.getClusterBlocks().size();
    }

    public boolean isPhysicsEnabled() {
        return this.entityData.get(CLUSTER_HAS_PHYSICS);
    }

    public void setPhysicsEnabled(boolean e) {
        this.entityData.set(CLUSTER_HAS_PHYSICS, e);
        this.noPhysics = !e;
    }

    public boolean containsBlock(Block block) {
        return this.getClusterBlocks().values().stream().anyMatch(s -> s.is(block));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(CLUSTER_BOUNDS_X) || key.equals(CLUSTER_BOUNDS_Y) || key.equals(CLUSTER_BOUNDS_Z))
            this.refreshDimensions();
        else if (key.equals(CLUSTER_WOBBLE_TICKS))
            this.wobbleTimer = this.entityData.get(CLUSTER_WOBBLE_TICKS);
    }

    public boolean isForceVisible() {
        return this.entityData.get(CLUSTER_FORCE_VISIBLE);
    }

    public void setForceVisible(boolean f) {
        this.entityData.set(CLUSTER_FORCE_VISIBLE, f);
    }

    public void setClusterDimensions(float x, float y, float z) {
        this.entityData.set(CLUSTER_BOUNDS_X, x);
        this.entityData.set(CLUSTER_BOUNDS_Y, y);
        this.entityData.set(CLUSTER_BOUNDS_Z, z);
        this.refreshDimensions();
    }

    @Override
    protected AABB makeBoundingBox() {
        float hw = this.entityData.get(CLUSTER_BOUNDS_X) / 2.0F;
        float h = this.entityData.get(CLUSTER_BOUNDS_Y);
        float hd = this.entityData.get(CLUSTER_BOUNDS_Z) / 2.0F;
        return new AABB(this.getX() - hw, this.getY(), this.getZ() - hd,
                this.getX() + hw, this.getY() + h, this.getZ() + hd);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public void setWobbleTicks(int t) {
        this.wobbleTimer = t;
        this.entityData.set(CLUSTER_WOBBLE_TICKS, t);
    }

    public int getWobbleTicks() {
        return this.wobbleTimer;
    }

    public void setGroundBuryDepth(int d) {
        this.groundBuryDepth = d;
    }

    public int getGroundBuryDepth() {
        return this.groundBuryDepth;
    }

    public void setAvoidBlockOverlap(boolean f) {
        this.avoidBlockOverlap = f;
    }

    public boolean isAvoidBlockOverlap() {
        return this.avoidBlockOverlap;
    }

    @Nullable
    public BlockPos getFadeCenter() {
        return this.entityData.get(CLUSTER_FADE_CENTER).orElse(null);
    }

    public void setFadeCenter(@Nullable BlockPos p) {
        this.entityData.set(CLUSTER_FADE_CENTER, Optional.ofNullable(p));
    }

    public void setFadePower(float p) {
        this.entityData.set(CLUSTER_FADE_POWER, p);
    }

    public float getFadePower() {
        return this.entityData.get(CLUSTER_FADE_POWER);
    }

    public int getFadeMargin() {
        return this.entityData.get(CLUSTER_FADE_MARGIN);
    }

    public void setFadeMargin(int m) {
        this.entityData.set(CLUSTER_FADE_MARGIN, m);
    }

    public void setShouldPlaceOnImpact(boolean f) {
        this.shouldPlaceOnImpact = f;
    }

    public boolean shouldPlaceOnImpact() {
        return this.shouldPlaceOnImpact;
    }

    public void setIgnoreConsumedCount(boolean f) {
        this.ignoreConsumedCount = f;
    }

    public boolean isIgnoreConsumedCount() {
        return this.ignoreConsumedCount;
    }

    public float getClusterXRot(float pt) {
        return Mth.lerp(pt, this.prevClusterPitchAngle, this.clusterPitchAngle);
    }

    public float getClusterYRot(float pt) {
        return Mth.lerp(pt, this.prevClusterYawAngle, this.clusterYawAngle);
    }

    private static ListTag writeBlockStateMap(Map<BlockPos, BlockState> map) {
        ListTag list = new ListTag();
        for (var e : map.entrySet()) {
            CompoundTag et = NbtUtils.writeBlockState(e.getValue());
            et.put("RelPos", NbtUtils.writeBlockPos(e.getKey()));
            list.add(et);
        }
        return list;
    }

    private static Map<BlockPos, BlockState> readBlockStateMap(
            net.minecraft.core.HolderGetter<Block> lookup, ListTag list) {
        Map<BlockPos, BlockState> result = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag et = list.getCompound(i);
            result.put(NbtUtils.readBlockPos(et.getCompound("RelPos")), NbtUtils.readBlockState(lookup, et));
        }
        return result;
    }

    private static ListTag writeCompoundTagList(List<CompoundTag> tags) {
        ListTag list = new ListTag();
        for (CompoundTag t : tags)
            list.add(t);
        return list;
    }

    private static List<CompoundTag> readCompoundTagList(ListTag list) {
        List<CompoundTag> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            result.add(list.getCompound(i));
        return result;
    }

    private static CompoundTag writeVec2(Vec2 v) {
        CompoundTag t = new CompoundTag();
        t.putFloat("vx", v.x);
        t.putFloat("vy", v.y);
        return t;
    }

    private static Vec2 readVec2(CompoundTag t) {
        return new Vec2(t.getFloat("vx"), t.getFloat("vy"));
    }

    static {
        CLUSTER_BLOCKS = SynchedEntityData.defineId(BlockClusterEntity.class,
                GoetyAwakenDataSerializers.BLOCK_STATE_POS_MAP);
        CLUSTER_TILE_DATA = SynchedEntityData.defineId(BlockClusterEntity.class,
                GoetyAwakenDataSerializers.COMPOUND_LIST);
        CLUSTER_ANCHOR = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BLOCK_POS);
        CLUSTER_SPIN = SynchedEntityData.defineId(BlockClusterEntity.class, GoetyAwakenDataSerializers.VECTOR_2F);
        CLUSTER_HAS_PHYSICS = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BOOLEAN);
        CLUSTER_FORCE_VISIBLE = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.BOOLEAN);
        CLUSTER_BOUNDS_X = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        CLUSTER_BOUNDS_Y = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        CLUSTER_BOUNDS_Z = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        CLUSTER_WOBBLE_TICKS = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.INT);
        CLUSTER_FADE_CENTER = SynchedEntityData.defineId(BlockClusterEntity.class,
                EntityDataSerializers.OPTIONAL_BLOCK_POS);
        CLUSTER_FADE_POWER = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.FLOAT);
        CLUSTER_FADE_MARGIN = SynchedEntityData.defineId(BlockClusterEntity.class, EntityDataSerializers.INT);
    }
}
