package com.k1sak1.goetyawaken.utils;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.common.entities.projectiles.Lavaball;
import com.Polarice3.Goety.common.entities.projectiles.ModWitherSkull;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class SpellBounceHelper {
    public static final int MAX_BOUNCE_TICKS = 600;
    private static final double NORMAL_EPS = 1.0E-4D;
    private static final double PUSH_DISTANCE = 0.5D;
    private static final double BOUNCE_DAMPING = 0.8D;
    private static final String BOUNCE_LEFT = "goetyawaken_bounce_left";
    private static final String BOUNCE_TOTAL = "goetyawaken_bounce_total";

    private SpellBounceHelper() {
    }

    public static boolean tryBounceFromDiscard(Projectile projectile) {
        if (!(projectile instanceof ISpellEntity)) {
            return false;
        }
        CompoundTag tag = projectile.getPersistentData();
        if (tag.getInt(BOUNCE_TOTAL) > 0 && tag.getInt(BOUNCE_LEFT) <= 0) {
            return false;
        }
        if (projectile.tickCount > MAX_BOUNCE_TICKS) {
            return false;
        }
        if (projectile instanceof Lavaball || projectile instanceof ModWitherSkull) {
            return false;
        }
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(projectile,
                entity -> entity != projectile.getOwner()
                        && !MobUtil.areAllies(projectile.getOwner(), entity));
        if (hit.getType() == HitResult.Type.MISS) {
            return false;
        }
        return tryBounce(projectile, hit);
    }

    public static boolean tryBounce(Projectile projectile, HitResult hit) {
        CompoundTag tag = projectile.getPersistentData();
        int left = tag.getInt(BOUNCE_LEFT);
        if (tag.getInt(BOUNCE_TOTAL) == 0) {
            if (!(projectile.getOwner() instanceof LivingEntity living)) {
                return false;
            }
            int total = ModAttributeRegistry.getSpellBounceLevel(living);
            if (total <= 0) {
                return false;
            }
            tag.putInt(BOUNCE_TOTAL, total);
            left = total;
        }
        if (left <= 0) {
            return false;
        }
        Vec3 normal = resolveNormal(projectile, hit);
        if (normal == null) {
            return false;
        }
        Vec3 velocity = projectile.getDeltaMovement();
        double dot = velocity.dot(normal);
        if (dot >= 0.0D) {
            return false;
        }
        projectile.setDeltaMovement(velocity.subtract(normal.scale(2.0D * dot)).scale(BOUNCE_DAMPING));
        projectile.setPos(hit.getLocation().add(normal.scale(PUSH_DISTANCE)));
        tag.putInt(BOUNCE_LEFT, left - 1);
        return true;
    }

    private static Vec3 resolveNormal(Projectile projectile, HitResult hit) {
        if (hit instanceof BlockHitResult blockHit) {
            if (!projectile.level().getFluidState(blockHit.getBlockPos()).isEmpty()) {
                return null;
            }
            return Vec3.atLowerCornerOf(blockHit.getDirection().getNormal());
        }
        if (hit instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            if (target == null || target.isRemoved()) {
                return null;
            }
            return faceNormal(target.getBoundingBox(), entityHit.getLocation(), projectile.getDeltaMovement());
        }
        return null;
    }

    private static Vec3 faceNormal(AABB bb, Vec3 hitLoc, Vec3 velocity) {
        Vec3[] normals = {
                new Vec3(-1.0D, 0.0D, 0.0D), new Vec3(1.0D, 0.0D, 0.0D),
                new Vec3(0.0D, -1.0D, 0.0D), new Vec3(0.0D, 1.0D, 0.0D),
                new Vec3(0.0D, 0.0D, -1.0D), new Vec3(0.0D, 0.0D, 1.0D)
        };
        boolean[] onFace = {
                Math.abs(hitLoc.x - bb.minX) < NORMAL_EPS, Math.abs(hitLoc.x - bb.maxX) < NORMAL_EPS,
                Math.abs(hitLoc.y - bb.minY) < NORMAL_EPS, Math.abs(hitLoc.y - bb.maxY) < NORMAL_EPS,
                Math.abs(hitLoc.z - bb.minZ) < NORMAL_EPS, Math.abs(hitLoc.z - bb.maxZ) < NORMAL_EPS
        };
        double bestDot = Double.NEGATIVE_INFINITY;
        Vec3 best = null;
        for (int i = 0; i < normals.length; i++) {
            if (!onFace[i]) {
                continue;
            }
            double dot = velocity.dot(normals[i]);
            if (dot < bestDot) {
                bestDot = dot;
                best = normals[i];
            }
        }
        if (best != null) {
            return best;
        }
        Vec3 delta = hitLoc.subtract(bb.getCenter());
        double ax = Math.abs(delta.x);
        double ay = Math.abs(delta.y);
        double az = Math.abs(delta.z);
        if (ax >= ay && ax >= az) {
            return new Vec3(Math.signum(delta.x), 0.0D, 0.0D);
        }
        if (ay >= az) {
            return new Vec3(0.0D, Math.signum(delta.y), 0.0D);
        }
        return new Vec3(0.0D, 0.0D, Math.signum(delta.z));
    }
}
