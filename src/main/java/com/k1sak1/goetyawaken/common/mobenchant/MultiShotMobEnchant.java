package com.k1sak1.goetyawaken.common.mobenchant;

import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID)
public class MultiShotMobEnchant {
    private static final String MULTISHOT_ANGLE_KEY = "MultiShotAngle";
    private static boolean isAdding = false;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.loadedFromDisk()) {
            if (!(event.getEntity() instanceof Projectile projectile)) {
                return;
            }
            Level level = event.getLevel();
            if (!isAllowedProjectile(projectile)) {
                return;
            }

            if (!(projectile.getOwner() instanceof LivingEntity livingOwner)) {
                return;
            }
            MobEnchantCapability capability = MobEnchantEventHandler.getCapability(livingOwner);
            if (capability == null) {
                return;
            }

            int multishotLevel = capability.getMobEnchantLevel(MobEnchantType.MULTISHOT);
            if (multishotLevel <= 0) {
                return;
            }
            CompoundTag entityData = projectile.getPersistentData();
            if (entityData.contains(MULTISHOT_ANGLE_KEY)) {
                return;
            }

            if (!level.isClientSide && projectile.tickCount == 0 && !isAdding) {
                isAdding = true;
                int totalProjectiles = 1 + 2 * multishotLevel;

                float angleStep = totalProjectiles <= 5 ? 15.0F : 7.5F;
                int centerIndex = totalProjectiles / 2;

                float[] angles = new float[totalProjectiles];
                for (int i = 0; i < totalProjectiles; i++) {
                    angles[i] = (i - centerIndex) * angleStep;
                }

                for (int i = 0; i < totalProjectiles; i++) {
                    if (i == centerIndex) {
                        continue;
                    }

                    addProjectile(projectile, level, angles[i], livingOwner);
                }

                isAdding = false;
            }
        }
    }

    private static boolean isAllowedProjectile(Projectile projectile) {
        List<? extends String> allowedProjectiles = Config.allowMultiShotProjectiles;
        if (allowedProjectiles == null) {
            return false;
        }
        ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(projectile.getType());
        String projectileId = registryName != null ? registryName.toString() : "";

        for (String allowed : allowedProjectiles) {
            if (allowed.equals(projectileId)) {
                return true;
            }
        }
        return false;
    }

    private static void addProjectile(Projectile projectile, Level level, float rotation, LivingEntity livingOwner) {
        Projectile newProjectile = (Projectile) projectile.getType().create(level);
        if (newProjectile == null) {
            return;
        }

        UUID uuid = newProjectile.getUUID();
        CompoundTag compoundNBT = new CompoundTag();
        projectile.saveWithoutId(compoundNBT);
        newProjectile.load(compoundNBT);
        newProjectile.setUUID(uuid);
        newProjectile.getPersistentData().putFloat(MULTISHOT_ANGLE_KEY, rotation);

        if (newProjectile instanceof AbstractArrow) {
            ((AbstractArrow) newProjectile).pickup = AbstractArrow.Pickup.DISALLOWED;
        }

        Vec3 viewVector = livingOwner.getViewVector(1.0F);
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(
                (double) (rotation * ((float) Math.PI / 180F)),
                livingOwner.getUpVector(1.0F).x,
                livingOwner.getUpVector(1.0F).y,
                livingOwner.getUpVector(1.0F).z);
        Vector3f rotatedVector = viewVector.toVector3f().rotate(quaternionf);
        double speed = projectile.getDeltaMovement().length();
        newProjectile.setDeltaMovement(
                new Vec3(rotatedVector.x(), rotatedVector.y(), rotatedVector.z()).normalize().scale(speed));

        float f = Mth.sqrt((float) newProjectile.getDeltaMovement().horizontalDistanceSqr());
        newProjectile
                .setYRot((float) (Mth.atan2(newProjectile.getDeltaMovement().x, newProjectile.getDeltaMovement().z)
                        * (double) (180F / (float) Math.PI)));
        newProjectile.setXRot((float) (Mth.atan2(newProjectile.getDeltaMovement().y, (double) f)
                * (double) (180F / (float) Math.PI)));
        newProjectile.yRotO = newProjectile.getYRot();
        newProjectile.xRotO = newProjectile.getXRot();

        newProjectile.setPos(projectile.getX(), projectile.getY(), projectile.getZ());

        if (newProjectile instanceof AbstractHurtingProjectile abstractHurtingProjectile) {
            Vec3 newPower = new Vec3(
                    abstractHurtingProjectile.xPower,
                    abstractHurtingProjectile.yPower,
                    abstractHurtingProjectile.zPower);
            Vector3f rotatedPower = newPower.toVector3f().rotate(quaternionf);
            abstractHurtingProjectile.xPower = rotatedPower.x();
            abstractHurtingProjectile.yPower = rotatedPower.y();
            abstractHurtingProjectile.zPower = rotatedPower.z();
        }

        newProjectile.setOwner(projectile.getOwner());

        if (newProjectile instanceof AbstractArrow) {
            ((AbstractArrow) newProjectile).pickup = AbstractArrow.Pickup.DISALLOWED;
        }

        level.addFreshEntity(newProjectile);
    }
}
